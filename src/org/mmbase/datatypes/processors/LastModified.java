/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import java.util.Date;

/**
 * This processor can be used as a 'commit' processor on a (datetime) field. The field will then be set
 * to the current time when the node is committed.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 * @see   LastModifier
 */

public class LastModified implements CommitProcessor {

    private static final long serialVersionUID = 1L;

    public void commit(Node node, Field field) {
        if (node.mayWrite()) {
            node.setValueWithoutProcess(field.getName(), new Date());
        }
    }

    public String toString() {
        return "lastmodified";
    }
}
