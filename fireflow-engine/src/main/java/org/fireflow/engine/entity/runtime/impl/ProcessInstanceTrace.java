/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.fireflow.engine.entity.runtime.impl;

import java.io.Serializable;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Apr 25, 2009
 */
@SuppressWarnings("serial")
public class ProcessInstanceTrace implements Serializable{
    public static final String TRANSITION_TYPE = "Transition";
    public static final String LOOP_TYPE = "Loop";
    public static final String JUMPTO_TYPE = "JumpTo";
    public static final String WITHDRAW_TYPE = "Withdraw";
    public static final String REJECT_TYPE = "Reject";
    String id;
    String processInstanceId ;
    Integer stepNumber;
    Integer minorNumber = 0;
    String type;//Transition, Loop, JumpTo, Withdraw, Reject
    String edgeId ;
    String fromNodeId;
    String toNodeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(String edgeId) {
        this.edgeId = edgeId;
    }

    public String getFromNodeId() {
        return fromNodeId;
    }

    public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getToNodeId() {
        return toNodeId;
    }

    public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMinorNumber() {
        return minorNumber;
    }

    public void setMinorNumber(Integer minorNumber) {
        this.minorNumber = minorNumber;
    }
 
}
