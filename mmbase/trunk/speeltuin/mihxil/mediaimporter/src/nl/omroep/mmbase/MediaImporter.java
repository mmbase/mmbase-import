package nl.omroep.mmbase;

import org.mmbase.applications.crontab.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.*;

import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.*;
import java.text.*;

/**
 * Bulk synchronization of a (gzipped) file with file-names with media-source objects (with 'url' field). This thing can
 * be scheduled using the crontab module.
 *
 * The read file should be generated with something like
 * <pre>
 find /e/streams -follow -type f -printf "%P\t%C@\t%s\n" | sort -t "`echo -e '\t'`" -k1,1df -k1,1r | gzip  > /tmp/test.gz
</pre>
 *
 * @author Michiel Meeuwissen
 */

public class MediaImporter extends BasicCronJob {

    private static final Logger log = Logging.getLoggerInstance(MediaImporter.class);


    /**
     * The file to be read.
     */
    private File file;

    /**
     * (Approximate) Batch size of database queries
     */
    private int  batchSize = 2000;

    /**
     * fifo size
     */
    private int  fifoSize = 1000;

    /**
     * Read configuration, from cronjob's configuration string.
     */
    protected void init() {
        try {
            String config = jCronEntry.getConfiguration();
            if (config == null) config = "";
            String[] configs = config.trim().split("\\s");
            String fileName;
            if(configs.length > 0) {
                fileName = configs[0].trim();
            } else {
                fileName = "/tmp/test.gz";
                log.info("No filename configured, taking " + fileName);
            }
            file = new File(fileName);
            if (! file.exists()) {
                log.warn("Configured file " + file + " does not exist (yet?)");
            }

            if (configs.length > 1) {
                batchSize = new Integer(configs[1].trim()).intValue();
                log.info("Set batch-size to " + batchSize);
            }
            if (configs.length > 2) {
                fifoSize = new Integer(configs[2].trim()).intValue();
                log.info("Set fifo-size to " + fifoSize);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
    }

    int fileLine = 0;
    int dbLine = 0;
    
    /**
     * Reads next line from the file-reader, and translates it into a 'StreamFile' object.
     *
     * @return null if end of stream.
     */
    protected StreamFile nextFileUrl(BufferedReader fileReader) throws IOException {
        String line = fileReader.readLine();
        if (line == null) return null;
        fileLine++;
        StringTokenizer st = new StringTokenizer(line, "\t");
        String url = st.nextToken();
        //if (url.endsWith("~"))     return nextFileUrl(); // ignore emacs-backup
        //if (url.indexOf("#") >= 0) return nextFileUrl(); // ignore a.o. emacs CVS files. Also # is sorted differntly by sort then by psql
        if (url.startsWith("./"))  url = url.substring(1);
        if (! url.startsWith("/")) url = "/" + url;
        long  lastModified = new Long(st.nextToken()).longValue();
        int byteSize = new Integer(st.nextToken()).intValue();
        return new StreamFile(url, lastModified, byteSize);
    }

    protected Node nextNode(NodeIterator nodeIterator) {
        Node node      = nodeIterator.hasNext() ? nodeIterator.nextNode() : null;
        if (node != null) dbLine++;
        return node;
    }


    // trying to compare like postgresql does.
    private static String IGNORED = "'-','/',' ','#','~', '!','^', '$', '%', '\\', '*', '(', ')', '_', '+', '=', '&', ':', ';', '<', '>', '.', '?', '{', '}', '@', ','";
    private static Collator collator;
    static {
        try {
            collator = new RuleBasedCollator("," + IGNORED + "<" +
                                             " 0 < 1 < 2 < 3 < 4 < 5 < 6 < 7 < 8 < 9 < a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J < k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T < u,U< v,V< w,W< x,X< y,Y< z,Z"
            // ((RuleBasedCollator) Collator.getInstance(Locale.US)).getRules()
                                             );
        } catch (Exception e) {
            log.error(e.toString());
        }
        collator.setStrength(Collator.IDENTICAL);
        collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    }


    protected static int getComp(String url1, String url2) {
        return collator.compare(url1, url2);
    }
    /**
     * Compares a StreamFile object with a Node object.
     */
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
            return getComp(node.getStringValue("url"), streamFile.url);
        }
    }

