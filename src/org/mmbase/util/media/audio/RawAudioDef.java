/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.media.audio;

import java.util.Vector;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The main function of this class is to provide a means to sort through a collection of RawAudio
 * objects (from the rawaudios builder).
 * MMObjectNodes can not easily be sorted (unless through sql querying).
 * This class is, essentially, a wrapper that provides this sorting functionality.
 *
 * @deprecation-used CompareInterface and Sortable are deprecated, use java.lang.Comparable
 * @duplicate this class almost exactly duplicates the functionality of the
 *            {@link org.mmbase.util.media.audio.RawVideoDef} class
 * @author Daniel Ockeloen
 * @author David van Zeventer
 * @author Rico Jansen
 * @version $Id: RawAudioDef.java,v 1.11 2002-02-20 10:43:27 pierre Exp $
 */
public class RawAudioDef implements Comparable, CompareInterface, Sortable {

    //logging
    private static Logger log = Logging.getLoggerInstance(RawAudioDef.class.getName());

    /**
     * @deprecated use STATUS_REQUEST
     */
    public final static int STATUS_VERZOEK = 1;
    /**
     * @duplicate also used in RawVideoDef
     */
    public final static int STATUS_REQUEST = 1;
    /**
     * @deprecated use STATUS_BUSY
     */
    public final static int STATUS_ONDERWEG  = 2;
    /**
     * @duplicate also used in RawVideoDef
     */
    public final static int STATUS_BUSY = 2;
    /**
     * @deprecated use STATUS_DONE
     */
    public final static int STATUS_GEDAAN = 3;
    /**
     * @duplicate also used in RawVideoDef
     */
    public final static int STATUS_DONE = 3;

    /**
     * Real (.ra) audio format
     * @duplicate defined in RawAudios as RA_FORMAT
     */
    public static final int FORMAT_R5       = 2;
    /**
     * WAV audio format
     * @duplicate defined in RawAudios as WAV_FORMAT
     */
    public static final int FORMAT_WAV      = 3;
    /**
     * PCM (MP2?) audio format
     * @duplicate defined in RawAudios as MP2_FORMAT.
     *            RawAudios' PCM_FORMAT defines a different value
     */
    public static final int FORMAT_PCM      = 5;
    /**
     * G2 (surestream) audio format
     * @duplicate defined in RawAudios as SURESTREAM_FORMAT
     */
    public static final int FORMAT_G2       = 6;

    /**
     * storage of raw media: stereo
     */
    public static final int STORAGE_STEREO          = 1;
    /**
     * storage of raw media: stereo, no backup
     */
    public static final int STORAGE_STEREO_NOBACKUP = 2;
    /**
     * storage of raw media: mono
     */
    public static final int STORAGE_MONO            = 3;
    /**
     * storage of raw media: mono, no backup
     */
    public static final int STORAGE_MONO_NOBACKUP   = 4;
    /**
     * Minimum speed for audio
     * @duplicate also used in RawVideoDef
     */
    public static final int MINSPEED        = 16000;
    /**
     * Maximum speed for audio
     * @duplicate also used in RawVideoDef
     */
    public static final int MAXSPEED        = 96000;
    /**
     * Minimum channels for audio
     * @duplicate also used in RawVideoDef
     */
    public static final int MINCHANNELS     = 1;
    /**
     * Maximum channels for audio
     * @duplicate also used in RawVideoDef
     */
    public static final int MAXCHANNELS     = 2;

    /**
     * Maximum speed for wav audio
     */
    public static final int WAV_MAXSPEED    = 44100;
    /**
     * Minimum speed for surestream audio (currently unused)
     */
    public static final int G2_MINSPEED     = 16000;
    /**
     * Maximum speed for surestream audio
     */
    public static final int G2_MAXSPEED     = 96000;

    /**
     * @javadoc
     * @vpro specific url/directory part, should be made configurable
     */
    public static final String AUDIO_DIR       = "/data/audio/";

    /**
     * Rawaudio object number
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int number;

    /**
     * Rawaudio object type
     * @scope private
     * @deprecated not used?
     */
    public int otype;
    /**
     * Rawaudio object owner
     * @scope private
     * @deprecated not used?
     */
    public String owner;

    /**
     * Rawaudio id, used for sorting
     * @scope private or package
     */
    public int id;
    /**
     * @javadoc
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int status;
    /**
     * Rawaudio format, determines audio url protocol
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int format;
    /**
     * Rawaudio speed, used for sorting
     * @scope private or package
     */
    public int speed;
    /**
     * Rawaudio nr of channels, used for sorting
     * @scope private or package
     */
    public int channels;
    /**
     * Rawaudio url
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String url;
    /**
     * Rawaudio cpu (?)
     * @scope private
     * @deprecated not used?
     */
    public String cpu;

