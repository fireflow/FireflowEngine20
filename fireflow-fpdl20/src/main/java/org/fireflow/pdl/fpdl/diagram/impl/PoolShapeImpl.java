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

import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.LaneShape;
import org.fireflow.pdl.fpdl.diagram.PoolShape;
import org.fireflow.pdl.fpdl.diagram.figure.Rectangle;
import org.fireflow.pdl.fpdl.diagram.figure.impl.RectangleImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class PoolShapeImpl extends AbsNodeShapeImpl implements PoolShape {
//	private boolean isAbstract = true;
	private List<LaneShape> lanes = new ArrayList<LaneShape>();

	public PoolShapeImpl(String id ){
		this.id = id ;
		
		Rectangle plane = new RectangleImpl();
		
		plane.getBounds().setWidth(600);
		plane.getBounds().setHeight(250);
		
		this.figure = plane;
	}
	
	public DiagramElement findChild(String diagramElementId){
		if (diagramElementId.equals(id)){
			return this;
		}
		for (DiagramElement diagramElm : lanes){
			DiagramElement foundElm = diagramElm.findChild(diagramElementId);
			
			if (foundElm!=null){
				return foundElm;
			}
		}
		return null;
	}
	
	public List<DiagramElement> getChildren(){
		List<DiagramElement> children = new ArrayList<DiagramElement>();
		children.addAll(this.lanes);
		return children;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Pool#isAbstract()
	 */
//	public boolean isAbstract() {
//		return isAbstract;
//	}
//	
//	public void setAbstract(boolean b){
//		this.isAbstract = b;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Pool#getLanes()
	 */
	public List<LaneShape> getLanes() {
		return lanes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.Pool#addLane(org.fireflow.pdl.fpdl.diagram.Lane)
	 */
	public void addLane(LaneShape ln) {
		this.lanes .add(ln);
		((AbsDiagramElement)ln).setParent(this);
	}
	
	public void addLane(LaneShape ln,int index){
		if (index<0 || index>this.lanes.size()){ 
			this.lanes .add(ln);
		}
		else{
			this.lanes.add(index, ln);
		}
		((AbsDiagramElement)ln).setParent(this);
	}

	public DiagramElement findChildByWorkflowElementId(String workflowElementId){
	
		for (DiagramElement diagramElm : lanes) {
			DiagramElement tmp = diagramElm.findChildByWorkflowElementId(workflowElementId);
			if (tmp != null) {
				return tmp;
			}
		}
		
		return null;
	}
	

}
