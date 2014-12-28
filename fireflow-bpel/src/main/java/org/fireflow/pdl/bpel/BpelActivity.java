package org.fireflow.pdl.bpel;

import org.fireflow.pvm.pdllogic.WorkflowBehavior;


public interface BpelActivity extends WorkflowBehavior{
	
	public String getId();
	
	public String getName();
	public void setName(String name);
	
	public BpelActivity getParent();
	public void setParent(BpelActivity p);
	
	public BpelProcess getProcess();
	public void setProcess(BpelProcess process);

}
