/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;


/**
 * This commit-processor is used on node of the type 'streamsources', and is used to delete
 * associated streamsourcescaches
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class DeleteCachesProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    public static String NOT = DeleteCachesProcessor.class.getName() + ".DONOT";

    private static final Logger LOG = Logging.getLoggerInstance(DeleteCachesProcessor.class);


    public void commit(final Node node, final Field field) {
        if (node.getCloud().getProperty(NOT) != null) {
            LOG.service("Not doing because of property");
            return;
        }
        if (node.getNumber() > 0) {
            LOG.service("Deleting streamsources #" + node.getNumber());
            final NodeManager caches = node.getCloud().getNodeManager("streamsourcescaches");
            NodeQuery q = caches.createQuery();
            Queries.addConstraint(q, Queries.createConstraint(q, "id", FieldCompareConstraint.EQUAL, node));
            for (Node cache : caches.getList(q)) {
                LOG.service("Deleting associated streamsourcescaches #" + cache.getNumber());
                if (cache.mayDelete()) {
                    cache.delete(true);
                } else {
                    LOG.warn("May not delete " + cache);
                }
            }
        }
    }

}
