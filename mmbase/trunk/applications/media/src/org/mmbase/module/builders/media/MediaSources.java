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

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import javax.servlet.http.*;

import org.mmbase.util.FileWatcher;
import org.mmbase.util.StringObject;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.media.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * The MediaSource class describes pieces of media (audio / video). Information about
 * format, quality, and status will be maintained in this object. A MediaSource belongs
 * to a MediaFragement that describes the piece of media, the MediaSource is the
 * real audio/video itself. A MediaSource is connected to provider objects that indicate
 * where the real audio/video files can be found.
 *
 *
 */
public class MediaSources extends MMObjectBuilder {
    
    
    private static Logger log = Logging.getLoggerInstance(MediaSources.class.getName());
    
    // Formats
    public final static int    RA_FORMAT        = 2;
    public final static int    RM_FORMAT        = 6;
    public final static String FORMATS_RESOURCE = "org.mmbase.module.builders.media.resources.formats";
    // store also in array, for quick access (in init).
    private static String[] formats;

    static {
        ResourceBundle formatsBundle = ResourceBundle.getBundle(FORMATS_RESOURCE,  Locale.ENGLISH, MediaSources.class.getClassLoader());
        int size = 0;
        for (Enumeration e = formatsBundle.getKeys(); e.hasMoreElements(); e.nextElement()) size++;
        formats = new String[size];    
        int i = 0;
        for (Enumeration e = formatsBundle.getKeys(); e.hasMoreElements(); formats[i++] = formatsBundle.getString((String) e.nextElement()));        
    }

    
    // Codecs
    public final static int VORBIS_CODEC = 1;
    public final static int G2_CODEC     = 2;
    public final static int DIV3_CODEC   = 3;
    public final static int DIV4_CODEC   = 4;
    public final static int DIVX_CODEC   = 5;
    
    // Status
    public final static int DONE   = 3; // jikes
    public final static String STATUS_RESOURCE = "org.mmbase.module.builders.media.resources.states";
    
    public final static int MONO = 1;
    public final static int STEREO = 2;
    
    // MediaProviderFilter helps by resolving the best media provider
    private static MediaProviderFilter mediaProviderFilter = null;
    
    // MediaUrlComposer helps by resolving the end of the url
    private static MediaUrlComposer mediaUrlComposer = null;
    
    // MediaProvider builder
    private static MediaProviders mediaProviderBuilder = null;
    
    // MediaFragment builder
    private static MediaFragments mediaFragmentBuilder = null;
    
    
    private org.mmbase.cache.Cache cache = new org.mmbase.cache.Cache(50) {
        public String getName()        { return "mediasource cache"; }
        public String getDescription() { return "If the server gives ram's you can read them, results are stored in this cache."; }
    };
    private Map    servers;
    private Map    descriptions;
    private String defaultServer;
    
    
    private FileWatcher configWatcher = new FileWatcher(true) {
        protected void onChange(File file) {
            readConfiguration(file);
        }
    };
    
    public boolean init() {
        boolean result = super.init();
        
        // Retrieve a reference to the MediaProvider
        if(mediaProviderBuilder==null) {
            mediaProviderBuilder = (MediaProviders) mmb.getMMObject("mediaproviders");
            
            if(mediaProviderBuilder==null) {
                log.error("Builder mediaproviders is not loaded.");
            } else {
                log.debug("The builder mediaproviders is retrieved.");
            }
        }
        // Retrieve a reference to the MediaFragment builder
        if(mediaFragmentBuilder==null) {
            mediaFragmentBuilder = (MediaFragments) mmb.getMMObject("mediafragments");
            
            if(mediaFragmentBuilder==null) {
                log.error("Builder mediafragments is not loaded.");
            } else {
                log.debug("The builder mediapfragments is retrieved.");
            }
        }
        
        // set the mediaproviderfilter
        if(mediaProviderFilter==null) {
            mediaProviderFilter = new MediaProviderFilter(this);
        }
        
        // set the mediaurlcomposer
        if(mediaUrlComposer==null) {
            mediaUrlComposer = new MediaUrlComposer(mediaFragmentBuilder, this);
        }

               
        return result;
    }
    
    
    public MediaSources() {
        
        /*
        cache.putCache();
        log.debug("static init");
        File configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath(), "mediaservers.xml");
        if (! configFile.exists()) {
            log.warn("Configuration file for mediasources " + configFile + " does not exist");
        }
        readConfiguration(configFile);
        configWatcher.add(configFile);
        configWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
        configWatcher.start();
         */
    }
    
