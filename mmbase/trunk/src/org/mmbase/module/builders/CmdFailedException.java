package org.mmbase.module.builders;

/**
 * @author David V van Zeventer
 * @version 25 Jan 1999
 */
public class CmdFailedException extends Exception {
        public String errval;
        public String explanation;

        public CmdFailedException(String errval,String explanation){
                this.errval = errval;
                this.explanation = explanation;
        }
}
