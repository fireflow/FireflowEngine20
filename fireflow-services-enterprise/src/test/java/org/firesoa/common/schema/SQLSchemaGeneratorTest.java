package org.firesoa.common.schema;

import java.sql.Connection;

import javax.sql.DataSource;
import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@SuppressWarnings("unused")
//hibernate
@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class SQLSchemaGeneratorTest extends AbstractJUnit4SpringContextTests{

	@Test
	public void testGenerateSchema()throws Exception {
		DataSource ds = (DataSource)this.applicationContext.getBean("MyDataSource");
		Connection conn = ds.getConnection();
		String nsUri = "http://test/";
		
		///查询
		String sql = "select * from t_ff_rt_workitem where id=? and (owner_id like ? or state=? )";

		XmlSchemaCollection schemaCollection = 
			SQLSchemaGenerator.generateXmlSchemaCollectionForSQL(sql, nsUri, conn);
		XmlSchema[] schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.DATA_SET_ELEMENT)));
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.ROW_ELEMENT)));
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.WHERE_ELEMENT)));
				}
			}
		}
		
		//删除
		sql = "Delete from t_ff_rt_workitem where id=? and (owner_id like ? or state=? )";

		schemaCollection = 
			SQLSchemaGenerator.generateXmlSchemaCollectionForSQL(sql, nsUri, conn);
		schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.WHERE_ELEMENT)));
				}
			}
		}
		
		//更新
		sql = "update t_ff_rt_workitem set created_time=?,end_time=?,owner_id='testUser' , owner_type=?  where id='123' and (owner_id like ? or created_time>? or state=? )";

		schemaCollection = 
			SQLSchemaGenerator.generateXmlSchemaCollectionForSQL(sql, nsUri, conn);
		schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.WHERE_ELEMENT)));
					Assert.assertNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.SET_ELEMENT)));
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.UPDATE_ELEMENT)));
				}
			}
		}
		
		//插入
		sql = "insert into t_ff_rt_variable(ID,SCOPE_ID,NAME,PROCESS_ELEMENT_ID,HEADERS,DATA_TYPE,PAYLOAD,PROCESS_ID,VERSION,PROCESS_TYPE) values(?,?,?,?,?,?,?,?,?,?)";

		schemaCollection = 
			SQLSchemaGenerator.generateXmlSchemaCollectionForSQL(sql, nsUri, conn);
		schemas = schemaCollection.getXmlSchemas();
		if (schemas!=null){
			for (XmlSchema schema :schemas){
				if (schema.getTargetNamespace().equals(nsUri)){
					schema.write(System.out);
					Assert.assertNotNull(schema.getElementByName(new QName(nsUri,SQLSchemaGenerator.VALUES_ELEMENT)));
				}
			}
		}
		
		sql = "insert into t_ff_rt_variable values(?,?,?,?,?,?,?,?,?,?)";
		try{
			schemaCollection = 
				SQLSchemaGenerator.generateXmlSchemaCollectionForSQL(sql, nsUri, conn);

		}catch(Exception e){
			
			System.out.println("不支持的Insert格式: "+e.getMessage());
		}
	
	}

}
