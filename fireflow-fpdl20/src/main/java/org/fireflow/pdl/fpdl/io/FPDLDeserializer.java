/**
 * Copyright 2007-2011 非也
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
package org.fireflow.pdl.fpdl.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.invocation.TimerOperationName;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.ModelElement;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Expression;
import org.fireflow.model.data.Property;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.Util4Deserializer;
import org.fireflow.model.io.resource.ResourceDeserializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.diagram.ActivityShape;
import org.fireflow.pdl.fpdl.diagram.AssociationShape;
import org.fireflow.pdl.fpdl.diagram.CommentShape;
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
import org.fireflow.pdl.fpdl.diagram.figure.Circle;
import org.fireflow.pdl.fpdl.diagram.figure.Line;
import org.fireflow.pdl.fpdl.diagram.figure.Rectangle;
import org.fireflow.pdl.fpdl.diagram.figure.impl.LineImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Bounds;
import org.fireflow.pdl.fpdl.diagram.figure.part.BoundsImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyleImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Label;
import org.fireflow.pdl.fpdl.diagram.figure.part.LabelImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Point;
import org.fireflow.pdl.fpdl.diagram.impl.ActivityShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.AssociationShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.CommentShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.DiagramImpl;
import org.fireflow.pdl.fpdl.diagram.impl.EndNodeShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.GroupShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.LaneShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.MessageFlowShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.PoolShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.RouterShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.StartNodeShapeImpl;
import org.fireflow.pdl.fpdl.diagram.impl.TransitionShapeImpl;
import org.fireflow.pdl.fpdl.misc.LoopStrategy;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Import;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;
import org.fireflow.pdl.fpdl.process.event.impl.EventListenerDefImpl;
import org.fireflow.pdl.fpdl.process.features.Feature;
import org.fireflow.pdl.fpdl.process.features.endnode.impl.ThrowCompensationFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.endnode.impl.ThrowFaultFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.endnode.impl.ThrowTerminationFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.router.impl.AndJoinAndSplitRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.CustomizedRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.DefaultRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.OrJoinOrSplitRouterFeature;
import org.fireflow.pdl.fpdl.process.features.router.impl.XOrJoinXOrSplitRouterFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchCompensationFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchFaultFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.TimerStartFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.CatchCompensationFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.CatchFaultFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.TimerStartFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.WebserviceStartFeatureImpl;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.ImportImpl;
import org.fireflow.pdl.fpdl.process.impl.RouterImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.SubProcessImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.ScriptLanguages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;



/**
 * @author 非也 nychen2000@163.com
 */
public class FPDLDeserializer implements FPDLNames{
	private static final Log log = LogFactory.getLog(FPDLDeserializer.class);
	private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    public static final String JDK_TRANSFORMER_CLASS = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
    
    private boolean useJDKTransformerFactory = false;//需要规避bug

    
	static {
		docBuilderFactory.setNamespaceAware(true);
    	docBuilderFactory.setCoalescing(false);
	}
    private static ImportLoader defaultImportLoader = new ImportLoaderClasspathImpl();
    
    protected ImportLoader importLoader = defaultImportLoader;
    
