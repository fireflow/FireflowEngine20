/**
 * Copyright 2007-2010 非也
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
package org.firesoa.common.jxpath.model.dom4j;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.jxpath.JXPathAbstractFactoryException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.NamespaceResolver;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.compiler.ProcessingInstructionTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.util.TypeUtils;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.firesoa.common.jxpath.Constants;




/**
 * @author 非也 nychen2000@163.com
 * 
 */
public class Dom4JNodePointer extends NodePointer {

	private Node node;
	private String id;
    private NamespaceResolver localNamespaceResolver;
    
	public Dom4JNodePointer(Node node, Locale locale) {
		super(null, locale);
		this.node = node;
	}

	/**
	 * Create a new JDOMNodePointer.
	 * 
	 * @param node
	 *            pointed
	 * @param locale
	 *            Locale
	 * @param id
	 *            String id
	 */
	public Dom4JNodePointer(Node node, Locale locale, String id) {
		super(null, locale);
		this.node = node;
		this.id = id;
	}

	/**
	 * Create a new JDOMNodePointer.
	 * 
	 * @param parent
	 *            NodePointer
	 * @param node
	 *            pointed
	 */
	public Dom4JNodePointer(NodePointer parent, Node node) {
		super(parent);
		this.node = node;
	}

    public String asPath() {
        if (id != null) {
            return "id('" + escape(id) + "')";
        }

        StringBuffer buffer = new StringBuffer();
        if (parent != null) {
       	
            buffer.append(parent.asPath());
        }

        if (node instanceof Element) {
            // If the parent pointer is not a JDOMNodePointer, it is
            // the parent's responsibility to produce the node test part
            // of the path
            if (parent instanceof Dom4JNodePointer) {
                if (buffer.length() == 0
                    || buffer.charAt(buffer.length() - 1) != '/') {
                    buffer.append('/');
                }
                String nsURI = getNamespaceURI();
                String ln = Dom4JNodePointer.getLocalName((Node)node);

                if (nsURI == null) {
                    buffer.append(ln);
                    buffer.append('[');
                    buffer.append(getRelativePositionByName()).append(']');
                }
                else {
                    String prefix = getNamespaceResolver().getPrefix(nsURI);
                    if (prefix != null) {
                    	if (prefix.equals(Constants.DEFAULT_NS_PREFIX)){
                            buffer.append(ln);
                            buffer.append('[');
                            buffer.append(getRelativePositionByName()).append(']');
                    	}else{
                            buffer.append(prefix);
                            buffer.append(':');
                            buffer.append(ln);
                            buffer.append('[');
                            buffer.append(getRelativePositionByName());
                            buffer.append(']');
                    	}

                    }
                    else {
                        buffer.append("node()");
                        buffer.append('[');
                        buffer.append(getRelativePositionOfElement());
                        buffer.append(']');
                    }
                }

            }
        }
        else if (node instanceof Text || node instanceof CDATA) {
            buffer.append("/text()");
            buffer.append('[').append(getRelativePositionOfTextNode()).append(
                ']');
        }
        else if (node instanceof ProcessingInstruction) {
            buffer.append("/processing-instruction(\'").append(((ProcessingInstruction) node).getTarget()).append(
                "')");
            buffer.append('[').append(getRelativePositionOfPI()).append(
                ']');
        }
        return buffer.toString();
    }
    
