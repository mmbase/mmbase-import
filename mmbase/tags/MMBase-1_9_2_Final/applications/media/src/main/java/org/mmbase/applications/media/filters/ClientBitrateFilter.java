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
 * Filters media sources according to a specified bitrate.
 * i.e. if bitrate=broadband, only broadband media sources are returned.
 * The definition of broadbast can be made in the filter.xml configuration file.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class ClientBitrateFilter implements Filter {
    private static final Logger log = Logging.getLoggerInstance(ClientBitrateFilter.class);
    private static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".bitrates";

    private final Map<String, BitrateInfo> bitrateFilters = new LinkedHashMap<String, BitrateInfo>();

    public void configure(DocumentReader reader, Element element) {
        bitrateFilters.clear();
        try {
            for(Element bitrate:reader.getChildElements(reader.getElementByPath(element, CONFIG_TAG))) {
                BitrateInfo brfi = new BitrateInfo(bitrate);
                log.debug("Adding BitrateFilterInfo "+brfi);
                bitrateFilters.put(brfi.getName(), brfi);
            }
        } catch (Exception ex) {
            log.error("Error in filter.xml:" + ex, ex);
        }
    }

    public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        List<URLComposer> filteredUrlcomposers = new ArrayList<URLComposer>();

        for (URLComposer urlcomposer : urlcomposers) {
            Object bitrate = urlcomposer.getInfo().get("bitrate");
            log.debug("Client specified bitrate = " + bitrate);

            if(bitrate == null) {
                log.debug("Client did not specify bitrate.");
                return urlcomposers;
            }

            if(bitrate instanceof List) {
                log.error("lits is not supported.");

            }

            if (bitrate instanceof String) {
                if(!bitrateFilters.containsKey(bitrate)) {
                    log.error("Specified bitrate keyword is invalidid. biterate="+bitrate);
                }
                BitrateInfo brfi = bitrateFilters.get(bitrate);
                int br = urlcomposer.getSource().getIntValue("bitrate");
                if (brfi.matches(br)) {
                    log.debug("BitrateFilter "+brfi+" fits for urlcomposer with bitrate "+br);
                    filteredUrlcomposers.add(urlcomposer);
                }
            }
        }

        log.debug("filteredUrlcomposers = "+filteredUrlcomposers);
        return filteredUrlcomposers;
    }


}
