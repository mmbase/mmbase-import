/*
$Id: AudioParts.java,v 1.5 2000-03-27 16:10:35 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
Revision 1.4  2000/03/24 14:33:56  wwwtech
Rico: total recompile

Revision 1.3  2000/02/28 17:13:48  wwwtech
- (marcel) Added getAudiopartUrl()

Revision 1.2  2000/02/24 13:40:03  wwwtech
Davzev activated replace() method and GETURL and fixed GETURL related methods.

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.gui.html.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.sessionsInterface;
import org.mmbase.module.sessionInfo;

import nl.vpro.mmbase.util.media.audio.*;
import nl.vpro.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen, David van Zeventer
 * @version 12 Mar 1997
 * @$Revision $Date
 *
 * 16 Dec 1999 davzev Added $MOD Command to retrieve Url to the Audiofiles.
 * NOTE: Only Audiofiles of type RA_FORMAT and SURESTREAM_FORMAT will be retrieved.
 */
public class AudioParts extends MMObjectBuilder {

	private static String classname = "org.mmbase.module.builders.AudioParts"; //getClass().getName();

	String diskid;
	int playtime;

	public int insertDone(EditState ed,MMObjectNode node) {
		String sourcepath=ed.getHtmlValue("sourcepath");
		int devtype=node.getIntValue("source");
		int id=node.getIntValue("number");
		String devname = null;

		if (devtype==7){		//Check if source is from a jazzdrive -> 7

			//sourcepath contains  eg. /Drivename/Dir/File
			String delim = "/";
			StringTokenizer tok = new StringTokenizer(sourcepath,delim);     //Retrieve devname
			if (tok.hasMoreTokens()) {
				devname = tok.nextToken();
			}else{
				System.out.println("AudioParts: insertDone: srcfile cannot be tokenized using symbol "+delim);
				System.out.println("AudioParts: insertDone: insertDone will fail");
			}
			jazzdrives bul=(jazzdrives)mmb.getMMObject("jazzdrives");
			Enumeration e=bul.search("WHERE name='"+devname+"'");
			if (e.hasMoreElements()) {
				MMObjectNode jnode=(MMObjectNode)e.nextElement();
				jnode.setValue("state","copy");
				jnode.setValue("info","srcfile="+sourcepath+" id="+id);
				jnode.commit();
			}
		} else if (devtype==4 || devtype==5) {		//Check if source is from a import/
			if (sourcepath!=null) {
				System.out.println ("AudioParts.insertDone -> sourcepath = " + sourcepath);
				System.out.println ("AudioParts.insertDone -> number = " + id);
				File newfile=new File("/data/audio/wav/"+id+".wav");
				// With the new editor-interface (pulldowns), the full pathname
				// will be provided (so including the leading '/data/import/')
				//File curfile=new File("/data/import/"+t);
				File curfile = new File (sourcepath);
				if (curfile.exists()) {
					if (curfile.renameTo(newfile)==false) {
						System.out.println("AudioParts -> Can't rename wav file : " + sourcepath);
					} else {
						int st=node.getIntValue("storage"); 
						RawAudios bul=(RawAudios)mmb.getMMObject("rawaudios");
						if (st==1 || st==2) {
							addRawAudio(bul,id,3,3,441000,2);   
						} else if (st==3 || st==4) {
							addRawAudio(bul,id,3,3,441000,1);   
						}
						wavAvailable(""+id);
					}
				}
			}
		}
		// devtype 8 is Armin
		return(id);
	}

