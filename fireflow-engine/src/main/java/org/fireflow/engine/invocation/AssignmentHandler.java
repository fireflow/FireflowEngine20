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
package org.fireflow.engine.invocation;

import java.io.Serializable;
import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;

/**
 * 工作项分配处理器。其职责为：解析User，然后为每个User分配相应的WorkItem；
 * 该接口调用workItemManager完成WorkItem的创建工作。
 * 
 * @author 非也
 * @version 2.0
 */
public interface AssignmentHandler extends Serializable{
	
    public List<WorkItem> assign(WorkflowSession session,ActivityInstance activityInstance,
    		WorkItemManager workItemManager,Object theActivity, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding) throws EngineException;
    
}
