/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.processlanguage;

import java.io.InputStream;
import java.util.Map;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.WebservicePublishException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;

/**
 * 流程定义服务。
 * @author 非也，nychen2000@163.com
 *
 */
public interface ProcessLanguageManager extends RuntimeContextAware,EngineModule {
	public String getProcessType();
	public void setProcessType(String processType);
	
	/************************************************/
	/**  一、流程语言对engine的特定扩展                                                             *****/
	/************************************************/
	public Map<String, EngineModule> getEngineModules();
	public void setEngineModules(Map<String,EngineModule> _engineModules);
	
	public <T extends EngineModule> T getEngineModule(Class<T> interfaceClass);
	
	
	/************************************************/
	/**  二、操作流程定义的方法                                                                                       *****/
	/************************************************/
	
	/**
	 * 返回流程的入口元素的Id，例如：FPDL (2.0)流程返回的是main_subflow的Id
	 * @param workflowProcessId
	 * @param version
	 * @param processType
	 * @return
	 */
	public String getProcessEntryId(String workflowProcessId, int version,String processType);
	
	public String serializeProcess2Xml(Object process) throws InvalidModelException;
	
	public Object deserializeXml2Process(InputStream inStream)throws InvalidModelException;
	
	public ProcessRepository serializeProcess2ProcessRepository(Object process)throws InvalidModelException;
	
	public ProcessDescriptor generateProcessDescriptor(Object process);
	
	/**
	 * 获得ServiceBinding对象
	 * @param activity 特定流程定义语言的Activity对象
	 * @return
	 * @throws InvalidModelException
	 */
    public ServiceBinding getServiceBinding(Object activity)throws InvalidModelException;
    
    /**
     * 根据serviceBinding.getServiceId()和activity，找到ServiceDef对象
     * @param activity
     * @param serviceBinding
     * @return
     */
    public ServiceDef getServiceDef(ActivityInstance activityInstance,Object activity,String serviceId);
    
    /**
     * 根据resourceId和Activity获得resourceDef对象
     * @param activityInstance
     * @param activity
     * @param resourceId
     * @return
     */
    public ResourceDef getResourceDef(ActivityInstance activityInstance,Object activity,String resourceId);
    /**
     * 获得resource binding对象
     * @param activity 特定流程定义语言的Activity对象
     * @return
     * @throws InvalidModelException
     */
    public ResourceBinding getResourceBinding(Object activity)throws InvalidModelException;
    
    /**
     * 提取子流程或者Activity的流程变量（Property对象）。
     * @param workflowDefinitionElement SubProcess 或者 Activity对象，由于Engine对流程定义语言的结构一无所知，所以需要交给特定流程定义语言的ProcessUtil工具类来返回Property对象。 
     * @param propertyName property的名字
     * @return
     */
    public Property getProperty(Object workflowDefinitionElement,String propertyName);
    
    /**
     * 根据条件查找Activity
     * @param processKey
     * @param subflowId
     * @param activityId
     * @return
     * @throws InvalidModelException
     */
    public Object findActivity(ProcessKey processKey,String subflowId, String activityId)throws InvalidModelException;
    

    public Object findSubProcess(ProcessKey processKey,String subflowId )throws InvalidModelException;
    
	/************************************************/
	/**  三、发布流程流程服务                                                                                          *****/
	/************************************************/
	/**
	 * 将所有的回调发布成WebService
	 */
	public void publishAllProcessServices()throws WebservicePublishException;
	
}
