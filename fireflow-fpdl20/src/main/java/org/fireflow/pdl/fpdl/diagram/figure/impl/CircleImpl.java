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
package org.fireflow.pdl.fpdl.diagram.figure.impl;

import org.fireflow.pdl.fpdl.diagram.figure.Circle;
import org.fireflow.pdl.fpdl.diagram.figure.part.Bounds;
import org.fireflow.pdl.fpdl.diagram.figure.part.BoundsImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyleImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Label;
import org.fireflow.pdl.fpdl.diagram.figure.part.LabelImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CircleImpl implements Circle {
	private Label titleLabel = null;
	private FulfilStyle fulfilStyle = null;
	private Bounds bounds = new BoundsImpl();

	public CircleImpl(){
		titleLabel = new LabelImpl();
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Circle#getLabel()
	 */
	public Label getTitleLabel() {
		return titleLabel;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.pdl.fpdl.diagram.figure.Circle#setLabel(org.fireflow.pdl.fpdl.diagram.figure.Label)
//	 */
//	public void setTitleLabel(Label lb) {
//		this.titleLabel = lb;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Circle#getFulfilStyle()
	 */
	public FulfilStyle getFulfilStyle() {
		return this.fulfilStyle;
	}

	public void setFulfilStyle(FulfilStyle style){
		this.fulfilStyle = style;
	}
//	/* (non-Javadoc)
//	 * @see org.fireflow.pdl.fpdl.diagram.figure.Circle#setFulfilStyle(org.fireflow.pdl.fpdl.diagram.style.FulfilStyle)
//	 */
//	public void setFulfilStyle(FulfilStyle style) {
//		this.fulfilStyle = style;
//	}
	public String getTitle(){
		if (titleLabel==null) return "";
		return titleLabel.getText();
	}
	public void setTitle(String title){
		if (titleLabel==null){
			titleLabel = new LabelImpl();
		}
		titleLabel.setText(title);
	}
	
	public String getContent(){
		if (titleLabel==null) return "";
		return titleLabel.getText();
	}
	public void setContent(String content){
		if (titleLabel==null){
			titleLabel = new LabelImpl();
		}
		titleLabel.setText(content);
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Circle#getBounds()
	 */
	public Bounds getBounds() {
		return bounds;
	}

}
