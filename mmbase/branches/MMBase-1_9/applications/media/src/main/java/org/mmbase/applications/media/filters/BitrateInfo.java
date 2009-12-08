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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * container for information beloning to a bitrate filter keyword.
 * In filter.xml the line <bitrate name="smallband" min="0" max="150000" />
 * will result in a BitrateFilterInfo inner object.
 */

public class BitrateInfo {
    private static final Logger log = Logging.getLoggerInstance(BitrateInfo.class);

    protected final String name;
    protected final int min;
    protected final int max;

    public BitrateInfo(String name, int min, int max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }
    public BitrateInfo(Element el) {
        this.name = el.getAttribute("name");
        String minAtt = el.getAttribute("min");
        this.min = minAtt.equals("") ? 0 : Integer.parseInt(minAtt);
        String maxAtt = el.getAttribute("max");
        this.max = maxAtt.equals("") ? Integer.MAX_VALUE : Integer.parseInt(maxAtt);
    }

    public String getName() {
        return name;
    }


    public String toString() {
        return "[" + min + "," + max + "]";
    }


    public boolean matches(int bitrateMedia) {

        log.debug("matching " + bitrateMedia + " with " + this);
        return bitrateMedia > min && bitrateMedia < max;
    }
}
