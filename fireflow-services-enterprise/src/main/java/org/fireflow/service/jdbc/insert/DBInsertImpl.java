package org.fireflow.service.jdbc.insert;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DBInsertImpl implements DBInsert {
		private static final Log log = LogFactory.getLog(DBInsertImpl.class);
		
	DBInsertServiceDef service = null;
	public void setDBInsertService(DBInsertServiceDef svc ){
		this.service = svc;
	}
	public DBInsertServiceDef getDBInsertService(){
		return service;
	}
	public void doInsert(Document valuesDoc) throws ServiceInvocationException {
		log.debug("The sql is '"+service.getSQL()+"'");
		
		Element valuesElement = valuesDoc.getDocumentElement();
		List<Element> valueFieldsElement = new ArrayList<Element>();
		NodeList nodeList = valuesElement.getChildNodes();
		for (int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE){
				valueFieldsElement.add((Element)node);
			}
		}
		
		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			DataSource ds = service.getDataSource();
			con = ds.getConnection();
			
			pstmt = con.prepareStatement(service.getSQL());
			DBQueryServiceDef.fulfillPreparedStatement(pstmt,valueFieldsElement,1);
			
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
