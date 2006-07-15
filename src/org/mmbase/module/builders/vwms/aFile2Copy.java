/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.lang.String;

/**
 * An object that holds information needed for the transfer of a file.
 * This is used with the CAHE PAGE mechanism, which copies calculated pages to a mirror server.
 * File2Copy objects are handled by the {@link FileCopier} class.
 *
 * @rename AFile2Copy
  * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id: aFile2Copy.java,v 1.8 2003-03-10 11:50:27 pierre Exp $
 */

public class aFile2Copy {

    /**
     * Name of the destination user.
     * Needed to log in to the host (using ssh).
     */
    public String dstuser;
    /**
     * Name of the destination host
     */
    public String dsthost;
    /**
     * Requested path of the file at the destination host
     */
    public String dstpath;
    /**
     * Path of the file at this server
     */
    public String srcpath;
    /**
     * File name
     */
    public String filename;
    /**
     * Path to ssh command
     */
    public String sshpath;

    /**
     * Constructor for the File2Copy object
     * @param dstuser User name needed to log on at the destination host
     * @param dsthost Name of the destination host
     * @param dstpath Requested path of the file at the destination host
     * @param srcpath Path of the file at this server
     * @param filename File name
     * @param sshpath Path to ssh command
     */
    public aFile2Copy(String dstuser,String dsthost,String dstpath,String srcpath,String filename,String sshpath) {
      this.dstuser=dstuser;
      this.dsthost=dsthost;
      this.dstpath=dstpath;
      this.srcpath=srcpath;
      this.sshpath=sshpath;
      this.filename=filename;
    }

    /**
     * Returns a hashcode based on the orifinal file source path.
     * @return a hashcode identifying the file, as an <code>int</code>.
     */
    public int hashCode() {
      return((srcpath+filename).hashCode());
    }
}
