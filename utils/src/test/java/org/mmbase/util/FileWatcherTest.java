/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;


import java.io.*;

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

    /**
     * perform lookup of non existing resource
     */
    public void test1() throws InterruptedException {
        FileWatcher.THREAD_DELAY = 50;
        FileWatcher.scheduleFileWatcherRunner();
        int baseSize = FileWatcher.getFileWatchers().size();

        FileWatcher watcher = new FileWatcher() {
            @Override
            public void onChange(File file) {
                System.out.println("Changed " + file);
            }
        };
        watcher.setDelay(100);
        watcher.start();
        assertEquals("Found " + FileWatcher.getFileWatchers(), baseSize + 1, FileWatcher.getFileWatchers().size());
        watcher.exit();
        synchronized(watcher) {
            System.out.println("Waiting for end");
            while (watcher.isRunning()) {
                watcher.wait();
            }
        }
        //Thread.currentThread().sleep(20000);
        assertEquals("Found " + FileWatcher.getFileWatchers(), baseSize, FileWatcher.getFileWatchers().size());
    }

}
