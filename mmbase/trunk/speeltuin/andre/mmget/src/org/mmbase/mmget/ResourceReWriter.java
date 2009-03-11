package org.mmbase.mmget;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Rewrites a resource found on an url to disk. 
 *
 * @author Andr&eacute; van Toly
 * @version $Id: ResourceReWriter.java,v 1.1 2009-03-11 08:34:20 andre Exp $
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
        this.url = url;
        this.uc = super.uc;

        if (uc == null) { 
            MMGet.ignoredURLs.add(url);        
            throw new MalformedURLException("Not found/correct? : " + url);
        }
        
        this.filename = makeFilename(url);
        this.contenttype = MMGet.contentType(uc);

    }
    
  
    /**
     * Saves it.
     */
    protected void write() throws IOException {
        log.debug("rewriting: " + url + ", filename: " + filename);
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
        
        MMGet.savedURLs.put(url, filename);
        log.debug("Saved: " + f.toString() );
        
    }
    
 }
