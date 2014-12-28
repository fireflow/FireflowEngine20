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
package org.fireflow.engine.modules.formsystem;

import javax.xml.namespace.QName;

import org.firesoa.common.schema.NameSpaces;

/**
 * 表单URL参数，数据类型仅限于该接口定义的类型常量。
 * Date类型的参数需要转换成String
 * 
 * 
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface UrlParameter {
	public static final QName STRING_TYPE = new QName(NameSpaces.JAVA.getUri(),"java.lang.String",NameSpaces.JAVA.getPrefix());
	public static final QName INT_TYPE = new QName(NameSpaces.JAVA.getUri(),"int",NameSpaces.JAVA.getPrefix());
	public static final QName BOOLEAN_TYPE = new QName(NameSpaces.JAVA.getUri(),"boolean",NameSpaces.JAVA.getPrefix());
	public static final QName FLOAT_TYPE = new QName(NameSpaces.JAVA.getUri(),"float",NameSpaces.JAVA.getPrefix());
	public static final QName DOUBLE_TYPE = new QName(NameSpaces.JAVA.getUri(),"double",NameSpaces.JAVA.getPrefix());
	public static final QName SHORT_TYPE = new QName(NameSpaces.JAVA.getUri(),"short",NameSpaces.JAVA.getPrefix());
	public static final QName LONG_TYPE = new QName(NameSpaces.JAVA.getUri(),"long",NameSpaces.JAVA.getPrefix());
	
	public void setName(String nm);
	public String getName();
	
	public void setDataType(QName dt);
	public QName getDataType();
	
    public String getInitialValueAsString() ;


    public void setInitialValueAsString(String initialValue) ;

}
