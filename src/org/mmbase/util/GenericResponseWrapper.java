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
import java.util.regex.*;
import java.util.regex.Matcher;

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
 * @version $Id: GenericResponseWrapper.java,v 1.7 2004-06-29 09:19:49 michiel Exp $
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper {
    private static final Logger log = Logging.getLoggerInstance(GenericResponseWrapper.class);


    private static final Pattern XMLHEADER = Pattern.compile("<\\?xml.*?(?:\\sencoding=(?:\"([^\"]+?)\"|'([^']+?)'))?\\s*\\?>.*", Pattern.DOTALL);    
    /**
     * Takes a String, which is considered to be (the first) part of an XML, and returns the encoding.
     * @returns The XML Encoding, or null if the String was not recognized as XML (not <?xml header found)
     * @since MMBase-1.7.1
     */
    public static final String getXMLEncoding(String xmlString) {
        Matcher m = XMLHEADER.matcher(xmlString);
        if (! m.matches()) {
            return null; // No <? xml header found, this file is probably not XML.
        }  else {
            String encoding = m.group(1);
            if (encoding == null) encoding = m.group(2);
            if (encoding == null) encoding = "UTF-8"; // default encoding for XML.
            return encoding;
        }
    }
    /**
     * Takes a ByteArrayInputStream, which is considered to be (the first) part of an XML, and returns the encoding.
     * @returns The XML Encoding, or null if the String was not recognized as XML (not <?xml header found)
     * @since MMBase-1.7.1
     */
    public static String getXMLEncoding(byte[] allBytes) {
        byte[] firstBytes = allBytes;
        if (allBytes.length > 100) {
            firstBytes = new byte[100];
            System.arraycopy(allBytes, 0, firstBytes, 0, 100);
        }
        try {
            return  getXMLEncoding(new String(firstBytes, "US-ASCII"));
        } catch (java.io.UnsupportedEncodingException uee) {
            // cannot happen, US-ASCII is known
        }
        return "UTF-8";
    }


    private static String DEFAULT_CHARSET = "utf-8";
    private static String XML_DEFAULT_CHARSET = "utf-8";
    private static String DEFAULT_CONTENTTYPE = "text/html;charset=" + DEFAULT_CHARSET;

    private PrintWriter         writer; 
    private StringWriter        string; // wrapped by writer

    private ServletOutputStream outputStream; // wrapped by outputStream
    private ByteArrayOutputStream   bytes;

    private String contentType       = DEFAULT_CONTENTTYPE;
    private String characterEncoding = DEFAULT_CHARSET;

    private HttpServletResponse wrappedResponse;

    /**
     * Public constructor
     */
    public GenericResponseWrapper(HttpServletResponse resp) {
        super(resp);        
        wrappedResponse = resp; // I don't understand why this object is not super.getResponse();

    }

    /**
     * Gets the response object which this wrapper is wrapping. You might need this when giving a
     * redirect or so.
     * @since MMBase-1.7.1
     */
    public HttpServletResponse getHttpServletResponse() {
        //return (HttpServletResponse) getResponse(); // shoudl work, I think, but doesn't
        HttpServletResponse response = wrappedResponse;
        while (response instanceof GenericResponseWrapper) { // if this happens in an 'mm:included' page.
            response = ((GenericResponseWrapper) response).wrappedResponse;
        } 
        return response;
    }


    public void sendRedirect(String location) throws IOException  {
        checkWritten();
        getHttpServletResponse().sendRedirect(location);
    }
    public void setStatus(int s) {
        checkWritten();
        getHttpServletResponse().setStatus(s);
    }

    public void addCookie(Cookie c) {
        checkWritten();
        getHttpServletResponse().addCookie(c);
    }
    public void setHeader(String header, String value) {
        getHttpServletResponse().setHeader(header,value);
    }


    protected void checkWritten() { 
        if (writer != null || outputStream != null) {
            log.error("Allready written headers, perhaps you need to increase the 'buffer' of your JSP (with the @page directive)");
            log.debug(Logging.stackTrace());
            //throw new RuntimeException("Allready written");
        }
    }

    /**
     * Return the OutputStream. This is a 'MyServletOutputStream'.
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new RuntimeException("Should use getOutputStream _or_ getWriter");
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
            throw new RuntimeException("Should use getOutputStream _or_ getWriter");
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
            if (contentType.equals("text/xml")) {
                // now it get's really interesting, the encoding is present on the _first line of the body_.
                characterEncoding = XML_DEFAULT_CHARSET; // toString is UTF-8, but it is an inidcation that it could be considered later
            } else {
                characterEncoding = DEFAULT_CHARSET;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("set contenttype of include page to: '" +  contentType + "' (and character encoding to '" + characterEncoding +  "')");
        }
    }

    /**
     * Returns the name of the charset used for the MIME body sent in this response.
     * If no charset has been assigned, it is implicitly set to ISO-8859-1 (Latin-1).
     * See <a href="http://www.ietf.org/rfc/rfc2047.txt">RFC 2047</a> for more information about character encoding and MIME.
     * returns the encoding
     */
    public String getCharacterEncoding() {
        log.debug(characterEncoding);
        if (characterEncoding == XML_DEFAULT_CHARSET && outputStream != null) {
            determinXMLEncoding();
        }
        return characterEncoding;
    }



    protected byte[] determinXMLEncoding() {
        byte[] allBytes = bytes.toByteArray();
        characterEncoding = getXMLEncoding(allBytes);
        if (characterEncoding == null) characterEncoding = "UTF-8"; // missing <?xml header, but we _know_ it is XML.
        return allBytes;
    }

    /**
     * Return all data that has been written to the PrintWriter.
     */
    public String toString() {
        if (writer != null) {
            return string.toString();
        } else if (outputStream != null) {
            try {
                byte[] allBytes;
                if (characterEncoding == XML_DEFAULT_CHARSET) {
                    allBytes = determinXMLEncoding();
                } else {
                    allBytes = bytes.toByteArray();
                }
                return new String(allBytes, getCharacterEncoding());
            } catch (Exception e) {
                return bytes.toString();
            }
        } else {
            return "";
        }
    }

    /*
    public static void  main(String[] argv) {
        log.info("Found encoding " + getXMLEncoding(argv[0]));
    }
    */

    
    
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

