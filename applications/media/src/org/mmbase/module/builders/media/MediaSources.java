 /*
  
 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.
 
 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
 */

package org.mmbase.module.builders.media;

import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;

//import org.mmbase.module.builders.AbstractServletBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBaseContext;
import javax.servlet.http.*;

import org.mmbase.util.FileWatcher;
import org.mmbase.util.StringObject;
import org.mmbase.util.XMLBasicReader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.media.*;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * The MediaSource class describes pieces of media (audio / video). Information about
 * format, quality, and status will be maintained in this object. A MediaSource belongs
 * to a MediaFragement that describes the piece of media, the MediaSource is the
 * real audio/video itself. A MediaSource is connected to provider objects that indicate
 * where the real audio/video files can be found.
 *
 * @author Rob Vermeulen
 * @author Michiel Meeuwissen
 * @version $Id: MediaSources.java,v 1.24 2003-01-21 17:46:24 michiel Exp $
 * @since MMBase-1.7
 */
public class MediaSources extends MMObjectBuilder {
    private static Logger log = Logging.getLoggerInstance(MediaSources.class.getName());

    
    // typo check
    public static final String FUNCTION_URLS        = "urls";
    public static final String FUNCTION_URL         = "url";
    public static final String FUNCTION_AVAILABLE   = "available";
    public static final String FUNCTION_FORMAT      = "format";
    public static final String FUNCTION_CODEC       = "codec";
    public static final String FUNCTION_MIMETYPE    = "mimetype";

    
    // Codecs
    public final static int VORBIS_CODEC = 1;
    public final static int G2_CODEC     = 2;
    public final static int DIV3_CODEC   = 3;
    public final static int DIV4_CODEC   = 4;
    public final static int DIVX_CODEC   = 5;
    
    // Status
    public final static int    DONE   = 3; // jikes
    public final static String STATUS_RESOURCE = "org.mmbase.module.builders.media.resources.states";
    
    public final static int MONO   = 1;
    public final static int STEREO = 2;
    
       
    private org.mmbase.cache.Cache cache = new org.mmbase.cache.Cache(50) {
        public String getName()        { return "mediasource cache"; }
        public String getDescription() { return "If the server gives ram's you can read them, results are stored in this cache."; }
    };
    
    private static Map mimeMapping = null;

    public boolean init() {
        boolean result = super.init();
        

        if (mimeMapping == null) {        
            File mimeMappingFile = new File(MMBaseContext.getConfigPath() + File.separator + "media" + File.separator + "mimemapping.xml");            
            log.service("Reading " + mimeMappingFile);
            XMLBasicReader reader = new XMLBasicReader(mimeMappingFile.toString(), getClass());
            mimeMapping = new Hashtable();
            
            for(Enumeration e = reader.getChildElements("mimemapping", "map"); e.hasMoreElements();) {
                Element map = (Element)e.nextElement();
                String format = reader.getElementAttributeValue(map, "format");
                String codec = reader.getElementAttributeValue(map, "codec");
                String mime = reader.getElementValue(map);
                
                mimeMapping.put(format + "/" + codec,mime);
                log.debug("Adding mime mapping " + format + "/" + codec + " -> " + mime);
            }
        }
               
        return result;
    }
    
    
    public MediaSources() {       
        cache.putCache();
    }
    
    /**
     * create a new mediasource, and relate it with specified mediafragment.
     *
     * @param mediafragment the media fragment to which this source belongs
     * @param status of the media source
     * @param format the format of the source (Real, Mp3)
     * @param channels stereo/mono
     * @param url the file name of the media source
     * @param owner creator of the new object
     * @return the new <code>MediaSource</code>
     */
    public MMObjectNode createSource(MMObjectNode mediafragment, int status, int format, int speed, int channels, String url, String owner) {
        // creating media source
        MMObjectNode source = getNewNode(owner);
        source.setValue("status",status);
        source.setValue("format",format);
        source.setValue("speed",speed);
        source.setValue("channels",channels);
        source.setValue("url",url);
        source.insert(owner);
        
        // creating relation between media source and media fragment
        MMObjectNode insrel = mmb.getInsRel().getNewNode(owner);
        insrel.setValue("snumber", source.getIntValue("number"));
        insrel.setValue("dnumber", mediafragment.getIntValue("number"));
        insrel.setValue("rnumber", mmb.getInsRel().oType);
        int ret = insrel.insert(owner);
        if(ret<0) {
            log.error("Cannot create relation between mediafragment and mediasource "+insrel);
        } else {
            log.debug("created "+insrel);
        }
        
        return source;
    }
       

