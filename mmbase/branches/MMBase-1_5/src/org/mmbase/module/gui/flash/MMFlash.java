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
 * @author Daniel Ockeloen
 * @version $Id: MMFlash.java,v 1.13 2001-10-29 14:10:49 vpro Exp $
 */
public class MMFlash extends Module {

	static Logger log =Logging.getLoggerInstance(MMFlash.class.getName()); 

	private String classname = getClass().getName();
	private boolean debug = false;
	private void debug( String msg ) { System.out.println( classname +":"+ msg ); } 
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

		// check if we may create a file on location of generatorTempPath
		File tempPath = new File(generatortemppath);
		if(!tempPath.isDirectory()) {
			log.error("Generator Temp Path was not a direcory('"+generatorpath+generatorprogram+"'), please edit mmflash.xml, or create directory");							
		}
		try {
			File test = File.createTempFile("flash", "test", tempPath);
			test.deleteOnExit();
		} catch (Exception e) {
			log.error("Could not create a temp file in directory:'"+generatortemppath+"' for flash, please edit mmflash.xml or change rights");					
		}
		
		// check if there is a program on this location
		try {
			(Runtime.getRuntime()).exec(generatorpath+generatorprogram);
		} catch (Exception e) {
			log.error("Could not execute command:'"+generatorpath+generatorprogram+"' for flash, please edit mmflash.xml");					
		}
		log.debug("Module MMFlash started (flash-generator='"+generatorpath+generatorprogram+"' and can be executed and tmpdir is checked)");							
	}

	public void onload() {
	}

	public MMFlash() {
	}

	public synchronized byte[] getDebugSwt(scanpage sp) {
		String filename=htmlroot+sp.req.getRequestURI();
		byte[] bytes=generateSwtDebug(filename);
		return(bytes);
	}

	public synchronized byte[] getScanParsedFlash(scanpage sp) {
		// Get inputfile
		String url = sp.req.getRequestURI();
		String filename=htmlroot+url;
    	byte[] inp=readBytesFile(filename);
		if (inp==null) {
			log.error( "No valid sxf file ("+filename+") !" );		
			return(null);
		}
		sp.body = new String(inp);

		// oke try to parse it
		if (scanp!=null) {
			try {
				sp.body = scanp.handle_line(sp.body,sp.session,sp);
			} catch(Exception e) {}
		} else {
			log.error("MMFlash-> can't reach scanparser");
		}

		// now feed it to the xml reader
		CharArrayReader reader=new CharArrayReader(sp.body.toCharArray());
			
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
		String query=sp.req.getQueryString();
		
		if (!sp.reload) {
			if (caching!=null && caching.equals("lru")) {
				byte[] bytes=(byte[])lru.get(url+query);
				if (bytes!=null) {
					return(bytes);
				}
			} else if (caching!=null && caching.equals("disk")) {
				byte[] bytes=(byte[])lru.get(url+query);
				if (bytes!=null) {
					log.info("WOW from disk+lru");
					return(bytes);
				} else {
					bytes=loadDiskCache(htmlroot+src,query);
					if (bytes!=null) {
						log.info("WOW from disk");
						lru.put(url+query,bytes);
						return(bytes);
					}
				}
			}
		}// !sp.reload


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

    /**
     * This function will try to generate a new flash thingie, generated from a template.
     * the only thing which has to be specified is the XML, and the working direcotory.
     * This function was added, so that there is the possibility to use the generater 
     * from a place without SCAN 
     * @param	flashXML    a xml which contains the manipulations on the flash template
     * @param 	workingdir  the path where there has to be searched for the template and the 
     *	    	    	    other things, like pictures.(THIS LOOKS BELOW THE mmbase.htmlroot !!)
     * @return      	    a byte thingie, which contains the new generated flash thingie
     */
    public synchronized byte[] getParsedFlash(String flashXML, String workingdir) {
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
	
    	// hey ho, generate our template..
	body+="INPUT \""+inputFile.getAbsolutePath()+"\"\n";
	body+="OUTPUT \""+generatortemppath+"export.swf\"\n";

    	String scriptpath=src;
	scriptpath=scriptpath.substring(0,scriptpath.lastIndexOf('/')+1);

	body+=addDefines(script.getDefines(),scriptpath);
	body+=addReplaces(script.getReplaces(),scriptpath);

	// save the created input file for the generator
	saveFile(generatortemppath+"input.sws",body);	

	// lets generate the file
	generateFlash(scriptpath);
	
	// retrieve the result of the genererator..
	byte[] bytes=readBytesFile(generatortemppath+"export.swf");
	
	// store the flash in cache, when needed...
	if (caching!=null && (caching.equals("lru")|| caching.equals("disk")) ) {
	    lru.put(src + flashXML, bytes);
	    if(caching.equals("disk")) {
    	    	saveDiskCache(src, flashXML, bytes);
	    }
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
			log.error("error getfile, not found : "+filename);
			return(null);
 		} catch(IOException e) {
			log.error("error getfile, could not read : "+filename);		
			return null;
		}
		return(buffer);
    }


	private byte[] loadDiskCache(String filename,String query) {
		if (query!=null) {
			filename=filename.substring(0,filename.length()-3)+"swf?"+query;
		} else {
			filename=filename.substring(0,filename.length()-3)+"swf";
		}

		if (subdir!=null && !subdir.equals("")) {
			int pos=filename.lastIndexOf('/');
			filename=filename.substring(0,pos)+"/"+subdir+filename.substring(pos);
		}

		File bfile = new File(filename);
		int filesize = (int)bfile.length();
		byte[] buffer=new byte[filesize];
		try {
				FileInputStream scan = new FileInputStream(bfile);
			int len=scan.read(buffer,0,filesize);
			scan.close();
		} catch(FileNotFoundException e) {
			log.error("error getfile, not found : "+filename);			
			return(null);
 		} catch(IOException e) {
			log.error("error getfile, could not read : "+filename);		
			return(null);
		}
		return(buffer);
    }


	private byte[] generateSwtDebug(String filename) {

		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;
		DataInputStream diperror= null;
		String command="";
		PrintStream out=null;	
		RandomAccessFile  dos=null;	


		try {
			command=generatorpath+generatorprogram+" -d "+filename;
			p = (Runtime.getRuntime()).exec(command);
		} catch (Exception e) {
			log.error("could not execute command:'"+command+"'");					
			s+=e.toString();
			out.print(s);
		}
		log.debug("Executed command: "+command+" succesfull, now gonna parse");									
		log.info("Executed command: "+command+" succesfull, now gonna parse");									
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
        	} catch (Exception f) {
			}
		}
		return(result);
	}

	private void generateFlash(String scriptpath) {	
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;
		DataInputStream diperror= null;
		String command="";
		PrintStream out=null;	
		RandomAccessFile  dos=null;	


		try {
			command=generatorpath+generatorprogram+" "+generatortemppath+"input.sws";
			p = (Runtime.getRuntime()).exec(command);
		} catch (Exception e) {
			log.error("could not execute command:'"+command+"'");					
			s+=e.toString();
			out.print(s);
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
			log.error("Could not write values to file:" +filename+ " with value" + value);		
			e.printStackTrace();
		}
		return(true);
	}


	private boolean saveDiskCache(String filename,String query,byte[] value) {
		if (query!=null) {
			filename=filename.substring(0,filename.length()-3)+"swf?"+query;
		} else {
			filename=filename.substring(0,filename.length()-3)+"swf";
		}

		if (subdir!=null && !subdir.equals("")) {
			int pos=filename.lastIndexOf('/');
			filename=filename.substring(0,pos)+"/"+subdir+filename.substring(pos);
			// Create dir if it doesn't exist
			File d=new File(filename.substring(0,pos)+"/"+subdir);
			if (!d.exists()) {
				d.mkdir();
			}
		}

		log.debug("filename="+filename);		
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
	
	String mapImage(String imageline,int counter) {
		Images bul=(Images)mmb.getMMObject("images");
		Vector params=new Vector();
		if (bul!=null) {
			// rebuild the param
			log.debug("rebuilding param");					
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
			log.error("Could not save to file:"+filename);							
			log.error(Logging.stackTrace(e));
		}
		return(true);
	}
}
