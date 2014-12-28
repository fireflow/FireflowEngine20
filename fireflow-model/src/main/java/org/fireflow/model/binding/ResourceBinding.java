/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */

package org.fireflow.model.binding;

import java.util.List;

import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 资源绑定。
 * 
 * AssignmentHandlerBeanName具有第一优先级。
 * AssignmentHandlerClassName具有第二优先级
 * PotentialOwners等具有第三优先级
 * 
 * @author 非也
 * @version 2.0
 */
public interface ResourceBinding {
	/**
	 * 资源的名称
	 * @return
	 */
	public String getDisplayName();
	
	
	/**
	 * 工作项分配策略
	 * @return
	 */
	public WorkItemAssignmentStrategy getAssignmentStrategy();
	
	public void setAssignmentStrategy(WorkItemAssignmentStrategy strategy);
	
	/**
	 * 业务领导
	 * @return
	 */
	public List<String> getAdministratorRefs();
	
	/**
	 * 增加一个资源引用作为“业务领导”
	 * @param resourceDefId
	 */
	public void addAdministratorRef(String resourceDefId);
	
	/**
	 * 潜在所有者，即参与者
	 * @return
	 */
	public List<String> getPotentialOwnerRefs();
	
	public void addPotentialOwnerRef(String resourceId);
//	public void setPotentialOwners(List<ResourceDef> potentialOwners);
	
	/**
	 * 抄送人
	 * @return
	 */
	public List<String> getReaderRefs();
	
	public void addReaderRef(String resourceId);
	
	/**
	 * 返回自定义AssignmentHandler 的class name 
	 * @return
	 */
	public String getAssignmentHandlerClassName();
	
	/**
	 * 返回自定义的AssignmentHandler 的Bean Name
	 * @return
	 */
	public String getAssignmentHandlerBeanName();
}