    /**
     * resolve the url of the mediasource (e.g. pnm://www.mmbase.org/test/test.ra)
     *
     * @param mediafragment the media fragment
     * @param mediasouce the media source
     * @param info extra info (i.e. HttpRequestIno, bitrate, etc.)
     * @return the url of the media source
     */
    protected String getURL(MMObjectNode source, Map info) {
        List urls = getSortedURLs(source, null, info);
        if (urls.size() == 0) return "[could not compose URL]";
        ResponseInfo ri = (ResponseInfo) urls.get(0);
        return ri.getURL();
    }

    /**
     * Resolve the mimetype for a certain media source
     *
     * @param source the media source
     * @return the content type
     */    
    String getMimeType(MMObjectNode source) { // package because it is used in URLResolver
        String format = getFormat(source).toString();
        if(format==null || format.equals("")) {
            format="*";
        }
        String codec = getCodec(source);
        if(codec==null || codec.equals("")) {
            codec="*";
        }
        
        String mimetype;
        if(mimeMapping.containsKey(format+"/"+codec)) {
            mimetype = (String) mimeMapping.get(format + "/" + codec);
        } else if (mimeMapping.containsKey(format + "/*")) {
            mimetype = (String) mimeMapping.get(format + "/*");
        } else if (mimeMapping.containsKey("*/" + codec)) {
            mimetype = (String) mimeMapping.get("/" + codec);
        } else if (mimeMapping.containsKey("*/*")) {
            mimetype = (String) mimeMapping.get("*/*");
        }  else {
            mimetype = "application/octet-stream";
        }
        if (log.isDebugEnabled()) {
            log.debug("Mimetype for mediasource " + source.getIntValue("number") + " is "+mimetype);
        }
        return mimetype;
    }
    


    /**
     * Gets the first line of a ram file. This can be needed in smil.
     */
 
    // to make a rm of a ram.
    /*
    protected String getURLResult(MMObjectNode node, String src) {
        if (log.isDebugEnabled()) log.debug("Getting URL-Result voor src " + src);
        Map info = new HashMap();
        info.put("src", src); // not used
        String url = getURL(node, info);
        String result = (String) cache.get(url);
        if (result == null) {
            try {
                URL u = new URL(url);
                URLConnection con = u.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                result = in.readLine();
                in.close();
            } catch (java.net.MalformedURLException e) {
                throw new RuntimeException(e.toString());
            } catch (java.io.IOException e) {
                throw new RuntimeException(e.toString());
            }
            cache.put(url, result);
        }
        return result;
    }
    */    
    /**
     * used in the editors
     */
    public String getGUIIndicator(MMObjectNode source) {
        List urls = getSortedURLs(source, null, null);
        if (urls.size() == 0) return "[could not compose URL]";
        ResponseInfo ri = (ResponseInfo) urls.get(0);
        if (ri.isAvailable()) {
            return "<a href='" + ri.getURL() + "'>" + Format.get(source.getIntValue("format")) + "</a>";
        } else {
            return "[<a href='" + ri.getURL() + "'>" + Format.get(source.getIntValue("format")) + "</a>]";
        }
    }
    
    public int getSpeed(MMObjectNode node) {
        return node.getIntValue("speed");
    }
    
    public int getChannels(MMObjectNode node) {
        return node.getIntValue("channels");
    }

    /**
     * The format field is an integer, this function returns a string-presentation
     */
    protected Format getFormat(MMObjectNode source) {
        return Format.get(source.getIntValue("format"));
    }

