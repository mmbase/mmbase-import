/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * Exception thrown by the methods that process search queries.
 *
 * @author Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class SearchQueryException extends Exception {

    //javadoc is inherited
    public SearchQueryException() {
        super();
    }

    //javadoc is inherited
    public SearchQueryException(String message) {
        super(message);
    }

    //javadoc is inherited
    public SearchQueryException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public SearchQueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
