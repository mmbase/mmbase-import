package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author David V van Zeventer
 * @version 23 dec 1998
 */
				
public class RALiveEncoder implements LiveEncoder, Runnable{

	String buildername = "-RALiveEncoder";
	String streamname;	//The name of the stream being encoded->TR_40 meaning 40kb/s stream
	Hashtable soptions_ht;	//The streamoptions for this stream.
	boolean reset_while_on_air = false;	//initial value = false.
	Runtime rt;	//objects used for starting/stopping LiveEncoder program 
	Process prcs;	//	

	String startscriptprefix = "startscriptname"; 	// a hashtable key in soptions_ht.
	String stopscriptprefix = "stopscriptname";	//

	Thread kicker;	//thread objects inwhich the Encoderprogram is started
			
	public RALiveEncoder(String streamname, Hashtable soptions_ht, boolean reset_while_on_air) {
		this.streamname = streamname;
		this.soptions_ht = soptions_ht;
		this.reset_while_on_air = reset_while_on_air;
		start();
	}

	/**
	 * Gets the RALEobjectname aka streamname aka threadname.
	 */
	public String getName() {
		return(streamname);	//Return the streamname that will be encoded
	}
				
	/**
	 * Creates and starts a RALiveEncoderThread Thread.
	 */
	public void start() {	
		if (kicker == null) {
			kicker = new Thread(this, streamname);
			kicker.start();
		}	
	}
	
	/**
	 * run method for RALiveEncoderthread which starts the RALiveEncoderprogram
	 */
	public void run () {
		String methodname = "run";
		//kicker.setPriority(Thread.MIN_PRIORITY+1);  
		try{ 	
			if (!reset_while_on_air) {
				System.out.println(buildername+": "+methodname+": Thread "+getName()+" is executing script: "+(String)soptions_ht.get(startscriptprefix));
				rt = Runtime.getRuntime();
				//Executing the Liveencoder program using a startscipt.
				prcs = rt.exec((String)soptions_ht.get(startscriptprefix));
			} else {
				System.out.println(buildername+": "+methodname+": Server has been reset while being ON_AIR.");

				//Wait an amount of time before executing startscripts again. 
				int delay = 30000;
				System.out.println(buildername+": "+methodname+": WAITING "+delay+" milliseconds BEFORE EXECUTING STARTSCRIPT.");
				try { 
					Thread.sleep(delay);
				}catch (InterruptedException ie){ 
					System.out.println(buildername+": "+methodname+": Sleep() failed "+ie);
				}	
				System.out.println(buildername+": "+methodname+": WAIT DONE -- > NOW EXECUTING START-SCRIPT AGAIN.");
				System.out.println(buildername+": "+methodname+": Thread "+getName()+" is executing script: "+(String)soptions_ht.get(startscriptprefix));
				rt = Runtime.getRuntime();
				//Executing the Liveencoder program using a startscipt.
				prcs = rt.exec((String)soptions_ht.get(startscriptprefix));
			}
       		} catch (IOException ioe1) {System.out.println(buildername+": "+methodname+": "+ioe1);}
	}
	
	/**
	 * Stops the RALiveEncoderthread.
	 */
	public void stop() {
		String methodname = "stop";
		try{     
			System.out.println(buildername+": "+methodname+": Thread "+getName()+ " is executing script: "+(String)soptions_ht.get(stopscriptprefix));
			rt = Runtime.getRuntime();
			//This script stops the RALiveEncoderprogram using a stopscript
			prcs = rt.exec((String)soptions_ht.get(stopscriptprefix));
			
	 	}catch (IOException ioe2) {System.out.println(buildername+": "+methodname+": "+ioe2);}
		//System.out.println(buildername+": "+methodname+": Stopping Thread kicker with value: "+kicker);
		//kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.stop();	//stopping RALiveEncoderthread
		kicker = null;
	}
}
