DROP TABLE IF EXISTS fireflowdemo.T_sys_sequences;
CREATE TABLE fireflowdemo.T_sys_sequences (
  `name` varchar(50) NOT NULL,
  `year` int not null default 0,
  `month` int not null default 0,
  `day` int not null default 0,
  `value` BIGINT,
  PRIMARY KEY (`name`,`year`,`month`,`day`)
) ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;



delimiter //
drop function if exists fireflowdemo.true_function //
create function true_function(p_param int) returns int
  deterministic
  sql security invoker
  return true
//

drop function if exists fireflowdemo.get_next_value//
create function get_next_value(p_name varchar(30), roll_flag char(1)) returns int
  deterministic
  sql security invoker
begin  
  declare current_val integer;
  declare record_exist integer default 0;

  declare thisTime datetime default now();

  declare thisYear int default 0;
  declare thisMonth int default 0;
  declare thisDay int default 0;
  
  /* 根据滚动规则，检查是否有当期记录，如果没有，则初始化 */
  /* roll_flag=D,序列发生器每天滚动  
   * roll_flag=M,序列发生器每月滚动  
   * roll_flag=Y,序列发生器每年滚动
   * roll_flag=X,序列发生器不滚动 
   * */
  
  if ('D'=roll_flag) then
	set thisYear = year(thisTime);
	set thisMonth = month(thisTime);
    set thisDay =  DAYOFMONTH(thisTime);
	select count(*) into record_exist 
		from T_sys_sequences where name=p_name and year=thisYear and month=thisMonth and day =thisDay;
    if (record_exist=0)then
		insert into T_sys_sequences(name,year,month,day,value) values(p_name,thisYear,thisMonth,thisDay,1);
	end if ;
  elseif ('M'=roll_flag) then
	set thisYear = year(thisTime);
	set thisMonth = month(thisTime);
	select count(*) into record_exist 
		from T_sys_sequences where name=p_name and year=thisYear and month=thisMonth and day=0;
    if (record_exist=0)then
		insert into T_sys_sequences(name,year,month,day,value) values(p_name,thisYear,thisMonth,0,1);
	end if ;
  elseif ('Y'=roll_flag) then
	set thisYear = year(thisTime);
	select count(*) into record_exist 
		from T_sys_sequences where name=p_name and year=thisYear and month=0 and day=0 ;
    if (record_exist=0)then
		insert into T_sys_sequences(name,year,month,day,value) values(p_name,thisYear,0,0,1);
	end if ;
  else
	select count(*) into record_exist 
		from T_sys_sequences where name=p_name and year=0 and month=0 and day=0  ;
    if (record_exist=0)then
		insert into T_sys_sequences(name,year,month,day,value) values(p_name,0,0,0,1);
	end if ;
  end if;
  
  
  update T_sys_sequences 
  	set value = value + 1
  	where name = p_name and year=thisYear and month=thisMonth and day=thisDay
  	  and true_function((@current_val :=T_sys_sequences.value) is not null);
  	  
  return @current_val;
end//
delimiter ;