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
package org.fireflow.engine.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.UnsupportedProcessTypeException;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.pvm.kernel.KernelException;

/**
 * RuntimeContext是Fire workflow Engine的总线。所有的服务都挂接在这个总线上，并通过这个总线获取。<br/>
 * RuntimeContext也是业务代码调用工作流引擎的入口，通过runtimeContext.getWorkflowSession()获得IWorkflowSession 对象，
 * 然后通过IWorkflowSession调用各种工作流实例对象及其API。<br/>
 * 
 * context管理的各种服务
 * @author 非也,nychen2000@163.com
 *
 */
public class RuntimeContext {
	private static RuntimeContext instance = null;
	
	public static RuntimeContext getInstance(){
		if (instance==null){
			instance = new RuntimeContext();
		}
		return instance;
	}
	/**
	 * FPDL2.0缺省需要Spring作为容器，该常量定义transaction template的名字
	 */
//	public static final String Spring_Transaction_Template_Name = "springTransactionTemplate";
	
	/**
	 * FPDL2.0 缺省采用Hibernate作为存储层ORM方案，该常量定义hibernate session factory的名字
	 */
//	public static final String Spring_Hibernate_Session_Factory_Name = "hibernateSessionFactory";
	
	/**
	 * FPDL2.0缺省采用Spring作为容器，该常量定义Transaction Manager的名字
	 */
//	public static final String Spring_Transaction_Manager_Name = "springTransactionManager";
	
	
	/**
	 * Fire workflow runtime context的bean name(或者id)
	 */
	public static final String Fireflow_Runtime_Context_Name = "fireflowRuntimeContext";
	
	
	private List<ProcessLanguageManager> processLanguageRegistry = new ArrayList<ProcessLanguageManager>();
	private Map<String,EngineModule> defaultEngineModules = new HashMap<String,EngineModule>();
	
    /**
     * 是否已经初始化
     */
    private boolean isInitialized = false;
    
    /**
     * 是否打开流程跟踪，如果打开，则会往T_FF_HIST_TRACE表中插入纪录。
     */
    private boolean enableTrace = false;
    
//    private TransactionTemplate transactionTemplate = null;
    
    private String defaultScript = "JEXL";//缺省的脚本语言
    private String defaultProcessType="FPDL";//缺省的流程类型
    
    private RuntimeContext() {
    }



    public boolean isIsInitialized() {
        return isInitialized;
    }






	/**
     * 初始化方法
     * @throws EngineException
     * @throws KernelException
     */
    public void initialize() throws EngineException, KernelException {
        if (!isInitialized) {
        	if (this.defaultEngineModules!=null){
            	Iterator<String> keys = this.defaultEngineModules.keySet().iterator();

            	while (keys.hasNext()){
            		String s = keys.next();
            		EngineModule module = defaultEngineModules.get(s);
            		if (module instanceof RuntimeContextAware){
            			((RuntimeContextAware)module).setRuntimeContext(this);
            		}
            		/* 需要将spring 分离,20140618
            		if (module instanceof TransactionTemplateAware){
            			((TransactionTemplateAware)module).setTransactionTemplate(this.transactionTemplate);
            		}*/
            	}
        	}
			if (this.processLanguageRegistry != null) {
				for (ProcessLanguageManager extension : processLanguageRegistry) {
					if (extension instanceof RuntimeContextAware) {
						((RuntimeContextAware) extension)
								.setRuntimeContext(this);
					}
					/*  需要将spring分离
					if (extension instanceof TransactionTemplateAware) {
						((TransactionTemplateAware) extension)
								.setTransactionTemplate(this.transactionTemplate);
					}
					*/
				}
			}
        	if (this.defaultEngineModules.size()>0){
        		Iterator<EngineModule> iter = this.defaultEngineModules.values().iterator();
        		while(iter.hasNext()){
        			EngineModule module = iter.next();
        			module.init(this);
        		}
        	}
        	
        	if (processLanguageRegistry.size()>0){
        		Iterator<ProcessLanguageManager> processDefExtReg = this.processLanguageRegistry.iterator();
        		while(processDefExtReg.hasNext()){
        			ProcessLanguageManager ext = processDefExtReg.next();
        			ext.init(this);
        		}
        	
        	}
        	
            isInitialized = true;
        }
    }

    /**
     * 初始化所有的工作流网实例
     * @throws KernelException
     */
//    protected void initAllNetInstances() throws KernelException {
//
//    }

    public boolean isEnableTrace() {
        return enableTrace;
    }

    public void setEnableTrace(boolean enableTrace) {
        this.enableTrace = enableTrace;
    }
    
    

    /**
	 * @return the defaultScript
	 */
	public String getDefaultScript() {
		return defaultScript;
	}



	/**
	 * @param defaultScript the defaultScript to set
	 */
	public void setDefaultScript(String defaultScript) {
		this.defaultScript = defaultScript;
	}


	private ProcessLanguageManager findProcessLanguageManager(String processType){
		if (processType==null)return null;
		if (processLanguageRegistry!=null && processLanguageRegistry.size()>0){
			for (ProcessLanguageManager mgr : processLanguageRegistry){
				if (processType.equals((mgr.getProcessType()))){
					return mgr;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EngineModule> T getEngineModule(Class<T> interfaceClass,String processType){	

		ProcessLanguageManager pdlExtension = this.findProcessLanguageManager(processType);
    	if (pdlExtension==null){
    		throw new UnsupportedProcessTypeException("The definition language "+processType+" is unsupported!");
    	}
    	
		if (ProcessLanguageManager.class.getName().equals(interfaceClass.getName())){
			return (T)pdlExtension;
		}
    	
    	EngineModule module = pdlExtension.getEngineModule(interfaceClass);
    	if (module==null){
    		module = this.defaultEngineModules.get(interfaceClass.getName());
    	}
    	return (T)module;
    }
    
    @SuppressWarnings("unchecked")
	public <T extends EngineModule> T getDefaultEngineModule(Class<T> interfaceClass){    	
    	EngineModule module = null;

    	module = this.defaultEngineModules.get(interfaceClass.getName());

    	return (T)module;
    }
    	
    public ScriptEngine getScriptEngine(String expressionLanguageName){
    	ScriptEngineManager manager = new ScriptEngineManager();
    	ScriptEngine engine = manager.getEngineByName(expressionLanguageName);
    	
    	return engine;
    }
    
    public List<ProcessLanguageManager> getProcessLanguages(){
    	return this.processLanguageRegistry;
    }
    
    public Map<String,EngineModule> getDefaultEngineModules(){
    	return defaultEngineModules;
    }
    
    public void setDefaultEngineModules(Map<String,EngineModule> _engineModules){
    	this.defaultEngineModules = _engineModules;
    }
    
    
    public void setProcessLanguages(List<ProcessLanguageManager> pdlExtensions){
    	processLanguageRegistry = pdlExtensions;

    }
    

	/**
	 * @return the defaultProcessType
	 */
	public String getDefaultProcessType() {
		return defaultProcessType;
	}



	/**
	 * @param defaultProcessType the defaultProcessType to set
	 */
	public void setDefaultProcessType(String defaultProcessType) {
		this.defaultProcessType = defaultProcessType;
	}
	
//	public void setTransactionTemplate(TransactionTemplate template){
//		this.transactionTemplate = template;
//	}
//	
//	public TransactionTemplate getTransactionTemplate(){
//		return this.transactionTemplate;
//	}
}
