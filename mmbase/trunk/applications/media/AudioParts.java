/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*************************************************************************
 * NOTE This Builder needs significant changes to operate on NON-VPRO
 * machines. Do NOT use before that, also ignore all errors stemming from
 * this builder
 *************************************************************************/
// package org.mmbase.module.builders;
package speeltuin.media;

import java.util.*;
import java.io.*;

import org.mmbase.module.gui.html.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.module.sessionsInterface;
import org.mmbase.module.sessionInfo;

import org.mmbase.util.media.*;
import org.mmbase.util.media.audio.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.builders.Properties;

/**
 * @author David van Zeventer
 * Custom audiopart builder extends from the org version but adds support for excerpt stuff.
 * This is currently solved by creating a property for the excerpt type audiopart in which 
 * the original audiopart number is stored.
 * So requesting the audio for an excerpt consists of getting the property that stores the
 * og. audiopart through which the rawaudio url is retrieved.
 * Next the url is build up using this url and the excerpt audiopart start stop and title 
 * values.
 * @version $Id: AudioParts.java,v 1.1 2002-05-28 11:06:10 rob Exp $
 * 
 */
public class AudioParts extends org.mmbase.module.builders.AudioParts {
	private static Logger log = Logging.getLoggerInstance(AudioParts.class.getName());

	public final static int AUDIOSOURCE_EXCERPT=9;

	protected String getAudioSourceString(int source) {
		log.debug("Getting audio source from NL, source:"+source);
		String rtn="";

		switch(source) {
			case AUDIOSOURCE_DEFAULT:
				rtn="default";
				break;
			case AUDIOSOURCE_DROPBOX:
				rtn="dropbox";
				break;
			case AUDIOSOURCE_UPLOAD:
				rtn="upload";
				break;
			case AUDIOSOURCE_CD:
				rtn="cd";
				break;
			case AUDIOSOURCE_JAZZ:
				rtn="jazz";
				break;
			case AUDIOSOURCE_VWM:
				rtn="vwm";
				break;
			case AUDIOSOURCE_EXCERPT:
				rtn=""+AUDIOSOURCE_EXCERPT;
				break;
			default:
				rtn="unknown";
				break;
		}
		return(rtn);
	}

	/**
	 * Gets the url for a audiopart using the mediautil classes.
	 * First the source fieldvalue is checked to see if audiopart is an excerpt or not.
	 * If it is, we call a method that creates the audio url for an excerpt audiopart.
	 * If it isn't we return the audio url.
	 * @param mmbase mmbase reference
	 * @param sp the scanpage
	 * @param number the audiopart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @return a String with url to a audiopart or null.
	 */
	public String getAudiopartUrl(MMBase mmbase,scanpage sp,int number,int speed,int channels){
		MMObjectNode apnode = getNode(number);
		if (apnode.getIntValue("source")==AUDIOSOURCE_EXCERPT) {
			log.debug("Audiopart "+number+" is an excerpt of another audiopart.");
			return makeExcerptUrl(mmbase,sp,number,speed,channels,apnode);
		} else {
			return AudioUtils.getAudioUrl(mmbase,sp,number,speed,channels);
		}
	}

	/**
	 * Returns the url for an excerpt mediapart.
	 * In the final final version excerpt mediapart are connected to original mediapart 
	 * and get url will query mediaparts,mediaparts on which geturl will be done.	
	 * Now however, we use a property named 'sourcemediapart' to store the original mediapartnr.
	 * The url is build up using the url for original mediapart except for the querystring.
	 * Then we buildup the querystring using the info from the excerpt mediapart.
	 * And finally we put everything together and return this as the mediaurl.
	 *
	 * IMPORTANT NOTE!!!!!!: 
	 * Node properties are retrieved via the properties builder instead of the node.
	 * This is done cause mmbase still has a bug when retrieving props through the node.
	 * This results in getting a null on a property even if it exists.
	 * Only after mmbase resets this problem is (sometimes) gone.
	 *
	 * @param mmbase mmbase reference
	 * @param sp the scanpage
	 * @param number the mediapart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @param node the excerpt mediapart node.
	 * @return a String with url to a mediapart or null.
	 */
	private String makeExcerptUrl(MMBase mmbase,scanpage sp,int number,int speed,int channels,
			MMObjectNode node) {

		// Getting props via props builder directly, see method comment!
		String key = "sourcemediapart";
		MMObjectNode sourceprop = getHardProperty(number,key);
		if (sourceprop==null) {
			log.error("No property node match found for node: "+number+", with key:"+key);
			return null;
		}
		String value = sourceprop.getStringValue("value");
		int ognumber = -1;
		try { 
			ognumber = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			log.error("Can't get property "+key+" for mediapart "+number+" value:"+value); 
			nfe.printStackTrace();
		}
		if (ognumber ==-1) {
			log.error("Property value representing original mediapart:"+ognumber);
			return null;
		} else {
			// get the original url, then cutoff everything behind the querystring character.
			String ogurl = getUrl(sp,ognumber,speed,channels);	
			String leftside = ogurl.substring(0,ogurl.indexOf('?'));
			// get start and stoptimes.
			// Getting props via props builder directly, see method comment!
			String title = MediaUtils.makeRealCompatible(node.getStringValue("title"));
			MMObjectNode startprop = getHardProperty(number,"starttime");
			MMObjectNode stopprop =  getHardProperty(number,"stoptime");
			if ((startprop==null) && (stopprop==null)) {
				log.warn("Can't find start(null) & stop(null) properties for mediapart "+number
						+" returning url without them");
				return (leftside+"?"+"title="+title);
			} else {
				String start = startprop.getStringValue("value");
				String stop = stopprop.getStringValue("value");
				if ((start==null) && (stop==null)) {
					log.warn("Start("+start+") & stop("+stop+") values are null for mediapart "+number
							+" returning url without them");
					return (leftside+"?"+"title="+title);
				} else {
					log.debug("returning "+leftside+"?"+"title="+title+"&start="+start+"&end="+stop);
					return (leftside+"?"+"title="+title+"&start="+start+"&end="+stop);
				}
			}
		}
	}
	
	/**
	 * Retrieves a property by quering on the properties builder. 
	 * @param number object number of node to which props may be connected.
	 * @param key the property wanted with this key. 
	 * @return the property node or null if no match was found.
	 */
	private MMObjectNode getHardProperty(int number, String key) {
		Properties propsbul = (Properties)mmb.getMMObject("properties");
		Enumeration e = propsbul.search("WHERE parent="+number+" and key='"+key+"'");
		if (e.hasMoreElements()) {
			return (MMObjectNode)e.nextElement();	
		}
		return null;
	}
}
