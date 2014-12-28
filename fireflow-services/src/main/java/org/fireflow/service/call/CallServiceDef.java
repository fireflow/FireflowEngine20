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
package org.fireflow.service.call;

import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CallServiceDef extends AbstractServiceDef implements ServiceDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6959933380266010700L;

	public static final int THE_LATEST_VERSION = 0;//0表示流程库中最新版本流程

	String packageId = null;
	String processId = null;
	String subflowId = null;
	Integer processVersion = THE_LATEST_VERSION;
	
	public CallServiceDef(){
		this.invokerClassName = CallServiceInvoker.class.getName();
		this.parserClassName = CallServiceParser.class.getName();
		
		CallServiceInterfaceDef callInterfaceDef = new CallServiceInterfaceDef();
		this.setInterface(callInterfaceDef);
		
		//构造缺省的interface和operation
		/*
		CommonInterfaceDef commonInterface = new CommonInterfaceDef();
		commonInterface.setName(INTERFACE_NAME);
		this.setInterface(commonInterface);
		
		OperationDefImpl op = new OperationDefImpl();
		op.setOperationName(OPERATION_NAME);
		commonInterface.getOperations().add(op);
		*/
	}
	
	public String getSubProcessId(){
		return subflowId;
	}
	
	public void setSubProcessId(String subflowId){
		this.subflowId = subflowId;
	}
	
	public String getProcessId(){
		return processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}
	
	public Integer getProcessVersion(){
		return processVersion;
	}
	
	public void setProcessVersion(Integer v){
		processVersion = v;
	}

	/**
	 * @return the packageId
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	
	
}
