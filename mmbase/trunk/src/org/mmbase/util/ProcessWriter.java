/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.io.*;

/*
	$Log: not supported by cvs2svn $
	Revision 1.1  2001/02/08 10:20:38  vpro
	Rico: changed the processing by using a Threaded writer to fix the "half" image bug using code provided by Kees Jongenburg
	

	$Id: ProcessWriter.java,v 1.2 2001-02-28 15:08:27 kees Exp $

*/

/**
 * A class to pipe data from one stream to the other as a thread
 * useful for building execution pipes
 * @author Kees Jongenburger
 * @version $Id: ProcessWriter.java,v 1.2 2001-02-28 15:08:27 kees Exp $
 */
public class ProcessWriter implements Runnable{
    public boolean debug = false;

    private String classname = getClass().getName();

    private void debug( String msg ) {
        System.out.println( classname +":"+msg );
    }

    InputStream in ;
    OutputStream out;
	int blocksize=1024;
	Thread runner=null;

    public ProcessWriter(InputStream in, OutputStream out) {
		this(in,out,1024);
	}

    public ProcessWriter(InputStream in, OutputStream out,int blocksize) {
		this.in = in;
		this.out = out;
		this.blocksize = blocksize;
    }

	public void start() {
		if (runner==null) {
			runner=new Thread(this);
			runner.start();
		}
	}

    public void run() {
		if (out != null && in != null) {
		    try {
				PrintStream printStream = new PrintStream(out);
				byte[] data = new byte[blocksize];
				int size;
				int total = 0;
				while((size = in.read(data)) >0 ) {
				    total += size;
				    if (debug) debug("Total write"+ total);
				    printStream.write(data,0,size);
				    printStream.flush();
				}
				printStream.close();
		    } catch (Exception e) {
				debug("Write exception "+e.getMessage());
		    }
		} else {
			if (in == null ) {
				debug("Inputstream is null");
			}
	  		if (out == null ) {
				debug("Inputstream is null");
			}
		}
    }
}
