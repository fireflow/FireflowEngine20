package org.fireflow.service.jdbc.update;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.jdbc.AbstractDBService;
import org.firesoa.common.schema.SQLSchemaGenerator;

public class DBUpdateServiceDef extends AbstractDBService {

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface
				.setInterfaceClassName("org.fireflow.service.jdbc.update.DBUpdate");
		javaInterface.putParameterTypeMap("doUpdate",0,
				new QName(this.getTargetNamespaceUri(), SQLSchemaGenerator.UPDATE_ELEMENT));

		this.setInterface(javaInterface);

		this.invokerClassName = "org.fireflow.service.jdbc.update.DBUpdateInvoker";

	}
	

}
