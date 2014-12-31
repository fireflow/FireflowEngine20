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
package org.fireflow.engine.modules.persistence;

import java.util.List;

import org.fireflow.engine.entity.runtime.Variable;

/**
 * @author 非也
 * @version 2.0
 */
public interface VariablePersister extends Persister {
	/**
	 * 根据流程变量的名称返回该流程变量。<br><br>	 * 
	 * 流程变量可以是流程实例级别的，也可以是活动实例级别的，
	 * 所以scopeId可以是ProcessInstanceId,也可以是ActivityInstanceId
	 * @param scopeId
	 * @param name
	 * @return
	 */
	public Variable findVariable(String scopeId,String name);
	
	/**
	 * 根据scopeId返回属于该scope的所有流程变量。<b<br><br>* 
	 * 流程变量可以是流程实例级别的，也可以是活动实例级别的，
	 * 所以scopeId可以是ProcessInstanceId,也可以是ActivityInstanceId
	 * @param scopeId
	 * @return
	 */
	public List<Variable> findVariables(String scopeId);
	
	/**
	 * 删除所有的流程变量，该方法提供给Simulator使用，业务系统一般用不上。
	 */
	public void deleteAllVariables();
	
//	public Object findVariableValue(String scopeId,String name);
//	public Map<String ,Object> findVariableValues(String scopeId);
	
//	public Variable setVariable(Scope scope,String name,Object value);
	
}
