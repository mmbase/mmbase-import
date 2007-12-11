/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.gui.flash;

import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.gui.html.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Implements the parsing and generating of dynamic flash files
 * @author Johannes Verelst
 * @author Daniel Ockeloen
 * @version $Id: MMFlash.java,v 1.25 2007-12-11 13:20:26 michiel Exp $
 */
public class MMFlash extends Module {

    static final Logger log = Logging.getLoggerInstance(MMFlash.class);

    private String classname = getClass().getName();
    private boolean debug = false;
    private String htmlroot;

    private int count=0;
    scanparser scanp;
    String subdir;
    String generatortemppath;
    String generatorpath;
    String generatorprogram;
    LRUHashtable lru=new LRUHashtable(128);
    MMBase mmb;

    public void init() {
        htmlroot = MMBaseContext.getHtmlRoot();
        mmb=(MMBase)getModule("MMBASEROOT");
        scanp=(scanparser)getModule("SCANPARSER");
        generatortemppath=getInitParameter("generatortemppath");
        log.debug("generatortemppath:'"+generatortemppath+"'");
        generatorpath=getInitParameter("generatorpath");
        log.debug("generatorpath:'"+generatorpath+"'");
        generatorprogram=getInitParameter("generatorprogram");
        log.debug("generatorprogram:'"+generatorprogram+"'");
        subdir=getInitParameter("subdir");
        log.debug("subdir:'"+subdir+"'");

        if (null == generatortemppath || "".equals(generatortemppath)) {
            generatortemppath = "" + MMBaseContext.getServletContext().getAttribute("javax.servlet.context.tempdir") + File.separator + "cache" + File.separator + "flash";
        }
        // check if we may create a file on location of generatorTempPath
        File tempPath = new File(generatortemppath);
        if(!tempPath.isDirectory()) {
            if (! tempPath.mkdirs()) {
                log.error("Generator Temp Path was not a direcory('" + generatortemppath + "'), please edit mmflash.xml, or create directory");
            } else {
                log.info("Created " + tempPath);
            }
        }

        try {
            File test = File.createTempFile("flash", "test", tempPath);
            test.delete();
        } catch (Exception e) {
            log.error("Could not create a temp file in directory:'" + generatortemppath + "' for flash, please edit mmflash.xml or change rights");
        }
        if (!generatortemppath.endsWith(File.separator)) {
            generatortemppath += File.separator;
        }


        // check if there is a program on this location
        try {
            (Runtime.getRuntime()).exec(generatorpath + generatorprogram);
        } catch (Exception e) {
            log.error("Could not execute command:'" + generatorpath+generatorprogram + "' for flash, please edit mmflash.xml");
        }
        log.debug("Module MMFlash started (flash-generator='" + generatorpath + generatorprogram + "' and can be executed and tmpdir is checked)");
    }

    public void onload() {
    }

    public MMFlash() {
    }

    /**
    * Return the generated flash with debug information.
    * @param sp the scanpage that includes the parameters needed to generate the flash
    * @return array of bytes containing the flash data
    */
    public byte[] getDebugSwt(scanpage sp) {
        String filename=htmlroot+sp.req.getRequestURI();
        byte[] bytes=generateSwtDebug(filename);
        return(bytes);
    }

