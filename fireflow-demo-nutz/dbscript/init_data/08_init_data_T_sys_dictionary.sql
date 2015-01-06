-- 删除所有数据
-- delete from T_sys_dictionary;

-- ==============请假类型=====================
INSERT INTO `T_sys_dictionary` (`dic_type`,`dic_key`,`dic_value`,`last_update_person`) VALUES 
	('DICT_CATEGORY','leave_type','请假类型','SYSINIT');
	
INSERT INTO `T_sys_dictionary` (`dic_type`,`dic_key`,`dic_value`,`last_update_person`,sort) VALUES 
	('leave_type','paid','带薪年假','SYSINIT',1),
	('leave_type','sick','病假','SYSINIT',2),
	('leave_type','absence','事假','SYSINIT',3),
	('leave_type','marital','婚假','SYSINIT',5),
	('leave_type','maternity','产假','SYSINIT',7),
	('leave_type','funeral','丧假','SYSINIT',9)
	;
