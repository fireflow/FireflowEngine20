package org.fireflow.pdl.bpel;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.FaultHandler;

public abstract class AbstractActivity implements BpelActivity {
	protected String id = null;
	protected String name = null;
	protected BpelActivity parent = null;
	protected BpelProcess process = null;

	public AbstractActivity(String name) {
		this.name = name;
	}

	public String getId() {
		if (this.parent != null) {
			return this.parent.getId() + "." + this.name;
		} else if (this.process != null) {
			return this.process.getName() + "." + this.name;
		} else {
			return name;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	public BpelActivity getParent() {
		return parent;
	}

	public void setParent(BpelActivity p) {
		parent = p;
	}

	public BpelProcess getProcess() {
		return process;
	}

	public void setProcess(BpelProcess process) {
		this.process = process;
	}

	/**
	 * 一个用于该实验的方法，获得当前Activity所处的层级。
	 * 
	 * @return
	 */
	protected int getLevel() {
		if (this.getParent() == null) {
			return 0;
		} else {
			AbstractActivity activity = (AbstractActivity) this.getParent();
			return activity.getLevel() + 1;
		}
	}

	public CompensationHandler getCompensationHandler(String compensationCode) {
		return null;
	}

	public CancellationHandler getCancellationHandler() {
		return null;
	}

	public FaultHandler getFaultHandler(String errorCode) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.pdllogic.WorkflowBehavior#canBeFired(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		int level = getLevel();
		for (int i = 0; i < (level + 1); i++) {
			System.out.print("    ");// 打印空格
		}
		System.out.println(this.getName() + " executing...");

		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(
				ActivityInstanceManager.class, BpelConstants.PROCESS_TYPE);
		PersistenceService persistenceStrategy = ctx.getEngineModule(
				PersistenceService.class, BpelConstants.PROCESS_TYPE);
		ActivityInstancePersister actInstPersistSvc = persistenceStrategy
				.getActivityInstancePersister();

		ProcessInstance processInstance = ((WorkflowSessionLocalImpl)session).getCurrentProcessInstance();
		ActivityInstance activityInstance = activityInstanceMgr
				.createActivityInstance(session, processInstance,
						workflowElement);
		((ActivityInstanceImpl) activityInstance).setTokenId(token.getId());
		((ActivityInstanceImpl) activityInstance).setStepNumber(token
				.getStepNumber());

		actInstPersistSvc.saveOrUpdate(activityInstance);

		((WorkflowSessionLocalImpl) session)
				.setCurrentActivityInstance(activityInstance);

		token.setElementInstanceId(activityInstance.getId());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {

		int level = getLevel();
		for (int i = 0; i < (level + 1); i++) {
			System.out.print("    ");// 打印空格
		}
		System.out.println(this.getName() + " completed!");

		return ContinueDirection.closeMe();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow
	 * .engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();

		PersistenceService persistenceStrategy = ctx.getEngineModule(
				PersistenceService.class, BpelConstants.PROCESS_TYPE);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy
				.getActivityInstancePersister();

		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, BpelConstants.PROCESS_TYPE);
		ActivityInstance activityInstance = actInstPersistenceService.find(
				ActivityInstance.class, token.getElementInstanceId());

		ActivityInstanceState state = ActivityInstanceState.valueOf(token
				.getState().name());
		((ActivityInstanceImpl) activityInstance).setState(state);
		if (state.getValue() > ActivityInstanceState.DELIMITER.getValue()) {
			((ActivityInstanceImpl) activityInstance)
					.setEndTime(calendarService.getSysDate());

		}
		actInstPersistenceService.saveOrUpdate(activityInstance);

	}
}
