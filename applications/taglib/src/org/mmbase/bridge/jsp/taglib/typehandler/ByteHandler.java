/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.ContextTag;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;
import org.mmbase.module.core.MMObjectBuilder;
import javax.servlet.jsp.PageContext;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: ByteHandler.java,v 1.11.2.3 2004-09-30 08:37:15 michiel Exp $
 */

public class ByteHandler extends AbstractTypeHandler {
    private static final Logger log = Logging.getLoggerInstance(ByteHandler.class);
    /**
     * Constructor for ByteHandler.
     * @param tag
     */
    public ByteHandler(FieldInfoTag tag) {
        super(tag);
    }
    
    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        Parameters args = new Parameters(MMObjectBuilder.GUI_PARAMETERS);
        args.set("field", ""); // lot of function implementations would not stand 'null' as field name value
        args.set(Parameter.LANGUAGE, tag.getLocale().getLanguage());
        args.set("session",  tag.getSessionName());
        PageContext pc = tag.getContextTag().getPageContext();
        args.set(Parameter.RESPONSE, pc.getResponse());
        args.set(Parameter.REQUEST,  pc.getRequest());
        // args.set(Parameter.LOCALE, tag.getLocale());
        return  (node != null ? node.getFunctionValue("gui", args).toString() : "") +
                 "<input type=\"" + (search ? "text" : "file") + "\" name=\"" + prefix(field.getName()) + "\" />";
    }
    
    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {        
        String fieldName = field.getName();
        ContextTag ct = tag.getContextTag();        
        ContextContainer cc = tag.getContextProvider().getContextContainer();        
        byte [] bytes  = ct.getBytes(prefix(fieldName));
        String fileName = null;        

        String fileType = null;

        if (bytes == null){
            throw new BridgeException("getBytes(" + prefix(fieldName) + ") returned null (node= " +  node.getNumber() +") field=(" + field + ") (Was your form  enctype='multipart/form-data' ?");
        }
        if (bytes.length > 0) {
            Object fileNameO = cc.find(tag.getPageContext(), prefix(fieldName + "_name"));
            if (fileNameO != null) {                
                if (fileNameO instanceof List) {
                    List l = (List) fileNameO;
                    if (l.size() == 1) {
                        fileName = "" + l.get(0);
                    } else {
                        fileName = "" + l;
                    }
                } else {
                    fileName = "" + fileNameO;
                }
            }
            Object fileTypeO = cc.find(tag.getPageContext(), prefix(fieldName + "_type"));
            if (fileTypeO != null) {
                // log.info("" + fileTypeO.getClass() + fileTypeO);
                if (fileTypeO instanceof List) {               
                    List l = (List) fileTypeO;
                    if (l.size() == 1) {
                        fileType = "" + l.get(0);
                    } else {
                        fileType = "" + l;
                    }
                } else {
                    fileType = "" + fileTypeO;
                }
                
            }
            
            node.setByteValue(fieldName, bytes);
            NodeManager nm = node.getNodeManager();
            if (nm.hasField("mimetype") && (fileType != null) && (! fileType.equals(""))) {
                node.setStringValue("mimetype", fileType);
            }
            if (nm.hasField("filename") && (fileName != null) && (! fileName.equals(""))) {
                node.setStringValue("filename", fileName);
            }
            if (nm.hasField("size")) {
                node.setIntValue("size", bytes.length);
            }
        }

        return true;
    }
    
    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        log.error("Don't know what to do with byte[]");
        return super.whereHtmlInput(field);
    }
    
}
