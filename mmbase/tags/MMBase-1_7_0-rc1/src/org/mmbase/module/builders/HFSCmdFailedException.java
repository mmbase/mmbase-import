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
 * @version $Id: HFSCmdFailedException.java,v 1.5 2003-08-29 09:36:52 pierre Exp $
 */
public class HFSCmdFailedException extends Exception {
    public String errval;
    public String explanation;

    //javadoc is inherited
    public HFSCmdFailedException() {
        super();
    }
    
    //javadoc is inherited
    public HFSCmdFailedException(String message) {
        super(message);
    }

    //javadoc is inherited
    public HFSCmdFailedException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public HFSCmdFailedException(String message, Throwable cause) {
        super(message,cause);
    }

    /**
     * @javadoc
     */
    public HFSCmdFailedException(String errval,String explanation) {
        super(errval);
        this.errval = errval;
        this.explanation = explanation;
    }
}

