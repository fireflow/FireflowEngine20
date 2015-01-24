INSERT INTO `T_security_group` (`code`,`name`,`parent_code`,`org_code`,`org_name`,`last_update_person`) VALUES ('GROUP_FIRECLOUD','某集团','0','FIRECLOUD','某集团','SYSINIT');
INSERT INTO `T_security_group` (`code`,`name`,`parent_code`,`org_code`,`org_name`,`last_update_person`) VALUES ('GROUP_FIREFLOW','FIRE WORKFLOW 公司','GROUP_FIRECLOUD','FIREFLOW','FIRE WORKFLOW 公司','SYSINIT');
INSERT INTO `T_security_group` (`code`,`name`,`parent_code`,`org_code`,`org_name`,`last_update_person`) VALUES ('GROUP_1','技术部','GROUP_FIREFLOW','FIREFLOW','FIRE WORKFLOW 公司','系统管理员');
INSERT INTO `T_security_group` (`code`,`name`,`parent_code`,`org_code`,`org_name`,`last_update_person`) VALUES ('GROUP_2','销售部','GROUP_FIREFLOW','FIREFLOW','FIRE WORKFLOW 公司','系统管理员');
INSERT INTO `T_security_group` (`code`,`name`,`parent_code`,`org_code`,`org_name`,`last_update_person`) VALUES ('GROUP_3','管理层','GROUP_FIREFLOW','FIREFLOW','FIRE WORKFLOW 公司','系统管理员');