    /**
    * Dynamically generated flash.
    *
    * This method parses a xml-script and generate flash.
    * This is done in 2 stages, first the xml is parsed through
    * scan, the next stage will generate the flash which will then
    * be returned.
    *
    * @param sp scanpage
    * @return the dynamically generated flash or null when error occured
    */
    public byte[] getScanParsedFlash(scanpage sp) {

        byte[] result = null;

        // get inputfile
        // -------------
        String url      = sp.req.getRequestURI();
        String filename = htmlroot+url;
        String query    = sp.req.getQueryString();

        log.debug("url("+url+"), filename("+filename+"), query("+query+")");

        // cache stage 1
        // -------------
        if (!sp.reload) {
            result =(byte[])lru.get(url+query);
            if (result!=null) {
                log.debug("Cache hit from disk+lru");
            } else {
                result=loadDiskCache(filename,query);
                if (result!=null) {
                    log.debug("Cache hit from from disk");
                    lru.put(url+query,result);
                }
            }
        }

        // cache miss or reload
        // --------------------
        if(result==null) {
            byte[] inp      = readBytesFile(filename);

            // check if file exists
            // --------------------
            if (inp == null) {
                log.error("Cannot find the sxf-file: '" + filename + "'!");
                return null;
            }

            sp.body = new String(inp);

            // oke try to parse it
            // -------------------
            if (scanp!=null) {
                try {
                    sp.body = scanp.handle_line(sp.body,sp.session,sp);
                } catch(Exception e) {}
            } else {
                log.error("The scanparser cannot be found! Check scanparser.xml in config-directory!");
            }

            File outputFile = createTemporaryFile("export", ".swf");
            outputFile.delete();
            Vector<File> tempFiles = new Vector<File>();
            tempFiles.add(outputFile);

            // now feed it to the xml reader
            CharArrayReader         reader  = new CharArrayReader(sp.body.toCharArray());
            XMLDynamicFlashReader   script  = new XMLDynamicFlashReader(reader);

            String body="";
            String src=script.getSrcName();
            if (src.startsWith("/")) {
                body+="INPUT \""+htmlroot+src+"\"\n";
            } else {
                String purl=url.substring(0,url.lastIndexOf('/')+1);
                src=purl+src;
                body+="INPUT \""+htmlroot+src+"\"\n";
            }
            body+="OUTPUT \"" + outputFile.getAbsolutePath() + "\"\n";

            String scriptpath=src;
            scriptpath=scriptpath.substring(0,scriptpath.lastIndexOf('/')+1);

            body+=addDefines(script.getDefines(),scriptpath,tempFiles);
            body+=addReplaces(script.getReplaces(),scriptpath);

            File inputFile = createTemporaryFile("input", ".sws");
            inputFile.delete();

            tempFiles.add(inputFile);
            saveFile(inputFile.getAbsolutePath(), body);

            generateFlash(scriptpath, inputFile.getAbsolutePath());

            result = readBytesFile(outputFile.getAbsolutePath());
            lru.put(url+query,result);
            saveDiskCache(filename,query,result);
            cleanup(tempFiles);
        } else {
            // log.debug("cache hit");
        }
        return result;
    }

    /**
     * This function cleans up the temporary files in the given vector
     */
    private void cleanup(Vector<File> tempFiles) {
        for (int i = 0; i < tempFiles.size(); i++) {
            File tf = tempFiles.get(i);
            log.debug("Deleting temporary file " + tf.getAbsolutePath());
            tf.delete();
       }
    }

    /**
     * Create a temporary file given the prefix and postfix
     * @param postfix
     * @param prefix
     **/
    private File createTemporaryFile(String prefix, String postfix) {
       File tempFile = null;
        try {
           tempFile = File.createTempFile(prefix, postfix, new File(generatortemppath));
       } catch (IOException e) {
           log.warn("Cannot create temporary file using File.createTempFile(), falling back to Las-Vegas method");
           while (tempFile == null || tempFile.exists()) {
               tempFile = new File(generatortemppath + prefix + (new Random()).nextLong() + postfix);
            }
       }
       return tempFile;
    }

