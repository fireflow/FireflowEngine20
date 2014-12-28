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
public class FulfilStyleImpl implements FulfilStyle {
	private String color1 = "#FFFFFF";//缺省为白色
	private String color2 = "#FFFFFF";
	private String gradientStyle = FulfilStyle.GRADIENT_STYLE_NONE;

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle#getColor()
	 */
	public String getColor1() {
		return color1;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle#setColor(java.lang.String)
	 */
	public void setColor1(String color) {
		this.color1 = color;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle#getGradientStyle()
	 */
	public String getGradientStyle() {
		return gradientStyle;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle#setGradientStyle(java.lang.String)
	 */
	public void setGradientStyle(String style) {
		gradientStyle = style;
		
	}
	public String getColor2(){
		return color2;
	}
	public void setColor2(String cl){
		this.color2 = cl;
	}

}
