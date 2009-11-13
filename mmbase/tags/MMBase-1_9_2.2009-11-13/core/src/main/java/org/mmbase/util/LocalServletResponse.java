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
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class LocalServletResponse implements ServletResponse {


    private final Writer writer;
    private final OutputStream output;
    private PrintWriter pwriter;


    public LocalServletResponse(Writer w) {
        writer = w;
        output = new ByteArrayOutputStream();
    }

    public LocalServletResponse(OutputStream output) {
        this.output = output;
        writer = null;
    }

    private String characterEncoding = "UTF-8";
    private String contentType = "text/plain";
    private Locale locale = Locale.US;

    public void flushBuffer() {
        if (writer != null) {
            try {
                writer.write(new String(((ByteArrayOutputStream) output).toByteArray(), characterEncoding));
                ((ByteArrayOutputStream) output).reset();
            } catch (Exception e) {
                // shouldn't happen
            }
        } else {
            if (pwriter != null) {
                pwriter.flush();
            }
            try {
                output.flush();
            } catch (IOException ioe) {
            }
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
                flushBuffer();
            }
        };
    }
    public PrintWriter  getWriter() {
        if (pwriter == null) {
            if (writer != null) {
                pwriter = new PrintWriter(writer);
            } else {
                pwriter = new PrintWriter(output, true);
            }
        }
        return pwriter;
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
