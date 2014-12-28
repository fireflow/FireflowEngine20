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

import java.util.List;

import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.NormalStartFeatureImpl;



/**
 * 开始节点
 * @author 非也,nychen2000@163.com
 */
@SuppressWarnings("serial")
public class StartNodeImpl extends SynchronizerImpl implements StartNode{

    public StartNodeImpl() {
    	this.setFeature(new NormalStartFeatureImpl());
    }

    public StartNodeImpl(SubProcess subflow, String name) {
        super(subflow, name);
    	this.setFeature(new NormalStartFeatureImpl());
    }

    /**
     * 返回null值，表示无输入弧
     */
    @Override
    public List<Transition> getEnteringTransitions() {
        return null;
    }
}
