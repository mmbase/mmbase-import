/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.applet.*;

import org.mmbase.remote.*;

//import org.mmbase.util.logging.Logger;
//import org.mmbase.util.logging.Logging;

/**
 * @version  3 Okt 1999
 * @author Daniel Ockeloen
 */
public class MMCounter extends RemoteBuilder { 
    //Logging removed automaticly by Michiel, and replace with __-methods
    private static String __classname = MMCounter.class.getName();


    boolean __debug = false;
    private static void __debug(String s) { System.out.println(__classname + ":" + s); }
    //private static Logger log = Logging.getLoggerInstance(MMCounter.class.getName()); 

	AudioClip audioclip;

	public void init(MMRemoteMultiCast mmc,String servicefile) {
		super.init(mmc,servicefile);
		// the node was loaded allready so check what the state was
		// and put us in ready/waiting state
		String state=getStringValue("state");
		if (!state.equals("waiting")) {
			// maybe add 'what' happened code ? but for now
			// just put the service on waiting state
			setValue("state","waiting");
			commit();
		}
		try {
		audioclip = Applet.newAudioClip(new URL("http://openbox.vpro.nl/1.aif"));
		} catch(Exception e) {
			/*log.error*/e.printStackTrace();
		}
	}

	public void nodeRemoteChanged(String nodenr,String buildername,String ctype) {		
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeLocalChanged(String nodenr,String buildername,String ctype) {		
		nodeChanged(nodenr,buildername,ctype);
	}

	public void nodeChanged(String nodenr,String buildername,String ctype) {		
		// get the node
		getNode();
				
		String state=getStringValue("state");
		if (state.equals("up")) {
			doUp();
		} else if (state.equals("down")) {
			doDown();
		}
	}

	private void doUp() {
		// well i can handle up, so claim node
		setValue("state","busy");
		commit();
		
		// do the work
        /* michiel: nothing happens here! Away with it.
		for (int i=0;i<40;i++) {
			for (int j=0;j<10000000;j++) { }
			System.out.print(".");
		}
        */
        /*log.debug*/__debug("in doUp");

		// decode and add one to devdata 
		int count=getIntValue("devdata");
		setValue("devdata",count+1);	

		// signal that we are done
		//setValue("state","waiting");
		setValue("state","down");
		commit();
	}


	private void doDown() {
		// well i can handle up, so claim node
		setValue("state","busy");
		commit();
		
		// do the work
        /* michiel: nothing happens here! Away with it.
		for (int i=0;i<40;i++) {
			for (int j=0;j<10000000;j++) { }
			System.out.print("+");
		}
        */
        /*log.debug*/__debug("in doDown");

		// decode and add one to devdata 
		int count=getIntValue("devdata");
		setValue("devdata",count+1);	

		// signal that we are done
		//setValue("state","waiting");
		setValue("state","up");
		commit();
		audioclip.play();
	}
}
