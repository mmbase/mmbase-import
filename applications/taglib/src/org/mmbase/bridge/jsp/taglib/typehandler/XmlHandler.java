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
import org.mmbase.util.transformers.*;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: XmlHandler.java,v 1.22 2009-01-12 12:48:20 michiel Exp $
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
    @Override public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        if(! search) {
            StringBuilder buffer = new StringBuilder();
            // the wrap attribute is not valid in XHTML, but it is really needed for netscape < 6
            // wrap attribute removed, we want to produce valid XHTML, and who is still using netscape < 6?
            buffer.append("<textarea class=\"big " + getClasses(node, field) + "\"  rows=\"10\" cols=\"80\" ");
            buffer.append("name=\"").append(prefix(field.getName())).append("\" ");
            buffer.append("id=\"").append(prefixID(field.getName())).append("\" ");
            addExtraAttributes(buffer);
            buffer.append(">");
            String value;
            if (node != null) {
                value = org.mmbase.util.Encode.encode("ESCAPE_XML", tag.decode(org.mmbase.util.Casting.toString(getFieldValue(node, field, false)), node));
            } else {
                value = "";
            }
            buffer.append(value);
            String opt = tag.getOptions();
            if (opt != null && opt.indexOf("noempty") > -1 && value.length() == 0) {
                buffer.append(" ");
            }
            buffer.append("</textarea>");
            return buffer.toString();
        } else {
            return super.htmlInput(node, field, search);
        }

    }


}
