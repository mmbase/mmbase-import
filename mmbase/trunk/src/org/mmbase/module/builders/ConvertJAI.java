/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.util.*;
import java.io.*;
import com.sun.media.jai.codec.*;

import org.mmbase.util.logging.*;

/**
 * Converts Images using image Java Advanced Imaging
 *
 * @author Daniel Ockeloen
 * @version $Id: ConvertJAI.java,v 1.9 2003-03-04 14:12:19 nico Exp $
 */
public class ConvertJAI implements ImageConvertInterface {

    private static Logger log = Logging.getLoggerInstance(ConvertJAI.class.getName());

    /**
     * The default image format.
     */
    protected String defaultImageFormat="jpeg";

    /**
     * @javadoc
     */
    public void init(Map params) {
        log.info("Starting JAI convertor");
    }

    /**
     * @javadoc
     */
    public byte[] ConvertImage(byte[] input,List commands) {
        return convertImage(input, commands);
    }

    /**
     * @javadoc
     */
    public byte[] convertImage(byte[] input,List commands) {
        String cmd,format;
        byte[] pict=null;
        try {
            ByteArraySeekableStream bin=new ByteArraySeekableStream(input);
            PlanarImage img = JAI.create("stream",bin);


            // determine outputformat
            format=getConvertFormat(commands);

            img = doConvertCommands(img,commands);

            ByteArrayOutputStream bout=new ByteArrayOutputStream();

            JAI.create("encode", img, bout,format,null);
            pict=bout.toByteArray();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return pict;
    }

    /**
     * Obtains the image format from the parameters list.
     * Note that JAI is not likely to produce outputs
     * for GIF (due to the license).
     * @param params the list of conversion paarmeters
     * @return the specified format, or the default image format is unspecified
     */
    private String getConvertFormat(List params) {
        String format=defaultImageFormat,key,cmd,type;
        int pos,pos2;

        for (Iterator t=params.iterator();t.hasNext();) {
            key=(String)t.next();
            pos=key.indexOf('(');
            pos2=key.lastIndexOf(')');
            if (pos!=-1 && pos2!=-1) {
                type=key.substring(0,pos);
                cmd=key.substring(pos+1,pos2);
                if (type.equals("f")) {
                    format=cmd.toLowerCase();
                    break;
                }
            }
        }
        // fix jpg format name
        if (format.equals("jpg")) format="jpeg";
        return format;
    }

    /**
     * MMBase has some abreviations to convert commands, like 's' for 'geometry'. These are treated here.
     * @param a alias
     * @return actual convert parameter name for alias.
     */
    protected String getAlias(String a) {
        if (a.equals("s"))            return "geometry";
        if (a.equals("r"))            return "rotate";
        if (a.equals("c"))            return "colors";
        if (a.equals("t"))            return "transparent";
        if (a.equals("i"))            return "interlace";
        if (a.equals("q"))            return "quality";
        if (a.equals("mono"))         return "monochrome";
        if (a.equals("highcontrast")) return "contrast";
        if (a.equals("flipx"))        return "flop";
        if (a.equals("flipy"))        return "flip";
        // I don't think that this makes any sense, I dia is not dianegative,
        // can be diapositive as well... But well, we are backwards compatible.
        if (a.equals("dia"))          return "negate";
        return a;

    }

    /**
     * @javadoc
     */
    private PlanarImage doConvertCommands(PlanarImage img,List params) {
        StringBuffer cmdstr=new StringBuffer();
        String key,cmd,type;
        int pos,pos2;
        Iterator t = params.iterator();
        if (t.hasNext()) t.next(); // first element is the number, ignore it.
        while (t.hasNext()) {
            try {
                key=(String)t.next();
                pos=key.indexOf('(');
                pos2=key.lastIndexOf(')');
                if (pos!=-1 && pos2!=-1) {
                    type=key.substring(0,pos);
                    cmd=key.substring(pos+1,pos2);
                    StringTokenizer tok = new StringTokenizer(cmd,"x,\n\r");
                    if (log.isDebugEnabled()) {
                        log.debug("getCommands(): type=" + type + " cmd=" + cmd);
                    }
                    // Following code translates some MMBase specific things
                    // to imagemagick's convert arguments.
                    // using this conversion ensures compatibility between systems
                    type=getAlias(type);
                    if (type.equals("geometry")) {
                        boolean mar=true;
                        int x=Integer.parseInt(tok.nextToken());
                        int y=x;
                        if (tok.hasMoreTokens()) {
                            String ycmd=tok.nextToken();
                            if (ycmd.endsWith("!")) {
                                mar=false;
                                ycmd=ycmd.substring(0,ycmd.length()-1);
                            }
                            y=Integer.parseInt(ycmd);
                        }
                        img = size(img,x,y,mar);
                    } else if (type.equals("border")) {
                        int x=Integer.parseInt(tok.nextToken());
                        int y=x;
                        if (tok.hasMoreTokens()) {
                            y=Integer.parseInt(tok.nextToken());
                        }
                        img = border(img,x,y);
                    } else if (type.equals("rotate")) {
                        int a=Integer.parseInt(tok.nextToken());
                        img = rotate(img,0,0,a);
                    } else if (type.equals("part")) {
                        int x1=Integer.parseInt(tok.nextToken());
                        int y1=Integer.parseInt(tok.nextToken());
                        int x2=Integer.parseInt(tok.nextToken());
                        int y2=Integer.parseInt(tok.nextToken());
                        img = crop(img,x1,y1,x2,y2);
                    }
                } else {
                    if (key.equals("negate")) {
                        img = negate(img);
                    } else if (key.equals("border")) {
                        img = border(img,1,1);
                    } else if (key.equals("flop")) {
                        img = flop(img);
                    } else if (key.equals("flip")) {
                        img = flip(img);
                    }
                }
            } catch(Exception e) {
                log.error(e.getMessage());
                log.error(Logging.stackTrace(e));
            }
        }
        return img;
    }

    /**
     * @javadoc
     */
    protected static PlanarImage crop(PlanarImage inImg,int x1,int y1, int x2,int y2) {
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
     * Performs the 's' or 'geometry' operation:
     * Resize an image using specified width and height.
     * @param the image to transform
     * @param width new width of the image
     * @param height new height of the image
     * @param maintainAspectRation if true, width and height are maximums: aspect ratio is maintained
     * @return the transformed image
     */
    protected static PlanarImage size(PlanarImage inImg,int width,int height, boolean maintainAspectRation) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        int curwidth=inImg.getWidth();
        int curheight=inImg.getHeight();
        float sx=((float)width/curwidth);
        float sy=((float)height/curheight);
        if (maintainAspectRation) {
            // use the smallest scale if aspect ratio is to be maintained
            if (sy<sx) {
                sx=sy;
            } else {
                sy=sx;
            }
        }
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add((float)sx);  // x scale
        params.add((float)sy);  // y scale
        params.add(0F);         // x trans
        params.add(0F);         // y trans
        params.add(interp);     // interpolation method
        PlanarImage outImg = JAI.create("scale", params);
        return outImg;
    }

    /**
     * Performs the 'dia' or 'negate' operation.
     * Replace every pixel with its complementary color (white becomes black, yellow becomes blue, etc.)
     * @param the image to transform
     * @return the transformed image
     */
    protected static PlanarImage negate(PlanarImage inImg) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(interp);       // interpolation method
        PlanarImage outImg = JAI.create("invert", params);
        return outImg;
    }

