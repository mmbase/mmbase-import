/*
 * $Id: Playlists.java,v 1.4 2000-03-24 14:33:53 wwwtech Exp $
 * 
 * VPRO (C)
 * This source file is part of mmbase and is (c) by VPRO until it is being
 * placed under opensource. This is a private copy ONLY to be used by the
 * MMBase partners.
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2000/02/24 13:16:13  wwwtech
 * - (marcel) Changed System.out into debug
 *
 */

package org.mmbase.module;

import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;	

/**
 * @author Daniel Ockeloen
 * @author Rob Vermeulen
 * @author Rico Jansen
 * 
 * @version $Revision: 1.4 $ $Date: 2000-03-24 14:33:53 $
 */
public class Playlists extends ProcessorModule implements PlaylistsInterface  {

	private String classname = getClass().getName();
	private boolean debug	 = false; 
	private void debug( String msg ) { System.out.println( classname +":"+msg ); }
	private static void debug2( String msg ) { System.out.println( "org.mmbase.module.Playlists:"+msg ); }

	private static final int maxRAspeed   = 96000;
	private static final int minRAspeed   = 16000;
	private static final int maxRAchannel = 2;
	private static final int minRAchannel = 1;

    	private MMBase mmbase;
	int delaycount=0;
	boolean flipper=false;

	Hashtable playcache=new Hashtable();
	Hashtable urlcache=new Hashtable();

	// cache for playlists
	// -------------------
	Hashtable listCache=new Hashtable();

	// Vector with dirty playlistItems
	// -------------------------------
	Vector dirtyItems = new Vector();
	Hashtable ItemCache=new Hashtable();

	MusicInterface  music;

	
	/**
	*
	*/
	public void init() {
    		mmbase=(MMBase)getModule("MMBASEROOT");
    		music=(MusicInterface)getModule("MUSIC");
	}
	
	
	/**
	*
	*/
	public void reload() {
			init();
	}

	
	/**
	*
	*/
	public void onload() {
	}

	
	/**
	*
	*/
	public void unload() {
	}
	
	/**
	*
	*/
	public void shutdown() {
	}
 
