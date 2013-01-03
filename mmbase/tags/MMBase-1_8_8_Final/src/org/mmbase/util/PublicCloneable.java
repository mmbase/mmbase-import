/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * The java interface {@link java.lang.Cloneable} has no public methods. This interface is simply
 * Cloneable, but with the clone method public. So, if an object is PublicCloneable, you don't know
 * merely that it's Cloneable, but you can also actually <em>do</em> it...
 *
 * @since MMBase-1.8
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public interface PublicCloneable extends Cloneable {
    public Object clone();
}
