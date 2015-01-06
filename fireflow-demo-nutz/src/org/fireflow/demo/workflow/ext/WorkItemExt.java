package org.fireflow.demo.workflow.ext;

import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;

public class WorkItemExt extends AbsWorkItem {

	
	int stateValue = 0;
	String stateDisplayName = "";


	public int getStateValue(){
		return stateValue;
		
	}
	
	public String getStateDisplayName(){
		return stateDisplayName;
	}

	public void setStateValue(int stateValue) {
		this.stateValue = stateValue;
	}

	public void setStateDisplayName(String stateDisplayName) {
		this.stateDisplayName = stateDisplayName;
	}
	
	
	
	
}
