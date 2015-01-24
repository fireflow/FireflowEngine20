drop  FUNCTION if exists `f_get_dicvalue`;

-- 通过字段key返回字典value的函数

DELIMITER $$

CREATE  FUNCTION `f_get_dicvalue`(in_dic_type varchar(50),in_dic_key varchar(50)) RETURNS varchar(50) CHARSET utf8
begin
	declare out_dic_value varchar(50);
	select dic_value into out_dic_value from T_sys_dictionary  where dic_type=in_dic_type and dic_key=in_dic_key;
    return out_dic_value;
end
$$
DELIMITER ;


-- 生成根据用户获取角色名称
drop function if exists f_get_user_role_name;
delimiter //

create function f_get_user_role_name(in_longin_name varchar(50)) returns varchar(100)
  deterministic
  sql security invoker
begin
	DECLARE record_found boolean default true;
	DECLARE tmpName varchar(50);
	DECLARE returnName varchar(50) default '';
	DECLARE tmpCode varchar(50);
	DECLARE countNum int default 0 ;
	declare roleExist int default 0;

	DECLARE  my_coursor CURSOR FOR select role_code 
		from T_security_user_role where user_code=in_longin_name; 

	DECLARE CONTINUE HANDLER FOR NOT FOUND set record_found=false; 

	
	open my_coursor;
		fetch my_coursor into tmpCode;
		while (record_found) do
			select count(*) into roleExist from T_security_role where code=tmpCode;
			if (roleExist>0) then
			
				select name into tmpName  from T_security_role where code=tmpCode;
	
				if countNum > 0 then
					set returnName=concat(returnName,',') ; 
				end if;
				set returnName=concat(returnName,tmpName) ; 
				
				set countNum=countNum+1;
			end if;
			fetch my_coursor into tmpCode;
            
		end while;

	close my_coursor;  
	
	return returnName;
end ;
//
delimiter ;


-- 生成根据用户获取角色名称
drop function if exists f_bank_account_format;
delimiter //

create function f_bank_account_format(bank_account varchar(50)) returns varchar(100)
  deterministic
  sql security invoker
begin
	DECLARE prefix varchar(50);
	DECLARE suffix varchar(50);
	DECLARE returnAccount varchar(50) default '';
	DECLARE countNum int default 0 ;
	DECLARE lengthNum int default 0;
  
	set lengthNum=char_length(bank_account) ;
	
	if(lengthNum>0) then
		-- 从左右截取各4个字符，其他饿格式化为注释符号*
		set prefix = left(bank_account,4);	
		set suffix = right(bank_account,4);	
		set returnAccount = CONCAT(returnAccount,prefix);
		while (countNum < lengthNum-8) do
			set returnAccount = CONCAT(returnAccount,'*');
			set countNum=countNum+1;
		end while;

	end if;  
	set returnAccount = CONCAT(returnAccount,suffix);
	return returnAccount;
end ;
//
delimiter ;
