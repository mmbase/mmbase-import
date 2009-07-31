/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import java.util.Date;

/**
 * Get-processor for 'created' field. Using day-markers to fill the field.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class CreationTimeGuesser implements Processor {

    private static final Logger log = Logging.getLoggerInstance(CreationTimeGuesser.class);

    private static final long serialVersionUID = 1L;


    public final Object process(Node node, Field field, Object value) {
        Object v = node.getValueWithoutProcess(field.getName());
        if (v == null) {
            int age = node.getFunctionValue("age", null).toInt();
            Date creationTime = new Date(System.currentTimeMillis() - 24L * 60 * 60 * 1000 * age);
            // could try to save it here.
            if (node.mayWrite() && ! field.isVirtual()) {
                boolean c = node.isChanged();
                node.setValueWithoutProcess(field.getName(), creationTime);
                if (! c) {
                    node.commit();
                }
            }
            value = 
                value == null ?
                creationTime :
                org.mmbase.util.Casting.toType(value.getClass(), node.getCloud(), creationTime); // return in similar type

            log.debug("Guessing creation time : " + age + " days  --> " + value);
        }
        return value;
    }

    @Override
    public String toString() {
        return "CreationTime";
    }
}


