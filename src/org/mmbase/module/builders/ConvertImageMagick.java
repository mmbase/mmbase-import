/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: ConvertImageMagick.java,v 1.17 2001-12-13 14:23:22 eduard Exp $

	$Log: not supported by cvs2svn $
	Revision 1.16  2001/10/16 15:10:05  vpro
	Wilbert added property to set scale for colorizehex command to bridge different versions of ImageMagic
	
	Revision 1.15  2001/06/25 14:33:03  vpro
	Wilbert added filter(filtertype) option to select used resize filters
	
	Revision 1.14  2001/06/18 15:06:01  vpro
	Davzev: Added convert cmd colorizehex(rrggbb) in hex.
	
	Revision 1.13  2001/04/26 12:45:46  vpro
	Rico: major bug fix in Images, minor stuff in the rest
	
	Revision 1.12  2001/04/26 12:23:19  vpro
	Rico: major bug fix in Images, minor stuff in the rest
	
	Revision 1.11  2001/03/08 13:40:45  install
	Rob converted to new logging system
	
	Revision 1.10  2001/02/08 10:22:34  vpro
	Rico: zapped the old code that did the convert
	
	Revision 1.9  2001/02/08 10:20:39  vpro
	Rico: changed the processing by using a Threaded writer to fix the "half" image bug using code provided by Kees Jongenburg
	
	Revision 1.8  2001/01/26 15:21:32  install
	Rob turned debug off
	
	Revision 1.7  2001/01/26 14:58:08  install
	Rob added some features
	
	Revision 1.6  2000/11/14 11:41:57  eduard
	Eduard: Added a check in init(Hashtable params) to check if the ConverterRoot and ConverterCommand are existing,.. furhermore added some documentation
	
	Revision 1.5  2000/10/04 13:36:47  vpro
	Rico: added fix for transparancy parameters
	
	Revision 1.4  2000/08/06 16:02:43  daniel
	changed some debug
	
	Revision 1.3  2000/07/06 08:54:41  install
	Rico: added debug to see paths
	
	Revision 1.2  2000/06/08 18:00:11  wwwtech
	Rico: reduced/switched-off debug
	
	Revision 1.1  2000/06/02 10:57:49  wwwtech
	Rico: seperated conversion from the builder
	
*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * Converts Images using image magick.
 *
 * @author Rico Jansen
 * @version $Id: ConvertImageMagick.java,v 1.17 2001-12-13 14:23:22 eduard Exp $
 */
public class ConvertImageMagick implements ImageConvertInterface {
    private static Logger log = Logging.getLoggerInstance(ConvertImageMagick.class.getName());

	// Currenctly only ImageMagick works, this are the default value's
	private static String ConverterRoot = "/usr/local/"; 
	private static String ConverterCommand = "bin/convert";
	private static int colorizeHexScale = 100;

	/** This function initalises this class
	* @param params a <code>Hashtable</code> of <code>String</string>s containing informationn, this should contina the key's  
	*	ImageConvert.ConverterRoot and ImageConvert.ConverterCommand specifing the converter root....
	*/
	public void init(Hashtable params) {
		String tmp;
		tmp=(String)params.get("ImageConvert.ConverterRoot");
		if (tmp!=null) ConverterRoot = tmp;
		
		// now check if the specified ImageConvert.ConverterRoot does exist and is a directory
		File checkConvDir = new File(ConverterRoot);
		if(!checkConvDir.exists()) {
                    log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterRoot("+ConverterRoot+") does not exist");
                }
		if(!checkConvDir.isDirectory()) {
                    log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterRoot("+ConverterRoot+") is not a directory");
                }
		tmp=(String)params.get("ImageConvert.ConverterCommand");
		if (tmp!=null) ConverterCommand=tmp;
		
		// now check if the specified ImageConvert.Command does exist and is a file..
		String command = ConverterRoot + ConverterCommand;
		File checkConvCom = new File(command);
		if(!checkConvCom.exists()) {
                    log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterCommand("+ConverterCommand+"), "+command+" does not exist");

                }
		if(!checkConvCom.isFile()) {
                    log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterCommand("+ConverterCommand+"), "+command+" is not a file");

                }                
		if(!checkConvCom.canRead()) {
                    log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterCommand("+ConverterCommand+"), "+command+" is not readable");

                }
                // do a test-run, maybe slow during startup, but when it is done this way, we can also output some additional info in the log about version..
                // and when somebody has failure with converting images, it is much earlier detectable, when it wrong in settings, since it are settings of 
                // the builder... TODO: on error switch to jai????
		try {
                    log.debug("Starting convert");
		    Process process=Runtime.getRuntime().exec(command);
                    InputStream in = null;                    
		    in=process.getInputStream();
                    process.waitFor(); 
                    
                    ByteArrayOutputStream outputstream=new ByteArrayOutputStream();
                    byte[] inputbuffer=new byte[1024];
                    int size=0;
                    // well it should be mentioned on first line, that means no need to look much further...
                    while((size=in.read(inputbuffer)) > 0 ) {
                        outputstream.write(inputbuffer,0,size);
		    }
                    // make stringtokenizer, with nextline as new token..
                    StringTokenizer tokenizer = new StringTokenizer(outputstream.toString(),"\n\r");
                    if(tokenizer.hasMoreTokens()) {
                        log.info("Will use: "+command+", "+tokenizer.nextToken());
                    }
                    else {
                        log.error("converter from location "+command+", gave strange result: "+ outputstream.toString()+ "conv.root='"+ConverterRoot+"' conv.command='"+ConverterCommand+"'");
                    }
                    
		} catch (Exception e) {
                    log.error("images.xml(ConvertImageMagick): "+command+" could not be executed("+ e.toString() +")conv.root='"+ConverterRoot+"' conv.command='"+ConverterCommand+"'");
		}
                // Cant do more checking then this, i think....		
		tmp=(String)params.get("ImageConvert.ColorizeHexScale");
		if (tmp!=null) {
			try {
				colorizeHexScale = Integer.parseInt(tmp);
			}
			catch (NumberFormatException e) {
				log.error("Property ImageConvert.ColorizeHexScale should be an integer: "+e.toString()+ "conv.root='"+ConverterRoot+"' conv.command='"+ConverterCommand+"'");
			}
		}
		// no need for next lines anymore...
		// log.info("Root="+ConverterRoot);
		// log.info("Command="+ConverterCommand);
	}

