/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * MMObjectNodes can contain Binary data. These BYTE fields can be
 * retrieved, byt teh node does not sstore them internally - nodes are cached, and
 * caching large amounts of binary data is bad for performance.
 * However, some classes keep a sepearta e cacahe for binaries (i.e.Images).
 * This class is meant to hold byte arrays while simultaneously keep data as to
 * which object it is part of. This allows for caching and passing binary values
 * between functions without loosing track of which image it is part of.
 *
 * @author Pierre van Rooden
 * @version $Id: ByteFieldContainer.java,v 1.1 2004-02-23 19:04:59 pierre Exp $
 */
public class ByteFieldContainer {
    public int number = -1;
    public byte[] value = null;

    public ByteFieldContainer(int number, byte[] value) {
        this.number = number;
        this.value = value;
    }

}
