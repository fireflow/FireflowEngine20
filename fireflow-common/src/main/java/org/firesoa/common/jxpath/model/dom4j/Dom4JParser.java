package org.firesoa.common.jxpath.model.dom4j;

import java.io.InputStream;

import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.xml.XMLParser2;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class Dom4JParser extends XMLParser2 {

	
	@Override
	public Object parseXML(InputStream stream) {
		SAXReader reader = new SAXReader();
		reader.setIgnoreComments(this.isIgnoringComments());
		reader.setValidation(this.isValidating());
		reader.setStripWhitespaceText(this.isIgnoringElementContentWhitespace());
		try{
			Document doc = reader.read(stream);
			return doc;
		}catch(DocumentException e){
			throw new JXPathException("Dom4J parser error",e);
		}

	}
}
