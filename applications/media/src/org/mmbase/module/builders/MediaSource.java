/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.module.builders;

import java.util.Enumeration;
import java.sql.Connection;
import java.io.File;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The MediaSource class describes pieces of media (audio / video). Information about
 * format, quality, and status will be maintained in this object. A MediaSource belongs
 * to a MediaFragement that describes the piece of media, the MediaSource is the
 * real audio/video itself. A MediaSource is connected to provider objects that indicate
 * where the real audio/video files can be found.
 *
 *
 */
public class MediaSource extends MMObjectBuilder {
    
    private static Logger log = Logging.getLoggerInstance(MediaSource.class.getName());
    
    // Audio formats
    private final static int MP3_FORMAT         = 1;
    private final static int RA_FORMAT          = 2;
    private final static int WAV_FORMAT         = 3;
    private final static int PCM_FORMAT         = 4;
    private final static int MP2_FORMAT         = 5;
    private final static int SURESTREAM_FORMAT  = 6;
    
    // Video formats
    private final static int MPG_FORMAT = 11;
    private final static int RM_FORMAT = 12;
    private final static int MOV_FORMAT = 13;
    
    // Status
    private final static int REQUEST = 1;
    private final static int BUSY = 2;
    private final static int DONE = 3;
    private final static int SOURCE = 4;
    
    private final static int MONO = 1;
    private final static int STEREO = 2;
    
    /**
     * resolves the url
     */
    public String getURL() {
    }
    
    /**
     * used in the editors
     */
    public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("number");
        if (str.length()>15) {
            return str.substring(0,12)+"...";
        } else {
            return str;
        }
    }
    
    /**
     * used in the editors
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
        return getValue(node, "str("+field")");
    }
    
    /**
     * return some human readable strings
     */
    public Object getValue(MMObjectNode node,String field) {
        if (field.equals("str(status)")) {
            int val=node.getIntValue("status");
            switch(val) {
                case REQUEST: return "Request";
                case BUSY: return "Busy";
                case DONE: return "Done";
                case SOURCE: return "Source";
                default: return "Undefined";
            }
        } else if (field.equals("str(channels)")) {
            int val=node.getIntValue("channels");
            switch(val) {
                case MONO: return "Mono";
                case STEREO: return "Stereo";
                default: return "Undefined";
            }
        } else if (field.equals("str(format)")) {
            int val=node.getIntValue("format");
            switch(val) {
                case MP3_FORMAT: return "mp3";
                case RA_FORMAT: return "ra";
                case WAV_FORMAT: return "wav";
                case PCM_FORMAT: return "pcm";
                case MP2_FORMAT: return "mp2";
                case SURESTREAM_FORMAT: return "g2/sure";
                case MPG_FORMAT: return "mpg";
                case RM_FORMAT: return "Rm";
                case MOV_FORMAT: return "Mov";
                default: return "Undefined";
            }
        } else if (field.equals("str(issurestream)")) {
            int val=node.getIntValue("issurestream");
            switch( val ) {
                case 0	: return "false";
                case 1	: return "true";
                default	: return "onbepaald";
            }
        }
        return super.getValue(node,field);
    }
    
    /**
     * remove this MediaSource, check configuration and check all places where this
     * file is reproduced.
     */
    public void remove() {
    }
    
    public static String getProtocolName(int format) {
        String protName = new String();
        
        if (format == SURESTREAM_FORMAT) {
            protName = "rtsp";
        } else if (format == RA_FORMAT) {
            protName = "pnm";
        } else {
            protName = "http";
        }
        return protName;
    }
}
