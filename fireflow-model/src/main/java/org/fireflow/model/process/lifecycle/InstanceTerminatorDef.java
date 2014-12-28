package org.fireflow.model.process.lifecycle;

import java.util.Map;

import org.fireflow.model.misc.ParameterDescriptor;

public interface InstanceTerminatorDef {
	public String getInstanceTerminatorClassName();
	public void setInstanceTerminatorClassName(String className);

	public String getInstanceTerminatorBeanName();	
	
}
