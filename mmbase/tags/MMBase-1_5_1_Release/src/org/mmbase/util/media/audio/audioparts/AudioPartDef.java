/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.media.audio.audioparts;

import java.util.Vector;

import org.mmbase.module.core.*;

import org.mmbase.util.scanpage;
import org.mmbase.util.media.MediaUtils;
import org.mmbase.util.media.audio.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author    vpro
 * @version   $Id: AudioPartDef.java,v 1.7 2002-01-28 16:35:02 pierre Exp $
 */

public class AudioPartDef {

    // logging
    private static Logger log    = Logging.getLoggerInstance(AudioPartDef.class.getName());

    /**
     * @javadoc
     * @scope private
     */
    public int number;

    /**
     * @javadoc
     * @scope private
     * @deprecated not used, implied by number
     */
    public int otype;

    /**
     * @javadoc
     * @scope private
     * @deprecated not used, implied by number
     */
    public String owner;

    /**
     * @javadoc
     * @scope private
     */
    public String title;

    /**
     * @javadoc
     * @scope private
     */
    public String subtitle;

    /**
     * @javadoc
     * @scope private
     */
    public int source;

    /**
     * @javadoc
     * @scope private
     */
    public int playtime;

    /**
     * @javadoc
     * @scope private
     */
    public String intro;

    /**
     * @javadoc
     */
    public String body;

    /**
     * @javadoc
     * @scope private
     */
    public int storage;

    /**
     * @javadoc
     */
    public String starttime;

    /**
     * @javadoc
     * @scope private
     */
    public String stoptime;

    /**
     * all the rawaudios connected to this node
     * @scope private
     */
    public Vector rawaudios;
    /**
     * the best for the user in speed/channels.
     * @scope private
     */
    public RawAudioDef rawaudio;

    public AudioPartDef() {
    }

    /**
     * @javadoc
     */
    public AudioPartDef(MMBase mmbase, int number) {
        if (!setparameters(mmbase, number)) {
            log.error("AudioPartDef(" + mmbase + "," + number + "): Not initialised, something went wrong");
        }
    }

    /**
     * @javadoc
     */
    public AudioPartDef(MMBase mmbase, MMObjectNode node) {
        if (!setparameters(mmbase, node)) {
            log.error("AudioPartDef(" + node + "): Not initialised, something went wrong!");
        }
    }

    /**
     * @javadoc
     */
    public boolean setparameters(MMBase mmbase, int number) {
        boolean result  = false;
        if (mmbase != null) {
            if (number > 0) {
                MMObjectBuilder builder  = mmbase.getMMObject("audioparts");
                if (builder != null) {
                    MMObjectNode node  = builder.getNode(number);
                    if (node != null) {
                        result = setparameters(mmbase, node);
                    } else {
                        log.error("AudioPartDef(" + mmbase + "," + number + "): No node found for this number!");
                    }
                } else {
                    log.error("AudioPartDef(" + mmbase + "," + number + "): no builder(audioparts) found in mmbase!");
                }
            } else {
                log.error("AudioPartDef(" + mmbase + "," + number + "): Number is not greater than 0!");
            }
        } else {
            log.error("AudioPartDef(" + mmbase + "," + number + "): mmbase not initialised!");
        }

        return result;
    }


    /**
     * @javadoc
     * @performance why are fields stored as properties, rather than the node itself?
     */
    public boolean setparameters(MMBase mmbase, MMObjectNode node) {
        boolean result  = false;
        if (node != null) {
            number = node.getIntValue("number");
            otype = node.getIntValue("otype");
            owner = node.getStringValue("owner");

            title = node.getStringValue("title");
            subtitle = node.getStringValue("subtitle");
            source = node.getIntValue("source");
            playtime = node.getIntValue("playtime");
            intro = node.getStringValue("intro");
            body = node.getStringValue("body");
            storage = node.getIntValue("storage");

            getStartStop(mmbase);

            result = true;
        } else {
            log.error("AudioPartsDef(" + node + "): Node is null!");
        }

        return result;
    }


    /**
     * @javadoc
     */
    public boolean getRawAudios(MMBase mmbase, int wantedspeed, int wantedchannels, boolean sorted) {
        boolean result  = false;

        rawaudios = AudioUtils.getRawAudios(mmbase, number, sorted);
        if (rawaudios != null) {
            rawaudio = AudioUtils.getBestRawAudio(rawaudios, wantedspeed, wantedchannels);
            if (rawaudio != null) {
                result = true;
            } else {
                log.warn("getRawAudios(" + number + "," + wantedspeed + "," + wantedchannels + "," + sorted + "): No best rawaudio found for this speed/channels!");
            }
        } else {
            log.warn("getRawAudios(" + number + "," + wantedspeed + "," + wantedchannels + "," + sorted + "): No rawaudios found for this node!");
        }

        return result;
    }


