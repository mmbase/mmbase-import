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
import org.mmbase.util.xml.UtilReader;
import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArraySet;

/**
 * Original javadoc.

 *  This will run as a thread after it has been started.
 *  It will check every interval if one of it's files has been changed.
 *  When one of them has been changed, the onChange method will be called, with the file that
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
 * MMBase javadoc
 *
 * This code was originally borrowed from the log4j project (as can still be seen from the authors),
 * it was however quite heavily adapted. You are probably better of using a {@link ResourceWatcher}
 * (since MMBase 1.8), because that does not watch only files. Its implementation does of course use
 * FileWatcher, for the 'file' part of the watching.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 * @since  MMBase-1.4
 * @version $Id$
 */
public abstract class FileWatcher {
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
    static public long THREAD_DELAY = 10000;



    private static Map props;


    /**
     * @since MMBase-1.8
     */
    static private Runnable watcher = new Runnable() {
            public void run() {
                try {
                    String delay =  (String) props.get("delay");
                    if (delay != null) {
                        THREAD_DELAY = Integer.parseInt(delay);
                        log.service("Set thread delay time to " + THREAD_DELAY);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        };


    static {
        props = new UtilReader("resourcewatcher.xml", watcher).getProperties();
        watcher.run();
    }

    /**
     * @since MMBase-1.8
     */
    public static void shutdown() {        
        fileWatchers.run = false;
        fileWatchers.interrupt();
        log.service("Shut down file watcher thread");
    }

    /**
     * The delay to observe between every check. By default set {@link
     * #DEFAULT_DELAY}.
     */
    private long delay = DEFAULT_DELAY;

    private Set files = new LinkedHashSet();
    private Set fileSet = new FileSet(); // (automaticly) wraps 'files'.
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
    abstract public void onChange(File file);

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public void setDelay(long delay) {
        this.delay = delay;
        if (delay < THREAD_DELAY) {
            log.service("Delay of " + this + "  (" + delay + " ms) is smaller than the delay of the watching thread. Will not watch more often then once per " + THREAD_DELAY + " ms.");
        }
    }

    /**
     * Add's a file to be checked...
     * @param file The file which has to be monitored..
     * @throws RuntimeException If file is null
     */
    public void add(File file) {
        FileEntry fe = new FileEntry(file);
        synchronized (this) {
            files.add(fe);
            if (removeFiles.remove(fe)) {
                log.service("Canceling removal from filewatcher " + fe);
            }
        }
    }

    /**
     * Wether the file is being watched or not.
     * @param file the file to be checked.
     * @since MMBase-1.6
     */
    public boolean contains(File file) {
        return files.contains(new FileEntry(file));
    }

    /**
     * Remove file from the watch-list
     */
    public void remove(File file) {
        synchronized (this) {
            removeFiles.add(file);
        }
    }

    /**
     * Returns a (modifiable) Set of all files (File object) of this FileWatcher. If you change it, you change the
     * FileWatcher. The order of the Set is predictable (backed by a {@link java.util.LinkedHashSet}).
     *
     * @since MMBase-1.8.
     */
    public Set getFiles() {
        return fileSet;
    }

    /**
     * Removes all files, this watcher will end up watching nothing.
     * @since MMBase-1.8
     */
    public void clear() {
        fileSet.clear();
    }

    /**
     * Stops watching.
     */
    public void exit() {
        synchronized (this) {
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
        synchronized (this) {
            Iterator i = files.iterator();
            while (i.hasNext()) {
                FileEntry fe = (FileEntry)i.next();
                if (fe.changed()) {
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
        synchronized (this) {
            // remove files if necessary
            Iterator ri = removeFiles.iterator();
            while (ri.hasNext()) {
                File f = (File)ri.next();
                FileEntry found = null;

                // search the file
                Iterator i = files.iterator();
                while (i.hasNext()) {
                    FileEntry fe = (FileEntry)i.next();
                    if (fe.getFile().equals(f)) {
                        if (log.isDebugEnabled()) {
                            log.debug("removing file[" + fe.getFile().getName() + "]");
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
    private boolean mustStop() {
        synchronized (this) {
            return stop;
        }
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (getClass().equals(o.getClass())) {
            FileWatcher f = (FileWatcher)o;
            return this.files.equals(f.files);
        }
        return false;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return files == null ? 0 : files.hashCode();
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
     * The one thread to handle all FileWatchers. In earlier implementation every FileWatcher had
     * it's own thread, but that is avoided now.
     */
    private static class FileWatcherRunner extends Thread {


        boolean run = true;
        /**
         * Set of file-watchers, which are currently active.
         */
        private Set watchers = new CopyOnWriteArraySet();
        private Set watchersToAdd = new HashSet();

        FileWatcherRunner() {
            super("MMBase FileWatcher thread");
            log.service("Starting the file-watcher thread");
            setPriority(MIN_PRIORITY);
            setDaemon(true);
        }

        void add(FileWatcher f) {
            watchers.add(f);
        }

        /**
         *  Main loop, will check every watched file every amount of time.
         *  It will never stop, this thread is a daemon.
         */
        public void run() {
            // todo: how to stop this thread except through interrupting it?
            List removed = new ArrayList();
            while (run) {
                try {
                    long now = System.currentTimeMillis();
                    Iterator i = watchers.iterator();
                    while (i.hasNext()) {
                        FileWatcher f = (FileWatcher)i.next();
                        if (now - f.lastCheck > f.delay) {
                            if (log.isDebugEnabled()) {
                                log.trace("Filewatcher will sleep for : " + f.delay / 1000 + " s. " + "Currently watching: " + f.getClass().getName() + " " + f.toString());
                            }
                            // System.out.print(".");
                            f.removeFiles();
                            //changed returns true if we can stop watching
                            if (f.changed() || f.mustStop()) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Removing filewatcher " + f + " " + f.mustStop());
                                }
                                removed.add(f);
                            }
                            f.lastCheck = now;
                        }
                    }
                    watchers.removeAll(removed);
                    removed.clear();
                    if (log.isTraceEnabled()) {
                        log.trace("Sleeping " + THREAD_DELAY + " ms");
                    }
                    Thread.sleep(THREAD_DELAY);
                } catch (InterruptedException e) {
                    Thread ct = Thread.currentThread();
                    log.debug((ct != null ? ct.getName() : "MMBase")+ " was interrupted.");
                    break; // likely interrupted due to MMBase going down - break out of loop
                } catch (Throwable ex) {
                    // unexpected exception?? This run method should never interrupt, so we catch everything.
                    log.error("Exception: " + ex.getClass().getName() + ": " + ex.getMessage() + Logging.stackTrace(ex));
                }
                // when we found a change, we exit..
            }
        }
    }

    /**
     * Performance test
     */
    private static class TestFileWatcher extends FileWatcher {
        int i = 0;
        public void onChange(java.io.File f) {
            // do something..
            i++;
        }
        protected void finalize() {
            System.out.println(this.toString() + ":" + i);
        }
    }

    /**
     * Object used in file-lists of the FileWatcher. It wraps a File object, but adminstrates
     * lastmodified an existence seperately (to compare with the actual values of the File).
     */
    private class FileEntry {
        // static final Logger log = Logging.getLoggerInstance(FileWatcher.class.getName());
        private long lastModified = -1;
        private boolean exists = false;
        private File file;

        public FileEntry(File file) {
            if (file == null) {
                String msg = "file was null";
                // log.error(msg);
                throw new RuntimeException(msg);
            }
            exists = file.exists();
            if (!exists) {
                // file does not exist. A change will be triggered
                // once the file comes into existence
                log.debug("file :" + file.getAbsolutePath() + " did not exist (yet)");
                lastModified = -1;
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
                    log.info("File " + file.getAbsolutePath() + " added");
                    return true;
                } else {
                    boolean result = lastModified < file.lastModified();
                    if (result) {
                        log.info("File " + file.getAbsolutePath() + " changed");
                    }
                    return result;
                }
            } else {
                if (exists) {
                    log.info("File " + file.getAbsolutePath() + " removed");
                }
                return exists;
            }
        }

        /**
         * Call if changes were treated.
         */
        public void updated() {
            exists = file.exists();
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
                FileEntry fe = (FileEntry)o;
                return file.equals(fe.file);
            } else if (o instanceof File) {
                return file.equals(o);
            }
            return false;
        }

        public int hashCode() {
            return file.hashCode();
        }

    }

    /**
     * This FileSet makes the 'files' object of the FileWatcher look like a Set of File rather then Set of FileEntry's.
     * @since MMBase-1.8
     */
    private class FileSet extends AbstractSet {
        public int size() {
            return FileWatcher.this.files.size();
        }
        public  Iterator iterator() {
            return new FileIterator();
        }
        public boolean add(Object o) {
            int s = size();
            FileWatcher.this.add((File) o);
            return s != size();
        }
    }
    /**
     * The iterator belonging to FileSet.
     * @since MMBase-1.8
     */
    private class FileIterator implements Iterator {
        Iterator it;
        File lastFile;
        FileIterator() {
            it = FileWatcher.this.files.iterator();
        }
        public boolean hasNext() {
            return it.hasNext();
        }
        public Object next() {
            FileEntry f = (FileEntry) it.next();
            lastFile = f.getFile();
            return  lastFile;
        }
        public void remove() {
            FileWatcher.this.remove(lastFile);
        }

    }

}
