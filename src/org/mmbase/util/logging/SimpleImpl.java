/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;

import java.io.PrintStream;

/**
 * A very simple implementation of Logger. It writes everything to
 * standard output or standard error (the configure string can be
 * `stderr' or `stdout' (default)).  It does not know categories (and
 * therefore is a Singleton class), and you cannot configure what to
 * log (everything is always logged).
 *
 * @author Michiel Meeuwissen */

public class SimpleImpl implements Logger {


	private static SimpleImpl root = new SimpleImpl();

	private static PrintStream ps = System.out;

	private SimpleImpl() {
		// a Singleton class.
	}

	public static  SimpleImpl getLoggerInstance(String name) {
		return root;
	}

   
	public static  void configure(String c) {
		if(c == null) {
			ps = System.out;
			return;
		}
		if(c.equals("stderr")) {
			ps = System.err;
			return;
		}
		if(c.equals("stdout")) {
			ps = System.out;
			return;
		}

		
	}

	public void setPriority(Level p) {
	}

   
	public final void log (String s) {
		ps.println(s);
	}

	public void trace (Object m) {
		log("TRACE " + m.toString());
	}
	public void debug (Object m) {
		log("DEBUG " + m.toString());
	}

	public void service (Object m) {
		log("SERVICE " + m.toString());
	}
	public void info    (Object m) {
		log("INFO " + m.toString());
	}
	public void warn    (Object m) {
		log("WARN " + m.toString());
	}
	public void error   (Object m) {
		log("ERROR " + m.toString());
	}
	public void fatal   (Object m) {
		log("FATAL " + m.toString());
	}

	public boolean isDebugEnabled() {
		return true;
	}

}
