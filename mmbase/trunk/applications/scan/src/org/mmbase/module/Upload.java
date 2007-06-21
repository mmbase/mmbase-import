/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module;

import java.util.*;
import java.io.*;
import java.net.*;

import org.mmbase.util.*;

import org.mmbase.util.logging.*;

/**
 * the Upload module stores files that are uploaded.
 * At this time files van only be stored in memory.
 * nog wat uitleg over hoe je de .shtml maakt
 */
public class Upload extends ProcessorModule {

    private static Logger log = Logging.getLoggerInstance(Upload.class.getName());
    private Hashtable<String, FileInfo> FilesInMemory = new Hashtable<String, FileInfo>();
    private String fileUploadDirectory = null;

    public void init() {
        fileUploadDirectory=getInitParameter("fileUploadDirectory");
        log.debug("Upload module, fileUploadDirectory = "+fileUploadDirectory);
    }

    /**
     * handle the uploaded bytestream.
     */
    public boolean process(scanpage sp, Hashtable cmds, Hashtable vars) {

        // Get the place where to store the file
        // Currently implemented places are: mem:// and file://
        String filename = (String)cmds.get("file");

        // Get the posted bytearray
        byte[] bytes = null;
        try {
            bytes = sp.poster.getPostParameterBytes("file");
        } catch (Exception e) {
            log.error("Upload module postValue to large");
            return false;
        }

        if (sp.poster.getPostParameter("file_name") == null) {
            log.error("no filename ");
            return false;
        }

        log.debug("Upload module is storing "+filename);
        // Store in memory
        if(filename.indexOf("mem://")!=-1) {

            // Create file object in memory
            FileInfo fi = new FileInfo();
            fi.bytes= bytes;
            fi.name = sp.poster.getPostParameter("file_name");
            fi.type = sp.poster.getPostParameter("file_type");
            fi.size = sp.poster.getPostParameter("file_size");
            FilesInMemory.put(filename,fi);
            log.debug("Upload module saves in memory: "+fi.toString());
            return true;
        }
        // Store at filesystem
        if(filename.indexOf("file://")!=-1) {
            String fname = filename.substring(7);
            // If no filename is given the real filename will be used.
            if (fname.equals("")) {
                fname = sp.poster.getPostParameter("file_name");
            }
            if (fname.indexOf("..")<0) {
                saveFile(fileUploadDirectory+fname,bytes);
                log.debug("Upload module saved to disk: "+filename);
                return true;
            } else {
                log.error("Upload Filename may not contain '..'");
                return false;
            }
        }
        return false;
    }

    /**
     * deletes an uploaded file.
     * @param filename the name of the file, e.g. mem://filename
     */
    public void deleteFile(String filename) {
        // Is file located in memory?
        if(filename.indexOf("mem://")!=-1) {
            if(FilesInMemory.containsKey(filename)) {
                FilesInMemory.remove(filename);
            }
        }
    }

    /**
     * gets the bytearray of an uploaded file.
     * @param filename the name of the file, e.g. mem://filename
     */
    public byte[] getFile(String filename) {
        log.debug("Upload module is getting "+filename);
        // Is file located in memory?
        if(filename.indexOf("mem://")!=-1) {
            if(FilesInMemory.containsKey(filename)) {
                FileInfo fi = FilesInMemory.get(filename);
                return fi.bytes;
            }
        }
        if(filename.indexOf("http://")!=-1) {
            return getHttp(filename);
        }
        return null;
    }



    /**
     * save bytearray to filesystem
     * @param filename name of the file
     * @param value the actual bytes you want to save
     */
    private boolean saveFile(String filename,byte[] value) {
        File file = new File(filename);
        try {
            FileOutputStream outputstream = new FileOutputStream(file);
            outputstream.write(value);
            outputstream.flush();
            outputstream.close();
        } catch(Exception e) {
            e.printStackTrace();
            return(false);
        }
        return(true);
    }


    /**
     * this method gets the requested http page
     */
    private byte[] getHttp(String page) {
        URL pageToGrab=null;
        DataInputStream dis=null;

        try {
            pageToGrab = new URL(page);
            dis = new DataInputStream(pageToGrab.openStream());
        } catch (Exception e) {
            log.debug("Upload module cannot get page "+page);
            return null;
        }
        StringBuffer tekst= new StringBuffer();
        String get="";
        while (true) {
            try {
                get = dis.readLine();
                if (get==null) break;
                tekst.append(get);
            } catch (IOException io) {
                break;
            }
        }
        return tekst.toString().getBytes();
    }


        /*
         * a class to store an uploaded file into memory
         */
    class FileInfo {
        byte[] bytes= null;
        String name = null;
        String type = null;
        String size = null;

        public String toString() {
            return "name="+name+" type="+type+" size="+size;
        }
    }
}
