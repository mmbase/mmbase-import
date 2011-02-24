 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.util.StringSplitter;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id: FieldValueSorter.java 38845 2009-09-24 14:00:43Z michiel $
 */
public class ClientLabelSorter extends  PreferenceSorter {
    public static final String ATT = ClientLabelSorter.class.getName() + ".label";

    private String keys = "label";
    public void setKeys(String k) {
        keys = k;
    }
    public int getPreference(URLComposer urlcomposer) {
        String requestedLabel = (String) FilterUtils.getClientAttribute(urlcomposer, ATT);
        if (requestedLabel == null) return 0;
        List<String> requestedLabels = new ArrayList<String>(StringSplitter.split(requestedLabel));
        Collections.reverse(requestedLabels); // earlier in the list is better, now they have higher index
        int i = -1;
        for (String k : StringSplitter.split(keys)) {
            String label = (String) urlcomposer.getInfo().get(k);
            int p = requestedLabels.indexOf(label);
            if (p > i) i = p;
        }
        if (i < 0) {
            return -1;
        } else {
            return i + 1; // don't return 0
        }
    }
}
