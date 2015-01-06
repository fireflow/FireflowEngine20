/**
 * 
 */
var ioc = {
		fireflowEventBroadcasterManager:{
			type:"org.fireflow.engine.modules.event.impl.EventBroadcasterManagerImpl",
			fields:{
				eventBroadcasters:{
					org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent:{refer:"activityInstanceEventBroadcaster"},
					org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent:{refer:"processInstanceEventBroadcaster"},
					org.fireflow.engine.modules.workitem.event.WorkItemEvent:{refer:"workItemEventBroadcaster"}
				}
			}
		}
}