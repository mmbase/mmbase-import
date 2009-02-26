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
 * @version $Id: MMGet.java,v 1.1 2009-02-26 16:29:00 andre Exp $
 */
public final class MMGet {
    
    private static final Logger log = Logging.getLoggerInstance(MMGet.class);
        
    public static final String CONFIG_FILE = "mmget.xml";
    private static final UtilReader reader = new UtilReader(CONFIG_FILE, new Runnable() {
                                                 public void run() {
                                                     configure(reader.getProperties());
                                                 }
                                             });
	/* link to start exporting from and directory of the start url  */
    public static String url;
	protected static URL startURL;
	protected static URL startdirURL;
    
    /* location the files should be saved to, directory to save files should be in the webroot (for now) */
    public static String directory;
	protected static File savedir;

    /* not wanted: offsite etc. */
	protected Set<URL> ignoredURLs = new HashSet<URL>();
	/* urls to parse (html, css) */
	protected List<URL> parseURLs = Collections.synchronizedList(new ArrayList<URL>());
	/* url -> filename */
	protected HashMap<URL,String> savedURLs = new HashMap<URL,String>();
	/* url -> link in page/new link in page */
	protected Map<URL,Map<String,String>> url2links = Collections.synchronizedMap(new HashMap<URL,Map<String,String>>());
	
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
		configure(reader.getProperties());
		File datadir = MMBase.getMMBase().getDataDir();
		ResourceLoader webroot = ResourceLoader.getWebRoot();
		
		startURL = new URL(url);
		startdirURL = getDirectoryURL(startURL);
		if (startdirURL.toString().length() > startURL.toString().length()) 
		        startURL = startdirURL;
		
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
	 * @param	url	The link to start from, normally the homepage
	 * @param	dir	The directory to save the files to
	 * @return	Message with the results
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
		
        Future<String> fthread = ThreadPools.jobsExecutor.submit(new Callable() {
	             public String call() {
					  return start();
	             }
       	    });
		
		try {
		    status = fthread.get();
		} catch(ExecutionException e) {
		    log.error(e);
		} catch(InterruptedException e) {
		    log.error(e);
		}
		
		StringBuilder info = new StringBuilder(status);
		info.append("\n***    url: ").append(startURL.toString());
		info.append("\n**    dir.: ").append(startdirURL.toString());
		info.append("\n* saved in: ").append(savedir.toString());
		status = info.toString();
		log.info(status);
		return status;
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
        
