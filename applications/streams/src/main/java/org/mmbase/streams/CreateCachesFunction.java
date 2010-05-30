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

import java.util.*;

import org.mmbase.streams.createcaches.Stage;
import org.mmbase.streams.createcaches.Processor;
import org.mmbase.streams.createcaches.JobDefinition;
import org.mmbase.streams.transcoders.*;

import org.mmbase.util.MimeType;
import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.security.ActionRepository;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;

/**
 * Triggers (re)creation of caches (streamsourcescaches) of a source node 
 * (streamsources). The parameter 'all' determines whether to recreate all caches
 * or just to transcode newly configured streams.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateCachesFunction extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesFunction.class);

    public final static Parameter[] CACHE_PARAMETERS = { new Parameter("all", java.lang.Boolean.class) };
    public CreateCachesFunction() {
        super("createcaches", CACHE_PARAMETERS);
    }

    /**
     * CommitProcessor is on url field of source node.
     * @param url   field url of source node
     * @return Processor to (re)create caches nodes
     */
    protected static Processor getCacheCreator(final Field url) {
        CommitProcessor commitProcessor = url.getDataType().getCommitProcessor();
        if (commitProcessor instanceof ChainedCommitProcessor) {
            ChainedCommitProcessor chain = (ChainedCommitProcessor) commitProcessor;
            LOG.service("Lookin in " + chain.getProcessors());
            for (CommitProcessor cp : chain.getProcessors()) {
                if (cp instanceof Processor) {
                    return (Processor) cp;
                }
            }
            return null;
        } else {
            if (commitProcessor instanceof Processor) {
                return (Processor) commitProcessor;
            } else {
                return null;
            }
        }
    }

    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        LOG.debug("params: " + parameters);
        if (node.getNumber() > 0 
                && node.getCloud().may(ActionRepository.getInstance().get("streams", "retrigger_jobs"), null)) {
            
            Boolean all = (Boolean) parameters.get("all");
            LOG.info("Recreating caches for #" + node.getNumber() + ", doing all: " + all);
            final Field url = node.getNodeManager().getField("url");

            {
                Node mediafragment = node.getNodeValue("mediafragment");
                String cachestype = node.getNodeManager().getProperty("org.mmbase.streams.cachestype");
                NodeList list = SearchUtil.findRelatedNodeList(mediafragment, cachestype, "related"); 
                
                // when the streamsourcescaches are initially of the wrong type they don't get deleted, this helps a bit
                if (list.size() < 1) {
                    if (cachestype.startsWith("video")) {
                        list = SearchUtil.findRelatedNodeList(mediafragment, "audiostreamsourcescaches", "related");
                    } else if (cachestype.startsWith("audio")) {
                        list = SearchUtil.findRelatedNodeList(mediafragment, "videostreamsourcescaches", "related");
                    }
                }

                final Processor cc = getCacheCreator(url);                
                Map<String, JobDefinition> jdlist = cc.getCreatecachesList();
                if ( list.size() > 0 && ! all ) {
                    jdlist = newJobList(list, jdlist);
                }
                
                if (cc != null) {
                    LOG.service("Calling " + cc);
                    cc.createCaches(node.getCloud().getNonTransactionalCloud(), node.getNumber(), jdlist);
                    return true;
                } else {
                    LOG.error("No CreateCachesProcessor in " + url);
                    return false;
                }
            }
        } else {
            return false;
        }
    }
    
    private Map<String, JobDefinition> newJobList(NodeList list, Map<String, JobDefinition> jdlist) {
        Map<String, JobDefinition> new_jdlist = new LinkedHashMap<String, JobDefinition>();
        // make keys from current config entries
        Map<String, String> config = new HashMap<String, String>();
        for (Map.Entry<String, JobDefinition> entry : jdlist.entrySet()) {
            String id = entry.getKey();
            JobDefinition jd = entry.getValue();
            String key = jd.getTranscoder().getKey();
            if (key != null && !"".equals(key)) {   // not recognizers 
                config.put(id, key);
            }
        }
        // for convenience make a map of caches keys 
        Map<String, String> caches = new HashMap<String, String>();
        for (Node cache : list) {
            caches.put("" + cache.getNumber(), cache.getStringValue("key"));
        }
        
        // iterate over config keys
        Iterator<Map.Entry<String,String>> it = config.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> e = it.next();
            String config_id  = e.getKey();
            String config_key = e.getValue();
            
            if (config_key != null && !caches.containsValue(config_key)) {
                // not in caches, must be new config
                LOG.info("@ (new config) not in caches : " + config_id + " [" + config_key + "]");
                
                JobDefinition jd = jdlist.get(config_id);
                Transcoder tr = jd.getTranscoder(); 
                String label = jd.getLabel(); 
                MimeType mt = jd.getMimeType();
                
                String inId = jd.getInId();
                String inKey = config.get(inId);
                
                // check if it's inId is already a cached node
                if (caches.containsValue(inKey)) {
                    LOG.info("@ in new_jdlist: " + inId + " [" + inKey + "]");
                    String in = "";
                    for (Node n : list) {
                        if (n.getStringValue("key").equals(inKey)) {
                            in = "" + n.getNumber();
                            LOG.info("@ cache as source node #" + in);
                            break;
                        }
                    }
                    
                    jd = new JobDefinition(config_id, in, label, tr, mt, Stage.TRANSCODER);
                    if (! new_jdlist.containsKey(config_id)) {
                        new_jdlist.put(config_id, jd);
                        LOG.info("@ put in new_jdlist: " + config_id);
                    }
                    
                } else {
                    // inId not yet cached
                    if (! new_jdlist.containsKey(inId)) {
                        new_jdlist.put(inId, jdlist.get(inId) );
                        LOG.info("@ inId in new_jdlist: " + inId);
                    }
                    
                    if (! new_jdlist.containsKey(config_id)) {
                        new_jdlist.put(config_id, jdlist.get(config_id) );
                        LOG.info("@ config_id in new_jdlist: " + config_id);
                    }
                }
            }
        }
        return new_jdlist;
    }    
}
