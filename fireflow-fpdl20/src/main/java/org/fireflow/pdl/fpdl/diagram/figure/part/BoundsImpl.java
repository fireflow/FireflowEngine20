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
package org.fireflow.pdl.fpdl.diagram.figure.part;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BoundsImpl implements Bounds {
	protected int x = 0;
	protected int y=0;
	
	protected int height = 10;
	protected int width = 10;

	protected int radius = 0;//缺省为直角
	
	protected int thick = 1;//线条宽度
	
	protected String lineType = Bounds.LINETYPE_SOLID;
	
	protected String color = "#000000";//缺省为黑色
	
	protected String dashArray = null;
	
	public String getDashArray(){
		return dashArray;
	}
	
	public void setDashArray(String dashArr){
		this.dashArray = dashArr;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Bounds#getWidth()
	 */
	public int getWidth() {
		return this.width;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Bounds#setWidth(int)
	 */
	public void setWidth(int w) {
		this.width = w;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Bounds#getHeight()
	 */
	public int getHeight() {
		
		return this.height;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.Bounds#setHeight(int)
	 */
	public void setHeight(int h) {
		this.height = h;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#getX()
	 */
	public int getX() {
		return x;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#setX(int)
	 */
	public void setX(int x) {
		this.x = x;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#getY()
	 */
	public int getY() {
		return y;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#setY()
	 */
	public void setY(int y) {
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#getRadius()
	 */
	public int getCornerRadius() {
		return radius;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#setRadius(int)
	 */
	public void setCornerRadius(int r) {
		radius = r;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#getThick()
	 */
	public int getThick() {
		return thick;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#setThick(int)
	 */
	public void setThick(int thick) {
		this.thick = thick;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#getLineType()
	 */
	public String getLineType() {
		return lineType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#setLineType(java.lang.String)
	 */
	public void setLineType(String type) {
		lineType = type;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#getColor()
	 */
	public String getColor() {
		return color;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.Bounds#setColor(java.lang.String)
	 */
	public void setColor(String color) {
		this.color = color;
	}

	public Bounds copy(){
		Bounds newInst = new BoundsImpl();
		newInst.setX(this.x);
		newInst.setY(this.y);
		newInst.setWidth(this.width);
		newInst.setHeight(this.height);
		newInst.setColor(this.color);
		newInst.setCornerRadius(this.radius);
		newInst.setLineType(this.lineType);
		newInst.setThick(this.thick);
		newInst.setDashArray(this.dashArray);
		return newInst;
	}

}
