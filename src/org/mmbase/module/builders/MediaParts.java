/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/*
import org.mmbase.module.gui.html.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.sessionsInterface;
import org.mmbase.module.sessionInfo;

import org.mmbase.util.media.*;
import org.mmbase.util.media.audio.*;
import org.mmbase.module.builders.*;
*/
/**
 * MediaParts is the main class for mediaobjects. All media type builders (eg. AudioParts) builders
 * extend from this one. MediaParts implements the replace command GETURL to get the url to a mediafile.
 *
 * MediaParts also implements an urlCache which comes in handy when queries lots of audioparts at once.
 * (This takes time since urls aren't stored directly in a mediapart but through rawaudios/videos etc..)
 * To use the urlCache you have to set the XML builder property 'UrlCaching' to 'true'. default is false.
 *
 * For each object whos url is requested two types of cache entries will me made. One is for requests coming
 * from the internal www server, and one for requests coming from outside.
 * VPRO uses this to send request from employees who visit the site to a local RealServer instead of the
 * main RealServer.
 *
 * If an audiopart or videopart node changes locally or remotely, the related UrlCache entries will be removed
 * immediately.
 *
 * @author David van Zeventer
 * @version $Id: MediaParts.java,v 1.6 2001-12-19 17:32:26 vpro Exp $
 */