    /**
     * This function will try to generate a new flash thingie, generated from a template.
     * the only thing which has to be specified is the XML, and the working direcotory.
     * This function was added, so that there is the possibility to use the generater
     * from a place without SCAN
     * @param    flashXML    a xml which contains the manipulations on the flash template
     * @param     workingdir  the path where there has to be searched for the template and the
     *                        other things, like pictures.(THIS LOOKS BELOW THE mmbase.htmlroot !!)
     * @return              a byte thingie, which contains the newly generated flash file
     */
    public byte[] getParsedFlash(String flashXML, String workingdir) {
        CharArrayReader reader=new CharArrayReader(flashXML.toCharArray());
        XMLDynamicFlashReader script=new XMLDynamicFlashReader(reader);
        String body="";

        // retrieve the template flash file path...
        String src=script.getSrcName();
        File inputFile;
        if (src.startsWith("/")) {
            inputFile = new File(htmlroot+src);
        }
        else {
            inputFile = new File(htmlroot+workingdir+src);
        }
        // get absolute path, and add it to our script..
        inputFile = inputFile.getAbsoluteFile();
        src = inputFile.getAbsolutePath();

        // is there a caching option set ?
        String caching=script.getCaching();
        if (caching!=null && (caching.equals("lru") || caching.equals("disk")) ) {
            // lru caching, always took here first... if we are caching on disk or on lru..
            byte[] bytes=(byte[])lru.get(src + flashXML);
            if (bytes!=null) {
                return(bytes);
            }

            // when we also have to check the disk..
            if(caching.equals("disk")) {
                // try to find on disk..
                bytes=loadDiskCache(src, flashXML);
                if (bytes!=null) {
                    // found on disk...
                    log.error("WOW from disk");
                    lru.put(src + flashXML, bytes);
                    return(bytes);
                }
            }
        }

        File outputFile = createTemporaryFile("export", ".swf");
        outputFile.delete();

        Vector<File> tempFiles = new Vector<File>();
        tempFiles.add(outputFile);

        // hey ho, generate our template..
        body+="INPUT \""+inputFile.getAbsolutePath()+"\"\n";
        body+="OUTPUT \""+outputFile.getAbsolutePath()+"\"\n";

        String scriptpath=src;
        scriptpath=scriptpath.substring(0,scriptpath.lastIndexOf('/')+1);

        body+=addDefines(script.getDefines(),scriptpath,tempFiles);
        body+=addReplaces(script.getReplaces(),scriptpath);

        // save the created input file for the generator
        File genInputFile = createTemporaryFile("input", ".sws");
        genInputFile.delete();
        tempFiles.add(genInputFile);
        saveFile(genInputFile.getAbsolutePath(), body);

        // lets generate the file
        generateFlash(scriptpath, genInputFile.getAbsolutePath());

        // retrieve the result of the genererator..
        byte[] bytes=readBytesFile(outputFile.getAbsolutePath());

        // store the flash in cache, when needed...
        if (caching!=null && (caching.equals("lru")|| caching.equals("disk")) ) {
            lru.put(src + flashXML, bytes);
            if(caching.equals("disk")) {
                    saveDiskCache(src, flashXML, bytes);
            }
        }
        cleanup(tempFiles);
        return(bytes);
    }