    /**
     * Creates an instance of the sortable wrapper for an MMObjectNode.
     * This class copies a node's values to speed up the sorting process.
     * @todo throw exception when no node is provided, keep a reference to the node
     *       and only copy fields needed to speed up sorting - see {@link #compareTo}.
     * @param node the node to wrap
     */
    public RawAudioDef( MMObjectNode node ) {
        if( node != null ) {
            number = node.getIntValue("number");  // not needed
            otype = node.getIntValue("otype");    // not needed
            owner = node.getStringValue("owner"); // not needed

            id = node.getIntValue("id");
            status = node.getIntValue("status"); // not needed
            format = node.getIntValue("format"); // not needed
            speed = node.getIntValue("speed");
            channels = node.getIntValue("channels");
            url = node.getStringValue("url");    // not needed
            cpu = node.getStringValue("cpu");    // not needed
        } else {
            log.error("RawAudioDef("+node+"): node is null!");
        }
    }

    /**
     * Compares this RawAudio object to the passed RawAudio object
     * RawAudio objects are compared (and ordered) by their id, speed, and channel.
     * @duplicate also used in RawVideoDef
     * @param o the object to compare this object to
     * @return 0 if the objects are the same, -1 if the passed object is greater than this object,
     *          1 passed if the object is smaller than this object.
     */
    public int compareTo(Object o) {
        RawAudioDef other  = (RawAudioDef)o;
        int result = 0;
        if (this.id == other.id) {
            if (this.speed == other.speed) {
                if (this.channels == other.channels) {
                    log.warn("compare( this(" + this.toString() + ", other(" + other.toString() + "): Comparing two identical items!");
                    result = 0;
                } else {
                    if (this.channels > other.channels) {
                        result = 1;
                    } else {
                        result = -1;
                    }
                }
            } else {
                if (this.speed > other.speed) {
                    result = 1;
                } else {
                    result = -1;
                }
            }
        } else
            if (this.id > other.id) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * Compares whether this object and the passed object are the same.
     * Objects are the same if they have the same id, speed, and nr of channels
     * @duplicate also used in RawVideoDef
     * @param o the object to compare this object to
     * @return true if the objects are the same, false otherwise
     */
    public boolean equals(Object o) {
        if (! (o instanceof RawAudioDef)) return false;
        RawAudioDef other  = (RawAudioDef)o;
        return (this.id == other.id) &&
               (this.speed == other.speed) &&
               (this.channels == other.channels);
    }

    /**
     * Compares this object to the passed object
     * @param otherItem the object to compare this object to
     * @return 0 if the objects are the same, -1 if the passed object is greater than this object,
     *          1 passed if the object is smaller than this object.
     * @deprecated use {@link #compareTo} instead
     */
    public int compare(Sortable otherItem) {
        return compareTo(otherItem);
    }

    /**
     * Compares two objects
     * @param thisO the first object to compare
     * @param otherO the second object to compare
     * @return 0 if the objects are the same, -1 if the second object is greater than the first,
     *          1 passed if the second object is smaller than the first.
     * @deprecated use {@link #compareTo} instead
     */
    public int compare(Object thisO, Object otherO) {
        return ((RawAudioDef)thisO).compare((RawAudioDef)otherO);
    }

    /**
     * Sort vector with RawAudios
     * @param unsorted the Vector to stor
     * @deprecated use Collections.sort()
     */
    public static Vector sort( Vector unsorted ) {
        return SortedVector.SortVector(unsorted);
    }

    /**
     * Retrieves the url for an raw audio, based on info in the format and url fields of
     * a wrapped rawaudio node.
     * @dependency scanpage (SCAN) - can be removed as it is not actually used except for debug code
     * @todo use a node reference to access the fields status, format, and url
     * @param sp scanpage parameter.
     */
    public String getRealAudioUrl( scanpage sp ) {
        String result = null;

        if( status == STATUS_DONE ) {
            if( format == FORMAT_R5 ) {
                result = "pnm://" + AudioUtils.getBestMirrorUrl( sp, url );
            } else if( format == FORMAT_G2 ) {
                result = "rtsp://" + AudioUtils.getBestMirrorUrl( sp, url );
            } else {
                log.error("For number("+number+"): Unknown format("+format+")");
            }
        } else {
            log.error("For number("+number+"): Asking url while status("+status+") signals 'not finished yet'!");
        }
        return result;
    }


    /**
     * Returns a string representation of this object, for debugging purposes.
     */
    public String toString() {
        return this.getClass().getName()+
               "( number("+number+") otype("+otype+") owner("+owner+") id("+id+") status("+status+") format("+format+") speed("+speed+") channels("+channels+") url("+url+") cpu("+cpu+") )";
    }
}
