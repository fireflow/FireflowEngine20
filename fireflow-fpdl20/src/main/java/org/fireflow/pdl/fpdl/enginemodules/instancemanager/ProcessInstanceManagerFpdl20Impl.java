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
package org.fireflow.pdl.fpdl.enginemodules.instancemanager;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.model.data.Property;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceManagerFpdl20Impl extends AbsProcessInstanceManager {
	private Log log = LogFactory.getLog(ProcessInstanceManagerFpdl20Impl.class);
	
	/**
	 * 启动WorkflowProcess实际上是启动main subflow,该方法供WorkflowStatement调用
	 */
//	public ProcessInstance startProcess(WorkflowSession session,String workflowProcessId, int version,String processType,
//			String bizId, Map<String, Object> variables)
//			throws InvalidModelException,
//			WorkflowProcessNotFoundException, InvalidOperationException{
//		assert (session instanceof WorkflowSessionLocalImpl);
//		
//		session.setAttribute(InternalSessionAttributeKeys.BIZ_ID, bizId);
//		session.setAttribute(InternalSessionAttributeKeys.VARIABLES, variables);
//		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
//		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);			
//		//启动WorkflowProcess实际上是启动该WorkflowProcess的main_flow，
//		kernelManager.startPObject(session, new PObjectKey(workflowProcessId,version,processType,workflowProcessId+"."+WorkflowProcess.MAIN_PROCESS_NAME));
//		
//		return session.getCurrentProcessInstance();
//	}
	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#createProcessInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, java.lang.String, java.util.Map, org.fireflow.engine.entity.repository.ProcessDescriptor, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowProcess,String processEntryId, ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance) {
		WorkflowProcess fpdlProcess = (WorkflowProcess)workflowProcess;
		SubProcess subProcess = (SubProcess)fpdlProcess.getLocalSubProcess(processEntryId);
		WorkflowProcess process = (WorkflowProcess)subProcess.getParent();
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		CalendarService calendarService = context.getDefaultEngineModule(CalendarService.class);
		User u = sessionLocal.getCurrentUser();
		
		ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
		processInstance.setProcessId(descriptor.getProcessId());
		processInstance.setVersion(descriptor.getVersion());
		processInstance.setProcessType(descriptor.getProcessType());
		processInstance.setSubProcessId(subProcess.getId());
//		processInstance.setBizId(bizId);
		processInstance.setProcessName(process.getName());
		String displayName = process.getDisplayName();
		processInstance.setProcessDisplayName(StringUtils.isEmpty(displayName)?process.getName():displayName);

		if (subProcess.getParent()!=null){
			processInstance.setPackageId(process.getPackageId());
		}
		
		processInstance.setSubProcessName(subProcess.getName());
		processInstance.setSubProcessDisplayName(StringUtils.isEmpty(subProcess.getDisplayName())?subProcess.getName():subProcess.getDisplayName());
		
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		Date now = calendarService.getSysDate();
		processInstance.setCreatedTime(now);
		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		
		if (parentActivityInstance!=null){
			processInstance.setParentActivityInstanceId(parentActivityInstance.getId());
			processInstance.setParentProcessInstanceId(parentActivityInstance.getProcessInstanceId());
			processInstance.setParentScopeId(parentActivityInstance.getScopeId());
		}
		
		if (subProcess.getDuration()!=null && subProcess.getDuration().getValue()>0){
			Date expiredDate = calendarService.dateAfter(now, subProcess.getDuration());
			processInstance.setExpiredTime(expiredDate);
		}else{
			processInstance.setExpiredTime(null);
		}
		
		PersistenceService persistenceService = this.getRuntimeContext().getEngineModule(PersistenceService.class, descriptor.getProcessType());
		ProcessInstancePersister processInstancePersister = persistenceService.getProcessInstancePersister();
		
		processInstancePersister.saveOrUpdate(processInstance);
		
		//发布事件
		this.fireProcessInstanceEvent(session, processInstance, subProcess, ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_CREATED);
		
		return processInstance;
	}


	protected void initProcessInstanceVariables(ProcessInstance processInstance,Object process,Map<String,Object> initVariables){
		PersistenceService persistenceService = this.getRuntimeContext().getEngineModule(PersistenceService.class, processInstance.getProcessType());
		VariablePersister variablePersister = persistenceService.getVariablePersister();
		WorkflowProcess fpdlProcess = (WorkflowProcess)process;
		SubProcess subProcess = (SubProcess)fpdlProcess.getLocalSubProcess(processInstance.getSubProcessId());
		List<Property> processProperties = subProcess.getProperties();
		if (processProperties!=null){
			for (Property property:processProperties){
				String valueAsStr = property.getInitialValueAsString();
				Object value = null;
				QName dataType = property.getDataType();
				if (valueAsStr!=null ){
					//数字类型,bool类型自动赋给初始值					
					
					if (valueAsStr.trim().equals("") && dataType!=null 
							&& NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())){
						if (JavaDataTypeConvertor.isByte(dataType.getLocalPart()) || 
								JavaDataTypeConvertor.isDouble(dataType.getLocalPart()) ||
								JavaDataTypeConvertor.isInt(dataType.getLocalPart()) ||
								JavaDataTypeConvertor.isFloat(dataType.getLocalPart()) ||
								JavaDataTypeConvertor.isLong(dataType.getLocalPart()) ||
								JavaDataTypeConvertor.isShort(dataType.getLocalPart())){
							valueAsStr = "0";
						}
						else if (JavaDataTypeConvertor.isBoolean(dataType.getLocalPart())){
							valueAsStr = "false";
						}
					}
					try {
//						System.out.println("valueAsStr=="+valueAsStr);
						if (  JavaDataTypeConvertor.isPrimaryDataType(dataType.getLocalPart())){
							value = JavaDataTypeConvertor.convertToJavaObject(property.getDataType(), valueAsStr, property.getDataPattern());
						}
						else{
							value = null;
						}

					} catch (ClassCastException e) {
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);
					} catch (ClassNotFoundException e) {
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);
					}catch(NumberFormatException e){
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);

					}catch(Exception e){
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);

					}
				}
				//从initVariables中获取value，覆盖初始值
				if (initVariables!=null){
					Object tmpValue = initVariables.remove(property.getName());
//					System.out.println("tmpValue is "+(tmpValue==null?"null":tmpValue));
//					System.out.println("tmpValue type is "+(tmpValue==null?"null":tmpValue.getClass().getName()));
					if (tmpValue!=null){
						if (tmpValue instanceof String){
							tmpValue = ((String)tmpValue).trim();
							//数字类型,bool类型自动赋给初始值
							
							if (tmpValue.equals("") && dataType!=null 
									&& NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())){
								if (JavaDataTypeConvertor.isByte(dataType.getLocalPart()) || 
										JavaDataTypeConvertor.isDouble(dataType.getLocalPart()) ||
										JavaDataTypeConvertor.isInt(dataType.getLocalPart()) ||
										JavaDataTypeConvertor.isFloat(dataType.getLocalPart()) ||
										JavaDataTypeConvertor.isLong(dataType.getLocalPart()) ||
										JavaDataTypeConvertor.isShort(dataType.getLocalPart())){
									tmpValue = "0";
								}
								else if (JavaDataTypeConvertor.isBoolean(dataType.getLocalPart())){
									tmpValue = "false";
								}
							}
						}

						try {
							value = JavaDataTypeConvertor.dataTypeConvert(property.getDataType(), tmpValue, property.getDataPattern());
						} catch (ClassCastException e) {
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);
						} catch (ClassNotFoundException e) {
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);
						}catch(NumberFormatException e){
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);

						}catch(Exception e){
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+processInstance.getSubProcessId()+", variableName="+property.getName(), e);

						}
					}
				}
