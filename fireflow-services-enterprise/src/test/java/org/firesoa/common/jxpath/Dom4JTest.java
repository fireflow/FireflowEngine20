package org.firesoa.common.jxpath;

import java.util.List;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.xml.DocumentContainer;
import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.firesoa.common.jxpath.model.dom4j.Dom4JFactory;
import org.firesoa.common.jxpath.model.dom4j.Dom4JParser;


public class Dom4JTest extends XMLModelTestCase {

	public void testID(){
		
	}
	
	@Override
	protected String getModel() {
		
		DocumentContainer.registerXMLParser("DOM4J", new Dom4JParser());
		return "DOM4J";
	}
	
    public void testGetNode() {
        assertXPathNodeType(context, "/", Document.class);
        assertXPathNodeType(context, "/vendor/location", Element.class);
        assertXPathNodeType(context, "//location/@name", Attribute.class);
        assertXPathNodeType(context, "//vendor", Element.class); //bugzilla #38586
        
        System.out.println(context.getValue("/"));
        System.out.println(context.getValue("/vendor[1]"));
        System.out.println(context.getValue("/vendor[1]/contact[1]"));
        System.out.println(context.getValue("/vendor[1]/location[1]/address/street"));
        System.out.println(context.getValue("/vendor[1]/location[1]/@name"));
        System.out.println(context.getValue("/vendor[1]/location[1]/@id"));
    }

    public void testGetElementDescendantOrSelf() {
        JXPathContext childContext = context.getRelativeContext(context.getPointer("/vendor"));
        assertTrue(childContext.getContextBean() instanceof Element);
        assertXPathNodeType(childContext, "//vendor", Element.class);
    }

	@Override
	protected AbstractFactory getAbstractFactory() {
        return new Dom4JFactory();
	}

	@Override
	protected String getXMLSignature(Object node, boolean elements,
			boolean attributes, boolean text, boolean pi) {
        StringBuffer buffer = new StringBuffer();
        appendXMLSignature(buffer, node, elements, attributes, text, pi);
        return buffer.toString();
	}
    private void appendXMLSignature(
            StringBuffer buffer,
            Object object,
            boolean elements,
            boolean attributes,
            boolean text,
            boolean pi) 
        {
            if (object instanceof Document) {
                buffer.append("<D>");
                appendXMLSignature(
                    buffer,
                    ((Document) object).content(),
                    elements,
                    attributes,
                    text,
                    pi);
                buffer.append("</D");
            }
            else if (object instanceof Element) {
                String tag = elements ? ((Element) object).getName() : "E";
                buffer.append("<");
                buffer.append(tag);
                buffer.append(">");
                appendXMLSignature(
                    buffer,
                    ((Element) object).content(),
                    elements,
                    attributes,
                    text,
                    pi);
                buffer.append("</");
                buffer.append(tag);
                buffer.append(">");
            }
            else if (object instanceof Text || object instanceof CDATA) {
                if (text) {
                    String string = ((Text) object).getText();
                    string = string.replace('\n', '=');
                    buffer.append(string);
                }
            }
        }

        private void appendXMLSignature(
            StringBuffer buffer,
            List children,
            boolean elements,
            boolean attributes,
            boolean text,
            boolean pi) 
        {
            for (int i = 0; i < children.size(); i++) {
                appendXMLSignature(
                    buffer,
                    children.get(i),
                    elements,
                    attributes,
                    text,
                    pi);
            }
        }
}
