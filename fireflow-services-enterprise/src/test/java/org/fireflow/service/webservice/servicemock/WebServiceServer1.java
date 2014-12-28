/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.service.webservice.servicemock;

import javax.xml.ws.Endpoint;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WebServiceServer1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HelloWorldImpl_1 helloWorld = new HelloWorldImpl_1();
		String address = "http://localhost:9001/HelloWorld";
		Endpoint endpoint = Endpoint.publish(address, helloWorld);
		
		System.out.println("WebService 发布在 "+address);
	}

}
