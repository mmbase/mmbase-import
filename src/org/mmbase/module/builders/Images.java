/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: Images.java,v 1.9 2000-03-30 14:15:04 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.8  2000/03/30 13:11:32  wwwtech
	Rico: added license
	
	Revision 1.7  2000/03/29 10:59:22  wwwtech
	Rob: Licenses changed
	
	Revision 1.6  2000/03/24 14:33:58  wwwtech
	Rico: total recompile
	
	Revision 1.5  2000/03/14 12:50:16  wwwtech
	Rico: changed gamma params from / seperator to , due to image cache problems
	
	Revision 1.4  2000/03/09 10:02:01  wwwtech
	Rico: added extra debug in case of failure
	
	Revision 1.3  2000/03/08 11:04:47  wwwtech
	Rico: added synchroniztion to images calculation
	
	Revision 1.2  2000/02/24 14:03:27  wwwtech
	Rico: fixed serveral debug messages plus added some
	
*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 *
 * images holds the images and provides ways to insert, retract and
 * search on them.
 *
 * @author Daniel Ockeloen
 * @version $Id: Images.java,v 1.9 2000-03-30 14:15:04 wwwtech Exp $
 */
public class Images extends MMObjectBuilder {

	private String classname = getClass().getName();
	private boolean debug = true;

	// Currenctly only ImageMagick works
	protected static String ConverterRoot = "/usr/local/";
	protected static String ConverterCommand = "bin/convert";

	public String getGUIIndicator(MMObjectNode node) {
		int num=node.getIntValue("number");
		if (num!=-1) {
			return("<A HREF=\"/img.db?"+node.getIntValue("number")+"\" TARGET=\"_new\"><IMG SRC=\"/img.db?"+node.getIntValue("number")+"+s(100x60)\" BORDER=0></A>");
		}
		return(null);
	}

