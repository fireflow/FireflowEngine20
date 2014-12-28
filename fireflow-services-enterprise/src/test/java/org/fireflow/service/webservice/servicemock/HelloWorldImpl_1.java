package org.fireflow.service.webservice.servicemock;

import javax.jws.WebService;

@WebService(endpointInterface = "org.fireflow.service.webservice.servicemock.HelloWorld_1",
        serviceName = "HelloWorldService",portName="HelloWorldPort")
public class HelloWorldImpl_1 implements HelloWorld_1 {

	public String sayHello(Person p) throws Exception {
		if (p==null){
			System.out.println("person object is null");
			return "person object is null";
		}else if ("Nobody".equals(p.getName())){
			System.out.println("Your name must NOT be 'Nobody'");
			throw new Exception("Your name must NOT be 'Nobody'");
		}else{
			System.out.println("Hello ,Mr/Ms "+p.getName()+".");
			return "Hello ,Mr/Ms "+p.getName()+".";
		}
	}

}
