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

    private static final Parameter<String> URL = new Parameter<String>("url", String.class);
    private static final Parameter<Boolean> CACHE = new Parameter<Boolean>("cache", Boolean.class, Boolean.FALSE);
    public final static Parameter[] PARAMETERS = { URL, CACHE };

    public CreateSourcesWithoutProcessFunction() {
        super("createsources", PARAMETERS);
    }

    @Override
    public Boolean getFunctionValue(final Node media, Parameters parameters) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }
        Boolean cache = parameters.get(CACHE);
        String url = parameters.get(URL);

        Node source = getMediaSource(media, cache);
        if (!cache) {
            source.setValueWithoutProcess("url", url);
            LOG.service("New url, not transcoding new streams for #" + media.getNumber());
        } else {
            source.setStringValue("url", url);
            LOG.service("New url for source of #" + media.getNumber() + ", will transcode caches.");
        }
        source.commit();

        return true;
    }

    public static Node getMediaSource(final Node mediafragment) {
        return getMediaSource(mediafragment, false);
    }
    /**
     * The media source node of this mediafragment, containing the original stream or file.
     *
     * @param   mediafragment Media node to get source from
     * @param   cache To (re)create streamsourcescaches or not
     * @return  mediasources node related to this mediafragment
     */
    public static Node getMediaSource(final Node mediafragment, Boolean cache) {
        Node src = null;

        if (!cache) {
            mediafragment.getCloud().setProperty(org.mmbase.streams.createcaches.Processor.NOT, "no implicit processing please");
        } else if (mediafragment.getCloud().getProperty(org.mmbase.streams.createcaches.Processor.NOT) != null) {
                 mediafragment.getCloud().setProperty(org.mmbase.streams.createcaches.Processor.NOT, null);
        }

        NodeList list = SearchUtil.findRelatedNodeList(mediafragment, "mediasources", "related");
        if (list.size() > 0) {
            if (list.size() > 1) {
                NodeManager nm = mediafragment.getNodeManager();
                NodeManager videofragments = mediafragment.getCloud().getNodeManager("videofragments");
                NodeManager audiofragments = mediafragment.getCloud().getNodeManager("audiofragments");
                NodeManager imagefragments = mediafragment.getCloud().getNodeManager("imagefragments");
                if (nm.equals(videofragments)) {
                    list = SearchUtil.findRelatedNodeList(mediafragment, "videosources", "related");
                } else if (nm.equals(audiofragments)) {
                    list = SearchUtil.findRelatedNodeList(mediafragment, "audiosources", "related");
                } else if (nm.equals(imagefragments)) {
                    list = SearchUtil.findRelatedNodeList(mediafragment, "imagesources", "related");
                }
                if (list.size() == 0) {
                    LOG.warn("Nothing found in second try!");
                    list = SearchUtil.findRelatedNodeList(mediafragment, "mediasources", "related");
                }

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
