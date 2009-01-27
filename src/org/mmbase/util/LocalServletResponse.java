/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.*;
import java.util.*;
import java.io.*;


/**
 * @see LocalHttpServletRequest
 * @author Michiel Meeuwissen
 * @version $Id: LocalServletResponse.java,v 1.1 2009-01-27 18:06:18 michiel Exp $
 * @since MMBase-1.9.1
 */
public class LocalServletResponse implements ServletResponse {


    private final Writer writer;
    private ByteArrayOutputStream output = new ByteArrayOutputStream();

    public LocalServletResponse(Writer w) {
        writer = w;
    }

    private String characterEncoding = "UTF-8";
    private String contentType = "text/plain";
    private Locale locale = Locale.US;

    public void flushBuffer() {
        try {
            writer.write(new String(output.toByteArray(), characterEncoding));
        } catch (Exception e) {
            // shouldn't happen
        }
    }
    public int  getBufferSize() {
        return 0;
    }
    public String  getCharacterEncoding() {
        return characterEncoding;
    }
    public String  getContentType() {
        return contentType;
    }
    public Locale  getLocale() {
        return locale;
    }
    public ServletOutputStream  getOutputStream() {
        return new ServletOutputStream() {
            public void write(int b) throws IOException {
                output.write(b);
            }
            @Override public void flush() throws IOException {
                super.flush();
                writer.write(new String(output.toByteArray(), characterEncoding));
                output.reset();

            }
        };
    }
    public PrintWriter  getWriter() {
        return new PrintWriter(writer);
    }
    public boolean  isCommitted() {
        return false;
    }
    public void  reset() {
    }
    public void  resetBuffer() {
    }
    public void  setBufferSize(int size) {
    }
    public void  setCharacterEncoding(String charset) {
        characterEncoding = charset;
    }
    public void  setContentLength(int len) {
    }
    public void  setContentType(String type) {
        contentType = type;
    }
    public void  setLocale(Locale loc) {
        locale = loc;
    }



}
