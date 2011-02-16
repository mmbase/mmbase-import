/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;

/**
 * A node wrapper in which fields are explicitely marked as 'changed' (even though they may actually not be).
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class ChangedNode extends NodeWrapper {

    private final Set<String> changedFields = new HashSet<String>();
    public ChangedNode(Node node, String... fields) {
        super(node);
        changedFields.addAll(Arrays.asList(fields));
    }

    @Override
    public boolean isChanged(String fieldName) {
        return changedFields.contains(fieldName) || super.isChanged(fieldName);
    }
    @Override
    public boolean isChanged() {
        return changedFields.size() > 0 || super.isChanged();
    }
    @Override
    public Set<String> getChanged() {
        Set<String> c = new HashSet<String>();
        c.addAll(changedFields);
        c.addAll(super.getChanged());
        return c;
    }

}
