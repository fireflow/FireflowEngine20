/**
 * Copyright 2007-2010 非也
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
package org.fireflow.pdl.fpdl.process.impl;

import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.features.router.impl.OrJoinOrSplitRouterFeature;

/**
 * @author 非也
 * @version 2.0
 */
public class RouterImpl extends SynchronizerImpl implements Router {

	/**
	 * 
	 */
	private static final long serialVersionUID = 855408631075569281L;

	/**
	 * 
	 */
	public RouterImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param workflowProcess
	 * @param name
	 */
	public RouterImpl(SubProcess subflow, String name) {
		super(subflow, name);
	}	
}
