/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.media.video;

import java.util.Vector;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The main function of this class is to provide a means to sort through a collection of RawVideo
 * objects (from the rawvideos builder).
 * MMObjectNodes can not easily be sorted (unless through sql querying).
 * This class is, essentially, a wrapper that provides this sorting functionality.
 *
 * @duplicate this class almost exactly duplicates the functionality of the
 *            {@link org.mmbase.util.media.audio.RawAudioDef} class
 * @deprecation-used CompareInterface and Sortable are deprecated, use java.lang.Comparable
 * @javadoc
 * @author vpro
 * @version $Id: RawVideoDef.java,v 1.7 2002-02-20 11:45:19 pierre Exp $
 */
public class RawVideoDef implements Comparable, CompareInterface, Sortable {
    /**
     * @deprecated use STATUS_REQUEST
     */
    public final static int STATUS_VERZOEK = 1;
    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int STATUS_REQUEST = 1;
    /**
     * @deprecated use STATUS_BUSY
     */
    public final static int STATUS_ONDERWEG  = 2;
    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int STATUS_BUSY = 2;
    /**
     * @deprecated use STATUS_DONE
     */
    public final static int STATUS_GEDAAN = 3;
    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int STATUS_DONE = 3;

    /**
     * Real (.ra) audio format
     * @duplicate also defined in RawAudioDef
     */
    public final static int FORMAT_R5 = 2;
    /**
     * G2 (surestream) audio format
     * @duplicate also defined in RawAudioDef
     */
    public final static int FORMAT_G2 = 6;

    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int MINSPEED = 16000;
    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int MAXSPEED = 96000;
    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int MINCHANNELS = 1;
    /**
     * @duplicate also used in RawAudioDef
     */
    public final static int MAXCHANNELS = 2;

    // logging
    private static Logger log = Logging.getLoggerInstance(RawVideoDef.class.getName());

    /**
     * Rawvideo object number
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int number;
    /**
     * Rawvideo object type
     * @scope private
     * @deprecated not used?
     */
    public int otype;

    /**
     * Rawvideo object owner
     * @scope private
     * @deprecated not used?
     */
    public String owner;

    /**
     * Rawvideo id, used for sorting
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
     * Rawvideo format, determines video url protocol
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public int format;
    /**
     * Rawvideo speed, used for sorting
     * @scope private or package
     */
    public int speed;
    /**
     * Rawvideo nr of channels, used for sorting
     * @scope private or package
     */
    public int channels;
    /**
     * Rawvideo url
     * @scope private
     * @deprecated should be retrieved from node reference
     */
    public String url;
    /**
     * Rawvideo cpu (?)
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
    public RawVideoDef(MMObjectNode node) {
        if (node != null) {
            number = node.getIntValue("number");  // not needed
            otype = node.getIntValue("otype");    // not needed
            owner = node.getStringValue("owner"); // not needed

            id = node.getIntValue("id");
            status = node.getIntValue("status");  // not needed
            format = node.getIntValue("format");  // not needed
            speed = node.getIntValue("speed");
            channels = node.getIntValue("channels");
            url = node.getStringValue("url");     // not needed
            cpu = node.getStringValue("cpu");     // not needed
        } else {
            log.error("node is null!");
        }
    }

    /**
     * Compares this RawVideo object to the passed RawVideo object
     * RawVideo objects are compared (and ordered) by their id, speed, and channel.
     * @duplicate also used in RawAudioDef
     * @since MMBase-1.6
     * @param o the object to compare this object to
     * @return 0 if the objects are the same, -1 if the passed object is greater than this object,
     *          1 passed if the object is smaller than this object.
     */
    public int compareTo(Object o) {
        RawVideoDef other  = (RawVideoDef)o;
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
     * Compares whether this object and the passed object are the same
     * @duplicate also used in RawAudioDef
     * @since MMBase-1.6
     * @param o the object to compare this object to
     * @return true if the objects are the same, false otherwise
     */
    public boolean equals(Object o) {
        if (! (o instanceof RawVideoDef)) return false;
        RawVideoDef other  = (RawVideoDef)o;
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
        return ((RawVideoDef)thisO).compare((RawVideoDef)otherO);
    }

    /**
     * Sort vector with RawVideos
     * @deprecated use Collections.sort()
     */
    public static Vector sort(Vector unsorted) {
        return SortedVector.SortVector(unsorted);
    }

    /**
     * Retrieves the url for an raw audio, based on info in the format and url fields of
     * a wrapped rawaudio node.
     * @dependency scanpage (SCAN) - can be removed as it is not actually used except for debug code
     * @todo use a node reference to access the fields status, format, and url
     * @param sp scanpage parameter.
     * @return the stream's url, or null if the audio ahs not yet bene rendered or is of an unsupported format
     */
    public String getRealVideoUrl(scanpage sp) {
        String result  = null;

        if (status == STATUS_DONE) {
            if (format == FORMAT_R5) {
                result = "pnm://" + VideoUtils.getBestMirrorUrl(sp, url);
            } else
                if (format == FORMAT_G2) {
                result = "rtsp://" + VideoUtils.getBestMirrorUrl(sp, url);
            } else {
                log.error("For number(" + number + "): Unknown format(" + format + ")");
            }
        } else {
            log.error("For number(" + number + "): Asking url while status(" + status + ") signals 'not finished yet'!");
        }

        return result;
    }

    /**
     * Returns a string representation of this object, for debugging purposes.
     */
    public String toString() {
        return this.getClass().getName() + "( number(" + number + ") otype(" + otype + ") owner(" + owner + ") id(" + id + ") status(" + status + ") format(" + format + ") speed(" + speed + ") channels(" + channels + ") url(" + url + ") cpu(" + cpu + ") )";
    }
}

