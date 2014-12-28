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
package org.fireflow.engine.modules.script;

/**
 * 流程提供的脚本引擎上下文变量名称
 * 
 * @author 非也
 * @version 2.0
 */
public interface ScriptContextVariableNames {
	//引擎环境
	public static final String WORKFLOW_SESSION = "workflowSession";
	public static final String RUNTIME_CONTEXT = "runtimeContext";
	
	//引擎对象
	public static final String CURRENT_PROCESS_INSTANCE = "currentProcessInstance";
	public static final String CURRENT_ACTIVITY_INSTANCE = "currentActivityInstance";
	public static final String PROCESS_VARIABLES = "processVars";//流程变量
	public static final String ACTIVITY_VARIABLES = "activityVars";//活动作用域的变量
	public static final String SESSION_ATTRIBUTES = "sessionAttributes";//session中的属性
	public static final String INPUTS = "inputs";//输入
	public static final String OUTPUTS = "outputs";//输出
	
	//其他
//	public static final String DATE_TIME_UTIL = "dateTimeUtil";
	
}
