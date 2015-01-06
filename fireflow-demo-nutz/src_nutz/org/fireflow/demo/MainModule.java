package org.fireflow.demo;

import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.ioc.provider.ComboIocProvider;


@Modules(scanPackage = true)
@IocBy(type=ComboIocProvider.class,args={"*org.nutz.ioc.loader.json.JsonLoader","conf/",
	  "*org.nutz.ioc.loader.annotation.AnnotationIocLoader","org.fireflow.demo"})
@Encoding(input="utf8",output="utf8")
public class MainModule {
	//在session中的业务对象的key
	public static final String BIZ_OBJECT_IN_SESSION_KEY = "com.okideaad.erp.nutz.BIZ_OBJECT_IN_SESSION_KEY";

	public static final String BILL_CODE_KEY = "bill_code";//单据号
	
	/*
	 * jTable相关的常量
	 */
	public static final String JTABLE_RESULT_KEY = "Result";//jTable json对象的Result字段名
	public static final String JTABLE_RECORDS_KEY = "Records";//jTable json对象的Records字段名
	public static final String JTABLE_RECORD_KEY = "Record";//jTable json对象的Record字段名
	public static final String JTABLE_MESSAGE_KEY = "Message";//jTable json 对象的Message字段名
	public static final String JTABLE_OPTIONS_KEY = "Options";//jTable json 对象的Options字段名
	
	public static final String JTABLE_RESULT_VALUE_OK = "OK";
	public static final String JTABLE_RESULT_VALUE_ERROR = "ERROR";
	public static final String JTABLE_TOTAL_RECORD_COUNT = "TotalRecordCount";
	
	//存放于request中的message对象的key
	public static final String MESSAGE_OBJECT = "MESSAGE_OBJECT";
	
	//流程操作相关参数的Key	
	public static final String WORKITEM = "workItem";
	public static final String WORKITEM_ID = "workItemId";
	public static final String BIZ_ID = "bizId";
	public static final String WORKITEM_STATE = "workItemState";
	public static final String PROCESS_ID = "processId";
	public static final String FIRST_ACTIVITY_ID = "firstActivityId";
	public static final String TARGET_ACTIVITY_ID = "targetActivityId";
	public static final String PROCESS_INSTANCE_ID = "processInstanceId";
	public static final String ACTIVITY_INSTANCE_ID = "activityInstanceId";
	
	public static final String DAO_BEAN_NAME = "dao";
}
