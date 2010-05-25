/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * This processor can be used as a 'set' processor on a field.
 * If the field is set an exception is thrown (in other words, the
 * field is read only). In the builder XML you should also add the
 * 'readonly' attribute on the field, to indicate to generic editors
 * that this field should not be presented editable. 
 *
 * @author Nico Klasens
 * @version $Id$
 * @since MMBase-1.8
 */
public class Readonly implements Processor {

    private static final long serialVersionUID = 1L;

    /**
     * You can plug this in on every set-action besides 'object' which will make this
     * field unmodifiable, except for set(Object) itself (which is never used from editors).
     */
    public Object process(Node node, Field field, Object value) {
        throw new BridgeException("You cannot change the field " + field.getName());
    }

}
