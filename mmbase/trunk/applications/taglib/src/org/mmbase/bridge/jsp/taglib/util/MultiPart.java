/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.TaglibException;
import java.util.Enumeration;
import java.io.*;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Taglib needs to read Multipart request sometimes. Functionallity is centralized here.
 * @author Michiel Meeuwissen
 * @version $Id: MultiPart.java,v 1.4 2003-12-03 11:26:05 keesj Exp $
 **/

public class MultiPart {
    private static final Logger log = Logging.getLoggerInstance(MultiPart.class);

    static String MULTIPARTREQUEST_KEY = "__multipart";

    public static boolean isMultipart(PageContext pageContext) {
        String ct = ((HttpServletRequest)pageContext.getRequest()).getContentType();
        if (ct == null)
            return false;
        return (ct.indexOf("multipart/form-data") != -1);
    }

    public static MMultipartRequest getMultipartRequest(PageContext pageContext) {
        MMultipartRequest multipartRequest = (MMultipartRequest)pageContext.getAttribute(MULTIPARTREQUEST_KEY);
        if (multipartRequest == null) {
            log.debug("Creating new MultipartRequest");
            multipartRequest = new MMultipartRequest((HttpServletRequest)pageContext.getRequest(), ContextContainer.getDefaultCharacterEncoding(pageContext));
            log.debug("have it");

            if (log.isDebugEnabled()) {
                if (multipartRequest != null) {
                    Enumeration e = multipartRequest.getParameterNames();
                    StringBuffer params = new StringBuffer();
                    while (e.hasMoreElements()) {
                        params.append(e.nextElement()).append(",");
                    }
                    log.debug("multipart parameters: " + params);
                } else {
                    log.debug("not a multipart request");
                }
            }
            pageContext.setAttribute(MULTIPARTREQUEST_KEY, multipartRequest);
        }
        return multipartRequest;
    }

    // following class can be reimplemented when using other MultiPart post parser.

    // oreilly implmenetation:
    /* Doesn't seem to work (yet)
    class MMultipartRequest {
    
        private static final Logger log = Logging.getLoggerInstance(ContextTag.class.getName());
        private MultipartRequest o;
    
        MMultipartRequest(HttpServletRequest req) {
            try {
                o = new MultipartRequest(req, System.getProperty("java.io.tmpdir"));
            } catch (IOException e) {
                log.warn("" + e);
            }
        };
    
        public byte[] getBytes(String param) throws JspTagException {
            try {
                File f = o.getFile(param);
                FileInputStream fs = new FileInputStream(f);
    
                // read the file to a byte[].
                // little cumbersome, but well...
                // perhaps it would be littler so if we use MultipartParser
                // but this is simpler, because oreilly..MultipartRequest is like a request.
    
                byte[] buf = new byte[1000];
                Vector bufs = new Vector();
                int size = 0;
                int grow;
                while ((grow = fs.read(buf)) > 0) {
                size += grow;
                bufs.add(buf);
                buf = new byte[1000];
                }
                log.debug("size of image " + size);
                byte[] bytes = new byte[size];
                // copy the damn thing...
                Iterator i = bufs.iterator();
                int curpos = 0;
                while (i.hasNext()) {
                    byte[] tmp = (byte []) i.next();
                    System.arraycopy(tmp, 0, bytes, curpos, tmp.length);
                    curpos += tmp.length;
                }
                log.debug("size of image " + curpos);
                return bytes;
            }
            catch (FileNotFoundException e) {
                throw new JspTagException(e.toString());
            }
            catch (IOException e) {
                throw new JspTagException(e.toString());
            }
    
                //} catch (org.mmbase.util.PostValueToLargeException e) {
                //throw new JspTagException("Post value to large (" + e.toString() + ")");
                //}
        };
        public Object getParameterValues(String param) {
            Object result = null;
            Object[] resultvec = o.getParameterValues(param);
            if (resultvec != null) {
                if (resultvec.length > 1) {
                    Vector rresult = new Vector(resultvec.length);
                    for (int i=0; i < resultvec.length; i++) {
                        rresult.add(resultvec[i]);
                    }
                    result  = rresult;
                } else {
                    result = (String) resultvec[0];
                }
            }
            return result;
        };
        public Enumeration getParameterNames() {
            return o.getParameterNames();
        }
    }
    */
    // org.mmbase.util.HttpPost implementation

    static public class MMultipartRequest {
        private static final Logger log = Logging.getLoggerInstance(MultiPart.class);
        private org.mmbase.util.HttpPost o = null;
        private String coding = null;

        MMultipartRequest(HttpServletRequest req, String c) {
            log.debug("Creating HttpPost instance");
            o = new org.mmbase.util.HttpPost(req);
            coding = c;
            log.debug("created");
        }

        /**
         * Method to retrieve the byte's
         * @param param The name of the parameter
         * @return <code>null</code> if parameter not found, otherwise the bytes from the parameter
         */
        public byte[] getBytes(String param) throws JspTagException {
            try {
                log.debug("Getting bytes for " + param);
                if (o.isPostedToFile()) {
                    File file = new File(o.getPostParameterFile(param));
                    if (!file.exists()) {
                    	log.warn(file.getPath() + "does not exits");
                    }
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[200];
                    int readsize = 0;
                    while ((readsize = fis.read(buffer)) > 0) {
                        baos.write(buffer, 0, readsize);
                    }
                    fis.close();
                    return baos.toByteArray();
                } else {
                    return o.getPostParameterBytes(param);
                }
            } catch (Exception e) {
                log.warn(Logging.stackTrace(e));
                throw new TaglibException(e);
            }
        }

        /**
         * Method to retrieve the parameter
         * @param param The name of the parameter
         * @return <code>null</code> if parameter not found, when a single occurence of the parameter
         * the result as a <code>String</code> using the encoding specified. When if was a MulitParameter parameter, it will return
         * a <code>Vector</code> of <code>String</code>'s
         */
        public Object getParameterValues(String param) throws JspTagException {
            // this method will return null, if the parameter is not set...
            if (!o.getPostParameters().containsKey(param)) {
                return null;
            }
            // if it is a PostMultiParameter, return it..
            if (o.checkPostMultiParameter(param)) {
                log.debug("This is a multiparameter!");
                return o.getPostMultiParameter(param, coding);
            }

            // get the info as String...
            byte[] data = getBytes(param);
            if (data == null) {
                throw new JspTagException("retrieved no data for parameter:" + param);
            }

            String encoding = coding;
            // determine encoding
            // get first 40 bytes to determine if this is a xml type
            byte[] xmlbytes = new byte[60];
            int sz = data.length;
            if (sz > 60)
                sz = 60;
            System.arraycopy((byte[])data, 0, xmlbytes, 0, sz);
            String xmltext = new String(xmlbytes);
            if (xmltext.startsWith("<?xml")) {
                int i = xmltext.indexOf("encoding");
                log.info("i=*" + i + "*");
                if (i > 0) {
                    int j = xmltext.indexOf("?>", i);
                    log.info("j=*" + j + "*");
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
                log.warn(Logging.stackTrace(e));
                throw new TaglibException(e);
            }
        }

        public Enumeration getParameterNames() {
            return o.getPostParameters().keys();
        }
    }

}
