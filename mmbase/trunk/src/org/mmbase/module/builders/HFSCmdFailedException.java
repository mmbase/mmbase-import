package org.mmbase.module.builders;

/**
 * @author David V van Zeventer
 * @version 6 Jan 1999
 */
public class HFSCmdFailedException extends Exception {
        public String errval;
        public String explanation;

        public HFSCmdFailedException(String errval,String explanation){
                this.errval = errval;
                this.explanation = explanation;
        }
}

