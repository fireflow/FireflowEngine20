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
package org.fireflow.simulation.client.impl;

import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowSessionRemoteImpl;
import org.fireflow.client.impl.WorkflowStatementRemoteImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SimulatorSessionImpl extends WorkflowSessionRemoteImpl {

	public WorkflowStatement createWorkflowStatement(String processType) {
		SimulatorStatementImpl statement = new SimulatorStatementImpl(this,processType);
		statement.setWorkflowServer(workflowServer);
		return statement;
	}


	public WorkflowStatement createWorkflowStatement() {
		SimulatorStatementImpl statement = new SimulatorStatementImpl(this,"FPDL20");//TODO 默认为FPDL20
		statement.setWorkflowServer(workflowServer);
		return statement;
	}
}
