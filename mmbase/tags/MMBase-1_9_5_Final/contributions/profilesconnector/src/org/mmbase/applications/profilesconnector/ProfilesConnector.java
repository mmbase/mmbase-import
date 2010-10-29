package org.mmbase.applications.profilesconnector;

import nl.kennisnet.profielen.webservices.*;
import nl.kennisnet.profielen.gedeeldeset.webservices.*;
import org.mmbase.applications.mmbob.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import java.lang.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class ProfilesConnector implements ExternalProfileInterface {

    static private Logger log = Logging.getLoggerInstance(ProfilesConnector.class);
    public static Map status = new HashMap();
    static {
        status.put(new Integer(0), "SUCCESSED");
        status.put(new Integer(1101), "GEEN_DATA_GEVONDEN");
        status.put(new Integer(1102), "GEEN_AANPASSING");
        status.put(new Integer(1111), "DIENSTID_NIET_GELDIG");
        status.put(new Integer(1113), "LOV_NIET_GELDIG");
        status.put(new Integer(1120), "VERPLICHT_VELD_ONTBREEKT");
        status.put(new Integer(1121), "PARAMETERS_ONGELDIG");
        status.put(new Integer(1201), "DATUM_TE_VER_IN_VERLEDEN");
        status.put(new Integer(1202), "EINDDATUM_VROEGER_DAN_BEGINDATUM");
        status.put(new Integer(1203), "ZOEKPERIODE_TE_GROOT");
        status.put(new Integer(1204), "ZOEKRESULTATEN_MEER_DAN_MAXIMUM");
        status.put(new Integer(1210), "NICKNAME_BEVAT_ACHTERNAAM");
        status.put(new Integer(1211), "NICKNAME_MAG_NIET_WIJZIGEN");
        status.put(new Integer(1212), "NICKNAME_NIET_UNIEK");
        status.put(new Integer(1220), "PROFIELNAAM_MAG_NIET_WIJZIGEN");
        status.put(new Integer(1221), "PROFIELNAAM_NIET_UNIEK");
        status.put(new Integer(1499), "DATABASE_FOUTMELDING");
    }

    String getVersion() {
        return "1.2.3";
    }

    /**
     * @see org.mmbase.applications.mmbob.ExternalProfileInterface#getValue(java.lang.String, java.lang.String) either
     *      using PROFIELEN or PROFIELENGS webservice to get a profile field. What service to use is determined by the
     *      target parameter. When 'target' has type 'GS' the PROFIELENGS service is used, and oterwise the PROFIELEN
     *      webservice is used
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
                        if (type.equals("GS")) {
                            try {
                                int i = Integer.parseInt(veldid);
                                return getGSField(d, entreeid, i);
                            } catch (NumberFormatException e) {
                                log.error("Field velidid with value " + veldid + " could not be parsed to an int");
                            }
                        } else {
                            return getBSField(d, entreeid, veldid);
                        }
                    } catch (NumberFormatException e) {
                        log.error("Field dienstid with value " + dienstid + " could not be parsed to an int");
                    }
                }
            }
        }
        return null;
    }

    /**
     * @see org.mmbase.applications.mmbob.ExternalProfileInterface#setValue(java.lang.String, java.lang.String,
     *      java.lang.String) either using PROFIELEN or PROFIELENGS webservice to set a profile field. What service to
     *      use is determined by the target parameter. When 'target' has type 'GS' the PROFIELENGS service is used, and
     *      oterwise the PROFIELEN webservice is used
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
                        if (type.equals("GS")) {
                            try {
                                int i = Integer.parseInt(veldid);
                                return setGSField(d, entreeid, i, value);
                            } catch (NumberFormatException e) {
                                log.error("Field velidid with value " + veldid + " could not be parsed to an int");
                                return false;
                            }
                        } else {
                            return setBSField(d, entreeid, veldid, value);
                        }
                    } catch (Exception e) {
                        log.error("Field dienstid with value " + dienstid + " could not be parsed to an int");
                        return false;
                    }
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
        log.debug("profielen url: " + profielenUrl);
        return profielenUrl;
    }

    private String initProfielenGS() {
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
        log.info("getBSField");
        String profielenUrl = initProfielen();
        try {
            URL url = new URL(profielenUrl);
            ProfielenService profielenService = new ProfielenServiceLocator();
            Profielen profielen = profielenService.getKennisnetProfielen(url);
            log.info("Client versie=" + ProfielenClient.getVersion());
            log.info("Server versie=" + profielen.getVersion());

            AntwoordProfiel result = profielen.getProfiel(dienstID, entreeID);
            Profiel pf = result.getProfiel();
            String value = "";
            if (veldID.equals("huisplaats")) {
                value = pf.getHuisPlaats();
            } else if (veldID.equals("nickname")) {
                value = pf.getNickName();
            }else if (veldID.equals("voornaam")) {
                value = pf.getVoorNaam();
            }else if (veldID.equals("achternaam")) {
                value = pf.getAchterNaam();
            }else if (veldID.equals("emailaddres")) {
                value = pf.getEmailAdres();
            }else if (veldID.equals("tussenvoegsel")) {
                value = pf.getTussenVoegsel();
            }
            else if (veldID.equals("geslacht")) {
                value = "" + pf.getGeslacht();
            } else if (veldID.equals("geboortedatum")) {
                value = pf.getGeboorteDatum().toString();
            } else if (veldID.equals("emailadres")) {
                value = pf.getEmailAdres();
            } else if (veldID.equals("huisland")) {
                value = pf.getHuisLand();
            } else if (veldID.equals("huisnummer")) {
                value = pf.getHuisNummer();
            } else if (veldID.equals("huispostcode")) {
                value = pf.getHuisPostcode();
            } else if (veldID.equals("leerjaar")) {
                value = "" + pf.getLeerJaar();
            } else if (veldID.equals("profielnaam")) {
                value = pf.getProfielNaam();
            } else if (veldID.equals("schoolland")) {
                value = pf.getSchoolLand();
            } else if (veldID.equals("schoolnaam")) {
                value = pf.getSchoolNaam();
            } else if (veldID.equals("schoolplaats")) {
                value = pf.getSchoolPlaats();
            } else if (veldID.equals("schoolrelatie")) {
                value = "" + pf.getSchoolRelatie();
            } else if (veldID.equals("soortopleiding")) {
                value = "" + pf.getSoortOpleiding();
            } else if (veldID.equals("studie")) {
                value = pf.getStudie();
            }

            log.debug("value for field '" + veldID + "' : " + value);
            return value;

        } catch (Exception e) {
            log.info("Foutje Profielen:Exception=" + e);
        }
        return null;
    }

    /**
     * tries to set a field of the basic profile.
     * @param dienstID
     * @param entreeID
     * @param veldID
     * @param value
     * @return false if the operation did not succeed.
     */
    private boolean setBSField(int dienstID, String entreeID, String veldID, String value) {

        String profielenUrl = initProfielen();
        try {
            URL url = new URL(profielenUrl);
            ProfielenService profielenService = new ProfielenServiceLocator();
            Profielen profielen = profielenService.getKennisnetProfielen(url);
            log.debug("Client versie=" + ProfielenClient.getVersion());
            log.debug("Server versie=" + profielen.getVersion());
            log.debug("setting value '" + value + "' in field '" + veldID + "'");

            AntwoordProfiel result = profielen.getProfiel(dienstID, entreeID);
            Profiel pf = result.getProfiel();

            if (veldID.equals("huisplaats")) {
                pf.setHuisPlaats(value);
            } else if (veldID.equals("nickname")) {
                pf.setNickName(value);
            } else if (veldID.equals("geslacht")) {
                pf.setGeslacht(new Integer(value).intValue());
            } else if (veldID.equals("emailadres")) {
                pf.setEmailAdres(value);
            } else if (veldID.equals("huisland")) {
                pf.setHuisLand(value);
            } else if (veldID.equals("huisnummer")) {
                pf.setHuisNummer(value);
            } else if (veldID.equals("huispostcode")) {
                pf.setHuisPostcode(value);
            } else if (veldID.equals("leerjaar")) {
                pf.setLeerJaar(new Integer(value).intValue());
            } else if (veldID.equals("profielnaam")) {
                pf.setProfielNaam(value);
            } else if (veldID.equals("schoolland")) {
                pf.setSchoolLand(value);
            } else if (veldID.equals("schoolnaam")) {
                pf.setSchoolNaam(value);
            } else if (veldID.equals("schoolplaats")) {
                pf.setSchoolPlaats(value);
            } else if (veldID.equals("schoolrelatie")) {
                pf.setSchoolRelatie(new Integer(value).intValue());
            } else if (veldID.equals("soortopleiding")) {
                pf.setSoortOpleiding(new Integer(value).intValue());
            } else if (veldID.equals("studie")) {
                pf.setStudie(value);
            } else if (veldID.equals("voornaam")) {
                pf.setVoorNaam(value);
            } else if (veldID.equals("tussenvoegsel")) {
                pf.setTussenVoegsel(value);
            } else if (veldID.equals("achternaam")) {
                pf.setAchterNaam(value);
            }
            int statusInt = profielen.updateProfiel(dienstID, pf);
            if (statusInt == 0) {
                log.debug("status: 0. update success");
                return true;
            } else {
                log.error("something went wrong setting field: " + veldID + " on the basic profile. Error code: " + statusInt + "("
                        + (String)status.get(new Integer(statusInt)) + ")");
            }
        } catch (Exception e) {
            System.out.println("something went wrong constructing a calendar from value: '" + value + "'");
            e.printStackTrace();
        }

        return false;
    }
}