    /**
     * Generate text to add to the swift-genertor input file. This text specifies
     * how the flash should be manipulated. It allows replacements of colors,
     * fontsizes, etc.
     */
    private String addReplaces(Vector<Hashtable> replaces, String scriptpath) {
        String part="";
        for (Hashtable rep : replaces) {
            String type=(String)rep.get("type");
            if (type.equals("text")) {
                part+="SUBSTITUTE TEXT";
                String id=(String)rep.get("id");
                if (id!=null) part+=" "+id;
                part+=" {\n";
                String fonttype=(String)rep.get("fonttype");
                if (fonttype!=null) {
                    part+="\tFONT "+fonttype;
                    String fontsize=(String)rep.get("fontsize");
                    if (fontsize!=null) part+=" HEIGHT "+fontsize;
                    String fontkerning=(String)rep.get("fontkerning");
                    if (fontkerning!=null) part+=" KERNING "+fontkerning;
                    String fontcolor=(String)rep.get("fontcolor");
                    if (fontcolor!=null) part+=" COLOR "+fontcolor;
                    part+="\n";
                }
                String str=(String)rep.get("string");
                if (str!=null) {
                    str = replaceQuote(str);
                    part+="\tSTRING \""+str+"\"\n";
                }
                String strfile=(String)rep.get("stringfile");
                if (strfile!=null) {
                    if (!strfile.startsWith("/")) {
                        strfile=scriptpath+strfile;
                    }
                    strfile=htmlroot+strfile;
                    byte[] txt=readBytesFile(strfile);
                    if (txt!=null) {
                        String body=new String(txt);
                        body = replaceQuote(body);
                        part+="\tSTRING \""+body+"\"\n";
                    }
                }
                part+="}\n";
            } else if (type.equals("textfield")) {
                part+="SUBSTITUTE TEXTFIELD";
                String id=(String)rep.get("id");
                if (id!=null) part+=" "+id;
                part+=" {\n";
                String fonttype=(String)rep.get("fonttype");
                if (fonttype!=null) {
                    part+="\tFONT "+fonttype;
                    String fontsize=(String)rep.get("fontsize");
                    if (fontsize!=null) part+=" HEIGHT "+fontsize;
                    String fontkerning=(String)rep.get("fontkerning");
                    if (fontkerning!=null) part+=" KERNING "+fontkerning;
                    String fontcolor=(String)rep.get("fontcolor");
                    if (fontcolor!=null) part+=" COLOR "+fontcolor;
                    part+="\n";
                }
                String str=(String)rep.get("string");
                if (str!=null) {
                    str = replaceQuote(str);
                    part+="\tSTRING \""+str+"\"\n";
                }
                String strfile=(String)rep.get("stringfile");
                if (strfile!=null) {
                    if (!strfile.startsWith("/")) {
                        strfile=scriptpath+strfile;
                    }
                    strfile=htmlroot+strfile;
                    System.out.println(strfile);
                    byte[] txt=readBytesFile(strfile);
                    if (txt!=null) {
                        String body=new String(txt);
                        body = replaceQuote(body);
                        part+="\tSTRING \""+body+"\"\n";
                    }
                }
                part+="}\n";
            }
            part+="\n";
        }
        return(part);
    }

    /**
     * Add all defined media files (sound, images, etc.) to the text
     * that is used for the swift-generator. Images that come from inside
     * MMBase are saved to disk using temporary files that are deleted when
     * generation is finished
     * @param defines
     * @param scriptpath
     * @param tempFiles Vector where all the temporary files are put into.
     */
    private String addDefines(Vector<Hashtable> defines,String scriptpath,Vector<File> tempFiles) {
        String part="";
        for (Hashtable rep : defines) {
            String type=(String)rep.get("type");
            if (type.equals("image")) {
                String id=(String)rep.get("id");
                part+="DEFINE IMAGE \""+id+"\"";
                String width=(String)rep.get("width");
                String height=(String)rep.get("height");
                if (width!=null && height!=null) {
                    part+=" -size "+width+","+height;
                }
                String src=(String)rep.get("src");
                if (src!=null) {
                    // bad way to test for MMBase images!
                    if (src.startsWith("/img.db?")) {
                        String result=mapImage(src.substring(8),tempFiles);
                        part+=" \""+result+"\"";
                    } else if (src.startsWith("/")) {
                        part+=" \""+htmlroot+src+"\"";
                    } else {
                        part+=" \""+htmlroot+scriptpath+src+"\"";
                    }
                }
            } else if (type.equals("sound")) {
                String id=(String)rep.get("id");
                part+="DEFINE SOUND \""+id+"\"";
                String src=(String)rep.get("src");
                if (src!=null) {
                    if (src.startsWith("/")) {
                        part+=" \""+htmlroot+src+"\"";
                    } else {
                        System.out.println("REL="+htmlroot+scriptpath+src);
                        part+=" \""+htmlroot+scriptpath+src+"\"";
                    }
                }
            } else if (type.equals("variable")) {
                String var=(String)rep.get("id");
                String val=(String)rep.get("value");
                if (val==null) {
                    String strfile=(String)rep.get("valuefile");
                    if (strfile!=null) {
                        if (!strfile.startsWith("/")) {
                            strfile=scriptpath+strfile;
                        }
                        strfile=htmlroot+strfile;
                            byte[] txt=readBytesFile(strfile);
                        if (txt!=null) {
                            val=new String(txt);
                        }
                    }
                }
                part+="SET "+var+" \""+val+"\"\n";
            } else if (type.equals("speed")) {
                String val=(String)rep.get("value");
                part+="FLASH {\n";
                part+="\tFRAMERATE "+val+"\n";
                part+="}\n\n";
            }
            part+="\n";
        }
        return(part);
    }

