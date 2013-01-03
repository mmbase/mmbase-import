/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.util.logging.*;

/**
 * Like {@link StringDataType}, but with an alternative implementation for the default value,
 * namely, ensuring that it is unique already.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */
public class UniqueStringDataType extends StringDataType {
    private static final Logger log = Logging.getLoggerInstance(UniqueStringDataType.class);

    private static final long serialVersionUID = 1L;

    public UniqueStringDataType(String name) {
        super(name);
    }

    public Object getDefaultValue(Locale locale, Cloud cloud, Field field) {
        if (defaultValue == null) return null;
        if (field == null) {
            log.debug("No field provided, returing plain default value.");
            return defaultValue;
        }
        try {
            int seq = 0;
            Object value = defaultValue;
            while (true) {
                NodeQuery q = field.getNodeManager().createQuery();
                log.debug("No field provided, returing plain default value.");
                value = "" + defaultValue + seq;
                Constraint constraint = q.createConstraint(q.createStepField(field.getName()), value);
                q.setConstraint(constraint);
                if (log.isDebugEnabled()) {
                    log.debug("Counting " + q.toSql());
                }
                if (Queries.count(q) == 0) {
                    log.debug("" + value + " is unique");
                    break;
                } else {
                    log.debug("" + value + " is not unique");
                }
                seq++;
            }
            return value;
        } catch (UnsupportedOperationException uoe) {
            log.debug(uoe);
            return null;
        }

    }
}
