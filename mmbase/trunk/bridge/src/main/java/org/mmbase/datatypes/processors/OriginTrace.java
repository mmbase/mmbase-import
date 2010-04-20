/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.2
 */


public class OriginTrace implements CommitProcessor {

    private static final long serialVersionUID = 1L;

    @Override
    public void commit(Node node, Field field) {
        if (node.mayWrite() && node.isNull(field.getName())) {
            node.setValueWithoutProcess(field.getName(), Logging.stackTrace());
        }
    }

    @Override
    public String toString() {
        return "origin_trace";
    }
}
