/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.service.java;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;

/**
 * java bean服务，负责调用java bean；
 * 
 * @author 非也
 * @version 2.0
 */
public class JavaInvoker extends AbsServiceInvoker implements ServiceInvoker {
	private static final Log log = LogFactory.getLog(JavaInvoker.class);

	@Override
	public Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef svc,Object activity) throws ServiceInvocationException {

		BeanFactory beanFactory = runtimeContext.getEngineModule(
				BeanFactory.class, activityInstance.getProcessType());

		JavaService javaService = (JavaService) svc;
		String beanName = javaService.getJavaBeanName();
		String javaClass = javaService.getJavaClassName();
		Object bean = null;
		if (!StringUtils.isEmpty(beanName)) {
			bean = beanFactory.getBean(beanName);
		} else if (!StringUtils.isEmpty(javaClass)) {
			bean = beanFactory.createBean(javaClass);

		}

		if (bean == null) {
			ServiceInvocationException ex = new ServiceInvocationException("无法初始化 service object，Service定义是[name="+svc.getName()+",displayName="+svc.getDisplayName()+"]");
			ex.setErrorCode(ServiceInvocationException.SERVICE_OBJECT_NOT_FOUND);
			ex.setActivityInstance(activityInstance);
			throw ex;
		} else {
			return bean;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.invocation.AbsServiceInvoker#getParameterTypes(java
	 * .lang.String, java.lang.Object[])
	 */
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName,
			Object[] params)throws ServiceInvocationException {
		Object[] _params = params;
		if (_params == null) {
			_params = new Object[0];
		}
		Class[] parameterTypes = new Class[_params.length];
		boolean hasNullParam = false;
		for (int i = 0; i < _params.length; i++) {
			Object param = _params[i];
			if (param!=null){
				parameterTypes[i] = param.getClass();
			}else{
				parameterTypes[i] = NullClass.class;
				hasNullParam = true;
			}
			
		}
		if (!hasNullParam){
			return parameterTypes;
		}
		else{
			Method[] candidateMethods = getMethods(serviceClass,methodName,_params.length);
			if (candidateMethods==null || candidateMethods.length==0){
				ServiceInvocationException ex = new ServiceInvocationException("Java服务没有名称为"+
						methodName+"的方法，Java服务的类名是[class="+serviceClass.getName()+
						"，输入参数类型是 "+parameterTypesToString(parameterTypes));
				ex.setErrorCode(ServiceInvocationException.OPERATION_NOT_FOUND);

				throw ex;

			}
			parameterTypes = findExactParameterTypes(serviceClass,methodName,candidateMethods,parameterTypes);
			return parameterTypes;
		}
	}
	
	private String parameterTypesToString(Class[] parameterTypes){
		StringBuffer sBuf = new StringBuffer("[");
		for (int i=0;i<parameterTypes.length;i++){
			Class cls = parameterTypes[i];
			if(cls!=NullClass.class){
				sBuf.append(cls.getName());
			}else{
				sBuf.append("null");
			}
			
			if (i<parameterTypes.length-1){
				sBuf.append(",");
			}
		}
		sBuf.append("]");
		return sBuf.toString();
	}
	private Class[] findExactParameterTypes(Class serviceClass,String methodName,Method[] candidateMethods,Class[] actInputParamTypes)throws ServiceInvocationException{
		for (Method m : candidateMethods){
			Class[] candidateParamTypes = m.getParameterTypes();
			int paramLength = candidateParamTypes.length;
			boolean isTheSameParamTypes = true;
			for (int i=0;i<paramLength;i++){
				if (actInputParamTypes[i].getName().equals(NullClass.class.getName())){
					continue;
				}else {
					Class actParamClass = actInputParamTypes[i];
					Class candidateParamClass = candidateParamTypes[i];
					
					if (candidateParamClass.isAssignableFrom(actParamClass)){
						isTheSameParamTypes = true;
					}else{
						isTheSameParamTypes = false;
						break;
					}				
					
				}
			}
			if (isTheSameParamTypes){
				return m.getParameterTypes();
			}
		}
		ServiceInvocationException ex = new ServiceInvocationException("Java服务没有名称为"+
				methodName+"的方法，Java服务的类名是[class="+serviceClass.getName()+
				"，输入参数类型是 "+parameterTypesToString(actInputParamTypes));
		ex.setErrorCode(ServiceInvocationException.OPERATION_NOT_FOUND);
		throw ex;
	}
	/**
	 * 查找名字和参数数量相匹配的method
	 * @param methodName
	 * @param paramsLength
	 * @return
	 */
	private Method[] getMethods(Class serviceClass,String methodName,int paramsLength){
		Method[] allMethods = serviceClass.getMethods();
		List<Method> foundMethods = new ArrayList<Method>();
		for (Method m : allMethods){
			if (m.getName().equals(methodName)){
				Class[] paramTypes = m.getParameterTypes();
				int _tmpParamLength = 0;
				if (paramTypes!=null){
					_tmpParamLength = paramTypes.length;
				}
				if (_tmpParamLength == paramsLength){
					foundMethods.add(m);
				}
			}
		}
		Method[] returnValue = new Method[foundMethods.size()];
		for (int i=0;i<foundMethods.size();i++){
			returnValue[i] = foundMethods.get(i);
		}
		return returnValue;
	}
	
	private class NullClass {

	}
	
}

