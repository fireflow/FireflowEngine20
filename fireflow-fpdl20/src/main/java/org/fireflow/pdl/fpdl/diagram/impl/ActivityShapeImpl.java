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

import org.fireflow.pdl.fpdl.diagram.ActivityShape;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.StartNodeShape;
import org.fireflow.pdl.fpdl.diagram.figure.impl.RectangleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ActivityShapeImpl extends AbsProcessNodeShapeImpl implements
		ActivityShape {
	
	List<StartNodeShape> attachedStartNodes = new ArrayList<StartNodeShape>();
	
	public ActivityShapeImpl(String id){
		this.id = id;

		this.figure = new RectangleImpl();
	}
	public DiagramElement findChild(String diagramElementId){
		
		
		for (DiagramElement diagramElm : attachedStartNodes){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}

		return null;
	}
	
	public List<DiagramElement> getChildren(){
		List<DiagramElement> children = new ArrayList<DiagramElement>();
		children.addAll(this.attachedStartNodes);
		return children;
	}
	
	public DiagramElement findChildByWorkflowElementId(String workflowElementId){

		
		for (DiagramElement diagramElm : attachedStartNodes){
			if (diagramElm.getWorkflowElementRef()==null){
				continue;
			}
			if (workflowElementId.equals(diagramElm.getWorkflowElementRef().getId())){
				return diagramElm;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.ActivityShape#getAttachedStartNodes()
	 */
	public List<StartNodeShape> getAttachedStartNodeShapes() {
		return attachedStartNodes;
	}
	
	public void addAttachedStartNodeShape(StartNodeShape startNodeShape){
		this.attachedStartNodes.add(startNodeShape);
		((AbsDiagramElement)startNodeShape).setParent(this);
	}
}