    protected String here() {
        return "" + fileLine + "/" + dbLine + ": ";
    }
    
    /**
     * {@inheritDoc}
     */
    public void run() {
        fileLine = 0;
        dbLine = 0;
        int created = 0;
        int removed = 0;
        int nofile = 0;
        int modified = 0;
        int total = 0;
        int errors = 0;
        try {
            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);            
            log.service("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank()); 

            NodeManager mediaSources = cloud.getNodeManager("mediasources");

            NodeQuery query = mediaSources.createQuery();
            query.addSortOrder(query.getStepField(mediaSources.getField("url")),  
                               SortOrder.ORDER_ASCENDING);
            Queries.addConstraint(query, query.createConstraint(query.getStepField(mediaSources.getField("filelastmodified")),  
                                                                FieldCompareConstraint.GREATER,
                                                                new Integer(0)));
            BufferedReader fileReader;

            
            if (file.getName().endsWith(".gz")) { // supporting that too.
                fileReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));                
            } else {
                fileReader = new BufferedReader(new FileReader(file));
            }
            
            NodeIterator nodeIterator = new HugeNodeListIterator(query, batchSize);
                       
            int i = 0;
            Transaction trans = cloud.createTransaction();

            NodeDeleterFifo deleter = new NodeDeleterFifo(fifoSize);
            NodeInserterFifo inserter = new NodeInserterFifo(mediaSources, fifoSize);

            while (true) {
                StreamFile streamFile = nextFileUrl(fileReader);
                Node node             = nextNode(nodeIterator);

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
                            if (inserter.check(node.getStringValue("url"))) {
                                log.debug(here() + "Detected discrepancy in ordering of " + node.getStringValue("url"));
                            } else {                                
                                removed += deleter.add(node);
                            }
                        }                  
                        node    = nextNode(nodeIterator);
                    } else { // ah, a new Node was found.
                        if (deleter.check(streamFile.url)) {
                            log.debug(here() + "Detected discrepancy in ordering of " + streamFile.url);
                        } else {
                            created += inserter.add(streamFile);
                        }

                        streamFile = nextFileUrl(fileReader);
                        if (streamFile != null) fileLine++;
                    }
                    total++;
                    comp = getComp(streamFile, node);
                }
                if (node != null && streamFile != null) {
                    if (node.getStringValue("url").equals(streamFile.url)) {
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
                }
                if (node != null) {
                    if (deleter.remove(node)) {
                        log.debug(here() + "Detected discrepancy in ordering of " + node.getStringValue("url"));
                    }
                }
                if (streamFile != null) {
                    if (inserter.remove(streamFile)) {
                        log.debug(here() + "Detected discrepancy in ordering of " + streamFile.url);
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


    
    /**
     * Structure describe one line of the file which jobs reads.
     */
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
        List list = new LinkedList();
        int size ;
        NodeDeleterFifo(int s) {
            size = s;
        }
        int add(Node node) {
            list.add(node);
            if (list.size() > size) {
                Node removedNode = (Node) list.remove(0);
                log.debug("Deleting because FIFO full");
                removedNode.delete();
                log.service(here() + "Deleted " + node.getStringValue("url"));
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
                log.service(here() + "Deleted " + removedNode.getStringValue("url"));
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

        int size;
        NodeInserterFifo(NodeManager nm, int s) {
            mediaSources = nm;
            size = s;
        }
        List list = new LinkedList();

        private int insert(StreamFile streamFile) {
            try {
                Node newNode = mediaSources.createNode();
                newNode.setStringValue("url", streamFile.url);
                newNode.setLongValue("filelastmodified", streamFile.lastModified);
                newNode.setIntValue("filesize", streamFile.byteSize);
                newNode.commit();
                log.service(here() + "Inserted " + streamFile.url); 
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
                log.debug("Inserting because FIFO full");
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

    public static void main(String[] args) {
        System.out.println(getComp("a/aa", "AA.A"));
    }

}
