/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Wrapper around the response. It collects all data that is sent to it, and makes it available
 * through a toString() method. It is used by taglib's Include-Tag, but it might find more general
 * use, outside taglib.
 *
 * @author Kees Jongenburger
 * @author Johannes Verelst
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: GenericResponseWrapper.java,v 1.2 2004-04-08 09:14:23 keesj Exp $
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper {
    private static final Logger log = Logging.getLoggerInstance(GenericResponseWrapper.class);

    private static String DEFAULT_CHARSET = "utf-8";
    private static String DEFAULT_CONTENTTYPE = "text/html;charset=" + DEFAULT_CHARSET;

    private PrintWriter         writer; 
    private StringWriter        string; // wrapped by writer

    private ServletOutputStream outputStream; // wrapped by outputStream
    private ByteArrayOutputStream   bytes;

    private String contentType       = DEFAULT_CONTENTTYPE;
    private String characterEncoding = DEFAULT_CHARSET;

    /**
     * Public constructor
     */
    public GenericResponseWrapper(HttpServletResponse resp) {
        super(resp);
    }

    /**
     * Return the OutputStream. This is a 'MyServletOutputStream'.
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new RuntimeException("Should use getOutputStream or getWriter");
        }
        if (outputStream == null) {
            bytes        = new ByteArrayOutputStream();
            outputStream = new MyServletOutputStream(bytes);
        }
        return outputStream;
    }

    /**
     * Return the PrintWriter
     */
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new RuntimeException("Should use getOutputStream or getWriter");
        }
        if (writer == null) {
            string = new StringWriter();
            writer  = new PrintWriter(string);
        }
        return writer;
    }


    /**
     * Sets the content type of the response being sent to the
     * client. The content type may include the type of character
     * encoding used, for example, text/html; charset=ISO-8859-4.  If
     * obtaining a PrintWriter, this method should be called first.
     */
    public void setContentType(String ct) {
        if (ct == null) {
            contentType = DEFAULT_CONTENTTYPE;
        } else {
            contentType = ct;
        }
        int i = contentType.indexOf("charset=");
        if (i >= 0) {
            characterEncoding = contentType.substring(i + 8);
        } else {
            characterEncoding = DEFAULT_CHARSET;
        }
        if (log.isDebugEnabled()) {
            log.debug("set contenttype of include page to: '" +  contentType + "' (and character encoding to '" + characterEncoding +  "')");
        }
    }

    /**
     * Returns the name of the charset used for the MIME body sent in this response.
     * If no charset has been assigned, it is implicitly set to ISO-8859-1 (Latin-1).
     * See <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2047</a> for more information about character encoding and MIME.
     * returns the encoding
     */
    public String getCharacterEncoding() {
        log.debug(characterEncoding);
        return characterEncoding;
    }

    /**
     * Return all data that has been written to the PrintWriter.
     */
    public String toString() {
        if (writer != null) {
            return string.toString();
        } else if (outputStream != null) {
            try {
                return new String(bytes.toByteArray(), getCharacterEncoding());
            } catch (Exception e) {
                return bytes.toString();
            }
        } else {
            return "";
        }
    }
    
}

/**
 * Implements SesrvletOutputStream.
 */
class MyServletOutputStream extends ServletOutputStream {

    private OutputStream stream;

    public MyServletOutputStream(OutputStream output) {
        stream = output;
    }

    public void write(int b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b, off, len);
    }
}

