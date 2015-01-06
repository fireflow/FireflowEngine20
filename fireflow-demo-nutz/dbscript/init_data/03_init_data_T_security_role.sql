-- 系统内置的角色信息
insert into T_security_role(code,name,is_built_in,last_update_person,is_position) values
	('ROLE_CEO','总经理',1,'SYSINIT',1),
	('ROLE_COO','副总经理',1,'SYSINIT',1),
	('ROLE_CFO','财务总监',1,'SYSINIT',1),
	('ROLE_DEPT_MANAGER','部门经理',1,'SYSINIT',1),
	('ROLE_ACCOUNTANT','会计',1,'SYSINIT',1),
	('ROLE_ACCOUNTING_CLERK','会计文员',1,'SYSINIT',1),
	('ROLE_CASHIER','出纳',1,'SYSINIT',1),
	('ROLE_BUSINESS_CONTROLLER','业务主管',1,'SYSINIT',0),
	('ROLE_LAW_CLERK','法务',1,'SYSINIT',1)
	;
	