    private int getRelativePositionOfPI() {
        String target = ((ProcessingInstruction) node).getTarget();
        Element parent = (Element) ((ProcessingInstruction) node).getParent();
        if (parent == null) {
            return 1;
        }
        List children = parent.content();
        int count = 0;
        for (int i = 0; i < children.size(); i++) {
            Object child = children.get(i);
            if (child instanceof ProcessingInstruction
                && (target == null
                    || target.equals(
                        ((ProcessingInstruction) child).getTarget()))) {
                count++;
            }
            if (child == node) {
                break;
            }
        }
        return count;
    }
    private int getRelativePositionOfTextNode() {
        Element parent;
        if (node instanceof Text) {
            parent = (Element) ((Text) node).getParent();
        }
        else {
            parent = (Element) ((CDATA) node).getParent();
        }
        if (parent == null) {
            return 1;
        }
        List children = parent.content();
        int count = 0;
        for (int i = 0; i < children.size(); i++) {
            Object child = children.get(i);
            if (child instanceof Text || child instanceof CDATA) {
                count++;
            }
            if (child == node) {
                break;
            }
        }
        return count;
    }
    private int getRelativePositionOfElement() {
        Object parent = ((Element) node).getParent();
        if (parent == null) {
            return 1;
        }
        List children;
        if (parent instanceof Element) {
            children = ((Element) parent).content();
        }
        else {
            children = ((Document) parent).content();
        }
        int count = 0;
        for (int i = 0; i < children.size(); i++) {
            Object child = children.get(i);
            if (child instanceof Element) {
                count++;
            }
            if (child == node) {
                break;
            }
        }
        return count;
    }
    public synchronized NamespaceResolver getNamespaceResolver() {
        if (localNamespaceResolver == null) {
            localNamespaceResolver = new NamespaceResolver(super.getNamespaceResolver());
            localNamespaceResolver.setNamespaceContextPointer(this);
        }
        return localNamespaceResolver;
    }
    protected String getDefaultNamespaceURI() {
        return getNamespaceResolver().getNamespaceURI(Constants.DEFAULT_NS_PREFIX);
    }
    
    public static String getLocalName(Node node) {
    	return node.getName();
    }
    
    public String getNamespaceURI(String prefix) {
        if (prefix.equals("xml")) {
            return Namespace.XML_NAMESPACE.getURI();
        }
        if (prefix.equals(Constants.DEFAULT_NS_PREFIX)){//采用context注册的默认命名空间
        	return getDefaultNamespaceURI();
        }
        Element element = null;
        if (node instanceof Document) {
            element = ((Document) node).getRootElement();
        }
        if (node instanceof Element) {
            element = (Element) node;
        }
        if (element == null) {
            return null;
        }
        Namespace ns = element.getNamespaceForPrefix(prefix);
        return ns == null ? null : ns.getURI();
    }
    
    public String getNamespaceURI() {
    	if (!(node instanceof Node)){
    		return null;
    	}else{
    		return getNamespaceURI((Node)node);
    	}
        
    }
    
    public static String getPrefix(Element node) {
        String prefix = node.getNamespacePrefix();
        return prefix;

    }
    
    /**
     * Get the ns uri of the specified node.
     * @param node Node to check
     * @return String ns uri
     */
    public static String getNamespaceURI(Node node) {
        if (node==null)return null;
        
        Element element = null;
        if (node instanceof Document) {
        	element = ((Document) node).getRootElement();
        }else if (node instanceof Element){
        	element = (Element)node;
        }
        else{
        	return null;
        }
        if (element==null)return null;

        String uri = element.getNamespaceURI();
        
        if (uri!=null && uri.trim().equals("")){
        	uri = null;
        }
        
        return uri;
    }

    private int getRelativePositionByName() {
        if (node instanceof Element) {
            Object parent = ((Element) node).getParent();
            if (!(parent instanceof Element)) {
                return 1;
            }

            List children = ((Element) parent).content();
            int count = 0;
            String name = ((Element) node).getQualifiedName();
            for (int i = 0; i < children.size(); i++) {
                Object child = children.get(i);
                if ((child instanceof Element)
                    && ((Element) child).getQualifiedName().equals(name)) {
                    count++;
                }
                if (child == node) {
                    break;
                }
            }
            return count;
        }
        return 1;
    }
    private static Element nodeParent(Object node) {
        if (node instanceof Element) {
            Object parent = ((Element) node).getParent();
            return parent instanceof Element ? (Element) parent : null;
        }
        if (node instanceof Text) {
            return (Element) ((Text) node).getParent();
        }
        if (node instanceof CDATA) {
            return (Element) ((CDATA) node).getParent();
        }
        if (node instanceof ProcessingInstruction) {
            return (Element) ((ProcessingInstruction) node).getParent();
        }
        if (node instanceof Comment) {
            return (Element) ((Comment) node).getParent();
        }
        return null;
    }

