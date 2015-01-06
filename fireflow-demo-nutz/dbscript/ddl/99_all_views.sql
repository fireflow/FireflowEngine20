-- 创建菜单视图      
DROP VIEW IF EXISTS V_sys_menu;

create view V_sys_menu as 
(select t.id as id, concat('m',t.code) as `code` ,`name`,t.ftype as `type`, t.sort,t.code as func_code ,concat('m',t.parent_code) as parent_code ,t.url as url 
 from T_sys_function t where t.ftype='GROUP' or t.ftype='MENU');      
 
-- 用户视图
 DROP VIEW IF EXISTS V_security_user;

create view V_security_user as select t.* ,
	f_get_user_role_name(t.login_name) as roleNames
 from T_security_user as t;
 
 -- 角色与群组关系视图
DROP VIEW IF EXISTS V_security_role_belongs;
create view V_security_role_belongs as select t.*,
	(select t2.name from T_security_role t2 where t2.code=t.role_code) as role_name from T_security_role_belongs t;
	
-- 角色用户的视图
DROP VIEW if EXISTS V_security_user_role;
create view V_security_user_role as select t.*,
	(select t2.name from T_security_user t2 where t2.login_name=t.user_code) as user_name ,
	(select t3.name from T_security_role t3 where t3.code = t.role_code) as role_name
	from T_security_user_role t;
	