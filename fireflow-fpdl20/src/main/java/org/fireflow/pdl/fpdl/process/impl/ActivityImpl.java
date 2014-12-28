/**
 * Copyright 2007-2008 非也
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
package org.fireflow.pdl.fpdl.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.misc.Duration;
import org.fireflow.pdl.fpdl.misc.LoopStrategy;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;


@SuppressWarnings("serial")
public class ActivityImpl extends NodeImpl implements Activity{

	private Duration duration = null;
	
	private String priority = null;
	
	private ServiceBinding serviceBinding = null;

	private List<Property> properties = new ArrayList<Property>();

	private ResourceBinding resourceBinding = null;
	
	private List<StartNode> attachedStartNodes = new ArrayList<StartNode>();
	
	/**
	 * 事件监听器注册表
	 */
	private List<EventListenerDef> eventListenerDefs = new ArrayList<EventListenerDef>();
	
	private List<EventListenerDef> workItemEventListenerDefs = new ArrayList<EventListenerDef>();
	
	private LoopStrategy loopStrategy = LoopStrategy.REDO;
	
	public ActivityImpl(){
		super();
	}
	
    public ActivityImpl(SubProcess subflow, String name) {
        super(subflow, name);
    }
	
	public ServiceBinding getServiceBinding() {
		return serviceBinding;
	}

	public void setServiceBinding(ServiceBinding serviceRef) {
		this.serviceBinding = serviceRef;
	}

	public List<EventListenerDef> getEventListeners() {
		return eventListenerDefs;
	}
	public List<EventListenerDef> getWorkItemEventListeners(){
		return this.workItemEventListenerDefs;
	}
	public List<Property> getProperties() {
		return properties;
	}


	public void setResourceBinding(ResourceBinding activityResource) {
		this.resourceBinding = activityResource;		
	}
	

	public ResourceBinding getResourceBinding() {
		return resourceBinding;
	}
	
	public List<StartNode> getAttachedStartNodes(){
		return this.attachedStartNodes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.Activity#getDuration()
	 */
	public Duration getDuration() {
		return this.duration;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.Activity#getPriority()
	 */
	public String getPriority() {
		return this.priority;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.Activity#setDuration(org.fireflow.model.misc.Duration)
	 */
	public void setDuration(Duration du) {
		this.duration = du;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.Activity#setPriority(java.lang.String)
	 */
	public void setPriority(String s) {
		this.priority = s;
		
	}

	/**
	 * @return the loopStrategy
	 */
	public LoopStrategy getLoopStrategy() {
		return loopStrategy;
	}

	/**
	 * @param loopStrategy the loopStrategy to set
	 */
	public void setLoopStrategy(LoopStrategy loopStrategy) {
		this.loopStrategy = loopStrategy;
	}
	
	public Property getProperty(String name) {
		if (StringUtils.isEmpty(name))return null;
		for (Property prop : properties){
			if (name.equals(prop.getName())){
				return prop;
			}
		}
		return null;
	}
}
