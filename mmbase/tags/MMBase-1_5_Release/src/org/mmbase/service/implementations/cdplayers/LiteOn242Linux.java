/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.service.implementations.cdplayers;


import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.service.interfaces.*;


/**
 * @author Daniel,Marcel
 * @version $Revision: 1.5 $ $Date: 2001-04-19 13:37:00 $
 */
public class LiteOn242Linux implements cdplayerInterface {
	private String  classname       = getClass().getName();
	private boolean debug           = true;
	private void    debug( String msg ) { System.out.println( classname +":"+ msg ); }

	public void startUp() { }

	public void shutDown() { }

	public LiteOn242Linux() { }

	public String getVersion() {
		return("1.0.2 (fake)");
	}

	public String getStatusCD() {
		String s=null;
        //while (s==null || s.equals("")) 
		s = execute ("/usr/local/bin/cdthing -s");
		// s = execute((execcommand+parameters.get("getstatus")).trim());
		return(s);
	}

	public String getListCD() {
		String s=null;
		s = execute ("/usr/local/bin/cdthing -l");
		//s= execute((execcommand+parameters.get("getlist")).trim());
		//list=s;
		return(s);
	}

	public String startCD(String number)  {
        String s = null;
		s = execute ("/usr/local/bin/cdthing "+number);
		return(s);
	}

    public String startCD() {
        String s = null;
        //while (s==null || s.equals("")) 
    		startCD("1");
		return (s);
	}

	public String stopCD() {
        String s = null;
        //while (s==null || s.equals("")) 
		s=execute ("/usr/local/bin/cdthing -S");
		//s= execute((execcommand+parameters.get("stop")).trim());
		return(s);
	}

	public String ejectCD() {
        String s = null;
        //while (s==null || s.equals("")) 
		s=execute ("/usr/local/bin/cdthing -E");
		//s= execute((execcommand+parameters.get("eject")).trim());
        return (s);
	}