    /**
     * The codec field is an integer, this function returns a string-presentation
     */
    protected String getCodec(MMObjectNode source) {        
        return convertNumberToCodec(source.getIntValue("codec"));
    }
        
    /**
     * Functions.
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) { 
            log.debug("executeFunction  " + function + "(" + args + ") on mediasources " + node);
        }
        if (function.equals("info")) {
            List empty = new Vector();
            java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
            info.put("absoluteurl", "(<??>)");
            info.put("urlresult", "(<??>) ");
            info.put(FUNCTION_URLS, "(fragment) A list of all possible URLs to this source/fragment (Really MediaURLComposer.ResponseInfo's)");
            info.put(FUNCTION_FORMAT, "() Shorthand for gui(format)");
            info.put(FUNCTION_CODEC, "() Shorthand for gui(codec)");
            info.put(FUNCTION_MIMETYPE, "() Returns the mime-type for this source");
            info.put("gui", "(state|channels|codec|format|..) Gui representation of this object.");

            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            } 
        } else if (FUNCTION_URLS.equals(function)) {
            MMObjectNode fragment;
            if (args == null || args.size() == 0) {
                fragment = null;
            } else if (args.size() == 1) {
                Object f = args.get(0);
                if (f instanceof MMObjectNode) { 
                    fragment = (MMObjectNode) f;
                } else if (f instanceof org.mmbase.bridge.Node) {
                    fragment = getNode(((org.mmbase.bridge.Node) f).getNumber());
                } else if (f instanceof String) {
                    fragment = getNode((String) f);
                } else {
                    throw new IllegalArgumentException("Argument of function " + FUNCTION_URLS + " must be a Node");
                }                
            } else {
                throw new IllegalArgumentException("Function " + FUNCTION_URLS + " has 0 or 1 arguments");                
            }
            return getURLs(node, fragment, null);
        } else if (FUNCTION_URL.equals(function)) {
            return getURL(node, null);
        } else if (FUNCTION_AVAILABLE.equals(function)) {
            Iterator providers = getProviders(node).iterator();
            while (providers.hasNext()) {
                // if one of the providers is online, then this source is availabe.                
                MMObjectNode provider = (MMObjectNode) providers.next();
                if (provider.getIntValue("state") == MediaProviders.STATE_ON) return Boolean.TRUE;
            }
            return Boolean.FALSE;            
        } else if (FUNCTION_FORMAT.equals(function)) {
            return getFormat(node);
        } else if (FUNCTION_CODEC.equals(function)) {
            return getCodec(node);
        } else if (FUNCTION_MIMETYPE.equals(function)) {
            return getMimeType(node);
        } else if (args != null && args.size() > 0) {
            if (function.equals("gui")) {
                if (args.get(0).equals("state")) {
                    String val = node.getStringValue("state");
                    ResourceBundle bundle;
                    if (args.size() > 1) {
                        bundle = ResourceBundle.getBundle(STATUS_RESOURCE,  new Locale((String) args.get(1), ""), getClass().getClassLoader());
                    } else {
                        bundle = ResourceBundle.getBundle(STATUS_RESOURCE, new Locale(mmb.getLanguage(), ""), getClass().getClassLoader());
                    }                    
                    try {
                        return bundle.getString(val);
                    } catch (java.util.MissingResourceException e) {
                        return val;
                    }
                } else if (args.get(0).equals("channels")) {
                    int val = node.getIntValue("channels");
                    switch(val) {
                    case MONO:   return "Mono";
                    case STEREO: return "Stereo";
                    default:     return "Undefined";
                    }
                } else if (args.get(0).equals("codec")) {
                    return getCodec(node);
                } else if (args.get(0).equals("format")) {
                    return getFormat(node);
                } else if (args.get(0).equals("")) {
                    return super.executeFunction(node, function, args); // call getGUIIndicoato
                } else {
                    return node.getStringValue((String) args.get(0));
                }            
            } 
        } else { 
            String arg = null;
            if (args != null && args.size() > 0) {
                arg = (String) args.get(0);
            } else {
                arg = "";
            }
            if (function.equals("absoluteurl")) {
                Map info = new HashMap();
                info.put("server", arg);
                return getURL(node, info);
            } else if (function.equals("urlresult")) {
                //return getURLResult(node, arg);
            }
        }
        log.debug("Function not matched in mediasources");
        return super.executeFunction(node, function, args);
    }
    
    /**
     * remove this MediaSource, check configuration and check all places where this
     * file is reproduced.
     */
    /*
    public void remove() {
    }
    */
     

