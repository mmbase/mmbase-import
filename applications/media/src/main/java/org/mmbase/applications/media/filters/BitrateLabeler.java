 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import java.util.*;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 */
public class  BitrateLabeler  extends Labeler  {
    private static final Logger log = Logging.getLoggerInstance(BitrateLabeler.class);
    private static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".bitrates";

    private final Map<String, BitrateInfo> bitrates= new LinkedHashMap<String, BitrateInfo>();

    public void configure(DocumentReader reader, Element element) {
        bitrates.clear();
        try {
            for(Element bitrate : reader.getChildElements(reader.getElementByPath(element, CONFIG_TAG))) {
                BitrateInfo bri = new BitrateInfo(bitrate);
                log.debug("Adding BitrateInfo "+ bri);
                bitrates.put(bri.getName(), bri);
            }
        } catch (Exception ex) {
            log.error("Error in filter.xml:" + ex, ex);
        }
        log.info("Configured bit rate labeler " + bitrates);
    }


    protected void label(URLComposer uc) {
        for (Map.Entry<String, BitrateInfo> entry : bitrates.entrySet()) {
            int bitrate = uc.getSource().getIntValue("bitrate");
            if (entry.getValue().matches(bitrate)) {
                log.debug("" + bitrate + " matched " + entry);
                uc.getInfo().put("bitrate", entry.getKey());
            }
        }

    }

}
