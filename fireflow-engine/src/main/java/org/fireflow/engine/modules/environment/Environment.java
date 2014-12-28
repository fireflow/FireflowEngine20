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
package org.fireflow.engine.modules.environment;

import org.fireflow.engine.context.EngineModule;

/**
 * 该接口抽象了Fire workflow引擎的运行环境。
 * 如：workdir路径，webservice context path等。
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface Environment extends EngineModule{
	public static final String WEBSERVICE_PORT_PROPERTY_NAME = "FireflowWebservicePort";
	public static final int DEFAULT_WEBSERVICE_PORT = 9099;
	public static final String DEFAULT_WEBSERVICE_IP = "127.0.0.1";
	public static final String DEFAULT_WEBSERVICE_CONTEXT_PATH = "/FireWorkflowServices/";
	
	/**
	 * Fire workflow的工作目录名
	 */
	public static final String WORK_DIR_NAME = "fireflow_workspace";
	
	/**
	 * Fire Workflow引擎的工作目录，用于存储临时性文件
	 * @return
	 */
	public String getWorkDir();
	
	
	/**
	 * Fire workflow发布的Webservice的上下文路径URL。
	 * 如：/MyApp/ff_webservices/
	 * @return
	 */
	public String getWebserviceContextPath();
	
	public String getWebserviceIP();
	
	public int getWebservicePort();
}
