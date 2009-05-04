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
        setMin(new Integer(1), true);
        setMax(new Integer(7), true);
    }

    public Iterator getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {
        final Calendar cal = Calendar.getInstance(locale);
        final SortedMap bundle = SortedBundle.getResource("org.mmbase.datatypes.resources.weekdays", locale, null,
                                                          SortedBundle.getConstantsProvider(Calendar.class), Integer.class, null);

        return new Iterator() {
            int i = 0;
            int day = cal.getFirstDayOfWeek();
            public boolean hasNext() {
                return i < 7;
            }
            public Object next() {
                Entry res  = new Entry(new Integer(day), bundle.get(new Integer(day)));
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
