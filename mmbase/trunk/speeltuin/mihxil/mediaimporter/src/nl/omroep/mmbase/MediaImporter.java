package nl.omroep.mmbase;

import org.mmbase.applications.crontab.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;

/**
 * 
 * @author Michiel Meeuwissen
 */

public class MediaImporter extends BasicCronJob {

    private static final Logger log = Logging.getLoggerInstance(MediaImporter.class);


    private File file;
    private int  batchSize = 300;
    protected void init() {
        try {

            String fileName = jCronEntry.getConfiguration();
            if(fileName == null) {
                fileName = "/tmp/test";
                log.info("No filename configured, taking " + fileName);
            }
            file = new File(fileName);
            if (! file.exists()) {
                log.warn("Configured file " + file + " does not exist (yet?)");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
    }

    private BufferedReader fileReader;


    private StreamFile nextFileUrl() throws IOException {
        String fileLine = fileReader.readLine();
        if (fileLine == null) return null;
        StringTokenizer st = new StringTokenizer(fileLine, "\t");
        String url = st.nextToken();
        if (url.endsWith("~"))       return nextFileUrl(); // ignore emacs-backup
        if (url.indexOf("#") >= 0) return nextFileUrl(); // ignore a.o. emacs CVS files. Also # is sorted differntly by sort then by psql
        if (url.startsWith("./")) url = url.substring(1);
        if (! url.startsWith("/")) url = "/" + url;
        long  lastModified = new Long(st.nextToken()).longValue();
        int byteSize = new Integer(st.nextToken()).intValue();
        return new StreamFile(url, lastModified, byteSize);
    }


    protected int getComp(StreamFile streamFile, Node node) {
        if (log.isDebugEnabled()) {
            log.debug("comparing " + (streamFile == null ? "NULL" : streamFile.url) + " to " + (node == null ? "NULL" : node.getStringValue("url")));
        }
        if (streamFile == null) {
            if (node == null) return 0;
            return -1;
        } else if (node == null) {
            return 1;
        } else {
            return node.getStringValue("url").compareTo(streamFile.url);
        }
    }

    


    public void run() {
        int created = 0;
        int removed = 0;
        int nofile = 0;
        int modified = 0;
        int total = 0;
        int errors = 0;
        try {
            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);            
            log.info("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank()); 

            NodeManager mediaSources = cloud.getNodeManager("mediasources");

            NodeQuery query = mediaSources.createQuery();
            query.addSortOrder(query.getStepField(mediaSources.getField("url")),  
                               SortOrder.ORDER_ASCENDING);
            query.setMaxNumber(batchSize);
        
            fileReader = new BufferedReader(new FileReader(file));
            
            NodeIterator nodeIterator = new HugeNodeListIterator(query, batchSize);
                       
            int i = 0;
            Transaction trans = cloud.createTransaction();

            NodeDeleterFifo deleter = new NodeDeleterFifo();
            NodeInserterFifo inserter = new NodeInserterFifo(mediaSources);

            while (true) {
                StreamFile streamFile = nextFileUrl();
                Node node      = nodeIterator.hasNext() ? nodeIterator.nextNode() : null;

                if (streamFile == null && node == null) {  // end of both
                    break;
                }

                int comp = getComp(streamFile, node);
     
                while (comp != 0) {
                    if (comp < 0) { // node's url is smaller, so node should be removed.
                        log.debug("Node " + node.getStringValue("url") + " not found " + node.getLongValue("filelastmodified"));
                        if (node.hasRelations() || (! node.mayDelete())) {
                            node.setLongValue("filelastmodified", -1);
                            node.commit();
                            nofile++;
                        } else {
                            removed += deleter.add(node);
                        }                  
                        node    = nodeIterator.hasNext() ? nodeIterator.nextNode() : null;
                    } else { // ah, a new Node was found.
                        if (deleter.check(streamFile.url)) {
                            log.service("Detected discrepancy in ordering of " + streamFile.url);
                        } else {
                            log.service("Should insert " + streamFile.url);
                       
                            created += inserter.add(streamFile);
                        }

                        streamFile = nextFileUrl();
                    }
                    total++;
                    comp = getComp(streamFile, node);
                }
                if (node != null && streamFile != null) {
                    // check.
                    if (node.getLongValue("filelastmodified") != streamFile.lastModified ||
                        node.getIntValue("filesize") != streamFile.byteSize) {
                        
                        log.info("Updating node " + node.getStringValue("url") + " file was modified on: " + new Date(streamFile.lastModified * 1000));
                        node.setLongValue("filelastmodified", streamFile.lastModified);
                        node.setIntValue("filesize", streamFile.byteSize);
                        node.commit();
                        modified++;
                    }
                }
                if (node != null) {
                    if (deleter.remove(node)) {
                        log.service("Detected discrepancy in ordering of " + node.getStringValue("url"));
                    }
                }
                if (streamFile != null) {
                    if (inserter.remove(streamFile)) {
                        log.service("Detected discrepancy in ordering of " + streamFile.url);
                    }
                }
                total++;

            }
            removed += deleter.empty();
            created += inserter.empty();
                
            
        } catch (Exception e) {
            errors++;
            log.error(e.getClass().getName() + ":" + e.getMessage() + Logging.stackTrace(e));
        }

        

        log.info("Ready importing media. Total: " + total + " Modified: "  + modified + " Created: " + created + " Removed: " + removed + " No-file: " + nofile + " Errors: " + errors);
    }


    

    class StreamFile {
        String url;
        long   lastModified;
        int    byteSize;

        StreamFile(String u, long l, int b) {
            url = u;
            lastModified = l;
            byteSize = b;
        }
    }

    /**
     * These FIFO's make it insensible to minor variations in sort-order
     */

    class NodeDeleterFifo {
        int size = 10;
        List list = new LinkedList();
        int add(Node node) {
            list.add(node);
            if (list.size() > size) {
                Node removedNode = (Node) list.remove(0);
                removedNode.delete();
                return 1;
            } else {
                return 0;
            }
        }

        boolean remove(Node node) {
            return list.remove(node);
        }

        int empty() {
            int result = 0;
            while (list.size() > 0) {
                Node removedNode = (Node) list.remove(0);
                removedNode.delete();
                result++;
            }
            return result;
        }
        boolean check(String url) {
            ListIterator i = list.listIterator();
            while (i.hasNext()) {
                Node node = (Node) i.next();
                if (node.getStringValue("url").equals(url)) {
                    i.remove();
                    return true;
                }
                
            }
            return false;
            
        }
    }
    class NodeInserterFifo {
        NodeManager mediaSources;

        NodeInserterFifo(NodeManager nm) {
            mediaSources = nm;
        }
        int size = 10;
        List list = new LinkedList();

        private int insert(StreamFile streamFile) {
            try {
                Node newNode = mediaSources.createNode();
                newNode.setStringValue("url", streamFile.url);
                newNode.setLongValue("filelastmodified", streamFile.lastModified);
                newNode.setIntValue("filesize", streamFile.byteSize);
                newNode.commit();
            } catch (Throwable t) {
                log.error(t.getMessage());
                return 0;
            }
            return 1;
        }
        int add(StreamFile sf) {
            list.add(sf);
            if (list.size() > size) {
                StreamFile streamFile = (StreamFile) list.remove(0);
                return insert(streamFile);
            } else {
                return 0;
            }
        }

        boolean remove(StreamFile sf) {
            return list.remove(sf);
        }

        int empty() {
            int result = 0;
            while (list.size() > 0) {
                StreamFile streamFile = (StreamFile) list.remove(0);
                result += insert(streamFile);
            }
            return result;
        }
        boolean check(String url) {
            ListIterator i = list.listIterator();
            while (i.hasNext()) {
                StreamFile streamFile = (StreamFile) i.next();
                if (streamFile.url.equals(url)) {
                    i.remove();
                    return true;
                }
                
            }
            return false;
            
        }
    }

}
