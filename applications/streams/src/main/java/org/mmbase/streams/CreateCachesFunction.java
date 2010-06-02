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
 * or just to transcode newly configured streams. The parameter 'cache' can hold the
 * node number of a singe caches node to retranscode.
 *
 * @author Michiel Meeuwissen
 * @author Andr&eacute; van Toly
 * @version $Id$
 */

public class CreateCachesFunction  extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesFunction.class);

    public final static Parameter[] PARAMETERS = { 
        new Parameter("all", java.lang.Boolean.class),
        new Parameter("cache", org.mmbase.bridge.Node.class)
    };
    public CreateCachesFunction() {
        super("createcaches", PARAMETERS);
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
            Node cache = (Node) parameters.get("cache");

            {
                final Field url = node.getNodeManager().getField("url");
                final Processor cc = getCacheCreator(url);                
                Map<String, JobDefinition> jdlist = cc.getCreatecachesList();

                if (cache != null && node.getCloud().hasNode(cache.getNumber())) {
                    // just one
                    String in = null;
                    Node inNode = cache.getNodeValue("id");
                    if (inNode.getNumber() != node.getNumber()) {
                        in = "" + inNode.getNumber();
                    }
                    String id     = "re-cache";
                    String label  = cache.getStringValue("label");
                    MimeType mt   = new MimeType( cache.getStringValue("mimetype") );
                    String key    = cache.getStringValue("key");
                    Transcoder tr = null;
                    try {
                        tr = AbstractTranscoder.getInstance(key);
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
        
                    JobDefinition jd = new JobDefinition(id, in, label, tr, mt, Stage.TRANSCODER);
                    jdlist.clear();
                    jdlist.put(id, jd);
                    LOG.info("Re-transcodig cache #" + cache.getNumber() + " : " + id + " [" + jd + "]");
                    
                } else {
                    // list
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
                
                    if ( list.size() > 0 && ! all ) {
                        jdlist = newJobList(list, jdlist);
                }
                    LOG.info("Recreating caches for #" + node.getNumber() + ", doing all: " + all);
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

    /**
     * Compares configuration with already transcoded (cached) streamsourcescaches nodes.
     *
     * @param list      nodelist of related streamsourcescaches nodes
     * @param jdlist    list based upon current configuration wit job descriptions 
     * @return a new list with jobs matched against already existing nodes
     */    
    private Map<String, JobDefinition> newJobList(NodeList list, Map<String, JobDefinition> jdlist) {
        Map<String, JobDefinition> new_jdlist = new LinkedHashMap<String, JobDefinition>();
        Map<String, String> caches = new HashMap<String, String>();
        Map<String, String> config = new HashMap<String, String>();
        // make keys from current config entries
        for (Map.Entry<String, JobDefinition> entry : jdlist.entrySet()) {
            String id = entry.getKey();
            JobDefinition jd = entry.getValue();
            String key = jd.getTranscoder().getKey();
            if (key != null && !"".equals(key)) {   // not recognizers 
                config.put(id, key);
            }
        }
        // for convenience make a map of caches keys 
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
                JobDefinition jd = jdlist.get(config_id);
                Transcoder tr = jd.getTranscoder();
                
                if (tr == null) {
                    try {
                        tr = AbstractTranscoder.getInstance(config_key);
                    } catch (ClassNotFoundException cnf) {
                        LOG.error("Class not found, transcoder in key '" + config_key + "' does not exist? - " + cnf);
                    } catch (InstantiationException ie) {
                        LOG.error("Exception while instantiating transcoder for key '" + config_key + "' - " + ie);
                    } catch (Exception ex) {
                        LOG.error("Exception while trying to (re)transcode - " + ex);
                    }                
                }
                
                String label = jd.getLabel(); 
                MimeType mt = jd.getMimeType();
                
                String inId = jd.getInId();
                String inKey = config.get(inId);
                
                // check if it's inId is already a cached node
                if (caches.containsValue(inKey)) {
                    String in = "";
                    for (Node n : list) {
                        if (n.getStringValue("key").equals(inKey)) {
                            in = "" + n.getNumber();
                            break;  // can only be 1
                        }
                    }
                    
                    jd = new JobDefinition(config_id, in, label, tr, mt, Stage.TRANSCODER);
                    if (! new_jdlist.containsKey(config_id)) {
                        new_jdlist.put(config_id, jd);
                        LOG.debug("Added id: " + config_id);
                    }
                    
                } else {
                    // inId not yet cached
                    if (! new_jdlist.containsKey(inId)) {
                        new_jdlist.put(inId, jdlist.get(inId) );
                        LOG.debug("Added inId: " + inId);
                    }
                    
                    if (! new_jdlist.containsKey(config_id)) {
                        new_jdlist.put(config_id, jdlist.get(config_id) );
                        LOG.debug("Added id: " + config_id);
                    }
                }
            }
        }

        return new_jdlist;
    }    
}
