package org.mmbase.remote.builders;

import java.net.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.applet.*;

import org.mmbase.remote.*;


/**
 * @version  3 Okt 1999
 * @author Daniel Ockeloen
 */
public class MMCounter extends RemoteBuilder {

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
			e.printStackTrace();
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
		for (int i=0;i<40;i++) {
			for (int j=0;j<10000000;j++) { }
			System.out.print(".");
		}

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
		for (int i=0;i<40;i++) {
			for (int j=0;j<10000000;j++) { }
			System.out.print("+");
		}

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
