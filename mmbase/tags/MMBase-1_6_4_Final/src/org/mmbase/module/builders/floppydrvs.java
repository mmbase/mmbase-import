/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.module.ParseException;
import org.mmbase.module.core.*;
import org.mmbase.util.StringTagger;
import org.mmbase.util.Strip;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @javadoc
 * @rename Floppydrvs
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 * @version $Id: floppydrvs.java,v 1.9 2002-04-22 15:45:15 pierre Exp $
 */
public class floppydrvs extends MMObjectBuilder implements MMBaseObserver {

    // logging
    private static Logger log = Logging.getLoggerInstance(floppydrvs.class.getName());

    public floppydrvs() {
    }

    /**
     * @javadoc
     * Why does this method return null?
     */
    public String getGUIIndicator(MMObjectNode node) {
        return null;
    }

    /**
     * @javadoc
     * Why does this method return null?
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
        return null;
    }

    /**
     * @javadoc
     */
    public String getDefaultUrl(int src) {
        return null;
    }

    /**
     * @javadoc
     */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * @javadoc
     */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype);
    }

    /**
     * @javadoc
     */
    public boolean nodeChanged(String machine,String number,String builder,String ctype) {
        MMObjectNode node=getNode(number);
        if (node!=null) {
            if (log.isDebugEnabled()) {
                log.debug("floppydrvs node=" + node.toString());
            }
            // is if for me ?, enum related mmservers to check
            Enumeration e=mmb.getInsRel().getRelated(number,"mmservers");
            while  (e.hasMoreElements()) {
                MMObjectNode node2=(MMObjectNode)e.nextElement();
                String wantedServer=node2.getStringValue("name");
                if (wantedServer.equals(mmb.getMachineName())) {
                    doAction(node);
                }
            }
        }
        return true;
    }

    /**
     * Handles commands for floppydrvs found in this machine
     */
    private void doAction(MMObjectNode node) {
        String name=node.getStringValue("name");
        String cdtype=node.getStringValue("floppytype");
        String state=node.getStringValue("state");
        String info=node.getStringValue("info");

        if (!state.equals("busy")) {
            if (log.isDebugEnabled()) {
                log.debug("Action called on floppydrive : " + name);
            }
            // start a thread to handle command
            new floppydrvsProbe(this,node);
        } else {
            log.warn("Problem action called on floppydrive : " + name + " while it was busy");
        }
    }

    /**
     * @javadoc
     */
    public Vector getList(HttpServletRequest req, StringTagger tagger, StringTokenizer tok) throws ParseException {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("getdir")) return getHTMLDir(tagger,tok);
        }
        return null;
    }

    /**
     * getHTMLDir: This method transforms the listing so that it looks 'nice' inside a browser.
     */
    public Vector getHTMLDir(StringTagger tagger, StringTokenizer tok) {
        Vector result=new Vector();
        String id=tagger.Value("NODE");
        MMObjectNode node=getNode(id);	//Get floppynodenr determined by user via audiodevice? selector.
        if (node!=null) {
            String info=(String)getValue(node,"getdir(info)");  //Call getValue to retrieve list.
            StringTokenizer info_st = new StringTokenizer(info,"\n\r");
            while (info_st.hasMoreTokens()) {	//Create vector from dir listing.
                result.addElement(info_st.nextToken());
            }
        }
        tagger.setValue("ITEMS","1");
        return result;
    }

    /**
     * getDriveProps: This method retrieves the driveprops using the nodes' floppytype field.
     */
    private java.util.Properties getDriveProps(MMObjectNode fnode) throws DrivePropsNotFoundException{
        java.util.Properties driveprops = new java.util.Properties();
        String floppytype = fnode.getStringValue("floppytype");
        StringTagger tagger = new StringTagger(floppytype);
        String OS = tagger.Value("OS");
        String FS = tagger.Value("FS");
        String DEV = tagger.Value("DEV");

        if (OS.equals("linux") && FS.equals("dosfs")){	//have to be changed.
            driveprops.put("mountdir"  , DEV);
            driveprops.put("mountcmd"  , "/bin/mount");
            driveprops.put("unmountcmd", "/bin/umount");
            driveprops.put("dircmd"    , "/bin/ls -1LRp");
            driveprops.put("copycmd"   , "/bin/cp");

            // Note: Error strings could be removed from driveprops.
            driveprops.put("mounterr"  , "error opening device (Device not configured)");
            driveprops.put("unmounterr", "Unknown volume");
            driveprops.put("direrr"    , "No volume is current; use `hmount' or `hvol'");

            driveprops.put("direrr2"   , "No such file or directory"); 	//endsWith

            driveprops.put("copyerr"   , "cp: Insufficient arguments");	//startsWith
            driveprops.put("copyerr2"  , "Permission denied");		//endsWith
            driveprops.put("copyerr3"  , "cp: cannot access");		//endsWith
            driveprops.put("copyerr4"  , "No such file or directory");	//endsWith
            driveprops.put("copyerr5"  , "Not a directory");		//endsWith
            driveprops.put("copyerr6"  , "not found");			//endsWith
        } else {
            String jname = fnode.getStringValue("name");
            log.error("fnode:"+jname+" has unknown floppytype OS="+OS+" , FS="+FS);
            String errval = "Floppynode:"+jname+" has unknown floppytype OS="+OS+" , FS="+FS;
            String explanation =  "User hasn't specified floppytype field correct.";
            throw new DrivePropsNotFoundException(errval,explanation);
        }
        return driveprops;
    }

    /**
     * getDir: Returns a string containing all the filepaths from the jazzdisk.
     *         eg. /FLOPPY-1/data/songs/primus/hitme.wav
     *             /FLOPPY-1/data/jingles/welcome.wav
     *  Note 26 jan 1999: See note in same method in jazzdrives.java
     */
    public String getDir(MMObjectNode fnode) throws GetDirFailedException{
        Vector entries = new Vector();
        Runtime rt = null;
        java.util.Properties driveprops= null;

        if (log.isDebugEnabled()) {
            log.debug("Executing getDir method.");
        }
        driveprops = new java.util.Properties();//Create properties object.
        try {
            driveprops = getDriveProps(fnode);
        } catch (DrivePropsNotFoundException dnfe) {
            String Exc = "DrivePropsNotFoundException -> ";
            log.error(Exc+dnfe.errval+"  "+dnfe.explanation);
            String error_found=Exc+dnfe.errval;
            String explanation=dnfe.explanation;
            throw new GetDirFailedException(error_found,explanation);
        }

        String mountdir   = driveprops.getProperty("mountdir");
        String mountcmd   = driveprops.getProperty("mountcmd");
        String unmountcmd = driveprops.getProperty("unmountcmd");
        String dircmd     = driveprops.getProperty("dircmd");
        String mounterr   = driveprops.getProperty("mounterr");
        String unmounterr = driveprops.getProperty("unmounterr");
        String direrr	  = driveprops.getProperty("direrr");
        try {
            rt = Runtime.getRuntime();
            try {
                doMount(mountcmd,mountdir,mounterr,rt);
                entries = doDir(dircmd,direrr,rt);
                doUnmount(unmountcmd,mountdir,unmounterr,rt);
            } catch (CmdFailedException cmdfe) {
                String Exc = "CmdFailedException -> ";
                log.error(Exc+cmdfe.errval+"  "+cmdfe.explanation);
                String error_found=Exc+cmdfe.errval;
                String explanation=cmdfe.explanation;
                throw new GetDirFailedException(error_found,explanation);
            }
            //log.debug("Entries: "+entries.size());
            entries = alterEntries(entries,"remove whitespace","","");	// Remove whitespace
            entries = alterEntries(entries,"remove empty entries","","");	// Revove empty entries
            entries = alterEntries(entries,"remove ending on","/","");	// Remove ending on
            entries = alterEntries(entries,"add paths","","");		// Add paths
            //log.debug("Entries: "+entries.size());

        } catch (IOException ioe) {
            String Exc = "IOException ->";
            log.error(Exc+ioe);
            String error_found=Exc+ioe;
            String explanation="Something went wrong during IO on floppydisk";
            throw new GetDirFailedException(error_found,explanation);
        }
        String list= new String();
        String drivename = fnode.getStringValue("name");
        for (Enumeration e = entries.elements(); e.hasMoreElements();){
            list+= "/"+drivename+(String)e.nextElement();
        }
        //log.debug("Final list contains:"+list);
        return list;
    }

    /**
     * copy : This method will copy the selected floppyfile (srcfile) to the destination (dstfile).
     * 	  Only Files WITHOUT Spaces In Filename OR Directory Can be Copied.
     * 	  srcfile eg.	= /FLOPPY-1/audio/file.wav
     *  	  dstfile eg.	= /data/audio/wav/123456.wav
     *
     *  Note 26 jan 1999: See note in same method in jazzdrives.java
     */
    public void copy(String srcfile,String dstfile) throws CopyFailedException{
        Runtime rt = null;
        String drivename = null;
        String srcfilepath = null,dstfilepath = null;
        java.util.Properties driveprops= null;

        if (log.isDebugEnabled()) {
            log.debug("Executing Copy method.");
            log.debug("srcfile -> "+srcfile);
            log.debug("dstfile -> "+dstfile);
        }

        if (dstfile==null){
            log.error("Dstfile:"+dstfile+" EMPTY.");
            String error_found="Dstfile:"+dstfile+" EMPTY.";
            String explanation="Hmmmmm!!!?!?! no idea.";
            throw new CopyFailedException(error_found,explanation);
        }
        dstfilepath = dstfile;	//Destination file can be used immediately.

        String delim = "/";
        StringTokenizer tok = new StringTokenizer(srcfile,delim);
        if (tok.hasMoreTokens()) {
            drivename = tok.nextToken();	//Retrieve drivename
        } else {
            log.error("Srcfile:"+srcfile+" INVALID.");
            log.error("Srcfile is empty OR doesn't contain any "+delim+" delimiters.");
            String error_found="Srcfile:"+srcfile+" INVALID.";
            String explanation="Srcfile is empty OR doesn't contain any "+delim+" delimiters.";
            throw new CopyFailedException(error_found,explanation);
        }
        try {
            MMObjectNode fnode = null;
            floppydrvs bul=(floppydrvs)mmb.getMMObject("floppydrvs");
            Enumeration e=bul.search("WHERE name='"+drivename+"'");
            if (e.hasMoreElements()) {
                fnode=(MMObjectNode)e.nextElement();
            }
            driveprops = getDriveProps(fnode);
        } catch (DrivePropsNotFoundException dnfe) {
            String Exc = "DrivePropsNotFoundException -> ";
            log.error(Exc+dnfe.errval+"  "+dnfe.explanation);
            String error_found=Exc+dnfe.errval;
            String explanation=dnfe.explanation;
            throw new CopyFailedException(error_found,explanation);
        }

        String mountdir   = driveprops.getProperty("mountdir");
        String mountcmd   = driveprops.getProperty("mountcmd");
        String copycmd	  = driveprops.getProperty("copycmd");
        String unmountcmd = driveprops.getProperty("unmountcmd");
        String mounterr   = driveprops.getProperty("mounterr");
        String unmounterr = driveprops.getProperty("unmounterr");
        String copyerr	  = driveprops.getProperty("copyerr");
        String copyerr2   = driveprops.getProperty("copyerr2");

        int startPos= delim.length() + drivename.length();
        srcfilepath = srcfile.substring(startPos);	//Only use part following the drivename.
        //log.debug("Cutting of drivename    srcfilepath -> "+srcfilepath);

        //log.debug("srcfilepath -> "+srcfilepath);
        //log.debug("dstfilepath -> "+dstfilepath);
        try {
            rt = Runtime.getRuntime();
            try {
                doMount(mountcmd,mountdir,mounterr,rt);
                doCopy(copycmd,copyerr,copyerr2,srcfilepath,dstfilepath,rt);
                doUnmount(unmountcmd,mountdir,unmounterr,rt);
            } catch (CmdFailedException cmdfe) {
                String Exc = "CmdFailedException -> ";
                log.error(Exc+cmdfe.errval+"  "+cmdfe.explanation);
                String error_found=Exc+cmdfe.errval;
                String explanation=cmdfe.explanation;
                throw new CopyFailedException(error_found,explanation);
            }
        } catch (IOException ioe) {
            String Exc = "IOException ->";
            log.error(ioe);
            String error_found=Exc+ioe;
            String explanation="Something went wrong during IO on floppydisk";
            throw new CopyFailedException(error_found,explanation);
        }
    }

    /**
     * findError: This method compares the 'errval with 'known_error'
     * 	      retval 	     = 'known_error' if 2 args are equal
     *	      else retval    = 'unknown_error'
     */
    private String findError(String errval,String cmd,String known_error){
        String stripped = null;
        String unknown_error = "Unknown error -->";

        if (log.isDebugEnabled()) {
            log.debug("Finding error.");
        }
        stripped = Strip.Whitespace(errval,errval.length()-3);	//Go back 3 chars and strip whitespace.
        if (stripped.endsWith(known_error)){	//If the error value returned is known to me.
            log.debug("I know this error.");
        } else {
            log.debug("Unknown error.");
            stripped = unknown_error + stripped;
        }
        return stripped;
    }

    /**
     * doMount: This method performs the mount command.
     *
     *  Note 26 jan 1999: See note in same method in jazzdrives.java
     */
    private void doMount(String mountcmd,String mountdir,String mounterr,Runtime rt) throws CmdFailedException,IOException{
        String volname = null;
        String retval = null, errval = null;
        String error_found=null, explanation=null;
        String unknown_error = "Unknown error -->";

        log.service("Now executing "+mountcmd+" "+mountdir);
        //Process p = rt.exec(mountcmd+" "+mountdir);    //hmount /dev/sda
        Process p = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        while ((retval = in.readLine())  != null){
            //log.debug("retval = "+retval);
            if (retval.startsWith("Volume name is")){
                StringTokenizer tok = new StringTokenizer(retval,"\"");
                if (tok.hasMoreTokens()){
                    volname = tok.nextToken();
                    volname = tok.nextToken();	//volname now contains volumename.
                    //log.debug("Volumename retrieved: "+volname);
                }
            }
        }
        //error handling
        if ((errval = err.readLine()) != null) {  //Get data from stderr.
            log.error(errval);
            throw new CmdFailedException(errval,explanation);
        }
    }

    /**
     * doUnMount: This method performs the unmount command.
     *  Note 26 jan 1999: See note in same method in jazzdrives.java
     */
    private void doUnmount(String unmountcmd,String mountdir,String unmounterr,Runtime rt) throws CmdFailedException,IOException{
        String retval=null, errval = null;
        String error_found=null, explanation = null;
        String unknown_error = "Unknown error -->";

        log.debug("Now executing "+unmountcmd+" "+mountdir);
        //Process p = rt.exec(unmountcmd+" "+mountdir);  //humount /dev/sda

        Process p = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        while ((retval = in.readLine())  != null){
            log.debug(retval);
        }
        //error handling
        if ((errval = err.readLine()) != null) {  //Get data from stderr.
            log.error(errval);
            throw new CmdFailedException(errval,explanation);
        }
    }

    /**
     * doDir: This method performs the dir command and returns a vector with filepaths.
     *
     *  Note 26 jan 1999: See note in same method in jazzdrives.java
     */
    private Vector doDir(String dircmd,String direrr,Runtime rt) throws CmdFailedException,IOException{
        Vector entries = new Vector();
        String entry=null, errval = null;
        String error_found=null, explanation = null;
        String unknown_error = "Unknown error -->";

        if (log.isDebugEnabled()) {
            log.debug("Now executing "+dircmd);
        }
        Process p = rt.exec(dircmd);      //eg ls -1pR
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        //Add all entries to a vector
        if (log.isDebugEnabled()) {
            log.debug("Adding all entries to a vector.");
        }
        while ((entry = in.readLine()) != null){
            //log.debug("entry = "+entry);
            entries.addElement(entry);
        }

        //error handling
        if ((errval = err.readLine()) != null) {  //Get data from stderr.
            log.error("errval = "+errval);
            throw new CmdFailedException(errval,explanation);
        }
        return entries;
    }

    /**
     * doCopy: This method performs the actual copying, by using a copycommand outside java (copy).
     * 	    Only Files WITHOUT Spaces In Filename OR Directory Can be Copied.
     * 	    The srcfilepath = :audio:jingles:boing.wav
     * 	    The dstfilepath = /data/import/123456.wav
     *
     *  Note 26 jan 1999: See note in same method in jazzdrives.java
     */
    private void doCopy(String copycmd,String copyerr,String copyerr2,String srcfilepath,String dstfilepath,Runtime rt) throws CmdFailedException,IOException{
        String retval=null, errval=null;
        String error_found = null, explanation=null;
        String unknown_error = "Unknown error -->";
        boolean error_occured = false;

        if (log.isDebugEnabled()) {
            log.debug(":TEST: Now executing "+copycmd+" :davzevtest.wav /tmp/davzev2");
        }
        Process p = rt.exec(copycmd+" "+"/tmp/davzev.wav"+" "+"/tmp/davzev.wav.copy"); //Used for testing

        //log.debug("Now executing "+copycmd+" "+srcfilepath+" "+dstfilepath);
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        while ((retval = in.readLine()) != null) {	//Get data from stdin.
            log.debug(retval);
        }
        //error handling
        if ((errval = err.readLine()) != null) {  //Get data from stderr.
            log.error(errval);
            throw new CmdFailedException(errval,explanation);
        }
        if (log.isDebugEnabled()) {
            log.debug("Copy done.");
        }
    }

    /**
     * alterEntries: This method contains several code to alter a vector with string entries.
     */
    public Vector alterEntries(Vector entries,String cmd,String param1,String param2){
        Vector altered = new Vector();

        if (cmd.equals("remove whitespace")){
            //log.debug("Removing whitespace from entries.");
            for (Enumeration e = entries.elements();e.hasMoreElements();){
                String s = (String) e.nextElement();
                altered.addElement(Strip.Whitespace(s,0));
            }
        } else if (cmd.equals("remove starting with")){
            //log.debug("Removing entries starting with a "+param1);
            for (Enumeration e = entries.elements(); e.hasMoreElements();){
                String s = (String)e.nextElement();
                if (!s.startsWith(param1)){	//Only add file entries.
                    altered.addElement(s);
                } else {
                    //log.debug("Removing element "+s);
                }
                        }
        } else if (cmd.equals("remove ending on")){
            //log.debug("Removing entries ending on a "+param1);
            for (Enumeration e = entries.elements(); e.hasMoreElements();){
                String s = (String)e.nextElement();
                if (!s.endsWith(param1)){	//Only add file entries.
                    altered.addElement(s);
                } else {
                    //log.debug("Removing element "+s);
                }
            }
        } else if (cmd.equals("remove empty entries")){
            //log.debug("Removing empty entries.");
            for (Enumeration e = entries.elements();e.hasMoreElements();){
                String s = (String) e.nextElement();
                if (s!=null){
                    altered.addElement(s);
                } else {
                    //log.debug("Removing empty entry.");
                }
            }
        } else if (cmd.equals("keep token")){
            //log.debug("Keeping "+param1+" token only.");
            if (param1.equals("last")){
                for (Enumeration e = entries.elements();e.hasMoreElements();){
                    String s = (String) e.nextElement();
                    String token = null;
                    StringTokenizer entry_st = new StringTokenizer(s," ");

                    while (entry_st.hasMoreTokens()){
                        token= entry_st.nextToken();
                        if (token.startsWith(":")){
                            while (entry_st.hasMoreTokens()){
                                token+= " "+entry_st.nextToken();
                            }
                            break;
                        } else if (token.startsWith("f")) {
                            int tokens = 7;
                            int i=0;
                            while (i<tokens && entry_st.hasMoreTokens()) { //Go to the right token.
                                token = entry_st.nextToken();
                                //log.debug("i:"+i+" token: "+token);
                                i++;
                            }
                            while (entry_st.hasMoreTokens()) { //Needed if filename contains spaces.
                                token+= " "+entry_st.nextToken();
                            }
                            break;
                        }
                    }
                    //log.debug("Adding: "+token);
                    altered.addElement(token);
                }
            }
        } else if (cmd.equals("add paths")) {
           //log.debug("Adding paths to entries.");
            String path = null;
            for (Enumeration e = entries.elements();e.hasMoreElements();) {
                String s = (String) e.nextElement();
                if(s.startsWith("./")){
                    path = new String(s);
                    path = path.substring(1,path.length())+"/";
                } else if(s.startsWith(".:")) {
                    path = "/";
                } else {
                    StringBuffer sb = new StringBuffer(s);
                    sb.insert(0,path);	//insert /bla/ to a.wav
                    sb.append("\n\r");	//Only used for layout while testing.
                    altered.addElement(new String(sb));
                }
            }
        } else if (cmd.equals("replace")) {
            //log.debug("Replacing all "+param1+" with "+param2);
            for (Enumeration e = entries.elements();e.hasMoreElements();) {
                            String s = (String) e.nextElement();
                                altered.addElement(s.replace(param1.charAt(0),param2.charAt(0)));
                        }
        }
        return altered;
    }

    /**
     * @javadoc
     * @todo getdir should use ExecuteFunction
     */
    public Object getValue(MMObjectNode node,String field) {
        if (field.equals("getdir(info)")) {
            // send the command to get the dir
            node.setValue("state","getdir");
            node.commit();
            boolean changed=false;
            MMObjectNode newnode=null;
            while (!changed) {
                waitUntilNodeChanged(node);
                newnode=getNode(node.getIntValue("number"));
                String state=newnode.getStringValue("state");
                if (log.isDebugEnabled()) {
                    log.debug("WAIT RESULT="+state);
                }
                if (state.equals("waiting") || state.equals("error")) changed=true;
            }
            String val=newnode.getStringValue("info");
            if (log.isDebugEnabled()) {
                log.debug("val="+val);
            }
            return val;
        } else {
            return super.getValue( node, field );
        }
    }
}
