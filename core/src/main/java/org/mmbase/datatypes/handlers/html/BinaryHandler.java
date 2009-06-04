/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers.html;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.handlers.Request;
import org.mmbase.storage.search.Constraint;
import org.apache.commons.fileupload.FileItem;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;

/**
 * The most straight forward implementation in HTML for an input widget for a binary field is a
 * 'input' tag with type 'file'.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public class BinaryHandler extends HtmlHandler {

    private boolean useSpecificSetter = false;

    /**
     * IF this is set it true the value is not set with {@link Node#setValue} but with {@link
     * Node#setInputStreamValue}.  The effect of this is that the method {@link org.mmbase.datatypes.DataType#cast} is
     * avoided. This may be useful if the set-processor expects an InputStream, and not a
     * byte-array (or another type if this Handler is used on a non-binary field).
     */
    public void setUseSpecificSetter(boolean s) {
        useSpecificSetter = s;
    }


    @Override
    public String input(Request request, Node node, Field field, boolean search) {
        StringBuilder show = new StringBuilder();
        if (node != null) {
            Function gui = node.getFunction("gui");
            Parameters args = gui.createParameters();
            args.set("field", field.getName());
            args.set(Parameter.LANGUAGE, request.getLocale().getLanguage());
            args.set("session",  request.getProperty(HtmlHandler.SESSIONNAME));
            args.set(Parameter.RESPONSE, request.getProperty(Parameter.RESPONSE));
            args.set(Parameter.REQUEST,  request.getProperty(Parameter.REQUEST));
            args.set(Parameter.LOCALE, request.getLocale());
            show.append("" + gui.getFunctionValue(args));
        }
        show.append("<input class=\"");
        appendClasses(show,node, field);
        show.append("\" type=\"").append(search ? "text" : "file").append("\" name=\"").append(request.getName(field)).append("\" ");
        //show.append("id=\"").append(prefixID(field.getName())).append("\" ");
        //addExtraAttributes(show);
        show.append("/>");
        return show.toString();

    }


    @Override
    protected void setValue(Node node, String fieldName, Object value) {
        if (value == null) {
            node.setValue(fieldName, value);
        } else {
            SerializableInputStream sis = Casting.toSerializableInputStream(value);
            if ("".equals(sis.getName())) {
                // notching changed.
            } else {
                if (useSpecificSetter) {
                    node.setInputStreamValue(fieldName, sis, sis.getSize());
                } else {
                    node.setValue(fieldName, sis);
                }
            }
        }
    }

    /**
     * Returns the field value as specified by the client's post.
     */
    @Override
    protected Object getFieldValue(Request request, Node node, Field field)  {
        if (MultiPart.isMultipart(request.getProperty(Parameter.REQUEST))) {
            SerializableInputStream bytes = MultiPart.getMultipartRequest(request.getProperty(Parameter.REQUEST),
                                                           request.getProperty(Parameter.RESPONSE)).getInputStream(request.getName(field));
            return bytes;
        } else {
            return null;
        }
    }


    @Override
    public String toString() {
        return super.toString() + (useSpecificSetter ? " (setInputStream)" : " (setValue)");
    }

}
