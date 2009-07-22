/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.mmbob.generate;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class Generate {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Generate.class); 

   private static int state=0;
   private static String livefeedback="";
   private static Handler handler;

   public static int getState() {
	return state;
   }

  public static void setState(int newstate) {
	state=newstate;
  }

   public static String getLiveFeedback() {
	return livefeedback;
   }

   public static boolean generatePosters(int generatecount,int generatedelay,String inforum) {
		log.info("GENERATE POSTERS : count="+generatecount+" delay="+generatedelay+" in forum="+inforum);
		if (state==0) {
			state=1;
			handler=(Handler)new PostersHandler(generatecount,generatedelay,inforum);	
		} else {
			// signal we are allready busy;
			return false;
		}
		return true;
  }


   public static boolean generateAreas(int generatecount,int generatedelay,String inforum) {
		log.info("GENERATE AREAS : count="+generatecount+" delay="+generatedelay+" in forum="+inforum);
		if (state==0) {
			state=1;
			handler=(Handler)new AreasHandler(generatecount,generatedelay,inforum);	
		} else {
			// signal we are allready busy;
			return false;
		}
		return true;
  }

   public static boolean generateThreads(int generatecount,int generatedelay,String inforum,String inpostarea) {
		log.info("GENERATE THREADS : count="+generatecount+" delay="+generatedelay+" in forum="+inforum+" postarea="+inpostarea);
		if (state==0) {
			state=1;
			handler=(Handler)new ThreadsHandler(generatecount,generatedelay,inforum,inpostarea);	
		} else {
			// signal we are allready busy;
			return false;
		}
		return true;
  }


   public static boolean generateReplys(int generatecount,int generatedelay,String inforum,String inpostarea) {
		log.info("GENERATE REPLYS : count="+generatecount+" delay="+generatedelay+" in forum="+inforum+" postarea="+inpostarea);
		if (state==0) {
			state=1;
			handler=(Handler)new ReplysHandler(generatecount,generatedelay,inforum,inpostarea);	
		} else {
			// signal we are allready busy;
			return false;
		}
		return true;
  }

}
