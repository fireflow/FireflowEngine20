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
package org.fireflow.pdl.fpdl.import_mechanism;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.io.FPDLDeserializer;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ImportDeserializerTest  extends FireWorkflowJunitEnviroment{

	@Test
	public void testDeserializeImport(){
		FPDLDeserializer deserializer = new FPDLDeserializer();
		InputStream inStream = ImportDeserializerTest.class.getResourceAsStream("/org/fireflow/pdl/fpdl20/import_mechanism/TheSimplestHumanProcessTest.xml");
		
		try {
			WorkflowProcess process = deserializer.deserialize(inStream);
			
			List<ServiceDef> services = process.getServices();
			Assert.assertNotNull(services);
			Assert.assertEquals(3, services.size());
			
			ServiceParser.serialize(services, System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.FireWorkflowJunitEnviroment#createWorkflowProcess()
	 */
	@Override
	public WorkflowProcess createWorkflowProcess() {
		// TODO Auto-generated method stub
		return null;
	}

}
