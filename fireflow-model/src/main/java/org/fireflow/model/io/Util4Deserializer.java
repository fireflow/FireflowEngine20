
package org.fireflow.model.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Util4Deserializer {

    /**
     * 私有构造方法
     */
    private Util4Deserializer() {
        // no op
    }

    /** 
     * Return the child element with the given name.  The element must be in
     * the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child element
     */
    public static Element child(Element element, String name) {
    	if (element==null)return null;
    	Element child = child(element,new QName(element.getNamespaceURI(),name));
        return child;
    }
    
    public static Element child(Element element,QName qName){
    	if (element==null){
    		return null;
    	}
    	
    	NodeList nodeList = element.getChildNodes();//element.getElementsByTagNameNS(qName.getNamespaceURI(), qName.getLocalPart());
    	if (nodeList!=null){
    		int length = nodeList.getLength();
    		for (int i = 0;i<length;i++){
    			Node node = nodeList.item(i);
    			if (node.getNodeType()==Node.ELEMENT_NODE 
    					&& equalStrings(node.getNamespaceURI(),qName.getNamespaceURI())
    					&& equalStrings(node.getLocalName(),qName.getLocalPart())){
    				return (Element)node;
    			}
    		}
    	}
    	return null;
    }
    
    

    /** 
     * Return the child elements with the given name.  The elements must be in
     * the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child elements
     */
    @SuppressWarnings("unchecked")
	public static List<Element> children(Element element, String name) {
        if (element==null){
            return null;
        }

        NodeList nodeList = element.getChildNodes();//element.getElementsByTagNameNS(element.getNamespaceURI(), name);

        List<Element> result = new ArrayList<Element>();
        QName qName = new QName(element.getNamespaceURI(),name);
        if (nodeList!=null){
        	int length = nodeList.getLength();
        	for (int i=0;i<length;i++){
        		Node node = nodeList.item(i);        		
    			if (node.getNodeType()==Node.ELEMENT_NODE && equalStrings(node.getNamespaceURI(),qName.getNamespaceURI())
    					&& equalStrings(node.getLocalName(),qName.getLocalPart())){
    				result.add((Element)node);
    			}
        	}
        }
        return result;
    }

    // Conversion

    /** 
     * Return the value of the child element with the given name.  The element
     * must be in the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child element value
     */
    public static String elementAsString(Element element, String name) {
    	Element child = child(element,name);
    	if (child!=null){
    		return child.getTextContent();
    	}
    	return null;
    }

    /**
     * @param element
     * @param name
     * @return
     * @throws DeserializerException
     */
    public static Date elementAsDate(Element element, String name) throws
        DeserializerException {
        String text = elementAsString(element, name);
        if (text == null) {
            return null;
        }

        try {
            return DateUtilities.getInstance().parse(text);
        } catch (ParseException e) {
            throw new DeserializerException("Error parsing date: " + text, e);
        }
    }

    /**
     * @param element
     * @param name
     * @return
     */
    public static int elementAsInteger(Element element, String name) {
        String text = elementAsString(element, name);
        if (text == null) {
            return 0;
        }

        return Integer.parseInt(text);
    }

    /**
     * @param element
     * @param name
     * @return
     */
    public static boolean elementAsBoolean(Element element, String name) {
        String text = elementAsString(element, name);
        if (text == null) {
            return false;
        }

        return new Boolean(text).booleanValue();
    }

    /**
     * @param element
     * @param name
     * @return
     * @throws DeserializerException
     */
    public static URL elementAsURL(Element element, String name) throws
        DeserializerException {
        String text = elementAsString(element, name);
        if (text == null) {
            return null;
        }

        try {
            return new URL(text);
        } catch (MalformedURLException e) {
            throw new DeserializerException("Invalid URL: " + text, e);
        }
    }

    
	private static boolean equalStrings(String s1, String s2) {
		if (s1 == s2) {
			return true;
		}
		s1 = s1 == null ? "" : s1.trim();
		s2 = s2 == null ? "" : s2.trim();
		return s1.equals(s2);
	}

}
