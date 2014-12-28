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
public interface Diagram extends DiagramElement{
	public static final String HORIZONAL = "H";
	public static final String VERTICAL = "V";

	public Boolean getSnapEnabled();//自动对齐
	public void setSnapEnabled(Boolean b);
	
	public Boolean getGridEnabled();
	public void setGridEnabled(Boolean b);
	
	public Boolean getRulerEnabled();
	public void setRulerEnabled(Boolean b);

	public String getDirection();//泳道的方向
	public void setDirection(String d);
	
	
	/**
	 * 所有的流程节点
	 * @return
	 */
	public List<ProcessNodeShape> getProcessNodeShapes();
	
	public void addProcessNodeShape(ProcessNodeShape shape);
	
	/**
	 * 返回所有的Transition
	 * @return
	 */
	public List<TransitionShape> getTransitions();
	
	/**
	 * 
	 * @param transitionShape
	 */
	public void addTransition(TransitionShape transitionShape);	
	
	
	public List<GroupShape> getGroups();
	
	public void addGroup(GroupShape groupShape);
	
	
	/**
	 * 返回所有的Pool
	 * @return
	 */
	public List<PoolShape> getPools();
	
	public void addPool(PoolShape pool);

	/**
	 * 返回所有的消息流
	 * @return
	 */
	public List<MessageFlowShape> getMessageFlows();
	
	/**
	 * 
	 * @param msgFlow
	 */
	public void addMessageFlow(MessageFlowShape msgFlow);
	
	/**
	 * 返回所有的注释
	 * @return
	 */
	public List<CommentShape> getComments();
	
	public void addComment(CommentShape cmmt);
	
	/**
	 * 注释与其他图形实体的关联
	 * @return
	 */
	public List<AssociationShape> getAssociations();
	public void addAssociation(AssociationShape association);
	


}
