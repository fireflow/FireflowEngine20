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

import org.fireflow.pdl.fpdl.diagram.figure.Rectangle;
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
public class RectangleImpl implements Rectangle {
	private Label titleLabel = null;
	private Label contentLabel = null;
	private Bounds bounds = null;
	private FulfilStyle fulfilStyle = null;
	
	public RectangleImpl(){
		titleLabel = new LabelImpl();
		bounds = new BoundsImpl();
	}
	
	public Label getTitleLabel(){
		return titleLabel;
	}
//	public void setTitleLabel(Label lb){
//		this.titleLabel = lb;
//	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Node#getBounds()
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Node#setBounds(org.fireflow.pdl.fpdl.diagram.figure.Bounds)
	 */
//	public void setBounds(Bounds bounds) {
//		this.bounds = bounds;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Node#getFulfillStyle()
	 */
	public FulfilStyle getFulfilStyle() {
		return fulfilStyle;
	}
	
	public void setFulfilStyle(FulfilStyle style){
		this.fulfilStyle = style;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Node#setFulfillStyle(org.fireflow.pdl.fpdl.diagram.style.FulfillStyle)
	 */
//	public void setFulfilStyle(FulfilStyle style) {
//		fulfilStyle = style;
//	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Rectangle#getMinorLabel()
	 */
	public Label getContentLabel() {
		return this.contentLabel;
	}
	
	public void setContentLabel(Label lb){
		this.contentLabel = lb;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Rectangle#setMinorLabel(org.fireflow.pdl.fpdl.diagram.figure.Label)
	 */
//	public void setContentLabel(Label lb) {
//		this.contentLabel = lb;		
//	}

	public String getTitle(){
		if (titleLabel==null)return "";
		return titleLabel.getText();
	}
	public void setTitle(String title){
		if (titleLabel==null){
			titleLabel = new LabelImpl();
		}
		titleLabel.setText(title);
	}
	
	public String getContent(){
		if (contentLabel==null) return "";
		return contentLabel.getText();
	}
	public void setContent(String content){
		if (contentLabel==null){
			contentLabel = new LabelImpl();
		}
		contentLabel.setText(content);
	}
}
