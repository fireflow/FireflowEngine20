package org.fireflow.service.webservice.servicemock;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HelloWorld_1 {
	public String sayHello(@WebParam(name="person",partName="person")Person p)throws Exception;
}
