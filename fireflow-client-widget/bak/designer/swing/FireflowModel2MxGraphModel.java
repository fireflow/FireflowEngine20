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
package org.fireflow.designer.swing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.designer.swing.proxy.Wrapper;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.ModelElement;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.pdl.fpdl.diagram.ActivityShape;
import org.fireflow.pdl.fpdl.diagram.AssociationShape;
import org.fireflow.pdl.fpdl.diagram.CommentShape;
import org.fireflow.pdl.fpdl.diagram.ConnectorShape;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.EndNodeShape;
import org.fireflow.pdl.fpdl.diagram.GroupShape;
import org.fireflow.pdl.fpdl.diagram.LaneShape;
import org.fireflow.pdl.fpdl.diagram.MessageFlowShape;
import org.fireflow.pdl.fpdl.diagram.NodeShape;
import org.fireflow.pdl.fpdl.diagram.PoolShape;
import org.fireflow.pdl.fpdl.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl.diagram.RouterShape;
import org.fireflow.pdl.fpdl.diagram.StartNodeShape;
import org.fireflow.pdl.fpdl.diagram.TransitionShape;
import org.fireflow.pdl.fpdl.diagram.figure.Figure;
import org.fireflow.pdl.fpdl.diagram.figure.Line;
import org.fireflow.pdl.fpdl.diagram.figure.Rectangle;
import org.fireflow.pdl.fpdl.diagram.figure.part.Bounds;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle;
import org.fireflow.pdl.fpdl.diagram.figure.part.Label;
import org.fireflow.pdl.fpdl.diagram.figure.part.Point;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.io.FPDLNames;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.Feature;
import org.fireflow.pdl.fpdl.process.features.endnode.NormalEndFeature;
import org.fireflow.pdl.fpdl.process.features.endnode.ThrowCompensationFeature;
import org.fireflow.pdl.fpdl.process.features.endnode.ThrowFaultFeature;
import org.fireflow.pdl.fpdl.process.features.endnode.ThrowTerminationFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.AndJoinAndSplitRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.CustomizedRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.DefaultRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.OrJoinOrSplitRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.XOrJoinXOrSplitRouterFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchCompensationFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchFaultFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.TimerStartFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.WebserviceStartFeature;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class FireflowModel2MxGraphModel {
	public static final int GROUP_TITLE_HEIGHT = 20;
	public static final int POOL_LANE_TITLE_HEIGHT = 32;
	public static final int START_NODE_HIGHT = 32;
	
	private Map<String,mxCell> allCells = new HashMap<String,mxCell>();
	private SubProcess subProcess = null;
	private Diagram diagram = null;
	
	public mxCell transformToCell(InputStream inStream, String subProcessName){
		try {
			FPDLDeserializer deser = new FPDLDeserializer();
			WorkflowProcess process;
			process = deser.deserialize(inStream);
			return transformToCell(process, subProcessName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public mxCell transformToCell(WorkflowProcess workflowProcess,
			String subProcessName){
		
		
		String subProcessId = workflowProcess.getId()+WorkflowProcess.ID_SEPARATOR+subProcessName;
		checkDiagram(workflowProcess, subProcessName);

		mxCell root = new mxCell();
		root.setId(workflowProcess.getId());

		Wrapper wrapper = this
				.createModelElementWrapper(workflowProcess);
		wrapper.setElementType(FPDLNames.WORKFLOW_PROCESS);
		root.setValue(wrapper);

		mxCell subProcessCell = new mxCell();
		diagram = workflowProcess
				.getDiagramBySubProcessId(subProcessId);
		subProcessCell.setId(diagram.getId());
		root.insert(subProcessCell);
		
		subProcess = workflowProcess
				.getLocalSubProcess(subProcessId);
		wrapper = this.createModelElementWrapper(subProcess);
		wrapper.setElementType(FPDLNames.SUBPROCESS);
		subProcessCell.setValue(wrapper);

		//流程节点
		List<ProcessNodeShape> processNodeShapeList = diagram
				.getProcessNodeShapes();
		if (processNodeShapeList!=null && processNodeShapeList.size()>0){
			for (ProcessNodeShape nodeShape : processNodeShapeList) {
				mxCell cell = this.transformProcessNodeShape(nodeShape);
				if (cell!=null){
					allCells.put(cell.getId(), cell);
					subProcessCell.insert(cell);
				}
			}
		}

		
		//注释
		List<CommentShape> commentShapeList = diagram.getComments();
		if (commentShapeList!=null && commentShapeList.size()>0){
			for (CommentShape commentShape : commentShapeList){
				mxCell cell = transformCommentShape(commentShape);
				if (cell!=null){
					allCells.put(cell.getId(), cell);
					subProcessCell.insert(cell);
				}

			}
		}

		
		
		//Group
		List<GroupShape> groupShapeList = diagram.getGroups();
		if (groupShapeList!=null && groupShapeList.size()>0){
			for (GroupShape groupShape : groupShapeList){
				mxCell cell = transformGroupShape(groupShape);
				if (cell!=null){
					allCells.put(cell.getId(), cell);
					subProcessCell.insert(cell);
				}
			}
		}
		
		//Pool
		List<PoolShape> poolShapeList = diagram.getPools();
		if (poolShapeList!=null && poolShapeList.size()>0){
			for (PoolShape poolShape:poolShapeList){
				mxCell cell = transformPoolShape(poolShape);
				if (cell!=null){
					allCells.put(cell.getId(), cell);
					subProcessCell.insert(cell);
				}
			}
		}
		
		//transition
		List<TransitionShape> transitionShapeList = diagram.getTransitions();
		if (transitionShapeList!=null && transitionShapeList.size()>0){
			for (TransitionShape transitionShape : transitionShapeList){
				mxCell edgeCell = transformTransitionShape(transitionShape);
				if (edgeCell!=null){
					allCells.put(edgeCell.getId(), edgeCell);
					subProcessCell.insert(edgeCell);//TODO 和insertEdge有何区别？
//					subProcessCell.insertEdge(edgeCell, false);
				}
			}
		}
		
		//association
		List<AssociationShape> associationShapeList = diagram.getAssociations();
		if(associationShapeList!=null && associationShapeList.size()>0){
			for (AssociationShape associationShape : associationShapeList){
				mxCell associationCell = transformAssociationShape(associationShape);
				subProcessCell.insert(associationCell);
			}
		}
		
		//messageflow
		List<MessageFlowShape> messageFlowShapeList = diagram.getMessageFlows();
		if (messageFlowShapeList!=null && messageFlowShapeList.size()>0){
			for (MessageFlowShape messageFlowShape : messageFlowShapeList){
				mxCell messageFlowCell = this.transformMessageFlowShape(messageFlowShape);
				subProcessCell.insert(messageFlowCell);
			}
		}

		
		return root;
	}
	public mxGraphModel transformToModel(InputStream inStream, String subProcessName) {

		try {
			FPDLDeserializer deser = new FPDLDeserializer();
			WorkflowProcess process;
			process = deser.deserialize(inStream);
			return transformToModel(process, subProcessName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public mxGraphModel transformToModel(WorkflowProcess workflowProcess,
			String subProcessName) {
		mxCell root = this.transformToCell(workflowProcess, subProcessName);
		
		mxGraphModel graphModel = new mxGraphModel();
		graphModel.setRoot(root);

		return graphModel;
	}
	
	private mxCell transformPoolShape(PoolShape poolShape){
		mxCell cell = new mxCell();
		cell.setId(poolShape.getId());
		cell.setVertex(true);
		
		//1、处理style
		Rectangle figure = (Rectangle)poolShape.getFigure();
		Bounds bounds = figure.getBounds();
		
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-pool");
		String style = this.transformRectangleStyle(figure,2);
		styleBuf.append(style);
		cell.setStyle(styleBuf.toString());
		
		//2、处理value
		Wrapper wrapper = this.createNodeShapeWrapper(poolShape);
		wrapper.setElementType(FPDLNames.POOL);
		cell.setValue(wrapper);

		//3、处理geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());


		cell.setGeometry(geometry);
		
		//Lane
		List<LaneShape> laneShapeList = poolShape.getLanes();
		if (laneShapeList!=null && laneShapeList.size()>0){
			int offsetY = 1;
			for (LaneShape laneShape : laneShapeList){
				mxCell childCell = transformLaneShape(laneShape,offsetY);
				if (childCell!=null){
					allCells.put(childCell.getId(), childCell);
					cell.insert(childCell);
				}
				Figure childFigure = laneShape.getFigure();
				Bounds childBounds = childFigure.getBounds();
				offsetY = offsetY+childBounds.getHeight();
			}
		}

		return cell;
	}
	
	private mxCell transformLaneShape(LaneShape laneShape,int offsetY){
		mxCell cell = new mxCell();
		cell.setId(laneShape.getId());
		cell.setVertex(true);
		
		//1、处理style
		Rectangle figure = (Rectangle)laneShape.getFigure();
		Bounds bounds = figure.getBounds();
		
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-pool");
		String style = this.transformRectangleStyle(figure,2);
		styleBuf.append(style);
		cell.setStyle(styleBuf.toString());
		
		//2、处理value
		Wrapper wrapper = this.createNodeShapeWrapper(laneShape);
		wrapper.setElementType(FPDLNames.LANE);
		cell.setValue(wrapper);

		//3、处理geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setRelative(true);
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());
		
		geometry.setOffset(new mxPoint(POOL_LANE_TITLE_HEIGHT+1,offsetY));//30是Pool的标题区域高度

		cell.setGeometry(geometry);
		
		//处理子节点
		//流程节点
		List<ProcessNodeShape> processNodeShapeList = laneShape
				.getProcessNodeShapes();
		for (ProcessNodeShape nodeShape : processNodeShapeList) {
			mxCell childCell = this.transformProcessNodeShape(nodeShape);
			if (childCell!=null){
				allCells.put(childCell.getId(), childCell);
				mxGeometry childGeometry = childCell.getGeometry();
				childGeometry.setX(childGeometry.getX()+POOL_LANE_TITLE_HEIGHT);
				cell.insert(childCell);
			}
		}
		
		//注释
		List<CommentShape> commentShapeList = laneShape.getComments();
		if (commentShapeList!=null && commentShapeList.size()>0){
			for (CommentShape commentShape : commentShapeList){
				mxCell childCell = transformCommentShape(commentShape);
				if (childCell!=null){
					allCells.put(childCell.getId(), childCell);
					mxGeometry childGeometry = childCell.getGeometry();
					childGeometry.setX(childGeometry.getX()+POOL_LANE_TITLE_HEIGHT);
					cell.insert(childCell);
				}

			}
		}
		
		//group
		List<GroupShape> groupShapeList = laneShape.getGroups();
		if (groupShapeList!=null && groupShapeList.size()>0){
			for (GroupShape groupShape : groupShapeList){
				mxCell childCell = this.transformGroupShape(groupShape);
				allCells.put(childCell.getId(), childCell);
				mxGeometry childGeometry = childCell.getGeometry();
				childGeometry.setX(childGeometry.getX()+POOL_LANE_TITLE_HEIGHT);
				cell.insert(childCell);
			}
		}

		
		return cell;
	}
	
	private mxCell transformGroupShape(GroupShape groupShape){
		mxCell cell = new mxCell();
		cell.setId(groupShape.getId());
		cell.setVertex(true);
		
		//1、处理style
		Rectangle figure = (Rectangle)groupShape.getFigure();
		Bounds bounds = figure.getBounds();
		
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-group");
		String style = this.transformRectangleStyle(figure,2);
		styleBuf.append(style);
		cell.setStyle(styleBuf.toString());
		
		//2、处理value
		Wrapper wrapper = this.createNodeShapeWrapper(groupShape);
		wrapper.setElementType(FPDLNames.GROUP);
		cell.setValue(wrapper);

		//3、处理geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());


		cell.setGeometry(geometry);
		
		//处理子节点
		//流程节点
		List<ProcessNodeShape> processNodeShapeList = groupShape
				.getProcessNodeShapes();
		for (ProcessNodeShape nodeShape : processNodeShapeList) {
			mxCell childCell = this.transformProcessNodeShape(nodeShape);
			if (childCell!=null){
				allCells.put(childCell.getId(), childCell);
				mxGeometry childGeometry = childCell.getGeometry();
				childGeometry.setY(childGeometry.getY()+GROUP_TITLE_HEIGHT);
				cell.insert(childCell);
			}
		}
		
		//注释
		List<CommentShape> commentShapeList = groupShape.getComments();
		if (commentShapeList!=null && commentShapeList.size()>0){
			for (CommentShape commentShape : commentShapeList){
				mxCell childCell = transformCommentShape(commentShape);
				if (childCell!=null){
					allCells.put(childCell.getId(), childCell);
					mxGeometry childGeometry = childCell.getGeometry();
					childGeometry.setY(childGeometry.getY()+GROUP_TITLE_HEIGHT);
					cell.insert(childCell);
				}

			}
		}


		return cell;
	}
	private mxCell transformCommentShape(CommentShape commentShape) {
		mxCell cell = new mxCell();
		cell.setId(commentShape.getId());
		cell.setVertex(true);
		
		//1、处理style
		Rectangle figure = (Rectangle)commentShape.getFigure();
		Bounds bounds = figure.getBounds();
		
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-comment");
		String style = this.transformRectangleStyle(figure,1);
		styleBuf.append(style);
		cell.setStyle(styleBuf.toString());
		
		//2、处理value
		Wrapper wrapper = this.createNodeShapeWrapper(commentShape);
		wrapper.setElementType(FPDLNames.COMMENT);
		cell.setValue(wrapper);

		//3、处理geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());


		cell.setGeometry(geometry);

		return cell;
	}
	
	private mxCell transformMessageFlowShape(MessageFlowShape messageFlowShape){
		mxCell cell = new mxCell();
		cell.setId(messageFlowShape.getId());
		cell.setEdge(true);
		cell.setSource(allCells.get(messageFlowShape.getFromNode().getId()));
		cell.setTarget(allCells.get(messageFlowShape.getToNode().getId()));
		
		
		//外观
		Line line = (Line)messageFlowShape.getFigure();
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-messageflow");
		
		String style = this.transformLineStyle(line);
		styleBuf.append(style);
		
		cell.setStyle(styleBuf.toString());
		
		
		//value
		Wrapper wrapper = this.createConnectorShapeWrapper(messageFlowShape);
		wrapper.setElementType(FPDLNames.ASSOCIATION);
		cell.setValue(wrapper);
		
		//geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setRelative(true);
		//设置折点
		List<Point> pointList = line.getPoints();
		List<mxPoint> mxPointList = new ArrayList<mxPoint>();
		if (pointList!=null && pointList.size()>0){
			for (Point p : pointList){
				mxPoint mxP = new mxPoint();
				mxP.setX(p.getX());
				mxP.setY(p.getY());
				
				mxPointList.add(mxP);
			}
			geometry.setPoints(mxPointList);
		}
		//设置label的位置
		Point p = line.getLabelPosition();
		if (p!=null){
			geometry.setOffset(new mxPoint(p.getX(),p.getY()));
		}
		cell.setGeometry(geometry);
		return cell;
	}
	private mxCell transformAssociationShape(AssociationShape associationShape){
		mxCell cell = new mxCell();
		cell.setId(associationShape.getId());
		cell.setEdge(true);
		cell.setSource(allCells.get(associationShape.getFromNode().getId()));
		cell.setTarget(allCells.get(associationShape.getToNode().getId()));
		
		
		//外观
		Line line = (Line)associationShape.getFigure();
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-association");
		
		String style = this.transformLineStyle(line);
		styleBuf.append(style);
		
		cell.setStyle(styleBuf.toString());
		
		
		//value
		Wrapper wrapper = this.createConnectorShapeWrapper(associationShape);
		wrapper.setElementType(FPDLNames.ASSOCIATION);
		cell.setValue(wrapper);
		
		//geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setRelative(true);
		//设置折点
		List<Point> pointList = line.getPoints();
		List<mxPoint> mxPointList = new ArrayList<mxPoint>();
		if (pointList!=null && pointList.size()>0){
			for (Point p : pointList){
				mxPoint mxP = new mxPoint();
				mxP.setX(p.getX());
				mxP.setY(p.getY());
				
				mxPointList.add(mxP);
			}
			geometry.setPoints(mxPointList);
		}
		//设置label的位置
		Point p = line.getLabelPosition();
		if (p!=null){
			geometry.setOffset(new mxPoint(p.getX(),p.getY()));
		}
		
		cell.setGeometry(geometry);
		return cell;
	}
	
	private mxCell transformTransitionShape(TransitionShape transitionShape){
		mxCell cell = new mxCell();
		cell.setId(transitionShape.getId());
		cell.setEdge(true);
		cell.setSource(allCells.get(transitionShape.getFromNode().getId()));
		cell.setTarget(allCells.get(transitionShape.getToNode().getId()));
		
		
		//外观
		Line line = (Line)transitionShape.getFigure();
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-transition");
		
		String style = this.transformLineStyle(line);
		styleBuf.append(style);
		
		cell.setStyle(styleBuf.toString());
		
		
		//value
		ModelElement modelElm = transitionShape.getWorkflowElementRef();
		Wrapper wrapper = this.createModelElementWrapper(modelElm);
		wrapper.setElementType(FPDLNames.TRANSITION);
		cell.setValue(wrapper);
		
		//geometry
		mxGeometry geometry = new mxGeometry();
		geometry.setRelative(true);
		//设置折点
		List<Point> pointList = line.getPoints();
		List<mxPoint> mxPointList = new ArrayList<mxPoint>();
		if (pointList!=null && pointList.size()>0){
			for (Point p : pointList){
				mxPoint mxP = new mxPoint();
				mxP.setX(p.getX());
				mxP.setY(p.getY());
				
				mxPointList.add(mxP);
			}
			geometry.setPoints(mxPointList);
		}
		//设置label的位置
		Point p = line.getLabelPosition();
		if (p!=null){
			geometry.setOffset(new mxPoint(p.getX(),p.getY()));
		}
		
		
		cell.setGeometry(geometry);
		return cell;
	}

	private mxCell transformProcessNodeShape(ProcessNodeShape nodeShape) {
		mxCell cell = null;
		if (nodeShape instanceof StartNodeShape){
			cell = this.transformStartNodeShape((StartNodeShape)nodeShape);
		}else if (nodeShape instanceof EndNodeShape){
			cell = this.transformEndNodeShape((EndNodeShape)nodeShape);
		}
		else if (nodeShape instanceof RouterShape){
			cell = this.transformRouterShape((RouterShape)nodeShape);
		}
		else if (nodeShape instanceof ActivityShape){
			cell = this.transformActivityShape((ActivityShape)nodeShape);
		}
		return cell;
	}
	
	private mxCell transformRouterShape(RouterShape routerShape){
		mxCell cell = new mxCell();
		cell.setId(routerShape.getId());		
		cell.setVertex(true);
		
		ModelElement modelElm = routerShape.getWorkflowElementRef();
		Router router = (Router)modelElm;
		Feature f = router.getFeature();
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-router;");
		if (f==null){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/empty_gateway.png");
			
		}
		else if (f instanceof AndJoinAndSplitRouterFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/parallel_gateway.png");
		}else if (f instanceof OrJoinOrSplitRouterFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/inclusive_gateway.png");
		}else if (f instanceof XOrJoinXOrSplitRouterFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/exclusive_gateway_2.jpg");
		}else if (f instanceof CustomizedRouterFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/complex_gateway.png");
		}else if (f instanceof DefaultRouterFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/empty_gateway.png");
		}else{
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/empty_gateway.png");
		}
		
		//边框 
		
		
		//字体
		cell.setStyle(styleBuf.toString());
		
		
		Wrapper wrapper = this.createModelElementWrapper(modelElm);
		wrapper.setElementType(FPDLNames.ROUTER);
		cell.setValue(wrapper);

		mxGeometry geometry = new mxGeometry();
		Figure figure = routerShape.getFigure();
		Bounds bounds = figure.getBounds();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());

		cell.setGeometry(geometry);

		return cell;
	}
	
	private mxCell transformEndNodeShape(EndNodeShape endNodeShape){
		mxCell cell = new mxCell();
		cell.setId(endNodeShape.getId());
		
		cell.setVertex(true);
		
		ModelElement modelElm = endNodeShape.getWorkflowElementRef();
		EndNode endNode = (EndNode)modelElm;
		Feature f = endNode.getFeature();
		
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-endnode;");
		
		if (f==null){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/end_none_event.png");
		}
		else if (f instanceof NormalEndFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/end_none_event.png");
		}
		else if (f instanceof ThrowFaultFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/end_error_event.png");
		}
		else if (f instanceof ThrowCompensationFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/end_compensation_event.png");
			
		}
		else if (f instanceof ThrowTerminationFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/end_terminate_event.png");
		}else{
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/end_none_event.png");
		}
		
		cell.setStyle(styleBuf.toString());
		
		Wrapper wrapper = this.createModelElementWrapper(modelElm);
		wrapper.setElementType(FPDLNames.END_NODE);
		cell.setValue(wrapper);

		mxGeometry geometry = new mxGeometry();
		Figure figure = endNodeShape.getFigure();
		Bounds bounds = figure.getBounds();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());

		cell.setGeometry(geometry);

		return cell;
	}
	
	private mxCell transformAttachedStartNodeShape(Bounds activityBounds,StartNodeShape startNodeShape){
		ModelElement modelElm = startNodeShape.getWorkflowElementRef();
		StartNode startNode = (StartNode)modelElm;
		Feature f = startNode.getFeature();
		//由Activity构造其attached event
		if (f==null)return null;
		if (!(f instanceof CatchFaultFeature) && 
				!(f instanceof CatchCompensationFeature)){
			return null;
		}
		
		mxCell cell = new mxCell();
		cell.setId(startNodeShape.getId());
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-startnode;");
		
		//图形
		if ((f instanceof CatchFaultFeature) ){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/error_start_event.png");
		}else if (f instanceof CatchCompensationFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/compensation_start_event.png");
		}
		
		//边框颜色，
		
		//字体		
		cell.setStyle(styleBuf.toString());
		cell.setVertex(true);
		
		
		Wrapper wrapper = this.createModelElementWrapper(modelElm);		
		wrapper.setAttribute(FPDLNames.DISPLAY_NAME,"");//AttacthedStartNode默认不要显示displayName 
		wrapper.setElementType(FPDLNames.START_NODE);
		cell.setValue(wrapper);

		mxGeometry geometry = new mxGeometry();
		geometry.setRelative(true);
		
		Figure figure = startNodeShape.getFigure();
		Bounds bounds = figure.getBounds();
		geometry.setX(0);
		geometry.setY(0);
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());
		
		mxPoint offset = new mxPoint(bounds.getX(),activityBounds.getHeight()-START_NODE_HIGHT/2);
		geometry.setOffset(offset);

		cell.setGeometry(geometry);

		return cell;
	}
	
	private mxCell transformStartNodeShape(StartNodeShape startNodeShape){
		ModelElement modelElm = startNodeShape.getWorkflowElementRef();
		StartNode startNode = (StartNode)modelElm;
		Feature f = startNode.getFeature();
		//由Activity构造其attached event
		if ((f instanceof CatchFaultFeature) || 
				(f instanceof CatchCompensationFeature)){
			return null;
		}
		
		mxCell cell = new mxCell();
		cell.setId(startNodeShape.getId());
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-startnode;");
		
		//图形
		if (f==null){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/empty_start_event.png");
		}else if (f instanceof TimerStartFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/timer_start_event.png");
		}else if (f instanceof WebserviceStartFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/message_start_event.png");
		}
		/*由Activity构造其attached event
		if ((f instanceof CatchFaultFeature) ){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/error_start_event.png");
		}else if (f instanceof CatchCompensationFeature){
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/compensation_start_event.png");
		}
		*/
		else {
			styleBuf.append("image=/org/fireflow/designer/swing/resources/obj32/empty_start_event.png");
		}
		
		//边框颜色，
		
		//字体
		
		cell.setStyle(styleBuf.toString());
		cell.setVertex(true);
		
		
		Wrapper wrapper = this.createModelElementWrapper(modelElm);
		wrapper.setElementType(FPDLNames.START_NODE);
		cell.setValue(wrapper);

		mxGeometry geometry = new mxGeometry();
		Figure figure = startNodeShape.getFigure();
		Bounds bounds = figure.getBounds();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());

		cell.setGeometry(geometry);

		return cell;
	}
	
	private mxCell transformActivityShape(ActivityShape activityShape){
		mxCell cell = new mxCell();
		cell.setId(activityShape.getId());
		cell.setVertex(true);
		
		//1、处理style
		Rectangle figure = (Rectangle)activityShape.getFigure();
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("fireflow-activity");
		String style = this.transformRectangleStyle(figure,1);
		styleBuf.append(style);
		cell.setStyle(styleBuf.toString());
		
		//2、处理value
		ModelElement modelElm = activityShape.getWorkflowElementRef();
		Wrapper wrapper = this.createModelElementWrapper(modelElm);
		wrapper.setElementType(FPDLNames.ACTIVITY);
		cell.setValue(wrapper);

		//3、处理geometry
		Bounds bounds = figure.getBounds();
		mxGeometry geometry = new mxGeometry();
		geometry.setX(bounds.getX());
		geometry.setY(bounds.getY());
		geometry.setWidth(bounds.getWidth());
		geometry.setHeight(bounds.getHeight());


		cell.setGeometry(geometry);
		
		//处理Attached Event
		Activity activity = (Activity)modelElm;
		List<StartNode> attachedStartNodeList = activity.getAttachedStartNodes();
		if (attachedStartNodeList!=null && attachedStartNodeList.size()>0){
			for (StartNode startNode : attachedStartNodeList){
				DiagramElement diatramElement = diagram.findChildByWorkflowElementId(startNode.getId());
				
				mxCell childCell = transformAttachedStartNodeShape(bounds,(StartNodeShape)diatramElement);
				if (childCell!=null){
					allCells.put(childCell.getId(), childCell);

					cell.insert(childCell);
				}
			}
		}

		return cell;
	}

	/**
	 * 检查流程定义中是否有diagram信息，如果没有则构造一个缺省的diagram
	 * 
	 * @param process
	 * @param subProcessName
	 */
	private void checkDiagram(WorkflowProcess process, String subProcessId) {
		// TODO

	}
	
	private Wrapper createConnectorShapeWrapper(ConnectorShape connectorShape){
		Wrapper wrapper = new Wrapper();
		wrapper.setRef(connectorShape.getId());
		wrapper.setAttribute(FPDLNames.NAME, connectorShape.getTitle());
		wrapper.setAttribute(FPDLNames.DISPLAY_NAME,
				connectorShape.getTitle());
		wrapper.setAttribute(FPDLNames.DESCRIPTION,
				connectorShape.getTitle());
		return wrapper;
	}
	
	private Wrapper createNodeShapeWrapper(NodeShape nodeShape){
		Wrapper wrapper = new Wrapper();
		wrapper.setRef("");
		wrapper.setAttribute(FPDLNames.NAME, nodeShape.getTitle());
		wrapper.setAttribute(FPDLNames.DISPLAY_NAME,
				nodeShape.getTitle());
		wrapper.setAttribute(FPDLNames.DESCRIPTION,
				nodeShape.getContent());
		return wrapper;
	}

	private Wrapper createModelElementWrapper(ModelElement modelElement) {
		Wrapper wrapper = new Wrapper();
		wrapper.setRef(modelElement.getId());
		wrapper.setAttribute(FPDLNames.NAME, modelElement.getName());
		wrapper.setAttribute(FPDLNames.DISPLAY_NAME,
				modelElement.getDisplayName());
		wrapper.setAttribute(FPDLNames.DESCRIPTION,
				modelElement.getDescription());
		return wrapper;

	}
	/**
	 * flag==1表示显示content，flag==2表示显示title，flag==3表示content和title都显示(暂时没有这种情况)
	 * @param rect
	 * @param flag
	 * @return
	 */
	private String transformRectangleStyle(Rectangle rect,int flag){
		Bounds bounds = rect.getBounds();
		
		StringBuffer styleBuf = new StringBuffer();

		//1.1线型
		String lineType = bounds.getLineType();
		if (Bounds.LINETYPE_DASHED.equals(lineType)){
			styleBuf.append(";").append(mxConstants.STYLE_DASHED).append("=").append("1");
		}else if (Bounds.LINETYPE_DOTTED.equals(lineType)){
			styleBuf.append(";").append(mxConstants.STYLE_DASHED).append("=").append("1");
			styleBuf.append(";").append(mxConstants.STYLE_DASH_PATTERN).append("=").append("1 1.5");
		}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
			styleBuf.append(";").append(mxConstants.STYLE_DASHED).append("=").append("1");
			styleBuf.append(";").append(mxConstants.STYLE_DASH_PATTERN).append("=").append("4 2 1 2");
		}else{
			//SOLID line是缺省的，不需要任何style
		}
		
		//线条宽度
		int thick = bounds.getThick();
		if (thick!=1){
			styleBuf.append(";").append(mxConstants.STYLE_STROKEWIDTH).append("=").append(thick);
		}
		//线条颜色
		String color = bounds.getColor();
		styleBuf.append(";").append(mxConstants.STYLE_STROKECOLOR).append("=").append(color);

		//填充色
		FulfilStyle fulfilStyle = rect.getFulfilStyle();
		if (fulfilStyle==null){
			styleBuf.append(";").append(mxConstants.STYLE_FILLCOLOR).append("=").append("#FFFFFF");
		}
		else{
			String direction = mxConstants.DIRECTION_EAST;
			String fillColor = fulfilStyle.getColor1();
			String gradientColor = fulfilStyle.getColor2();
			String gradientStyle = fulfilStyle.getGradientStyle();
			if(FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT.equals(gradientStyle)){
				direction = mxConstants.DIRECTION_EAST;
			}
			else if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradientStyle)){
				direction = mxConstants.DIRECTION_SOUTH;
			}
			else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT.equals(gradientStyle)){
				direction = mxConstants.DIRECTION_EAST;//TODO mxGraph暂不支持，故采用变通方案
			}
			else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT.equals(gradientStyle)){
				direction = mxConstants.DIRECTION_SOUTH;//TODO mxGraph暂不支持，故采用变通方案
			}
			
			if (FulfilStyle.GRADIENT_STYLE_NONE.equals(gradientStyle)){
				styleBuf.append(";").append(mxConstants.STYLE_FILLCOLOR).append("=").append(gradientColor);
			}else{
				styleBuf.append(";").append(mxConstants.STYLE_FILLCOLOR).append("=").append(fillColor);
				styleBuf.append(";").append(mxConstants.STYLE_GRADIENTCOLOR).append("=").append(gradientColor);
				styleBuf.append(";").append(mxConstants.STYLE_GRADIENT_DIRECTION).append("=").append(direction);
			}
			
		}

		//字体字号颜色
		Label contentLabel = rect.getContentLabel();
		if (flag==2){
			contentLabel = rect.getTitleLabel();
		}
		styleBuf.append(";").append(mxConstants.STYLE_FONTCOLOR).append("=").append(contentLabel.getFontColor());
		styleBuf.append(";").append(mxConstants.STYLE_FONTSIZE).append("=").append(contentLabel.getFontSize());
		String fontStyle = contentLabel.getFontStyle();
		int fontStyleInt = 0;
		if (Label.FONT_STYLE_BOLD.equals(fontStyle)){
			fontStyleInt = 1;
		}else if (Label.FONT_STYLE_ITALIC.equals(fontStyle)){
			fontStyleInt = 2;
		}
		else if (Label.FONT_STYLE_ITALIC_BOLD.equals(fontStyle)){
			fontStyleInt = 3;
		}
		if (fontStyleInt>0){
			styleBuf.append(";").append(mxConstants.STYLE_FONTSTYLE).append("=").append(fontStyleInt);
		}
		
		return styleBuf.toString();
	}
	
	private String transformLineStyle(Line line){
		StringBuffer styleBuf = new StringBuffer();
		Bounds bounds = line.getBounds();
		

		//1.1线型
		String lineType = bounds.getLineType();
		if (Bounds.LINETYPE_DASHED.equals(lineType)){
			styleBuf.append(";").append(mxConstants.STYLE_DASHED).append("=").append("1");
		}else if (Bounds.LINETYPE_DOTTED.equals(lineType)){
			styleBuf.append(";").append(mxConstants.STYLE_DASHED).append("=").append("1");
			styleBuf.append(";").append(mxConstants.STYLE_DASH_PATTERN).append("=").append("1 1.5");
		}else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)){
			styleBuf.append(";").append(mxConstants.STYLE_DASHED).append("=").append("1");
			styleBuf.append(";").append(mxConstants.STYLE_DASH_PATTERN).append("=").append("4 2 1 2");
		}else{
			//SOLID line是缺省的，不需要任何style
		}
		
		//线条宽度
		int thick = bounds.getThick();
		if (thick!=1){
			styleBuf.append(";").append(mxConstants.STYLE_STROKEWIDTH).append("=").append(thick);
		}
		//线条颜色
		String color = bounds.getColor();
		styleBuf.append(";").append(mxConstants.STYLE_STROKECOLOR).append("=").append(color);

		//文字字体，颜色
		//字体字号颜色
		Label contentLabel = line.getTitleLabel();
		styleBuf.append(";").append(mxConstants.STYLE_FONTCOLOR).append("=").append(contentLabel.getFontColor());
		styleBuf.append(";").append(mxConstants.STYLE_FONTSIZE).append("=").append(contentLabel.getFontSize());
		String fontStyle = contentLabel.getFontStyle();
		int fontStyleInt = 0;
		if (Label.FONT_STYLE_BOLD.equals(fontStyle)){
			fontStyleInt = 1;
		}else if (Label.FONT_STYLE_ITALIC.equals(fontStyle)){
			fontStyleInt = 2;
		}
		else if (Label.FONT_STYLE_ITALIC_BOLD.equals(fontStyle)){
			fontStyleInt = 3;
		}
		if (fontStyleInt>0){
			styleBuf.append(";").append(mxConstants.STYLE_FONTSTYLE).append("=").append(fontStyleInt);
		}

		return styleBuf.toString();
	}
}
