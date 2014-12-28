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

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;

import com.mxgraph.swing.handler.mxGraphTransferHandler;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FireflowGraphTransferHandler extends mxGraphTransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6451767163873845931L;

	/* (non-Javadoc)
	 * @see com.mxgraph.swing.handler.mxGraphTransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
	 */
	@Override
	public boolean importData(JComponent c, Transferable t) {
		// TODO Auto-generated method stub
		return super.importData(c, t);
	}

	
}
