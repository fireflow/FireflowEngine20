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
 * 填充样式
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface FulfilStyle{
	public static final String GRADIENT_STYLE_NONE = "NONE";
	public static final String GRADIENT_STYLE_LEFT2RIGHT = "L2R";
	public static final String GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT = "UL2LR";
	public static final String GRADIENT_STYLE_TOP2DOWN = "T2D";
	public static final String GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT = "UR2LL";
	
	public String getColor1();
	public void setColor1(String color);
	
	public String getColor2();
	public void setColor2(String cl);
	
	public String getGradientStyle();
	public void setGradientStyle(String style);
}
