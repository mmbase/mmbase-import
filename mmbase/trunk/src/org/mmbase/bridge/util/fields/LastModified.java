/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.*;
import java.util.Date;

/**
 * This processor can be used as a 'set' processor on a (datetime) field. The field will then be set
 * to the current time when the node is committed. If the field is set in another way, an exception is
 * thrown (in other words, the field is read only).
 *
 * @author Michiel Meeuwissen
 * @version $Id: LastModified.java,v 1.1 2004-09-17 09:29:25 michiel Exp $
 * @since MMBase-1.8
 */

public class LastModified implements CommitProcessor, Processor {

    public Object process(Node node, Field field, Object value) {        
        throw new BridgeException("You cannot change the field " + field.getName());
    }     
  
    public void commit(Node node, Field field) {
        node.getFieldValue(field).setDate(new Date());
    }        
}
