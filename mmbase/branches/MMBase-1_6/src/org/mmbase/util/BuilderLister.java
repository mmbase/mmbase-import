/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.io.StringWriter;

import java.util.*;

/**
 * Gives an xml-representation of a dir structure with builders
 * (TODO: optimize by using StringBuffers
 * @author Gerard van Enk
 * @version $Id: BuilderLister.java,v 1.1 2002-10-22 07:48:06 gerard Exp $
 *
 */
public class BuilderLister {
    // logger not used at the moment
    //private static Logger log = Logging.getLoggerInstance(BuilderLister.class.getName());

    /**
     * Empty constructor
     */
    public BuilderLister () {}


    /**
     * Directory name containing the builders
     *
     */
    private String dirname;

    /**
     * Base directory structure
     *
     */
    private DirStructure builderdir;

    /**
     * Holder of the result, the xml code with all the builders
     *
     */
    protected String result;

    /**
     * Set the base directory where the builders can be found
     *
     * @param dirname String with the name of the 
     *        directory where the builders can be found
     */
    public void setDirname(String dirname) {
        this.dirname = dirname;
    }

    /**
     * Get the resulting xml with all the builders
     *
     * @return String with the xml-structure with all the builders
     */
    public String getResult() {
        return result;
    }

    /**
     * Build a hierarchy of DirStructure's and BuilderFile's 
     * for the directory that has been set
     *
     */
    public void build() {
        File f = new File(dirname);
        builderdir = build(f);
    }


    /**
     * Build a hierarchy of DirStructure's and/or BuilderFile's for input File
     *
     * @param f a <code>File</code> with a directory which containts builders
     * @return a <code>DirStructure</code> with all the builders and subdirs
     */
    private DirStructure build(File f) { 
        // this is some code i found in an example somewhere on the internet
        if (!f.exists()) return null;
        if (!f.isDirectory()) return null; 
    
        // f is an existing directory
        String path = f.getPath();
        String name = f.getName();
        DirStructure builderdir = new DirStructure(path, name);

        // Loop thru files and directories in this path
        String[] files = f.list();
        for (int i = 0; i < files.length; i++) {
            File f2 = new File(path, files[i]);
            if (f2.isFile()) {
                builderdir.addFile(new BuilderFile(path, files[i]));
            } else if (f2.isDirectory()) {
                File f3 = new File(path, files[i]);
                DirStructure m = build(f3); // recursive call
                if (m != null) { builderdir.addDir(m); }  
            }
        }
        return builderdir;
    }

    /**
     * Write a line of xml for every builder that has been found
     * in the result string
     *
     * @param f A File with a builder
     * @param level not really used here
     */
    protected void outFile(BuilderFile f, int level) {
        String name = f.getName();
        result += "<builder name=\"" + f.getName() + "\" source=\"" 
               + f.getPath()
               + System.getProperty("file.separator") 
               + f.getName() + "\"/>\n";
    }  
  
    /**
     * Write a line of xml for every dir that has been found in the result
     *
     * @param d A directorystructure inside the base dir
     * @param level not really used here
     */
    protected void outDir(DirStructure d, int level) {
        String name = d.getName();
        if (!name.equals("CVS")) {
            result += "<buildertype name=\""+name+"\" >\n" ;
        }
    }
  
    /**
     * Finish the result with the ending tag
     *
     */
    protected void outEndDir() {
        result += "</buildertype>\n";
    }
  
    /**
     * Start listing the contents of the directory
     *
     */
    public void list() {
        if (builderdir == null) {
            System.out.println("Not a valid directory");
            return;
        }
        result = "";
        list(builderdir, 0);
    }
  
    /**
     * Recursive method for listing directory content
     *
     * @param m a directory structure that needs to be listed
     * @param level counter to see how many levels deep 
     *        into the directory structure
     */
    private void list(DirStructure m, int level) {
        // ignore CVS-dirs
        if (!m.getName().equals("CVS")) {
            level++;
            Vector md = m.getDirs();
            for (int i = 0; i < md.size(); i++) {
                DirStructure d = (DirStructure)md.elementAt(i);
                outDir(d, level);
                list(d, level); // recursive call
                
            }
            Vector mf = m.getFiles();
            if (level==1) {
                outDir(m,level);
            }
            for (int i = 0; i < mf.size(); i++) {
                BuilderFile f = (BuilderFile)mf.elementAt(i);
                outFile(f,level);
            }
            outEndDir();
        }
    }



    /**
     * Main method can be called from an Ant build file and will return
     * the xml with a listing of all the builders
     *
     * @param args base dir to start with
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            BuilderLister bulLister = new BuilderLister();
            bulLister.setDirname(args[0]);
            bulLister.build();
            bulLister.list();
            System.out.println("<builders>");
            System.out.println(bulLister.getResult());
            System.out.println("</builders>");
        } else {
            System.out.println("usage: java BuilderLister <basedirwithbuilderconfig>");
        } 
    }


    /**
     * Internal directory structure
     */ 
    protected class DirStructure {
        // path to dir
        private String path;
        // name of dir
        private String name;
        // All files in this directory:
        private Vector files = new Vector();
        // All directories in this directory:
        private Vector dirs = new Vector();

        /**
         * Creates a new <code>DirStructure</code> instance.
         *
         * @param path a <code>String</code> containing the path of 
         * this directory structure
         * @param name a <code>String</code> containing the name of 
         * this directory structure
         */
        public DirStructure(String path, String name) {
            this.path = path;
            this.name = name;
        }
  
        /**
         * Get the name of this directory structure
         *
         * @return a <code>String</code> with the name of this directory structure
         */
        public String getName() { return name; }  


        /**
         * Get the path of this directory structure
         *
         * @return a <code>String</code> with the path of this directory structure
         */
        public String getPath() { return path; }

  
        /**
         * Add a file to this directory structure
         *
         * @param f a <code>BuilderFile</code> containing the file to add
         */
        public void addFile(BuilderFile f) { files.addElement(f); }  

        /**
         * Add a directory to this directory structure
         *
         * @param d a <code>DirStructure</code> containing the dir to add
         */
        public void addDir(DirStructure d) { dirs.addElement(d); }

        /**
         * Get all the files in this directory structure
         *
         * @return a <code>Vector</code> containing all the Files 
         */
        public Vector getFiles() { return files; }  
  
        /**
         * Get all the directories in this directory structure
         *
         * @return a <code>Vector</code> containing all the dirs 
         */
        public Vector getDirs() { return dirs; }  
  
    }

    /**
     * Internal builder file structure
     */
    protected class BuilderFile {
        // path of the builder file
        private String path;
        // name of the builder file
        private String name;
  
        /**
         * Creates a new <code>BuilderFile</code> instance.
         *
         * @param path a <code>String</code> with the path of this file
         * @param name a <code>String</code> with the name of this file
         */
        public BuilderFile(String path, String name) {
            this.path = path;
            this.name = name;
        }  
  
        /**
         * Get the name of the file
         *
         * @return a <code>String</code> with the file name
         */
        public String getName() { return name; }  

        /**
         * Get the path of this file
         *
         * @return a <code>String</code> with the path of this file
         */
        public String getPath() { return path; }  
    }
}
