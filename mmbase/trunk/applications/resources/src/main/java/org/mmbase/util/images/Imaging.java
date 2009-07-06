/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.*;
import java.util.regex.*;

import org.mmbase.util.IOUtil;
import org.mmbase.util.transformers.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

import java.io.*;

/**
 * Utilities related to Images.
 *
 * @author Michiel Meeuwissen
 */


public abstract class Imaging {

    private static final Logger log = Logging.getLoggerInstance(Imaging.class);

    public static final String FIELD_HANDLE  = "handle";
    public static final String FIELD_CKEY    = "ckey";

    private static final CharTransformer unicode = new UnicodeEscaper();

    /**
     * Returns the mimetype using ServletContext.getServletContext which returns the servlet context
     * @param ext A String containing the extension.
     * @return The mimetype.
     */
    public static String getMimeTypeByExtension(String ext) {
        return getMimeTypeByFileName("dummy." + ext);
    }

    public static String getMimeTypeByFileName(String fileName) {
        javax.servlet.ServletContext sx = org.mmbase.module.core.MMBaseContext.getServletContext();
        String mimeType = sx.getMimeType(fileName);
        if (mimeType == null) {
            log.warn("Can't find mimetype for a file with name '" + fileName + "'. Defaulting to text/html");
            log.warn(Logging.stackTrace());
            mimeType = "text/html";
        }
        return mimeType;
    }

