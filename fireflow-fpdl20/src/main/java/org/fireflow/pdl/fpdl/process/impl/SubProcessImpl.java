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
package org.fireflow.pdl.fpdl.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang.StringUtils;
import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.ModelElement;
import org.fireflow.model.data.Property;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.process.lifecycle.InstanceCreatorDef;
import org.fireflow.model.process.lifecycle.InstanceExecutorDef;
import org.fireflow.model.process.lifecycle.InstanceTerminatorDef;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SubProcessImpl extends AbstractModelElement implements SubProcess {
	
	private Duration duration = null;
	/**
	 * 流程数据项，运行时转换为流程变量进行存储。
	 */
	private List<Property> properties = new ArrayList<Property>();

	private Node entry = null;

	/**
	 * 流程环节
	 */
	private List<Activity> activities = SetUniqueList.decorate(new ArrayList<Activity>());

	/**
	 * 转移
	 */
	private List<Transition> transitions = new ArrayList<Transition>();

	/**
	 * 路由器
	 */
	private List<Router> routers = new ArrayList<Router>();

	/**
	 * 开始节点
	 */
	private List<StartNode> startNodes = new ArrayList<StartNode>();

	/**
	 * 结束节点
	 */
	private List<EndNode> endNodes = new ArrayList<EndNode>();
	
	/**
	 * 事件监听器注册表
	 */
	private List<EventListenerDef> eventListenerDefs = new ArrayList<EventListenerDef>();
	
	private InstanceCreatorDef instanceCreatorDef = null;
	private InstanceExecutorDef instanceExecutorDef = null;
	private InstanceTerminatorDef instanceTerminatorDef = null;
	
	/**
	 * 构造函数。当采用该函数时，displayName默认等于name;
	 * @param parentElement
	 * @param name flow的name属性，必须符合java变量的命名规范
	 */
	public SubProcessImpl(ModelElement parentElement, String name) {
		super(parentElement,name);
	}
	
	/**
	 * 构造函数。
	 * @param parentElement
	 * @param name flow的name属性，必须符合java变量的命名规范
	 * @param displayName flow的显示名称，可以是中文
	 */
	public SubProcessImpl(ModelElement parentElement, String name,String displayName) {
		super(parentElement,name,displayName);
	}
	
	/**
	 * 获得该子流程的预计的运行时间
	 * @return
	 */
	public Duration getDuration(){
		return this.duration;
	}
	
	/**
	 * 返回该子流程的预计的运行时间
	 * @param du
	 */
	public void setDuration(Duration du){
		this.duration = du;
	}
	
	/**
	 * 通过Id查找任意元素的序列号，该方法提供给设计器使用，外部系统禁止使用该方法。
	 * 
	 * @param id
	 *            流程元素的id
	 * @return 流程元素的序列号
	 */
//	public String findSnById(String id) {
//		ModelElement elem = this.findWFElementById(id);
//		if (elem != null) {
//			return elem.getSn();
//		}
//		return null;
//	}
	
	public Node getEntry() {
		return this.entry;
	}


	public List<Router> getRouters() {
		return this.routers;
	}

	public void setEntry(Node start) {
		this.entry = start;
	}

	public Activity getActivity(String activityId) {
		if (this.activities != null) {
			for (Activity elm : this.activities) {
				if (elm.getId().equals(activityId)) {
					return elm;
				}
			}
		}
		return null;
	}

	public EndNode getEndNode(String endNodeId) {
		if (this.endNodes != null) {
			for (EndNode elm : this.endNodes) {
				if (elm.getId().equals(endNodeId)) {
					return elm;
				}
			}
		}
		return null;
	}

	public Router getRouter(String routerId) {
		if (this.routers != null) {
			for (Router elm : this.routers) {
				if (elm.getId().equals(routerId)) {
					return elm;
				}
			}
		}
		return null;
	}

	public StartNode getStartNode(String startNodeId) {
		if (this.startNodes != null) {
			for (StartNode startNode : this.startNodes) {
				if (startNode.getId().equals(startNodeId)) {
					return startNode;
				}
			}
		}
		return null;
	}

	public Transition getTransition(String transitionId) {
		if (this.transitions != null) {
			for (Transition transition : this.transitions) {
				if (transition.getId().equals(transitionId)) {
					return transition;
				}
			}
		}
		return null;
	}
	/**
	 * 返回所有的流程数据项
	 * 
	 * @return
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * 返回所有的环节
	 * 
	 * @return
	 */
	public List<Activity> getActivities() {
		return activities;
	}

	/**
	 * 返回所有的转移
	 * 
	 * @return
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * 返回开始节点
	 * 
	 * @return
	 */
	public List<StartNode> getStartNodes() {
		return startNodes;
	}
	

	public List<EventListenerDef> getEventListeners() {
		return eventListenerDefs;
	}

	/**
	 * 返回所有的结束节点
	 * 
	 * @return
	 */
	public List<EndNode> getEndNodes() {
		return endNodes;
	}
	/**
	 * 通过ID查找该流程中的任意元素
	 * 
	 * @param argId
	 *            元素的Id
	 * @return 流程元素，如：Activity,Task,Synchronizer等等
	 */
	public WorkflowElement findWFElementById(String argId) {
		if (this.getId().equals(argId)) {
			return this;
		}
		int i = 0;
		
		
		List<StartNode> startNodes = this.getStartNodes();
		for (StartNode startNode : startNodes){
			if (argId.equals(startNode.getId())){
				return startNode;
			}
		}
		
		List<Activity> activityList = this.getActivities();
		for (i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			if (activity.getId().equals(argId)) {
				return activity;
			}
		}

		List<Router> routers = this.getRouters();
		for (Router router : routers){
			if (argId.equals(router.getId())){
				return router;
			}
		}
		
		List<EndNode> endNodeList = this.getEndNodes();
		for (i = 0; i < endNodeList.size(); i++) {
			EndNode endNode =endNodeList.get(i);
			if (endNode.getId().equals(argId)) {
				return endNode;

			}
		}

		List<Transition> transitionList = this.getTransitions();
		for (i = 0; i < transitionList.size(); i++) {
			Transition transition = transitionList.get(i);
			if (transition.getId().equals(argId)) {
				return transition;
			}
		}
		return null;

	}
	
	/**
	 * 判断两个Activity是否在同一个执行线上
	 * 
	 * @param activityId1
	 * @param activityId2
	 * @return true表示在同一个执行线上，false表示不在同一个执行线上
	 * @deprecated
	 */
	public boolean isInSameLine(String activityId1, String activityId2) {

		return true;
	}

	//在计算可到达节点和可进入节点时，默认节点本身不会形成环路，即节点不能同时是一条连线的输入又是这一条连线的输出
	
	/**
	 * 获取可以到达的节点,相比较于fireflow1.0，2.0中activity和activty之间可以直接有连线，那么应当重写该方法
	 * 
	 * @param nodeId
	 * @return
	 */
	public List<NodeImpl> getReachableNodes(String nodeId) {
		List<NodeImpl> reachableNodes = new ArrayList<NodeImpl>();
		NodeImpl location = (NodeImpl)findWFElementById(nodeId);
		//先把自身添加到可到达节点列表
		reachableNodes.add(location);
		List<Transition> outLines = location.getLeavingTransitions();
		for(Transition outLine:outLines){
			Node nextNode = outLine.getToNode();
			if(!reachableNodes.contains(nextNode)){
				reachableNodes.add((NodeImpl)nextNode);
				for(Transition nextNodeOutLine:nextNode.getLeavingTransitions()){
					//防止连线形成环
					if(!reachableNodes.contains(nextNodeOutLine.getToNode())){
						getReachableNodes(nextNodeOutLine.getToNode().getId());
					}
				}
			}
		}
		return reachableNodes;
		//-----------------------------------------------------------------
		/*
		 * List<NodeImpl> reachableNodesList = new ArrayList<NodeImpl>();
		 * NodeImpl node = (NodeImpl) this.findWFElementById(nodeId); if (node
		 * instanceof ActivityImpl) { ActivityImpl activity = (ActivityImpl)
		 * node; TransitionImpl leavingTransition =
		 * activity.getLeavingTransition(); if (leavingTransition != null) {
		 * NodeImpl toNode = leavingTransition.getToNode(); if (toNode != null)
		 * { reachableNodesList.add(toNode);
		 * reachableNodesList.addAll(getReachableNodes(toNode.getId())); } } }
		 * else if (node instanceof Synchronizer) { Synchronizer synchronizer =
		 * (Synchronizer) node; List<TransitionImpl> leavingTransitions =
		 * synchronizer.getLeavingTransitions(); for (int i = 0;
		 * leavingTransitions != null && i < leavingTransitions.size(); i++) {
		 * TransitionImpl leavingTransition = leavingTransitions.get(i); if
		 * (leavingTransition != null) { NodeImpl toNode = (NodeImpl)
		 * leavingTransition.getToNode(); if (toNode != null) {
		 * reachableNodesList.add(toNode);
		 * reachableNodesList.addAll(getReachableNodes(toNode.getId())); }
		 * 
		 * } } } //剔除重复节点 List<NodeImpl> tmp = new ArrayList<NodeImpl>();
		 * boolean alreadyInTheList = false; for (int i = 0; i <
		 * reachableNodesList.size(); i++) { NodeImpl nodeTmp =
		 * reachableNodesList.get(i); alreadyInTheList = false; for (int j = 0;
		 * j < tmp.size(); j++) { NodeImpl nodeTmp2 = tmp.get(j); if
		 * (nodeTmp2.getId().equals(nodeTmp.getId())) { alreadyInTheList = true;
		 * break; } } if (!alreadyInTheList) { tmp.add(nodeTmp); } }
		 * reachableNodesList = tmp; return reachableNodesList;
		 */
//		return null;
	}

	/**
	 * 获取进入的节点(activity 或者synchronizer)
	 * 
	 * @param nodeId
	 * @return
	 */
	public List<NodeImpl> getEnterableNodes(String nodeId) {
		List<NodeImpl> enterableNodes = new ArrayList<NodeImpl>();
		NodeImpl location = (NodeImpl)findWFElementById(nodeId);
		//先把自身加入到可进入节点列表
		enterableNodes.add(location);
		List<Transition> inLines = location.getEnteringTransitions();
		for(Transition inLine:inLines){
			Node preNode = inLine.getFromNode();
			if(!enterableNodes.contains(preNode)){
				enterableNodes.add((NodeImpl)preNode);
				for(Transition preNodeInLine:preNode.getEnteringTransitions()){
					//防止连线形成环
					if(!enterableNodes.contains(preNodeInLine.getFromNode())){
						getEndNode(preNodeInLine.getFromNode().getId());
					}
				}
			}
		}
		return enterableNodes;
		//-----------------------------------------------------
		/*
		 * List<NodeImpl> enterableNodesList = new ArrayList<NodeImpl>();
		 * NodeImpl node = (NodeImpl) this.findWFElementById(nodeId); if (node
		 * instanceof ActivityImpl) { ActivityImpl activity = (ActivityImpl)
		 * node; TransitionImpl enteringTransition =
		 * activity.getEnteringTransition(); if (enteringTransition != null) {
		 * NodeImpl fromNode = enteringTransition.getFromNode(); if (fromNode !=
		 * null) { enterableNodesList.add(fromNode);
		 * enterableNodesList.addAll(getEnterableNodes(fromNode.getId())); } } }
		 * else if (node instanceof Synchronizer) { Synchronizer synchronizer =
		 * (Synchronizer) node; List<TransitionImpl> enteringTransitions =
		 * synchronizer.getEnteringTransitions(); for (int i = 0;
		 * enteringTransitions != null && i < enteringTransitions.size(); i++) {
		 * TransitionImpl enteringTransition = enteringTransitions.get(i); if
		 * (enteringTransition != null) { NodeImpl fromNode =
		 * enteringTransition.getFromNode(); if (fromNode != null) {
		 * enterableNodesList.add(fromNode);
		 * enterableNodesList.addAll(getEnterableNodes(fromNode.getId())); }
		 * 
		 * } } }
		 * 
		 * //剔除重复节点 //TODO mingjie.mj 20091018 改为使用集合是否更好? List<NodeImpl> tmp =
		 * new ArrayList<NodeImpl>(); boolean alreadyInTheList = false; for (int
		 * i = 0; i < enterableNodesList.size(); i++) { NodeImpl nodeTmp =
		 * enterableNodesList.get(i); alreadyInTheList = false; for (int j = 0;
		 * j < tmp.size(); j++) { NodeImpl nodeTmp2 = tmp.get(j); if
		 * (nodeTmp2.getId().equals(nodeTmp.getId())) { alreadyInTheList = true;
		 * break; } } if (!alreadyInTheList) { tmp.add(nodeTmp); } }
		 * enterableNodesList = tmp; return enterableNodesList;
		 */
//		return null;
	}

	/**
	 * 判断是否可以从from节点到达to节点
	 * 
	 * @param fromNodeId
	 *            from节点的id
	 * @param toNodeId
	 *            to节点的id
	 * @return
	 */
	public boolean isReachable(String fromNodeId, String toNodeId) {
		if (fromNodeId == null || toNodeId == null) {
			return false;
		}
		if (fromNodeId.equals(toNodeId)) {
			return true;
		}
		List<NodeImpl> reachableList = this.getReachableNodes(fromNodeId);

		for (int j = 0; reachableList != null && j < reachableList.size(); j++) {
			NodeImpl node = reachableList.get(j);
			if (node.getId().equals(toNodeId)) {
				return true;
			}
		}

		return false;
	}



	/**
	 * 验证workflow process是否完整正确。
	 * 
	 * @return null表示流程正确；否则表示流程错误，返回值是错误原因
	 */
	public String validate() {

		String errHead = "Workflow process is invalid：";
		/*
		 * if (this.getStartNode() == null) { return errHead +
		 * "must have one start node"; } if
		 * (this.getStartNode().getLeavingTransitions().size() == 0) { return
		 * errHead + "start node must have leaving transitions."; }
		 * 
		 * List<ActivityImpl> activities = this.getActivities(); for (int i = 0;
		 * i < activities.size(); i++) { ActivityImpl activity =
		 * activities.get(i); String theName = (activity.getDisplayName() ==
		 * null || activity .getDisplayName().equals("")) ? activity.getName() :
		 * activity.getDisplayName(); if (activity.getEnteringTransition() ==
		 * null) { return errHead + "activity[" + theName +
		 * "] must have entering transition."; } if
		 * (activity.getLeavingTransition() == null) { return errHead +
		 * "activity[" + theName + "] must have leaving transition."; }
		 * 
		 * // check tasks List<AbstractTask> taskList = activity.getTasks(); for
		 * (int j = 0; j < taskList.size(); j++) { AbstractTask task =
		 * taskList.get(j); if (task.getType() == null) { return errHead +
		 * "task[" + task.getId() + "]'s taskType can Not be null."; } else if
		 * (task.getType().equals(org.fireflow.model.service.impl.FORM)) {
		 * FormTask formTask = (FormTask) task; if (formTask.getPerformer() ==
		 * null) { return errHead + "FORM-task[id=" + task.getId() +
		 * "] must has a performer."; } } else if
		 * (task.getType().equals(org.fireflow.model.service.impl.TOOL)) {
		 * ToolTask toolTask = (ToolTask) task; if (toolTask.getApplication() ==
		 * null) { return errHead + "TOOL-task[id=" + task.getId() +
		 * "] must has a application."; } } else if
		 * (task.getType().equals(org.fireflow.model.service.impl.SUBFLOW)) {
		 * SubflowTask subflowTask = (SubflowTask) task; if
		 * (subflowTask.getSubWorkflowProcess() == null) { return errHead +
		 * "SUBPROCESS-task[id=" + task.getId() + "] must has a subflow."; } } else
		 * { return errHead + " unknown task type of task[" + task.getId() +
		 * "]"; } } }
		 * 
		 * List<Synchronizer> synchronizers = this.getSynchronizers(); for (int
		 * i = 0; i < synchronizers.size(); i++) { Synchronizer synchronizer =
		 * synchronizers.get(i); String theName = (synchronizer.getDisplayName()
		 * == null || synchronizer .getDisplayName().equals("")) ?
		 * synchronizer.getName() : synchronizer.getDisplayName(); if
		 * (synchronizer.getEnteringTransitions().size() == 0) { return errHead
		 * + "synchronizer[" + theName + "] must have entering transition."; }
		 * if (synchronizer.getLeavingTransitions().size() == 0) { return
		 * errHead + "synchronizer[" + theName +
		 * "] must have leaving transition."; } }
		 * 
		 * List<EndNodeImpl> endnodes = this.getEndNodes(); for (int i = 0; i <
		 * endnodes.size(); i++) { EndNodeImpl endnode = endnodes.get(i); String
		 * theName = (endnode.getDisplayName() == null || endnode
		 * .getDisplayName().equals("")) ? endnode.getName() : endnode
		 * .getDisplayName(); if (endnode.getEnteringTransitions().size() == 0)
		 * { return errHead + "end node[" + theName +
		 * "] must have entering transition."; } }
		 * 
		 * List<TransitionImpl> transitions = this.getTransitions(); for (int i
		 * = 0; i < transitions.size(); i++) { TransitionImpl transition =
		 * transitions.get(i); String theName = (transition.getDisplayName() ==
		 * null || transition .getDisplayName().equals("")) ?
		 * transition.getName() : transition.getDisplayName(); if
		 * (transition.getFromNode() == null) { return errHead + "transition[" +
		 * theName + "] must have from node.";
		 * 
		 * } if (transition.getToNode() == null) { return errHead +
		 * "transition[" + theName + "] must have to node."; } }
		 * 
		 * // check datafield List<DataField> dataFieldList =
		 * this.getDataFields(); for (int i = 0; i < dataFieldList.size(); i++)
		 * { DataField df = dataFieldList.get(i); if (df.getDataType() == null)
		 * { return errHead + "unknown data type of datafield[" + df.getId() +
		 * "]"; } }
		 */
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.SubProcess#getProperty(java.lang.String)
	 */
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
