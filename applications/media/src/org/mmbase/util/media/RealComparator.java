 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;


/**
 * This can sort according to properties of real streams.
 * @author  Michiel Meeuwissen
 * @version $Id: RealComparator.java,v 1.2 2003-01-08 08:50:18 michiel Exp $
 */
public class RealComparator extends  PreferenceComparator {
    private static Logger log = Logging.getLoggerInstance(RealComparator.class.getName());

    private static int minSpeed        = 0;
    private static int maxSpeed        = 0;
    private static int minChannels     = 0;
    private static int maxChannels     = 0;

    public  RealComparator() {
    }

    public void configure(XMLBasicReader reader, Element e) {
        try {
            minSpeed    = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.minspeed")));
            maxSpeed    = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.maxspeed")));
            minChannels = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.minchannels")));
            maxChannels = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.maxchannels")));
        } catch (Exception ex) {
            log.error("Check mediasourcefilter.xml, something went wrong while reading realaudio information:" + ex);
            log.error(Logging.stackTrace(ex));
        }
        if(log.isDebugEnabled()) {
            log.debug("Minspeed="   + minSpeed);
            log.debug("Maxspeed="   + maxSpeed);
            log.debug("Minchannels="+ minChannels);
            log.debug("Maxchannels="+ maxChannels);
        }
    }
        
    /**
     * select the best realaudio mediasource if available
     * @param mediaSources the list of appropriate mediasources
     * @return the best realaudio mediasource
     */
    protected int getPreference(ResponseInfo ri) {
        MMObjectNode source = ri.getSource();
        /*
        if (source.getIntValue("format") =
        int result;

        if(info.containsKey("wantedspeed")) {
            wantedspeed=Integer.parseInt(""+info.get("wantedspeed"));
        }
        if(info.containsKey("wantedchannels")) {
            wantedchannels=Integer.parseInt(""+info.get("wantedchannels"));
        }
        
        if( wantedspeed < minSpeed ) {
            log.error("wantedspeed("+wantedspeed+") less than minspeed("+minSpeed+")");
            wantedspeed = minSpeed;
        }
        if( wantedspeed > maxSpeed ) {
            log.error("wantedspeed("+wantedspeed+") greater than maxspeed("+maxSpeed+")");
            wantedspeed = maxSpeed;
        }
        if( wantedchannels < minChannels ) {
            log.error("wantedchannels("+wantedchannels+") less than minchannels("+minChannels+")");
            wantedchannels = minChannels;
        }
        if( wantedchannels > maxChannels ) {
            log.error("wantedchannels("+wantedchannels+") greater than maxchannels("+maxChannels+")");
            wantedchannels = maxChannels;
        }
        
        MMObjectNode bestR5 = null;
        for(Iterator i=mediaSources.iterator(); i.hasNext();) {
            MMObjectNode mediaSource = (MMObjectNode) i.next();
            
            // Is the MediaSource ready for use && is format realaudio
            if( mediaSource.getIntValue("status") == MediaSources.DONE  &&
            mediaSource.getIntValue("format") == MediaSources.RA_FORMAT ) {
                if(mediaSourceBuilder.getSpeed(mediaSource) <= wantedspeed && mediaSourceBuilder.getChannels(mediaSource) <= wantedchannels) {
                    if(bestR5==null) {
                        bestR5 = mediaSource;
                    } else {
                        if(mediaSourceBuilder.getChannels(bestR5) < mediaSourceBuilder.getChannels(mediaSource)) {
                            bestR5 = mediaSource;
                        }
                        if(mediaSourceBuilder.getSpeed(bestR5) < mediaSourceBuilder.getSpeed(mediaSource) && mediaSourceBuilder.getChannels(bestR5) == mediaSourceBuilder.getChannels(mediaSource)) {
                            bestR5 = mediaSource;
                        }
                    }
                }
            }
        }
        // did we find a R5 stream ?
        if( bestR5 != null ) {
            log.debug("R5 stream found "+bestR5.getStringValue("number"));
            return bestR5;
        }
        
        return null;
        */
        return 0;
    }

}

