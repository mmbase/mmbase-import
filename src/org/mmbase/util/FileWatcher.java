/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.File;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 *  This will run as a thread after it has been started.
 *  It will check every interval if one of it's files has been changed.
 *  When one of them has been changed, the OnChange method will be called, with the file that
 *  was changed. After that the thread will stop.
 *  To stop a running thread, call the method exit();
 *
 *  Example:
 *   <code style="white-space:pre;">
 *   	class FooFileWatcher extends FileWatcher {
 *
 *           public FooFileWatcher() {
 *               super(true); // true: keep reading.
 *           }
 *
 *	    public void onChange(File file) {
 *		System.out.println(file.getAbsolutePath());
 *	    }
 *   	}
 *
 *	// create new instance
 *    	FooFileWatcher watcher = new FooFileWatcher();
 *	// set inteval
 *	watcher.setDelay(10 * 1000);
 *	watcher.add(new File("/tmp/foo.txt"));
 *	watcher.start();
 *	watcher.add(new File("/tmp/foo.txt"));
 *	wait(100*1000);
 *	watcher.exit();
 *    </code>
 *
 * Thanks to contributions by Mathias Bogaert.
 * Licence was changed from apache 1.1 to Mozilla.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 * @since  MMBase-1.4
 * @version $Id: FileWatcher.java,v 1.19 2003-11-16 14:04:21 michiel Exp $
 */
public abstract class FileWatcher  {
    private static Logger log = Logging.getLoggerInstance(FileWatcher.class);

    private static FileWatcherRunner fileWatchers = new FileWatcherRunner();
    static {
        fileWatchers.start();
    }

    /**
     *	The default delay between every file modification check, set to 60
     *  seconds.
     */
    static final public long DEFAULT_DELAY = 60000;

    /**
     * The one thread doing al the work also needs a delay.
     */
    static final public long THREAD_DELAY = 1000;

    /**
     * The delay to observe between every check. By default set {@link
     * #DEFAULT_DELAY}.
     */
    private long delay = DEFAULT_DELAY;
    private Set files       = new HashSet();
    private Set removeFiles = new HashSet();
    private boolean stop = false;
    private boolean continueAfterChange = false;
    private long lastCheck = 0;

    protected FileWatcher() {
        this(true);
    }

    protected FileWatcher(boolean c) {
        // make it end when parent treath ends..
        continueAfterChange = c;
    }


    public void start() {        
        fileWatchers.add(this);
    }
    /**
     * Put here the stuff that has to be executed, when a file has been changed.
     * @param file The file that was changed..
     */
    abstract protected void onChange(File file);

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * Add's a file to be checked...
     * @param file The file which has to be monitored..
     * @throws RuntimeException If file is null or does not exist.
     */
    public void add(File file) {
        FileEntry fe = new FileEntry(file);
        synchronized(this) {
            files.add(fe);
            if (removeFiles.remove(fe)) log.service("Canceling removal from filewatcher " + fe);
        }
    }

    /**
     * Wether the file is being watched or not.
     * @param the file to be checked.
     * @since MMBase-1.6
     */
    public boolean contains(File file) {
        return files.contains(new FileEntry(file));
    }

    /**
     * Remove file from the watch-list
     */
    public void remove(File file) {
        synchronized(this) {
            removeFiles.add(file);
        }
    }

    /**
     */
    public void exit() {
        synchronized(this) {
            stop = true;
        }
    }

    /**
     * Shows the 'contents' of the filewatcher. It shows a list of files/last modified timestamps.
     */
    public String toString() {
        return files.toString();
    }

    /**
     * Looks if a file has changed. If it is, the 'onChance' for this file is called.
     *
     * Before doing so, it removes the files which were requested for
     * removal from the watchlist.
     *
     */
    private boolean changed() {
        synchronized(this) {
            Iterator i = files.iterator();
            while(i.hasNext()) {
                FileEntry fe = (FileEntry) i.next();
                if(fe.changed()) {
                    log.debug("the file :" + fe.getFile().getAbsolutePath() + " has changed.");
                    try {
                        onChange(fe.getFile());
                    } catch (Throwable e) {
                        log.warn("onChange of " + fe.getFile().getName() + " lead to exception:");
                        log.warn(Logging.stackTrace(e));
                    }
                    if (continueAfterChange) {
                        fe.updated(); // onChange was called now, it can be marked up-to-date again
                    } else { //
                        return true; // stop watching
                    }
                }
            }
        }
        return false;
    }

    private void removeFiles() {
        synchronized(this) {
            // remove files if necessary
            Iterator ri = removeFiles.iterator();
            while (ri.hasNext()) {
                File f = (File) ri.next();
                FileEntry found = null;

                // search the file
                Iterator i = files.iterator();
                while (i.hasNext()) {
                    FileEntry fe = (FileEntry) i.next();
                    if (fe.getFile().equals(f)) {
                        if (log.isDebugEnabled()){
                            log.debug("removing file["+ fe.getFile().getName() +"]");
                        }
                        found = fe;
                        break;
                    }
                }
                if (found != null) {
                    files.remove(found);
                    log.service("Removed " + found + " from watchlist");
                }
            }
            removeFiles.clear();
        }
    }

