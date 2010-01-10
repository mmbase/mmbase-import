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
 * Wraps another Node, and adds 'isChangedByThis'.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class NodeChanger extends NodeWrapper {
    protected Map originalValues = null;

    public NodeChanger(Node node) {
        super(node);
    }

    protected void change(String fieldName) {
        if (originalValues == null) originalValues = new HashMap();
        if (! originalValues.containsKey(fieldName)) {
            originalValues.put(fieldName, node.getValueWithoutProcess(fieldName));
        }
    }

    public void setValue(String fieldName, Object value) { change(fieldName); super.setValue(fieldName, value); }
    public void setValueWithoutProcess(String fieldName, Object value) { change(fieldName); super.setValueWithoutProcess(fieldName, value); }
    public void setObjectValue(String fieldName, Object value) { change(fieldName); super.setObjectValue(fieldName, value); }
    public void setBooleanValue(String fieldName, boolean value) { change(fieldName); super.setBooleanValue(fieldName, value); }
    public void setNodeValue(String fieldName, Node value) { change(fieldName); super.setNodeValue(fieldName, value); }
    public void setIntValue(String fieldName, int value) { change(fieldName); super.setIntValue(fieldName, value); }
    public void setFloatValue(String fieldName, float value) { change(fieldName); super.setFloatValue(fieldName, value); }
    public void setDoubleValue(String fieldName, double value) { change(fieldName); super.setDoubleValue(fieldName, value); }
    public void setByteValue(String fieldName, byte[] value) { change(fieldName); super.setByteValue(fieldName, value); }
    public void setInputStreamValue(String fieldName, java.io.InputStream value, long size) { change(fieldName); super.setInputStreamValue(fieldName, value, size); }
    public void setLongValue(String fieldName, long value) { change(fieldName); super.setLongValue(fieldName, value); }
    public void setStringValue(String fieldName, String value) { change(fieldName); super.setStringValue(fieldName, value); }
    public void setDateValue(String fieldName, Date value) { change(fieldName); super.setDateValue(fieldName, value); }
    public void setListValue(String fieldName, List value) { change(fieldName); super.setListValue(fieldName, value); }
    /**
     * The isChanged method reflects the isChanged status of the underlying core node.isChanged. Before
     * commiting the node, you may want to check if _you_ changed to node, and not some other
     * thread. In that case you can first wrap your Node in a NodeChanger object.
     *
     */
    public boolean isChangedByThis() {
        return originalValues != null && super.isChanged();
    }

    public void cancelThis() {
        if (originalValues != null) {
            Iterator i = originalValues.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();                
                getNode().setValueWithoutProcess((String ) entry.getKey(), entry.getValue());
            }
                
        }
    }

}
