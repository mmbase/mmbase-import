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
import org.mmbase.util.StringBufferWriter;
import org.mmbase.util.logging.Logging;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.mmbase.util.transformers.*;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: XmlHandler.java,v 1.5 2004-01-05 17:35:03 michiel Exp $
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

                    if(xml != null) {
                        // make a string from the XML
                        TransformerFactory tfactory = TransformerFactory.newInstance();
                        //tfactory.setURIResolver(new org.mmbase.util.xml.URIResolver(new java.io.File("")));
                        Transformer serializer = tfactory.newTransformer();
                        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        StringBufferWriter str = new StringBufferWriter(buffer);
                        ChainedCharTransformer ct = new ChainedCharTransformer();
                        ct.add(new TabToSpacesTransformer(2));
                        Xml x = new Xml();
                        x.configure(Xml.ESCAPE);
                        ct.add(x);
                        Writer w = new TransformingWriter(str, new TabToSpacesTransformer(3));
                        // there is a <field> tag placed around it,... we hate it :)
                        // change this in the bridge?
                        serializer.transform(new DOMSource(xml),  new StreamResult(w));

                        w.close();

                    }
                } catch(IOException ioe) {
                    throw new JspTagException(ioe.toString() + " " + Logging.stackTrace(ioe));
                } catch(TransformerConfigurationException tce) {
                    throw new JspTagException(tce.toString() + " " + Logging.stackTrace(tce));
                } catch(TransformerException te) {
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
