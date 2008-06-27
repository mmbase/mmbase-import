/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Class for storing error information useful in parsing.
 * Information that can be stored includes the error type, column and
 * line number of the parsed text where the error occurred, and a message.
 * used by the org.mmbase.module.Config module when parsing XML files.
 *
 * @application Config
 * @author vpro
 * @version $Id: ErrorStruct.java,v 1.6 2008-03-25 21:00:24 nklasens Exp $
 */
public class ErrorStruct {

    // error type
    String errorType;
    // line number
    int line;
    // column number
    int col;
    // error message
    String msg;

    /**
     * Creates an error structure, with errortype "none".
     * @param line the line number where the error occurred
     * @param col the column number where the error occurred
     * @param msg the error message
     */
    public ErrorStruct(int line, int col, String msg) {
    this("none",line,col,msg);
    }

    /**
     * Creates an error structure.
     * @param errorType the type of error,
     * @param line the line number where the error occurred
     * @param col the column number where the error occurred
     * @param msg the error message
     */
    public ErrorStruct(String errorType, int line, int col, String msg) {
    this.errorType = errorType;
    this.line = line;
    this.col = col;
    this.msg = msg;
    }

    /**
     * Returns the error type.
     * Values that might be expected are "warning", "error" and "fatal".
     */
    public String getErrorType() {
    return errorType;
    }

    /**
     * Returns the line number in the parsed source (file or textbuffer)
     * where the error occurred.
     */
    public int getLineNumber() {
    return line;
    }

    /**
     * Returns the column number in the parsed source (file or textbuffer)
     * where the error occurred.
     */
    public int getColumnNumber() {
    return col;
    }

    /**
     * Returns a more detailed error message.
     */
    public String getMessage() {
    return msg;
    }

    /**
     * prints the ErrorStruct
     */
    public String toString() {
        return "ErrorStruct: type="+errorType+" line="+line+" position="+col+" message="+msg;
    }
}
