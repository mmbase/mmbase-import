/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import org.mmbase.util.logging.*;


/**
 * MMBase version reporter
 */
public class Version {


    public static void main(String args[]) {
	System.out.println("\n\n Version report tool 0.2 ");
	System.out.println("=========================\n");
	if (args.length>0) {
		String cmd=args[0];
		if (cmd.equals("list")) {
			performList(args[1]);
		} else if (cmd.equals("help")) {
			showHelp();
		} else if (cmd.equals("check")) {
			performCheck(args[1],args[2]);
		} else if (cmd.equals("checklist")) {
			performCheckList(args[1],args[2],false);
		} else if (cmd.equals("checknew")) {
			performCheckList(args[1],args[2],true);
		} else if (cmd.equals("create")) {
			createManifest(args[1],args[2],args[3]);
		}
	} else {
		System.out.println("No command found, showing help\n");
		showHelp();
	}
    }

    public static void showHelp() {
	System.out.println("usage   : [command] [param1] [param2] [...]\n");
	System.out.println("format  : check localjar remote-manifest");
	System.out.println("info    : compare mmbase.jar version against a manifest file");
	System.out.println("example : check /usr/mmrunner/lib/mmbase.jar http://www.mmbase.org/cvs/MANIFEST.MF\n");
	System.out.println("format  : checklist localjar remote-manifest");
	System.out.println("info    : compare mmbase.jar version per classfile");
	System.out.println("example : checklist /usr/mmrunner/lib/mmbase http://www.mmbase.org/cvs/MANIFEST.MF\n");
	System.out.println("format  : create sourcepath/cvsfile manifest-file versionfile");
	System.out.println("info    : create manifest file from source or cvs version file");
	System.out.println("example : create /tmp/cvsversions.log /tmp/MANIFEST.MF /tmp/version.txt");
	System.out.println("remark  : normally only used by release maintainer/admin\n");
    }

    public static void createManifest(String path,String targetfile,String versionfile) {
	String body="Manifest-Version: 1.0\n";
		
	body+=getMainMMBaseVersion(versionfile);

	// check if input is a cvs log or a src dir
	File fl=new File(path);
	if (fl.isDirectory()) {
		body+=getManifestFile(path);
	} else {
		body+=getManifestCVS(path);
	}
	System.out.println("writing new manifest file : "+targetfile);
	saveFile(targetfile,body);
    }

    public static String getMainMMBaseVersion(String versionfile) {
	String maintainer="unknown";
	int major=0;
	int minor=0;
	int build=0;
	
	String ver=loadFile(versionfile);
	StringTokenizer tok=new StringTokenizer(ver,"\n\r");
	while (tok.hasMoreTokens()) {
		String line=tok.nextToken();
		if (line.startsWith("maintainer=")) {
			maintainer=line.substring(11);
		} else if (line.startsWith("major=")) {
			try {
				major=Integer.parseInt(line.substring(6));
			} catch(Exception e) {}
		} else if (line.startsWith("minor=")) {
			try {
				minor=Integer.parseInt(line.substring(6));
			} catch(Exception e) {}
		} else if (line.startsWith("build=")) {
			try {
				build=Integer.parseInt(line.substring(6));
			} catch(Exception e) {}
		}
	}
	String body="Name: MMBase Content Management System\n";
	body+="Created-By: "+maintainer+"\n";
	body+="Implementation-Vendor: mmbase organisation\n";
	body+="Implementation-Version: "+major+"."+minor+"."+(build+1)+"\n";
	body+="Implementation-Date: "+new Date().toGMTString()+"\n\n";
	String newbody="maintainer="+maintainer+"\n";
	newbody+="major="+major+"\n";
	newbody+="minor="+minor+"\n";
	newbody+="build="+(build+1)+"\n";
	System.out.println("updating version file : "+versionfile+" to : "+major+"."+minor+"."+(build+1));
	saveFile(versionfile,newbody);
	return(body);
    }


    public static String getManifestCVS(String filename) {
	System.out.println("reading : "+filename+" for target manifest");
	String body="";
	String cvslog=loadFile(filename);
	StringTokenizer tok=new StringTokenizer(cvslog,"\n\r");
	while (tok.hasMoreTokens()) {
		// read 9 lines
		String l1=tok.nextToken();
		String fileline=tok.nextToken();
		String wline=tok.nextToken();
		String rline=tok.nextToken();
		String l6=tok.nextToken();	
		String l7=tok.nextToken();	
		String l8=tok.nextToken();	
		// convert the rline to a valid package name
		if (rline.endsWith(".java,v")) {
			int pos=rline.indexOf("/cvs/");
			if (pos!=-1) {
				String p="org."+rline.substring(pos+5,rline.length()-7);
				p=p.replace('/','.');
				String v=rline.substring(24);
				int pos2=v.indexOf("\t");
				if (pos2!=-1) {
					v=v.substring(0,pos2);
				}
				if (!p.equals("org.mmbase.Version")) {
					body+="Name: "+p+"\n";
					body+="Created-By: www.mmbase.org\n";
					body+="Implementation-Version: "+v+"\n\n";
				}
			}
		}
	}
	return(body);
    }

