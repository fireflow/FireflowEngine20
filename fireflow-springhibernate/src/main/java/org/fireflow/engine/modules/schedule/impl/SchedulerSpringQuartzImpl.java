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
package org.fireflow.engine.modules.schedule.impl;

import java.text.ParseException;
import java.util.Date;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.TransactionTemplateAware;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.schedule.AbsScheduler;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class SchedulerSpringQuartzImpl  extends AbsScheduler implements TransactionTemplateAware {
    private Scheduler quartzScheduler;
    protected TransactionTemplate transactionTemplate = null;

	/**
	 * @return the transactionTemplate
	 */
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	/**
	 * @param transactionTemplate the transactionTemplate to set
	 */
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}    
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.timmer.AbsTimerHandlerManager#schedule(org.fireflow.engine.entity.runtime.TimerHandler)
	 */
	public void schedule(ScheduleJob timerHandler,RuntimeContext runtimeContext) {
        try
        {
            String triggerType = timerHandler.getTriggerType();
            String triggerExpression = timerHandler.getTriggerExpression();
            Trigger trigger = null;
            if (triggerType.equals(ScheduleJob.CRON)){
            	trigger =new CronTrigger((new StringBuilder()).append(timerHandler.getId()).append("_trigger").toString(), JOB_GROUP_NAME, timerHandler.getId(), JOB_GROUP_NAME, triggerExpression);
            }
            else if (triggerType.equals(ScheduleJob.STARTTIME_REPEATCOUNT_INTERVAL)){

            	int index = triggerExpression.indexOf("|");
            	String timeStr = triggerExpression.substring(0, index);
            	triggerExpression = triggerExpression.substring(index+1);
            	
            	index = triggerExpression.indexOf("|");
            	String repeatCountStr = triggerExpression.substring(0, index);
            	triggerExpression = triggerExpression.substring(index+1);
            	
            	String intervalStr = triggerExpression;

            	Date time = new Date(Long.parseLong(timeStr));
            	Integer repeatCount = Integer.parseInt(repeatCountStr);
            	Integer interval = Integer.parseInt(intervalStr);
            	
            	if(time==null ){
            		throw new NullPointerException("The start time is null");
            	}
            	if (repeatCount==null){
            		throw new NullPointerException("The repeat count is null");
            	}
            	if (interval==null){
            		throw new NullPointerException("The interval is null");
            	}
            	
            	trigger = new SimpleTrigger((new StringBuilder()).append(timerHandler.getId()).append("_trigger").toString(), JOB_GROUP_NAME, timerHandler.getId(), JOB_GROUP_NAME,
            			time,null,repeatCount,interval);
            	
            }else if (triggerType.equals(ScheduleJob.STARTTIME_ENDTIME_INTERVAL)){
            	int index = triggerExpression.indexOf("|");
            	String timeStr = triggerExpression.substring(0, index);
            	triggerExpression = triggerExpression.substring(index+1);
            	
            	index = triggerExpression.indexOf("|");
            	String endTimeStr = triggerExpression.substring(0, index);
            	triggerExpression = triggerExpression.substring(index+1);
            	
            	String intervalStr = triggerExpression;

            	Date time = new Date(Long.parseLong(timeStr));
            	Date endTime = null;
            	if (endTimeStr==null || endTimeStr.trim().equals("") || endTimeStr.equalsIgnoreCase("null")){
            		endTime = null;
            	}else{
            		endTime = new Date(Integer.parseInt(endTimeStr));
            	}
            	Integer interval = Integer.parseInt(intervalStr);
            	
            	if(time==null ){
            		throw new NullPointerException("The start time is null");
            	}

            	if (interval==null){
            		throw new NullPointerException("The interval is null");
            	}
            	
            	trigger = new SimpleTrigger((new StringBuilder()).append(timerHandler.getId()).append("_trigger").toString(), JOB_GROUP_NAME, timerHandler.getId(), JOB_GROUP_NAME,
            			time,endTime,0,interval);
            }
            
            if (trigger==null){
            	//将timerHandler的状态改为非活动状态
            	((ScheduleJobImpl)timerHandler).setState(ScheduleJobState.OUT_OF_DATE);
            	((ScheduleJobImpl)timerHandler).setNote("Unsupported trigger type!");
        		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, timerHandler.getProcessType());
        		ScheduleJobPersister persister = persistenceService.getScheduleJobPersister();
        		persister.saveOrUpdate(timerHandler);
            }
            //TODO 检验trigger是否可以被触发
            
            
            JobDetail jobDetail = new JobDetail(timerHandler.getId(),JOB_GROUP_NAME, ScheduleJobBean4SpringQuartz.class);
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(ScheduleJob.class.getName(), timerHandler);
            jobDataMap.put(RuntimeContext.class.getName(), runtimeContext);
            jobDetail.setJobDataMap(jobDataMap);
            quartzScheduler.addJob(jobDetail, true);

            quartzScheduler.scheduleJob(trigger);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        catch(SchedulerException ex)
        {
            ex.printStackTrace();
        }
	}
	public void unSchedule(ScheduleJob scheduleJob,RuntimeContext runtimeContext){
		try {
			quartzScheduler.deleteJob(scheduleJob.getId(), JOB_GROUP_NAME);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean ifJobCanBeFiredAgain(ScheduleJob job,RuntimeContext runtimeContext){
		try {
			Trigger trigger = quartzScheduler.getTrigger(job.getId()+"_trigger", JOB_GROUP_NAME);
			return trigger.mayFireAgain();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	/**
	 * @return the quartzScheduler
	 */
	public Scheduler getQuartzScheduler() {
		return quartzScheduler;
	}
	/**
	 * @param quartzScheduler the quartzScheduler to set
	 */
	public void setQuartzScheduler(Scheduler quartzScheduler) {
		this.quartzScheduler = quartzScheduler;
	}
	

	/**
	 * 触发器被触发
	 */
	public void onTimerTriggered(final ScheduleJob scheduleJob,final RuntimeContext runtimeContext){
		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, scheduleJob.getProcessType());
		final ScheduleJobPersister persister = persistenceService.getScheduleJobPersister();	
		final TokenPersister tokenPersister = persistenceService.getTokenPersister();
		final CalendarService calendarService = runtimeContext.getEngineModule(CalendarService.class, scheduleJob.getProcessType());
		final ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
		final WorkflowStatement statement = session.createWorkflowStatement(scheduleJob.getProcessType());
		if (scheduleJob.isCreateNewProcessInstance()){
			transactionTemplate.execute(new TransactionCallback(){

				
				public Object doInTransaction(TransactionStatus arg0) {
					try {
						statement.startProcess(scheduleJob.getProcessId(), scheduleJob.getVersion(), null, null);
						((ScheduleJobImpl)scheduleJob).setTriggeredTimes(scheduleJob.getTriggeredTimes()+1);
						((ScheduleJobImpl)scheduleJob).setLatestTriggeredTime(calendarService.getSysDate());
						
						persister.saveOrUpdate(scheduleJob);
						
					} catch (InvalidModelException e) {
						((ScheduleJobImpl)scheduleJob).setState(ScheduleJobState.FAULTED);
						((ScheduleJobImpl)scheduleJob).setEndTime(calendarService.getSysDate());
						((ScheduleJobImpl)scheduleJob).setNote(e.getMessage());
						persister.saveOrUpdate(scheduleJob);
						unSchedule(scheduleJob, runtimeContext);
						e.printStackTrace();
					} catch (WorkflowProcessNotFoundException e) {
						// TODO Auto-generated catch block
						((ScheduleJobImpl)scheduleJob).setState(ScheduleJobState.FAULTED);
						((ScheduleJobImpl)scheduleJob).setEndTime(calendarService.getSysDate());
						((ScheduleJobImpl)scheduleJob).setNote(e.getMessage());
						persister.saveOrUpdate(scheduleJob);
						unSchedule(scheduleJob, runtimeContext);
						e.printStackTrace();
					} catch (InvalidOperationException e) {
						// TODO Auto-generated catch block
						((ScheduleJobImpl)scheduleJob).setState(ScheduleJobState.FAULTED);
						((ScheduleJobImpl)scheduleJob).setEndTime(calendarService.getSysDate());
						((ScheduleJobImpl)scheduleJob).setNote(e.getMessage());	
						persister.saveOrUpdate(scheduleJob);
						unSchedule(scheduleJob, runtimeContext);
						e.printStackTrace();
					}
					return null;
				}
				
			});

		} else {
			transactionTemplate.execute(new TransactionCallback() {

				
				public Object doInTransaction(TransactionStatus arg0) {
					ActivityInstance activityInstance =  actInstPersister.fetch(ActivityInstanceImpl.class, scheduleJob
							.getActivityInstanceId())	;

					if (activityInstance == null
							|| activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
									.getValue()) {
						((ScheduleJobImpl) scheduleJob)
								.setState(ScheduleJobState.FAULTED);
						((ScheduleJobImpl) scheduleJob)
								.setEndTime(calendarService.getSysDate());
						if (activityInstance == null) {
							((ScheduleJobImpl) scheduleJob)
									.setNote("The activity instance is null.");
						} else {
							((ScheduleJobImpl) scheduleJob)
									.setNote("The state of the activity instance is dead.");
						}
						persister.saveOrUpdate(scheduleJob);
						unSchedule(scheduleJob, runtimeContext);
						return null;
					}

					//记录触发信息
					((ScheduleJobImpl) scheduleJob).setLatestTriggeredTime(calendarService.getSysDate());
					((ScheduleJobImpl) scheduleJob).setTriggeredTimes(scheduleJob.getTriggeredTimes()+1);
					persister.saveOrUpdate(scheduleJob);
					
					ProcessInstance processInstance = activityInstance
							.getProcessInstance(session);
					((WorkflowSessionLocalImpl) session)
							.setCurrentActivityInstance(activityInstance);
					((WorkflowSessionLocalImpl) session)
							.setCurrentProcessInstance(processInstance);

					RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
							.getRuntimeContext();
					ActivityInstanceManager activityInstanceMgr = ctx
							.getEngineModule(ActivityInstanceManager.class,
									activityInstance.getProcessType());

					activityInstanceMgr.onServiceCompleted(session,
							activityInstance);
					
					//将所依附的activity实例关闭
					if (scheduleJob.isCancelAttachedToActivity()){
						Token thisToken = tokenPersister.fetch(Token.class, activityInstance.getTokenId());
						Token attachedToToken = tokenPersister.fetch(Token.class,thisToken.getAttachedToToken()==null?"":thisToken.getAttachedToToken());
						if (attachedToToken!=null && attachedToToken.getState().getValue()<TokenState.DELIMITER.getValue()){
							KernelManager kernelManager = ctx.getEngineModule(KernelManager.class, attachedToToken.getProcessType());
							
							BookMark bookMark = new BookMark();
							bookMark.setToken(attachedToToken);
							bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);
							bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
							
							kernelManager.addBookMark(bookMark);
							
							kernelManager.execute(session);
						}
					}
					return null;
				}

			});

		}
	}	
	
	public boolean hasJobInSchedule(RuntimeContext runtimeContext){
		try {
			String[] triggerNames = this.quartzScheduler.getTriggerNames(JOB_GROUP_NAME);
			if (triggerNames==null || triggerNames.length==0){
				return false;
			}
			else return true;
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
