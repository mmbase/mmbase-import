/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * An enumeration datatype representing all days of a week, so an integer with the value 1 through
 * 7. It considers the locale to determin which day is to be the first of the week, and hence the
 * first in the enumeration.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */
public class WeekdaysDataType extends IntegerDataType {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public WeekdaysDataType(String name) {
        super(name, false);
        setMin(1, true);
        setMax(7, true);
    }

    public Iterator<Map.Entry<Integer, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        final Calendar cal = Calendar.getInstance(locale);
        final SortedMap<Object, String> bundle = SortedBundle.getResource("org.mmbase.datatypes.resources.weekdays", locale, null,
                                                                          SortedBundle.getConstantsProvider(Calendar.class), Integer.class, null);

        return new Iterator<Map.Entry<Integer, String>>() {
            int i = 0;
            int day = cal.getFirstDayOfWeek();
            public boolean hasNext() {
                return i < 7;
            }
            public Map.Entry<Integer, String> next() {
                Entry res  = new Entry(day, bundle.get(day));
                i++;
                day++;
                if (day > 7) day = 1;
                return res;
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


}
