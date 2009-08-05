/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import java.io.*;

/**
 * this class supports by copying/deleting/moving/writing files.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class FileUtils {
    
    /**
     * copy all files starting from the source. If the source is one file, only one file will be copied.
     * If the source is a directory the directory will be copied (including subdirectories).
     * @param source the source
     * @param destination the destination
     */
    static public void copyFiles(String source, String destination) throws Exception {
        File file = new File(source);
        if (file.isDirectory()) {
            // Create the directory
            File newdir = new File(destination);
            newdir.mkdirs();
            // Add an extra file separator after the filename if necesary
            if(source.length()-1!=source.lastIndexOf(File.separator)) {
                source+=File.separator;
            }
            if(destination.length()-1!=destination.lastIndexOf(File.separator)) {
                destination+=File.separator;
            }
            String[] files = file.list();
            for(int i=0;i<files.length;) {
                copyFiles(source+files[i], destination+files[i]);
                i++;
            }
        } else {
            copyFile(source, destination);
        }
    }
    
    /**
     * copy one file. This method reads the file into a buffer and writes it to the destination file.
     * With big files this will fail, and we will have to improve the method.
     * @param sourcefile the source
     * @param destinationfile the destination
     */
    static public void copyFile(String sourcefile, String destinationfile) throws Exception {
        // To prevent that an older longer file will leave data in the new file.
        File file = new File(destinationfile);
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile source = new RandomAccessFile(sourcefile,"r");
        RandomAccessFile destination = new RandomAccessFile(file,"rw");
        
        //System.out.println("Copying "+sourcefile +" -> "+destinationfile+" ("+source.length()+")");
        byte[] bytes = new byte[(int)source.length()];
        source.read(bytes);
        destination.write(bytes);
        source.close();
        destination.close();
    }
    
    /**
     * delete file(s) starting with the source. If the source is a file, only the file will be removed. If source is a directory inclusive subdirectories will be removed.
     * @param source the starting point for removing.
     */
    static public void deleteFiles(String source) throws Exception {
        File file = new File(source);
        if (file.isDirectory()) {
            if(source.length()-1!=source.lastIndexOf(File.separator)) {
                source+=File.separator;
            }
            String[] files = file.list();
            for(int i=0;i<files.length;) {
                deleteFiles(source+files[i]);
                i++;
            }
            file.delete();
        } else {
            file.delete();
        }
    }
    
    /**
     * writes a string into a file
     * @param filename name of the file
     * @param value the string to write
     */
    static void saveFile(String filename,String value) throws Exception {
        File sfile = new File(filename);
        
        DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
        scan.writeBytes(value);
        scan.flush();
        scan.close();
    }
}
