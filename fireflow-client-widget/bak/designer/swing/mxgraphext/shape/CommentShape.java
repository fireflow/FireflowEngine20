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
package org.fireflow.designer.swing.mxgraphext.shape;

import java.awt.Rectangle;
import java.util.Map;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxBasicShape;
import com.mxgraph.view.mxCellState;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CommentShape extends mxBasicShape{
	public static final int COMMENT_FIGURE_INSETS = 5;
	public static final String SHAPE_FIREFLOW_COMMENT = "fireflow_comment";
	

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle rect = state.getRectangle();

		// Paints the background
		if (configureGraphics(canvas, state, true))
		{
			canvas.fillShape(rect, hasShadow(canvas, state));
		}

		// Paints the foreground
		if (configureGraphics(canvas, state, false))
		{
			canvas.getGraphics().drawLine(rect.x, rect.y, rect.x, rect.y+rect.height);
			canvas.getGraphics().drawLine(rect.x, rect.y, rect.x+(rect.width/3), rect.y);
			canvas.getGraphics().drawLine(rect.x, rect.y+rect.height,rect.x+(rect.width/3),rect.y+rect.height );
		}
	}


}
