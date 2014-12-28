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
package org.fireflow.server.support;

import java.util.Map;
import java.util.Properties;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.exception.InvalidOperationException;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ScopeBean implements Scope {
	private String scopeId = null;
	private String processType = null;
	private String processId = null;
	private Integer version = null;
	private String parentScopeId = null;
	private String processElementId = null;

	public static ScopeBean fromScopeObject(Scope scope){
		ScopeBean bean = new ScopeBean();
		bean.setScopeId(scope.getScopeId());
		bean.setParentScopeId(scope.getParentScopeId());
		bean.setProcessElementId(scope.getProcessElementId());
		bean.setProcessId(scope.getScopeId());
		bean.setProcessType(scope.getProcessType());
		bean.setVersion(scope.getVersion());
		return bean;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getScopeId()
	 */
	public String getScopeId() {
		return scopeId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getParentScopeId()
	 */
	public String getParentScopeId() {
		return parentScopeId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getVariableValue(org.fireflow.client.WorkflowSession, java.lang.String)
	 */
	public Object getVariableValue(WorkflowSession session, String name) {
		throw new UnsupportedOperationException("不支持该方法，此Bean仅用作Webservice参数传递。");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#setVariableValue(org.fireflow.client.WorkflowSession, java.lang.String, java.lang.Object)
	 */
	public void setVariableValue(WorkflowSession session, String name,
			Object value) throws InvalidOperationException {
		throw new UnsupportedOperationException("不支持该方法，此Bean仅用作Webservice参数传递。");

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#setVariableValue(org.fireflow.client.WorkflowSession, java.lang.String, java.lang.Object, java.util.Map)
	 */
	public void setVariableValue(WorkflowSession session, String name,
			Object value, Properties headers)
			throws InvalidOperationException {
		throw new UnsupportedOperationException("不支持该方法，此Bean仅用作Webservice参数传递。");

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getVariableValues(org.fireflow.client.WorkflowSession)
	 */
	public Map<String, Object> getVariableValues(WorkflowSession session) {
		throw new UnsupportedOperationException("不支持该方法，此Bean仅用作Webservice参数传递。");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getProcessElementId()
	 */
	public String getProcessElementId() {
		return processElementId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getProcessId()
	 */
	public String getProcessId() {
		return processId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getVersion()
	 */
	public Integer getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.Scope#getProcessType()
	 */
	public String getProcessType() {
		return processType;
	}
	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public void setParentScopeId(String parentScopeId) {
		this.parentScopeId = parentScopeId;
	}
	public void setProcessElementId(String processElementId) {
		this.processElementId = processElementId;
	}

	
}
