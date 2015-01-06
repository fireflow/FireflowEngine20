-- 在命令行界面登陆mysql ， mysql -uokerp -p  --default-character-set=utf8
-- 用use okerpdb;命令指定当前数据库
-- 然后通过source ~/dbscript/ddl/batch_execute.sql批量运行所有的脚本
-- 导入之前请先将路径"~/dbscript/ddl/"替换成正确的值
use fireflowdemo;
source ~/dbscript/ddl/01_common.sql;
source ~/dbscript/ddl/02_workflow.sql;
source ~/dbscript/ddl/03_system.sql;
source ~/dbscript/ddl/04_security.sql;
source ~/dbscript/ddl/15_hr_holidays.sql;
source ~/dbscript/ddl/98_function_procedures.sql;
source ~/dbscript/ddl/99_all_views.sql;