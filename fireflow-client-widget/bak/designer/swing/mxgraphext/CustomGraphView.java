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
package org.fireflow.designer.swing.mxgraphext;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CustomGraphView extends mxGraphView {

	/**
	 * @param graph
	 */
	public CustomGraphView(mxGraph graph) {
		super(graph);
		// TODO Auto-generated constructor stub
	}
	
	

	/* (non-Javadoc)
	 * @see com.mxgraph.view.mxGraphView#updateLabel(com.mxgraph.view.mxCellState)
	 */
	/*
	@Override
	public void updateLabel(mxCellState state) {
		// TODO Auto-generated method stub
		//super.updateLabel(state);
		
		//保持原始状态，不做任何换行处理
		String label = graph.getLabel(state.getCell());
		state.setLabel(label);
	}
	*/


	/* (non-Javadoc)
	 * @see com.mxgraph.view.mxGraphView#getPoint(com.mxgraph.view.mxCellState, com.mxgraph.model.mxGeometry)
	 */
	@Override
	public mxPoint getPoint(mxCellState state, mxGeometry geometry) {
		int pointCount = state.getAbsolutePointCount();
		if (pointCount<2){
			return super.getPoint(state, geometry);
		}
		mxPoint referencePoint = null;
		if (pointCount%2==1){//奇数个节点
			int index = (pointCount-1)/2;
			referencePoint = state.getAbsolutePoint(index);
		}else{
			int index1 = pointCount/2-1;
			mxPoint p1 = state.getAbsolutePoint(index1);
			int index2 = pointCount/2;
			mxPoint p2 = state.getAbsolutePoint(index2);
			referencePoint = new mxPoint((p1.getX()+p2.getX())/2,(p1.getY()+p2.getY())/2);
		}

		if (geometry==null || geometry.getOffset()==null){
			return referencePoint;
		}
		mxPoint offset = geometry.getOffset();
		
		return new mxPoint(referencePoint.getX()+offset.getX()*scale,referencePoint.getY()+offset.getY()*scale);
	}

	
}
