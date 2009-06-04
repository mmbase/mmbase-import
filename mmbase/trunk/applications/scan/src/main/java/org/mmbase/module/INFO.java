/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * The INFO module provides access to the environment on which the mmbase system resides.
 * It can retrieve information on the file system, system memory, time, current user or browser,
 * and miscellaneous information that is not directly related to the object cloud.
 * Most functions in this module are specific for SCAN - other scripting languages generally have
 * their own ways of obtaining this data.
 *
 * @application SCAN
 * @rename Info
 * @author Daniel Ockeloen
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @version $Id$
.*/
public class INFO extends ProcessorModule {

    /**
     * @rename NOT
     */
    public final static int Not=0;
    public final static int Dutch=1;
    public final static int English=2;

    private static Logger log = Logging.getLoggerInstance(INFO.class.getName());

    /**
     * @scope private
     */
    Random rnd;
    /**
     * @scope private
     */
    String documentroot;
    /**
     * @scope private
     */
    Hashtable<String,SortedVector> DirCache=new Hashtable<String,SortedVector>();

    /**
     * Constructor for
     */
    public INFO() {
    }

    /**
     * Initializes the module.
     * Determines the document root by reading system properties.
     */
    public void init() {
        documentroot=MMBaseContext.getHtmlRoot();
        // org.mmbase super.init();
        rnd=new RandomPlus();
    }

    /**
     * Returns one propertyvalue to the subclass (original in Module).
     */
    protected String getProperty(String name, String var) {
        return ""; // really unsure what this is supposed to do?
    }

