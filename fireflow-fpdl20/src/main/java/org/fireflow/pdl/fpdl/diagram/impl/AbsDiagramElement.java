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
package org.fireflow.pdl.fpdl.diagram.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.ModelElement;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.figure.Figure;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbsDiagramElement implements DiagramElement {
//	protected String sn = UUID.randomUUID().toString();
	protected String id = null;
	protected ModelElement workflowElementRef = null;
	protected Figure figure = null;
	protected DiagramElement parent = null;

//	public String getSn(){
//		return sn;
//	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.DiagramElement#getId()
	 */
	public String getId() {
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.DiagramElement#getWorkflowElementRef()
	 */
	public ModelElement getWorkflowElementRef() {
		
		return this.workflowElementRef;
	}
	
	public void setWorkflowElementRef(ModelElement wfElmId){
		this.workflowElementRef = wfElmId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.DiagramElement#getShape()
	 */
	public Figure getFigure() {
		return this.figure;
	}
	
//	public void setFigure(Figure sp){
//		this.figure = sp;
//	}
	
	public DiagramElement findChild(String id){
		return null;
	}
	public DiagramElement findChildByWorkflowElementId(String workflowElementId){
		return null;
	}
	
	public List<DiagramElement> getChildren(){
		return null;
	}
	
	public boolean equals(Object obj){
		if (obj==null || !(obj instanceof DiagramElement)){
			return false;
		}
		if (this==obj)return true;
		String id = ((DiagramElement)obj).getId();
		if (StringUtils.isEmpty(id)){
			return false;
		}
		if (id.equals(this.getId())){
			return true;
		}

		return false;
	}
	
	public int hashCode(){
		
		String id = this.getId();
		if (id!=null){
			return id.hashCode();
		}else{
			return 0;
		}
	}
	
	public void setParent(DiagramElement p){
		this.parent = p;
	}
	
	public DiagramElement getParent(){
		return this.parent;
	}
}
