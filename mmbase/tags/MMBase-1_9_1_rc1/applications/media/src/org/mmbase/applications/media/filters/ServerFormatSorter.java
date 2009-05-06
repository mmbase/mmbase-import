 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.Format;
import java.util.*;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;

/**
 * Sorts on format of the source, preferred formats can be can be
 * configured with the filters.xml. This is called 'server'
 * formatsorter, because this preference is configured on the server,
 * rather then on the client, which is another logical option (which
 * can be combined with this one).
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ServerFormatSorter.java,v 1.10 2007-06-21 15:50:21 nklasens Exp $
 * @see     ClientFormatSorter
 */
public class ServerFormatSorter extends  PreferenceSorter {
    private static Logger log = Logging.getLoggerInstance(ServerFormatSorter.class);

    public static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".preferredSource";
    public static final String FORMAT_ATT = "format";

    protected List<Format> preferredFormats = new ArrayList<Format>();

    public  ServerFormatSorter() {};

    public void configure(DocumentReader reader, Element el) {
        preferredFormats.clear();
        // reading preferredSource information
        for (Element n3:reader.getChildElements(reader.getElementByPath(el, CONFIG_TAG))) {
            String format = reader.getElementAttributeValue(n3, FORMAT_ATT);
            preferredFormats.add(Format.get(format));
            log.service("Adding preferredSource format: '"+format +"'");
        }
    }

    protected int getPreference(URLComposer ri) {
        Format format = ri.getFormat();
        int index =  preferredFormats.indexOf(format);
        if (index == -1) {
            if (log.isDebugEnabled()) log.debug("Not found format: '" + format + "' in" + preferredFormats);
            index = preferredFormats.size() + 1;
        }
        index = -index;   // low index =  high preference
        if (log.isDebugEnabled()) log.debug("preference of format '" + format + "': " + index);
        return index;
    }

}

