/**
 * Copyright 2004-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
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

import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;



/**
 * 流程转移
 * @author 非也,nychen2000@163.com
 */
@SuppressWarnings("serial")
public class TransitionImpl extends ConnectorImpl implements Transition{
	private boolean isLoop = false;
	private boolean isDefault = false;
	private Expression condition = null;
	
    public TransitionImpl() {
    }

    public TransitionImpl(SubProcess subflow, String name) {
        super(subflow, name);
    }

    public TransitionImpl(SubProcess subflow, String name, Node fromNode, Node toNode) {
        super(subflow, name);
        this.fromNode = fromNode;
        this.toNode = toNode;
    }


//	@Override
//	public void setRuleDef(RuleDef rule) {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public RuleDef getRuleDef() {
//		// TODO Auto-generated method stub
//		return null;
//	}


	public boolean isLoop() {
		return isLoop;
	}
	
	public void setIsLoop(boolean isLoop){
		this.isLoop = isLoop;
	}


	public void setCondition(Expression condition) {
		this.condition = condition;
		
	}

	public Expression getCondition() {
		return this.condition;
	}
	
	public boolean isDefault(){
		return this.isDefault;
	}
	
	public void setDefault(boolean isDefault){
		this.isDefault = isDefault;
	}
	
    public List<Activity> getNextActivities(){
    	List<Activity> result = new ArrayList<Activity>();
    	
    	Node node = this.getToNode();
    	if (node instanceof Activity){
    		result.add((Activity)node);
    		return result;
    	}
    	else {
    		return node.getNextActivities();
    	}
    }
}