    /**
     * Generate a list of values from a command to the processor.
     * The commands processed are : <br />
     * COLOR-BASIC : returns a list of (system) color names and their RGB values<br />
     * RANGE-X-Y-Z : returns a list of values in the numeric range X to Y, using Z as the increment factor (step) i.e
     *             RANGE-0-12-3 returns the values 0, 3 ,6 ,9, 12<br />
     *             The default values of X, Y and Z are 1, 10 and 1.<br />
     * RANGE-ALPHA : returns the values 'A' thru 'Z'<br />
     * SCANDATE :returns a list of dates (date, month, day, day-of-week) of all directories in a given path with a file length of 10 characters.
     *           No, I don't get it either.<br />
     *
     * @param sp the current page context
     * @param tagger the parameters (name-value pairs) belonging to the command to process
     * @param value the command to process
     * @return a <code>Vector</code> containing the requested values.
     * @throws ParseException
     */
    @Override public List<String>  getList(PageInfo sp, StringTagger tagger, String value) {

        String line = Strip.doubleQuote(value,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("COLOR")) { tagger.setValue("ITEMS","2"); return doColor(tok); }
            if (cmd.equals("RANGE")) { tagger.setValue("ITEMS","1"); return doRange(tok); }
            if (cmd.equals("SCANDATE")) { tagger.setValue("ITEMS","4"); return doScanDate(sp,tagger); }
        }
        return null;
    }

    /**
     * Execute the commands provided in the form values.
     * Does not do anything except output debug code.
     * @param sp the current page context
     * @param cmds the command to process
     * @param vars the variables to process
     * @return alwyas <code>false</code>
     */
    @Override public boolean process(PageInfo sp, Hashtable cmds,Hashtable vars) {
        if (log.isDebugEnabled()) {
            log.debug("CMDS="+cmds);
            log.debug("VARS="+vars);
        }
        return false;
    }

    /**
     * Handle a $MOD command.
     * This generally replaces the command in the SCAN page with the value returned by the command.
     * Commands include:<br />
     * BROWSER : returns browser or host name<br />
     * DECODE : decodes a URL-encodes stringvalue<br />
     * ENCODE : URL-encodes a strignvalue <br />
     * ESCAPE : escapes the single quotes in a stringvalue <br />
     * EXISTS : test if a file exists
     * MEMORY : returns free meory <br />
     * MOVE : Move a file on the system
     * OS : retrieve the name of the user's Operating System
     * RANDOM : returns a random number
     * RELTIME : convert (relative) time values
     * TIME : return a specific time value
     * TIMEFORMAT/TIMEFORMATSEC : format a timevalue
     * PARSETIME : parse time to seconds
     * STRING :
     * USER :
     * @param sp the current page context
     * @param cmds the command to process
     * @return a <code>String</code> with the command's result value
       */
    @Override public String replace(PageInfo sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("CLASS")) return "CLASS="+this;
            if (cmd.equals("BROWSER")) return doBrowser(sp,tok);
            if (cmd.equals("DECODE")) return doParamDecode(sp,tok);
            if (cmd.equals("ENCODE")) return doParamEncode(sp,tok);
            if (cmd.equals("ESCAPE")) return doEscape(sp,tok);
            if (cmd.equals("EXISTS")) return doExists(sp,tok);
            if (cmd.equals("MEMORY")) return doMemory(tok);
            if (cmd.equals("MOVE"))      return doMove(sp,tok);
            if (cmd.equals("OS")) return doOs(sp,tok);
            if (cmd.equals("RANDOM")) return doRandom(sp,tok);
            if (cmd.equals("RELTIME")) return doRelTime(tok);
            if (cmd.equals("STRING")) return doString(tok);
            if (cmd.equals("STRINGCMP")) return toYesNo(doString(tok).equals(""+true));
                    if (cmd.equals("TIME")) return doTime(tok);
            if (cmd.equals("TIMEFORMAT")) return doTimeFormat(tok, false);
            if (cmd.equals("TIMEFORMATSEC")) return doTimeFormat(tok, true);
            if (cmd.equals("PARSETIME")) return doParseTime(tok);

            if (cmd.equals("USER")) return doUser(sp,tok);
            if (cmd.equals("SERVERCONTEXT")) return  sp.req.getContextPath();
        }
        return "No command defined";
    }


    /**
     * takes a time in several formats and creates a time from it
     * @param tok the processing command's arguments
     */
    String doParseTime(StringTokenizer tok) {
        String rawstr=tok.nextToken();
        String formatstr="";
        if (tok.hasMoreTokens()) {
            formatstr=tok.nextToken();
            formatstr.replace('_',' ');
        } else {
            int len=rawstr.length();
            if (len==8) formatstr="ddMMyyyy";
            if (len==6) formatstr="HHmmss";
            if (len==14) formatstr="HHmmssddMMyyyy";
        }

        SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateTimeInstance();
        TimeZone tz;
        //df.applyLocalizedPattern("yyyyMMdd");
        df.applyLocalizedPattern(formatstr);

        tz=TimeZone.getDefault() ;
        df.setTimeZone(tz);

        Date date = null;
        try {
            date = df.parse(rawstr);
        } catch( java.text.ParseException e ) {
            log.error(e.toString());
        }

        if( date != null) {
            return ""+(int)((date.getTime()-DateSupport.getMilliOffset())/1000);
        } else {
            return "";
        }
    }

    /**
     * Formats either the current or a given timevalue according to a specified format.
     * Cmd arguments are an optional timevalue and a format (default HH:MM:ss).
     * @param tok the processing command's arguments
     * @param inSec if <code>true</code>, the timevalue is in seconds instead of milliseconds
     * @return a <code>String</code> containing the time in the specified format
     */
    String doTimeFormat(StringTokenizer tok, boolean inSec)    {
        String format = "HH:mm:ss";
        long timeInMs = System.currentTimeMillis();
        while (tok.hasMoreTokens())
        {    String tmp = tok.nextToken();
            if ((tmp.charAt(0) >= '0') && (tmp.charAt(0) <= '9'))
            {    if (inSec)
                    timeInMs = (Long.decode(tmp + "000")).longValue();
                else
                    timeInMs = (Long.decode(tmp)).longValue();
                if (tok.hasMoreTokens()) format = tok.nextToken();
            }
            else
            {    // Time parameter is skipped, use current time.
                format = tmp;
            }
            // If there are more tokens add them to the format because in that case the format contains '-'.
            while (tok.hasMoreTokens()) format += "-" + tok.nextToken();
            format=format.replace('_', ' ');
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String result = simpleDateFormat.format(new Date(timeInMs));
        return result;
    }


    /**
     * Performs tests on strings. Cmd arguments are:
     * EQUALS-val-compareVal : checks whether two strings are the same
     * STARTSWITH-val-compareVal(-toffset) or LEFTSTRING : checks whether one strings starts with another
     * ENDSWITH-val-compareVal or RIGHTSTRING : checks whether one strings ends with another
     * INDEXOF-val-compareVal or CONTAINS : checks whether one string contains another
     * @param tok StringTokenizer with the rest of the cmd.
     * @return A string conmtaining the value <code>true</code> if the test succeeds
     */
    String doString(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (tok.hasMoreTokens()) {
                String val = tok.nextToken();
                if ((tok.hasMoreTokens())||(!val.equals(""))) {
                    String compareVal = tok.nextToken();
                    if (cmd.equals("STARTSWITH")||cmd.equals("LEFTSTRING")) {
                        if (tok.hasMoreTokens()) {
                            int toffset = 0;
                            try { toffset = Integer.parseInt(tok.nextToken());
                            } catch (NumberFormatException nfe) {
                                log.error(""+nfe);
                                return "Error in "+cmd+" offset arg";
                            }
                            return ""+val.startsWith(compareVal,toffset);
                        }
                        return ""+val.startsWith(compareVal);
                    } else if (cmd.equals("ENDSWITH")||cmd.equals("RIGHTSTRING")) {
                        return ""+val.endsWith(compareVal);
                    } else if (cmd.equals("EQUALS")) {
                        return ""+val.equals(compareVal);
                    } else if (cmd.equals("INDEXOF")||cmd.equals("CONTAINS")) {
                        return ""+(val.indexOf(compareVal)!=-1);
                    } else { return ("Unknown String cmd "+cmd);
                    }
                } else { return ("Syntax error, $MOD-INFO-"+cmd+"-"+val);
                }
            } else { return "Syntax error, $MOD-INFO-"+cmd+"-";
            }
        } else { return "Syntax error, $MOD-INFO-";
        }
    }

    /**
     * Converts a string into a string with 'escaped' quotes.<br />
     * The argument for this command is the string to 'escape'.
     * @param sp the current page context
     * @param tok the StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> which is the converted value
     */
    String doEscape(PageInfo sp, StringTokenizer tok) {
        String result=null;
        while (tok.hasMoreTokens()) {
            String tmp=tok.nextToken();
            if (result==null) {
                result=tmp;
            } else {
                result+="-"+tmp;
            }
        }
        return Encode.encode("ESCAPE_SINGLE_QUOTE", result);
    }


    /**
     * Converts an ASCII string into a URL-encoded string.<br />
     * The argument for this command is the string to encode.
     * @param sp the current page context
     * @param tok the StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> which is the converted value
     */
    String doParamEncode(PageInfo sp, StringTokenizer tok) {
        String result=null;
        while (tok.hasMoreTokens()) {
            String tmp=tok.nextToken();
            if (result==null) {
                result=tmp;
            } else {
                result+="-"+tmp;
            }
        }
        return URLParamEscape.escapeurl(result);
    }


    /**
     * Converts an URL-encoded string into a ASCII string.<br />
     * The argument for this command is the string to decode.
     * @param sp the current page context
     * @param tok the StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> which is the converted value
     */
    String doParamDecode(PageInfo sp, StringTokenizer tok) {
        String result=null;
        while (tok.hasMoreTokens()) {
            String tmp=tok.nextToken();
            if (result==null) {
                result=tmp;
            } else {
                result+="-"+tmp;
            }
        }
        return URLParamEscape.unescapeurl(result);
    }


    /**
     * Retrieve the name of the user's operating system.
     * This command takes no arguments
     * @param sp the current page context
     * @param tok the StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> which is the converted value
     */
    String doOs(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            return "Illegal OS command";
        } else {
            String tmp=sp.req.getHeader("User-Agent");
            tmp = tmp.toLowerCase();
            if (tmp.indexOf("windows 95")!=-1 || tmp.indexOf("win95")!=-1) {
                return "WIN95";
            } else if (tmp.indexOf("win98")!=-1) {
                return "Windows 98";
            } else if (tmp.indexOf("windows nt 5.0")!=-1) {
                return "Windows 2000";
            } else if (tmp.indexOf("winnt")!=-1 || tmp.indexOf("windows nt")!=-1) {
                return "Windows NT";
            } else if (tmp.indexOf("win")!=-1) {
                return "Windows";
            } else if (tmp.indexOf("mac")!=-1) {
                return "MAC";
            } else if (tmp.indexOf("sun")!=-1) {
                return "Unix";
            } else if (tmp.indexOf("irix")!=-1) {
                return "Irix";
            } else if (tmp.indexOf("freebsd")!=-1) {
                return "FreeBSD";
            } else if (tmp.indexOf("hp-ux")!=-1) {
                return "HP Unix";
            } else if (tmp.indexOf("aix")!=-1) {
                return "AIX";
            } else if (tmp.indexOf("linux")!=-1) {
                return "Linux";
            }
            return "Unknown OS";
        }
    }


    /**
     * Returns a random number in a specified range.
     * The command arguments are the start and end of the numerical range in which the random number should fall.
     * @param sp the current page context
     * param tok the StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> containing a random number
     */
    String doRandom(PageInfo sp, StringTokenizer tok) {
    int j=0;
    int s=0;
    int e=0;
    if (tok.hasMoreTokens()) {
        String start=tok.nextToken();
        if (tok.hasMoreTokens()) {
            String end=tok.nextToken();
            try {
                s=Integer.parseInt(start);
                e=Integer.parseInt(end);
                j=Math.abs(rnd.nextInt()%(e-s));
            } catch (Exception f) {}
        }
    }
    return ""+(s+j);
    }


    /**
     * Returns data about the user's browser.<br />
     * Valid options are: <br />
     * OS : returns the operating system.
     * HTTP : returns the requested HTTP-header.
     * WANTEDHOST : returns the host name.
     * NAME : returns the name of the current browser
     * NETSCAPE : returns YES is the current browser is netscape navigator, NO otherwise
     * MSIE : returns YES is the current browser is internet explorer, NO otherwise
     * @param sp the current page context
     * @param tok The StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> containing the result
     */
    String doBrowser(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("OS")) {
                return doOs(sp,tok);
            }
            if (cmd.equals("HTTP")) {
                if (tok.hasMoreTokens()) {
                    cmd=tok.nextToken();
                    while (tok.hasMoreTokens()) {
                        cmd+="-"+tok.nextToken();
                    }
                    return sp.req.getHeader(cmd);
                } else {
                    return "Illegal browser command";
                }
            }
            if (cmd.equals("WANTEDHOST")) {
                return sp.req.getHeader("Host");
            }
            if (cmd.equals("NAME")) {
                return sp.req.getHeader("User-Agent");
            }
            String br=sp.req.getHeader("User-Agent");
            if (cmd.equals("NETSCAPE")) {
                return toYesNo(br.indexOf("Mozilla")==0 && br.indexOf("MSIE")==-1);
            }
            if (cmd.equals("MSIE")) {
                return toYesNo(br.indexOf("MSIE")!=-1);
            }
            return "Illegal browser command";
        } else {
            return sp.req.getHeader("User-Agent");
        }
    }

    /** Returns information on the user
     * Valid options are:<br />
     * NAME, which returns the username in SCAN<br />
     * HOSTNAME, which returns the name of the remote host (visiting) in SCAN<br />
     * IPNUMBER, which returns the ipnumber of the remote host(visiting) in SCAN        <br />
     * SECLEVEL, which returns current security level in SCAN        <br />
     * REQUEST_URI, which returns the path of the file requested in SCAN<br />
     * BACK, which returns the name of the page visted befote the current page, notice not supported by all browsers<br />
     * COUNTRY, which returns the country name of the remote host ( mmbase.nl -> nl; mmbase.org-> org )<br />
     * DOMAIN, which returns the domain name of the remote host<br />
     * INDOMAIN, which returns YES when remote host has the same domain as us otherwise it returns NO in SCAN    <br />
     * @param tok StringTokenizer with the rest of the cmd.
     * @param sp the scanpage
     * @return a String containing cmd result.
     * @author Eduard Witteveen 08-11-2000
     */
     // http://uk.php.net/manual/language.variables.predefined.php <-- a few defines from php
    String doUser(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("NAME")) return HttpAuth.getRemoteUser(sp.req);
            if (cmd.equals("SESSIONNAME")) return ((scanpage) sp).getSessionName();
            if (cmd.equals("HOSTNAME")) return sp.req.getRemoteHost();
            if (cmd.equals("REQUEST_URI")) return sp.req.getRequestURI();
            if (cmd.equals("SECLEVEL")) return sp.req.getAuthType();
            if (cmd.equals("IPNUMBER")) return sp.req.getRemoteAddr();
            if (cmd.equals("BACK")) {
                String tmp=sp.req.getHeader("Referer");
                if (tmp!=null) {
                    return tmp;
                } else {
                    return "";
                }
            }
            if (cmd.equals("COUNTRY")) {
                String tmp=sp.req.getRemoteHost();
                if (tmp!=null) {
                    String domain = tmp.substring(tmp.lastIndexOf('.')+1);
                    if (domain!=null) {
                        return domain;
                    } else {
                        return "";
                    }
                }
            }
            if (cmd.equals("DOMAIN")) {
                String tmp=sp.req.getRemoteHost();
                if (tmp!=null && tmp.indexOf('.')!=-1) {
                    String domain = tmp.substring(tmp.lastIndexOf('.'));
                    tmp = tmp.substring(0,tmp.lastIndexOf('.'));
                    tmp = tmp.substring(tmp.lastIndexOf('.')+1);
                    domain = tmp+domain;
                    if (domain!=null) {
                        return domain;
                    }
                }
                return "";
            }
            if (cmd.equals("INDOMAIN")) {
                String tmp=sp.req.getRemoteHost();
                if (tmp!=null && tmp.indexOf('.')!=-1) {
                    String domain = tmp.substring(tmp.lastIndexOf('.'));
                    tmp = tmp.substring(0,tmp.lastIndexOf('.'));
                    tmp = tmp.substring(tmp.lastIndexOf('.')+1);
                    domain = tmp + domain;
                    if (domain!=null) {
                        String serverdomain = getProperty("server","Domain");
                        return toYesNo(serverdomain.equals(domain));
                    }
                    return toYesNo(false);
                } else {
                    String servername = getProperty("server","MachineName");
                    return toYesNo(servername.equals(tmp));
                }
            }
            return "Illegal user command";
        } else {
            return HttpAuth.getRemoteUser(sp.req);
        }
    }
    /**
     * Returns the properties to the subclass.
     */
    protected Map getProperties(String propertytable) {
         return null;
     }

    /**
     * Returns a continues range of values with two set numerical boundaries and a step-increase, or
     * the range of characters of the alphabet. <br />
     * i.e. RANGE-60-100-10 returns 60,70,80,90,100 <br />
     * RANGE-ALPHA returns A,B,C,....Y.Z
     * @param tok The StringTokenizer containing the subsequent cmd argument tokens.
     * @return a <code>String</code> containing the result
     */
    Vector doRange(StringTokenizer tok) {
        Vector results = new Vector();
        String firstToken="";
        int start=1;
        int end=10;
        int step=1;
        try {
            // check for numerical boundaries (defaults are 1 and 10)
            if (tok.hasMoreTokens()) {
                firstToken=tok.nextToken();
                start=Integer.parseInt(firstToken);
                if (tok.hasMoreTokens()) {
                    end=Integer.parseInt(tok.nextToken());
                    if (tok.hasMoreTokens()) {
                        step=Integer.parseInt(tok.nextToken());
                    }
                    for (int i=start;i<=end;i+=step) {
                        results.addElement(""+i);
                    }
                }
            }
        } catch (Exception e) {
            if (firstToken.equals("ALPHA")) {  // return the alphabet
                for (int i='A';i<='Z';i++) {
                    results.addElement(""+(char)i);
                }
            }
        }
        return results;
    }

    /**
     * Returns a list of color names and their RGB values. <br />
     * COLOR-BASIC returns a list of basic colors. <br />
     * COLOR-PRIMARY returns a list of primary colors. <br />
     * No other options are yet implemented
     * @param tok The commands to be executed
     * @return a <code>Vector</code> containing color names and RGB values
     */
    Vector doColor(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("BASIC")) {
                return doColorBasic();
            } else if (cmd.equals("SPECTRUM")) {
                return doColorSpectrum();
            } else if (cmd.equals("WINDOWS") || cmd.equals("16")) {
                return doColor16();
            } else {
                return null;
            }
        }
        return doColorSpectrum();
    }

    /**
     * Returns a list of basic color names and their RGB values. <br />
     * These include the basic RGB colors, as well as mint-blue and mint-green.
     * @return a <code>Vector</code> containing color names and RGB values
     */
    Vector doColorBasic() {
        Vector results = new Vector();
        results.addElement("black");results.addElement("000000");
        results.addElement("white");results.addElement("FFFFFF");
        results.addElement("red");results.addElement("FF0000");
        results.addElement("green");results.addElement("00FF00");
        results.addElement("blue");results.addElement("0000FF");
        results.addElement("mint-blue");results.addElement("31FFCE");
        results.addElement("mint-green");results.addElement("20FFFFF");
        return results;
    }

    /**
     * Returns a list of primary and secondary color names and their RGB values. <br />
     * @return a <code>Vector</code> containing color names and RGB values
     */
    Vector doColorSpectrum() {
        Vector results = new Vector();
        results.addElement("white");results.addElement("FFFFFF");
        results.addElement("purple");results.addElement("800080");
        results.addElement("red");results.addElement("FF0000");
        results.addElement("orange");results.addElement("FFA500");
        results.addElement("yellow");results.addElement("FFFF00");
        results.addElement("green");results.addElement("008000");
        results.addElement("blue");results.addElement("0000FF");
        results.addElement("black");results.addElement("000000");
        return results;
    }

    /**
     * Returns a list of the 16 windows color names and their RGB values. <br />
     * @return a <code>Vector</code> containing color names and RGB values
     */
    Vector doColor16() {
        Vector results = new Vector();
        results.addElement("white");results.addElement("FFFFFF");
        results.addElement("black");results.addElement("000000");
        results.addElement("red");results.addElement("FF0000");
        results.addElement("lime");results.addElement("00FF00");
        results.addElement("blue");results.addElement("0000FF");
        results.addElement("magenta");results.addElement("FF00FF");
        results.addElement("yellow");results.addElement("FFFF00");
        results.addElement("cyan");results.addElement("00FFFF");
        results.addElement("silver");results.addElement("C0C0C0");
        results.addElement("gray");results.addElement("808080");
        results.addElement("maroon");results.addElement("800000");
        results.addElement("green");results.addElement("008000");
        results.addElement("navy");results.addElement("000080");
        results.addElement("purple");results.addElement("800080");
        results.addElement("olive");results.addElement("808000");
        results.addElement("teal");results.addElement("008080");
        return results;
    }

    /**
     * Formats either the current or a given timevalue according to a specified format.
     * Cmd arguments are an optional timevalue, a format, and the timepart to return.
     * @deprecation uses Date instead of Calendar
     * @param tok the processing command's arguments
     * @param inSec if <code>true</code>, the timevalue is in seconds instead of milliseconds
     * @return a <code>String</code> containing the time in the specified format
     */
    String doTime(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken(),rtn="";

            Date d=new Date(System.currentTimeMillis());
            TimeZone tz = TimeZone.getTimeZone("ECT"); //Rob
            GregorianCalendar calendar = new GregorianCalendar(tz); //Rob
            // extra hack to check if the first param is a number
            // so we can have given times instead of epoch
            try {
                int i=Integer.parseInt(cmd);
                d=new Date(((long)i)*1000);
                calendar.setTime(d);
                cmd=tok.nextToken();
            } catch(Exception e) {
                // no problem it was probably not a number
            }
            int ctime=(int)(calendar.getTime().getTime()/1000);

            int whichname=INFO.Not;

            if (cmd.equals("NAME") || cmd.equals("ENGLISH")) {
                whichname=INFO.English;
                if (tok.hasMoreTokens()) cmd=tok.nextToken();
            } else if (cmd.equals("DUTCHNAME") || cmd.equals("DUTCH")) {
                whichname=INFO.Dutch;
                if (tok.hasMoreTokens()) cmd=tok.nextToken();
            } else if (cmd.equals("NUMBER")) {
                whichname=INFO.Not;
                if (tok.hasMoreTokens()) cmd=tok.nextToken();
            } else {
                whichname=INFO.Not;
            }
            if (cmd.equals("TIME")) {
                int getminutes = calendar.get(Calendar.MINUTE);
                if (getminutes<10) {
                    return ""+calendar.get(Calendar.HOUR_OF_DAY)+":0"+getminutes;
                } else {
                    return ""+calendar.get(Calendar.HOUR_OF_DAY)+":"+getminutes;
                }
            }

            int days = calendar.get(Calendar.DAY_OF_YEAR);

            if (cmd.equals("CURTIME")) {
                if (tok.hasMoreTokens()) {
                    return nextCurTime(tok);
                } else {
                    return ""+System.currentTimeMillis()/1000;
                }
            }
            if (cmd.equals("DCURTIME")) return ""+System.currentTimeMillis()/1000;
            if (cmd.equals("CURTIME10")) return ""+System.currentTimeMillis()/(10*1000);
            if (cmd.equals("CURTIME20")) return ""+System.currentTimeMillis()/(20*1000);

            // YEAR
            if (cmd.equals("YEAR")) return ""+calendar.get(Calendar.YEAR);
            if (cmd.equals("SHORTYEAR")) {
                int getyear = calendar.get(Calendar.YEAR)%100;
                if(getyear<10) {
                    return "0"+getyear;
                } else {
                    return ""+getyear;
                }
            }

            //MONTH
            if (cmd.equals("MONTH") || cmd.equals("SHORTMONTH")) {
                int getmonth = calendar.get(Calendar.MONTH);
                switch(whichname) {
                    case INFO.Not:
                        rtn=""+(++getmonth);
                        break;
                    case INFO.English:
                        if (cmd.equals("MONTH")) {
                            rtn=DateStrings.ENGLISH_DATESTRINGS.getMonth(getmonth);
                        } else {
                            rtn=DateStrings.ENGLISH_DATESTRINGS.getShortMonth(getmonth);
                        }
                        break;
                    case INFO.Dutch:
                        if (cmd.equals("MONTH")) {
                            rtn=DateStrings.DUTCH_DATESTRINGS.getMonth(getmonth);
                        } else {
                            rtn=DateStrings.DUTCH_DATESTRINGS.getShortMonth(getmonth);
                        }
                        break;
                }
                return rtn;
            }

            //MONTHS
            if (cmd.equals("MONTHS") || cmd.equals("SHORTMONTHS")) {
                rtn="";
                int year,month,months;
                Calendar cal=null;
                String tk;
                int w=0;

                if (whichname!=INFO.Not) {
                    int imonth;
                    if (tok.hasMoreTokens()) {
                        tk=tok.nextToken();
                        if (tk.equals("YEAR")) {
                            tk=tok.nextToken();
                            w=1;
                        } else if (tk.equals("MONTH")) {
                            tk=tok.nextToken();
                            w=2;
                        }
                        try {
                            imonth=Integer.parseInt(tk);
                        } catch (NumberFormatException nfe) {
                            imonth=0;
                        }
                    } else {
                        imonth=0;
                    }
                    cal=getCalendarMonths(imonth);
                }
                switch(whichname) {
                    case INFO.Not:
                        year=calendar.get(Calendar.YEAR)-1970;
                        month=calendar.get(Calendar.MONTH);
                        months=month+year*12;
                        rtn=""+months;
                        break;
                    case INFO.English:
                        switch(w) {
                            case 1:
                                month=cal.get(Calendar.YEAR);
                                rtn=""+month;
                                break;
                            case 2:
                                month=cal.get(Calendar.MONTH);
                                if (cmd.equals("MONTHS")) {
                                    rtn=DateStrings.ENGLISH_DATESTRINGS.getMonth(month);
                                } else {
                                    rtn=DateStrings.ENGLISH_DATESTRINGS.getShortMonth(month);
                                }
                                break;
                            default:
                                month=cal.get(Calendar.MONTH);
                                if (cmd.equals("MONTHS")) {
                                    rtn=DateStrings.ENGLISH_DATESTRINGS.getMonth(month);
                                } else {
                                    year=cal.get(Calendar.YEAR);
                                    rtn=DateStrings.ENGLISH_DATESTRINGS.getShortMonth(month)+" "+year;
                                }
                                break;
                        }
                        break;
                    case INFO.Dutch:
                        switch(w) {
                            case 1:
                                month=cal.get(Calendar.YEAR);
                                rtn=""+month;
                                break;
                            case 2:
                                month=cal.get(Calendar.MONTH);
                                if (cmd.equals("MONTHS")) {
                                    rtn=DateStrings.DUTCH_DATESTRINGS.getMonth(month);
                                } else {
                                    rtn=DateStrings.DUTCH_DATESTRINGS.getShortMonth(month);
                                }
                                break;
                            default:
                                month=cal.get(Calendar.MONTH);
                                if (cmd.equals("MONTHS")) {
                                    rtn=DateStrings.DUTCH_DATESTRINGS.getMonth(month);
                                } else {
                                    year=cal.get(Calendar.YEAR);
                                    rtn=DateStrings.DUTCH_DATESTRINGS.getShortMonth(month)+" "+year;
                                }
                                break;
                        }
                        break;
                }

                return rtn;
            }

            //WEEK
            if (cmd.equals("WEEK")) return ""+((days/7)+1);
            if (cmd.equals("WEEKOFYEAR")) return ""+calendar.get(Calendar.WEEK_OF_YEAR);
            if (cmd.equals("WEEKOFMONTH")) return ""+calendar.get(Calendar.WEEK_OF_MONTH);

            // DAY
            if (cmd.equals("DAY") || cmd.equals("DAYOFMONTH")) return ""+calendar.get(Calendar.DAY_OF_MONTH);
            if (cmd.equals("WEEKDAY") || cmd.equals("DAYOFWEEK") || cmd.equals("SHORTDAYOFWEEK")) {
                int getday = calendar.get(Calendar.DAY_OF_WEEK);
                switch(whichname) {
                    case INFO.Not:
                        rtn=""+getday;
                        break;
                    case INFO.English:
                        if(cmd.equals("SHORTDAYOFWEEK")) {
                            rtn=DateStrings.ENGLISH_DATESTRINGS.getShortDay(--getday);
                        } else {
                            rtn=DateStrings.ENGLISH_DATESTRINGS.getDay(--getday);
                        }
                        break;
                    case INFO.Dutch:
                        if (cmd.equals("SHORTDAYOFWEEK")) {
                            rtn=DateStrings.DUTCH_DATESTRINGS.getShortDay(--getday);
                        } else {
                            rtn=DateStrings.DUTCH_DATESTRINGS.getDay(--getday);
                        }
                        break;
                }
                return rtn;
            }
            if (cmd.equals("DAYOFWEEKINMONTH")) return ""+calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            if (cmd.equals("YDAY") || cmd.equals("DAYOFYEAR")) return ""+calendar.get(Calendar.DAY_OF_YEAR);

            //HOUR
            if (cmd.equals("HOUR")) return ""+calendar.get(Calendar.HOUR);
            if (cmd.equals("HOUROFDAY")) return ""+calendar.get(Calendar.HOUR_OF_DAY);

            //MINUTES
            if (cmd.equals("MIN") || cmd.equals("MINUTE")) return ""+calendar.get(Calendar.MINUTE);

            //SECONDS
            if (cmd.equals("SEC") || cmd.equals("SECOND")) return ""+calendar.get(Calendar.SECOND);


            if (cmd.equals("PREVWEEK")) {
                long tmp=days/7;
                if (tmp<1) tmp=52;
                if (tmp>52) tmp=1;
                return ""+tmp;
            }
            if (cmd.equals("GWEEK")) return ""+(((days+3)/7)); // +3 days
            if (cmd.equals("PREVGWEEK")) {
                long tmp=((days+3)/7)-1; // +3 days
                if (tmp<1) tmp=52;
                if (tmp>52) tmp=1;
                return ""+tmp;
            }
            if (cmd.equals("NEXTWEEK")) {
                long tmp=(days/7)+2;
                if (tmp==53) tmp=1;
                if (tmp==54) tmp=2;
                return ""+tmp;
            }
            if (cmd.equals("NEXTGWEEK")) {
                long tmp=((days+3)/7)+1; // +3days
                if (tmp==53) tmp=1;
                if (tmp==54) tmp=2;
                return ""+tmp;
            }
            if (cmd.equals("WEEKDATE")) {
                String sday;
                int iday,iweek;
                iweek=(days/7)+1;
                if (tok.hasMoreTokens()) {
                    sday=tok.nextToken();
                } else {
                    sday="1";
                }
                try {
                    iday=Integer.parseInt(sday);
                } catch (NumberFormatException e) {
                    iday=1;
                }
                iday-=2;
                if (iday<0) {
                    iweek--;
                    iday=6;
                }
                d=DateSupport.Date(d.getYear(),iweek,iday);
                rtn=d.getDate()+" "+DateStrings.DUTCH_DATESTRINGS.getMonth(d.getMonth());
                return rtn;
            }
            if (cmd.equals("NEXTWEEKDATE")) {
                String sday;
                int iday,iweek;

                iweek=(days/7)+2;
                if (tok.hasMoreTokens()) {
                    sday=tok.nextToken();
                } else {
                    sday="1";
                }
                try {
                    iday=Integer.parseInt(sday);
                } catch (NumberFormatException e) {
                    iday=1;
                }
                iday-=2;
                if (iday<0) {
                    iweek--;
                    iday=6;
                }
                d=DateSupport.Date(d.getYear(),iweek,iday);
                rtn=d.getDate()+" "+DateStrings.DUTCH_DATESTRINGS.getMonth(d.getMonth());
                return rtn;
            }
            if (cmd.equals("GWEEKDATE")) {
                String sday;
                int iday,iweek;
                iweek=((days+3)/7)+1;
                if (tok.hasMoreTokens()) {
                    sday=tok.nextToken();
                } else {
                    sday="1";
                }
                try {
                    iday=Integer.parseInt(sday);
                } catch (NumberFormatException e) {
                    iday=1;
                }
                iday-=2;
                if (iday<0) {
                    iweek--;
                    iday=6;
                }
                d=DateSupport.Date(d.getYear(),iweek,iday);
                rtn=d.getDate()+" "+DateStrings.DUTCH_DATESTRINGS.getMonth(d.getMonth());
                return rtn;
            }
            if (cmd.equals("NEXTGWEEKDATE")) {
                String sday;
                int iday,iweek;
                iweek=((days+3)/7)+2;
                if (tok.hasMoreTokens()) {
                    sday=tok.nextToken();
                } else {
                    sday="1";
                }
                try {
                    iday=Integer.parseInt(sday);
                } catch (NumberFormatException e) {
                    iday=1;
                }
                iday-=2;
                if (iday<0) {
                    iweek--;
                    iday=6;
                }
                d=DateSupport.Date(d.getYear(),iweek,iday);
                rtn=d.getDate()+" "+DateStrings.DUTCH_DATESTRINGS.getMonth(d.getMonth());
                return rtn;
            } else if (cmd.equals("WEEKCURTIME")) {
                Date d2=new Date((long)ctime*1000);
                int day=d2.getDay();
                int hours=d2.getHours();
                int min=d2.getMinutes();
                int sec=d2.getSeconds();
                ctime-=((day+1)*86400);
                ctime-=(hours*3600);
                ctime-=(min*60);
                ctime-=(sec);
                return ""+ctime;
            } else if (cmd.equals("DAYCURTIME")) {
                Date d2=calendar.getTime();
                int hours=d2.getHours();
                int min=d2.getMinutes();
                int sec=d2.getSeconds();
                ctime-=(hours*3600);
                ctime-=(min*60);
                ctime-=(sec);
                return ""+ctime;
            }


            return "Illegal date command";
        } else {
            return new Date(System.currentTimeMillis()).toString();
        }
    }

    /**
     * Returns a description of the module.
     * @return a <code>String</code> describing the module's function.
     */
    @Override public String getModuleInfo() {
        return "Support routines for servscan, Daniel Ockeloen";
    }

    /**
     * This method is used to retrieve time related info from a relative time value.
     * Valid commands are :
     * GET/GETTIME-timeValueInMillis : time passed since the indicated time
     * GETHOURS-timeValueInMillis : hours passed since the indicated time
     * GETMINUTES-timeValueInMillis : minutes passed since the indicated time
     * GETSECONDS-timeValueInMillis : seconds passed since the indicated time
     * GETMILLIS-timeValueInMillis : milliseconds passed since the indicated time
     * COUNTMILLIS-hourValue-minuteValue-secondValue-milliValue : milisecodms past siunce the indicated time
     * @param tok The StringTokenizer containing the subsequent cmd argument tokens.
     * @return A String containing cmd result.
     */
    String doRelTime(StringTokenizer tok) {
        int timeValue = 0;
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();

            // Check commandname.
            if (cmd.equals("COUNTMILLIS")) {
                if (tok.hasMoreTokens() && (tok.countTokens()==4)) {
                    String value = tok.nextToken()+":"+tok.nextToken()+":"+tok.nextToken()+"."+tok.nextToken();
                    timeValue = RelativeTime.convertTimeToInt(value);
                    if (log.isDebugEnabled()) {
                        log.debug("doRelTime -> COUNTMILLIS result= " +timeValue);
                    }
                    return (""+timeValue);
                } else {
                    String error = "doRelTime: Error, Amount of timeValues is != 4 (h,m,s,ms)";
                    log.error(error);
                    return error;
                }
            } else if (cmd.startsWith("GET")) {

                if (tok.hasMoreTokens()) {
                    try {
                        timeValue = Integer.parseInt(tok.nextToken());
                    } catch (NumberFormatException nfe) {
                        log.warn("doRelTime: Invalid timeValue specified. "+nfe);
                        return "INFO::doRelTime: Invalid timeValue specified timeValue="+timeValue;
                    }
                } else {
                    return "INFO::doRelTime: No timeValue specified";
                }
                if (log.isDebugEnabled()) {
                    log.debug("doRelTime: "+cmd+"-"+timeValue+" result= "+RelativeTime.getHours(timeValue));
                }
                if (cmd.equals("GETHOURS")) {
                    return (""+RelativeTime.getHours(timeValue));
                } else if (cmd.equals("GETMINUTES")) {
                    return (""+RelativeTime.getMinutes(timeValue));
                } else if (cmd.equals("GETSECONDS")) {
                return (""+RelativeTime.getSeconds(timeValue));
                } else if (cmd.equals("GETMILLIS")) {
                    return (""+RelativeTime.getMillis(timeValue));
                } else {
                    return (""+RelativeTime.convertIntToTime(timeValue));
                }
            } else {
                return "INFO::doRelTime: Undefined command specified, command= "+cmd;
            }
        } else {
            return "INFO::doRelTime: No command specified";
        }
    }

    /**
     * This method is used to retrieve the amount of FREE MEMORY in either the JVM or the SYSTEM.<br />
     * Valid options are: <br />
     * GETJVM (default) : returns free memory of the Java Virtual Machine
     * GETSYS : return the free memory on the system
     * B(default) :return memory in bytes
     * KB: :return memory in kilo bytes
     * MB: :return memory in mega bytes
     * @param tok The StringTokenizer containing the subsequent cmd argument tokens.
     * @return A String containing the available memory.
     */
    String doMemory(StringTokenizer tok) {
        int whichMem = 0;
        float memDiv = 0.0f;
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            // Check commandname.
            if (cmd.equals("GETJVM")) {
                whichMem = 0;
            } else if (cmd.equals("GETSYS")) {
                whichMem = 1;
            } else {
                log.error("doMemory: Undefined command requested -> "+cmd);
                return "INFO::doMemory: Undefined command requested -> "+cmd;
            }
            if (tok.hasMoreTokens()) {
                cmd = tok.nextToken();
                if (cmd.equals("MB"))      memDiv = 1048576.0f;
                else if (cmd.equals("KB")) memDiv = 1024.0f;
            }
        }
        Runtime rt = Runtime.getRuntime();

        if (memDiv < 1.0f) {
            if (whichMem == 0)
                return (""+rt.totalMemory());
            else
                return (""+rt.freeMemory());
        } else {
            if (whichMem == 0)
                return (""+(rt.totalMemory()/memDiv));
            else
                return (""+(rt.freeMemory()/memDiv));
        }
    }

    /**
     * Returns a list of dates (date, month,day, day-of-week) of all directories in a given path with a file length of 10 characters.
     * Requires tags are base (path) and start (date)
     * Optional tags are end (date), STARTINCLUDED, ENDINCLUDED and REVERS (booleans)
     * @param sp the current page context
     * @param tok the commands to be executed
     * @return a <code>Vector</code> containing color names and RGB values
     * @deprecated hereditary code. Should be dropped or adapted.
     */
    Vector doScanDate(PageInfo sp,StringTagger tagger) {
        String temp = sp.req.getHeader("Pragma");
        if (temp!=null && temp.indexOf("no-cache")!=-1) {
            DirCache=new Hashtable();
        }

        Vector results=new Vector();

        // get base
        String base=tagger.Value("BASE");
        if (base==null) return results;
           base = Strip.doubleQuote(base,Strip.BOTH);

        // find the start
        Date start=null;
        String val=tagger.Value("START");
        if (val!=null) {
            val = Strip.doubleQuote(val,Strip.BOTH);
            start=DateSupport.parsedbmdate(val);
        }

        // find the end
        Date end=null;
        val=tagger.Value("END");
        if (val!=null) {
            val = Strip.doubleQuote(val,Strip.BOTH);
            end=DateSupport.parsedbmdate(val);
        }

        // start included
        boolean startincluded=true;
        val=tagger.Value("STARTINCLUDED");
        if (val!=null) {
            val = Strip.doubleQuote(val,Strip.BOTH);
            startincluded=isNotNo(val);
        }

        // end included
        boolean endincluded=true;
        val=tagger.Value("ENDINCLUDED");
        if (val!=null) {
            val = Strip.doubleQuote(val,Strip.BOTH);
            endincluded=isNotNo(val);
        }

        // revers list
        boolean revert=false;
        val=tagger.Value("REVERS");
        if (val!=null) {
            val = Strip.doubleQuote(val,Strip.BOTH);
            revert = isYes(val);
        }

        // scan the disk
        File scanfile = new File(documentroot+base);
        //debug(documentroot+base);
        SortedVector fullres=DirCache.get(documentroot+base);
        if (fullres==null) {
            fullres=getDirTimes(scanfile);
            DirCache.put(documentroot+base,fullres);
            //debug(DirCache.toString());
        }

        if (start!=null) {
            long val2=start.getTime();
            if (!startincluded) val2++; // shift start to exclude start
            long val3;
            if (revert) fullres=revertVector(fullres);
            if (end!=null) {
                long val4=end.getTime();
                if (!endincluded) val4--; // shift end to exclude start
                Date rd;
                for (Enumeration e=fullres.elements();e.hasMoreElements();) {
                    val3=Long.parseLong((String)e.nextElement());
                    if (val3>=val2 && val3<=val4) {
                        rd=new Date(val3);
                        results.addElement(DateSupport.makedbmdate(rd));
                        results.addElement(DateStrings.DUTCH_DATESTRINGS.getMonth(rd.getMonth()));
                        results.addElement(""+rd.getDate());
                        results.addElement(DateStrings.DUTCH_DATESTRINGS.getMonth(rd.getDay()));
                    }
                }
            } else {
                Date rd;
                for (Enumeration e=fullres.elements();e.hasMoreElements();) {
                    val3=Long.parseLong((String)e.nextElement());
                    if (val3>=val2) {
                        rd=new Date(val3);
                        results.addElement(DateSupport.makedbmdate(rd));
                        results.addElement(DateStrings.DUTCH_DATESTRINGS.getMonth(rd.getMonth()));
                        results.addElement(""+rd.getDate());
                        results.addElement(DateStrings.DUTCH_DATESTRINGS.getMonth(rd.getDay()));
                    }
                }
            }
        }
        tagger.setValue("ITEMS","4");
        return results;
    }

    /**
     * Reverse the order of a list of values
     * @param src the source of values to reverse
     * @return a <code>Vector</code> containing the reverse ordered list
     */
    SortedVector revertVector(SortedVector src) {
        SortedVector dst=new SortedVector();
        for (int i=0;i<src.size();i++) {
            dst.insertElementAt(src.elementAt(i),0);
        }
        return dst;
    }

    /**
     * Retrieves the creation times of all directories under a specific path
     * whose names are 10 characters long.
     * @param scanfile the path to search
     * @return a <code>Vector</code> containing the times
     */
    SortedVector getDirTimes(File scanfile) {
        SortedVector results;
        // scan the disk
        results=new SortedVector();
        File theFile;
        Date d;
        String theFileName;
        String files[] = scanfile.list();
        if (files!=null) {
            for (String element : files) {
                theFileName=element;
                theFile = new File(scanfile,theFileName);
                if (theFile.isDirectory() && theFileName.length()==10) {
                    d=DateSupport.parsedbmdate(theFileName);
                    results.addSorted(""+d.getTime());
                }
            }
        }
        return results;
    }

    /**
     * Tests whether a given filename exists either as a directory, as a file, or as a path (depending on the subcommand given).
     * Subcommands are DIR, FILE, and PATH. This subcommand need be followed by a filename.
     * @param sp the current page context
     * @param tok the commands to be executed
     * @return a <code>String</code> withe the value 'YES' if the check succeeded, 'NO' if it failed.
     */
    protected String doExists(PageInfo sp,StringTokenizer tok) {
        String type=tok.nextToken();
        String path=tok.nextToken();
        boolean rtn=false;

        /* fully quallify path */
        if (path.charAt(0)=='/') {
            String droot = documentroot;

            if(documentroot.endsWith("/"))
            {
                droot = documentroot.substring(0, documentroot.length()-1);
            }
            path=droot+path;
            // debug("INFO -> doExists full path : "+path);
        } else {
            String r= ((scanpage) sp).req_line;
            int i=r.lastIndexOf('/');
            path=documentroot+r.substring(0,i+1)+path;
            // debug("INFO -> doExists req path : "+path);
        }
        /* check */
        if (type.equals("PATH")) {
            rtn=pathExists(path);
        } else if (type.equals("FILE")) {
            rtn=fileExists(path);
        } else if (type.equals("DIR")) {
            rtn=dirExists(path);
        }
        return toYesNo(rtn);
    }

    // Determines whether a path exists on the server's file system.
    private boolean pathExists(String path) {
        File f=new File(path);
        return f.exists();
    }

    // Determines whether a path exists as a directory on the server's file system.
    private boolean dirExists(String path) {
        File f=new File(path);
        return (f.exists() && f.isDirectory());
    }

    // Determines whether a path exists as a file the server's file system.
    private boolean fileExists(String path) {
        File f=new File(path);
        return (f.exists() && f.isFile());
    }

    // returns a Calendar object set to the first day of the indicated month (counted from 1-1-1970)
    private Calendar getCalendarMonths(int months) {
        int year,month;
        year=months/12;
        month=months%12;
        GregorianCalendar cal=new GregorianCalendar();
        cal.set(year+1970,month,1,0,0,0);
        return cal;
    }

    /**
     * Move a file on the system. The command line should include a filepath of the original file, and one for its destination.
     * @param sp the current page context
     * @param tok the commands to be executed
     * @return Always <code>null</code>. This comamnd is executed for its side effects, it does not return a value.
     */
    private String doMove(PageInfo sp, StringTokenizer tok ) {
        String result = null;

        if( tok.hasMoreTokens() ) {

            String from = tok.nextToken();
            if( tok.hasMoreTokens() ) {
                String toDir = tok.nextToken();
                    moveFile( from, toDir );
            } else
                log.error("doMove(): ERROR: page(" + sp + "): no destination specified in $MOD-INFO-MOVE-"+from+" !");
        } else
            log.error("doMove(): ERROR: page("+ sp +"): no source directory given in $MOD-INFO-MOVE-.. !");
        return result;
    }


    // check if this will work with moveFile("/a/b/file.txt", "../c")
    //
    // moves file from '/../directory/filename' to '/../otherdirectory/'
    private boolean moveFile( String pathslashfile , String otherdirectory ) {
        boolean result = false;
        if( fileExists(pathslashfile) ) {
            File f1 = new File( pathslashfile );

            String     name     = f1.getName();
            String     path     = f1.getAbsolutePath();                         // filename included
                    path    = path.substring( 0, path.lastIndexOf("/") );     // remove filename
            String     parent    = path.substring( 0, path.lastIndexOf("/") );     // remove last directory

            String oparent    = parent + f1.separator + otherdirectory;
            File    f2         = new File( oparent );

            if( f2.isDirectory() ) {
                if( f2.canWrite() ) {
                    f2 = new File( oparent , name );
                    if( f1.renameTo( f2 ) ) {
                        result = true;
                    } else
                        log.error("moveFile("+pathslashfile+","+otherdirectory+"): ERROR: move file("+pathslashfile+") -> file("+oparent+","+name+") did not succeed!");
                } else
                    log.error("moveFile("+pathslashfile+","+otherdirectory+"): ERROR: directory("+oparent+") has no write-permission set!");
            } else
                log.error("moveFile("+pathslashfile+","+otherdirectory+"): ERROR: directory("+oparent+") is not a valid directory!");
        } else
            log.error("moveFile("+pathslashfile+","+otherdirectory+"): ERROR: first parameter is not a valid file!");

        return result;
    }

    // determines string value for boolean results
    private String toYesNo(boolean value) {
        return ( value ? "YES" : "NO" );
    }

    // returns true is a string value is equal to YES or TRUE
    private boolean isYes(String value) {
        return (value.equals("YES") || value.equals("TRUE"));
    }

    // returns true if a string value is not equal to  NO or FALSE
    private boolean isNotNo(String value) {
        return !(value.equals("NO") || value.equals("FALSE"));
    }

    /**
     * @javadoc
     */
    private String nextCurTime(StringTokenizer tok) {
        int curtime=(int)(System.currentTimeMillis()/1000);
        //int curtime=(int)(System.currentTimeMillis()/1000);
        String cmd=tok.nextToken();
        if (cmd.equals("NEXTHOUR")) {
            // gives us the next full hour based on realtime
            int hours=curtime/3600;
            hours++;
            return ""+(hours*3600);
        } else if (cmd.equals("NEXTDAY")) {
            // gives us the next full day based on realtime (00:00)
            int days=curtime/(3600*24);
            days++;
            return ""+((days*(3600*24))-3600);
        } else if (cmd.equals("TODAY")) {
            // gives us the next full day based on realtime (00:00)
            int days=curtime/(3600*24);
            return ""+((days*(3600*24))-3600);
        } else if (cmd.equals("NEXTTIME")) {
            // gives us the next full day at time definedd based on realtime
            int days=curtime/(3600*24);
            if (tok.hasMoreTokens()) {
                String timestring=tok.nextToken();
                int pos=timestring.indexOf(":");
                if (pos!=-1) {
                    String hourstring=timestring.substring(0,pos);
                    String minstring=timestring.substring(pos+1);
                    try {
                        int hours=Integer.parseInt(hourstring)*3600;
                        int min=Integer.parseInt(minstring)*60;
                        int total=(days*3600*24)+hours+min;
                        return ""+(total-7200);
                    } catch (Exception e) {
                        log.error("Error in NEXTTIME time part make sure its 00:00 format");
                    }
                }
            }
            return ""+(days*(3600*24));
        }
        return "";
    }
}
