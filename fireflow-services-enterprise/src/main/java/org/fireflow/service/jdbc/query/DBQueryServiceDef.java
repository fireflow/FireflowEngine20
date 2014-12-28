package org.fireflow.service.jdbc.query;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.jdbc.AbstractDBService;
import org.firesoa.common.schema.SQLSchemaGenerator;

public class DBQueryServiceDef extends AbstractDBService implements ServiceDef {

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface
				.setInterfaceClassName("org.fireflow.service.jdbc.query.DBQuery");
		javaInterface.putParameterTypeMap("doQuery",0,
				new QName(this.getTargetNamespaceUri(), SQLSchemaGenerator.WHERE_ELEMENT));
		javaInterface.putParameterTypeMap("doQuery",-1,
				new QName(this.getTargetNamespaceUri(), SQLSchemaGenerator.DATA_SET_ELEMENT));

		this.setInterface(javaInterface);

		this.invokerClassName = "org.fireflow.service.jdbc.query.DBQueryInvoker";

	}



}
