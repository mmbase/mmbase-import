/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
/*
$Id: VideoParts.java,v 1.15 2001-04-10 12:20:39 michiel Exp $

$Log: not supported by cvs2svn $
Revision 1.14  2000/12/14 16:22:16  vpro
davzev: AudioParts and VideoParts now extend from MediaParts

Revision 1.13  2000/12/14 15:52:04  vpro
davzev: Added methods getMinSpeed,getMinChannels and doGetUrl.

Revision 1.12  2000/11/23 14:42:46  vpro
Wilbert: Added cvs log and id, removed unused fields diskid and playtime, unnesc overrides of getGuiIndicator, constructor and getNewNode

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

import org.mmbase.util.media.video.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Daniel Ockeloen
 * @version $Id: VideoParts.java,v 1.15 2001-04-10 12:20:39 michiel Exp $
 */
public class VideoParts extends MediaParts {

    private static Logger log = Logging.getLoggerInstance(VideoParts.class.getName()); 

	public int insertDone(EditState ed,MMObjectNode node) {
		String sourcepath=ed.getHtmlValue("sourcepath");
        int devtype=node.getIntValue("source");
		int id=node.getIntValue("number");
		String devname = null;

		if(devtype==7) {		//Check if source is from a jazzdrive -> 7
			log.info("jazzdrives aren't supported at this moment");
			/* See Audioparts for comment 
           	//sourcepath contains  eg. /Drivename/Dir/File
           	String delim = "/";
           	StringTokenizer tok = new StringTokenizer(sourcepath,delim);     //Retrieve devname
           	if(tok.hasMoreTokens()) {
         		devname = tok.nextToken();
         	} else {
          	 	System.out.println("VideoParts: insertDone: srcfile cannot be tokenized using symbol "+delim);
           	 	System.out.println("VideoParts: insertDone: insertDone will fail");
			}
           	jazzdrives bul=(jazzdrives)mmb.getMMObject("jazzdrives");
			Enumeration e=bul.search("WHERE name='"+devname+"'");
       		if (e.hasMoreElements()) {
             	MMObjectNode jnode=(MMObjectNode)e.nextElement();
             	jnode.setValue("state","copy");
             	jnode.setValue("info","srcfile="+sourcepath+" id="+id);
             	jnode.commit();
       		}
			*/
		} else if (devtype==4 || devtype==5) {	//Check if source is from a import/
			if (sourcepath!=null) {
				System.out.println ("VideoParts.insertDone -> sourcepath = " + sourcepath);
				System.out.println ("VideoParts.insertDone -> number = " + id);
				File newfile=new File("/data/video/mov/"+id+".wav");
				// With the new editor-interface (pulldowns), the full pathname
				// will be provided (so including the leading '/data/import/')
				//File curfile=new File("/data/import/"+t);
				File curfile = new File (sourcepath);
				if (curfile.exists()) {
					if (curfile.renameTo(newfile)==false) {
						System.out.println("VideoParts -> Can't rename wav file : " + sourcepath);
					} else {
						int st=node.getIntValue("storage"); 
						RawVideos bul=(RawVideos)mmb.getMMObject("rawvideos");
						if (st==1 || st==2) {
							addRawVideo(bul,id,3,3,441000,2);   
						} else if (st==3 || st==4) {
							addRawVideo(bul,id,3,3,441000,1);   
						}
						movAvailable(""+id);
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
	public int preEdit(EditState ed, MMObjectNode node) 
	{
		//debug("preEdit(): start");
		if ( node != null )
		{
			String starttime = ed.getHtmlValue("starttime");
			String stoptime  = ed.getHtmlValue("stoptime");
	
            if (log.isDebugEnabled()) {
                log.debug("preEdit(" + node.getName() + "):starttime(" + starttime + ")");
                log.debug("preEdit(" + node.getName() + "): stoptime(" + stoptime + ")");
            }

			// check if (stop - start) == lengthOfPart, if lengthOfPart != -1

			// startstop
			if( starttime != null )
			{
				// is it valid ?
				// -------------

				if (checktime(starttime))
					putProperty( node, "starttime", starttime);
				else
				{
					// no, maybe we have to remove it (when its empty or '-1')
					// -------------------------------------------------------

					if (starttime.equals("") || starttime.equals("-1")) {
						removeProperty( node, "starttime" );
					} else {
						log.error("preEdit(" + node + "," + starttime + "): Dont know what to do with this starttime for this node!");
                    }
				}
			}
			else {
				// error ? daniel	putProperty( node, "starttime", "-1");
			}

			if ( stoptime != null )
			{
				// check if its a valid time
				// -------------------------

				if(checktime(stoptime))
					putProperty( node, "stoptime" , stoptime);
				else
				{
					// not a valid time, maybe we have tot remove this property
					// --------------------------------------------------------

					if(stoptime.equals("") || stoptime.equals("-1")) {
						removeProperty(node, "stoptime");	
					} else {
						log.error("preEdit("+node+","+stoptime+"): Dont know what to do this this stoptime for this node!");
                    }
				}
			}
			else {
				// error ? daniel	putProperty( node, "stoptime" , "-1");
			}
		} else {
			log.error("preEdit(): node is null!");
        }

		return(-1);	
	}

	public static long calcTime( String time )
	{
		long result = -1;

		long r 		= 0;

		int calcList[] 	= new int[5];
		calcList[0]		= 0;
		calcList[1] 	= 100; 	// secs 
		calcList[2] 	= 60;	// min 
		calcList[3] 	= 60;	// hour 
		calcList[4] 	= 24;	// day

		if (time.indexOf(".")!=-1 || time.indexOf(":") != -1)
		{
			int day 	= -1;
			int hour 	= -1;
			int min		= -1;
			int	sec		= -1;
			StringTokenizer tok = new StringTokenizer( time, ":" );
			if (tok.hasMoreTokens())
			{	
				int i 		= 0;
				int	total 	= tok.countTokens();
				int mulfac, t;

				String tt	= null;
				try
				{
					int ttt = 0;
					int tttt = 0;

					while(tok.hasMoreTokens())
					{
						tt 		= tok.nextToken();
						tttt	= 0;

						if (tt.indexOf(".")==-1)
						{
							tttt	= total - i;
							t 		= Integer.parseInt( tt );
							int tot		= t;
	
							while (tttt != 0)
							{
								mulfac 	 = calcList[ tttt ];
								tot 	 = mulfac * tot;	
								tttt--;
							}
							r += tot;
							i++;
						}
					}
				}
				catch( NumberFormatException e )
				{
					System.out.println("calcTime("+time+"): ERROR: Cannot convert pos("+(total-i)+") to a number("+tt+")!" + e.toString());
				}
			}

			if (time.indexOf(".") != -1)
			{
				// time is secs.msecs

				int index = time.indexOf(":");	
				while(index != -1)
				{
					time = time.substring( index+1 );
					index = time.indexOf(":");
				}
	
				index = time.indexOf(".");
				String 	s1 = time.substring( 0, index );
				String	s2 = time.substring( index +1 );

				try
				{
					int t1 = Integer.parseInt( s1 );
					int t2 = Integer.parseInt( s2 );
		
					r += (t1*100) + t2; 
				}
				catch( NumberFormatException e )
				{
					System.out.println("calctime("+time+"): ERROR: Cannot convert s1("+s1+") or s2("+s2+")!");
				} 
			}

			result = r;
		}	
		else
		{
			// time is secs
			try
			{
				r = Integer.parseInt( time );
				result = r * 100;
			}
			catch( NumberFormatException e )
			{
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
	private boolean checktime( String time )
	{
		boolean result = true;
		
		if (time!=null && !time.equals(""))
		{
			StringTokenizer tok = new StringTokenizer( time, ":." );
			while( tok.hasMoreTokens() )
				if (!checktimeint(tok.nextToken()))
				{
					result = false;
					break;
				}
		}
		else {
			log.error("checktime("+time+"): Time is not valid!");
        }
		
		//debug("checktime("+time+"): simpleTimeCheck(" + result+")");
		return result;
	}

	private boolean checktimeint( String time )
	{
		boolean result = false;

		try
		{
			int t = Integer.parseInt( time );
			if (t >= 0)
			{
				if( t < 100 )
					result = true;
				else
				{
					log.error("checktimeint(" + time + "): this part is higher than 100!"); 
					result = false;
				}
			}
			else
			{
				log.error("checktimeint(" + time + "): Time is negative!");
				result = false;
			}
		}
		catch( NumberFormatException e )
		{
			log.error("checktimeint(" + time + "): Time is not a number!");
			result = false;
		}

		//debug("checktimeint("+time+"): " + result);	
		return result;
	}

	private String getProperty( MMObjectNode node, String key )
	{
		String result = null;

		//debug("getProperty("+key+"): start");

		int id = -1;
		if( node != null )
		{
			id = node.getIntValue("number");
			
			MMObjectNode pnode = node.getProperty( key );
			if( pnode != null )
			{
				result = pnode.getStringValue( "value" );
			}
			else
			{
				log.error("getProperty(" + node.getName() + "," + key + "): No prop found for this item(" + id + ")!");
			}
		}
		else {
			log.error("getProperty(" + "null" + "," + key + "): Node is null!");
        }

		//debug("getProperty("+key+"): end("+result+")");
		return result;
	}

	private void putProperty( MMObjectNode node, String key, String value )
	{
		//debug("putProperty("+key+","+value+"): start");
		int id = -1;
		if ( node != null )
		{
			id = node.getIntValue("number");

        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) 
			{
				if (value.equals("") || value.equals("null") || value.equals("-1"))
				{
					// remove
					pnode.parent.removeNode( pnode );

				} else {
					// insert
            		pnode.setValue("value",value);
                	pnode.commit();
				}
            } 
			else 
			{
				if ( value.equals("") || value.equals("null") || value.equals("-1") )
				{
					// do nothing
				}
				else
				{
					// insert
					MMObjectBuilder properties = mmb.getMMObject("properties");
					MMObjectNode snode = properties.getNewNode ("videoparts");
					 //snode.setValue ("otype", 9712);
   		             snode.setValue ("ptype","string");
   		             snode.setValue ("parent",id);
   		             snode.setValue ("key",key);
   		             snode.setValue ("value",value);
   		             int id2=properties.insert("videoparts", snode); // insert db
   		             snode.setValue("number",id2);
   		             node.putProperty(snode); // insert hash
				}
			}
		}
		else {
			log.error("putProperty(" + "null" + "," + key + "," + value + "): Node is null!");
        }

		//debug("putProperty("+key+","+value+"): end");
	}

	private void removeProperty( MMObjectNode node, String key )
	{
		//debug("removeProperty("+key+","+value+"): start");
		if ( node != null )
		{
        	MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null) 
				pnode.parent.removeNode( pnode );
			else
				log.error("removeNode(" + node + "," + key + "): Property not found( and cannot remove )");
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

	public void movAvailable(String id) {
		MMObjectNode node=getNode(id);
		int st=node.getIntValue("storage"); 
		if (st!=0) {
			System.out.println("VideoParts -> Store command on "+id+" = "+st);
			RawVideos bul=(RawVideos)mmb.getMMObject("rawvideos");
			if (bul!=null) {
				if (st==1 || st==2) { 
					try {
						int idi=Integer.parseInt(id);
						addRawVideo(bul,idi,1,2,20000,1);   
						addRawVideo(bul,idi,1,2,32000,1);   
						addRawVideo(bul,idi,1,2,45000,1);   
						addRawVideo(bul,idi,1,2,45000,2);   
						addRawVideo(bul,idi,1,2,80000,2);   
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("VideoParts -> Wrong id in ParseInt");
					}
				}
				if (st==3 || st==4) { 
					try {
						int idi=Integer.parseInt(id);
						addRawVideo(bul,idi,1,2,20000,1);   
						addRawVideo(bul,idi,1,2,32000,1);   
						addRawVideo(bul,idi,1,2,45000,1);   
						addRawVideo(bul,idi,1,2,80000,1);   
					} catch (Exception e) {
						System.out.println("VideoParts -> Wrong id in ParseInt");
					}
				}
			}
		}
	}

	public void addRawVideo(RawVideos bul,int id, int status, int format, int speed, int channels) {
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
			System.out.println("VideoParts -> Store command on "+id+" = "+st);
			RawVideos bul=(RawVideos)mmb.getMMObject("rawvideos");
			if (bul!=null) {
				if (st==1) {
					try {
						int idi=Integer.parseInt(id);
						addRawVideo(bul,idi,1,5,192000,2);   
					} catch (Exception e) {
						System.out.println("VideoParts -> Wrong id in ParseInt");
					}
				}

				if (st==2) {
					try {
						int idi=Integer.parseInt(id);
						addRawVideo(bul,idi,1,2,20000,1);   
						addRawVideo(bul,idi,1,2,20000,2);   
						addRawVideo(bul,idi,1,2,32000,1);   
						addRawVideo(bul,idi,1,2,32000,2);   
						addRawVideo(bul,idi,1,2,45000,1);   
						addRawVideo(bul,idi,1,2,45000,2);   
						addRawVideo(bul,idi,1,2,80000,1);   
						addRawVideo(bul,idi,1,2,80000,2);   
					} catch (Exception e) {
						System.out.println("VideoParts -> Wrong id in ParseInt");
					}
				}

				if (st==3) {
					try {
						int idi=Integer.parseInt(id);
						addRawVideo(bul,idi,1,2,20000,1);   
						addRawVideo(bul,idi,1,2,32000,1);   
						addRawVideo(bul,idi,1,2,45000,1);   
						addRawVideo(bul,idi,1,2,80000,1);   
					} catch (Exception e) {
						System.out.println("VideoParts -> Wrong id in ParseInt");
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
	 * Calls the get url method for videoparts.
	 * @param sp the scanpage
	 * @param number the videopart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @return a String with url to a videopart.
	 */
	public String doGetUrl(scanpage sp,int number,int userSpeed,int userChannels) {
		return getVideopartUrl(mmb,sp,number,userSpeed,userChannels);
	}
	
	/**
	 * Gets the url for a videopart using the mediautil classes.
	 * @param mmbase mmbase reference
	 * @param sp the scanpage
	 * @param number the videopart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @return a String with url to a videopart.
	 */
	public String getVideopartUrl(MMBase mmbase,scanpage sp,int number,int speed,int channels){
        return VideoUtils.getVideoUrl(mmbase,sp,number,speed,channels);
	}

	/**
	 * Gets minimal speed setting from videoutil
	 * @return minimal speed setting
	 */
	public int getMinSpeed() {
		return RawVideoDef.MINSPEED;
	}
	/**
	 * Gets minimal channel setting from videoutil
	 * @return minimal channel setting
	 */
	public int getMinChannels() {
		return RawVideoDef.MINCHANNELS;
	}
	
	public static void main( String args[] )
	{
		String time = "05:04:03:02.01";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "04:03:02.01";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "03:02";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "02.01";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
		time = "02";
		System.out.println("calcTime("+time+") = " + VideoParts.calcTime( time ));	
	}	
}