    /**
     * Find the nearest occurrence of the specified attribute
     * on the specified and enclosing elements.
     * @param n current node
     * @param attrName attribute name
     * @param ns Namespace
     * @return attribute value
     */
    protected static String findEnclosingAttribute(Object n, String attrName, Namespace ns) {
        while (n != null) {
            if (n instanceof Element) {
                Element e = (Element) n;
                String attr = e.attributeValue(new org.dom4j.QName(attrName, ns));
                if (attr != null && !attr.equals("")) {
                    return attr;
                }
            }
            n = nodeParent(n);
        }
        return null;
    }
    
    /**
     * Get the language of this element.
     * @return String language
     */
    protected String getLanguage() {
        return findEnclosingAttribute(node, "lang", Namespace.XML_NAMESPACE);
    }
    
    public boolean isLanguage(String lang) {
        String current = getLanguage();
        return current == null ? super.isLanguage(lang) : current.toUpperCase(
                Locale.ENGLISH).startsWith(lang.toUpperCase(Locale.ENGLISH));
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
        if (node instanceof Element) {
            return ((Element) node).content().size() == 0;
        }
        if (node instanceof Document) {
            return ((Document) node).content().size() == 0;
        }
        return true;

	}
    public int hashCode() {
        return node.hashCode();
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#isCollection()
	 */
	@Override
	public boolean isCollection() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getLength()
	 */
	@Override
	public int getLength() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getName()
	 */
	@Override
	public QName getName() {
	       String ns = null;
	        String ln = null;
	        if (node instanceof Element) {
	            ns = ((Element) node).getNamespacePrefix();
	            if (ns != null && ns.equals("")) {
	                ns = null;
	            }
	            ln = ((Element) node).getName();
	        }
	        else if (node instanceof ProcessingInstruction) {
	            ln = ((ProcessingInstruction) node).getTarget();
	        }
	        return new QName(ns, ln);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getBaseValue()
	 */
	@Override
	public Object getBaseValue() {
		return node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getImmediateNode()
	 */
	@Override
	public Object getImmediateNode() {
		return node;
	}
    public Object getValue() {
    	if (node instanceof Document){
    		return node;
    	}
        if (node instanceof Element) {
            StringBuffer buf = new StringBuffer();
            for (NodeIterator children = childIterator(null, false, null); children.setPosition(children.getPosition() + 1);) {
                NodePointer ptr = children.getNodePointer();
                if (ptr.getImmediateNode() instanceof Element || ptr.getImmediateNode() instanceof Text) {
                    buf.append(ptr.getValue());
                }
            }
            return buf.toString();
        }
        if (node instanceof Comment) {
            String text = ((Comment) node).getText();
            if (text != null) {
                text = text.trim();
            }
            return text;
        }
        String result = null;
        if (node instanceof Text) {
            result = ((Text) node).getText();
        }
        if (node instanceof ProcessingInstruction) {
            result = ((ProcessingInstruction) node).getStringValue();//TODO ?
        }
        boolean trim = !"preserve".equals(findEnclosingAttribute(node, "space", Namespace.XML_NAMESPACE));
        return result != null && trim ? result.trim() : result;
        
//		if ((node instanceof org.dom4j.CharacterData
//				|| node instanceof Attribute || node instanceof DocumentType
//				|| node instanceof Entity || node instanceof ProcessingInstruction)) {
//			return ((Node)node).getText();
//		}else{
//			if (node instanceof Document){
//				((Document)node).getText();
//			}else if (node instanceof Element){
//				Element elm = (Element)node;
//				return elm.getText();
//			}
//		}
//        return node.toString();
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.jxpath.ri.model.NodePointer#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		if (value==null) value="";//null当做空字符串处理
		if ((node instanceof org.dom4j.CharacterData
				|| node instanceof Attribute || node instanceof DocumentType
				|| node instanceof Entity || node instanceof ProcessingInstruction)) {
			String string = (String) TypeUtils.convert(value, String.class);
			if (string != null && !string.equals("")) {
				((Node) node).setText(string);
			} else {
				((Node) node).getParent().remove((Node) node);
			}
		} else if (node instanceof Document) {
			Document theOriginalDoc = (Document)node;
			Element theOrigialRoot = theOriginalDoc.getRootElement();

			if (value instanceof Document){//拷贝整个document
			
				Document valueDoc = (Document) value;
				Element valueRoot = valueDoc.getRootElement();
				
				if (theOrigialRoot==null || valueRoot==null ||
						theOrigialRoot.getQName().equals(valueRoot.getQName())){
					
					theOriginalDoc.clearContent();
					
					List content = valueDoc.content();
					if (content != null) {
						for (int i = 0; i < content.size(); i++) {
							Node dom4jNode = (Node) content.get(i);
							Node newDom4jNode = (Node) dom4jNode.clone();
							theOriginalDoc.add(newDom4jNode);
						}
					}
				}else{
					throw new RuntimeException("Can NOT assign "+valueRoot.getQName()+" to "+theOrigialRoot.getQName());

				}

			}
			else if (value instanceof Element){
				Element valueElem = (Element)value;
				if (valueElem.getQName().equals(theOrigialRoot.getQName())){
					theOriginalDoc.clearContent();
					Element newValueElem = (Element)valueElem.clone();
					theOriginalDoc.setRootElement(newValueElem);
				}
				else{
					throw new RuntimeException("Can NOT assign "+valueElem.getQName()+" to "+theOrigialRoot.getQName());
				}
			}
			else{
				throw new RuntimeException("Can NOT assign "+value+" to "+theOrigialRoot.getQName());

			}
//			else if (value instanceof Comment){
//				Comment cmmt = (Comment)((Comment)value).clone();
//				theOriginalDoc.add(cmmt);
//				
//			}else if (value instanceof ProcessingInstruction){
//				ProcessingInstruction instru = (ProcessingInstruction)((ProcessingInstruction)value).clone();
//				theOriginalDoc.add(instru);
//			}


		} else if (node instanceof Element) {
			Element originalElem = ((Element) node);
			
			if (value!=null && value instanceof Element){
				Element valueElm = (Element) value;
				if (originalElem.getQName().equals(valueElm.getQName())){
					originalElem.clearContent();
					List content = valueElm.content();
					if (content != null) {
						for (int i = 0; i < content.size(); i++) {
							Node dom4jNode = (Node) content.get(i);
							Node newDom4jNode = (Node) dom4jNode.clone();
							originalElem.add(newDom4jNode);
						}
					}
				}else{
					throw new RuntimeException("Can NOT assign "+valueElm.getQName()+" to "+originalElem.getQName());
				}

			}
			else if (value!=null && value instanceof Text ){
				originalElem.clearContent();
				Text txt = (Text)((Text)value).clone();
				originalElem.add(txt);
			}
			else if (value!=null &&  value instanceof CDATA){
				originalElem.clearContent();
				CDATA cdata = (CDATA)((CDATA)value).clone();
				originalElem.add(cdata);
			}
			else if (value!=null && value instanceof java.util.Date){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateStr = format.format((java.util.Date)value);
				originalElem.clearContent();
				originalElem.addText(dateStr);
			}
			else {
				String string = (String) TypeUtils.convert(value, String.class);
				originalElem.clearContent();
				originalElem.addText(string);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.jxpath.ri.model.NodePointer#compareChildNodePointers
	 * (org.apache.commons.jxpath.ri.model.NodePointer,
	 * org.apache.commons.jxpath.ri.model.NodePointer)
	 */
	@Override
	public int compareChildNodePointers(NodePointer pointer1,
			NodePointer pointer2) {
        Object node1 = pointer1.getBaseValue();
        Object node2 = pointer2.getBaseValue();
        if (node1 == node2) {
            return 0;
        }

        if ((node1 instanceof Attribute) && !(node2 instanceof Attribute)) {
            return -1;
        }
        if (
            !(node1 instanceof Attribute) && (node2 instanceof Attribute)) {
            return 1;
        }
        if (
            (node1 instanceof Attribute) && (node2 instanceof Attribute)) {
            List list = ((Element) getNode()).attributes();
            int length = list.size();
            for (int i = 0; i < length; i++) {
                Object n = list.get(i);
                if (n == node1) {
                    return -1;
                }
                else if (n == node2) {
                    return 1;
                }
            }
            return 0; // Should not happen
        }

        if (!(node instanceof Element)) {
            throw new RuntimeException(
                "JXPath internal error: "
                    + "compareChildNodes called for "
                    + node);
        }

        List children = ((Element) node).content();
        int length = children.size();
        for (int i = 0; i < length; i++) {
            Object n = children.get(i);
            if (n == node1) {
                return -1;
            }
            if (n == node2) {
                return 1;
            }
        }

        return 0;
	}
	
    public NodePointer createChild(
            JXPathContext context,
            QName name,
            int index) {
            if (index == WHOLE_COLLECTION) {
                index = 0;
            }
            boolean success =
                getAbstractFactory(context).createObject(
                    context,
                    this,
                    node,
                    name.toString(),
                    index);
            if (success) {
                NodeTest nodeTest;
                String prefix = name.getPrefix();
                String namespaceURI = prefix == null ? null : context
                        .getNamespaceURI(prefix);
                nodeTest = new NodeNameTest(name, namespaceURI);

                NodeIterator it =
                    childIterator(nodeTest, false, null);
                if (it != null && it.setPosition(index + 1)) {
                    return it.getNodePointer();
                }
            }
            throw new JXPathAbstractFactoryException("Factory could not create "
                    + "a child node for path: " + asPath() + "/" + name + "["
                    + (index + 1) + "]");
        }

        public NodePointer createChild(
                JXPathContext context, QName name, int index, Object value) {
            NodePointer ptr = createChild(context, name, index);
            ptr.setValue(value);
            return ptr;
        }	
        
        public NodeIterator childIterator(
                NodeTest test,
                boolean reverse,
                NodePointer startWith) {
                return new Dom4JNodeIterator(this, test, reverse, startWith);
            }    
	
	/**
	 * 创建一个名字为name的attribute
	 */
    public NodePointer createAttribute(JXPathContext context, QName name) {
        if (!(node instanceof Element)) {
            return super.createAttribute(context, name);
        }
        Element element = (Element) node;
        String prefix = name.getPrefix();
        if (prefix != null) {
            String nsUri = null;
            NamespaceResolver nsr = getNamespaceResolver();
            if (nsr != null) {
                nsUri = nsr.getNamespaceURI(prefix);
            }
            if (nsUri == null) {
                throw new JXPathException(
                    "Unknown namespace prefix: " + prefix);
            }
            Namespace dom4jNs = Namespace.get(prefix,nsUri);
            org.dom4j.QName attributeName = new org.dom4j.QName(name.getName(),dom4jNs);
            Attribute attr = element.attribute(attributeName);
            if (attr==null){
            	element.addAttribute(attributeName, "");
            }
        }
        else {
        	Attribute attr = element.attribute(name.getName());
            if (attr==null) {
                element.addAttribute(name.getName(), "");
            }
        }
        NodeIterator it = attributeIterator(name);
        it.setPosition(1);
        return it.getNodePointer();
    }
    public NodeIterator attributeIterator(QName name) {
        return new Dom4JAttributeIterator(this, name);
    }
    /**
     * Get any prefix from the specified node.
     * @param node the node to check
     * @return String xml prefix
     */
    public static String getPrefix(Node node) {
    	if (node instanceof Element){
    		String prefix = ((Element)node).getNamespacePrefix();
    		return (prefix == null || prefix.equals("")) ? null : prefix;
    	}
    	else if (node instanceof Attribute){
    		String prefix = ((Attribute)node).getNamespacePrefix();
    		return (prefix == null || prefix.equals("")) ? null : prefix;
    	}
        return null;
    }

    /**
     * Get the local name of the specified node.
     * @param node node to check
     * @return String local name
     */
    public static String getLocalName(Object node) {
        if (node instanceof Element) {
            return ((Element) node).getName();
        }
        if (node instanceof Attribute) {
            return ((Attribute) node).getName();
        }
        return null;
    }
    
    public boolean testNode(NodeTest test) {
        return testNode(this, node, test);
    }
    
    /**
     * Get the parent of the specified node.
     * @param node to check
     * @return parent Element
     */
    private static Element nodeParent(Node node) {
    	return node.getParent();

    }
    
    public NodeIterator namespaceIterator() {
        return new Dom4JNamespaceIterator(this);
    }

    public NodePointer namespacePointer(String prefix) {
        return new Dom4JNamespacePointer(this, prefix);
    }
    
    public void remove() {    	
        Element parent = nodeParent(node);
        if (parent == null) {
            throw new JXPathException("Cannot remove root Dom4J node");
        }
        parent.remove(node);
    }
    /**
     * Execute test against node on behalf of pointer.
     * @param pointer Pointer
     * @param node to test
     * @param test to execute
     * @return true if node passes test
     */
    public static boolean testNode(
        NodePointer pointer,
        Object node,
        NodeTest test) {
        if (test == null) {
            return true;
        }
        if (test instanceof NodeNameTest) {
            if (!(node instanceof Element)) {
                return false;
            }

            NodeNameTest nodeNameTest = (NodeNameTest) test;
            QName testName = nodeNameTest.getNodeName();
            String namespaceURI = nodeNameTest.getNamespaceURI();
            boolean wildcard = nodeNameTest.isWildcard();
            String testPrefix = testName.getPrefix();
            
            //如果testPrefix未null则采用默认命名空间
            if (testPrefix==null && namespaceURI==null){
            	namespaceURI = pointer.getNamespaceURI(Constants.DEFAULT_NS_PREFIX);
            }
            
            if (wildcard && testPrefix == null) {
                return true;
            }
            if (wildcard
                || testName.getName()
                        .equals(Dom4JNodePointer.getLocalName(node))) {
                String nodeNS = Dom4JNodePointer.getNamespaceURI((Node)node);
                return equalStrings(namespaceURI, nodeNS) || nodeNS == null
                        && equalStrings(testPrefix, Dom4JNodePointer.getPrefix((Node)node));
            }
            return false;
        }
        if (test instanceof NodeTypeTest) {
            switch (((NodeTypeTest) test).getNodeType()) {
                case Compiler.NODE_TYPE_NODE :
                    return true;
                case Compiler.NODE_TYPE_TEXT :
                    return (node instanceof Text) || (node instanceof CDATA);
                case Compiler.NODE_TYPE_COMMENT :
                    return node instanceof Comment;
                case Compiler.NODE_TYPE_PI :
                    return node instanceof ProcessingInstruction;
                default:
                    return false;
            }
        }
        if (test instanceof ProcessingInstructionTest && node instanceof ProcessingInstruction) {
            String testPI = ((ProcessingInstructionTest) test).getTarget();
            String nodePI = ((ProcessingInstruction) node).getTarget();
            return testPI.equals(nodePI);
        }
        return false;
    }
    private static boolean equalStrings(String s1, String s2) {
        if (s1 == s2) {
            return true;
        }
        s1 = s1 == null ? "" : s1.trim();
        s2 = s2 == null ? "" : s2.trim();
        return s1.equals(s2);
    }
    
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Dom4JNodePointer)) {
            return false;
        }

        Dom4JNodePointer other = (Dom4JNodePointer) object;
        return node == other.node;
    }
}
