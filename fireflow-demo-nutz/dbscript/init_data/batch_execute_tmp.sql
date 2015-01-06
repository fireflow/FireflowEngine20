-- 在命令行界面登陆mysql ， mysql -uokerp -p  --default-character-set=utf8
-- 然后通过source batch_execute.sql批量运行所有的脚本
-- 导入之前请先将路径"~/dbscript/init_data/"替换成正确的值
use fireflowdemo;
source ~/dbscript/init_data/00_init_data_T_hr_organization.sql;
source ~/dbscript/init_data/01_init_data_T_security_group.sql;
source ~/dbscript/init_data/03_init_data_T_security_role.sql;
source ~/dbscript/init_data/04_init_data_T_security_role_belongs.sql;
source ~/dbscript/init_data/05_init_test_data_T_security_user.sql;
source ~/dbscript/init_data/07_init_data_T_sys_function.sql;
source ~/dbscript/init_data/08_init_data_T_sys_dictionary.sql;
source ~/dbscript/init_data/12_init_data_T_security_user_role.sql;
source ~/dbscript/init_data/13_init_data_T_security_permissions.sql;
