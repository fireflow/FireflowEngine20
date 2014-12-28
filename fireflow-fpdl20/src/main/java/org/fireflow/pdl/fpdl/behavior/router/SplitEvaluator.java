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
package org.fireflow.pdl.fpdl.behavior.router;

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pvm.kernel.Token;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface SplitEvaluator {
	/**
	 * 分支逻辑描述信息
	 * @return
	 */
	public String getSplitDescription();
	
	public List<String> determineNextTransitions(
			WorkflowSession session, Token token4Node, Node node); 
}
