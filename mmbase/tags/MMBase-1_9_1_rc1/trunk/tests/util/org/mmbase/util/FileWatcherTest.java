/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;


import java.io.*;
import java.util.*;

import junit.framework.TestCase;

/**
 * Test the working of the ResourceLoader.
 *
 * <ul>
 * <li>tests if the resource loader can run when mmbase is not started</li>
 * </ul>
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class FileWatcherTest extends TestCase {

    public static int baseSize = FileWatcher.getFileWatchers().size();

    /**
     * perform lookup of non existing resource
     */
    public void test1() throws InterruptedException {
        FileWatcher watcher = new FileWatcher() {
                public void onChange(File file) {
                    System.out.println("Changed " + file);
                }
            };
        watcher.setDelay(100);
        watcher.start();
        assertEquals("Found " + FileWatcher.getFileWatchers(), baseSize + 1, FileWatcher.getFileWatchers().size());
        watcher.exit();
        Thread.currentThread().sleep(20000);
        assertEquals("Found " + FileWatcher.getFileWatchers(), baseSize, FileWatcher.getFileWatchers().size());
    }

}
