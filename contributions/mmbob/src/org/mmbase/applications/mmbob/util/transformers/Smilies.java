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
 * Replaces certain 'forbidden' words by something more decent. Of course, censoring is evil, but
 * sometimes it can be amusing too. This is only an example implementation.
 *
 * @author Gerard van Enk 
 * @since MMBob
 * @version $Id: Smilies.java,v 1.1 2004-06-13 14:30:34 daniel Exp $
 */

public class Smilies extends StringTransformer implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(Smilies.class);

    protected static Map smilies; 
    protected static Map smileySets;
    protected static Map smileyPatterns;
    protected static Map smileyMatchers;
    private static Pattern[] patterns;
    private static Matcher[] matchers;
    private static ImageSet is;

    public Smilies() {
        smileySets = new HashMap();
        smileyPatterns = new HashMap ();
        smileyMatchers = new HashMap ();
    }


    protected static void initSmilies() {
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


    public static void initSmilies(String themeID, String smileysetID) {
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
	
    }

    protected void initSmileySets(String themeID, String smileySetID, String smileyKey) {
        log.debug("going to init smilies");
        Theme theme = ThemeManager.getTheme(themeID);
        if (theme != null) {
            ImageSet is = theme.getImageSet(smileySetID);
            smileySets.put(smileyKey,is);
        } else {
            log.error("couldn't find imageset");
        }
    }

    protected void initPatterns(String themeID, String smileySetID, String smileyKey) {
        log.debug("going to init the patterns");
        Pattern[] patterns;
        ImageSet smileySet;
        if (!smileySets.containsKey(smileyKey)) {
            initSmileySets(themeID, smileySetID, smileyKey);
        }
        smileySet = (ImageSet)smileySets.get(smileyKey);

        patterns = new Pattern[smileySet.getCount()];
        log.debug("er zijn: " + smileySet.getCount() + " smilies");
        int i = 0;
        for (Iterator it = smileySet.getImageIds(); it.hasNext();) {
            //log.debug("smiley = " + (String) it.next());
            patterns[i] = Pattern.compile("\\Q"+(String) it.next()+"\\E");
            i++;
        }
        log.debug("added all patterns");
        smileyPatterns.put(smileyKey,patterns);
    }

    protected void initMatchers(String themeID, String smileySetID, String smileyKey) {
        log.debug("going to init the matchers");
        Pattern [] patterns;
        Matcher [] matchers;
        if (!smileyPatterns.containsKey(smileyKey)) {
            initPatterns(themeID, smileySetID, smileyKey);
        }
        patterns = (Pattern[])smileyPatterns.get(smileyKey);

        matchers = new Matcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = patterns[i].matcher("test");
        }

        smileyMatchers.put(smileyKey,matchers);
    }

    /*
    protected void initMatchers() {
        if (matchers == null) {
            if (patterns == null) {
                initPatterns();
            }
            matchers = new Matcher[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                matchers[i] = patterns[i].matcher("test");
            }
        }
    }
    */

    public String transform (String originalString) {
        return transform(originalString, "default", "/thememanager/images");
    }

    public String transform (String originalString, String themeID, String imagecontext) {
        int replaced = 0;
        String code = null;
        Pattern pattern;
        Matcher matcher;
        boolean found = false;
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer tempBuffer = new StringBuffer(originalString);

        ImageSet smileySet;

        log.debug("going to get theme with id = " + themeID);
        String assignedID = ThemeManager.getAssign(themeID);
        Theme theme = ThemeManager.getTheme(assignedID);
        String smileySetID = "default";
        if (theme != null) {
            Map imageSets = theme.getImageSets("smilies");
            Iterator i = imageSets.entrySet().iterator();
            while(i.hasNext()) {
                ImageSet is = (ImageSet)imageSets.get(((Map.Entry)i.next()).getKey());
                //atm let's use the last one and hope there's only one
                smileySetID = is.getId();
            } 
        } else {
            //get default imageset somewhere?
        }
        String smileyKey = assignedID + "." + smileySetID;
        log.debug("smileyKey = " +smileyKey);
        Matcher[] matchers;
        if (!smileyMatchers.containsKey(smileyKey)) {
            initMatchers(assignedID, smileySetID, smileyKey);
        }
        smileySet = (ImageSet)smileySets.get(smileyKey);
        matchers = (Matcher[])smileyMatchers.get(smileyKey);

        for (int i = 0; i < matchers.length; i++) {
            resultBuffer = new StringBuffer();
            matchers[i].reset(tempBuffer);
            
            while (matchers[i].find()) {
                log.debug("I found the text \"" + matchers[i].group() +
                               "\" starting at index " + matchers[i].start() +
                               " and ending at index " + matchers[i].end() + ".");
                log.debug("bijbehorende image: "+ (String)smileySet.getImage(matchers[i].group()));
                found = true;
                matchers[i].appendReplacement(resultBuffer,"<img src=\"" + imagecontext +"/" + assignedID + "/" + smileySetID + "/"  + (String)smileySet.getImage(matchers[i].group()) + "\" />");
            }

            if (found) {
                matchers[i].appendTail(resultBuffer);
                tempBuffer = resultBuffer;
                found = false;
            } else {
                log.debug("helaas, niets gevonden");
                resultBuffer = tempBuffer;
            }

            //result = originalString.replaceAll("\\(?<=.\\W|\\W.|^\\W\\)\\Q"+code+"\\E\\(?=.\\W|\\W.|\\W$\\)",(String)smilies.get(code));
            //result = originalString.replaceAll(code,(String)smilies.get(code));

        }
        log.debug("origalString: "+originalString);
        log.debug("result: "+resultBuffer.toString());
        return resultBuffer.toString();
            
    }

    /*    public Writer transform(Reader r, Writer w) {
        int replaced = 0;
        StringBuffer word = new StringBuffer();  // current word
        try {
            log.trace("Starting Smilies");
            while (true) {
                int c = r.read();
                if (c == -1) break;
                if ( Character.isWhitespace((char) c)) {
                    if (replace(word.toString(), w)) replaced++;
                    word.setLength(0);
                    w.write(c);
                } else {       
                    word.append((char) c);
                }
            }
            // write last word
            if (replace(word.toString(), w)) replaced++;
            log.debug("Finished Smilies. Replaced " + replaced + " words");
        } catch (java.io.IOException e) {
            log.error(e.toString());
        }
        return w;
        }*/


    public String toString() {
        return "SMILIES";
    }
}
