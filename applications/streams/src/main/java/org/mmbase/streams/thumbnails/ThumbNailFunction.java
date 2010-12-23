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
 * @version $Id: CreateCachesFunction.java 43817 2010-11-19 11:45:01Z andre $
 */

public class ThumbNailFunction extends NodeFunction<Node> {

    private static final Logger LOG = Logging.getLoggerInstance(ThumbNailFunction.class);

    public final static Parameter<Long> OFFSET = new Parameter<Long>("offset", java.lang.Long.class);
    public final static Parameter<Boolean> WAIT = new Parameter<Boolean>("wait", java.lang.Boolean.class, Boolean.TRUE);
    public final static Parameter[] PARAMETERS = { OFFSET, WAIT };
    public ThumbNailFunction() {
        super("thumbnail", PARAMETERS);
    }


    protected Node getSourceNode(Node node) {
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
            }
        } else if (manager.equals(videosources) || videosources.getDescendants().contains(manager)) {
            return node;
        } else if (manager.equals(videocaches) || videocaches.getDescendants().contains(manager)) {
            Node fragment = node.getNodeValue("mediafragment");
            return getSourceNode(fragment);
        }
        LOG.warn("Could not determin source node for " + node);
        return null;
    }

    @Override
    protected Node getFunctionValue(final Node node, final Parameters parameters) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }
        Node sourceNode = getSourceNode(node);
        if (sourceNode == null) {
            return null;
        }
        long videoLength = node.getLongValue("length");

        Long offset = parameters.get(OFFSET);

        if (offset == null) {
            offset = videoLength / 2;
        }


        // round of the offset a bit, otherwise it would be possible to create a thumbnail for every millisecond, which
        // seems a bit overdone....
        if (videoLength > 1000) {
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
                thumb = thumbs.createNode();
                thumb.setValue("id", sourceNode);
                thumb.setValue("time", offset);
                thumb.commit();
            } else {
                thumb = thumbNodes.get(0);
            }
        }
        if (thumb.isNull("handle")) {
            ThumbNailCallable callable = new ThumbNailCallable(thumb, thumbs.getField("handle"));
            Future<Node> future = Executors.submit(Stage.RECOGNIZER, callable);
            if (parameters.get(WAIT)) {

                try {
                    future.get(); // wait for result
                } catch (InterruptedException ie) {
                    LOG.warn(ie.getMessage(), ie);
                } catch (ExecutionException ee) {
                    LOG.error(ee.getMessage(), ee);
                }
            }
        }
        return thumb;


    }


}
