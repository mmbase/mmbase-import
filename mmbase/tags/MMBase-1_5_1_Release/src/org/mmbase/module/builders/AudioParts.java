/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders;

import java.util.StringTokenizer;

import org.mmbase.module.core.*;

import org.mmbase.module.gui.html.EditState;
import org.mmbase.util.scanpage;

import org.mmbase.util.media.audio.RawAudioDef;
import org.mmbase.util.media.audio.AudioUtils;

import org.mmbase.util.logging.*;

/**
 * NOTE This Builder needs significant changes to operate on NON-VPRO
 * machines. Do NOT use before that, also ignore all errors stemming from
 * this builder
 *
 * @javadoc
 * @author Daniel Ockeloen
 * @author David van Zeventer
 * @author Rico Jansen
 * @version $Id: AudioParts.java,v 1.27 2002-01-28 13:26:20 pierre Exp $
 */
public class AudioParts extends MediaParts {
    private static Logger log = Logging.getLoggerInstance(AudioParts.class.getName());

    public final static int AUDIOSOURCE_DEFAULT=0;
    public final static int AUDIOSOURCE_DROPBOX=4;
    public final static int AUDIOSOURCE_UPLOAD=5;
    public final static int AUDIOSOURCE_CD=6;
    public final static int AUDIOSOURCE_JAZZ=7;
    public final static int AUDIOSOURCE_VWM=8;

    /**
     * Just before commiting or inserting this method is called and
     * only when a node was changed (commit) we update the start
     * and stoptimes.
     * (start & stoptimes of newly inserted nodes are inserted by insertDone).
     * @dependency EditState (SCAN code)
     * @param ed the editstate
     * @param node the audiopart node that has just been inserted.
     * @return -1
     */
    public int preEdit(EditState ed, MMObjectNode node) {
        if (node==null)
            log.error("node is null!");
        else
            if (node.getIntValue("number")!=-1) {
                log.debug("Updating start & stop times for "+node.getIntValue("number"));
                handleTimes(ed,node);
            }
        return -1;
    }

    /**
     * When an audiopart is inserted, the start & stop times still need to be
     * saved.(start & stop are not real fields, these were later)
     * @param ed the editstate
     * @param node the audiopart node that has just been inserted.
     * @return -1
     */
    public int insertDone(EditState ed, MMObjectNode node) {
        log.debug("Inserting start & stop times for "+node.getIntValue("number"));
        handleTimes(ed,node);
        return -1;
    }

    /**
     * We save start and stoptime values in property objects where the
     * keys are 'starttime' and 'stoptime' and values the start & stoptimes.
     * @dependency EditState (SCAN code)
     * @param ed the editstate
     * @param node the audiopart node that has just been inserted.
     */
    private void handleTimes(EditState ed, MMObjectNode node) {

        if (node != null) {
            String starttime = ed.getHtmlValue("starttime");
            String stoptime  = ed.getHtmlValue("stoptime");

            log.debug(node.getName()+" ("+node.getIntValue("number")+") starttime: "+starttime);
            log.debug(node.getName()+" ("+node.getIntValue("number")+") stoptime : "+stoptime);

            // check if (stop - start) == lengthOfPart, if lengthOfPart != -1

            // Check the starttime value
            if (checktime(starttime))
                putProperty(node, "starttime", starttime);
            else
                removeProperty(node, "starttime");

            // Check the stoptime value
            if (checktime(stoptime))
                putProperty(node, "stoptime", stoptime);
            else
                removeProperty(node, "stoptime");
        } else {
            log.error("Node is null!");
        }
    }

