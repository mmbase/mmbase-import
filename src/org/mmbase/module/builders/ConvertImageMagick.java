/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Converts Images using image magick.
 *
 * @author Rico Jansen
 * @version $Id: ConvertImageMagick.java,v 1.22 2002-01-23 21:21:20 eduard Exp $
 */
public class ConvertImageMagick implements ImageConvertInterface {
    private static Logger log = Logging.getLoggerInstance(ConvertImageMagick.class.getName());

    // Currenctly only ImageMagick works, this are the default value's

    private static String ConverterRoot = "/usr/local/";
    private static String ConverterCommand = "bin/convert";
    private static int colorizeHexScale = 100;

    /**
     * This function initalises this class
     * @param params a <code>Hashtable</code> of <code>String</string>s containing informationn, this should contina the key's
     *               ImageConvert.ConverterRoot and ImageConvert.ConverterCommand specifing the converter root....
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
            } else {
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
            } catch (NumberFormatException e) {
                log.error("Property ImageConvert.ColorizeHexScale should be an integer: "+e.toString()+ "conv.root='"+ConverterRoot+"' conv.command='"+ConverterCommand+"'");
            }
        }
    }

    /**
     * This functions converts an image by the given parameters
     * @param input an array of <code>byte</code> which represents the original image
     * @param commands a <code>Vector</code> of <code>String</code>s containing commands which are operations on the image which will be returned.
     *                 ImageConvert.ConverterRoot and ImageConvert.ConverterCommand specifing the converter root....
     * @return an array of <code>byte</code>s containing the new converted image.
     */
    public byte[] ConvertImage(byte[] input,Vector commands) {
        String cmd,format;
        byte[] pict=null;

        if (commands!=null && input!=null) {
            cmd=getConvertCommands(commands);
            format=getConvertFormat(commands);
            pict=ConvertImage(input,cmd,format);
        }
        return pict;
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
     * @javadoc
     */
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
        return cmdstr.toString();
    }

    /**
     * @javadoc
     */
    private byte[] ConvertImage(byte[] pict,String cmd, String format) {
        String command=ConverterRoot+ConverterCommand+" - "+cmd+" "+format+":-";
        log.debug("command:"+command);
        try {            
            log.debug("starting program");
            Process p = Runtime.getRuntime().exec(command);
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
                log.debug("copying "+size+" bytes from stream ");
                imagestream.write(inputbuffer,0,size);
            }
            log.debug("retrieved all information");
            byte[] image=imagestream.toByteArray();            
            
            // no bytes in the image thingie
            if(image.length < 1) {
                log.debug("result was 0 bytes, gonna look for an message on stderr");
                // we a image of 0 bytes is not a real image to me... i will leave the code here intact, but maybe nicer to return null....
                // checkout what our error? was...
                imagestream=new ByteArrayOutputStream();
                size=0;
                while((size=err.read(inputbuffer))>0) {
                    log.debug("copying "+size+" bytes from ERROR-stream ");
                    imagestream.write(inputbuffer,0,size);
                }
                byte[]  errorMessage = imagestream.toByteArray();
                if(errorMessage.length > 0) {
                    log.error("from stderr with command '" + command + "' --> '" + new String(errorMessage) + "'");
                    log.error("working dir: '" + new File("file").getParentFile().getAbsolutePath() + "'");
                } 
                else {
                    log.debug("no information on stderr found");
                }
                log.warn("should it return null here?, it wasnt this way, so i didnt change it...");
            }
            
            // print some info and return....            
            log.service("converted image(#"+pict.length+" bytes) with options '"+cmd+"' to '"+format+"'-image(#"+image.length+" bytes)('"+command+"')");            
            return image;
        } 
        catch (IOException e) {
            log.error("converting image with command: '" + command + "' failed  with reason: '" + e.getMessage() + "'");
            log.error(Logging.stackTrace(e));
        }
        return null;
    }
}
