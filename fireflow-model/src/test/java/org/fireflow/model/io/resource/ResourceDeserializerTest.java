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

import java.io.InputStream;
import java.util.List;

import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.ResourceType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ResourceDeserializerTest {

	/**
	 * Test method for {@link org.fireflow.model.io.resource.ResourceDeserializer#deserialize(java.io.InputStream)}.
	 */
	@Test
	public void testDeserialize() throws Exception{
		InputStream in = ResourceDeserializerTest.class.getResourceAsStream("resourcedef.xml");
		List<ResourceDef> resourceList2 = ResourceDeserializer.deserialize(in);
		Assert.assertNotNull(resourceList2);
		Assert.assertEquals(5, resourceList2.size());
		
		ResourceDef tmpResourceDef = findResource(resourceList2,"var_implement");
		Assert.assertNotNull(tmpResourceDef);
		Assert.assertEquals(ResourceType.VARIABLE_IMPLICATION, tmpResourceDef.getResourceType());
		Assert.assertEquals(1,tmpResourceDef.getExtendedAttributes().size());
		Assert.assertEquals("var1", tmpResourceDef.getValue());
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
