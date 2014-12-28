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
package org.fireflow.pvm.kernel;

import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;

/**
 * @author 非也
 * @version 2.0
 */
public interface NetInstance extends PObject {
	////////////////////////////////////////////////////////
	//////////////////   结构抽象        ////////////////////////
	///////////////////////////////////////////////////////
//	/**
//	 * 用于内嵌子流程的情形
//	 */
//	public NodeInstance getParentNodeInstance();
//	
//	public void setParentNodeInstance(NodeInstance nodeInst);
	

	
	//TODO 需要有对其子元素的引用吗？
	
	
	////////////////////////////////////////////////////////
	//////////////////   NodeInstance行为抽象     /////////////
	///////////////////////////////////////////////////////
}
