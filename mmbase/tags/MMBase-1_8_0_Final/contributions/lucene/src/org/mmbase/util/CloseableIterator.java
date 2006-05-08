/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.*;

/**
 * An iterator which may need closing (e.g. when wrapping a ResultSet).
 *
 * @version $Id: CloseableIterator.java,v 1.1 2005-12-27 15:45:06 michiel Exp $
 * @author Michiel Meeuwissen
 **/
public interface CloseableIterator extends Iterator {
    void close();
}


