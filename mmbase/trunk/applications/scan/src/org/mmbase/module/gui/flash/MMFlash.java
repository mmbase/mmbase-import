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

public class MMFlash extends Module {

	private String classname = getClass().getName();
	private boolean debug = false;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); } 
	private String htmlroot;

	private int count=0;	
	scanparser scanp;
	String generatortemppath;
	String generatorpath;
	String generatorprogram;
	LRUHashtable lru=new LRUHashtable(128);
	MMBase mmb;

	public void init() {
		String dtmp=System.getProperty("mmbase.mode");
		if (dtmp!=null && dtmp.equals("demo")) {
			String curdir=System.getProperty("user.dir");
			htmlroot=curdir+"/default-web-app/";
		} else {
			htmlroot=System.getProperty("mmbase.htmlroot");
		}
		mmb=(MMBase)getModule("MMBASEROOT");
		scanp=(scanparser)getModule("SCANPARSER");
		generatortemppath=getInitParameter("generatortemppath");
		generatorpath=getInitParameter("generatorpath");
		generatorprogram=getInitParameter("generatorprogram");
	}

	public void onload() {
	}

	public MMFlash() {
	}

	public synchronized byte[] getScanParsedFlash(String url,String query,HttpServletRequest req) {

	
		// its generated now load it
		String filename=htmlroot+url;

    	byte[] inp=readBytesFile(filename);
		if (inp==null) {
			System.out.println("No valid sxf file ("+filename+") !");
			return(null);
		}
		String ibody=new String(inp);


		// oke try to parse it
		if (scanp!=null) {
			scanpage sp=new scanpage();
			sp.body=ibody;
			if (query!=null) sp.setParamsLine(query);
			sp.req=req;
			try {
				ibody=scanp.handle_line(sp.body,null,sp);
			} catch(Exception e) {}
		} else {
			System.out.println("MMFlash-> can't reach scanparser");
		}


		// now feed it to the xml reader
		CharArrayReader reader=new CharArrayReader(ibody.toCharArray());
			
		XMLDynamicFlashReader script=new XMLDynamicFlashReader(reader);

		String body="";
		String src=script.getSrcName();
		if (src.startsWith("/")) {
			body+="INPUT \""+htmlroot+src+"\"\n";
		} else {
			String purl=url.substring(0,url.lastIndexOf('/')+1);
			src=purl+src;
			body+="INPUT \""+htmlroot+src+"\"\n";
		}
		body+="OUTPUT \""+generatortemppath+"export.swf\"\n";


		// is there a caching option set ?
		String caching=script.getCaching();
		if (caching!=null && caching.equals("lru")) {
			byte[] bytes=(byte[])lru.get(url+query);
			if (bytes!=null) {
				return(bytes);
			} else {
			}
		} else if (caching!=null && caching.equals("disk")) {
			byte[] bytes=(byte[])lru.get(url+query);
			if (bytes!=null) {
				System.out.println("WOW from disk+lru");
				return(bytes);
			} else {
				bytes=loadDiskCache(htmlroot+src,query);
				if (bytes!=null) {
					System.out.println("WOW from disk");
					lru.put(url+query,bytes);
					return(bytes);
				}
			}
		}


		String scriptpath=src;
		scriptpath=scriptpath.substring(0,scriptpath.lastIndexOf('/')+1);

		body+=addDefines(script.getDefines(),scriptpath);
		body+=addReplaces(script.getReplaces(),scriptpath);

		// save the created input file for the generator
		saveFile(generatortemppath+"input.sws",body);	

		// lets generate the file
		generateFlash(scriptpath);
	
		byte[] bytes=readBytesFile(generatortemppath+"export.swf");
		if (caching!=null && caching.equals("lru")) {
			lru.put(url+query,bytes);
		} else if (caching!=null && caching.equals("disk")) {
			saveDiskCache(htmlroot+src,query,bytes);
			lru.put(url+query,bytes);
		}	
		return(bytes);
	}

	private String addReplaces(Vector replaces,String scriptpath) {
		String part="";
		for (Enumeration e=replaces.elements();e.hasMoreElements();) {
			Hashtable rep=(Hashtable)e.nextElement();
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
						body=body.replace('\"','\'');
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
						body=body.replace('\"','\'');
						part+="\tSTRING \""+body+"\"\n";
					}
				}
				part+="}\n";	
			}
			part+="\n";
		}
		return(part);
	}


	private String addDefines(Vector defines,String scriptpath) {
		String part="";
		int counter=1;
		for (Enumeration e=defines.elements();e.hasMoreElements();) {
			Hashtable rep=(Hashtable)e.nextElement();
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
					if (src.startsWith("/img.db?")) {
						String result=mapImage(src.substring(8),counter++);
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

    byte[] readBytesFile(String filename) {
		File bfile = new File(filename);
		int filesize = (int)bfile.length();
		byte[] buffer=new byte[filesize];
		try {
				FileInputStream scan = new FileInputStream(bfile);
			int len=scan.read(buffer,0,filesize);
			scan.close();
		} catch(FileNotFoundException e) {
			//System.out.println("error getfile : "+filename);
			return(null);
 		} catch(IOException e) {
			return(null);
		}
		return(buffer);
    }


	static byte[] loadDiskCache(String filename,String query) {
		if (query!=null) {
			filename=filename.substring(0,filename.length()-3)+"swf?"+query;
		} else {
			filename=filename.substring(0,filename.length()-3)+"swf";
		}
		File bfile = new File(filename);
		int filesize = (int)bfile.length();
		byte[] buffer=new byte[filesize];
		try {
				FileInputStream scan = new FileInputStream(bfile);
			int len=scan.read(buffer,0,filesize);
			scan.close();
		} catch(FileNotFoundException e) {
			//System.out.println("error getfile : "+filename);
			return(null);
 		} catch(IOException e) {
			return(null);
		}
		return(buffer);
    }


	private void generateFlash(String scriptpath) {	
		Process p=null;
        	String s="",tmp="";
		DataInputStream dip= null;
		DataInputStream diperror= null;
		String command;
		PrintStream out=null;	
		RandomAccessFile  dos=null;	


		try {
			command=generatorpath+generatorprogram+" "+generatortemppath+"input.sws";
			p = (Runtime.getRuntime()).exec(command);
		} catch (Exception e) {
			s+=e.toString();
			out.print(s);
		}

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
			e.printStackTrace();
        	try {
				dip.close();
        	} catch (Exception f) {
			}
		}
	}


	static boolean saveFile(String filename,String value) {
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.writeBytes(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}


	static boolean saveDiskCache(String filename,String query,byte[] value) {
		if (query!=null) {
			filename=filename.substring(0,filename.length()-3)+"swf?"+query;
		} else {
			filename=filename.substring(0,filename.length()-3)+"swf";
		}
		//System.out.println("filename="+filename);	
		
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.write(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}
	
	String mapImage(String imageline,int counter) {
		Images bul=(Images)mmb.getMMObject("images");
		Vector params=new Vector();
		if (bul!=null) {
			// rebuild the param
			StringTokenizer tok = new StringTokenizer(imageline,"+\n\r");
			while (tok.hasMoreTokens()) {
				params.addElement(tok.nextToken());
				scanpage sp=new scanpage();
				byte[] bytes=bul.getImageBytes(sp,params);
				saveFile(generatortemppath+"/image"+counter+".jpg",bytes);
			}
		}
		return(generatortemppath+"/image"+counter+".jpg");
	}


	static boolean saveFile(String filename,byte[] value) {
		File sfile = new File(filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.write(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}
}
