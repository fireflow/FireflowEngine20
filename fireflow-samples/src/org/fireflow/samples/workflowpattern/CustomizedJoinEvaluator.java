package org.fireflow.samples.workflowpattern;

import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pdl.fpdl20.behavior.router.AbsJoinEvaluator;
import org.fireflow.pdl.fpdl20.process.Synchronizer;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

public class CustomizedJoinEvaluator extends AbsJoinEvaluator {

	
	
	@Override
	public int canBeFired(WorkflowSession session,
			Token current_token_for_router, List<Token> siblingTokens,
			Synchronizer node) {
		String fromTokenId = current_token_for_router.getFromToken();
		
		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class);
		
		Token fromToken = q4Token.get(fromTokenId);
		
		if (fromToken!=null && "CustomizedRouter.main.Transition6".equals(fromToken.getElementId())){
			//执行之前，将所有的兄弟token的state设置为completed状态；这一步必须执行，否则流程实例不能正确complete
			if (siblingTokens!=null){
				for (Token sibling: siblingTokens){
					if (!sibling.getId().equals(current_token_for_router.getId())){
						sibling.setState(TokenState.COMPLETED);
					}
				}
			}
			
			return current_token_for_router.getStepNumber();
		}
		else{
			//其他状况，统统不能被执行
			current_token_for_router.setState(TokenState.COMPLETED);//直接设置为completed状态

			return -1;//-1表示不能被执行

		}
		
	}

	public String getJoinDescription() {
		String desc = "只有Transition6上的Token才能激发该Router，其他的都被忽略";
		return desc;
	}

}