public abstract class MediaParts extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(MediaParts.class.getName());

	// Define LRU Cache for video urls.
	public static LRUHashtable urlCache = new LRUHashtable(1024);

	// Use caching of not, determined by builder property 'CacheUrls'
	private boolean urlCaching=false;

	/**
	 * Initializes and gets builder properties.
	 * @return true always
	 */
	public boolean init() {
		super.init();
		String propValue = getInitParameter("UrlCaching");
		log.debug("init(): Builder property UrlCaching=" + propValue);
		if (propValue!=null)
			urlCaching = (Boolean.valueOf(propValue)).booleanValue();
		return true;
	}

	/**
    * Called when a node was changed on a local server.
	* @param machine Name of the node that was changed.
    * @param number the object number of the node that was changed.
    * @param builder the buildername of the object that was changed
    * @param ctype the node changed type
    * @return true, always
    */
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
		if (log.isDebugEnabled()) {
            log.debug("nodeLocalChanged("+machine+","+number + "," + builder + "," + ctype + ") ctype:" + ctype);
        }
		if (ctype.equals("c"))
			removeFromUrlCache(number);
		if (ctype.equals("d")) {
			try {
				int num=Integer.parseInt(number);
				boolean success = removeRaws(builder,num);
				if (!success)
                    log.error("removeRaws was not succesful!");
			} catch (NumberFormatException nfe) {
				log.error("nodeLocalChanged: number value(" + number + ") is not an integer.");
				nfe.printStackTrace();
			}
		}
        return true;
    }

	/**
    * Called when a node was changed by a remote server.
	* @param machine Name of the node that was changed.
    * @param number the object number of the node that was changed.
    * @param builder the buildername of the object that was changed
    * @param ctype the node changed type
    * @return true, always
    */
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
		super.nodeRemoteChanged(machine,number,builder,ctype);
		if (log.isDebugEnabled()) {
            log.debug("nodeRemoteChanged("+machine+","+number + "," + builder + "," + ctype + ") ctype:" + ctype);
        }
		if (ctype.equals("c"))
			removeFromUrlCache(number);
        return true;
    }

	/**
	 * Removes the entries related with this objectnumber from the urlCache.
	 * @param number
	 */
	public void removeFromUrlCache(String number) {
		int key;
		try {
			key = Integer.parseInt(number);
			if (log.isDebugEnabled()) {
                log.debug("removeFromUrlCache(" + number + ") Removing entries with " + key + " and " + (-1*key) + " from urlCache");
            }
			urlCache.remove(new Integer(key));
			urlCache.remove(new Integer(-1*key)); //Also remove internal requests entries.
		} catch(NumberFormatException nfe) {
			log.error("removeFromUrlCache(" + number + ") Invalid number value:" + number);
            log.error(Logging.stackTrace(nfe));
		}
	}

	/**
	 * Removes related rawaudio/video objects.
	 * @param buildername the buildername of which type this number is.
	 * @param number objectnumber of audio/videopart.
	 * @return true if remove was succesful, false otherwise.
	 */
	public boolean removeRaws(String buildername,int number) {
		MMObjectBuilder builder = null;
		Enumeration e = null;

		if (buildername.equals("audioparts")) {
			if (log.isDebugEnabled()) {
                log.debug("removeRaws: Deleting all rawaudios where id=" + number);
            }
			builder = mmb.getMMObject("rawaudios");
		} else if (buildername.equals("videoparts")) {
			if (log.isDebugEnabled()) {
                log.debug("removeRaws: Deleting all rawvideos where id=" + number);
            }
			builder = mmb.getMMObject("rawvideos");
		} else {
			log.error("Can't delete raws since number:"+number+" is not an audio/videopart but a "+buildername);
			return false;
		}

		e = builder.search("WHERE id='"+number+"'");
		MMObjectNode rawNode = null;
		while (e.hasMoreElements()) {
			rawNode = (MMObjectNode)e.nextElement();
			if (log.isDebugEnabled()) {
                debug("removeRaws: Removing rawobject " + rawNode.getIntValue("number"));
            }
			builder.removeNode(rawNode);
		}
		return true;
	}

    /**
     * replace all for frontend code
	 * Replace commands available are GETURL (gets mediafile url for an objectnumber),
	 * from cache or not depending on builder property.
	 * @param sp the scanpage
	 * @param sp the stringtokenizer reference with the replace command.
	 * @return a String the result value of the replace command or null.
     */
    public String replace(scanpage sp,StringTokenizer command) {
		if (command.hasMoreTokens()) {
            String token=command.nextToken();
            // debug("replace: The nextToken = "+token);
            if (token.equals("GETURL")) {
				int number=0;
				int userSpeed=getMinSpeed();
				int userChannels=getMinChannels();
				if (command.hasMoreTokens()) number=getNumberParam(command.nextToken());
				if (command.hasMoreTokens()) userSpeed=getSpeedParam(command.nextToken());
				if (command.hasMoreTokens()) userChannels=getChannelsParam(command.nextToken());
				if (number!=-1) {
					String url = null;
					if (urlCaching)
						url = getUrlFromCache(sp,number,userSpeed,userChannels);
					else
						url = getUrl(sp,number,userSpeed,userChannels);
					if (log.isDebugEnabled()) {
                        log.debug("replace: GETURL returns: " + url);
                    }
					return url;
				} else {
                    log.error("getUrl: No objectnumber defined.");
					return null;
				}
            } else if (token.equals("GETURLNOCACHE")) {
				if (log.isDebugEnabled()) {
                    log.debug("replace: Command is GETURLNOCACHE getting url directly.");
                }
				int number=0;
				int userSpeed=getMinSpeed();
				int userChannels=getMinChannels();
				if (command.hasMoreTokens()) number=getNumberParam(command.nextToken());
				if (command.hasMoreTokens()) userSpeed=getSpeedParam(command.nextToken());
				if (command.hasMoreTokens()) userChannels=getChannelsParam(command.nextToken());
				if (number!=-1) {
					String url = null;
					url = getUrl(sp,number,userSpeed,userChannels);
					if(log.isDebugEnabled()) {
                        log.debug("replace: GETURLNOCACHE returns: " + url);
                    }
					return url;
				} else {
					log.error("getUrl: No objectnumber defined.");
					return null;
				}
            } else {
				log.error("replace: Unknown command: "+token);
				return("ERROR: Unknown command: "+token);
			}
        }
  		log.info("replace: No command defined.");
  		return("No command defined, says the VideoParts builder.");
    }

	/**
	 * Gets related url from cache if it's not in there, generates it and put it in cache and return it.
	 * If url generation fails (returns null), no cache entry will be made and null will be returned.
	 * @param sp the scanpage object used when retrieving the users' settings.
	 * @param number the objectnumber for which url is retrieved.
	 * @param userSpeed speed settings.
	 * @param userChannels channel settings.
	 * @return a String with the Url to the file or null.
	 */
	String getUrlFromCache(scanpage sp,int number,int userSpeed,int userChannels) {
		if ( ((urlCache.getHits()+urlCache.getMisses()) % 100) == 0 )
			debug("getUrlFromCache: "+urlCache.getStats());
		String url = null;
		int key = number;
		url = (String) urlCache.get(new Integer(key));
		if (url == null) {
			// NOT IN CACHE retrieving & putting in cache now and returning.
			log.info("getUrlFromCache: MISS for KEY: " + key);
			url =  getUrl(sp,number,userSpeed,userChannels);
			if (url == null) {
				log.debug("getUrlFromCache: doGetUrl returns null, no cache put returning null");
				return null;
			} else if (url.charAt(0)=='r') {
					urlCache.put(new Integer(key),url);
					if (log.isDebugEnabled()) {
                        log.debug("getUrlFromCache: Cached VALUE: " + url + ", KEY:" + key);
                    }
					return url;
			} else if (url.charAt(0)=='p') {
					int pos = 0;
					StringBuffer urlsb = new StringBuffer(url);
					pos = url.indexOf(".ra");
					String cachedUrl = ""+urlsb.replace((pos-4),pos,"%%_%");
					urlCache.put(new Integer(key),cachedUrl);
					if (log.isDebugEnabled()) {
                        log.debug("getUrlFromCache: Cached VALUE: " + cachedUrl + ", KEY:" + key);
                    }
					return url; //Return original result url from method doGetUrl.
			} else {
				log.info("getUrlFromCache: Invalid Url string: " + url + " , returning null");
				return null;
			}
		} else if (url.startsWith("r")) {
			// IN CACHE and object is RealPlayer format G2 or higher.
			if (log.isDebugEnabled()) {
                log.debug("getUrlFromCache : HIT Returning entry: " + url);
            }
			return url;
		} else if (url.startsWith("p")) {
			// IN CACHE and object is RealPlayer format RA5 or lower.
			StringBuffer urlsb = new StringBuffer(url);
			int delim = '%';
			int pos = 0;
			pos = url.indexOf(delim);
			urlsb.setCharAt(pos,(""+userSpeed).charAt(0));
			url = ""+urlsb;
			pos = url.indexOf(delim);
			urlsb.setCharAt(pos,(""+userSpeed).charAt(1));
			url = ""+urlsb;
			pos = url.indexOf(delim);
			urlsb.setCharAt(pos,(""+userChannels).charAt(0));
			url = ""+urlsb;
            if (log.isDebugEnabled()) {
                log.debug("getUrlFromCache : HIT Returning entry: " + url);
            }
			return url;
		} else {
			log.info("getUrlFromCache: Invalid UrlCache entry: " + url + " , returning null");
			return null;
		}
	}

	/**
	 * Retrieves the media file url elated with the object.
	 * @param sp the scanpage
	 * @param number objectnumber requested.
	 * @param userSpeed speed settings.
	 * @param userChannels channel settings.
	 * @return a String with url of the media file or null;
	 */
	public String getUrl(scanpage sp,int number,int userSpeed,int userChannels) {
		return doGetUrl(sp,number,userSpeed,userChannels);
	}

	/**
	 * Retrieves the media file url elated with the object.
	 * A subclass must provide an implementation of this method.
	 * @param sp the scanpage
	 * @param number objectnumber requested.
	 * @param userSpeed speed settings.
	 * @param userChannels channel settings.
	 * @return a String with url of the media file or null;
	 */
	public abstract String doGetUrl(scanpage sp,int number,int userSpeed,int userChannels);

	/**
	 * Gets minimal speed setting from mediautil
	 * @return minimal speed setting
	 */
	public abstract int getMinSpeed();
	/**
	 * Gets minimal channel setting from mediautil
	 * @return minimal channel setting
	 */
	public abstract int getMinChannels();

	/**
	 * Parses the number parameter value.
	 * @param number the object number as string parameter
	 * @return the objectnumber as integer or -1 if it isn't an integer.
	 */
	public int getNumberParam(String number) {
		try {
			return Integer.parseInt(number);
		} catch(NumberFormatException nfe) {
			log.error("getNumberParam: Invalid number value:" + number);
			log.error(Logging.stackTrace(nfe));
			return -1;
		}
	}
	/**
	 * Parses the speed parameter value.
	 * @param number the speed setting as string parameter
	 * @return the speed value as integer or minimal settings if it isn't an integer.
	 */
	public int getSpeedParam(String speed) {
		try {
			return Integer.parseInt(speed);
		} catch(NumberFormatException nfe) {
			log.error("getSpeedParam: Invalid speed value:" + speed + " using default " + getMinSpeed());
			log.error(Logging.stackTrace(nfe));
			return getMinSpeed();
		}
	}
	/**
	 * Parses the channels parameter value.
	 * @param channels the channels setting as string parameter
	 * @return the channels value as integer or minimal settings if it isn't an integer.
	 */
	public int getChannelsParam(String channels) {
		try {
			return Integer.parseInt(channels);
		} catch(NumberFormatException nfe) {
			log.error("getChannelsParam: Invalid channels value:" + channels+" using default " + getMinChannels());
			log.error(Logging.stackTrace(nfe));
			return getMinChannels();
		}
	}
}
