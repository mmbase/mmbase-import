/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;


import org.mmbase.bridge.Node;

/**
 * Interface for doing field processing.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Processor.java,v 1.1 2003-12-05 09:23:25 michiel Exp $
 * @since MMBase-1.7
 */

public interface Processor {
    
    Object process(Node node, Object value);

}
