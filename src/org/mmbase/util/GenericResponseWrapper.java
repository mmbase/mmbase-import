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

import java.util.Locale;
import java.util.regex.*;

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
 * @version $Id$
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper {
    private static final Logger log = Logging.getLoggerInstance(GenericResponseWrapper.class);

    /**
     * If this pattern matched the first line of an InputStream then it is a XML. The encoding is in
     * matching group 1 (when using " as quote) or 2 (when using ' as quote)
     */
    private static final Pattern XMLHEADER = Pattern.compile("<\\?xml.*?(?:\\sencoding=(?:\"([^\"]+?)\"|'([^']+?)'))?\\s*\\?>.*", Pattern.DOTALL);


    private static String UNSET_CHARSET   = "iso-8859-1";
    public static String TEXT_XML_DEFAULT_CHARSET = "US-ASCII";

    private static String DEFAULT_CONTENTTYPE = "text/html";

    private static String[] IGNORED_HEADERS = new String[]{"Last-Modified", "ETag"};

    private PrintWriter         writer;
    private StringWriter        string; // wrapped by writer

    private ServletOutputStream outputStream; // wrapped by outputStream
    private ByteArrayOutputStream   bytes;

    private String contentType       = DEFAULT_CONTENTTYPE;
    private String characterEncoding = UNSET_CHARSET;

    private HttpServletResponse wrappedResponse;


    private String redirected = null;
    /**
     * Public constructor
     */
    public GenericResponseWrapper(HttpServletResponse resp) {
        super(resp);
        wrappedResponse = resp; // I don't understand why this object is not super.getResponse();

    }
    /**
     * Sets also a value for the characterEncoding which must be supposed.
     * Normally it would be determined automaticly right, but if for some reason it doesn't you can override it.
     */
    public GenericResponseWrapper(HttpServletResponse resp, String encoding) {
        this(resp);
        characterEncoding = encoding;
        wrappedResponse = resp; //
    }


    /**
     * Gets the response object which this wrapper is wrapping. You might need this when giving a
     * redirect or so.
     * @since MMBase-1.7.1
     */
    public HttpServletResponse getHttpServletResponse() {
        //return (HttpServletResponse) getResponse(); // shoudl work, I think, but doesn't
        HttpServletResponse response = wrappedResponse;
        while (response instanceof HttpServletResponseWrapper) {
            if (response instanceof GenericResponseWrapper) { // if this happens in an 'mm:included' page.
                response = ((GenericResponseWrapper) response).wrappedResponse;
            } else {
                response = (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
            }
        }
        return response;
    }

    private boolean mayAddHeader(String header) {
        for (int i=0; i<IGNORED_HEADERS.length; i++) {
            if (IGNORED_HEADERS[i].equalsIgnoreCase(header)) {
                return false;
            }
        }
        return true;
    }

    public void sendRedirect(String location) throws IOException  {
        redirected = location;
        getHttpServletResponse().sendRedirect(location);
    }
    /**
     * @since MMBase-1.8.5
     */
    public String getRedirected() {
        return redirected;
    }

    public void setStatus(int s) {
        getHttpServletResponse().setStatus(s);
    }

    public void addCookie(Cookie c) {
        getHttpServletResponse().addCookie(c);
    }

    public void setHeader(String header, String value) {
        if (mayAddHeader(header)) {
            getHttpServletResponse().setHeader(header,value);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    public void addDateHeader(String arg0, long arg1) {
        if (mayAddHeader(arg0)) {
            getHttpServletResponse().addDateHeader(arg0, arg1);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String arg0, String arg1) {
        if (mayAddHeader(arg0)) {
            getHttpServletResponse().addHeader(arg0, arg1);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    public void addIntHeader(String arg0, int arg1) {
        if (mayAddHeader(arg0)) {
            getHttpServletResponse().addIntHeader(arg0, arg1);
        }
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    public boolean containsHeader(String arg0) {
        return getHttpServletResponse().containsHeader(arg0);
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    public String encodeRedirectURL(String arg0) {
        return getHttpServletResponse().encodeRedirectURL(arg0);
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    public String encodeURL(String arg0) {
        return getHttpServletResponse().encodeURL(arg0);
    }
    /**
     * @see javax.servlet.ServletResponse#getLocale()
     */
    public Locale getLocale() {
        return getHttpServletResponse().getLocale();
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    public void sendError(int arg0, String arg1) throws IOException {
        getHttpServletResponse().sendError(arg0, arg1);
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    public void sendError(int arg0) throws IOException {
        getHttpServletResponse().sendError(arg0);
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    public void setDateHeader(String arg0, long arg1) {
        if (mayAddHeader(arg0)) {
            getHttpServletResponse().setDateHeader(arg0, arg1);
        }
    }
    /**
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    public void setIntHeader(String arg0, int arg1) {
        if (mayAddHeader(arg0)) {
            getHttpServletResponse().setIntHeader(arg0, arg1);
        }
    }
    /**
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    public void setLocale(Locale arg0) {
        getHttpServletResponse().setLocale(arg0);
    }

    /**
     * Return the OutputStream. This is a 'MyServletOutputStream'.
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream != null) return outputStream;

        if (writer != null) {
            outputStream = new MyServletOutputStream(new WriterOutputStream(writer, characterEncoding));
            return outputStream;
            //throw new RuntimeException("Should use getOutputStream _or_ getWriter");
        }
        if (log.isDebugEnabled()) {
            log.debug("Got outputstream", new Exception());
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
        if (writer != null) return writer;

        if (outputStream != null) {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, characterEncoding)));
            return writer;
            //throw new RuntimeException("Should use getOutputStream _or_ getWriter");
        }
        if (log.isDebugEnabled()) {
            log.debug("Got writer", new Exception());
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
            characterEncoding = getEncoding(ct); // gets char-encoding from content type
            if (characterEncoding == null) {
                characterEncoding = getDefaultEncoding(contentType);
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
        /*
        if (characterEncoding == UNSET_CHARSET && outputStream != null) {
            determinXMLEncoding();
        }
        */
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
        if (string != null) {
            return string.toString();
        } else if (outputStream != null) {
            try {
                byte[] allBytes;
                if (TEXT_XML_DEFAULT_CHARSET.equals(characterEncoding)) {
                    // see comments in getDefaultEncoding
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

    /**
     * Takes a String, which is considered to be (the first) part of an XML, and returns the
     * encoding (the specified one, or the XML default)
     * @return The XML Encoding, or <code>null</code> if the String was not recognized as XML (no &lt;?xml&gt; header found)
     * @since MMBase-1.7.1
     * @see #getXMLEncoding(byte[])
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
     * @return The XML Encoding, or <code>null</code> if the String was not recognized as XML (not &lt;?xml&gt; header found)
     * @since MMBase-1.7.1
     * @see #getXMLEncoding(String)
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
        return "UTF-8"; // cannot come here.
    }

    /**
     * Takes the value of a Content-Type header, and tries to find the encoding from it.
     * @since MMBase-1.7.1
     * @return The found charset if found, otherwise 'null'
     */
    public static String getEncoding(String contentType) {
        String contentTypeLowerCase = contentType.toLowerCase();
        int cs = contentTypeLowerCase.indexOf("charset=");
        if (cs > 0) {
            return  contentType.substring(cs + 8);
        } else {
            return null;
        }
    }

    /**
     * Supposes that no explicit charset is mentioned in a contentType, and returns a default. (UTF-8 or US-ASCII
     * for XML types and ISO-8859-1 otherwise).
     * @since MMBase-1.7.1
     * @return A charset.
     */
    public static String getDefaultEncoding(String contentType) {
        if (contentType.equals("text/xml")) {
            return TEXT_XML_DEFAULT_CHARSET; // = us-ascii, See
                                             // http://www.rfc-editor.org/rfc/rfc3023.txt.  We will
                                             // ignore it, because if not not ascii, it will never
                                             // work, and all known charset are superset of us-ascii
                                             // (so the response _is_ correct it will work).
        } else if ( contentType.equals("application/xml") || contentType.equals("application/xhtml+xml")) {
            return "UTF-8";
        } else {
            return "iso-8859-1";
        }

    }
}

/**
 * Implements ServletOutputStream.
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

