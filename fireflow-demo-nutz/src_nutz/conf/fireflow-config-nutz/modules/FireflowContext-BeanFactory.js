/**
 * 
 */
var ioc = {
		fireflowBeanFactoryNutzImpl:{
			type:"org.fireflow.engine.modules.beanfactory.nutz.BeanFactoryNutzImpl",
			fields:{
				ioc:{refer: '$Ioc'}
			}
		}
}