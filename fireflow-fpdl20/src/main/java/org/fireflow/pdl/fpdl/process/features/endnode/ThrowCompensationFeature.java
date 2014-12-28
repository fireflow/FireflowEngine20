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
package org.fireflow.pdl.fpdl.process.features.endnode;

import java.util.List;

import org.fireflow.pdl.fpdl.process.features.Feature;

/**
 * 当流程以该类型EndNode结束的时候，表示流程将被Cancel，已完成的工作被回滚。
 * @author 非也
 * @version 2.0
 */
public interface ThrowCompensationFeature extends Feature {
	/**
	 * 将一个compensationCode追加在补偿代码列的末尾
	 * @param compensationCode
	 */
	public void addCompensationCode(String compensationCode);
	
	/**
	 * 获得补偿代码列，补偿操作将按照补偿代码列中的顺序执行。
	 * @return
	 */
	public List<String> getCompensationCodes();
}
