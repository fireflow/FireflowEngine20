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

import org.fireflow.pdl.fpdl.behavior.router.AbsSplitEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.SplitEvaluator;

/**
 * 通过计算transition的转移条件来确定是否激活
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class OrSplitEvaluator extends AbsSplitEvaluator implements SplitEvaluator {
	public static final String SPLIT_DESCRIPTION = "分支逻辑：所有符合条件分支都会被执行。";
	public String getSplitDescription(){
		return SPLIT_DESCRIPTION;
	}
	

}
