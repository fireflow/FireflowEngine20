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
package org.fireflow.engine.modules.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;

/**
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsScheduler  extends AbsEngineModule 
implements RuntimeContextAware,Scheduler{
	private static Log log = LogFactory.getLog(AbsScheduler.class);

	
	protected RuntimeContext runtimeContext = null;
	

	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}


	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;		
	}

}
