package org.mmbase.module.builders;

/**
 * &author David V van Zeventer
 * @version 5 Jan 1999
 */
public class GetDirFailedException extends Exception {
        public String errval;
        public String explanation;

        public GetDirFailedException(String errval,String explanation){
                this.errval = errval;
                this.explanation = explanation;
        }
}