    /**
    * Read a file from disk.
    *
    * @param filename
    * @return bytes from file
    */
    private byte[] readBytesFile(String filename) {
        File   bfile    = new File(filename);
        int    filesize = (int)bfile.length();
        byte[] buffer   = new byte[filesize];
        try {
            FileInputStream scan = new FileInputStream(bfile);
            int len = scan.read(buffer,0,filesize);
            scan.close();
        } catch(FileNotFoundException e) {
            log.error("Cannot find file: "+filename);
            return(null);
         } catch(IOException e) {
            log.error("Cannot read file: "+filename);
            return null;
        }
        return(buffer);
    }


    /**
    * Reads a file from disk.
    *
    * @param filename filename (+query) constructs the real filename
    * @param query query, is put after filename to construct real file on disk
    * @return bytes from file
    */
    private byte[] loadDiskCache(String filename,String query) {
        if(query!=null && !query.equals(""))
            filename = filename.substring(0,filename.length()-3) + "swf?" + query;
        else
            filename = filename.substring(0,filename.length()-3) + "swf";

        if (subdir!=null && !subdir.equals("")) {
            int pos=filename.lastIndexOf('/');
            filename=filename.substring(0,pos)+"/"+subdir+filename.substring(pos);
        }
        log.debug("load from disk: " + filename);
        return readBytesFile(filename);
    }


    private byte[] generateSwtDebug(String filename) {
        Process p=null;
        DataInputStream dip= null;
        String command="";
        try {
            command=generatorpath+generatorprogram+" -d "+filename;
            p = (Runtime.getRuntime()).exec(command);
        } catch (Exception e) {
            log.error("could not execute command:'"+command+"'");
        }
        log.service("Executed command: "+command+" succesfull, now gonna parse");
        dip = new DataInputStream(new BufferedInputStream(p.getInputStream()));
        byte[] result=new byte[32000];

        // look on the input stream
        try {
            int len3=0;
            int len2=0;

            len2=dip.read(result,0,result.length);
            if (len2==-1) {
                return(null);
            }
            while (len2!=-1 && len3!=-1) {
                len3=dip.read(result,len2,result.length-len2);
                if (len3==-1) {
                    break;
                } else {
                    len2+=len3;
                }
            }
            dip.close();
        } catch (Exception e) {
            log.error("could not parse output from '"+command+"'");
            e.printStackTrace();
            try {
                dip.close();
            } catch (Exception f) {}
        }
        return(result);
    }

