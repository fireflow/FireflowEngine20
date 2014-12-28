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
package org.fireflow.service.webservice.servicemock3;

import javax.xml.ws.Endpoint;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WebServiceServer_3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HelloWorldImpl_3 helloWorld = new HelloWorldImpl_3();
		String address = "http://localhost:9002/HelloWorld";
		Endpoint endpoint = Endpoint.publish(address, helloWorld);
		
		try {
			Thread.currentThread().sleep(10*60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
