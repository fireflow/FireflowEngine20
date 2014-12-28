/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.service.callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Expression;
import org.firesoa.common.schema.DOMInitializer;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;

/**
 * 响应WebService的回调请求
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@WebServiceProvider()
@ServiceMode(value=Service.Mode.PAYLOAD)
public class ProcessServiceProvider implements Provider<Source>{
	protected static final 	TransformerFactory transformerFactory = TransformerFactory
	.newInstance();
	@Resource
	protected WebServiceContext wsContext;
	
	protected RuntimeContext workflowRuntimeContext = null;
	
	protected CallbackService callbackService = null;
	
	protected ServiceBinding serviceBinding = null;
	
	protected TransactionTemplate transactionTemplate = null;
	
	protected boolean startNewProcess = false;
	
	protected String processId = null;

	protected String processType = null;
	
//	protected Integer processVersion = null;
	
	/**
	 * @return the transactionTemplate
	 */
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	/**
	 * @param transactionTemplate the transactionTemplate to set
	 */
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	public ServiceBinding getServiceBinding() {
		return serviceBinding;
	}

	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}

	public void setWorkflowRuntimeContext(RuntimeContext ctx){
		this.workflowRuntimeContext = ctx;
	}
	
	public RuntimeContext getWorkflowRuntimeContext(){
		return this.workflowRuntimeContext;
	}
	
	public CallbackService getCallbackService() {
		return callbackService;
	}

	public void setCallbackService(CallbackService callbackService) {
		this.callbackService = callbackService;
	}

	
	public ProcessServiceProvider(){
		
	}
	
	
	
	public boolean isStartNewProcess() {
		return startNewProcess;
	}
	public void setStartNewProcess(boolean startNewProcess) {
		this.startNewProcess = startNewProcess;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
//	public Integer getProcessVersion() {
//		return processVersion;
//	}
//	public void setProcessVersion(Integer processVersion) {
//		this.processVersion = processVersion;
//	}
	
	
	public Source invoke(Source request) {
		Expression correlation = callbackService.getCorrelation();
		
		final QName responseRootElementQName = new QName(callbackService.getTargetNamespaceUri(),this.serviceBinding.getOperationName()+"Response");
		
		//下面是测试代码
		/*
		try{
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			// transformer.transform()方法 将 XML Source转换为 Result
			transformer.transform(request, new StreamResult(
					outputStream));
			System.out.println( outputStream.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		
		final Document requestDOM ;
		try{
			Transformer transformer = transformerFactory.newTransformer();
			DOMResult domResult = new DOMResult();
			transformer.transform(request, domResult);
			requestDOM = (Document)domResult.getNode();//从reuqest中获得DOM对象	
		}catch(TransformerException e){
			throw new WebServiceException("Can NOT transform request to DOM.",e);
		}


		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(workflowRuntimeContext, FireWorkflowSystem.getInstance());
		if (!startNewProcess){//调用某个中间节点
			if (correlation==null || StringUtils.isEmpty(correlation.getBody())){
				throw new WebServiceException("The correlation can NOT be empty; the callbackservice is "+callbackService.getName());
			}
			//1、通过serviceId和service version获得candidate activityInstance
			WorkflowQuery<ActivityInstance> query = session.createWorkflowQuery(ActivityInstance.class);
			List<ActivityInstance> candidates = query.add(Restrictions.eq(ActivityInstanceProperty.SERVICE_ID, callbackService.getId()))
				.add(Restrictions.eq(ActivityInstanceProperty.SERVICE_VERSION, callbackService.getVersion()))
				.add(Restrictions.eq(ActivityInstanceProperty.STATE, ActivityInstanceState.RUNNING))
				.list();
			
			//2、匹配correlation
			//correlation是一个bool表达式，如：processVars.var1==xpath(requestDom,'method1Request/id')
			ProcessInstance theProcessInstance = null;
			ActivityInstance theActivityInstance = null;
			if (candidates!=null && candidates.size()>0){
				for (ActivityInstance activityInstance : candidates){					
					ProcessInstance processInstance = activityInstance.getProcessInstance(session);					
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(processInstance);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);

					Map<String, Object> varContext = ScriptEngineHelper
							.fulfillScriptContext(session,
									workflowRuntimeContext, processInstance,
									activityInstance);
					varContext.put(ScriptContextVariableNames.INPUTS,
							requestDOM);
					
					Object result = ScriptEngineHelper.evaluateExpression(
							workflowRuntimeContext, correlation, varContext);
					if (result != null && (result instanceof Boolean)) {
						if ((Boolean) result) {
							theActivityInstance = activityInstance;
							theProcessInstance = processInstance;
							break;
						}
					}

				}
			}
			
			final ActivityInstance theMatchedActivityInstance = theActivityInstance;//匹配上的activityInstance
			final ProcessInstance theMatchedProcessInstance = theProcessInstance;
			
			if (theMatchedActivityInstance!=null){			
				
				//1、首先设置currentProcessInstance和CurrentActivityInstance
				((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(theMatchedActivityInstance);
				((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(theMatchedProcessInstance);
				
				try{
					this.transactionTemplate.execute(new TransactionCallback(){
						public Object doInTransaction(TransactionStatus status) {
							//2、设置流程变量
							List<Assignment> inputAssignments_ = serviceBinding.getInputAssignments();
							Map<String,Object> scriptContext = new HashMap<String,Object>();
							scriptContext.put(ScriptContextVariableNames.INPUTS,
									requestDOM);
							try {
								ScriptEngineHelper.assignOutputToVariable(session,workflowRuntimeContext,
										theMatchedProcessInstance,theMatchedActivityInstance, inputAssignments_,
										scriptContext);
							} catch (ScriptException e) {
								throw new RuntimeException("Can NOT assign inputs to process instance varialbes,the callback service is  "+callbackService.getName(),e);
							}

							//3、执行closeActivity的操作	
							ActivityInstanceManager actInstMgr = workflowRuntimeContext.getEngineModule(ActivityInstanceManager.class, theMatchedProcessInstance.getProcessType());
							actInstMgr.onServiceCompleted(session, theMatchedActivityInstance);
							
							return null;
						}
						
					});
				}catch(TransactionException e){
					throw new WebServiceException(e);
				}
				//4、返回结果
				try{
					Map<String, Object> allTheVars = ScriptEngineHelper.fulfillScriptContext(session,workflowRuntimeContext, theMatchedProcessInstance, theMatchedActivityInstance);
					List<Assignment> outputAssignments = serviceBinding.getOutputAssignments();
					Document doc = DOMInitializer.generateDocument(callbackService.getXmlSchemaCollection(),responseRootElementQName);
					allTheVars.put(ScriptContextVariableNames.OUTPUTS, doc);
					Map<String, Object> tmp =  ScriptEngineHelper.resolveAssignments(workflowRuntimeContext, outputAssignments, allTheVars);
					Document resultDOM = (Document)tmp.get(ScriptContextVariableNames.OUTPUTS);
					
					return new DOMSource(resultDOM);
				}catch(ScriptException e){
					throw new WebServiceException("Can NOT assign process instance varialbes to output,the callback service is  "+callbackService.getName(),e);
				} catch (ParserConfigurationException e) {
					throw new WebServiceException("Can NOT init output DOM,the callback service is  "+callbackService.getName(),e);
				}
			}else{
				throw new WebServiceException("Process instance NOT found for the conditions as follows,service id='"+callbackService.getId()+"' and service version='"+callbackService.getVersion()+"' and correlation='"+correlation.getBody()+"'");
			}
		}
		
		
		else{//启动新的流程实例

			//1、获得输入变量，解析bizId
			final Map<String,Object> processVars;
			final String bizId ;
			try{
				List<Assignment> inputAssignments_ = serviceBinding.getInputAssignments();
				Map<String,Object> scriptContext = new HashMap<String,Object>();
				scriptContext.put(ScriptContextVariableNames.INPUTS,
						requestDOM);
				
				Map<String, Object> temp = ScriptEngineHelper.resolveAssignments(workflowRuntimeContext, inputAssignments_, scriptContext);
				processVars = (Map<String, Object>)temp.get(ScriptContextVariableNames.PROCESS_VARIABLES);

				Map<String, Object> varContext = new HashMap<String,Object>();
				varContext.put(ScriptContextVariableNames.INPUTS,	requestDOM);
		
				Object result = ScriptEngineHelper.evaluateExpression(workflowRuntimeContext, correlation, varContext);
				bizId=result==null?null:result.toString();
			}catch(ScriptException e){
				throw new WebServiceException("Can NOT assign inputs to process instance varialbes,the callback service is  "+callbackService.getName(),e);
			}

			//2、启动流程
			ProcessInstance processInstance = null;
			try {
				processInstance = (ProcessInstance)transactionTemplate.execute(new TransactionCallback(){
					public Object doInTransaction(TransactionStatus status) {
						WorkflowStatement stmt = session.createWorkflowStatement(processType);						
						
						ProcessInstance procInst = null;
						try {
							procInst = stmt.startProcess(processId,bizId, processVars);
						} catch (InvalidModelException e1) {
							throw new RuntimeException("Start process instance error! The callback service is "+callbackService.getName()+"; the process is "+processId, e1);
						} catch (WorkflowProcessNotFoundException e1) {
							throw new RuntimeException("Start process instance error! The callback service is "+callbackService.getName()+"; the process is "+processId, e1);

						} catch (InvalidOperationException e1) {
							throw new RuntimeException("Start process instance error! The callback service is "+callbackService.getName()+"; the process is "+processId, e1);

						}
						return procInst;
					}
					
				});
			} catch (TransactionException e) {
				throw new WebServiceException(e);
			}


				
			//3、返回结果
			try{
				Map<String, Object> allTheVars = ScriptEngineHelper.fulfillScriptContext(session,workflowRuntimeContext, processInstance, null);
				List<Assignment> outputAssignments = serviceBinding.getOutputAssignments();
				//  首先初始化返回的DOM对象
				Document doc = DOMInitializer.generateDocument(callbackService.getXmlSchemaCollection(),responseRootElementQName);
				allTheVars.put(ScriptContextVariableNames.OUTPUTS, doc);
				Map<String, Object> tmp =  ScriptEngineHelper.resolveAssignments(workflowRuntimeContext, outputAssignments, allTheVars);
				Document resultDOM = (Document)tmp.get(ScriptContextVariableNames.OUTPUTS);
				
				return new DOMSource(resultDOM);
			}catch(ScriptException e){
				throw new WebServiceException("Can NOT assign process instance varialbes to output,the callback service is  "+callbackService.getName(),e);
			} catch (ParserConfigurationException e) {
				throw new WebServiceException("Can NOT init output DOM,the callback service is  "+callbackService.getName(),e);
			}


		}

	}
	

}
