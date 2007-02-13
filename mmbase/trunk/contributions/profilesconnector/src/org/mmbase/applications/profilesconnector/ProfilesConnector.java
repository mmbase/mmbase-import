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

    String getVersion() {
        return "1.2.3";
    }

    /**
     * @see org.mmbase.applications.mmbob.ExternalProfileInterface#getValue(java.lang.String, java.lang.String)
     * either using PROFIELEN or PROFIELENGS webservice to get a profile field. What service to use is determined by the 
     * target parameter.
     * When 'target' has type 'GS' the PROFIELENGS service is used, and oterwise the PROFIELEN webservice is used 
     */
    public String getValue(String entreeid, String target) {
        StringTokenizer tok = new StringTokenizer(target, ":\n\r");
        if (tok.hasMoreTokens()) {
            String dienstid = tok.nextToken();
            if (tok.hasMoreTokens()) {
                String type = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String veldid = tok.nextToken();
                    log.info("GET DienstId=" + dienstid + " entreeid=" + entreeid + " type=" + type + " veldid=" + veldid);
                    try {
                        int d = Integer.parseInt(dienstid);
                        int i = Integer.parseInt(veldid);
                        if (type.equals("GS")) {
                            return getGSField(d, entreeid, i);
                        } else {
                            return getBSField(d, entreeid, veldid);
                        }
                    } catch (Exception e) {}
                }
            }
        }
        return null;
    }

    /**
     * @see org.mmbase.applications.mmbob.ExternalProfileInterface#setValue(java.lang.String, java.lang.String, java.lang.String)
     * either using PROFIELEN or PROFIELENGS webservice to set a profile field. What service to use is determined by the 
     * target parameter.
     * When 'target' has type 'GS' the PROFIELENGS service is used, and oterwise the PROFIELEN webservice is used 
     */
    public boolean setValue(String entreeid, String target, String value) {
        StringTokenizer tok = new StringTokenizer(target, ":\n\r");
        if (tok.hasMoreTokens()) {
            String dienstid = tok.nextToken();
            if (tok.hasMoreTokens()) {
                String type = tok.nextToken();
                if (tok.hasMoreTokens()) {
                    String veldid = tok.nextToken();
                    log.info("SET DienstId=" + dienstid + " entreeid=" + entreeid + " type=" + type + " veldid=" + veldid);
                    try {
                        int d = Integer.parseInt(dienstid);
                        int i = Integer.parseInt(veldid);
                        if (type.equals("GS")) {
                            return setGSField(d, entreeid, i, value);
                        } else {
                            return setBSField(d, entreeid, i, value);
                        }
                    } catch (Exception e) {}
                }
            }
        }
        return true;
    }

    // dummy routines
    private String getGSField(int dienstID, String entreeID, int veldID) {
        String profielenGsUrl = initProfielenGS();
        try {
            URL url = new URL(profielenGsUrl);
            ProfielenService profielenService = new ProfielenServiceLocator();
            Profielen profielen = profielenService.getKennisnetProfielen(url);
            log.info("Client versie=" + ProfielenClient.getVersion());
            log.info("Server versie=" + profielen.getVersion());

            GedeeldeSetImpl gs = (GedeeldeSetImpl) profielen;

            log.info("GS=" + gs);
            AntwoordField result = gs.getField(dienstID, entreeID, veldID);
            Field[] fieldlist = result.getFieldList();
            Field value = fieldlist[0];
            log.info("RESULT " + value.getTekst());
            return value.getTekst();
        } catch (Exception e) {
            log.info("Foutje Profielen:Exception=" + e);
        }
        return null;
    }

    /**
     * read the url's for the webservices from global properties profielen and profielengs.
     */
    private String initProfielen() {
        String profielenUrl = ForumManager.getProperty("profielen");
        try {
            new URL(profielenUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("could not create valid url from property 'profielen' with value: '" + profielenUrl + "'");
        }
        return profielenUrl;
    }
    
    private String initProfielenGS(){
        String profielenGsUrl = ForumManager.getProperty("profielengs");
        try {
            new URL(profielenGsUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("could not create valid url from property 'profielen' with value: '" + profielenGsUrl + "'");
        }
        return profielenGsUrl;
    }

    private boolean setGSField(int dienstID, String entreeID, int veldID, String value) {
        return true;
    }

    /**
     * Read a profile field from the 'profielen' webservice;
     * @param dienstID
     * @param entreeID
     * @param veldID
     * @return
     */
    private String getBSField(int dienstID, String entreeID, String veldID) {
        String profielenUrl = initProfielen();
        try {
            URL url = new URL(profielenUrl);
            ProfielenService profielenService = new ProfielenServiceLocator();
            Profielen profielen = profielenService.getKennisnetProfielen(url);
            log.info("Client versie=" + ProfielenClient.getVersion());
            log.info("Server versie=" + profielen.getVersion());

            AntwoordProfiel result = profielen.getProfiel(dienstID, entreeID);
            Profiel pf = result.getProfiel();
            if (veldID.equals("huisplaats")) {
                return pf.getHuisPlaats();
            } else if (veldID.equals("nickname")) {
                return pf.getNickName();
            } else if (veldID.equals("geslacht")) {
                return "" + pf.getGeslacht();
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
                return "" + pf.getLeerJaar();
            } else if (veldID.equals("profielnaam")) {
                return pf.getProfielNaam();
            } else if (veldID.equals("schoolland")) {
                return pf.getSchoolLand();
            } else if (veldID.equals("schoolnaam")) {
                return pf.getSchoolNaam();
            } else if (veldID.equals("schoolplaats")) {
                return pf.getSchoolPlaats();
            } else if (veldID.equals("schoolrelatie")) {
                return "" + pf.getSchoolRelatie();
            } else if (veldID.equals("soortopleiding")) {
                return "" + pf.getSoortOpleiding();
            } else if (veldID.equals("studie")) {
                return pf.getStudie();
            }

        } catch (Exception e) {
            log.info("Foutje Profielen:Exception=" + e);
        }
        return null;
    }

    private boolean setBSField(int dienstID, String entreeID, int veldID, String value) {
        return false;
    }
}
