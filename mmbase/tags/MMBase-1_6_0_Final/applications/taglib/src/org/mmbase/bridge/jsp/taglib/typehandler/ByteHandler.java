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
import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public class ByteHandler extends AbstractTypeHandler {
    
    /**
     * Constructor for ByteHandler.
     * @param context
     */
    public ByteHandler(FieldInfoTag context) {
        super(context);
    }
    
    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        List args = new ArrayList();
        args.add("");
        args.add(context.getSessionName());
        args.add(context.getCloud().getLocale().getLanguage());
        return  (node != null ? node.getFunctionValue("gui", args).toString() : "") +
                                "<input type=\"file\" name=\"" + prefix(field.getName()) + "\" />";
    }
    
    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public String useHtmlInput(Node node, Field field) throws JspTagException {        
        String fieldName = field.getName();
        byte [] bytes  = context.getContextTag().getBytes(prefix(fieldName));
        if (bytes.length > 0) {
            node.setByteValue(fieldName, bytes);
        }
        return "";
    }
    
    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        throw new JspTagException("Don't know what to do with bytes()");
    }
    
}
