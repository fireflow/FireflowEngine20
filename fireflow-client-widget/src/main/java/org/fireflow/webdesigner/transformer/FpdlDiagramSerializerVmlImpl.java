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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.model.ModelElement;
import org.fireflow.model.io.SerializerException;
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
import org.fireflow.pdl.fpdl.diagram.figure.part.BoundsImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.FulfilStyle;
import org.fireflow.pdl.fpdl.diagram.figure.part.Label;
import org.fireflow.pdl.fpdl.diagram.figure.part.LabelImpl;
import org.fireflow.pdl.fpdl.diagram.figure.part.Point;
import org.fireflow.pdl.fpdl.io.FPDLNames;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class FpdlDiagramSerializerVmlImpl extends AbstractFpdlDiagramSerializer {
	private Log log = LogFactory.getLog(FpdlDiagramSerializerVmlImpl.class);

	public Document serializeDiagramToDoc(WorkflowProcess workflowProcess,
			String subProcessName) throws SerializerException {
		_init(workflowProcess, subProcessName);

		// 构造根节点
		Element root = document.createElement("v:group");
		root.setAttribute("id", diagram.getId());
		if (diagram.getWorkflowElementRef() != null) {
			root.setAttribute(FPDLNames.REF, diagram.getWorkflowElementRef()
					.getId());
		}

		document.appendChild(root);

		// 流程节点
		List<ProcessNodeShape> processNodeShapeList = diagram
				.getProcessNodeShapes();
		if (processNodeShapeList != null && processNodeShapeList.size() > 0) {
			for (ProcessNodeShape nodeShape : processNodeShapeList) {
				Element cell = this.transformProcessNodeShape2Vml(nodeShape,
						true);
				if (cell != null) {
					root.appendChild(cell);
				}
			}
		}

		// 注释
		List<CommentShape> commentShapeList = diagram.getComments();
		if (commentShapeList != null && commentShapeList.size() > 0) {
			for (CommentShape commentShape : commentShapeList) {
				Element cell = transformCommentShape2Vml(commentShape, true);
				if (cell != null) {
					root.appendChild(cell);
				}

			}
		}

		// Group
		List<GroupShape> groupShapeList = diagram.getGroups();
		if (groupShapeList != null && groupShapeList.size() > 0) {
			for (GroupShape groupShape : groupShapeList) {
				Element cell = transformGroupShape2Vml(groupShape, true);
				if (cell != null) {
					root.appendChild(cell);
				}
			}
		}
		//
		// //Pool
		List<PoolShape> poolShapeList = diagram.getPools();
		if (poolShapeList != null && poolShapeList.size() > 0) {
			for (PoolShape poolShape : poolShapeList) {
				Element cell = this.transformPool2Vml(poolShape, true);
				if (cell != null) {
					root.appendChild(cell);
				}
			}
		}

		//
		// transition
		List<TransitionShape> transitionShapeList = diagram.getTransitions();
		if (transitionShapeList != null && transitionShapeList.size() > 0) {
			for (TransitionShape transitionShape : transitionShapeList) {
				transformConnectorShape2Vml(root, transitionShape);
			}
		}

		// association
		List<AssociationShape> associationShapeList = diagram.getAssociations();
		if (associationShapeList != null && associationShapeList.size() > 0) {
			for (AssociationShape associationShape : associationShapeList) {
				Element associationCell = transformConnectorShape2Vml(root,
						associationShape);
			}
		}
		//
		// //messageflow
		List<MessageFlowShape> messageFlowShapeList = diagram.getMessageFlows();
		if (messageFlowShapeList != null && messageFlowShapeList.size() > 0) {
			for (MessageFlowShape messageFlowShape : messageFlowShapeList) {
				Element messageFlowCell = this
						.transformConnectorShape2Vml(root,messageFlowShape);
			}
		}

		// 根据图形长宽，设置root属性
		int width = this.rightDownX - this.leftTopX;
		int height = this.rightDownY - this.leftTopY;
		StringBuffer styleBuf = new StringBuffer();
		styleBuf.append("position:absolute;").append("width:").append(width)
				.append("px;").append("height:").append(height).append("px;");
		root.setAttribute("style", styleBuf.toString());

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(width).append(",").append(height);
		root.setAttribute("coordsize", coordSizeBuf.toString());

		int origX = 0;
		int origY = 0;
		if (this.leftTopX < 0) {
			origX = this.leftTopX;
		}
		if (this.leftTopY < 0) {
			origY = this.leftTopY;
		}
		root.setAttribute("coordorigin", origX + "," + origY);

		// 构造一个和viewport一样尺寸的rect，便于拖拉
		Element rect = document.createElement("v:rect");
		StringBuffer rectStyle = (new StringBuffer())
				.append("position:absolute;").append("left:").append(0)
				.append("px;").append("top:").append(0).append("px;")
				.append("width:").append(width).append("px;").append("height:")
				.append(height).append("px;").append("z-index:-99");
		rect.setAttribute("style", rectStyle.toString());
		rect.setAttribute("strokecolor", "white");
		// rect.setAttribute("stroked","false");
		// rect.setAttribute("filled", "false");

		root.appendChild(rect);

		return document;
	}

	private Element transformPool2Vml(PoolShape nodeShape,
			boolean isTopLevelElem) {
		// 1、构造viewport
		Bounds viewportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle) nodeShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(viewportBounds.getX()).append("px;").append("top:")
				.append(viewportBounds.getY()).append("px;").append("width:")
				.append(viewportBounds.getWidth()).append("px;")
				.append("height:").append(viewportBounds.getHeight())
				.append("px;");

		nodeGroup.setAttribute("style", groupStyle.toString());

		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.POOL);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(viewportBounds.getWidth()).append(",")
				.append(viewportBounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		// 2、构造pool边框
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth() - thick * 2;
		int newHeight = rectBounds.getHeight() - thick * 2;
		Element rect = document.createElement("v:rect");
		StringBuffer rectStyle = (new StringBuffer())
				.append("position:absolute;").append("left:").append(thick)
				.append("px;").append("top:").append(thick).append("px;")
				.append("width:").append(newWidth).append("px;")
				.append("height:").append(newHeight).append("px;")
				.append("z-index:-1;");
		rect.setAttribute("style", rectStyle.toString());
		nodeGroup.appendChild(rect);

		Element line = document.createElement("v:line");
		if (Diagram.VERTICAL.equals(diagram.getDirection())) {
			String from = thick + "," + POOL_LANE_TITLE_HEIGHT;
			line.setAttribute("from", from);

			String to = (thick + newWidth) + "," + POOL_LANE_TITLE_HEIGHT;
			line.setAttribute("to", to);
		} else {
			String from = POOL_LANE_TITLE_HEIGHT + "," + thick;
			line.setAttribute("from", from);

			String to = POOL_LANE_TITLE_HEIGHT + "," + (thick + newHeight);
			line.setAttribute("to", to);
		}
		nodeGroup.appendChild(line);

		// 2.1画笔
		if (rectBounds != null) {
			Element stroke = document.createElement("v:stroke");

			stroke.setAttribute("weight",
					Integer.toString(thick < 0 ? 1 : thick) + "px");
			String color = rectBounds.getColor();
			stroke.setAttribute("color",
					(color == null || color.trim().equals("")) ? "#000000"
							: color);
			String lineType = rectBounds.getLineType();
			if (lineType == null || lineType.trim().equals("")) {
				stroke.setAttribute("dashstyle", "solid");
			} else if (Bounds.LINETYPE_DOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dot");
			} else if (Bounds.LINETYPE_DASHED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dash");
			} else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dashdot");
			}

			Element stroke2 = (Element) stroke.cloneNode(true);

			rect.appendChild(stroke);
			line.appendChild(stroke2);
		}

		// 2.2填充
		if (figure.getFulfilStyle() != null) {
			String color1 = figure.getFulfilStyle().getColor1();
			String color2 = figure.getFulfilStyle().getColor2();
			String gradient = figure.getFulfilStyle().getGradientStyle();

			Element fillStyle = document.createElement("v:fill");

			if (gradient == null || gradient.trim().equals("")
					|| FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)) {
				fillStyle.setAttribute("type", "solid");
				fillStyle.setAttribute("color", color2);
			} else {
				fillStyle.setAttribute("type", "gradient");
				fillStyle.setAttribute("method", "linear");

				fillStyle.setAttribute("color", color1);
				fillStyle.setAttribute("color2", color2);

				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)) {
					fillStyle.setAttribute("angle", "180");// 上至下
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "225");
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "135");
				} else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "270");// 从左到右
				} else {
					fillStyle.setAttribute("angle", "270");// 从左到右
				}
			}

			rect.appendChild(fillStyle);
		}

		// 2.3 label的名称
		String title = figure.getTitle();
		if (title != null && !title.trim().equals("")) {
			// 构造textbox
			int paddingLeft = (thick);
			Label titleLabel = figure.getTitleLabel().copy();
			int fontSize = titleLabel.getFontSize();
			;

			int textRectHeight = (int) (fontSize + 2);// 设置一定的余量

			int check = POOL_LANE_TITLE_HEIGHT - textRectHeight - thick;
			int paddingTop = thick;

			if (check > 0) {
				paddingTop = thick + (int) (check / 2);

			} else {
				textRectHeight = POOL_LANE_TITLE_HEIGHT - thick;
			}
			// 纵向泳道
			if (Diagram.VERTICAL.equals(diagram.getDirection())) {
				int width = rectBounds.getWidth() - paddingLeft * 2;// 一般而言，width不可能小于2倍thick
				if (width < 0) {
					width = 0;
				}
				Element textRect = this.buildVmlTextBox(nodeShape,
						FPDLNames.POOL, paddingLeft, paddingTop, width,
						textRectHeight, titleLabel, true, "center");
				nodeGroup.appendChild(textRect);
			} else {// 横向泳道
				int height = rectBounds.getHeight() - paddingLeft * 2;
				Element textRect = this.buildVmlTextBox(nodeShape,
						FPDLNames.POOL, paddingTop, paddingLeft,
						textRectHeight, height, titleLabel, true, "center",
						"middle", false);
				nodeGroup.appendChild(textRect);
			}
		}

		if (isTopLevelElem) {
			_refreshDiagramSize(viewportBounds);
		}

		// 3、构造子节点
		List<DiagramElement> children = nodeShape.getChildren();
		if (children != null && children.size() > 0) {
			// 构造一个新的坐标系统
			Element childrenGroup = document.createElement("v:group");
			StringBuffer tmpStyle = new StringBuffer();
			if (Diagram.VERTICAL.equals(diagram.getDirection())) {
				tmpStyle.append("position:absolute;")
						.append("left:")
						.append(thick)
						.append("px;")
						// 去掉边线的宽度
						.append("top:")
						.append(POOL_LANE_TITLE_HEIGHT)
						.append("px;")
						//
						.append("width:")
						.append(newWidth)
						.append("px;")
						.append("height:")
						.append(rectBounds.getHeight() - GROUP_TITLE_HEIGHT
								- thick).append("px");

			} else {
				tmpStyle.append("position:absolute;")
						.append("left:")
						.append(POOL_LANE_TITLE_HEIGHT)
						.append("px;")
						.append("top:")
						.append(thick)
						.append("px;")
						.append("width:")
						.append(rectBounds.getWidth() - POOL_LANE_TITLE_HEIGHT
								- thick).append("px;").append("height:")
						.append(newHeight).append("px");
			}

			childrenGroup.setAttribute("style", tmpStyle.toString());

			if (Diagram.VERTICAL.equals(diagram.getDirection())) {
				StringBuffer coordSizeBufTmp = new StringBuffer();
				coordSizeBufTmp
						.append(newWidth)
						.append(",")
						.append(rectBounds.getHeight() - GROUP_TITLE_HEIGHT
								- thick);
				childrenGroup.setAttribute("coordsize",
						coordSizeBufTmp.toString());
			} else {
				StringBuffer coordSizeBufTmp = new StringBuffer();
				coordSizeBufTmp
						.append(viewportBounds.getWidth()
								- POOL_LANE_TITLE_HEIGHT - thick).append(",")
						.append(newHeight);
				childrenGroup.setAttribute("coordsize",
						coordSizeBufTmp.toString());
			}
			int offset = 0;
			int laneCount = children.size();
			for (int index = 0; index < laneCount; index++) {
				DiagramElement diagramElm = children.get(index);
				if (diagramElm instanceof LaneShape) {
					LaneShape laneShape = (LaneShape) diagramElm;
					Element elm = this.transformLane2Vml(laneShape, offset,
							index, laneCount);
					if (elm != null) {
						childrenGroup.appendChild(elm);
					}

					Rectangle rectTmp = (Rectangle) laneShape.getFigure();
					Bounds tmpBounds = rectTmp.getBounds();

					if (Diagram.VERTICAL.equals(diagram.getDirection())) {
						offset = offset + tmpBounds.getWidth();
					} else {
						offset = offset + tmpBounds.getHeight();
					}
				}
			}

			nodeGroup.appendChild(childrenGroup);
		}

		return nodeGroup;
	}

	private Element transformLane2Vml(LaneShape nodeShape, int offset,
			int laneIndex, int laneCount) {
		Bounds viewportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle) nodeShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("width:")
				.append(viewportBounds.getWidth()).append("px;")
				.append("height:").append(viewportBounds.getHeight())
				.append("px;");

		if (Diagram.VERTICAL.equals(diagram.getDirection())) {
			groupStyle.append("left:").append(offset).append("px;")
					.append("top:").append(0).append("px;");
		} else {
			groupStyle.append("left:").append(0).append("px;").append("top:")
					.append(offset).append("px;");
		}

		nodeGroup.setAttribute("style", groupStyle.toString());

		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.LANE);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(viewportBounds.getWidth()).append(",")
				.append(viewportBounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth() - thick * 2;
		int newHeight = rectBounds.getHeight() - thick * 2;
		String title = figure.getTitle();

		// 画笔
		Element stroke = null;
		if (rectBounds != null) {
			stroke = document.createElement("v:stroke");

			stroke.setAttribute("weight",
					Integer.toString(thick < 0 ? 1 : thick) + "px");
			String color = rectBounds.getColor();
			stroke.setAttribute("color",
					(color == null || color.trim().equals("")) ? "#000000"
							: color);
			String lineType = rectBounds.getLineType();
			if (lineType == null || lineType.trim().equals("")) {
				stroke.setAttribute("dashstyle", "solid");
			} else if (Bounds.LINETYPE_DOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dot");
			} else if (Bounds.LINETYPE_DASHED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dash");
			} else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dashdot");
			}

		}
		// lane边框
		if (Diagram.VERTICAL.equals(diagram.getDirection())) {

			// 横线2
			if (title != null && !title.trim().equals("")) {
				Element line = document.createElement("v:line");
				String from = thick + "," + POOL_LANE_TITLE_HEIGHT;
				line.setAttribute("from", from);

				String to = (thick + newWidth) + "," + POOL_LANE_TITLE_HEIGHT;
				line.setAttribute("to", to);
				if (stroke != null) {
					Node tmpStroke = stroke.cloneNode(true);
					line.appendChild(tmpStroke);
				}
				nodeGroup.appendChild(line);
			}

			// 竖线
			if (laneIndex > 0) {
				Element line = document.createElement("v:line");
				String from = thick + "," + thick;
				line.setAttribute("from", from);

				String to = (thick + newHeight) + "," + (thick);
				line.setAttribute("to", to);
				if (stroke != null) {
					Node tmpStroke = stroke.cloneNode(true);
					line.appendChild(tmpStroke);
				}
				nodeGroup.appendChild(line);
			}

		} else {
			// 竖线1

			// 竖线2
			if (title != null && !title.trim().equals("")) {
				Element line = document.createElement("v:line");
				String from = POOL_LANE_TITLE_HEIGHT + "," + thick;
				line.setAttribute("from", from);

				String to = POOL_LANE_TITLE_HEIGHT + "," + (thick + newHeight);
				line.setAttribute("to", to);
				if (stroke != null) {
					Node tmpStroke = stroke.cloneNode(true);
					line.appendChild(tmpStroke);
				}
				nodeGroup.appendChild(line);
			}

			// 横线
			if (laneIndex > 0) {
				Element line = document.createElement("v:line");
				String from = thick + "," + thick;
				line.setAttribute("from", from);

				String to = (thick + newWidth) + "," + (thick);
				line.setAttribute("to", to);
				if (stroke != null) {
					Node tmpStroke = stroke.cloneNode(true);
					line.appendChild(tmpStroke);
				}
				nodeGroup.appendChild(line);
			}

		}

		// 填充
		Element rect = document.createElement("v:rect");
		StringBuffer rectStyle = (new StringBuffer())
				.append("position:absolute;").append("width:").append(newWidth)
				.append("px;").append("height:").append(newHeight)
				.append("px;").append("z-index:-1;");
		rectStyle.append("left:").append(thick).append("px;").append("top:")
				.append(thick).append("px;");

		rect.setAttribute("style", rectStyle.toString());
		rect.setAttribute("stroked", "false");

		nodeGroup.appendChild(rect);
		/* rect.appendChild(stroke); */

		if (figure.getFulfilStyle() != null) {
			String color1 = figure.getFulfilStyle().getColor1();
			String color2 = figure.getFulfilStyle().getColor2();
			String gradient = figure.getFulfilStyle().getGradientStyle();

			Element fillStyle = document.createElement("v:fill");

			if (gradient == null || gradient.trim().equals("")
					|| FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)) {
				fillStyle.setAttribute("type", "solid");
				fillStyle.setAttribute("color", color2);
			} else {
				fillStyle.setAttribute("type", "gradient");
				fillStyle.setAttribute("method", "linear");

				fillStyle.setAttribute("color", color1);
				fillStyle.setAttribute("color2", color2);

				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)) {
					fillStyle.setAttribute("angle", "180");// 上至下
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "225");
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "135");
				} else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "270");// 从左到右
				} else {
					fillStyle.setAttribute("angle", "270");// 从左到右
				}
			}

			rect.appendChild(fillStyle);
		}

		// label的名称
		if (title != null && !title.trim().equals("")) {
			int paddingLeft = (thick);
			Label titleLabel = figure.getTitleLabel().copy();
			int fontSize = titleLabel.getFontSize();

			int textRectHeight = (int) (fontSize + 2);// 设置一定的余量

			int check = POOL_LANE_TITLE_HEIGHT - textRectHeight - thick;
			int paddingTop = thick;

			if (check > 0) {
				paddingTop = thick + (int) (check / 2);

			} else {
				textRectHeight = POOL_LANE_TITLE_HEIGHT - thick;
			}
			// 纵向泳道
			if (Diagram.VERTICAL.equals(diagram.getDirection())) {
				int width = rectBounds.getWidth() - paddingLeft * 2;// 一般而言，width不可能小于2倍thick
				if (width < 0) {
					width = 0;
				}
				Element textRect = this.buildVmlTextBox(nodeShape,
						FPDLNames.LANE, paddingLeft, paddingTop, width,
						textRectHeight, titleLabel, true, "center");
				nodeGroup.appendChild(textRect);
			} else {// 横向泳道
				int height = rectBounds.getHeight() - paddingLeft * 2;
				Element textRect = this.buildVmlTextBox(nodeShape,
						FPDLNames.LANE, paddingTop, paddingLeft,
						textRectHeight, height, titleLabel, true, "center",
						"middle", false);
				nodeGroup.appendChild(textRect);
			}
		}

		// 构造子节点
		List<DiagramElement> children = nodeShape.getChildren();
		if (children != null && children.size() > 0) {

			// 构造一个新的坐标系统
			Element childrenGroup = document.createElement("v:group");
			StringBuffer tmpStyle = new StringBuffer();
			if (Diagram.VERTICAL.equals(diagram.getDirection())) {
				tmpStyle.append("position:absolute;")
						.append("left:")
						.append(thick)
						.append("px;")
						.append("top:")
						.append(POOL_LANE_TITLE_HEIGHT)
						.append("px;")
						.append("width:")
						.append(rectBounds.getWidth() - thick * 2)
						.append("px;")
						.append("height:")
						.append(rectBounds.getHeight() - POOL_LANE_TITLE_HEIGHT
								- thick).append("px");

			} else {
				tmpStyle.append("position:absolute;")
						.append("left:")
						.append(POOL_LANE_TITLE_HEIGHT)
						.append("px;")
						.append("top:")
						.append(thick)
						.append("px;")
						.append("width:")
						.append(rectBounds.getWidth() - POOL_LANE_TITLE_HEIGHT
								- thick).append("px;").append("height:")
						.append(rectBounds.getHeight() - thick * 2)
						.append("px");
			}

			childrenGroup.setAttribute("style", tmpStyle.toString());
			if (Diagram.VERTICAL.equals(diagram.getDirection())) {
				StringBuffer coordSizeBufTmp = new StringBuffer();
				coordSizeBufTmp
						.append(rectBounds.getWidth() - thick * 2)
						.append(",")
						.append(rectBounds.getHeight() - POOL_LANE_TITLE_HEIGHT
								- thick);
				childrenGroup.setAttribute("coordsize",
						coordSizeBufTmp.toString());
			} else {
				StringBuffer coordSizeBufTmp = new StringBuffer();
				coordSizeBufTmp
						.append(rectBounds.getWidth() - POOL_LANE_TITLE_HEIGHT
								- thick).append(",")
						.append(rectBounds.getHeight() - thick * 2);
				childrenGroup.setAttribute("coordsize",
						coordSizeBufTmp.toString());
			}

			for (DiagramElement diagramElm : children) {
				if (diagramElm instanceof ProcessNodeShape) {
					Element elm = this.transformProcessNodeShape2Vml(
							(ProcessNodeShape) diagramElm, false);
					if (elm != null) {
						childrenGroup.appendChild(elm);
					}
				} else if (diagramElm instanceof CommentShape) {
					Element elm = this.transformCommentShape2Vml(
							(CommentShape) diagramElm, false);
					if (elm != null) {
						childrenGroup.appendChild(elm);
					}
				} else if (diagram instanceof GroupShape) {
					Element elm = this.transformGroupShape2Vml(
							(GroupShape) diagram, false);
					if (elm != null) {
						childrenGroup.appendChild(elm);
					}
				}
			}

			nodeGroup.appendChild(childrenGroup);

		}

		return nodeGroup;
	}

	private Element transformGroupShape2Vml(GroupShape nodeShape,
			boolean isTopLevelElem) {

		// 1、构造viewport
		Bounds viewportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle) nodeShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(viewportBounds.getX()).append("px;").append("top:")
				.append(viewportBounds.getY()).append("px;").append("width:")
				.append(viewportBounds.getWidth()).append("px;")
				.append("height:").append(viewportBounds.getHeight())
				.append("px");

		nodeGroup.setAttribute("style", groupStyle.toString());

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(viewportBounds.getWidth()).append(",")
				.append(viewportBounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.GROUP);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		// 2、Group边框
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		Element rect = document.createElement("v:roundrect");
		StringBuffer rectStyle = (new StringBuffer())
				.append("position:absolute;").append("left:").append(thick)
				.append("px;").append("top:").append(thick).append("px;")
				.append("width:").append(rectBounds.getWidth() - thick * 2)
				.append("px;").append("height:")
				.append(rectBounds.getHeight() - thick * 2).append("px;")
				.append("z-index:-1;");
		rect.setAttribute("style", rectStyle.toString());
		rect.setAttribute("arcsize", "9.0%");
		nodeGroup.appendChild(rect);

		// 2.1 画笔
		if (rectBounds != null) {
			Element stroke = document.createElement("v:stroke");

			stroke.setAttribute("weight",
					Integer.toString(thick < 0 ? 1 : thick) + "px");
			String color = rectBounds.getColor();
			stroke.setAttribute("color",
					(color == null || color.trim().equals("")) ? "#000000"
							: color);
			String lineType = rectBounds.getLineType();
			if (lineType == null || lineType.trim().equals("")) {
				stroke.setAttribute("dashstyle", "solid");
			} else if (Bounds.LINETYPE_DOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dot");
			} else if (Bounds.LINETYPE_DASHED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dash");
			} else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dashdot");
			}

			rect.appendChild(stroke);
		}

		// 2.2 填充
		if (figure.getFulfilStyle() != null) {
			String color1 = figure.getFulfilStyle().getColor1();
			String color2 = figure.getFulfilStyle().getColor2();
			String gradient = figure.getFulfilStyle().getGradientStyle();

			Element fillStyle = document.createElement("v:fill");

			if (gradient == null || gradient.trim().equals("")
					|| FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)) {
				fillStyle.setAttribute("type", "solid");
				fillStyle.setAttribute("color", color2);
			} else {
				fillStyle.setAttribute("type", "gradient");
				fillStyle.setAttribute("method", "linear");

				fillStyle.setAttribute("color", color1);
				fillStyle.setAttribute("color2", color2);

				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)) {
					fillStyle.setAttribute("angle", "180");// 上至下
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "225");
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "135");
				} else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "270");// 从左到右
				} else {
					fillStyle.setAttribute("angle", "270");// 从左到右
				}
			}

			rect.appendChild(fillStyle);
		}

		// 2.3 label的名称
		String title = figure.getTitle();
		if (title != null && !title.trim().equals("")) {
			// 构造textbox
			int paddingLeft = thick;
			Label titleLabel = figure.getTitleLabel();
			int fontSize = titleLabel.getFontSize();

			int width = rectBounds.getWidth() - paddingLeft * 2;// 一般而言，width不可能小于2倍thick
			if (width < 0) {
				width = 0;
			}

			int textRectHeight = (int) (fontSize + 2);// 设置一定的余量

			int check = GROUP_TITLE_HEIGHT - textRectHeight - thick;
			int paddingTop = thick;
			int height = textRectHeight;

			if (check > 0) {
				paddingTop = thick + (int) (check / 2);

			} else {
				height = GROUP_TITLE_HEIGHT - thick;
			}

			Element textRect = this.buildVmlTextBox(nodeShape, FPDLNames.GROUP,
					paddingLeft, paddingTop, width, height, titleLabel, true,
					"center");

			nodeGroup.appendChild(textRect);
		}

		if (isTopLevelElem) {
			_refreshDiagramSize(viewportBounds);
		}

		// 3、构造子节点
		List<DiagramElement> children = nodeShape.getChildren();
		if (children != null && children.size() > 0) {
			// 构造一个新的view port，该viewport应该和group边框匹配
			Element childrenGroup = document.createElement("v:group");
			StringBuffer tmpStyle = new StringBuffer();
			tmpStyle.append("position:absolute;")
					.append("left:")
					.append(thick)
					.append("px;")
					.append("top:")
					.append(GROUP_TITLE_HEIGHT)
					.append("px;")
					.append("width:")
					.append(rectBounds.getWidth() - thick * 2)
					.append("px;")
					.append("height:")
					.append(rectBounds.getHeight() - GROUP_TITLE_HEIGHT - thick)
					.append("px");

			childrenGroup.setAttribute("style", tmpStyle.toString());

			StringBuffer coordSizeBufTmp = new StringBuffer();
			coordSizeBufTmp
					.append(rectBounds.getWidth() - thick * 2)
					.append(",")
					.append(rectBounds.getHeight() - GROUP_TITLE_HEIGHT - thick);
			childrenGroup.setAttribute("coordsize", coordSizeBufTmp.toString());

			for (DiagramElement diagramElm : children) {
				if (diagramElm instanceof ProcessNodeShape) {
					Element elm = this.transformProcessNodeShape2Vml(
							(ProcessNodeShape) diagramElm, false);
					if (elm != null) {
						childrenGroup.appendChild(elm);
					}
				} else if (diagramElm instanceof CommentShape) {
					Element elm = this.transformCommentShape2Vml(
							(CommentShape) diagramElm, false);
					if (elm != null) {
						childrenGroup.appendChild(elm);
					}
				}
			}

			nodeGroup.appendChild(childrenGroup);
		}

		return nodeGroup;
	}

	private Element transformCommentShape2Vml(CommentShape commentShape,
			boolean isTopLevelElm) {
		// 1、构造view port
		Bounds viewportBounds = _getViewPortBounds(commentShape);
		Rectangle figure = (Rectangle) commentShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(viewportBounds.getX()).append("px;").append("top:")
				.append(viewportBounds.getY()).append("px;").append("width:")
				.append(viewportBounds.getWidth()).append("px;")
				.append("height:").append(viewportBounds.getHeight())
				.append("px");

		nodeGroup.setAttribute("style", groupStyle.toString());

		nodeGroup.setAttribute(FPDLNames.ID, commentShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.COMMENT);
		ModelElement wfElmRef = commentShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(viewportBounds.getWidth()).append(",")
				.append(viewportBounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		// 2、Comment边框
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth() - thick * 2;
		int newHeight = rectBounds.getHeight() - thick * 2;

		Element polyline = document.createElement("v:polyline");
		StringBuffer pointList = new StringBuffer();
		pointList.append((int) (rectBounds.getWidth() / 3)).append(",")
				.append(0).append(" ").append(0).append(",").append(0)
				.append(" ").append(0).append(",")
				.append(rectBounds.getHeight()).append(" ")
				.append((int) (rectBounds.getWidth() / 3)).append(",")
				.append(rectBounds.getHeight());

		polyline.setAttribute("points", pointList.toString());
		polyline.setAttribute("filled", "f");
		StringBuffer polyLineStyle = (new StringBuffer()).append(
				"position:absolute;").append("z-index:-1;");
		polyline.setAttribute("style", polyLineStyle.toString());
		nodeGroup.appendChild(polyline);
		/*
		 * Element borderRect = document.createElement("v:rect");
		 * borderRect.setAttribute("filled", "f"); StringBuffer polyLineStyle =
		 * (new StringBuffer()).append("position:absolute;")
		 * .append("top:").append(thick).append("px;")
		 * .append("left:").append(thick).append("px;")
		 * .append("width:").append(newWidth).append("px;")
		 * .append("height:").append(newHeight).append("px;")
		 * .append("z-index:-1;"); borderRect.setAttribute("style",
		 * polyLineStyle.toString()); nodeGroup.appendChild(borderRect);
		 */
		// 2.1边框画笔
		if (figure.getBounds() != null) {
			Element stroke = document.createElement("v:stroke");

			stroke.setAttribute("weight",
					Integer.toString(thick < 0 ? 1 : thick) + "px");
			String color = rectBounds.getColor();
			stroke.setAttribute("color",
					(color == null || color.trim().equals("")) ? "#000000"
							: color);
			String lineType = rectBounds.getLineType();
			if (lineType == null || lineType.trim().equals("")) {
				stroke.setAttribute("dashstyle", "solid");
			} else if (Bounds.LINETYPE_DOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dot");
			} else if (Bounds.LINETYPE_DASHED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dash");
			} else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dashdot");
			}

			polyline.appendChild(stroke);
		}

		// 2.2 内容区域填充

		Element contentBackground = document.createElement("v:rect");
		StringBuffer rectStyle = (new StringBuffer())
				.append("position:absolute;").append("left:").append(thick + 1)
				.append("px;").append("top:").append(thick + 1).append("px;")
				.append("width:").append(newWidth - 2).append("px;")
				.append("height:").append(newHeight - 2).append("px;");
		contentBackground.setAttribute("style", rectStyle.toString());

		contentBackground.setAttribute("strokecolor", "white");
		contentBackground.setAttribute("StrokeWeight", "1px");

		nodeGroup.appendChild(contentBackground);

		if (figure.getFulfilStyle() != null) {
			String color1 = figure.getFulfilStyle().getColor1();
			String color2 = figure.getFulfilStyle().getColor2();
			String gradient = figure.getFulfilStyle().getGradientStyle();

			Element fillStyle = document.createElement("v:fill");

			if (gradient == null || gradient.trim().equals("")
					|| FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)) {
				fillStyle.setAttribute("type", "solid");
				if (color2 == null || color2.trim().equals("")) {
					color2 = "white";
				}
				fillStyle.setAttribute("color", color2);
			} else {
				fillStyle.setAttribute("type", "gradient");
				fillStyle.setAttribute("method", "linear");

				fillStyle.setAttribute("color", color1);
				fillStyle.setAttribute("color2", color2);

				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)) {
					fillStyle.setAttribute("angle", "180");// 上至下
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "225");
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "135");
				} else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "270");// 从左到右
				} else {
					fillStyle.setAttribute("angle", "270");// 从左到右
				}
			}

			contentBackground.appendChild(fillStyle);
		}

		// comment的内容
		String contentTxt = figure.getContent();
		if (contentTxt != null && !contentTxt.trim().equals("")) {
			// 构造textbox
			int padding = 4;
			int spacing = 0;
			int textBoxPadding = (padding + thick);
			Label contentLabel = figure.getContentLabel().copy();

			Element textRect = this.drawCommentText(commentShape,
					FPDLNames.COMMENT, textBoxPadding, textBoxPadding, newWidth
							- textBoxPadding * 2, newHeight - textBoxPadding
							* 2, contentLabel);
			if (textRect != null) {
				nodeGroup.appendChild(textRect);
			}

		}

		if (isTopLevelElm) {
			_refreshDiagramSize(viewportBounds);
		}

		return nodeGroup;
	}

	private Element transformConnectorShape2Vml(Element root,
			ConnectorShape connectorShape) {
		NodeShape fromNodeShape = connectorShape.getFromNode();
		NodeShape toNodeShape = connectorShape.getToNode();
		ModelElement wfElm = connectorShape.getWorkflowElementRef();
		String wfElmId = wfElm == null ? "null" : wfElm.getId();
		if (fromNodeShape == null || toNodeShape == null) {
			log.warn("TransitionShape[Id=" + connectorShape.getId()
					+ ",WfElemId=" + wfElmId
					+ "]的 from_node或者to_node为空，无法绘制该连接线");
			return null;
		}

		Bounds absoluteFromNodeBounds = this._getAbsoluteBounds(fromNodeShape);
		Bounds absoluteToNodeBounds = this._getAbsoluteBounds(toNodeShape);

		if (absoluteFromNodeBounds == null || absoluteToNodeBounds == null) {
			log.warn("TransitionShape[Id=" + connectorShape.getId()
					+ ",WfElemId=" + wfElmId
					+ "]的 from_node或者to_node的bounds为空，无法绘制该连接线");
			return null;
		}

		Line fireLine = (Line) connectorShape.getFigure();
		List<Point> linePoints = fireLine.getPoints();

		Point fromRefPoint = null;// from_anchor的参考点
		Point toRefPoint = null;// to_anchor的参考点
		if (linePoints == null || linePoints.size() == 0) {
			fromRefPoint = new Point();
			fromRefPoint.setX(absoluteToNodeBounds.getX()
					+ absoluteToNodeBounds.getWidth() / 2);
			fromRefPoint.setY(absoluteToNodeBounds.getY()
					+ absoluteToNodeBounds.getHeight() / 2);

		} else {
			fromRefPoint = linePoints.get(0);
		}
		if (linePoints == null || linePoints.size() == 0) {
			toRefPoint = new Point();
			toRefPoint.setX(absoluteFromNodeBounds.getX()
					+ absoluteFromNodeBounds.getWidth() / 2);
			toRefPoint.setY(absoluteFromNodeBounds.getY()
					+ absoluteFromNodeBounds.getHeight() / 2);

		} else {
			toRefPoint = linePoints.get(linePoints.size() - 1);
		}

		// 计算from_anchor
		Point fromAnchor = null;
		fromAnchor = _calculateAnchor(fromNodeShape, absoluteFromNodeBounds,
				fromRefPoint);

		// 计算to_anchor
		Point toAnchor = null;
		toAnchor = _calculateAnchor(toNodeShape, absoluteToNodeBounds,
				toRefPoint);

		// polyline
		Element polylineElm = document.createElement("v:polyline");
		polylineElm.setAttribute("style", "position:absolute");
		polylineElm.setAttribute("points",
				_makePointsSeq(fromAnchor.copy(), toAnchor.copy(), linePoints));
		polylineElm.setAttribute("filled", "f");

		String elementType = null;
		polylineElm.setAttribute(FPDLNames.ID, connectorShape.getId());
		if (connectorShape instanceof TransitionShape) {
			elementType = FPDLNames.TRANSITION;
		} else if (connectorShape instanceof MessageFlowShape) {
			elementType = FPDLNames.MESSAGEFLOW;
		} else if (connectorShape instanceof AssociationShape) {
			elementType = FPDLNames.ASSOCIATION;
		}
		polylineElm.setAttribute(TYPE, elementType);

		ModelElement wfElmRef = connectorShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			polylineElm.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		// 画笔
		Element strokeElm = document.createElement("v:stroke");
		Bounds tmpBounds = fireLine.getBounds() ;
		String color = "#000000";
		color = tmpBounds.getColor();
		color = (color == null || color.trim()
				.equals("")) ? "#000000" : color;
		
		if (connectorShape instanceof TransitionShape) {
			strokeElm.setAttribute("EndArrow", "Block");
		} else if (connectorShape instanceof MessageFlowShape) {
			strokeElm.setAttribute("EndArrow", "Open");
			
			//在前头画一个圆圈
			Element startArrowElm = document.createElement("v:oval");
			int tmpX = fromAnchor.getX()-4;
			int tmpY = fromAnchor.getY()-4;
			int tmpW = 8;
			int tmpH = 8;
			StringBuffer circleStyle = new StringBuffer();
			circleStyle.append("left:").append(tmpX).append("px;")
						.append("top:").append(tmpY).append("px;")
						.append("width:").append(tmpW).append("px;")
						.append("height:").append(tmpH).append("px;");
			startArrowElm.setAttribute("style", circleStyle.toString());
			startArrowElm.setAttribute("strokecolor", color);
			startArrowElm.setAttribute("filled", "false");
			root.appendChild(startArrowElm);
		}

		if (tmpBounds!= null) {
			int thick = fireLine.getBounds().getThick();
			strokeElm.setAttribute("weight",
					Integer.toString(thick < 0 ? 1 : thick) + "px");
			
			strokeElm.setAttribute("color", color);
			String lineType = fireLine.getBounds().getLineType();
			if (lineType == null || lineType.trim().equals("")) {
				strokeElm.setAttribute("dashstyle", "solid");
			} else if (Bounds.LINETYPE_DOTTED.equals(lineType)) {
				strokeElm.setAttribute("dashstyle", "dot");
			} else if (Bounds.LINETYPE_DASHED.equals(lineType)) {
				strokeElm.setAttribute("dashstyle", "dash");
			} else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)) {
				strokeElm.setAttribute("dashstyle", "dashdot");
			}
		}

		polylineElm.appendChild(strokeElm);

		// 构造标题
		Label lb = fireLine.getTitleLabel();
		String displayName = null;
		if (wfElm != null) {
			displayName = wfElm.getDisplayName();
		} else {
			displayName = fireLine.getTitle();
		}
		if (displayName != null && !displayName.trim().equals("")) {
			Point labelRelativePos = fireLine.getLabelPosition();
			// labelAbsPos是label的中心点
			Point labelAbsPos = _getLabelAbsolutePosition(fromAnchor.copy(),
					toAnchor.copy(), linePoints, labelRelativePos);
			Label titleLabel = null;
			if (lb == null) {
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(DEFAULT_FONT_SIZE);
			} else {
				titleLabel = lb.copy();
			}
			titleLabel.setText(displayName);
			Dimension dimension = _calculateFontSize(titleLabel.getText(),
					titleLabel.getFontSize());

			labelAbsPos.setX(labelAbsPos.getX() - dimension.width / 2);
			labelAbsPos.setY(labelAbsPos.getY() - dimension.height / 2);

			Element textRect = this.buildVmlTextBox(connectorShape,
					elementType, labelAbsPos.getX(), labelAbsPos.getY(),
					dimension.width, dimension.height, titleLabel, true,
					"center");

			root.appendChild(textRect);

			// 重设画布大小
			Bounds boundsTmp = new BoundsImpl();
			boundsTmp.setX(labelAbsPos.getX());
			boundsTmp.setY(labelAbsPos.getY());
			boundsTmp.setWidth(dimension.width);
			boundsTmp.setHeight(dimension.height);

			this._refreshDiagramSize(boundsTmp);

		}

		root.appendChild(polylineElm);

		// 重设画布大小
		List<Point> tmpList = new ArrayList<Point>();
		tmpList.addAll(linePoints);
		tmpList.add(fromAnchor);
		tmpList.add(toAnchor);
		_refreshDiagramSize(tmpList);

		return polylineElm;
	}

	private Element transformProcessNodeShape2Vml(ProcessNodeShape nodeShape,
			boolean isTopLevelElem) {
		Element cell = null;
		if (nodeShape instanceof StartNodeShape) {
			cell = this.transformStartNodeShape2Vml((StartNodeShape) nodeShape,
					isTopLevelElem);
		}

		else if (nodeShape instanceof EndNodeShape) {
			cell = this.transformEndNodeShape2Vml((EndNodeShape) nodeShape,
					isTopLevelElem);
		} else if (nodeShape instanceof RouterShape) {
			cell = this.transformRouterShape2Vml((RouterShape) nodeShape,
					isTopLevelElem);
		} else if (nodeShape instanceof ActivityShape) {
			cell = this.transformActivityShape2Vml((ActivityShape) nodeShape,
					true);
		}
		// ModelElement wfElm = nodeShape.getWorkflowElementRef();

		return cell;
	}

	/**
	 * 
	 * @param nodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformActivityShape2Vml(ActivityShape nodeShape,
			boolean isTopLevelElem) {
		Activity node = (Activity) nodeShape.getWorkflowElementRef();
		String imgUri = _getActivityImgUri(node);

		// 1、构造viewport
		Bounds newportBounds = _getViewPortBounds(nodeShape);
		Rectangle figure = (Rectangle) nodeShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(newportBounds.getX()).append("px;").append("top:")
				.append(newportBounds.getY()).append("px;").append("width:")
				.append(newportBounds.getWidth()).append("px;")
				.append("height:").append(newportBounds.getHeight())
				.append("px");

		nodeGroup.setAttribute("style", groupStyle.toString());

		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.ACTIVITY);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(newportBounds.getWidth()).append(",")
				.append(newportBounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		// 2、Activity边框
		Bounds rectBounds = figure.getBounds().copy();
		int thick = rectBounds.getThick();
		int newWidth = rectBounds.getWidth() - thick;
		int newHeight = rectBounds.getHeight() - thick;
		Element rect = document.createElement("v:roundrect");
		StringBuffer rectStyle = (new StringBuffer())
				.append("position:absolute;").append("left:").append(thick / 2)
				.append("px;").append("top:").append(thick / 2).append("px;")
				.append("width:").append(newWidth).append("px;")
				.append("height:").append(newHeight).append("px;")
				.append("z-index:-1;");
		rect.setAttribute("style", rectStyle.toString());
		rect.setAttribute("arcsize", "7.0%");
		nodeGroup.appendChild(rect);

		// 2.1 画笔
		if (figure.getBounds() != null) {
			Element stroke = document.createElement("v:stroke");

			stroke.setAttribute("weight",
					Integer.toString(thick < 0 ? 1 : thick) + "px");
			String color = rectBounds.getColor();
			stroke.setAttribute("color",
					(color == null || color.trim().equals("")) ? "#000000"
							: color);
			String lineType = rectBounds.getLineType();
			if (lineType == null || lineType.trim().equals("")) {
				stroke.setAttribute("dashstyle", "solid");
			} else if (Bounds.LINETYPE_DOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dot");
			} else if (Bounds.LINETYPE_DASHED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dash");
			} else if (Bounds.LINETYPE_DASHDOTTED.equals(lineType)) {
				stroke.setAttribute("dashstyle", "dashdot");
			}

			rect.appendChild(stroke);
		}

		// 2.2填充
		if (figure.getFulfilStyle() != null) {
			String color1 = figure.getFulfilStyle().getColor1();
			String color2 = figure.getFulfilStyle().getColor2();
			String gradient = figure.getFulfilStyle().getGradientStyle();

			Element fillStyle = document.createElement("v:fill");

			if (gradient == null || gradient.trim().equals("")
					|| FulfilStyle.GRADIENT_STYLE_NONE.equals(gradient)) {
				fillStyle.setAttribute("type", "solid");
				fillStyle.setAttribute("color", color2);
			} else {
				fillStyle.setAttribute("type", "gradient");
				fillStyle.setAttribute("method", "linear");

				fillStyle.setAttribute("color", color1);
				fillStyle.setAttribute("color2", color2);

				if (FulfilStyle.GRADIENT_STYLE_TOP2DOWN.equals(gradient)) {
					fillStyle.setAttribute("angle", "180");// 上至下
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERLEFT2LOWERRIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "225");
				} else if (FulfilStyle.GRADIENT_STYLE_UPPERRIGHT2LOWERLEFT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "135");
				} else if (FulfilStyle.GRADIENT_STYLE_LEFT2RIGHT
						.equals(gradient)) {
					fillStyle.setAttribute("angle", "270");// 从左到右
				} else {
					fillStyle.setAttribute("angle", "270");// 从左到右
				}
			}

			rect.appendChild(fillStyle);
		}

		// 2.3 activity的名称
		String displayName = node.getDisplayName();
		if (displayName == null || displayName.trim().equals("")) {
			displayName = node.getName();
		}
		int padding = 6;
		int spacing = 5;

		// 构造textbox
		int textBoxPaddingY = (SVC_LOGO_RADIUS * 2 + padding + spacing);
		int textBoxPaddingX = padding ;
		Label contentLabel = null;
		int fontSize = DEFAULT_FONT_SIZE;
		if (figure.getContentLabel() != null) {
			contentLabel = figure.getContentLabel().copy();
			fontSize = contentLabel.getFontSize();
		} else {
			contentLabel = new LabelImpl();
			contentLabel.setFontSize(fontSize);
		}
		contentLabel.setText(displayName);

		Element textRect = this.drawActivityText(nodeShape, FPDLNames.ACTIVITY,
				textBoxPaddingX, textBoxPaddingY, rectBounds.getWidth()
						- textBoxPaddingX * 2, rectBounds.getHeight()
						- textBoxPaddingY * 2, contentLabel);
		if (textRect != null) {
			nodeGroup.appendChild(textRect);
		}

		// 图形logo
		if (imgUri != null) {
			Element nodeImg = document.createElement("v:image");
			StringBuffer imgStyle = new StringBuffer();
			imgStyle.append("position:absolute;").append("left:")
					.append(padding + thick).append("px;").append("top:")
					.append(padding + thick).append("px;").append("width:")
					.append(SVC_LOGO_RADIUS * 2).append("px;")
					.append("height:").append(SVC_LOGO_RADIUS * 2).append("px");

			nodeImg.setAttribute("style", imgStyle.toString());
			nodeImg.setAttribute("src", this.resourcePathPrefix + imgUri);
			nodeGroup.appendChild(nodeImg);
		}
		
		//构造attatched event node

		List<DiagramElement> children = nodeShape.getChildren();
		if (children!=null && children.size()>0){
			//1、首先构造坐标系
			int width = rectBounds.getWidth();
			int height = IMG_RADIUS*2+DEFAULT_FONT_SIZE+IconTextGap;
			Element attachedEvtSvg = document.createElement("v:group");
			StringBuffer tmpStyle = new StringBuffer();
			tmpStyle.append("position:absolute;")
					.append("left:")
					.append(0)
					.append("px;")
					.append("top:")
					.append(rectBounds.getHeight()-IMG_RADIUS)
					.append("px;")
					.append("width:")
					.append(width)
					.append("px;")
					.append("height:")
					.append(height)
					.append("px");
			
			attachedEvtSvg.setAttribute("style", tmpStyle.toString());
			attachedEvtSvg.setAttribute("coordsize",width+","+height);
			
			nodeGroup.appendChild(attachedEvtSvg);
			
			for (DiagramElement child : children){
				Element elm = this.transformAttachedEvent2Vml((StartNodeShape)child);
				attachedEvtSvg.appendChild(elm);
			}
		}

		if (isTopLevelElem) {
			_refreshDiagramSize(newportBounds);
		}

		return nodeGroup;
	}

	/**
	 * 
	 * @param nodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformRouterShape2Vml(RouterShape nodeShape,
			boolean isTopLevelElem) {
		String imgUri = _getSynchronizerNodeImgUri(nodeShape);

		Router node = (Router) nodeShape.getWorkflowElementRef();

		// 计算start node group的大小以及位置
		Bounds bounds = _getViewPortBounds(nodeShape);
		Figure figure = nodeShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(bounds.getX()).append("px;").append("top:")
				.append(bounds.getY()).append("px;").append("width:")
				.append(bounds.getWidth()).append("px;").append("height:")
				.append(bounds.getHeight()).append("px");

		nodeGroup.setAttribute("style", groupStyle.toString());
		// id 等属性
		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.ROUTER);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(bounds.getWidth()).append(",")
				.append(bounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		Element nodeImg = document.createElement("v:image");
		StringBuffer imgStyle = new StringBuffer();
		imgStyle.append("position:absolute;").append("left:")
				.append((bounds.getWidth() / 2 - IMG_RADIUS)).append("px;")
				.append("top:").append(0).append("px;").append("width:")
				.append(IMG_RADIUS * 2).append("px;").append("height:")
				.append(IMG_RADIUS * 2).append("px");

		nodeImg.setAttribute("style", imgStyle.toString());
		nodeImg.setAttribute("src", this.resourcePathPrefix + imgUri);
		nodeGroup.appendChild(nodeImg);

		// 增加一个临时边框
		// Element rect = document.createElement("v:rect");
		// StringBuffer rectStyle = (new
		// StringBuffer()).append("position:absolute;")
		// .append("left:").append(0).append("px;")
		// .append("top:").append(0).append("px;")
		// .append("width:").append(bounds.getWidth()).append("px;")
		// .append("height:").append(bounds.getHeight()).append("px");
		// rect.setAttribute("style", rectStyle.toString());
		// startNodeGroup.appendChild(rect);

		String displayName = node.getDisplayName();
		if (displayName != null && !displayName.trim().equals("")) {
			Label titleLabel = null;
			int fontSize = DEFAULT_FONT_SIZE;
			if (figure.getTitleLabel() != null) {
				titleLabel = figure.getTitleLabel().copy();
				fontSize = titleLabel.getFontSize();
			} else {
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(fontSize);
			}
			titleLabel.setText(displayName);

			Element textRect = this.buildVmlTextBox(nodeShape,
					FPDLNames.ROUTER, 0, (IMG_RADIUS * 2 + IconTextGap),
					bounds.getWidth(), (int) (fontSize * PT_2_PX_RATE),
					titleLabel, true, "center");

			nodeGroup.appendChild(textRect);
		}

		if (isTopLevelElem) {
			_refreshDiagramSize(bounds);
		}

		return nodeGroup;
	}

	/**
	 * 
	 * @param nodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformEndNodeShape2Vml(EndNodeShape nodeShape,
			boolean isTopLevelElem) {
		String imgUri = _getSynchronizerNodeImgUri(nodeShape);

		EndNode node = (EndNode) nodeShape.getWorkflowElementRef();

		// 计算start node group的大小以及位置
		Bounds bounds = _getViewPortBounds(nodeShape);
		Figure figure = nodeShape.getFigure();

		Element nodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(bounds.getX()).append("px;").append("top:")
				.append(bounds.getY()).append("px;").append("width:")
				.append(bounds.getWidth()).append("px;").append("height:")
				.append(bounds.getHeight()).append("px");

		nodeGroup.setAttribute("style", groupStyle.toString());

		nodeGroup.setAttribute(FPDLNames.ID, nodeShape.getId());
		nodeGroup.setAttribute(TYPE, FPDLNames.END_NODE);
		ModelElement wfElmRef = nodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			nodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(bounds.getWidth()).append(",")
				.append(bounds.getHeight());
		nodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		// 增加一个临时边框
		// Element rect = document.createElement("v:rect");
		// StringBuffer rectStyle = (new
		// StringBuffer()).append("position:absolute;")
		// .append("left:").append(0).append("px;")
		// .append("top:").append(0).append("px;")
		// .append("width:").append(bounds.getWidth()).append("px;")
		// .append("height:").append(bounds.getHeight()).append("px");
		// rect.setAttribute("style", rectStyle.toString());
		// startNodeGroup.appendChild(rect);

		Element nodeImg = document.createElement("v:image");
		StringBuffer imgStyle = new StringBuffer();
		imgStyle.append("position:absolute;").append("left:")
				.append((bounds.getWidth() / 2 - IMG_RADIUS)).append("px;")
				.append("top:").append(0).append("px;").append("width:")
				.append(IMG_RADIUS * 2).append("px;").append("height:")
				.append(IMG_RADIUS * 2).append("px");

		nodeImg.setAttribute("style", imgStyle.toString());
		nodeImg.setAttribute("src", this.resourcePathPrefix + imgUri);
		nodeGroup.appendChild(nodeImg);

		String displayName = node.getDisplayName();
		if (displayName != null && !displayName.trim().equals("")) {
			Label titleLabel = null;
			int fontSize = DEFAULT_FONT_SIZE;
			if (figure.getTitleLabel() != null) {
				titleLabel = figure.getTitleLabel().copy();
				fontSize = titleLabel.getFontSize();
			} else {
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(fontSize);
			}
			titleLabel.setText(displayName);

			Element textRect = this.buildVmlTextBox(nodeShape,
					FPDLNames.END_NODE, 0, (IMG_RADIUS * 2 + IconTextGap),
					bounds.getWidth(), (int) (fontSize * PT_2_PX_RATE),
					titleLabel, true, "center");

			nodeGroup.appendChild(textRect);
		}

		if (isTopLevelElem) {
			_refreshDiagramSize(bounds);
		}

		return nodeGroup;
	}
	
	private Element transformAttachedEvent2Vml(StartNodeShape startNodeShape){
		String imgUri = _getSynchronizerNodeImgUri(startNodeShape);

		StartNode startNode = (StartNode) startNodeShape
				.getWorkflowElementRef();

		// 计算start node group的大小以及位置		
		Figure figure = startNodeShape.getFigure();
		Bounds bounds = figure.getBounds();

		Element startNodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(bounds.getX()).append("px;").append("top:")
				.append(bounds.getY()).append("px;").append("width:")
				.append(bounds.getWidth()).append("px;").append("height:")
				.append(bounds.getHeight()).append("px");

		startNodeGroup.setAttribute("style", groupStyle.toString());

		// id 等属性
		startNodeGroup.setAttribute(FPDLNames.ID, startNodeShape.getId());
		startNodeGroup.setAttribute(TYPE, FPDLNames.START_NODE);
		ModelElement wfElmRef = startNodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			startNodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(bounds.getWidth()).append(",")
				.append(bounds.getHeight());
		startNodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		Element startNodeImg = document.createElement("v:image");
		StringBuffer imgStyle = new StringBuffer();
		imgStyle.append("position:absolute;").append("left:")
				.append((bounds.getWidth() / 2 - IMG_RADIUS)).append("px;")
				.append("top:").append(0).append("px;").append("width:")
				.append(IMG_RADIUS * 2).append("px;").append("height:")
				.append(IMG_RADIUS * 2).append("px");

		startNodeImg.setAttribute("style", imgStyle.toString());
		startNodeImg.setAttribute("src", this.resourcePathPrefix + imgUri);
		startNodeGroup.appendChild(startNodeImg);


		return startNodeGroup;
	}

	/**
	 * 
	 * @param startNodeShape
	 * @param isTopLevelElem
	 * @return
	 */
	private Element transformStartNodeShape2Vml(StartNodeShape startNodeShape,
			boolean isTopLevelElem) {
		String imgUri = _getSynchronizerNodeImgUri(startNodeShape);

		StartNode startNode = (StartNode) startNodeShape
				.getWorkflowElementRef();

		// 计算start node group的大小以及位置
		Bounds bounds = _getViewPortBounds(startNodeShape);
		Figure figure = startNodeShape.getFigure();

		Element startNodeGroup = document.createElement("v:group");
		StringBuffer groupStyle = new StringBuffer();
		groupStyle.append("position:absolute;").append("left:")
				.append(bounds.getX()).append("px;").append("top:")
				.append(bounds.getY()).append("px;").append("width:")
				.append(bounds.getWidth()).append("px;").append("height:")
				.append(bounds.getHeight()).append("px");

		startNodeGroup.setAttribute("style", groupStyle.toString());

		// id 等属性
		startNodeGroup.setAttribute(FPDLNames.ID, startNodeShape.getId());
		startNodeGroup.setAttribute(TYPE, FPDLNames.START_NODE);
		ModelElement wfElmRef = startNodeShape.getWorkflowElementRef();
		if (wfElmRef != null) {
			startNodeGroup.setAttribute(FPDLNames.REF, wfElmRef == null ? ""
					: wfElmRef.getId());
		}

		StringBuffer coordSizeBuf = new StringBuffer();
		coordSizeBuf.append(bounds.getWidth()).append(",")
				.append(bounds.getHeight());
		startNodeGroup.setAttribute("coordsize", coordSizeBuf.toString());

		Element startNodeImg = document.createElement("v:image");
		StringBuffer imgStyle = new StringBuffer();
		imgStyle.append("position:absolute;").append("left:")
				.append((bounds.getWidth() / 2 - IMG_RADIUS)).append("px;")
				.append("top:").append(0).append("px;").append("width:")
				.append(IMG_RADIUS * 2).append("px;").append("height:")
				.append(IMG_RADIUS * 2).append("px");

		startNodeImg.setAttribute("style", imgStyle.toString());
		startNodeImg.setAttribute("src", this.resourcePathPrefix + imgUri);
		startNodeGroup.appendChild(startNodeImg);

		String displayName = startNode.getDisplayName();
		if (displayName != null && !displayName.trim().equals("")) {
			Label titleLabel = null;
			int fontSize = DEFAULT_FONT_SIZE;
			if (figure.getTitleLabel() != null) {
				titleLabel = figure.getTitleLabel().copy();
				fontSize = titleLabel.getFontSize();
			} else {
				titleLabel = new LabelImpl();
				titleLabel.setFontSize(fontSize);
			}
			titleLabel.setText(displayName);

			Element textRect = this.buildVmlTextBox(startNodeShape,
					FPDLNames.START_NODE, 0, (IMG_RADIUS * 2 + IconTextGap),
					bounds.getWidth(), (int) (fontSize * PT_2_PX_RATE),
					titleLabel, true, "center");

			startNodeGroup.appendChild(textRect);
		}

		if (isTopLevelElem) {
			_refreshDiagramSize(bounds);
		}

		return startNodeGroup;
	}

	private Element drawActivityText(DiagramElement diagramElement,
			String elementType, int x, int y, int w, int h, Label label) {
		Element result = null;

		String text = label.getText();

		if (text == null || text.trim().length() == 0) {
			return null;
		}

		String fontColor = label.getFontColor();
		// String fontFamily = "FangSong_GB2312";//mxUtils.getString(style,
		// mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILIES);

		int fontSize = label.getFontSize();

		String fireFontStyle = label.getFontStyle();

		boolean isItalic = false;
		if (Label.FONT_STYLE_ITALIC.equals(fireFontStyle)
				|| Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)) {
			isItalic = true;
		}
		// int fontStyle = mxUtils.getInt(style, mxConstants.STYLE_FONTSTYLE);
		String weight = "normal";
		if (Label.FONT_STYLE_BOLD.equals(fireFontStyle)
				|| Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)) {
			weight = "bold";
		}

		String uline = "none";

		String fontStyle = label.getFontStyle();

		if (fontStyle == null || fontStyle.trim().equals("")) {
			fontStyle = Label.FONT_STYLE_NORMAL;
		}

		Element textBox = document.createElement("v:textbox");
		StringBuffer textStyle = new StringBuffer();
		textStyle.append("position:absolute;").append("left:").append(x)
				.append("px;").append("top:").append(y).append("px;")
				.append("width:").append(w).append("px;").append("height:")
				.append(h).append("px;")
				 .append("font-size:").append(fontSize).append("px;")
				.append("letter-spacing:").append("1px;")
		// .append("word-wrap:").append("break-word;")
		// .append("color:").append(fontColor).append(";")
		;

		// if (fontStyle.equals(Label.FONT_STYLE_BOLD)){
		// textStyle.append("font-weight:").append("bold;");
		// }else if (fontStyle.equals(Label.FONT_STYLE_ITALIC)){
		// textStyle.append("font-style:").append("italic;");
		// }else if (fontStyle.equals(Label.FONT_STYLE_ITALIC_BOLD)){
		// textStyle.append("font-weight:").append("bold;");
		// textStyle.append("font-style:").append("italic;");
		// }

		textBox.setAttribute("style", textStyle.toString());
		textBox.setAttribute("inset", "0,0,0,0");
		textBox.setAttribute("posLeft", Integer.toString(x));
		textBox.setAttribute("posTop", Integer.toString(y));
		textBox.setAttribute("pixelWidth", Integer.toString(w));
		textBox.setAttribute("pixelHeight", Integer.toString(h));
		textBox.setAttribute("fontSize", Integer.toString(fontSize));

		Element textDiv = document.createElement("div");
		textBox.appendChild(textDiv);

