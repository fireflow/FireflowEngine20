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
package org.fireflow.simulation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.environment.Environment;
import org.fireflow.server.WorkflowEngineService;
import org.fireflow.server.WorkflowServer;
import org.fireflow.simulation.springutil.ContextLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SimulatorRunner {
	public static final String WEBSERVICE_PORT_ARG = "webservicePort"; 
	
	protected ApplicationContext springApplicationContext = null;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		springApplicationContext = applicationContext;

	}

	/**
	 * 命令行参数：
	 * 所有命令航参数都是spring context配置文件的路径
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args)throws SimulationException {
		//1、输出环境信息
		Properties props = System.getProperties();
		//java.home
		String javahome = props.getProperty("java.home");
		printOperationTip("Java Home是："+javahome);
		
		//sun.boot.class.path
		String bootClassPath = props.getProperty("sun.boot.class.path");
		printOperationTip("Boot Class Path 是：");
		printOperationTip(bootClassPath);
		
		//java.class.path
		String classPath =  props.getProperty("java.class.path");
		printOperationTip("Class Path 是：");
		printOperationTip(classPath);
		
		//file.encoding和sun.jnu.encoding
		printOperationTip("服务端编码格式：file.encoding="+System.getProperty("file.encoding")+"; sun.jnu.encoding="+System.getProperty("sun.jnu.encoding"));
		
		//3、解析spring context
		List<String> locationsList = new ArrayList<String>();
		Collections.addAll(locationsList, args);
		//locationsList.add("classpath:/default_fireflow_config_4_simulation/springContext_FireflowSimulator.xml");
		
		String[] locations = locationsList.toArray(new String[]{});

		ApplicationContext springAppCtx = null;
		try{
			springAppCtx = ContextLoader.loadContext(locations);

		}catch(Exception e){
			throw new SimulationException(e);
		}
		
		//
		RuntimeContext rtCtx = (RuntimeContext)springAppCtx.getBean(RuntimeContext.Fireflow_Runtime_Context_Name);
		Environment env = rtCtx.getDefaultEngineModule(Environment.class);
		
		printOperationTip("Fire Workflow Server监听端口是:"+env.getWebservicePort());
		printOperationTip("Fire Workflow Server工作目录是:"+env.getWorkDir());
		
		WorkflowServer server = (WorkflowServer)springAppCtx.getBean("fireflowServer");
		if (server.isEngineServiceEnabled()){
			String address = "http://"+env.getWebserviceIP()+":"
			+Integer.toString(env.getWebservicePort())
			+env.getWebserviceContextPath()+WorkflowEngineService.SERVICE_LOCAL_NAME;
			printOperationTip("Fire Workflow Engine Webservice地址是:"+address);
			
			printOperationTip("Fire Workflow Server成功启动。");
		}else{
			printOperationTip("Fire Workflow Engine Webservice被关闭，可通过FireflowContext-Override.properties文件的“fireflowServer.engineServiceEnabled”属性开启。");
			
			printOperationTip("Fire Workflow Server已启动，但由于Fire Workflow Engine Webservice功能被关闭，***无法进行模拟！****");
		}


	}
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void printOperationTip(String tip){
		System.out.println("FireBPM "+format.format(new Date())+"> "+tip);
	}
}
