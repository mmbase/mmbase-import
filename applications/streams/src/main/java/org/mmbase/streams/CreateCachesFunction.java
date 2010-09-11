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
import org.mmbase.applications.media.State;

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

public class CreateCachesFunction extends NodeFunction<Boolean> {

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
        if (LOG.isDebugEnabled()) {
            LOG.debug("params: " + parameters);
        }
        if (node.getNumber() > 0 
                && node.getCloud().may(ActionRepository.getInstance().get("streams", "retrigger_jobs"), null)) {
            
            Boolean all = (Boolean) parameters.get("all");
            Node cache = (Node) parameters.get("cache");

            {
                final Field url = node.getNodeManager().getField("url");
                final Processor cc = getCacheCreator(url);                
                Map<String, JobDefinition> jdlist = new LinkedHashMap<String, JobDefinition>();

                if (cache != null && node.getCloud().hasNode(cache.getNumber())) {
                    // just one
                    LOG.info("Re-transcodig cache #" + cache.getNumber());
                    NodeList list = cache.getNodeManager().createNodeList();
                    list.add(cache);
                    jdlist = newJobList(node, list, cc.getConfiguration());
                    
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
                        for (Node n : list) {
                            n.delete(true);
                            LOG.service("Deleted " + n.getNumber());
                        }
                        list.clear();
                    }
    
                    LOG.info("Re-transcoding "+ list.size() + " caches for source #" + node.getNumber() + ", all: " + all);
                    if ( list.size() > 0 && ! all ) {
                        jdlist = newJobList(node, list, cc.getConfiguration());
                    } else {
                        jdlist = cc.getConfiguration();
                    }
                }
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("jdlist: " + jdlist);
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
     * Compares configuration with already transcoded (cached) streamsourcescaches nodes and makes
     * a new list with {$link JobDefinition}s.
     *
     * @param src       source node
     * @param list      streamsourcescaches nodes to re-transcode
     * @param jdlist    list with current configured job descriptions
     * @return a new list with jobs matched against already existing nodes if needed
     */
    private static Map<String, JobDefinition> newJobList(Node src, NodeList list, 
            Map<String, JobDefinition> jdlist) {
        Map<String, JobDefinition> new_jdlist = new LinkedHashMap<String, JobDefinition>();
        Map<Node, String> cacheKeys = new HashMap<Node, String>();
        Map<String, String> configKeys = new HashMap<String, String>();
        
        // current config: id/keys
        for (Map.Entry<String, JobDefinition> entry : jdlist.entrySet()) {
            String id = entry.getKey();
            JobDefinition jd = entry.getValue();
            String key = jd.getTranscoder().getKey();
            if (key != null && !"".equals(key)) {
                //LOG.debug("config: " + id + ", key: " + key);
                configKeys.put(id, key);
            }
        }
        
        // current caches: cache/key
        Node mediafragment = src.getNodeValue("mediafragment");
        String cachestype = src.getNodeManager().getProperty("org.mmbase.streams.cachestype");
        NodeList cachesNodeList = SearchUtil.findRelatedNodeList(mediafragment, cachestype, "related");         
        for (Node cache : cachesNodeList) {
            String key = cache.getStringValue("key");
            //LOG.debug("Already existing cache #" + cache.getNumber() + ", key: " + key);
            cacheKeys.put(cache, key);
        }
        
        if (list.size() > 1) {
            // iterate config keys, look for not yet existing caches
            Iterator<Map.Entry<String,String>> it = configKeys.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> e = it.next();
                String config_id  = e.getKey();
                String config_key = e.getValue();
                
                if (! cacheKeys.containsValue(config_key)) {   // make new
                    LOG.debug("Not found in caches: " + config_id);
                    JobDefinition jd = jdlist.get(config_id);
                    if (!new_jdlist.containsKey(jd.getId())) {
                        //new_jdlist.putAll( newJobDefsList(jd, jdlist, cacheKeys) );
                        
                        new_jdlist.putAll( inJobDefsList(jd, jdlist, configKeys, cacheKeys) );
                        new_jdlist.putAll( outJobDefsList(jd, jdlist, configKeys, cacheKeys) );
                        
                        LOG.info("New config, added for transcoding id: " + config_id + ", new_jdlist now: " + new_jdlist);
                    }
                }
            }
            
            
        } else {    // just one node: do always
            Node stream = list.get(0);
            String key = stream.getStringValue("key");
            
            if (configKeys.containsValue(key)) {
                LOG.debug("Found for #" + stream.getNumber() + " in config: " + key);
                
                // get config id
                Iterator<Map.Entry<String,String>> it = configKeys.entrySet().iterator();
                String id = "";
                while (it.hasNext()) {
                    Map.Entry<String,String> e = it.next();
                    if (key.equals(e.getValue())) {
                        id = e.getKey();
                    }
                }
                JobDefinition jd = jdlist.get(id);
                new_jdlist.putAll( inJobDefsList(jd, jdlist, configKeys, cacheKeys) );
                // check this jd if its an inId of another
                new_jdlist.putAll( outJobDefsList(jd, jdlist, configKeys, cacheKeys) );

                LOG.info("Added for re-transcoding id: " + id + ", new_jdlist now: " + new_jdlist);

            } 
        }
        
        /* last resort (for just 1 node): re-transcode based on cache key
           nothing matched in config but we have a cache to re-transcode */
        if (new_jdlist.isEmpty() && list.size() == 1) {
            Node cache = list.getNode(0);
            String in = "";
            Node inNode = cache.getNodeValue("id");
            if (inNode.getNumber() != src.getNumber()) {
                in = "" + inNode.getNumber();
            }
            String id     = "re-cache";
            String key    = cache.getStringValue("key");
            String label  = cache.getStringValue("label");
            MimeType mt   = new MimeType( cache.getStringValue("mimetype") );
            
            Transcoder tr = null;
            try {
                tr = AbstractTranscoder.getInstance(key);
            } catch (ClassNotFoundException cnf) {
                LOG.error("Class not found, transcoder in key '" + key + "' does not exist? - " + cnf);
            } catch (InstantiationException ie) {
                LOG.error("Exception while instantiating transcoder for key '" + key + "' - " + ie);
            } catch (Exception e) {
                LOG.error("Exception while trying to (re)transcode - " + e);
            }
            
            JobDefinition jd = new JobDefinition(id, in.length() > 0 ? in : null, label.length() > 0 ? label : null, tr, mt, Stage.TRANSCODER);
            new_jdlist.put(id, jd);
            LOG.info("Added for re-transcoding (based on key) id: " + id + "[" + jd + "]");
 
        }
        return new_jdlist;
    }
    
