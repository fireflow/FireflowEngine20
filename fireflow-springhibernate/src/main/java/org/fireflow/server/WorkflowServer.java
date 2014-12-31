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
package org.fireflow.server;

import java.util.List;

import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.exception.WebservicePublishException;
import org.fireflow.engine.modules.environment.Environment;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowServer {
	private static final Log log = LogFactory.getLog(WorkflowServer.class);
	
    /**
     * 是否以发布WorkflowEngineService这个Webservice，让流程引擎接受远程调用。<br>
     * 
     * WebService的地址由org.fireflow.engine.modules.environment.Environment.getWebserviceContextPath()决定。
     * 
     */
    private boolean engineServiceEnabled = false;
    
	private RuntimeContext runtimeContext = null;
    /**
     * @param ctx
     */
    public void setRuntimeContext(RuntimeContext ctx){
    	runtimeContext = ctx;
    }
    
    /**
     * @return
     */
    public RuntimeContext getRuntimeContext(){
    	return runtimeContext;
    }
    
    public boolean isEngineServiceEnabled() {
		return engineServiceEnabled;
	}



	public void setEngineServiceEnabled(boolean b) {
		this.engineServiceEnabled = b;
	}
	
	/**
	 * 启动Server，使得Workflow Engine的方法可以被远程调用（webservice方式），
	 * 同时将系统中的call-back-service发布成webservice
	 */
	public void start(){
		try{
			//1、首先发布WorkflowEngineService
			if (engineServiceEnabled){
				publishWorkflowEngineService();
			}
			
			
			//2、然后发布ProcessService
			List<ProcessLanguageManager> processLanguageManagers = runtimeContext.getProcessLanguages();
			for (ProcessLanguageManager manager : processLanguageManagers){
				manager.publishAllProcessServices();
			}
		}catch(WebservicePublishException e){
			log.error(e.getMessage(),e);
		}

	}
	
	public void stop(){
		
	}
	
	public void publishWorkflowEngineService() throws WebservicePublishException {
		Environment evn = runtimeContext
				.getDefaultEngineModule(Environment.class);
		String contextPath = evn.getWebserviceContextPath();
		if (!contextPath.startsWith("/")){
			contextPath = "/"+contextPath;
		}
		String address = "http://"+evn.getWebserviceIP()+":"
					+Integer.toString(evn.getWebservicePort())
					+contextPath;

		WorkflowEngineService implementor = runtimeContext.getDefaultEngineModule(WorkflowEngineService.class);
		
		Endpoint.publish(address, implementor);
	}
}
