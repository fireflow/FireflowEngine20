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
package org.fireflow.pdl.fpdl.diagram.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.pdl.fpdl.diagram.ProcessNodeShape;
import org.fireflow.pdl.fpdl.diagram.TransitionShape;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public abstract class AbsProcessNodeShapeImpl extends AbsNodeShapeImpl implements
		ProcessNodeShape {
	List<TransitionShape> leavingTransitionShapes = new ArrayList<TransitionShape>();
	List<TransitionShape> enteringTransitionShapes = new ArrayList<TransitionShape>();
	
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.ProcessNodeShape#getLeavingTransitionShapes()
	 */
	public List<TransitionShape> getLeavingTransitionShapes() {
		return leavingTransitionShapes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.diagram.ProcessNodeShape#getEnteringTransitionShapes()
	 */
	public List<TransitionShape> getEnteringTransitionShapes() {
		return enteringTransitionShapes;
	}

}
