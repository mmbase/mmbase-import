/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;


/**
 * Take the systemId and converts it into a local file, using the MMBase config path
 *
 * @author Gerard van Enk
 * @author Michiel Meeuwissen
 * @version $Id: EntityResolver.java,v 1.2 2009-01-05 16:33:07 michiel Exp $
 */
public class EntityResolver extends org.mmbase.util.XMLEntityResolver {
    public EntityResolver() {
        super(true);
    }

    public EntityResolver(boolean v) {
        super(v);
    }

    public EntityResolver(boolean v, Class base) {
        super(v, base);
    }

}
