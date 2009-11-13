/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.apache.commons.fileupload.FileItem;

/**
 * To be used in conjuction with {@link DelayedSetter}. The process of this processor does nothing
 * unless an update for the field was scheduled, in which case the value associated with that update
 * is returned.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public class DelayedGetter implements Processor {

    private static final long serialVersionUID = 1L;

    public final Object process(Node node, Field field, Object value) {
        DelayedSetter.NodeField nf = new DelayedSetter.NodeField(node, field);
        DelayedSetter.Setter s = DelayedSetter.queued.get(nf);
        if (s != null) {
            Object v = s.getValue();
            if (value == null) return v;
            return Casting.toType(value.getClass(), node.getCloud(), v);
        } else {
            return value;
        }
    }

    public String toString() {
        return "delayedget";
    }
}


