/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import javax.servlet.ServletException;


/**
 * This Exception will occur if the upload file exceeds a certain size,
 * that's specified in HttpPost.
 * @deprecated-now not used by any CVS classes (probably local code)
 * @rename FileTooLargeException
 * @move org.mmbase.servlet
 *
 * @author vpro
 * @version $Id: FileToLargeException.java,v 1.8 2004-09-30 14:07:10 pierre Exp $
 */
public class FileToLargeException extends ServletException {

    //javadoc is inherited
    public FileToLargeException() {
        super();
    }

    //javadoc is inherited
    public FileToLargeException(String message) {
        super(message);
    }

    //javadoc is inherited
    public FileToLargeException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public FileToLargeException(String message, Throwable cause) {
        super(message,cause);
    }

}
