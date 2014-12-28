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
import org.fireflow.pdl.fpdl.diagram.CommentShape;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.GroupShape;
import org.fireflow.pdl.fpdl.diagram.MessageFlowShape;
import org.fireflow.pdl.fpdl.diagram.PoolShape;
import org.fireflow.pdl.fpdl.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl.diagram.TransitionShape;
import org.fireflow.pdl.fpdl.process.SubProcess;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class DiagramImpl extends AbsDiagramElement implements Diagram {
	private String direction = Diagram.HORIZONAL;
	private Boolean snapEnabled = true;
	private Boolean gridEnabled = false;
	private Boolean rulerEnabled = false;
	
	private List<TransitionShape> transitions = new ArrayList<TransitionShape>();
	private List<ProcessNodeShape> workflowNodes = new ArrayList<ProcessNodeShape>();

	private List<GroupShape> groups = new ArrayList<GroupShape>();
	private List<PoolShape> pools = new ArrayList<PoolShape>();
	private List<MessageFlowShape> messageFlows = new ArrayList<MessageFlowShape>();
	private List<CommentShape> comments = new ArrayList<CommentShape>();
	private List<AssociationShape> associations = new ArrayList<AssociationShape>();

	public DiagramImpl(String id,SubProcess subProcess){
		this.id = id;
		this.workflowElementRef = subProcess;
	}
	
	public DiagramElement findChild(String diagramElementId){
		if (diagramElementId.equals(this.id)){
			return this;
		}
		for (DiagramElement diagramElm : transitions){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
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
		for (DiagramElement diagramElm : associations){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}
		for (DiagramElement diagramElm : comments){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : messageFlows){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : pools){
			if (diagramElementId.equals(diagramElm.getId())){
				return diagramElm;
			}
			if (diagramElm instanceof PoolShape){
				DiagramElement tmp = diagramElm.findChild(diagramElementId);
				if (tmp!=null){
					return tmp;
				}
			}
		}
		
		for (DiagramElement diagramElm : groups){
			if (diagramElementId.equals(diagramElm.getId())){
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

	
	public DiagramElement findChildByWorkflowElementId(String workflowElementId){
		if (workflowElementRef==null)return null;
		if (workflowElementId.equals(this.workflowElementRef.getId())){
			return this;
		}
		for (DiagramElement diagramElm : transitions){
			if (diagramElm.getWorkflowElementRef()==null){
				continue;
			}
			if (workflowElementId.equals(diagramElm.getWorkflowElementRef().getId())){
				return diagramElm;
			}
		}
		
		for (DiagramElement diagramElm : workflowNodes){
			if (diagramElm.getWorkflowElementRef()==null){
				continue;
			}
			if (workflowElementId.equals(diagramElm.getWorkflowElementRef().getId())){
				return diagramElm;
			}
			DiagramElement tmp = diagramElm.findChildByWorkflowElementId(workflowElementId);
			if (tmp!=null){
				return tmp;
			}
		}
		
		for (DiagramElement diagramElm : groups){
			DiagramElement tmp = diagramElm.findChildByWorkflowElementId(workflowElementId);
			if (tmp!=null){
				return tmp;
			}
		}
		
		for (DiagramElement diagramElm : pools){
			DiagramElement tmp = diagramElm.findChildByWorkflowElementId(workflowElementId);
			if (tmp!=null){
				return tmp;
			}
		}
		return null;
	}	

	public List<TransitionShape> getTransitions() {
		return transitions;
	}


	public void addTransition(TransitionShape transitionShape) {
		transitions.add(transitionShape);
		((AbsDiagramElement)transitionShape).setParent(this);
	}


	public List<ProcessNodeShape> getProcessNodeShapes() {		
		return workflowNodes;
	}


	public void addProcessNodeShape(ProcessNodeShape shape) {
		workflowNodes.add(shape);	
		((AbsDiagramElement)shape).setParent(this);
	}

	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#getDirection()
	 */
	public String getDirection() {
		return this.direction;
	}
	
	public void setDirection(String d){
		this.direction = d;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#getPools()
	 */
	public List<PoolShape> getPools() {
		return pools;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#addPool(org.fireflow.pdl.fpdl.diagram.Pool)
	 */
	public void addPool(PoolShape pool) {
		pools.add(pool);
		((AbsDiagramElement)pool).setParent(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#gegMessageFlows()
	 */
	public List<MessageFlowShape> getMessageFlows() {
		return messageFlows;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#addMessageFlow(org.fireflow.pdl.fpdl.diagram.MessageFlow)
	 */
	public void addMessageFlow(MessageFlowShape msgFlow) {
		messageFlows.add(msgFlow);
		((AbsDiagramElement)msgFlow).setParent(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#getComments()
	 */
	public List<CommentShape> getComments() {

		return comments;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#addComment(org.fireflow.pdl.fpdl.diagram.Comment)
	 */
	public void addComment(CommentShape cmmt) {
		comments.add(cmmt);
		((AbsDiagramElement)cmmt).setParent(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#getAssociations()
	 */
	public List<AssociationShape> getAssociations() {
		return associations;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#addAssociation(org.fireflow.pdl.fpdl.diagram.Association)
	 */
	public void addAssociation(AssociationShape association) {
		associations.add(association);
		((AbsDiagramElement)association).setParent(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#getGroups()
	 */
	public List<GroupShape> getGroups() {
		return this.groups;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Diagram#addGroup(org.fireflow.pdl.fpdl.diagram.GroupShape)
	 */
	public void addGroup(GroupShape groupShape) {
		this.groups.add(groupShape);
		((AbsDiagramElement)groupShape).setParent(this);
	}

	public Boolean getSnapEnabled() {
		return snapEnabled;
	}

	public void setSnapEnabled(Boolean snapEnabled) {
		this.snapEnabled = snapEnabled;
	}

	public Boolean getGridEnabled() {
		return gridEnabled;
	}

	public void setGridEnabled(Boolean gridEnabled) {
		this.gridEnabled = gridEnabled;
	}

	public Boolean getRulerEnabled() {
		return rulerEnabled;
	}

	public void setRulerEnabled(Boolean rulerEnabled) {
		this.rulerEnabled = rulerEnabled;
	}

}
