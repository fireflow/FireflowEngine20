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
package org.fireflow.engine.modules.processlanguage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
//import org.fireflow.engine.context.TransactionTemplateAware;
import org.fireflow.engine.exception.EngineException;
//import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbsProcessLanguageManager implements ProcessLanguageManager {
	protected RuntimeContext ctx = null;
	
	
	/**
	 * 流程定义语言的名称，取值如XPDL,FPDL,
	 */
	private String processType = null;
	
	
	/**
	 * 该语言特定的引擎模块
	 */
	private Map<String,EngineModule> engineModules = new HashMap<String,EngineModule>();
	
//	private TransactionTemplate transactionTemplate = null;
//
//	/**
//	 * @return the transactionTemplate
//	 */
//	public TransactionTemplate getTransactionTemplate() {
//		return transactionTemplate;
//	}
//	/**
//	 * @param transactionTemplate the transactionTemplate to set
//	 */
//	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
//		this.transactionTemplate = transactionTemplate;
//	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return this.ctx;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		this.ctx = ctx;

	}
	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.EngineModule#init(org.fireflow.engine.context.RuntimeContext)
	 */
	public void init(RuntimeContext runtimeContext) throws EngineException {
		Iterator<EngineModule> iterator = this.engineModules.values().iterator();
		while(iterator.hasNext()){
			EngineModule module = iterator.next();
    		if (module instanceof RuntimeContextAware){
    			((RuntimeContextAware)module).setRuntimeContext(runtimeContext);
    		}
    		/*
    		if (module instanceof TransactionTemplateAware){
    			((TransactionTemplateAware)module).setTransactionTemplate(runtimeContext.getTransactionTemplate());
    		}*/
		}
		
		if (engineModules!=null && engineModules.size()>0){
			Iterator<EngineModule> iter = this.engineModules.values().iterator();
			while (iter.hasNext()){
				EngineModule module = iter.next();
				module.init(runtimeContext);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getProcessType()
	 */
	public String getProcessType() {
		return this.processType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#setProcessType(java.lang.String)
	 */
	public void setProcessType(String processType) {
		this.processType = processType;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getEngineModules()
	 */
	public Map<String, EngineModule> getEngineModules() {
		return this.engineModules;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#setEngineModules(java.util.Map)
	 */
	public void setEngineModules(Map<String, EngineModule> _engineModules) {
    	if (_engineModules==null){
    		return ;
    	}
    	this.engineModules.putAll(_engineModules);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getEngineModule(java.lang.Class)
	 */
	public <T extends EngineModule> T getEngineModule(Class<T> interfaceClass) {
    	EngineModule module = this.engineModules.get(interfaceClass.getName());
    	return (T)module;
	}

}
