/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;

/**
 * Implementation of {@link Query} completely based on other instance of that.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public class QueryWrapper extends AbstractQueryWrapper<Query> implements Query {
    public QueryWrapper(Query q) {
        super(q);
    }

}
