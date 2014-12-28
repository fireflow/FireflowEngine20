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
package org.fireflow.pvm.translate;

import java.util.List;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.PObject;

/**
 * 特定流程定义语言到流程虚拟机流程对象的转换器。
 * @author 非也
 * @version 2.0
 */
public interface Process2PObjectTranslator extends EngineModule,RuntimeContextAware{
	public List<PObject> translateProcess(ProcessKey processKey,Object process);
	public List<PObject> translateProcess(ProcessKey processKey) throws InvalidModelException,WorkflowProcessNotFoundException;
}