    /**
     * Generate a flash file for a given input filename
     * @param scriptpath Unused parameter
     * @param inputfile File to generate flash for
     */
    private void generateFlash(String scriptpath, String inputfile) {
        Process p=null;
        DataInputStream dip= null;
        String command="";
        try {
            command=generatorpath+generatorprogram+" "+inputfile;
            p = (Runtime.getRuntime()).exec(command);
        } catch (Exception e) {
            log.error("could not execute command:'"+command+"'");
        }
        log.debug("Executed command: "+command+" succesfull, now gonna parse");
        dip = new DataInputStream(new BufferedInputStream(p.getInputStream()));
        byte[] result=new byte[1024];

        // look on the input stream
        try {
            int len3=0;
            int len2=0;

            len2=dip.read(result,0,result.length);
            while (len2!=-1) {
                len3=dip.read(result,len2,result.length-len2);
                if (len3==-1) {
                    break;
                } else {
                    len2+=len3;
                }
            }
            dip.close();
        } catch (Exception e) {
            log.error("could not parse output from '"+command+"'");
            e.printStackTrace();
            try {
                dip.close();
            } catch (Exception f) {}
        }
    }

    /**
     * Save a stringvalue to a file on the filesystem
     * @param filename File to save the stringvalue to
     * @param value Value to save to disk
     * @return Boolean indicating succes
     */
    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
            return true;
        } catch(Exception e) {
            log.error("Could not write values to file:" +filename+ " with value" + value);
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveDiskCache(String filename,String query,byte[] value) {
        if(query!=null && !query.equals(""))
            filename = filename.substring(0,filename.length()-3) + "swf?" + query;
        else
            filename = filename.substring(0,filename.length()-3) + "swf";

        if (subdir!=null && !subdir.equals("")) {
            int pos=filename.lastIndexOf('/');
            filename=filename.substring(0,pos)+File.separator+subdir+filename.substring(pos);
            // Create dir if it doesn't exist
            File d=new File(filename.substring(0,pos)+File.separator+subdir);
            if (!d.exists()) {
                d.mkdir();
            }
        }

        log.debug("save to disk: "+filename);
        //System.out.println("filename="+filename);

        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error("Could not write to disk cache, file:"+filename+" query:" + query);
            log.error(Logging.stackTrace(e));
        }
        return(true);
    }

    /**
     * Get an image from MMBase given a list of parameters, and save it to disk
     * @param imageline The image number with it's manipulations (eg: 34235+s(50))
     * @param tempFiles The vector to put temporary files in
     * @return The complete path to the image
     */
    private String mapImage(String imageline, Vector<File> tempFiles) {
        Images bul=(Images)mmb.getMMObject("images");
        Vector params=new Vector();
        if (bul!=null) {
            // rebuild the param
            log.debug("rebuilding param");
            String imageId = null;
            StringBuffer template = new StringBuffer();
            if (imageline != null) {
                StringTokenizer tok=new StringTokenizer(imageline,"+\n\r");
                // rico
                if(tok.hasMoreTokens()) {
                    imageId = tok.nextToken();
                    params.addElement(tok.nextToken());
                }
                while(tok.hasMoreTokens()) {
                    template.append(tok.nextToken());
                    if (tok.hasMoreTokens()) {
                        template.append("+");
                    }
                }

            }
            byte[] bytes = bul.getCachedNode(bul.getNode(imageId), template.toString()).getByteValue("handle");
            File tempFile = createTemporaryFile("image", ".jpg");
            saveFile(tempFile.getAbsolutePath(), bytes);
            tempFiles.add(tempFile);
            return tempFile.getAbsolutePath();
        } else {
            log.error("Cannot locate images builder, make sure you activated it!");
        }
        return "";
    }

    /**
     * Save a byte-array to disk
     * @param filename The name of the file to save the data to
     * @param value The byte-array containing the data for the file
     * @return Boolean indicating the success of the operation
     */
    static boolean saveFile(String filename,byte[] value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
            return true;
        } catch(Exception e) {
            log.error("Could not save to file:"+filename);
            log.error(Logging.stackTrace(e));
            return false;
        }
    }

    /**
     * Escape quotes in a string, because flash generator will fail otherwise
     * @param unquoted The string with quotes (") in it
     * @return The string where all quotes are escaped as \"
     */
    String replaceQuote(String unquoted) {
        StringObject so = new StringObject(unquoted);
        so.replace("\"", "\\\"");
        return so.toString();
    }
}
