/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.xmlimporter;

/**
 * This exception is thrown when something goes wrong while parsing ot handling a Transacion by the xml importer.
 *
 * @author Rob van Maris: Finalist IT Group
 * @since MMBase-1.5
 * @version $Id$
 */
public class TransactionHandlerException extends Exception {
    String code = "";
    String fieldId = "";
    String fieldOperator = "";
    String objectOperator = "";
    String objectId = "";
    String transactionOperator = "";
    String transactionId = "";
    String exceptionPage = "";

    //javadoc is inherited
    public TransactionHandlerException() {
        super();
    }

    //javadoc is inherited
    public TransactionHandlerException(String message) {
        super(message);
    }

    //javadoc is inherited
    public TransactionHandlerException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public TransactionHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

}

