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
package org.fireflow.pdl.fpdl.io;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.model.misc.Duration;
import org.fireflow.pdl.fpdl.diagram.ActivityShape;
import org.fireflow.pdl.fpdl.diagram.EndNodeShape;
import org.fireflow.pdl.fpdl.diagram.LaneShape;
import org.fireflow.pdl.fpdl.diagram.StartNodeShape;
import org.fireflow.pdl.fpdl.diagram.TransitionShape;
import org.fireflow.pdl.fpdl.diagram.impl.ActivityShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.AssociationShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.CommentShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.DiagramImpl;
import org.fireflow.pdl.fpdl.diagram.impl.EndNodeShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.LaneShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.MessageFlowShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.PoolShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.StartNodeShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.TransitionShapeImpl;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.junit.Test;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FPDLSerializerTest  extends FireWorkflowJunitEnviroment{
	protected static final String processName = "TheSimplestSquenceProcess";
	protected static final String processDisplayName = "最简单的测试流程";
	protected static final String description = "一个最简单的顺序流程，没有绑定服务和resource";

	
	@Test
	public void testSerialize(){
		//构建流程定义
		WorkflowProcess process = getWorkflowProcess();
	}
	
	/**
	 * Start-->Activity-->End
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess(){
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		process.setDescription(description);
		
		SubProcess mainflow = process.getMainSubProcess();
		
		mainflow.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(process.getMainSubProcess(),"Start");
		ActivityImpl activity = new ActivityImpl(process.getMainSubProcess(),"Activity1");
		activity.setDuration(new Duration(6,Duration.DAY));
		EndNodeImpl endNode = new EndNodeImpl(process.getMainSubProcess(),"End");
		
		mainflow.setEntry(startNode);
		mainflow.getStartNodes().add(startNode);
		mainflow.getActivities().add(activity);
		mainflow.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process.getMainSubProcess(),"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity);
		startNode.getLeavingTransitions().add(transition1);
		activity.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(process.getMainSubProcess(),"activity2end");
		transition2.setFromNode(activity);
		transition2.setToNode(endNode);
		activity.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		mainflow.getTransitions().add(transition1);
		mainflow.getTransitions().add(transition2);
		
		//构造mainflow的图形信息
		DiagramImpl diagram = new DiagramImpl(mainflow.getId()+"_diagram",mainflow);
		process.addDiagram(diagram);		
		
		PoolShapeImpl pool = new PoolShapeImpl(mainflow.getId()+"_pool");
		diagram.addPool(pool);
		LaneShapeImpl lane1 = new LaneShapeImpl("lane_1");
		pool.addLane(lane1);
		
		CommentShapeImpl commentShape = new CommentShapeImpl("comment_1");
		commentShape.setContent("This is a comment");
		diagram.addComment(commentShape);
		
		AssociationShapeImpl associationShape = new AssociationShapeImpl("association_1");
		associationShape.setFromNode(commentShape);
		associationShape.setToNode(pool);
		associationShape.setTitle("This is a association");
		diagram.addAssociation(associationShape);
		
		PoolShapeImpl pool2 = new PoolShapeImpl("pool_2");
		diagram.addPool(pool2);
		
		MessageFlowShapeImpl messageFlow = new MessageFlowShapeImpl("message_1");
		messageFlow.setFromNode(pool);
		messageFlow.setToNode(pool2);
		messageFlow.setTitle("This is a message Flow");
		diagram.addMessageFlow(messageFlow);
		
		
		StartNodeShape startNodeShape = new StartNodeShapeImpl(startNode.getId()+"_shape");
		startNodeShape.setWorkflowElementRef(startNode);
		diagram.addProcessNodeShape(startNodeShape);
		
		ActivityShape activityShape = new ActivityShapeImpl(activity.getId()+"_shape");
		activityShape.setWorkflowElementRef(activity);
		lane1.addProcessNodeShape(activityShape);
		
		EndNodeShape endNodeShape = new EndNodeShapeImpl(endNode.getId()+"_shape");
		endNodeShape.setWorkflowElementRef(endNode);
		diagram.addProcessNodeShape(endNodeShape);
		
		TransitionShape transitionShape1 = new TransitionShapeImpl(transition1.getId()+"_shape");
		transitionShape1.setWorkflowElementRef(transition1);
		transitionShape1.setFromNode(startNodeShape);
		transitionShape1.setToNode(activityShape);
		diagram.addTransition(transitionShape1);
		
		TransitionShape transitionShape2 = new TransitionShapeImpl(transition2.getId()+"_shape");
		transitionShape2.setWorkflowElementRef(transition2);
		transitionShape2.setFromNode(activityShape);
		transitionShape2.setToNode(endNodeShape);
		diagram.addTransition(transitionShape2);
		
		LaneShape lane = new LaneShapeImpl("lane_3");
		pool2.addLane(lane);
		
		LaneShape lane2 = new LaneShapeImpl("lane_4");
		pool2.addLane(lane2);

		return process;
	}
	
}
