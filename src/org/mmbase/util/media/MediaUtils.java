/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.media;

import java.util.*;

import org.mmbase.util.scanpage;
import org.mmbase.util.StringTagger;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @author 	Marcel Maatkamp, marmaa@vpro.nl
 * @version $Id: MediaUtils.java,v 1.11 2002-02-20 10:43:26 pierre Exp $
 */

public class MediaUtils {

    // logging
    private static Logger log = Logging.getLoggerInstance(MediaUtils.class.getName());

    /**
     * @javadoc
     * @deprecation-used contains commented-out code
     * @dependency scanpage (SCAN) - can be removed as it is not actually used except for debug code
     * @vpro uses fixed urls/paths
     */
    public static String getBestMirrorUrl( scanpage sp, String url ) {
        String result = null;
        // parameters ok?
        if (log.isDebugEnabled()) {
            log.debug("getBestMirrorUrl("+url+")");
        }

        //if( sp != null )
        //{
        if( checkstring("getBestMirrorUrl","url",url) ) {
            // start tagging the url, format is one of two formats:
            //         - "http://station.vpro.nl/data/<nr>/<speed>_<channels>.ra" or            (old way)
            //         - "F=/<nr>/<speed>_<channels>.ra H1=station.vpro.nl H2=streams.omroep.nl"  (new)
            //         - "F=/<nr>/surestream.rm H1=station.vpro.nl H2=streams.omroep.nl"        (newest :)
            StringTagger tagger = new StringTagger( url );
            String file = tagger.Value("F");
            String u = url;
            // is it format "F=.."?
            if( file != null && !file.equals("") ) {
                // is this class used by VPRO or is it for others
                Hashtable urls = getUrls( url );

                file = file.trim();

                u = filterBestUrl(sp, urls);

                if( u.endsWith("/") ) {
                    if( file.startsWith("/") )
                        // too many /'s
                        file = file.substring(1);
                    } else {
                        // ??? should be endsWith?
                        if( !file.startsWith("/") ) {
                            // too few /'s
                            file = file + "/";
                        }
                    }
                    result = u + file;
            } else {
                    // format = http://station.vpro.nl/audio/ra/<nr>/<speed>_<chan>.ra
                    // output = station.vpro.nl/<nr>/<speed>_<chan>.ra
                    u = url;
                    int i = u.indexOf("//");
                    if( i > 0 ) {
                        u = u.substring( i+2 );

                        i = u.indexOf("/");
                        if( i > -1 ) {
                            String hostname = u.substring(0, i);
                            u = u.substring( i+1 );

                            if( u.startsWith("data/") ) {
                                u = u.substring( 5 );
                            }
                            if( u.startsWith("audio/") ) {
                                u = u.substring( 6 );
                            }
                            if( u.startsWith("ra/") ) {
                                u = u.substring( 3 );
                            }
                            if( u.startsWith("/") ) {
                                u = hostname + u;
                            } else {
                                u = hostname + "/" + u;
                            }
                        }
                    }
                    result = u ;
            }
        }
        // }
        if( url != null && !url.equals("")) {
            if (log.isDebugEnabled()) {
                log.debug("getBestMirrorUrl("+url+"): result("+result+")");
            }
        } else {
            log.error("getBestMirrorUrl("+url+"): No url found for this node on page("+sp.getUrl()+"), ref("+sp.req.getHeader("Referer")+")") ;
        }
        return result;
    }

