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
package org.fireflow.clientwidget.tag;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface ClientWidgetBase {
	public static final String FIREFLOW_RESOURCE_SERVLET = "/FireflowResourceServlet";
	public static final String Fireflow_ClientWidget_Servlet_Path = "/FireflowClientWidgetServlet";
	public static final String HAS_SVG_VML_CONTROL_JS_INCLUDED = "org.fireflow.constants.HAS_SVG_VML_CONTROL_JS_INCLUDED";
	public static final String HAS_JQUERY_JS_INCLUDED = "org.fireflow.constants.HAS_JQUERY_JS_INCLUDED";
	public static final String HAS_JQUERY_CSS_INCLUDED = "org.fireflow.constants.HAS_JQUERY_CSS_INCLUDED";
	public static final String HAS_VML_SETTINGS_INCLUDED = "org.fireflow.constants.HAS_VML_SETTINGS_INCLUDED";

	public void setUseFireflowJQueryUI(boolean b);
	public boolean getUseFireflowJQueryUI();
	
	/**
	 * 是否使用Fireflow widget自带的jquery主题；如果不设置，则使用宿主环境的主题
	 * @param themeName
	 */
	public void setUseFireflowJQueryTheme(String themeName);
	public String getUseFireflowJQueryTheme();
}
