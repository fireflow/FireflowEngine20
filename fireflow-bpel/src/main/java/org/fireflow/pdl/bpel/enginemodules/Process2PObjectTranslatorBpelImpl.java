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
package org.fireflow.pdl.bpel.enginemodules;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.bpel.BpelActivity;
import org.fireflow.pdl.bpel.BpelProcess;
import org.fireflow.pdl.bpel.structure.Child;
import org.fireflow.pdl.bpel.structure.StructureActivity;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.impl.NetInstanceImpl;
import org.fireflow.pvm.kernel.impl.NodeInstanceImpl;
import org.fireflow.pvm.translate.Process2PObjectTranslator;

/**
 * @author 非也
 * @version 2.0
 */
public class Process2PObjectTranslatorBpelImpl implements
		Process2PObjectTranslator {
	RuntimeContext runtimeContext = null;
	
	private void translateActivity(BpelActivity activity ,List<PObject> result,ProcessKey pk){
		PObjectKey key = new PObjectKey(pk.getProcessId(),
				pk.getVersion(), pk
						.getProcessType(), activity.getId());
		
		PObject pObject = new NodeInstanceImpl(key);
		pObject.setWorkflowBehavior(activity);
		pObject.setWorkflowElement(activity);
		
		result.add(pObject);
		
		if (activity instanceof StructureActivity){
			List<Child> children = ((StructureActivity)activity).getChildren();
			if (children!=null){
				for (Child child : children){
					translateActivity(child.getChildBpelActivity(),result,pk);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.translate.Process2PObjectTranslator#translateProcess(org.fireflow.engine.entity.repository.ProcessKey, java.lang.Object)
	 */
	public List<PObject> translateProcess(ProcessKey processKey, Object process) {
		BpelProcess bpelProcess = (BpelProcess)process;
		
		PObjectKey key = new PObjectKey(processKey.getProcessId(),
				processKey.getVersion(), processKey
						.getProcessType(), bpelProcess.getId());
		
		PObject pObject = new NetInstanceImpl(key);
		pObject.setWorkflowBehavior(bpelProcess);
		pObject.setWorkflowElement(bpelProcess);
		
		List<PObject> pobjectList = new ArrayList<PObject>();
		pobjectList.add(pObject);
		
		ProcessKey pk = ProcessKey.valueOf(key);
		
		translateActivity(bpelProcess.getStartActivity(),pobjectList,pk);
		
		return pobjectList;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.translate.Process2PObjectTranslator#translateProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	public List<PObject> translateProcess(ProcessKey processKey)  throws InvalidModelException,WorkflowProcessNotFoundException{
		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, processKey.getProcessType());
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(processKey);
		if (repository==null){
			throw new WorkflowProcessNotFoundException("The process is not found, id="+processKey.getProcessId()+", version="+processKey.getVersion()+", processType="+processKey.getProcessType());
		}else{
			return this.translateProcess(processKey, repository.getProcessObject());
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		this.runtimeContext = ctx;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.EngineModule#init(org.fireflow.engine.context.RuntimeContext)
	 */
	public void init(RuntimeContext runtimeContext) throws EngineException {
		// TODO Auto-generated method stub
		
	}

}
