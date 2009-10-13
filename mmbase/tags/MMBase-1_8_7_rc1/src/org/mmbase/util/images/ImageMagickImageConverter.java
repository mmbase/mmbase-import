/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.images;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.util.Encode;
import org.mmbase.util.externalprocess.CommandLauncher;
import org.mmbase.util.externalprocess.ProcessException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Converts images using ImageMagick.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @author Nico Klasens
 * @author Jaco de Groot
 * @version $Id$
 */
public class ImageMagickImageConverter implements ImageConverter {
    private static final Logger log = Logging.getLoggerInstance(ImageMagickImageConverter.class);

    private static final Pattern IM_VERSION_PATTERN = Pattern.compile("(?is).*?\\s(\\d+)\\.(\\d+)\\.(\\d+)\\s.*");
    private static final Pattern IM_FORMAT_PATTERN  = Pattern.compile("(?is)\\s*([A-Z0-9]+)\\*?\\s+[A-Z0-9]*\\s*[r\\-]w[\\+\\-]\\s+.*");

    private int imVersionMajor = 5;
    private int imVersionMinor = 5;
    private int imVersionPatch = 0;


    // Currently only ImageMagick works, this are the default value's
    private static String converterPath = "convert"; // in the path.

    private static int colorizeHexScale = 100;
    // The modulate scale base holds the builder property to specify the scalebase.
    // If ModulateScaleBase property is not defined, then value stays max int.
    private static int modulateScaleBase = Integer.MAX_VALUE;


    // private static String CONVERT_LC_ALL= "LC_ALL=en_US.UTF-8"; I don't know how to change it.

    protected List excludeFormats = new ArrayList();

    private static final Set validFormats = new TreeSet();



    /**
     * This function initializes this class
     * @param params a <code>Map</code> of <code>String</code>s containing information, this should contain the key's
     *               ImageConvert.ConverterRoot and ImageConvert.ConverterCommand specifying the converter root, and it can also contain
     *               ImageConvert.DefaultImageFormat which can also be 'asis'.
     */
    public void init(Map params) {
        String converterRoot = "";
        String converterCommand = "convert";

        String tmp;
        tmp = (String) params.get("ImageConvert.ConverterRoot");
        if (tmp != null && ! tmp.equals("")) {
            converterRoot = tmp;
        }

        tmp = (String) params.get("ImageConvert.ConverterCommand");
        if (tmp != null && ! tmp.equals("")) {
            converterCommand = tmp;
        }

        tmp = (String) params.get("ImageConvert.ExcludeFormats");
        if (tmp != null && ! tmp.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(tmp, " ,");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                excludeFormats.add(token);
            }
        }

        if(System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
            // on the windows system, we _can_ assume the it uses .exe as extention...
            // otherwise the check on existance of the program will fail.
            if (!converterCommand.endsWith(".exe")) {
                converterCommand += ".exe";
            }
        }

        String configFile = params.get("configfile").toString();
        if (configFile == null) configFile = "images builder xml";

        converterPath = converterCommand; // default.
        if (!converterRoot.equals("")) { // also a root was indicated, add it..
            // now check if the specified ImageConvert.converterRoot does exist and is a directory
            File checkConvDir = new File(converterRoot).getAbsoluteFile();
            if (!checkConvDir.exists()) {
                log.error( "ImageConvert.ConverterRoot " + converterRoot + " in " + configFile + " does not exist");
            } else if (!checkConvDir.isDirectory()) {
                log.error( "ImageConvert.ConverterRoot " + converterRoot + " in " + configFile + " is not a directory");
            } else {
                // now check if the specified ImageConvert.Command does exist and is a file..
                File checkConvCom = new File(converterRoot, converterCommand);
                converterPath = checkConvCom.toString();
                if (!checkConvCom.exists()) {
                    log.error( converterPath + " specified by " + configFile + "  does not exist");
                } else if (!checkConvCom.isFile()) {
                    log.error( converterPath + " specified by " + configFile + "  is not a file");
                }
            }
        }
        // do a test-run, maybe slow during startup, but when it is done this way, we can also output some additional info in the log about version..
        // and when somebody has failure with converting images, it is much earlier detectable, when it wrong in settings, since it are settings of
        // the builder...

