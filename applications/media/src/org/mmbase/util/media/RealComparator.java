 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import org.mmbase.module.builders.media.Format;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import java.util.*;


/**
 * This can sort according to properties of real streams.  The client
 * can request a certain speed/channels, but it can be forced to be
 * between two values (configured in mediasourcefilter.xml).
 *
 * @author  Michiel Meeuwissen
 * @version $Id: RealComparator.java,v 1.5 2003-01-08 22:20:25 michiel Exp $
 */
public class RealComparator extends  ChainComparator {
    private static Logger log = Logging.getLoggerInstance(RealComparator.class.getName());


    /**
     * Prefer real a little if this filter is used?
     * Other possibility: Impelmeent it that if one of both ResponseInfo are no reals, that they are equal then.
     */

    class RealFormatComparator extends PreferenceComparator {        
        protected int getPreference(ResponseInfo ri) {
            if (ri.getSource().getIntValue("format") != Format.RM.toInt()) return 0; 
            return 1;
        }
    }

    /**
     * Sort with speed
     */

    class SpeedComparator extends PreferenceComparator {

        private int minSpeed        = -1;
        private int maxSpeed        = -1;    
        public void configure(XMLBasicReader reader, Element e) {
            try {
                minSpeed    = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.minspeed")));
                maxSpeed    = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.maxspeed")));
            } catch (Exception ex) {
                log.error("Check mediasourcefilter.xml, something went wrong while reading realaudio information:" + ex);
                log.error(Logging.stackTrace(ex));
            }
            if(log.isDebugEnabled()) {
                log.debug("Minspeed="   + minSpeed);
                log.debug("Maxspeed="   + maxSpeed);
           }
        }
        /**
         * select the best realaudio mediasource if available
         * @param mediaSources the list of appropriate mediasources
         * @return the best realaudio mediasource
         */
        protected int getPreference(ResponseInfo ri) {
            Map info           = ri.getInfo();
            int wantedSpeed    = -1;

            int preference     = 0;
            if(info.containsKey("wantedSpeed")) {
                preference = 1;  // explicitely requested something real-specific, prefer real
                wantedSpeed    = ((Integer) info.get("wantedSpeed")).intValue();
            }

            if (log.isDebugEnabled()) {
                log.debug("wantedSpeed:" + wantedSpeed + " minspeed: " + minSpeed + " maxspeed: " + maxSpeed);
            }

            if( wantedSpeed < minSpeed) {
                wantedSpeed = minSpeed;
            } else if( wantedSpeed > maxSpeed) {
                wantedSpeed = maxSpeed;
            }

            int speed    = ri.getSource().getIntValue("speed");

            if (speed <= wantedSpeed) {
                preference -= Math.abs(wantedSpeed - speed);
            }
            return preference;
        }
    }

    /**
     * Sort with channels 
     */

    class ChannelsComparator extends PreferenceComparator {
        private int minChannels     = -1;
        private int maxChannels     = -1;


        public void configure(XMLBasicReader reader, Element e) {
            try {
                minChannels = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.minchannels")));
                maxChannels = Integer.parseInt(reader.getElementValue(reader.getElementByPath(e, "filterConfigs.realaudio.maxchannels")));
            } catch (Exception ex) {
                log.error("Check mediasourcefilter.xml, something went wrong while reading realaudio information:" + ex);
                log.error(Logging.stackTrace(ex));
            }
            if(log.isDebugEnabled()) {
                log.debug("Minchannels="   + minChannels);
                log.debug("Maxchannels="   + maxChannels);
            }
        }

        /**
         * select the best realaudio mediasource if available
         * @param mediaSources the list of appropriate mediasources
         * @return the best realaudio mediasource
         */
        protected int getPreference(ResponseInfo ri) {
            Map info           = ri.getInfo();
            int wantedChannels = -1;

            int preference     = 0;
            if(info.containsKey("wantedChannels")) {
                preference = 1; // explicitely requested something real-specific, prefer real
                wantedChannels = ((Integer) info.get("wantedChannels")).intValue();
            }

            if (log.isDebugEnabled()) {
                log.debug("wantedChannels:" + wantedChannels + " minchennels: " + minChannels + " maxchannels: " + maxChannels);
            }
        

            int channels    = ri.getSource().getIntValue("channels");

            if (channels <= wantedChannels) {
                preference -= Math.abs(wantedChannels - channels);
         
            }
            return preference;
        }
    }

    
    public  RealComparator() {
        add(new RealFormatComparator()); // Prefer real?
        add(new SpeedComparator());
        add(new ChannelsComparator());
    }


}

