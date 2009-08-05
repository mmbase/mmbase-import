/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class UnixCopy
 * 
 * @javadoc
 */

public class UnixCopy {

    private static Logger log = Logging.getLoggerInstance(UnixCopy.class.getName()); 
    Execute exec=new Execute();
    String thisserver;
    String dstpath,dstuser,dsthost;
    String binpath;

	public UnixCopy() {
		setBinpath("/usr/local/bin");
		setUser("wwwtech");
		setHost("www.mmbase.org");
		setPath("/tmp/htdocs");
	}


	public UnixCopy(String binpath) {
		setBinpath(binpath);
		setUser("wwwtech");
		setHost("www.mmbase.org");
		setPath("/tmp/htdocs");
	}

	public UnixCopy(String binpath,String user) {
		setBinpath(binpath);
		setUser(user);
		setHost("www.mmbase.org");
		setPath("/tmp/htdocs");
	}

	public UnixCopy(String binpath,String user,String host) {
		setBinpath(binpath);
		setUser(user);
		setHost(host);
		setPath("/tmp/htdocs");
	}

	public UnixCopy(String binpath,String user,String host,String path) {
		setBinpath(binpath);
		setUser(user);
		setHost(host);
		setPath(path);
	}

	public void copy(String base,String src,String dst) {
		dsthost=dst;
		copy(base,src);		
	}

	public void copy(String base,String src) {
		int last;
		String path;

		last=src.lastIndexOf('/');
		if (last!=-1) {
			path=dstpath+src.substring(0,last);
			mkdirs(path);
		}
		path=dstpath+src;
		realcopy(base+src,path+".tmp");
		rename(path+".tmp",path);
	}


	public boolean mkdir(String path) {
		boolean rtn=true;;
		String res;

		res=exec.execute(binpath+"/mkdir "+path+"");
		if (log.isDebugEnabled()) {
                    log.debug("mkdir " + path + " : " + res);
                }

		rtn=res.length()<=0;
		return(rtn);
	}

	public boolean mkdirs(String path) {
		String res;
		boolean rtn=true;

		res=exec.execute(binpath+"/mkdir -p "+path+"");
		if (log.isDebugEnabled()) {
            log.debug("mkdirs " + path + " : " + res);
        }
		rtn=res.length()<=0;
		return(rtn);
	}

	public boolean rename(String src,String dst) {
		boolean rtn=true;
		String res;

		int pos=src.indexOf("(");
		if (pos==-1) {
			res=exec.execute(binpath+"/mv "+src+" "+dst+"");
		} else {
			res=exec.execute(binpath+"/mv \""+src+"\" \""+dst+"\"");
		}

		if (log.isDebugEnabled()) {
            log.debug("rename " + src + "->" + dst + " : " + res);
        }
		rtn=res.length()<=0;
		return(rtn);
	}

	public boolean realcopy(String src,String dst) {
		String res;
		boolean rtn=true;
		res=exec.execute(binpath+"/cp "+src+" "+dst);
		if (log.isDebugEnabled()) {
            log.debug("copy " + src + "->" + dst + " : " + res);
        }
		rtn=res.length()<=0;
		return (rtn);
	}

	public boolean remove(String path) {
		String res;
		boolean rtn=true;

		res=exec.execute(binpath+"/rm -f "+path);

		if (log.isDebugEnabled()) {
            log.debug("remove " + path + " : " + res);
        }
		rtn=res.length()<=0;
		return (rtn);
	}

	public boolean removedir(String path) {
		String res;
		boolean rtn=true;

		res=exec.execute(binpath+"/rmdir "+path);

		if (log.isDebugEnabled()) {
            log.debug("removedir " + path + " : " + res);
        }
		rtn=res.length()<=0;
		return (rtn);
	}


	
	public String getBinpath() {
		return(binpath);
	}

	public String getPath() {
		return(dstpath);
	}

	public String getHost() {
		return(dsthost);
	}

	public String getUser() {
		return(dstuser);
	}


	
	public void setBinpath(String path) {
		binpath=path;		
	}

	public void setPath(String path) {
		dstpath=path;
	}

	public void setHost(String host) {
		dsthost=host;
	}

	public void setUser(String user) {
		dstuser=user;
	}


	public static void main(String args[]) {
		SCPcopy scp=new SCPcopy();
		scp.copy(args[0],args[1],args[2]);
		System.exit(0);
	}
}