	public String randomCD() {
        String s = null;
        //while (s==null || s.equals(""))
		s=execute ("/usr/local/bin/cdthing -r");
		//s= execute((execcommand+parameters.get("random")).trim());
		return("");
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


/** -------------------------------------------------------- */

	// Sperate, because different methods use diffent string
	private String cdInfoFromCdthing;
	private String cdInfoFromCdda2wav;

	private String discID;	
	private int cdLength = 0;
	private int tracks[];
	private int numberOfTracks;


	public void getInfo()
	{
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

	// hacked this class should be redone, so ive added this as a fast hack
	// that can be parsed by a StringTagger
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
		for (int i =0; i < numberOfTracks; i++)
		{
			result+=(" TR"+i+"LEN=" + tracks[i]);
			result+=(" TR"+i+"TITLE=\"Unknown\"");
		}
		System.out.println("getInfoCDtoString "+result);
		return(result);
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
	public String getCdInfoViaCdthing()
	{
		String result = execute("/usr/local/bin/cdthing -l");
		// rip first line, take everything after \n
		result = result.substring(result.indexOf("\n")+1);
		return(result);
	}

	public String getCdInfoViaCdda2wav()
	{
		String result = execute("/usr/local/bin/cdda2wav -D /dev/cdrom -I cooked_ioctl -N -H -V -v 3 -d 1");
		return(result);
	}

	public String getDiscID()
	{
		StringTokenizer st = new StringTokenizer(cdInfoFromCdda2wav, "\n");
		String s, result=null;
		while (st.hasMoreTokens())
		{
			// search on "CDDB discid: 0x....."

			s = st.nextToken();
			if (s.indexOf("CDDB discid: ")!=-1)
			{
				s = s.substring(s.indexOf(":")+1);
				result = s.trim();
				break;
			}
		}
		return(result);
	}

	/**
	* @param int track-number
	* @return track-length in seconds
	*/
	public int getTrackLength(int number)
	{
		StringTokenizer st = new StringTokenizer(cdInfoFromCdthing, "\n\t: ");
		String ss; 
		int minutes=0, seconds=0, result=0;
		while(st.hasMoreTokens())
		{
			ss = st.nextToken();
			if (ss.equals(""+number))
			{
				minutes = Integer.parseInt(st.nextToken().trim());
				seconds = Integer.parseInt(st.nextToken().trim());

				// eerst maar terug als string
				
				result = (minutes * 60 + seconds);	
				break;
			}
			else
			{
				st.nextToken();
				st.nextToken();
			}
		}
		return result;
	}

	public int getCDLength()
	{
		int totalLength = 0;
		int trackLength = 0;
		tracks = new int[getNumberOfTracks()];
		for (int track =1; track < numberOfTracks+1; track++)
		{
			trackLength = getTrackLength(track);
			totalLength += trackLength;	
			tracks[track-1]= trackLength;				
		}
		cdLength = totalLength;
	 	return(totalLength);	
	}

	public int getNumberOfTracks()
	{
		String s;
		int result = 0;
		StringTokenizer st = new StringTokenizer(cdInfoFromCdthing, "\n");
		while (st.hasMoreTokens())
		{
			// "Found x tracks", we seek x
			s = st.nextToken();
			if ((s.indexOf("Found")!=-1) && (s.indexOf("tracks")!=-1))
			{
				s = s.substring(s.indexOf(" ")+1);
				s = s.substring(0,s.indexOf(" "));
				result = Integer.parseInt(s);
				break;
			}
		}
		return result;
	}	

	public String convertMinutesSeconds(int time)
	{
		int hour = time / 60;
		int minutes = (time - (hour*60));
		return("" + hour +":"+ minutes );
	}

	/**
	 * Executes the given command and returns a String with stdout and stderror.
	 * @param command command to be executed.
	 * @return returns a String with stdout and stderror returnvalues. or null if something goes wrong
	 */
	private String execute (String command) {
		Process p=null;
		String s="",tmp="";
		// Execute and wait for process to finish.
		try {
			p = (Runtime.getRuntime()).exec(command,null);
			p.waitFor(); // Added waitFor
		} catch (Exception e) {
			debug("execute: ERROR: "+e.toString()+" returning null");
			e.printStackTrace();
			return null;
		}
		// Put info from std output stream of process in a String and add stderror after that.
		DataInputStream dip = new DataInputStream(p.getInputStream());
		DataInputStream dep = new DataInputStream(p.getErrorStream());
		try {
			while ((tmp = dip.readLine()) != null) 
				s+=tmp+"\n"; 
			while ((tmp = dep.readLine()) != null)
				s+=tmp+"\n"; 
        	} catch (Exception e) {
			debug("execute: ERROR getting info from stdout or stderr, returning what I read sofar:"+s);
			e.printStackTrace();
			return s;
	   	}

		// Print exitvalue of the process as the returnvalue
		try {
			int exitValue = p.exitValue();
			s += ", exitvalue:"+exitValue+"\n";
			debug("execute: Done, exitvalue:"+exitValue);
			debug("execute: Returning string: "+s);
			return s;
		} catch (IllegalThreadStateException itse) {
			debug("execute: ERROR getting exitvalue, returning string with stdout & err info: "+s);
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
	private int standardExecute (String command) {
		Process p=null;
		String s="",tmp="";
		// Execute and wait for process to finish.
		try {
			p = (Runtime.getRuntime()).exec(command,null);
			p.waitFor(); // Added waitFor
		} catch (Exception e) {
			debug("execute: ERROR: "+e.toString()+" returning 1 as exitvalue.");
			e.printStackTrace();
			return 1;
		}
		// Put info from std output stream of process in a String and add stderror after that.
		DataInputStream dip = new DataInputStream(p.getInputStream());
		DataInputStream dep = new DataInputStream(p.getErrorStream());
		try {
			s = "stdout:";
			while ((tmp = dip.readLine()) != null) 
				s+=tmp+"\n"; 
			s += ", stderr:";
			while ((tmp = dep.readLine()) != null)
				s+=tmp+"\n"; 
        	} catch (Exception e) {
			debug("execute: ERROR getting info from stdout or stderr, sofar I've read: "+s+", returning 1 as exitvalue.");
			e.printStackTrace();
			return 1;
	   	}

		// Return exitvalue of the process as the returnvalue
		try {
			int exitValue = p.exitValue();
			s += ", exitvalue:"+exitValue+"\n";
			debug("execute: Done "+s);
			return exitValue;
		} catch (IllegalThreadStateException itse) {
			debug("execute: ERROR getting exitvalue: "+s+", returning 1 as exitvalue.");
			itse.printStackTrace();
			return 1;
		}
	}

	public static void main(String args[]) {
		LiteOn242Linux cd = new LiteOn242Linux();
		System.out.println(cd.getInfoCDtoString());
	}
}
