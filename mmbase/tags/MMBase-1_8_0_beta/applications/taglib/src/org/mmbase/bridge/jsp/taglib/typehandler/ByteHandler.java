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
import org.mmbase.bridge.jsp.taglib.*;
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
 * @version $Id: ByteHandler.java,v 1.22 2005-12-21 08:25:00 michiel Exp $
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
        args.set(Parameter.LOCALE, tag.getLocale());
        return  (node != null ? node.getFunctionValue("gui", args).toString() : "") +
                 "<input type=\"" + (search ? "text" : "file") + "\" name=\"" + prefix(field.getName()) + "\" />";
    }

    public String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException {
        // XXXX TODO
        return "";
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        String fieldName = field.getName();
        ContextTag ct = tag.getContextTag();
        ContextContainer cc = tag.getContextProvider().getContextContainer();
        org.apache.commons.fileupload.FileItem bytes = ct.getFileItem(prefix(fieldName));

        if (bytes == null){
            throw new BridgeException("getBytes(" + prefix(fieldName) + ") returned null (node= " +  node.getNumber() +") field=(" + field + ") (Was your form  enctype='multipart/form-data' ?");
        }
        if (bytes.getSize() > 0) {
            String fileName = bytes.getName();
            String fileType = bytes.getContentType();

            try {
                node.setInputStreamValue(fieldName, bytes.getInputStream(), bytes.getSize());
            } catch (java.io.IOException ioe) {
                throw new TaglibException(ioe);
            }
            log.debug("Filename : " + fileName);
            NodeManager nm = node.getNodeManager();
            if (nm.hasField("mimetype") && (fileType != null) && (! fileType.equals("")) &&
                cc.find(tag.getPageContext(), prefix("mimetype")) == null
                ) {
                node.setStringValue("mimetype", fileType);
            }
            if (nm.hasField("filename") && (fileName != null) && (! fileName.equals("")) &&
                cc.find(tag.getPageContext(), prefix("filename")) == null
                ) {
                node.setStringValue("filename", fileName);
            }
            if (nm.hasField("size") &&
                cc.find(tag.getPageContext(), prefix("size")) == null
                ) {
                node.setLongValue("size", bytes.getSize());
            }
            if (nm.hasField("filesize") &&
                cc.find(tag.getPageContext(), prefix("filesize")) == null
                ) {
                node.setLongValue("filesize", bytes.getSize());
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
