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
import org.mmbase.bridge.jsp.taglib.edit.FormTag;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.Xml;
import org.mmbase.datatypes.*;
import javax.servlet.jsp.PageContext;
import org.apache.commons.fileupload.FileItem;

/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8 (was named ByteHandler previously)
 * @version $Id: BinaryHandler.java,v 1.4.2.4 2007-10-22 14:10:01 michiel Exp $
 */

public class BinaryHandler extends AbstractTypeHandler {
    private static final Logger log = Logging.getLoggerInstance(ByteHandler.class);
    /**
     * Constructor
     * @param tag
     */
    public BinaryHandler(FieldInfoTag tag) {
        super(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        StringBuffer show = new StringBuffer();
        if (node != null) {
            Function gui = node.getFunction("gui");
            Parameters args = gui.createParameters();
            args.set("field", field.getName());
            args.set(Parameter.LANGUAGE, tag.getLocale().getLanguage());
            args.set("session",  tag.getSessionName());
            PageContext pc = tag.getContextTag().getPageContext();
            args.set(Parameter.RESPONSE, pc.getResponse());
            args.set(Parameter.REQUEST,  pc.getRequest());
            args.set(Parameter.LOCALE, tag.getLocale());
            show.append("" + gui.getFunctionValue(args));
        }
        show.append("<input class=\"" + getClasses(node, field) + "\" type=\"").append(search ? "text" : "file").append("\" name=\"").append(prefix(field.getName())).append("\" id=\"").append(prefixID(field.getName())).append("\" ");
        addExtraAttributes(show);
        show.append("/>");
        return show.toString();

    }


    /**
     * Returns the field value as specified by the client's post.
     */
    protected Object getFieldValue(Field field) throws JspTagException {
        if (MultiPart.isMultipart(tag.getPageContext())) {
            ContextContainer cc = tag.getContextProvider().getContextContainer();
            ContextTag ct = tag.getContextTag();
            FileItem bytes = ct.getFileItem(prefix(field.getName()));
            return bytes;
        } else {
            return null;
        }
    }

    public String checkHtmlInput(Node node, Field field, boolean errors) throws JspTagException {
        Object fieldValue = getFieldValue(field);

        DataType dt = field.getDataType();
        Collection col = dt.validate(fieldValue, node, field);
        if (col.size() == 0) {
            // do actually set the field, because some datatypes need cross-field checking
            // also in an mm:form, you can simply commit.
            if (node != null && ! field.isReadOnly() && fieldValue != null) {
                setValue(node, field, (FileItem) fieldValue);
            }
            if (errors) {
                return "<div id=\"" + prefixError(field.getName()) + "\" class=\"mm_check_noerror\"> </div>";
            } else {
                return "";
            }
        } else {
            FormTag form =  tag.getFormTag(false, null);
            if (form != null) {
                form.setValid(false);
            }
            if (errors) {
                StringBuffer show = new StringBuffer("<div id=\"");
                show.append(prefixError(field.getName()));
                show.append("\" class=\"mm_check_error\">");
                Locale locale =  tag.getLocale();
                Iterator i = col.iterator();
                while (i.hasNext()) {
                    LocalizedString error = (LocalizedString) i.next();
                    show.append("<span>");
                    Xml.XMLEscape(error.get(locale), show);
                    show.append("</span>");
                }
                show.append("</div>");
                return show.toString();
            } else {
                return "";
            }
        }
    }

    /**
     * Sets the binary value. Also tries to set some fields which are generaly associated with the binary.
     * This could actually be moved to commit-processors of those fields.
     */
    protected void setValue(Node node, Field field, FileItem bytes) throws JspTagException {
        if (bytes.getSize() > 0) {
            String fileName = bytes.getName();
            int pos1 = fileName.lastIndexOf("/");
            int pos2 = fileName.lastIndexOf("\\");
            int pos = pos1 > pos2 ? pos1 : pos2;
            if (pos > 0) {
                fileName = fileName.substring(pos + 1);
                if ("".equals(fileName)) {
                    fileName = bytes.getName();
                }
            }
            String fileType = bytes.getContentType();

            try {
                node.setInputStreamValue(field.getName(), bytes.getInputStream(), bytes.getSize());
            } catch (java.io.IOException ioe) {
                throw new TaglibException(ioe);
            }
            ContextContainer cc = tag.getContextProvider().getContextContainer();
            log.debug("Filename : " + fileName);
            NodeManager nm = node.getNodeManager();
            // follwing stuff should probably be moved to commit-processors of the fields themselves.
            if (nm.hasField("mimetype") && (fileType != null) && (! fileType.equals("")) &&
                cc.find(tag.getPageContext(), prefix("mimetype")) == null
                ) {
                node.setValueWithoutProcess("mimetype", fileType);
            }
            Object specFileName = cc.find(tag.getPageContext(), prefix("filename"));
            if (nm.hasField("filename") &&
                fileName != null &&
                (! fileName.equals("")) &&
                (specFileName == null || specFileName.equals("") || specFileName.equals(node.getStringValue("filename")))
                ) {
                node.setValueWithoutProcess("filename", fileName);
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
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        FileItem bytes = (FileItem) getFieldValue(field);
        if (bytes == null){
            throw new BridgeException("getBytes(" + prefix(field.getName()) + ") returned null (node= " +  node.getNumber() +") field=(" + field + ") (Was your form  enctype='multipart/form-data' ?");
        }
        setValue(node, field, bytes);


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
