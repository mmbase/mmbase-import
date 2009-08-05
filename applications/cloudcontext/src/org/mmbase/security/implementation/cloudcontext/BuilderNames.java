/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: BuilderNames.java,v 1.3 2008-12-30 17:49:44 michiel Exp $
 * MMBase-1.9.1
 */
public class BuilderNames extends AbstractCollection<String> {

    private final Collection<NodeSearchQuery> backing;
    public BuilderNames(Collection<NodeSearchQuery> b) {
        backing = b;

    }
    public int size() {
        return backing.size();
    }
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            Iterator<NodeSearchQuery> i = BuilderNames.this.backing.iterator();
            public String next() {
                NodeSearchQuery q = i.next();
                return q == null ? null : q.getBuilder().getTableName();
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
