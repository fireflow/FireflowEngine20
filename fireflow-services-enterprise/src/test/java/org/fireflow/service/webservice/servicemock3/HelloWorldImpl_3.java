package org.fireflow.service.webservice.servicemock3;

import javax.jws.WebService;

@WebService(endpointInterface = "org.fireflow.service.webservice.servicemock3.HelloWorld_3",
        serviceName = "HelloWorldService",portName="HelloWorldPort")
public class HelloWorldImpl_3 implements HelloWorld_3 {

	public String sayHello(Person2 p) throws Exception {
		if (p.getName().equals("Nobody")){
			throw new Exception("Your name must NOT be 'Nobody'");
		}else{
			return "Hello ,Mr/Ms "+p.getName()+".";
		}
	}

}
