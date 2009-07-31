 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.Format;
import org.mmbase.util.images.Dimension;
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
 * @version $Id: ServerFormatSorter.java 36047 2009-06-14 14:44:44Z michiel $
 * @see     ClientFormatSorter
 */
public class ServerDimensionSorter extends  PreferenceSorter {
    private static Logger log = Logging.getLoggerInstance(ServerDimensionSorter.class);

    public static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".preferredDimension";
    public static final String FORMAT_ATT = "dimension";


    protected final List<Dimension> preferredDimensions = new ArrayList<Dimension>();

    public  ServerDimensionSorter() {};

    public void configure(DocumentReader reader, Element parent) {
        preferredDimensions.clear();
        for (Element el: reader.getChildElements(reader.getElementByPath(parent, CONFIG_TAG))) {
            String xa = el.getAttribute("x");
            String ya = el.getAttribute("y");
            preferredDimensions.add(new Dimension(xa.equals("*") ? - 1 : Integer.parseInt(xa),
                                                  ya.equals("*") ? - 1 : Integer.parseInt(ya)));

        }
        log.service("Preferred dimensions '"+ preferredDimensions +"'");

    }

    protected int getPreference(URLComposer ri) {
        Dimension dimension = ri.getDimension();

        int index;
        for (index = 0; index <= preferredDimensions.size(); index++) {
            if (index< preferredDimensions.size()) {
                Dimension dim = preferredDimensions.get(index);

                if ((dim.getHeight() == -1 || dimension.getHeight() == dim.getHeight()) &&
                    (dim.getWidth() == -1 || dimension.getWidth() == dim.getWidth())) {
                    log.debug("Comparing " + dim + " with " + dimension + " -> matched");
                    break;
                } else {
                    log.debug("Comparing " + dim + " with " + dimension + " -> not matched");
                }
            }
        }
        index = -index;   // low index =  high preference
        if (log.isDebugEnabled()) {
            log.debug("preference of dimension '" + dimension + "': " + index);
        }
        return index;
    }

}