    /**
     * Returns all possible URLs for this source. (A source can be on different providers)
     * @author mm
     */

    public List getURLs(MMObjectNode source, MMObjectNode fragment, Map info) {
        List result = new ArrayList();

        Iterator i = getProviders(source).iterator();
        while (i.hasNext()) {
            MMObjectNode provider = (MMObjectNode) i.next();
            MediaProviders bul = (MediaProviders) provider.parent; // cast everytime, because it can be extended
            List providersurls = bul.getURLs(provider, source, fragment, info);
            if (providersurls.removeAll(result)) {// avoid duplicates 
                log.service("removed duplicates");
            }
            result.addAll(providersurls);
        }
        return result;
    }
    /**
     * @author mm
     */
    protected List getSortedURLs(MMObjectNode source, MMObjectNode fragment, Map info) {
        List urls = getURLs(source, fragment, info);
        return MediaSourceFilter.getInstance().filter(urls);
    }

    /**
     * get all mediaproviders belonging to this mediasource
     * @param mediasource the mediasource
     * @return All mediaproviders related to the given mediasource
     */
    protected List getProviders(MMObjectNode source) {
        if (log.isDebugEnabled()) {
            log.debug("mediasource " + source.getStringValue("number"));
        }        
        return source.getRelatedNodes("mediaproviders");
    }
    

    private void checkFields(MMObjectNode node) {
        if (log.isDebugEnabled()) {
            log.debug("format: " + node.getValue("format"));
        }
        if (node.getValue("format") == null || node.getIntValue("format") == -1) {
            String url = node.getStringValue("url");
            int dot = url.lastIndexOf('.');
            if (dot > 0) {
                String extension = url.substring(dot + 1);
                log.service("format was unset, trying to autodetect by using 'url' field with extension '" + extension);
                node.setValue("format", Format.get(extension).toInt());
            }            
        }
    }

    public boolean setValue(MMObjectNode node,String fieldName, Object value) {
        if ("format".equals(fieldName)) {
        }
        return super.setValue(node, fieldName, value);
    }

    /**
     * The commit can be used to automaticly fill unfilled fields. For
     * example the format can well be guessed by the URL.
     * @todo which of commit,insert must be overriden?
     */ 

    public boolean commit(MMObjectNode node) {
        checkFields(node);     
        return super.commit(node);
    }
    public int insert(String owner, MMObjectNode node) {
        checkFields(node);
        return super.insert(owner, node);
    }

    /*
     * @scope private (and use it in checkFields)
     */
    protected static int convertCodecToNumber(String codec) {
        codec = codec.toLowerCase();
        if(codec.equals("vorbis")) return VORBIS_CODEC;
        if(codec.equals("g2"))     return G2_CODEC;
        if(codec.equals("div3"))   return DIV3_CODEC;
        if(codec.equals("div4"))   return DIV4_CODEC;
        if(codec.equals("divx"))   return DIVX_CODEC;
        log.error("Cannot convert codec ("+codec+") to number");
        return -1;
    }
    
    protected static String convertNumberToCodec(int codec) {
        switch(codec) {
            case VORBIS_CODEC: return "vorbis";
            case G2_CODEC: return "g2";
            case DIV3_CODEC: return "div3";
            case DIV4_CODEC: return "div4";
            case DIVX_CODEC: return "divx";
            default: return "Undefined";
        }
   }
}
