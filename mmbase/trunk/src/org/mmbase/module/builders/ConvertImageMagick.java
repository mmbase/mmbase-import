/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: ConvertImageMagick.java,v 1.3 2000-07-06 08:54:41 install Exp $

	$Log: not supported by cvs2svn $
	Revision 1.2  2000/06/08 18:00:11  wwwtech
	Rico: reduced/switched-off debug
	
	Revision 1.1  2000/06/02 10:57:49  wwwtech
	Rico: seperated conversion from the builder
	
*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 *
 * Converts Images using image magick.
 *
 * @author Rico Jansen
 * @version $Id: ConvertImageMagick.java,v 1.3 2000-07-06 08:54:41 install Exp $
 */
public class ConvertImageMagick implements ImageConvertInterface {

	private String classname = getClass().getName();
	private boolean debug = false;
	private void debug(String msg) { System.out.println(classname+":"+msg); }

	// Currenctly only ImageMagick works
	private static String ConverterRoot = "/usr/local/";
	private static String ConverterCommand = "bin/convert";

	public void init(Hashtable params) {
		String tmp;
		tmp=(String)params.get("ImageConvert.ConverterRoot");
		if (tmp!=null) ConverterRoot=tmp;
		tmp=(String)params.get("ImageConvert.ConverterCommand");
		if (tmp!=null) ConverterCommand=tmp;
		if (debug) debug("Root="+ConverterRoot);
		if (debug) debug("Command="+ConverterCommand);
	}

	public byte[] ConvertImage(byte[] input,Vector commands) {	
		String cmd,format;
		byte[] pict=null;

		if (commands!=null && input!=null) {
			cmd=getConvertCommands(commands);
			format=getConvertFormat(commands);
			pict=ConvertImage(input,cmd,format);
		}
		return(pict);
	}


	private String getConvertFormat(Vector params) {
		String format="jpg",key,cmd,type;
		int pos,pos2;

		for (Enumeration t=params.elements();t.hasMoreElements();) {
			key=(String)t.nextElement();
			pos=key.indexOf('(');
			pos2=key.lastIndexOf(')');
			if (pos!=-1 && pos2!=-1) {
				type=key.substring(0,pos);
				cmd=key.substring(pos+1,pos2);
				if (type.equals("f")) {
					format=cmd;
					break;
				}
			}
		}
		return(format);
	}

