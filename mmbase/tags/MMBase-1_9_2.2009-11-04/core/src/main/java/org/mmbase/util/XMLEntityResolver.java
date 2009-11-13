/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 *
 * @deprecated Use {@link org.mmbase.util.xml.EntityResolver}
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class XMLEntityResolver extends org.mmbase.util.xml.EntityResolver {

    public XMLEntityResolver() {
        super();
    }

    public XMLEntityResolver(boolean v) {
        super(v);
    }

    public XMLEntityResolver(boolean v, Class<?> base) {
        super(v, base);
    }
}
