/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * MMObjectNodes can contain Binary data. These BYTE fields can be
 * retrieved, byt the node does not store them internally - nodes are cached, and
 * caching large amounts of binary data is bad for performance.
 * However, some classes keep a separate  cache for binaries (i.e. Images).
 * This class is meant to hold byte arrays while simultaneously keep data as to
 * which object it is part of. This allows for caching and passing binary values
 * between functions without loosing track of which image it is part of.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.7
 * @deprecated
 */
public class ByteFieldContainer {
    public int number = -1;
    public byte[] value = null;


    /**
     * Constructor of this container class.
     * @param number The node number of the node where the byte[] is belonging to, or -1 if the byte array is not yet associated with a node.
     * @param value  The byte array which this container is wrapping.
     */
    public ByteFieldContainer(int number, byte[] value) {
        this.number = number;
        this.value = value;
    }

}
