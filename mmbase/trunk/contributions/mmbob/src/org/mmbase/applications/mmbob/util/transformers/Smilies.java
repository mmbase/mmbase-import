/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.util.transformers;

import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.regex.*;

import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;
import org.mmbase.applications.thememanager.*;

/**
 * Replaces known smilies with their graphical version. 
 * It uses the thememanager for defining the smilies.
 *
 * @author Gerard van Enk 
 * @version $Id: Smilies.java,v 1.8 2007-01-24 16:06:43 michiel Exp $
 * @since MMBob-1.0
 */
public class Smilies extends StringTransformer implements CharTransformer {
    private static final Logger log = Logging.getLoggerInstance(Smilies.class);
    private String defaultid = "default";
    private String defaultcontext = "/thememanager/images";

    /** All known smilies */
    protected static final Map smilies = new HashMap();
    /** All known smileysets */ 
    protected static final Map smileySets = new HashMap();
    /** Smiley patterns translated into their regexp version */
    protected static final Map smileyPatterns = new HashMap();
    /** Compiled regexps*/
    protected static final Map smileyMatchers = new HashMap();

    public Smilies() {
    }

    public Smilies(String defaultid, String defaultcontext) {
        this.defaultid = defaultid;
        this.defaultcontext = defaultcontext;
    }

    /**
     * Initializes a specific smileySet in a specific theme.
     *
     * @param themeID id of of the theme to be used to access the smileyset
     * @param smileySetID the id of the smileyset to be used to access the smiley
     * @param smileyKey the unique key of the smileyset
     */
    protected void initSmileySets(String themeID, String smileySetID, String smileyKey) {
        log.debug("init smileyset:\ntheme: " + themeID 
                + "\nsmileySetID: " + smileySetID + "\nsmileyKey: " + smileyKey);
        Theme theme = ThemeManager.getTheme(themeID);
        if (theme != null) {
            ImageSet is = theme.getImageSet(smileySetID);
            smileySets.put(smileyKey, is);
        } else {
            log.error("could not find smileyset (theme: " + themeID + "smileySetID: " + smileySetID + "smileyKey: " + smileyKey+")");
        }
    }

    /**
     * Initializes regexp patterns for the combination of (themeID,smileySetID,smileyKey,smilies)
     *
     * @param themeID id of of the theme to be used to access the smileyset
     * @param smileySetID the id of the smileyset to be used to access the smiley
     * @param smileyKey the id of the smiley (this is the text version of the smiley)
     */
    protected void initPatterns(String themeID, String smileySetID, String smileyKey) {
        if (!smileySets.containsKey(smileyKey)) {
            //init the smileyset if it hasn't been initialized already
            initSmileySets(themeID, smileySetID, smileyKey);
        }
        //get the smileyset
        ImageSet smileySet = (ImageSet)smileySets.get(smileyKey);
        if (smileySet == null) return;
        //get number of smileys in this set
        Pattern[] patterns = new Pattern[smileySet.getCount()];
        if (log.isDebugEnabled()) {
            log.debug("There are " + smileySet.getCount() + " smilies in this set (theme: " + themeID 
                    + "smileySetID: " + smileySetID + "smileyKey: " + smileyKey+")");
        }
        int i = 0;
        //get all smileys in the set and it's graphical version and add it to the patterns (and compile it)
        for (Iterator it = smileySet.getImageIds(); it.hasNext();) {
            patterns[i] = Pattern.compile("\\Q"+(String) it.next()+"\\E");
            i++;
        }
        smileyPatterns.put(smileyKey,patterns);
    }

