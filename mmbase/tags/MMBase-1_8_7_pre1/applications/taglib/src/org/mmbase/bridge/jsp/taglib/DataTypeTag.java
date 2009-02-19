/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.datatypes.*;
import org.mmbase.datatypes.util.xml.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.containers.*;

import org.mmbase.util.XMLEntityResolver;
import org.mmbase.util.XMLErrorHandler;

import java.io.StringReader;


import org. w3c.dom.Element;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;

import javax.servlet.jsp.*;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This tags produces request scoped new datatypes. (To be used in conjuction with mm:fieldinfo datatype='')
 * @author Michiel Meeuwissen
 * @version $Id: DataTypeTag.java,v 1.1.2.3 2008-08-19 11:44:16 michiel Exp $
 * @since MMBase-1.8.7
 */
public class DataTypeTag extends CloudReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(DataTypeTag.class);

    public static final String KEY = "org.mmbase.taglib.datatypecollector";
    public static final int SCOPE = PageContext.REQUEST_SCOPE;

    private Attribute base = Attribute.NULL;
    private Attribute nodeManager = Attribute.NULL;
    private Attribute field = Attribute.NULL;

    public void setBase(String b) throws JspTagException {
        base = getAttribute(b);
    }

    public void setNodemanager(String n) throws JspTagException {
        nodeManager = getAttribute(n);
    }
    public void setField(String f) throws JspTagException {
        field = getAttribute(f);
    }

    protected DataTypeCollector getCollector() {
        DataTypeCollector collector = (DataTypeCollector) pageContext.getAttribute(KEY, SCOPE);
        if (collector == null) {
            collector = new DataTypeCollector(new Object());
            pageContext.setAttribute(KEY, collector, SCOPE);

        }
        return collector;
    }
    /**
     *
     **/
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_BUFFERED;
    }
    private String body;

    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) body = bodyContent.getString();
        return SKIP_BODY;
    }

    private static String ATTR =
        "xmlns=\"" + DataTypeReader.NAMESPACE_DATATYPES + "\" " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
        "xsi:schemaLocation=\"" + DataTypeReader.NAMESPACE_DATATYPES + " " + DataTypeReader.NAMESPACE_DATATYPES + ".xsd\"";

    protected BasicDataType getBaseDataType(DataTypeCollector collector) throws JspTagException {
        if (base.getString(this).equals("")) {
            String nm = nodeManager.getString(this);
            if ("".equals(nm)) throw new JspTagException("Should specify either 'base' or 'nodemanager' attribute");
            String fn = field.getString(this);
            if ("".equals(fn)) throw new JspTagException("Attribute 'field' is required when using 'nodemanager' attribute");
            return (BasicDataType) getCloudVar().getNodeManager(nm).getField(fn).getDataType();
        } else {
            return collector.getDataType(base.getString(this));
        }
    }

    public int doEndTag() throws JspTagException {
        StringBuffer buf = new StringBuffer("<datatype id=\"");
        buf.append(getId()).append("\" ").append(ATTR).append(">");
        if (body != null) buf.append(body);
        buf.append("</datatype>");
        if (log.isTraceEnabled()) {
            log.trace("Parsing: " + buf);
        }
        try {
            XMLErrorHandler errorHandler = new XMLErrorHandler(false, XMLErrorHandler.WARNING);
            XMLEntityResolver resolver = new XMLEntityResolver(true, DataTypeReader.class);
            DocumentBuilder dbuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(true, true,
                                                                                             errorHandler, resolver);
            Element element = dbuilder.parse(new InputSource(new StringReader(buf.toString()))).getDocumentElement();
            DataTypeCollector collector = getCollector();
            BasicDataType dt = DataTypeReader.readDataType(element, getBaseDataType(collector), collector).dataType;
            collector.finish(dt);
            getContextProvider().getContextContainer().register(getId(), dt);
            if (log.isDebugEnabled()) {
                log.debug("Created " + dt);
                log.debug("In " + collector.getDataTypes());
            }
            BasicDataType old  = collector.addDataType(dt);

        } catch (org.mmbase.datatypes.util.xml.DependencyException de) {
            throw new TaglibException(de);
        } catch (org.xml.sax.SAXException se) {
            throw new TaglibException("" + buf + ":" + se.getMessage(), se);
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }
        body = null;
        return super.doEndTag();
    }




}

