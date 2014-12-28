package org.fireflow.pdl.bpel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.InternalSessionAttributeKeys;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;
import org.firesoa.common.schema.NameSpaces;

public class BpelProcess implements WorkflowBehavior{
	private BpelActivity startActivity = null;
	

	private String name = null;
	
	public BpelProcess(String name){
		this.name = name;
	}
	
	public String getId() {
		return name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BpelActivity getStartActivity() {
		return startActivity;
	}
	public BpelProcess setStartActivity(BpelActivity activity) {
		this.startActivity = activity;
		return this;
	}

	public CompensationHandler getCompensationHandler(String compensationCode){
		return null;
	}
	
	public CancellationHandler getCancellationHandler(){
		return null;
	}
	
	public FaultHandler getFaultHandler(String errorCode){
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#canBeFired(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		PersistenceService persistenceStrategy = context.getEngineModule(PersistenceService.class, BpelConstants.PROCESS_TYPE);

		ProcessInstancePersister procInstPersistSvc = persistenceStrategy.getProcessInstancePersister();



		ProcessInstance newProcessInstance = ((WorkflowSessionLocalImpl)session).getCurrentProcessInstance();
		((ProcessInstanceImpl)newProcessInstance).setTokenId(token.getId());
		procInstPersistSvc.saveOrUpdate(newProcessInstance);
		

		token.setProcessInstanceId(newProcessInstance.getId());
		token.setElementInstanceId(newProcessInstance.getId());
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		tokenPersister.saveOrUpdate(token);
	

		return true;//true表示告诉虚拟机，“我”已经准备妥当了。
	}
	

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		List<Token> childTokenList = kernelManager.getChildren(token);
		if (childTokenList==null || childTokenList.size()==0){
			return ContinueDirection.closeMe();
		}else{
			for (Token tk : childTokenList){
				if (tk.getState().getValue()<TokenState.DELIMITER.getValue()){
					return ContinueDirection.waitingForClose();
				}
			}
		}
		return ContinueDirection.closeMe();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token parentToken,
			Object workflowElement) {
		BpelActivity activity = this.getStartActivity();
		
		PObjectKey pobjectKey = new PObjectKey(parentToken.getProcessId(),parentToken.getVersion(),parentToken.getProcessType(),activity.getId());
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.startPObject(session, pobjectKey, parentToken,null);
		
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.RUNNING);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, BpelConstants.PROCESS_TYPE);
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class,BpelConstants.PROCESS_TYPE);
		ProcessInstance procInst = procInstPersistenceService.find(ProcessInstance.class, token.getElementInstanceId());
		
		ProcessInstanceState state = ProcessInstanceState.valueOf(token.getState().name());
		((ProcessInstanceImpl)procInst).setState(state);
		if (state.getValue()>ProcessInstanceState.DELIMITER.getValue()){
			((ProcessInstanceImpl)procInst).setEndTime(calendarService.getSysDate());
			
		}
		procInstPersistenceService.saveOrUpdate(procInst);
		
	}
	
	public void abort(WorkflowSession session,Token thisToken,Object workflowElement){
		
	}
}
