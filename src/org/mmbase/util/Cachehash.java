/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * Cachehash, a hashtable that keeps info on max size and type, to be able to
 * let the parent control it better for cache/writeback caches.
 *
 * @deprecated-now Not used anyhwere
 * @author Daniel Ockeloen
 * @version $Id: Cachehash.java,v 1.4 2004-09-29 14:29:23 pierre Exp $
 */
public class Cachehash extends Hashtable {

    // what do these do ???
    public static final int TEMP 		= 0;
    public static final int SAVE 		= 1;
    public static final int DELAYEDSAVE = 2;

    private int type;
    private int max;

    public Cachehash(int type, int max) {
        this.type = type;
        this.max = max;
    }

    public Cachehash(int type, int max, Hashtable newHash) {
        super(newHash);
        this.type = type;
        this.max = max;
    }

    public int getType() {
        return type;
    }

    public int getMax() {
        return max;
    }
}
