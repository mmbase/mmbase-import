/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: AudioParts.java,v 1.19 2000-12-14 16:22:15 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.18  2000/12/14 15:53:39  vpro
davzev: Removed replace() and related GETURL methods, now available in mediaparts, and added methods getMinSpeed,getMinChannels and doGetUrl.

Revision 1.17  2000/11/10 10:31:32  vpro
davzev: Added method makeRealCompatible that checks for Realplayer incompatible chars in title and author field plus url rtsp-pnm check uses charAt(0) instead of startsWith.

Revision 1.16  2000/10/05 12:14:14  vpro
Rico: removed limit on getGUIIndicator

Revision 1.15  2000/08/01 09:49:40  install
changed import

Revision 1.14  2000/07/31 13:32:16  vpro
davzev: Made urlCache variable static.

Revision 1.13  2000/07/03 09:32:47  vpro
davzev: Added url cache on top of $ MOD GETURL for performance reasons. When an url is retrieved using $ MOD-MMBASE-BUILDER-audioparts-GETURL-AudiopartNr-Speed-Channels, then first it will be looked up in a url cache (LRU). If its not in there it will be retrieved using the doGetUrl method and put in the cache. If an audiopart is not fully encoded yet, then it will not be put in the cache and null will be returned.

Revision 1.12  2000/05/26 12:09:28  wwwtech
davzev: Reduced debug from doGetUrl getSongInfo and getStartStopTimes

Revision 1.11  2000/05/22 13:21:21  wwwtech
Rico: removed cdtrack references

Revision 1.10  2000/05/19 11:15:42  wwwtech
Rico: fixed package name

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

import org.mmbase.util.media.*;
import org.mmbase.util.media.audio.*;
import org.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen, David van Zeventer, Rico Jansen
 * @version $Id: AudioParts.java,v 1.19 2000-12-14 16:22:15 vpro Exp $
 * 
 */
public class AudioParts extends MediaParts {


	public final static int AUDIOSOURCE_DEFAULT=0;
	public final static int AUDIOSOURCE_DROPBOX=4;
	public final static int AUDIOSOURCE_UPLOAD=5;
	public final static int AUDIOSOURCE_CD=6;
	public final static int AUDIOSOURCE_JAZZ=7;
	public final static int AUDIOSOURCE_VWM=8;

	// Define LRU Cache for audio urls.
	//moved to mediaparts public static LRUHashtable urlCache = new LRUHashtable(1024);

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
		return(str);
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

	
	/**
	 * Calls the get url method for audioparts.
	 * @param sp the scanpage
	 * @param number the videopart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @return a String with url to a videopart.
	 */
	public String doGetUrl(scanpage sp,int number,int userSpeed,int userChannels){
		return getAudiopartUrl(mmb,sp,number,userSpeed,userChannels);
	}
	
	/**
	 * Gets the url for a audiopart using the mediautil classes.
	 * @param mmbase mmbase reference
	 * @param sp the scanpage
	 * @param number the videopart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @return a String with url to a videopart.
	 */
	public String getAudiopartUrl(MMBase mmbase,scanpage sp,int number,int speed,int channels){
        return AudioUtils.getAudioUrl(mmbase,sp,number,speed,channels);
	}

	/**
	 * Gets minimal speed setting from audioutils
	 * @return minimal speed setting
	 */
	public int getMinSpeed() {
		return RawAudioDef.MINSPEED;
	}
	/**
	 * Gets minimal channel setting from audioutil
	 * @return minimal channel setting
	 */
	public int getMinChannels() {
		return RawAudioDef.MINCHANNELS;
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
