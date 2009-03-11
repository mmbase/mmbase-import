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
 * @version $Id: ResourceReWriter.java,v 1.2 2009-03-11 16:11:51 andre Exp $
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
        log.debug("Trying to download... " + url.toString() + " to " + filename);
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
        MMGet.savedURLs.put(url, filename);
    }

    /**
     * Saves and rewrites the links in the resource to (relative?) ones that work
     * on the filesystem. Only for HTML or CSS (text) files of course.
     * @param url
     * @param uc the already elsewhere created URLConnection for efficiency
     */
    private void rewrite() throws IOException {
        log.debug("REwriting: " + url + ", filename: " + filename);
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

                    StringBuilder sbl = new StringBuilder();
                    sbl.append("\"").append(link);
                    
                    StringBuilder sbf = new StringBuilder();
                    sbf.append("\"").append(file).append("\"");
                    
                    int pos1 = line.indexOf(sbl.toString());
                    if (pos1 > -1) {
                        int pos2 = line.indexOf("\"", pos1 + 1);
                        //log.debug("pos1: " + pos1 + ", pos2: " + pos2);
                        String linelink = line.substring(pos1, pos2 + 1);
                        //log.debug("linelink: " + linelink);
                        
                        // compensate for ;jsessionid=ECF5A0BB7709202CEDC4D7FBA3AC3AAD etc.
                        if ((pos2 - pos1) > link.length() && linelink.indexOf(";") > -1) {
                            link = linelink;
                        } else {
                            sbl.append("\"");
                            link = sbl.toString();
                        }
                        //log.debug("link: " + link);
                        
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
        
        log.debug("Saved: " + f.toString() );
    
    }
    
 }