	private String getConvertCommands(Vector params) {
		StringBuffer cmdstr=new StringBuffer();
		Vector cmds=new Vector();
		String key,cmd,type;
		int pos,pos2;

		for (Enumeration t=params.elements();t.hasMoreElements();) {
			key=(String)t.nextElement();
			pos=key.indexOf('(');
			pos2=key.lastIndexOf(')');
			if (pos!=-1 && pos2!=-1) {
				type=key.substring(0,pos);
				cmd=key.substring(pos+1,pos2);
				if (debug) debug("getCommands(): type="+type+" cmd="+cmd);
				if (type.equals("s")) {
					cmds.addElement("-geometry "+cmd);
				} else if (type.equals("r")) {
					cmds.addElement("-rotate "+cmd);
				} else if (type.equals("c")) {
					cmds.addElement("-colors "+cmd);
				} else if (type.equals("colorize")) {
					// not supported ?
					cmds.addElement("-colorize "+cmd);
				} else if (type.equals("bordercolor")) {
					// not supported ?
					cmds.addElement("-bordercolor #"+cmd);
				} else if (type.equals("blur")) {
					cmds.addElement("-blur "+cmd);
				} else if (type.equals("edge")) {
					cmds.addElement("-edge "+cmd);
				} else if (type.equals("implode")) {
					cmds.addElement("-implode "+cmd);
				} else if (type.equals("gamma")) {
					// cmds.addElement("-gamma "+cmd);
					StringTokenizer tok = new StringTokenizer(cmd,",");
					String r=tok.nextToken();
					String g=tok.nextToken();
					String b=tok.nextToken();
					cmds.addElement("-gamma "+r+"/"+g+"/"+b);
				} else if (type.equals("border")) {
					cmds.addElement("-border "+cmd);
				} else if (type.equals("pen")) {
					cmds.addElement("-pen #"+cmd+"");
				} else if (type.equals("font")) {
					cmds.addElement("font "+cmd);
				} else if (type.equals("circle")) {
					cmds.addElement("draw 'circle "+cmd+"'");
				} else if (type.equals("text")) {
					StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
					try {
						String x=tok.nextToken();
						String y=tok.nextToken();
						String te=tok.nextToken();
						cmds.addElement("-draw \"text +"+x+"+"+y+" "+te+"\"");
					} catch (Exception e) {}
				} else if (type.equals("raise")) {
					cmds.addElement("-raise "+cmd);
				} else if (type.equals("shade")) {
					cmds.addElement("-shade "+cmd);
				} else if (type.equals("modulate")) {
					cmds.addElement("-modulate "+cmd);
				} else if (type.equals("colorspace")) {
					cmds.addElement("-colorspace "+cmd);
				} else if (type.equals("shear")) {
					cmds.addElement("-shear "+cmd);
				} else if (type.equals("swirl")) {
					cmds.addElement("-swirl "+cmd);
				} else if (type.equals("wave")) {
					cmds.addElement("-wave "+cmd);
				} else if (type.equals("t")) {
					cmds.addElement("-transparency #"+cmd+"");
				} else if (type.equals("part")) {
					StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
					try {
						int x1=Integer.parseInt(tok.nextToken());
						int y1=Integer.parseInt(tok.nextToken());
						int x2=Integer.parseInt(tok.nextToken());
						int y2=Integer.parseInt(tok.nextToken());
						cmds.addElement("-crop "+(x2-x1)+"x"+(y2-y1)+"+"+x1+"+"+y1);
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
				} else if (type.equals("i")) {
                    cmds.addElement("-interlace "+cmd);
                } else if (type.equals("q")) {
					cmds.addElement("-quality "+cmd);
				}
			} else {
				if (key.equals("mono")) {
					cmds.addElement("-monochrome");
				} else if (key.equals("contrast")) {
					cmds.addElement("-contrast");
				} else if (key.equals("lowcontrast")) {
					cmds.addElement("+contrast");
				} else if (key.equals("highcontrast")) {
					cmds.addElement("-contrast");
				} else if (key.equals("noise")) {
					cmds.addElement("-noise");
				} else if (key.equals("emboss")) {
					cmds.addElement("-emboss");
				} else if (key.equals("flipx")) {
					cmds.addElement("-flop");
				} else if (key.equals("flipx")) {
					cmds.addElement("-flop");
				} else if (key.equals("flipy")) {
					cmds.addElement("-flip");
				} else if (key.equals("dia")) {
					cmds.addElement("-negate");
				} else if (key.equals("neg")) {
					cmds.addElement("+negate");
				}
			}
		}
		for (Enumeration t=cmds.elements();t.hasMoreElements();) {
			key=(String)t.nextElement();
			cmdstr.append(key);
			cmdstr.append(" ");
		}
		return(cmdstr.toString());
	}
	
	private byte[] ConvertImage(byte[] pict,String cmd, String format) {	
		Process p=null;
        String s="",tmp="";
		DataInputStream dip= null;
		DataInputStream diperror= null;
		String command;
		PrintStream out=null;	
		RandomAccessFile  dos=null;	

		if (debug) debug("ConvertImage(): converting img("+cmd+")");

		byte[] result=new byte[1024*1024];
		try {
			command=ConverterRoot+ConverterCommand+" - "+cmd+" "+format+":-";
			debug("ConvertImage(): "+command);
			p = (Runtime.getRuntime()).exec(command);
        	PrintStream printStream = new PrintStream(p.getOutputStream()); // set the input stream for cgi
			printStream.write(pict,0,pict.length);
			printStream.flush();	
			printStream.close();	
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
			if (debug) debug("ConvertImage(): ok "+cmd+" len "+len2);
			return(res);
        } catch (Exception e) {
			debug("ConvertImage(): convert failed ! img("+cmd+")");
			e.printStackTrace();
        	try {
				dip.close();
        	} catch (Exception f) {
			}
			return(null);
		}
	}

}