    public static String getManifestFile(String path) {
	System.out.println("scanning : "+path+"/* for target manifest");
	String body="";
	File fl=new File(path);
	if (fl.isDirectory()) {
            String files[] = fl.list();
            for (int i=0;i<files.length;i++) {
                String bname=files[i];
		body+=getManifestFile(path+bname+"/");
		
	    }
	} else {
		String filename=fl.getPath();
		if (filename.endsWith(".java")) {
			String classname="";
			String filebody=loadFile(filename);
			// find package name
			int pos=filebody.indexOf("package");
			int pos2=filebody.indexOf(";",pos);
			if (pos!=-1 && pos2!=-1) {
				classname=filebody.substring(pos+8,pos2);

				// find class name or interface name
				classname+="."+fl.getName().substring(0,fl.getName().length()-5);
			}
			pos=filebody.indexOf("$Id:");
			if (pos!=-1) {
				filebody=filebody.substring(pos);
				pos2=filebody.indexOf(",v ");
				filebody=filebody.substring(pos2+3);
				int pos3=filebody.indexOf(" ");
				String version=filebody.substring(0,pos3);
				if (!classname.equals("org.mmbase.Version")) {
					body+="Name: "+classname+"\n";
					body+="Created-By: www.mmbase.org\n";
					body+="Implementation-Version: "+version+"\n\n";
				}
			}
		}
	}
	return(body);
    }

    public static void performCheck(String file,String checkurl) {
	System.out.println("checking : "+file+" against public manifest : "+checkurl);
	try {
		JarFile jf=new JarFile(file);
		Manifest mf=jf.getManifest();
		System.out.println("local manifest : loaded");

		URL ur=new URL(checkurl);
		InputStream in=ur.openStream();	
		Manifest remotemf=new Manifest(in);
		System.out.println("remote manifest : loaded");


		if (mf.equals(remotemf)) {
			System.out.println("mmbase.jar up to date");
		} else {
			System.out.println("mmbase.jar needs update !! ");
		}
	
	} catch(Exception e) {
		e.printStackTrace();
	}
    }


    public static void performCheckList(String file,String checkurl,boolean newonly) {
	System.out.println("checking : "+file+" against public manifest : "+checkurl);
	try {
		URL ur=new URL(checkurl);
		InputStream in=ur.openStream();	
		Manifest remotemf=new Manifest(in);
		System.out.println("remote manifest : loaded");

		JarFile jf=new JarFile(file);
		Manifest mf=jf.getManifest();
		System.out.println("local manifest : loaded");

		Iterator r=remotemf.getEntries().keySet().iterator();
		while (r.hasNext()) {
			String key=(String)r.next();
			Attributes at=mf.getAttributes(key);
			String version=at.getValue("Implementation-Version");
			String createdby=at.getValue("Created-By");
			Attributes remoteat=remotemf.getAttributes(key);
			String remoteversion=remoteat.getValue("Implementation-Version");
			String remotecreatedby=remoteat.getValue("Created-By");
			/*
			System.out.println("version="+version);
			System.out.println("createdby="+createdby);
			*/

			if (version.equals(remoteversion)) {
				if (!newonly) {
				System.out.println("valid : "+key+" "+version+" "+createdby);
				}
			} else {
				System.out.println("newer version : "+key+" "+version+" "+createdby+" (remote version : "+remoteversion+")");
			}
		
		}
	} catch(Exception e) {
		e.printStackTrace();
	}
    }

    public static void performList(String file) {

	try {
		JarFile jf=new JarFile(file);
		Manifest mf=jf.getManifest();
		//System.out.println("mf="+mf.getMainAttributes().keySet());
		//System.out.println("me="+mf.getEntries().keySet());
		Iterator r=mf.getEntries().keySet().iterator();
		while (r.hasNext()) {
			String key=(String)r.next();
			Attributes at=mf.getAttributes(key);
			String version=at.getValue("Implementation-Version");
			String createdby=at.getValue("Created-By");
			System.out.println("check="+key+" "+version+" "+createdby);
		
		}
	} catch(Exception e) {
		e.printStackTrace();
	}
    }


    private static String loadFile(String file) {
        File scanfile;
        int filesize,len=0;
        byte[] buffer;
        FileInputStream scan;
        Date lastmod;
        String rtn=null;

        scanfile = new File(file);
        filesize = (int)scanfile.length();
        lastmod=new Date(scanfile.lastModified());
        buffer=new byte[filesize];
        try {
            scan = new FileInputStream(scanfile);
            len=scan.read(buffer,0,filesize);
            scan.close();
        } catch(FileNotFoundException e) {
			// oops we have a problem
        } catch(IOException e) {}
        if (len!=-1) {
            rtn=new String(buffer);
        }
        return(rtn);
    }


    public static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}


