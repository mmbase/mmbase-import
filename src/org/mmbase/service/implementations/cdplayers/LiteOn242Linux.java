/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.implementations.cdplayers;

import java.io.*;
import java.util.StringTokenizer;

import org.mmbase.service.interfaces.*;

/**
 * @javadoc
 * @todo : fix use of logger
 * @author Daniel Ockeloen
 * @author Marcel
 * @version $Id: LiteOn242Linux.java,v 1.6 2002-04-29 10:54:17 pierre Exp $
 */
public class LiteOn242Linux implements cdplayerInterface {
    private String classname = getClass().getName();
    private void log( String msg ) { System.out.println( classname +":"+ msg ); }

    // Separate, because different methods use diffent string
    private String cdInfoFromCdthing;
    private String cdInfoFromCdda2wav;

    private String discID;
    private int cdLength = 0;
    private int tracks[];
    private int numberOfTracks;

    public LiteOn242Linux() { }

    public void startUp() { }

    public void shutDown() { }

    public String getVersion() {
        return("1.0.2 (fake)");
    }

    /**
     * @javadoc
     */
    public String getStatusCD() {
        return execute ("/usr/local/bin/cdthing -s");
    }

    /**
     * @javadoc
     */
    public String getListCD() {
        return execute ("/usr/local/bin/cdthing -l");
    }

    /**
     * @javadoc
     */
    public String startCD(String number)  {
        return execute ("/usr/local/bin/cdthing "+number);
    }

    /**
     * @javadoc
     */
    public String startCD() {
        return startCD("1");
    }

    /**
     * @javadoc
     */
    public String stopCD() {
        return execute ("/usr/local/bin/cdthing -S");
    }

    /**
     * @javadoc
     */
    public String ejectCD() {
        return execute ("/usr/local/bin/cdthing -E");
    }

    /**
     * @javadoc
     */
    public String randomCD() {
        return execute ("/usr/local/bin/cdthing -r");
    }

    /**
     * Actually performs the ripping by executing cdda2wav.
     * @param number
     * @param filename
     * @return the proces exitvalue 0 ok or !0 when something goesx wroing.
     */
    public int getTrack(int number, String filename) {
        return standardExecute("/usr/local/bin/cdda2wav -S 1 -D /dev/cdrom -I cooked_ioctl -t "+number+" "+filename);
    }

    /**
     * @javadoc
     */
    public void getInfo() {
        cdInfoFromCdthing = getCdInfoViaCdthing();
        cdInfoFromCdda2wav = getCdInfoViaCdda2wav();

        numberOfTracks = getNumberOfTracks();
        cdLength = getCDLength();
        discID = getDiscID();

        // Display information

        System.out.println("Information about disk : " + discID);
        for (int i =0; i < numberOfTracks; i++)
        {
            System.out.println("Track ["+i+"] " + convertMinutesSeconds(tracks[i]));
        }
        System.out.println("Total time : " + convertMinutesSeconds(cdLength));
    }

    /**
     * Hacked - this class should be redone, so I've added this as a fast hack
     * that can be parsed by a StringTagger.
     * @javadoc
     */
    public String getInfoCDtoString() {
        cdInfoFromCdthing = getCdInfoViaCdthing();
        cdInfoFromCdda2wav = getCdInfoViaCdda2wav();

        numberOfTracks = getNumberOfTracks();
        cdLength = getCDLength();
        discID = getDiscID();

        // Display information

        String result=("CDDB="+discID);
        result+=(" NROFTRACKS="+numberOfTracks);
        result+=(" CDLEN="+cdLength);
        for (int i =0; i < numberOfTracks; i++) {
            result+=(" TR"+i+"LEN=" + tracks[i]);
            result+=(" TR"+i+"TITLE=\"Unknown\"");
        }
        log("getInfoCDtoString "+result);
        return result;
    }

    /**
     * Execute cdthing and buffer output.
     * Output looks like this:
     *
     * cdthing version 1.4 by Dustin Sallings
     * 1:      8:42
     * 2:      10:49
     * .............
     * 8:      3:40
     * Found 8 tracks
     *
     * @return String output from cdthing
     */
    public String getCdInfoViaCdthing() {
        String result = execute("/usr/local/bin/cdthing -l");
        // rip first line, take everything after \n
        return result.substring(result.indexOf("\n")+1);
    }

    /**
     * @javadoc
     */
    public String getCdInfoViaCdda2wav() {
        return execute("/usr/local/bin/cdda2wav -D /dev/cdrom -I cooked_ioctl -N -H -V -v 3 -d 1");
    }

    /**
     * @javadoc
     */
    public String getDiscID() {
        StringTokenizer st = new StringTokenizer(cdInfoFromCdda2wav, "\n");
        String s, result=null;
        while (st.hasMoreTokens()) {
            // search on "CDDB discid: 0x....."
            s = st.nextToken();
            if (s.indexOf("CDDB discid: ")!=-1) {
                s = s.substring(s.indexOf(":")+1);
                result = s.trim();
                break;
            }
        }
        return result;
    }

