/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Performs a transfer of a file from the current server to a host.
 * Uses invocation of ssh on the commandline to achieve its ends.
 * Since no password is used, the client machine (this server) has to be
 * configured to gain access (i.e. through the use of public keys).
 * Also note that the client needs execute rights on ssh.<br />
 * A system that directlky acecsses ssh, and does not make use of {@link Execute}
 * is being developed.
 *
 * @author Rico Jansen
 * @author Pierre van Rooden (javadocs)
 * @version $Id: SCPcopy.java,v 1.7 2007-06-21 15:50:23 nklasens Exp $
 */
public class SCPcopy {

    /**
     * Program runner for executing the 'ssh' command.
     */
    Execute exec=new Execute();
    /**
     * Name of the current server
     */
    String thisserver;
    /**
     * Root-path of the file at the destination host
     */
    String dstpath;
    /**
     * Name of the destination user.
     * Needed to log in to the host (using ssh).
     */
    String dstuser;
    /**
     * Name of the destination host
     */
    String dsthost;
    /**
     * Path to ssh command
     */
    String sshpath;

    // Logger class
    private static Logger log = Logging.getLoggerInstance(SCPcopy.class.getName());

    /**
     * SCPCopy Constructor.
     * @deprecated vpro-specific
     */
    public SCPcopy() {
        setSSHpath("/usr/local/bin");
        setUser("vpro");
        setHost("vpro.omroep.nl");
        setPath("/bigdisk/htdocs");
    }

    /**
     * SCPCopy Constructor.
     * @param sshpath Path to ssh command
     * @deprecated vpro-specific
     */
    public SCPcopy(String sshpath) {
        setSSHpath(sshpath);
        setUser("vpro");
        setHost("vpro.omroep.nl");
        setPath("/bigdisk/htdocs");
    }

    /**
     * SCPCopy Constructor.
     * @param sshpath Path to ssh command
     * @param user User name needed to log on at the destination host
     * @deprecated vpro-specific
     */
    public SCPcopy(String sshpath,String user) {
        setSSHpath(sshpath);
        setUser(user);
        setHost("vpro.omroep.nl");
        setPath("/bigdisk/htdocs");
    }

    /**
     * SCPCopy Constructor.
     * @param sshpath Path to ssh command
     * @param user User name needed to log on at the destination host
     * @param host Name of the destination host
     * @deprecated vpro-specific
     */
    public SCPcopy(String sshpath,String user,String host) {
        setSSHpath(sshpath);
        setUser(user);
        setHost(host);
        setPath("/bigdisk/htdocs");
    }

    /**
     * SCPCopy Constructor.
     * @param sshpath Path to ssh command
     * @param user User name needed to log on at the destination host
     * @param host Name of the destination host
     * @param path Root path of the file at the destination host
     */
    public SCPcopy(String sshpath,String user,String host,String path) {
        setSSHpath(sshpath);
        setUser(user);
        setHost(host);
        setPath(path);
    }

    /**
     * Transfers a file from this server to a host.
     * Creates directories if needed.
     * @param base Root path of the file on this server
     * @param src The actual file to transfer (includes path info)
     * @param dst Name of the destination host
     */
    public void copy(String base,String src,String dst) {
        dsthost=dst;
        copy(base,src);
    }

    /**
     * Transfers a file from this server to the host.
     * Creates directories if needed.
     * @param base Root path of the file on this server
     * @param src The actual file to transfer (includes path info)
     */
    public void copy(String base,String src) {
        int last;
        String path;

        last=src.lastIndexOf('/');

        // creates the directories
        if (last!=-1) {
            path=dstpath+src.substring(0,last);
            // note: mkdirs returns success or failure, but this is not checked
            mkdirs(path);
        }
        // determine full file path
        path=dstpath+src;
        realcopy(base+src,path+".tmp");
        rename(path+".tmp",path);
    }

    /**
     * Creates a directory at the host.
     * @param path directory to create
     * @return <code>true</code> if successful
     */
    public boolean mkdir(String path) {
        String res;
        res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mkdir "+path+"");
        log.debug("SCPcopy -> mkdir "+path+" : "+res);
        return res.length()<=0;
    }

    /**
     * Creates a full directory path at the host.
     * @param path path to create
     * @return <code>true</code> if successful
     */
    public boolean mkdirs(String path) {
        String res;
        res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mkdir -p "+path+"");
        log.debug("SCPcopy -> mkdirs "+path+" : "+res);
        return res.length()<=0;
    }

    /**
     * Renames a file at the host.
     * @param src Original file name
     * @param dst New file name
     * @return <code>true</code> if successful
     */
    public boolean rename(String src,String dst) {
        String res;
        int pos=src.indexOf("("); // strange check. and how about spaces?
        if (pos==-1) {
            res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mv "+src+" "+dst+"");
        } else {
            res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mv \""+src+"\" \""+dst+"\"");
        }

        log.debug("SCPcopy -> rename "+src+"->"+dst+" : "+res);
        return res.length()<=0;
    }

    /**
     * Transfers a file at this server to the host.
     * Directories should already exist.
     * @param src file path at this server
     * @param dst new file path at the host
     * @return <code>true</code> if successful
     */
    public boolean realcopy(String src,String dst) {
        String res;
        int pos=src.indexOf("("); // strange check. and how about spaces?
        if (pos==-1) {
            res=exec.execute(sshpath+"/scp -B -A -q "+src+" "+dstuser+"@"+dsthost+":"+dst);
        } else {
            res=exec.execute(sshpath+"/scp -B -A -q "+src+" "+dstuser+"@"+dsthost+":\""+dst+"\"");
        }

        log.service("SCPcopy -> copy "+src+"->"+dst+" : "+res);
        return res.length()<=0;
    }

    /**
     * Removes a file at the host.
     * @param path path of the file to remove
     * @return <code>true</code> if successful
     */
    public boolean remove(String path) {
        String res;
        res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" rm -f "+path);
        log.debug("SCPcopy -> remove "+path+" : "+res);
        return res.length()<=0;
    }

    /**
     * Removes a directory at the host.
     * @param path path of the directory to remove
     * @return <code>true</code> if successful
     */
    public boolean removedir(String path) {
        String res;
        res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" rmdir "+path);
        log.debug("SCPcopy -> removedir "+path+" : "+res);
        return res.length()<=0;
    }

    /**
     * Get path to ssh command
     */
    public String getSSHpath() {
        return(sshpath);
    }

    /**
     * Get root-path of the file at the destination host
     */
    public String getPath() {
        return(dstpath);
    }

    /**
     * Get name of the destination host
     */
    public String getHost() {
        return(dsthost);
    }

    /**
     * Get name of the destination user.
     */
    public String getUser() {
        return(dstuser);
    }



    /**
     * Set path to ssh command
     */
    public void setSSHpath(String path) {
        sshpath=path;
    }

    /**
     * Set root-path of the file at the destination host
     */
    public void setPath(String path) {
        dstpath=path;
    }

    /**
     * Set name of the destination host
     */
    public void setHost(String host) {
        dsthost=host;
    }

    /**
     * Set name of the destination user.
     */
    public void setUser(String user) {
        dstuser=user;
    }

    /**
     * Entry for direct invocation from the commandline.
     * Usage:<br />
     * java SCPCopy [basedir],[filepath],[destinationhost]<br />
     * @param args The commandline arguments
     * @deprecated VPRO-specific
     */
    public static void main(String args[]) {
        SCPcopy scp=new SCPcopy();
        scp.copy(args[0],args[1],args[2]);
        System.exit(0);
    }
}