    /**
     * After changing the configuration file, you could call this
     * method. (No need to restart MMBase then).
     **/
    
    private void readConfiguration(File configFile) {
        servers      = new HashMap();
        descriptions = new HashMap();
        try {
            log.debug("reading " + configFile);
            DOMParser parser = new DOMParser();
            parser.parse(configFile.toString());
            
            Document doc = parser.getDocument();
            org.w3c.dom.Node n = doc.getFirstChild();
            while (n != null) {
                if (n.getNodeName().equals("mediaservers")) {
                    NamedNodeMap map1= n.getAttributes();
                    defaultServer = map1.getNamedItem("default").getNodeValue();
                    
                    org.w3c.dom.Node n2 = n.getFirstChild();
                    while (n2 != null) {
                        if (n2.getNodeName().equals("server")) {
                            NamedNodeMap map= n2.getAttributes();
                            String id      = map.getNamedItem("id").getNodeValue();
                            
                            descriptions.put(id, map.getNamedItem("description").getNodeValue());
                            servers.put(id, n2.getFirstChild().getNodeValue());;
                            // check:
                            StringObject s = new StringObject((String)servers.get(id));
                            if (s.indexOf("%s", 0) < 0 ) {
                                throw new RuntimeException("No %s in servert");
                            }
                            
                            if (log.isDebugEnabled()) {
                                log.info("found script in configuration file mediaservers.xml. Id: " + id + " source: " + servers.get(id));
                            }
                        }
                        n2 = n2.getNextSibling();
                    }
                }
                n = n.getNextSibling();
            }
        } catch (org.xml.sax.SAXException e) {
            String message = "Error reading mediaservers.xml " + e.toString();
            throw new RuntimeException(message);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error reading mediaservers.xml " + e.toString());
        }
    }
    
    
    
    
    /**
     * Resolves the url
     */
    protected String getURL(MMObjectNode node, String srv) {
        if (srv == null || srv.equals("")) srv = defaultServer;
        log.debug("Getting URL for node " + node + " and server " + srv);
        String serverTemplate = (String) servers.get(srv); // not yet decided how to do anything else then default
        if (serverTemplate == null) {
            throw new RuntimeException("No server with id = " + serverTemplate + " is not defined");
        }
        
        StringObject serverObj = new StringObject(serverTemplate);
        serverObj.replace("%s", node.getStringValue("url"));
        if (log.isDebugEnabled()) log.debug("new url: " + serverObj.toString() + " with " + node.getStringValue("url"));
        return  serverObj.toString();
    }
    
