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
package org.firesoa.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.jws.WebParam;

import org.apache.commons.lang.StringUtils;
/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class JSR181ParameterNameDiscover implements ParameterNameDiscoverer {

	/* (non-Javadoc)
	 * @see org.firesoa.common.util.ParameterNameDiscoverer#getParameterNames(java.lang.reflect.Method)
	 */
	public String[] getParameterNames(Method method) {
		Annotation[][] annotations = method.getParameterAnnotations();
		int paramNum = annotations.length;
		String[] paramNames = new String[paramNum];
		if (paramNum==0) return paramNames;
		for (int i=0;i<paramNum;i++){
			Annotation[] annotationArr = annotations[i];
			if (annotationArr.length==0){
				
				//如果采用WebParam 命名参数，则所有参数必须都使用WebParam；如果，任何一个参数没有使用WebParam，则不再解析
				paramNames[i] = null;
				continue;
			}
			javax.jws.WebParam obj = (javax.jws.WebParam)this.findWebParam(annotationArr);
			if (obj==null){
				paramNames[i] = null;
				continue;
			}
			String name = obj.name();
			if (StringUtils.isEmpty(name)){
				paramNames[i] = null;
				continue;
			}
			paramNames[i] = name;
		}
		
		return paramNames;
	}
	
	private Annotation findWebParam(Annotation[] annotationArr){
		for (Annotation annotation : annotationArr){
			if (annotation.annotationType().equals(WebParam.class)){
				
				return annotation;
			}
		}
		return null;
	}

}
