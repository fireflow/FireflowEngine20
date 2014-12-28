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
package org.fireflow.webdesigner.transformer;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.model.ModelElement;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.diagram.ActivityShape;
import org.fireflow.pdl.fpdl.diagram.CommentShape;
import org.fireflow.pdl.fpdl.diagram.Diagram;
import org.fireflow.pdl.fpdl.diagram.DiagramElement;
import org.fireflow.pdl.fpdl.diagram.EndNodeShape;
import org.fireflow.pdl.fpdl.diagram.GroupShape;
import org.fireflow.pdl.fpdl.diagram.LaneShape;
import org.fireflow.pdl.fpdl.diagram.NodeShape;
import org.fireflow.pdl.fpdl.diagram.PoolShape;
import org.fireflow.pdl.fpdl.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl.diagram.RouterShape;
import org.fireflow.pdl.fpdl.diagram.StartNodeShape;
import org.fireflow.pdl.fpdl.diagram.figure.Figure;
import org.fireflow.pdl.fpdl.diagram.figure.part.Bounds;
import org.fireflow.pdl.fpdl.diagram.figure.part.BoundsImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Point;
import org.fireflow.pdl.fpdl.io.FPDLNames;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.Synchronizer;
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
import org.w3c.dom.Document;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbstractFpdlDiagramSerializer implements
		FpdlDiagramSerializer {
	private Log log = LogFactory.getLog(AbstractFpdlDiagramSerializer.class);


	protected static DocumentBuilderFactory factory = DocumentBuilderFactory
	.newInstance();
	protected static final int IconTextGap = 5;
	protected static final int IMG_RADIUS = 16;
	protected static final int SVC_LOGO_RADIUS = 8;
	protected static final int DEFAULT_FONT_SIZE = 12;
	protected static final int GROUP_TITLE_HEIGHT = 20;
	protected static final int POOL_LANE_TITLE_HEIGHT = 32;
	protected static final String TYPE = "element_"+FPDLNames.TYPE;
	protected static final String ON_CLICK_HANDLER = "on_element_click";
	
	protected static final String DEFAULT_DOT_DASHARRAY = "1 2";
	protected static final String DEFAULT_DASH_DASHARRAY = "5 4";
	protected static final String DEFAULT_DOTDASH_DASHARRAY = "2 4 5 4";
	
	protected static final String DEFAULT_GROUP_DASHARRAY = "3 2";
	protected static final String DEFAULT_ASSOC_DASHARRAY = DEFAULT_DASH_DASHARRAY;
	protected static final String DEFAULT_MESSAGEFLOW_DASHARRAY = DEFAULT_DASH_DASHARRAY;
	

	protected static final float PT_2_PX_RATE = 1.8f;

	protected static final Map<String,String> SERVICE_LOGO_CACHE = new HashMap<String,String>();

	protected WorkflowProcess workflowProcess = null;
	protected String subProcessName = null;
	
	protected Document document = null;
	protected Diagram diagram = null;
	protected int leftTopX = 0;//左上角x
	protected int leftTopY = 0;//左上角y
	protected int rightDownX = 0;//右下角x
	protected int rightDownY = 0;//右下角y
	
	protected String resourcePathPrefix = null;
	
	
	
	protected void _refreshDiagramSize(Bounds bounds){
		if (bounds.getX()<this.leftTopX){
			this.leftTopX = bounds.getX();
		}
		
		if (bounds.getY()<this.leftTopY){
			this.leftTopY = bounds.getY();
		}
		
		int x = bounds.getX()+bounds.getWidth();
		int y = bounds.getY()+bounds.getHeight();
		
		if (x>this.rightDownX){
			this.rightDownX = x;
		}
		if (y>this.rightDownY){
			this.rightDownY = y;
		}
		
	}
	
	protected void _refreshDiagramSize(List<Point> pointList){
		if (pointList==null || pointList.size()==0){
			for (Point p : pointList){
				if (p.getX()<this.leftTopX){
					this.leftTopX = p.getX();
				}
				
				if (p.getX()>this.rightDownX){
					this.rightDownX = p.getX();
				}
				
				if (p.getY()<this.leftTopY){
					this.leftTopY = p.getY();
				}
				
				if (p.getY()>this.rightDownY){
					this.rightDownY = p.getY();
				}
			}
		}
	}
	
	protected Point _calculateAnchor(NodeShape nodeShape,Bounds nodeBounds,Point refPoint){
		
		Point boundsCenter = new Point();
		boundsCenter.setX(nodeBounds.getX()+nodeBounds.getWidth()/2);
		boundsCenter.setY(nodeBounds.getY()+nodeBounds.getHeight()/2);
		
		if (nodeShape instanceof StartNodeShape || nodeShape instanceof EndNodeShape){
			Point anchor = _calculateStartNodeEndNodeAnchor(nodeBounds,boundsCenter,refPoint);
			return anchor;
		}
		if (nodeShape instanceof ActivityShape
				|| nodeShape instanceof CommentShape
				|| nodeShape instanceof PoolShape
				|| nodeShape instanceof LaneShape){
			Point anchor = _calculateChopboxAnchor(nodeBounds,boundsCenter,refPoint);
			return anchor;
		}
		else if (nodeShape instanceof RouterShape){
			Point anchor = _calculateRouterAnchor(nodeBounds,boundsCenter,refPoint);
			return anchor;
		}
		
		//测试用，直接返回bounds的中心
		return boundsCenter;
	}
	
	protected Point _calculateRouterAnchor(Bounds r,Point center,Point reference){

		float centerX = center.getX();
		float centerY = center.getY();

		if (r.getWidth()<=0 || r.getHeight()<=0 
				|| (reference.getX() == (int) centerX && reference.getY() == (int) centerY))
			return new Point((int) centerX, (int) centerY); // This avoids
															// divide-by-zero
		
		//误差忽略
		Point ref = new Point();
		ref.setX(reference.getX()-center.getX());
		ref.setY(reference.getY()-center.getY());
		if (Math.abs(ref.getX()) <= 1)//允许有1像素误差
			return new Point(reference.getX(), (ref.getY() > 0) ? r.getY()+r.getHeight() : r.getY());
		if (Math.abs(ref.getY()) <= 1)//允许有1像素误差
			return new Point((ref.getX() > 0) ? r.getX()+r.getWidth() : r.getX(), reference.getY());
		
		float dx = reference.getX() - centerX;
		float dy = reference.getY() - centerY;
		
		if (dy==0){
			if (dx>0){//right
				Point result = new Point();
				result.setX(r.getX()+r.getWidth());
				result.setY(r.getY()+r.getHeight()/2);
				return result;
			}
			else{//left
				Point result = new Point();
				result.setX(r.getX());
				result.setY(r.getY()+r.getHeight()/2);				
				return result;
			}
		}
		if (dx==0){
			if (dy>0){//bottom
				Point result = new Point();
				result.setX(r.getX()+r.getWidth()/2);
				result.setY(r.getY()+r.getHeight());	
				return result;
			}else {
				Point result = new Point();
				result.setX(r.getX()+r.getWidth()/2);
				result.setY(r.getY());	
				return result;
			}
		}
		
		float scale = Math.abs(dx/dy);
		float ddy = 0.5f*r.getWidth() / (scale+1);
		float ddx = Math.abs(scale*ddy);
		
		if (dx>=0 && dy>=0){
			Point result = new Point();
			result.setX((int)(centerX+ddx));
			result.setY((int)(centerY+ddy));
			return result;
		}
		else if (dx<=0 && dy<=0){
			Point result = new Point();
			result.setX((int)(centerX-ddx));
			result.setY((int)(centerY-ddy));
			
			return result;
		}
		else if (dx>=0 && dy<=0){
			Point result = new Point();
			result.setX((int)(centerX+ddx));
			result.setY((int)(centerY-ddy));
			return result;
		}
		else {
			Point result = new Point();
			result.setX((int)(centerX-ddx));
			result.setY((int)(centerY+ddy));
			
			return result;
		}
	}
	
	protected Point _getLabelAbsolutePosition(Point fromAnchor,Point toAnchor,List<Point> linePoints,Point relativePos){
		Point result = new Point();
		Point centerP = null;
		if (linePoints!=null && linePoints.size()>0){
			if ((linePoints.size() % 2)==1){
				int index = (linePoints.size()-1)/2;
				centerP = linePoints.get(index);
			}else{
				int index = (linePoints.size())/2;
				Point p1 = linePoints.get(index-1);
				Point p2 = linePoints.get(index);
				centerP = new Point();
				centerP.setX((p1.getX()+p2.getX())/2);
				centerP.setY((p1.getY()+p2.getY())/2);
				
			}
			
		}else{
			centerP = new Point();
			centerP.setX((fromAnchor.getX()+toAnchor.getX())/2);
			centerP.setY((fromAnchor.getY()+toAnchor.getY())/2);
		}
		
		result.setX(centerP.getX()+relativePos.getX());
		result.setY(centerP.getY()+relativePos.getY());
		return result;
	}
	
	protected Point _calculateChopboxAnchor(Bounds r,Point center,Point reference){
		r.setX(r.getX()-1);
		r.setY(r.getY()-1);
		r.setWidth(r.getWidth()+1);
		r.setHeight(r.getHeight()+1);

		float centerX = center.getX();
		float centerY = center.getY();

		if (r.getWidth() <= 0 || r.getHeight() <= 0
				|| (reference.getX() == (int) centerX && reference.getY() == (int) centerY))
			return new Point((int) centerX, (int) centerY); // This avoids
		
		//误差忽略
		Point ref = new Point();
		ref.setX(reference.getX()-center.getX());
		ref.setY(reference.getY()-center.getY());
		if (Math.abs(ref.getX()) <= 1)//允许有1像素误差
			return new Point(reference.getX(), (ref.getY() > 0) ? r.getY()+r.getHeight() : r.getY());
		if (Math.abs(ref.getY()) <= 1)//允许有1像素误差
			return new Point((ref.getX() > 0) ? r.getX()+r.getWidth() : r.getX(), reference.getY());

		
		// divide-by-zero
		float dx = reference.getX() - centerX;
		float dy = reference.getY() - centerY;

		// r.width, r.height, dx, and dy are guaranteed to be non-zero.
		float scale = 0.5f / Math.max(Math.abs(dx) / r.getWidth(), Math.abs(dy)
				/ r.getHeight());

		dx *= scale;
		dy *= scale;
		centerX += dx;
		centerY += dy;

		return new Point(Math.round(centerX), Math.round(centerY));
	}
	
	protected Point _calculateStartNodeEndNodeAnchor(Bounds r,Point center,Point reference){
		Point ref = new Point();
		ref.setX(reference.getX()-center.getX());
		ref.setY(reference.getY()-center.getY());
		
		//Point ref = r.getCenter().negate().translate(reference);
		
		if (Math.abs(ref.getX()) <= 1)//允许有1像素误差
			return new Point(reference.getX(), (ref.getY() > 0) ? r.getY()+r.getHeight() : r.getY());
		if (Math.abs(ref.getY()) <= 1)//允许有1像素误差
			return new Point((ref.getX() > 0) ? r.getX()+r.getWidth() : r.getX(), reference.getY());

		
		float dx = (ref.getX() > 0) ? 0.5f : -0.5f;
		float dy = (ref.getY() > 0) ? 0.5f : -0.5f;

		// ref.x, ref.y, r.width, r.height != 0 => safe to proceed

		float k = (float) (ref.getY() * r.getWidth()) / (ref.getX() * r.getHeight());
		k = k * k;

		int ddx = (int) (r.getWidth() * dx / Math.sqrt(1 + k));
		int ddy = (int) (r.getHeight() * dy / Math.sqrt(1 + 1 / k));
		
		Point result = new Point();
		result.setX(center.getX()+ddx);
		result.setY(center.getY()+ddy);
		return result;
	}
	
	protected Bounds _getAbsoluteBounds(DiagramElement diagramElm){
		//TODO 针对Activity的边事件，需要优化
		//TODO 可能需要特殊处理
		Bounds result = null;
		Bounds original = null;
		if (diagramElm instanceof LaneShape){
			LaneShape argLane = (LaneShape)diagramElm;
			original = new BoundsImpl();
			original.setX(0);
			original.setY(0);
			original.setWidth(0);
			original.setHeight(0);
			
			PoolShape poolShape = (PoolShape)diagramElm.getParent();
			List<DiagramElement> allLanes = poolShape.getChildren();
			
			for (DiagramElement tmpElm : allLanes){
				LaneShape lane = (LaneShape)tmpElm;
				Bounds tmpBounds = lane.getFigure().getBounds();
				if (argLane.equals(lane)){
					original.setWidth(tmpBounds.getWidth()-tmpBounds.getThick()*2);
					original.setHeight(tmpBounds.getHeight()-tmpBounds.getThick()*2);
					break;
				}else{
					if (Diagram.VERTICAL.equals(diagram.getDirection())){
						original.setX(original.getX()+tmpBounds.getWidth());
					}else{
						original.setY(original.getY()+tmpBounds.getHeight());
					}
				}
			}
			result = original;
		}else if (diagramElm instanceof StartNodeShape 
				|| diagramElm instanceof EndNodeShape
				|| diagramElm instanceof RouterShape){
			original = diagramElm.getFigure()==null?null:diagramElm.getFigure().getBounds();
		
			if (original==null){
				log.warn("NodeShape[id="+diagramElm.getId()+"]的bounds信息为空，无法将bounds坐标转换成绝对值。");
				return null;
			}
			result = original.copy();
		}else{
			original = diagramElm.getFigure()==null?null:diagramElm.getFigure().getBounds();
			
			if (original==null){
				log.warn("NodeShape[id="+diagramElm.getId()+"]的bounds信息为空，无法将bounds坐标转换成绝对值。");
				return null;
			}
			result = original.copy();
			
			result.setX(result.getX()+result.getThick());
			result.setY(result.getY()+result.getThick());
			result.setWidth(result.getWidth()-result.getThick()*2);
			result.setHeight(result.getHeight()-result.getThick()*2);
		}

		DiagramElement parent = diagramElm.getParent();
		if (parent==null){
			return result;
		}else{
			
			Bounds parentBounds = _getAbsoluteBounds(parent);
			if (parentBounds!=null){
				
				if (parent instanceof GroupShape){
					result.setX(parentBounds.getX()+result.getX());
					result.setY(parentBounds.getY()+result.getY()+(GROUP_TITLE_HEIGHT-parentBounds.getThick()));
					
				}
				else if ((parent instanceof LaneShape) || (parent instanceof PoolShape)){
					if (Diagram.VERTICAL.equals(diagram.getDirection())){
						result.setX(parentBounds.getX()+result.getX());
						result.setY(parentBounds.getY()+result.getY()+(POOL_LANE_TITLE_HEIGHT-parentBounds.getThick()));
					}else{
						result.setX(parentBounds.getX()+result.getX()+(POOL_LANE_TITLE_HEIGHT-parentBounds.getThick()));
						result.setY(parentBounds.getY()+result.getY());
					}
				}
				else if (parent instanceof ActivityShape){//边事件
					ActivityShape actShape = (ActivityShape)parent;
					
					Bounds actBounds = actShape.getFigure().getBounds();
					result.setX((parentBounds.getX()-parentBounds.getThick())+result.getX());
					
					result.setY(parentBounds.getY()+(actBounds.getHeight()-parentBounds.getThick()-IMG_RADIUS)+result.getY());
				}
				else{
					result.setX(parentBounds.getX()+result.getX());
					result.setY(parentBounds.getY()+result.getY());
				}
			}

		}
		
		return result;
	}
	
	protected String _getSynchronizerNodeImgUri(ProcessNodeShape nodeShape){
		ModelElement modelElm = nodeShape.getWorkflowElementRef();
		Synchronizer node = (Synchronizer)modelElm;
		Feature f = node.getFeature();
		
		String uri = null;
		//图形
		if (node instanceof StartNode){
			if (f==null){
				uri="/org/fireflow/webdesigner/resources/images/obj32/empty_start_event.png";
			}else if (f instanceof TimerStartFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/timer_start_event.png";
			}else if (f instanceof WebserviceStartFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/message_start_event.png";
			}

			else if ((f instanceof CatchFaultFeature) ){
				uri="/org/fireflow/webdesigner/resources/images/obj32/error_start_event.png";
			}else if (f instanceof CatchCompensationFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/compensation_start_event.png";
			}

			else {
				uri="/org/fireflow/webdesigner/resources/images/obj32/empty_start_event.png";
			}
		}else if (node instanceof EndNode){
			if (f==null){
				uri="/org/fireflow/webdesigner/resources/images/obj32/end_none_event.png";
			}
			else if (f instanceof NormalEndFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/end_none_event.png";
			}
			else if (f instanceof ThrowFaultFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/end_error_event.png";
			}
			else if (f instanceof ThrowCompensationFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/end_compensation_event.png";
				
			}
			else if (f instanceof ThrowTerminationFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/end_terminate_event.png";
			}else{
				uri="/org/fireflow/webdesigner/resources/images/obj32/end_none_event.png";
			}
		}else if (node instanceof Router){
			if (f==null){
				uri="/org/fireflow/webdesigner/resources/images/obj32/empty_gateway.png";
				
			}
			else if (f instanceof AndJoinAndSplitRouterFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/parallel_gateway.png";
			}else if (f instanceof OrJoinOrSplitRouterFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/inclusive_gateway.png";
			}else if (f instanceof XOrJoinXOrSplitRouterFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/exclusive_gateway_2.jpg";
			}else if (f instanceof CustomizedRouterFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/complex_gateway.png";
			}else if (f instanceof DefaultRouterFeature){
				uri="/org/fireflow/webdesigner/resources/images/obj32/empty_gateway.png";
			}else{
				uri="/org/fireflow/webdesigner/resources/images/obj32/empty_gateway.png";
			}
			
		}

		return uri;
	}
	
	/**
	 * fontSize的单位是pt
	 * @param text
	 * @param fontSize
	 * @return
	 */
	protected Dimension _calculateFontSize(String text,int fontSize){
		if (text==null || text.equals("")){
			return new Dimension(0,0);
		}else{
			int length = text.length();
			return new Dimension((int)(fontSize*PT_2_PX_RATE*length),(int)(fontSize*PT_2_PX_RATE));
		}
	}
	
	/**
	 * 在待处理的diagram element的外围构造一个<group>节点，当做该图形对象的view port
	 * @param diagramElm
	 * @return
	 */
	protected Bounds _getViewPortBounds(DiagramElement diagramElm){
		Figure figure = diagramElm.getFigure();
		ModelElement wfElm = diagramElm.getWorkflowElementRef();
		int fontSize=DEFAULT_FONT_SIZE;
		if (figure.getTitleLabel()!=null){
			fontSize = figure.getTitleLabel().getFontSize();
		}
		if (wfElm instanceof StartNode ||
				wfElm instanceof EndNode ||
				wfElm instanceof Router){
			
			String displayName = wfElm.getDisplayName();

			
			/*
			 * 对于这些节点，当displayName为空时，不需要取值name，因为displayname为空时，界面上也显示空，而不是显示name
			if(displayName==null || displayName.trim().equals("")){
				displayName = nodeWrapper.getName();
			}
			*/
			 

			// 计算bounds
			Dimension dimension = null;
			dimension = _calculateFontSize(displayName,fontSize);
			dimension.width = dimension.width+4;//加上一个余量，以便字体可以显示完全


			Bounds bounds = figure.getBounds().copy();
			if (dimension.width > 0) {
				//高度
				bounds.setHeight(bounds.getHeight() + IconTextGap+dimension.height);
				
				if (dimension.width > bounds.getWidth()) {
					int oldWidth = bounds.getWidth();
					bounds.setWidth(dimension.width);
					int newX = bounds.getX()-((dimension.width - oldWidth) / 2);
					bounds.setX(newX);
				}
			}
			return bounds;
		}
		else if (wfElm instanceof Activity){
			Activity activity = (Activity)wfElm;

			//计算Activity的bounds，宽度暂时限制在Activity本身的宽度之内
			Bounds bounds = figure.getBounds().copy();
			
			
			bounds.setHeight(bounds.getHeight()+IMG_RADIUS+DEFAULT_FONT_SIZE+IconTextGap);

			return bounds;
		}
		else{
			Bounds bounds = figure.getBounds().copy();
			
			return bounds;
		}
	}


	protected void _init(WorkflowProcess workflowProcess,
			String subProcessName) throws SerializerException {
		this.workflowProcess = workflowProcess;
		this.subProcessName = subProcessName;
		try {
			String subProcessId = this.workflowProcess.getId()+WorkflowProcess.ID_SEPARATOR+this.subProcessName;

			_checkDiagram(this.workflowProcess, this.subProcessName);
			
			diagram = this.workflowProcess.getDiagramBySubProcessId(subProcessId);
			
			
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			document = docBuilder.newDocument();
			

		} catch (ParserConfigurationException e) {
			throw new SerializerException(e);
		}

	}
	
	protected String _makePointsSeq(Point fromAnchor,Point toAnchor,List<Point> pointList){
		//如果linePoints为空，且fromAnchor与toAnchor的坐标仅相差一个像素的情况下进行在矫正
		if (Math.abs(fromAnchor.getX()-toAnchor.getX())<=1){
			fromAnchor.setX(toAnchor.getX());
		}
		if (Math.abs(fromAnchor.getY()-toAnchor.getY())<=1){
			fromAnchor.setY(toAnchor.getY());
		}
		
		StringBuffer pointSeqBuf = new StringBuffer();
		pointSeqBuf.append(fromAnchor.getX()).append(",").append(fromAnchor.getY()).append(" ");
		
		if (pointList!=null && pointList.size()>0){
			for (Point p : pointList){
				pointSeqBuf.append(p.getX()).append(",").append(p.getY()).append(" ");
			}
		}
		pointSeqBuf.append(toAnchor.getX()).append(",").append(toAnchor.getY());
		
		return pointSeqBuf.toString();
	}
	
	/**
	 * 检查流程定义中是否有diagram信息，如果没有则构造一个缺省的diagram
	 * 
	 * @param process
	 * @param subProcessName
	 */
	protected void _checkDiagram(WorkflowProcess process, String subProcessId) {
		// TODO

	}

	protected String _getActivityImgUri(Activity activity){
		ServiceBinding serviceBinding = activity.getServiceBinding();
		
		if (serviceBinding==null)return null;
		
		String serviceId = serviceBinding.getServiceId();
	
		ServiceDef serviceDef = this.workflowProcess.getService(serviceId);

		if (serviceDef==null){
			return null;
		}
		
		String svcClassName = serviceDef.getClass().getName();
		
		
		String pngUri = SERVICE_LOGO_CACHE.get(svcClassName);
		if (pngUri==null){
			pngUri = "/"+svcClassName.replaceAll("\\.", "/")+".png";

			InputStream inStream = this.getClass().getResourceAsStream(pngUri);

			if (inStream!=null){
				SERVICE_LOGO_CACHE.put(svcClassName, pngUri);
				return pngUri;
			}else{
				SERVICE_LOGO_CACHE.put(svcClassName, "_LOGO_NOT_FOUND_");
				return null;
			}
			
		}else if (pngUri.equals("_LOGO_NOT_FOUND_")){
			return null;
		}else{
			return pngUri;
		}
	}
	
	public void setResourcePathPrefix(String s){
		this.resourcePathPrefix = s;
	}
	
	public String serializeDiagramToStr(WorkflowProcess workflowProcess,
			String subProcessName,String encoding,boolean omitXmlDeclaration) {
		try
		{
			Document doc = this.serializeDiagramToDoc(workflowProcess, subProcessName);
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			if (omitXmlDeclaration){
				tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			
			tf.setOutputProperty(OutputKeys.ENCODING, encoding);
			tf.setOutputProperty(OutputKeys.INDENT,"yes");
			tf.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			StreamResult dest = new StreamResult(new StringWriter());
			tf.transform(new DOMSource(doc), dest);

			return dest.getWriter().toString();
		}
		catch (Exception e)
		{
			//TODO 记录日志
			e.printStackTrace();
		}

		return "";
	}
}