    /**
     * @javadoc
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals("showsource")) {
            return getAudioSourceString( node.getIntValue("source") );
        } else if (field.equals("showclass")) {
            return getAudioClassificationString( node.getIntValue("class") );
        } else {
            return super.getValue( node, field );
        }
    }

    /**
     * @javadoc
     */
    public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("title");
        return(str);
    }

    /**
     * @javadoc
     */
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

    /**
     * @javadoc
     */
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

    /**
     * @javadoc
     */
    protected String getAudioSourceString(int source) {
        log.debug("Getting audio source from ORG, source:"+source);
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
                rtn="vwm";
                break;
            default:
                rtn="unknown";
                break;
        }
        return(rtn);
    }

    /**
     * @javadoc
     */
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
     * @javadoc
     * @deprecated Time stuff should be in util class
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
                    log.error("calcTime("+time+"): Cannot convert pos("+(total-i)+") to a number("+tt+")!" + e.toString());
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
                    log.error("calctime("+time+"): Cannot convert s1("+s1+") or s2("+s2+")!");
                }
            }
            result = r;

        } else {
            // time is secs
            try {
                r = Integer.parseInt( time );
                result = r * 100;
            } catch( NumberFormatException e ) {
                log.error("calctime("+time+"): Cannot convert time("+time+")!");
            }
        }

        return result;
    }

    /**
     * checktime( time ) <br />
     * time = dd:hh:mm:ss.ss <br />
     * Checks whether part is valid, each part (dd/hh/mm/ss/ss) are numbers, higher than 0, lower than 100
     * If true, time can be inserted in DB.
     */
    private boolean checktime(String time) {
        if (time==null || time.equals("")) {
            log.error("Timevalue ("+time+") invalid");
            return false;
        }

        StringTokenizer tok = new StringTokenizer(time, ":.");
        while(tok.hasMoreTokens()) {
            if (!checktimeint(tok.nextToken()))
                return false;
        }
        return true;
    }

    /**
     * @javadoc
     */
    private boolean checktimeint( String time ) {
        boolean result = false;

        try {
            int t = Integer.parseInt( time );
            if (t >= 0) {
                if( t < 100 ) {
                    result = true;
                } else {
                    log.error("checktimeint("+time+"): ERROR: this part is higher than 100!");
                    result = false;
                }
            } else {
                log.error("checktimeint("+time+"): ERROR: Time is negative!");
                result = false;
            }

        } catch( NumberFormatException e ) {
            log.error("checktimeint("+time+"): ERROR: Time is not a number!");
            result = false;
        }
        return result;
    }

    /**
     * @deprecated Property stuff should either be easier or moved to MMObjectNode
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
                log.error("getProperty("+node.getName()+","+key+"): ERROR: No prop found for this item("+id+")!");
            }
        } else {
            log.error("getProperty("+"null"+","+key+"): ERROR: Node is null!");
        }
        return result;
    }

    /**
     * @deprecated Property stuff should either be easier or moved to MMObjectNode
     */
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
            log.error("putProperty("+"null"+","+key+","+value+"): ERROR: Node is null!");
        }
    }

    /**
     * @deprecated Property stuff should either be easier or moved to MMObjectNode
     */
    private void removeProperty( MMObjectNode node, String key ) {
        if ( node != null ) {
            MMObjectNode pnode=node.getProperty(key);
            if (pnode!=null)
                pnode.parent.removeNode( pnode );
        }
    }

    /**
     * Calls the get url method for audioparts.
     * @dependency scanpage (SCAN)
     * @param sp the scanpage
     * @param number the audiopart object number
     * @param speed the user speed value
     * @param channels the user channels value
     * @return a String with url to a audiopart.
     */
    public String doGetUrl(scanpage sp,int number,int userSpeed,int userChannels){
        return getAudiopartUrl(mmb,sp,number,userSpeed,userChannels);
    }

    /**
     * Gets the url for a audiopart using the mediautil classes.
     * @dependency scanpage (SCAN)
     * @param mmbase mmbase reference
     * @param sp the scanpage
     * @param number the audiopart object number
     * @param speed the user speed value
     * @param channels the user channels value
     * @return a String with url to a audiopart.
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

    /**
     * Test
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
