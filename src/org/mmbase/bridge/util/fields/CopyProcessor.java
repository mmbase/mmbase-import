/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.Node;
/**
 * The Processor that does nothing.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CopyProcessor.java,v 1.1 2003-12-05 09:23:24 michiel Exp $
 * @since MMBase-1.7
 */

public class CopyProcessor {


    public final Object process(Node node, Object value) {
        return value;
    }        
}
