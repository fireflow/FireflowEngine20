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
package org.fireflow.pdl.fpdl.behavior.router.impl;

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pdl.fpdl.behavior.router.AbsJoinEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Synchronizer;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

/**
 * 动态判断是否有活动的前驱来确定是否汇聚
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class OrJoinEvaluator extends AbsJoinEvaluator implements JoinEvaluator {
	public static final String JOIN_DESCRIPTION = "汇聚逻辑：当任意输入Transition到达时，判断是否有活动的前驱结点，如果有则等待汇聚，否则执行后续分支。";
	public String getJoinDescription(){
		return JOIN_DESCRIPTION;
	}


}