	/**
	* pre commit from the editor
	*/
	public int preEdit(EditState ed, MMObjectNode node) {
		//debug("preEdit(): start");
		if ( node != null ) {
			String starttime = ed.getHtmlValue("starttime");
			String stoptime  = ed.getHtmlValue("stoptime");
	
			debug("preEdit("+node.getName()+"):starttime("+starttime+")");
			debug("preEdit("+node.getName()+"): stoptime("+stoptime+")");

			// check if (stop - start) == lengthOfPart, if lengthOfPart != -1

			// startstop
			if( starttime != null ) {
				// is it valid ?
				// -------------

				if (checktime(starttime)) {
					putProperty( node, "starttime", starttime);
				} else {
					// no, maybe we have to remove it (when its empty or '-1')
					// -------------------------------------------------------

					if (starttime.equals("") || starttime.equals("-1")) {
						removeProperty( node, "starttime" );
					} else {
						debug("preEdit("+node+","+starttime+"): ERROR: Dont know what to do with this starttime for this node!");
					}
				}
			}
			else {
				// error ? daniel	putProperty( node, "starttime", "-1");
			}

			if ( stoptime != null ) {
				// check if its a valid time
				// -------------------------

				if(checktime(stoptime)) {
					putProperty( node, "stoptime" , stoptime);
				} else {
					// not a valid time, maybe we have tot remove this property
					// --------------------------------------------------------

					if(stoptime.equals("") || stoptime.equals("-1"))
						removeProperty(node, "stoptime");	
					else
						debug("preEdit("+node+","+stoptime+"): ERROR: Dont know what to do this this stoptime for this node!");
				}
			} else {
				// error ? daniel	putProperty( node, "stoptime" , "-1");
			}
		} else {
			debug("preEdit(): ERROR: node is null!");
		}
		return(-1);	
	}

	public static long calcTime( String time ) {
		long result = -1;

		long r 		= 0;

		int calcList[] 	= new int[5];
		calcList[0]		= 0;
		calcList[1] 	= 100; 	// secs 
		calcList[2] 	= 60;	// min 
		calcList[3] 	= 60;	// hour 
		calcList[4] 	= 24;	// day

		if (time.indexOf(".")!=-1 || time.indexOf(":") != -1) {
			int day 	= -1;
			int hour 	= -1;
			int min		= -1;
			int	sec		= -1;
			StringTokenizer tok = new StringTokenizer( time, ":" );
			if (tok.hasMoreTokens()) {	
				int i 		= 0;
				int	total 	= tok.countTokens();
				int mulfac, t;

				String tt	= null;
				try {
					int ttt = 0;
					int tttt = 0;

					while(tok.hasMoreTokens()) {
						tt 		= tok.nextToken();
						tttt	= 0;

						if (tt.indexOf(".")==-1) {
							tttt	= total - i;
							t 		= Integer.parseInt( tt );
							int tot		= t;
	
							while (tttt != 0) {
								mulfac 	 = calcList[ tttt ];
								tot 	 = mulfac * tot;	
								tttt--;
							}
							r += tot;
							i++;
						}
					}
				}
				catch( NumberFormatException e ) {
					System.out.println("calcTime("+time+"): ERROR: Cannot convert pos("+(total-i)+") to a number("+tt+")!" + e.toString());
				}
			}

			if (time.indexOf(".") != -1) {
				// time is secs.msecs

				int index = time.indexOf(":");	
				while(index != -1) {
					time = time.substring( index+1 );
					index = time.indexOf(":");
				}
	
				index = time.indexOf(".");
				String 	s1 = time.substring( 0, index );
				String	s2 = time.substring( index +1 );

				try {
					int t1 = Integer.parseInt( s1 );
					int t2 = Integer.parseInt( s2 );
		
					r += (t1*100) + t2; 

				} catch( NumberFormatException e ) {
					System.out.println("calctime("+time+"): ERROR: Cannot convert s1("+s1+") or s2("+s2+")!");
				} 
			}

			result = r;

		} else {
			// time is secs
			try {
				r = Integer.parseInt( time );
				result = r * 100;
			} catch( NumberFormatException e ) {
				System.out.println("calctime("+time+"): ERROR: Cannot convert time("+time+")!");
			}
		}

		return result;
	}

	/**
	 * checktime( time )
	 *
	 * time = dd:hh:mm:ss.ss
	 * 
	 * Checks whether part is valid, each part (dd/hh/mm/ss/ss) are numbers, higher than 0, lower than 100
	 * If true, time can be inserted in DB.
	 *
	 */
	private boolean checktime( String time ) {
		boolean result = true;
		
		if (time!=null && !time.equals("")) {

			StringTokenizer tok = new StringTokenizer( time, ":." );
			while( tok.hasMoreTokens() ) {
				if (!checktimeint(tok.nextToken())) {
					result = false;
					break;
				}
			}
		} else {
			debug("checktime("+time+"): ERROR: Time is not valid!");
		}
		
		//debug("checktime("+time+"): simpleTimeCheck(" + result+")");
		return result;
	}

