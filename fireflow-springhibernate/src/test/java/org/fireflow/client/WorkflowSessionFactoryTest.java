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
package org.fireflow.client;

import java.net.MalformedURLException;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowSessionFactoryTest extends FireWorkflowJunitEnviroment {
	private static final String address = "http://127.0.0.1:3069/FireWorkflowServices";
	@Test
	public void testCreateRemoteSession()throws MalformedURLException{
		WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(address, "zhangsan", "");
		
		Assert.assertNotNull(session);
		Assert.assertNotNull(session.getCurrentUser());
		Assert.assertNotNull(session.getSessionId());
		
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.FireWorkflowJunitEnviroment#createWorkflowProcess()
	 */
	@Override
	public WorkflowProcess createWorkflowProcess() {
		// TODO Auto-generated method stub
		return null;
	}

}
