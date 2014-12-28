/**
 * Copyright 2007-2011 非也
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
package org.firesoa.common.util;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
public class PrioritizedParameterNameDiscoverer implements
		ParameterNameDiscoverer {
	
	public static final PrioritizedParameterNameDiscoverer DEFAULT =
		(new PrioritizedParameterNameDiscoverer())
		.addDiscoverer(new LocalVariableTableParameterNameDiscoverer())
		.addDiscoverer(new JDKReflectParameterNameDiscover());
	
	
	private final List parameterNameDiscoverers = new LinkedList();


	/**
	 * Add a further ParameterNameDiscoverer to the list of discoverers
	 * that this PrioritizedParameterNameDiscoverer checks.
	 */
	public PrioritizedParameterNameDiscoverer addDiscoverer(ParameterNameDiscoverer pnd) {
		this.parameterNameDiscoverers.add(pnd);
		return this;
	}


	public String[] getParameterNames(Method method) {
		//首先应用JSR181
		JSR181ParameterNameDiscover discover = new JSR181ParameterNameDiscover();
		String[] paramNamesFromJSR181 = discover.getParameterNames(method);
		if (ifFindAllParamName(paramNamesFromJSR181)){
			return paramNamesFromJSR181;
		}
		String[] tempResult = null;
		for (Iterator it = this.parameterNameDiscoverers.iterator(); it.hasNext(); ) {
			ParameterNameDiscoverer pnd = (ParameterNameDiscoverer) it.next();
			tempResult = pnd.getParameterNames(method);
			if (tempResult != null) {
				break;
			}
		}
		
		String[] result = new String[paramNamesFromJSR181.length];
		
		for (int i=0;i<result.length;i++){
			String nameFromJSR181 = paramNamesFromJSR181[i];
			String nameFromOthers = (i<tempResult.length)?tempResult[i]:null;
			if (!StringUtils.isEmpty(nameFromJSR181)){
				result[i] = nameFromJSR181;
			}else{
				result[i] = nameFromOthers;
			}
		}
		
		return result;
	}
	
	private boolean ifFindAllParamName(String[] paramNames){
		//检查paramNames中是否有空值，如果有空说明尚有parameter的name没有找到
		if (paramNames==null) return false;
		for (String name : paramNames){
			if (StringUtils.isEmpty(name)){
				return false;
			}
		}
		return true;
	}


}
