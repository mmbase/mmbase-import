/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Directories contains methods for retrieving all file entries starting from a given path.
 * It also goes down the directory tree in search for file entries.
 * A wildcard can be used in searching for file entries.
 * It also contains sort methods to sort file entries.
 * Used by a device buidlers (i.e. Cameras), and by SCAN
 * @application SCAN or Devices
 * @author David V van Zeventer
 * @version $Id$
 */
public class DirectoryLister {

    // logger
    private static Logger log = Logging.getLoggerInstance(DirectoryLister.class.getName());

    /**
     * Adds all elements from Vector v2 to Vector v1.
     */
    private Vector vectorJoin (Vector v1, Vector v2) {
        for (Enumeration e = v2.elements (); e.hasMoreElements ();) {
            v1.addElement (e.nextElement ());
        }
        return v1;
    }

    /**
     * Creates a vector with all files in a specified directory. If the
     * directory has sub-directories, these sub-directories are scanned
     * recursively. Not just filenames are stored in the vector, but the
     * complete pathnames.
     */
    private Vector _directory (String dir, String ext) {
        Vector v = new Vector ();
        File d = new File (dir);    //Create a new fileobj for this directory
        String [] files = d.list ();    //List files and store in files array.
        String path = null;

        if(files==null) {
            log.error("Cannot read in directory - "+dir);
            return v;
        }

        for (String element : files) {
            path = dir + File.separator + element;    //Build full pathname for this entry.
            d = new File (path);    //Assign new fileobject for this newly build path.

            if ( !d.isDirectory() && element.endsWith(ext) ) {  //fileobj d is not a directory
                v.addElement(path);                 //AND has the right extension.
            } else {
                if (d.isDirectory()) {                 //fileobj d is a directory.
                    vectorJoin (v, _directory (path, ext));
                }
            }
        }
        return v;
    }

    /**
     * Returns all filepaths that follow a certain pattern.
     * @param args The requeststring to be used. This string consists of a directory,
     * followed by an extension.
     * @return a <code>vector</code> containing all filepaths requested.
     */
    public Vector getDirectories(String args) {
        String methodname ="getDirectories";
        Vector v = new Vector ();
        String fileSpec=null;

        log.debug(methodname+": Getting dirs using args: "+args);

         StringTokenizer st = new StringTokenizer (args, ";");
         while (st.hasMoreTokens ()) {

            /* fileSpec is a string that consists of a directory, followed by a '*.',
             * followed by an extension.
             * i.e. /usr/lib/pclown/*.jokes
             */
             fileSpec = st.nextToken ();

             /* In the HTML-file, the file-specification should have one of the following formats:
              * 1. To list all the files in directory <dir>:
              *    /<dir>
              *    example: /data/import
              * 2. To list files in directory <dir> ending with extension <ext>:
              *    /<dir>/*.<ext>
              *    example: /data/import/*.wav
              */
            File f = new File (fileSpec);
            if (f.isDirectory ()) {
                vectorJoin (v, _directory (fileSpec, ""));
            } else {
                // Ok, so the specified filename is not a directory,
                // but a file specification with wildcards.
                int li = fileSpec.lastIndexOf ('/');
                /* split fileSpec in it's parts, i.e.
                 *   dir      = "/usr/lib/pclown"
                 *   file     = "*.jokes"
                 *   ext      = ".jokes"
                 */
                String dir  = fileSpec.substring (0, li);
                String file = fileSpec.substring (li + 1);
                String ext  = file.substring (1);
                f = new File (dir);
                    if (f.isDirectory ()) vectorJoin (v, _directory (dir, ext));
                }
            }
            return v;
        }

    /**
     * Creates a Vector with File-objs by using the
     * directories vector. Used by {#link #sortDirectoriesOnModTime}
     */
    private Vector<File> createFilesVector(Vector directories) {
        Vector<File> filesvec = new Vector<File>();
        String filepathname=null;
        File file=null;

        Enumeration dirs_enum = directories.elements();
        while (dirs_enum.hasMoreElements()) {
            filepathname = (String)dirs_enum.nextElement();
            file = new File(filepathname);
            filesvec.addElement(file);  //Add file to filesvec.
        }
        return filesvec;
    }

