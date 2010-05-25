/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;

import org.mmbase.bridge.*;

/**
 * Like {@link NodeMap} but it uses {@link Node#getValueWithoutProcess} and {@link
 * Node#setValueWithoutProcess} to acces the wrapped node.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: NodeMap.java 35967 2009-06-11 06:57:24Z michiel $
 * @since   MMBase-1.9.2
 */

public class NodeMapWithoutProcess extends NodeMap {

    public NodeMapWithoutProcess(Node node) {
        super(node);
    }

    @Override
    protected Object getValueForMap(String field) {
        return getValueWithoutProcess(field);
    }

    @Override
    protected void setValueForMap(String field, Object value) {
        setValueWithoutProcess(field, value);
    }


}

