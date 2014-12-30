package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.kernel.impl.TokenHistory;
import org.fireflow.pvm.kernel.impl.TokenImpl;
import org.nutz.dao.Cnd;

public class TokenPersisterNutzImpl extends AbsPersisterNutzImpl implements
		org.fireflow.engine.modules.persistence.TokenPersister {

	public void deleteAllTokens() {
		dao().clear(TokenImpl.class);

	}

	public Token findParentToken(Token token) {
		String pid = token.getParentTokenId();
		if (pid==null || pid.trim().equals("")){
			return null;
		}
		return this.fetch(TokenImpl.class, pid);
	}

	public List<Token> findChildTokens(Token token) {
		List result =  dao().query(TokenImpl.class, Cnd.where(TokenProperty.PARENT_TOKEN_ID.getPropertyName(),
				"=",token.getId()));

		return (java.util.List<Token>)result;
	}

	public List<Token> findChildTokens4Compensation(Token token) {
		Cnd cnd = Cnd.where(TokenProperty.PARENT_TOKEN_ID.getPropertyName(),"=",token.getId())
				.and(TokenProperty.OPERATION_CONTEXT_NAME.getPropertyName(),"=", OperationContextName.NORMAL);
		cnd.getOrderBy().desc(TokenProperty.STEP_NUMBER.getPropertyName());
		List result =  dao().query(TokenImpl.class, cnd	);
		

		return (java.util.List<Token>)result;
	}

	public int countAliveToken(String processInstanceId, String nodeId,
			OperationContextName operationContextName) {
		
		Cnd cnd = Cnd.where(TokenProperty.PROCESS_INSTANCE_ID.getPropertyName(),"=",processInstanceId)
		.and(TokenProperty.ELEMENT_ID.getPropertyName(),"=", nodeId)
		.and(TokenProperty.OPERATION_CONTEXT_NAME.getPropertyName(),"=", operationContextName)
		.and(TokenProperty.STATE.getPropertyName(),"<",TokenState.DELIMITER);

		return dao().count(TokenImpl.class, cnd);

	}

	public List<Token> findSiblings(Token token) {
		Cnd cnd = Cnd.where(TokenProperty.PROCESS_INSTANCE_ID.getPropertyName(),"=", token.getProcessInstanceId())
		.and(TokenProperty.ELEMENT_ID.getPropertyName(),"=", token.getElementId())
		.and(TokenProperty.OPERATION_CONTEXT_NAME.getPropertyName(),"=", token.getOperationContextName())
		.and(TokenProperty.STATE.getPropertyName(),"<",TokenState.DELIMITER);

		List result = dao().query(TokenImpl.class, cnd);
		return (List<Token>)result;
	}

	public List<Token> findAttachedTokens(Token token) {
		Cnd cnd = Cnd.where(TokenProperty.ATTACHED_TO_TOKEN.getPropertyName(),"=",token.getId());
		
		List result = dao().query(TokenImpl.class, cnd);

		return (List<Token>)result;
	}

	public Token findTokenByElementInstanceId(String elementInstanceId) {
		return dao().fetch(TokenImpl.class, Cnd.where(TokenProperty.ELEMENT_INSTANCE_ID.getPropertyName(), "=", elementInstanceId));
	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return TokenImpl.class;
	}

	@Override
	public Class getEntityClass4History(Class interfaceClz) {
		return TokenHistory.class;
	}

	
}
