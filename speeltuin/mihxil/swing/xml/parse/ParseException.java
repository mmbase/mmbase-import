/** 
 *  Used to report errors which may occur during parsing.
 *  @author Tom Gibara
 *  @author Michiel Meeuwissen
 *  @since MMBAse-1.6
 */

package org.mmbase.util.swing.xml.parse;

public class ParseException extends java.io.IOException {


    /** Constructs an exception with a default description.
     */

    public ParseException() { super("unspecified error"); }


    /** Constructs an exception with the specified description.
     *  @param msg  a description of the exception created
     */

    public ParseException(String msg) { super(msg); }

}
