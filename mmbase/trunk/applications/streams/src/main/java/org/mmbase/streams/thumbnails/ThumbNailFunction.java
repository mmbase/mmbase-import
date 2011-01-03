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

import java.io.*;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import org.mmbase.streams.createcaches.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class ThumbNailFunction extends NodeFunction<Node> {

    private static final Logger LOG = Logging.getLoggerInstance(ThumbNailFunction.class);

    public final static Parameter<Long> OFFSET = new Parameter<Long>("offset", java.lang.Long.class);
    public final static Parameter<Boolean> WAIT = new Parameter<Boolean>("wait", java.lang.Boolean.class, Boolean.FALSE);
    public final static Parameter[] PARAMETERS = { OFFSET, WAIT };
    public ThumbNailFunction() {
        super("thumbnail", PARAMETERS);
    }


    protected static Node getSourceNode(Node node) {
        if (node == null) {
            return null;
        }
        NodeManager videofragments = node.getCloud().getNodeManager("videofragments");
        NodeManager videosources   = node.getCloud().getNodeManager("videostreamsources");
        NodeManager videocaches    = node.getCloud().getNodeManager("videostreamsourcescaches");
        NodeManager  manager = node.getNodeManager();
        if (manager.equals(videofragments) || videofragments.getDescendants().contains(manager)) {
            Node root = node.getFunctionValue("root", null).toNode();
            NodeList related = root.getRelatedNodes("videostreamsources", "related", "destination");
            if (! related.isEmpty()) {
                return related.get(0);
            } else {
                //  simply does not have any sources
                return null;
            }
        } else if (manager.equals(videosources) || videosources.getDescendants().contains(manager)) {
            return node;
        } else if (manager.equals(videocaches) || videocaches.getDescendants().contains(manager)) {
            Node fragment = node.getNodeValue("mediafragment");
            return getSourceNode(fragment);
        }
        LOG.warn("Could not determin source node for " + manager + " " + node);
        return null;
    }
    private static Node getDefault(Cloud cloud) {
        if (cloud.hasNode("default_video_thumbnail")) {
            return cloud.getNode("default_video_thumbnail");
        } else {
            return null;
        }
    }

    static Node getThumbNail(final Node node, Long offset) {
        Node sourceNode = getSourceNode(node);
        if (sourceNode == null) {
            return getDefault(node.getCloud());
        }
        long videoLength = node.getLongValue("length");

        if (offset == null) {
            offset = videoLength / 2;
        }


        // round of the offset a bit, otherwise it would be possible to create a thumbnail for every millisecond, which
        // seems a bit overdone....
        if (videoLength > 5000) {
            offset = 1000 * (offset / 1000); // round off to seconds
        } else {
            offset = 100 * (offset / 100);
        }


        Cloud myCloud = node.getCloud().getCloudContext().getCloud("mmbase", "class", null);
        NodeManager thumbs = myCloud.getNodeManager("thumbnails");
        Node thumb;
        synchronized(ThumbNailFunction.class) {
            NodeQuery q = thumbs.createQuery();
            Queries.addConstraint(q, q.createConstraint(q.getStepField(thumbs.getField("id")), sourceNode));
            Queries.addConstraint(q, q.createConstraint(q.getStepField(thumbs.getField("time")), offset));
            List<Node> thumbNodes = thumbs.getList(q);
            if (thumbNodes.isEmpty()) {
                File input = (File) sourceNode.getFunctionValue("file", null).get();
                if (input == null || ! input.exists() || ! input.canRead() || input.length() == 0) {
                    LOG.debug("Source seems broken, no point in creating a thumbnail");
                    return getDefault(node.getCloud());
                }
                thumb = thumbs.createNode();
                thumb.setValue("id", sourceNode);
                thumb.setValue("time", offset);
                thumb.setValue("height", sourceNode.getIntValue("height"));
                thumb.setValue("width",  sourceNode.getIntValue("width"));
                thumb.commit();
                LOG.service("Created thumbnail " + thumb.getNumber());
            } else {
                thumb = thumbNodes.get(0);
            }
        }
        return thumb;
    }

    @Override
    protected Node getFunctionValue(final Node node, final Parameters parameters) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }

        Node thumb = getThumbNail(node, parameters.get(OFFSET));

        if (parameters.get(WAIT)) {
            LOG.service("Waiting");
            WaitFunction.wait(thumb);
        }
        return thumb;


    }


}
