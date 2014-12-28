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

import org.fireflow.pdl.fpdl.diagram.CommentShape;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.GroupShape;
import org.fireflow.pdl.fpdl.diagram.LaneShape;
import org.fireflow.pdl.fpdl.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl.diagram.figure.Rectangle;
import org.fireflow.pdl.fpdl.diagram.figure.impl.RectangleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class LaneShapeImpl extends AbsNodeShapeImpl implements LaneShape {
	private List<ProcessNodeShape> workflowNodes = new ArrayList<ProcessNodeShape>();
	private List<CommentShape> comments = new ArrayList<CommentShape>();
	private List<GroupShape> groups = new ArrayList<GroupShape>();
	
	public LaneShapeImpl(String id){
		this.id = id;
		
		Rectangle plane = new RectangleImpl();
		
		plane.getBounds().setWidth(560);
		plane.getBounds().setHeight(400);
		
		this.figure = plane;
	}
	

	public List<ProcessNodeShape> getProcessNodeShapes() {
		return workflowNodes;
	}


	public void addProcessNodeShape(ProcessNodeShape shape) {
		workflowNodes.add(shape);
		((AbsDiagramElement)shape).setParent(this);
	}

	
	public List<CommentShape> getComments(){
		return comments;
	}
	public void addComment(CommentShape commentShape){
		this.comments.add(commentShape);
		((AbsDiagramElement)commentShape).setParent(this);
	}
	

	public List<GroupShape> getGroups(){
		return this.groups;
	}
	public void addGroup(GroupShape groupShape){
		this.groups.add(groupShape);
		((AbsDiagramElement)groupShape).setParent(this);
	}
	public DiagramElement findChild(String diagramElementId){
		if (diagramElementId.equals(this.id)){
			return this;
		}

		
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
			
			DiagramElement tmp = diagramElm.findChild(diagramElementId);
			if (tmp!=null){
				return tmp;
			}
		}
		
		for (DiagramElement diagramElm : comments){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}
		

		for (DiagramElement diagramElm : groups){
			if (diagramElm.getId().equals(diagramElementId)){
				return diagramElm;
			}
			if (diagramElm instanceof GroupShape){
				DiagramElement tmp = diagramElm.findChild(diagramElementId);
				if (tmp!=null){
					return tmp;
				}
			}
		}
		return null;
	}
	
	public List<DiagramElement> getChildren(){
		List<DiagramElement> children = new ArrayList<DiagramElement>();
		children.addAll(this.workflowNodes);
		children.addAll(comments);
		children.addAll(groups);
		return children;
	}
	
	public DiagramElement findChildByWorkflowElementId(String workflowElementId){
		if (workflowElementId==null)return null;
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElm.getWorkflowElementRef()==null ){
				continue;
			}
			if (workflowElementId.equals(diagramElm.getWorkflowElementRef().getId())){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : groups) {
			DiagramElement tmp = diagramElm.findChildByWorkflowElementId(workflowElementId);
			if (tmp != null) {
				return tmp;
			}
		}
		
		return null;
	}
}
