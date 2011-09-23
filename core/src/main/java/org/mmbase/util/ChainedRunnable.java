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
 * @version $Id: ChainedWriter.java 34900 2009-05-01 16:29:42Z michiel $
 */
public class ChainedRunnable implements Runnable {

    private final List<Runnable> runnables = new ArrayList<Runnable>();
    public ChainedRunnable(Runnable... ls) {
        for (Runnable r : ls) {
            add(r);
        }
    }

    public ChainedRunnable add(Runnable r) {
        runnables.add(r);
        return this;
    }

    public void run() {
        for (Runnable r : runnables) {
            r.run();
        }
    }
}
