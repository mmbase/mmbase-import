package nl.omroep.mmbase;

import org.mmbase.applications.crontab.*;
import org.mmbase.applications.media.builders.MediaSources;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.cache.*;

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
 * 
<pre>
  find /e/streams -follow -type f -printf "%P\t%T@\t%s\n" | sort -t "`echo -e '\t'`" -k1,1 | gzip  > /tmp/test.gz
</pre>
 *
 * @author Michiel Meeuwissen
 */

public class MediaImporter extends AbstractCronJob {

    private static final Logger log = Logging.getLoggerInstance(MediaImporter.class);
    protected static NodeCache nodeCache  = NodeCache.getCache(); 


    private static final Comparator NODE_URL_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Node node = (Node) o1; String url = (String) o2;
                return node.getStringValue("url").compareTo(url);
            }
        };

    private static final Comparator NODE_DIR_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                Node node = (Node) o1; String dir = (String) o2;
                return node.getStringValue("name").compareTo(dir);
            }
        };
    
    private static final Comparator STREAMFILE_URL_COMPARATOR = new Comparator() {
            public int compare(Object o1, Object o2) {
                StreamFile streamFile = (StreamFile) o1; String url = (String) o2;
                return streamFile.url.compareTo(url);
            }
        };

    private static final Comparator CASE_SENSITIVE_ORDER = new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String) o1; String s2 = (String) o2;
                return s1.compareTo(s2);
            }
            public String toString() { return "DEFAULT_STRING"; }
        };

    private static Collator PSQL_COMPARATOR;

    // trying to compare like postgresql does.

    static {
        try {

            final String IGNORED = "'-','/',' ','#','~', '!','^', '$', '%', '\\', '*', '(', ')', '_', '+', '=', '&', ':', ';', '<', '>', '.', '?', '{', '}', '@', ',','|'";
            PSQL_COMPARATOR = new RuleBasedCollator("," + IGNORED + "<" +
                                           " 0 < 1 < 2 < 3 < 4 < 5 < 6 < 7 < 8 < 9 < a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I< j,J < k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R< s,S< t,T < u,U< v,V< w,W< x,X< y,Y< z,Z" +
             ((RuleBasedCollator) Collator.getInstance(Locale.US)).getRules()
            );
            PSQL_COMPARATOR.setStrength(Collator.IDENTICAL);
            PSQL_COMPARATOR.setDecomposition(Collator.FULL_DECOMPOSITION);
        } catch (Exception e) {
            log.error(e.toString());
        }



    }



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

    
    private Comparator comparator = null;

    /**
     * Read configuration, from cronjob's configuration string.
     */
    protected void init() {
        try {
            String config = cronEntry.getConfiguration();
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

            if (configs.length > 3) {
                String caseSens = configs[3].trim().toLowerCase();
                if (caseSens.equals("caseinsensitive")) {
                    comparator = String.CASE_INSENSITIVE_ORDER;
                } else if (caseSens.equals("casesensitive")) {
                    comparator = CASE_SENSITIVE_ORDER;
                } else if (caseSens.equals("psqlcaseinsensitive")) {
                    comparator = PSQL_COMPARATOR;
                } else {
                    log.warn("Unrecognised comparator " + caseSens);
                }
            } 
            if (comparator == null) { 
                comparator = CASE_SENSITIVE_ORDER;
            }
            log.info("Using comparator: " + comparator);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        
    }

    
    protected BufferedReader getFileReader() throws IOException, FileNotFoundException {
        // build file reader
        if (file.getName().endsWith(".gz")) { // supporting that too.
            return  new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));                
        } else {
            return new BufferedReader(new FileReader(file));
        }
    }



    private int fileLine = 0;
    private int dbLine = 0;
    
    /**
     * Reads next line from the file-reader, and translates it into a 'StreamFile' object.
     *
     * @return null if end of stream.
     */
    protected StreamFile nextFileUrl(BufferedReader fileReader) throws IOException {
        String line = fileReader.readLine();
        if (line == null) return null;
        fileLine++;
        // default in case of error
        String url = "ERROR";
        long   lastModified = -1;
        int    byteSize = 0;
        try {
            StringTokenizer st = new StringTokenizer(line, "\t");
            url = st.nextToken();
            if (url.startsWith("./"))  url = url.substring(1);
            if (! url.startsWith("/")) url = "/" + url;
            lastModified = new Long(st.nextToken()).longValue();
            byteSize = new Integer(st.nextToken()).intValue();
        } catch (Throwable t) {
            log.error(t.getMessage());
        }
        return new StreamFile(url, lastModified, byteSize);
    }


    protected Node nextNode(NodeIterator nodeIterator) {
        Node node      = nodeIterator.hasNext() ? nodeIterator.nextNode() : null;
        if (node != null) dbLine++;
        return node;
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
            return comparator.compare(node.getStringValue("url"), streamFile.url);
        }
    }

    protected int getComp(String dir, Node node) {

        if (dir == null) {
            if (node == null) return 0;
            return -1;
        } else if (node == null) {
            return 1;
        } else {
            return comparator.compare(node.getStringValue("name"), dir);
        }
    }


    protected String getDir(StreamFile string) {
        if (string == null) return null;
        File f = new File(string.url).getParentFile();;
        while (f.getName().matches("\\d+")) { // remove trailing dir names only existing from digits (vpro object numbers)
            f = f.getParentFile();
        }
        // now, apply 'maxdepth'

        int depth = new StringTokenizer(f.toString(), "/").countTokens();
        while (depth > 4) {
            depth --;
            f = f.getParentFile();
        }
        return f.toString() + "/";

    }

    protected void addDir(StreamFile sf, Set set) {
        String s = getDir(sf);
        if (s != null && ! s.equals("//")) {
            set.add(s);
        }
        
    }
    /**
     * Compares a StreamFile object with a Node object.
     */
    protected int getDirComp(StreamFile streamFile, Node node) {
        if (log.isDebugEnabled()) {
            log.debug("comparing " + (streamFile == null ? "NULL" : streamFile.url) + " to " + (node == null ? "NULL" : node.getStringValue("url")));
        }
        if (streamFile == null) {
            if (node == null) return 0;
            return -1;
        } else if (node == null) {
            return 1;
        } else {
            int pos = streamFile.url.lastIndexOf('/');
            String dir = streamFile.url.substring(0, pos);
            return node.getStringValue("name").compareTo(dir);
            //return getComp(node.getStringValue("url"), streamFile.url);
        }
    }

    protected String here() {
        return "" + fileLine + "/" + dbLine + ": ";
    }
    
    /**
     * {@inheritDoc}
     * Just catched the exception of #importMedia
     */
    public void run() {
        try {
            log.info("Starting media-import: files");
            importMedia();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

   

    
    protected void importMedia() throws Exception {

        long lastTimeCheck = System.currentTimeMillis();
        long startTime     = lastTimeCheck;

        // build query node iterator
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);            
        log.service("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank()); 
        
        final NodeManager mediaSources = cloud.getNodeManager("mediasources");
        final NodeManager undefSources = cloud.getNodeManager("undefsources");
        
        NodeQuery query = mediaSources.createQuery();
        query.addSortOrder(query.getStepField(mediaSources.getField("url")), SortOrder.ORDER_ASCENDING);

        Queries.addConstraint(query, query.createConstraint(query.getStepField(mediaSources.getField("filelastmodified")),  
                                                            FieldCompareConstraint.GREATER,
                                                            new Integer(0)));

        NodeIterator nodeIterator = new HugeNodeListIterator(query, batchSize);


        BufferedReader fileReader  = getFileReader();
            

        // fifo's are needed to handle minor discrepancies in sorting order (caused by non alfanumeric chars)
        NodeDeleterFifo  deleter  = new NodeDeleterFifo(fifoSize);
        NodeInserterFifo inserter = new NodeInserterFifo(fifoSize, new NodeInserterFifo.Inserter() {
                public int insert(Object o) {
                    StreamFile streamFile = (StreamFile) o;
                    try {
                        Node newNode = undefSources.createNode();
                        newNode.setStringValue("url", streamFile.url);
                        newNode.setLongValue("filelastmodified", streamFile.lastModified);
                        newNode.setIntValue("filesize", streamFile.byteSize);
                        newNode.setIntValue("state", MediaSources.STATE_SOURCE);
                        newNode.commit();
                        nodeCache.remove(new Integer(newNode.getNumber()));
                        return 1;
                    } catch (Throwable t) {
                        log.error(t.getMessage());
                        return 0;
                    }
                }
                        
            });

        SortedSet dirs = new TreeSet();

        fileLine = 0;
        dbLine = 0;

        int created = 0;
        int removed = 0;
        int nofile = 0;
        int modified = 0;
        int total = 0;
        int errors = 0;

        // this loop iterates through all nodes and list of files simultaneously
        while (true) {
            StreamFile streamFile = nextFileUrl(fileReader);
            Node       node       = nextNode(nodeIterator);
            addDir(streamFile, dirs);
     
            int comp;
            do {
                comp = getComp(streamFile, node);
                total++;
                if (total % 1000 == 0) {
                    long time =  System.currentTimeMillis();
                    log.info("Current check-rate (on " + total + "): " + 1000000 / (time - lastTimeCheck) + " records / s. Current no of dirs: " + dirs.size());
                    lastTimeCheck = time;
                }

                if (comp < 0) { // node's url is smaller, so node should be removed.
                    log.debug("Node " + node.getStringValue("url") + " not found " + node.getLongValue("filelastmodified"));
                    if (node.hasRelations() || (! node.mayDelete())) {
                        node.setLongValue("filelastmodified", -1);
                        node.setIntValue("state", MediaSources.STATE_REMOVED);
                        node.commit();
                        nodeCache.remove(new Integer(node.getNumber()));
                        nofile++;
                    } else {
                        if (inserter.remove(node.getStringValue("url"), STREAMFILE_URL_COMPARATOR)) {
                            log.service(here() + "Detected discrepancy in ordering of " + node.getStringValue("url"));
                            created--; errors++;
                        } else if (deleter.add(node)) {
                            removed++;
                        }
                    }                  
                    node    = nextNode(nodeIterator);
                } else if (comp > 0) { // ah, a new Node was found.
                    if (deleter.remove(streamFile.url, NODE_URL_COMPARATOR)) {
                        log.service(here() + "Detected discrepancy in ordering of " + streamFile.url);
                        removed--; errors++;
                    } else if(inserter.add(streamFile)) {
                        created++; 
                    }

                    streamFile = nextFileUrl(fileReader);
                    addDir(streamFile, dirs);
                }
             
            } while (comp != 0);


            if (streamFile == null && node == null) {  // end of both, we are ready.
                break;
            }

                
            // node and streamFile in sync now, check if streamFile was updated.
            if (node.getStringValue("url").equals(streamFile.url)) {
                // check.
                if (node.getLongValue("filelastmodified") < streamFile.lastModified || 
                                                       // < and not != because some files appear with 2 last different last-modified dates (because the file-set is a join of more sets)
                    node.getIntValue("filesize") != streamFile.byteSize) {
                    if (log.isServiceEnabled()) {
                        log.service("Updating node " + node.getStringValue("url") + " file was modified on: " + new Date(streamFile.lastModified * 1000));
                    }
                    node.setLongValue("filelastmodified", streamFile.lastModified);
                    node.setIntValue("filesize", streamFile.byteSize);
                    node.commit();
                    modified++;
                }
            } 

            // remove from deleter and inserter, in case they accidently ended up there (discrepancy in sort order).
            log.debug("check deleter");
            if (deleter.remove(node)) {
                removed--; errors++;
                log.service(here() + "Detected discrepancy in ordering of " + node.getStringValue("url"));
            }
            log.debug("check inserter");
            if (inserter.remove(streamFile)) {
                created--; errors++;
                log.service(here() + "Detected discrepancy in ordering of " + streamFile.url);
            }


        }// end of loop.

        deleter.empty();
        inserter.empty();
                
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Ready importing media. Total: " + total + " Modified: "  + modified + 
                 " Created: " + created + " Removed: " + removed + 
                 " No-file: " + nofile + 
                 " Sort-order discrepancies: " + errors);
        log.info("Duration: " + (duration / 60000) + " minutes (" + (total * 1000 / duration) + " records /s)");

        
        log.info("Starting media-import: directory-structure");
        importDirs(dirs);

        
    }




    
    protected void importDirs(Set foundDirs) throws Exception {

        long lastTimeCheck = System.currentTimeMillis();
        long startTime     = lastTimeCheck;

        // build query node iterator
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);            
        log.service("found cloud " + cloud.getUser().getIdentifier() + "/" + cloud.getUser().getRank()); 
        
        final NodeManager dirs = cloud.getNodeManager("dirs");
        NodeQuery dirQuery = dirs.createQuery();
        dirQuery.addSortOrder(dirQuery.getStepField(dirs.getField("name")), SortOrder.ORDER_ASCENDING);

        NodeIterator dirsIterator = new HugeNodeListIterator(dirQuery, batchSize);
        // fifo's are needed to handle minor discrepancies in sorting order (caused by non alfanumeric chars)

        NodeDeleterFifo  deleter  = new NodeDeleterFifo(500);
        NodeInserterFifo inserter = new NodeInserterFifo(500, new NodeInserterFifo.Inserter() {
                public int insert(Object o) {
                    String dir = (String) o;
                    try {
                        Node newNode = dirs.createNode();
                        newNode.setStringValue("name", dir);
                        newNode.setStringValue("description", "Automaticly created by media-import");
                        newNode.commit();
                        nodeCache.remove(new Integer(newNode.getNumber()));
                        return 1;
                    } catch (Throwable t) {
                        log.error(t.getMessage());
                        return 0;
                    }
                        
                }
            });


        fileLine = 0;
        dbLine = 0;

        int created = 0;
        int removed = 0;
        int nofile = 0;
        int modified = 0;
        int total = 0;
        int errors = 0;

        Iterator foundDirsIterator = foundDirs.iterator();
       
        String lastDir = null;
        // this loop iterates through all nodes and list of files simultaneously
        while (true) {
            if (foundDirsIterator.hasNext()) {
                lastDir =  (String) foundDirsIterator.next();
            } else {
                lastDir = null;
            }
            Node       node       = nextNode(dirsIterator);

     
            int comp;
            do {
                comp = getComp(lastDir, node);
                total++;
                if (total % 1000 == 0) {
                    long time =  System.currentTimeMillis();
                    log.info("Current check-rate (on " + total + "): " + 1000000 / (time - lastTimeCheck) + " records / s");
                    lastTimeCheck = time;
                }

                if (comp < 0) { // node's url is smaller, so node should be removed.
                    if (node.hasRelations() || (! node.mayDelete())) {
                        node.setStringValue("description", "No files found in this dir");
                        node.commit();
                        nodeCache.remove(new Integer(node.getNumber()));
                        nofile++;
                    } else {
                        if (inserter.remove(node.getStringValue("name"), comparator)) {
                            log.service(here() + "Detected discrepancy in ordering of " + node.getStringValue("url"));
                            created--; errors++;
                        } else if (deleter.add(node)) {
                            removed++;
                        }
                    }                  
                    node    = nextNode(dirsIterator);
                } else if (comp > 0) { // ah, a new Node was found.
                    if (deleter.remove(lastDir, NODE_DIR_COMPARATOR)) {
                        log.service(here() + "Detected discrepancy in ordering of " + lastDir);
                        removed--; errors++;
                    } else if(inserter.add(lastDir)) {
                        created++; 
                    }

                    if (foundDirsIterator.hasNext()) {
                        lastDir =  (String) foundDirsIterator.next();
                    } else {
                        lastDir = null;
                    }
                }
             
            } while (comp != 0);


            if (lastDir == null && node == null) {  // end of both, we are ready.
                break;
            }

                
            // remove from deleter and inserter, in case they accidently ended up there (discrepancy in sort order).
            log.debug("check deleter");
            if (deleter.remove(node)) {
                removed--; errors++;
                log.service(here() + "Detected discrepancy in ordering of " + node.getStringValue("url"));
            }
            log.debug("check inserter");
            if (inserter.remove(lastDir)) {
                created--; errors++;
                log.service(here() + "Detected discrepancy in ordering of " + lastDir);
            }


        }// end of loop.

        deleter.empty();
        inserter.empty();
                
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Ready importing media-dirs. Total: " + total + " Modified: "  + modified + 
                 " Created: " + created + " Removed: " + removed + 
                 " No-file: " + nofile + 
                 " Sort-order discrepancies: " + errors);
        log.info("Duration: " + (duration / 60000) + " minutes (" + (total * 1000 / duration) + " records /s)");
    }




    
    /**
     * Structure describing one line of the file which the job reads.
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
     * for testing only
     */
    public static void main(String[] args) {
        //System.out.println(getCompn("a/aa", "AA.A"));
    }

}
