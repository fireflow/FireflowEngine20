package org.fireflow.engine.modules.instancemanager.event;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.modules.event.EventTrigger;

/**
 * 事件类型，活动实例的事件以及流程实例的事件类型都在该枚举型中表达。
 * @author 非也
 *
 */
public enum ProcessInstanceEventTrigger implements EventTrigger{
	ON_PROCESS_INSTANCE_CREATED,
	BEFORE_PROCESS_INSTANCE_RUN,
	ON_PROCESS_INSTANCE_SUSPENDED,
	ON_PROCESS_INSTANCE_RESTORED,
	AFTER_PROCESS_INSTANCE_END

	;
		
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}	
//	ON_PROCESS_INSTANCE_CREATED("ON_PROCESS_INSTANCE_CREATED","流程实例被创建"),
//	AFTER_PROCESS_INSTANCE_END("AFTER_PROCESS_INSTANCE_END","流程实例结束后"),
//	ON_ACTIVITY_INSTANCE_CREATED("ON_ACTIVITY_INSTANCE_CREATED","活动实例被创建"),
//	ON_ACTIVITY_INSTANCE_SUSPEND("ON_ACTIVITY_INSTANCE_SUSPENDED","活动实例被挂起"),
//	ON_ACTIVITY_INSTANCE_RESTORE("ON_ACTIVITY_INSTANCE_RESTORED","ON_ACTIVITY_INSTANCE_RESTORE"),
//	AFTER_ACTIVITY_INSTANCE_END("AFTER_ACTIVITY_INSTANCE_END","活动实例结束后"),
//	ON_WORKITEM_CREATED("AFTER_WORKITEM_CREATED","工作项被创建"),
//	AFTER_WORKITEM_END("AFTER_WORKITEM_END","工作项结束后");
//	
//	private String name = null;
//	private String displayName = null;
//	private EventType(String name,String displayName){
//		this.name = name;
//		this.displayName = displayName;
//	}
//	
//	public String getName(){
//		
//		return this.name;
//	}
//	
//	public String getDisplayName(){
//		return this.displayName;
//	}
}