	/**
	 * 
	 */
	public FPDLDeserializer() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		if (JDK_TRANSFORMER_CLASS.equals(transformerFactory.getClass().getName())){
			useJDKTransformerFactory = true;
		}
	}

	/**
	 * @return the importLoader
	 */
	public ImportLoader getImportLoader() {
		return importLoader;
	}

	/**
	 * @param importLoader the importLoader to set
	 */
	public void setImportLoader(ImportLoader importLoader) {
		this.importLoader = importLoader;
	}

	public WorkflowProcess deserialize(InputStream in) throws IOException,
			DeserializerException ,InvalidModelException{
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			Document document = docBuilder.parse(in);
			
			String encoding = document.getXmlEncoding();
			log.info("Xml文件的字符集是:"+encoding);
			
			final org.w3c.dom.Node pi = 
				document.createProcessingInstruction(StreamResult.PI_DISABLE_OUTPUT_ESCAPING,"");
			document.appendChild(pi);

			WorkflowProcess wp = deserialize(document);// 解析
			return wp;
		} catch (ParserConfigurationException e) {
			throw new DeserializerException("Error parsing document.", e);
		} catch (SAXException e) {
			throw new DeserializerException("Error parsing document.", e);
		} finally {
		}
		// return parse(new InputStreamReader(in));
	}
	
	public WorkflowProcess parse(InputStream in,RuntimeContext ctx){
		return null;
	}

	@SuppressWarnings("static-access")
	public WorkflowProcess deserialize(Document document)
			throws DeserializerException,InvalidModelException,IOException {
		Element workflowProcessElement = document.getDocumentElement();
		String processName = workflowProcessElement.getAttribute(NAME);
		String processDisplayName = workflowProcessElement.getAttribute(DISPLAY_NAME);
		
		// 流程ID
		WorkflowProcessImpl wp = new WorkflowProcessImpl(processName,processDisplayName);
		
		// 流程整体描述
		wp.setDescription(this.loadCDATA(Util4Deserializer.child(workflowProcessElement,DESCRIPTION)));

		// 流程业务类别
		wp.setPackageId(workflowProcessElement.getAttribute(PACKAGE_ID));

		//解析Import
		this.loadImports(wp,Util4Deserializer.children(workflowProcessElement, IMPORT));
		
		
		//解析Service
        QName qname = new QName(SERVICE_NS_URI,	SERVICES, SERVICE_NS_PREFIX);
        List<ServiceDef> serviceList = new ArrayList<ServiceDef>();
        ServiceParser.loadServices(serviceList,Util4Deserializer.child(workflowProcessElement, qname));
		if (serviceList!=null){
			for (ServiceDef serviceDef : serviceList){
				wp.addService(serviceDef);
			}
		}
        
		//解析Resource
        List<ResourceDef> resourcesList = new ArrayList<ResourceDef>();
        qname = new QName(RESOURCE_NS_URI,	RESOURCES, RESOURCE_NS_PREFIX);
        ResourceDeserializer.loadResources(resourcesList,Util4Deserializer.child(workflowProcessElement, qname));
        if (resourcesList!=null){
        	for (ResourceDef resourceDef : resourcesList){
        		wp.addResource(resourceDef);
        	}
        }
        
        //解析subflow
        Element subflowsElem = Util4Deserializer.child(workflowProcessElement, SUBPROCESSES);
        List<Element> subflowElementList = Util4Deserializer.children(subflowsElem, SUBPROCESS);

        if (subflowElementList!=null){
        	for (Element subflowElm : subflowElementList){
        		this.loadSubflow(wp, subflowElm);
        	}
        }
        
        //解析diagrams
        Element diagramsElm = Util4Deserializer.child(workflowProcessElement, DIAGRAMS);
        List<Element> diagramsElementList = Util4Deserializer.children(diagramsElm, DIAGRAM);
        
        if (diagramsElementList!=null){
        	for (Element diagramElm : diagramsElementList){
        		this.loadDiagram(wp,diagramElm);
        	}
        }
        
		return wp;

	}
	
	protected void loadDiagram(WorkflowProcess wp,Element diagramElm)throws DeserializerException{
		if (diagramElm==null) return;
		
		String id = diagramElm.getAttribute(ID);
		String subflowId = diagramElm.getAttribute(REF);
		String direction = diagramElm.getAttribute(DIRECTION);
		String snapEnabled = diagramElm.getAttribute(SNAP_ENABLED);
		String gridEnabled = diagramElm.getAttribute(GRID_ENABLED);
		String rulerEnabled = diagramElm.getAttribute(RULER_ENABLED);
		
		SubProcess subProcess = wp.getLocalSubProcess(subflowId);
		
		Diagram diagram = new DiagramImpl(id,subProcess);
		if (!StringUtils.isEmpty(direction)){
			diagram.setDirection(direction);
		}
		
		try{
			diagram.setGridEnabled(Boolean.parseBoolean(gridEnabled));
		}catch(Exception e){
			log.error(e.getMessage());
		}
		
		try{
			diagram.setRulerEnabled(Boolean.parseBoolean(rulerEnabled));
		}catch(Exception e){
			log.error(e.getMessage());
		}
		
		try{
			diagram.setSnapEnabled(Boolean.parseBoolean(snapEnabled));
		}catch(Exception e){
			log.error(e.getMessage());
		}
		
		wp.addDiagram(diagram);
		
		
		//1、首先load所有的节点
		List<Element> childElmList = Util4Deserializer.children(diagramElm,NODE);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";

				if (ACTIVITY.equals(type)){
					ActivityShape activityShape =loadActivityShape(subProcess,childElm);
					diagram.addProcessNodeShape(activityShape);
				}else if (START_NODE.equals(type)){
					StartNodeShape startNodeShape = loadStartNodeShape(subProcess,childElm);
					diagram.addProcessNodeShape(startNodeShape);
				}else if (END_NODE.equals(type)){
					EndNodeShape endNodeShape = loadEndNodeShape(subProcess,childElm);
					diagram.addProcessNodeShape(endNodeShape);
				}else if (ROUTER.equals(type)){
					RouterShape routerShape = loadRouterShape(subProcess,childElm);
					diagram.addProcessNodeShape(routerShape);
				}
				else if (GROUP.equals(type)){
					GroupShape groupShape = loadGroupShapeWithoutConnector(subProcess,childElm);
					diagram.addGroup(groupShape);
				}
				else if (type.equals(POOL)){
					PoolShape poolShape = loadPoolShapeWithoutConnector(subProcess,diagram,childElm);
					diagram.addPool(poolShape);
				}
				else if (type.equals(COMMENT)){
					CommentShape commentShape = loadCommentShape(childElm);
					diagram.addComment(commentShape);
				}
			}
		}
		
		//2、然后load所有的线条
		/*
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				String elmId = childElm.getAttribute(ID);
				if (GROUP.equals(type)){
					GroupShape groupShape = (GroupShape)diagram.findChild(elmId);
					this.loadConnectorsInGroupShape(diagram, childElm, groupShape);
				}
				else if (type.equals(POOL)){
					PoolShape poolShape = (PoolShape)diagram.findChild(elmId);
					loadConnectorsInPoolShape(diagram,childElm,poolShape);
				}
			}
		}
		*/
		
		childElmList = Util4Deserializer.children(diagramElm, CONNECTOR);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (TRANSITION.equals(type)){
					TransitionShape transitionShape = loadTransitionShape(subProcess,diagram,childElm);
					ProcessNodeShape from = (ProcessNodeShape)transitionShape.getFromNode();
					if (from!=null){
						from.getLeavingTransitionShapes().add(transitionShape);
					}
					ProcessNodeShape to = (ProcessNodeShape)transitionShape.getToNode();
					if (to!=null){
						to.getEnteringTransitionShapes().add(transitionShape);
					}
					diagram.addTransition(transitionShape);
				}
				else if (type.equals(MESSAGEFLOW)){
					MessageFlowShape messageFlow = this.loadMessageFlowShape(diagram,childElm);
					NodeShape from = messageFlow.getFromNode();
					if (from!=null){
						from.getLeavingMessageFlowShapes().add(messageFlow);
					}
					NodeShape to = messageFlow.getToNode();
					if (to!=null){
						to.getEnteringMessageFlowShapes().add(messageFlow);
					}
					diagram.addMessageFlow(messageFlow);
				}
				else if (type.equals(ASSOCIATION)){
					AssociationShape association = loadAssociationShape(diagram,childElm);
					NodeShape from =  association.getFromNode();
					if (from!=null){
						from.getLeavingAssociationShapes().add(association);
					}
					NodeShape to = association.getToNode();
					if (to!=null){
						to.getEnteringAssociationShapes().add(association);
					}
					
					diagram.addAssociation(association);
				}
			}
		}
	}
	
	protected GroupShape loadGroupShapeWithoutConnector(SubProcess subProcess,Element groupElement){
		String id = groupElement.getAttribute(ID);
		GroupShape groupShape = new GroupShapeImpl(id);
		
		Element figureElement = Util4Deserializer.child(groupElement, FIGURE);
		Element rectElm = Util4Deserializer.child(figureElement, RECTANGLE);
		this.loadRectangle(groupShape, rectElm);
		
		//修正代码中的错误，例如，如果fulfilstyle==null，则给一个缺省值；如果bound为0
		Rectangle rect = (Rectangle)groupShape.getFigure();
		if (rect.getFulfilStyle()==null){
			FulfilStyle style = new FulfilStyleImpl();
			rect.setFulfilStyle(style);
		}
		
		List<Element> childElmList = Util4Deserializer.children(groupElement,NODE);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (ACTIVITY.equals(type)){
					ActivityShape activityShape =loadActivityShape(subProcess,childElm);
					groupShape.addProcessNodeShape(activityShape);
				}else if (START_NODE.equals(type)){
					StartNodeShape startNodeShape = loadStartNodeShape(subProcess,childElm);
					groupShape.addProcessNodeShape(startNodeShape);
				}else if (END_NODE.equals(type)){
					EndNodeShape endNodeShape = loadEndNodeShape(subProcess,childElm);
					groupShape.addProcessNodeShape(endNodeShape);
				}else if (ROUTER.equals(type)){
					RouterShape routerShape = loadRouterShape(subProcess,childElm);
					groupShape.addProcessNodeShape(routerShape);
				}
				else if (COMMENT.equals(type)){
					CommentShape commentShape = this.loadCommentShape( childElm);
					groupShape.addComment(commentShape);
				}

			}
		}
		

		return groupShape;
	}
	
	/* 所有的链接放在diagram中
	protected void loadConnectorsInGroupShape(Diagram diagram ,Element groupElement,GroupShape groupShape){
		List<Element> childElmList = Util4Deserializer.children(groupElement, CONNECTOR);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (TRANSITION.equals(type)){
					TransitionShape transitionShape = loadTransitionShape(diagram,childElm);
					groupShape.addTransition(transitionShape);	
				}
				else if (ASSOCIATION.equals(type)){
					AssociationShape associationShape = this.loadAssociationShape(diagram, childElm);
					groupShape.addAssociation(associationShape);
				}
			}
		}
	}
	*/
	protected PoolShape loadPoolShapeWithoutConnector(SubProcess subProcess,Diagram diagram,Element poolElm){
		if (poolElm==null){
			return null;
		}
		
		String id = poolElm.getAttribute(ID);		
		String wfElementRef = poolElm.getAttribute(REF);
		
		PoolShape pool = new PoolShapeImpl(id);

		if (!StringUtils.isEmpty(wfElementRef)){
			ModelElement modelElm = subProcess==null?null:subProcess.findWFElementById(wfElementRef);
			pool.setWorkflowElementRef(modelElm);
		}

		Element figureElement = Util4Deserializer.child(poolElm, FIGURE);
		Element planeElm = Util4Deserializer.child(figureElement, RECTANGLE);
		this.loadRectangle(pool, planeElm);
		
		//修正代码中的错误，例如，如果fulfilstyle==null，则给一个缺省值；如果bound为0
		Rectangle rect = (Rectangle)pool.getFigure();
		if (rect.getFulfilStyle()==null){
			FulfilStyle style = new FulfilStyleImpl();
			rect.setFulfilStyle(style);
		}
		
		//下级节点
		List<Element> children = Util4Deserializer.children(poolElm, NODE);
		if (children!=null){
			for (Element child : children){
				String type = child.getAttribute(TYPE);
				if (type==null) type="";
				
				if (LANE.equals(type)){
					LaneShape lane = loadLaneShape(subProcess,child);
					pool.addLane(lane);
				}
			}
		}

		return pool;
	}
	
	/* 所有的链接放在diagram层里面
	protected void loadConnectorsInPoolShape(Diagram diagram ,Element poolElement,PoolShape poolShape){
		List<Element> childElmList = Util4Deserializer.children(poolElement, CONNECTOR);
		if (childElmList!=null){
			for (Element childElm : childElmList){
				String type = childElm.getAttribute(TYPE);
				if (type==null) type="";
				if (TRANSITION.equals(type)){
					TransitionShape transitionShape = loadTransitionShape(diagram,childElm);
					poolShape.addTransition(transitionShape);	
				}
				else if (ASSOCIATION.equals(type)){
					AssociationShape associationShape = this.loadAssociationShape(diagram, childElm);
					poolShape.addAssociation(associationShape);
				}
			}
		}
		List<Element> children = Util4Deserializer.children(poolElement, NODE);
		if (children!=null){
			for (Element child : children){
				String type = child.getAttribute(TYPE);
				String elmId = child.getAttribute(ID);
				if (GROUP.equals(type)){
					GroupShape groupShape = (GroupShape)diagram.findChild(elmId);
					this.loadConnectorsInGroupShape(diagram, child, groupShape);
				}
			}
		}
	}
	*/
	
	protected TransitionShape loadTransitionShape(SubProcess subProcess,Diagram diagram,Element transitionElm){
		if (transitionElm==null)return null;
		
		String id = transitionElm.getAttribute(ID);
		String ref = transitionElm.getAttribute(REF);
		String from = transitionElm.getAttribute(FROM);
		String to = transitionElm.getAttribute(TO);
		
		ModelElement modelElm = subProcess==null?null:subProcess.findWFElementById(ref);
		
		DiagramElement fromElm = diagram.findChild(from);
		DiagramElement toElm = diagram.findChild(to);
		
		
		TransitionShape transitionShape = new TransitionShapeImpl(id);
		transitionShape.setWorkflowElementRef(modelElm);
		transitionShape.setFromNode((ProcessNodeShape)fromElm);
		transitionShape.setToNode((ProcessNodeShape)toElm);
		
		Element figureElement = Util4Deserializer.child(transitionElm, FIGURE);
		Element lineElm = Util4Deserializer.child(figureElement, LINE);
		this.loadLine(transitionShape, lineElm);
		
		return transitionShape;
	}
	
	protected RouterShape loadRouterShape(SubProcess subProcess,Element routerShapeElm){
		if (routerShapeElm==null) return null;
		
		String id = routerShapeElm.getAttribute(ID);
		String ref = routerShapeElm.getAttribute(REF);
		ModelElement modelElm = subProcess==null?null:subProcess.findWFElementById(ref);
		
		RouterShape routerShape = new RouterShapeImpl(id);
		routerShape.setWorkflowElementRef(modelElm);
		
		Element figureElement = Util4Deserializer.child(routerShapeElm, FIGURE);
		Element rectElm = Util4Deserializer.child(figureElement, RECTANGLE);
		this.loadRectangle(routerShape, rectElm);
		
		return routerShape;
	}
	
	protected EndNodeShape loadEndNodeShape(SubProcess subProcess,Element endNodeShapeElm){
		if (endNodeShapeElm==null) return null;
		String id = endNodeShapeElm.getAttribute(ID);
		String ref = endNodeShapeElm.getAttribute(REF);
		ModelElement modelElm = subProcess==null?null:subProcess.findWFElementById(ref);
		
		EndNodeShape endNodeShape = new EndNodeShapeImpl(id);
		endNodeShape.setWorkflowElementRef(modelElm);
		
		Element figureElement = Util4Deserializer.child(endNodeShapeElm, FIGURE);
		Element circleElm = Util4Deserializer.child(figureElement,CIRCLE);
		loadCircle(endNodeShape,circleElm);
		
		return endNodeShape;
	}
	
	protected StartNodeShape loadStartNodeShape(SubProcess subProcess,Element startNodeShapeElm){
		if (startNodeShapeElm==null) return null;
		
		String id = startNodeShapeElm.getAttribute(ID);
		String ref = startNodeShapeElm.getAttribute(REF);
		ModelElement modelElm = subProcess==null?null:subProcess.findWFElementById(ref);
		
		StartNodeShape startNodeShape = new StartNodeShapeImpl(id);
		startNodeShape.setWorkflowElementRef(modelElm);
		
		Element figureElement = Util4Deserializer.child(startNodeShapeElm, FIGURE);
		Element circleElm = Util4Deserializer.child(figureElement,CIRCLE);
		loadCircle(startNodeShape,circleElm);
		
		return startNodeShape;
	}
	
	protected void loadCircle(DiagramElement diagramElm,Element circleElm){
		if (circleElm==null) return;
		Circle circle = (Circle)diagramElm.getFigure();
		
		Element titleElm = Util4Deserializer.child(circleElm, TITLE);		
		if (titleElm!=null){
			Element labelElm = Util4Deserializer.child(titleElm, LABEL);
			Label lb = this.loadLabel(labelElm);
			if (lb!=null){
				circle.getTitleLabel().setFontColor(lb.getFontColor());
				circle.getTitleLabel().setFontName(lb.getFontName());
				circle.getTitleLabel().setFontSize(lb.getFontSize());
				circle.getTitleLabel().setText(lb.getText());
				circle.getTitleLabel().setTextDirection(lb.getTextDirection());
				circle.getTitleLabel().setFontStyle(lb.getFontStyle());
			}
		}

		Element boundsElm = Util4Deserializer.child(circleElm, BOUNDS);
		if (boundsElm!=null){
			Bounds bounds = loadBounds(boundsElm);
			if (bounds!=null){
				circle.getBounds().setX(bounds.getX());
				circle.getBounds().setY(bounds.getY());
				circle.getBounds().setWidth(bounds.getWidth());
				circle.getBounds().setHeight(bounds.getHeight());
				circle.getBounds().setLineType(bounds.getLineType());
				circle.getBounds().setThick(bounds.getThick());
				circle.getBounds().setColor(bounds.getColor());
				circle.getBounds().setCornerRadius(bounds.getCornerRadius());
				circle.getBounds().setDashArray(bounds.getDashArray());
			}

		}

		Element fulfilStyleElm = Util4Deserializer.child(circleElm,
				FULFIL_STYLE);
		FulfilStyle fulfilStyle = this.loadFulfilStyle(fulfilStyleElm);
		circle.setFulfilStyle(fulfilStyle);
	}
	
	protected ActivityShape loadActivityShape(SubProcess subProcess,Element activityShapeElm){
		if (activityShapeElm==null){
			return null;
		}
		
		String id = activityShapeElm.getAttribute(ID);
		String ref = activityShapeElm.getAttribute(REF);
		
		ModelElement modelElement = subProcess==null?null:subProcess.findWFElementById(ref);
		
		ActivityShape activityShape = new ActivityShapeImpl(id);
		activityShape.setWorkflowElementRef(modelElement);
		
		Element figureElement = Util4Deserializer.child(activityShapeElm,FIGURE);
		Element rectElm = Util4Deserializer.child(figureElement, RECTANGLE);
		this.loadRectangle(activityShape, rectElm);
		
		//修正代码中的错误，例如，如果fulfilstyle==null，则给一个缺省值；如果bound为0
		Rectangle rect = (Rectangle)activityShape.getFigure();
		if (rect.getFulfilStyle()==null){
			FulfilStyle style = new FulfilStyleImpl();
			rect.setFulfilStyle(style);
		}
		
		if (rect.getContentLabel()==null){
			Label contentLb = new LabelImpl();
			contentLb.setText(Label.CONTENT_FROM_WORKFLOW_ELEMENT);
			rect.setContentLabel(contentLb);
		}
		
		List<Element> attachedStartNodeList = Util4Deserializer.children(activityShapeElm, NODE);
		if (attachedStartNodeList!=null){
			for (Element startNodeElm : attachedStartNodeList){
				StartNodeShape startNodeShape = this.loadStartNodeShape(subProcess,startNodeElm);
				activityShape.addAttachedStartNodeShape(startNodeShape);
			}
		}
		
		return activityShape;
	}
	
	protected LaneShape loadLaneShape(SubProcess subProcess,Element laneElm){
		if (laneElm==null) return null;
		String id = laneElm.getAttribute(ID);
		LaneShape lane = new LaneShapeImpl(id);
		
		Element figureElement = Util4Deserializer.child(laneElm, FIGURE);
		Element planeElm = Util4Deserializer.child(figureElement, RECTANGLE);
		this.loadRectangle(lane, planeElm);
		
		//修正代码中的错误，例如，如果fulfilstyle==null，则给一个缺省值；如果bound为0
		Rectangle rect = (Rectangle)lane.getFigure();
		if (rect.getFulfilStyle()==null){
			FulfilStyle style = new FulfilStyleImpl();
			rect.setFulfilStyle(style);
		}
		
		//下级节点
		List<Element> children = Util4Deserializer.children(laneElm, NODE);
		if (children!=null){
			for (Element child : children){
				String type = child.getAttribute(TYPE);
				if (type==null) type="";
				
				if (ACTIVITY.equals(type)){
					ActivityShape activityShape =loadActivityShape(subProcess,child);
					lane.addProcessNodeShape(activityShape);
				}else if (START_NODE.equals(type)){
					StartNodeShape startNodeShape = loadStartNodeShape(subProcess,child);
					lane.addProcessNodeShape(startNodeShape);
				}else if (END_NODE.equals(type)){
					EndNodeShape endNodeShape = loadEndNodeShape(subProcess,child);
					lane.addProcessNodeShape(endNodeShape);
				}else if (ROUTER.equals(type)){
					RouterShape routerShape = loadRouterShape(subProcess,child);
					lane.addProcessNodeShape(routerShape);
				}
				else if (COMMENT.equals(type)){
					CommentShape commentShape = this.loadCommentShape(child);
					lane.addComment(commentShape);
				}
				else if (GROUP.equals(type)){
					GroupShape groupShape = this.loadGroupShapeWithoutConnector(subProcess,child);
					lane.addGroup(groupShape);
				}
			}
		}
		
		return lane;
	}
	
	/*
	protected void loadPlane(DiagramElement diagramElm, Element planeElm){
		if (planeElm==null) return ;
		Plane plane = new PlaneImpl();
		diagramElm.setShape(plane);
		
		Element labelElm = Util4Deserializer.child(planeElm, LABEL);
		Label lb = this.loadLabel(labelElm);
		plane.setLabel(lb);
		
		Element boundsElm = Util4Deserializer.child(planeElm, BOUNDS);
		Bounds bounds = this.loadBounds(boundsElm);
		plane.setBounds(bounds);
		
		Element fulfilStyleElm = Util4Deserializer.child(planeElm, FULFIL_STYLE);
		FulfilStyle fulfilStyle = this.loadFulfilStyle(fulfilStyleElm);
		plane.setFulfilStyle(fulfilStyle);
	}
	*/
	protected CommentShape loadCommentShape( Element commentElm){
		if (commentElm==null) return null;
		String id = commentElm.getAttribute(ID);
		CommentShape comment = new CommentShapeImpl(id);
		
		Element figureElement = Util4Deserializer.child(commentElm, FIGURE);
		Element rectElm = Util4Deserializer.child(figureElement,RECTANGLE);
		loadRectangle(comment, rectElm);
		
		//修正代码中的错误，例如，如果fulfilstyle==null，则给一个缺省值；如果bound为0
		Rectangle rect = (Rectangle)comment.getFigure();
		if (rect.getFulfilStyle()==null){
			FulfilStyle style = new FulfilStyleImpl();
			rect.setFulfilStyle(style);
		}
		
		//如果没有contentLabel则初始化一个
		if (rect.getContentLabel()==null){
			Label contentLb = new LabelImpl();
			contentLb.setText("");
			rect.setContentLabel(contentLb);
		}
		
		return comment;
	}
	
	protected void loadRectangle(DiagramElement diagramElm , Element rectElm){
		if (rectElm==null)return;
		Rectangle rect = (Rectangle)diagramElm.getFigure();
		
		Element titleElm = Util4Deserializer.child(rectElm, TITLE);		
		if (titleElm!=null){
			Element labelElm = Util4Deserializer.child(titleElm, LABEL);
			Label lb = this.loadLabel(labelElm);
			if (lb!=null){
				rect.getTitleLabel().setFontColor(lb.getFontColor());
				rect.getTitleLabel().setFontName(lb.getFontName());
				rect.getTitleLabel().setFontSize(lb.getFontSize());
				rect.getTitleLabel().setText(lb.getText());
				rect.getTitleLabel().setTextDirection(lb.getTextDirection());
				rect.getTitleLabel().setFontStyle(lb.getFontStyle());
			}
		}
		
		Element contentElm = Util4Deserializer.child(rectElm, CONTENT);
		if (contentElm!=null){
			Element labelElm = Util4Deserializer.child(contentElm, LABEL);
			Label lb = this.loadLabel(labelElm);
			rect.setContentLabel(lb);
		}else{
			rect.setContentLabel(null);
		}
		
		Element boundsElm = Util4Deserializer.child(rectElm, BOUNDS);
		if (boundsElm!=null){
			Bounds bounds = loadBounds(boundsElm);
			if (bounds!=null){
				rect.getBounds().setX(bounds.getX());
				rect.getBounds().setY(bounds.getY());
				rect.getBounds().setWidth(bounds.getWidth());
				rect.getBounds().setHeight(bounds.getHeight());
				rect.getBounds().setLineType(bounds.getLineType());
				rect.getBounds().setThick(bounds.getThick());
				rect.getBounds().setColor(bounds.getColor());
				rect.getBounds().setCornerRadius(bounds.getCornerRadius());
				rect.getBounds().setDashArray(bounds.getDashArray());
			}

		}
		
		Element fulfilElm = Util4Deserializer.child(rectElm, FULFIL_STYLE);
		if (fulfilElm!=null){
			FulfilStyle fulfilStyle = loadFulfilStyle(fulfilElm);
			rect.setFulfilStyle(fulfilStyle);
		}else{
			rect.setFulfilStyle(null);
		}
	}
	
	protected Bounds loadBounds(Element boundsElm){
		if (boundsElm==null)return null;
		Bounds bounds = new BoundsImpl();
		
		String x = boundsElm.getAttribute(X);
		try{
			bounds.setX(Integer.parseInt(x));
		}catch(Exception e){
			
		}
		String y = boundsElm.getAttribute(Y);
		try{
			bounds.setY(Integer.parseInt(y));
		}catch(Exception e){
			
		}
		
		String height = boundsElm.getAttribute(HEIGHT);
		if(StringUtils.isNumeric(height)){
			bounds.setHeight(Integer.parseInt(height));
		}
		
		String width = boundsElm.getAttribute(WIDTH);
		if (StringUtils.isNumeric(width)){
			bounds.setWidth(Integer.parseInt(width));
		}
		
		Element boundsStyleElm = Util4Deserializer.child(boundsElm, BORDER_STYLE);
		if(boundsStyleElm!=null){
			
			String color = boundsStyleElm.getAttribute(COLOR);
			if (!StringUtils.isEmpty(color)){
				bounds.setColor(color);
			}
			
			String lineType = boundsStyleElm.getAttribute(LINE_TYPE);
			if (!StringUtils.isEmpty(lineType)){
				bounds.setLineType(lineType);
			}
			
			String thick = boundsStyleElm.getAttribute(THICK);
			if (StringUtils.isNumeric(thick)){
				bounds.setThick(Integer.parseInt(thick));
			}
			

			String radius = boundsStyleElm.getAttribute(RADIUS);
			if (!StringUtils.isEmpty(radius) && StringUtils.isNumeric(radius)){
				bounds.setCornerRadius(Integer.parseInt(radius));
			}
			
			String dashArr = boundsStyleElm.getAttribute(DASH_ARRAY);
			if (!StringUtils.isEmpty(dashArr)){
				bounds.setDashArray(dashArr);
			}
		}else{
			bounds.setThick(-1);
			bounds.setColor(null);
			bounds.setLineType(null);
			bounds.setCornerRadius(0);
		}
		return bounds;
	}
	
	protected FulfilStyle loadFulfilStyle(Element fulfilStyleElm){
		if (fulfilStyleElm==null) return null;
		
		FulfilStyle fulfilStyle = new FulfilStyleImpl();
		String color1 = fulfilStyleElm.getAttribute(COLOR+"1");
		if (color1!=null){
			fulfilStyle.setColor1(color1);
		}
		
		String color2 = fulfilStyleElm.getAttribute(COLOR+"2");
		if (color2!=null){
			fulfilStyle.setColor2(color2);
		}
		
		
		String gradientStyle = fulfilStyleElm.getAttribute(GRADIENT_STYLE);
		if (gradientStyle!=null){
			fulfilStyle.setGradientStyle(gradientStyle);
		}
		
		return fulfilStyle;
	}
	
	protected AssociationShape loadAssociationShape(Diagram diagram,Element associationElm){
		String id = associationElm.getAttribute(ID);
		String from = associationElm.getAttribute(FROM);
		String to = associationElm.getAttribute(TO);
		
		DiagramElement fromElm = diagram.findChild(from);
		DiagramElement toElm = diagram.findChild(to);
			
		AssociationShape associationShape = new AssociationShapeImpl(id);
		associationShape.setFromNode((NodeShape)fromElm);
		associationShape.setToNode((NodeShape)toElm);

		Element figureElement = Util4Deserializer.child(associationElm, FIGURE);
		Element lineElm = Util4Deserializer.child(figureElement, LINE);
		loadLine(associationShape,lineElm);
		
		return associationShape;
	}
	
	protected MessageFlowShape loadMessageFlowShape(Diagram diagram,Element messageFlowElm){
		String id = messageFlowElm.getAttribute(ID);
		String from = messageFlowElm.getAttribute(FROM);
		String to = messageFlowElm.getAttribute(TO);
		DiagramElement fromElm = diagram.findChild(from);
		DiagramElement toElm = diagram.findChild(to);
			
		MessageFlowShape messageFlow = new MessageFlowShapeImpl(id);
		messageFlow.setFromNode((NodeShape)fromElm);
		messageFlow.setToNode((NodeShape)toElm);

		Element figureElement = Util4Deserializer.child(messageFlowElm, FIGURE);
		Element lineElm = Util4Deserializer.child(figureElement, LINE);
		loadLine(messageFlow,lineElm);
		
		return messageFlow;
		
	}
	protected void loadLine(DiagramElement diagramElm , Element lineElm ){
		Line line = (Line)diagramElm.getFigure();
		
		Element titleElm = Util4Deserializer.child(lineElm, TITLE);		
		if (titleElm!=null){
			Element labelElm = Util4Deserializer.child(titleElm, LABEL);
			Label lb = this.loadLabel(labelElm);
			if (lb!=null){
				line.getTitleLabel().setFontColor(lb.getFontColor());
				line.getTitleLabel().setFontName(lb.getFontName());
				line.getTitleLabel().setFontSize(lb.getFontSize());
				line.getTitleLabel().setText(lb.getText());
				line.getTitleLabel().setTextDirection(lb.getTextDirection());
				line.getTitleLabel().setFontStyle(lb.getFontStyle());
			}
		}

		Element boundsElm = Util4Deserializer.child(lineElm, BOUNDS);
		if (boundsElm!=null){
			Bounds bounds = loadBounds(boundsElm);
			if (bounds!=null){
				line.getBounds().setX(bounds.getX());
				line.getBounds().setY(bounds.getY());
				line.getBounds().setWidth(bounds.getWidth());
				line.getBounds().setHeight(bounds.getHeight());
				line.getBounds().setLineType(bounds.getLineType());
				line.getBounds().setThick(bounds.getThick());
				line.getBounds().setColor(bounds.getColor());
				line.getBounds().setCornerRadius(bounds.getCornerRadius());
				line.getBounds().setDashArray(bounds.getDashArray());
			}

		}
		
		String position = lineElm.getAttribute(LABEL_POSITION);
		

		if (!StringUtils.isEmpty(position)) {
			Point p = Point.fromString(position);
			if (p!=null){
				line.getLabelPosition().setX(p.getX());
				line.getLabelPosition().setY(p.getY());
			}
		}
		
		String pointListStr = lineElm.getAttribute(POINT_LIST);
		if (!StringUtils.isEmpty(pointListStr)){
			List<Point> l = string2PointList(pointListStr);
			line.getPoints().clear();
			line.getPoints().addAll(l);
		}


	}
    private List<Point> string2PointList(String s){
    	StringTokenizer tokenizer = new StringTokenizer(s,";");
    	List<Point> l = new ArrayList<Point>();
    	while(tokenizer.hasMoreTokens()){
    		String tmp = tokenizer.nextToken();
    		Point p = Point.fromString(tmp);
    		l.add(p);
    	}
    	return l;
    }
	
	protected Label loadLabel(Element labelElm){
		if (labelElm==null) return null;
		Label lb = new LabelImpl();		
		

		String direction = labelElm.getAttribute(TEXT_DIRECTION);
		if(!StringUtils.isEmpty(direction)){
			lb.setTextDirection(direction);
		}
		
		String fontName = labelElm.getAttribute(FONT_NAME);
		if (!StringUtils.isEmpty(fontName)){
			lb.setFontName(fontName);
		}		
		
		String color = labelElm.getAttribute(COLOR);
		if (!StringUtils.isEmpty(color)){
			lb.setFontColor(color);
		}
		
		String weight = labelElm.getAttribute(FONT_STYLE);
		if (!StringUtils.isEmpty(weight)){
			lb.setFontStyle(weight);
		}
		
		
		String size = labelElm.getAttribute(SIZE);
		if (!StringUtils.isEmpty(size)){
			try{
				lb.setFontSize(Integer.parseInt(size));
			}catch(Exception e){
				
			}
		}

		String txt = this.loadCDATA(labelElm);
		lb.setText(txt);
		
		return lb;
	}
	
	protected void loadSubflow(WorkflowProcess wp,Element subflowElement) throws DeserializerException{
		String name = subflowElement.getAttribute(NAME);
		String displayName = subflowElement.getAttribute(DISPLAY_NAME);

		SubProcess subflow = new SubProcessImpl(wp,name,displayName);
		
		//
		// 解析datafields
		this.loadProperties(subflow, subflow.getProperties(), Util4Deserializer.child(
				subflowElement, this.PROPERTIES));
		
		// 解析duration
		subflow.setDuration(this.createDuration(subflowElement.getAttribute(DURATION)));


		// 所有业务节点,同时将这个节点的所有的属性都解析出来保存到节点信息中。
		loadActivities(subflow, subflow.getActivities(), Util4Deserializer.child(
				subflowElement, ACTIVITIES));
		
		// 工作流同步器节点
		loadRouters(subflow, subflow.getRouters(), Util4Deserializer.child(
				subflowElement, ROUTERS));
		// 结束节点
		loadEndNodes(subflow, subflow.getEndNodes(), Util4Deserializer.child(
				subflowElement, END_NODES));
		

		// 开始节点
		loadStartNodes(subflow, subflow.getStartNodes(), Util4Deserializer.child(
				subflowElement, START_NODES));
		
		
		// 转移线
		loadTransitions(subflow, subflow.getTransitions(),Util4Deserializer.child(subflowElement,
				TRANSITIONS));
		
		//设置entry
		String entryNodeId = subflowElement.getAttribute(ENTRY);
		WorkflowElement entryNode = subflow.findWFElementById(entryNodeId);
		if (entryNode!=null){
			
			subflow.setEntry((Node)entryNode);
		}
		
		//设置activity的attached nodes
		List<StartNode> startNodes = subflow.getStartNodes();
		for (StartNode startNode : startNodes){
			Feature feature = startNode.getFeature();
			if (feature!=null){
				if (feature instanceof TimerStartFeature){
					Activity act = ((TimerStartFeature) feature).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
				else if (feature instanceof CatchFaultFeature){
					Activity act = ((CatchFaultFeature) feature).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
				
				else if (feature instanceof CatchCompensationFeature){
					Activity act = ((CatchCompensationFeature) feature).getAttachedToActivity();
					if (act!=null){
						act.getAttachedStartNodes().add(startNode);
					}
				}
			}
		}
		
		// 所有的监听器
		loadEventListeners(subflow,subflow.getEventListeners(), Util4Deserializer.child(
				subflowElement, EVENT_LISTENERS));
		// 加载扩展属性
		Map<String, String> extAttrs = subflow.getExtendedAttributes();
		loadExtendedAttributes(extAttrs, Util4Deserializer.child(
				subflowElement, EXTENDED_ATTRIBUTES));
		
		subflow.setDescription(loadCDATA(Util4Deserializer.child(subflowElement,DESCRIPTION)));
		wp.addSubProcess(subflow);

	}
	


	protected void loadImports(WorkflowProcess wp,List<Element> importElems)throws InvalidModelException,DeserializerException,IOException{
		if (importElems==null){
			return ;
		}
		
		for (Element importElm : importElems){
			String type = importElm.getAttribute(IMPORT_TYPE);
			if (Import.SERVICES_IMPORT.equals(type)){
				/* 20140106 ResourceRepository被取消
				ImportImpl<ServiceDef> serviceImport = new ImportImpl<ServiceDef>(wp);
				serviceImport.setImportType(type);
				serviceImport.setLocation(importElm.getAttribute(LOCATION));
				
				List<ServiceDef> services = this.importLoader.loadServices(serviceImport.getLocation());
				serviceImport.setContents(services);
				
				wp.getImportsForService().add(serviceImport);
				*/
			}
			else if (Import.RESOURCES_IMPORT.equals(type)){
				/* 20140106 ResourceRepository被取消
				ImportImpl<ResourceDef> resourceImport = new ImportImpl<ResourceDef>(wp);
				resourceImport.setImportType(type);
				resourceImport.setLocation(importElm.getAttribute(LOCATION));
				
				List<ResourceDef> resources = this.importLoader.loadResources(resourceImport.getLocation());
				resourceImport.setContents(resources);
				
				wp.getImportsForResource().add(resourceImport);
				*/
			}
			else if (Import.PROCESS_IMPORT.equals(type)){
				ImportImpl processImport = new ImportImpl(wp);
				processImport.setImportType(type);
				processImport.setLocation(importElm.getAttribute(LOCATION));
				processImport.setPackageId(importElm.getAttribute(PACKAGE_ID));
				processImport.setId(importElm.getAttribute(ID));
				processImport.setName(importElm.getAttribute(NAME));
				processImport.setDisplayName(importElm.getAttribute(DISPLAY_NAME));
				
				wp.addImport(processImport);
				
			}
		}
	}
	
	/**
	 * duration string的格式为：5 HOUR,或者 5 BUSINESS DAY 等。
	 * @param durationStr
	 * @return
	 */
	protected Duration createDuration(String durationStr){
		if (StringUtils.isEmpty(durationStr))return null;
		StringTokenizer tokenizer = new StringTokenizer(durationStr," ");
		String valueStr = null;
		String unit = null;
		String business = null;
		if (tokenizer.countTokens()==2){
			valueStr = tokenizer.nextToken();
			unit = tokenizer.nextToken();
		}else if (tokenizer.countTokens()==3){
			valueStr = tokenizer.nextToken();
			business = tokenizer.nextToken();
			unit = tokenizer.nextToken();
		}
		if (!StringUtils.isEmpty(valueStr)){
			try{
				int value = Integer.parseInt(valueStr);
				Duration du = new Duration(value,unit);
				if (!StringUtils.isEmpty(business)){
					du.setBusinessTime(true);
				}
				return du;
			}catch(Exception e){
				log.error(e);
				return null;
			}
			
		}else{
			log.error("Error duration attribute:"+durationStr);
		}
		return null;
		
	}
	protected Duration createDuration_deprecated(Element durationElem){
		if (durationElem==null) return null;

		String _v = durationElem.getAttribute(VALUE);
		int intV = -1;
		if (_v!=null){
			try{
				intV = Integer.parseInt(_v);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		String unit = durationElem.getAttribute(UNIT);
		
		String _isBizTime = durationElem.getAttribute(IS_BUSINESS_TIME);
		boolean isBizTime = false;
		if (_isBizTime!=null){
			try{
				isBizTime = Boolean.parseBoolean(_isBizTime);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Duration du = new Duration(intV,unit);
		du.setBusinessTime(isBizTime);
		return du;
	}

	/**
	 * @param listeners
	 * @param element
	 */
	protected void loadEventListeners(ModelElement parent,List<EventListenerDef> listeners,
			Element element) {
		listeners.clear();
		if (element == null) {
			return;
		}
		if (element == null) {
			return;
		}
		List<Element> listenerElms = Util4Deserializer.children(element,
				EVENT_LISTENER);
		Iterator<Element> iter = listenerElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			EventListenerDefImpl listener = new EventListenerDefImpl();
			listener.setParent(parent);
			listener.setName(elm.getAttribute(NAME));
			listener.setDisplayName(elm.getAttribute(DISPLAY_NAME));
			//listener.setBizCategory(elm.getAttribute(PACKAGE_ID));
			listener.setDescription(this.loadCDATA(Util4Deserializer.child(elm,DESCRIPTION)));
			listener.setListenerBeanName(elm.getAttribute(BEAN_NAME));
			listener.setListenerClassName(elm.getAttribute(CLASS_NAME));
			listeners.add(listener);
		}
	}

	/**
	 * @param subflow
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadStartNodes(SubProcess subflow,
			List<StartNode> startNodes, Element startNodesElem)
			throws DeserializerException {
		if (startNodesElem == null) {
			return;
		}
		List<Element> startNodeElements = Util4Deserializer.children(startNodesElem,
				START_NODE);
		startNodes.clear();
		Iterator<Element> iter = startNodeElements.iterator();
		while (iter.hasNext()) {
			Element startNodeElem = iter.next();

			StartNode startNode = new StartNodeImpl(subflow, startNodeElem
					.getAttribute(NAME));

			startNode.setDescription(loadCDATA(Util4Deserializer.child(startNodeElem,DESCRIPTION)));
			startNode.setDisplayName(startNodeElem.getAttribute(DISPLAY_NAME));
			
			Element decoratorElem = Util4Deserializer.child(startNodeElem, FEATURES);
			if (decoratorElem!=null){
				boolean find = false;
				Element normalStartFeatureElm = Util4Deserializer.child(decoratorElem, NORMAL_START_FEATURE);
				if (normalStartFeatureElm!=null){
					//默认值，无需更多设置
					find = true;
				}
				
				Element catchCompensationFeatureElm = Util4Deserializer.child(decoratorElem, CATCH_COMPENSATION_FEATURE);
				if (catchCompensationFeatureElm!=null && !find){
					CatchCompensationFeatureImpl catchCompensationFeature = new CatchCompensationFeatureImpl();
					catchCompensationFeature.setCompensationCode(catchCompensationFeatureElm.getAttribute(COMPENSATION_CODE));
					
					String attachedToActId = catchCompensationFeatureElm.getAttribute(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						catchCompensationFeature.setAttachedToActivity((Activity)subflow.findWFElementById(attachedToActId));
					}
					
					startNode.setFeature(catchCompensationFeature);
					find=true;
				}
				
				Element catchFaultFeatureElm = Util4Deserializer.child(decoratorElem, CATCH_FAULT_FEATURE);
				if (catchFaultFeatureElm!=null && !find){
					CatchFaultFeatureImpl catchFaultFeature = new CatchFaultFeatureImpl();
					catchFaultFeature.setErrorCode(catchFaultFeatureElm.getAttribute(ERROR_CODE));
					
					String attachedToActId = catchFaultFeatureElm.getAttribute(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						catchFaultFeature.setAttachedToActivity((Activity)subflow.findWFElementById(attachedToActId));
					}
					
					startNode.setFeature(catchFaultFeature);
					find = true;
				}
				
				Element timerStartFeatureElm = Util4Deserializer.child(decoratorElem, TIMER_START_FEATURE);
				if (timerStartFeatureElm!=null && !find){
					TimerStartFeatureImpl timerStartDec = new TimerStartFeatureImpl();
					
					String timerOperationName = timerStartFeatureElm.getAttribute(TIMER_OPERATION_NAME);
					timerStartDec.setTimerOperationName(TimerOperationName.fromValue(timerOperationName));
					
					String attachedToActId = timerStartFeatureElm.getAttribute(ATTACHED_TO_ACTIVITY);
					if (attachedToActId!=null){
						timerStartDec.setAttachedToActivity((Activity)subflow.findWFElementById(attachedToActId));
					}
					
					String cancelAttachedToAct = timerStartFeatureElm.getAttribute(IS_CANCEL_ATTACHED_TO_ACTIVITY);
					if (cancelAttachedToAct!=null){
						try{
							timerStartDec.setCancelAttachedToActivity(Boolean.parseBoolean(cancelAttachedToAct));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
					Element startTimeElem = Util4Deserializer.child(timerStartFeatureElm, START_TIME_EXPRESSION);
					if (startTimeElem!=null){
						timerStartDec.setStartTimeExpression(this.createExpression(Util4Deserializer.child(startTimeElem, EXPRESSION)));
					}
					
					Element endTimeElem = Util4Deserializer.child(timerStartFeatureElm, END_TIME_EXPRESSION);
					if (endTimeElem!=null){
						timerStartDec.setEndTimeExpression(this.createExpression(Util4Deserializer.child(endTimeElem, EXPRESSION)));
					}
					
					Element intervalElem = Util4Deserializer.child(timerStartFeatureElm, REPEAT_INTERVAL_EXPRESSION);
					if (intervalElem!=null){
						timerStartDec.setRepeatIntervalExpression(this.createExpression(Util4Deserializer.child(intervalElem, EXPRESSION)));
					}
					
					Element repeatCountElem = Util4Deserializer.child(timerStartFeatureElm, REPEAT_COUNT_EXPRESSION);
					if (repeatCountElem!=null){
						timerStartDec.setRepeatCountExpression(this.createExpression(Util4Deserializer.child(repeatCountElem, EXPRESSION)));
					}
					
					Element cronElem = Util4Deserializer.child(timerStartFeatureElm, CRON_EXPRESSION);
					if (cronElem!=null){
						timerStartDec.setCronExpression(this.createExpression(Util4Deserializer.child(cronElem, EXPRESSION)));
					}
					startNode.setFeature(timerStartDec);
					find = true;
				}
				
				Element webserviceStartElement = Util4Deserializer.child(decoratorElem, WEBSERVICE_START_FEATURE);
				if (webserviceStartElement!=null && !find){
					WebserviceStartFeatureImpl wsStartFeatureImpl = new WebserviceStartFeatureImpl();
					ServiceBinding serviceBinding = this.loadServiceBinding(subflow, Util4Deserializer.child(webserviceStartElement, SERVICE_BINDING));
					wsStartFeatureImpl.setServiceBinding(serviceBinding);
					startNode.setFeature(wsStartFeatureImpl);
				}
			}
			
			loadExtendedAttributes(startNode.getExtendedAttributes(),
					Util4Deserializer.child(startNodeElem, EXTENDED_ATTRIBUTES));

			startNodes.add(startNode);
		}
	}

	/**
	 * @param subflow
	 * @param endNodes
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadEndNodes(SubProcess subflow, List<EndNode> endNodes,
			Element endNodesElem) throws DeserializerException {

		if (endNodesElem == null) {
			return;
		}
		endNodes.clear();
		
		List<Element> endNodesElms = Util4Deserializer.children(endNodesElem, END_NODE);
		Iterator<Element> iter = endNodesElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			EndNode endNode = new EndNodeImpl(subflow, elm.getAttribute(NAME));

			endNode.setDescription(loadCDATA(Util4Deserializer.child(elm,DESCRIPTION)));
			endNode.setDisplayName(elm.getAttribute(DISPLAY_NAME));
			
			Element featuresElem = Util4Deserializer.child(elm, FEATURES);
			if (featuresElem!=null){
				boolean find = false;
				Element normalEndFeatureElm = Util4Deserializer.child(featuresElem, NORMAL_END_FEATURE);
				if (normalEndFeatureElm!=null){
					//默认值，无需更多设置
					find = true;
				}
				
				Element throwCompensationFeatureElm = Util4Deserializer.child(featuresElem, THROW_COMPENSATION_FEATURE);
				if (throwCompensationFeatureElm!=null && !find){
					ThrowCompensationFeatureImpl throwCompensationFeature = new ThrowCompensationFeatureImpl();
					String compensationCodes = throwCompensationFeatureElm.getAttribute(COMPENSATION_CODES);
					if (compensationCodes!=null){
						StringTokenizer tokenizer = new StringTokenizer(compensationCodes,",");
						while(tokenizer.hasMoreTokens()){
							throwCompensationFeature.addCompensationCode(tokenizer.nextToken());
						}
					}else{
						//添加缺省的compensation code
						throwCompensationFeature.addCompensationCode(CatchCompensationFeature.CATCH_ALL_COMPENSATION);
					}
					endNode.setFeature(throwCompensationFeature);
					find=true;
				}
				
				Element throwFaultFeatureElm = Util4Deserializer.child(featuresElem, THROW_FAULT_FEATURE);
				if (throwFaultFeatureElm!=null && !find){
					ThrowFaultFeatureImpl throwFaultFeature = new ThrowFaultFeatureImpl();
					throwFaultFeature.setErrorCode(throwFaultFeatureElm.getAttribute(ERROR_CODE));
					
					endNode.setFeature(throwFaultFeature);
					find = true;
				}
				
				Element throwTerminationFeatureElm = Util4Deserializer.child(featuresElem, THROW_TERMINATION_FEATURE);
				if (throwTerminationFeatureElm!=null && !find){
					endNode.setFeature(new ThrowTerminationFeatureImpl());
					find = true;
				}
			}
			
			loadExtendedAttributes(endNode.getExtendedAttributes(), Util4Deserializer
					.child(elm, EXTENDED_ATTRIBUTES));
			endNodes.add(endNode);
		}
	}

	/**
	 * @param wp
	 * @param routers
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadRouters(SubProcess wp,
			List<Router> routers, Element element)
			throws DeserializerException {

		if (element == null) {
			return;
		}
		routers.clear();
		
		List<Element> synchronizerElms = Util4Deserializer.children(element,
				ROUTER);
		Iterator<Element> iter = synchronizerElms.iterator();
		while (iter.hasNext()) {
			Element elm = iter.next();
			Router router = new RouterImpl(wp, elm
					.getAttribute(NAME));

			router.setDescription(this.loadCDATA(Util4Deserializer.child(elm,
					DESCRIPTION)));
			router.setDisplayName(elm.getAttribute(DISPLAY_NAME));

			loadExtendedAttributes(router.getExtendedAttributes(),
					Util4Deserializer.child(elm, EXTENDED_ATTRIBUTES));

			Element featuresElm = Util4Deserializer.child(elm, FEATURES);
			if (featuresElm!=null){				
				Element fElm = Util4Deserializer.child(featuresElm, DEFAULT_ROUTER_FEATURE);
				if (fElm!=null){
					router.setFeature(new DefaultRouterFeature());
				}
				
				fElm = Util4Deserializer.child(featuresElm, ANDJOIN_ANDSPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new AndJoinAndSplitRouterFeature());
				}
				fElm = Util4Deserializer.child(featuresElm, XORJOIN_XORSPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new XOrJoinXOrSplitRouterFeature());
				}
				fElm = Util4Deserializer.child(featuresElm, ORJOIN_ORSPLIT_FEATURE);
				if (fElm!=null){
					router.setFeature(new OrJoinOrSplitRouterFeature());
				}
				fElm = Util4Deserializer.child(featuresElm, CUSTOMIZED_JOIN_SPLIT_FEATURE);
				if (fElm!=null){
					CustomizedRouterFeature routerFeature = new CustomizedRouterFeature();
					router.setFeature(routerFeature);
					String joinEvaluator = Util4Deserializer.elementAsString(fElm, JOIN_EVALUATOR);
					if (!StringUtils.isEmpty(joinEvaluator)){
						routerFeature.setJoinEvaluatorClass(joinEvaluator);
					}
					String splitEvaluator = Util4Deserializer.elementAsString(fElm, SPLIT_EVALUATOR);
					if (!StringUtils.isEmpty(splitEvaluator)){
						routerFeature.setSplitEvaluatorClass(splitEvaluator);
					}
					
				}
			}
			
			routers.add(router);
		}
	}

	/**
	 * @param subflow
	 * @param activities
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadActivities(SubProcess subflow,
			List<Activity> activities, Element element)
			throws DeserializerException {

		if (element == null) {
			// log.debug("Activites element was null");
			return;
		}

		List<Element> activitElements = Util4Deserializer.children(element, ACTIVITY);
		activities.clear();
		Iterator<Element> iter = activitElements.iterator();
		while (iter.hasNext()) {
			Element activityElement = iter.next();

			ActivityImpl activity = new ActivityImpl(subflow, activityElement
					.getAttribute(NAME));

			activity.setDisplayName(activityElement
					.getAttribute(DISPLAY_NAME));
			activity.setDescription(loadCDATA(Util4Deserializer.child(activityElement,DESCRIPTION)));
			
			Duration du = this.createDuration(activityElement.getAttribute(DURATION));
			activity.setDuration(du);
			activity.setPriority(activityElement.getAttribute(PRIORITY));
			String loopStrategy = activityElement.getAttribute(LOOP_STRATEGY);
			activity.setLoopStrategy(LoopStrategy.fromValue(loopStrategy));
			
			
			//Load Service Binding
			activity.setServiceBinding(loadServiceBinding(subflow,Util4Deserializer.child(activityElement, SERVICE_BINDING)));
			
			//Load Resource Binding
			loadResourceBinding(subflow,activity,Util4Deserializer.child(activityElement, RESOURCE_BINDING));
	
			loadEventListeners(activity,activity.getEventListeners(), Util4Deserializer.child(
					activityElement, EVENT_LISTENERS));
			
			loadEventListeners(activity,activity.getWorkItemEventListeners(), Util4Deserializer.child(
					activityElement, WORKITEM_EVENT_LISTENERS));
			
			loadExtendedAttributes(activity.getExtendedAttributes(),
					Util4Deserializer.child(activityElement, EXTENDED_ATTRIBUTES));

			//Load Porperties
			this.loadProperties(activity, activity.getProperties(), 
					Util4Deserializer.child(activityElement, PROPERTIES));

			activities.add(activity);
		}
	}
	
	protected void loadResourceBinding(SubProcess subflow,Activity activity,Element resourceBindingElem)throws DeserializerException{
		if (resourceBindingElem==null) return;
		
		WorkflowProcess workflowProcess = (WorkflowProcess)subflow.getParent();
		
		ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
		resourceBinding.setDisplayName(resourceBindingElem.getAttribute(DISPLAY_NAME));
		String assignmentStrategy = resourceBindingElem.getAttribute(ASSIGNMENT_STRATEGY);
		resourceBinding.setAssignmentStrategy(WorkItemAssignmentStrategy.fromValue(assignmentStrategy));
		
		Element administratorsElem = Util4Deserializer.child(resourceBindingElem, ADMINISTRATORS);
		if (administratorsElem!=null){
			List<Element> resourceRefElems = Util4Deserializer.children(administratorsElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					String resourceId = elm.getAttribute(RESOURCE_ID);
					
					ResourceDef resource = workflowProcess.getResource(resourceId);
					if (resource==null){
						throw new DeserializerException("Resource not found,resource id = "+resourceId);
					}
					
//					Element prameterAssignmentsElem = Util4Deserializer.child(elm, PARAMETER_ASSIGNMENTS);
//					if (prameterAssignmentsElem!=null){
//						List<Element> parameterAssignmentElems = Util4Deserializer.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
//						for (Element parameterAssignmentElm : parameterAssignmentElems){
//							AssignmentImpl assignment = new AssignmentImpl();
//							Element fromElm = Util4Deserializer.child(parameterAssignmentElm, FROM);
//							
//							assignment.setFrom(this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION)));
//							
//							
//							assignment.setTo(Util4Deserializer.elementAsString(parameterAssignmentElm,TO));
//							
//							resourceRef.getParameterAssignments().add(assignment);
//						}
//					}
					
					resourceBinding.addAdministratorRef(resourceId);
				}//for (Element elm : resourceRefElems)
			}
		}
		
		Element potentialOwnersElem = Util4Deserializer.child(resourceBindingElem, POTENTIAL_OWNERS);
		if (potentialOwnersElem!=null){
			List<Element> resourceRefElems = Util4Deserializer.children(potentialOwnersElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					
					String resourceId = elm.getAttribute(RESOURCE_ID);
					
					ResourceDef resource = workflowProcess.getResource(resourceId);
					if (resource==null){
						throw new DeserializerException("Resource not found,resource id = "+resourceId);
					}
					
//					Element prameterAssignmentsElem = Util4Deserializer.child(elm, PARAMETER_ASSIGNMENTS);
//					if (prameterAssignmentsElem!=null){
//						List<Element> parameterAssignmentElems = Util4Deserializer.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
//						for (Element parameterAssignmentElm : parameterAssignmentElems){
//							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
//							Element fromElm = Util4Deserializer.child(parameterAssignmentElm, FROM);
//							
//							assignment.setFrom(this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION)));
//							assignment.setTo(Util4Deserializer.elementAsString(parameterAssignmentElm,TO));
//							
//							resourceRef.getParameterAssignments().add(assignment);
//						}
//					}
					
					resourceBinding.addPotentialOwnerRef(resource.getId());
				}//for (Element elm : resourceRefElems)
			}
		}
		
		Element readersElem = Util4Deserializer.child(resourceBindingElem, READERS);
		if (readersElem!=null){
			List<Element> resourceRefElems = Util4Deserializer.children(readersElem, RESOURCE_REF);
			if (resourceRefElems!=null){
				for (Element elm : resourceRefElems){
					String resourceId = elm.getAttribute(RESOURCE_ID);
					
					ResourceDef resource = workflowProcess.getResource(resourceId);
					if (resource==null){
						throw new DeserializerException("Resource not found,resource id = "+resourceId);
					}
					
//					Element prameterAssignmentsElem = Util4Deserializer.child(elm, PARAMETER_ASSIGNMENTS);
//					if (prameterAssignmentsElem!=null){
//						List<Element> parameterAssignmentElems = Util4Deserializer.children(prameterAssignmentsElem, PARAMETER_ASSIGNMENT);
//						for (Element parameterAssignmentElm : parameterAssignmentElems){
//							ParameterAssignmentImpl assignment = new ParameterAssignmentImpl();
//							Element fromElm = Util4Deserializer.child(parameterAssignmentElm, FROM);
//							
//							assignment.setFrom(this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION)));
//							assignment.setTo(Util4Deserializer.elementAsString(parameterAssignmentElm,TO));
//							
//							resourceRef.getParameterAssignments().add(assignment);
//						}
//					}
					
					resourceBinding.addReaderRef(resource.getId());
				}//for (Element elm : resourceRefElems)
			}
		}	
		
		activity.setResourceBinding(resourceBinding);
	}
	
	protected ServiceBinding loadServiceBinding(SubProcess subflow,Element serviceBindingElem)throws DeserializerException{
		if (serviceBindingElem==null) return null;
		WorkflowProcess workflowProcess = (WorkflowProcess)subflow.getParent();
		
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setServiceId(serviceBindingElem.getAttribute(SERVICE_ID));
		serviceBinding.setOperationName(serviceBindingElem.getAttribute(OPERATION_NAME));

		//ServiceBinding无需保存Service以及Operation本身，只需要保存其id即可，2012-06-10
//		ServiceDef service = workflowProcess.getService(serviceBinding.getServiceId());
//		if (service==null){
//			throw new DeserializerException("Service not found ,id=["+serviceBinding.getServiceId()+"]");
//		}
//		serviceBinding.setService(service);
		
//		if (service.getInterface()==null) return serviceBinding;//人工活动无需定义interface，2012-02-03 
//		InterfaceDef interfaceDef = service.getInterface();
//		OperationDef op = interfaceDef.getOperation(serviceBinding.getOperationName());
//		if (op==null){
//			throw new DeserializerException("Operation not found ,service id=["+serviceBinding.getServiceId()+"],opreation name=["+serviceBinding.getOperationName()+"]");
//		}
//		serviceBinding.setOperation(op);
		
		Element inputAssignmentsElem = Util4Deserializer.child(serviceBindingElem, INPUT_ASSIGNMENTS);
		if (inputAssignmentsElem!=null){
			List<Element> inputAssignmentElems = Util4Deserializer.children(inputAssignmentsElem, INPUT_ASSIGNMENT);
			if (inputAssignmentElems!=null){
				for (Element inputAssignmentElm : inputAssignmentElems){
					Assignment inputAssignment = new AssignmentImpl();
					Element fromElm = Util4Deserializer.child(inputAssignmentElm, FROM);
					Expression from = this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION));
					inputAssignment.setFrom(from);
					Element toElm = Util4Deserializer.child(inputAssignmentElm, TO);
					Expression to = this.createExpression(Util4Deserializer.child(toElm, EXPRESSION));
					inputAssignment.setTo(to);
					
					serviceBinding.getInputAssignments().add(inputAssignment);
				}
			}
		}
		
		Element outputAssignmentsElem = Util4Deserializer.child(serviceBindingElem, OUTPUT_ASSIGNMENTS);
		if (outputAssignmentsElem!=null){
			List<Element> outputAssignmentElems = Util4Deserializer.children(outputAssignmentsElem, OUTPUT_ASSIGNMENT);
			if (outputAssignmentElems!=null){
				for (Element outputAssignmentElm : outputAssignmentElems){
					AssignmentImpl outputAssignment = new AssignmentImpl();
					Element fromElm = Util4Deserializer.child(outputAssignmentElm, FROM);
					Expression from = this.createExpression(Util4Deserializer.child(fromElm, EXPRESSION));
					
					outputAssignment.setFrom(from);
					
					Element toElm = Util4Deserializer.child(outputAssignmentElm, TO);
					Expression to = this.createExpression(Util4Deserializer.child(toElm, EXPRESSION));
					outputAssignment.setTo(to);
					serviceBinding.getOutputAssignments().add(outputAssignment);
				}
			}
		}
		return serviceBinding;
//		Element propOverridesElem = Util4Deserializer.child(serviceBindingElem, PROP_OVERRIDES);
//		if (propOverridesElem!=null){
//			List<Element> propOverrideElems = Util4Deserializer.children(propOverridesElem, PROP_OVERRIDE);
//			if (propOverrideElems!=null){
//				for (Element propOverrideElm : propOverrideElems){
//					PropOverrideImpl propOverride = new PropOverrideImpl();
//					propOverride.setPropGroupName(propOverrideElm.getAttribute(PROP_GROUP_NAME));
//					propOverride.setPropName(propOverrideElm.getAttribute(PROP_NAME));
//					propOverride.setValue(propOverrideElm.getAttribute(VALUE));
//					
//					serviceBinding.getPropOverrides().add(propOverride);
//				}
//			}
//		}
//		
		
	}




	/**
	 * @param subflow
	 * @param transitionsElement
	 * @throws DeserializerException
	 */
	protected void loadTransitions(SubProcess subflow,List<Transition> transitions,
			Element transitionsElement) throws DeserializerException {

		if (transitionsElement == null) {
			return;
		}

		transitions.clear();
		
		List<Element> transitionElements = Util4Deserializer.children(transitionsElement, TRANSITION);

		Iterator<Element> iter = transitionElements.iterator();
		while (iter.hasNext()) {
			Element transitionElement = iter.next();
			Transition transition = createTransition(subflow, transitionElement);
			transitions.add(transition);
			Node fromNode = transition.getFromNode();
			Node toNode = transition.getToNode();
			if (fromNode != null ) {
				fromNode.getLeavingTransitions().add(
						transition);
			}
			if (toNode != null ) {
				toNode.getEnteringTransitions()
				.add(transition);
			}
		}
	}


	/**
	 * @param subflow
	 * @param element
	 * @return
	 * @throws DeserializerException
	 */
	protected Transition createTransition(SubProcess subflow, Element element)
			throws DeserializerException {
		String fromNodeId = element.getAttribute(FROM);
		String toNodeId = element.getAttribute(TO);
		Node fromNode = (Node) subflow.findWFElementById(fromNodeId);
		Node toNode = (Node) subflow.findWFElementById(toNodeId);

		TransitionImpl transition = new TransitionImpl(subflow,
				element.getAttribute(NAME), fromNode, toNode);


		transition.setDisplayName(element.getAttribute(DISPLAY_NAME));
		transition.setDescription(this.loadCDATA(Util4Deserializer.child(element,DESCRIPTION)));
		
		String isLoop = element.getAttribute(IS_LOOP);
		if (isLoop!=null){
			try{
				transition.setIsLoop(Boolean.parseBoolean(isLoop));
			}catch(Exception e){
				
			}
		}
		
		String isDefault = element.getAttribute(IS_DEFAULT);
		if (isDefault!=null){
			try{
				transition.setDefault(Boolean.parseBoolean(isDefault));
			}catch(Exception e){
				
			}
		}
		
		
		Element conditionElement = Util4Deserializer.child(element, CONDITION);
		
		if (conditionElement!=null){
			Element expressionElem = Util4Deserializer.child(conditionElement, EXPRESSION);
			transition.setCondition(createExpression(expressionElem));
		}

		// load extended attributes
		Map<String, String> extAttrs = transition.getExtendedAttributes();
		loadExtendedAttributes(extAttrs, Util4Deserializer.child(element,
				EXTENDED_ATTRIBUTES));

		return transition;
	}
	
	protected Expression createExpression(Element expressionElement){
		if (expressionElement!=null){
			ExpressionImpl exp = new ExpressionImpl();
			exp.setLanguage(expressionElement.getAttribute(LANGUAGE));
			exp.setName(expressionElement.getAttribute(NAME));
			exp.setDisplayName(expressionElement.getAttribute(DISPLAY_NAME));
			String dataTypeStr = expressionElement.getAttribute(DATA_TYPE);
			if (dataTypeStr!=null && !dataTypeStr.trim().equals("")){
				QName qname = QName.valueOf(dataTypeStr);
				exp.setDataType(qname);
			}

			Element bodyElement = Util4Deserializer.child(expressionElement, BODY);
			
			String body = this.loadCDATA(bodyElement);
			exp.setBody(body);

			Element namespacePrefixUriMapElem = Util4Deserializer.child(expressionElement, this.NAMESPACE_PREFIX_URI_MAP);
	        
			if (namespacePrefixUriMapElem!=null){
				List<Element> children = Util4Deserializer.children(namespacePrefixUriMapElem, ENTRY);
				if (children!=null && children.size()>0){
					for (Element elem : children){
						String prefix = elem.getAttribute(NAME);
						String uri = elem.getAttribute(VALUE);
						exp.getNamespaceMap().put(prefix, uri);
					}
				}
			}

			return exp;
		}else{
			ExpressionImpl exp = new ExpressionImpl();
			exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
			exp.setDataType(new QName(NameSpaces.JAVA.getUri(), String.class
					.getName(), NameSpaces.JAVA.getPrefix()));
			exp.setName("WorkItemSubject");
			exp.setDisplayName("工作项主题");// 需国际化
			exp.setBody("");
			return exp;
		}
	}

	/**
	 * @param parent
	 * @param dataFields
	 * @param propertiesElement
	 * @throws DeserializerException
	 */
	protected void loadProperties(WorkflowElement parent,
			List<Property> dataFields, Element propertiesElement)
			throws DeserializerException {

		if (propertiesElement == null) {
			return;
		}

		List<Element> datafieldsElement = Util4Deserializer.children(propertiesElement,
				PROPERTY);
		dataFields.clear();
		Iterator<Element> iter = datafieldsElement.iterator();
		while (iter.hasNext()) {
			Element dataFieldElement = iter.next();
			dataFields.add(createProperty(parent, dataFieldElement));
		}
	}

	/**
	 * @param parent
	 * @param element
	 * @return
	 * @throws DeserializerException
	 */
	protected Property createProperty(WorkflowElement parent, Element element)
			throws DeserializerException {
		if (element == null) {
			return null;
		}
		String dataTypeStr = element.getAttribute(DATA_TYPE);
		QName dataType = QName.valueOf(dataTypeStr);
		if (dataType == null) {
			dataType = new QName(NameSpaces.JAVA.getUri(),"java.lang.String");
		}

		Property dataField = new PropertyImpl(parent, element.getAttribute(NAME));

		dataField.setDataType(dataType);

		dataField.setDisplayName(element.getAttribute(DISPLAY_NAME));
		dataField.setInitialValueAsString(element.getAttribute(INIT_VALUE));
		dataField.setDescription(loadCDATA(Util4Deserializer.child(element,DESCRIPTION)));

		return dataField;
	}

	/**
	 * @param extendedAttributes
	 * @param element
	 * @throws DeserializerException
	 */
	protected void loadExtendedAttributes(
			Map<String, String> extendedAttributes, Element element)
			throws DeserializerException {

		if (element == null) {
			return;
		}
		extendedAttributes.clear();
		List<Element> extendAttributeElementsList = Util4Deserializer.children(
				element, EXTENDED_ATTRIBUTE);
		Iterator<Element> iter = extendAttributeElementsList.iterator();
		while (iter.hasNext()) {
			Element extAttrElement = iter.next();
			String name = extAttrElement.getAttribute(NAME);
			String value = extAttrElement.getAttribute(VALUE);

			extendedAttributes.put(name, value);

		}
	}
	
	protected String loadCDATA(Element cdataElement){
		if (cdataElement==null){
			return "";
		}else{
			String data = cdataElement.getTextContent();
			if (data==null)return data;
			else{
				if (data.trim().equals(Label.CONTENT_FROM_WORKFLOW_ELEMENT)){
					return Label.CONTENT_FROM_WORKFLOW_ELEMENT;
				}else if (data.trim().equals(Label.CONTENT_IGNORE)){
					return Label.CONTENT_IGNORE;
				}else if (useJDKTransformerFactory){
					if (data.startsWith(" ")){
						return data.substring(1);//去掉一个空格
					}
				}
				return data;
			}
			
		}
	}
}
