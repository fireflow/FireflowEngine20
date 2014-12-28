/**
 * Copyright 2004-2008 非也
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

import org.fireflow.model.AbstractModelElement;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pdl.fpdl.process.features.Feature;

/**
 * 流程图的节点。
 * @author 非也,nychen2000@163.com
 *
 */
@SuppressWarnings("serial")
public abstract class NodeImpl extends AbstractModelElement implements Node{
    /**
     * 输入转移
     */
    protected List<Transition> enteringTransitions = new ArrayList<Transition>();//输入弧
    
    /**
     * 输出转移
     */
    protected List<Transition> leavingTransitions = new ArrayList<Transition>();//输出弧
    
    protected Feature decorator = null;
	
	
    public NodeImpl() {
    }

    public NodeImpl(SubProcess subflow, String name) {
        super(subflow, name);
    }

	public List<Transition> getEnteringTransitions() {
		return enteringTransitions;
	}


	public List<Transition> getLeavingTransitions() {
		return leavingTransitions;
	}

	public Feature getFeature() {
		return decorator;
	}

	public void setFeature(Feature dec) {
		this.decorator = dec;
	}
	
	public List<Node> getNextNodes(){
		List<Transition> leavingTransitions = this.getLeavingTransitions();
		if (leavingTransitions==null || leavingTransitions.size()==0){
			return null;
		}
		List<Node> result = new ArrayList<Node>();
		for (Transition trans : leavingTransitions){
			result.add(trans.getToNode());
		}
		return result;
	}
	
	
	public List<Activity> getNextActivities(){
		List<Node> nextNodes = this.getNextNodes();
		if (nextNodes==null || nextNodes.size()==0)return null;
		List<Activity> result = new ArrayList<Activity>();
		for (Node node : nextNodes){
			if (node instanceof Activity){
				result.add((Activity)node);
			}else {
				List<Activity> temp = node.getNextActivities();
				if (temp!=null){
					result.addAll(temp);
				}
			}
		}
		return result;
	}
}
