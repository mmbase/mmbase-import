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
 * @deprecation-used CompareInterface is deprecated, use java.lang.Comparable
 * @javadoc
 * @author vpro
 * @version $Id: RawVideoDef.java,v 1.5 2002-01-28 16:35:02 pierre Exp $
 */
public class RawVideoDef implements Sortable, CompareInterface {
    /**
     * @javadoc
     * @rename STATUS_REQUEST
     */
    public final static int STATUS_VERZOEK   = 1;
    /**
     * @javadoc
     * @rename STATUS_BUSY
     */
    public final static int STATUS_ONDERWEG  = 2;
    /**
     * @javadoc
     * @rename STATUS_DONE
     */
    public final static int STATUS_GEDAAN    = 3;
//  -------------------------
    /**
     * @javadoc
     */
    public final static int FORMAT_R5        = 2;
    /**
     * @javadoc
     */
    public final static int FORMAT_G2        = 6;
//  -------------------------
    /**
     * @javadoc
     */
    public final static int MINSPEED         = 16000;
    /**
     * @javadoc
     */
    public final static int MAXSPEED         = 96000;
    /**
     * @javadoc
     */
    public final static int MINCHANNELS      = 1;

    // logging
    private static Logger log                = Logging.getLoggerInstance(RawVideoDef.class.getName());

    /**
     * @javadoc
     * @scope private
     */
    public int number;
    /**
     * @javadoc
     * @scope private
     * @deprecated implied by number
     */
    public int otype;

    /**
     * @javadoc
     * @scope private
     * @deprecated implied by number
     */
    public String owner;

    /**
     * @javadoc
     * @scope private
     */
    public int id;
    /**
     * @javadoc
     * @scope private
     */
    public int status;
    /**
     * @javadoc
     * @scope private
     */
    public int format;
    /**
     * @javadoc
     * @scope private
     */
    public int speed;
    /**
     * @javadoc
     * @scope private
     */
    public int channels;
    /**
     * @javadoc
     * @scope private
     */
    public String url;
    /**
     * @javadoc
     * @scope private
     */
    public String cpu;

    /**
     * @javadoc
     * @performance why are fields stored as properties, rather than the node itself?
     */
    public RawVideoDef(MMObjectNode node) {
        if (node != null) {
            number = node.getIntValue("number");
            otype = node.getIntValue("otype");
            owner = node.getStringValue("owner");

            id = node.getIntValue("id");
            status = node.getIntValue("status");
            format = node.getIntValue("format");
            speed = node.getIntValue("speed");
            channels = node.getIntValue("channels");
            url = node.getStringValue("url");
            cpu = node.getStringValue("cpu");
        } else {
            log.error("RawVideoDef(" + node + "): ERROR: node is null!");
        }
    }

    /**
     * Sort vector with RawVideos
     * @javadoc parameters
     * @duplicate use Sortedvector call directly
     */
    public static Vector sort(Vector unsorted) {
        return SortedVector.SortVector(unsorted);
    }

    /**
     * @javadoc
     */
    public String getRealVideoUrl(scanpage sp) {
        String result  = null;

        if (status == STATUS_GEDAAN) {
            if (format == FORMAT_R5) {
                result = "pnm://" + VideoUtils.getBestMirrorUrl(sp, url);
            } else
                if (format == FORMAT_G2) {
                result = "rtsp://" + VideoUtils.getBestMirrorUrl(sp, url);
            } else {
                log.error("getRealVideoUrl(): For number(" + number + "): Unknown format(" + format + ")");
            }
        } else {
            log.error("getRealVideoUrl(): For number(" + number + "): Asking url while status(" + status + ") signals 'not finished yet'!");
        }

        return result;
    }

    /**
     * @javadoc
     */
    public int compare(Object thisO, Object otherO) {
        return ((RawVideoDef)thisO).compare((RawVideoDef)otherO);
    }

    /**
     * @javadoc
     * @deprecated implement compareTo for Comparable interface
     */
    public int compare(Sortable otherItem) {
        RawVideoDef other  = (RawVideoDef)otherItem;
        int result         = 0;

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
     * @javadoc
     */
    public String toString() {
        return this.getClass().getName() + "( number(" + number + ") otype(" + otype + ") owner(" + owner + ") id(" + id + ") status(" + status + ") format(" + format + ") speed(" + speed + ") channels(" + channels + ") url(" + url + ") cpu(" + cpu + ") )";
    }
}

