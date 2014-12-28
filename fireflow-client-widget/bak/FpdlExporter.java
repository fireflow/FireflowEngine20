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
package org.fireflow.clientwidget;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.designer.swing.FireflowModel2MxGraphModel;
import org.fireflow.designer.swing.mxgraphext.CustomGraph;
import org.fireflow.designer.swing.mxgraphext.util.CellRendererEx;
import org.fireflow.designer.swing.proxy.Wrapper;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FpdlExporter {
	private static Log log = LogFactory.getLog(FpdlExporter.class);
	
	private static Document doc = null;
	static {
		mxCodecRegistry.addPackage(Wrapper.class.getPackage().getName());
	
		//设置Graph		
		
		doc = mxUtils.loadDocument(FireflowModel2MxGraphModel.class.getResource(
				"/org/fireflow/designer/swing/resources/fireflow2-styles.xml")
				.toString());
		
	}
	
	public static String exportToSVGDiagram(WorkflowProcess process,String subProcessName,
			final String svgId,
			final int viewPortWidth,final int viewPortHeight,
			String clientWidgetServletPath,String contextPath,String encoding){
		mxGraph graph = new CustomGraph();
		mxCodec codec = new mxCodec();
		codec.decode(doc.getDocumentElement(), graph.getStylesheet());
		
		//打开model		
		FireflowModel2MxGraphModel transformer = new FireflowModel2MxGraphModel();
		mxCell cell = transformer.transformToCell(process, subProcessName);
		graph.getModel().setRoot(cell);
	
		//输出svg
		org.w3c.dom.Document w3cDoc = CellRendererEx.createSvgDocument(graph, null, 1.0d, null, null,
				svgId,viewPortWidth,viewPortHeight,
				clientWidgetServletPath,contextPath);
		String svgXml = getXml(w3cDoc,encoding);
		
		return svgXml;
	}
	
	public static  String exportToVMLDiagram(WorkflowProcess process,String subProcessName,
			String vmlId,
			String fireflowResourceServlet,String contextPath,String encoding){
		mxGraph graph = new CustomGraph();
		mxCodec codec = new mxCodec();
		codec.decode(doc.getDocumentElement(), graph.getStylesheet());
		
		//打开model
		FireflowModel2MxGraphModel transformer = new FireflowModel2MxGraphModel();
		mxCell cell = transformer.transformToCell(process, subProcessName);
		graph.getModel().setRoot(cell);
	
		//输出svg
		org.w3c.dom.Document w3cDoc = CellRendererEx.createVmlDocument(graph, null, 1.0d, null, null,vmlId,fireflowResourceServlet,contextPath);

		String vmlXml = getXml(w3cDoc,encoding);

		return vmlXml;
	}
	
	public static String getXml(Node node,String encoding)
	{
		try
		{
			Transformer tf = TransformerFactory.newInstance().newTransformer();

			tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tf.setOutputProperty(OutputKeys.ENCODING, encoding);
			tf.setOutputProperty(OutputKeys.INDENT,"yes");
			tf.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");

			StreamResult dest = new StreamResult(new StringWriter());
			tf.transform(new DOMSource(node), dest);

			return dest.getWriter().toString();
		}
		catch (Exception e)
		{
			// ignore
		}

		return "";
	}
}