    /**
     * Sorts a Vector containing file pathnames on modification time.
     * @return a new sorted <code>Vector</code> containing filepathnames.
     */
    public Vector<String> sortDirectoriesOnModTime(Vector directories) {
        String methodname = "sortDirectoriesOnModTime";
        CompareInterface filecmp = new FileCompare();
        SortedVector sortedfilesvec = new SortedVector(filecmp); //Create new sortedvec.
        Vector<String> dirs_sorted= new Vector<String>();
        File file=null;

        Vector<File> filesvec = createFilesVector(directories); //Create filesvector.
        log.debug(methodname+": filesvec= "+filesvec);

        //Sort the filesvec on modification time.
        Enumeration<File> filesvec_enum = filesvec.elements(); //Enumerate all filesvec elements.
        while (filesvec_enum.hasMoreElements()) {
            sortedfilesvec.addSorted(filesvec_enum.nextElement());    //Add fileobj to sortedvec.
        }
        log.debug(methodname+": sortedfilesvec= "+sortedfilesvec);

        Enumeration sortedfilesvec_enum = sortedfilesvec.elements();
        while (sortedfilesvec_enum.hasMoreElements()) {
            file = (File)sortedfilesvec_enum.nextElement();
            dirs_sorted.addElement(file.getPath());    //Add filepathname to new sortedvec.
        }
        log.debug(methodname+": dirs_sorted = "+dirs_sorted);
        return dirs_sorted;
    }

    /**
     * Sort a Vector containing file pathnames using a comparefield argument.
     * First creates XFile objs from vector.
     * It then adds these objs to a SortedVector obj (sorted on comparefield)..
     * @return a <code>Vector</code> with 2 items : filepath & moddate
     */
    public Vector<String> sortDirectories(Vector directories, String comparefield) {

        String methodname = "sortDirectories";
        CompareInterface xfilecmp = new XFileCompare(comparefield); //Compare implementation that will be used, also specifying comparefield.
        SortedVector sortedxfiles = new SortedVector(xfilecmp);        //SortedVector containing xfileobjs.
        Vector<String> sorteddirs = new Vector<String>(); //Sorted Vector containing the xfile fields stored as strings.
        XFile xfile=null;
        int modtime=0;
        String filepath =null;

        log.debug(methodname+": Sorting dirs using comparefield:"+comparefield);

        Enumeration dirs_enum = directories.elements();
        while (dirs_enum.hasMoreElements()) {
            filepath = (String)dirs_enum.nextElement();
            xfile = new XFile(filepath);
            sortedxfiles.addSorted(xfile);     //Add xfile obj to sortedxfilesvector.
        }

        Enumeration xfiles_enum = sortedxfiles.elements();
        while (xfiles_enum.hasMoreElements()) {     //Enumerate xfiles vector and create return vector.
            xfile = (XFile)xfiles_enum.nextElement();
            filepath = xfile.getFilePath();
            //Get modtime field and subtract offset.
            modtime = (int) ((xfile.getModTime()-DateSupport.getMilliOffset())/1000);
            //Add filepath and modtime to sorteddirs vector
            sorteddirs.addElement(filepath);
            sorteddirs.addElement(time2DateAndTime_NoYear(modtime));
        }

        return sorteddirs;
    }

    /**
     * This method creates a vector with 3 items:
     * ITEM1=fileentry, ITEM2=moddate,
     * ITEM3=previewfilename if exists else fileentry.
     */
     public Vector createThreeItems(Vector<String> sorted,StringTagger tagger) {
        String methodname = "createThreeItems";
        Vector merged = new Vector();
        String typefmt=null,previewfmt=null,path=null,indexsymbol=null;
        Enumeration<String> sort_enum=null;

        log.debug(methodname+": Creating 3 items vector.");
        typefmt    = tagger.Value("TYPEFORMAT");    //eg. fullsize.#.jpg
        previewfmt = tagger.Value("PREVIEWFORMAT");    //eg. fullsize-s.#.jpg
        indexsymbol= tagger.Value("INDEXSYMBOL");    //eg. #

        sort_enum = sorted.elements();
        if(sort_enum.hasMoreElements()) {
            //Retrieve path using a sorted entry.
            path = sort_enum.nextElement();
            int lastSlash = path.lastIndexOf('/');
            path = path.substring(0,lastSlash+1);

            //Check parameters and create a 3 items vector.
            if ((typefmt!=null)&&(previewfmt!=null)&&(indexsymbol!=null)) { //All params provided.
                log.debug(methodname+": TYPEFMT="+typefmt+" PREVIEWFMT="+previewfmt+" INDEXSYMBOL="+indexsymbol+" PROVIDED ->Creating vector");

                typefmt   =path+typefmt;  //Add path to typefmt & previewfmt
                previewfmt=path+previewfmt;

                sort_enum = sorted.elements(); //Find sorted entry & date
                while (sort_enum.hasMoreElements()) {

                    String sentry = sort_enum.nextElement();

                    //Check if the sorted entry equals the typeformat
                    if(checkEntryformat(indexsymbol,typefmt,sentry)) {
                        merged.addElement(sentry);  //ITEM1
                        String date = sort_enum.nextElement();
                        merged.addElement(date);    //ITEM2

                        //Create preview entry & check if exists in sorted vector.
                        String pentry = createPreviewEntry(indexsymbol,typefmt,previewfmt,sentry);
                        if (sorted.contains(pentry)) {
                            merged.addElement(pentry);  //ITEM3
                        } else {
                            merged.addElement(sentry);  //ITEM3=ITEM1
                        }
                    }else{    //sorted entry is other ->skip date
                        String skipdate=sort_enum.nextElement(); //Skipping date
                        log.debug("skipping date " + skipdate);
                    }
                }//while loop
            } else if ((typefmt==null)&&(previewfmt==null)&&(indexsymbol==null)) { //All params empty.
                log.debug(methodname+": TYPEFMT & PREVIEWFMT & INDEXSYMBOL EMPTY -> Creating std vector where ITEM3=ITEM1");

                sort_enum = sorted.elements();
                while (sort_enum.hasMoreElements()) {    //Create std vector.
                    String sentry = sort_enum.nextElement();
                    merged.addElement(sentry);  //ITEM1
                    String date   = sort_enum.nextElement();
                    merged.addElement(date);    //ITEM2
                    merged.addElement(sentry);  //ITEM3=ITEM1
                }
            } else { //1 OR 2 params are empty.
                log.debug(methodname+": Error typefmt="+typefmt+" previewfmt="+previewfmt+" indexsymbol="+indexsymbol+" ->Returning empty vector.");
            }
        }
        return merged;
    }

