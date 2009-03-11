package org.mmbase.mmget;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.ResourceLoader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Writes a resource found on an url to disk. 
 *
 * @author Andr&eacute; van Toly
 * @version $Id: ResourceWriter.java,v 1.1 2009-03-11 08:34:20 andre Exp $
 */
public class ResourceWriter {
    private static final Logger log = Logging.getLoggerInstance(ResourceWriter.class);
    
    private URL url;
    protected HttpURLConnection uc = null;
    protected static String filename;
    protected static int contenttype;

    /**
     * Constructs  writer.
     * @throws IOException When failed to write
     */
    public ResourceWriter(URL url) throws IOException {
        log.debug("Trying to download .. " + url.toString());
        this.url = url;

        try {
            uc = (HttpURLConnection)getURLConnection(url);
        } catch (IOException e) {
            log.warn(e);
        }
        if (uc == null) { 
            MMGet.ignoredURLs.add(url);        
            throw new MalformedURLException("Not found/correct? : " + url);
        }
        
        this.filename = makeFilename(url);
        this.contenttype = MMGet.contentType(uc);
    }
    
    protected String getFilename() {
        return filename;
    }
    
    protected int getContentType() {
        return contenttype;
    }
    
    protected void disconnect() {
        if (uc != null) uc.disconnect();
    }
    
    /**
     * Saves it.
     */
    protected void write() throws IOException {
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
    
    /**
     * Opens and tests an URLConnection.
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
            } else if (res < 200 || res >= 400) {
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
            f = new File(MMGet.savedir, dir);
            if (!f.exists()) {
                if (f.mkdirs()) {
                    //log.debug("Directory created: " + savedir);
                } else {
                    log.warn("Directory '" + f + "' could not be created");
                }
            }
            resource = path.substring(path.lastIndexOf("/"), path.length());
        } else {
            f = MMGet.savedir;
            resource = path;
        }
        return new File(f, resource);
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
     * @return path and filename that can be saved (f.e. pics/button.gif)
     */
    public String makeFilename(URL url) {
        /*
        start: www.toly.nl/bla
        link:  www.toly.nl/pics/button.gif
        filename: 1up/pics/buttons.gif
        
        start: www.toly.nl/bla/bla
        link:  www.toly.nl/pics/button.gif
        filename: 2up/pics/buttons.gif
        */
        String filename = url.getFile();    
        filename = MMGet.removeSessionid(filename);
        
        String link = url.toString();
        link = MMGet.removeSessionid(link);
        
        // path starting from startdirURL
        int startdirlength = MMGet.startdirURL.toString().length();
        if (link.length() > startdirlength) {
            filename = link.substring(startdirlength);
        }
        
        log.debug("0: file: " + filename);
        if (contenttype == MMGet.CONTENTTYPE_HTML) {
            if (filename.equals("")) {
                filename = "index.html";
            } else if (!filename.endsWith("/") && !MMGet.hasExtension(filename)) {
                filename = filename + "/index.html";
                log.debug("1: /bla file: " + filename);
            }
            
            if (filename.endsWith("/")) {
                filename = filename + "index.html";
                log.debug("2: /bla/ file: " + filename);
            }
        }
        
        log.debug("filename: " + filename);
        return filename;
    }
 }
