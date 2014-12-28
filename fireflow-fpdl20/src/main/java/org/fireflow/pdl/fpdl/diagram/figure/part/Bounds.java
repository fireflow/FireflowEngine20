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
public interface Bounds {
	public static final String LINETYPE_SOLID = "SOLID";
	public static final String LINETYPE_DASHED = "DASHED";
	public static final String LINETYPE_DASHDOTTED = "DASHDOTTED";
	public static final String LINETYPE_DOTTED = "DOTTED";
	
	
	public int getX();
	public void setX(int x);
	
	public int getY();
	public void setY(int y);
	
	public int getWidth();
	public void setWidth(int w);
	
	public int getHeight();
	public void setHeight(int h);
	
	
	/**
	 * 该值等于表示直角方形，否则是圆角方形，其值是圆角的半径
	 * @return
	 */
	public int getCornerRadius();
	public void setCornerRadius(int r);
	

	
	public int getThick();
	public void setThick(int thick);
	
	/**
	 * 线条类型：实线，虚线，点画线，点线
	 * @return
	 */
	public String getLineType();
	public void setLineType(String type);
	
	/**
	 * 
	 * @return
	 */
	public String getDashArray();
	
	public void setDashArray(String dashArr);
	
	public String getColor();
	public void setColor(String color);
	
	public Bounds copy();
}
