 /*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

  */

package org.mmbase.applications.media.filters;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.*;
import java.util.regex.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Filters out all sources where a field's values matches or not a certain regular expression
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id$
 */
public class FieldValueFilter implements Filter {
    private static Logger log = Logging.getLoggerInstance(FieldValueFilter.class);

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
    public void configure(DocumentReader reader, Element e) {
        FilterUtils.propertiesConfigure(this, reader, e);
    }


    @Override
    final public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        List<URLComposer> filteredUrlcomposers = new ArrayList<URLComposer>();
        for (URLComposer urlcomposer : urlcomposers) {

            String value = urlcomposer.getSource().getStringValue(field);
            Matcher matcher = pattern.matcher(value);
            if (matcher.matches()) {
                if (inverse) {
                    filteredUrlcomposers.add(urlcomposer);
                }
            } else {
                if (! inverse) {
                    filteredUrlcomposers.add(urlcomposer);
                }
            }

        }
        return filteredUrlcomposers;
    }
}
