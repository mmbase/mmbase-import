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
 * @version $Id: MMGet.java,v 1.8 2009-03-12 10:30:38 andre Exp $
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
    /* urls to parse (html, css) */
    protected static List<URL> parseURLs = Collections.synchronizedList(new ArrayList<URL>());
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
        parseURLs.clear();
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
        if (reader == null) return;
        
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
                log.debug("link: " + linkURL.toString());
                
                if (ignoredURLs.contains(linkURL)) continue;
                if (!linkURL.getHost().equals(url.getHost())) {
                    //log.info(linkURL.toString() + " -- OFFSITE, not following");
                    ignoredURLs.add(linkURL);
                    continue;
                }
                if (!linkURL.toString().startsWith(startdirURL.toString())) {
                    // if (linkURL.toString().length() < startdirURL.toString().length()) {    // BUG: Klopt niet!
                    log.info(linkURL.toString() + " -- UP TREE, not following");
                    ignoredURLs.add(linkURL);
                    continue;
                }
                    
                String filename = null;
                if (savedURLs.containsKey(linkURL)) {
                    filename = savedURLs.get(linkURL);
                    log.debug("already saved");
                    
                } else {
                    ResourceWriter rw = null;
                    try {
                        rw = new ResourceWriter(linkURL);
                        filename = rw.getFilename();
                        
                        if (rw.getContentType() < 1) {
                            rw.write(); 
                        } else {
                            rw.disconnect();
                            addParseURL(linkURL);   // save for later
                            rw = null;
                        }
                    } catch(IOException e) {
                        log.error(e);
                        ignoredURLs.add(linkURL);
                    }
                    if (rw == null) continue;
                }
                
                String calclink = serverpart + "/" + filename;    // 'calculated' link
                String calcdir  = dirURL.toString();
                if (calcdir.endsWith("/")) calcdir = calcdir.substring(0, calcdir.lastIndexOf("/"));
                
                String relative = UriParser.makeRelative(calcdir, calclink);
                if (!"".equals(link) && !links2files.containsKey(link) && !link.equals(relative)) { // only when different
                    log.debug("link2files: " + link + " -> " + relative);
                    links2files.put(link, relative); /* /dir/css/bla.css + ../css/bla.css */
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
            
            URL nextURL = getParseURL();
            if (nextURL != null) readUrl(nextURL);  // recurse!
            
        } catch (IOException e) {
            log.error("IOException: " + e);
        }
        
    }
    
    protected static int contentType(URLConnection uc) {
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
        
        /*
        } else if (hasExtension(url.getFile())) {
            dir = link.substring(0, link.lastIndexOf("/") + 1);
            log.debug("2: dir " + dir);
        } else {
            startdirURL = new URL(strUrl + "/");    // only for html !
            log.debug("3: startdir " + startdirURL.toString());
        }
        */
        /*
        String path = url.getPath();
        
        String server = link;
        if (link.lastIndexOf("/") > 7) server = link.substring(0, link.indexOf("/", 7));
        
        if ("".equals(path)) { // this only happens with links like: http://www.toly.net
            link = link + "/";
        } else if (hasExtension(path)) {
            link = link.substring(0, link.lastIndexOf("/") + 1);
        } else if (!path.endsWith("/")) {   // here we can have a page
            
            // this can only be tested with a connection 
            try {
                URLConnection uc = url.openConnection();
                int type = contentType(uc);
                if (type == CONTENTTYPE_HTML) {
                    path = path + "/";
                }
            } catch (IOException ioe) {
                log.warn("Can not open connection " + url + " : " + ioe );
            }
            
            link = server + path;   // make sure query and fragment (#sdf) are gone
            link = link.substring(0, link.lastIndexOf("/") + 1);
        }
        */
        log.debug("url: " + url + ", returning: " + dir);
        
        try {
            return new URL(dir);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Creates the directory/filename to save a file (= url - url startdir),
     * does not start with a "/". The filename is the exact location of the 
     * file in the export directory. Goes something like this:
     *    1. substract the startdir from this url, that is the file to save
     *    2. check if /bla is a html-page, make it /bla/index.html if needed
     *       possible input is:
     *       /bla
     *       /bla/
     *       /bla/blabla.html
     *       /   
     *
     * @param  url resource for which a filename is needed
     * @return path and filename that can be saved (f.e. pics/button.gif)
     */
//     public String makeFilename(URL url) {
//         String filename = url.getFile();    
//         filename = removeSessionid(filename);
//         
//         String link = url.toString();
//         link = removeSessionid(link);
//         
//         // path starting from startdirURL
//         int startdirlength = startdirURL.toString().length();
//         if (link.length() > startdirlength) {
//             filename = link.substring(startdirlength);
//         }
//         
//         if (filename.equals("") || filename.endsWith("/")) {
//             filename = filename + "index.html";
//         }
//     
//         return filename;
//     }
    
    /**
     * Creates the directory/filename to save a file (= url - url startdir),
     * does not start with a "/". The filename is the exact location of the 
     * file in the export directory. Goes something like this:
     *    1. substract the startdir from this url, that is the file to save
     *    2. check if /bla is a html-page, make it /bla/index.html if needed
     *       possible input is:
     *       /bla
     *       /bla/
     *       /bla/blabla.html
     *       /   
     *
     * @param  url resource for which a filename is needed
     * @param  type content-type of the file to save
     * @return path and filename that can be saved (f.e. pics/button.gif)
     */
//     public String makeFilename(URL url, int type) {
//         /*
//         
//         start: www.toly.nl/bla
//         link:  www.toly.nl/pics/button.gif
//         
//         filename: 1up/pics/buttons.gif
//         
//         start: www.toly.nl/bla/bla
//         link:  www.toly.nl/pics/button.gif
//         
//         filename: 2up/pics/buttons.gif
//         
//         */
//         String filename = url.getFile();    
//         filename = removeSessionid(filename);
//         
//         String link = url.toString();
//         link = removeSessionid(link);
//         
//         // path starting from startdirURL
//         int startdirlength = startdirURL.toString().length();
//         if (link.length() > startdirlength) {
//             filename = link.substring(startdirlength);
//         }
//         
//         log.debug("0: file: " + filename);
//         if (type == CONTENTTYPE_HTML) {
//             if (filename.equals("")) {
//                 filename = "index.html";
//             } else if (!filename.endsWith("/") && !hasExtension(filename)) {
//                 filename = filename + "/index.html";
//                 log.debug("1: /bla file: " + filename);
//             }
//             
//             if (filename.endsWith("/")) {
//                 filename = filename + "index.html";
//                 log.debug("2: /bla/ file: " + filename);
//             }
//         }
//     
//         return filename;
//     }
    
    /**
     * Returns the file or guesses it
     */
//     public static String getFileUrl(URL url, int type) {
//         String link = url.toString();
//         String filename = url.getFile();
//         filename = removeSessionid(filename);
//         
//         String serverpart = link;
//         if (link.indexOf("/", 7) > 7) {
//             serverpart = link.substring(0, link.indexOf("/", 7));
//         }
// 
//         if (type == CONTENTTYPE_HTML) {
//             if (filename.equals("")) {
//                 filename = "index.html";
//             } else if (!filename.endsWith("/") && !hasExtension(filename)) {
//                 filename = filename + "/index.html";
//                 log.debug("1: /bla file: " + filename);
//             }
//             
//             if (filename.endsWith("/")) {
//                 filename = filename + "index.html";
//                 log.debug("2: /bla/ file: " + filename);
//             }
//         }
//             
//         filename = serverpart + filename;
//         log.debug("server: " + serverpart + ", returning filename: " + filename);
//         return filename;
//     }
    
    /**
     * remove ;jsessionid=a69bd9e162de1cfa3ea57ef6f3cf03af
     */
    public static String removeSessionid(String str) {
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
     * Opens and tests a connection to an url
     *
     * @param  url
     * @return a connection or null in case of a bad response (f.e. not a 200)
     */
//     private static URLConnection getURLConnection(URL url) throws SocketException, IOException {
//         URLConnection uc = url.openConnection();
//         if (url.getProtocol().equals("http")) {
//             HttpURLConnection huc = (HttpURLConnection)uc;
//             int res = huc.getResponseCode();
//             if (res == -1) {
//                 log.error("Server error, bad HTTP response: " + res);
//                 return null;
//             } else if (res < 200 || res >= 300) {
//                 log.warn(res + " - " + huc.getResponseMessage() + " : " + url.toString());
//                 return null;
//             } else {
//                 log.debug("url from uc: " + huc.getURL().toString());
//                 return huc;
//             }
//         } else if (url.getProtocol().equals("file")) {
//             InputStream is = uc.getInputStream();
//             is.close();
//             // If that didn't throw an exception, the file is probably OK
//             return uc;
//         } else {
//             // return "(non-HTTP)";
//             return null;
//         }
//     }
    
    /**
     * Creates an empty file in the save directory, checks if its directories exist (but not itself).
     *
     * @param  path the exact path from the startposition of the export (that's seen as 'root')
     * @return file
     */
    public File getFile(String path) {
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
     public static boolean hasExtension(String file) {
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
    private void addParseURL(URL url) {
        synchronized(parseURLs) {
            if (!parseURLs.contains(url)) parseURLs.add(url);
        }
    }

    private URL getParseURL() {
        URL url = null;
        synchronized(parseURLs) {
            if (!parseURLs.isEmpty()) url = parseURLs.remove(0);
        }
        return url;
    }

}
