/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import java.util.*;
import org.mmbase.bridge.Field;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class MSSqlSqlHandler extends BasicSqlHandler implements SqlHandler {

    private static final Logger log = Logging.getLoggerInstance(MSSqlSqlHandler.class);

    /**
     * Don't add UPPER'ed field also unuppered, because MSSql seems to choke in that.
     *
     * We can also consider removing that odd behaviour from super.
     */
    protected StringBuffer appendSortOrderField(StringBuffer sb, SortOrder sortOrder, boolean multipleSteps) {
         boolean uppered = false;
         if (! sortOrder.isCaseSensitive() && sortOrder.getField().getType() == Field.TYPE_STRING) {
             sb.append("UPPER(");
             uppered = true;
         }
         // Fieldname.
         appendField(sb, sortOrder, multipleSteps);
         if (uppered) {
             sb.append("),");
         }
         return sb;
    }

}
