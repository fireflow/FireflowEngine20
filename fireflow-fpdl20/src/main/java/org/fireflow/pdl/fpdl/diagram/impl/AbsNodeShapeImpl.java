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

import java.util.ArrayList;
import java.util.List;

import org.fireflow.pdl.fpdl.diagram.AssociationShape;
import org.fireflow.pdl.fpdl.diagram.MessageFlowShape;
import org.fireflow.pdl.fpdl.diagram.NodeShape;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbsNodeShapeImpl extends AbsDiagramElement implements
		NodeShape {
	List<AssociationShape> leavingAssociationShapes = new ArrayList<AssociationShape>();
	List<AssociationShape> enteringAssociationShapes = new ArrayList<AssociationShape>();
	
	List<MessageFlowShape> leavingMessageFlowShapes = new ArrayList<MessageFlowShape>();
	List<MessageFlowShape> enteringMessageFlowShapes = new ArrayList<MessageFlowShape>();
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.NodeShape#getLeavingAssociationShapes()
	 */
	public List<AssociationShape> getLeavingAssociationShapes() {
		return leavingAssociationShapes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.NodeShape#getEnteringAssociationShapes()
	 */
	public List<AssociationShape> getEnteringAssociationShapes() {
		return enteringAssociationShapes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.NodeShape#getLeavingMessageFlowShapes()
	 */
	public List<MessageFlowShape> getLeavingMessageFlowShapes() {
		return leavingMessageFlowShapes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.NodeShape#getEnteringMessageFlowShapes()
	 */
	public List<MessageFlowShape> getEnteringMessageFlowShapes() {
		return enteringMessageFlowShapes;
	}
	
	public String getTitle(){
		return this.figure==null?"":this.figure.getTitle();
	}
	public void setTitle(String title){
		if (this.figure!=null){
			this.figure.setTitle(title);
		}
	}
	
	public String getContent(){
		return this.figure==null?"":figure.getContent();
	}
	public void setContent(String content){
		if (this.figure!=null){
			this.figure.setContent(content);
		}
	}

}