//		textDiv.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
		textDiv.setAttribute("onmouseover", "this.style.cursor='pointer';");
		textDiv.setAttribute("onmouseout", "this.style.cursor='default';");

		StringBuffer styleBufP = new StringBuffer();

		//
		styleBufP.append("width:100%;")//.append(w).append("px;")
				.append("height:100%;")//.append(h).append("px;")
				.append("text-align:center;word-wrap:break-word;");

		// 行高 等于 fontSize+3像素
		styleBufP.append("line-height:100%;");

		styleBufP.append("font-weight:").append(weight).append(";")
				//.append("font-size:").append(String.valueOf(fontSize))//font-size必须放在textBox的Style里面，使之能够放缩
				.append("px;").append("color:").append(fontColor).append(";")
				.append("text-decoration:none;");
		if (isItalic) {
			styleBufP.append("font-style:").append("italic").append(";");
		}
		textDiv.setAttribute("style", styleBufP.toString());

		// 构造click handler，diagramId
		StringBuffer clickHandler = new StringBuffer();
		clickHandler.append("on_element_click(").append("'")
				.append(diagramElement.getId()).append("',");

		ModelElement wfElement = diagramElement.getWorkflowElementRef();

		// 构造click handler wfElement Id
		if (wfElement != null) {
			clickHandler.append("'").append(wfElement.getId()).append("',");
		} else {
			clickHandler.append("'',");
		}
		// 构造click handler --element type
		clickHandler.append("'").append(elementType).append("',");

		// 构造click handler --process id
		clickHandler.append("'").append(workflowProcess.getId()).append("',");

		// 构造click handler --subprocess name
		clickHandler.append("'").append(this.subProcessName).append("');");

		textDiv.setAttribute("onclick", clickHandler.toString());

		textDiv.appendChild(document.createTextNode(text));

		result = textBox;

		return result;
	}

	private Element drawCommentText(DiagramElement diagramElement,
			String elementType, int x, int y, int w, int h, Label label) {
		Element result = null;

		String text = label.getText();

		if (text == null || text.trim().length() == 0) {
			return null;
		}

		String fontColor = label.getFontColor();
		// String fontFamily = "FangSong_GB2312";//mxUtils.getString(style,
		// mxConstants.STYLE_FONTFAMILY, mxConstants.DEFAULT_FONTFAMILIES);

		int fontSize = label.getFontSize();

		String fireFontStyle = label.getFontStyle();

		boolean isItalic = false;
		if (Label.FONT_STYLE_ITALIC.equals(fireFontStyle)
				|| Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)) {
			isItalic = true;
		}
		// int fontStyle = mxUtils.getInt(style, mxConstants.STYLE_FONTSTYLE);
		String weight = "normal";
		if (Label.FONT_STYLE_BOLD.equals(fireFontStyle)
				|| Label.FONT_STYLE_ITALIC_BOLD.equals(fireFontStyle)) {
			weight = "bold";
		}

		String uline = "none";

		String fontStyle = label.getFontStyle();

		if (fontStyle == null || fontStyle.trim().equals("")) {
			fontStyle = Label.FONT_STYLE_NORMAL;
		}

		Element textBox = document.createElement("v:textbox");
		StringBuffer textStyle = new StringBuffer();
		textStyle.append("position:absolute;").append("left:").append(x)
				.append("px;").append("top:").append(y).append("px;")
				.append("width:").append(w).append("px;").append("height:")
				.append(h).append("px;")
				 .append("font-size:").append(fontSize).append("px;")
				.append("letter-spacing:").append("1px;")
		// .append("word-wrap:").append("break-word;")
		// .append("color:").append(fontColor).append(";")
		;

		// if (fontStyle.equals(Label.FONT_STYLE_BOLD)){
		// textStyle.append("font-weight:").append("bold;");
		// }else if (fontStyle.equals(Label.FONT_STYLE_ITALIC)){
		// textStyle.append("font-style:").append("italic;");
		// }else if (fontStyle.equals(Label.FONT_STYLE_ITALIC_BOLD)){
		// textStyle.append("font-weight:").append("bold;");
		// textStyle.append("font-style:").append("italic;");
		// }

		textBox.setAttribute("style", textStyle.toString());
		textBox.setAttribute("inset", "0,0,0,0");
		textBox.setAttribute("posLeft", Integer.toString(x));
		textBox.setAttribute("posTop", Integer.toString(y));
		textBox.setAttribute("pixelWidth", Integer.toString(w));
		textBox.setAttribute("pixelHeight", Integer.toString(h));
		textBox.setAttribute("fontSize", Integer.toString(fontSize));

		Element textDiv = document.createElement("div");
