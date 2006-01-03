package org.mmbase.applications.profilesconnector;

import nl.kennisnet.profielen.webservices.*;
import nl.kennisnet.profielen.gedeeldeset.webservices.*;
import org.mmbase.applications.mmbob.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class ProfilesConnector implements ExternalProfileInterface {
	
        static private Logger log = Logging.getLoggerInstance(ProfilesConnector.class);
	static String PROFIELEN = "http://profielen.test.kennisnet.nl/profielen/services/KennisnetProfielen";
	static String PROFIELENGS = "http://profielen.test.kennisnet.nl/profielen/services/KennisnetProfielenGS";

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
						return getBSField(d,entreeid,veldid); 
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
		try {
			URL url = new URL(PROFIELENGS);
	       		ProfielenService profielenService = new ProfielenServiceLocator();
	       		Profielen profielen = profielenService.getKennisnetProfielen(url);
	       		log.info("Client versie="+ProfielenClient.getVersion()); 
	       		log.info("Server versie="+profielen.getVersion());
			
			GedeeldeSetImpl gs = (GedeeldeSetImpl)profielen;
		
			log.info("GS="+gs);
			AntwoordField result = gs.getField(dienstID,entreeID,veldID);
			Field[] fieldlist = result.getFieldList();
			Field value = fieldlist[0];
			log.info("RESULT "+value.getTekst());
			return value.getTekst();
		} catch (Exception e) {
	       		log.info("Foutje Profielen:Exception=" + e);
		}	
		return null;
	}


	private boolean setGSField(int dienstID, String entreeID, int veldID,String value) {
		return true;
	}

	private String getBSField(int dienstID, String entreeID, String veldID) {
		try {
			URL url = new URL(PROFIELEN);
	       		ProfielenService profielenService = new ProfielenServiceLocator();
	       		Profielen profielen = profielenService.getKennisnetProfielen(url);
	       		log.info("Client versie="+ProfielenClient.getVersion()); 
	       		log.info("Server versie="+profielen.getVersion());


			AntwoordProfiel result = profielen.getProfiel(dienstID,entreeID);
			Profiel pf = result.getProfiel();
			if (veldID.equals("huisplaats")) {
				return pf.getHuisPlaats();
			} else if (veldID.equals("nickname")) {
				return pf.getNickName();
			} else if (veldID.equals("geslacht")) {
				return ""+pf.getGeslacht();
			} else if (veldID.equals("geboortedatum")) {
				return pf.getGeboorteDatum().toString();
			} else if (veldID.equals("emailadres")) {
				return pf.getEmailAdres();
			} else if (veldID.equals("huisland")) {
				return pf.getHuisLand();
			} else if (veldID.equals("huisnummer")) {
				return pf.getHuisNummer();
			} else if (veldID.equals("huispostcode")) {
				return pf.getHuisPostcode();
			} else if (veldID.equals("leerjaar")) {
				return ""+pf.getLeerJaar();
			} else if (veldID.equals("profielnaam")) {
				return pf.getProfielNaam();
			} else if (veldID.equals("schoolland")) {
				return pf.getSchoolLand();
			} else if (veldID.equals("schoolnaam")) {
				return pf.getSchoolNaam();
			} else if (veldID.equals("schoolplaats")) {
				return pf.getSchoolPlaats();
			} else if (veldID.equals("schoolrelatie")) {
				return ""+pf.getSchoolRelatie();
			} else if (veldID.equals("soortopleiding")) {
				return ""+pf.getSoortOpleiding();
			} else if (veldID.equals("studie")) {
				return pf.getStudie();
			}
		
		} catch (Exception e) {
	       		log.info("Foutje Profielen:Exception=" + e);
		}	
		return null;
	}

	private boolean setBSField(int dienstID, String entreeID, int veldID,String value) {
		return false;
	}
}
