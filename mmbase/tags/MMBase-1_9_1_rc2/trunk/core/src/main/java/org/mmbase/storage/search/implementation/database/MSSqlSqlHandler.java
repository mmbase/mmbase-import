/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import org.mmbase.bridge.Field;
import org.mmbase.storage.search.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class MSSqlSqlHandler extends BasicSqlHandler implements SqlHandler {

    /**
     * Don't add UPPER'ed field also unuppered, because MSSql seems to choke in that.
     *
     * We can also consider removing that odd behaviour from super.
     */
    @Override
    protected StringBuilder appendSortOrderField(StringBuilder sb, SortOrder sortOrder, boolean multipleSteps) {
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
