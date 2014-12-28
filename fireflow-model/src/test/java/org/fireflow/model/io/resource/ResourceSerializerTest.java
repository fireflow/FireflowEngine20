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
package org.fireflow.model.io.resource;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.impl.ResourceDefImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ResourceSerializerTest {

	/**
	 * Test method for {@link org.fireflow.model.io.resource.ResourceSerializer#serialize(java.util.List, java.io.OutputStream)}.
	 */
	@Test
	public void testSerialize() {
		List<ResourceDef> resourceList = new ArrayList<ResourceDef>();
		
		ResourceDef resourceDef = new ResourceDefImpl();
		resourceDef.setName("ProcessCreator");
		resourceDef.setDisplayName("流程创建者");
		resourceDef.setResourceType(ResourceType.PROCESS_INSTANCE_CREATOR);
		resourceList.add(resourceDef);
		
		resourceDef = new ResourceDefImpl();
		resourceDef.setName("Applicant");
		resourceDef.setDisplayName("申报人");
		resourceDef.setResourceType(ResourceType.PROCESS_INSTANCE_CREATOR);
		resourceDef.setValue("process1.main_flow.activity1");
		resourceList.add(resourceDef);
		
		resourceDef = new ResourceDefImpl();
		resourceDef.setName("var_implement");
		resourceDef.setDisplayName("流程变量");
		resourceDef.setResourceType(ResourceType.VARIABLE_IMPLICATION);
		resourceDef.setValue( "var1");
		resourceList.add(resourceDef);
		
		resourceDef = new ResourceDefImpl();
		resourceDef.setName("TempGroup1");
		resourceDef.setDisplayName("临时工作组1");
		resourceDef.setResourceType(ResourceType.CUSTOM);
		resourceDef.setResolverBeanName("resolver_bean_1");
		resourceList.add(resourceDef);
		
		resourceDef = new ResourceDefImpl();
		resourceDef.setName("TempGroup2");
		resourceDef.setDisplayName("临时工作组2");
		resourceDef.setResourceType(ResourceType.CUSTOM);
		resourceDef.setResolverClassName("com.fireflow.demo.TheResourceResolver");
		resourceList.add(resourceDef);

		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		try {
			ResourceSerializer.serialize(resourceList, out);
			byte[] bytes = out.toByteArray();
			
			java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes);
			
			List<ResourceDef> resourceList2 = ResourceDeserializer.deserialize(in);
			Assert.assertNotNull(resourceList2);
			Assert.assertEquals(resourceList.size(), resourceList2.size());
			
			ResourceDef tmpResourceDef = findResource(resourceList2,"var_implement");
			Assert.assertNotNull(tmpResourceDef);
			Assert.assertEquals(ResourceType.VARIABLE_IMPLICATION, tmpResourceDef.getResourceType());
			Assert.assertEquals(1,tmpResourceDef.getExtendedAttributes().size());
			Assert.assertEquals("var1", tmpResourceDef.getValue());
		} catch (SerializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeserializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private ResourceDef findResource(List<ResourceDef> resourceList,String resourceName){
		if (resourceList==null){
			return null;
		}
		for (ResourceDef resourceDef: resourceList){
			if (resourceDef.getName().equals(resourceName)){
				return resourceDef;
			}
		}
		return null;
	}

}
