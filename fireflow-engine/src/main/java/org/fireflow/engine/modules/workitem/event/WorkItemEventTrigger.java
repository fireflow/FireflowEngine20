package org.fireflow.engine.modules.workitem.event;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.modules.event.EventTrigger;

public enum WorkItemEventTrigger implements EventTrigger{
	ON_WORKITEM_CREATED,
	BEFORE_WORKITEM_CLAIMED,
	AFTER_WORKITEM_CLAIMED,
	AFTER_WORKITEM_END
	;
		
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}	
}
