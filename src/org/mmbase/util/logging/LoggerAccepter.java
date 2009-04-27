/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.*;
import java.util.*;

/**
 * A LoggerAccepter is a class with a public 'addLogger' method. The class can log things to that
 * which may be of interest to the caller of that method.

 * It may well be implemented using {@ling ChainedLogger}.
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.9.1
 * @version $Id: LoggerAccepter.java,v 1.2 2009-04-27 12:03:59 michiel Exp $
 */
public interface LoggerAccepter {

    void  addLogger(Logger l);

    boolean containsLogger(Logger l);

    boolean removeLogger(Logger l);

}
