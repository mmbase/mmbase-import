 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.regex.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Prefer  sources where a field's value matches, or not, a certain regular expression
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id$
 */
public class FieldValueSorter extends  PreferenceSorter {
    private static Logger log = Logging.getLoggerInstance(FieldValueSorter.class);

    private String field = "label";

    private boolean inverse = false;
    private Pattern pattern = Pattern.compile(".*");

    public void setField(String f) {
        field = f;
    }

    /**
     * If inverse, than only filters out those sources that do <em>not</em> match the regular expression.
     */
    public void setInverse(boolean i) {
        inverse = i;
    }

    public void setPattern(String  p) {
        pattern = Pattern.compile(p);
    }

    @Override
    public int getPreference(URLComposer urlcomposer) {
        String value = urlcomposer.getSource().getStringValue(field);
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            if (inverse) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (! inverse) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
