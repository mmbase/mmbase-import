/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 *
 * @deprecated Use org.mmbase.util.xml.EntityResolver
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $Id: XMLEntityResolver.java,v 1.72 2008-09-03 23:41:47 michiel Exp $
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
