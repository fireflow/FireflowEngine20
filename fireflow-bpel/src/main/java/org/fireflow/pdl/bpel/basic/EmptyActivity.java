package org.fireflow.pdl.bpel.basic;

import org.fireflow.client.WorkflowSession;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ExecuteResult;

/**
 * 空Activity
 * @author 非也
 *
 */
public class EmptyActivity extends BasicActivity {
	public EmptyActivity(String name){
		super(name);
	}
	
	
	@Override
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
	
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.COMPLETED);
		return result;
	}
	
}
