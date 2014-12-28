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
package org.fireflow.engine.invocation.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.fireflow.model.servicedef.ServiceDef;
import org.firesoa.common.schema.DOMInitializer;
import org.firesoa.common.schema.NameSpaces;
import org.w3c.dom.Document;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsServiceInvoker implements ServiceInvoker {
//	DocumentFactory docFactory = DocumentFactory.getInstance();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.service.ServiceExecutor#complete(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance,
	 * java.lang.Object)
	 */
//	public void onServiceCompleted(WorkflowSession session,
//			ActivityInstance activityInstance) {
//		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
//				.getRuntimeContext();
//		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(
//				ActivityInstanceManager.class,
//				activityInstance.getProcessType());
//
//		activityInstanceMgr.onServiceCompleted(session, activityInstance);
//
//	}

	public int determineActivityCloseStrategy(WorkflowSession session,
			ActivityInstance activityInstance, Object theActivity, ServiceBinding serviceBinding) {
		return ServiceInvoker.CLOSE_ACTIVITY;
	}

	public boolean invoke(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivity) throws ServiceInvocationException {
		try {
			WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl) session;
			RuntimeContext runtimeContext = sessionLocalImpl
					.getRuntimeContext();

			ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();
			
			ProcessLanguageManager processUtil = runtimeContext.getEngineModule(ProcessLanguageManager.class, activityInstance.getProcessType());
			ServiceDef svc = processUtil.getServiceDef(activityInstance,theActivity, serviceBinding.getServiceId());
			if (svc==null){
				ServiceInvocationException ex = new ServiceInvocationException("没有找到Id为"+serviceBinding.getServiceId()+"的服务");
				ex.setErrorCode(ServiceInvocationException.SERVICE_DEF_NOT_FOUND);
				ex.setActivityInstance(activityInstance);
				throw ex;
			}
			
			// 1、首先获得Service Object
			Object serviceObject = this.getServiceObject(runtimeContext,
					session, activityInstance, serviceBinding,svc,theActivity);
			
			if (serviceObject==null){
				ServiceInvocationException ex = new ServiceInvocationException("无法初始化 service object，Service定义是[name="+svc.getName()+",displayName="+svc.getDisplayName()+"]");
				ex.setErrorCode(ServiceInvocationException.SERVICE_OBJECT_NOT_FOUND);
				ex.setActivityInstance(activityInstance);
				throw new ServiceInvocationException();
			}

			// 2、然后获得被调用的Method
			String methodName = this.getOperationName(runtimeContext,
					session, activityInstance, serviceBinding);
			
			if (methodName==null || methodName.trim().equals("")){
				ServiceInvocationException ex = new ServiceInvocationException("服务没有名称为"+serviceBinding.getOperationName()+"的方法，Service定义是[name="+svc.getName()+",displayName="+svc.getDisplayName()+"]");
				ex.setErrorCode(ServiceInvocationException.OPERATION_NOT_FOUND);
				ex.setActivityInstance(activityInstance);
				throw new ServiceInvocationException();
			}

			
			
			// 3、解析参数
			Object[] params = resolveInputParams(runtimeContext, session,processInstance,
					activityInstance, serviceBinding,svc);
			
			// 4、获得参数类型
			Class[] parameterTypes = this.getParameterTypes(serviceObject.getClass(), methodName, params);

			
			// 5、调用
			System.out.println("===Inside AbsServiceInvoker,serviceObject="+serviceObject);
			System.out.println("===methodName is "+methodName);
			System.out.println("===params is "+ java.util.Arrays.toString(params));
			System.out.println("===params typs is "+ java.util.Arrays.toString(parameterTypes));
			Object result = MethodUtils.invokeMethod(serviceObject, methodName,
						params,parameterTypes);


			// 6、返回值回写到流程系统中
			assignOutputToVariable(runtimeContext, session,processInstance, activityInstance,
					serviceBinding, result);

			return true;
		}
		catch(ServiceInvocationException e){
			if (e.getActivityInstance()==null){
				e.setActivityInstance(activityInstance);
			}
			throw e;
		}
		catch (ScriptException e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		}
		catch (SecurityException e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		} catch (NoSuchMethodException e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		} catch (IllegalArgumentException e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		} catch (IllegalAccessException e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		} catch (InvocationTargetException e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		} 
		catch (Exception e) {
			ServiceInvocationException ex = new ServiceInvocationException(e);
			ex.setErrorCode(findRootCause(e).getClass().getName());
			ex.setActivityInstance(activityInstance);
			throw ex;
		}

	}
	
	private Throwable findRootCause(Throwable e){

		if (e.getCause()==null){
			return e;
		}
		return findRootCause(e.getCause());
	}

	protected abstract Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef serviceDef,Object activity)throws ServiceInvocationException;

	public static Map<String ,Object> resolveInputAssignments(RuntimeContext runtimeContext,
			WorkflowSession session,ProcessInstance processInstance, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef service)throws ScriptException{
		List<Assignment> inputAssignmentList = serviceBinding.getInputAssignments();
//		OperationDef operation = serviceBinding.getOperation();
//		List<Input> inputs = operation.getInputs();

		Map<String, Object> contextVars = ScriptEngineHelper.fulfillScriptContext(session,
				runtimeContext, processInstance, activityInstance);
		
		//初始化xml类型的input输入，所有的xml类型输入用org.w3c.dom.Document表示
		Map<String,Object> inputsContext = new HashMap<String,Object>();
		for (Assignment assignment : inputAssignmentList){
			Expression toExpression = assignment.getTo();
			if (toExpression.getName()==null || toExpression.getName().trim().equals("")){
				throw new ScriptException("The name of the assignment's To-Expression can NOT be empty. More information : the body of the To-Expression is '"+toExpression.getBody()+"';the activity id is "+activityInstance.getNodeId());
			}
			if (toExpression.getDataType()!=null && !NameSpaces.JAVA.getUri().equals(toExpression.getDataType().getNamespaceURI())
					&& inputsContext.get(toExpression.getName())==null){
				Document doc = initDocument(service.getXmlSchemaCollection(),toExpression.getDataType());
				inputsContext.put(toExpression.getName(), doc);
			}
		}
		contextVars.put(ScriptContextVariableNames.INPUTS, inputsContext);
		
		
		Map<String, Object> inputParamValues = ScriptEngineHelper
				.resolveInputParameters(runtimeContext,
						serviceBinding.getInputAssignments(),
						contextVars);		
		
		return inputParamValues;
	}
	
	protected Object[] resolveInputParams(RuntimeContext runtimeContext,
			WorkflowSession session,ProcessInstance processInstance, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef service)throws ScriptException {
		
		Map<String,Object> inputParamValues = resolveInputAssignments(runtimeContext,session,processInstance,
				activityInstance,serviceBinding,service); 
		List<Assignment> inputAssignmentList = serviceBinding.getInputAssignments();
//		OperationDef operation = null;
//		List<Input> inputs = operation.getInputs();
		
		List<Object> args = new ArrayList<Object>();
		for (Assignment assignment : inputAssignmentList) {
			Expression toExpression = assignment.getTo();
			if (inputParamValues!=null && inputParamValues.containsKey(toExpression.getName())){
				Object paramValue = inputParamValues.get(toExpression.getName());
				args.add(paramValue);
			}else{
				args.add(null);
				//TODO 缺省参数需要处理吗？
				//由于所有的接口采用java接口，貌似没有必要。
//				String defaultValueAsString = _input.getDefaultValueAsString();
//				if (StringUtils.isEmpty(defaultValueAsString)){
//					args.add(null);
//				}else {
//					Object obj = JavaDataTypeConvertor.dataTypeConvert(_input.getDataType(), defaultValueAsString, _input.getDataPattern());
//					args.add(obj);
//				}
			}

		}
		return args.toArray();
	}

	protected void assignOutputToVariable(RuntimeContext runtimeContext,
			WorkflowSession session, ProcessInstance processInstance,ActivityInstance activityInstance,
			ServiceBinding serviceBinding, Object result) throws ScriptException{
//		OperationDef operation = serviceBinding.getOperation();
		List<Assignment> outputAssignments = serviceBinding.getOutputAssignments();
		if (outputAssignments==null || outputAssignments.size()==0)return ;//无赋值操作
		
		Map<String,Object> scriptContext = new HashMap<String,Object>();
		Map<String, Object> outputsResults = new HashMap<String, Object>();

		Assignment assignment = outputAssignments.get(0);
		Expression fromExp = assignment.getFrom();
		outputsResults.put(fromExp.getName(), result);
		scriptContext.put(ScriptContextVariableNames.OUTPUTS,
					outputsResults);
		
		//System.out.println("======待输出的结果是===="+outputsResults);
		

		ScriptEngineHelper.assignOutputToVariable(session,runtimeContext,
				processInstance,activityInstance, outputAssignments,
				scriptContext);
	}
	
	/**
	 * 根据Schema创建org.w3c.dom.Document;
	 * TODO 对于Schema中的Choice指示器，将不创建子元素，所以在这种情况下，有可能出现问题。
	 * @param xmlSchemaCollection
	 * @param elementQName
	 * @return
	 */
	protected static  Document initDocument(XmlSchemaCollection xmlSchemaCollection,
			QName rootElementQName)throws ScriptException{
		try{
			Document doc = DOMInitializer.generateDocument(xmlSchemaCollection, rootElementQName);
			
			return doc;
		}catch(ParserConfigurationException e){
			throw new ScriptException(e);
		}
	}
	
//	protected org.dom4j.QName javaxQName4Dom4JQName(javax.xml.namespace.QName qNameIn){
//		org.dom4j.Namespace ns = new org.dom4j.Namespace(qNameIn.getPrefix(),qNameIn.getNamespaceURI());
//		org.dom4j.QName qName = new org.dom4j.QName(qNameIn.getLocalPart(),ns);
//		return qName;
//	}
	
	
	protected String getOperationName(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding)throws ServiceInvocationException{
		return serviceBinding.getOperationName();
	}
	
	protected abstract Class[] getParameterTypes(Class serviceClass,String methodName, Object[] params)throws ServiceInvocationException;
}
