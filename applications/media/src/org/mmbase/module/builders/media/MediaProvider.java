 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package speeltuin.media.org.mmbase.module.builders.media;

import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.FileWatcher;
import org.mmbase.util.StringObject;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.media.MediaProviderFilter;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * The MediaSource class describes pieces of media (audio / video). Information about
 * format, quality, and status will be maintained in this object. A MediaSource belongs
 * to a MediaFragement that describes the piece of media, the MediaSource is the
 * real audio/video itself. A MediaSource is connected to provider objects that indicate
 * where the real audio/video files can be found.
 *
 *
 */
public class MediaProvider extends MMObjectBuilder {

    
    private static Logger log = Logging.getLoggerInstance(MediaPrivder.class.getName());
    
    /**
     * the media provider is in charge of selecting the protocol.
     * This method contains a list of default media formats to protocols.
     * In a later version we can create extra objects that overwride these values.
     */
    private static String getDefaultProtocol(int format) {
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
    
    /**
     * evaluate the protocol for the media source
     */
    public static String getProtocol(MMObjectNode mediasource) {
       // Later check extra protocol objects to evaluate the protocol.
       // At this point just use the static deafult list.
       return getDefaultProtocol(mediasource.getIntValue("format"));
    }
}