	/** This functions converts an image by the given parameters
	* @param 	input an array of <code>byte</code> which represents the original image
	* @param 	commands a <code>Vector</code> of <code>String</code>s containing commands which are operations on the image which will be returned.		
	*	ImageConvert.ConverterRoot and ImageConvert.ConverterCommand specifing the converter root....
	*	@return an array of <code>byte</code>s containing the new converted image.
	*/
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
				log.debug("getCommands(): type="+type+" cmd="+cmd);
				if (type.equals("s")) {
					cmds.addElement("-geometry "+cmd);
				} else if (type.equals("quality")) {
					cmds.addElement("-quality "+cmd);
				} else if (type.equals("region")) {
					cmds.addElement("-region "+cmd);
				} else if (type.equals("spread")) {
					cmds.addElement("-spread "+cmd);
				} else if (type.equals("solarize")) {
					cmds.addElement("-solarize "+cmd);
				} else if (type.equals("r")) {
					cmds.addElement("-rotate "+cmd);
				} else if (type.equals("c")) {
					cmds.addElement("-colors "+cmd);
				} else if (type.equals("colorize")) {
					// not supported ?
					cmds.addElement("-colorize "+cmd);
				} else if (type.equals("colorizehex")) {
					// Incoming hex number rrggbb is converted to 
					// decimal values rr,gg,bb which are inverted on a scale from 0 to 100.
					log.debug("colorizehex, cmd: "+cmd);
					String hex = cmd;
					// Check if hex length is 123456 6 chars.
					if (hex.length()==6) {
						log.debug("Hex is :"+hex);
						// Byte.decode doesn't work correctly.
						int r = colorizeHexScale - Math.round(colorizeHexScale*Integer.parseInt(hex.substring(0,2),16)/255.0f);
						int g = colorizeHexScale - Math.round(colorizeHexScale*Integer.parseInt(hex.substring(2,4),16)/255.0f);
						int b = colorizeHexScale - Math.round(colorizeHexScale*Integer.parseInt(hex.substring(4,6),16)/255.0f);
						log.debug("Calling colorize with r:"+r+" g:"+g+" b:"+b);
						cmds.addElement("-colorize "+r+"/"+g+"/"+b);
					}
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
					cmds.addElement("-transparency #"+cmd.toLowerCase()+"");
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
				} else if (type.equals("filter")) {
					cmds.addElement("-filter "+cmd);
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
		InputStream in;
		Runtime runtime=Runtime.getRuntime();
		String command="";
		Process p;
		ByteArrayOutputStream imagestream;
		byte[] inputbuffer=new byte[2048],image=null;
		int size;
		ProcessWriter pw;
		
		log.info("ConvertImage(): converting img("+cmd+")");

		try {
			command=ConverterRoot+ConverterCommand+" - "+cmd+" "+format+":-";
			log.debug("Starting convert");
			p=runtime.exec(command);
			in=p.getInputStream();
			pw=new ProcessWriter(new ByteArrayInputStream(pict),p.getOutputStream());
			log.debug("Starting writer");
			pw.start();

			imagestream=new ByteArrayOutputStream();
			size=0;
			log.debug("Reading image");
			while((size=in.read(inputbuffer))>0) {
				log.debug("Reading data size "+size);
				imagestream.write(inputbuffer,0,size);
			}
			log.debug("Done converting"); 
			image=imagestream.toByteArray();
			log.debug("Returning Image"); 
		} catch (Exception e) {
			log.error("Failure converting image "+cmd+" "+format);
			log.error("Message : "+e.getMessage());
		}
		return(image);
	}
}
