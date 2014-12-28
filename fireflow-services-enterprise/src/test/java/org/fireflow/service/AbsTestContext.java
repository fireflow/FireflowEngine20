package org.fireflow.service;

import java.util.Date;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

public class AbsTestContext extends AbstractJUnit4SpringContextTests {
	public ProcessInstance createProcessInstance(WorkflowSession sessionLocal,RuntimeContext runtimeContext){
		CalendarService calendarService = runtimeContext.getDefaultEngineModule(CalendarService.class);
		User u = sessionLocal.getCurrentUser();
		
		ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
		processInstance.setProcessId("processId-123456");
		processInstance.setVersion(1);
		processInstance.setProcessType("FPDL");
		processInstance.setSubProcessId("processId-123456.main_flow");
		processInstance.setBizId("bizId-123456");
		processInstance.setProcessName("TestProcess");
		processInstance.setProcessDisplayName("TestProcess");
		processInstance.setBizType("test");
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		Date now = calendarService.getSysDate();
		processInstance.setCreatedTime(now);
		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		processInstance.setTokenId("tokenId-123");
		
		PersistenceService persistenceService = runtimeContext.getDefaultEngineModule(PersistenceService.class);
		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
		
		persister.saveOrUpdate(processInstance);
		
		return processInstance;
	}
	
	public ActivityInstance createActivityInstance(RuntimeContext runtimeContext,ProcessInstance processInstance){
		CalendarService calendarService = runtimeContext.getDefaultEngineModule(CalendarService.class);
		
		
//		Node node = (Node)activity;
		ActivityInstanceImpl actInst = new ActivityInstanceImpl();
		actInst.setName("test-activity");
	
		actInst.setDisplayName("test-activity");
		actInst.setState(ActivityInstanceState.INITIALIZED);
		
		actInst.setProcessName(processInstance.getProcessName());
		actInst.setProcessDisplayName(processInstance.getProcessDisplayName());
		actInst.setBizType(processInstance.getBizType());
		
		actInst.setProcessId(processInstance.getProcessId());
		actInst.setVersion(processInstance.getVersion());
		actInst.setProcessType(processInstance.getProcessType());
		actInst.setProcessInstanceId(processInstance.getId());
		actInst.setNodeId("activityId-123456");		
		actInst.setBizId(processInstance.getBizId());
		actInst.setTokenId("tokenId-456");
		actInst.setStepNumber(1);
		actInst.setSubProcessId("subflow_123");
		
		actInst.setParentScopeId(processInstance.getScopeId());
		
		Date now = calendarService.getSysDate();
		actInst.setCreatedTime(now);

		PersistenceService persistenceService = runtimeContext.getDefaultEngineModule(PersistenceService.class);
		ActivityInstancePersister persister = persistenceService.getActivityInstancePersister();
		
		persister.saveOrUpdate(actInst);
		
		return actInst;
	}
}