    /**
     * Makes a list with {$link JobDefinition}s of streams that are needed for this one just based on config.
     * Compares config with already cached streams, if found that node as in node for the new jobdefinition,
     * or else uses config to add a jobdefinition to create that stream.  
     *
     * @param jobdef    job definition we need parent job definitions for
     * @param jdlist    list with job definitions to look in
     * @param configKeys
     * @param cacheKeys    already existing nodes and their keys
     * @return a new list with jobs matched against already existing nodes
     */
    private static Map<String, JobDefinition> inJobDefsList(JobDefinition jobdef, Map<String, JobDefinition> jdlist, Map<String, String> configKeys, Map<Node, String> cacheKeys) {
        
        Map<String, JobDefinition> injds = new LinkedHashMap<String, JobDefinition>();
        String inId = jobdef.getInId();
        
        int c = 0;
        while (jdlist.get(inId) != null && c < 5) {
            
            String inKey = configKeys.get(inId);
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("This jd has inId: " + inId);
            }
    
            if (cacheKeys.containsValue(inKey)) {      // source node
                Node inNode = null;
                Iterator<Map.Entry<Node,String>> ic = cacheKeys.entrySet().iterator();
                while (ic.hasNext()) {
                    Map.Entry<Node,String> e = ic.next();
                    if (inKey.equals(e.getValue())) {
                        inNode = e.getKey();
                    }
                }
                if (LOG.isDebugEnabled()) { 
                    LOG.debug("inId is cached as #" + inNode.getNumber());
                }
                if (inNode.getIntValue("state") > State.BUSY.getValue()) {
                    String id     = jobdef.getId();
                    Transcoder tr = jobdef.getTranscoder(); 
                    String label  = jobdef.getLabel(); 
                    MimeType mt   = jobdef.getMimeType();
                    List<Analyzer> analyzers = jobdef.getAnalyzers();
                    LOG.debug("analyzers: "  + analyzers);
            
                    jobdef = new JobDefinition(id, "" + inNode.getNumber(), label, tr, mt, Stage.TRANSCODER);
                    jobdef.addAnalyzers(analyzers);
                    injds.put(id, jobdef);
                    
                    break;  // return just jobdef itself with new inId
                    
                } else {
                    // in not ready, do everything based on config
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("infile not ready " + inNode.getStringValue("state"));
                    }
                }
                
                
            }
            
            injds.put(inId, jdlist.get(inId));
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added inId: " + inId);
            }
            
            inId = jdlist.get(inId).getInId();  // get inId of this inId
            c++;
        }
        if (LOG.isDebugEnabled()) { 
            LOG.debug("injds now: " + injds); 
        }
        
        Map<String, JobDefinition> reverse = new LinkedHashMap<String, JobDefinition>();
        for (int i = injds.size() - 1; i >= 0; i--) {
            reverse.put((String)injds.keySet().toArray()[i], (JobDefinition)injds.values().toArray()[i]);
        }
        
        //return injds;
        return reverse;
    }
    
    /**
     * Makes a list with {$link JobDefinition}s of streams that need (result from) this one, based on config.
     * Iterates over config, looks for jd with inId and if that's already cached, or else
     * put it in new jdlist.
     *
     * @param jobdef    job definition we need child job definitions for
     * @param jdlist    list with job definitions to look in
     * @return list with job definitions that result from this one
     */
     private static Map<String, JobDefinition> outJobDefsList(JobDefinition jobdef, Map<String, JobDefinition> jdlist, Map<String, String> configKeys, Map<Node, String> cacheKeys) {
        Map<String, JobDefinition> outjds = new LinkedHashMap<String, JobDefinition>();
        String id = jobdef.getId();

        for (Map.Entry<String, JobDefinition> entry : jdlist.entrySet()) {
            JobDefinition jd = entry.getValue();
            String inId = jd.getInId();
            
            if (id.equals(inId)) {  // this jd is an inId
                id = jd.getId();
                
                String key = configKeys.get(id);
                if (cacheKeys.containsValue(key)) {
                    
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Already cache for id: " + id);
                    }
                } else {
                    
                    outjds.put(id, jd);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Added id: " + id);
                    }
                }
            }
        }
        return outjds;
    }
    
    
}