    /**
     * @javadoc
     * @param int track-number
     * @return track-length in seconds
     */
    public int getTrackLength(int number) {
        StringTokenizer st = new StringTokenizer(cdInfoFromCdthing, "\n\t: ");
        String ss;
        int minutes=0, seconds=0, result=0;
        while(st.hasMoreTokens()) {
            ss = st.nextToken();
            if (ss.equals(""+number)) {
                minutes = Integer.parseInt(st.nextToken().trim());
                seconds = Integer.parseInt(st.nextToken().trim());
                // eerst maar terug als strin
                result = (minutes * 60 + seconds);
                break;
            } else {
                st.nextToken();
                st.nextToken();
            }
        }
        return result;
    }

    /**
     * @javadoc
     */
    public int getCDLength() {
        int totalLength = 0;
        int trackLength = 0;
        tracks = new int[getNumberOfTracks()];
        for (int track =1; track < numberOfTracks+1; track++) {
            trackLength = getTrackLength(track);
            totalLength += trackLength;
            tracks[track-1]= trackLength;
        }
        cdLength = totalLength;
        return totalLength;
    }

    /**
     * @javadoc
     */
    public int getNumberOfTracks() {
        String s;
        int result = 0;
        StringTokenizer st = new StringTokenizer(cdInfoFromCdthing, "\n");
        while (st.hasMoreTokens()) {
            // "Found x tracks", we seek x
            s = st.nextToken();
            if ((s.indexOf("Found")!=-1) && (s.indexOf("tracks")!=-1)) {
                s = s.substring(s.indexOf(" ")+1);
                s = s.substring(0,s.indexOf(" "));
                result = Integer.parseInt(s);
                break;
            }
        }
        return result;
    }

    /**
     * @javadoc
     */
    public String convertMinutesSeconds(int time) {
        int hour = time / 60;
        int minutes = (time - (hour*60));
        return "" + hour +":"+ minutes;
    }

    /**
     * Executes the given command and returns a String with stdout and stderror.
     * @param command command to be executed.
     * @return returns a String with stdout and stderror returnvalues. or null if something goes wrong
     */
    private String execute(String command) {
        Process p=null;
        String s="",tmp="";
        // Execute and wait for process to finish.
        try {
            p = (Runtime.getRuntime()).exec(command,null);
            p.waitFor(); // Added waitFor
        } catch (Exception e) {
            log("execute: ERROR: "+e.toString()+" returning null");
            e.printStackTrace();
            return null;
        }
        // Put info from std output stream of process in a String and add stderror after that.
        BufferedReader dip = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader dep = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        try {
            while ((tmp = dip.readLine()) != null) {
                s+=tmp+"\n";
            }
            while ((tmp = dep.readLine()) != null) {
                s+=tmp+"\n";
            }
        } catch (Exception e) {
            log("execute: ERROR getting info from stdout or stderr, returning what I read sofar:"+s);
            e.printStackTrace();
            return s;
        }

        // Print exitvalue of the process as the returnvalue
        try {
            int exitValue = p.exitValue();
            s += ", exitvalue:"+exitValue+"\n";
            log("execute: Done, exitvalue:"+exitValue);
            log("execute: Returning string: "+s);
            return s;
        } catch (IllegalThreadStateException itse) {
            log("execute: ERROR getting exitvalue, returning string with stdout & err info: "+s);
            itse.printStackTrace();
            return s;
        }
    }

    /**
     * Executes the given command and returns the process exitvalue.
     * The Difference with the execute method is that this one returns the process exitvalue.
     * This method is used by getTrack since exitvalue is used later for recovery etc...
     * @param command command to be executed.
     * @return returns proces exitValue, value 0 indicates normal termination, !0 otherwise.
     */
    private int standardExecute(String command) {
        Process p=null;
        String s="",tmp="";
        // Execute and wait for process to finish.
        try {
            p = (Runtime.getRuntime()).exec(command,null);
            p.waitFor(); // Added waitFor
        } catch (Exception e) {
            log("execute: ERROR: "+e.toString()+" returning 1 as exitvalue.");
            e.printStackTrace();
            return 1;
        }
        // Put info from std output stream of process in a String and add stderror after that.
        BufferedReader dip = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader dep = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        try {
            s = "stdout:";
            while ((tmp = dip.readLine()) != null) {
                s+=tmp+"\n";
            }
            s += ", stderr:";
            while ((tmp = dep.readLine()) != null) {
                s+=tmp+"\n";
            }
        } catch (Exception e) {
            log("execute: ERROR getting info from stdout or stderr, sofar I've read: "+s+", returning 1 as exitvalue.");
            e.printStackTrace();
            return 1;
        }

        // Return exitvalue of the process as the returnvalue
        try {
            int exitValue = p.exitValue();
            s += ", exitvalue:"+exitValue+"\n";
            log("execute: Done "+s);
            return exitValue;
        } catch (IllegalThreadStateException itse) {
            log("execute: ERROR getting exitvalue: "+s+", returning 1 as exitvalue.");
            itse.printStackTrace();
            return 1;
        }
    }

    /**
     * @javadoc
     */
    public static void main(String args[]) {
        LiteOn242Linux cd = new LiteOn242Linux();
        System.out.println(cd.getInfoCDtoString());
    }
}
