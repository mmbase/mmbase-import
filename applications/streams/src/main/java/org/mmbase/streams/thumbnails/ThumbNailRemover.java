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

package org.mmbase.streams.thumbnails;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.core.event.*;
import org.mmbase.util.logging.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class ThumbNailRemover implements NodeEventListener {

    private static final Logger LOG = Logging.getLoggerInstance(ThumbNailRemover.class);

    private static Set<String> builders = new HashSet<String>(Arrays.asList(new String[]{"videostreamsources"}));


    @Override
    public void notify(NodeEvent ne) {
        if (ne.getType() == Event.TYPE_DELETE) {
            if (builders.contains(ne.getBuilderName())) {
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
                NodeManager thumbs = cloud.getNodeManager("thumbnails");
                NodeQuery q = thumbs.createQuery();
                Queries.addConstraint(q, q.createConstraint(q.getStepField(thumbs.getField("id")), ne.getNodeNumber()));
                for (Node thumb : thumbs.getList(q)) {
                    LOG.service("Deleting " + thumb.getNumber());
                    thumb.delete(true);
                }

            }
        }
        if (ne.getType() == Event.TYPE_CHANGE) {
            if ("thumbnails".equals(ne.getBuilderName()) && ne.getChangedFields().contains("time")) {
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
                Node node = cloud.getNode(ne.getNodeNumber());
                if (! node.isNull("handle")) {
                    node.setValue("handle", null);
                    node.commit();
                }
            }
        }


    }


}
