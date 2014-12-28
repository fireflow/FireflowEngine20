/**
 * Copyright 2007-2011 非也
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
package org.fireflow.service.java.mock;


/**
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
public interface Calculator {
	/**
	 * 
	 * @param action 操作符
	 * @param param 操作数
	 * @param flag 简单类型的标志位，没有什么作用，纯粹用于测试简单类型输入参数。
	 * @return
	 * @throws Exception
	 */
	public Result calculate(MathsAction action,Operand param,int flag) throws Exception;
}