    /**
     * Performs the 'rotate' operation: rotates an image.
     * @param the image to transform
     * @param x xposition of the rotation
     * @param y yposition of the rotation
     * @param a angle of the rotation
     * @return the transformed image
     */
    protected static PlanarImage rotate(PlanarImage inImg,int x,int y,int a) {
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

    /**
     * Performs the 'border' operation:
     * Adds a border to an image using the specified horizontal and vertical padding.
     * @param the image to transform
     * @param xpadding the horizontal padding
     * @param ypadding the vertical padding
     * @return the transformed image
     */
    protected static PlanarImage border(PlanarImage inImg, int xpadding,int ypadding) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(xpadding);         // leftPad
        params.add(xpadding);         // rightPad
        params.add(ypadding);         // topPad
        params.add(ypadding);         // bottomPad
        params.add(BorderDescriptor.BORDER_ZERO_FILL); // type
        params.add(null);         // constants (ignored)

        PlanarImage outImg = JAI.create("border", params);
        return outImg;
    }

    /**
     * Performs the 'flop' or 'flipx' operation:
     * Flip an image across an imaginary horizontal line that runs through the center of the image.
     * @param the image to transform
     * @return the transformed image
     */
    protected static PlanarImage flop(PlanarImage inImg) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(TransposeDescriptor.FLIP_HORIZONTAL);         // flip over X
        PlanarImage outImg = JAI.create("transpose", params);
        return outImg;
    }

    /**
     * Performs the 'flip' or 'flipy' operation:
     * Flip an image across an imaginary vertical line that runs through the center of the image.
     * @param the image to transform
     * @return the transformed image
     */
    protected static PlanarImage flip(PlanarImage inImg) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(TransposeDescriptor.FLIP_VERTICAL);         // flip over Y
        PlanarImage outImg = JAI.create("transpose", params);
        return outImg;
    }

    /**
     * @javadoc
     */
    protected static PlanarImage loadImage(String filename) {
        ParameterBlock pb = new ParameterBlock();
        pb.add(filename);
        PlanarImage image = JAI.create("fileload",pb);
        if (image==null) {
            log.warn("Can't load image");
        }
        return image;
    }

    /**
     * @javadoc
     */
    protected static float getDeg2Rad(float deg) {
        return (float)((3.14*2)/360)*deg;
    }

    /**
     * @javadoc
     */
    protected int getOneInt(String cmd) {
        try {
            return Integer.parseInt(cmd);
        } catch(Exception e) {
            return 0;
        }
    }
}
