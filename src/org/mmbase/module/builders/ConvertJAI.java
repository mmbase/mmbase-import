/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
	$Id: ConvertJAI.java,v 1.1 2000-08-20 00:29:29 daniel Exp $

	$Log: not supported by cvs2svn $
	
*/
package org.mmbase.module.builders;


import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.*;
import java.util.*;
import java.io.*;
import com.sun.media.jai.codec.*;

import org.mmbase.util.*;

/**
 *
 * Converts Images using image Java Advanced Imaging
 *
 * @author Daniel Ockeloen
 * @version $Id: ConvertJAI.java,v 1.1 2000-08-20 00:29:29 daniel Exp $
 */
public class ConvertJAI implements ImageConvertInterface {

	private String classname = getClass().getName();
	private boolean debug = true;
	private void debug(String msg) { System.out.println(classname+":"+msg); }

	public void init(Hashtable params) {
		/*
		String tmp;
		tmp=(String)params.get("ImageConvert.ConverterRoot");
		if (tmp!=null) ConverterRoot=tmp;
		tmp=(String)params.get("ImageConvert.ConverterCommand");
		if (tmp!=null) ConverterCommand=tmp;
		if (debug) debug("Root="+ConverterRoot);
		if (debug) debug("Command="+ConverterCommand);
		*/
		System.out.println("INIT JAI convertor");
	}

	public byte[] ConvertImage(byte[] input,Vector commands) {	
		String cmd,format;
		byte[] pict=null;

		try {
		ByteArraySeekableStream bin=new ByteArraySeekableStream(input);
		PlanarImage img = JAI.create("stream",bin);

		img=doConvertCommands(img,commands);

		//img = size(img,100,100);
		//img = scale(img,0.3F,0.4F);
		//img = rotate(img,0,0,30);

		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		JAI.create("encode", img, bout,"JPEG",null);	
		pict=bout.toByteArray();
		
		} catch(Exception e) {
			e.printStackTrace();
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

	private PlanarImage doConvertCommands(PlanarImage img,Vector params) {
		StringBuffer cmdstr=new StringBuffer();
		String key,cmd,type;
		int pos,pos2;

		for (Enumeration t=params.elements();t.hasMoreElements();) {
			key=(String)t.nextElement();
			pos=key.indexOf('(');
			pos2=key.lastIndexOf(')');
			if (pos!=-1 && pos2!=-1) {
				type=key.substring(0,pos);
				cmd=key.substring(pos+1,pos2);
				StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
				if (debug) debug("getCommands(): type="+type+" cmd="+cmd);
				if (type.equals("s")) {
					try {
						int x=Integer.parseInt(tok.nextToken());
						int y=x;
						if (tok.hasMoreTokens()) {
							y=Integer.parseInt(tok.nextToken());
						}
						img = size(img,x,y);
					} catch(Exception e) {}
				} else if (type.equals("r")) {
					try {
						int a=Integer.parseInt(tok.nextToken());
						img = rotate(img,0,0,a);
					} catch(Exception e) {}
				} else if (type.equals("c")) {
				} else if (type.equals("colorize")) {
				} else if (type.equals("bordercolor")) {
				} else if (type.equals("blur")) {
				} else if (type.equals("edge")) {
				} else if (type.equals("implode")) {
				} else if (type.equals("gamma")) {
				} else if (type.equals("border")) {
				} else if (type.equals("pen")) {
				} else if (type.equals("font")) {
				} else if (type.equals("circle")) {
				} else if (type.equals("text")) {
				} else if (type.equals("raise")) {
				} else if (type.equals("shade")) {
				} else if (type.equals("modulate")) {
				} else if (type.equals("colorspace")) {
				} else if (type.equals("shear")) {
				} else if (type.equals("swirl")) {
				} else if (type.equals("wave")) {
				} else if (type.equals("t")) {
				} else if (type.equals("part")) {
				} else if (type.equals("roll")) {
				} else if (type.equals("i")) {
                		} else if (type.equals("q")) {
				}
			} else {
				if (key.equals("mono")) {
				} else if (key.equals("contrast")) {
				} else if (key.equals("lowcontrast")) {
				} else if (key.equals("highcontrast")) {
				} else if (key.equals("noise")) {
				} else if (key.equals("emboss")) {
				} else if (key.equals("flipx")) {
				} else if (key.equals("flipx")) {
				} else if (key.equals("flipy")) {
				} else if (key.equals("dia")) {
				} else if (key.equals("neg")) {
				}
			}
		}
		return(img);
	}
	


	public static PlanarImage scale(PlanarImage inImg,float sx,float sy) {
		//Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
		Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
		ParameterBlock params = new ParameterBlock();
		params.addSource(inImg);
		params.add((float)sx);         // x scale
		params.add((float)sy);         // y scale
		params.add(0F);         // x trans
		params.add(0F);         // y trans
		params.add(interp);       // interpolation method
		PlanarImage outImg = JAI.create("scale", params);
		return(outImg);
	}

	public static PlanarImage size(PlanarImage inImg,int x,int y) {
		//Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
		Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
		int curx=inImg.getWidth();
		int cury=inImg.getHeight();
		
		float sx=((float)x/curx);
		float sy=((float)y/cury);

		ParameterBlock params = new ParameterBlock();
		params.addSource(inImg);
		params.add((float)sx);         // x scale
		params.add((float)sy);         // y scale
		params.add(0F);         // x trans
		params.add(0F);         // y trans
		params.add(interp);       // interpolation method
		PlanarImage outImg = JAI.create("scale", params);
		return(outImg);
	}

	public static PlanarImage rotate(PlanarImage inImg,int x,int y,int a) {
		//Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_BILINEAR);
		Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
		ParameterBlock params = new ParameterBlock();
		params.addSource(inImg);
		params.add((float)x);         // x org
		params.add((float)y);         // y org
		params.add(getDeg2Rad((float)(a)));        // angle 
		params.add(interp);       // interpolation method
		PlanarImage outImg = JAI.create("rotate", params);
		return(outImg);
	}


	public static PlanarImage loadImage(String filename) {
		ParameterBlock pb = new ParameterBlock();
		pb.add(filename);
		PlanarImage image = JAI.create("fileload",pb);
		if (image==null) {
			System.out.println("Can't load image");
		}
		return(image);
	}


	public static float getDeg2Rad(float deg) {
		return((float)((3.14*2)/360)*deg);
	}

	private int getOneInt(String cmd) {
		try {
			return(Integer.parseInt(cmd));	
		} catch(Exception e) {
			return(0);
		}
	}


}