//				System.out.println("value === "+value);
				createVariable(variablePersister,processInstance,property.getName(),value,property.getDataType());
			}
		}
		//如果还有未预先定义的initVariable，则继续仍然保存到变量列表中
		if (initVariables!=null && !initVariables.isEmpty()){
			Iterator<String> keySet = initVariables.keySet().iterator();
			while (keySet.hasNext()){
				String key = keySet.next();
				Object value = initVariables.get(key);
				createVariable(variablePersister,processInstance,key,value,null);
			}
		}

	}
	
	private void createVariable(VariablePersister variablePersister,
			ProcessInstance processInstance,String name ,Object value,QName dataType){
//		System.out.println("===流程变量"+name+"="+value);
		VariableImpl v = new VariableImpl();
		((AbsVariable)v).setScopeId(processInstance.getScopeId());
		((AbsVariable)v).setName(name);
		((AbsVariable)v).setProcessElementId(processInstance.getProcessElementId());
		((AbsVariable)v).setPayload(value);
		
		if (dataType!=null){
			v.setDataType(dataType);
		}else{//通过value判断dataType
			if (value==null){
				v.setDataType(new QName(NameSpaces.JAVA.getUri(),String.class.getName()));
			}else{
				((AbsVariable)v).setDataType(new QName(NameSpaces.JAVA.getUri(),value.getClass().getName()));
			}
		}
		
		if (value instanceof org.w3c.dom.Document){

			v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.w3c.dom.Document");
		}else if (value instanceof org.dom4j.Document){
			v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.dom4j.Document");
		}

		((AbsVariable)v).setProcessId(processInstance.getProcessId());
		((AbsVariable)v).setVersion(processInstance.getVersion());
		((AbsVariable)v).setProcessType(processInstance.getProcessType());
		

		variablePersister.saveOrUpdate(v);
	}

}
