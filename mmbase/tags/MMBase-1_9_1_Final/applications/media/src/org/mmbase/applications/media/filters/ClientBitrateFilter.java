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
    private static Logger log = Logging.getLoggerInstance(ClientBitrateFilter.class);
    private static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".bitrates";
    private static Map<String, BitrateFilterInfo> bitrateFilters = new HashMap<String, BitrateFilterInfo>();

    public void configure(DocumentReader reader, Element element) {
        try {
            for(Element bitrate:reader.getChildElements(reader.getElementByPath(element, CONFIG_TAG))) {
                String name = reader.getElementAttributeValue(bitrate, "name");
                int min = Integer.parseInt(reader.getElementAttributeValue(bitrate, "min"));
                int max = Integer.parseInt(reader.getElementAttributeValue(bitrate, "max"));
                BitrateFilterInfo brfi = new BitrateFilterInfo(name, min, max);
                log.debug("Adding BitrateFilterInfo "+brfi);
                bitrateFilters.put(name, brfi);
            }
        } catch (Exception ex) {
            log.error("Error in filter.xml:" + ex);
            log.error(Logging.stackTrace(ex));
        }
    }

    public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        List<URLComposer> filteredUrlcomposers = new ArrayList<URLComposer>();

        for (URLComposer urlcomposer : urlcomposers) {
            Object bitrate = urlcomposer.getInfo().get("bitrate");
            log.debug("Client specified bitrate = " + bitrate);

            if(bitrate==null) {
                log.debug("Client did not specify bitrate.");
                return urlcomposers;
            }

            if(bitrate instanceof List) {
                log.error("lits is not supported.");

            }

            if (bitrate instanceof String) {
                if(!bitrateFilters.containsKey(bitrate)) {
                    log.error("Specified bitrate keyword is invaled. biterate="+bitrate);
                }
                BitrateFilterInfo brfi = bitrateFilters.get(bitrate);
                int br = urlcomposer.getSource().getIntValue("bitrate");
                if (brfi.validate(br)) {
                    log.debug("BitrateFilter "+brfi+" fits for urlcomposer with bitrate "+br);
                    filteredUrlcomposers.add(urlcomposer);
                }
            }
        }

        log.debug("filteredUrlcomposers = "+filteredUrlcomposers);
        return filteredUrlcomposers;
    }

    /**
     * container for information beloning to a bitrate filter keyword.
     * In filter.xml the line <bitrate name="smallband" min="0" max="150000" />
     * will result in a BitrateFilterInfo innerclass.
     */
    private class BitrateFilterInfo {
        private String name;
        private int min, max;

        private BitrateFilterInfo(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }

        private boolean validate(int bitrateMedia) {
            return min<bitrateMedia && max>bitrateMedia;
        }

        public String toString() {
            return "BitrateFilterInfo name="+name+" max="+max+" min="+min;
        }
    }
}