	/**
	*	Handle a $MOD command
	*/
	public String replace(HttpServletRequest req, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			//if (cmd.equals("EXTRACT")) return(getVars(req,tok.nextToken()));

		}
		return("No command defined");
	}
	
	/**
	*
	*/
	public Vector  getList(HttpServletRequest requestInfo,StringTagger tagger, String value) {
		//debug("Playlist->"+tagger);
		//debug("Playlist->"+value);
    		String line = Strip.DoubleQuote(value,Strip.BOTH);

		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("PLAYLIST")) return(doPlayList(tok,tagger));
			debug("Playlists : getList not parsed : "+line);
		}
		return(null);
	}

	/**
	* obtain the Vector for the fromtend command of 'PLAYLIST'
	*/
	public synchronized Vector doPlayList(StringTokenizer tok,StringTagger tagger) {
		
		//time("doPlayList(begin)", true);

		Vector results=new Vector();
		try {
		String number=tagger.Value("NODE");
		String so=tagger.Value("TYPE");
		if (so==null) so="cdtracks";
		MMObjectBuilder bul=mmbase.getMMObject("pools");
		MMObjectNode node=getAliasedNode(number);

	   	if (node!=null) 
		{
			// temp hack !!
			Vector list;
			if (so.equals("cdtracks")) {
				list=getTable2(node,so);
			} else {
				list=getTable(node);
			}
		   	list=SortedVector.SortVector(list,new PlaylistScoreCompare());	

			double playlistsize = list.size();
			int a=1,b=1;
			try {
				String part = (String)tagger.get("PART");
				// a = Dat deel van de playlist dat afgedrukt moet worden.
				// b = Het aantal delen waaruit de playlist bestaat.
				a=Integer.parseInt(part.substring(1,part.indexOf('-')));
				b=Integer.parseInt(part.substring(part.indexOf('-')+1,part.length()-1));
			} catch (Exception e) {
				a=1; b=1; // Laat de gehele playlist zien.
			}
			int begin = (a-1)*(int)Math.ceil(playlistsize/(double)b);
			int end = (a*(int)Math.ceil(playlistsize/(double)b))-1;
			for (int index=begin; index<=end; index++) {
				try {	
					//debug("Playlists -> index = "+index);
					PlaylistItem newi=(PlaylistItem)list.elementAt(index);
					results.addElement(""+newi.id);
					results.addElement(newi.title);
					results.addElement(""+newi.score);
					results.addElement(""+newi.oldpos);
					results.addElement(""+(index+1));

					int now=(int)(System.currentTimeMillis()/1000);	
					int then=newi.startdate;
					int len=3600*24*7;
					if ((then+len)>now) {
						results.addElement("new");
					} else {
						results.addElement("old");
					}
					results.addElement(""+newi.group);
					results.addElement(""+newi.groupId);
					
					// startstop
					// results.addElement("" + newi.starttime);
					// results.addElement("" + newi.stoptime);

               	} catch (Exception e) {
                   	// Trying to put some lasts element into vector
               	}
			}
		}
		tagger.setValue("ITEMS","8");

		// startstop
		//tagger.setValue("ITEMS","10");

		} catch (Exception e) {
			e.printStackTrace();
		}

		//debug("doPlayList(): calc-time("+(System.currentTimeMillis() - oldtime)+")");
	
		return(results);
	}


	/**
	* 
	*/
	public boolean process(HttpServletRequest req, Hashtable cmds,Hashtable vars) {
		String doProcess = (String)vars.get("DO");
		if(doProcess==null) debug("Playlists -> No 'PRC-VAR-DO' specified in HTML-page.");

		if(doProcess.equals("VOTE")) {
			return computeVotes(cmds, (String)vars.get("NODE"));
			//debug("IGNORE ON VOTES");
			//return(true);
		}
		if(doProcess.equals("SELECTED")) {
			debug("cmds = "+cmds);
			debug("vars = "+vars);
		}
		return(true);
	}


	/**
	* creates the ram file for a playlist, called by servdb servlet mostly
	* can be controlled by params in form of speed, channels, way of 
	* playback, jingles and selections
	*/
	public byte[] getRAMfile(boolean isInternal, Vector params) {
		time("doRAMFile(begin)", true);
		// check cache

		// String machineName = getProperty("server","MachineName"); if machineName.equals("TEST1")){;} else {;}
		if (debug) debug("getRAMFile() : Start");

		MMObjectNode node;
		String strid;
		int jinglefreq=6;
		Vector PJingles=null,SPJingles;
		// get info field
		String info=getParamValue("info",params);
		if (info==null) info="VPRO";
		info=info.replace('_',' ');

		// try to get a list
		String list=getParamValue("l",params);

		// try to get a list
		String program=getParamValue("pr",params);

		// try to get a list
		String age=getParamValue("age",params);

		// get the poolnr from params
		String poolNr=getParamValue("p",params);

		// get the bandnr from params
		String groupNr=getParamValue("g",params);

		// get the epipoolnr from params
		String epipoolNr=getParamValue("pe",params);

		// get the epipoolnr from params
		String epiAudioparts=getParamValue("ea",params);

		// get the play methode from params
		String method=getParamValue("m",params);
		if (method==null) method="plain";

		// get the speed from params
		String speed=getParamValue("s",params);
		if (speed==null) speed="16000";

		// get the channels from params
		String channels=getParamValue("c",params);
		if (channels==null) channels="1";
		
		// get the 'only' nodes from params
		String only=getParamValue("o",params);

		// get the the begin-jingle from params
		String beginJingle=getParamValue("bj",params);

		// get the the end-jingle from params
		String endingJingle=getParamValue("ej",params);

		// get the the end-jingle from params
		Vector Jingles=getParamValues("ji",params);

		String PJingle=getParamValue("jp",params);

		String jfreq=getParamValue("jf",params);

		String ProgramPool=getParamValue("po",params);

		String frq=getParamValue("f",params); // Not Used ?

		int freq;
		try {
			freq=Integer.parseInt(frq);
		} catch (NumberFormatException e) {
			freq=3;
		}


		// debug output for the different params

		if (debug) debug("Pools="+poolNr);	
		if (debug) debug("Program="+program);	
		if (debug) debug("Method="+method);	
		if (debug) debug("Speed="+speed);	
		if (debug) debug("Channels="+channels);	
		if (debug) debug("Begin-Jingle="+beginJingle);
		if (debug) debug("Ending-Jingle="+endingJingle);
		if (debug) debug("Jingle="+Jingles);
		if (debug) debug("PJingle="+PJingle);
		if (debug) debug("ProgramPool="+ProgramPool);
		if (debug) debug("Only="+only);
		if (debug) debug("List="+list);
		if (debug) debug("Age="+age);
		if (debug) debug("EpiAudio="+epiAudioparts);
		if (debug) debug("JingleFreq="+jfreq);
		if (debug) debug("Group="+groupNr);

		String fmachine="station.vpro.nl";
		//if (flipper) fmachine="noise.vpro.nl";
		flipper=!flipper;

		// time("start pool", false);
		// get the playlist in a vector of nodes

		boolean playRandom = method.equals("random");
		boolean playShuffled = method.equals("frequentie");
		boolean playFreqAge = method.equals("aged");
		if (playFreqAge) playRandom=true; // bypass because code isn't built yet RICO

		int maxNumberOfTracks = 75;

		if (jfreq!=null) {
			try {
				jinglefreq=Integer.parseInt(jfreq);
			} catch (NumberFormatException x) {
				jinglefreq=6;
			}
		}
		if (debug) debug("JingleFreq="+jfreq+" : "+jinglefreq);

		Vector numbers=new Vector();
		if (mmbase!=null) 
		{
			if (ProgramPool!=null) {
				try {
					// RICO aliased
				   node=getAliasedNode(ProgramPool);
					if (debug) debug("ProgramPool node "+node);
				   if (node!=null) {
					 Vector p=getTableVector(node,"programs");
						if (debug) debug("Program Vector "+p);
					 if (p.size()>0) {
						program=vector2string(p);
					 } else {
						debug("Playlists : No programs in pool "+node);
					}
				   }
				} catch(Exception e) {
					debug("servdb>no pool defined");
				}
			}
			if (poolNr!=null) 
			{
				if (debug) debug("doRAMFile(): poolNr("+poolNr+"): start");
				MMObjectBuilder bul=mmbase.getMMObject("pools");
				try 
				{
					if (debug) debug("doRAMFile(): pools start");
					//time("doRAMFile(begin node)", false);

					// RICO aliased
			   		node=getAliasedNode(poolNr);
//			   		node=bul.getNode(poolNr);
			   		if (node!=null) 
					{
						if (debug) debug("doRAMFile(): node("+node+") not null");
						//numbers=getTableListVector( tr,  playRandom, maxNumberOfTracks, age);

						// bla
						numbers=getTable2(node,"cdtracks");

							 if( numbers == null 	 )	debug("doRAMFile(): ERROR: at poolNr("+poolNr+"): numbers is null!");
						else if( numbers.size() == 0 ) 	debug("doRAMFile(): ERROR: at poolNr("+poolNr+"): numbers is empty!");

						if (only!=null) 
						{
							if (debug) debug("doRAMFile(): getSubList() - start");
							// create a new vector from the list & nodes
							numbers=getSublist(numbers,only);


							 	 if( numbers == null 	 )	debug("doRAMFile(): ERROR: at poolNr("+poolNr+"): at getSubList(): numbers is null!");
							else if( numbers.size() == 0 ) 	debug("doRAMFile(): ERROR: at poolNr("+poolNr+"): at getSubList(): numbers is empty!");

							if (debug) debug("doRAMFile(): getSubList() - end");
						}


						//time("doRAMFile(age begin)", false);	
						// is the age set ifso lets ignore all the ones to old
						if (age!=null) 
						{
							try 
							{
								int agenr=Integer.parseInt(age);
								if (debug) debug("doRamFile: Filtering numbers("+numbers.size()+") on age("+age+")");
								numbers=filterAge(numbers,agenr);

							 		 if( numbers == null 	 )	debug("doRAMFile(): at filterAge(): ERROR: numbers is null!");
								else if( numbers.size() == 0 ) 	debug("doRAMFile(): at filterAge(): ERROR: numbers is empty!");

								if (debug) debug("doRamFile: Filtering numbers("+numbers.size()+") done.");
							} catch (Exception e) {

								debug("doRamFile(): age("+age+") is not a number!");
							}
						}
						// its the end 
			   		}
					//time("doRAMFile(end node)", false);

				} catch(Exception e) {
					debug("servdb>no pool defined");
				}

				if (debug) debug("doRAMFile(): end pool");
			} 
			else 
			if (epipoolNr!=null) 
			{
				if (debug) debug("doRAMFile(): epipoolnr("+epipoolNr+"): start"); 
				//time("doRAMFile(pool begin)", false);
				MMObjectBuilder bul=mmbase.getMMObject("pools");
				try 
				{
					// RICO aliased
			   		node=getAliasedNode(epipoolNr);
//			   		node=bul.getNode(epipoolNr);
			   		if (node!=null) 
					{
						numbers=getTableEpisodes2(node);

							 if( numbers == null 	 )	debug("doRAMFile(): ERROR: at epiPoolNr("+epipoolNr+"): numbers is null!");
						else if( numbers.size() == 0 ) 	debug("doRAMFile(): ERROR: at epiPoolNr("+epipoolNr+"): numbers is empty!");

						if (only!=null) 
						{
							// create a new vector from the list & nodes
							numbers=getSublist(numbers,only);

							 if( numbers == null 	 )	debug("doRAMFile(): ERROR: at epiPoolNr("+epipoolNr+"): at getSubList(): numbers is null!");
						else if( numbers.size() == 0 ) 	debug("doRAMFile(): ERROR: at epiPoolNr("+epipoolNr+"): at getSubList(): numbers is empty!");
						}


						//time("doRAMFile(age begin)", false);	
						// is the age set ifso lets ignore all the ones to old
						if (age!=null) 
						{
							try 
							{
								int agenr=Integer.parseInt(age);
								if (debug) debug("doRamFile: Filtering numbers("+numbers.size()+") on age("+age+")");
								numbers=filterAge(numbers,agenr);

							 		 if( numbers == null 	 )	debug("doRAMFile(): at epiPoolNr("+epipoolNr+"): at getAge("+age+"): ERROR: numbers is null!");
								else if( numbers.size() == 0 ) 	debug("doRAMFile(): at epiPoolNr("+epipoolNr+"): at getAge("+age+"): ERROR: numbers is empty!");

								if (debug) debug("doRamFile: Filtering numbers("+numbers.size()+") done.");
							} catch (Exception e) {
							}
						}
			   		}
					//time("doRAMFile(pool end)", false);
				} 
				catch(Exception e) 
				{
					debug("servdb>no epipool defined");
				}

				if (debug) debug("doRAMFile(): end epipool");
			} 
			else 
			if (program!=null) 
			{
				//time("doRAMFile(playlist begin)", false);
				if (debug) debug("doRamFile(): music: Getting PlaylistMusic");
				if (program.indexOf(',')==-1) 
				{
					//time("doRAMFile(begin music", false);
					Vector tr=music.getAreaVector(program,"cdtracks");
					//time("doRAMFile(begin tablelist)", false);

					if (tr != null )
					{
						numbers=getTableListVector( tr,  playRandom, maxNumberOfTracks, age);

							 if( numbers == null 	 )	debug("doRAMFile(): at program("+program+"): ERROR: numbers is null!");
						else if( numbers.size() == 0 ) 	debug("doRAMFile(): at program("+program+"): ERROR: numbers is empty!");
					}
					else
						debug("doRAMFile(): music: ERROR: No programs("+program+") in music found!");
				} 
				else 
				{
					if (debug) debug("doRamFile(): music"); 
					Vector programs=string2vector(program);	
					if ( programs != null )
					{
						Vector tr=music.getAreaVector(programs,"cdtracks");
						if (tr != null) 
						{
							if (tr.size() > 0)
							{
								numbers=getTableListVector( tr, playRandom, maxNumberOfTracks, age);

							 		 if( numbers == null 	 )	debug("doRAMFile(): at programs("+programs+"): ERROR: numbers is null!");
								else if( numbers.size() == 0 ) 	debug("doRAMFile(): at programs("+programs+"): ERROR: numbers is empty!");

						
								if (numbers == null)
									debug("doRAMFile(): programs: ERROR: Found for program("+program+"), area("+makeSet(tr, "", "")+", size("+tr.size()+"))  no items!");
							}
							else
								debug("doRAMFile(): programs: ERROR: No music("+programs+") found! (empty)");
						}
						else
							debug("doRAMFile(): programs: ERROR: No music("+programs+") found! (null)");
					}
					else
						debug("doRAMFile(): programs: ERROR: No programs("+program+") found!");
				}
			}
			else
			if (epiAudioparts != null )
			{
				if (debug) debug("doRAMFile(): episode detected");
				try
				{
					numbers = episode2rawaudio(  Integer.parseInt(epiAudioparts) );

							 if( numbers == null 	 )	debug("doRAMFile(): ERROR: at epiAudioparts("+epiAudioparts+"): numbers is null!");
						else if( numbers.size() == 0 ) 	debug("doRAMFile(): ERROR: at epiAudioparts("+epiAudioparts+"): numbers is empty!");

					if (debug)
						for (Enumeration e = numbers.elements(); e.hasMoreElements(); )
							debug("doRAMFile(): Episodes: element: "+ ((PlaylistItem)e.nextElement()).toString());
				}
				catch( NumberFormatException e )
				{
					debug("epiAudioparts("+epiAudioparts+"): ERROR: While converting to integer: " + e.toString());
				}
			}
			else if (list!=null) 
			{
				if (debug) debug("doRAMFile: List begin");
				numbers=getTableList(list, playRandom, maxNumberOfTracks, age);

				 	 if( numbers == null 	 )	debug("doRAMFile(): ERROR: at list("+list+"): numbers is null!");
				else if( numbers.size() == 0 ) 	debug("doRAMFile(): ERROR: at list("+list+"): numbers is empty!");

				if (debug) debug("doRAMFile: List end("+numbers.size()+")");
			}

            // Jingle pool
			if (PJingle!=null) {
				try {
					// RICO aliased
				   node=getAliasedNode(PJingle);
					if (debug) debug("PJingle node "+node);
				   if (node!=null) {
						PJingles=getTablePools(node,"audioparts");
						if (debug) debug("PJingles Vector "+PJingles);
				   }
				} catch(Exception e) {
					debug("servdb>no pool defined");
				}
			}


			if (debug) debug("doRAMFile: possible tracks("+numbers.size()+")");
		}

		//time("getRamFile(): pooltime", false);

		// now that we got the full list do we need it in a different
		// order 
		if (method.equals("plain")) {
		   // its oke do noting
		} else 
		if (method.equals("score")) {
		   // create a score of these nodes
			if (debug) debug("doRAMFile(): sortOnScore() - start");
		    numbers=SortedVector.SortVector(numbers,new PlaylistScoreCompare());	

							 if( numbers == null 	 )	debug("doRAMFile(): at score(): ERROR: numbers is null!");
						else if( numbers.size() == 0 ) 	debug("doRAMFile(): at score(): ERROR: numbers is empty!");

			if (debug) debug("doRAMFile(): sortOnScore() - stop");
		}

		if (numbers == null)
			debug("doRamFile: ERROR: Before adding jingle, no numbers in list !!!");

		// Adding Jingles

		int iSpeed, iChan;
		try
		{
			if (debug) debug("getRAMFile: Checking user's speed("+speed+") and channel("+channels+")");
			iSpeed 	= Integer.parseInt( speed );
			iChan	= Integer.parseInt( channels );
			if (debug) debug("speed("+iSpeed+"), channels("+iChan+")");

			if (iSpeed < minRAspeed) 	{ iSpeed = minRAspeed; 		debug("getRAMFile: ERROR: speed("+iSpeed+") is lower than min("+minRAspeed+")!"); }
			if (iSpeed > maxRAspeed) 	{ iSpeed = maxRAspeed; 		debug("getRAMFile: ERROR: speed("+iSpeed+") is higher than max("+maxRAspeed+")!"); }
			if (iChan  < minRAchannel) 	{ iChan  = minRAchannel; 	debug("getRAMFile: ERROR: channel("+iChan+") is lower than min("+minRAchannel+")!"); }
			if (iChan  > maxRAchannel) 	{ iChan  = maxRAchannel; 	debug("getRAMFile: ERROR: channel("+iChan+") is higher than max("+maxRAchannel+")"); }

		}
		catch( NumberFormatException e)
		{
			e.printStackTrace();

			// if error, assume that user has minimal equipment
			iSpeed 	= minRAspeed;
			iChan	= minRAchannel;
		}


		// got the nodes
		String result="";
		speed=speed.substring(0,2);
		StringBuffer buffer = new StringBuffer();

		// Build track Vector;
		if (debug) debug("getUrls: number of tracks("+numbers.size()+")");
		Vector urls=getUrls( numbers, iSpeed, iChan, info, fmachine, maxNumberOfTracks, playRandom, playShuffled, isInternal);
	
		// Add jingles;
		if(PJingles!=null && PJingles.size()>0) {
			SPJingles=getUrls( PJingles, iSpeed, iChan, info, fmachine, maxNumberOfTracks, playRandom, playShuffled, isInternal);

			int jingleWidth=jinglefreq; // Spreiding van de jingles.
			int numberOfJinglesToAdd = (int)Math.floor(numbers.size()/jingleWidth)-1;
			String str;
			int index=0,nr;

			while (SPJingles.size()<numberOfJinglesToAdd) {
				SPJingles.addElement(SPJingles.elementAt(index++));
			}
			String pl;
			int insertidx;
			for(index=0; index<numberOfJinglesToAdd; index++) {
				pl=(String)SPJingles.elementAt(index);
				insertidx=index*(jingleWidth+1)+jingleWidth;
				if (insertidx>0 && insertidx<urls.size()) urls.insertElementAt(pl,insertidx);
			}
		}

		// Needs start/stop code
		if(beginJingle!=null) {
			node=getAliasedNode(beginJingle);
			if (node!=null) {
				int id=node.getIntValue("number");
//				buffer.append("pnm://"+fmachine+"/"+id+"/"+speed+"_"+channels+".ra\n");
				buffer.append(getBestUrl( getBestSpeed( getPlaylistItems(node), iSpeed, iChan ) , info, fmachine, iSpeed, iChan, isInternal ));
			}
		}

		// Convert Vector of tracks/jingles to string
		String line;
		for (Enumeration e = urls.elements(); e.hasMoreElements(); )
		{
			line = (String) e.nextElement();
			buffer.append( line );
			if (debug) debug( line.substring( 0, (line.length()-1) )); // show entry without return
		}

		// Needs start/stop code
		if (endingJingle!=null) {
			node=getAliasedNode(endingJingle);
			if (node!=null) {
				int id=node.getIntValue("number");
//				buffer.append("pnm://"+fmachine+"/"+id+"/"+speed+"_"+channels+".ra\n");
				buffer.append(getBestUrl( getBestSpeed( getPlaylistItems(node), iSpeed, iChan ) , info, fmachine, iSpeed, iChan, isInternal ));
			}
		}

		result = buffer.toString();

		byte[] data=new byte[result.length()];
		result.getBytes(0,result.length(),data,0);	

		if (debug) debug("getRAMFile() : End");
//		debug("Playlists2 -> ");
//		debug(result);

		time("getRAMFile(end)", false);
		return(data);	
	}

	
	/**
	*
	*/
	private Vector getUrls(Vector numbers, int myspeed, int mychan, String info, String fmachine, int maxNumberOfTracks, boolean playRandom, boolean playShuffled, boolean isInternal)
	{
		if (debug) debug("getUrls("+numbers.size()+","+myspeed+","+mychan+","+info+","+fmachine+","+maxNumberOfTracks+","+playRandom+")");

		if (debug) 
			for (Enumeration e = numbers.elements(); e.hasMoreElements(); )
				debug( " - " + ((PlaylistItem) e.nextElement()).toString() );

		Vector 		 	result = new Vector(); 
		if (numbers != null && numbers.size() > 0)
		{
			//debug("getUrls("+numbers.size()+","+myspeed+","+mychan+"): start");
			int 		 	max=0;
			PlaylistItem 	old = null;
			PlaylistItem	item;
			Vector 			speeds  = new Vector();
			Vector			vResult = new Vector();

			/*	
			if (debug)
			{
				debug("---------------------------------------------------------------------");
				for (Enumeration e = numbers.elements(); e.hasMoreElements(); )
					debug( ((PlaylistItem) e.nextElement()).toString() );
				debug("---------------------------------------------------------------------");
			}
			*/
	
			for (Enumeration e=numbers.elements();(e.hasMoreElements());) 
			{
				item=(PlaylistItem)e.nextElement();
				if ((old != null) && (old.id != item.id))
				{
					// we found a new(other) track; calculate best match for current tracks, and add new track to current tracks
					if (speeds.size() > 0)
					{
						// debug("getUrls(): oldid="+old.id+", itemid="+item.id+", calc list with ("+speeds.size()+") items");
						vResult.addElement( getBestUrl( getBestSpeed( speeds, myspeed, mychan ) , info, fmachine, myspeed, mychan, isInternal ));
						max++;
					}
					else
						debug("getUrls(): ERROR: for item("+item.toString()+") no match coud be found( This is an impossible error! ).");
	
					speeds 	= new Vector();
					speeds.addElement( item );
					old		= item;
				}
				// add this track to current tracks (with diff speeds / channels)
				else
				{
					speeds.addElement( item );
					old = item;
				}
			}
	
			if (speeds.size() > 0)		// add the last track also
				vResult.addElement( getBestUrl( getBestSpeed( speeds, myspeed, mychan ) ,info, fmachine, myspeed, mychan, isInternal ));
	
			
			if (playRandom)
				RandomThings.shuffleVector( vResult );
		
			if (playShuffled)
				RandomThings.shuffleVector( vResult );
		   		//vResult = shuffleFreq(vResult,3);	

			result=vResult;
		}
		else
		{
			debug("getUrls(): empty playlist!");
		}
		return result;	
	}

	
	/**
	*
	*/
	private PlaylistItem getBestSpeedOld( Vector playlist, int myspeed, int mychannels )
	{
		// debug("getBestSpeed("+playlist.size()+","+myspeed+","+mychannels+"): start");
		// myspeed & mychannels reflect optimal choice
		// pi will always be equal or less 
		// if myspeed or mychannels does not reflect any choice, the most optimal will be choosen
 
		PlaylistItem pi = null, current = null, lowest = null;

		//debug("getBestSpeed("+((PlaylistItem)playlist.firstElement()).toString()+","+myspeed+","+mychannels+") : number of choices("+playlist.count()+").");


		for (Enumeration e = playlist.elements(); e.hasMoreElements(); )
		{
			current = (PlaylistItem) e.nextElement();
			
			if (lowest != null)
			{
				if (current.speed < lowest.speed && current.channels == lowest.channels)
					lowest = current;
			}
			else
				lowest = current;

			// if none choosen yet, is current item equal or less than my optimum ?
			// --------------------------------------------------------------------
			if ((pi==null) && (current.speed <= myspeed && current.channels == mychannels)) 
				pi = current;
			else
				// if one choosen already, is current better than choosen one and current equal or less than optimal chioce, choose it
				// -------------------------------------------------------------------------------------------------------------------
				if( (pi!=null) && (current.speed >= pi.speed && current.speed <= myspeed) && (current.channels == pi.channels))
					pi = current;
		}
		
		// if no match coud be found, take the lowest one	
		if (pi == null)
			pi = lowest;

		if (pi == null)
			debug("getBestSpeed("+playlist+","+myspeed+","+mychannels+"): ERROR: No match could be made !!!");

		if (debug) 
			debug("getBestSpeed("+playlist.size()+","+myspeed+","+mychannels+"): I found speed("+pi.speed+") and channels("+pi.channels+").");	

		return pi;
	}


	/**
	* Vector with numbers only diff in speed and format
	*/
	private PlaylistItem getBestSpeed( Vector playlist, int myspeed, int mychannels )
	{
		// debug("getBestSpeed("+playlist.size()+","+myspeed+","+mychannels+"): start");
		// myspeed & mychannels reflect optimal choice
		// pi will always be equal or less
		// if myspeed or mychannels does not reflect any choice, the most optimal will be choosen

		PlaylistItem result = null, current = null, lowest = null;

        	//debug("getBestSpeed("+((PlaylistItem)playlist.firstElement()).toString()+","+myspeed+","+mychannels+") : number of choices("+playlist.count()+").");
		for (Enumeration e = playlist.elements(); e.hasMoreElements(); ) {
			current = (PlaylistItem) e.nextElement();

			if (lowest != null) {
				if (current.speed < lowest.speed && current.channels == lowest.channels)
					lowest = current;
			} else lowest = current;
	
			// if none choosen yet, is current item equal or less than my optimum ?
			// --------------------------------------------------------------------
			if (result==null) {
	       	         	if( current.format == 6 ) {
					result = current;
				} else if( current.format == 2 ) { 
					if(current.speed <= myspeed && current.channels == mychannels) { 
						result = current; 
					} 
				}
			} else {
       	         		// if one choosen already, is current better than 
				// choosen one and current equal or less than optimal chioce, choose it
       		         	// --------------------------------------------------------------------
				if( result!=null) { 
					// always use 6 if available 
					// ------------ ------------ 
					if( result.format != 6 ) { 
						if( current.format == 6 ) { 
							result = current; 
						} else if( current.format == 2 ) {
       	                     				if (current.speed >= result.speed && current.speed <= myspeed && current.channels == result.channels) { 
								result = current; 
							}
       	                 			}
       	             			}
       	         		}
			}
		}  

		// if no match coud be found, take the lowest one
		if (result == null) result = lowest;
		if (result == null) debug("getBestSpeed("+playlist+","+myspeed+","+mychannels+"): ERROR: No match could be made !!!");
		if (debug) debug("getBestSpeed("+playlist.size()+","+myspeed+","+mychannels+"): I found speed("+result.speed+") and channels("+result.channels+").");
        	return result;
	}

	/**
	 * getBestUrl()
	 * This part will be rewritten to use classes in audiobranche in /util 
	 */
	private String getBestUrl( PlaylistItem item, String info, String fmachine, int uspeed, int uchan, boolean isInternal ) {
		int speed = item.speed;

		if (item.format==2) {
			if (speed>=minRAspeed && speed<=maxRAspeed) {
				// speed moet 16/32/40/80 zijn ipv 16000/32000/40000/80000
				speed = speed / 1000;
				//debug("converted speed("+item.speed+") to newspeed("+speed+")");
			} else {
				debug("getBestUrl("+item.id+","+item.speed+","+item.channels+"): WARNING: speed("+speed+") from user is higher/lower than min/max("+minRAspeed+"/"+maxRAspeed+"), using users speed("+uspeed+")");
				if (uspeed > 1000)
					uspeed = uspeed / 1000;
				speed = uspeed;
			}
		} else if (item.format==6) {
			speed=96;
		}
			
		

		if (item.channels == 0)
			item.channels = uchan;

		// figure out the correct url
		String result = null;	

		String startstop = "";

		if (item.starttime != null && !item.starttime.equals(""))
			startstop += "&start=\""+item.starttime+"\"";
		if (item.stoptime  != null && !item.stoptime.equals(""))	
			startstop += "&end=\""+item.stoptime+"\"";

		if (item.url!=null && !item.url.equals("")) {
			if(item.url.indexOf("streams")!=-1 && !isInternal) {
   	       			if( item.format == 2 ) {
					// debug("Mirror url from streams.omroep.nl");
					// Mirror to 7072 ipv 7070
					result="pnm://streams.omroep.nl/vpro/"+item.id+"/"+speed+"_"+item.channels+ 
						".ra?title=\""+item.title+" ( "+item.group+" )\"&author=\""+item.group+"\"&copyright=\""+info+ 
						"\"" + startstop + "\n";
   			    	}
   		        	else if( item.format == 6 ) {
   		            		result="rtsp://streams.omroep.nl/vpro/"+item.id+"/surestream.rm" + 
   		                    		"?title=\""+item.title+" ( "+item.group+" )\"&author=\""+item.group+"\"&copyright=\""+info+ 
   		                    		"\"" + startstop + "\n";
   		        	} else {
					debug("getBestUrl("+item.id+"): url="+item.url+" speed="+uspeed+" uchan="+uchan+", format="+item.format+": ERROR: format not valid!");
   			             result="pnm://streams.omroep.nl/vpro/"+item.id+"/"+speed+"_"+item.channels+ 
   		                     ".ra?title=\""+item.title+" ( "+item.group+" )\"&author=\""+item.group+"\"&copyright=\""+info+ 
   		                     "\"" + startstop + "\n";
				}
			} else {
				if( item.format == 2 ) {
   		            		result="pnm://"+fmachine+"/"+item.id+"/"+speed+"_"+item.channels+ 
   		                    	".ra?title=\""+item.title+" ( "+item.group+" )\"&author=\""+item.group+"\"&copyright=\""+info+ 
   		                    	"\""+ startstop +"\n";
				} else if( item.format == 6 ) {
					result="rtsp://"+fmachine+"/"+item.id+"/surestream.rm" + 
					"?title=\""+item.title+" ( "+item.group+" )\"&author=\""+item.group+"\"&copyright=\""+info+ 
					"\"" + startstop + "\n";
				} else {
					debug("getBestUrl("+item.id+"): url="+item.url+" speed="+uspeed+" uchan="+uchan+", format="+item.format+": ERROR: format not valid!");
   			             result="pnm://streams.omroep.nl/vpro/"+item.id+"/"+speed+"_"+item.channels+ 
   		                     ".ra?title=\""+item.title+" ( "+item.group+" )\"&author=\""+item.group+"\"&copyright=\""+info+ 
   		                     "\"" + startstop + "\n";
				}
			}
        	}
		else
			debug("getBestUrl("+item.id+"): url="+item.url+" speed="+uspeed+" uchan="+uchan+", format="+item.format+": ERROR: url not valid!");

		//temp fix daniel to solve server problems at streams.omroep.nl
/*
		debug("Checking url="+result);
		int checkpos=result.indexOf("streams.omroep.nl/vpro");
		if (checkpos!=-1) {
			// so it wants togo to the streams server remap it to local machine
			String result2=result.substring(0,checkpos);
			result2+="station.vpro.nl";
			result2+=result.substring(checkpos+22);
			debug("Check rewrite="+result2);
			return(result2);
		}
*/

		if( result == null )
			debug("getBestUrl("+item.id+"): url="+item.url+" speed="+uspeed+" uchan="+uchan+", format="+item.format+": ERROR: Result is null!");
		else
		if( result.equals("") )
			debug("getBestUrl("+item.id+"): url="+item.url+" speed="+uspeed+" uchan="+uchan+": ERROR: Result is empty!");

		return(result);
	}
	
	/**
	* Laat de nummers in een vector met breedste spreiding terugkomen
	* @param frequency frequentie 3 creeert als het ware 3 boxen en laat
	* de eerste box hiervan 3x horen, de 2e box 2 maal en de 3e eenmaal.
	* Dit algorithme werkt met een breedste spreiding!
	*/
	private Vector shuffleFreqq(Vector oldvec, int frequency) {

		// startstop b
		if (oldvec==null)
			return null;
		if (oldvec.size() == 0)
			return oldvec;
		// startstop e

		if(frequency<=1) return oldvec;
		Vector newvec = new Vector();

		oldvec=SortedVector.SortVector(oldvec,new PlaylistScoreCompare());
		int a, b=frequency, playlistsize=oldvec.size();
		for(a=1;a<=b;a++) {
			int begin = (a-1)*(int)Math.ceil(playlistsize/(double)b);
			int end = (a*(int)Math.ceil(playlistsize/(double)b))-1;
			int times=1; // Welke iteratie is het?
			int size=newvec.size();
			int sizeNewBox=(end-begin+1)*(b-a+1);
			int r=0; // doordat je 1 item insert moet je teller telkens 1 verder staan.

			for (int rep=a; rep<=b;rep++) {
				for (int index=begin; index<=end; index++) {
					try {
                        			newvec.insertElementAt(oldvec.elementAt(index),(int)Math.floor(times*size/sizeNewBox)+r);
						times++;
						r++;
					} catch (Exception e) {
                        			//debug("Playlists shuffleFreq -> "+e);
                        			// Trying to put some last elements into the vector
					}
                		}
            		}
        	}
        	return newvec;
    	}

	/**
	 * Dit is nog niet echt een mooie oplossing, maarja
	 * @param frequency frequentie 3 creeert als het ware 3 boxen en laat
	 * de eerste box hiervan 3x horen, de 2e box 2 maal en de 3e eenmaal. 
	 * Dit algorithme werkt dus niet met een breedste spreiding!
	 */
	private Vector shuffleFreq(Vector oldvec, int frequency) {
  		if(frequency<=1) return oldvec;
		Vector newvec = new Vector();
		oldvec=SortedVector.SortVector(oldvec,new PlaylistScoreCompare());	
    		int a, b=frequency, playlistsize=oldvec.size();
    		for(a=1;a<=b;a++) {
        		int begin = (a-1)*(int)Math.ceil(playlistsize/(double)b);
        		int end = (a*(int)Math.ceil(playlistsize/(double)b))-1;
        		for (int index=begin; index<=end; index++) {
            			for (int i=1;i<=(b-a+1);i++) {
                			try {
                    				newvec.addElement(oldvec.elementAt(index));
                			} catch (Exception e) {
                    				// Trying to put some last elements into the vector
                			}
            			}
        		}
    		}	
		//uit een paar testjes bleek dat 3x voldoende was om dezelfde opeenvolgende items te 'voorkomen'. 
     		RandomThings.shuffleVector(newvec);
     		RandomThings.shuffleVector(newvec);
     		RandomThings.shuffleVector(newvec);
    		return (newvec);
	}

	/**
	*
	*/ 
	private Vector getPlaylistItems(MMObjectNode node) {
		Vector res=new Vector();
		String tmp,startT=null,stopT=null;
		MMObjectNode t;

		
		tmp=null;
		t=node.getProperty("starttime");
		if (t!=null) tmp=t.getStringValue("value");
		if (tmp!=null && tmp.length()>0) startT=tmp;
		tmp=null;
		t=node.getProperty("stoptime");
		if (t!=null) tmp=t.getStringValue("value");
		if (tmp!=null && tmp.length()>0) stopT=tmp;
		if (debug) debug(" "+startT+" - "+stopT);

	        MultiConnection con = mmbase.getConnection();
		try {
			String query="select id,speed,channels,url,status,format from vpro4_rawaudios where id="+node.getIntValue("number")+" and status=3 order by id;";
			Statement       stmt        = con.createStatement();
			ResultSet       rs          = stmt.executeQuery( query );
			while( rs.next() ) {
				PlaylistItem newi=new PlaylistItem();
				newi.id			=rs.getInt(1);
				newi.relid		=9467; // hacked on vpro
				newi.score		=0;
				newi.oldpos		=0;
				newi.startdate		=0;
				newi.title		=node.getStringValue("title");
				newi.group		="vpro";
				newi.groupId		=9468;
				newi.speed		=rs.getInt(2);
				newi.channels		=rs.getInt(3);
				newi.url		=rs.getString(4);
				newi.format		=rs.getInt(6);
				newi.starttime		= startT;
				newi.stoptime		= stopT;
				res.addElement(newi);
			}
			stmt.close();
			con.close();	
		} catch(Exception f) {
			debug("ERROR");
			f.printStackTrace();
		}
		if (debug) debug(" "+res);

		return(res);
	}

	/**
	* creates a Vector of playlistitems from a MMObjectNode, the number
	* value will be used to get the pool and the audio's in it.
	* this needs a speedup from new database routines
	*/
	public Vector getTable(MMObjectNode node) {

		// is list in cache ?
		Vector result=(Vector)listCache.get(node.getIntegerValue("number"));
		if (result!=null) return(result);
	
		// get the pool number
		int nodeNr=node.getIntValue("number");	
	
		// create return vector
		result=new Vector();

		// get the pools builder 
		MMObjectBuilder bul=mmbase.getMMObject("pools");

		// get all the relations this pool has
	        Enumeration w=node.getRelations();

		// loop these relations to filter any audioparts from it.
		while (w.hasMoreElements()) {
			MMObjectNode relnode=(MMObjectNode)w.nextElement();
			//debug("Rel="+relnode);
			int otherNr=relnode.getIntValue("sNumber");
			if (nodeNr==otherNr) {
				otherNr=relnode.getIntValue("dNumber");
			}
			// needed in the future ? if (othernode!=null && othernode.getIntValue("otype")==1147) {
			MMObjectNode othernode=bul.getNode(otherNr);
			if (othernode!=null) {
				relnode=mmbase.castNode(relnode);
				othernode=mmbase.castNode(othernode);
				PlaylistItem newi=new PlaylistItem();
				newi.id=othernode.getIntValue("number");
				newi.relid=relnode.getIntValue("number");
				newi.score=relnode.getIntValue("score");
				newi.oldpos=relnode.getIntValue("oldpos");
				newi.startdate=relnode.getIntValue("startdate");
				newi.title=othernode.getStringValue("title");

				//debug("OTHER="+newi);
				result.addElement(newi);
			}
		}
		// return the Vector with resulting playlist items
		listCache.put(node.getIntegerValue("number"),result);
		return(result);
	}


	/**
	 * Voor speed en channel is tabel rawaudios nodig.
	 * De normale multirelation is hiervoor niet toereikend, want deze selecteerd
	 * automatisch alle tabellen op number, rawaudios moet echter op 'id' geselecteerd worden.
	 * Vandaar deze extra methode.
	 */
	public Vector getTableEpisodes2( MMObjectNode node )
	{
		//debug("getTableEpisodes2("+node+"): start");	

		int pnumber=node.getIntValue("number");
		// create return vector
		Vector result=new Vector();
	
		MultiConnection con=mmbase.getConnection();

		try {
			Statement stmt=con.createStatement();
			// ---------------------------------------------------------------------------------------------------------------
			String query =  
				"select c.number, e.number, e.title, g.speed, g.channels, g.url, h.key, h.value, i.key, i.value, g.format, g.status " + 
				"from   vpro4_pools a, vpro4_insrel b, vpro4_episodes c, vpro4_insrel d, " + 
						"vpro4_audioparts e, vpro4_insrel f, vpro4_rawaudios g, vpro4_properties h, vpro4_properties i "+
				"where  (a.number="+pnumber+") AND a.number=b.dnumber AND c.number=b.snumber AND c.number=d.snumber AND e.number=d.dnumber AND " + 
				   		"e.number=f.dnumber AND g.id=e.number AND (g.format=2 OR g.format=6) and status=3 AND e.number=h.parent AND e.number=i.parent AND " + 
						"h.key=\"starttime\" AND i.key=\"stoptime\" " +
				"union " + 
				"select c.number, e.number, e.title, g.speed, g.channels, g.url, '', '', '', '', g.format, g.status " + 
				"from   vpro4_pools a, vpro4_insrel b, vpro4_episodes c, vpro4_insrel d, " +
						"vpro4_audioparts e, vpro4_insrel f, vpro4_rawaudios g " + 
				"where  (a.number="+pnumber+") AND a.number=b.dnumber AND c.number=b.snumber AND c.number=d.snumber AND e.number=d.dnumber AND " +
						"e.number=f.dnumber AND (NOT EXISTS (select * from vpro4_properties where parent=e.number)) " + 
						"AND g.id=e.number AND (g.format=2 OR g.format=6) AND status=3 " +
				"ORDER BY 1 ASC, 3 ASC, 4 ASC, 5 ASC";

// SELECT  c.number, e.number, e.title, g.speed, g.channels, g.url, h.key, h.value
// FROM    vpro4_pools a, vpro4_insrel b, vpro4_episodes c, vpro4_insrel d, vpro4_audioparts e,
//        vpro4_insrel f, vpro4_rawaudios g,vpro4_properties h
// WHERE  (a.number=1633158) AND a.number=b.dnumber AND c.number=b.snumber AND c.number=d.snumber AND e.number=d.dnumber
//       AND e.number=f.dnumber AND g.id=e.number AND g.format=2 AND e.number=h.parent
// UNION
// SELECT  c.number, e.number, e.title, g.speed, g.channels, g.url, '', ''
// FROM    vpro4_pools a, vpro4_insrel b, vpro4_episodes c, vpro4_insrel d, vpro4_audioparts e, vpro4_insrel f, vpro4_rawaudios g
// WHERE  (a.number=1633158) AND a.number=b.dnumber AND c.number=b.snumber AND c.number=d.snumber AND e.number=d.dnumber
//         AND e.number=f.dnumber AND
//             (NOT EXISTS (select * from vpro4_properties where parent=e.number)) AND
//         g.id=e.number AND g.format=2 ORDER BY 1 ASC, 3 ASC, 4 ASC, 5 ASC

			// ---------------------------------------------------------------------------------------------------------------
			// query produces : 
			// 
			// 1.) number    1267054
			// 2.) number    1296999
			// 3.) title     Dubbel Check 15 Dec 1998 uur 1
			// 4.) speed     16000
			// 5.) channels  1
			// 6.) url       http://station.vpro.nl/audio/ra/1296999/16_1.ra

			//debug( "getTableEpisodes2("+node+"): query(" + query +")");
			ResultSet rs=stmt.executeQuery( query );
			PlaylistItem newi;
			String	sStartkey = null,   sStopkey = null;
			String	sStartvalue = null, sStopvalue = null;

			while(rs.next()) 
			{
				newi=new PlaylistItem();

				newi.id			= rs.getInt(2);
				newi.relid		= 9467; // hacked on vpro
				newi.score		= 0;
				newi.oldpos		= 0;
				newi.startdate	= 0;
				newi.title		=  rs.getString(3);
				newi.group		= "vpro";
				newi.groupId	= 9468;
				
				newi.speed 		= rs.getInt(4);
				newi.channels 	= rs.getInt(5);
				newi.url		= rs.getString(6);
				newi.starttime  = "";
				newi.stoptime	= "";
				newi.format		= rs.getInt(11);


				sStartkey = rs.getString( 7 );
				if (sStartkey!=null && sStartkey.equals("starttime"))
				{
					sStartvalue = rs.getString(8);
					if( sStartvalue != null && !sStartvalue.equals(""))
					{
						newi.starttime = sStartvalue;
					}
				}

				sStopkey = rs.getString( 9 );
				if( sStopkey != null && sStopkey.equals("stoptime"))
				{
					sStopvalue = rs.getString( 10 );
					if( sStopvalue!=null && !sStopvalue.equals(""))
					{
						newi.stoptime = sStopvalue;
					}
				}
	
				if (debug) debug("getTableEpisodes2("+node+"): Adding("+newi.toString()+"), start("+sStartkey+","+sStartvalue+"), stop("+sStopkey+","+sStopvalue+")");	
				result.addElement(newi);
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//debug("getTableEpisodes("+node+"): end, found " + result.size() + " options.");	
		return(result);
	}


	/**
	* creates a Vector of playlistitems from a MMObjectNode, the number
	* value will be used to get the pool and the audio's in it.
	* this needs a speedup from new database routines
	*
	* Because speed ,channels, start- and endtimes and url have to be availabe,.
	* this method is rewritten into 'getTableEpisodes2'.
	*/
	public Vector getTableEpisodes(MMObjectNode node) {

// select  c.number, e.number, e.title, g.speed, g.channels
// from    vpro4_pools a, vpro4_insrel b, vpro4_episodes c, vpro4_insrel d, vpro4_audioparts e, vpro4_insrel f, vpro4_rawaudios g
// where   a.number=b.dnumber AND c.number=b.snumber AND c.number=d.snumber AND e.number=d.dnumber AND e.number=f.dnumber AND g.id=e.number
// ORDER BY c.number ASC, e.title ASC, g.speed ASC, g.channels ASC

		Vector result=new Vector();
		MultiRelations multirelations=(MultiRelations)mmbase.getMMObject("multirelations");		
		int pnumber=node.getIntValue("number");
		Vector tables=new Vector();
		tables.addElement("pools");
		tables.addElement("episodes");
		tables.addElement("audioparts");
		tables.addElement("rawaudios");
		
		Vector fields=new Vector();
		fields.addElement("episodes.number");
		fields.addElement("audioparts.number");
		fields.addElement("audioparts.title");
		fields.addElement("rawaudios.id");
		fields.addElement("rawaudios.speed");
		fields.addElement("rawaudios.channels");
		fields.addElement("rawaudios.format");

		Vector ordervec=new Vector();
		ordervec.addElement("episodes.number");
		ordervec.addElement("audioparts.title");
		ordervec.addElement("rawaudios.speed");
		ordervec.addElement("rawaudios.channels");

		Vector dirvec=new Vector();
		dirvec.addElement("UP");

		//debug("BAH");
		Vector vec=multirelations.searchMultiLevelVector(pnumber,fields,"NO",tables,"WHERE status=3",ordervec,dirvec);
		//debug("BAH2="+vec);
		Enumeration e=vec.elements();
		while (e.hasMoreElements()) {
				MMObjectNode pnode=(MMObjectNode)e.nextElement();
				int epid=pnode.getIntValue("episodes.number");
				int apid=pnode.getIntValue("audioparts.number");

				int ispeed = pnode.getIntValue("rawaudios.speed");
				int ichan  = pnode.getIntValue("rawaudios.channels");
				int format = pnode.getIntValue("rawaudios.format");

				String aptitle=pnode.getStringValue("audioparts.title");
				PlaylistItem newi=new PlaylistItem();
				try {
				newi.id=apid;
				newi.relid=9467; // hacked on vpro
				newi.score=0;
				newi.oldpos=0;
				newi.startdate=0;
				newi.title=aptitle;
				newi.group="vpro";
				newi.groupId=9468;

				// speed and channel has to be set
				newi.speed = ispeed;
				newi.channels = ichan;

				newi.format = format;

				result.addElement(newi);
				} catch(Exception f) {
					debug("ERROR");
					f.printStackTrace();
				}
		}	
		//debug("BAH="+result);
		return(result);
	}


	/**
	* creates a Vector of playlistitems from a MMObjectNode, the number
	* value will be used to get the pool and the audio's in it.
	* this needs a speedup from new database routines
	*/
	public Vector getTable2(MMObjectNode node,String so) {

		int nodeNr=node.getIntValue("number");	
		if (debug) debug("getTable2("+nodeNr+","+so+"): start (-- This method has to be rewritten for start/stop --)");

		// is list in cache ?
		Vector result=(Vector)listCache.get(node.getIntegerValue("number"));
		if (result!=null) return(result);
	
		// create return vector
		result=new Vector();
	
		MultiConnection con=mmbase.getConnection();
		Statement stmt;
		ResultSet rs;

		PlaylistItem newi;

		// for cdtracks
		try {

			// -------------------------------
			// so = cdtracks, number = 1884459
			// -------------------------------

			stmt=con.createStatement();

			// -----------
			// old routine 
			// -----------
			// rs=stmt.executeQuery("select 	b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number " + 
			//					 	"from 		vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e " + 
			//					 	"where 		a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.snumber AND b.number=e.dnumber;");
			//
			//								1			2			3			4			5				6			7		8			9				10					11
			rs=stmt.executeQuery("select 	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number, 	audio.speed, 	audio.channels, 	audio.url, audio.format " + 
								 "from 		vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e, vpro4_rawaudios audio " + 
								 "where 	a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.snumber AND b.number=e.dnumber AND audio.id=b.number AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			=rs.getInt(1);
				newi.relid		=rs.getInt(2);
				newi.score		=rs.getInt(3);
				newi.oldpos		=rs.getInt(4);
				newi.startdate	=rs.getInt(5);
				newi.title		=rs.getString(6);
				newi.group		=rs.getString(7);
				newi.groupId	=rs.getInt(8);
				newi.speed		=rs.getInt(9);
				newi.channels	=rs.getInt(10);
				newi.url		=rs.getString(11);
				newi.format		=rs.getInt(12);
				
				newi.starttime	= null;
				newi.stoptime	= null;

				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			debug("getTable2(): 1: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}

		try {
			con=mmbase.getConnection();
			stmt=con.createStatement();
	
			// ---------	
			// old query 
			// ---------	
			//	rs=stmt.executeQuery("	select 	b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number " + 
			//						 "	from 	vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e " + 
			//						 "	where 	a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.dnumber AND b.number=e.snumber;");
			//
			//									1			2			3			4			5				6			7		8			9				10				11
			rs=stmt.executeQuery(	"select 	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number,	audio.speed,	audio.channels,	audio.url, audio.format " + 
									"from 		vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e, vpro4_rawaudios audio " + 
									"where 		a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.dnumber AND b.number=e.snumber AND audio.id=b.number AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);

				//startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("getTable2(): 2: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}


		try {
			con=mmbase.getConnection();
			stmt=con.createStatement();

			// ---------
			// old query
			// ---------
			// rs=stmt.executeQuery("select b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number from vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e where a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.snumber AND b.number=e.dnumber;");
			//
			//									1			2			3			4			5				6			7		8			9				10				11	
			rs=stmt.executeQuery(	"select 	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number,	audio.speed,	audio.channels,	audio.url, audio.format " + 
									"from 		vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e, vpro4_rawaudios audio " +
									"where 		a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.snumber AND b.number=e.dnumber AND audio.id=b.number AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);
				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("getTable2(): 3: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}

		try {
			con=mmbase.getConnection();
			stmt=con.createStatement();
		
			// ---------	
			// old query
			// ---------
			//rs=stmt.executeQuery("select b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number from vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e where a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.dnumber AND b.number=e.snumber;");

			//									1			2			3			4			5				6			7			8			9				10				11
			rs=stmt.executeQuery(	"select 	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,		c.number,	audio.speed,	audio.channels,	audio.url, audio.format " + 
									"from 		vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,vpro4_funrel e, vpro4_rawaudios audio " + 
									"where 		a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.dnumber AND b.number=e.snumber AND audio.id=b.number AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);
				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("getTable2(): 4: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}


		// for audioparts
		so="audioparts";
		try {
			stmt=con.createStatement();
			
			// ---------
			// old query
			// ---------
			// rs=stmt.executeQuery("select b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number from vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e where a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.snumber AND b.number=e.dnumber;");

			//								1			2			3			4			5				6			7		8			9				10				11
			rs=stmt.executeQuery(	"select	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number,	audio.speed,	audio.channels,	audio.url, audio.format " +
									"from 	vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e, vpro4_rawaudios audio " + 
									"where 	a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.snumber AND b.number=e.dnumber AND b.number=audio.id AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);
				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			debug("getTable2(): 5: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}

		try {
			con=mmbase.getConnection();
			stmt=con.createStatement();

			// ---------
			// old query 
			// ---------
			// rs=stmt.executeQuery("select b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number from vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e where a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.dnumber AND b.number=e.snumber;");

			//								1			2			3			4			5				6			7		8			9				10				11
			rs=stmt.executeQuery(	"select	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number, 	audio.speed,	audio.channels,	audio.url, audio.format " +
									"from 	vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e, vpro4_rawaudios audio " + 
									"where 	a.number="+nodeNr+" AND a.number=d.snumber AND b.number=d.dnumber AND c.number=e.dnumber AND b.number=e.snumber AND audio.id=b.number AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);
				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("getTable2(): 6: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}


		try {
			con=mmbase.getConnection();
			stmt=con.createStatement();
			
			// ---------
			// old query
			// ---------
			// rs=stmt.executeQuery("select b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number from vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e where a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.snumber AND b.number=e.dNumber;");

			// 								1			2			3			4			5				6			7		8			9				10				11
			rs=stmt.executeQuery(	"select	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number, 	audio.speed,	audio.channels,	audio.url, audio.format " + 
									"from 	vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e, vpro4_rawaudios audio " + 
									"where 	a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.snumber AND b.number=e.dNumber AND b.number=audio.id AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);
				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("getTable2(): 7: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}

		try {
			con=mmbase.getConnection();
			stmt=con.createStatement();

			// ---------
			// old query
			// ---------
			// rs=stmt.executeQuery("select b.number,d.number,d.score,d.oldpos,d.startdate,b.title,c.name,c.number from vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e where a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.dnumber AND b.number=e.snumber;");

			//								1			2			3			4			5				6			7		8			9				10				11
			rs=stmt.executeQuery(	"select	b.number,	d.number,	d.score,	d.oldpos,	d.startdate,	b.title,	c.name,	c.number, 	audio.speed,	audio.channels,	audio.url, audio.format " +
									"from 	vpro4_pools a,vpro4_"+so+" b,vpro4_groups c,vpro4_hitlistrel d,only(vpro4_insrel) e, vpro4_rawaudios audio " + 
									"where 	a.number="+nodeNr+" AND a.number=d.dnumber AND b.number=d.snumber AND c.number=e.dnumber AND b.number=e.snumber AND audio.id=b.number AND (audio.format=2 OR audio.format=6) and audio.status=3;");
			while(rs.next()) {
				newi=new PlaylistItem();
				newi.id			= rs.getInt(1);
				newi.relid		= rs.getInt(2);
				newi.score		= rs.getInt(3);
				newi.oldpos		= rs.getInt(4);
				newi.startdate	= rs.getInt(5);
				newi.title		= rs.getString(6);
				newi.group		= rs.getString(7);
				newi.groupId	= rs.getInt(8);
				newi.speed		= rs.getInt(9);
				newi.channels	= rs.getInt(10);
				newi.url		= rs.getString(11);
				newi.format		= rs.getInt(12);
				// startstop
				result.addElement(newi);
			}	
			stmt.close();
			con.close();
		} catch (SQLException e) {
			debug("getTable2(): 8: ERROR: " + e.toString()); 
			//e.printStackTrace();
		}

		result = SortedVector.SortVector( result );

	/* boe

		if (debug)
			for (Enumeration e = result.elements(); e.hasMoreElements(); )		
				debug( " - " + ((PlaylistItem) e.nextElement()).toString() );
	boe */

		listCache.put(node.getIntegerValue("number"),result);
		if (debug) debug("getTable2("+nodeNr+"): end (-- This method has to be rewritten for start/stop --)");
		return(result);
	}


	public Vector getTableListVector( Vector v, boolean playRandom, int maxNumberOfTracks, String age )
	{
		return getTableList( makeSet(v, "", ""), playRandom, maxNumberOfTracks, age);
	}


	/**
	* creates a Vector of playlistitems from a MMObjectNode, the number
	* value will be used to get the pool and the audio's in it.
	* this needs a speedup from new database routines
	*/
	public Vector getTableList(String list, boolean playRandom, int maxNumberOfTracks, String age)
	{
		Vector result = null;
		if (list == null )
		{
			debug("getTableList("+list+"): WARNING: List is null!");
		}
		else
		{
			if (list.equals(""))
			{
				debug("getTableList("+list+"): WARNING: List is empty!");	
			}
			else
			{
				if (age != null && !age.equals(""))
				{
					try
					{
						int ageNr = Integer.parseInt( age );	
						//debug("getTableList: Filerting on age("+ageNr+")");
						int max=((DayMarkers)mmbase.getMMObject("daymarks")).getDayCountAge(ageNr);
						StringTokenizer tok = new StringTokenizer( list, ",");
						StringBuffer	buf = new StringBuffer();

						String 	tmp = null;
						int		tmpnr;

						int i = 0;
						while(tok.hasMoreElements())	
						{
							tmp=(String)tok.nextElement();
							try 
							{
								tmpnr=Integer.parseInt(tmp);
							if (tmpnr>max) 
									buf.append( "" + tmpnr + ",");
							} 
							catch(NumberFormatException e) 
							{
								debug("getTableList(): While filtering on age("+max+"): ERROR: Not a number("+tmp+")! : " + e.toString());
							}
						}
						list = buf.toString().trim();

						if( list.endsWith(","))
							list = list.substring( 0, list.length() -1 ).trim();
					}
					catch( NumberFormatException e )
					{
						debug("getTableList(): While filtering on age("+age+"): its not a number! " + e.toString());
					}
				}

				//time("getTableList(): countList begin", false );
				int count = countList( list );
				//debug("getTableList: total number of tracks in list : " + count);
				//time("getTableList(): countList end, begin getAll", false );
				if (count > 0)
				{
					list = getTableItems( list, count, playRandom, maxNumberOfTracks );
					//time("getTableList(): getall end, begin getTracks", false );
					if (count > maxNumberOfTracks)
						count = maxNumberOfTracks;
				}

				if (list != null && !list.equals(""))
					result = getTableListCdTracks( list, count );
				else
					debug("getTableList("+list+"): WARNING: No numbers to insert!");
				
				//time("getTableList(): getTracks end, ", false );
			}
		}
		return result;
	}

	private int countList( String list )
	{
		StringTokenizer tok = new StringTokenizer( list, "," );
		int result = 0;
		while( tok.hasMoreTokens() )
		{
			tok.nextToken();
			result ++;
		}
		return result;
	}

	private String getTableItems( String list, int count, boolean playRandom, int maxNumberOfTracks )
	{
		//debug("getTableItems("+list+","+count+","+playRandom+","+maxNumberOfTracks+"):start");
		int i = 1;
		String 			result = "";
		String 			number;
		StringBuffer 	s 	= new StringBuffer();
		StringTokenizer tok = new StringTokenizer( list, "," );
		Vector 			v 	= new Vector( count );

        while( tok.hasMoreTokens() )
       	   	v.addElement( (String)tok.nextToken() );

		//debug("getTableItems: vector size("+v.size()+")");

		if (playRandom)
			RandomThings.shuffleVector( v );
	
		for (Enumeration e=v.elements(); e.hasMoreElements() && i<maxNumberOfTracks ; i++)
		{
			number = (String) e.nextElement();

			if (i==1)
				s.append( number );
			else
				s.append( "," + number );
		}

		// debug("getTableItems: returning items("+i+", ["+s.toString()+"])");

        return s.toString();
	} 

	private Vector getTableListCdTracks(String list, int size) 
	{
		// debug("getTableListCdTracks("+list+","+size+"): start");

		Vector 			result	= new Vector(size);
		if( list!= null && !list.equals(""))
		{

			// debug("getTableListCdTracks("+list+"): start");	
			// create return vector

			MultiConnection con		= mmbase.getConnection();
			Statement		stmt;
			ResultSet		rs;
			PlaylistItem	newi;
	
			String			sStartkey;
			String			sStopkey;
	
			String			sStarttime = null;
			String			sStoptime  = null; 
	
			// for cdtracks
			try {
				stmt=con.createStatement();
	
				// new query, with startstop-key in property
	
				// ---------------------------------------------------------------------------------------------------------------
				String query=	"SELECT a.number, a.title, d.number, d.name, b.number, b.otype, b.owner, b.id, b.status, b.format, b.speed, b.channels, b.url, b.cpu, c.key, c.value, f.key, f.value " + 
								"FROM 	vpro4_groups d,vpro4_insrel e, vpro4_cdtracks a, vpro4_rawaudios b ,vpro4_properties c, vpro4_properties f " + 
								"WHERE 	d.number=e.snumber AND e.dnumber=a.number AND a.number=b.id AND " +
								"       a.number=c.parent AND a.number in ("+list+") AND (b.format=2 OR b.format=6) AND b.status=3 AND c.key=\"starttime\" AND f.key=\"stoptime\" " +
	
								"UNION " + 
	
								"SELECT a.number, a.title, d.number, d.name, b.number, b.otype, b.owner, b.id, b.status, b.format, b.speed, b.channels, b.url, b.cpu,'','','','' " + 
								"FROM 	vpro4_groups d,vpro4_insrel e ,vpro4_cdtracks a, vpro4_rawaudios b " + 
								"WHERE 	d.number=e.snumber AND e.dnumber=a.number AND a.number=b.id AND (b.format=2 OR b.format=6) and b.status=3 AND a.number in ("+list+") AND " + 
								"(NOT EXISTS (SELECT * FROM vpro4_properties WHERE parent=a.number)) " +
								"ORDER BY  1, 5, 8, 9, 10, 11, 12, 13 " ;
				// ---------------------------------------------------------------------------------------------------------------
				String newquery ="SELECT c.number, c.title, a.number, a.name " +
								"FROM 	vpro4_groups a, vpro4_insrel b, vpro4_cdtracks c " + 
								"WHERE 	a.number=b.snumber AND b.dnumber=c.number AND c.number in ("+list+") " + 
	
								"UNION " + 
	
								"SELECT c.number, c.title, a.number,a.name " + 
								"FROM 	vpro4_groups a, vpro4_insrel b, vpro4_cdtracks c " + 
								"WHERE 	a.number=b.dnumber AND b.snumber=c.number AND c.number in ("+list+")";
				// ---------------------------------------------------------------------------------------------------------------
				// query produces : 
				// 
				// 01. name      Beck
				// 02. number    2230
				// 03. title     Deadweight
				// 04. number    2232
				// 05. otype     1156
				// 06. owner     system
				// 07. id        2230
				// 08. status    3
				// 09. format    2
				// 10. speed     16000
				// 11. channels  1
				// 12. url       F=/2230/16_1.ra H1=station.vpro.nl H2=streams.omroep.nl/vpro
				// 13. cpu       audio
				// 14. key		 starttime
				// 15. value
				// 16. key		 stoptime
				// 17.
				// debug( "getListCdTrack("+list+"): query(" + query +")");
	
				//time("getCdTracks(begin query)", false);
				rs=stmt.executeQuery( query );
				//time("getCdTracks(end query)", false);
				
				while(rs.next()) 
				{
					newi=new PlaylistItem();
	
					int id			= rs.getInt(1); 		
					newi.id			= id; 					//debug("getListCdTrack("+list+"): id:"+newi.id);
					newi.relid		= -1;
					newi.score		= -1;
					newi.oldpos		= -1;
					newi.startdate	= -1;
					newi.title		= rs.getString(2); 		//debug("getListCdTrack("+list+"): title:"+newi.title);
					newi.group		= rs.getString(4); 		//debug("getListCdTrack("+list+"): group:"+newi.group);
					newi.groupId	= rs.getInt(3); 		//debug("getListCdTrack("+list+"): groupId:"+newi.groupId);
					newi.format		= rs.getInt(10);	
					newi.speed		= rs.getInt(11); 		//debug("getListCdTrack("+list+"): speed:" +newi.speed);
					newi.channels 	= rs.getInt(12);	 	//debug("getListCdTrack("+list+"): channels:" +newi.channels);
					newi.url		= rs.getString(13); 	//debug("getListCdTrack("+list+"): url:" + newi.url);
					newi.starttime  = "";
					newi.stoptime	= "";
					// ---------	
					// startstop
					// ---------
					// 15 - starttime, 16 - value, 17 - stoptime, 18 - value
	
					sStartkey = rs.getString( 15 ); //debug("getListCdTrack("+list+"): sStartStop:"+sStartstopKey);
					if (sStartkey != null && sStartkey.equals("starttime"))
					{
						sStarttime = rs.getString( 16 );
						if ( sStarttime!=null && !sStarttime.equals("") )
							newi.starttime = sStarttime;
					}
	
					sStopkey = rs.getString( 17 );
					if (sStopkey != null && sStopkey.equals("stoptime"))
					{
						sStoptime = rs.getString( 18 );
						if (sStoptime!=null && !sStoptime.equals(""))
							newi.stoptime = sStoptime;
					}
					result.addElement( newi );

					//if (debug) debug("getTabelIstCdTracks(): Entry: " + newi.toString() +", start("+sStartkey+","+sStarttime+"), stop("+sStopkey+","+sStoptime+")");
				}
				//time("getCdTracks(end method)", false);
				stmt.close();
				con.close();	
			} catch (SQLException e) {
				e.printStackTrace();
			}

			//debug("getTableListCdTracks("+result.size()+")");
		}
		return(result);
	}



	public Vector getTableListAudioParts(String list) {

		//debug("getTableListAudioParts("+list+"): start");
		Vector result=new Vector();

	/**	
		MultiConnection con=mmbase.getConnection();

		// for cdtracks
		try {


			// ---------------------------------------------------------------------------------------------------------------

			// ---------------------------------------------------------------------------------------------------------------
            String query = "" +
                "SELECT  ap.number,ap.title,ra.number,ra.id,ra.format,ra.speed,ra.channels,ra.url,c.key,c.value " +
                "FROM    vpro4_audioparts ap, vpro4_rawaudios ra ,vpro4_properties prop " +
                "WHERE   ap.number = ra.id AND       " +
                        "ap.number = prop.parent AND " +
                        "ap.number IN ("+list+") AND " +
                        "prop.key=\"STARTSTOP\"      " +

                "UNION " +

                "SELECT  ap.number,ap.title,ra.number,ra.id,ra.format,ra.speed,ra.channels,ra.url,'','' " +
                "FROM    vpro4_audioparts ap, vpro4_rawaudios ra " +
                "WHERE   ap.number=ra.id AND " +
                        "(NOT EXISTS (SELECT * FROM vpro4_properties WHERE parent=ap.number)) " +
                        "AND ap.number IN ("+list+") " +
                        "ORDER BY 1,4,5,6,7,8";

			Statement stmt=con.createStatement();
			//debug( "getTableListCdTracks("+query+"): executing" );
			ResultSet rs=stmt.executeQuery( query );

			while(rs.next()) {
				int id=rs.getInt(1);
				PlaylistItem newi=new PlaylistItem();
				newi.id			= id;
				newi.relid		= -1;
				newi.score		= -1;
				newi.oldpos		= -1;
				newi.startdate	= -1;
				newi.format		= rs.getInt(5);
				newi.title		= rs.getString(3);
				newi.group		= rs.getString(1);
				newi.groupId	= rs.getInt(2);
			
				String startstopKey = rs.getString(9);	
				String startstop    = rs.getString(10);
				if ( startstopKey != null && !startstopKey.equals("") && startstopKey.equals("STARTSTOP") && startstop != null && !startstop.equals(""))
				{
					int	   index 	= startstop.indexOf(' ');
					String	sStart=null, sStop=null;
					try
					{
						sStart			= startstop.substring(0, index);
						newi.starttime  = sStart;
						if (newi.starttime < 0) 
							debug("getTableListCdTracks(): got a starttime("+newi.starttime+") for item("+newi.toString()+") which is less than 0!");
					}
					catch( NumberFormatException e)
					{
						// illegal number in start 
						// -----------------------
						debug("While parsing start-time("+sStart+") for playlistItem("+newi.toString()+"), an exception occured:");
						e.printStackTrace();	
						newi.starttime = null;
					}

					try
					{
						sStop			= startstop.substring(index +1);
						newi.stoptime   = sStop;
						if (newi.stoptime  < 0) 
							debug("getTableListCdTracks(): got a stoptime("+newi.stoptime+") for item("+newi.toString()+") which is less than 0!");
					}
					catch( NumberFormatException e)
					{
						// illegal number in start 
						// -----------------------
						debug("While parsing stop-time("+sStop+") for playlistItem("+newi.toString()+"), an exception occured:");
						e.printStackTrace();	
						newi.stoptime = null;
					}
				}
				else
				{
					// non-existent times in DB
					// ------------------------

					newi.starttime  = -1;
					newi.stoptime   = -1;
				}

				result.addElement(newi);
			}	
			stmt.close();
			con.close();	
		} catch (SQLException e) {
			e.printStackTrace();
		}

		*/
		//debug("getTableListAudioParts("+list+"): end, found " + result.size() + " options");
		return(result);
	}

    private Vector sortOnPlaylistID( Vector old )
    {
        return SortedVector.SortVector( old );
    }

    private Vector episode2rawaudio( MMObjectNode node )
    {
        Vector  result = null;
        int     number = node.getIntValue("number");
        result  = episode2rawaudio( number );
        return result;
    }

    private Vector episode2rawaudio( int number )
    {
        Vector result = null;

            // this is a simple query
            // ----------------------
            //                       1                2               3                  4                 5                6                   7
            String query =  "select  episodes.number, audioparts.title, episodes.subtitle, audioparts.number, rawaudios.speed, rawaudios.channels, rawaudios.url, " +
                            //       8                    9                      10                  11
                            "        propertiesStart.key, propertiesStart.value, propertiesStop.key, propertiesStop.value, rawaudios.format " +
                            "from    vpro4_episodes episodes, vpro4_insrel insrel, vpro4_audioparts audioparts, vpro4_rawaudios rawaudios, " +
                            "        vpro4_properties propertiesStart, vpro4_properties propertiesStop " +
                            "where   episodes.number="+ number +" AND episodes.number = insrel.snumber AND insrel.dnumber = audioparts.number AND " +
                            "        audioparts.number = rawaudios.id AND rawaudios.id = propertiesStart.parent AND rawaudios.id=propertiesStop.parent AND " +
                            "        (rawaudios.format=2 OR rawaudios.format=6) and rawaudios.status=3 AND propertiesStart.key=\"starttime\" AND propertiesStop.key=\"stoptime\" " +
                            "union   " +
                            "select  episodes.number, audioparts.title, episodes.subtitle, audioparts.number, rawaudios.speed, rawaudios.channels, rawaudios.url, '', '', '', '', rawaudios.format " +
                            "from    vpro4_episodes episodes, vpro4_insrel insrel, vpro4_audioparts audioparts, vpro4_rawaudios rawaudios " +
                            "where   episodes.number = "+number+" AND episodes.number = insrel.snumber AND insrel.dnumber = audioparts.number AND " +
                            "        audioparts.number = rawaudios.id  AND (rawaudios.format=2 OR rawaudios.format=6) and rawaudios.status=3 AND " +
                            "        ( NOT EXISTS( select * from vpro4_properties where parent=rawaudios.id)) " +
                            "order by 1 ASC, 4 ASC, 5 ASC ";
        MultiConnection con = mmbase.getConnection();
        try
        {
            Statement       stmt        = con.createStatement();
            ResultSet       rs          = stmt.executeQuery( query );
            PlaylistItem    item        = null;
            String          sStartkey   = null, sStartvalue = null;
            String          sStopkey    = null, sStopvalue  = null;
            while( rs.next() )
            {
                if( result == null ) result = new Vector();
                item = new PlaylistItem();

                item.episode    = rs.getInt(1);
                item.title      = rs.getString(2);
                item.group      = rs.getString(3);
                item.id         = rs.getInt(4);
                item.speed      = rs.getInt(5);
                item.channels   = rs.getInt(6);
                item.url        = rs.getString(7);
                item.starttime  = null;
                item.stoptime   = null;
				item.format		= rs.getInt(12);
                sStartkey       = rs.getString(8);
                sStopkey        = rs.getString(10);

                if( sStartkey != null && !sStartkey.equals(""))
                {
                    sStartvalue = rs.getString(9);
                    if( sStartvalue != null && !sStartvalue.equals(""))
                        item.starttime = sStartvalue;
                    else
                        debug("episode2rawaudio("+number+"): starttime: ERROR: key \"starttime\" is defined, but has no value!");
                }

                if( sStopkey != null && !sStopkey.equals(""))
                {
                    sStopvalue = rs.getString(11);
                    if( sStopvalue != null && !sStopvalue.equals(""))
                        item.stoptime = sStopvalue;
                    else
                        debug("episode2rawaudio("+number+"): stoptime: ERROR: key \"stoptime\" is defined, but has no value!");
                }
                result.addElement( item );
            }
            stmt.close();
            con.close();
        }
        catch( SQLException e )
        {
            debug("episode2rawadio("+number+"): ERROR: While executing SQL-statement("+query+"): ");
            e.printStackTrace();
        }

        // print results
        // -------------

        if (result == null)
            debug("episode2rawaudio("+number+"): WARNING: No results are found!");
        else
        {
            result = sortOnPlaylistID( result );
            if (debug)
            {
                debug("episode2rawaudio(): Displaying "+ result.size() +" elements :");
                for (Enumeration e = result.elements(); e.hasMoreElements(); )
                    debug("episode2rawaudio("+number+"): item("+((PlaylistItem)e.nextElement()).toString()+")");
            }
        }
        return result;
    }

	/**
	* try to obtain a decoded param string from the input Vector
	* format in : ji(11212,22323,33434)
	* format out : Vector met 11212 22323 33434
	* on a get with 'ji'
	*/	
    private Vector getParamValues(String in, Vector params) {
        Vector ret = new Vector();
		String p = getParamValue(in,params);
		if(p==null) return ret;
        StringTokenizer st = new StringTokenizer(p,",()");

        while(st.hasMoreTokens()) {
            ret.addElement(st.nextElement());
        }
        return ret;
    }

	/**
	* try to obtain a decoded param string from the input Vector
	* format in : s(11212)
	* format out 11212
	* on a get with 's'
	*/	
	public String getParamValue(String wanted,Vector params) {
		String val=null;
		int pos=-1;
	        Enumeration e=params.elements();
		while (e.hasMoreElements()) {
		 	val=(String)e.nextElement();
			pos=val.indexOf((wanted+"("));
			if (pos==0) {
				pos=val.indexOf('(');
				int pos2=val.indexOf(')');
				return(val.substring(pos+1,pos2));
			}
		}
		return(null);
	}

	/**
	 * computes the votes	
	 */
	public boolean computeVotes(Hashtable votes, String playlistId) {
		//debug("Playlists -> votes "+votes);
		//debug("Playlists -> playlistId "+playlistId);
		debug("** VOTES START **");

		MMObjectBuilder bul=mmbase.getMMObject("pools");
		MMObjectNode node=bul.getNode(playlistId);
	   	if (node!=null) {
			Vector list=getTable2(node,"cdtracks");
			if (list.size()<300) { // added check for size
			for (Enumeration e=list.elements();e.hasMoreElements();) {
				PlaylistItem newi=(PlaylistItem)e.nextElement();
				if(votes.containsKey(""+newi.id)) {
					if(votes.get(""+newi.id).equals("GOOD")) {
						newi.score++;
					} else {
						newi.score--;
					}
					//debug("Playlists -> Nieuwe score "+newi);
					if (!dirtyItems.contains(newi)) dirtyItems.addElement(newi);
				}
			}
			}
			debug("** VOTES END **");
			return true;	
		}
		return false;
	}

	/**
	 * zet de huidige scores in de 'vorige' score-lijst
	 */
	public boolean computeWeekVotes(String playlistId) {
		//debug("Playlists -> Computing weekvotes for node "+playlistId);

		MMObjectBuilder bul=mmbase.getMMObject("pools");
		MMObjectNode node=bul.getNode(playlistId);
	   	if (node!=null) {
			Vector list=getTable2(node,"cdtracks");
		   	list=SortedVector.SortVector(list,new PlaylistScoreCompare());	
			int newpos=1;
			int newscore=list.size()*5; // 5 punten verschil tussen de nummers.
			for (Enumeration e=list.elements();e.hasMoreElements();) {
				PlaylistItem newi=(PlaylistItem)e.nextElement();
				newi.oldpos=newpos++;
				newi.score=newscore;
				newscore-=5;	
				// Elementen die erin zitten moeten eruit en alle deze nieuwe elementen
				// erin, je reset namelijk de scores... XXXXX
				if (!dirtyItems.contains(newi)) dirtyItems.addElement(newi);
			}
			////debug(dirtyItems);
			return true;
		}	
		return false;
	}

	/** 
	 * Wordt niet gebruikt
	 */
	public int secondsTillMonday700() {
		
        String[] ids = TimeZone.getAvailableIDs(1 * 60 * 60 * 1000);
        SimpleTimeZone pdt = new SimpleTimeZone(1 * 60 * 60 * 1000, ids[0]);
        Calendar rightNow = new GregorianCalendar(pdt);
		Date date = new Date();
		rightNow.setTime(date);
		int uur = rightNow.get(Calendar.HOUR_OF_DAY);
		int minute = rightNow.get(Calendar.MINUTE);
		int x = 31*60-(uur*60+minute);

		switch(rightNow.get(Calendar.DAY_OF_WEEK)) {
        	case 3: //Tis dinsdag
            	x+=5*24*60;
                break;
            case 4: //Tis woensdag
                x+=4*24*60;
                break;
            case 5: //Tis donderdag
                x+=3*24*60;
                break;
            case 6: //Tis vrijdag
                x+=2*24*60;
                break;
            case 7: //Tis zaterdag
                x+=1*24*60;
                break;
            case 1: //Tis zondag
                break;
            case 2: //Tis maandag
				if(minute<7) {
					x%=1440; //Het is nog geen 7 uur.
				} else {
					x+=6*24*60; //Volgende week weer.
				}
                break;
            default:
				debug("Playlists -> uhhh, deze dag vang ik niet af");
                break;
        }
		debug("Playlists -> Nog "+x+" minuten tot maandag 7:00");
		return(x*60);
	}

	public void checkVotes() {
		// Bereken alle 'vorige scores' in de playlist
		/*
		String servername=getProperty("server","MachineName");
		int plnumber,lastupdated,waittime;
		if (servername.equals("station")) {
		   	MultiConnection con=mmbase.getConnection();
			int currentTime = (int)(System.currentTimeMillis()/1000);

        	try {
            	Statement stmt=con.createStatement();
				// Haal alle playlists op waarvan de oldpos berekend moeten worden.
            	ResultSet rs=stmt.executeQuery("select * from vpro4_playlistupdate;");
            	while(rs.next()) {
               		plnumber=rs.getInt(1);
               		lastupdated=rs.getInt(2);
               		waittime=rs.getInt(3);

					// Moet de oldpos berekend worden?
					if(currentTime>lastupdated+waittime) {
            			stmt.executeUpdate("update vpro4_playlistupdate set lastupdated="+currentTime+" where plnumber="+plnumber+";");
						computeWeekVotes(""+plnumber);
						debug("Playlists }=> weekVotes("+plnumber+") at "+currentTime);
					}
            	}
            	stmt.close();
            	con.close();
        	} catch (SQLException e) {
            	e.printStackTrace();
				debug("Playlists -> checkVotes, het berekenen van de 'weekvotes' ging fout."); 
        	}
		}
		try {
			MMObjectBuilder bul=mmbase.getMMObject("pools");
			while (dirtyItems.size()>0) {
				PlaylistItem changed=(PlaylistItem)dirtyItems.elementAt(0);
				MMObjectNode relnode=bul.getNodeDef(changed.relid);
				debug("Updating node "+relnode);
				if (relnode.getIntValue("otype")!=11017) {
					debug("Playlist WEIRD ="+relnode);
				} else {
					relnode.setValue("score",changed.score);
					relnode.setValue("oldpos",changed.oldpos);
					relnode.commit();	
				}
				dirtyItems.removeElementAt(0);
				try {Thread.sleep(5000);} catch (InterruptedException e){}
			}
		} catch (Exception e) {
			debug("PLaylist error");
			e.printStackTrace();
		}
		debug("Playlists -> checkVotes afgerond");
		*/
	}

	Vector getSublist(Vector numbers,String only) {
		PlaylistItem item;
		Vector ones=new Vector();
        StringTokenizer tok = new StringTokenizer(only,",\n\r");
		while (tok.hasMoreTokens()) {
			ones.addElement(new Integer(tok.nextToken()));
		}
		Vector results=new Vector();
		for (Enumeration e=numbers.elements();e.hasMoreElements();) {
			item=(PlaylistItem)e.nextElement();
			if (ones.contains(new Integer(item.id))) results.addElement(item);
		}
		//debug("RESULTS="+results);
		return(results);
	}


// ----------------------------------------------------------------------


    public static String makeSet(Vector lst) {
        return(makeSet(lst," {","",",","","} "));
    }

    public static String makeSet(Vector lst,String pre,String post) { 
        return(makeSet(lst,pre,"",",","",post));
    }

    public static String makeSet(Vector lst,String pre,String preobj,String between,String postobj,String post) {
        boolean first=true;
        StringBuffer b=new StringBuffer();
        String obj;
        b.append(pre);
        for (Enumeration t = lst.elements();t.hasMoreElements();) {
            obj=(String)t.nextElement();
            if (obj.length()>0) {
                if (first) {
                    b.append(preobj+obj+postobj);
                    first=false;
                } else {
                    b.append(between+preobj+obj+postobj);
                }
            } else {
                debug2("Filter-> makeSet : Invalid filmkey");
            }
        }
        b.append(post);
        return(b.toString());

    }

// ----------------------------------------------------------------------



	public String vector2string(Vector vec){
		String result=null;
		Enumeration enum = vec.elements();
		while (enum.hasMoreElements()){
			if (result==null) {	
				result=(String)enum.nextElement();
			} else { 
				result+=","+(String)enum.nextElement();
			}
		}
		return (result);
	}


	public Vector string2vector(String line){
		Vector results=new Vector();
		StringTokenizer tok = new StringTokenizer(line,",\n\r");
		while (tok.hasMoreTokens()){
			results.addElement(tok.nextToken());
		}
		return (results);
	}

	public Vector filterAge(Vector numbers, int age) {
		// oops we should convert the age to a max mmbase number
		int max=((DayMarkers)mmbase.getMMObject("daymarks")).getDayCountAge(age);
		Vector results=new Vector();
		Enumeration enum = numbers.elements();
		while (enum.hasMoreElements()){
			PlaylistItem newi=(PlaylistItem)enum.nextElement();
			if (newi.id>max) results.addElement(newi);
		}	
		return(results);
	}

	boolean getBestUrl(int id, boolean isInternal){
		String streams=(String)urlcache.get(new Integer(id));
		if (streams!=null && streams.equals("Y")) return(true);
		MMObjectBuilder bul=mmbase.getMMObject("rawaudios");
		Vector nodes=bul.searchVector("id=="+id);
		Enumeration enum = nodes.elements();
		String url=null;
		while (enum.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)enum.nextElement();
			url=node.getStringValue("url");
		}
		if (url!=null && url.indexOf("streams")!=-1) {
			urlcache.put(new Integer(id),"Y");
			return(true);
		} else {
			urlcache.put(new Integer(id),"N");
			return(false);
		}
	}

	private MMObjectNode getAliasedNode(String key) {
		OAlias oalias=(OAlias)mmbase.getMMObject("oalias");
		int nr;
		MMObjectNode node;

//		debug("Playlists -> Alias Key "+key);
		try {
			nr=Integer.parseInt(key);
		} catch (Exception e) {
			nr=-1;
		}
//		debug("Playlists -> Alias Number "+nr);
		if (nr>0) {
			node=oalias.getNode(nr);
		} else {
			node=oalias.getAliasedNode(key);
		}
//		debug("Playlists -> Alias Node "+node);
		return(node);
	}

	long oldtime   = 0;
	long newtime   = 0;
	long totaltime = 0;
	long thistime  = 0;

	private void time( String msg, boolean reset )
	{
		if (!reset)
		{
			newtime    = System.currentTimeMillis();
			thistime   = (newtime - oldtime);
			oldtime    = newtime;
			totaltime += thistime; 
		}
		else
		{
			oldtime   = System.currentTimeMillis();
			totaltime = 0;
		}

		if (reset)
			debug( msg + ", start timing now..");
		else 
			debug( msg + ", time("+ thistime +"/"+ totaltime+")");

	}

	public Vector getTableVector(MMObjectNode node,String so) {
		Vector result=new Vector();
		MultiRelations multirelations=(MultiRelations)mmbase.getMMObject("multirelations");

		int pnumber=node.getIntValue("number");
		Vector tables=new Vector();
		tables.addElement("pools");
		tables.addElement(so);
		Vector fields=new Vector();
		fields.addElement(so+".number");
		Vector ordervec=new Vector();
		ordervec.addElement(so+".number");
		Vector dirvec=new Vector();
		dirvec.addElement("UP");

		int apid;
		MMObjectNode pnode;
		Vector vec=multirelations.searchMultiLevelVector(pnumber,fields,"NO",tables,"",ordervec,dirvec);
		Enumeration e=vec.elements();
		while (e.hasMoreElements()) {
			pnode=(MMObjectNode)e.nextElement();
			apid=pnode.getIntValue(so+".number");
			result.addElement(""+apid);
		}
		return(result);
	}


	public Vector getTablePools(MMObjectNode node,String so) {
		Vector result=new Vector();
		MMObjectNode pnode;
		int apid;
		Vector v;
		int idx;
		MultiRelations multirelations=(MultiRelations)mmbase.getMMObject("multirelations");

		int pnumber=node.getIntValue("number");
		Vector tables=new Vector();
		tables.addElement("pools");
		tables.addElement(so);
		Vector fields=new Vector();
		fields.addElement(so+".number");
		fields.addElement(so+".title");
		Vector ordervec=new Vector();
		ordervec.addElement(so+".number");
		Vector dirvec=new Vector();
		dirvec.addElement("UP");

		//debug("BAH");
		Vector vec=multirelations.searchMultiLevelVector(pnumber,fields,"NO",tables,"",ordervec,dirvec);
		Enumeration e=vec.elements();
		v=new Vector();
		while (e.hasMoreElements()) {
			pnode=(MMObjectNode)e.nextElement();
			apid=pnode.getIntValue(so+".number");
			v.addElement(""+apid);
		}


        MultiConnection con = mmbase.getConnection();
        try
        {
			String query="select id,speed,channels,url,format,status from vpro4_rawaudios where id in ("+vector2string(v)+") and status=3 order by id;";
            Statement       stmt        = con.createStatement();
            ResultSet       rs          = stmt.executeQuery( query );
			String tmp,startT=null,stopT=null;
			MMObjectNode t,anode;

			idx=0;
			int previd=-1;
            while( rs.next() )
            {
				PlaylistItem newi=new PlaylistItem();

				newi.id			=rs.getInt(1);
				newi.relid=9467; // hacked on vpro
				newi.score		=0;
				newi.oldpos		=0;
				newi.startdate	=0;
				newi.title		=findTitle(idx,newi.id,vec,so);
				newi.group="vpro";
				newi.groupId=9468;
				newi.speed		=rs.getInt(2);
				newi.channels	=rs.getInt(3);
				newi.url		=rs.getString(4);
				newi.format		=rs.getInt(5);
				
				if (debug) debug(" "+newi.id+" - "+previd);
				if (previd!=newi.id) {
					stopT=startT=null;
					anode=multirelations.getNode(newi.id);
					
					tmp=null;
					t=anode.getProperty("starttime");
					if (t!=null) tmp=t.getStringValue("value");
					if (tmp!=null && tmp.length()>0) startT=tmp;
					tmp=null;
					t=anode.getProperty("stoptime");
					if (t!=null) tmp=t.getStringValue("value");
					if (tmp!=null && tmp.length()>0) stopT=tmp;
					if (debug) debug(" "+startT+" - "+stopT);
				}
				newi.starttime	= startT;
				newi.stoptime	= stopT;

				result.addElement(newi);
				if (previd==-1) previd=newi.id;
				if (previd!=newi.id) idx++;
				previd=newi.id;
			}
			stmt.close();
			con.close();	
		} catch(Exception f) {
			debug("ERROR");
			f.printStackTrace();
		}
		return(result);
	}

	private String findTitle(int idx,int id,Vector v,String so) {
		MMObjectNode n;
		int k;
		String rtn="Jingle";
		n=(MMObjectNode)v.elementAt(idx);
		k=n.getIntValue(so+".number");
		if (k==id)  {
			rtn=n.getStringValue(so+".title");
		} else {
			if (k<id) {
				idx++;
				while(idx<v.size() && k<id) {
					n=(MMObjectNode)v.elementAt(idx);
					k=n.getIntValue(so+".number");
					if (k==id) {
						rtn=n.getStringValue(so+".title");
						break;
					}
					idx++;
				}
			} else {
				idx--;
				while(idx>0 && k>id) {
					n=(MMObjectNode)v.elementAt(idx);
					k=n.getIntValue(so+".number");
					if (k==id) {
						rtn=n.getStringValue(so+".title");
						break;
					}
					idx--;
				}
			}
		}
		if (rtn==null) rtn="Jingle";
		return(rtn);
	}
}
