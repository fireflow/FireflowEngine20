package org.fireflow.pdl.bpel.basic;

import javax.xml.namespace.QName;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.firesoa.common.schema.NameSpaces;

public class XYZActivity extends BasicActivity {
	public XYZActivity(String name){
		super(name);
	}
	
	
	@Override
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ProcessInstance processInstance = ((WorkflowSessionLocalImpl)session).getCurrentProcessInstance();
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, token.getProcessType());
		VariablePersister variablePersister = persistenceService.getVariablePersister();
		Variable var = variablePersister.findVariable(processInstance.getScopeId(), "x");
		Object _x = var.getPayload();
		
		if (_x != null) {
			Integer x = (Integer) _x;
			
			int level = getLevel();
			for (int i = 0; i < (level + 1); i++) {
				System.out.print("    ");// 打印空格
			}
			System.out.println(this.getName() + " executed!(x=" + x + ")");

			x = x + 1;

			((VariableImpl)var).setPayload(x);
			variablePersister.saveOrUpdate(var);
		}else{
			System.out.println(this.getName() + " executed!(x is null)");
		}
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.COMPLETED);
		return result;
	}
		
}
