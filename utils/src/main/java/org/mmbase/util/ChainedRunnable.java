/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;
import java.util.*;

/**
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.9.5
 * @version $Id$
 */
public class ChainedRunnable implements Runnable {

    private final List<Runnable> runnables = new ArrayList<Runnable>();
    public ChainedRunnable(Runnable... ls) {
        for (Runnable r : ls) {
            add(r);
        }
    }

    public final ChainedRunnable add(Runnable r) {
        runnables.add(r);
        return this;
    }

    public void run() {
        for (Runnable r : runnables) {
            r.run();
        }
    }
}
