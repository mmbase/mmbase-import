/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import java.util.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.*;
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.apache.commons.fileupload.*;

/**
 * Taglib needs to read Multipart request sometimes. Functionallity is centralized here.
 * @author Michiel Meeuwissen
 * @version $Id: MultiPart.java,v 1.18 2006-03-28 20:32:40 michiel Exp $
 **/

public class MultiPart {
    private static final Logger log = Logging.getLoggerInstance(MultiPart.class);

    static String MULTIPARTREQUEST_KEY = "__multipart";

    public static boolean isMultipart(PageContext pageContext) {
        String ct = ((HttpServletRequest)pageContext.getRequest()).getContentType();
        if (ct == null) {
            return false;
        }
        return (ct.startsWith("multipart/"));
    }

    public static MMultipartRequest getMultipartRequest(PageContext pageContext) {
        MMultipartRequest multipartRequest = (MMultipartRequest)pageContext.getAttribute(MULTIPARTREQUEST_KEY, PageContext.REQUEST_SCOPE);
        if (multipartRequest == null) {
            log.debug("Creating new MultipartRequest");
            multipartRequest = new MMultipartRequest((HttpServletRequest)pageContext.getRequest(), ContextContainer.getDefaultCharacterEncoding(pageContext));
            log.debug("have it");

            if (log.isDebugEnabled()) {
                if (multipartRequest != null) {
                    Iterator paramNames = multipartRequest.getParameterNames();
                    StringBuffer params = new StringBuffer();
                    while (paramNames.hasNext()) {
                        params.append(paramNames.next()).append(",");
                    }
                    log.debug("multipart parameters: " + params);
                } else {
                    log.debug("not a multipart request");
                }
            }
            pageContext.setAttribute(MULTIPARTREQUEST_KEY, multipartRequest, PageContext.REQUEST_SCOPE);
        } else {
            log.debug("Found multipart request on pageContext" + multipartRequest);
        }
        return multipartRequest;
    }

    static public class MMultipartRequest {

        private Map parametersMap = new HashMap();
        private String coding = null;

        MMultipartRequest(HttpServletRequest req, String c) {
            try {
                DiskFileUpload fu =  new DiskFileUpload();
                fu.setHeaderEncoding("ISO-8859-1"); // if incorrect, it will be fixed later.
                List fileItems = fu.parseRequest(req);
                for (Iterator i = fileItems.iterator(); i.hasNext(); ) {
                    FileItem fi = (FileItem)i.next();
                    if (fi.isFormField()) {
                        String value;
                        try {
                            value = fi.getString("ISO-8859-1");
                        } catch(java.io.UnsupportedEncodingException uee) {
                            log.error("could not happen, ISO-8859-1 is supported");
                            value = fi.getString();
                        }
                        Object oldValue = parametersMap.get(fi.getFieldName());
                        if (oldValue == null ) {
                            parametersMap.put(fi.getFieldName(), value);
                        } else if (!(oldValue instanceof FileItem)) {
                            List values;
                            if (oldValue instanceof String) {
                                values = new ArrayList();
                                values.add(oldValue);
                            } else {
                                values = (List)oldValue;
                            }
                            values.add(value);
                            parametersMap.put(fi.getFieldName(), values);
                        }
                    } else {
                        parametersMap.put(fi.getFieldName(), fi);
                    }
                }
            } catch (FileUploadException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            coding = c;
            log.debug("Created with encoding: " + coding);
        }

        /**
         * Method to retrieve the bytes of an uploaded file.
         * @param param The name of the parameter
         * @return <code>null</code> if parameter not found, otherwise the bytes from the parameter
         */
        public byte[] getBytes(String param) throws JspTagException {
            log.debug("Getting bytes for " + param);
            Object value = parametersMap.get(param);
            if (value instanceof FileItem) {
                try {
                    return ((FileItem)value).get();
                } catch (Exception e) {
                    throw new TaglibException(e);
                }
            } else {
                return null;
            }
        }
        public FileItem getFileItem(String param) throws JspTagException {
            log.debug("Getting outputstream for " + param);
            Object value = parametersMap.get(param);
            if (value instanceof FileItem) {
                try {
                    return (FileItem)value;
                } catch (Exception e) {
                    throw new TaglibException(e);
                }
            } else {
                return null;
            }
        }

        /**
         * Method to retrieve the bytes of an uploaded file as a string using eitehr the encoding specified in the file or
         * the default encoding.
         * @return <code>null</code> if parameter not found, otherwise the bytes from the parameter
         */
        protected String encodeBytesAsString(byte[] data) throws JspTagException {
            String encoding = coding;
            // get first 60 bytes to determine if this is a xml type
            byte[] xmlbytes = new byte[60];
            int sz = data.length;
            if (sz > 60) sz = 60;
            System.arraycopy(data, 0, xmlbytes, 0, sz);
            String xmltext = new String(xmlbytes);
            if (xmltext.startsWith("<?xml")) {
                int i = xmltext.indexOf("encoding");
                log.debug("i=*" + i + "*");
                if (i > 0) {
                    int j = xmltext.indexOf("?>", i);
                    log.debug("j=*" + j + "*");
                    if (j > i) {
                        // get trimmed attribute value
                        encoding = xmltext.substring(i + 8, j).trim();
                        // trim '='
                        encoding = encoding.substring(1).trim();
                        // trim quotes
                        encoding = encoding.substring(1, encoding.length() - 1).trim();
                    }
                }
            }
            try {
                return new String(data, encoding);
            } catch (java.io.UnsupportedEncodingException e) {
                throw new TaglibException(e);
            }
        }

        /**
         * @since MMBase-1.8
         */
        public boolean isFile(String param) {
            Object value = parametersMap.get(param);
            return value instanceof FileItem;
        }

        /**
         * Method to retrieve the parameter.
         * @param param The name of the parameter
         * @return <code>null</code> if parameter not found, when a single occurence of the parameter
         * the result as a <code>String</code> using the encoding specified. When if was a MultiParameter parameter, it will return
         * a <code>List</code> of <code>String</code>'s
         */
        public Object getParameterValues(String param) throws JspTagException {
            // this method will return null, if the parameter is not set...
            Object value = parametersMap.get(param);
            //log.debug("Got param " + param + " " + (value == null ? "NULL" : value.getClass().getName()) + " " + value);

            if (value instanceof FileItem) {
                try {
                    return encodeBytesAsString(((FileItem)value).get());
                } catch (Exception e) {
                    throw new TaglibException(e);
                }
            } else {
                return value;
            }
        }

        public Iterator getParameterNames() {
            return parametersMap.keySet().iterator();
        }
    }

}
