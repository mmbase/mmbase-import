/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.lang.Exception;
import javax.servlet.*;

/**
 * This Exception will occur if the upload file exceeds a certain size,
 * that's specified in WorkerPostHandler.
 */
public class FileToLargeException extends ServletException {
    /**
     * Create the exception
     */
    public FileToLargeException (String s) {
        super(s);
    }
}
