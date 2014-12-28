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

import java.util.ArrayList;
import java.util.List;

import org.fireflow.pdl.fpdl.diagram.figure.Line;
import org.fireflow.pdl.fpdl.diagram.figure.part.Bounds;
import org.fireflow.pdl.fpdl.diagram.figure.part.BoundsImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Label;
import org.fireflow.pdl.fpdl.diagram.figure.part.LabelImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Point;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class LineImpl implements Line {
	Label titleLabel = new LabelImpl();
	Point labelPosition = new Point();
	List<Point> points = new ArrayList<Point>();
	Bounds bounds = new BoundsImpl();


	public LineImpl(){
		titleLabel = new LabelImpl();
	}
	
	public Label getTitleLabel(){
		return titleLabel;
	}
	
	
//	public void setTitleLabel(Label lb){
//		this.label = lb;
//	}
	
	public Point getLabelPosition(){
		return labelPosition;
	}
//	public void setLabelPosition(Point p){
//		this.labelPosition = p;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Line#getPoints()
	 */
	public List<Point> getPoints() {
		return points;
	}


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
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Line#getBounds()
	 */
	public Bounds getBounds() {
		return bounds;
	}
	
}