        // TODO: on error switch to Dummy????
        // TODO: research how we tell convert, that is should use the System.getProperty(); with respective the value's 'java.io.tmpdir', 'user.dir'
        //       this, since convert writes at this moment inside the 'user.dir'(working dir), which isnt writeable all the time.

        {
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                CommandLauncher launcher = new CommandLauncher("ConvertImage");
                log.debug("Starting convert");
                List cmd = new ArrayList();
                cmd.add("-version");
                launcher.execute(converterPath, (String[]) cmd.toArray(new String[] {}));
                launcher.waitAndRead(outputStream, errorStream);
            } catch (ProcessException e) {
                log.error("Convert test failed. " + converterPath + " (" + e.toString() + ") conv.root='" + converterRoot
                          + "' conv.command='" + converterCommand + "'", e);
            }

            String imOutput = outputStream.toString();
            Matcher m = IM_VERSION_PATTERN.matcher(imOutput);
            if (m.matches()) {
                imVersionMajor = Integer.parseInt(m.group(1));
                imVersionMinor = Integer.parseInt(m.group(2));
                imVersionPatch = Integer.parseInt(m.group(3));
                log.info("Found ImageMagick version " + imVersionMajor + "." + imVersionMinor + "." + imVersionPatch);
            } else {
                log.error( "converter from location " + converterPath + ", gave strange result: " + imOutput
                           + "conv.root='" + converterRoot + "' conv.command='" + converterCommand + "'");
                log.info("Supposing ImageMagick version " + imVersionMajor + "." + imVersionMinor + "." + imVersionPatch);

            }
        }
        {
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                CommandLauncher launcher = new CommandLauncher("ConvertImage");
                log.debug("Starting convert");
                List cmd = new ArrayList();
                cmd.add("-list");
                cmd.add("format");
                launcher.execute(converterPath, (String[]) cmd.toArray(new String[] {}));
                launcher.waitAndRead(outputStream, errorStream);
            } catch (ProcessException e) {
                log.error("Convert -list format failed. " + converterPath + " (" + e.toString() + ") conv.root='" + converterRoot
                          + "' conv.command='" + converterCommand + "'", e);
            }

            String[] imOutput = outputStream.toString().split("\n");
            for (int i = 0; i < imOutput.length; i++) {
                String imLine = imOutput[i];
                Matcher m = IM_FORMAT_PATTERN.matcher(imLine);
                if (m.matches()) {
                    String format = m.group(1);
                    validFormats.add(format.toUpperCase());
                }
            }
            log.info("Found ImageMagick supported formats " + validFormats);
        }

        // Cant do more checking then this, i think....
        tmp = (String) params.get("ImageConvert.ColorizeHexScale");
        if (tmp != null) {
            try {
                colorizeHexScale = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                log.error( "Property ImageConvert.ColorizeHexScale should be an integer: " + e.toString() + "conv.root='" + converterRoot + "' conv.command='" + converterCommand + "'");
            }
        }
        // See if the modulate scale base is defined. If not defined, it will be ignored.
        log.debug("Searching for ModulateScaleBase property.");
        tmp = (String) params.get("ImageConvert.ModulateScaleBase");
        if (tmp != null) {
            try {
                modulateScaleBase = Integer.parseInt(tmp);
            } catch (NumberFormatException nfe) {
                log.error( "Property ImageConvert.ModulateScaleBase should be an integer, instead of:'" + tmp + "'" + ", conv.root='" + converterRoot + "' conv.command='" + converterCommand + "'");
                log.error("Ignoring modulateScaleBase property.");
                log.error(nfe.getMessage());
            }
        } else {
            log.debug(
            "ModulateScaleBase property not found, ignoring the modulateScaleBase.");
        }
    }

    private static class ParseResult {
        List args;
        String format;
        File cwd;
    }

    /**
     * This functions converts an image by the given parameters
     * @param input an array of <code>byte</code> which represents the original image
     * @param sourceFormat original image format
     * @param commands a <code>List</code> of <code>String</code>s containing commands which are operations on the image which will be returned.
     *                 ImageConvert.converterRoot and ImageConvert.converterCommand specifying the converter root....
     * @return an array of <code>byte</code>s containing the new converted image.
     */
    public byte[] convertImage(byte[] input, String sourceFormat, List commands) {
        if (input == null) {
            log.error("Converting an empty image does not make sense.");
            return input;
        }
        if (excludeFormats.contains(sourceFormat)) {
            log.debug("Conversion is excluded for image format: " + sourceFormat);
            return input;
        }

        byte[] pict = null;
        if (commands != null && !commands.isEmpty()) {
            ParseResult parsedCommands = getConvertCommands(commands);
            if (parsedCommands.format.equals("asis")) {
                if (sourceFormat != null) {
                    log.debug("Format 'asis' specified. Using sourceformat '" + sourceFormat + "'");
                    parsedCommands.format = sourceFormat;
                } else {
                    log.debug("Format 'asis' specified. But no sourceformat provided.");
                }
            }

            if ("gif".equals(parsedCommands.format)) {
                if (isAnimated(input)) {
                    parsedCommands.args.add(0, "-coalesce");
                }
            }

            pict = convertImage(input, sourceFormat, parsedCommands.args, parsedCommands.format, parsedCommands.cwd);
        }
        return pict;
    }

    protected boolean isAnimated(byte[] rawimage) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setDetermineImageNumber(true);
        ByteArrayInputStream inp = new ByteArrayInputStream(rawimage);

        imageInfo.setInput(inp);
        imageInfo.check();
        return (imageInfo.getNumberOfImages() > 1);
    }

    /**
     * Translates MMBase color format (without #) to an convert color format (with or without);
     */
    protected String color(String c) {
        if (c.charAt(0) == 'X') {
            // the # was mentioned but replaced by X in ImageTag
            c = '#' + c.substring(1); // put it back.
        }
        if (c.length() == 6) {
            // obviously a little to simple now, because color names of 6 letters don't work now
            return "#" + c.toLowerCase();
        } else {
            return c.toLowerCase();
        }
    }


    /**
     * Translates the arguments for img.db to arguments for convert of ImageMagick.
     * @param params  List with arguments. First one is the image's number, which will be ignored.
     * @return        Map with three keys: 'args', 'cwd', 'format'.
     */
    private ParseResult getConvertCommands(List params) {
        if (log.isDebugEnabled()) {
            log.debug("getting convert commands from " + params);
        }
        ParseResult result = new ParseResult();
        List cmds = new ArrayList();
        result.args = cmds;
        result.cwd = null;

        String key, type;
        String cmd;
        int pos, pos2;
        Iterator t = params.iterator();
        while (t.hasNext()) {
            key = (String) t.next();
            if (log.isDebugEnabled()) log.debug("parsing '" + key + "'");
            pos = key.indexOf('(');
            pos2 = key.lastIndexOf(')');
            if (pos != -1 && pos2 != -1) {
                type = key.substring(0, pos).toLowerCase();
                cmd = key.substring(pos + 1, pos2);
                if (log.isDebugEnabled()) {
                    log.debug("getCommands(): type=" + type + " cmd=" + cmd);
                }
                // Following code translates some MMBase specific things to imagemagick's convert arguments.
                type = Imaging.getAlias(type);
                // Following code will only be used when ModulateScaleBase builder property is defined.
                if (type.equals("modulate") && (modulateScaleBase != Integer.MAX_VALUE)) {
                    cmd = calculateModulateCmd(cmd, modulateScaleBase);
                } else if (type.equals("colorizehex")) {
                    // Incoming hex number rrggbb is converted to
                    // decimal values rr,gg,bb which are inverted on a scale from 0 to 100.
                    if (log.isDebugEnabled())
                        log.debug("colorizehex, cmd: " + cmd);
                    String hex = cmd;
                    // Check if hex length is 123456 6 chars.
                    if (hex.length() == 6) {

                        // Byte.decode doesn't work correctly.
                        int r = colorizeHexScale - Math.round( colorizeHexScale * Integer.parseInt( hex.substring(0, 2), 16) / 255.0f);
                        int g = colorizeHexScale - Math.round( colorizeHexScale * Integer.parseInt( hex.substring(2, 4), 16) / 255.0f);
                        int b = colorizeHexScale - Math.round( colorizeHexScale * Integer.parseInt( hex.substring(4, 6), 16) / 255.0f);
                        if (log.isDebugEnabled()) {
                            log.debug("Hex is :" + hex);
                            log.debug( "Calling colorize with r:" + r + " g:" + g + " b:" + b);
                        }
                        type = "colorize";
                        cmd = r + "/" + g + "/" + b;
                    }
                } else if (type.equals("gamma")) {
                    StringTokenizer tok = new StringTokenizer(cmd, ",/");
                    String r = tok.nextToken();
                    String g = tok.nextToken();
                    String b = tok.nextToken();
                    cmd = r + "/" + g + "/" + b;
                } else if (
                type.equals("pen")
                || type.equals("transparent")
                || type.equals("fill")
                || type.equals("bordercolor")
                || type.equals("background")
                || type.equals("box")
                || type.equals("opaque")
                || type.equals("stroke")) {
                    // rather sucks, because we have to maintain manually which options accept a color
                    cmd = color(cmd);
                } else if (type.equals("text")) {
                    int firstcomma = cmd.indexOf(',');
                    int secondcomma = cmd.indexOf(',', firstcomma + 1);
                    if (imVersionMajor < 6) {
                        type = "draw";
                        try {
                            File tempFile = File.createTempFile("mmbase_image_text_", null);
                            tempFile.deleteOnExit();
                            Encode encoder = new Encode("ESCAPE_SINGLE_QUOTE");
                            String text = cmd.substring(secondcomma + 1);
                            FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
                            tempFileOutputStream.write(encoder.decode(text.substring(1, text.length() - 1)).getBytes("UTF-8"));
                            tempFileOutputStream.close();
                            cmd = "text " + cmd.substring(0, secondcomma) + " '@" + tempFile.getPath() + "'";
                        } catch (IOException e) {
                            log.error("Could not create temporary file for text: " + e.toString());
                            cmd = "text " + cmd.substring(0, secondcomma) + " 'Could not create temporary file for text.'";
                        }
                    } else {
                        cmds.add("-encoding");
                        cmds.add("unicode");
                        cmds.add("-annotate");
                        try {
                            File tempFile = File.createTempFile("mmbase_image_text_", null);
                            tempFile.deleteOnExit();
                            FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
                            Encode encoder = new Encode("ESCAPE_SINGLE_QUOTE");
                            String text =  cmd.substring(secondcomma + 1);
                            log.debug("Using '" + text + "'");
                            tempFileOutputStream.write(encoder.decode(text.substring(1, text.length() - 1)).getBytes("UTF-8"));
                            tempFileOutputStream.close();
                            cmds.add("+" + cmd.substring(0, firstcomma) + "+" + cmd.substring(firstcomma + 1, secondcomma));
                            cmds.add("@" + tempFile.getPath());
                        } catch (IOException e) {
                            log.error("Could not create temporary file for text: " + e.toString());
                            cmd =  cmd.substring(0, secondcomma) + " 'Could not create temporary file for text.'";
                        }
                        continue;
                    }
                } else if (type.equals("draw")) {
                    //try {
                        //cmd = new String(cmd.getBytes("UTF-8"), "ISO-8859-1");
                        // can be some text in the draw command
                    //} catch (java.io.UnsupportedEncodingException e) {
                    //   log.error(e.toString());
                    //}
                } else if (type.equals("font")) {
                    if (cmd.startsWith("mm:")) {
                        // recognize MMBase config dir, so that it is easy to put the fonts there.
                        cmd = org.mmbase.module.core.MMBaseContext.getConfigPath()+ File.separator + cmd.substring(3);
                    }
                    File fontFile = new File(cmd);
                    if (!fontFile.isFile()) {
                        // if not pointed to a normal file, then set the cwd to <config>/fonts where you can put a type.mgk
                        File fontDir =
                        new File( org.mmbase.module.core.MMBaseContext.getConfigPath(),"fonts");
                        if (fontDir.isDirectory()) {
                            if (log.isDebugEnabled()) {
                                log.debug("Using " + fontDir + " as working dir for conversion. A 'type.mgk' (see ImageMagick documentation) can be in this dir to define fonts");
                            }
                            result.cwd = fontDir;
                        } else {
                            log.debug(
                            "Using named font without MMBase 'fonts' directory, using ImageMagick defaults only");
                        }
                    }

                } else if (type.equals("circle")) {
                    type = "draw";
                    cmd = "circle " + cmd;
                } else if (type.equals("part")) {
                    StringTokenizer tok = new StringTokenizer(cmd, "x,\n\r");
                    try {
                        int x1 = Integer.parseInt(tok.nextToken());
                        int y1 = Integer.parseInt(tok.nextToken());
                        int x2 = Integer.parseInt(tok.nextToken());
                        int y2 = Integer.parseInt(tok.nextToken());
                        type = "crop";
                        cmd = (x2 - x1) + "x" + (y2 - y1) + "+" + x1 + "+" + y1;
                    } catch (Exception e) {

                        log.error(e.toString());
                    }
                } else if (type.equals("roll")) {
                    StringTokenizer tok = new StringTokenizer(cmd, "x,\n\r");
                    String str;
                    int x = Integer.parseInt(tok.nextToken());
                    int y = Integer.parseInt(tok.nextToken());
                    if (x >= 0)
                        str = "+" + x;
                    else
                        str = "" + x;
                    if (y >= 0)
                        str += "+" + y;
                    else
                        str += "" + y;
                    cmd = str;
                } else if (type.equals("f")) {
                    if (! (cmd.equals("asis") && result.format != null)) {
                        result.format = cmd;
                    }
                    continue; // ignore this one, don't add to cmds.
                }
                if (log.isDebugEnabled()) {
                    log.debug("adding " + type + " " + cmd);
                }
                // all other things are recognized as well..
                if (! isCommandPrefixed(type)) { // if no prefix given, suppose '-'
                    cmds.add("-" + type);
                } else {
                    cmds.add(type);
                }
                cmds.add(cmd);

            } else {
                key = Imaging.getAlias(key);
                if (key.equals("lowcontrast")) {
                    cmds.add("+contrast");
                } else if (key.equals("neg")) {
                    cmds.add("+negate");
                } else {
                    if (! isCommandPrefixed(key)) { // if no prefix given, suppose '-'
                        cmds.add("-" + key);
                    } else {
                        cmds.add(key);
                    }
                }
            }
        }
        if (result.format == null)  result.format = Factory.getDefaultImageFormat();

        return result;
    }

    /**
     * @since MMBase-1.7
     */
    private boolean isCommandPrefixed(String s) {
        if (s == null || s.length() == 0) return false;
        char c = s.charAt(0);
        return c == '-' || c == '+';
    }

    /**
     * Calculates the modulate parameter values (brightness,saturation,hue) using a scale base.
     * ImageMagick's convert command changed its modulate scale somewhere between version v4.2.9 and v5.3.8.<br />
     * In version 4.2.9 the scale ranges from -100 to 100.<br />
     * (relative, eg. 20% higher, value is 20, 10% lower, value is -10).<br />
     * In version 5.3.8 the scale ranges from 0 to 100.<br />
     * (absolute, eg. 20% higher, value is 120, 10% lower, value is 90).<br />
     * Now, for different convert versions the scale range can be corrected with the scalebase. <br />
     * The internal scale range that's used will be from -100 to 100. (eg. modulate 20,-10,0).
     * With the base you can change this, so for v4.2.9 scalebase=0 and for v5.3.9 scalebase=100.
     * @param cmd modulate command string
     * @param scaleBase the scale base value
     * @return the transposed modulate command string.
     */
    private String calculateModulateCmd(String cmd, int scaleBase) {
        log.debug( "Calculating modulate cmd using scale base " + scaleBase + " for modulate cmd: " + cmd);
        String modCmd = "";
        StringTokenizer st = new StringTokenizer(cmd, ",/");
        while (st.hasMoreTokens()) {
            modCmd += scaleBase + Integer.parseInt(st.nextToken()) + ",";
        }
        if (!modCmd.equals("")) {
            modCmd = modCmd.substring(0, modCmd.length() - 1);
        }
        // remove last ',' char.
        log.debug("Modulate cmd after calculation: " + modCmd);
        return modCmd;
    }





    /**
     * Does the actual conversion.
     *
     * @param pict Byte array with the original picture
     * @param cmd  List with convert parameters.
     * @param format The picture format to output to (jpg, gif etc.).
     * @return      The result of the conversion (a picture).
     *
     */
    private byte[] convertImage(byte[] pict, String sourceFormat, List cmd, String format, File cwd) {

        if (pict != null && pict.length > 0) {
            cmd.add(0, "-");
            cmd.add(0, converterPath);
            if (! validFormats.contains(format.toUpperCase())) {
                log.warn("format " + format + "' is not supported (" + validFormats + ") falling back to " + Factory.getDefaultImageFormat());
                format = Factory.getDefaultImageFormat();
                if ("asis".equals(format)) format = sourceFormat;
            }
            cmd.add(format + ":-");

            String command = cmd.toString(); // only for debugging.
            log.debug("Converting image(#" + pict.length + " bytes)  to '" + format + "' ('" + command + "')");

            CommandLauncher launcher = new CommandLauncher("ConvertImage");
            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            ByteArrayInputStream originalStream = new ByteArrayInputStream(pict);

            try {
                if (cwd != null) {
                    // using MAGICK_HOME for mmbase config/fonts if 'font' option used (can put type.mgk)
                    String[] env = { "MAGICK_HOME=" + cwd.toString() };
                    if (log.isDebugEnabled()) {
                        log.debug("MAGICK_HOME " + env[0]);
                    }
                    launcher.execute((String[]) cmd.toArray(new String[0]), env);
                }
                else {
                    launcher.execute((String[]) cmd.toArray(new String[0]));
                }
                launcher.waitAndWrite(originalStream, imageStream, errorStream);

                log.debug("retrieved all information");
                byte[] image = imageStream.toByteArray();

                if (image.length < 1) {
                    // No bytes in the image -
                    // ImageMagick failed to create a proper image.
                    // return null so this image is not by accident stored in the database
                    log.error("Imagemagick conversion did not succeed. Returning null.");
                    String errorMessage = errorStream.toString();

                    if (errorMessage.length() > 0) {
                        log.error( "From stderr with command '" + command + "' in '" + new File("").getAbsolutePath() + "'  --> '" + errorMessage + "'");
                    } else {
                        log.debug("No information on stderr found");
                    }
                    return null;
                }
                else {
                    // print some info and return....
                    if (log.isServiceEnabled()) {
                        log.service("converted image(#" + pict.length + " bytes)  to '" + format + "'-image(#" + image.length + " bytes)('" + command + "')");
                    }
                    return image;
                }
            }
            catch (ProcessException e) {
                log.error("converting image with command: '" + command + "' failed  with reason: '" + e.getMessage() + "'");
                log.error(Logging.stackTrace(e));
            }
            finally {
                try {
                    if (originalStream != null) {
                        originalStream.close();
                    }
                }
                catch (IOException ioe) {
                }
                try {
                    if (imageStream != null) {
                        imageStream.close();
                    }
                }
                catch (IOException ioe) {
                }
            }
        }
        else {
            log.error("Converting an empty image does not make sense.");
        }

        return null;
    }

}