	private boolean checktimeint( String time ) {
		boolean result = false;

		try {
			int t = Integer.parseInt( time );
			if (t >= 0) {
				if( t < 100 ) {
					result = true;
				} else {
					debug("checktimeint("+time+"): ERROR: this part is higher than 100!"); 
					result = false;
				}
			} else {
				debug("checktimeint("+time+"): ERROR: Time is negative!");
				result = false;
			}

		} catch( NumberFormatException e ) {
			debug("checktimeint("+time+"): ERROR: Time is not a number!");
			result = false;
		}

		//debug("checktimeint("+time+"): " + result);	
		return result;
	}

	private String getProperty( MMObjectNode node, String key ) {
		String result = null;

		//debug("getProperty("+key+"): start");

		int id = -1;
		if( node != null ) {
			id = node.getIntValue("number");
			
			MMObjectNode pnode = node.getProperty( key );
			if( pnode != null ) {
				result = pnode.getStringValue( "value" );
			} else {
				debug("getProperty("+node.getName()+","+key+"): ERROR: No prop found for this item("+id+")!");
			}
		} else {
			debug("getProperty("+"null"+","+key+"): ERROR: Node is null!");
		}

		//debug("getProperty("+key+"): end("+result+")");
		return result;
	}

	private void putProperty( MMObjectNode node, String key, String value )
	{
		//debug("putProperty("+key+","+value+"): start");
		int id = -1;
		if ( node != null ) {
			id = node.getIntValue("number");

        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) {
				if (value.equals("") || value.equals("null") || value.equals("-1")) {
					// remove
					pnode.parent.removeNode( pnode );
				} else {
					// insert
            		pnode.setValue("value",value);
                	pnode.commit();
				}
            } else {
				if ( value.equals("") || value.equals("null") || value.equals("-1") ) {
					// do nothing
				} else {
					// insert
					MMObjectBuilder properties = mmb.getMMObject("properties");
					MMObjectNode snode = properties.getNewNode ("audioparts");
					 //snode.setValue ("otype", 9712);
   		             snode.setValue ("ptype","string");
   		             snode.setValue ("parent",id);
   		             snode.setValue ("key",key);
   		             snode.setValue ("value",value);
   		             int id2=properties.insert("audioparts", snode); // insert db
   		             snode.setValue("number",id2);
   		             node.putProperty(snode); // insert hash
				}
			}
		} else {
			debug("putProperty("+"null"+","+key+","+value+"): ERROR: Node is null!");
		}

