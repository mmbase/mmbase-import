 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.util.StringSplitter;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id: FieldValueSorter.java 38845 2009-09-24 14:00:43Z michiel $
 */
public class ClientLabelFilter implements Filter {
    public static final String ATT = ClientLabelFilter.class.getName() + ".label";

    private String keys = "label";

    public void setKeys(String k) {
        keys = k;
    }

    public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        List<URLComposer> filteredUrlcomposers = new ArrayList<URLComposer>();
        for (URLComposer urlcomposer : urlcomposers) {
            String label   = (String) FilterUtils.getClientAttribute(urlcomposer, ATT);
            if (label == null) {
                filteredUrlcomposers.add(urlcomposer);
            } else {
                Set<String> labels = new HashSet<String>(StringSplitter.split(label));
                for (String k : StringSplitter.split(keys)) {
                    if (urlcomposer.getInfo().containsKey(k)) {
                        if (labels.contains(urlcomposer.getInfo().get(k))) {
                            filteredUrlcomposers.add(urlcomposer);
                        }
                        break;
                    }
                }
            }
        }
        return filteredUrlcomposers;
    }

    public void configure(DocumentReader reader, Element element) {
        FilterUtils.propertiesConfigure(this, reader, element);
    }
}
