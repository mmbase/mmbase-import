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
 * @version $Id: CopyProcessor.java,v 1.1 2005-10-25 12:30:26 michiel Exp $
 * @since MMBase-1.7
 */

public class CopyProcessor implements Processor {

    private static final int serialVersionUID = 1;

    public final Object process(Node node, Field field, Object value) {
        return value;
    }        
}
