package org.fireflow.service.jdbc.delete;

//import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.jdbc.AbstractDBService;
import org.firesoa.common.schema.SQLSchemaGenerator;

public class DBDeleteServiceDef extends AbstractDBService {
	
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface
				.setInterfaceClassName("org.fireflow.service.jdbc.delete.DBDelete");
		javaInterface.putParameterTypeMap("doDelete",0,
				new QName(this.getTargetNamespaceUri(), SQLSchemaGenerator.WHERE_ELEMENT));

		this.setInterface(javaInterface);

		this.invokerClassName = "org.fireflow.service.jdbc.delete.DBDeleteInvoker";

	}

}