    /**
     * Gets the first line of a ram file. This can be needed in smil.
     */
    protected String getURLResult(MMObjectNode node, String src) {
        String url = getURL(node, src);
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
    
    /**
     * used in the editors
     */
    public String getGUIIndicator(MMObjectNode node) {
        return node.getStringValue("url");
    }
    
    public int getSpeed(MMObjectNode node) {
        return node.getIntValue("speed");
    }
    
    public int getChannels(MMObjectNode node) {
        return node.getIntValue("channels");
    }
    
    
    public String getGUIIndicator(String field, MMObjectNode node) {
        return "" + getValue(node, "str("+field+")");
    }
    
    /**
     * return some human readable strings
     */
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) { 
            log.debug("executeFunction  " + function + "(" + args + ") on" + node);
        }
        if (args.size() >= 1) {
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
                        return bundle.getString("undefined");
                    }
                } else if (args.get(0).equals("channels")) {
                    int val = node.getIntValue("channels");
                    switch(val) {
                    case MONO:   return "Mono";
                    case STEREO: return "Stereo";
                    default:     return "Undefined";
                    }
                } else if (args.get(0).equals("codec")) {
                    int val = node.getIntValue("codec");
                    return MediaSources.convertNumberToCodec(val);
                } else if (args.get(0).equals("format")) {
                    int val = node.getIntValue("format");
                    return MediaSources.convertNumberToFormat(val);
                } else {
                    return node.getStringValue((String) args.get(0));
                }            
            } else if (function.equals("absoluteurl")) {
                return getURL(node, (String) args.get(0));
            } else if (function.equals("urlresult")) {
                return getURLResult(node, (String) args.get(0));
            }
        }
        return super.executeFunction(node, function, args);
    }
    
    /**
     * remove this MediaSource, check configuration and check all places where this
     * file is reproduced.
     */
    public void remove() {
    }
    
    
    /**
     * get the url of the mediasource. This url will be created by the MediaProvider
     * and an uriFilter.
     * @param mediafragment the mediafragment (maybe not needed...)
     * @param mediasource the mediasource
     * @param info     
     * @return the url
     */
    public String getLongUrl(MMObjectNode mediafragment, MMObjectNode mediasource, Map info) {
        
        // Find the provider for the url
        MMObjectNode mediaProvider = mediaProviderFilter.filterMediaProvider(mediasource, info);
        if(mediaProvider==null) {
            log.error("Cannot selected mediaprovider, check mediaproviderfilter configuration");
            return null;
        }
        
        log.debug("Selected mediaprovider is "+mediaProvider.getNumber());
        // Get the protocol and the hostinfomation of the provider.
        String url = mediaProviderBuilder.getProtocol(mediasource) + mediaProvider.getStringValue("rooturl");
        String uri = mediaUrlComposer.getURI(mediafragment, mediasource, info);
        
        return url+uri;
    }
    
    /**
     * resolve the url of the mediasource (e.g. pnm://www.mmbase.org/test/test.ra)
     *
     * @param mediafragment the media fragment
     * @param mediasouce the media source
     * @param info extra info (i.e. HttpRequestIno, bitrate, etc.)
     * @return the url of the media source
     */
    public String getUrl(MMObjectNode mediasource, Map info) {
        log.debug("Getting url");
        
        // Find the provider for the url
        MMObjectNode mediaProvider = mediaProviderFilter.filterMediaProvider(mediasource, info);
        if(mediaProvider==null) {
            log.error("Cannot selected mediaprovider, check mediaproviderfilter configuration");
            return null;
        }
        
        log.debug("Selected mediaprovider is "+mediaProvider.getNumber());
        // Get the protocol and the hostinfomation of the provider.
        String providerinfo = mediaProviderBuilder.getProtocol(mediasource) + mediaProvider.getStringValue("rooturl");
        return providerinfo+mediaUrlComposer.getUrl(mediasource, info);
    }
    
    /**
     * resolve the content type of the mediasource
     *
     * @param mediasource the media source
     * @return the content type
     */
    public String getContentType(MMObjectNode mediasource) {
        log.debug("Getting content type");
        return mediaUrlComposer.getContentType(mediasource);
    }
    
    /**
     * get all mediaproviders belonging to this mediasource
     * @param mediasource the mediasource
     * @return All mediaproviders related to the given mediasource
     */
    public Vector getMediaProviders(MMObjectNode mediasource) {
        log.debug("mediasource "+mediasource.getStringValue("number"));
        return mediasource.getRelatedNodes("mediaproviders");
    }
    
    /**
     * converting format number to string representation
     * @param format the format number
     * @return the format
     */
    public static String convertNumberToFormat(int format) {
        if (format >= formats.length || format < 0) { 
            return "undefined";
        } else {
            return formats[format];
        }
    }
    
    public static int convertFormatToNumber(String format) {
        format = format.toLowerCase();
        for (int i = 0; i < formats.length; i++) {
            if(format.equals(formats[i])) return i;
        }
        log.error("Cannot convert format ("+format+") to number");
        return -1;
    }

    public static int convertCodecToNumber(String codec) {
        codec = codec.toLowerCase();
        if(codec.equals("vorbis")) return VORBIS_CODEC;
        if(codec.equals("g2")) return G2_CODEC;
        if(codec.equals("div3")) return DIV3_CODEC;
        if(codec.equals("div4")) return DIV4_CODEC;
        if(codec.equals("divx")) return DIVX_CODEC;
        log.error("Cannot convert codec ("+codec+") to number");
        return -1;
    }
    public static String convertNumberToCodec(int codec) {
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
