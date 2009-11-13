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
 * Wraps another Node, and adds '{@link #isChangedByThis}'.
 *
 * Before commiting the node, you may want to check if <em>you</em> changed to node, and not some
 * other thread. In that case you can first wrap your Node in a NodeChanger object.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class NodeChanger extends NodeWrapper {
    protected Map<String, Object> originalValues = null;

    public NodeChanger(Node node) {
        super(node);
    }

    protected void change(String fieldName) {
        if (originalValues == null) originalValues = new HashMap<String, Object>();
        if (! originalValues.containsKey(fieldName)) {
            originalValues.put(fieldName, node.getValueWithoutProcess(fieldName));
        }
    }

    @Override
    public void setValue(String fieldName, Object value) { change(fieldName); super.setValue(fieldName, value); }
    @Override
    public void setValueWithoutProcess(String fieldName, Object value) { change(fieldName); super.setValueWithoutProcess(fieldName, value); }
    @Override
    public void setObjectValue(String fieldName, Object value) { change(fieldName); super.setObjectValue(fieldName, value); }
    @Override
    public void setBooleanValue(String fieldName, boolean value) { change(fieldName); super.setBooleanValue(fieldName, value); }
    @Override
    public void setNodeValue(String fieldName, Node value) { change(fieldName); super.setNodeValue(fieldName, value); }
    @Override
    public void setIntValue(String fieldName, int value) { change(fieldName); super.setIntValue(fieldName, value); }
    @Override
    public void setFloatValue(String fieldName, float value) { change(fieldName); super.setFloatValue(fieldName, value); }
    @Override
    public void setDoubleValue(String fieldName, double value) { change(fieldName); super.setDoubleValue(fieldName, value); }
    @Override
    public void setByteValue(String fieldName, byte[] value) { change(fieldName); super.setByteValue(fieldName, value); }
    @Override
    public void setInputStreamValue(String fieldName, java.io.InputStream value, long size) { change(fieldName); super.setInputStreamValue(fieldName, value, size); }
    @Override
    public void setLongValue(String fieldName, long value) { change(fieldName); super.setLongValue(fieldName, value); }
    @Override
    public void setStringValue(String fieldName, String value) { change(fieldName); super.setStringValue(fieldName, value); }
    @Override
    public void setDateValue(String fieldName, Date value) { change(fieldName); super.setDateValue(fieldName, value); }
    @Override
    public void setListValue(String fieldName, List value) { change(fieldName); super.setListValue(fieldName, value); }

    /**
     * The {@link #isChanged} method reflects the isChanged status of the underlying core node.isChanged,
     * this one does that too, but only return true, if this instance performed this change.
     */
    public boolean isChangedByThis() {
        return originalValues != null && super.isChanged();
    }

    public void cancelThis() {
        if (originalValues != null) {
            for (Map.Entry<String, Object> entry : originalValues.entrySet()) {
                getNode().setValueWithoutProcess(entry.getKey(), entry.getValue());
            }

        }
    }

}
