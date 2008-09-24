/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import org.mmbase.util.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * The set- and get- processors implemented in this file can be used to make a virtual 'age' field.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.5
 */

public class Age {

    private static final Logger log = Logging.getLoggerInstance(Age.class);

    public static class Setter implements Processor {

        private static final long serialVersionUID = 1L;

        private String birthdateField = "birthdate";

        public void setBirthdateField(String f) {
            birthdateField = f;
        }

        public Object process(Node node, Field field, Object value) {
            if (! node.isChanged(birthdateField)) {
                log.debug("setting age to " + value);
                try {
                    // educated guess for the birth date:
                    Date date = DynamicDate.getInstance("today - 6 month - " + value + " year");
                    node.setDateValue(birthdateField, date);
                } catch (org.mmbase.util.dateparser.ParseException pe) {
                }
            }
            return value;
        }
    }

    public static class Getter implements Processor {
        private static final long serialVersionUID = 1L;

        private String birthdateField = "birthdate";

        public void setBirthdateField(String f) {
            birthdateField = f;
        }

        public Object process(Node node, Field field, Object value) {
            if (value == null) return null;
            Date birthDate = node.getDateValue(birthdateField);
            Date now = new Date();
            int age = (int) Math.floor((double) (now.getTime() - birthDate.getTime()) / (1000 * 3600 * 24 * 365.25));
            log.debug("getting age for " + node + " --> " + age);
            return Casting.toType(value.getClass(), new Integer(age));
        }
    }

}
