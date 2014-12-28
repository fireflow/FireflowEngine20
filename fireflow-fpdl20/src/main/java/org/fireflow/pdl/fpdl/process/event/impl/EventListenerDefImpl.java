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
package org.fireflow.pdl.fpdl.process.event.impl;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class EventListenerDefImpl extends AbstractModelElement implements
		EventListenerDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7035440449471905773L;
	String beanName = null;
	String className = null;
	String bizCategory = null;

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.event.EventListenerDef#getBeanName()
	 */
	public String getListenerBeanName() {
		return beanName;
	}
	
	public void setListenerBeanName(String beanName){
		this.beanName = beanName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.event.EventListenerDef#getBizCategory()
	 */
//	public String getBizCategory() {
//		return bizType;
//	}
//	
//	public void setBizCategory(String bizType){
//		this.bizCategory = bizType;
//	}

	public String getListenerClassName(){
		return className;
	}
	
	public void setListenerClassName(String clzName){
		this.className = clzName;
	}
}
