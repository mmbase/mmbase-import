/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: XmlHandler.java,v 1.4 2003-11-25 21:31:55 michiel Exp $
 */

public class XmlHandler extends StringHandler {

    /**
     * Constructor for XmlHandler.
     * @param context
     */
    public XmlHandler(FieldInfoTag tag) {
        super(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        if(! search) {
            StringBuffer buffer = new StringBuffer();
            // the wrap attribute is not valid in XHTML, but it is really needed for netscape < 6
            buffer.append("<textarea wrap=\"soft\" rows=\"10\" cols=\"80\" class=\"big\"  name=\"");
            buffer.append(prefix(field.getName()));
            buffer.append("\"");
            addExtraAttributes(buffer);
            buffer.append(">");
            if (node != null) {
                try {
                    // get the XML from this thing....
                    // javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                    // javax.xml.parsers.DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
                    // org.w3c.dom.Element xml = node.getXMLValue(field.getName(), dBuilder.newDocument());
                    org.w3c.dom.Document xml = node.getXMLValue(field.getName());

                    if(xml!=null) {
                        // make a string from the XML
                        javax.xml.transform.TransformerFactory tfactory = javax.xml.transform.TransformerFactory.newInstance();
                        //tfactory.setURIResolver(new org.mmbase.util.xml.URIResolver(new java.io.File("")));
                        javax.xml.transform.Transformer serializer = tfactory.newTransformer();
                        serializer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
                        serializer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
                        java.io.StringWriter str = new java.io.StringWriter();
                        // there is a <field> tag placed around it,... we hate it :)
                        // change this in the bridge?
                        serializer.transform(new javax.xml.transform.dom.DOMSource(xml),  new javax.xml.transform.stream.StreamResult(str));

                        // fill the field with it....
                        buffer.append(Encode.encode("ESCAPE_XML", str.toString()));
                    }
                }
                catch(javax.xml.transform.TransformerConfigurationException tce) {
                    throw new JspTagException(tce.toString() + " " + Logging.stackTrace(tce));
                }
                catch(javax.xml.transform.TransformerException te) {
                    throw new JspTagException(te.toString() + " " + Logging.stackTrace(te));
                }
            }
            buffer.append("</textarea>");
            return buffer.toString();
        } else {
            return super.htmlInput(node, field, search);
        }

    }

}
