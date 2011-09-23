/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

/**
 * A LoggerAccepter is a class with a public 'addLogger' method. The class can log things to the as
 * such provided {@link Logger}'s which may be of interest to the caller of that method.

 * It may well be implemented using {@link ChainedLogger}.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.9.1
 * @version $Id$
 */
public interface LoggerAccepter {

    void  addLogger(Logger l);

    boolean containsLogger(Logger l);

    boolean removeLogger(Logger l);

}
