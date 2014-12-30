/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.pdl.fpdl.behavior;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.invocation.TimerOperationName;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.ModelElement;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl.behavior.router.SplitEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.OrSplitEvaluator;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Synchronizer;
import org.fireflow.pdl.fpdl.process.features.Feature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchCompensationFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchFaultFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.NormalStartFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.TimerStartFeature;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class StartNodeBehavior extends AbsSynchronizerBehavior implements
		WorkflowBehavior {
	private static Log log = LogFactory.getLog(StartNodeBehavior.class);

	protected List<String> determineNextTransitions(
			WorkflowSession session, Token token4Node, Node node){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		
		String className = OrSplitEvaluator.class.getName();
		
		SplitEvaluator splitEvaluator = this.splitEvaluatorRegistry.get(className);
		if (splitEvaluator==null){
			splitEvaluator = (SplitEvaluator)beanFactory.createBean(className);
			splitEvaluatorRegistry.put(className, splitEvaluator);
		}
		return splitEvaluator.determineNextTransitions(session, token4Node, node);
	}
	
	public int canBeFired(WorkflowSession session, Token token,List<Token> liblings,
			Synchronizer synchronizer){
		return token.getStepNumber();
	}
	protected boolean hasAlivePreviousNode(WorkflowSession session,
			Token token, Node thisNode) {
		return false;
	}

	@Override
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		ActivityInstance activityInstance = sessionLocalImpl
				.getCurrentActivityInstance();

		StartNode startNode = (StartNode) workflowElement;
		Feature dec = startNode.getFeature();
		// 如果是流程入口，则直接执行后续节点
		// 流程入口的TimerStartFeature，JmsMessageStartFeature，
		// WebserviceStartFeature需要通过外部逻辑进行处理，其逻辑不是Startnode.execute(...)的职责
		SubProcess process = (SubProcess) startNode.getParent();
		Node entry = process.getEntry();
		if (entry != null && entry.getId().equals(startNode.getId())) {
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}
		// 如果是NormalStartFeature 或者 CatchFaultFeature或者CatchCompensationFeature，
		// 也不需要有执行逻辑，直接运行后续节点
		else if (dec == null || dec instanceof NormalStartFeature
				|| dec instanceof CatchFaultFeature
				|| dec instanceof CatchCompensationFeature) {
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}
		// 只处理Activity边上的TimerStartFeature
		else if (dec != null && dec instanceof TimerStartFeature) {

			// 1、检验currentActivityInstance和currentProcessInstance的一致性
			ProcessInstance oldProcInst = sessionLocalImpl.getCurrentProcessInstance();
			ActivityInstance oldActInst = sessionLocalImpl.getCurrentActivityInstance();

			RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
					.getRuntimeContext();
			PersistenceService persistenceService = ctx.getEngineModule(
					PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
			ActivityInstancePersister actInstPersistenceService = persistenceService
					.getActivityInstancePersister();
			ProcessInstancePersister processInstancePersister = persistenceService
					.getProcessInstancePersister();

			if (oldProcInst == null
					|| !oldProcInst.getId()
							.equals(token.getProcessInstanceId())) {
				ProcessInstance procInst = processInstancePersister.fetch(
						ProcessInstance.class, token.getProcessInstanceId());
				((WorkflowSessionLocalImpl) session)
						.setCurrentProcessInstance(procInst);
			}
			if (oldActInst == null
					|| !oldActInst.getId().equals(token.getElementInstanceId())) {
				ActivityInstance actInst = actInstPersistenceService.fetch(
						ActivityInstance.class, token.getElementInstanceId());
				((WorkflowSessionLocalImpl) session)
						.setCurrentActivityInstance(actInst);
			}

			try {
				// 2、执行业务逻辑
				TimerStartFeature timerDecorator = (TimerStartFeature) dec;
				createScheduleJob(session, activityInstance, timerDecorator);

				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.RUNNING);
				return result;
			} finally {
				((WorkflowSessionLocalImpl) session)
						.setCurrentProcessInstance(oldProcInst);
				((WorkflowSessionLocalImpl) session)
						.setCurrentActivityInstance(oldActInst);
			}
		} else {
			// 其他情况一律不进行处理，直接执行后续节点
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}
	}

	private void createScheduleJob(WorkflowSession session,
			ActivityInstance activityInstance, TimerStartFeature timerDecorator) {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		CalendarService calendarService = runtimeContext.getEngineModule(
				CalendarService.class, activityInstance.getProcessType());
		
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();

		String operationName = timerDecorator.getTimerOperationName().name();
		String triggerType = null;
		String triggerExpression = null;

		// TODO 注册ScheduleJob
		if (TimerOperationName.TRIGGERED_ONLY_ONCE.name().equalsIgnoreCase(
				operationName.trim())) {
			triggerType = ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL;
			triggerExpression = triggeredOnlyOnce(session, activityInstance,
					timerDecorator);
		} else if (TimerOperationName.TRIGGERED_BY_REPEAT_COUNT.name()
				.equalsIgnoreCase(operationName.trim())) {
			triggerType = ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL;
			triggerExpression = triggeredByRepeatCount(session,
					activityInstance, timerDecorator);
		} else if (TimerOperationName.TRIGGERED_BY_STARTTIME_ENDTIME.name()
				.equalsIgnoreCase(operationName.trim())) {
			triggerType = ScheduleJob.STARTTIME_ENDTIME_INTERVAL;
			triggerExpression = triggerdByStarttimeEndtime(session,
					activityInstance, timerDecorator);
		} else if (TimerOperationName.TRIGGERED_BY_CRON.name()
				.equalsIgnoreCase(operationName.trim())) {
			triggerType = ScheduleJob.CRON;
			triggerExpression = triggeredByCron(session, activityInstance,
					timerDecorator);
		}

		else {
			log.error("Unsupported timer operation '" + operationName + "'.");
			throw new EngineException(activityInstance,
					"Unsupported timer operation '" + operationName + "'.");
		}

		ScheduleJobImpl scheduleJob = new ScheduleJobImpl();
		scheduleJob.setName(activityInstance.getName());
		scheduleJob.setDisplayName(activityInstance.getDisplayName());
		scheduleJob.setState(ScheduleJobState.RUNNING);
		scheduleJob.setActivityInstance(activityInstance);
		if (timerDecorator.getAttachedToActivity() == null) {// 如果不是依附在Activity上，则是定时启动的StartNode
			scheduleJob.setCreateNewProcessInstance(true);
		} else {
			scheduleJob.setCreateNewProcessInstance(false);
		}

		scheduleJob.setCancelAttachedToActivity(timerDecorator
				.getCancelAttachedToActivity());

		scheduleJob.setProcessId(activityInstance.getProcessId());
		scheduleJob.setProcessType(activityInstance.getProcessType());
		scheduleJob.setVersion(activityInstance.getVersion());
		scheduleJob.setTriggerType(triggerType);
		scheduleJob.setTriggerExpression(triggerExpression);
		scheduleJob.setCreatedTime(calendarService.getSysDate());

		// 保存

		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, activityInstance.getProcessType());
		ScheduleJobPersister persister = persistenceService
				.getScheduleJobPersister();
		persister.saveOrUpdate(scheduleJob);

		// 加入调度器
		Scheduler scheduler = runtimeContext.getEngineModule(Scheduler.class,
				activityInstance.getProcessType());
		scheduler.schedule(scheduleJob, runtimeContext);
	}

	private String triggeredOnlyOnce(WorkflowSession session,
			ActivityInstance activityInstance, TimerStartFeature timerDecorator)
			throws EngineException {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();

		Expression theStartTimeExpression = timerDecorator
				.getStartTimeExpression();
		Expression theRepeatCountExpression = null;
		Expression theRepeatIntervalExpression = null;

		if (theStartTimeExpression == null
				|| theStartTimeExpression.getBody() == null
				|| theStartTimeExpression.getBody().trim().equals("")) {
			log.error("The start time expression is null");
			throw new EngineException(activityInstance,
					"The start time expression is null");
		}

		Map<String, Object> varContext = ScriptEngineHelper
				.fulfillScriptContext(session, runtimeContext, processInstance,
						activityInstance);

		Date startTime = null;
		Integer repeatCount = 0;
		Integer repeatInterval = 0;

		startTime = (Date) ScriptEngineHelper.evaluateExpression(
				runtimeContext, theStartTimeExpression, varContext);

		StringBuffer buf = new StringBuffer();
		buf.append(startTime.getTime()).append("|")
				.append(repeatCount.toString()).append("|")
				.append(repeatInterval);
		return buf.toString();

	}

	private String triggeredByRepeatCount(WorkflowSession session,
			ActivityInstance activityInstance, TimerStartFeature timerDecorator)
			throws EngineException {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();

		Expression theStartTimeExpression = timerDecorator
				.getStartTimeExpression();
		Expression theRepeatCountExpression = timerDecorator
				.getRepeatCountExpression();
		Expression theRepeatIntervalExpression = timerDecorator
				.getRepeatIntervalExpression();

		if (theStartTimeExpression == null
				|| theStartTimeExpression.getBody() == null
				|| theStartTimeExpression.getBody().trim().equals("")) {
			log.error("The start time expression is null");
			throw new EngineException(activityInstance,
					"The start time expression is null");
		}

		Map<String, Object> varContext = ScriptEngineHelper
				.fulfillScriptContext(session, runtimeContext, processInstance,
						activityInstance);

		Date startTime = null;
		Integer repeatCount = 0;
		Integer repeatInterval = 0;

		startTime = (Date) ScriptEngineHelper.evaluateExpression(
				runtimeContext, theStartTimeExpression, varContext);

		if (theRepeatCountExpression != null
				&& theRepeatCountExpression.getBody() != null
				&& !theRepeatCountExpression.getBody().trim().equals("")) {
			repeatCount = (Integer) ScriptEngineHelper.evaluateExpression(
					runtimeContext, theRepeatCountExpression, varContext);
		}

		if (theRepeatIntervalExpression != null
				&& theRepeatIntervalExpression.getBody() != null
				&& !theRepeatIntervalExpression.getBody().trim().equals("")) {
			repeatInterval = (Integer) ScriptEngineHelper.evaluateExpression(
					runtimeContext, theRepeatIntervalExpression, varContext);
		}

		StringBuffer buf = new StringBuffer();
		buf.append(startTime.getTime()).append("|")
				.append(repeatCount.toString()).append("|")
				.append(repeatInterval);
		return buf.toString();

	}

	public String triggerdByStarttimeEndtime(WorkflowSession session,
			ActivityInstance activityInstance, TimerStartFeature timerDecorator)
			throws EngineException {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();
		Expression theStartTimeExpression = timerDecorator
				.getStartTimeExpression();
		Expression theEndTimeExpression = timerDecorator.getEndTimeExpression();
		Expression theRepeatIntervalExpression = timerDecorator
				.getRepeatCountExpression();

		if (theStartTimeExpression == null
				|| theStartTimeExpression.getBody() == null
				|| theStartTimeExpression.getBody().trim().equals("")) {
			log.error("The start time expression is null");
			throw new EngineException(activityInstance,
					"The start time expression is null");
		}

		Map<String, Object> varContext = ScriptEngineHelper
				.fulfillScriptContext(session, runtimeContext, processInstance,
						activityInstance);

		Date startTime = null;
		Date endTime = null;
		Integer repeatInterval = 0;

		startTime = (Date) ScriptEngineHelper.evaluateExpression(
				runtimeContext, theStartTimeExpression, varContext);

		if (theEndTimeExpression != null
				&& theEndTimeExpression.getBody() != null
				&& !theEndTimeExpression.getBody().trim().equals("")) {
			endTime = (Date) ScriptEngineHelper.evaluateExpression(
					runtimeContext, theEndTimeExpression, varContext);
		}

		if (theRepeatIntervalExpression != null
				&& theRepeatIntervalExpression.getBody() != null
				&& !theRepeatIntervalExpression.getBody().trim().equals("")) {
			repeatInterval = (Integer) ScriptEngineHelper.evaluateExpression(
					runtimeContext, theRepeatIntervalExpression, varContext);
		}

		StringBuffer buf = new StringBuffer();
		buf.append(startTime.getTime()).append("|")
				.append(endTime == null ? "null" : endTime.getTime())
				.append("|").append(repeatInterval);
		return buf.toString();

	}

	private String triggeredByCron(WorkflowSession session,
			ActivityInstance activityInstance, TimerStartFeature timerDecorator)
			throws EngineException {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();
		Expression theCronExpression = timerDecorator.getCronExpression();

		if (theCronExpression == null) {
			log.error("The cron expression is null!");
			throw new EngineException(activityInstance,
					"The cron expression is null");
		}

		Map<String, Object> varContext = ScriptEngineHelper
				.fulfillScriptContext(session, runtimeContext, processInstance,
						activityInstance);

		try {
			Object obj = ScriptEngineHelper.evaluateExpression(runtimeContext,
					theCronExpression, varContext);

			return (String) obj;
		} catch (ClassCastException e) {
			log.error(
					"The result of the cron expression is not a java String object.",
					e);
			throw new EngineException(activityInstance,
					"The result of the cron expression is not a java String object."
							+ e.getMessage());
		}
	}

	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		// 启动后续节点，同时
		if (token.getState().getValue() != TokenState.RUNNING.getValue()) {
			return ContinueDirection.closeMe();
		}

		List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,
				token, workflowElement);
		ContinueDirection direction = null;

		// 判断start节点的装饰器类型
		StartNode startNode = (StartNode) workflowElement;
		Feature dec = startNode.getFeature();
		// 只处理TimerStartDecorator
		if (dec == null || !(dec instanceof TimerStartFeature)) {
			direction = ContinueDirection.closeMe();
			direction.setNextProcessObjectKeys(nextPObjectKeys);
			return direction;
		} else {
			// 只有依附在其他Activity上的timer才用ContinueDirection.startNextAndWaitingForClose();
			SubProcess subflow = (SubProcess) ((ModelElement) startNode).getParent();
			Node entry = subflow.getEntry();
			if (entry != null && entry.getId().equals(startNode.getId())) {
				direction = ContinueDirection.closeMe();
				direction.setNextProcessObjectKeys(nextPObjectKeys);
				return direction;
			} else {
				direction = ContinueDirection.startNextAndWaitingForClose();
				direction.setNextProcessObjectKeys(nextPObjectKeys);
			}
		}

		return direction;

	}
}