    /**
     * Checks if the entry is compatible with the type format.
     */
    private boolean checkEntryformat(String indexsymbol,String typefmt,String entry) {
        boolean compatible=false;
        int digits = 0;
        int pos = typefmt.indexOf(indexsymbol);
        String ls_typefmt = typefmt.substring(0,pos);
        String ls_entry   = entry.substring(0,pos);
        if(ls_entry.equals(ls_typefmt)) {  //Leftside compare
            boolean checkHex = indexsymbol.equals("$");
            while ( ((entry.charAt(pos+digits)>='0')&&(entry.charAt(pos+digits)<='9'))
                || ( checkHex
                    && (((entry.charAt(pos+digits)>='a') && (entry.charAt(pos+digits)<='f'))
                        || ((entry.charAt(pos+digits)>='A') && (entry.charAt(pos+digits)<='F'))))) {
                digits++;  //Count digits in entry.
            }
            if (entry.charAt(pos+digits)==typefmt.charAt(pos+1)) {
                String rs_typefmt = typefmt.substring(pos+1);
                String rs_entry   = entry.substring(pos+digits);
                if (rs_entry.equals(rs_typefmt)) {  //Rightside compare
                    compatible=true;
                }
            }
        }
        return compatible;
    }

    /**
     * Creates preview entry from original entry by cutting out the file
     * number and replacing the index symbol in previewfmt with this number.
     * <br />
     * Eg:entry = fullsize.36.jpg -> <br />
     * previewentry = fullsize-s.36.jpg <br />
     * previewfmt= fullsize-s.#.jpg
     */
    private String createPreviewEntry(String indexsymbol,String typefmt,String previewfmt,String entry) {
        int digits=0;
        int pos = typefmt.indexOf(indexsymbol);
        while ((entry.charAt(pos+digits)>='0')&&(entry.charAt(pos+digits)<='9')) {
            digits++;
        }
        String number = entry.substring(pos,pos+digits);  //Cut out number eg. scan43.jpg ->43
        StringObject pfmt = new StringObject(previewfmt);
        String pentry = pfmt.replace(indexsymbol,number).toString();
        return pentry;
    }


    /**
     * Converts the integer timevalue to a String containing
     * hours, minutes, seconds, monthday, month in that order.
     */
    private String time2DateAndTime_NoYear(int time) {
        return DateSupport.getTime(time)+" "+DateSupport.getMonthDay(time)+" "+DateStrings.DUTCH_DATESTRINGS.getShortMonth(DateSupport.getMonthInt(time));
    }


    /**
     * Reverses the elementorder in a vector.
     */
    public Vector reverse(Vector v) {

        Vector rev = new Vector();
        int index= 0;
        int size = v.size();

        for (int i=1;i<=size;i++) {
            index = size - i;  // index=size - offset
            rev.addElement(v.elementAt(index));
        }
        return rev;
    }

    /**
     * Reverses the elementorder in a vector, taking into consideration that
     * the vector consists of sets of elements (items).
     */
    public Vector reverse(Vector v,int items) {

        Vector rev = new Vector();
        int index=0, sets=0;
        int size = v.size();

        if ((size%items!=0) || (items>size)) {
            log.debug("Reverse: Incompatible pair: Vectorsize: "+size+" Itemsnumber: "+items+
                      ", Returning old vector instead");
            return v;
        }

        sets = size/items;   // #sets of elements in vector,  eg 12/3= 4 sets.

        for (int i=1;i<=sets;i++) {
            for (int item=0;item<items;item++) {
                index = size - i*items + item;    // index=size - offset + itemnr
                rev.addElement(v.elementAt(index));
            }
        }
        return rev;
    }
}
