/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.*;
import java.util.*;
import java.io.*;
import com.sun.media.jai.codec.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * Converts Images using image Java Advanced Imaging
 *
 * @author Daniel Ockeloen
 * @version $Id: ConvertJAI.java,v 1.7 2002-02-12 19:30:42 michiel Exp $
 */
public class ConvertJAI implements ImageConvertInterface {
    
    private static Logger log = Logging.getLoggerInstance(ConvertJAI.class.getName());
    
    public void init(Hashtable params) {
        log.info("Starting JAI convertor");
    }
    

    public byte[] ConvertImage(byte[] input,Vector commands) {
        return convertImage(input, commands);
    }
    public byte[] convertImage(byte[] input,Vector commands) {	
        String cmd,format;
        byte[] pict=null;
	
        try {
            ByteArraySeekableStream bin=new ByteArraySeekableStream(input);
            PlanarImage img = JAI.create("stream",bin);
	    
            img = doConvertCommands(img,commands);
	    
            //img = size(img,100,100);
            //img = scale(img,0.3F,0.4F);
            //img = rotate(img,0,0,30);
	    
            ByteArrayOutputStream bout=new ByteArrayOutputStream();
            JAI.create("encode", img, bout,"JPEG",null);	
            pict=bout.toByteArray();		    
        } 
        catch(Exception e) {
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
                log.debug("getCommands(): type="+type+" cmd="+cmd);
                if (type.equals("s")) {
                    try {
                        int x=Integer.parseInt(tok.nextToken());
                        if (tok.hasMoreTokens()) {
                            int y=Integer.parseInt(tok.nextToken());
                            img = size(img,x,y);
                        } 
                        else {
                            img = size(img,x);
                        }
                    } 
                    catch(Exception e) {
                        log.error(e.getMessage());
                        log.error(Logging.stackTrace(e));
                    }
                } 
                else if (type.equals("r")) {
                    try {
                        int a=Integer.parseInt(tok.nextToken());
                        img = rotate(img,0,0,a);
                    } 
                    catch(Exception e) {
                        log.error(e.getMessage());
                        log.error(Logging.stackTrace(e));                
                    }
                } 
                else if (type.equals("c")) {
                } 
                else if (type.equals("colorize")) {
                } 
                else if (type.equals("bordercolor")) {
                } 
                else if (type.equals("blur")) {
                } 
                else if (type.equals("edge")) {
                } 
                else if (type.equals("implode")) {
                } 
                else if (type.equals("gamma")) {
                } 
                else if (type.equals("border")) {
                } 
                else if (type.equals("pen")) {
                } 
                else if (type.equals("font")) {
                } 
                else if (type.equals("circle")) {
                } 
                else if (type.equals("text")) {
                } 
                else if (type.equals("raise")) {
                } 
                else if (type.equals("shade")) {
                } 
                else if (type.equals("modulate")) {
                } 
                else if (type.equals("colorspace")) {
                } 
                else if (type.equals("shear")) {
                } 
                else if (type.equals("swirl")) {
                } 
                else if (type.equals("wave")) {
                } 
                else if (type.equals("t")) {
                } 
                else if (type.equals("part")) {
                    try {
                        int x1=Integer.parseInt(tok.nextToken());
                        int y1=Integer.parseInt(tok.nextToken());
                        int x2=Integer.parseInt(tok.nextToken());
                        int y2=Integer.parseInt(tok.nextToken());
                        img = crop(img,x1,y1,x2,y2);
                    } 
                    catch (Exception e) { 
                        log.error(e.getMessage());
                        log.error(Logging.stackTrace(e));
                    }
                } 
                else if (type.equals("roll")) {
                } 
                else if (type.equals("i")) {
                } 
                else if (type.equals("q")) {
                }
            } 
            else {
                if (key.equals("mono")) {
                } 
                else if (key.equals("contrast")) {
                } 
                else if (key.equals("lowcontrast")) {
                } 
                else if (key.equals("highcontrast")) {
                } 
                else if (key.equals("noise")) {
                } 
                else if (key.equals("emboss")) {
                } 
                else if (key.equals("flipx")) {
                    img = flipx(img);
                } 
                else if (key.equals("flipy")) {
                    img = flipy(img);
                } 
                else if (key.equals("dia")) {
                } 
                else if (key.equals("neg")) {
                }
            }
        }
        return img;
    }
	


    public static PlanarImage scale(PlanarImage inImg,float sx,float sy) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add((float)sx);         // x scale
        params.add((float)sy);         // y scale
        params.add(0F);         // x trans
        params.add(0F);         // y trans
        params.add(interp);       // interpolation method
        PlanarImage outImg = JAI.create("scale", params);
        return outImg;
    }


    public static PlanarImage crop(PlanarImage inImg,int x1,int y1, int x2,int y2) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add((float)x1);         // x 
        params.add((float)y2);         // y 
        params.add((float)(x2-x1));    // width
        params.add((float)(y2-y1));    // height
        params.add(interp);       // interpolation method
        PlanarImage outImg = JAI.create("crop", params);
        return outImg;
    }

    /**
     * @param inImg the input image
     * @param max the maximum size of the image x and y in pixels
     * @return an image that will fit in max*max. The aspect ratio is not changed
     **/
    public static PlanarImage size(PlanarImage inImg,int max) {
        int curx=inImg.getWidth();
        int cury=inImg.getHeight();
        float sx=((float)max/curx);
        float sy=((float)max/cury);
        // the original image is 100x200
        // the requested size = 50
        // sx -> 50 / 100 = 0.5
        // sy -> 50 / 200 = 0.25
        // so y needs to be scaled more since we whant the picture
        // to fit in a box we will scale with the lowest value (.25 =  1/4 = scale 4 time)
        float scale = 0;
        if (sy < sx ){
            scale = sy;
        } 
        else {
            scale = sx;
        }
        return size(inImg,new Float(curx * scale).intValue(),new Float(cury * scale).intValue());

    }
    
    public static PlanarImage size(PlanarImage inImg,int x,int y) {
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
        return outImg;
    }

    public static PlanarImage rotate(PlanarImage inImg,int x,int y,int a) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add((float)x);         // x org
        params.add((float)y);         // y org
        params.add(getDeg2Rad((float)(a)));        // angle 
        params.add(interp);       // interpolation method
        PlanarImage outImg = JAI.create("rotate", params);
        return outImg;
    }


    public static PlanarImage flipx(PlanarImage inImg) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(1);         // flip over X
        PlanarImage outImg = JAI.create("transpose", params);
        return outImg;
    }


    public static PlanarImage flipy(PlanarImage inImg) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(0);         // flip over X
        PlanarImage outImg = JAI.create("transpose", params);
        return outImg;
    }


    public static PlanarImage loadImage(String filename) {
        ParameterBlock pb = new ParameterBlock();
        pb.add(filename);
        PlanarImage image = JAI.create("fileload",pb);
        if (image==null) {
            log.warn("Can't load image");
        }
        return image;
    }


    public static float getDeg2Rad(float deg) {
        return (float)((3.14*2)/360)*deg;
    }

    private int getOneInt(String cmd) {
        try {
            return(Integer.parseInt(cmd));	
        } catch(Exception e) {
            return 0;
        }
    }
}
