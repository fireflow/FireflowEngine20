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
package org.fireflow.client.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.fireflow.client.query.Criterion;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.firesoa.common.util.JavaDataTypeConvertor;
import org.junit.Test;



/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowQueryImplTest {

	@Test
	public void testMarshal_Unmarshal()throws JAXBException,UnsupportedEncodingException{
		WorkflowQueryImpl query = new WorkflowQueryImpl(ActivityInstance.class);
		query.add(Restrictions.eq(ActivityInstanceProperty.ID, "id-123"))
			.add(Restrictions.isNotNull(ActivityInstanceProperty.NAME))
			.add(Restrictions.in(ActivityInstanceProperty.PROCESS_TYPE, new Object[]{"FPDL","XPDL"}))
			.add(Restrictions.between(ActivityInstanceProperty.CREATED_TIME, new Date(), new Date()))
			.add(Restrictions.and(
					Restrictions.ge(ActivityInstanceProperty.STATE, 10), 
					Restrictions.ne(ActivityInstanceProperty.NODE_ID, 100)))
			.addOrder(Order.asc(ActivityInstanceProperty.EXPIRED_TIME))
			.addOrder(Order.desc(ActivityInstanceProperty.PROCESS_DISPLAY_NAME));
		System.out.println(query);
        System.out.println("=============================================");
		JAXBContext jc = JAXBContext.newInstance(WorkflowQueryImpl.class); 
		
        Marshaller marshaller=jc.createMarshaller(); 
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        marshaller.marshal( query,byteOut); 
        
        String xml = byteOut.toString("UTF-8");
        System.out.println(xml);
        
        ByteArrayInputStream inStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object obj = unmarshaller.unmarshal(inStream);
        System.out.println("=============================================");
        System.out.println(obj);
        
        Assert.assertTrue(obj instanceof WorkflowQueryImpl);
        WorkflowQueryImpl newQuery = (WorkflowQueryImpl)obj;
        Assert.assertNotNull(newQuery.getEntityClass());
        Assert.assertEquals(ActivityInstance.class, newQuery.getEntityClass());
        Assert.assertNotNull(newQuery.getAllOrders());
        Assert.assertEquals(2, newQuery.getAllOrders().size());
        Order order = (Order)newQuery.getAllOrders().get(0);
        Assert.assertEquals(ActivityInstanceProperty.EXPIRED_TIME, order.getEntityProperty());
        
        order = (Order)newQuery.getAllOrders().get(1);
        Assert.assertEquals(ActivityInstanceProperty.PROCESS_DISPLAY_NAME, order.getEntityProperty());
        
        Assert.assertNotNull(newQuery.getAllCriterions());
        Assert.assertEquals(5, newQuery.getAllCriterions().size());
        
        List criterionList = newQuery.getAllCriterions();
        Criterion c = (Criterion)criterionList.get(0);
        Assert.assertTrue(c.getEntityProperty().equals(ActivityInstanceProperty.ID));
        Assert.assertEquals(Criterion.OPERATION_EQ, c.getOperation());
        Assert.assertEquals("id-123", c.getValues()[0]);
        
        c = (Criterion)criterionList.get(1);
        Assert.assertTrue(c.getEntityProperty().equals(ActivityInstanceProperty.NAME));
        Assert.assertEquals(Criterion.OPERATION_IS_NOT_NULL, c.getOperation());

        
        c = (Criterion)criterionList.get(2);
        Assert.assertTrue(c.getEntityProperty().equals(ActivityInstanceProperty.PROCESS_TYPE));
        Assert.assertEquals(Criterion.OPERATION_IN, c.getOperation());
        Assert.assertEquals(2, c.getValues().length);
        Assert.assertEquals("FPDL", c.getValues()[0]);
        Assert.assertEquals("XPDL", c.getValues()[1]);
        
        c = (Criterion)criterionList.get(3);
        Assert.assertTrue(c.getEntityProperty().equals(ActivityInstanceProperty.CREATED_TIME));
        Assert.assertEquals(Criterion.OPERATION_BETWEEN, c.getOperation());
        Assert.assertEquals(2, c.getValues().length);
        Assert.assertTrue((JavaDataTypeConvertor.isDate(c.getValues()[0].getClass().getName())));
        Assert.assertTrue((JavaDataTypeConvertor.isDate(c.getValues()[1].getClass().getName())));
        
        
        c = (Criterion)criterionList.get(4);
        Assert.assertNull(c.getEntityProperty());
        Assert.assertEquals(Criterion.OPERATION_AND, c.getOperation());
        Assert.assertNotNull(c.getValues());
        Assert.assertEquals(2, c.getValues().length);
	}
}
