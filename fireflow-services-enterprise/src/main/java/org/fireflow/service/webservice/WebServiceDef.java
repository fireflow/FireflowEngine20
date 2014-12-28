package org.fireflow.service.webservice;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.AbstractServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.ibm.wsdl.factory.WSDLFactoryImpl;

public class WebServiceDef extends AbstractServiceDef implements ServiceDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3168927065098105863L;
	
	private static final Log log = LogFactory.getLog(WebServiceDef.class);
	protected static final String SCHEMA_FILE_PREFIX = "wsdl_schema_";

	protected static final String CLASSPATH_URL_PREFIX = "classpath:";

	protected String wsdlURLString = null;

	protected Definition wsdlDefinition = null;

	protected String portName = null;

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public String getPortName() {
		return this.portName;
	}

	public String getWsdlURL() {

		return wsdlURLString;
	}

	public void setWsdlURL(String wsdlURL) {
		this.wsdlURLString = wsdlURL;
	}

	public Definition getWsdlDefinition() {
		return wsdlDefinition;
	}

	protected Definition resolveWsdlDefinition(String urlStr)
			throws InvalidModelException {
		String _urlString = urlStr;
		if (urlStr.toLowerCase().startsWith(CLASSPATH_URL_PREFIX)) {
			String tmpStr = urlStr.substring(CLASSPATH_URL_PREFIX.length());
			URL url = WebServiceDef.class.getResource(tmpStr);
			if (url == null) {
				throw new InvalidModelException(
						"The wsdl can NOT be found at path[" + urlStr + "]");
			}

			_urlString = url.toString();
		}

		WSDLFactory factory = new WSDLFactoryImpl();
		WSDLReader reader = factory.newWSDLReader();
		Definition implDef = null;
		try {
			implDef = reader.readWSDL(_urlString);
		} catch (WSDLException e) {
			log.error("Can NOT read wsdl from " + _urlString, e);
			throw new InvalidModelException("Can NOT read wsdl from path["
					+ urlStr + "]", e);
		}

		return implDef;
	}


	public void afterPropertiesSet() throws InvalidModelException {
		this.invokerClassName = "org.fireflow.service.webservice.WebServiceCallerInvoker";

		if (!StringUtils.isEmpty(wsdlURLString)) {
			wsdlDefinition = resolveWsdlDefinition(wsdlURLString);
		} else {
			throw new InvalidModelException(
					"The wsdl url of a web service can NOT be empty.");
		}
		if (wsdlDefinition != null) {
			// 1、构建interface
			buildInterfaceInfo();

			// 2、构建XmlSchemaCollection
			buildSchemaInfo();
		}
	}

	private void buildSchemaInfo() {
		XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
		URIResolver originalURIResolver = schemaCollection.getSchemaResolver();
		WsdlSchemaURIResolver uriResolver = new WsdlSchemaURIResolver();
		schemaCollection.setSchemaResolver(uriResolver);
		schemaCollection.setBaseUri(wsdlDefinition.getDocumentBaseURI());
		Types types = wsdlDefinition.getTypes();
		if (types != null) {
			Vector<Schema> schemaExtElemList = findExtensibilityElement(types
					.getExtensibilityElements());
			for (int i = 0; i < schemaExtElemList.size(); i++) {
				Schema schemaElement = schemaExtElemList.elementAt(i);
				Element w3cSchemaElement = schemaElement.getElement();
				//Map imports = schemaElement.getImports();
				schemaCollection.read(w3cSchemaElement, SCHEMA_FILE_PREFIX + i
						+ ".xsd");
//				 schemaCollection.read(w3cSchemaElement);
			}
		}

		this.setXmlSchemaCollection(schemaCollection);
	}

	private static Vector<Schema> findExtensibilityElement(
			List extensibilityElements) {
		Vector<Schema> elements = new Vector<Schema>();
		if (extensibilityElements != null) {
			Iterator iter = extensibilityElements.iterator();
			while (iter.hasNext()) {
				ExtensibilityElement elment = (ExtensibilityElement) iter
						.next();
				if (elment instanceof Schema) {
					elements.add((Schema) elment);
				}
			}
		}
		return elements;
	}

	private void buildInterfaceInfo() {

		String targetNsUri = wsdlDefinition.getTargetNamespace();
		QName serviceQName = new QName(targetNsUri, this.getName());
		javax.wsdl.Service wsdlService = wsdlDefinition
				.getService(serviceQName);
		javax.wsdl.Port wsdlPort = wsdlService.getPort(this.getPortName());

		javax.wsdl.PortType wsdlPortType = wsdlPort.getBinding().getPortType();

		QName portTypeQName = wsdlPortType.getQName();

		CommonInterfaceDef commonInterface = new CommonInterfaceDef();
//		commonInterface.setNamespaceUri(portTypeQName.getNamespaceURI());
		commonInterface.setName(portTypeQName.getLocalPart());

		List wsdlOperationList = wsdlPortType.getOperations();
		for (int i = 0; i < wsdlOperationList.size(); i++) {
			Operation wsdlOp = (Operation) wsdlOperationList.get(i);
			OperationDefImpl op = new OperationDefImpl();
			op.setOperationName(wsdlOp.getName());

			Input wsdlInput = wsdlOp.getInput();
			Message wsdlInputMessage = wsdlInput.getMessage();
			List<Part> wsdlMessageParts = wsdlInputMessage
					.getOrderedParts(null);
			for (Part part : wsdlMessageParts) {
				InputImpl input = new InputImpl();
				input.setName(part.getName());
				input.setDisplayName(part.getName());
				if (part.getElementName() != null) {
					input.setDataType(part.getElementName());
				} else {
					input.setDataType(part.getTypeName());
				}

				op.getInputs().add(input);
			}

			Output wsdlOutput = wsdlOp.getOutput();
			Message wsdlOutputMessage = wsdlOutput.getMessage();
			List<Part> wsdlOutputParts = wsdlOutputMessage
					.getOrderedParts(null);
			for (Part part : wsdlOutputParts) {
				OutputImpl output = new OutputImpl();
				output.setName(part.getName());
				if (part.getElementName() != null) {
					output.setDataType(part.getElementName());
				} else {
					part.getTypeName();
				}

				op.getOutputs().add(output);
			}

			commonInterface.getOperations().add(op);
		}

		this.setInterface(commonInterface);
	}

}

class WsdlSchemaURIResolver extends DefaultURIResolver {
	public InputSource resolveEntity(String namespace, String schemaLocation,
			String baseUri) {
		if (baseUri != null
				&& baseUri.startsWith(WebServiceDef.SCHEMA_FILE_PREFIX)) {
			String ref = null;
			return this.resolveEntity(namespace, schemaLocation, this.getCollectionBaseURI());
		} else if (baseUri != null) {
			try {
				File baseFile = null;
				try {
					URI uri = new URI(baseUri);
					baseFile = new File(uri);
					if (!baseFile.exists()) {
						baseFile = new File(baseUri);
					}
				} catch (Throwable ex) {
					baseFile = new File(baseUri);
				}
				if (baseFile != null && baseFile.exists()) {
					baseUri = baseFile.toURI().toString();
				} else if (this.getCollectionBaseURI() != null) {
					baseFile = new File(this.getCollectionBaseURI());
					if (baseFile.exists()) {
						baseUri = baseFile.toURI().toString();
					}
				}

				String ref = new URL(new URL(baseUri), schemaLocation)
						.toString();

				return new InputSource(ref);
			} catch (MalformedURLException e1) {
				throw new RuntimeException(e1);
			}

		}
		return new InputSource(schemaLocation);

	}
}
