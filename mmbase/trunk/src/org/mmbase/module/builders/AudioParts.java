/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: AudioParts.java,v 1.10 2000-05-19 11:15:42 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.9  2000/05/18 15:10:28  wwwtech
Rico: built in number - text translations of both source/class/storage

Revision 1.8  2000/03/30 13:11:29  wwwtech
Rico: added license

Revision 1.7  2000/03/30 12:42:57  wwwtech
Rico: added warning to these VPRO dependent builders

Revision 1.6  2000/03/29 10:59:21  wwwtech
Rob: Licenses changed

Revision 1.5  2000/03/27 16:10:35  wwwtech
Rico: added more refs in Audio/Video builders

Revision 1.4  2000/03/24 14:33:56  wwwtech
Rico: total recompile

Revision 1.3  2000/02/28 17:13:48  wwwtech
- (marcel) Added getAudiopartUrl()

Revision 1.2  2000/02/24 13:40:03  wwwtech
Davzev activated replace() method and GETURL and fixed GETURL related methods.

*/

/*************************************************************************
 * NOTE This Builder needs significant changes to operate on NON-VPRO
 * machines. Do NOT use before that, also ignore all errors stemming from
 * this builder
 *************************************************************************/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import org.mmbase.module.gui.html.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.module.sessionsInterface;
import org.mmbase.module.sessionInfo;

import nl.vpro.mmbase.util.media.audio.*;
import nl.vpro.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen, David van Zeventer, Rico Jansen
 * @version $Id: AudioParts.java,v 1.10 2000-05-19 11:15:42 wwwtech Exp $
 * 
 */
public class AudioParts extends MMObjectBuilder {

	public final static int AUDIOSOURCE_DEFAULT=0;
	public final static int AUDIOSOURCE_DROPBOX=4;
	public final static int AUDIOSOURCE_UPLOAD=5;
	public final static int AUDIOSOURCE_CD=6;
	public final static int AUDIOSOURCE_JAZZ=7;
	public final static int AUDIOSOURCE_VWM=8;

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

	public Object getValue(MMObjectNode node, String field) {
		if (field.equals("showsource")) {
			return getAudioSourceString( node.getIntValue("source") );
		} else if (field.equals("showclass")) {
			return getAudioClassificationString( node.getIntValue("class") );
		} else {
			return super.getValue( node, field );
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
				case RawAudioDef.STORAGE_STEREO: return("Stereo");
				case RawAudioDef.STORAGE_STEREO_NOBACKUP: return("Stereo no backup");
				case RawAudioDef.STORAGE_MONO: return("Mono");
				case RawAudioDef.STORAGE_MONO_NOBACKUP: return("Mono no backup");
				default: return("Unknown");
			}
		} else if (field.equals("source")) {
			return(getAudioSourceString(node.getIntValue("source")));
			
		} else if (field.equals("class")) {
			return(getAudioClassificationString(node.getIntValue("class")));
		}
		return(null);
	}

	private String getAudioClassificationString(int classification) {
		String rtn="";

		switch(classification) {
			case 0:
				rtn="";					// Default
				break;
			case 1:
				rtn="Track";			// Recording of a studio track
				break;
			case 2:
				rtn="Studio Session";	// Recording of a live session in a Studio
				break;
			case 3:
				rtn="Live Recording";	// Recording of a live performance
				break;
			case 4:
				rtn="DJ Set";			// Recording of a DJ-set
				break;
			case 5:
				rtn="Remix";			// Remixed by
				break;
			case 6:
				rtn="Interview";		// Interview of
				break;
			case 7:
				rtn="Report";			// Report of
				break;
			case 8:
				rtn="Jingle";			// Jingle
				break;
			case 9:
				rtn="Program";			// Broadcast of a program
				break;
			default:
				rtn="Unknown";			// Unknown
				break;
		}
		return(rtn);
	}

	private String getAudioSourceString(int source) {
		String rtn="";

		switch(source) {
			case AUDIOSOURCE_DEFAULT:
				rtn="default";
				break;
			case AUDIOSOURCE_DROPBOX:
				rtn="dropbox";
				break;
			case AUDIOSOURCE_UPLOAD:
				rtn="upload";
				break;
			case AUDIOSOURCE_CD:
				rtn="cd";
				break;
			case AUDIOSOURCE_JAZZ:
				rtn="jazz";
				break;
			case AUDIOSOURCE_VWM:
				break;
			default:
				rtn="unknown";
				break;
		}
		return(rtn);
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

	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		node.setValue("storage",RawAudioDef.STORAGE_STEREO_NOBACKUP);
		node.setValue("body","");
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




	/*
		------------------------------------------------------------
		Duplicate code, should move to mediautils and be generalized
		------------------------------------------------------------
	*/

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
	 * @param apNumber An integer which is either an audiopart number or cdtracks number.
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
		Enumeration e=mmb.getInsRel().getRelated(node.getIntValue("number"),"groups");
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

	/*
		------------------------------------------------------------
	*/




	public String getAudiopartUrl(MMBase mmbase, scanpage sp, int number, int speed, int channels)
	{
        	return AudioUtils.getAudioUrl( mmbase, sp, number, speed, channels);
	}


	/*
		Time stuff should be in util class
	*/

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



	/*
		Property stuff should either be easier or moved to MMObjectNode
	*/

	private String getProperty( MMObjectNode node, String key ) {
		String result = null;

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
		return result;
	}

	private void putProperty( MMObjectNode node, String key, String value ) {
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
					MMObjectNode snode = properties.getNewNode ("audiopart");
   		             snode.setValue ("ptype","string");
   		             snode.setValue ("parent",id);
   		             snode.setValue ("key",key);
   		             snode.setValue ("value",value);
   		             int id2=properties.insert("audiopart", snode); // insert db
   		             snode.setValue("number",id2);
   		             node.putProperty(snode); // insert property into node
				}
			}
		} else {
			debug("putProperty("+"null"+","+key+","+value+"): ERROR: Node is null!");
		}
	}

	private void removeProperty( MMObjectNode node, String key ) {
		if ( node != null ) {
        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) 
				pnode.parent.removeNode( pnode );
			else
				debug("removeNode("+node+","+key+"): ERROR: Property not found( and cannot remove )");
		}
	}


	/*
		Test
	*/
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
