package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.util.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 */
public class FileCopier implements Runnable {

	private String  classname = getClass().getName();
	private boolean debug	  = true;
	private void	debug(String msg){System.out.println(classname+":"+msg);}

	Thread kicker = null;
	int sleepTime=8888;
	Queue files;

	public FileCopier(Queue files) {
		this.files=files;
		init();
	}

	public void init() {
		this.start();	
	}


	/**
	 * Starts the main Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"FileCopier");
			kicker.start();
		}
	}
	
	/**
	 * Stops the main Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker = null;
	}

	/**
	 * Main loop, exception protected
	 */
	public void run () {
		if (kicker!=null) {
			try {
				doWork();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main work loop
	 */
	public void doWork() {
		String sshpath="/usr/local/bin";
		aFile2Copy afile;
	
		debug("Active");
		while (kicker!=null) {
			afile=(aFile2Copy)files.get();
			if (afile!=null) {
				debug("Copying "+afile.srcpath+"/"+afile.filename);
				SCPcopy scpcopy=new SCPcopy(sshpath,afile.dstuser,afile.dsthost,afile.dstpath);
				scpcopy.copy(afile.srcpath,afile.filename);
			} else {
				debug("afile is null ?");
			}
		}
	}

}
