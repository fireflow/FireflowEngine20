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
package org.fireflow.simulation.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="breakpoint")
@XmlType(name="breakpointType")
@XmlAccessorType(XmlAccessType.FIELD)
public class BreakPoint {
	private String elementId = null;//打断点的ElementId，目前只允许在Transition上打断点
	private String processId;
	
	
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	public boolean equals(Object obj){
		if (obj==null)return false;
		if (!(obj instanceof BreakPoint)){
			return false;
		}
		BreakPoint p = (BreakPoint)obj;
		if (p.getElementId()==null ){
			if (this.getElementId()!=null)return false;
		}
		if (p.getProcessId()==null){
			if(this.getProcessId()!=null) return false;
		}
		if (p.getElementId().equals(this.getElementId()) && p.getProcessId().equals(this.getProcessId())){
			return true;
		}
		return false;
	}
	
	
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((processId == null) ? 0 : processId.hashCode());

		result = prime
				* result
				+ ((elementId == null) ? 0 : elementId
						.hashCode());
		return result;
	}
	
	
}
