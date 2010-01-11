 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.Format;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import java.util.*;


/**
 * This can sort according to properties of real streams.  The client
 * can request a certain speed/channels, but it can be forced to be
 * between two values (configured in mediasourcefilter.xml).
 *
 * This ia 'chainsorter' meaning that it is a combination of severa;
 * other sorters, which are implemented as inner classes. This is
 * because several criteria are to be distinguished, and taking them
 * apart makes things nice and simple.
 *
 * @author  Michiel Meeuwissen
 * @author  Rob Vermeulen
 * @version $Id$
 */
public class RealSorter extends  ChainSorter {
    private static Logger log = Logging.getLoggerInstance(RealSorter.class);

    // XML subtag
    public static final String CONFIG_TAG          = "config.realAudio";

    /**
     * Prefer real a little if this filter is used.
     * Other possibility: Impelmeent it that if one of both URLComposer are no reals, that they are equal then.
     */

    protected class RealFormatSorter extends PreferenceSorter {        
        protected int getPreference(URLComposer ri) {           
            if (ri.getFormat() != Format.RM) return 0; 
            return 1;
        }
    }

    /**
     * Sort with speed
     */
    protected class SpeedSorter extends PreferenceSorter {

        private int minSpeed        = -1;
        private int maxSpeed        = -1;    
        private int defaultSpeed        = -1;    

        @Override
        public void configure(DocumentReader reader, Element e) {
            try {
                minSpeed    = Integer.parseInt(DocumentReader.getElementValue(DocumentReader.getElementByPath(e, CONFIG_TAG + ".minspeed")));
                maxSpeed    = Integer.parseInt(DocumentReader.getElementValue(DocumentReader.getElementByPath(e, CONFIG_TAG + ".maxspeed")));
                defaultSpeed    = Integer.parseInt(DocumentReader.getElementValue(DocumentReader.getElementByPath(e, CONFIG_TAG + ".defaultspeed")));
            } catch (Exception ex) {
                log.error("Check mediasourcefilter.xml, something went wrong while reading realaudio information:" + ex);
                log.error(Logging.stackTrace(ex));
            }
            if(log.isDebugEnabled()) {
                log.debug("Minspeed="   + minSpeed);
                log.debug("Maxspeed="   + maxSpeed);
                log.debug("Defaultspeed="   + defaultSpeed);
           }
        }
        /**
         * @todo
         */
        protected int getPreference(URLComposer ri) {
            Map<String, Object> info           = ri.getInfo();
            int wantedSpeed    = -1;

            int preference     = 0;
            if(info.containsKey("wantedSpeed")) {
                preference = 1;  // explicitely requested something real-specific, prefer real
                wantedSpeed    = ((Integer) info.get("wantedSpeed")).intValue();
            } else {
		wantedSpeed = defaultSpeed;
	    }

            if (log.isDebugEnabled()) {
                log.debug("wantedSpeed:" + wantedSpeed + " minspeed: " + minSpeed + " maxspeed: " + maxSpeed);
            }

            if( wantedSpeed < minSpeed) {
                wantedSpeed = minSpeed;
            } else if( wantedSpeed > maxSpeed) {
                wantedSpeed = maxSpeed;
            }

            int speed    = ri.getSource().getIntValue("bitrate");

            if (speed <= wantedSpeed) {
                preference -= Math.abs(wantedSpeed - speed);
            } else {
                preference -= Math.abs(wantedSpeed - speed)*5; //Still sort them, but give them a lower priority
	    }
            return preference;
        }
    }

    /**
     * Sort with channels 
     */

    protected class ChannelsSorter extends PreferenceSorter {
        private int minChannels     = -1;
        private int maxChannels     = -1;
        private int defaultChannels = -1;


        @Override
        public void configure(DocumentReader reader, Element e) {
            try {
                minChannels = Integer.parseInt(DocumentReader.getElementValue(DocumentReader.getElementByPath(e, CONFIG_TAG + ".minchannels")));
                maxChannels = Integer.parseInt(DocumentReader.getElementValue(DocumentReader.getElementByPath(e, CONFIG_TAG + ".maxchannels")));
                defaultChannels = Integer.parseInt(DocumentReader.getElementValue(DocumentReader.getElementByPath(e, CONFIG_TAG + ".defaultchannels")));
            } catch (Exception ex) {
                log.error("Check mediasourcefilter.xml, something went wrong while reading realaudio information:" + ex);
                log.error(Logging.stackTrace(ex));
            }
            if(log.isDebugEnabled()) {
                log.debug("Minchannels="   + minChannels);
                log.debug("Maxchannels="   + maxChannels);
                log.debug("Defaultchannels="   + defaultChannels);
            }
        }

        /**
         * @javadoc
         */
        protected int getPreference(URLComposer ri) {
            Map<String, Object> info           = ri.getInfo();
            int wantedChannels = -1;

            int preference     = 0;
            if(info.containsKey("wantedChannels")) {
                preference = 1; // explicitely requested something real-specific, prefer real
                wantedChannels = ((Integer) info.get("wantedChannels")).intValue();
            } else {
		    wantedChannels = defaultChannels;
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

    
    public  RealSorter() {        
        add(new RealFormatSorter()); // Prefer real?
        add(new SpeedSorter());
        add(new ChannelsSorter());
    }


}

