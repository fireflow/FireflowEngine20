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

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface NodeShape extends DiagramElement {
	/**
	 * 所有从本节点输出的关联线
	 * @return
	 */
	public List<AssociationShape> getLeavingAssociationShapes();
	
	/**
	 * 所有输入到本节点的关联线
	 * @return
	 */
	public List<AssociationShape> getEnteringAssociationShapes();	
	
	/**
	 * 所有从本节点输出的消息连线
	 * @return
	 */
	public List<MessageFlowShape> getLeavingMessageFlowShapes();
	
	/**
	 * 所有到达本节点的消息连线
	 * @return
	 */
	public List<MessageFlowShape> getEnteringMessageFlowShapes();
	
	public String getTitle();
	public void setTitle(String title);
	
	public String getContent();
	public void setContent(String content);
}
