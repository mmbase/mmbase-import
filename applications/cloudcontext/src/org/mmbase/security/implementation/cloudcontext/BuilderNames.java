/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.module.core.MMObjectBuilder;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: BuilderNames.java,v 1.2 2008-12-29 09:27:42 michiel Exp $
 * MMBase-1.9.1
 */
public class BuilderNames extends AbstractCollection<String> {

    private final Collection<MMObjectBuilder> backing;
    public BuilderNames(Collection<MMObjectBuilder> b) {
        backing = b;

    }
    public int size() {
        return backing.size();
    }
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            Iterator<MMObjectBuilder> i = BuilderNames.this.backing.iterator();
            public String next() {
                MMObjectBuilder bul = i.next();
                return bul == null ? null : bul.getTableName();
            }
            public boolean hasNext() {
                return i.hasNext();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
