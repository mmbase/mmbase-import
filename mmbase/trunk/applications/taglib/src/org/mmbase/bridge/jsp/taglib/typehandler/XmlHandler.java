/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
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
 * @version $Id: XmlHandler.java,v 1.11 2005-05-19 13:09:40 michiel Exp $
 */

public class XmlHandler extends StringHandler {

    /**
     * Constructor for XmlHandler.
     * @param tag
     */
    public XmlHandler(FieldInfoTag tag) {
        super(tag);
    }

    private static ChainedCharTransformer ct;

    static {
        ct = new ChainedCharTransformer();
        ct.add(new TabToSpacesTransformer(2));
        Xml x = new Xml();
        x.configure(Xml.ESCAPE);
        ct.add(x);
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
            String value;
            if (node != null) {
                value = node.getStringValue(field.getName());
            } else {
                value = "";
            }
            buffer.append(value);
            String opt = tag.getOptions();
            if (opt != null && opt.indexOf("noempty") > -1 && value.equals("")) {
                buffer.append(" ");
            }            
            buffer.append("</textarea>");
            return buffer.toString();
        } else {
            return super.htmlInput(node, field, search);
        }

    }


}
