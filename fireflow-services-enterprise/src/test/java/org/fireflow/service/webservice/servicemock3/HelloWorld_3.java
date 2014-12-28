package org.fireflow.service.webservice.servicemock3;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HelloWorld_3 {
	public String sayHello(@WebParam(name="person",partName="person")Person2 p)throws Exception;
}
