package org.firesoa.common.jxpath.model.dom4j;

import java.util.List;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.firesoa.common.jxpath.Constants;

public class Dom4JFactory extends AbstractFactory {
	DocumentFactory docFactory = DocumentFactory.getInstance();

	/**
	 * Create a new instance and put it in the collection on the parent object.
	 * Return <b>false</b> if this factory cannot create the requested object.
	 */
	public boolean createObject(JXPathContext context, Pointer pointer,
			Object parent, String name, int index) {
		String prefix = null;
		String localName = null;
		String namespaceURI = null;
		int thePosition = name.indexOf(":");
		if (thePosition > 0) {
			prefix = name.substring(0, thePosition);
			localName = name.substring(thePosition + 1);
			namespaceURI = context.getNamespaceURI(prefix);
		} else {
			//取缺省命名空间
			namespaceURI = ((Dom4JNodePointer)pointer).getNamespaceURI(Constants.DEFAULT_NS_PREFIX);
			localName = name;
		}
		if (parent instanceof Element) {
			addDom4JElement((Element) parent, index, localName, namespaceURI);
		} else if (parent instanceof Document) {
			addDom4JElement((Document) parent, index, prefix, localName,
					namespaceURI);
		}

		return true;

		// if (name.equals("location")
		// || name.equals("address")
		// || name.equals("street")) {
		// addJDOMElement((Element) parent, index, name, null);
		// return true;
		// }
		// if (name.startsWith("price:")) {
		// String namespaceURI = context.getNamespaceURI("price");
		// addJDOMElement((Element) parent, index, name, namespaceURI);
		// return true;
		// }

		// return false;
	}

	private void addDom4JElement(Document parent, int index, String prefix,
			String localName, String namespaceURI) {

		// 创建新的root，如果root已經存在，dom4j会抛出异常
		if (namespaceURI != null && !namespaceURI.trim().equals("")) {
			String qualifiedName = (prefix == null ? localName
					: (prefix + ":" + localName));
			parent.addElement(qualifiedName, namespaceURI);
		} else {
			parent.addElement(localName);
		}

	}

	private void addDom4JElement(Element parent, int index, String localName,
			String namespaceURI) {
		List children = parent.content();
		// System.out.println("===Parent is "+parent.getQualifiedName()+";children.size() is "+children.size());

		int count = 0;
		for (int i = 0; i < children.size(); i++) {
			Object child = children.get(i);
			// System.out.println("===Child is "+child.getClass()+"; "+((Node)child).getName());
			if (child instanceof Element
					&& ((Element) child).getName().equals(localName)) {
				count++;
			}
		}

		// Keep inserting new elements until we have index + 1 of them
		while (count <= index) {
			// In a real factory we would need to do the right thing with
			// the namespace prefix.
			Element newElement;
			if (namespaceURI != null) {
				localName = localName.substring(localName.indexOf(':') + 1);
				newElement = docFactory.createElement(localName, namespaceURI);
			} else {
				newElement = docFactory.createElement(localName);
			}
			parent.add(newElement);
			count++;
		}
	}

	public boolean declareVariable(JXPathContext context, String name) {
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
}
