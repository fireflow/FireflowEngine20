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

import java.util.List;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;



/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CallbackService extends AbstractServiceDef implements ServiceDef{
	private Expression correlation = null;//关联条件，当startProcess==False时有效
//	private Boolean startProcess = Boolean.FALSE;//是否新的流程实例
//	private String workflowProcessId = null;//被启动的流程的id
//	private int workflowProcessVersion = -1;//被启动的流程的版本，-1表示最新版本
//	private String workflowProcessType = "FPDL";
	
	public CallbackService(){
		this.invokerClassName = "org.fireflow.service.callback.CallbackInvoker";
		this.parserClassName = "org.fireflow.service.callback.CallbackServiceParser";
	}
	
	/**
	 * 设置关联条件，关联条件是一个bool表达式；如：
	 * processInstance.bizId==inputs.bizId
	 * @return
	 */
	public Expression getCorrelation(){
		return this.correlation;
	}
	
	public void setCorrelation(Expression expression){
		this.correlation = expression;
	}
//	
//	/**
//	 * 判断该CallbackService是否启动流程实例
//	 * @return
//	 */
//	public Boolean isStartProcess(){
//		return startProcess;
//	}
//	
//	public void setStartProcess(Boolean b){
//		startProcess = b;
//	}
//	
//	public String getWorkflowProcessId(){
//		return this.workflowProcessId;
//	}
//	
//	public void setWorkflowProcessId(String id){
//		this.workflowProcessId = id;
//	}
//	
//	public Integer getWorkflowProcessVersion(){
//		return this.workflowProcessVersion;
//	}
//	
//	public void setWorkflowProcessVersion(Integer v){
//		this.workflowProcessVersion = v;
//	}
//	
//	public String getWorkflowProcessType(){
//		return this.workflowProcessType;
//	}
//	
//	public void setWorkflowProcessType(String processType){
//		this.workflowProcessType = processType;
//	}
	
	/**
	 * 所有的属性设置完毕后，组装XmlSchemaCollections
	 */
	public void afterPropertiesSet()throws Exception{
		super.afterPropertiesSet();
		if (this.schemaCollection==null){
			this.schemaCollection = new XmlSchemaCollection();
		}
		
		if (this.getInterface()==null)return;
		List<OperationDef> operationDefList = this.getInterface().getOperations();
		if (operationDefList==null || operationDefList.size()==0)return ;
		
		XmlSchema xmlschema = new XmlSchema(this.getTargetNamespaceUri(),this.getName()+"_"+this.getVersion()+".xsd", schemaCollection);
		xmlschema.setElementFormDefault(XmlSchemaForm.QUALIFIED);
		xmlschema.setAttributeFormDefault(XmlSchemaForm.UNQUALIFIED);
						
		for (OperationDef operationDef : operationDefList){
			//构造Request消息类型
			List<Input> inputsList = operationDef.getInputs();
			if (inputsList!=null && inputsList.size()>0){
				XmlSchemaSequence inputsTypeSequence = new XmlSchemaSequence();
				for (Input input : inputsList){
					XmlSchemaElement inputElement = new XmlSchemaElement(xmlschema,
							false);
					inputElement.setName(input.getName());
					inputElement.setSchemaTypeName(input.getDataType());
					inputsTypeSequence.getItems().add(inputElement);
				}
				
				XmlSchemaComplexType requestType = new XmlSchemaComplexType(
						xmlschema, true);
				requestType.setName(operationDef.getOperationName() + "RequestType");
				requestType.setParticle(inputsTypeSequence);
				
				XmlSchemaElement requestElement = new XmlSchemaElement(xmlschema,
						true);
				requestElement.setName(operationDef.getOperationName()+"Request");
				requestElement.setSchemaTypeName(requestType.getQName());
			}
			
			//构造response消息类型
			List<Output> outputsList = operationDef.getOutputs();
			if (outputsList!=null && outputsList.size()>0){
				XmlSchemaSequence outputsTypeSequence = new XmlSchemaSequence();
				for (Output output : outputsList){
					XmlSchemaElement outputElement = new XmlSchemaElement(xmlschema,
							false);
					outputElement.setName(output.getName());
					outputElement.setSchemaTypeName(output.getDataType());
					outputsTypeSequence.getItems().add(outputElement);
				}
				
				XmlSchemaComplexType responseType = new XmlSchemaComplexType(
						xmlschema, true);
				responseType.setName(operationDef.getOperationName() + "ResponseType");
				responseType.setParticle(outputsTypeSequence);
				
				XmlSchemaElement requestElement = new XmlSchemaElement(xmlschema,
						true);
				requestElement.setName(operationDef.getOperationName()+"Response");
				requestElement.setSchemaTypeName(responseType.getQName());
			}
		}
		
	}
}
