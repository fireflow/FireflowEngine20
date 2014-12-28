package org.fireflow.service.jdbc.delete;

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

public class DBDeleteImpl implements DBDelete {
	private static final Log log = LogFactory.getLog(DBDeleteImpl.class);

	DBDeleteServiceDef service = null;

	public void doDelete(Document where) throws ServiceInvocationException {
		log.debug("The sql is '"+service.getSQL()+"'");
		
		//获得根节点
		Element whereElement = (Element)where.getDocumentElement();
		
		//获得所有where 条件节点
		List<Element> whereFieldsElement = new ArrayList<Element>();;
		NodeList whereNodeList = whereElement.getChildNodes();
		for (int i=0;i<whereNodeList.getLength();i++){
			Node childNode = whereNodeList.item(i);
			if (childNode.getNodeType()==Node.ELEMENT_NODE){
				whereFieldsElement.add((Element)childNode);
			}
		}

		Connection con = null;
		PreparedStatement pstmt = null;
		try{
			DataSource ds = service.getDataSource();
			con = ds.getConnection();
			
			pstmt = con.prepareStatement(service.getSQL());
			DBDeleteServiceDef.fulfillPreparedStatement(pstmt,whereFieldsElement,1);
			
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


	public void setDBDeleteService(DBDeleteServiceDef svc){
		this.service = svc;
	}
	
	public DBDeleteServiceDef getDBDeleteService(){
		return this.service;
	}
}
