package org.mmbase.module.builders.vwms;

import org.mmbase.util.*;

public class SCPcopy {
Execute exec=new Execute();
String thisserver;
String dstpath,dstuser,dsthost;
String sshpath;
private static final boolean debug=true;

	public SCPcopy() {
		setSSHpath("/usr/local/bin");
		setUser("vpro");
		setHost("vpro.omroep.nl");
		setPath("/bigdisk/htdocs");
	}


	public SCPcopy(String sshpath) {
		setSSHpath(sshpath);
		setUser("vpro");
		setHost("vpro.omroep.nl");
		setPath("/bigdisk/htdocs");
	}

	public SCPcopy(String sshpath,String user) {
		setSSHpath(sshpath);
		setUser(user);
		setHost("vpro.omroep.nl");
		setPath("/bigdisk/htdocs");
	}

	public SCPcopy(String sshpath,String user,String host) {
		setSSHpath(sshpath);
		setUser(user);
		setHost(host);
		setPath("/bigdisk/htdocs");
	}

	public SCPcopy(String sshpath,String user,String host,String path) {
		setSSHpath(sshpath);
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
		String path,res;

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

		res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mkdir "+path+"");
		System.out.println("SCPcopy -> mkdir "+path+" : "+res);
		rtn=res.length()<=0;
		return(rtn);
	}

	public boolean mkdirs(String path) {
		String res;
		boolean rtn=true;

		res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mkdir -p "+path+"");
		if (debug) System.out.println("SCPcopy -> mkdirs "+path+" : "+res);
		rtn=res.length()<=0;
		return(rtn);
	}

	public boolean rename(String src,String dst) {
		boolean rtn=true;
		String res;

		int pos=src.indexOf("(");
		if (pos==-1) {
			res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mv "+src+" "+dst+"");
		} else {
			res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" mv \""+src+"\" \""+dst+"\"");
		}

		if (debug) System.out.println("SCPcopy -> rename "+src+"->"+dst+" : "+res);
		rtn=res.length()<=0;
		return(rtn);
	}

	public boolean realcopy(String src,String dst) {
		String res;
		boolean rtn=true;

		int pos=src.indexOf("(");
		if (pos==-1) {
			res=exec.execute(sshpath+"/scp -B -A -q "+src+" "+dstuser+"@"+dsthost+":"+dst);
		} else {
			res=exec.execute(sshpath+"/scp -B -A -q "+src+" "+dstuser+"@"+dsthost+":\""+dst+"\"");
		}

		if (debug) System.out.println("SCPcopy -> copy "+src+"->"+dst+" : "+res);
		rtn=res.length()<=0;
		return (rtn);
	}

	public boolean remove(String path) {
		String res;
		boolean rtn=true;

		res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" rm -f "+path);

		if (debug) System.out.println("SCPcopy -> remove "+path+" : "+res);
		rtn=res.length()<=0;
		return (rtn);
	}

	public boolean removedir(String path) {
		String res;
		boolean rtn=true;

		res=exec.execute(sshpath+"/ssh -q -l "+dstuser+" "+dsthost+" rmdir "+path);

		if (debug) System.out.println("SCPcopy -> removedir "+path+" : "+res);
		rtn=res.length()<=0;
		return (rtn);
	}


	
	public String getSSHpath() {
		return(sshpath);
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


	
	public void setSSHpath(String path) {
		sshpath=path;		
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