    /**
     * Initializes regexp matchers for the combination of (themeID,smileySetID,smileyKey,smilies)
     *
     * @param themeID id of of the theme to be used to access the smileyset
     * @param smileySetID the id of the smileyset to be used to access the smiley
     * @param smileyKey the id of the smiley (this is the text version of the smiley)
     */
    protected void initMatchers(String themeID, String smileySetID, String smileyKey) {
        if (!smileyPatterns.containsKey(smileyKey)) {
            //if there are no patterns for this combination init it
            initPatterns(themeID, smileySetID, smileyKey);
        }
        Pattern[] patterns = (Pattern[])smileyPatterns.get(smileyKey);
        if (patterns == null) {
            log.warn("There is not smiley key '" + smileyKey + "' in smileySet '" + smileySetID + "' of theme '" + themeID + "'");
            return;
        }
        //get the matchers
        Matcher[] matchers = new Matcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = patterns[i].matcher("test");
        }
        //store the matchers for later use
        smileyMatchers.put(smileyKey,matchers);
    }

    /**
     * Default transform method (with no other params).
     * It will transform the originalString into a version with the graphical version of the smilies
     *
     * @param originalString the string which must be transformed
     * @return the transformed string
     */
    public String transform (String originalString) {
        return transform(originalString, defaultid, defaultcontext);
    }

    /**
     * Transform the originalString into a version with the graphical version of the smilies
     *
     * @param originalString the string which must be transformed
     * @param themeID the id of the theme to be used
     * @param imagecontext the image path to be used
     * @return the transformed string
     */
    public String transform (String originalString, String themeID, String imagecontext) {
        boolean found = false;
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer tempBuffer = new StringBuffer(originalString);
        //get theme
        String assignedID = ThemeManager.getAssign(themeID);
        Theme theme = ThemeManager.getTheme(themeID);
        String smileySetID = "default";

        if (theme != null) {
            Map imageSets = theme.getImageSets("smilies");
            if (imageSets == null) {
                log.warn("Theme '" + theme + "' has no smilies. Now not escaping smilies.");
                return originalString;
            }
            Iterator i = imageSets.entrySet().iterator();
            while(i.hasNext()) {
                ImageSet is = (ImageSet)imageSets.get(((Map.Entry)i.next()).getKey());
                //atm let's use the last one and hope there's only one
                smileySetID = is.getId();
            }
        } else {
            //get default imageset somewhere?
            log.warn("theme '" + themeID + "' not found");
        }
        String smileyKey = themeID + "." + smileySetID;
        log.debug("smileyKey = " + smileyKey);

        //get matcher or init it
        if (!smileyMatchers.containsKey(smileyKey)) {
            initMatchers(themeID, smileySetID, smileyKey);
        }
        ImageSet smileySet = (ImageSet)smileySets.get(smileyKey);
        Matcher[] matchers = (Matcher[])smileyMatchers.get(smileyKey);
        if (matchers == null) {
            log.warn("No smiley matchers for key '" + smileyKey + "' found. Returing unmodified string.");
            return originalString;
        }
        //loop through all smileys and check if they are found in the original text
        for (int i = 0; i < matchers.length; i++) {
            resultBuffer = new StringBuffer();
            matchers[i].reset(tempBuffer);
            //find next match
            while (matchers[i].find()) {
                if (log.isDebugEnabled()) {
                    log.debug("found the text \"" + matchers[i].group() +
                              "\" starting at index " + matchers[i].start() +
                              " and ending at index " + matchers[i].end() + ".");
                    log.debug("bijbehorende image: "+ (String)smileySet.getImage(matchers[i].group()));
                }
                found = true;
                //replace smiley with graphical version
                matchers[i].appendReplacement(resultBuffer,"<img src=\"" + imagecontext +"/" + themeID + "/" + smileySetID + "/"  + (String)smileySet.getImage(matchers[i].group()) + "\" />");
            }

            if (found) {
                matchers[i].appendTail(resultBuffer);
                tempBuffer = resultBuffer;
                found = false;
            } else {
                log.debug("nothing found");
                resultBuffer = tempBuffer;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("origalString: "+originalString);
            log.debug("result: "+resultBuffer.toString());
        }
        return resultBuffer.toString();
    }


    public String toString() {
        return "SMILIES";
    }








    /* this methods aren't used at the moment, and maybe they must be removed, but I'm not sure (GvE)
    protected static void init() {
        smilies = new HashMap();
        //smilies.put(":)","images/smile.gif");
        String themeid="MMBaseWebsite";
        Theme theme=ThemeManager.getTheme(themeid);
        if (theme!=null) {
                is=theme.getImageSet("blue");
                Iterator i=is.getImageIds();
                while (i.hasNext()) {
                        String id=(String)i.next();
                        smilies.put(id,"/thememanager/images/"+themeid+"/blue/"+is.getImage(id));
                }


	} else {
		log.error("Can't find theme for smilies");
	}
    }

    public static void init(String themeID, String smileysetID) {
        log.debug("going to get theme with id = " + themeID);
        String assignedID = ThemeManager.getAssign(themeID);
        Theme theme = ThemeManager.getTheme(assignedID);
        if (theme != null) {
            Map imageSets = theme.getImageSets("smilies");
            Iterator i = imageSets.entrySet().iterator();
            while(i.hasNext()) {
                ImageSet is = (ImageSet)imageSets.get(((Map.Entry)i.next()).getKey());
                smileySets.put(themeID,is);
            }

            //is=theme.getImageSet("blue");
            //Iterator i=is.getImageIds();
            //	while (i.hasNext()) {
			//String id=(String)i.next();
            //smilies.put(id,"/thememanager/images/"+themeid+"/blue/"+is.getImage(id));
            //}
	} else {
		log.error("Can't find theme for smilies");
	}
	
    }*/


}
