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
package org.fireflow.pdl.fpdl.behavior;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.pdl.fpdl.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.SplitEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.OrJoinEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.OrSplitEvaluator;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.Synchronizer;
import org.fireflow.pdl.fpdl.process.features.router.RouterFeature;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class RouterBehavior extends AbsSynchronizerBehavior implements WorkflowBehavior {
	public int canBeFired(WorkflowSession session, Token token,List<Token> siblings,
			Synchronizer synchronizer){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		
		String className = OrJoinEvaluator.class.getName();//缺省是DynamicJoin
		
		RouterFeature feature = (RouterFeature)synchronizer.getFeature();
		if (feature!=null && !StringUtils.isEmpty(feature.getJoinEvaluatorClass())){
			className = feature.getJoinEvaluatorClass();
		}
		
		JoinEvaluator joinEvaluator = joinEvaluatorRegistry.get(className);
		if (joinEvaluator==null){
			joinEvaluator = (JoinEvaluator)beanFactory.createBean(className);
			joinEvaluatorRegistry.put(className, joinEvaluator);
		}

		return joinEvaluator.canBeFired(session, token, siblings, synchronizer);
	}
	
	protected List<String> determineNextTransitions(
			WorkflowSession session, Token token4Node, Node node){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		
		String className = OrSplitEvaluator.class.getName();
		
		RouterFeature feature = (RouterFeature)node.getFeature();
		if (feature!=null && !StringUtils.isEmpty(feature.getSplitEvaluatorClass())){
			className = feature.getSplitEvaluatorClass();
		}
		
		SplitEvaluator splitEvaluator = this.splitEvaluatorRegistry.get(className);
		if (splitEvaluator==null){
			splitEvaluator = (SplitEvaluator)beanFactory.createBean(className);
			splitEvaluatorRegistry.put(className, splitEvaluator);
		}
		return splitEvaluator.determineNextTransitions(session, token4Node, node);
	}
}
