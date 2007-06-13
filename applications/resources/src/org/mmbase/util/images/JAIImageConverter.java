/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.*;
import javax.media.jai.operator.*;
import java.awt.image.IndexColorModel;
import java.awt.Transparency;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import com.sun.media.jai.codec.*;

import org.mmbase.util.logging.*;

/**
 * Converts Images using image Java Advanced Imaging
 *
 * @author Daniel Ockeloen
 * @author Martijn Houtman (JAI fix)
 * @version $Id: JAIImageConverter.java,v 1.3 2007-06-13 18:54:55 nklasens Exp $
 */
public class JAIImageConverter extends AbstractImageConverter implements ImageConverter {

    private static final Logger log = Logging.getLoggerInstance(JAIImageConverter.class);

    /**
     * @javadoc
     */
    public void init(Map<String,String> params) {
        log.info("Starting JAI convertor");
    }


    /**
     * @javadoc
     */
    public byte[] convertImage(byte[] input, String sourceFormat, List<String> commands) {
        String format;
        byte[] pict=null;
        try {
            ByteArraySeekableStream bin = new ByteArraySeekableStream(input);
            PlanarImage img = JAI.create("stream", bin);

            // determine outputformat
            format = getConvertFormat(commands);
            if (format.equals("asis")) format = sourceFormat;

            // correct for gif transparency
            if (! (format.equals("gif") || format.equals("png")) &&
                img.getColorModel()instanceof IndexColorModel) {
              IndexColorModel icm = (IndexColorModel) img.getColorModel();
              if (icm.getTransparency() == Transparency.BITMASK) {
                byte[][] data = new byte[3][icm.getMapSize()];
                icm.getReds(data[0]);
                icm.getGreens(data[1]);
                icm.getBlues(data[2]);
                LookupTableJAI lut = new LookupTableJAI(data);
                img = JAI.create("lookup", img, lut);
                log.debug("palette-color image converted to RGB");
              }
            }

            img = doConvertCommands(img, commands);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            JAI.create("encode", img, bout, format, null);
            pict = bout.toByteArray();
        } catch(Exception e) {
            log.error(e);
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
    private String getConvertFormat(List<String> params) {
        String format = null;

        for (String key : params) {
            int pos  = key.indexOf('(');
            int pos2 = key.lastIndexOf(')');
            if (pos!=-1 && pos2!=-1) {
                String type = key.substring(0, pos);
                String cmd  = key.substring(pos + 1, pos2);
                if (type.equals("f")) {
                    if (! (cmd.equals("asis") && format != null)) {
                        format = cmd.toLowerCase();
                    }
                    break;
                }
            }
        }
        if (format == null) format = Factory.getDefaultImageFormat();
        // fix jpg format name
        if (format.equals("jpg")) format="jpeg";
        return format;
    }
    /**
     * @javadoc
     */
    private PlanarImage doConvertCommands(PlanarImage img, List<String> params) {
        Iterator<String> t = params.iterator();
        while (t.hasNext()) {
            try {
                String key = t.next();
                int pos = key.indexOf('(');
                int pos2 = key.lastIndexOf(')');
                if (pos!=-1 && pos2 != -1) {
                    String type = key.substring(0, pos);
                    String cmd = key.substring(pos + 1, pos2);
                    String[] tokens = cmd.split("[x,\\n\\r]");
                    if (log.isDebugEnabled()) {
                        log.debug("getCommands(): type=" + type + " cmd=" + cmd);
                        log.debug("Image is now " + img.getWidth() + "x" + img.getHeight());
                        log.debug(" or " + img.getMinX() + "-" + img.getMaxX() + ".." + img.getMinY() + "-" + img.getMaxY());
                    }
                    // Following code translates some MMBase specific things
                    // to imagemagick's convert arguments.
                    // using this conversion ensures compatibility between systems
                    type = Imaging.getAlias(type);
                    if (type.equals("geometry")) {
                        String xString = tokens.length > 0 ? tokens[0] : "";
                        String yString = tokens.length > 1 ? tokens[1] : "";

                        Matcher matchX = Imaging.GEOMETRY.matcher(xString);
                        xString = matchX.matches() ? matchX.group(1) : "";
                        Matcher matchY = Imaging.GEOMETRY.matcher(yString);
                        yString = matchY.matches() ? matchY.group(1) : "";

                        String options = (matchX.matches() ? matchX.group(2) : "") + (matchY.matches() ? matchY.group(2) : "");

                        boolean aspectRatio = true;
                        boolean area        = false;
                        boolean percentage  = false;
                        boolean onlyWhenOneBigger    = false;
                        boolean onlyWhenBothSmaller   = false;

                        for (int j = 0 ; j < options.length(); j++) {
                            char o = options.charAt(j);
                            if (o == '%') percentage = true;
                            if (o == '@') area = true;
                            if (o == '!') aspectRatio = false;
                            if (o == '>') onlyWhenOneBigger = true;
                            if (o == '<') onlyWhenBothSmaller = true;
                        }

                        int x = "".equals(xString) ? 0 : Integer.parseInt(xString);
                        int y = "".equals(yString) ? 0 : Integer.parseInt(yString);

                        if (x == 0) {
                            x = Math.round((float) img.getWidth() * y / img.getHeight());
                        }
                        if (area) {
                            float a = x;
                            if (img.getWidth() * img.getHeight() > a) {
                                float ratio = (float) img.getWidth() / img.getHeight();;
                                x = (int) Math.floor(Math.sqrt(a * ratio));
                                y = (int) Math.floor(Math.sqrt(a / ratio));
                            } else {
                                x = img.getWidth();
                                y = img.getHeight();
                            }
                        }

                        if (y == 0) {
                            y = Math.round((float) img.getHeight() * x / img.getWidth());
                        }

                        if (percentage) {
                            x = img.getWidth() * x / 100;
                            y = img.getHeight() * y / 100;
                            aspectRatio = false;
                        }

                        boolean skipScale =
                            (onlyWhenOneBigger &&  img.getWidth() < x &&  img.getHeight() < y) ||
                            (onlyWhenBothSmaller && (img.getWidth() > x || img.getHeight() > y));

                        img = skipScale ? img : size(img, x, y, aspectRatio);
                    } else if (type.equals("border")) {
                        int x = Integer.parseInt(tokens[0]);
                        int y = tokens.length > 1 ? Integer.parseInt(tokens[1]) : x;
                        img = border(img, x ,y);
                    } else if (type.equals("rotate")) {
                        int a = Integer.parseInt(tokens[0]);
                        img = rotate(img, 0, 0,a);
                    } else if (type.equals("part")) {
                        int x1 = Integer.parseInt(tokens[0]);
                        int y1 = Integer.parseInt(tokens[1]);
                        int x2 = Integer.parseInt(tokens[2]);
                        int y2 = Integer.parseInt(tokens[3]);
                        if (x2 > img.getWidth())  x2 = img.getWidth();
                        if (y2 > img.getHeight()) y2 = img.getHeight();
                        if (x1 > x2) x1 = x2 - 1;
                        if (y1 > y2) y1 = y2 - 1;
                        img = crop(img, x1, y1, x2, y2);
                    }
                } else {
                    if (key.equals("negate")) {
                        img = negate(img);
                    } else if (key.equals("border")) {
                        img = border(img, 1, 1);
                    } else if (key.equals("flop")) {
                        img = flop(img);
                    } else if (key.equals("flip")) {
                        img = flip(img);
                    }
                }
            } catch(Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return img;
    }

    /**
     * @javadoc
     */
    protected static PlanarImage crop(PlanarImage inImg, int x1, int y1, int x2,int y2) {
        Interpolation interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add((float)x1);         // x
        params.add((float)y1);         // y
        params.add((float)(x2 - x1));    // width
        params.add((float)(y2 - y1));    // height
        params.add(interp);       // interpolation method
        PlanarImage outImg = JAI.create("crop", params);
        return outImg;
    }

    /**
     * Performs the 's' or 'geometry' operation:
     * Resize an image using specified width and height.
     * @param inImg the image to transform
     * @param width new width of the image
     * @param height new height of the image
     * @param maintainAspectRation if true, width and height are maximums: aspect ratio is maintained
     * @return the transformed image
     */
    protected static PlanarImage size(PlanarImage inImg, int width, int height, boolean maintainAspectRation) {
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
        params.add(sx);  // x scale
        params.add(sy);  // y scale
        params.add(0F);         // x trans
        params.add(0F);         // y trans
        params.add(interp);     // interpolation method
        PlanarImage outImg = JAI.create("scale", params);
        return outImg;
    }

    /**
     * Performs the 'dia' or 'negate' operation.
     * Replace every pixel with its complementary color (white becomes black, yellow becomes blue, etc.)
     * @param inImg the image to transform
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
     * @param inImg the image to transform
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
        params.add((float)Math.toRadians((double)(a)));        // angle
        params.add(interp);       // interpolation method
        PlanarImage outImg = JAI.create("rotate", params);
        return outImg;
    }

    /**
     * Performs the 'border' operation:
     * Adds a border to an image using the specified horizontal and vertical padding.
     * @param inImg the image to transform
     * @param xpadding the horizontal padding
     * @param ypadding the vertical padding
     * @return the transformed image
     */
    protected static PlanarImage border(PlanarImage inImg, int xpadding,int ypadding) {
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(xpadding);         // leftPad
        params.add(xpadding);         // rightPad
        params.add(ypadding);         // topPad
        params.add(ypadding);         // bottomPad
        params.add(BorderExtender.BORDER_ZERO); // type
        params.add(null);         // constants (ignored)

        PlanarImage outImg = JAI.create("border", params);
        return outImg;
    }

    /**
     * Performs the 'flop' or 'flipx' operation:
     * Flip an image across an imaginary horizontal line that runs through the center of the image.
     * @param inImg the image to transform
     * @return the transformed image
     */
    protected static PlanarImage flop(PlanarImage inImg) {
        ParameterBlock params = new ParameterBlock();
        params.addSource(inImg);
        params.add(TransposeDescriptor.FLIP_HORIZONTAL);         // flip over X
        PlanarImage outImg = JAI.create("transpose", params);
        return outImg;
    }

    /**
     * Performs the 'flip' or 'flipy' operation:
     * Flip an image across an imaginary vertical line that runs through the center of the image.
     * @param inImg the image to transform
     * @return the transformed image
     */
    protected static PlanarImage flip(PlanarImage inImg) {
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


}
