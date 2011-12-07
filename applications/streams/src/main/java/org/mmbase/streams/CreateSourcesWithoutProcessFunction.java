/*

This file is part of the MMBase Streams application,
which is part of MMBase - an open source content management system.
    Copyright (C) 2011 André van Toly, Michiel Meeuwissen

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

import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Function on mediafragments to create a streamsources nodes without processing
 * the stream and creating (transcoding into) streamsourcescaches.
 * It looks for existing streamsources nodes and asigns them the new url.
 *
 * @author Andr&eacute; van Toly
 * @version $Id:  $
 */
public class CreateSourcesWithoutProcessFunction extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(CreateSourcesWithoutProcessFunction.class);

    public final static Parameter[] PARAMETERS = {
        new Parameter("url", java.lang.String.class)
    };
    public CreateSourcesWithoutProcessFunction() {
        super("createsources", PARAMETERS);
    }

    @Override
    public Boolean getFunctionValue(final Node media, Parameters parameters) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }

        Cloud cloud = media.getCloud();
        cloud.setProperty(org.mmbase.streams.createcaches.Processor.NOT, "no implicit processing please");

        String url = (String) parameters.get("url");
        Node source = getMediaSource(media);

        source.setValueWithoutProcess("url", url);
        source.commit();

        return true;
    }

    public static Node getMediaSource(final Node mediafragment) {
        Node src = null;
        NodeList list = SearchUtil.findRelatedNodeList(mediafragment, "mediasources", "related");
        if (list.size() > 0) {
            if (list.size() > 1) {
                LOG.warn(list.size() + " mediasources nodes found, using the first.");
            }
            src = list.get(0);
            if (src.getNodeValue("mediafragment") != mediafragment) {
                src.setNodeValue("mediafragment", mediafragment);
            }
            LOG.service("Found existing source " + src.getNodeManager().getName() + " #" + src.getNumber());
        } else {
            // create node
            src = mediafragment.getCloud().getNodeManager("streamsources").createNode();
            src.setNodeValue("mediafragment", mediafragment);
            LOG.service("Created source " + src.getNodeManager().getName() + " #" + src.getNumber());
        }

        return src;
    }
}
