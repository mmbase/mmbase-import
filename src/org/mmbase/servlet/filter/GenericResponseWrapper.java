/**
 
 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

*/
package org.mmbase.servlet.filter; 
    
import javax.servlet.ServletOutputStream; 
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpServletResponseWrapper; 
import java.io.ByteArrayOutputStream; 
import java.io.PrintWriter; 

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Generic response wrapper
 *
 * For futher explination, 
 *  see http://www.orionserver.com/tutorials/filters
 *
 * @author Marcel Maatkamp, VPRO Netherlands (marmaa_at_vpro.nl)
 * @version $Version$
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper { 
    static Logger log = Logging.getLoggerInstance(GenericResponseWrapper.class.getName());

    private ByteArrayOutputStream output; 
    private int contentLength; 
    private String contentType; 
 
    public GenericResponseWrapper(HttpServletResponse response) { 
        super(response); 
        output = new ByteArrayOutputStream(); 
    } 
 
    public byte[] getData() { 
        return output.toByteArray(); 
    } 
 
    public ServletOutputStream getOutputStream() { 
        return new FilterServletOutputStream(output); 
    } 
 
    public void setContentLength(int length) { 
        this.contentLength = length; 
        super.setContentLength(length); 
    } 
 
    public int getContentLength() { 
        return contentLength; 
    } 
 
    public void setContentType(String type) { 
        this.contentType = type; 
        super.setContentType(type); 
    } 
 
    public String getContentType() { 
        return contentType; 
    } 
 
    public PrintWriter getWriter() { 
        return new PrintWriter(getOutputStream(), true); 
    } 
} 
