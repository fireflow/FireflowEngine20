package org.fireflow.pdl.bpel.structure;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.pdl.bpel.AbstractActivity;
import org.fireflow.pdl.bpel.BpelActivity;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;

public abstract class StructureActivity extends AbstractActivity {
	
	List<Child> children = new ArrayList<Child>();//子节点
	
	public StructureActivity(String name){
		super(name);
	}

	
	public void addChild(Child child){
		this.children.add(child);
	}
	
	public void addChild(int index,Child child){
		this.children.add(index, child);
	}
	
	public void setChild(int index,Child child){
		this.children.set(index, child);
	}
	
	public List<Child> getChildren(){
		return children;
	}
	
	protected void executeChildActivity(WorkflowSession session ,Token parentToken,BpelActivity activity){
		PObjectKey pobjectKey = new PObjectKey(parentToken.getProcessId(),parentToken.getVersion(),parentToken.getProcessType(),activity.getId());
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.startPObject(session, pobjectKey, parentToken,null);
	}	
	
	public void abort(WorkflowSession session,Token thisToken,Object workflowElement){
		
	}
}
