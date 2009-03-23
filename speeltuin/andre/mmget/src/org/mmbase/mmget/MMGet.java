package org.mmbase.mmget;

import java.util.*;
import java.util.regex.*;
import java.util.concurrent.*;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import java.io.*;
import java.net.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;

import org.mmbase.util.ThreadPools;
import org.mmbase.util.UriParser;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.xml.UtilReader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * An MMBase application that excepts and url to export all files 'below' that url.
 * TODO: init rootURL early on, and check all urls against it (so we don't travel up the rootURL)
 *
 * @author Andr&eacute; van Toly
 * @version $Id: MMGet.java,v 1.10 2009-03-23 22:30:22 andre Exp $
 */
public final class MMGet {
    
    private static final Logger log = Logging.getLoggerInstance(MMGet.class);
        
    public static final String CONFIG_FILE = "mmget.xml";
    private static final UtilReader utilreader = new UtilReader(CONFIG_FILE, new Runnable() {
                                                     public void run() {
                                                         configure(utilreader.getProperties());
                                                     }
                                                 });
    /* link to start exporting from and directory of the start url  */
    public static String url;
    public static String serverpart;
    protected static URL startURL;
    protected static URL startdirURL;
    
    /* location the files should be saved to, directory to save files should be in the webroot (for now) */
    public static String directory;
    protected static File savedir;

    /* not wanted: offsite, already tried but 404 etc. */
    protected static Set<URL> ignoredURLs = new HashSet<URL>();
    /* urls to read (html, css) */
    protected static List<URL> readURLs = Collections.synchronizedList(new ArrayList<URL>());
    /* saved: url -> filename */
    protected static Map<URL,String> savedURLs = Collections.synchronizedMap(new HashMap<URL,String>());
    /* rewrite these: url -> link in page / new link in rewritten page */
    protected static Map<URL,Map<String,String>> url2links = Collections.synchronizedMap(new HashMap<URL,Map<String,String>>());
    
    /* future status */
    public Future<String> fstatus;
    
    /* homepage to use when saving a file with no extension (thus presuming directory) */
    protected static String homepage = "index.html";
    protected static List<String> contentheadersHTML = Arrays.asList(
        "text/html",
        "application/xhtml+xml",
        "application/xml",
        "text/xml"
    );
    protected static List<String> contentheadersCSS = Arrays.asList(
        "text/css"
    );
    
    /* content-types */
    protected static final int CONTENTTYPE_OTHER = 0;
    protected static final int CONTENTTYPE_HTML  = 1;
    protected static final int CONTENTTYPE_CSS   = 2;

    /**
     * Checks and sets links and export directory. 
     * Checks if the export directory exists, if not will try to create one in the MMBase data directory. 
     */
    public static void init() throws IOException, URISyntaxException, MalformedURLException {
        configure(utilreader.getProperties());
        File datadir = MMBase.getMMBase().getDataDir();
        ResourceLoader webroot = ResourceLoader.getWebRoot();
        
        startURL = new URL(url);
        serverpart = url;
        if (url.lastIndexOf("/") > 7) {
            serverpart = url.substring(0, url.indexOf("/", 7));
        }

        // savedir
        if (directory == null || "".equals(directory) || !webroot.getResource(directory).openConnection().getDoInput()) {
            log.warn("Exportdir '" + directory + "' does not exist! Will try to save to MMBase datadir.");
            log.debug("Datadir is: " + datadir.toString());

            savedir = new File(datadir, "mmget");
            if (! savedir.exists()) {
                if (savedir.mkdirs()) {
                    log.info("Directory " + savedir + " was created");
                } else {
                    log.warn("Directory " + savedir + " could not be created");
                }
            }
        } else {
            URL savedirURL = webroot.getResource(directory);
            savedir = new File(savedirURL.toURI());
        }

    }

    /**
     * Reads configuration
     * @param configuration config properties 
     */
    synchronized static void configure(Map<String, String> config) {
        //if (log.isDebugEnabled()) log.debug("Reading configuration..");
        String tmp = config.get("directory");
        if (tmp != null && !tmp.equals("") && directory != null && directory.equals("")) {
            directory = tmp;
            log.info("Default directory to save files: " + directory);
        }        
        tmp = config.get("homepage");
        if (tmp != null && !tmp.equals("")) {
            homepage = tmp;
            log.info("Default homepage: " + homepage);
        }
        tmp = config.get("htmlheaders");
        if (tmp != null && !tmp.equals("")) {
            contentheadersHTML = Arrays.asList(tmp.split(","));
            log.info("Headers for html to check: " + tmp);
        }
        tmp = config.get("cssheaders");
        if (tmp != null && !tmp.equals("")) {
            contentheadersCSS = Arrays.asList(tmp.split(","));
            log.info("Headers for css to check: " + tmp);
        }

    }

    /**
     * Starts the job saving the site and inits export directory
     *
     * @param   url The link to start from, normally the homepage
     * @param   dir The directory to save the files to
     * @return  Message with the results
     */
    public String downloadSite(String url, String dir) {
        this.directory = dir;
        this.url = url;
        
        String status = "";
        try {
            init();
        } catch (MalformedURLException e) {
            status = "Can't parse: " + url + ", " + e;
            log.error(status);
            return "Error: " + status;
        } catch (IOException e) {
            status = "Could not find (or create) export directory '" + dir + "': " + e;
            log.error(status);
            return "Error: " + status;
        } catch (URISyntaxException e) {
            status = "Could not make a correct link to directory '" + dir + "': " + e;
            log.error(status);
            return "Error: " + status;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("\n***    url: ").append(startURL.toString());
        //info.append("\n**    dir.: ").append(startdirURL.toString());
        info.append("\n* saved in: ").append(savedir.toString());
        log.info(info.toString());
        
        ThreadPools.jobsExecutor.submit(new Callable() {
                 public String call() {
                      return start();
                 }
            });
        // this Future stuff prevents errors to surface
        /*
        Future<String> fthread = ThreadPools.jobsExecutor.submit(new Callable() {
                 public String call() {
                      return start();
                 }
            });
        
        try {
            status = fthread.get(60, TimeUnit.SECONDS);
        } catch(TimeoutException e) {
            log.error(e);
        } catch(ExecutionException e) {
            log.error(e);
        } catch(InterruptedException e) {
            log.error(e);
        }
        
        info.append(status);
        log.info(status);
        */
        return info.toString();
    }

    /**
     * Kick method that starts export from the initial link
     * @param  link 
     */
    private String start() {
        readURLs.clear();
        ignoredURLs.clear();
        savedURLs.clear();
        url2links.clear();
        startdirURL = null;
        
        readUrl(startURL);
        return "Job finished?!";
    }

    /**
     * Parses urls it recieves.
     * @param url   link to html page or css
     */
    private void readUrl(URL url) {
        if (url == null) return;
        log.debug("---------------------------------------------------------------------");
        log.debug("reading:   " + url.toString());
        
        URL dirURL;
        UrlReader reader = null;
        try {
            reader = UrlReaders.getUrlReader(url);
            url = reader.getUrl();
            dirURL = getDirectoryURL(url);
            if (startdirURL == null) startdirURL = dirURL;
            
        } catch (MalformedURLException e) {
            log.error("Can't parse '" + url + "' - " + e);
            return;
        } catch (IOException e) {
            log.error("Can't find '" + url + "' - " + e);
            return;
        }
        //if (reader == null) return;
        
        try {
            ArrayList<String> links = reader.getLinks();
            Map<String,String> links2files = new HashMap<String,String>();      /* maps a harvested link to the resulting saved file if different */
            
            //if (startdirURL == null) startdirURL = dirURL;
            log.debug("@@ dirURL: " + dirURL.toString());
            log.debug("@@ startdirURL: " + startdirURL.toString());
            
            Iterator<String> it = links.iterator();
            while (it.hasNext()) {
                String link = it.next();
                link = removeSessionid(link);   // remove sessionid crud etc. (changes over time)
                URL linkURL;
                if (link.indexOf("://") < 0) {
                    try {
                        linkURL = new URL(url, link);
                    } catch (MalformedURLException e) {
                        log.warn("Can't parse '" + link + "' - " + e);
                        continue;
                    }
                } else {
                    try {
                        linkURL = new URL(link);
                    } catch (MalformedURLException e) {
                        log.warn("Can't parse '" + link + "' - " + e);
                        continue;
                    }
                }
                //log.debug("link: " + linkURL.toString());
                
                if (ignoredURLs.contains(linkURL)) continue;
                if (!linkURL.getHost().equals(url.getHost())) {
                    //log.info(linkURL.toString() + " -- OFFSITE, not following");
                    ignoredURLs.add(linkURL);
                    continue;
                }
                /*
                if (!linkURL.toString().startsWith(startdirURL.toString())) {
                    log.info(linkURL.toString() + " -- UP TREE, not following");
                    ignoredURLs.add(linkURL);
                    continue;
                }
                */
                    
                String filename = getSavedFilename(linkURL);    // already saved?
                if (filename == null) {
                    ResourceWriter rw = null;
                    try {
                        rw = new ResourceWriter(linkURL);
                        filename = rw.getFilename();
                        
                        if (rw.getContentType() < 1) {
                            rw.write();
                            rw.disconnect();
                            
                        } else {
                            if (rw.getContentType() == CONTENTTYPE_HTML
                                && !linkURL.toString().startsWith(startdirURL.toString())) {
                                    log.info(linkURL.toString() + " -- UP TREE, not following");
                                    
                                    if (!link.equals(linkURL.toString()) && !links2files.containsKey(link)) {
                                        links2files.put(link, linkURL.toString());  // replace with full url
                                    }
                                    rw.disconnect();
                                    continue;
                            }
                            rw.disconnect();
                            // save for later
                            synchronized(readURLs) {
                                if (!readURLs.contains(linkURL)) readURLs.add(linkURL);
                            }
                            
                        }
                    } catch(IOException e) {
                        log.error(e);
                        ignoredURLs.add(linkURL);
                        continue;
                    }
                }
                
                StringBuilder calclink = new StringBuilder(serverpart);
                calclink.append("/").append(filename);    // 'calculated' link
                String calcdir  = dirURL.toString();
                if (calcdir.endsWith("/")) calcdir = calcdir.substring(0, calcdir.lastIndexOf("/"));
                
                String relative = UriParser.makeRelative(calcdir, calclink.toString());
                if (!"".equals(link) && !links2files.containsKey(link) && !link.equals(relative)) { // only when different
                    log.debug("link2files: " + link + " -> " + relative);
                    links2files.put(link, relative);    /* /dir/css/bla.css + ../css/bla.css */
                }
                
            } // while ends
            
            reader.close();
            synchronized(url2links) {
                if (!url2links.containsKey(url)) url2links.put(url, links2files);
            }
            
            ResourceReWriter rrw = null;
            try {
                rrw = new ResourceReWriter(url);
                rrw.write();
            } catch (IOException e) {
                log.error(e);
                ignoredURLs.add(url);
            }
            
            readUrl(getReadURL());  // recurse!
            
        } catch (IOException e) {
            log.error("IOException: " + e);
        }
        
    }
    
    protected final static int contentType(URLConnection uc) {
        String contentheader = uc.getHeaderField("content-type");
        //log.debug("header: " + contentheader);
        int pk = contentheader.indexOf(";");
        if (pk > -1) contentheader = contentheader.substring(0, pk);
        
        int type;
        if (contentheadersHTML.contains(contentheader)) {
            type = 1;
        } else if (contentheadersCSS.contains(contentheader)) {
            type = 2;
        } else {
            type = 0;
        }
        return type;
    }

    /**
     * Gets a files directory. This normally ends with a '/' !
     * It returns http://www.toly.net/ for html-pages like http://www.toly.net/contact
     * presuming (for convenience) http://www.toly.net/contact/index.html
     *
     * @param  url page or other resource
     * @return the directory it is in
     */
    private static URL getDirectoryURL(URL url) {
        String dir = url.toString();
        
        if (dir.lastIndexOf("/") > 7 && hasExtension(url.getFile())) {
            dir = dir.substring(0, dir.lastIndexOf("/") + 1);
        }

        log.debug("url: " + url + ", returning: " + dir);
        
        try {
            return new URL(dir);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    
    /**
     * remove ;jsessionid=a69bd9e162de1cfa3ea57ef6f3cf03af
     */
    public final static String removeSessionid(String str) {
        int pk = str.indexOf(";");
        if (pk > -1) {
            int q = str.indexOf("?");
            if (q > -1) {
                str = str.substring(0, pk) + str.substring(q, str.length());
            } else {
                str = str.substring(0, pk);
            }
        }
        return str;   
    }
    
    
    /**
     * Creates an empty file in the save directory, checks if its directories exist (but not itself).
     *
     * @param  path the exact path from the startposition of the export (that's seen as 'root')
     * @return file
     */
    public final File getFile(String path) {
        File f;
        String resource;
        
        if (path.lastIndexOf("/") > 0) {
            String dir = ResourceLoader.getDirectory(path);
            //log.debug("dir: " + dir);
            f = new File(savedir, dir);
            if (!f.exists()) {
                if (f.mkdirs()) {
                    //log.debug("Directory created: " + savedir);
                } else {
                    log.warn("Directory '" + f + "' could not be created");
                }
            }
            resource = path.substring(path.lastIndexOf("/"), path.length());
        } else {
            f = savedir;
            resource = path;
        }
        return new File(f, resource);
    }

    /** 
     * Checks if a filename ends with an extension.
     *
     * @param   file    path or filename to check (not an URL!)
     * @return  true if it contains an extension like .html f.e.
     */
     public static final boolean hasExtension(String file) {
        int i = file.lastIndexOf(".");
        return (i != -1 && i != file.length() - 1);
    }
    
/*
    public List splitPath(String path) {
        List<String> pathList = new ArrayList<String>();
        for (String p: path.split("/")) {
            if (!p.equals("")) pathList.add(p);
        }
        return pathList;
    }
*/
    protected String getSavedFilename(URL url) {
        synchronized(savedURLs) {
            return savedURLs.get(url);
        }
    }

    protected static void addSavedURL(URL url, String filename) {
        synchronized(savedURLs) {
            if (!savedURLs.containsKey(url)) savedURLs.put(url, filename);
        }
    }


    private URL getReadURL() {
        URL url = null;
        synchronized(readURLs) {
            if (!readURLs.isEmpty()) url = readURLs.remove(0);
        }
        return url;
    }

}
