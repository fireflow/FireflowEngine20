/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.pdl.fpdl.process.impl;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.process.lifecycle.InstanceCreatorDef;
import org.fireflow.model.process.lifecycle.InstanceExecutorDef;
import org.fireflow.model.process.lifecycle.InstanceTerminatorDef;
import org.fireflow.pdl.fpdl.process.Connector;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.SubProcess;

/**
 * 工作流网的边。
 * @author 非也
 * @version 1.0
 * Created on Mar 18, 2009
 */
@SuppressWarnings("serial")
public class ConnectorImpl extends AbstractModelElement implements Connector{
    /**
     * 转移(或者循环)的源节点。<br>
     * 转移的源节点可以是StartNode、 Activity或者Synchronizer。<br>
     * 循环的源节点必须是Synchronizer或者EndNode，同时循环的目标节点必须是循环源节点的前驱。
     */
    protected Node fromNode = null;

    /**
     * 转移(或者循环)的目标节点。<br>
     * 转移的终止目标可以是EndNode、 Activity或者Synchronizer。<br>
     * 循环的目标节点必须是Synchronizer或者StartNode。
     */
    protected Node toNode = null;


    protected InstanceCreatorDef instanceCreatorDef = null;
    protected InstanceExecutorDef instanceExecutorDef = null;
    protected InstanceTerminatorDef instanceTerminatorDef = null;

    public ConnectorImpl(){
        
    }

    public ConnectorImpl(SubProcess subflow, String name) {
        super(subflow, name);
    }


    /**
     * 返回转移(或者循环)的源节点
     * @return
     */
    public Node getFromNode() {
        return fromNode;
    }

    public void setFromNode(Node fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * 返回转移(或者循环)的目标节点
     * @return
     */
    public Node getToNode() {
        return toNode;
    }

    public void setToNode(Node toNode) {
        this.toNode = toNode;
    }


//	@Override
	public InstanceCreatorDef getInstanceCreatorDef() {		
		return instanceCreatorDef;
	}

//	@Override
	public InstanceExecutorDef getInstanceExecutorDef() {		
		return instanceExecutorDef;
	}

//	@Override
	public InstanceTerminatorDef getInstanceTerminatorDef() {
		return instanceTerminatorDef;
	}

//	@Override
	public void setInstanceCreatorDef(InstanceCreatorDef instanceCreator) {
		this.instanceCreatorDef = instanceCreator;
		
	}

//	@Override
	public void setInstanceExecutorDef(InstanceExecutorDef instanceExecutor) {
		this.instanceExecutorDef = instanceExecutor;		
	}

//	@Override
	public void setInstanceTerminatorDef(
			InstanceTerminatorDef instanceTerminator) {
		this.instanceTerminatorDef = instanceTerminator;		
	}
}
