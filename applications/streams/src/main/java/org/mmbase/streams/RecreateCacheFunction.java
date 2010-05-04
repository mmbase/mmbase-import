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

import java.lang.reflect.InvocationTargetException;
import java.util.*;


import org.mmbase.streams.createcaches.*;
import org.mmbase.streams.createcaches.Processor;
import org.mmbase.streams.transcoders.*;

import org.mmbase.util.MimeType;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.security.ActionRepository;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;

/**
 * Triggers recreation of a certain streamsourcescaches belonging to this streamsources node.
 *
 * @author Michiel Meeuwissen
 * @author André van Toly
 * @version $Id: RecreateCacheFunction.java 41117 2010-02-17 16:23:03Z michiel $
 */

public class RecreateCacheFunction extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(RecreateCacheFunction.class);

    // parameter definitions
    public final static Parameter[] RECACHE_PARAMETERS = { new Parameter("recache", org.mmbase.bridge.Node.class) };

    public RecreateCacheFunction() {
        super("recache", RECACHE_PARAMETERS);
    }

    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        LOG.debug("params: " + parameters);
        if (node.getNumber() > 0
                && node.getCloud().may(ActionRepository.getInstance().get("streams", "retrigger_jobs"), null)) {

            Node recache = (Node) parameters.get("recache");
            LOG.info("Recreating cache #" + recache.getNumber() + " for node #" + node.getNumber());
            final Field url = node.getNodeManager().getField("url");

            String in = null;
            Node inNode = recache.getNodeValue("id");
            if (inNode.getNumber() != node.getNumber()) {
                in = "" + inNode.getNumber();
            }
            String id        = "recache";
            String mimetype  = recache.getStringValue("mimetype");
            String key       = recache.getStringValue("key");
            Transcoder transcoder = null;
            try {
                transcoder = AbstractTranscoder.getInstance(key);
            } catch (ClassNotFoundException cnf) {
                LOG.error("Class not found, transcoder in key '" + key + "' does not exist? - " + cnf);
                return false;
            } catch (InstantiationException ie) {
                LOG.error("Exception while instantiating transcoder for key '" + key + "' - " + ie);
                return false;
            } catch (Exception e) {
                LOG.error("Exception while trying to (re)transcode - " + e);
                return false;
            }

            JobDefinition jd = new JobDefinition(id, in, null, transcoder, new MimeType(mimetype), Stage.TRANSCODER);
            Map<String, JobDefinition> jdlist = new LinkedHashMap<String, JobDefinition>();
            jdlist.put(id, jd);

            {
                final Processor cc = CreateCachesFunction.getCacheCreator(url);

                if (cc != null) {
                    LOG.service("Calling " + cc);
                    cc.createCaches(node.getCloud().getNonTransactionalCloud(), node.getNumber(), jdlist);

                    return true;
                } else {
                    LOG.error("No CreateCacheProcessor in " + url);
                    return false;
                }
            }

        } else {
            return false;
        }
    }

}
