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
package org.fireflow.pdl.fpdl.process.features.router;

import org.fireflow.pdl.fpdl.process.features.Feature;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface RouterFeature extends Feature {
	/**
	 * 汇聚逻辑的描述信息
	 * @return
	 */
	public String getJoinDescription();
	
	/**
	 * 分支描述信息
	 * @return
	 */
	public String getSplitDescription();
	/**
	 * 汇聚评价器的java类名或者Spring Bean id；
	 * 汇聚评价器必须实现org.fireflow.pdl.fpdl20.behavior.router.JoinEvaluator
	 * @return
	 */
	public String getJoinEvaluatorClass();
	
	/**
	 * 分支评价器的类名或者Spring Bean id；
	 * 分支评价器必须实现org.fireflow.pdl.fpdl20.behavior.router.SplitEvaluator
	 * @return
	 */
	public String getSplitEvaluatorClass();
}
