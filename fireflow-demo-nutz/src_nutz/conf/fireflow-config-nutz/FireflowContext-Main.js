/**
 * 
 * 
 */
var ioc = {
		fireflowRuntimeContext:{
			type:"org.fireflow.engine.context.RuntimeContext",
			factory:"org.fireflow.engine.context.RuntimeContext#getInstance",
			fields:{
				defaultEngineModules:{
					"org.fireflow.pvm.kernel.KernelManager":{refer:"fireflowKernelManager"},
					"org.fireflow.engine.modules.beanfactory.BeanFactory":{refer:"fireflowBeanFactoryNutzImpl"},
					"org.fireflow.engine.modules.event.EventBroadcasterManager":{refer:"fireflowEventBroadcasterManager"},
					"org.fireflow.engine.modules.persistence.PersistenceService":{refer:"nutzPersistenceService"},
					"org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy":{refer:"fireflowDefaultProcessLoadStrategy"},
					"org.fireflow.engine.modules.ousystem.OUSystemConnector":{refer:"ouSystemConnectorEmptyImpl"},
					"org.fireflow.engine.modules.formsystem.FormSystemConnector":{refer:"formSystemConnectorEmptyImpl"},
					"org.fireflow.engine.modules.calendar.CalendarService":{refer:"fireflowDefaultCalendarService"}
				},
				processLanguages:[{refer:"fireflowProcessDefinitionLanguageManager"}]
			},
			events : {
				create : 'initialize'
			}

		}
}