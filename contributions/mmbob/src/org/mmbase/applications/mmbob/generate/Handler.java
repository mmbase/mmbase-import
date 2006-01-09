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
public class Handler implements Runnable {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(Handler.class); 
   private static ArrayList words=new ArrayList();
   private static ArrayList firstnames=new ArrayList();
   private static ArrayList surnames=new ArrayList();
   private static ArrayList places=new ArrayList();
   private static ArrayList providers=new ArrayList();
   private static ArrayList areas=new ArrayList();
   private static Random rand = new Random();

	
    Thread kicker = null;

    public boolean init() {
        kicker = new Thread(this, "generate thread");
        kicker.setDaemon(true);
        kicker.start();
	return true;
    }

    public void run() {
	log.info("Generate Handler, run() called but not extended");
    }


  public static String generateLines() {
	String body="";
  	int len = 5+rand.nextInt(35);
	for (int i=0;i<len;i++) {
		body+=generateLine()+"\n";
	}
	return body;
  }

  public static String generateLine() {
  	int len = rand.nextInt(120);
	return generateLine(len+40);
  }

  public static String generateLine(int len) {
    
	// generates a line of max len x
	String result="";
	int numberofwords=words.size();
	if (numberofwords>0) {
		while (result.length()<len) {
    			int i = rand.nextInt(numberofwords);
			if (!result.equals("")) result+=" ";
			result+=(String)words.get(i);
		}
	}
	return result;
  }

  public static void setGenerateFile(String role,String name,String tokens) {
    	try {
       		Reader rd = ResourceLoader.getConfigurationRoot().getReader("mmbob/"+name);
       //String filename = MMBaseContext.getConfigPath()+File.separator+"mmbob"+File.separator+name;
        	BufferedReader in = new BufferedReader(rd);
        	String str;
        	while ((str = in.readLine()) != null) {
			StringTokenizer tok=new StringTokenizer(str,tokens+"\n\r");
			while (tok.hasMoreTokens()) {
				String part=tok.nextToken();
				if (role.equals("body")) {
					words.add(part);
				} else if (role.equals("firstnames")) {
					firstnames.add(part);
				} else if (role.equals("surnames")) {
					surnames.add(part);
				} else if (role.equals("places")) {
					places.add(part);
				} else if (role.equals("providers")) {
					providers.add(part);
				} else if (role.equals("areas")) {
					areas.add(part);
				}
			}
        	}
        	in.close();
        } catch(IOException e) {
		e.printStackTrace();
	}
  }

  public static String generateFirstName() {
        int numberofnames=firstnames.size();
        if (numberofnames>0) {
                int i = rand.nextInt(numberofnames);
                return (String)firstnames.get(i);
        }
        return "";
  }

  public static String generateSurName() {
        int numberofnames=surnames.size();
        if (numberofnames>0) {
                int i = rand.nextInt(numberofnames);
                return (String)surnames.get(i);
        }
        return "";
  }


  public static String generatePlace() {
        int numberofplaces=places.size();
        if (numberofplaces>0) {
                int i = rand.nextInt(numberofplaces);
                return (String)places.get(i);
        }
        return "";
  }


  public static String generateArea() {
        int numberofareas=areas.size();
        if (numberofareas>0) {
                int i = rand.nextInt(numberofareas);
                return (String)areas.get(i);
        }
        return "";
  }


  public static String generateProvider() {
        int numberofproviders=providers.size();
        if (numberofproviders>0) {
                int i = rand.nextInt(numberofproviders);
                return (String)providers.get(i);
        }
        return "";
  }

}

