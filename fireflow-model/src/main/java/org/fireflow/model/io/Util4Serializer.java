/**
 * Copyright 2007-2011 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */

package org.fireflow.model.io;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.fireflow.model.misc.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/** 
 * Dom4J XPDL Serializer utility class.
 * @author 非也 nychen2000@163.com
 * @version 2.0
 */
public class Util4Serializer{

    /* ISO standard date format. */
    private static final DateFormat STANDARD_DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    /** 
     * Noop constructor. 
     *
     */
    private Util4Serializer(){
        // no op
    }

    /** 
     * Add a child element with the specific name to the given parent
     * element and return the child element.  This method will use the
     * namespace of the parent element for the child element's namespace.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @return The child element
     */
    public static Element addElement(Element parent, String name){
    	Document doc = parent.getOwnerDocument();
    	String qualifiedName = name;
    	if (!StringUtils.isEmpty(parent.getPrefix())){
    		qualifiedName = parent.getPrefix()+":"+name;
    	}
    	Element child = doc.createElementNS(parent.getNamespaceURI(), qualifiedName);
        parent.appendChild(child);
        return child;
    }

    public static Element addElement(Element parent,QName qname){
    	Document doc = parent.getOwnerDocument();
    	
    	String qualifiedName = qname.getLocalPart();
    	if (!StringUtils.isEmpty(qname.getPrefix())){
    		qualifiedName = qname.getPrefix()+":"+qname.getLocalPart();
    	}
    	Element child = doc.createElementNS(parent.getNamespaceURI(), qualifiedName);
        parent.appendChild(child);
    	return child;
    }
    
    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    public static Element addElement(Element parent, String name, Date value){
        return addElement(parent, name, value, null);
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is used.  
     * If the value is null then this method will not add the child
     * element and will return null.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    public static Element addElement(Element parent, String name, Date value,
    Date defaultValue){
    	Document doc = parent.getOwnerDocument();
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(parent, name);
            child.appendChild(doc.createTextNode(STANDARD_DF.format(value)));
        }

        return child;
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    public static Element addElement(Element parent, String name, String value){
        return addElement(parent, name, value, null);
    }

    /**
     * 
     * @param parent
     * @param name
     * @param value
     * @return
     */
//    public static Element addElement(Element parent,String name,CDATA value){
//
//      Element child = null;
//
//      child = addElement(parent, name);
//      child.add(value);
//
//      return child;
//
//    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    public static Element addElement(Element parent, String name, String value,
    String defaultValue){
    	Document doc = parent.getOwnerDocument();
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(parent, name);
            child.appendChild(doc.createTextNode(value));
        }

        return child;
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    public static Element addElement(Element parent, String name, URL value){
        return addElement(parent, name, value, null);
    }

    /**
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    public static Element addElement(Element parent, String name, URL value,
    URL defaultValue){
    	Document doc = parent.getOwnerDocument();
        Element child = null;

        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(parent, name);
            child.appendChild(doc.createTextNode(value.toString()));
        }

        return child;
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @return The child element
     */
    public static Element addElement(Element parent, String name, Duration value){
        return addElement(parent, name, value, null);
    }

    /** 
     * Add a child element with the specific name and the given value to
     * the given parent element and return the child element.  This method
     * will use the namespace of the parent element for the child element's
     * namespace.  If the given value is null then the default value is
     * used.  If the value is null then this method will not add the child
     * element and will return null.
     * 
     * @param parent The parent element
     * @param name The new child element name
     * @param value The value
     * @param defaultValue The default value (if the value is null)
     * @return The child element
     */
    public static Element addElement(Element parent, String name, Duration value,
    Duration defaultValue){
        Element child = null;
        Document doc = parent.getOwnerDocument();
        if(value == null){
            value = defaultValue;
        }

        if(value != null){
            child = addElement(parent, name);
            child.appendChild(doc.createTextNode(value.toString()));
        }

        return child;
    }
    /**
     * 
     * @param element
     * @param name
     * @return
     */
//    public static Element child(Element element, String name) {
//        return element.element(new QName(name, element.getNamespace()));
//    }

    /** 
     * Return the child elements with the given name.  The elements must be in
     * the same name space as the parent element.
     * @param element The parent element
     * @param name The child element name
     * @return The child elements
     */
//    @SuppressWarnings("unchecked")
//	public static List children(Element element, String name) {
//        return element.elements(new QName(name, element.getNamespace()));
//    }

    // Conversion

    /** 
     * Return the value of the child element with the given name.  The element
     * must be in the same name space as the parent element.
     * 
     * @param element The parent element
     * @param name The child element name
     * @return The child element value
     */
//    public static String elementAsString(Element element, String name) {
//        String s = element.elementTextTrim(
//            new QName(name, element.getNamespace()));
//        return (s == null || s.length() == 0) ? null : s;
//    }

    /**
     * 
     * @param element
     * @param name
     * @return
     * @throws DeserializerException
     */
//    public static Date elementAsDate(Element element, String name) throws
//        DeserializerException {
//        String text = elementAsString(element, name);
//        if (text == null) {
//            return null;
//        }
//
//        try {
//            return DateUtilities.getInstance().parse(text);
//            //return STANDARD_DF.parse(text);
//        } catch (ParseException e) {
//            throw new DeserializerException("Error parsing date: " + text, e);
//        }
//    }
//
//    /**
//     * 
//     * @param element
//     * @param name
//     * @return
//     */
//    public static int elementAsInteger(Element element, String name) {
//        String text = elementAsString(element, name);
//        if (text == null) {
//            return 0;
//        }
//
//        return Integer.parseInt(text);
//    }
}
