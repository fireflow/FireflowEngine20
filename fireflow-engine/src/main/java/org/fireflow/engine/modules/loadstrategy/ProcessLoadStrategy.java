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
package org.fireflow.engine.modules.loadstrategy;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.InvalidModelException;

/**
 * 流程加载策略。一般情况下，启动新流程时都是运行当前发布的最新版本的流程；
 * 但是某些特定情况下，例如在一个大的行政机构（企业集团）中，同一个流程在不同的部门都不一样。
 * 通过这个加载策略，可以方便地进行定制。<br>
 * 从WorkflowSession可以得到当前的用户，然后根据用户及其部门信息，定位到特定的流程。
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ProcessLoadStrategy extends EngineModule, RuntimeContextAware {
	
	
	public ProcessRepository findTheProcessForRunning(
			String workflowProcessId, String processType,User currentUser,WorkflowSession session)
			throws InvalidModelException;
	
	public ProcessKey findTheProcessKeyForRunning(String workflowProcessId, String processType,User currentUser,WorkflowSession session);
}
