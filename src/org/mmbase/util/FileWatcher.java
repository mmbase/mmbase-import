/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/* 
CREDITS:
    Contributors:  Mathias Bogaert
    @author Ceki G&uuml;lc&uuml;    
    changed licence from apache 1.1 to current licence    
*/

package org.mmbase.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import org.mmbase.util.logging.*;

/**
 *  This will run as a thread afer it has been started, it will check every interval if one
 *  of it's files has been changed.
 *  When one of them has been changed, the OnChange method will be called, with the file witch 
 *  was changed. After that the thread will stop.
 *  To stop a running thread, call the method exit();
 *
 *  Example:
    <code>
    	class FooFileWatcher extends FileWatcher {

            public FooFileWatcher() {
                super(true); // true: keep reading.
            }

	    public void onChange(File file) {
		System.out.println(file.getAbsolutePath());
	    }
    	}
	    
	// create new instance
    	FooFileWatcher watcher = new FooFileWatcher();
	// set inteval
	watcher.setDelay(10 * 1000);
	watcher.add(new File("/tmp/foo.txt"));
	watcher.start();
	watcher.add(new File("/tmp/foo.txt"));
	wait(100*1000);
	watcher.exit();
    </code>
 *
 */
public abstract class FileWatcher extends Thread {
    static Logger log = Logging.getLoggerInstance(FileWatcher.class.getName());

    private class FileEntry {
    	// static final Logger log = Logging.getLoggerInstance(FileWatcher.class.getName());
    	private long lastModified; 
    	private File file;
        	
	public FileEntry(File file) {
	    if(file == null) {
	    	String msg = "file was null";
	    	// log.error(msg);
	    	throw new RuntimeException(msg);	    
	    }
	    
    	    if(!file.exists()) {
	    	String msg = "file :"  + file.getAbsolutePath() + " did not exist";
	    	// log.error(msg);
	    	throw new RuntimeException(msg);	    
	    }
	    this.file = file;
	    lastModified = file.lastModified();
	}
	
	public boolean changed() {
	    return (lastModified < file.lastModified());
	}

    	public File getFile() {
	    return file;
	}	
        
    }

    private ArrayList files = new ArrayList();
    
    /**
     *	The default delay between every file modification check, set to 60
     *  seconds.  
     */
    static final public long DEFAULT_DELAY = 60000; 
    
    /**
     * The delay to observe between every check. By default set {@link
     * #DEFAULT_DELAY}. 
     */
    private long delay = DEFAULT_DELAY; 
    private boolean stop = false;
    private boolean continueAfterChange = false;
  
    protected FileWatcher() {
    	// make it end when parent treath ends..
    	setDaemon(true);
    }

    protected FileWatcher(boolean c) {
    	// make it end when parent treath ends..
        continueAfterChange = c;
    	setDaemon(true);
    }

    /**
     * Put here the stuff that has to be executed, when a file has been changed.
     *@param file The file that was changed..
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
     *@param file The file which has to be monitored..
     */
    public void add(File file) {
    	FileEntry fe = new FileEntry(file);
    	synchronized(this) {
	    files.add(fe);
	}
    }

    /**
     * Add's a file to be checked...
     *@param file The file which has to be monitored..
     */
    public void exit() {
    	synchronized(this) {
    	    stop = true;
	}
    }
    
    /**
     * looks if a file has changed...
     */
    private boolean changed() {
    	synchronized(this) {
	    Iterator i = files.iterator();
	    while(i.hasNext()) {
	    	FileEntry fe = (FileEntry) i.next();
		if(fe.changed()) {
		    log.info("the file :" + fe.getFile().getAbsolutePath() + " has changed.");
		    onChange(fe.getFile());
                    if (! continueAfterChange) {
                        return true;
                    }
		}
	    }
	}
	return false;
    }
    
    /**
     * looks if we have to stop
     */
    private boolean mustStop(){
    	synchronized(this) {
	    return stop;
	}
    }

    /**
     *  Main loop, will repeat every amount of time.
     *	It will stop, when either a file has been changed, or exit() has been called
     */
    public void run() {
    	do {
    	    try {
    	    	log.debug("gonna sleep this filewatcher for : " + delay / 1000 + " seconds");	    
	    	Thread.currentThread().sleep(delay);
      	    }    
	    catch(InterruptedException e) {
	    	// no interruption expected
      	    }
	// when we found a change, we exit..
	} while(!mustStop() && !changed());
    }
}
