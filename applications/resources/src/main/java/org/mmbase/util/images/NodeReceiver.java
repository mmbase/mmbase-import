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

    @Override
    public OutputStream  getOutputStream() {
        return receiver;
    }
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(receiver.toByteArray());
    }
    @Override
    public void setSize(long size) {
        icacheNode.setSize(Imaging.FIELD_HANDLE, size);
    }
    @Override
    public long getSize() {
        return icacheNode.getSize(Imaging.FIELD_HANDLE);

    }
    @Override
    public boolean wantsDimension() {
        return icacheNode.getBuilder().hasField("height");
    }
    @Override
    public void setDimension(Dimension dim) {
        Dimension predicted = (Dimension) icacheNode.getFunctionValue("dimension", null);
        if (! predicted.equals(Dimension.UNDETERMINED)) {
            if (! predicted.equalsIgnoreRound(dim, 1)) {
                log.warn("Predicted dimension " + predicted + " was not equal to resulting dimension " + dim + " for icache " + icacheNode);
            } else if (! predicted.equals(dim)) {
                // It is equal ignoring round, but not precisely equal, that's not worth a warning.
                log.service("Predicted dimension " + predicted + " was not equal to resulting dimension " + dim + " for  icache " + icacheNode);
            }

        }
        icacheNode.setValue("height", dim.y);
        icacheNode.setValue("width", dim.x);
    }

    @Override
    public void ready() {
        icacheNode.setValue(Imaging.FIELD_HANDLE, receiver.toByteArray());
        icacheNode.commit();
    }

    @Override
    public int hashCode() {
        return  icacheNode.getStringValue(Imaging.FIELD_CKEY).hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof NodeReceiver) {
            NodeReceiver r = (NodeReceiver) o;
            return r.icacheNode.getNumber() == icacheNode.getNumber();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return icacheNode.getStringValue("id") + " --> " + icacheNode.getNumber() + " " + icacheNode.getStringValue(Imaging.FIELD_CKEY);
    }
}
