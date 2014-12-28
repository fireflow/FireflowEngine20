package org.fireflow.service.java;

import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;


public class JavaService extends AbstractServiceDef implements ServiceDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7707022769451546942L;
	
	public static final String PROP_JAVA_CLASS_NAME = "java-class";
	public static final String PROP_JAVA_BEAN_NAME = "java-bean";
	
	private String javaClassName = null;
	private String javaBeanName = null;
	
	public JavaService(){
		this.invokerClassName = JavaInvoker.class.getName();
		this.parserClassName = JavaServiceParser.class.getName();
	}

	public String getJavaClassName() {
		return javaClassName;
	}
	
	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
	}
	public String getJavaBeanName() {
		return javaBeanName;
	}
	public void setJavaBeanName(String javaBeanName) {
		this.javaBeanName = javaBeanName;
	}	
}
