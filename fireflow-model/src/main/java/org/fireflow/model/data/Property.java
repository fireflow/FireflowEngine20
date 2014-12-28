/**
 * Copyright 2007-2008 非也
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
package org.fireflow.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.model.ModelElement;


/**
 * 流程变量
 * @author 非也,nychen2000@163.com
 */
public interface Property extends DataElement,ModelElement{
    /**
     * 返回初始值
     * @return 初始值
     */
	public String getInitialValueAsString() ;
    
    /**
     * 设置初始值
     * @param initialValue 初始值
     */
	public void setInitialValueAsString(String initialValue);
	
    /**
     * 返回数据的pattern，目前主要用于日期类型。如 yyyyMMdd 等等。
     * @return
     */
	public String getDataPattern();
	
    /**
     * 设置数据的pattern，目前主要用于日期类型。如 yyyyMMdd 等等。
     * @param dataPattern
     */
	public void setDataPattern(String dataPattern) ;
	
//	/**
//	 * 生命期，session级别的或processInstance级别的。
//	 * session级别的不用存储到数据库，processInstance级别的都必须存储到数据库。
//	 * 不存储到数据库可以提高系统性能。
//	 * @return
//	 */
//	public String getLifetime();
//	
//	public String setLifetime(String lifeTime);
}
