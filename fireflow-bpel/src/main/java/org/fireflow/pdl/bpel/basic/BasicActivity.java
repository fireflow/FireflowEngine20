package org.fireflow.pdl.bpel.basic;

import org.fireflow.client.WorkflowSession;
import org.fireflow.pdl.bpel.AbstractActivity;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ExecuteResult;

public class BasicActivity extends AbstractActivity {

	public BasicActivity(String name){
		super(name);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.COMPLETED);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#abort(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void abort(WorkflowSession session, Token token,
			Object workflowElement) {
		// TODO Auto-generated method stub
		
	}
	

}
