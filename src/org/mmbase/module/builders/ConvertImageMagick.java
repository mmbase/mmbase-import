/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.mmbase.util.ProcessWriter;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * Converts images using ImageMagick.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id: ConvertImageMagick.java,v 1.28 2002-03-12 12:41:09 vpro Exp $
 */
public class ConvertImageMagick implements ImageConvertInterface {
    private static Logger log = Logging.getLoggerInstance(ConvertImageMagick.class.getName());

    // Currently only ImageMagick works, this are the default value's
    private static String converterRoot    = "/usr/local/";
    private static String converterCommand = "bin/convert";
    private static int colorizeHexScale    = 100;
	// The modulate scale base holds the builder property to specify the scalebase.
	// If ModulateScaleBase property is not defined, then value stays max int.
	private static int modulateScaleBase = Integer.MAX_VALUE; 
	
    /**
     * This function initalises this class
     * @param params a <code>Hashtable</code> of <code>String</string>s containing informationn, this should contina the key's
     *               ImageConvert.ConverterRoot and ImageConvert.ConverterCommand specifing the converter root....
     */
    public void init(Hashtable params) {
        String tmp;
        tmp=(String)params.get("ImageConvert.ConverterRoot");
        if (tmp!=null) converterRoot = tmp;

        // now check if the specified ImageConvert.converterRoot does exist and is a directory
        File checkConvDir = new File(converterRoot);
        if(!checkConvDir.exists()) {
            log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterRoot(" + converterRoot + ") does not exist");
        }
        if(!checkConvDir.isDirectory()) {
            log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterRoot(" + converterRoot + ") is not a directory");
        }
        tmp=(String)params.get("ImageConvert.ConverterCommand");
        if (tmp!=null) converterCommand=tmp;

        // now check if the specified ImageConvert.Command does exist and is a file..
        String command = converterRoot + converterCommand;
        File checkConvCom = new File(command);
        if(!checkConvCom.exists()) {
            log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterCommand(" + converterCommand + "), " + command + " does not exist");
        }
        if(!checkConvCom.isFile()) {
            log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterCommand(" + converterCommand + "), " + command + " is not a file");
        }
        if(!checkConvCom.canRead()) {
            log.error("images.xml(ConvertImageMagick): ImageConvert.ConverterCommand(" + converterCommand + "), " + command + " is not readable");
        }

        // do a test-run, maybe slow during startup, but when it is done this way, we can also output some additional info in the log about version..
        // and when somebody has failure with converting images, it is much earlier detectable, when it wrong in settings, since it are settings of
        // the builder... TODO: on error switch to jai????
        try {
            log.debug("Starting convert");
            Process process = Runtime.getRuntime().exec(command);
            InputStream in = null;
            in=process.getInputStream();

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
                log.info("Will use: " + command+", " + tokenizer.nextToken());
            } else {
                log.error("converter from location " + command + ", gave strange result: " + outputstream.toString() + "conv.root='" + converterRoot + "' conv.command='" + converterCommand + "'");
            }
        } catch (Exception e) {
            log.error("images.xml(ConvertImageMagick): " + command + " could not be executed("+ e.toString() +")conv.root='" + converterRoot + "' conv.command='" + converterCommand + "'");
        }
        // Cant do more checking then this, i think....
        tmp=(String)params.get("ImageConvert.ColorizeHexScale");
        if (tmp!=null) {
            try {
                colorizeHexScale = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                log.error("Property ImageConvert.ColorizeHexScale should be an integer: "+e.toString()+ "conv.root='"+converterRoot+"' conv.command='"+converterCommand+"'");
            }
        }
		// See if the modulate scale base is defined. If not defined, it will be ignored.
		log.debug("Searching for ModulateScaleBase property.");
		tmp=(String)params.get("ImageConvert.ModulateScaleBase");
        if (tmp!=null) {
            try {
                modulateScaleBase = Integer.parseInt(tmp);
            } catch (NumberFormatException nfe) {
				log.error("Property ImageConvert.ModulateScaleBase should be an integer, instead of:'"+tmp+"'"
						  +", conv.root='"+converterRoot+"' conv.command='"+converterCommand+"'");
				log.error("Ignoring modulateScaleBase property.");
				log.error(nfe.getMessage());
            }
		} else {
			log.debug("ModulateScaleBase property not found, ignoring the modulateScaleBase.");
		}
    }

    /**
     * This functions converts an image by the given parameters
     * @param input an array of <code>byte</code> which represents the original image
     * @param commands a <code>Vector</code> of <code>String</code>s containing commands which are operations on the image which will be returned.
     *                 ImageConvert.converterRoot and ImageConvert.converterCommand specifing the converter root....
     * @return an array of <code>byte</code>s containing the new converted image.
     *
     */
    public byte[] convertImage(byte[] input,Vector commands) {
        Vector cmd;
        String format;
        byte[] pict=null;

        if (commands!=null && input!=null) {
            cmd =getConvertCommands(commands);
            format=getConvertFormat(commands);
            pict = convertImage(input,cmd,format);
        }
        return pict;
    }

    /**
     * @deprecated Use convertImage
     */
    public byte[] ConvertImage(byte[] input,Vector commands) {
        return convertImage(input, commands);
    }

    /**
     * @javadoc
     */
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
        return format;
    }

    /**
     * Translates MMBase color format (without #) to an convert color format (with or without);
     */

    protected String color(String c) {
        if (c.charAt(0) == 'X') { // the # was mentioned but replaced by X in ImageTag
            c = '#' + c.substring(1); // put it back.
        }
        if(c.length() == 6) { // obviously a little to simple now, because color names of 6 letters don't work now
            return "#" + c.toLowerCase();  
        } else {
            return c.toLowerCase();
        }
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
		// can be diapositive as well... But well, be are backwards compatible.
        if (a.equals("dia"))          return "negate"; 
        return a;
            
    }

    /**
     * Translates the arguments for img.db to arguments for convert of ImageMagick.
     * @param params  Vector with arguments. First one is the image's number, which will be ignored.
     * @return        Vector with convert arguments.
     */
    private Vector getConvertCommands(Vector params) {
        StringBuffer cmdstr = new StringBuffer();
        Vector cmds = new Vector();
        String key, type;
        String cmd;
        int pos, pos2;
        Enumeration t = params.elements();
        if (t.hasMoreElements()) t.nextElement(); // first element is the number, ignore it.
        while (t.hasMoreElements()) {
            key=(String)t.nextElement();
            pos=key.indexOf('(');
            pos2=key.lastIndexOf(')');
            if (pos != -1 && pos2 != -1) {
                type = key.substring(0,pos).toLowerCase();
                cmd  = key.substring(pos+1, pos2);
                if (log.isDebugEnabled()) {
                    log.debug("getCommands(): type=" + type + " cmd=" + cmd);
                }

                /*
                  Following code translates some MMBase specific things to imagemagick's convert arguments.
                 */
                type = getAlias(type);
				// Following code will only be used when ModulateScaleBase builder property is defined.
				if (type.equals("modulate") && (modulateScaleBase!=Integer.MAX_VALUE)) {
					cmd = calculateModulateCmd(cmd,modulateScaleBase);
				} else if (type.equals("colorizehex")) {
                    // Incoming hex number rrggbb is converted to
                    // decimal values rr,gg,bb which are inverted on a scale from 0 to 100.
                    if (log.isDebugEnabled()) log.debug("colorizehex, cmd: "+cmd);
                    String hex = cmd;
                    // Check if hex length is 123456 6 chars.
                    if (hex.length()==6) {

                        // Byte.decode doesn't work correctly.
                        int r = colorizeHexScale - Math.round(colorizeHexScale*Integer.parseInt(hex.substring(0,2),16)/255.0f);
                        int g = colorizeHexScale - Math.round(colorizeHexScale*Integer.parseInt(hex.substring(2,4),16)/255.0f);
                        int b = colorizeHexScale - Math.round(colorizeHexScale*Integer.parseInt(hex.substring(4,6),16)/255.0f);
                        if (log.isDebugEnabled()) {
                            log.debug("Hex is :"+hex);
                            log.debug("Calling colorize with r:"+r+" g:"+g+" b:"+b);
                        }
                        type = "colorize";
                        cmd = r+"/"+g+"/"+b;
                    }
                } else if (type.equals("gamma")) {
                    StringTokenizer tok = new StringTokenizer(cmd, ",/");
                    String r=tok.nextToken();
                    String g=tok.nextToken();
                    String b=tok.nextToken();
                    cmd = r+"/"+g+"/"+b;
                } else if (type.equals("pen") || 
                           type.equals("transparent") ||
                           type.equals("fill") || 
                           type.equals("bordercolor") ||
                           type.equals("background") ||
                           type.equals("box") ||
                           type.equals("opaque") ||
                           type.equals("stroke")
                           ) {
                    // rather sucks, because we have to maintain manually which options accept a color
                    cmd = color(cmd);
                } else if (type.equals("text")) {
                    int firstcomma  = cmd.indexOf(',');
                    int secondcomma = cmd.indexOf(',', firstcomma + 1);
                    type = "draw";
                    try {
                        cmd = "text " + cmd.substring(0, secondcomma) + " " + ((String)cmd).substring(secondcomma +1 ).replace('\'', '"');
                        cmd = new String(cmd.getBytes("UTF-8"), "ISO-8859-1");
                        // convert needs UTF-8, but Runtime seemingly always writes ISO-8859-1, so we
                        // are going to lie here.
                        
                        // even the value of this doesn't seem to matter
                        if (log.isDebugEnabled()) {
                            log.debug("file.encoding: " + java.lang.System.getProperty("file.encoding"));
                        } 
                    } catch (java.io.UnsupportedEncodingException e) {
                        log.error(e.toString());
                    }
                } else if (type.equals("font")) {
                    if (cmd.startsWith("mm:")) {
                        // recognize MMBase config dir, so that it is easy to put the fonts there.
                        cmd = org.mmbase.module.core.MMBaseContext.getConfigPath() +  File.separator + cmd.substring(3);
                    }
                } else if (type.equals("circle")) {
                    type = "draw";
                    cmd  = "circle " + cmd;
                } else if (type.equals("part")) {
                    StringTokenizer tok = new StringTokenizer(cmd, "x,\n\r");
                    try {
                        int x1=Integer.parseInt(tok.nextToken());
                        int y1=Integer.parseInt(tok.nextToken());
                        int x2=Integer.parseInt(tok.nextToken());
                        int y2=Integer.parseInt(tok.nextToken());
                        type="crop";                        
                        cmd = (x2-x1)+"x"+(y2-y1)+"+"+x1+"+"+y1;
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                } else if (type.equals("roll")) {
                    StringTokenizer tok = new StringTokenizer(cmd, "x,\n\r");
                    String str;
                    int x=Integer.parseInt(tok.nextToken());
                    int y=Integer.parseInt(tok.nextToken());
                    if (x>=0) str="+"+x;
                    else str=""+x;
                    if (y>=0) str+="+"+y;
                    else str+=""+y;                    
                    cmd = str;
                } else if (type.equals("f")) { // format was already dealt with
                    continue; // ignore this one.
                }
                if (log.isDebugEnabled()) log.debug("adding -" + type + " " + cmd);
                // all other things are recognized as well..
                cmds.add("-" + type); 
                cmds.add(cmd);
            } else {
                key = getAlias(key);
                if (key.equals("lowcontrast")) {
                    cmds.add("+contrast");
                }  else if (key.equals("neg")) {
                    cmds.add("+negate");
                } else {
                    cmds.add("-" + key);
                }
            }
        }
        return cmds; 
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
		log.debug("Calculating modulate cmd using scale base "+scaleBase+" for modulate cmd: "+cmd);
		String modCmd = "";
		StringTokenizer st = new StringTokenizer(cmd, ",/");
		while (st.hasMoreTokens())
			modCmd+=scaleBase+Integer.parseInt(st.nextToken())+",";
        if (!modCmd.equals(""))
                modCmd = modCmd.substring(0,modCmd.length()-1); // remove last ',' char.
		log.debug("Modulate cmd after calculation: "+modCmd);
		return modCmd;
	}
	
    /**
     * Does the actual conversion.
     *
     * @param pict Byte array with the original picture
     * @param cmd  Vector with convert parameters.
     * @param format The picture format to output to (jpg, gif etc.).
     * @return      The result of the conversion (a picture).
     *
     */
    private byte[] convertImage(byte[] pict, Vector cmd, String format) {
        cmd.add(0, "-");
        cmd.add(0, converterRoot + converterCommand);
        cmd.add(format + ":-");
        
        String command = cmd.toString(); // only for debugging.

        if (log.isDebugEnabled()) {
            log.debug("command:" + command + " in " +   new File("").getAbsolutePath());
        }
        try {            
            log.debug("starting program");
            Process p = Runtime.getRuntime().exec((String [])cmd.toArray(new String[0]));
            // in grabs the stuff coming from stdout from program...
            InputStream in = p.getInputStream();
            // err grabs the stuff coming from stderr from program...            
            InputStream err = p.getErrorStream();
            
            ProcessWriter pw = new ProcessWriter(new ByteArrayInputStream(pict),p.getOutputStream());
            log.debug("starting process writer");
            pw.start();
            log.debug("done with process writer");            
            
            ByteArrayOutputStream imagestream=new ByteArrayOutputStream();
            int size=0;
            byte[] inputbuffer=new byte[2048];
            while((size=in.read(inputbuffer))>0) {
                imagestream.write(inputbuffer,0,size);
            }
            log.debug("retrieved all information");
            byte[] image=imagestream.toByteArray();            
            
                // no bytes in the image thingie
            if(image.length < 1) {
                log.warn("Imagemagick conversion did not succeed. Returning byte array of " + image.length + " bytes.");
            }
            // we a image of 0 bytes is not a real image to me... i will leave the code here intact, but maybe nicer to return null....
            // checkout what our error? was...

            // log what came on STDERR
            ByteArrayOutputStream errorstream = new ByteArrayOutputStream();
            size=0;
            while((size=err.read(inputbuffer))>0) {
                log.debug("copying "+size+" bytes from ERROR-stream ");
                errorstream.write(inputbuffer,0,size);
            }
            byte[]  errorMessage = errorstream.toByteArray();
            if(errorMessage.length > 0) {
                log.error("From stderr with command '" + command + 
                          "' in '" + 
                          new File("").getAbsolutePath() + 
                          "'  --> '" + 
                          new String(errorMessage) + "'");
            } else {
                log.debug("No information on stderr found");
            }
            
                     
            // print some info and return....            
            log.service("converted image(#" + pict.length + " bytes) with options '" + cmd + "' to '" + format + "'-image(#" + image.length + " bytes)('" + command + "')");            
            return image;
        } 
        catch (IOException e) {
            log.error("converting image with command: '" + command + "' failed  with reason: '" + e.getMessage() + "'");
            log.error(Logging.stackTrace(e));
        }
        return null;
    }
}
