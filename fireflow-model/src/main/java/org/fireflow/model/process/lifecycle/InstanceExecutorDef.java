package org.fireflow.model.process.lifecycle;

import java.util.Map;

import org.fireflow.model.misc.ParameterDescriptor;

public interface InstanceExecutorDef {
	public String getInstanceExecutorClassName();
	public void setInstanceExecutorClassName(String className);

	public String getInstanceExecutorBeanName();
}
