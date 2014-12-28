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
public interface Label {
	public static final int DEFAULT_FONT_SIZE = 12;//
	
	public static final String CONTENT_FROM_WORKFLOW_ELEMENT = "#CONTENT_FROM_WF_ELEMENT#";
	public static final String CONTENT_IGNORE = "#CONTENT_IGNORE#";
	
	public static final String FONT_STYLE_BOLD = "BOLD";
	public static final String FONT_STYLE_NORMAL = "NORMAL";
	public static final String FONT_STYLE_ITALIC = "ITALIC";
	public static final String FONT_STYLE_ITALIC_BOLD = "ITALIC_BOLD";
	
	
	public static final String TEXT_HORIZANTAL = "HORIZANTAL";//文字水平
	public static final String TEXT_VERTICAL = "VERTICAL";//文字垂直
	
	/**
	 * 文字放置的方向，可以取值TEXT_HORIZANTAL或者TEXT_VERTICAL
	 * @return
	 */
	public String getTextDirection();
	public void setTextDirection(String d);
	
	public String getText();
	public void setText(String content);
	
	public String getFontName();
	public void setFontName(String fontName);
	
	public int getFontSize();
	public void setFontSize(int font);
	
	public String getFontColor();
	public void setFontColor(String cl);
	
	public String getFontStyle();
	public void setFontStyle(String wt);
	
	public Label copy();
}
