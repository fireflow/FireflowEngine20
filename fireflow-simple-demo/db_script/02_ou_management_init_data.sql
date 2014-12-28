-- 增加组织机构
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001','0001','Fire科技集团','');
	
-- 集团管理机构
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('000191','000191','集团总经办','0001');	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('000192','000192','集团战略规划部','0001');		
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('000193','000193','财务部','0001');		
	
-- 集团1级公司一
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('00011','00011','Fire软件有限公司','0001');
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001101','0001101','总经理办公室','00011');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001102','0001102','研发中心','00011');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001103','0001103','销售部','00011');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001104','0001104','财务部','00011');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001105','0001105','人力资源部','00011');		
	
-- 集团1级公司二		
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('00012','00012','Fire房地产有限公司','0001');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001201','0001201','总经理办公室','00012');	
	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001203','0001203','销售部','00012');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001204','0001204','财务部','00012');	
	
insert into t_demo_department(id,department_Id,department_Name,parent_department_id)
	values('0001205','0001205','人力资源部','00012');	
	
-- 测试用户
insert into t_demo_user(id,user_id,password,name,position_id,position_Name,department_id,department_name)
	values('1','test','test','测试用户','testPosition','系统测试组','0001102','研发中心');
	