    /**
     * Looks if we have to stop
     */
    private boolean mustStop(){
        synchronized(this) {
            return stop;
        }
    }

    public boolean equals(Object o) {
        if (! (o instanceof FileWatcher)) return false;
        FileWatcher f = (FileWatcher) o;
        return this.getClass().equals(f.getClass()) && this.files.equals(f.files);
    }

    /**
     * @javadoc
     */
    public static void main(String[] args) {

        // start some filewatchers
        for (int i = 0; i < 100; i++) {
            FileWatcher w = new TestFileWatcher();
            // add some files
            for (int j = 0; j < 4; j++) {
                try {
                    w.add(File.createTempFile("filewatchertestfile", ".txt"));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            //set a delay and start it.
            w.setDelay(1 * 1000); // 1 s
            w.start();
            //this.wait(123); // make all watchers out sync
        }

        System.out.println("Starting");
        // ok, a lot of those are running now, let time something else, and see if it suffers.
        long start = System.currentTimeMillis();
        long k;
        for (k = 0; k < 400000000;) {
            k++;
        }
        System.out.println("\ntook " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * @javadoc
     */
    private static class FileWatcherRunner extends Thread {

        private Set watchers = new HashSet();
        FileWatcherRunner() {
            log.info("Starting the file-watcher thread");
            setPriority(MIN_PRIORITY);
            setDaemon(true);
        }

        void add(FileWatcher f) {
            synchronized(watchers) {
                watchers.add(f);
            }
        }

        /**
         *  Main loop, will repeat every amount of time.
         *	It will stop, when either a file has been changed, or exit() has been called
         */
        public void run() {
            do {
                try {
                    long now = System.currentTimeMillis();
                    synchronized(watchers) {
                        Iterator i = watchers.iterator();
                        while (i.hasNext()) {
                            FileWatcher f = (FileWatcher) i.next();
                            if (now - f.lastCheck > f.delay) {
                                if(log.isDebugEnabled()) {
                                    log.trace("Filewatcher will sleep for : " + f.delay / 1000 + " s. " +
                                              "Currently watching: " + f.getClass().getName() + " " + f.toString());
                                }
                                // System.out.print(".");
                                f.removeFiles();
                                if (f.changed() || f.mustStop()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Removing filewatcher " + f + " " + f.mustStop());
                                    }
                                    i.remove();
                                }
                                f.lastCheck = now;
                            }
                        }
                    }
                    Thread.sleep(THREAD_DELAY);
                } catch(InterruptedException e) {
                    log.error("Interrupted" + Logging.stackTrace(e));
                    // no interruption expected
                } catch (Throwable ex) { 
                    // unexpected exception?? This run method should never interrupt, so we catch everything.
                    log.error("Exception: " + ex.getClass().getName() + ": " + ex.getMessage()  + Logging.stackTrace(ex));
                }
                // when we found a change, we exit..
            } while (true);
        }
    }

    /**
     * Performance test
     */
    private static class TestFileWatcher extends FileWatcher {
        int i = 0;
        protected void onChange(java.io.File f) {
            // do something..
            i++;
        }
        protected void finalize() {
            System.out.println(this.toString() + ":" + i);
        }
    }

    /**
     * @javadoc
     */
    private class FileEntry {
        // static final Logger log = Logging.getLoggerInstance(FileWatcher.class.getName());
        private long lastModified=-1;
        private boolean exists=false;
        private File file;

        public FileEntry(File file) {
            if(file == null) {
                String msg = "file was null";
                // log.error(msg);
                throw new RuntimeException(msg);
            }
            exists=file.exists();
            if(!exists) {
                // file does not exist. A change will be triggered
                // once the file comes into existence
log.info("file :"  + file.getAbsolutePath() + " did not exist (yet)");
                log.debug("file :"  + file.getAbsolutePath() + " did not exist (yet)");
                lastModified=-1;
            } else {
                lastModified = file.lastModified();
            }
            this.file = file;
        }

        /**
         * Signal a change.
         * Returns true if the file was modified, added, or removed.
         * @return <code>true</code> if the file was changed
         */
        public boolean changed() {
            if (file.exists()) {
                if (!exists) {
                    log.info("file :"  + file.getAbsolutePath() + " added");
                    return true;
                } else {
                    boolean result=lastModified < file.lastModified();
                    if (result) {
                        log.info("file :"  + file.getAbsolutePath() + " changed");
                    }
                    return result;
                }
            } else {
                if (exists) {
                    log.info("file :"  + file.getAbsolutePath() + " removed");
                }
                return exists;
            }
        }

        /**
         * Call if changes were treated.
         */
        public void updated() {
            exists=file.exists();
            if (exists) {
                lastModified = file.lastModified();
            } else {
                lastModified = -1;
            }
        }

        public File getFile() {
            return file;
        }

        public String toString() {
            return file.toString() + ":" + lastModified;
        }

        public boolean equals(Object o) {
            if (o instanceof FileEntry) {
                FileEntry fe = (FileEntry) o;
                return file.equals(fe.file);
            } else if (o instanceof File) {
                return file.equals((File) o);
            }
            return false;
        }

        public int hashCode() {
            return file.hashCode();
        }

    }

}
