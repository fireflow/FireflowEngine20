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
package org.fireflow.pdl.fpdl.process.features.router.impl;

import org.fireflow.pdl.fpdl.behavior.router.impl.AndJoinEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.AndSplitEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.OrJoinEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.OrSplitEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.XOrJoinEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.XOrSplitEvaluator;
import org.fireflow.pdl.fpdl.process.features.router.RouterFeature;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CustomizedRouterFeature implements RouterFeature {

	private String joinEvaluatorName = OrJoinEvaluator.class.getName();
	private String splitEvaluatorName = OrSplitEvaluator.class.getName();
	
	private static final String joinDesc = "自定义汇聚逻辑。";
	private static final String splitDesc = "自定义分支逻辑。";
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.features.router.RouterFeature#getDescription()
	 */
	public String getJoinDescription() {
		if(AndJoinEvaluator.class.getName().equals(joinEvaluatorName)){
			return AndJoinEvaluator.JOIN_DESCRIPTION;
		}else if (XOrJoinEvaluator.class.getName().equals(joinEvaluatorName)){
			return XOrJoinEvaluator.JOIN_DESCRIPTION;
		}else if (OrJoinEvaluator.class.getName().equals(joinEvaluatorName)){
			return OrJoinEvaluator.JOIN_DESCRIPTION;
		}
		return joinDesc;
	}
	
	public String getSplitDescription(){
		if (AndSplitEvaluator.class.getName().equals(splitEvaluatorName)){
			return AndSplitEvaluator.SPLIT_DESCRIPTION;
		}else if (XOrSplitEvaluator.class.getName().equals(splitEvaluatorName)){
			return XOrSplitEvaluator.SPLIT_DESCRIPTION;
		}else if (OrSplitEvaluator.class.getName().equals(splitEvaluatorName)){
			return OrSplitEvaluator.SPLIT_DESCRIPTION;
		}
		return splitDesc;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.features.router.RouterFeature#getJoinEvaluatorClass()
	 */
	public String getJoinEvaluatorClass() {
		return joinEvaluatorName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.features.router.RouterFeature#getSplitEvalutorClass()
	 */
	public String getSplitEvaluatorClass() {
		return splitEvaluatorName;
	}
	
	public void setJoinEvaluatorClass(String className){
		this.joinEvaluatorName = className;
	}

	public void setSplitEvaluatorClass(String className){
		this.splitEvaluatorName = className;
	}
}
