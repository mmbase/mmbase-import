/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: Images.java,v 1.15 2000-06-02 10:57:48 wwwtech Exp $

	$Log: not supported by cvs2svn $
	Revision 1.10  2000/04/05 11:52:16  wwwtech
	Rico: added debug, so you can see you need to load icaches as well
	
	Revision 1.9  2000/03/30 14:15:04  wwwtech
	Rico: added static string to reference the Path to the converter as per suggestion of Arjen de Vries, NOTE that this has to be configurable instead of static like this
	
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

import org.mmbase.module.builders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 *
 * images holds the images and provides ways to insert, retract and
 * search on them.
 *
 * @author Daniel Ockeloen, Rico Jansen
 * @version $Id: Images.java,v 1.15 2000-06-02 10:57:48 wwwtech Exp $
 */
public class Images extends MMObjectBuilder {

	private String classname = getClass().getName();
	private boolean debug = true;

	ImageConvertInterface imageconvert=null;
	Hashtable ImageConvertParams=new Hashtable();

	// Currenctly only ImageMagick works / this gets parameterized soon
	protected static String ImageConvertClass="org.mmbase.module.builders.ConvertImageMagick";
	protected static String ConverterRoot = "/usr/local/";
	protected static String ConverterCommand = "bin/convert";


	public boolean init() {
		super.init();
		/* Wait for builder property support
		String tmp;
		tmp=getParameter("ImageConvertClass");
		if (tmp!=null) ImageConvertClass=tmp;
		loadImageConvertParams(getParameters());
		*/

		// HACK remove when above comes true
		ImageConvertParams.put("ImageConvert.ConverterRoot",ConverterRoot);
		ImageConvertParams.put("ImageConvert.ConverterCommand",ConverterCommand);

		imageconvert=loadImageConverter(ImageConvertClass);
		imageconvert.init(ImageConvertParams);
		return(true);
	}

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

	private void getImageConvertParams(Hashtable params) {
		String key;
		for (Enumeration e=params.keys();e.hasMoreElements();) {
			key=(String)e.nextElement();
			if (key.startsWith("ImageConvert.")) {
				ImageConvertParams.put(key,params.get(key));
			}
		}
	}

	private ImageConvertInterface loadImageConverter(String classname) {
		Class cl;
		ImageConvertInterface ici=null;

		try {
			cl=Class.forName(classname);
			ici=(ImageConvertInterface)cl.newInstance();
			debug("loadImageConverter(): loaded : "+classname);
		} catch (Exception e) {
			debug("loadImageConverter(): can't load : "+classname);
		}
		return(ici);
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
		if (debug) debug("getImageMimeType: mmb.getMimeType("+format+") = "+mimetype);
		
		return(mimetype);
	}


	// glue method until org.mmbase.servlet.servdb is updated
	public byte[] getImageBytes5(Vector params) {
		return getImageBytes5(null,params);
	}

	// glue method until org.mmbase.servlet.servdb is updated
	public byte[] getImageBytes5(scanpage sp,Vector params) {
		return ConvertImage(sp,params);
	}

	public String convertAlias(String num) {
		// check if its a number if not check for name
		int number=-1;
		try {
			number=Integer.parseInt(num);
		} catch(NumberFormatException e) {
			Enumeration g=search("WHERE title='"+num+"'");
			while (g.hasMoreElements()) {
				MMObjectNode imgnode=(MMObjectNode)g.nextElement();
				number=imgnode.getIntValue("number");
			}
		}	
		return(""+number);
	}

	public byte[] ConvertImage(scanpage sp,Vector params) {
		String ckey="",key;
		byte[] picture=null;
		int number=-1;

		if (params!=null && params.size()==0) {
	
			String num=(String)params.elementAt(0);

			num=convertAlias(num);
			number=Integer.parseInt(num);
				
			if (number>=0) {
				// flatten parameters as a 'hashed' key;
				ckey=""+number;
				for (Enumeration t=params.elements();t.hasMoreElements();) {
					key=(String)t.nextElement();
					ckey+=key;
				}
			
				ImageCaches bul=(ImageCaches)mmb.getMMObject("icaches");
				if (bul!=null) {
					picture=bul.getCkeyNode(ckey);
					if (picture==null) {
						MMObjectNode node;
						node=getNode(number);
						if (node!=null) {
							byte[] inputpicture=node.getByteValue("handle");
							if (inputpicture!=null) {
								picture=imageconvert.ConvertImage(inputpicture,params);
								if (picture!=null) {
									MMObjectNode newnode=bul.getNewNode("system");
									newnode.setValue("ckey",ckey);
									newnode.setValue("id",number);
									newnode.setValue("handle",picture);
									newnode.setValue("filesize",picture.length);
									newnode.insert("imagesmodule");
								} else {
									debug("ConvertImage(): Convert problem params : "+params);
								}
							} else {
								debug("ConvertImage: Image Node is bad "+number);
							}
						} else {
							debug("ConvertImage: Image node not found "+number);
						}
					} else {
						// We are done ImageCache HIT
					}
				} else {
					debug("ConvertImage(): ERROR builder icaches not loaded, load it by putting it in objects.def");
				}
			} else {
				debug("ConvertImage: Parameter is not a valid image "+num);
			}
		} else {
			debug("ConvertImage(): no parameters");
		}
		return(picture);
	}
}
		
