/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.gui.flash;

import java.lang.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
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
 * @version $Id: MMFlash.java,v 1.13.2.2 2002-09-25 13:30:53 johannes Exp $
 */
public class MMFlash extends Module {

    static Logger log = Logging.getLoggerInstance(MMFlash.class.getName()); 

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

    /**
     * Initialize the module, check if all given parameters (in the XML file) are correct
     */
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

        // check if we may create a file on location of generatorTempPath
        File tempPath = new File(generatortemppath);
        if(!tempPath.isDirectory()) {
            log.error("Generator Temp Path was not a direcory('" + generatortemppath + "'), please edit mmflash.xml, or create directory");
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
            log.error("Could not execute command:'" + generatorpath + generatorprogram + "' for flash, please edit mmflash.xml");                    
        }
        log.debug("Module MMFlash started (flash-generator='" + generatorpath + generatorprogram + "' can be executed and tmpdir is checked)");                            
    }

    public void onload() {
    }

    public MMFlash() {
    }
 
    /**
     * Return the generated flash with debug information.<br>
     * @param sp the scanpage that includes the parameters needed to generate the flash
     * @return array of bytes containing the flash data
     */
    public byte[] getDebugSwt(scanpage sp) {
        String filename = htmlroot + sp.req.getRequestURI();
        byte[] bytes = generateSwtDebug(filename);
        return bytes;
    }

    /**
     * Return the generated flash. <br>
     * First, the XML file that does the transformation is being read into an XML reader. Then
     * all operations for the flash-generator are saved into a file 'input.sws'. Then the
     * swift-generator is executed on this file, generating a 'export.swf'. This file is read
     * into a byte array and returned as a result of this method.
     * @param sp the scanpage that includes the parameters needed to generate the flash
     * @return array of bytes containing the flash data
     */
    public byte[] getScanParsedFlash(scanpage sp) {
        // Get inputfile
        String url = sp.req.getRequestURI();
        String filename = htmlroot + url;
        byte[] inp = readBytesFile(filename);
        if (inp == null) {
            log.error( "No valid sxf file (" + filename + ") !" );        
            return(null);
        }
        sp.body = new String(inp);

        // ok, try to parse it
        if (scanp != null) {
            try {
                sp.body = scanp.handle_line(sp.body, sp.session, sp);
            } catch(Exception e) {}
        } else {
            log.error("MMFlash -> can't reach scanparser");
        }

        // now feed it to the xml reader
        CharArrayReader reader = new CharArrayReader(sp.body.toCharArray());
        XMLDynamicFlashReader script = new XMLDynamicFlashReader(reader);

	// Initialize a tempory file where the output will be written to
        File outputFile = createTemporaryFile("export", ".swf");
	outputFile.delete();
    
	Vector tempFiles = new Vector();
	tempFiles.add(outputFile);

        String body = "";
        String src = script.getSrcName();
        if (src.startsWith("/")) {
            body += "INPUT \"" + htmlroot + src + "\"\n";
        } else {
            String purl = url.substring(0, url.lastIndexOf('/') + 1);
            src = purl + src;
            body += "INPUT \"" + htmlroot + src + "\"\n";
        }
        body += "OUTPUT \"" + outputFile.getAbsolutePath() + "\"\n";

        // is there a caching option set ?
        String caching = script.getCaching();
        String query = sp.req.getQueryString();
        
        if (!sp.reload) {
            if (caching != null && caching.equals("lru")) {
                byte[] bytes = (byte[])lru.get(url + query);
                if (bytes != null) {
                    return(bytes);
                }
            } else if (caching != null && caching.equals("disk")) {
                byte[] bytes = (byte[])lru.get(url + query);
                if (bytes != null) {
                    log.info("WOW from disk+lru");
                    return(bytes);
                } else {
                    bytes = loadDiskCache(htmlroot + src, query);
                    if (bytes != null) {
                        log.info("WOW from disk");
                        lru.put(url + query, bytes);
                        return(bytes);
                    }
                }
            }
        }

        String scriptpath = src;
        scriptpath = scriptpath.substring(0, scriptpath.lastIndexOf('/')+1);

        body += addDefines(script.getDefines(), scriptpath, tempFiles);
        body += addReplaces(script.getReplaces(), scriptpath);

        // save the created input file for the generator
        File inputFile = createTemporaryFile("input", ".sws");
	inputFile.delete();

	tempFiles.add(inputFile);
        saveFile(inputFile.getAbsolutePath(), body);
	
        // lets generate the file
        generateFlash(scriptpath, inputFile.getAbsolutePath());
    
        byte[] bytes = readBytesFile(outputFile.getAbsolutePath());

        if (caching != null && caching.equals("lru")) {
            lru.put(url + query, bytes);
        } else if (caching != null && caching.equals("disk")) {
            saveDiskCache(htmlroot + src, query, bytes);
            lru.put(url + query, bytes);
        }    

	cleanup(tempFiles);
        
        return bytes;
    }

    /**
     * This function cleans up the temporary files in the given vector
     */
    private void cleanup(Vector tempFiles) {
        for (int i = 0; i < tempFiles.size(); i++) {
            File tf = (File)tempFiles.get(i);
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
     * from a place without SCAN . Note that this method is never called yet.
     *
     * @param    flashXML    a xml which contains the manipulations on the flash template
     * @param    workingdir  the path where there has to be searched for the template and the 
     *                       other things, like pictures.(THIS LOOKS BELOW THE mmbase.htmlroot !!)
     * @return               a byte thingie, which contains the new generated flash thingie
     */
    public byte[] getParsedFlash(String flashXML, String workingdir) {
        CharArrayReader reader = new CharArrayReader(flashXML.toCharArray());
        XMLDynamicFlashReader script = new XMLDynamicFlashReader(reader);

        // Initialize a tempory file where the output will be written to
	File outputFile = createTemporaryFile("export", ".swf");
	outputFile.delete();

	Vector tempFiles = new Vector();
	tempFiles.add(outputFile);
	
        String body = "";

        // retrieve the template flash file path...
        String src = script.getSrcName();        
        File inputFile;
        if (src.startsWith("/")) {
            inputFile = new File(htmlroot + src);
        } else {
            inputFile = new File(htmlroot + workingdir + src);        
        }    
        // get absolute path, and add it to our script..
        inputFile = inputFile.getAbsoluteFile();
        src = inputFile.getAbsolutePath();

        // is there a caching option set ?
        String caching = script.getCaching();
        if (caching != null && (caching.equals("lru") || caching.equals("disk")) ) {
            // lru caching, always took here first... if we are caching on disk or on lru..
            byte[] bytes= (byte[])lru.get(src + flashXML);
            if (bytes != null) {
                return(bytes);
            }

            // when we also have to check the disk..
            if(caching.equals("disk")) {
                // try to find on disk..
                bytes = loadDiskCache(src, flashXML);
                if (bytes != null) {
                    // found on disk...
                    log.error("WOW from disk");
                    lru.put(src + flashXML, bytes);
                    return(bytes);
                }        
            }
        } 

        // hey ho, generate our template..
        body += "INPUT \"" + inputFile.getAbsolutePath() + "\"\n";
        body += "OUTPUT \"" + outputFile.getAbsolutePath() + "\"\n";

        String scriptpath = src;
        scriptpath = scriptpath.substring(0, scriptpath.lastIndexOf('/')+1);

        body += addDefines(script.getDefines(), scriptpath, tempFiles);
        body += addReplaces(script.getReplaces(), scriptpath);

        // save the created input file for the generator
	File genInputFile = createTemporaryFile("input", ".sws");
	genInputFile.delete();

	tempFiles.add(genInputFile);

        saveFile(genInputFile.getAbsolutePath(), body); 

        // lets generate the file
        generateFlash(scriptpath, genInputFile.getAbsolutePath());

        // retrieve the result of the genererator..
        byte[] bytes = readBytesFile(outputFile.getAbsolutePath());

        // store the flash in cache, when needed...
        if (caching != null && (caching.equals("lru")|| caching.equals("disk")) ) {
            lru.put(src + flashXML, bytes);
            if(caching.equals("disk")) {
                saveDiskCache(src, flashXML, bytes);
            }
        }     
	
	cleanup(tempFiles);
        return bytes;
    }

    /**
     * Generate text to add to the swift-genertor input file. This text specifies
     * how the flash should be manipulated. It allows replacements of colors, 
     * fontsizes, etc.
     */
    private String addReplaces(Vector replaces, String scriptpath) {
        String part = "";
        for (Enumeration e = replaces.elements(); e.hasMoreElements();) {
            Hashtable rep = (Hashtable)e.nextElement();
            String type = (String)rep.get("type");
            if (type.equals("text")) {
                part += "SUBSTITUTE TEXT";
                String id = (String)rep.get("id");    
                if (id != null) part += " " + id;
                part += " {\n";
                String fonttype=(String)rep.get("fonttype");
                if (fonttype != null) {
                    part += "\tFONT " + fonttype;
                    String fontsize = (String)rep.get("fontsize");
                    if (fontsize != null) part += " HEIGHT " + fontsize;
                    String fontkerning = (String)rep.get("fontkerning");
                    if (fontkerning != null) part += " KERNING " + fontkerning;
                    String fontcolor = (String)rep.get("fontcolor");
                    if (fontcolor != null) part += " COLOR " + fontcolor;
                    part += "\n";
                } 
                String str = (String)rep.get("string");
                if (str != null) {
                    str = replaceQuote(str);
                    part += "\tSTRING \"" + str + "\"\n";
                }
                String strfile = (String)rep.get("stringfile");
                if (strfile != null) {
                    if (!strfile.startsWith("/")) {
                        strfile = scriptpath + strfile;
                    }    
                    strfile = htmlroot + strfile;
                    byte[] txt = readBytesFile(strfile);
                    if (txt != null) {
                        String body = new String(txt);
                        body = replaceQuote(body);
                        part += "\tSTRING \"" + body + "\"\n";
                    }
                }
                part += "}\n";
            } else if (type.equals("textfield")) {
                part += "SUBSTITUTE TEXTFIELD";
                String id = (String)rep.get("id");    
                if (id != null) part += " " + id;
                part += " {\n";
                String fonttype = (String)rep.get("fonttype");
                if (fonttype != null) {
                    part += "\tFONT "+fonttype;
                    String fontsize = (String)rep.get("fontsize");
                    if (fontsize != null) part += " HEIGHT " + fontsize;
                    String fontkerning=(String)rep.get("fontkerning");
                    if (fontkerning != null) part += " KERNING " + fontkerning;
                    String fontcolor=(String)rep.get("fontcolor");
                    if (fontcolor != null) part += " COLOR " + fontcolor;
                    part += "\n";
                } 
                String str = (String)rep.get("string");
                if (str != null) {
                    str = replaceQuote(str);
                    part += "\tSTRING \"" + str + "\"\n";
                }
                String strfile = (String)rep.get("stringfile");
                if (strfile != null) {
                    if (!strfile.startsWith("/")) {
                        strfile = scriptpath + strfile;
                    }    
                    strfile = htmlroot + strfile;
                    System.out.println(strfile);
                    byte[] txt = readBytesFile(strfile);
                    if (txt != null) {
                        String body = new String(txt);
                        body = replaceQuote(body);
                        part += "\tSTRING \"" + body + "\"\n";
                    }
                }
                part += "}\n";    
            }
            part += "\n";
        }
        return part;
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
    private String addDefines(Vector defines, String scriptpath, Vector tempFiles) {
        String part = "";
        int counter = 1;
        for (Enumeration e = defines.elements(); e.hasMoreElements();) {
            Hashtable rep = (Hashtable)e.nextElement();
            String type = (String)rep.get("type");
            if (type.equals("image")) {
                String id = (String)rep.get("id");    
                part += "DEFINE IMAGE \"" + id + "\"";
                String width = (String)rep.get("width");
                String height = (String)rep.get("height");
                if (width != null && height != null) {
                    part += " -size " + width + "," + height;
                }
                String src = (String)rep.get("src");
                if (src != null) {
                    if (src.startsWith("/img.db?")) {
                        String result = mapImage(src.substring(8), tempFiles);
                        part += " \"" + result + "\"";
                    } else if (src.startsWith("/")) {
                        part += " \"" + htmlroot + src + "\"";
                    } else {
                        part += " \"" + htmlroot + scriptpath + src + "\"";
                    }
                }
            } else if (type.equals("sound")) {
                String id = (String)rep.get("id");    
                part += "DEFINE SOUND \"" + id + "\"";
                String src = (String)rep.get("src");
                if (src != null) {
                    if (src.startsWith("/")) {
                        part += " \"" + htmlroot + src + "\"";
                    } else {
                        System.out.println("REL=" + htmlroot + scriptpath + src);
                        part += " \"" + htmlroot + scriptpath + src + "\"";
                    }
                }
            } else if (type.equals("variable")) {
                String var = (String)rep.get("id");
                String val = (String)rep.get("value");
                if (val == null) {
                    String strfile = (String)rep.get("valuefile");
                    if (strfile != null) {
                        if (!strfile.startsWith("/")) {
                            strfile = scriptpath + strfile;
                        }    
                        strfile = htmlroot + strfile;
                        byte[] txt = readBytesFile(strfile);
                        if (txt != null) {
                            val = new String(txt);
                        }
                    }
                }
                part += "SET " + var + " \"" + val + "\"\n";
            } else if (type.equals("speed")) {
                String val = (String)rep.get("value");
                part += "FLASH {\n";
                part += "\tFRAMERATE " + val + "\n";
                part += "}\n\n";
            }
            part += "\n";
        }
        return part;
    }

    /**
     * Read binary data from a file
     * @param filename File to read data from
     * @return bytearray containing the data
     */
    private byte[] readBytesFile(String filename) {
        File bfile = new File(filename);
        int filesize = (int)bfile.length();
        byte[] buffer = new byte[filesize];
        try {
            FileInputStream scan = new FileInputStream(bfile);
            int len = scan.read(buffer, 0, filesize);
            scan.close();
        } catch(FileNotFoundException e) {
            log.error("error getfile, not found : " + filename);
            return(null);
         } catch(IOException e) {
            log.error("error getfile, could not read : " + filename);        
            return null;
        }
        return(buffer);
    }


    /**
     * Load the flash data from disk for a given filename with it's query
     * @param filename Filename to read from disk
     * @param query Querystring for this filename
     * @return bytearray containing the flash data
     */
    private byte[] loadDiskCache(String filename, String query) {
        if (query != null) {
            filename = filename.substring(0, filename.length() - 3) + "swf?" + query;
        } else {
            filename = filename.substring(0, filename.length() - 3) + "swf";
        }

        if (subdir != null && !subdir.equals("")) {
            int pos = filename.lastIndexOf('/');
            filename = filename.substring(0, pos) + "/" + subdir + filename.substring(pos);
        }

        File bfile = new File(filename);
        int filesize = (int)bfile.length();
        byte[] buffer = new byte[filesize];
        try {
            FileInputStream scan = new FileInputStream(bfile);
            int len = scan.read(buffer, 0, filesize);
            scan.close();
        } catch(FileNotFoundException e) {
            log.error("error getfile, not found : " + filename);            
            return(null);
         } catch(IOException e) {
            log.error("error getfile, could not read : " + filename);        
            return(null);
        }
        return(buffer);
    }


    /**
    * Generate a flash file for a given input filename and return the data as a bytearray
    * @param filename File to generator flash for
    * @return byte-array containing the data
    */
    private byte[] generateSwtDebug(String filename) {
        Process p = null;
        String s = "",tmp = "";
        DataInputStream dip = null;
        DataInputStream diperror = null;
        String command = "";
        PrintStream out = null;    
        RandomAccessFile dos = null;    

        try {
            command = generatorpath + generatorprogram + " -d " + filename;
            p = (Runtime.getRuntime()).exec(command);
        } catch (Exception e) {
            log.error("could not execute command:'" + command + "'");                    
            s += e.toString();
            out.print(s);
        }
        log.info("Executed command: " + command + " succesfull, now gonna parse");                                    
        dip = new DataInputStream(new BufferedInputStream(p.getInputStream()));
        byte[] result = new byte[32000];

        // look on the input stream
        try {
            int len3 = 0;
            int len2 = 0;

            len2 = dip.read(result, 0, result.length);
            if (len2 == -1) {
                return(null);
            }
            while (len2 != -1 && len3 != -1) { 
                len3 = dip.read(result, len2, result.length - len2);
                if (len3 == -1) {
                    break;
                } else {
                    len2 += len3;
                }
            }
            dip.close();
        } catch (Exception e) {
            log.error("could not parse output from '" + command + "'");
            e.printStackTrace();
            try {
                dip.close();
            } catch (Exception f) {}
        }
        return result;
    }

    /**
     * Generate a flash file for a given input filename
     * @param scriptpath Unused parameter
     * @param inputfile File to generate flash for
     */
    private void generateFlash(String scriptpath, String inputfile) {    
        Process p = null;
        String s = "", tmp = "";
        DataInputStream dip = null;
        DataInputStream diperror = null;
        String command = "";
        PrintStream out = null;    
        RandomAccessFile dos = null;    

        try {
            command = generatorpath + generatorprogram + " " + inputfile;
            p = (Runtime.getRuntime()).exec(command);
        } catch (Exception e) {
            log.error("could not execute command:'" + command + "'");
            s += e.toString();
            out.print(s);
        }
        log.debug("Executed command: " + command + " succesfull, now gonna parse");                                    
        dip = new DataInputStream(new BufferedInputStream(p.getInputStream()));
        byte[] result = new byte[1024];

        // look on the input stream
        // WHY???
        try {
            int len3 = 0;
            int len2 = 0;

            len2 = dip.read(result, 0, result.length);
            while (len2 != -1) { 
                len3 = dip.read(result, len2, result.length - len2);
                if (len3 == -1) {
                    break;
                } else {
                    len2 += len3;
                }
            }
            dip.close();
        } catch (Exception e) {
            log.error("could not parse output from '" + command + "'");                    
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
    private boolean saveFile(String filename, String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
            return true;
        } catch(Exception e) {
            log.error("Could not write values to file:" + filename + " with value" + value);        
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save the binary data to the disk cache<br>
     * <b>TODO<b>: This assumes that the filename has a 3-character long extension, and that '?' characters are allowed in filenames
     * @param filename The filename to save the data to
     * @param value The binary data
     * @param query The querystring to add to the filename
     */
    private boolean saveDiskCache(String filename, String query, byte[] value) {
        if (query != null) {
            filename = filename.substring(0, filename.length() - 3) + "swf?" + query;
        } else {
            filename = filename.substring(0,filename.length() - 3) + "swf";
        }

        if (subdir != null && !subdir.equals("")) {
            int pos = filename.lastIndexOf('/');
            filename = filename.substring(0, pos) + File.separator + subdir + filename.substring(pos);
            // Create dir if it doesn't exist
            File d = new File(filename.substring(0, pos) + File.separator + subdir);
            if (!d.exists()) {
                d.mkdir();
            }
        }

        log.debug("filename=" + filename);        
        
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error("Could not write to disk cache, file:" + filename + " query:" + query);        
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
    private String mapImage(String imageline, Vector tempFiles) {
        Images bul = (Images)mmb.getMMObject("images");
        Vector params = new Vector();
        if (bul != null) {
            // rebuild the param
            log.debug("rebuilding param");                    
            StringTokenizer tok = new StringTokenizer(imageline, "+\n\r");
            while (tok.hasMoreTokens()) {
                params.addElement(tok.nextToken());
                scanpage sp = new scanpage();
                byte[] bytes = bul.getImageBytes(sp, params);
                File tempFile = createTemporaryFile("image", ".jpg");
                saveFile(tempFile.getAbsolutePath(), bytes);
		tempFiles.add(tempFile);
		return tempFile.getAbsolutePath();
            }
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
    private boolean saveFile(String filename, byte[] value) {
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
    private String replaceQuote(String unquoted) {
        StringObject so = new StringObject(unquoted);
        so.replace("\"", "\\\"");
        return so.toString();
    }
}
