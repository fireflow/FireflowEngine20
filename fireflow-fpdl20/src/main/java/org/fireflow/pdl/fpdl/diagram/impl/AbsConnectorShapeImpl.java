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

import org.fireflow.pdl.fpdl.diagram.ConnectorShape;
import org.fireflow.pdl.fpdl.diagram.NodeShape;
import org.fireflow.pdl.fpdl.diagram.figure.Line;
import org.fireflow.pdl.fpdl.diagram.figure.impl.LineImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbsConnectorShapeImpl extends AbsDiagramElement implements
		ConnectorShape {
	NodeShape fromDiagramElement = null;
	NodeShape toDiagramElement = null;

	public AbsConnectorShapeImpl(String id){
		this.id = id;
		
		this.figure = new LineImpl();
	}
	
	public void setTitle(String lb){
		((Line)this.figure).getTitleLabel().setText(lb);
	}
	public String getTitle(){
		return ((Line)this.figure).getTitleLabel().getText();
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.AssociationShape#getFromDiagramElement()
	 */
	public NodeShape getFromNode() {
		return fromDiagramElement;
	}
	
	public void setFromNode(NodeShape from){
		this.fromDiagramElement = from;
	}
	
	public void setToNode(NodeShape to){
		this.toDiagramElement = to;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.AssociationShape#getToDiagramElement()
	 */
	public NodeShape getToNode() {
		return toDiagramElement;
	}


}
