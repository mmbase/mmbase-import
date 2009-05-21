/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.io.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;


/**
 * The 'image conversion receiver' storing the result in an 'icaches' node.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class NodeReceiver implements ImageConversionReceiver {

    private static final Logger log = Logging.getLoggerInstance(NodeReceiver.class);

    private final MMObjectNode icacheNode;
    private final ByteArrayOutputStream receiver = new ByteArrayOutputStream();
    /**
     */
    public NodeReceiver(MMObjectNode icacheNode) {
        this.icacheNode = icacheNode;
    }

    /**
     * The icache node in which the conversion results must be stored.
     */
    public MMObjectNode getNode() {
        return icacheNode;
    }

    public OutputStream  getOutputStream() {
        return receiver;
    }
    public InputStream getInputStream() {
        return new ByteArrayInputStream(receiver.toByteArray());
    }
    public void setSize(long size) {
        icacheNode.setSize(Imaging.FIELD_HANDLE, size);
    }
    public long getSize() {
        return icacheNode.getSize(Imaging.FIELD_HANDLE);

    }
    public boolean wantsDimension() {
        return icacheNode.getBuilder().hasField("height");
    }
    public void setDimension(Dimension dim) {
        Dimension predicted = (Dimension) icacheNode.getFunctionValue("dimension", null);
        if (log.isDebugEnabled()) {
            if (! predicted.equals(dim)) {
                log.warn("Predicted dimension " + predicted + " was not equal to resulting dimension " + dim + " for  icache " + icacheNode);
            }
        }
        else {
            if (! predicted.equalsIgnoreRound(dim, 1)) {
                log.warn("Predicted dimension " + predicted + " was not equal to resulting dimension " + dim + " for icache " + icacheNode);
            }
        }
        icacheNode.setValue("height", dim.y);
        icacheNode.setValue("width", dim.x);
    }

    public void ready() {
        icacheNode.setValue(Imaging.FIELD_HANDLE, receiver.toByteArray());
        icacheNode.commit();
    }

    public int hashCode() {
        return  icacheNode.getStringValue(Imaging.FIELD_CKEY).hashCode();
    }
    public boolean equals(Object o) {
        if (o instanceof NodeReceiver) {
            NodeReceiver r = (NodeReceiver) o;
            return r.icacheNode.getNumber() == icacheNode.getNumber();
        } else {
            return false;
        }
    }

    // javadoc inherited (of Object)
    public String toString() {
        return icacheNode.getStringValue("id") + " --> " + icacheNode.getNumber() + " " + icacheNode.getStringValue(Imaging.FIELD_CKEY);
    }
}