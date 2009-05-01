/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.logging.*;

/**
 * A confirmed password datatype must have the same value as another field of the node (and makes
 * only sense as a field of a node).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class ConfirmPasswordDataType extends CheckEqualityDataType {
    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public ConfirmPasswordDataType(String name) {
        super(name);
        setField("password");
        setPassword(true);
    }


}
