/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * The Processor that does nothing.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public final class CopyProcessor implements Processor {

    private static final long serialVersionUID = 1L;

    private static CopyProcessor instance = new CopyProcessor();
    public static CopyProcessor getInstance() {
        return instance;
    }

    public final Object process(Node node, Field field, Object value) {
        return value;
    }

    public String toString() {
        return "COPY";
    }
}


