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
 * @version $Id: ResourceWriter.java,v 1.4 2009-03-12 10:55:20 andre Exp $
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
    public ResourceWriter(URL u) throws IOException {
        try {
            uc = (HttpURLConnection)getURLConnection(u);
        } catch (IOException e) {
            log.warn(e);
        }
        if (uc == null) { 
            MMGet.ignoredURLs.add(u);
            throw new MalformedURLException("Not found/correct? : " + u);
        }
        
        this.url = getUrl();
        this.contenttype = MMGet.contentType(uc);
        this.filename = makeFilename(url);
    }
    
    protected URL getUrl() {
        return uc.getURL();
    }
    
    protected String getFilename() {
        return filename;
    }
    
    protected int getContentType() {
        return contenttype;
    }
    
    protected void disconnect() {
        if (uc != null) { 
            log.debug("disconnecting... " + url.toString());
            uc.disconnect(); 
        }
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
        
        log.debug("Saved: " + f.toString() );
        MMGet.savedURLs.put(url, filename);
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
     * file in the export directory. The url should be the one from URLConverter 
     * to be able to conclude if we have a directory or a file (directory should end with a slash).
     *
     * @param  url of resource
     * @return path and filename that can be saved (f.e. pics/button.gif)
     */
    public String makeFilename(URL url) {
        String link = url.toString();
        link = MMGet.removeSessionid(link);
        
        String filename = link.substring(MMGet.serverpart.length());
        if (filename.startsWith("/")) filename = filename.substring(1);

        log.debug("0: file: " + filename);
        if (contenttype == MMGet.CONTENTTYPE_HTML) {
            if (filename.equals("")) {
                filename = "index.html";
            } else if (!filename.endsWith("/") && !MMGet.hasExtension(filename)) {
                filename = filename + "/index.html";
                log.debug("1: /bla file: " + filename); // TODO: add extra ../ to rewritten links !!?
            }
            
            if (filename.endsWith("/")) {
                filename = filename + "index.html";
                log.debug("2: /bla/ file: " + filename);
            }
        }

        log.debug("url: " + url.toString() + " -> file: " + filename);
        return filename;
    }
 }
