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
package org.fireflow.pvm.kernel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 非也
 * @version 2.0
 */
public class BookMark {
	public static final String ERROR_CODE = "ERROR_CODE";
	public static final String COMPENSATION_CODE = "COMPENSATION_CODE";
	public static final String SOURCE_TOKEN = "SOURCE_TOKEN";
	
	private Token token = null;
	private ExecutionEntrance executionEntrance = null;
	private Map<String,Object> extraArgs = new HashMap<String,Object>();
	public BookMark(){
		
	}
	public void setToken(Token tk){
		token = tk;
	}
	public Token getToken(){
		return token;
	}
	
	public ExecutionEntrance getExecutionEntrance(){
		return executionEntrance;
	}
	
	public void setExecutionEntrance(ExecutionEntrance entrance){
		this.executionEntrance = entrance;
	}
	
	public void setExtraArg(String name,Object value){
		extraArgs.put(name, value);
	}
	
	public Object getExtraArg(String name){
		return extraArgs.get(name);
	}
}
