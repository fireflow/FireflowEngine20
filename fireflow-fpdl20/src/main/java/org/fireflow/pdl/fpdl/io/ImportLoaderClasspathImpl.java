/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.pdl.fpdl.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.resource.ResourceDeserializer;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ImportLoaderClasspathImpl implements ImportLoader {

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.io.ImportLoader#loadResources(java.lang.String)
	 */
	public List<ResourceDef> loadResources(String resourceLocation) throws DeserializerException,IOException{
		if (resourceLocation==null || resourceLocation.trim().equals("")){
			return null;
		}
		String fileName = resourceLocation;
		if (resourceLocation.startsWith("/") || resourceLocation.startsWith("\\")){
			fileName = resourceLocation.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);	
		ResourceDeserializer parser = new ResourceDeserializer();
		
		return parser.deserialize(inStream);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.io.ImportLoader#loadServices(java.lang.String)
	 */
	public List<ServiceDef> loadServices(String serviceFileName) throws InvalidModelException,DeserializerException,IOException{
		if (serviceFileName==null || serviceFileName.trim().equals("")){
			return null;
		}
		String fileName = serviceFileName;
		if (serviceFileName.startsWith("/") || serviceFileName.startsWith("\\")){
			fileName = serviceFileName.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);	
		
		return ServiceParser.deserialize(inStream);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.io.ImportLoader#loadProcess(java.lang.String)
	 */
	public WorkflowProcess loadProcess(String processLocation)
			throws InvalidModelException,DeserializerException, IOException {
		if (processLocation==null || processLocation.trim().equals("")){
			return null;
		}
		String fileName = processLocation;
		if (processLocation.startsWith("/") || processLocation.startsWith("\\")){
			fileName = processLocation.substring(1);
		}
		
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);	
		FPDLDeserializer deserializer = new FPDLDeserializer();
		deserializer.setImportLoader(this);
		return deserializer.deserialize(inStream);
	}

}