//		textDiv.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
		textBox.appendChild(textDiv);

		StringBuffer styleBuf = new StringBuffer();
		//
		styleBuf.append("width:100%;")//.append(w).append("px;")
					.append("height:100%;")	//.append(h).append("px;")
					.append("word-wrap:break-word;");

		// 行高 等于 fontSize+3像素
		styleBuf.append("line-height:100%;");
		styleBuf.append("font-weight:").append(weight).append(";")
//				.append("font-size:").append(String.valueOf(fontSize))//必须放在textBox的style中，使之能够被放缩
				.append("px;").append("color:").append(fontColor).append(";");
		if (isItalic) {
			styleBuf.append("font-style:").append("italic").append(";");
		}

		textDiv.setAttribute("style", styleBuf.toString());

		StringTokenizer tokenizer = new StringTokenizer(text, "\n", true);
		while (tokenizer.hasMoreTokens()) {
			String tmpStr = tokenizer.nextToken();
			if (tmpStr.equals("\n")) {				
				Element brElm = document.createElement("br");
				textDiv.appendChild(brElm);
			} else {
				Element pElem = document.createElement("span");

				pElem.appendChild(document.createTextNode(tmpStr));
				textDiv.appendChild(pElem);
			}
		}

		result = textBox;

		return result;
	}

	private Element buildVmlTextBox(DiagramElement diagramElement,
			String elementType, int left, int top, int width, int height,
			Label label, boolean createWrapperRect, String textAlign) {
		return buildVmlTextBox(diagramElement, elementType, left, top, width,
				height, label, createWrapperRect, textAlign, "middle", true);
	}

	private Element buildVmlTextBox(DiagramElement diagramElement,
			String elementType, int left, int top, int width, int height,
			Label label, boolean createWrapperRect, String textAlign,
			String valign, boolean ishorizonal) {

		int fontSize = label.getFontSize();
		String fontColor = label.getFontColor();
		String fontStyle = label.getFontStyle();

		if (fontStyle == null || fontStyle.trim().equals("")) {
			fontStyle = Label.FONT_STYLE_NORMAL;
		}

		Element textBox = document.createElement("v:textbox");
		StringBuffer textStyle = new StringBuffer();
		textStyle.append("position:absolute;").append("left:").append(left)
				.append("px;").append("top:").append(top).append("px;")
				.append("width:").append(width).append("px;").append("height:")
				.append(height).append("px;").append("font-size:")
				.append(fontSize).append("px;").append("letter-spacing:")
				.append("1px;").append("word-wrap:").append("break-word;")
				.append("color:").append(fontColor).append(";");
		// .append("border:1 solid black;");//测试
		// .append("text-align:").append(textAlign).append(";");//横向对齐

		if (fontStyle.equals(Label.FONT_STYLE_BOLD)) {
			textStyle.append("font-weight:").append("bold;");
		} else if (fontStyle.equals(Label.FONT_STYLE_ITALIC)) {
			textStyle.append("font-style:").append("italic;");
		} else if (fontStyle.equals(Label.FONT_STYLE_ITALIC_BOLD)) {
			textStyle.append("font-weight:").append("bold;");
			textStyle.append("font-style:").append("italic;");
		}

		textBox.setAttribute("style", textStyle.toString());
		textBox.setAttribute("inset", "0,0,0,0");
		textBox.setAttribute("posLeft", Integer.toString(left));
		textBox.setAttribute("posTop", Integer.toString(top));
		textBox.setAttribute("pixelWidth", Integer.toString(width));
		textBox.setAttribute("pixelHeight", Integer.toString(height));
		textBox.setAttribute("fontSize", Integer.toString(fontSize));

		Element textDiv = document.createElement("div");
		if (ishorizonal) {

			textDiv.setAttribute("style", "width:100%;height:100%;text-align:"
					+ textAlign + ";");
		} else {
			textDiv.setAttribute("style",
					"width:100%;height:100%;layout-flow:vertical-ideographic;text-align:"
							+ textAlign + ";");
		}
		textBox.appendChild(textDiv);

		Element textA = document.createElement("a");
		textA.setAttribute("href", "#");

		// 构造click handler，diagramId
		StringBuffer clickHandler = new StringBuffer();
		clickHandler.append("on_element_click(").append("'")
				.append(diagramElement.getId()).append("',");

		ModelElement wfElement = diagramElement.getWorkflowElementRef();

		// 构造click handler wfElement Id
		if (wfElement != null) {
			clickHandler.append("'").append(wfElement.getId()).append("',");
		} else {
			clickHandler.append("'',");
		}
		// 构造click handler --element type
		clickHandler.append("'").append(elementType).append("',");

		// 构造click handler --process id
		clickHandler.append("'").append(workflowProcess.getId()).append("',");

		// 构造click handler --subprocess name
		clickHandler.append("'").append(this.subProcessName).append("');");

		textA.setAttribute("onclick", clickHandler.toString());

		// 设置anchor的样式
		StringBuffer anchorStyle = new StringBuffer();
		anchorStyle.append("color:").append(fontColor).append(";")
				.append("text-decoration:none;");

		textA.setAttribute("style", anchorStyle.toString());

		Text textNode = document.createTextNode(label.getText());
		textA.appendChild(textNode);

		textDiv.appendChild(textA);

		if (createWrapperRect) {
			Element textRect = document.createElement("v:rect");
			StringBuffer textRectStyle = new StringBuffer();
			textRectStyle.append("position:absolute;").append("width:")
					.append(width).append("px;").append("height:")
					.append(height).append("px;").append("left:").append(left)
					.append("px;").append("top:").append(top).append("px;");
			textRect.setAttribute("style", textRectStyle.toString());
			textRect.setAttribute("filled", "f");
			textRect.setAttribute("stroked", "f");

			textRect.appendChild(textBox);
			return textRect;
		} else {
			return textBox;
		}
	}

}