    /**
     * MMBase has some abreviations to convert commands, like 's' for 'geometry'. These are treated here.
     * @param a alias
     * @return actual convert parameter name for alias.
     */
    public static String getAlias(String a) {
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

    private static final char NOQUOTING = '-';
    /**
     * Parses the 'image conversion template' to a List. I.e. it break
     * it up in substrings, with '+' delimiter. However a + char does
     * not count if it is somewhere between brackets (). Brackets nor
     * +-chars count if they are in quotes (single or double)
     *
     * @since MMBase-1.7
     */
    // @author michiel
    public static List<String> parseTemplate(String template) {
        if (log.isDebugEnabled()) log.debug("parsing " + template);
        List<String> params = new ArrayList<String>();
        if (template != null) {
            int bracketDepth = 0;
            char quoteState = NOQUOTING; // can be - (not in quote), ' or ".
            StringBuilder buf = new StringBuilder();

            int i = 0;
            while (i < template.length() && template.charAt(i) == '+') i++; // ignoring leading +'es (can sometimes be one)
            for (; i < template.length(); i++) {
                char c = template.charAt(i);
                switch(c) {
                case '\'':
                case '"':
                    if (quoteState == c) {
                        quoteState = NOQUOTING;
                    } else if (quoteState == NOQUOTING) {
                        quoteState = c;
                    }
                    break;
                case '(': if (quoteState == NOQUOTING) bracketDepth++; break;
                case ')': if (quoteState == NOQUOTING) bracketDepth--; break;
                case '+': // command separator
                    if (bracketDepth == 0  // ignore if between brackets
                        && quoteState == NOQUOTING // ignore if between quotes
                        ) {
                        removeSurroundingQuotes(buf);
                        params.add(buf.toString());
                        buf.setLength(0);
                        continue;
                    }
                    break;
                }

                buf.append(c);
            }
            if (bracketDepth != 0) log.warn("Unbalanced brackets in " + template);
            if (quoteState != NOQUOTING) log.warn("Unbalanced quotes in " + template);

            removeSurroundingQuotes(buf);
            if (! buf.toString().equals("")) params.add(buf.toString());
        }
        return params;
    }
    /**
     * Just a utitility function, used by the function above.
     * @since MMBase-1.7
     */
    protected static void removeSurroundingQuotes(StringBuilder buf) {
        // remove surrounding quotes --> "+contrast" will be changed to +contrast
        if (buf.length() >= 2 && (buf.charAt(0) == '"' || buf.charAt(0) == '\'') && buf.charAt(buf.length() - 1) == buf.charAt(0)) {
            buf.deleteCharAt(0);
            buf.deleteCharAt(buf.length() - 1);
        }
    }

    /**
     * Only used in legacy-support and perhaps debug code.
     */
    public static String unparseTemplate(List<String> params) {
        StringBuilder buf = new StringBuilder();
        Iterator<String> i = params.iterator();
        while (i.hasNext()) {
            buf.append(i.next());
            if (i.hasNext()) {
                buf.append('+');
            }
        }
        return buf.toString();
    }


    public static final Pattern GEOMETRY = Pattern.compile("(\\d*)([\\%\\!\\<\\>\\@]*)");

    /**
     * Predict the size of a image after converting it with the given parameters. This will not
     * actually trigger a conversion, but only calculate the dimension of the result, so only
     * involves some basic arithmetic and string-parsing.
     *
     * Most transformations which alter the dimension of an image are supported: geometry, border, rotate, part.
     *
     * Probably because of different rounding strategies, there is sometimes a difference of one or
     * two pixels beteen the prediction and/or the result of ImageMagick and/or JAI.
     * @return A reasonable prediction of the new dimension or <code>Dimension.UNDETERMINED</code> if that really is
     * not possible
     */
    public static Dimension predictDimension(Dimension originalSize, List<String> params) {

        Dimension dim = new Dimension(originalSize);
        for (String key : params) {
            int pos = key.indexOf('(');
            int pos2 = key.lastIndexOf(')');
            if (pos != -1 && pos2 != -1) {
                String type = key.substring(0, pos);
                String cmd = key.substring(pos + 1, pos2);
                String[] tokens = cmd.split("[x,\\n\\r]");
                if (log.isDebugEnabled()) {
                    log.debug("getCommands(): type=" + type + " cmd=" + cmd);
                }
                // Following code translates some MMBase specific things
                // to imagemagick's convert arguments.
                // using this conversion ensures compatibility between systems
                type = getAlias(type);
                if (type.equals("geometry")) {
                    String xString = tokens.length > 0 ? tokens[0] : "";
                    String yString = tokens.length > 1 ? tokens[1] : "";

                    Matcher matchX = GEOMETRY.matcher(xString);
                    xString = matchX.matches() ? matchX.group(1) : "";
                    Matcher matchY = GEOMETRY.matcher(yString);
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


                    if (percentage) {
                        x *= dim.x / 100.0;
                        y *= dim.y / 100.0;
                        aspectRatio = false;
                    }
                    if (x == 0) {
                        x = Math.round((float) dim.x * y / dim.y);
                    }

                    if (area) {
                        float a = x;
                        if (dim.getArea() > a) {
                            float ratio = (float) dim.x / dim.y;
                            x = (int) Math.floor(Math.sqrt(a * ratio));
                            y = (int) Math.floor(Math.sqrt(a / ratio));
                        } else {
                            x = dim.x;
                            y = dim.y;
                        }
                        aspectRatio = false; // simply copy this;
                    }

                    if (y == 0) {
                        y = Math.round((float) dim.y * x / dim.x);
                    }


                    boolean skipScale =
                        (onlyWhenOneBigger &&  dim.x < x &&  dim.y < y) ||
                        (onlyWhenBothSmaller && (dim.x > x || dim.y > y));

                    if (! skipScale) {
                        if (! aspectRatio) {
                            dim.x = x;
                            dim.y = y;
                        } else {
                            if ((float)dim.x/dim.y > (float)x / y) {
                                dim.y *= ((float) x / dim.x);
                                dim.x = x;
                            } else {
                                dim.x *= ((float) y / dim.y);
                                dim.y = y;
                            }
                        }
                    }
                } else if (type.equals("border")) {
                    int x = tokens.length > 0 ? Integer.parseInt(tokens[0]) : 0;
                    int y = tokens.length > 1 ? Integer.parseInt(tokens[1]) : x;
                    dim.x += 2 * x;
                    dim.y += 2 * y;
                } else if (type.equals("rotate")) {
                    double degrees = Double.parseDouble(tokens[0]);
                    double a = Math.toRadians(degrees); //
                    double xorg = dim.x;
                    double yorg = dim.y;
                    dim.x = (int) Math.round(Math.abs(Math.cos(a)) * xorg + Math.abs(Math.sin(a) * yorg));
                    dim.y = (int) Math.round(Math.abs(Math.sin(a)) * xorg + Math.abs(Math.cos(a) * yorg));

                } else if (type.equals("gravity")) {
                } else if (type.equals("chop")) {
                } else if (type.equals("shave")) {
                } else if (type.equals("crop")) {
                } else if (type.equals("part")) {
                    int x1 = Integer.parseInt(tokens[0]);
                    int y1 = Integer.parseInt(tokens[1]);
                    int x2 = Integer.parseInt(tokens[2]);
                    int y2 = Integer.parseInt(tokens[3]);
                    if (x2 > dim.x) x2 = dim.x;
                    if (y2 > dim.y) y2 = dim.y;
                    if (x1 > x2) x1 = x2;
                    if (y1 > y2) y1 = y2;
                    dim.x = x2 - x1;
                    dim.y = y2 - y1;
                }
            } else {
                // options without arguments

                if (key.equals("trim")) {
                    // This requires information about the content of the image
                    log.debug("Trimming makes the dimension impossible  to predict " + params);
                    return Dimension.UNDETERMINED;
                }
            }

        }
        return dim;
    }

    /**
     * Tries to predict the new file-size after conversion.  The result (in the current
     * implementation) is very unreliable, because it assumes that the file-size is proportional to the area.
     */
    public static int predictFileSize(Dimension originalDimension, int originalFileSize, Dimension predictedDimension) {
        // hard, lets guess that the file-size is proportional to the area of an image.
        return originalFileSize * predictedDimension.getArea() / originalDimension.getArea();


    }

    /**
     * Parses a ckey String into a CKey structure.
     */
    public static CKey parseCKey(String ckey) {
        int pos = 0;
        ckey = unicode.transformBack(ckey);
        while (Character.isDigit(ckey.charAt(pos))) pos ++;
        return new CKey(Integer.parseInt(ckey.substring(0, pos)), ckey.substring(pos));
    }

    /**
     * Structure with node-number and template.
     */
    public static class CKey {
        public final String template;
        public final int    node;
        CKey(int n, String t) {
            template = t;
            node = n;
        }
        public String toString() {
            return "" + node + unicode.transform(template);
        }
    }


    /**
     * main is only for testing.
     */
    public static void main(String[] args) {
        try {
            File file = new File(args[0]);
            FileInputStream input = new FileInputStream(file);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            IOUtil.copy(input, bytes);
            input.close();
            byte[] ba = bytes.toByteArray();
            ImageInformer   informer   = new ImageMagickImageInformer();

            Dimension originalSize = informer.getDimension(ba);

            ImageConverter  converter1  = new ImageMagickImageConverter();
            ImageConverter  converter2  = new JAIImageConverter();


            String[] templates = {
                "s(100x60)+f(jpeg)",
                "part(10x10x30x50)",
                "part(10x10x2000x2000)",
                "s(10000@)",  "s(100x100@)",
                "s(10000x2000>)", "s(100000x2000<)",
                "s(4x5<)", "s(4x5>)",
                "r(90)", "r(45)", "r(198)", "r(-30)",
                "border(5)", "border(5x8)",
                "r(45)+border(10x20)",
                "flip",
                "s(100)", "s(x100)", "s(10x70)", "s(70x10)",  "s(60x70!)", "s(80%x150%)", "s(100x100>)","s(100x100&gt;)",
                "s(x100)",
                "s(100)+f(png)+r(20)+s(400x400)"
            };

            System.out.println("original size: " + originalSize);
            System.out.println("template:predicted size:actual size (IM):actual size(JAI)");
            for (String template : templates) {
                List<String> params = parseTemplate(template);
                System.out.print(template + ":" + predictDimension(originalSize, params) + ":");
                try {
                    System.out.print(informer.getDimension(converter1.convertImage(ba, null, params)));
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
                System.out.print(":");
                try {
                    System.out.print(informer.getDimension(converter2.convertImage(ba, null, params)));
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
                System.out.println("");
            }

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

}
