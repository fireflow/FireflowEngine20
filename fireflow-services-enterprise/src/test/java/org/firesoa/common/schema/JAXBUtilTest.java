package org.firesoa.common.schema;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.firesoa.common.schema.pojo_1.City;
import org.firesoa.common.schema.pojo_2.AnnotationedPerson1;
import org.firesoa.common.schema.pojo_2.childobj.Address;
import org.junit.Assert;
import org.junit.Test;


public class JAXBUtilTest {


	/**
	 * 在有package-info的情况下，检查qname及schema的正确性
	 */
	@Test
	public void testGeneratePojoSchema() {

		try{
		
			PojoSchema pojoSchema = JAXBUtil.generatePojoSchema(AnnotationedPerson1.class);
			Assert.assertNotNull(pojoSchema.getSchema());

			System.out.println(pojoSchema.getSchema());
			
			QName qname_2 = new QName("http://pojo_2.schema.common.firesoa.org/","AnnotationedPerson1");
			Assert.assertEquals(qname_2, pojoSchema.getQname());
			
			Assert.assertEquals(2, pojoSchema.getAllSchemas().size());
			
			Iterator<String> keys = pojoSchema.getAllSchemas().keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				System.out.println("===========schema file name is : "+key );
				System.out.println(pojoSchema.getAllSchemas().get(key));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 在有package-info.java的情况下，检查qname的正确性
	 */
	@Test
	public void testGeneratePojoQname() {
		try{
			QName qname = JAXBUtil.generatePojoQname(Address.class);
			QName qname_2 = new QName("http://childob.pojo_2.schema.common.firesoa.org/","address");
			Assert.assertEquals(qname_2, qname);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 在没有任何标注的情况下，检查qname的正确性
	 */
	@Test
	public void testGeneratePojoQname2() {
		try{
			QName qname = JAXBUtil.generatePojoQname(City.class);
//			QName qname_2 = new QName("http://childob.pojo_2.schema.common.firesoa.org/","address");
//			Assert.assertEquals(qname_2, qname);
			System.out.println(qname);
			
			PojoSchema pojoSchema = JAXBUtil.generatePojoSchema(City.class);
			System.out.println(pojoSchema.getSchema());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
