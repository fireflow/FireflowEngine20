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
package org.fireflow.server.support;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.config.FireflowConfigProperty;
import org.fireflow.engine.entity.config.ReassignConfigProperty;
import org.fireflow.engine.entity.repository.ProcessDescriptorProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.ScheduleJobProperty;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.pvm.kernel.TokenProperty;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class EntityPropertyXmlAdapter extends
		XmlAdapter<String, EntityProperty> {

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public EntityProperty unmarshal(String v) throws Exception {
		if (v==null || v.trim().equals(""))return null;
		if (v.startsWith(WorkflowEntity.ENTITY_NAME_ACTIVITY_INSTANCE+".")){
			return ActivityInstanceProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_ACTIVITY_INSTANCE.length()+1));
		}else if (v.startsWith(WorkflowEntity.ENTITY_NAME_FIREFLOW_CONFIG+".")){
			return FireflowConfigProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_FIREFLOW_CONFIG.length()+1));
		}
		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_PROCESS_DESCRIPTOR+".")){
			return ProcessDescriptorProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_PROCESS_DESCRIPTOR.length()+1));
		}
		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_PROCESS_INSTANCE+".")){
			return ProcessInstanceProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_PROCESS_INSTANCE.length()+1));
		}
		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_REASSIGN_CONFIG+".")){
			return ReassignConfigProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_REASSIGN_CONFIG.length()+1));
		}
//		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_RESOUCE_DESCRIPTOR+".")){
//			return ResourceDescriptorProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_RESOUCE_DESCRIPTOR.length()+1));
//		}
		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_SCHEDULE_JOB+".")){
			return ScheduleJobProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_SCHEDULE_JOB.length()+1));
		}
//		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_SERVICE_DESCRIPTOR+".")){
//			return ServiceDescriptorProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_SERVICE_DESCRIPTOR.length()+1));
//		}
		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_TOKEN+".")){
			return TokenProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_TOKEN.length()+1));
		}
		else if (v.startsWith(WorkflowEntity.ENTITY_NAME_VARIABLE+".")){
			return VariableProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_VARIABLE.length()+1));
		}else if (v.startsWith(WorkflowEntity.ENTITY_NAME_WORKITEM+".")){
			return WorkItemProperty.fromValue(v.substring(WorkflowEntity.ENTITY_NAME_WORKITEM.length()+1));
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(EntityProperty v) throws Exception {
		if (v==null)return null;
		return v.getEntityName()+"."+v.getPropertyName();
	}

}
