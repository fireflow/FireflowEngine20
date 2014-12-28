package org.fireflow.model.process.lifecycle;

import java.util.Map;

import org.fireflow.model.misc.ParameterDescriptor;

public interface InstanceCreatorDef {
	public String getInstanceCreatorClassName();
	public void setInstanceCreatorClassName(String className);

	public String getInstanceCreatorBeanName();
}