    /**
     * @javadoc
     * @dependency scanpage (SCAN) - can be removed as it is not actually used except for debug code
     * @vpro uses fixed urls/path (beep.vpro.nl)s
     */
    private static String filterBestUrl( scanpage sp, Hashtable urls) {
        String result = null;
        String key = null;
        String value = null;
        Enumeration e = urls.keys();

        while( e.hasMoreElements() ) {
            key = (String) e.nextElement();
            value = (String) urls.get( key );

            // not null or empty
            if( checkstring( "filterBestUrl","value",value) ) {
                if( value.startsWith("station") || value.startsWith("beep")
                        || value.startsWith("streams.vpro.nl")) {
                       // only use if none better found
                       if( result == null ) {
                           result = value;
                       }
                } else if( value.startsWith("streams.omroep.nl") ) {
                        result = value;
                } else {
                    log.warn("filterBestUrl("+sp+","+urls+"): Found url("+value+") with unknown server!");
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("filterBestUrl("+sp+","+urls+"): found url("+result+")");
        }

        if( result == null ) {
            log.error("filterBestUrl("+sp.getUrl()+","+urls+"): No valid url found in table, urls: ");
            Enumeration e2 = urls.keys();
            int i = 1;

            String k = null;
            String v = null;

            while( e2.hasMoreElements() ) {
                k = (String)e2.nextElement();
                v = (String)urls.get( k );
                if (log.isDebugEnabled()) {
                    log.debug("url("+i+"): key("+k+"), value("+v+")");
                }
                i++;
            }
            result = "beep.vpro.nl";
        }
        return result;
    }

    /**
     * Get all the tags in form 'H<nr>=<url>' out of this string and put urls in hashtable
     * @javadoc parameters
     */
    private static Hashtable getUrls( String url ) {
        Hashtable result = new Hashtable();
        StringTokenizer tok = new StringTokenizer( url );
        String other = null;
        int i = 0;

        String snumber = null;
        int number = 0;

        if (log.isDebugEnabled()) {
            log.debug("getUrls("+url+")");
        }

        while( tok.hasMoreTokens() ) {
            other = tok.nextToken();
            if( other.startsWith("F=") ) {
                // do nothing, have already
            } else {
                if( other.startsWith("H") ) {
                    i = other.indexOf("=");
                    // -1 is def wrong, 0 = serious trouble in the JVM :)
                    if( i > 0 ) {
                        snumber = other.substring(1, i).trim();
                        try {
                            // number not actually needed (yet?) but fetch it anyway
                            number     = Integer.parseInt( snumber );
                            other     = other.substring( i+1 ).trim();
                            // add url in hashtable
                            result.put( snumber, other );
                            if (log.isDebugEnabled()) {
                                log.debug("urls(): got url("+number+","+other+")");
                            }

                        } catch( NumberFormatException e ) {
                            log.error("urls("+url+"): While parsing url("+other+"): This is not a number("+snumber+")!");
                        }
                    } else {
                        log.error("urls("+url+"): This url("+other+") is malformed! (Where is the '='?)");
                    }
                } else {
                    log.warn("urls("+url+"): got something, dunno what("+other+")! (not 'F=..' or 'H<nr>=..')");
                }
            }
        }
        return result;
    }

    /**
     * @javadoc
     */
    private static boolean checkstring( String method, String name, String value ) {
        boolean result = false;
        //            if(method == null)         debug("checkstring("+method+","+name+","+value+"): ERROR: method("+method+") is null!");
        //    else     if(method.equals(""))     debug("checkstring("+method+","+name+","+value+"): ERROR: method("+method+") is empty!");
        //    else     if(name == null)         debug("checkstring("+method+","+name+","+value+"): ERROR: name("+method+") is null!");
        //    else     if(name.equals(""))     debug("checkstring("+method+","+name+","+value+"): ERROR: name("+method+") is empty!");
        //    else
        if (value == null) {
            log.error( method+"(): "+name+"("+value+") is null!");
        } else if (value.equals("")) {
            log.error( method+"(): "+name+"("+value+") is empty!");
        } else {
           result = true;
        }
        return result;
    }

    /**
     * Replaces all plus characters to procent 20
     * @param s String in which chars will be replaced.
     * @return replaced String
     */
    public static String plusToProcent20(String s) {
        String result = "";
        for(int i=0; i<s.length(); i++) {
            if (s.charAt(i) != '+') {
                result += s.charAt(i);
            } else {
                result += "%20";
            }
        }
        return result;
    }

    /**
     * Removes RealPlayer incompatible characters from the string.
     * '#' characters are replaced by space characters.
     * Characters that are allowed are every letter or digit and ' ', '.', '-' and '_' chars.
     * @param s the String that needs to be fixed.
     * @return a Nedstat compatible String.
     */
    public static String makeRealCompatible(String s) {
        if (s != null) {
            char[] sArray = s.replace('#',' ').toCharArray();
            char[] dArray = new char[sArray.length];

            int j = 0;
            for (int i=0;i<sArray.length;i++) {
                if (Character.isLetterOrDigit(sArray[i]) ||(sArray[i]==' ')||(sArray[i]=='.')||(sArray[i]=='-')||(sArray[i]=='_')) {
                    dArray[j] = sArray[i];
                    j++;
                }
            }
            //Only use the characters until the first character with value=0. This is from index 0 to j-1.
            return (new String(dArray)).substring(0,j);
        } else {
            return null;
        }
    }

    /**
     * Get all the tags in form 'H<nr>=<url>' out of this string and put urls in hashtable
     * @javadoc parameters
     */
    public static void main( String args[] ) {
        String filename;
        String classname = "MediaUtils";

        System.out.println( classname + ": Test the methods:");

        filename = "F=/1426/40_1.ra H1=station.vpro.nl/ H2=streams.omroep.nl/vpro/";
        System.out.println( classname +": -> " + filename + " : " + org.mmbase.util.media.MediaUtils.getBestMirrorUrl(null, filename) );
        filename = "http://station.vpro.nl/audio/ra/1200/40_1.ra";
        System.out.println( classname +": -> " + filename + " : " + org.mmbase.util.media.MediaUtils.getBestMirrorUrl(null, filename) );
        filename = "rtsp://station.vpro.nl/audio/ra/1200/40_1.ra";
        System.out.println( classname +": -> " + filename + " : " + org.mmbase.util.media.MediaUtils.getBestMirrorUrl(null, filename) );
    }
}
