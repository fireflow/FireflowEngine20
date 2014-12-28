package org.fireflow.service.webservice;

import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.firesoa.common.schema.DOMInitializer;
import org.w3c.dom.Document;

public class WebServiceCallerImpl implements WebServiceCaller {
	private static final Log log = LogFactory.getLog(WebServiceCallerImpl.class);
	private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();	
	WebServiceDef ws = null;

	public void setWebService(WebServiceDef ws){
		this.ws = ws;
	}
	public WebServiceDef getWebService(){
		return ws;
	}
	/*
	public List<Document> callWebService(List<Document> messagePlayload) throws ServiceInvocationException {
		Definition wsdlDef = ws.getWsdlDefinition();
		String targetNsUri = wsdlDef.getTargetNamespace();
		QName serviceQName = new QName(targetNsUri,ws.getName());
		QName portQName = new QName(targetNsUri,ws.getPortName());
		
		String urlStr = ws.getWsdlURL();
		URL url = null;
		if (urlStr.toLowerCase().startsWith(WebService.CLASSPATH_URL_PREFIX)) {
			url = WebServiceCallerImpl.class.getResource(urlStr
					.substring(WebService.CLASSPATH_URL_PREFIX.length()));
		}else{
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e) {
				throw new ServiceInvocationException(e);
			}
		}
		
		if (url==null){
			throw new ServiceInvocationException("Invalid wsdl url '"+urlStr+"'");
		}
		
		javax.xml.ws.Service jawsService = javax.xml.ws.Service.create(url, serviceQName);
		Dispatch<DOMSource> dispatch = jawsService.createDispatch(portQName, DOMSource.class, javax.xml.ws.Service.Mode.PAYLOAD);
		
		Document thePayLoad = messagePlayload.get(0);//TODO 待处理
		
		DOMSource request = new DOMSource(thePayLoad);
		DOMSource response = dispatch.invoke(request);

		Document theResponsePayload = (Document)response.getNode();
		
		List<Document> result = new ArrayList<Document>();
		result.add(theResponsePayload);
		return result;
	}
	*/
	
	public Document callWebService(Document messagePlayload) throws ServiceInvocationException {
		Definition wsdlDef = ws.getWsdlDefinition();
		String targetNsUri = wsdlDef.getTargetNamespace();
		QName serviceQName = new QName(targetNsUri,ws.getName());
		QName portQName = new QName(targetNsUri,ws.getPortName());
		
		String urlStr = ws.getWsdlURL();
		URL url = null;
		if (urlStr.toLowerCase().startsWith(WebServiceDef.CLASSPATH_URL_PREFIX)) {
			url = WebServiceCallerImpl.class.getResource(urlStr
					.substring(WebServiceDef.CLASSPATH_URL_PREFIX.length()));
		}else{
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e) {
				throw new ServiceInvocationException(e);
			}
		}
		
		if (url==null){
			throw new ServiceInvocationException("Invalid wsdl url '"+urlStr+"'");
		}
		
		javax.xml.ws.Service jawsService = javax.xml.ws.Service.create(url, serviceQName);
		Dispatch<Source> dispatch = jawsService.createDispatch(portQName, Source.class, javax.xml.ws.Service.Mode.PAYLOAD);
				
		DOMSource domSource = new DOMSource(messagePlayload);

		
		log.debug("Call web service , servicename="+serviceQName+", portname="+portQName+", the soap message is: \n");
		log.debug(DOMInitializer.dom2String(messagePlayload));
		
		Source response = dispatch.invoke(domSource);
		
		DOMResult domResult = new DOMResult();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(response, domResult);	
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Couldn't parse response stream.", e);
		} catch (TransformerException e) {
			throw new RuntimeException("Couldn't parse response stream.", e);
		}
	
		Document theResponsePayload = (Document)domResult.getNode();
		log.debug("The response of the web service is :");
		log.debug(DOMInitializer.dom2String(theResponsePayload));
		return theResponsePayload;
	}
	
	private URL getURL()throws ServiceInvocationException{
		String urlStr = ws.getWsdlURL();
		URL url = null;
		if (urlStr.toLowerCase().startsWith(WebServiceDef.CLASSPATH_URL_PREFIX)) {
			url = WebServiceCallerImpl.class.getResource(urlStr
					.substring(WebServiceDef.CLASSPATH_URL_PREFIX.length()));
		}else{
			try {
				url = new URL(urlStr);
			} catch (MalformedURLException e) {
				throw new ServiceInvocationException(e);
			}
		}
		
		if (url==null){
			throw new ServiceInvocationException("Invalid wsdl url '"+urlStr+"'");
		}
		
		return url;
	}

}
