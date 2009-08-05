/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

/**
 * Interface for transformations. Actually, since there are so many
 * types of transformers (byte/byte, char/char, char/byte), nothing
 * can be pointed out to be typical for Transformers. This interface
 * ended up a bit emptier than I anticipated.
 *
 * A Transformer must be serializable, because it is exposed through bridge (as 'processors').
 *
 * @author Michiel Meeuwissen
 */

public interface Transformer extends java.io.Serializable {

    String toString();
}
