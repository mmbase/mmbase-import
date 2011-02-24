 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mmbase.applications.media.urlcomposers.URLComposer;

/**
 * The (request or info) attribute {@link #ATT} is a comma separated list of labels. URLComposer's with one of those labels in their {@link URLComposer#getInfo()} get preference.
 * @see ClientBitrateFilter.
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id: FieldValueSorter.java 38845 2009-09-24 14:00:43Z michiel $
 */
public class ClientLabelSorter extends  PreferenceSorter {
    public static final String ATT = ClientLabelSorter.class.getName() + ".label";

    @Override
    public int getPreference(URLComposer urlcomposer) {
        String requestedLabel = (String) FilterUtils.getClientAttribute(urlcomposer, ATT);
        String label  = (String) urlcomposer.getInfo().get("label");
        if (requestedLabel == null || label == null) return 0;
        List<String> requestedLabels = new ArrayList<String>(Arrays.asList(requestedLabel.split(",")));
        Collections.reverse(requestedLabels); // earlier in the list is better, now they have higher index
        int i = requestedLabels.indexOf(label);
        if (i < 0) {
            return -1;
        } else {
            return i + 1; // don't return 0
        }
    }
}
