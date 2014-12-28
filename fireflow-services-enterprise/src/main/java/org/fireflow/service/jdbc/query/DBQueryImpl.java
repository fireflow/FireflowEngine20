package org.fireflow.service.jdbc.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.DocumentSource;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@WebServiceProvider
@ServiceMode(value=Service.Mode.PAYLOAD)
public class DBQueryImpl implements DBQuery, Provider<DOMSource> {
	private static final Log log = LogFactory.getLog(DBQueryImpl.class);
	
	DBQueryServiceDef service = null;
	
	public DBQueryServiceDef getDBQueryService() {
		return service;
	}


	public void setDBQueryService(DBQueryServiceDef service) {
		this.service = service;
	}


	public DOMSource invoke(DOMSource request) {
		try{

			Document message = doQuery((Document)request.getNode());
			return new DOMSource(message);
		}catch(ServiceInvocationException e){
			throw new WebServiceException(e);
		}
	}


	public Document doQuery(Document condition)
			throws ServiceInvocationException {
		log.debug("The sql is '"+service.getSQL()+"'");
		
		Element whereElement = condition.getDocumentElement();
		List<Element> whereFieldsElement = new ArrayList<Element>();
		NodeList nodeList = whereElement.getChildNodes();
		for (int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if (node.getNodeType()==Node.ELEMENT_NODE){
				whereFieldsElement.add((Element)node);
			}
		}
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			DataSource ds = service.getDataSource();
			con = ds.getConnection();
			
			pstmt = con.prepareStatement(service.getSQL());
			DBQueryServiceDef.fulfillPreparedStatement(pstmt,whereFieldsElement,1);
			
			rs = pstmt.executeQuery();
			
			Document doc = DBQueryServiceDef.createDataSet(rs,rs.getMetaData(),service.getTargetNamespaceUri());
			
			return doc;//
		}catch(SQLException e){
			throw new ServiceInvocationException(e);
		}catch(Exception e){
			throw new ServiceInvocationException(e);
		}
		finally{
			try{
				if (rs!=null){
					rs.close();
				}
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