    /**
     * @javadoc
     */
    public void getStartStop(MMBase mmbase) {
        if (mmbase != null) {
            if (number > 0) {
                MMObjectBuilder builder  = mmbase.getMMObject("properties");
                if (builder != null) {
                    MMObjectNode node  = builder.getNode(number);
                    if (node != null) {
                        MMObjectNode start  = (MMObjectNode)node.getProperty("starttime");
                        MMObjectNode stop   = (MMObjectNode)node.getProperty("stoptime");

                        if (start != null) {
                            starttime = start.getStringValue("value");
                        }
                        if (stop != null) {
                            stoptime = stop.getStringValue("value");
                        }
                    }
                } else {
                    log.warn("getStartStop(): builder(properties(" + builder + ")) not found!");
                }
            } else {
                log.warn("getStartStop(): number(" + number + ") not valid!");
            }
        } else {
            log.warn("getStartStop(" + mmbase + "): mmbase not valid!");
        }
    }


    /**
     * Gets the Realaudio url and ads the 'title','start' and 'end' name and values
     * parameters. davzev: Removed the double quotes around the values since Real SMIL
     * doesn't handle it correctly.
     * @deprecation-used contains commented-out code
     * @dependency scanpage (SCAN)
     * @param sp  the scanpage
     * @return    a String with the url.
     */
    public String getRealAudioUrl(scanpage sp) {
        String result  = null;
        result = rawaudio.getRealAudioUrl(sp);

        if (result != null) {

            if (title != null && !title.equals("")) {
                //result += "?title=\""+title+"\"";
                //Our realplayer can't handle url encoded string.
                //result += "?title="+MediaUtils.plusToProcent20(URLEncoder.encode(title)); //URLEncode value.
                title = MediaUtils.makeRealCompatible(title);
                result += "?title=" + title;
            } else {
                //result += "?title=\"\"";
                result += "?title=";
            }

            String ss  = null;
            if (starttime != null && !starttime.equals("")) {
                //ss = "&start=\""+starttime+"\"";
                ss = "&start=" + starttime;
            }

            if (stoptime != null && !stoptime.equals("")) {
                if (ss != null) {
                    //ss += "&end=\""+stoptime+"\"";
                    ss += "&end=" + stoptime;
                } else {
                    //ss = "&end=\""+stoptime+"\"";
                    ss = "&end=" + stoptime;
                }
            }

            if (ss != null && !ss.equals("")) {
                result += ss;
            }
        }
        return result;
    }


    /**
     * @javadoc
     */
    public String toText() {
        String classname  = this.getClass().getName();
        StringBuffer b    = new StringBuffer();
        b.append(classname + ":" + "number(" + number + ")\n");
        b.append(classname + ":" + "otype(" + otype + ")\n");
        b.append(classname + ":" + "owner(" + owner + ")\n");

        b.append(classname + ":" + "title(" + title + ")\n");
        b.append(classname + ":" + "subtitle(" + subtitle + ")\n");
        b.append(classname + ":" + "source(" + source + ")\n");
        b.append(classname + ":" + "playtime(" + playtime + ")\n");
        b.append(classname + ":" + "intro(" + intro + ")\n");
        b.append(classname + ":" + "body(" + body + ")\n");
        b.append(classname + ":" + "storage(" + storage + ")\n");
        b.append(classname + ":" + "starttime(" + starttime + ")\n");
        b.append(classname + ":" + "stoptime(" + stoptime + ")\n");
        b.append(classname + ":" + "rawaudios found : " + rawaudios.size() + "\n");
        b.append(classname + ":" + "best rawaudio   : " + rawaudio.toString() + "\n");
        return b.toString();
    }


    /**
     * @javadoc
     */
    public String toString() {
        return this.getClass().getName() + "( number(" + number + ") otype(" + otype + ") owner(" + owner + ") title(" + title + ") subtitle(" + subtitle + ") source(" + source + ") playtime(" + playtime + ") intro(" + intro + ") body(" + body + ") storage(" + storage + "), starttime(" + starttime + "), stoptime(" + stoptime + "), rawaudios(" + rawaudios.size() + "), rawaudio(" + rawaudio.toString() + ")";
    }
}

