/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * This commit processor can be used to check how often a commitprocessor is called.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public class CountCommitProcessor implements CommitProcessor {

    public static int count = 0;
    public static int changed = 0;

    public final void commit(Node node, Field field) {
        count++;
        if (node.isChanged()) {
            changed++;
            node.setIntValue(field.getName(), changed);
        }
    }
}