	public void setDefaults(MMObjectNode node) {
		node.setValue("description","");
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("handle")) {
			int num=node.getIntValue("number");
			if (num!=-1) {
				return("<A HREF=\"/img.db?"+num+"\" TARGET=\"_new\"><IMG SRC=\"/img.db?"+num+"+s(100x60)\" BORDER=0></A>");
			}
		}
		return(null);
	}

	// glue method until org.mmbase.servlet.servdb is updated
	public byte[] getImageBytes5(Vector params) {
		return getImageBytes5(null,params);
	}

	public synchronized byte[] getImageBytes5(scanpage sp,Vector params) {
		int pos,pos2;
		String key;
		String type;
		String cmd;
		String format="jpg";
		String size=null;
		Vector cmds=new Vector();
		String ckey="";

		try {
		MMObjectBuilder bul=mmb.getMMObject("images");
		if (params==null || params.size()==0) {
			MMObjectNode node=bul.getNode(7452);
			return(node.getByteValue("handle"));
		}
		String num=(String)params.elementAt(0);
		
		// check if its a number if not check for name and even oalias
		try {
			int numint=Integer.parseInt(num);
		} catch(Exception e) {
			Enumeration g=search("MMNODE images.title==*"+num+"*");
			while (g.hasMoreElements()) {
				MMObjectNode imgnode=(MMObjectNode)g.nextElement();
				num=""+imgnode.getIntValue("number");
			}
		}	

		// is it a alias ? check in database and unmap
		if (params.size()>1 && ((String)params.elementAt(1)).indexOf('(')==-1) {
		} else if (params.size()==1) {
		}

		ckey=num;
		for (Enumeration t=params.elements();t.hasMoreElements();) {
			key=(String)t.nextElement();
			pos=key.indexOf('(');
			pos2=key.lastIndexOf(')');
			if (pos!=-1 && pos2!=-1) {
				type=key.substring(0,pos);
				cmd=key.substring(pos+1,pos2);
				debug("getImageBytes5(): type="+type+" cmd="+cmd);
				if (type.equals("f")) {
					format=cmd;
					ckey+=key;
				} else if (type.equals("s")) {
					cmds.addElement("-geometry "+cmd);
					ckey+=key;
				} else if (type.equals("r")) {
					cmds.addElement("-rotate "+cmd);
					ckey+=key;
				} else if (type.equals("c")) {
					cmds.addElement("-colors "+cmd);
					ckey+=key;
				} else if (type.equals("colorize")) {
					// not supported ?
					cmds.addElement("-colorize "+cmd);
					ckey+=key;
				} else if (type.equals("bordercolor")) {
					// not supported ?
					cmds.addElement("-bordercolor #"+cmd);
					ckey+=key;
				} else if (type.equals("blur")) {
					cmds.addElement("-blur "+cmd);
					ckey+=key;
				} else if (type.equals("edge")) {
					cmds.addElement("-edge "+cmd);
					ckey+=key;
				} else if (type.equals("implode")) {
					cmds.addElement("-implode "+cmd);
					ckey+=key;
				} else if (type.equals("gamma")) {
					// cmds.addElement("-gamma "+cmd);
					StringTokenizer tok = new StringTokenizer(cmd,",");
					String r=tok.nextToken();
					String g=tok.nextToken();
					String b=tok.nextToken();
					cmds.addElement("-gamma "+r+"/"+g+"/"+b);
					ckey+=key;
				} else if (type.equals("border")) {
					cmds.addElement("-border "+cmd);
					ckey+=key;
				} else if (type.equals("pen")) {
					cmds.addElement("-pen #"+cmd+"");
					ckey+=key;
				} else if (type.equals("font")) {
					cmds.addElement("font "+cmd);
					ckey+=key;
				} else if (type.equals("circle")) {
					cmds.addElement("draw 'circle "+cmd+"'");
					ckey+=key;
				} else if (type.equals("text")) {
					StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
					try {
						String x=tok.nextToken();
						String y=tok.nextToken();
						String te=tok.nextToken();
						cmds.addElement("-draw \"text +"+x+"+"+y+" "+te+"\"");
						ckey+=key;
					} catch (Exception e) {}
				} else if (type.equals("raise")) {
					cmds.addElement("-raise "+cmd);
					ckey+=key;
				} else if (type.equals("shade")) {
					cmds.addElement("-shade "+cmd);
					ckey+=key;
				} else if (type.equals("modulate")) {
					cmds.addElement("-modulate "+cmd);
					ckey+=key;
				} else if (type.equals("colorspace")) {
					cmds.addElement("-colorspace "+cmd);
					ckey+=key;
				} else if (type.equals("shear")) {
					cmds.addElement("-shear "+cmd);
					ckey+=key;
				} else if (type.equals("swirl")) {
					cmds.addElement("-swirl "+cmd);
					ckey+=key;
				} else if (type.equals("wave")) {
					cmds.addElement("-wave "+cmd);
					ckey+=key;
				} else if (type.equals("t")) {
					cmds.addElement("-transparency #"+cmd+"");
					ckey+=key;
				} else if (type.equals("part")) {
					StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
					try {
						int x1=Integer.parseInt(tok.nextToken());
						int y1=Integer.parseInt(tok.nextToken());
						int x2=Integer.parseInt(tok.nextToken());
						int y2=Integer.parseInt(tok.nextToken());
						cmds.addElement("-crop "+(x2-x1)+"x"+(y2-y1)+"+"+x1+"+"+y1);
						ckey+=key;
					} catch (Exception e) {}
				} else if (type.equals("roll")) {
					StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
					String str;
					int x=Integer.parseInt(tok.nextToken());
					int y=Integer.parseInt(tok.nextToken());
					if (x>=0) str="+"+x;
					else str=""+x;
					if (y>=0) str+="+"+y;
					else str+=""+y;
					cmds.addElement("-roll "+str);
					ckey+=key;
				} else if (type.equals("i")) {
                    cmds.addElement("-interlace "+cmd);
                    ckey+=key;
                }
			} else {
				if (key.equals("mono")) {
					cmds.addElement("-monochrome");
					ckey+=key;
				} else if (key.equals("contrast")) {
					cmds.addElement("-contrast");
					ckey+=key;
				} else if (key.equals("lowcontrast")) {
					cmds.addElement("+contrast");
					ckey+=key;
				} else if (key.equals("highcontrast")) {
					cmds.addElement("-contrast");
					ckey+=key;
				} else if (key.equals("noise")) {
					cmds.addElement("-noise");
					ckey+=key;
				} else if (key.equals("emboss")) {
					cmds.addElement("-emboss");
					ckey+=key;
				} else if (key.equals("flipx")) {
					cmds.addElement("-flop");
					ckey+=key;
				} else if (key.equals("flipx")) {
					cmds.addElement("-flop");
					ckey+=key;
				} else if (key.equals("flipy")) {
					cmds.addElement("-flip");
					ckey+=key;
				} else if (key.equals("dia")) {
					cmds.addElement("-negate");
					ckey+=key;
				} else if (key.equals("neg")) {
					cmds.addElement("+negate");
					ckey+=key;
				}
			}
		}


		ImageCaches bul2=(ImageCaches)mmb.getMMObject("icaches");
		synchronized(ckey) {
			byte[] ibytes=bul2.getCkeyNode(ckey);
	
			if (ibytes!=null) {
				return(ibytes);
			} else {
				// aaa
				byte[] pict=null;
				if (num.indexOf('(')==-1 && !num.equals("-1")) {
					MMObjectNode node=bul.getNode(num);
					 pict=node.getByteValue("handle");
				}
				if (pict!=null) {
					byte[] pict2=null;
					if (cmds.size()==0) {
						// pict2=getConverted(pict,format);
						pict2=getAllCalc(sp,pict,"",format);
					} else {
						cmd="";
						for (Enumeration t=cmds.elements();t.hasMoreElements();) {
							key=(String)t.nextElement();
							cmd+=key+" ";
						}
						pict2=getAllCalc(sp,pict,cmd,format);
					}
					if (pict2!=null) {
						bul=mmb.getMMObject("icaches");
						try {
							MMObjectNode newnode=bul.getNewNode("system");
							newnode.setValue("ckey",ckey);
							newnode.setValue("id",Integer.parseInt(num));
							newnode.setValue("handle",pict2);
							newnode.setValue("filesize",pict2.length);
							newnode.insert("imagesmodule");
						} catch (Exception e) {}
						return(pict2);
					} else {
						debug("getImageBytes5(): Convert problem params : "+params);
						return(null);
					}
				} else {
					bul=mmb.getMMObject("images");
					MMObjectNode node=bul.getNode(7452);
					return(node.getByteValue("handle"));
				}
			}
		}

		} catch(Exception h) {
			debug("getImageBytes5(): IMAGE PROBLEM ON : "+params);
			return(null);
		}
	}

	byte[] getAllCalc(scanpage sp,byte[] pict,String cmd, String format) {	
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;
		DataInputStream diperror= null;
		String command;
		PrintStream out=null;	
		RandomAccessFile  dos=null;	

 		if (sp!=null)
			debug("getAllCalc(): converting img("+cmd+") for page("+sp.req.getRequestURI()+") and user("+sp.getSessionName()+")");
		else
			debug("getAllCalc(): converting img("+cmd+") for UNKNOWN");

		byte[] result=new byte[1024*1024];
		try {
			command=ConverterRoot+ConverterCommand+" - "+cmd+" "+format+":-";
			debug("getAllCalc(): "+command);
			p = (Runtime.getRuntime()).exec(command);
        	PrintStream printStream = new PrintStream(p.getOutputStream()); // set the input stream for cgi
			printStream.write(pict,0,pict.length);
			printStream.flush();	
			printStream.close();	
			debug("getAllCalc(): close out done "+cmd);
			String line;
			debug("getAllCalc(): close error read done "+cmd);
			//p.waitFor();
		} catch (Exception e) {
			s+=e.toString();
			out.print(s);
			return(null);
		}

		dip = new DataInputStream(new BufferedInputStream(p.getInputStream()));

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
			byte[] res=new byte[len2];
	    	System.arraycopy(result, 0, res, 0, len2);
			debug("getAllCalc(): read oke "+cmd+" len "+len2);
			return(res);
        } catch (Exception e) {
			debug("getAllCalc(): converting failed ! img("+cmd+")");
			e.printStackTrace();
        	try {
				dip.close();
        	} catch (Exception f) {
			}
			return(null);
			//e.printStackTrace();
		}


	}


	public String getImageMimeType(Vector params) {
		String format=null,mimetype;
		String key,type,cmd;
		int pos,pos2;

		for (Enumeration e=params.elements();e.hasMoreElements();) {
			key=(String)e.nextElement();
			pos=key.indexOf('(');
			pos2=key.lastIndexOf(')');
			if (pos!=-1 && pos2!=-1) {
				type=key.substring(0,pos);
				cmd=key.substring(pos+1,pos2);
				if (type.equals("f")) {
					format=cmd;
				}
			}
		}
		if (format==null) format="jpg";
		mimetype=mmb.getMimeType(format);
		// debug("Images:: getImageMimeType: mmb.getMimeType("+format+") = "+mimetype);
		
		return(mimetype);
	}
}
		
