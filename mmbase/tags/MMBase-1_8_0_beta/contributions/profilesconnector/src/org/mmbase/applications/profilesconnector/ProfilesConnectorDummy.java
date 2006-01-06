package org.mmbase.applications.profilesconnector;

import nl.kennisnet.profielen.webservices.*;
import org.mmbase.applications.mmbob.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class ProfilesConnectorDummy implements ExternalProfileInterface {

        static private Logger log = Logging.getLoggerInstance(ProfilesConnectorDummy.class);
	static String danielmsn = "rmsn1212";
	static String danielyahoo = "ryah4334";
	static String danielicq = "ricq7667";
	static String adminmsn = "rmsn2048";
	static String adminyahoo = "ryah1024";
	static String adminicq = "ricq4545";

	String getVersion()  {
		return "1.2.3";
        }

	public String getValue(String entreeid,String target) {
		StringTokenizer tok =  new StringTokenizer(target,":\n\r");
		if (tok.hasMoreTokens()) {
			String dienstid = tok.nextToken();
			if (tok.hasMoreTokens()) {
				String type = tok.nextToken();
				if (tok.hasMoreTokens()) {
					String veldid = tok.nextToken();
					log.info("GET DienstId="+dienstid+" entreeid="+entreeid+" type="+type+" veldid="+veldid);
					try {
					int d = Integer.parseInt(dienstid);
					int i = Integer.parseInt(veldid);
					if (type.equals("GS")) {
						return getGSField(d,entreeid,i); 
					} else {
						return getBSField(d,entreeid,i); 
					}
					} catch (Exception e) {}
				}
			}
		}
		return null;
	}


	public boolean setValue(String entreeid,String target,String value) {
		StringTokenizer tok =  new StringTokenizer(target,":\n\r");
		if (tok.hasMoreTokens()) {
			String dienstid = tok.nextToken();
			if (tok.hasMoreTokens()) {
				String type = tok.nextToken();
				if (tok.hasMoreTokens()) {
					String veldid = tok.nextToken();
					log.info("SET DienstId="+dienstid+" entreeid="+entreeid+" type="+type+" veldid="+veldid);
					try {
					int d = Integer.parseInt(dienstid);
					int i = Integer.parseInt(veldid);
					if (type.equals("GS")) {
						return setGSField(d,entreeid,i,value); 
					} else {
						return setBSField(d,entreeid,i,value); 
					}
					} catch (Exception e) {}
				}
			}
		}
		return true;
	}



	// dummy routines
	private String getGSField(int dienstID, String entreeID, int veldID) {
		if (entreeID.equals("daniel")) {
		      switch (veldID) {
		        case 1:  return danielmsn;
            		case 2:  return danielyahoo;
            		case 3:  return danielicq;
		      }
		} else if (entreeID.equals("admin")) {
		      switch (veldID) {
		        case 1:  return adminmsn;
            		case 2:  return adminyahoo; 
            		case 3:  return adminicq; 
		      }
		}
		return null;
	}


	private boolean setGSField(int dienstID, String entreeID, int veldID,String value) {
		if (entreeID.equals("daniel")) {
		      switch (veldID) {
		        case 1:  danielmsn =  value; break;
            		case 2:  danielyahoo = value; break;
            		case 3:  danielicq = value; break;
		      }
		} else if (entreeID.equals("admin")) {
		      switch (veldID) {
		        case 1:  adminmsn =  value; break;
            		case 2:  adminyahoo = value; break;
            		case 3:  adminicq = value; break;
		      }
		}
		return true;
	}

	private String getBSField(int dienstID, String entreeID, int veldID) {
		return null;
	}

	private boolean setBSField(int dienstID, String entreeID, int veldID,String value) {
		return false;
	}
}
