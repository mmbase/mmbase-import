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
 * This is a wrapper class for the AudioPart object.
 * It is used to provide member functions for selecting, for a wrapped audiopart,
 * the best available rawaudio (or rather, rawaudio wrapper), and then requesting
 * the best  url for that rawaudio (using the best mirror site available).
 *
 * @deprecated Most of the actual functionality for these functions resides in
 * AudioUtils, which is alo the place where these (temporary) objects are created.
 * Presumably, the function of this wrapper is to enhance or speed up the selection
 * of a url (or to cache it), but in reality these wrapper functions only copy data,
 * and do not add functionality that could not be performed on it's own by AudioUtils.
 * @duplicate very similar to {@link org.mmbase.util.media.video.videoparts.VideoPartDef}
 *
 * @author    vpro
 * @version   $Id: AudioPartDef.java,v 1.8 2002-02-20 10:43:28 pierre Exp $
 */

public class AudioPartDef {

    // logging
    private static Logger log    = Logging.getLoggerInstance(AudioPartDef.class.getName());

    /**
     * Audiopart object number
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int number;

    /**
     * Audiopart object type
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int otype;

    /**
     * Audiopart object owner
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String owner;

    /**
     * Audiopart title
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String title;

    /**
     * Audiopart subtitle
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String subtitle;

    /**
     * Audiopart title
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int source;

    /**
     * Audiopart speel duur
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int playtime;

    /**
     * Audiopart intro tekst
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String intro;

    /**
     * Audiopart body tekst
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String body;

    /**
     * Audiopart mono of stereo
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int storage;

    /**
     * Audiopart start tijd
     * @scope private
     */
    public String starttime;

    /**
     * Audiopart eind tijd
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
     * Creates the wrapper and initializes it by copying the node's fields and values.
     * @param mmbase The mmbase to use (a bit strange as a parameter, as you are already passing a node)
     * @param number The number of the node to wrap
     * @performance why are fields stored as properties, rather than the node itself?
     */
    public AudioPartDef(MMBase mmbase, int number) {
        if (!setparameters(mmbase, number)) {
            log.error("AudioPartDef(" + mmbase + "," + number + "): Not initialised, something went wrong");
        }
    }

    /**
     * Creates the wrapper and initializes it by copying the node's fields and values.
     * @param mmbase The mmbase to use (a bit strange as a parameter, as you are already passing a node)
     * @param node The node to wrap
     * @performance why are fields stored as properties, rather than the node itself?
     */
    public AudioPartDef(MMBase mmbase, MMObjectNode node) {
        if (!setparameters(mmbase, node)) {
            log.error("AudioPartDef(" + node + "): Not initialised, something went wrong!");
        }
    }

    /**
     * Initializes the wrapper by copying the node's fields and values.
     * @param mmbase The mmbase to use (a bit strange as a parameter, as you are already passing a node)
     * @param number The number of the node to wrap
     * @performance why are fields stored as properties, rather than the node itself?
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
     * Initializes the wrapper by copying the node's fields and values.
     * @param mmbase The mmbase to use (a bit strange as a parameter, as you are already passing a node)
     * @param node The node to wrap
     * @performance why are fields stored as properties, rather than the node itself?
     */
    public boolean setparameters(MMBase mmbase, MMObjectNode node) {
        boolean result  = false;
        if (node != null) {
            number = node.getIntValue("number"); // not needed
            otype = node.getIntValue("otype"); // not needed
            owner = node.getStringValue("owner"); // not needed

            title = node.getStringValue("title"); // not needed
            subtitle = node.getStringValue("subtitle"); // not needed
            source = node.getIntValue("source"); // not needed
            playtime = node.getIntValue("playtime"); // not needed
            intro = node.getStringValue("intro"); // not needed
            body = node.getStringValue("body"); // not needed
            storage = node.getIntValue("storage"); // not needed

            getStartStop(mmbase);

            result = true;
        } else {
            log.error("AudioPartsDef(" + node + "): Node is null!");
        }
        return result;
    }

    /**
     * Attempts to selects the best available rawaudio object for the given speed and channels
     * The result of this method is deciding for the outcome of the {@link @getRealAudioUrl} call.
     * @param mmbase The mmbase to use (a bit strange as a parameter, as you are already wrapping a node)
     * @param wantedspeed the desired speed of the audio
     * @param wantedchannels  the desired nr of channels for the audio
     * @param sorted whether to sort the available audios first (?)
     * @return true if a raw audio could be found. If true, the rawaudio is remembered by the object,
     *          for use by a subsequent call to {@link @getRealAudioUrl}
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
     * Determine start and stop time values of the given object.
     * The values are retrieved from the properties builder (?) and stored in the wrapper object.
     * @param mmbase The mmbase to use (a bit strange as a parameter, as you are already wrapping a node)
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
     * parameters.
     * The result of this method is dependent on the outcome of the previous {@link @getRawAudios} call,
     * which makes it unsafe for use in a multithreaded environment.
     * @deprecation-used contains commented-out code (SMIL does not accept quoted values)
     * @dependency scanpage (SCAN) - can be removed as it is not actually used except for debug code
     * @param sp the scanpage
     * @return a String with the url.
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
     * Returns a string representation of this object, for debugging purposes.
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
     * Returns a string representation of this object, for debugging purposes.
     */
    public String toString() {
        return this.getClass().getName() + "( number(" + number + ") otype(" + otype + ") owner(" + owner + ") title(" + title + ") subtitle(" + subtitle + ") source(" + source + ") playtime(" + playtime + ") intro(" + intro + ") body(" + body + ") storage(" + storage + "), starttime(" + starttime + "), stoptime(" + stoptime + "), rawaudios(" + rawaudios.size() + "), rawaudio(" + rawaudio.toString() + ")";
    }
}