        readUrl(startURL);
        return "Job finished?!";
    }

	/**
	 * Parses urls it recieves.
	 * @param url   to html page or css
	 */
    private void readUrl(URL url) {
        log.debug("---------------------------------------------------------------------");
        log.debug("reading:   " + url.toString());
        
    	UrlReader reader = null;
		try {
            reader = new UrlReader(url);
		} catch (IOException e) {
			log.error("Can't parse: " + e);
			return;
		}
		if (reader == null) return;
		
		try {
			ArrayList<String> links = reader.getLinks();
        	Map<String,String> links2files = new HashMap<String,String>();    	/* maps a harvested link to the resulting saved file if different */
			
            URL dirURL = getDirectoryURL(url);
            if (startdirURL == null) startdirURL = dirURL;
			String calcUrl = startdirURL.toString() + makeFilename(url, reader.getContentType());
			String calcDir = calcUrl.substring(0, calcUrl.lastIndexOf("/"));
            
            log.debug("directory: " + dirURL.toString());
			log.debug("@ calcUrl: " + calcUrl);
			log.debug("@ calcDir: " + calcDir);
			
            Iterator<String> it = links.iterator();
            while (it.hasNext()) {
                String link = it.next();
                link = removeSessionid(link);   // remove sessionid crud etc. (changes over time)
                
                URL linkURL;
                if (link.indexOf("://") < 0) {
                    linkURL = new URL(url, link);
                } else {
                    linkURL = new URL(link);
                }
                
                if (ignoredURLs.contains(linkURL)) continue;
                if (!linkURL.getHost().equals(url.getHost())) {
                    //log.info(linkURL.toString() + " -- OFFSITE, not following");
                    ignoredURLs.add(linkURL);
                    continue;
                }
                if (linkURL.toString().length() < startdirURL.toString().length()) {    // BUG: Klopt niet!
                    //log.info(linkURL.toString() + " -- UP TREE, not following");
                    ignoredURLs.add(linkURL);
                    continue;
                }
                                
                // save resource
                String filename = saveResource(linkURL);
                if (filename == null) continue;
                
                // !!? String dir = dirURL.toString();  /* remove last / from dir for UriParser */
                // !!? if (dir.endsWith("/")) dir = dir.substring(0, dir.lastIndexOf("/"));
                
                String calclink = startdirURL.toString() + filename;    // 'calculated' link
                String relative = UriParser.makeRelative(calcDir, calclink);

                if (!"".equals(link) && !links2files.containsKey(link) && !link.equals(relative)) { // only when different
                    log.debug("link: " + link + ", relative: " + relative);
                    links2files.put(link, relative); /* /dir/css/bla.css + ../css/bla.css */
                }
                
            } // while ends
    		
    		reader.close();
		    synchronized(url2links) {
    		    if (!url2links.containsKey(url)) url2links.put(url, links2files);
    		}
    		saveResource(url);
    		
    		URL nextURL = getParseURL();
            if (nextURL != null) readUrl(nextURL);  // recurse!
            
    	} catch (IOException e) {
    		log.error("IOException: " + e);
		}
        
    }
	
	/**
	 * Saves a url and returns the filename. Returns null when no connection.
	 * @param  url
	 * @return the filename of the saved file or null if we dit not succeed to connect
	 */
	protected String saveResource(URL url) throws IOException {
        if (savedURLs.containsKey(url)) {
            return savedURLs.get(url);
        }
        
        URLConnection uc = null;
        try {
            uc = getURLConnection(url);
        } catch (SocketException e) {
            log.warn(e);
        }
        if (uc == null) return null;

        int type = contentType(uc);
        if (type > 0) {
            if (url2links.containsKey(url)) {
                return rewriteSaveResource(url, uc);
            } else {
                log.debug("Not parsed yet: " + url.toString());
                addParseURL(url);
                return makeFilename(url, type);
            }
        }
        
        String filename = makeFilename(url, type);
        File f = getFile(filename);
        if (f.exists()) {
            //log.warn("File '" + f.toString() + "' already exists, deleting it and saving again.");
            f.delete();
        }

        BufferedInputStream  in  = new BufferedInputStream(uc.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        byte[] buf = new byte[1024];
        int b = 0;
        while ((b = in.read(buf)) != -1) {
            out.write(buf, 0, b);
        }
        
        out.flush();
        in.close();
        out.close();
        
        savedURLs.put(url, filename);
        log.debug("Saved: " + f.toString() );
	    
	    return filename;
	}

	/**
	 * Saves and rewrites the links in the resource to (relative?) ones that work
	 * on the filesystem. Only for HTML or CSS (text) files of course.
	 * @param url
	 * @param uc the already elsewhere created URLConnection for efficiency
	 */
	protected String rewriteSaveResource(URL url, URLConnection uc) throws IOException {
        String filename = makeFilename(url, contentType(uc));
        File f = getFile(filename);
        if (f.exists()) {
            //log.warn("File '" + f.toString() + "' already exists, deleting it and saving again.");
            f.delete();
        }
        
        Map<String,String> links2files = url2links.get(url);        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        PrintWriter out = new PrintWriter(new FileWriter(f));
        String line;
        while((line = in.readLine()) != null) {
            if (links2files != null) {
                for (Map.Entry<String,String> pair : links2files.entrySet()) {
                    String link = pair.getKey();
                    String file = pair.getValue();

                    StringBuilder sbl = new StringBuilder();
                    sbl.append("\"").append(link);
                    
                    StringBuilder sbf = new StringBuilder();
                    sbf.append("\"").append(file).append("\"");
                    
                    int pos = line.indexOf(sbl.toString());
                    if (pos > -1) {
                        int pos2 = line.indexOf("\"", pos + 1);
                        log.debug("pos: " + pos + ", pos2: " + pos2);
                        String linelink = line.substring(pos, pos2 + 1);
                        log.debug("linelink: " + linelink);
                        
                        // compensate for ;jsessionid=ECF5A0BB7709202CEDC4D7FBA3AC3AAD
                        if ((pos2 - pos) > link.length() && linelink.indexOf(";") > -1) {
                            link = linelink;
                        } else {
                            sbl.append("\"");
                            link = sbl.toString();
                        }
                        log.debug("link: " + link);
                        
                        //sbl.append("\"");
                        line = line.replace(link, sbf.toString());
                        log.debug("replaced '" + link + "' with '" + sbf + "' in: " + filename);
                    }
                }
            }
            out.write(line + "\n");
        }
        out.flush();
        in.close();
        out.close();
        
        savedURLs.put(url, filename);
        log.debug("Saved: " + f.toString());
        
        return filename;
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
	    String link = url.toString();
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
        
	    log.debug("url: " + url + ", returning: " + link);
	    
	    try {
    	    return new URL(link);
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
	 * @param  type content-type of the file to save
	 * @return path and filename that can be saved (f.e. dir/bla)
	 */
	public String makeFilename(URL url, int type) {
        String filename = "";
        
        String link = url.toString();
        link = removeSessionid(link);
        
        // path starting from startdirURL
        int startdirlength = startdirURL.toString().length();
        if (link.length() > startdirlength) {
            filename = link.substring(startdirlength);
        }
        
        //log.debug("0: file: " + filename);
        if (type == CONTENTTYPE_HTML) {
            if (filename.equals("")) {
                filename = "index.html";
            } else if (!filename.endsWith("/") && !hasExtension(filename)) {
                filename = filename + "/index.html";
                //log.debug("1: /bla file: " + filename);
            }
            
            if (filename.endsWith("/")) {
                filename = filename + "index.html";
                //log.debug("2: /bla/ file: " + filename);
            }
        }
    
        return filename;
	}
	
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
	private static URLConnection getURLConnection(URL url) throws SocketException, IOException {
   		URLConnection uc = url.openConnection();
   		if (url.getProtocol().equals("http")) {
   			HttpURLConnection huc = (HttpURLConnection)uc;
			int res = huc.getResponseCode();
   			if (res == -1) {
   			    log.error("Server error, bad HTTP response: " + res);
   			    return null;
   			} else if (res < 200 || res >= 300) {
   			    log.warn(res + " - " + huc.getResponseMessage() + " : " + url.toString());
				return null;
		    } else {
		        return huc;
		    }
   		} else if (url.getProtocol().equals("file")) {
   		    InputStream is = uc.getInputStream();
   		    is.close();
   		    // If that didn't throw an exception, the file is probably OK
			return uc;
   		} else {
   			// return "(non-HTTP)";
			return null;
   		}
	}
	
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
     * @param   file    path or filename to check
     * @return  true if it contains an extension like .html f.e.
     */
     public static boolean hasExtension(String file) {
        int i = file.lastIndexOf(".");
        return (i != -1 && i != file.length() - 1);
    }
    
	/** 
     * Extracts the link from a tag.
     *
     * @param tag    the first parameter
     * @return       a link to a resource hopefully
     */
    public static String extractHREF(String tag) {
    	String lcTag = tag.toLowerCase(); 
    	String attr;
    	int p1, p2, p3, p4;
    	
    	if (lcTag.startsWith("<a ") || lcTag.startsWith("<link ") || lcTag.startsWith("<area ")) {
            attr = "href";
        } else {
            attr = "src"; 		// TODO: src's of css in html
        }
    	
    	p1 = lcTag.indexOf(attr);
    	if (p1 < 0) {
    		log.warn("Can't find attribute '" + attr + "' in '" + tag + "'");
    	}
    	p2 = tag.indexOf("=", p1);
    	p3 = tag.indexOf("\"", p2);
    	p4 = tag.indexOf("\"", p3 + 1);
    	if (p3 < 0 || p4 < 0) {
    		log.warn("Invalide attribute '" + attr + "' in '" + tag + "'");
    	}
    	
    	String href = tag.substring(p3 + 1, p4);
    	return href;
    }

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
