/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

/**
 * @javadoc
 * @deprecated-now not used by any CVS classes (probably local code)
 *
 * @author David V van Zeventer
 * @version $Id: DrivePropsNotFoundException.java,v 1.6 2003-08-29 09:36:52 pierre Exp $
 */
public class DrivePropsNotFoundException extends Exception {
	public String errval;
	public String explanation;

    //javadoc is inherited
    public DrivePropsNotFoundException() {
        super();
    }
    
    //javadoc is inherited
    public DrivePropsNotFoundException(String message) {
        super(message);
    }

    //javadoc is inherited
    public DrivePropsNotFoundException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public DrivePropsNotFoundException(String message, Throwable cause) {
        super(message,cause);
    }

    /**
     * @javadoc
     */
	public DrivePropsNotFoundException(String errval,String explanation) {
        super(errval);
		this.errval = errval;
		this.explanation = explanation;
	}
}

