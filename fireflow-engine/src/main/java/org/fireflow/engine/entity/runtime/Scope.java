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
package org.fireflow.engine.entity.runtime;

import java.util.Map;
import java.util.Properties;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.exception.InvalidOperationException;

/**
 * @author 非也
 * @version 2.0
 */
public interface Scope {
	public String getScopeId();
	public String getParentScopeId();
	public Object getVariableValue(WorkflowSession session,String name);
	public void setVariableValue(WorkflowSession session ,String name ,Object value)throws InvalidOperationException;
	public void setVariableValue(WorkflowSession session ,String name,Object value,Properties headers)throws InvalidOperationException;
	public Map<String,Object> getVariableValues(WorkflowSession session);
	
	/**
	 * 获得对应的流程元素的Id
	 * @return
	 */
	public String getProcessElementId();
	public String getProcessId();
	public Integer getVersion();
	public String getProcessType();
}
