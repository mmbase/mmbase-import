package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 */
public class CALC extends ProcessorModule {

	private String classname = getClass().getName();

	public void init() {
	}

	public void reload() {
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}


	/**
	 * CALC, a support module for servscan.
	 */
	public CALC() {
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector  getList(scanpage sp,StringTagger tagger, String value) {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
		}
		return(null);
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		debug("CMDS="+cmds);
		debug("VARS="+vars);
		return(false);
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			while (tok.hasMoreTokens()) {
				cmd+="-"+tok.nextToken();
			}
			return(doCalc(cmd));
		}
		return("No command defined");
	}
	

	String doCalc(String cmd) {
//		System.out.println("CMD="+cmd);
		ExprCalc cl=new ExprCalc(cmd);
		return(""+(int)(cl.getResult()+0.5));
	}



	public String getModuleInfo() {
		return("Support routines simple calc, Daniel Ockeloen");
	}


	private void debug( String msg )
	{
		System.out.println( classname +":"+msg );
	}
}
