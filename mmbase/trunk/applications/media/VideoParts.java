/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/

// package org.mmbase.module.builders;
package speeltuin.media;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.gui.html.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.sessionsInterface;
import org.mmbase.module.sessionInfo;

import org.mmbase.util.media.*;
import org.mmbase.util.media.video.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author David van Zeventer
 * Custom videopart builder extends from the org version but adds support for excerpt stuff.
 * This is currently solved by creating a property for the excerpt type videopart in which
 * the original videopart number is stored.
 * So requesting the video for an excerpt consists of getting the property that stores the
 * og. videopart through which the rawvideo url is retrieved.
 * Next the url is build up using this url and the excerpt videopart start stop and title
 * values.
 * @version $Id: VideoParts.java,v 1.1 2002-05-28 11:06:10 rob Exp $
 */
public class VideoParts extends org.mmbase.module.builders.VideoParts {

	private static Logger log = Logging.getLoggerInstance(VideoParts.class.getName()); 

	public final static int VIDEOSOURCE_EXCERPT=9;

	/**
	 * Gets the url for a videopart using the mediautil classes.
	 * First the source fieldvalue is checked to see if videopart is an excerpt or not.
	 * If it is, we call a method that creates the video url for an excerpt videopart.
	 * If it isn't we return the video url.
	 * @param mmbase mmbase reference
	 * @param sp the scanpage
	 * @param number the videopart object number
	 * @param speed the user speed value
	 * @param channels the user channels value
	 * @return a String with url to a videopart or null.
	 */
	public String getVideopartUrl(MMBase mmbase,scanpage sp,int number,int speed,int channels){
		MMObjectNode vpnode = getNode(number);
		if (vpnode.getIntValue("source")==VIDEOSOURCE_EXCERPT) {
			log.debug("Videopart "+number+" is an excerpt of another videopart.");
			return makeExcerptUrl(mmbase,sp,number,speed,channels,vpnode);
		} else {
			return VideoUtils.getVideoUrl(mmbase,sp,number,speed,channels);
		}
	}


	/**
	 * (Copy pasted from AudioParts.java in this package. )
	 * Returns the url for an excerpt mediapart.
	 * In the final final version excerpt mediapart are connected to original mediapart
	 * and get url will query mediaparts,mediaparts on which geturl will be done.
	 * Now however, we use a property named 'sourcemediapart' to store the original mediapartnr.
	 * The url is build up using the url for original mediapart except for the querystring.
	 * Then we buildup the querystring using the info from the excerpt mediapart.
	 * And finally we put everything together and return this as the mediaurl.
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

		String key = "sourcemediapart";
		MMObjectNode sourceprop = (MMObjectNode)node.getProperty(key);
		if (sourceprop==null) {
			log.error("Property node containing original mediapart nr is null");
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
			String title = MediaUtils.makeRealCompatible(node.getStringValue("title"));
			MMObjectNode startprop = (MMObjectNode)node.getProperty("starttime");
			MMObjectNode stopprop = (MMObjectNode)node.getProperty("stoptime");
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
}
