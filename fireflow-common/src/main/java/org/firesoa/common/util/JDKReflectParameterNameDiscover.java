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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 返回类似arg0,arg1,arg2命名模式的参数名称。
 * @author 非也 www.firesoa.com
 * 
 *
 */
public class JDKReflectParameterNameDiscover implements ParameterNameDiscoverer {

	/* (non-Javadoc)
	 * @see org.firesoa.common.util.ParameterNameDiscoverer#getParameterNames(java.lang.reflect.Method)
	 */
	public String[] getParameterNames(Method method) {
		
		Type[] types = method.getGenericParameterTypes();
		if (types!=null && types.length>=0){
			String[] paramNames = new String[types.length];
			for (int i=0;i<types.length;i++){
				paramNames[i]="arg"+i;
			}
			
			return paramNames;
		}
		return null;
	}

}
