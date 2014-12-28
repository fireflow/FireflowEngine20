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
package org.fireflow.pdl.fpdl.diagram;

import java.util.List;

import org.fireflow.model.ModelElement;
import org.fireflow.pdl.fpdl.diagram.figure.Figure;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface DiagramElement {
	/**
	 * 具有唯一性，必须自动生成
	 * @return
	 */
	public String getId();
	public void setId(String id);
	
	public ModelElement getWorkflowElementRef();
	public void setWorkflowElementRef(ModelElement wfElm);
	
	public Figure getFigure();
//	public void setFigure(Figure sp);//figure只能在构造函数中初始化，不能外部设置
	
	public DiagramElement findChild(String id);
	
	public DiagramElement findChildByWorkflowElementId(String workflowElementId);
	
	public DiagramElement getParent();//获得父级
	
	public List<DiagramElement> getChildren();//获得所有子节点
}
