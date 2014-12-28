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
package org.fireflow.simulation.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Service;

import org.fireflow.client.WorkflowSession;
import org.fireflow.server.WorkflowEngineService;
import org.fireflow.simulation.FireflowSimulator;
import org.fireflow.simulation.client.impl.SimulatorSessionImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SimulatorSessionFactory{
	public static final String SIMULATOR_DEFAULT_USER_NAME = "FireflowSimulator";
	private static Map<String,Service> workflowServerServiceMap = new HashMap<String,Service>();
	
	public static WorkflowSession createSimulatorSession(String url)throws MalformedURLException{
		return createSimulatorSession(url,SIMULATOR_DEFAULT_USER_NAME,"");
	}
	
	public static WorkflowSession createSimulatorSession(String url,String userId,String password)throws MalformedURLException{
		Service svc = createWorkflowServerService(url);
		FireflowSimulator serverStub = svc.getPort(WorkflowEngineService.PORT_QNAME,FireflowSimulator.class);//获得WorkflowServer的远程代理
		//System.out.println("====serverStub is "+serverStub.hashCode());
		WorkflowSession sessionStub = serverStub.login(userId, password);
		
		SimulatorSessionImpl remoteSession = new SimulatorSessionImpl();
		remoteSession.setSessionId(sessionStub.getSessionId());
		remoteSession.setCurrentUser(sessionStub.getCurrentUser());
		remoteSession.setWorkflowServer(serverStub);
		return remoteSession;
	}
	
	
	protected static Service createWorkflowServerService(String url)throws MalformedURLException{
		if (!url.endsWith("/")){
			url = url+"/";
		}
		String wsdlAddress = url+WorkflowEngineService.SERVICE_LOCAL_NAME+"?wsdl";

		if (workflowServerServiceMap.get(wsdlAddress)==null){
			synchronized(SimulatorSessionFactory.class){
				if (workflowServerServiceMap.get(wsdlAddress)==null){
					URL wsdlURL = new URL(wsdlAddress);
					Service workflowServerService = Service.create(wsdlURL,WorkflowEngineService.SERVICE_QNAME);
					workflowServerServiceMap.put(wsdlAddress, workflowServerService);
				}
			}
		}
		return workflowServerServiceMap.get(wsdlAddress);

	}
}
