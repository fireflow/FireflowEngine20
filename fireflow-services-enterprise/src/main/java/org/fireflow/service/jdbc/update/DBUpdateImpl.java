package org.fireflow.service.jdbc.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.service.jdbc.query.DBQueryServiceDef;
import org.firesoa.common.schema.SQLSchemaGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DBUpdateImpl implements DBUpdate {
	private static final Log log = LogFactory.getLog(DBUpdateImpl.class);

	DBUpdateServiceDef service = null;
	public void setDBUpdateService(DBUpdateServiceDef svc){
		this.service = svc;
	}
	
	public DBUpdateServiceDef getDBUpdateService(){
		return this.service;
	}
	public void doUpdate(Document updateDoc) throws ServiceInvocationException {
		log.debug("The sql is '"+service.getSQL()+"'");
		
		Element rootElement = updateDoc.getDocumentElement();

		List<Element> setFieldsElement = new ArrayList<Element>();
		List<Element> whereFieldsElement =new ArrayList<Element>();
		
		NodeList set_where_nodeList = rootElement.getChildNodes();
		
		for (int i=0;i<set_where_nodeList.getLength();i++){
			Node childNode = set_where_nodeList.item(i);
			if (childNode.getNodeType()==Node.ELEMENT_NODE && childNode.getLocalName().equals(SQLSchemaGenerator.SET_ELEMENT)){
				NodeList sets_nodeList = childNode.getChildNodes();
				for (int j=0;j<sets_nodeList.getLength();j++){
					Node setNode = sets_nodeList.item(j);
					if (setNode.getNodeType()==Node.ELEMENT_NODE){
						setFieldsElement.add((Element)setNode);
					}
				}
			}else if (childNode.getNodeType()==Node.ELEMENT_NODE && childNode.getLocalName().equals(SQLSchemaGenerator.WHERE_ELEMENT)){
				NodeList wheres_nodeList = childNode.getChildNodes();
				for (int k=0;k<wheres_nodeList.getLength();k++){
					Node whereNode = wheres_nodeList.item(k);
					if (whereNode.getNodeType()==Node.ELEMENT_NODE){
						whereFieldsElement.add((Element)whereNode);
					}
				}
			}
		}
		
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			DataSource ds = service.getDataSource();
			con = ds.getConnection();
			
			pstmt = con.prepareStatement(service.getSQL());
			
			int nextStartIndex = DBUpdateServiceDef.fulfillPreparedStatement(pstmt,setFieldsElement,1);
			DBUpdateServiceDef.fulfillPreparedStatement(pstmt,whereFieldsElement,nextStartIndex);
			
			pstmt.execute();
			
		}catch(SQLException e){
			throw new ServiceInvocationException(e);
		}catch(Exception e){
			throw new ServiceInvocationException(e);
		}
		finally{
			try{

				if (pstmt!=null){
					pstmt.close();
				}
				if (con!=null){
					con.close();
				}
			}catch(SQLException ex){
				
			}
		}

	}


}
