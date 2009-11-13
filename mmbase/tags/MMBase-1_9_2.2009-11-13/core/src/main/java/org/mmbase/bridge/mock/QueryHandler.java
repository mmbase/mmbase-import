/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.Query;

/**
 * Interface for handling of Queries. A bit similar to {@link
 * org.mmbase.storage.search.SearchQueryHandler} but that one requires us to produces
 * MMObjectNode's, so we consider it unusable for this 'mock' implementation of the bridge.
 *
 * This version only requires you to produce a List of Maps when handling a Query.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

interface QueryHandler  {


    List<Map<String, Object>> getRecords(Query query);


}
