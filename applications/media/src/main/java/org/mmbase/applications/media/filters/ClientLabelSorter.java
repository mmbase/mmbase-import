 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;
import org.mmbase.applications.media.urlcomposers.URLComposer;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 * @version $Id: FieldValueSorter.java 38845 2009-09-24 14:00:43Z michiel $
 */
public class ClientLabelSorter extends  PreferenceSorter {
    public static final String ATT = ClientLabelSorter.class.getName() + ".label";

    public int getPreference(URLComposer urlcomposer) {
        String requestedLabel = (String) FilterUtils.getClientAttribute(urlcomposer, ATT);
        String label  = (String) urlcomposer.getInfo().get("label");
        if (requestedLabel == null || label == null) return 0;
        if (requestedLabel.equals(label)) {
            return 1;
        } else {
            return -1;
        }
    }
}