		//debug("putProperty("+key+","+value+"): end");
	}

	private void removeProperty( MMObjectNode node, String key ) {
		//debug("removeProperty("+key+","+value+"): start");
		if ( node != null ) {
        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) 
				pnode.parent.removeNode( pnode );
			else
				debug("removeNode("+node+","+key+"): ERROR: Property not found( and cannot remove )");
		}
	}

	public String getGUIIndicator(MMObjectNode node) {
		String str=node.getStringValue("title");
		if (str.length()>15) {
			return(str.substring(0,12)+"...");
		} else {
			return(str);
		}
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("storage")) {
			int val=node.getIntValue("storage");
			switch(val) {
				case 1: return("Stereo");
				case 2: return("Stereo geen backup");
				case 3: return("Mono");
				case 4: return("Mono geen backup");
				default: return("Onbepaald");
			}
		}
		return(null);
	}

	/**
	* get new node
	*/
	public MMObjectNode getNewNode(String owner) {
		MMObjectNode node=super.getNewNode(owner);
		return(node);
	}

	public void wavAvailable(String id) {
		MMObjectNode node=getNode(id);
		int st=node.getIntValue("storage"); 
		if (st!=0) {
			System.out.println("AudioParts -> Store command on "+id+" = "+st);
			RawAudios bul=(RawAudios)mmb.getMMObject("rawaudios");
			if (bul!=null) {
				if (st==1 || st==2) { 
					try {
						int idi=Integer.parseInt(id);
						addRawAudio(bul,idi,1,2,16000,1);   
						addRawAudio(bul,idi,1,2,32000,1);   
						addRawAudio(bul,idi,1,2,40000,1);   
						addRawAudio(bul,idi,1,2,40000,2);   
						addRawAudio(bul,idi,1,2,80000,2);   
						addRawAudio(bul,idi,1,6,96000,2);   
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("AudioParts -> Wrong id in ParseInt");
					}
				}
				if (st==3 || st==4) { 
					try {
						int idi=Integer.parseInt(id);
						addRawAudio(bul,idi,1,2,16000,1);   
						addRawAudio(bul,idi,1,2,32000,1);   
						addRawAudio(bul,idi,1,2,40000,1);   
						addRawAudio(bul,idi,1,2,80000,1);   
						addRawAudio(bul,idi,1,6,96000,2);   
					} catch (Exception e) {
						System.out.println("AudioParts -> Wrong id in ParseInt");
					}
				}
			}
		}
	}

	public void addRawAudio(RawAudios bul,int id, int status, int format, int speed, int channels) {
		MMObjectNode node=bul.getNewNode("system");		
		node.setValue("id",id);
		node.setValue("status",status);
		node.setValue("format",format);
		node.setValue("speed",speed);
		node.setValue("channels",channels);
		bul.insert("system",node);
	}

	public void pcmAvailable(String id) {
		MMObjectNode node=getNode(id);
		int st=node.getIntValue("storage"); 
		if (st!=0) {
			System.out.println("AudioParts -> Store command on "+id+" = "+st);
			RawAudios bul=(RawAudios)mmb.getMMObject("rawaudios");
			if (bul!=null) {
				if (st==1) {
					try {
						int idi=Integer.parseInt(id);
						addRawAudio(bul,idi,1,5,192000,2);   
					} catch (Exception e) {
						System.out.println("AudioParts -> Wrong id in ParseInt");
					}
				}

				if (st==2) {
					try {
						int idi=Integer.parseInt(id);
						addRawAudio(bul,idi,1,2,16000,1);   
						addRawAudio(bul,idi,1,2,20000,2);   
						addRawAudio(bul,idi,1,2,32000,1);   
						addRawAudio(bul,idi,1,2,32000,2);   
						addRawAudio(bul,idi,1,2,40000,1);   
						addRawAudio(bul,idi,1,2,40000,2);   
						addRawAudio(bul,idi,1,2,80000,1);   
						addRawAudio(bul,idi,1,2,80000,2);   
						addRawAudio(bul,idi,1,6,96000,2);   
					} catch (Exception e) {
						System.out.println("AudioParts -> Wrong id in ParseInt");
					}
				}

				if (st==3) {
					try {
						int idi=Integer.parseInt(id);
						addRawAudio(bul,idi,1,2,16000,1);   
						addRawAudio(bul,idi,1,2,32000,1);   
						addRawAudio(bul,idi,1,2,40000,1);   
						addRawAudio(bul,idi,1,2,80000,1);   
						addRawAudio(bul,idi,1,6,96000,2);   
					} catch (Exception e) {
						System.out.println("AudioParts -> Wrong id in ParseInt");
					}
				}
			}
		}
	}

	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		node.setValue("storage",2);
	}

    /**
    * replace all for frontend code
    */
    public String replace(scanpage sp, StringTokenizer command) {

		if (command.hasMoreTokens()) {
            String token=command.nextToken();
            // debug("replace: The nextToken = "+token);

            if (token.equals("GETURL")) {
				return doGetUrl(sp, command);
            } else {
  				debug("replace: Unknown command used: "+token);
		  		return("Unknown command used: "+token+" ,says the AudioParts builder");
			}
        }
        
  		debug("replace: No command defined.");
  		return("No command defined, says the AudioParts builder.");
    }

	/**
	 * doGetUrl: Retrieve the Url of the audiofile of this AudioParts or cdtracks node.
	 * This method first checks to see if the related RawAudios node uses a SureStream format.
	 * If so And the file is available, then it builds the Url using RawAudios.getHostname .getFileName
	 * .getProtocolName and returns it.
	 *
	 * If it uses a RA format and is available it adds it to a availableRaNodes vector.
	 * After adding, it selects the bestRaNode from this vector relying on the users' settings
	 * and builds the Url using RawAudios.getHostname .getFileName .getProtocalName and returns it.
	 *
	 * NOTE: The title and author informat is NOT added to the Url, since this can be done using LISTINGS.
	 *
	 * @param sp The scanpage object used when retrieving the users' settings.
	 * @param command A StringTokenizer object containing the rest of the $MOD cmd string.
	 * @return A String containing the Url to the audiofile.
	 */
	String doGetUrl(scanpage sp, StringTokenizer command) {
		int apNumber  = 0;  //The value of the AudioParts node or cdtracks node.
		Vector availableRaNodes = new Vector();

		if (command.hasMoreTokens()) {
			String token=command.nextToken();
			// debug("doGetUrl: The nextToken = "+token);

			debug("doGetUrl: Session name = "+sp.sname);
			try {
				apNumber = Integer.parseInt(token);
			} catch(Exception e) {
				debug("doGetUrl: "+e);
				debug("doGetUrl: Invalid AudioPartnumber used -> number="+token+" returning null");
				return ("");
			}

			// First select the RawAudio node and determine format and status.
			MMObjectBuilder raBuilder = mmb.getMMObject("rawaudios");
			Enumeration e = raBuilder.search("where id="+apNumber);
			while (e.hasMoreElements()) {
				MMObjectNode raNode = (MMObjectNode) e.nextElement();
				int format = raNode.getIntValue("format");
				int status = raNode.getIntValue("status");
				if (format == RawAudios.SURESTREAM_FORMAT) {
					if (status == RawAudios.GEDAAN) {
						String protName = RawAudios.getProtocolName(format);
						String hostName = RawAudios.getHostName(raNode.getStringValue("url"));
						// Since a surestream controls the speed & channels himself, the other 2 args I give value 0.
						String fileName = RawAudios.getFileName(format,0,0);
						debug("doGetUrl: protName = "+protName+" , hostName = "+hostName+" , fileName = "+fileName);
						return (protName+"://"+hostName+"/"+apNumber+"/"+fileName+getSongInfo(apNumber)+getStartStopTimes(apNumber));
					} else {
						debug("doGetUrl: This rawaudio isn't ready yet status="+status);
						return ("");
					}
				} else if (format == RawAudios.RA_FORMAT) {
					if (status == RawAudios.GEDAAN) availableRaNodes.addElement(raNode);
				}
			}

			// Retrieve the best RA file if one was found else return nothing.
			// NOTE: If only other formats were found (eg WAVS MP3 etc.) then do nothing and return.
			if (availableRaNodes != null) {
				// debug("doGetUrl: The availableRanodes Vector contains: "+availableRaNodes);
				// Retrieve the best RawAudios node.
				MMObjectNode bestNode = getBestRaNode(sp, command, availableRaNodes);
				if (bestNode != null) {
					int speed    = bestNode.getIntValue("speed");
					int channels = bestNode.getIntValue("channels");
					String protName = RawAudios.getProtocolName(RawAudios.RA_FORMAT);
					String hostName = RawAudios.getHostName(bestNode.getStringValue("url"));
					String fileName = RawAudios.getFileName(RawAudios.RA_FORMAT,speed,channels);
					debug("doGetUrl: protName = "+protName+" , hostName = "+hostName+" , fileName = "+fileName);
					return (protName+"://"+hostName+"/"+apNumber+"/"+fileName+getSongInfo(apNumber)+getStartStopTimes(apNumber));
				} else {
					debug("doGetUrl: There isn't any rawaudio available at this moment.");
					return ("");
				}
			} else {
				debug("doGetUrl: There isn't any rawaudio available at this moment at all.");
				return ("");
			}
		}

		debug("doGetUrl: No AudioParts ObjectNumber defined.");
		return("No AudioParts ObjectNumber defined, says the AudioParts builder.");
	}

	/**
	 * getBestRaNode: Retrieves the best RawAudios Node by using the command arguments given
	 * OR by using the users' speed & channel settings from his SESSION VARS.
	 *
	 * @param sp The scanpage object used when retrieving the users' settings.
	 * @param command A StringTokenizer object containing the rest of the $MOD cmd string.
	 * @param availableRaNodes
	 * @return The bestRaNode as an MMObjectNode.
	 */
	MMObjectNode getBestRaNode(scanpage sp, StringTokenizer command, Vector availableRaNodes) {
		int userSpeed    = 0;
		int userChannels = 0;
		int bestSpeed    = 0;
		int bestChannels = 0;
		int posOffset = Integer.MAX_VALUE;
		int negOffset = Integer.MAX_VALUE;
		Vector userSettings = null;

		try {
			// Retrieve speed & channels from either command args or users' SESSION VAR
			if (command.hasMoreTokens()) {
				debug("getBestRaNode: Gettings speed & channels settings from the command args.");
				String token = command.nextToken();
				userSpeed    = Integer.parseInt(token);
				token = command.nextToken();
				userChannels = Integer.parseInt(token);
			} else {
				debug("getBestRaNode: Gettings speed & channels settings from users' SESSION");
				// Get the session module using "mmb" classfield from MMObjectBuilder from which AudioParts is extended.
				sessionsInterface sessions = (sessionsInterface) mmb.getModule("SESSION");
				sessionInfo session = sessions.getSession(sp,sp.sname);
				userSpeed    = Integer.parseInt(sessions.getValue(session,"SETTING_RASPEED"));
				userChannels = Integer.parseInt(sessions.getValue(session,"SETTING_RACHANNELS"));
			}
		} catch(Exception e) {
			debug("getBestRaNode: "+e);
			debug("getBestRaNode: Invalid userSpeed or userChannels, using closest starting from "+"s="+userSpeed+" c="+userChannels);
			// Since the userspeed & channels aren't set they are still 0 , so the closest to 0 will be used.
		}

		debug("getBestRaNode: userSpeed = "+userSpeed+" , userChannels = "+userChannels);

		//Calculate speed offset to speed in availableRaNodes vector.
		Enumeration e = availableRaNodes.elements();
		while (e.hasMoreElements()) {
			MMObjectNode node = (MMObjectNode) e.nextElement();
			int nodeSpeed    = node.getIntValue("speed");
			int nodeChannels = node.getIntValue("channels");
			if (userSpeed >= nodeSpeed) {
				if (posOffset > Math.abs(userSpeed - nodeSpeed)) posOffset = Math.abs(userSpeed - nodeSpeed);
			} else {
				if (negOffset > Math.abs(userSpeed - nodeSpeed)) negOffset = Math.abs(userSpeed - nodeSpeed);
			}
		}

		// Calculate closest AKA best speed
		if (posOffset < negOffset) {
			// use lower closest RaNode
			bestSpeed = userSpeed - posOffset;
		} else {
			// use higher closest RaNode
			bestSpeed = userSpeed + negOffset;
		}

		// The 40 & 80Kbit streams can have 1 or 2 channels so test what the user wants.
		if ((bestSpeed == 40000) || (bestSpeed == 80000)) {
			if ((userChannels == 1) || (userChannels == 2))
				bestChannels = userChannels;
		}

		// Select & return the bestNode.
		e = availableRaNodes.elements();
		while (e.hasMoreElements()) {
			MMObjectNode node = (MMObjectNode) e.nextElement();
			if (node.getIntValue("speed") == bestSpeed) {
				// Check bestChannels if it isn't set again (thus still 0) then take first node and return.
				if (bestChannels == 0) {
					return node;
				} else if (node.getIntValue("channels") == bestChannels) {
					return node;
				}
			}
		}

		// We Only Get Here When A 40Kbit OR 80Kbit Stream is found but the Channel Number is != userChannels.
		// Then we return the first 40Kbit OR 80Kbit Stream that is found!
		if ((bestSpeed == 40000) || (bestSpeed == 80000)) {
			debug("getBestRaNode: Requested a 40 or 80Kbit stream with channel setting != node channel, so I give you the one I do have.");
			e = availableRaNodes.elements();
			while (e.hasMoreElements()) {
				MMObjectNode node = (MMObjectNode) e.nextElement();
				if (node.getIntValue("speed") == bestSpeed) {
					return node;
				}
			}
		}

		// Did Something went wrong?
		debug("getBestRaNode: The're Aren't any available RaNodes or something went wrong.");
		return null;
	}

	/**
	 * getSongInfo: Gets the song info for this audiopart/cdtrack number.
	 * @param apNumber An integer which is either an audioparts number or cdtracks number.
	 * @return The song info in a RealFormat compliant String.
	 */
	String getSongInfo(int apNumber) {
		String title  = null; // Set title and author string to init value null.
		String author = null;

		// Get the title info by retrieving the node for this number (which is either an audiopart or a cdtrack!).
		MMObjectNode node = getNode(apNumber); // check this
		if (node != null) {
			title = node.getStringValue("title");
			if (title == null)
				title="";
		} else {
			debug("getSongInfo: ERROR: Cannot get node for audiopart number = "+apNumber);
		}

		// Get the author info by finding the related groups node.
		Enumeration e=mmb.getInsRel().getRelated(node.getIntValue("number"),1573);
		if (e.hasMoreElements()) {
			MMObjectNode groupsNode = (MMObjectNode) e.nextElement();
			author = groupsNode.getStringValue("name");
		}
		if (author == null)
			author="";

		// String songinfo = "?title=\""+title+"\"&author=\""+author+"\"";
		// NOTE: SMIL has problems with double quotes inside <audio src=""/> tags so double quotes are removed.
		// NOTE: No "&" characters are allowed inside a title or author field.
		String songinfo = "?title="+title+"&author="+author;

		debug("getSongInfo: Returning String: "+"\""+songinfo+"\"");
		return songinfo;
	}

	/**
	 * getStartStopTimes: Get the start & stop times for this AudioPart if any were applied.
	 * using the RealFormat "?start=starttime&end=endtime"
	 * @param apNumber An integer containing the AudioPart number.
	 * @return The start&stoptimes in a RealFormat compliant String.
	 */
	String getStartStopTimes(int apNumber) {
		String starttime = null;
		String stoptime  = null;
		String startstoptimes = "";

		MMObjectNode nodestartstop = getNode(apNumber); // check this
		if (nodestartstop != null) {

			// Get related propnodes having fields starttime & stoptime to find out the start&stop times.
			MMObjectNode sStartprop = (MMObjectNode) nodestartstop.getProperty("starttime");
			if (sStartprop != null) {
				String sStartvalue = sStartprop.getStringValue("value");
				starttime = sStartvalue;
			}
			MMObjectNode sStopprop = (MMObjectNode) nodestartstop.getProperty("stoptime");
			if (sStopprop != null) {
				String sStopvalue = sStopprop.getStringValue("value");
				stoptime = sStopvalue;
			}
		} else {
			debug("getStartStopTimes: ERROR: Cannot get node for audiopart number = "+apNumber);
		}
		// debug("getStartStopTimes: starttime = "+starttime+" , stoptime = "+stoptime);

		// NOTE: SMIL has problems with double quotes inside <audio src=""/> tags so double quotes are removed.
		if (starttime != null) startstoptimes += "&start="+starttime;
		if (stoptime  != null) startstoptimes += "&end="+stoptime;

		debug("getStartStopTimes: Returning String: "+"\""+startstoptimes+"\"");
		return startstoptimes;
	}
	public String getAudiopartUrl(MMBase mmbase, scanpage sp, int number, int speed, int channels)
	{
        	return AudioUtils.getAudioUrl( mmbase, sp, number, speed, channels);
	}


	public static void main( String args[] ) {
		String time = "05:04:03:02.01";
		System.out.println("calcTime("+time+") = " + AudioParts.calcTime( time ));	
		time = "04:03:02.01";
		System.out.println("calcTime("+time+") = " + AudioParts.calcTime( time ));	
		time = "03:02";
		System.out.println("calcTime("+time+") = " + AudioParts.calcTime( time ));	
		time = "02.01";
		System.out.println("calcTime("+time+") = " + AudioParts.calcTime( time ));	
		time = "02";
		System.out.println("calcTime("+time+") = " + AudioParts.calcTime( time ));	
	}	
}
