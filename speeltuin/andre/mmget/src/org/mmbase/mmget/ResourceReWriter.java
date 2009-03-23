package org.mmbase.mmget;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Writes and rewrites a resource found on an url to disk. 
 * Typically to be used for html and css files.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: ResourceReWriter.java,v 1.4 2009-03-23 21:12:53 andre Exp $
 */
public class ResourceReWriter extends ResourceWriter {
    private static final Logger log = Logging.getLoggerInstance(ResourceReWriter.class);
    
    private URL url;
    private HttpURLConnection uc = null;
    private static String filename;
    private static int contenttype;

    /**
     * Constructs rewriter.
     * @throws IOException When failed to write
     */
    public ResourceReWriter(URL url) throws IOException {
        super(url);
        //log.debug("Trying to download... " + url.toString() + " to " + filename);
        this.uc = super.uc;
        
        this.url = getUrl();
        this.contenttype = MMGet.contentType(uc);
        this.filename = makeFilename(url);
    }
  
    /**
     * Saves it.
     */
    protected void write() throws IOException {
        rewrite();
        // MMGet.savedURLs.put(url, filename);
        MMGet.addSavedURL(url, filename);
    }

    /**
     * Saves and rewrites the links in the resource to (relative?) ones that work
     * on the filesystem. Only for HTML or CSS (text) files of course.
     * @param url
     * @param uc the already elsewhere created URLConnection for efficiency
     */
    private void rewrite() throws IOException {
        log.debug("REwriting: " + url + " -> file: " + filename);
        File f = getFile(filename);
        if (f.exists()) {
            //log.warn("File '" + f.toString() + "' already exists, deleting it and saving again.");
            f.delete();
        }
        
        Map<String,String> links2files = MMGet.url2links.get(url);        
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        PrintWriter out = new PrintWriter(new FileWriter(f));
        String line;
        while((line = in.readLine()) != null) {
            if (links2files != null) {
                for (Map.Entry<String,String> pair : links2files.entrySet()) {
                    String link = pair.getKey();
                    String file = pair.getValue();

                    StringBuilder sblink = new StringBuilder();
                    sblink.append("\"").append(link);
                    
                    int pos1 = line.indexOf(sblink.toString());
                    if (pos1 > -1) {
                        int pos2 = line.indexOf("\"", pos1 + 1);
                        
                        String hitlink = line.substring(pos1 + 1, pos2);
                        String testlink = hitlink;
                        if (hitlink.indexOf(";") > -1) testlink = MMGet.removeSessionid(hitlink);
                        //log.debug("hitlink: '" + hitlink + "', testlink: '" + testlink + "'" + "', link: '" + link + "'");
                        if (!testlink.equals(link)) continue;
                        
                        line = line.replace(hitlink, file);
                        log.debug("replaced '" + link + "' with '" + file + "' in: " + filename);
                    }
                }
            }
            out.write(line + "\n");
        }
        out.flush();
        in.close();
        out.close();
        
        log.debug("Saved: " + url + " -> file: " + f.toString() );
    
    }
    
 }
