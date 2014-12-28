package org.fireflow.model.resourcedef;


public enum ResourceType {
	USER("org.fireflow.constants.resource_type.User","用户"),//,"用户"
	ROLE("org.fireflow.constants.resource_type.Role","角色"),//,"角色"
	GROUP("org.fireflow.constants.resource_type.Group","用户组"),//,"用户组"
	DEPARTMENT("org.fireflow.constants.resource_type.Department","部门"),//,"部门"
	CUSTOM("org.fireflow.constants.resource_type.Custom","自定义"),//,"用户自定义"
	PROCESS_INSTANCE_CREATOR("org.fireflow.constants.resource_type.ProcessInstanceCreator","流程创建者"),//,"流程创建者"
	ACTIVITY_INSTANCE_PERFORMER("org.fireflow.constants.resource_type.ActivityInstancePerformer","活动实例执行者"),//,"活动实例执行者"
	VARIABLE_IMPLICATION("org.fireflow.constants.resource_type.VariableImplication","流程变量所指定的用户");//,"流程变量所指用户"
	//SYSTEM("org.fireflow.constants.System","系统");//,"系统"
	
	private String value = null;
	private String displayName = null;
	private ResourceType(String v,String displayName){
		this.value = v;
		this.displayName = displayName;
	}
	
	public String getValue(){
		return value;
	}
	public String getDisplayName(){
		return displayName;
	}
	public static ResourceType fromValue(String v){
		ResourceType[] values =  ResourceType.values();
		for (ResourceType tmp : values){
			if (tmp.getValue().equals(v)){
				return tmp;
			}
		}
		return null;
	}	
}
