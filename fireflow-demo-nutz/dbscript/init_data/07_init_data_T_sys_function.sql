-- truncate table T_sys_function;

-- 顶层功能
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('01','系统管理','ALL','0','','',99,'GROUP'),
	('03','行政事务','ALL','0','','',9,'GROUP'),
	('98','我的工作台','ALL','0','','',1,'GROUP')
;

insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('9801','我的待办工作','ALL','98','','/module/workflow/WorkflowModule/gotoTodoList',1,'MENU'),
	('9802','我的已办工作','ALL','98','','/module/workflow/WorkflowModule/gotoHaveDoneList',1,'MENU'),
	('9803','我发起的流程','ALL','98','','/module/workflow/WorkflowModule/gotoMyProcInst',1,'MENU')

	;		
	--	('9804','我的消息','ALL','98','','',1,'MENU')
	
--  系统管理
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('0101','用户管理','ALL','01','','',1,'GROUP'),
	('0102','群组管理','ALL','01','','',3,'GROUP'),
	('0103','系统配置','ALL','01','','',13,'GROUP'),
	('0104','角色管理','ALL','01','','',5,'GROUP'),
	('0105','员工信息管理','ALL','01','','',7,'GROUP'),
	('0106','部门信息管理','ALL','01','','',9,'GROUP'),
	('0107','岗位管理','ALL','01','','',11,'GROUP')
	;	
	
	-- 系统管理.用户管理
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('010101','创建及修改','ALL','0101','','/module/User/gotoEdit',1,'MENU'),
	('010102','用户查询','ALL','0101','','/module/User/gotolist',1,'MENU'),
	('010103','重置密码','ALL','0101','','/module/User/gotoPassReset',1,'MENU'),
	('010104','用户授权','ALL','0101','','/module/User/gotoAuthorize',1,'MENU')
	;		
	-- 系统管理.群组管理	
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('010201','创建及修改','ALL','0102','','/module/Group/Groupedit',1,'MENU'),
	('010202','群组查询','ALL','0102','','/module/Group/Grouplist',1,'MENU'),
	('010203','群组授权','ALL','0102','','/module/Group/gotoAuthorize',1,'MENU')
	;		
	
	-- 系统管理.系统配置
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('010301','上传流程定义','ALL','0103','','/module/system/workflow/DefinitionsModule/initUpload',1,'MENU'),
	('010302','流程定义列表','ALL','0103','','/module/system/workflow/DefinitionsModule/gotoListAllProcess',2,'MENU');
	
	-- 系统管理.角色管理	
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('010401','创建及修改','ALL','0104','','/module/Role/gotoRoleEdit',1,'MENU'),
	('010402','角色查询','ALL','0104','','/module/Role/gotoRoleList',1,'MENU'),
	('010403','角色授权','ALL','0104','','/module/Role/gotoAuthorize',1,'MENU')
	;	


--  行政事务
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('0306','假期管理','ALL','03','','',9,'GROUP');


	-- 假期管理
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('030601','请假','ALL','0306','','/module/holiday/LeaveRequest/initRequest',1,'MENU');	
insert into T_sys_function(code,name,scope,parent_code,description,url,sort,ftype) values 
	('030602','我的请假情况','ALL','0306','','/module/holiday/LeaveRequest/initRequest2',1,'MENU');		