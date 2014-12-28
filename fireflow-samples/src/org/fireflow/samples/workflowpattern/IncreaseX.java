package org.fireflow.samples.workflowpattern;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;

//import org.fireflow.

public class IncreaseX {
	public void increaseX(WorkflowSession session,ProcessInstance processInstance){
		Integer x = (Integer)processInstance.getVariableValue(session, "x");
		if (x==null){
			x = 0;
		}
		x = x+1;
		try {
			processInstance.setVariableValue(session, "x", x);
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
