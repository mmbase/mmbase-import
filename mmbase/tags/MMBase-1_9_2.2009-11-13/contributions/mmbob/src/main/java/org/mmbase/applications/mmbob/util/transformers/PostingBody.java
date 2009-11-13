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

/**
 * xmlize the posting body.
 * At this moment it will only escape the characters:
 * & --> &amp;
 * " --> &quot;
 * < --> &lt;
 * > --> &gt;
 * \r --> ""
 * \n --> <br />
 * In the future probably more escaping will be done (urls, images, etc.)
 *
 * @author Gerard van Enk 
 * @since MMBob 
 * @version $Id$
 */
public class PostingBody {
    private static Logger log = Logging.getLoggerInstance(PostingBody.class);
    //map of all characters which need to be replaced
    protected static Map replacements; 
    private static Pattern[] patterns;
    private static Matcher[] matchers;

    /**
     * Initializes the Map containing the replacements
     */
    protected static void initReplacements() {
        replacements = new HashMap();
        replacements.put("&","&amp;");
        replacements.put("\"","&quot;");
        replacements.put("<","&lt;");
        replacements.put(">","&gt;");
        replacements.put("\r","");
        replacements.put("\n","<br />");
    }


    /**
     * Initializes the pattern array with all possible handlers.
     */
    protected void initPatterns () {
        if (patterns == null) {
            if (replacements == null) {
                initReplacements();
            }
            patterns = new Pattern[6];
            patterns[0] = Pattern.compile("&");
            patterns[1] = Pattern.compile("(?<!(<quote poster=|poster=[\"][\\w\\s]{1,255}))\"");
            patterns[2] = Pattern.compile(">(?<!(poster=[\"][\\w\\s]{1,255})\">|</quote>)");
            patterns[3] = Pattern.compile("<(?!(quote|/quote))");
            patterns[4] = Pattern.compile("\r");
            patterns[5] = Pattern.compile("\n");
        }
    }

    /**
     * Initializes the matcher array
     */
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

    /**
     * transforms the body in the xml-version.
     *
     * @param originalBody the body to be transformed
     */
    public String transform(String originalBody) {
        StringBuffer escapedBody = escapeStandard(originalBody);
        return escapedBody.toString();
    }

    /**
     * really transforms the body in the xml-version.
     *
     * @param body the body to be transformed
     */
    private StringBuffer escapeStandard(String body) {
        int replaced = 0;
        String code = null;
        Pattern pattern;
        Matcher matcher;
        boolean found = false;
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer tempBuffer = new StringBuffer(body);
        //init matchers if they weren't initialized already
        if (matchers == null) {
            initMatchers();
        }

        for (int i = 0; i < matchers.length; i++) {
            resultBuffer = new StringBuffer();
            matchers[i].reset(tempBuffer);            
            while (matchers[i].find()) {
                if (log.isDebugEnabled()) {
                    log.debug("found the text \"" + matchers[i].group() +
                               "\" starting at index " + matchers[i].start() +
                               " and ending at index " + matchers[i].end() + ".");
                }
                found = true;
                matchers[i].appendReplacement(resultBuffer,"" + (String)replacements.get(matchers[i].group()));
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
            log.debug("origalString: "+body);
            log.debug("result: "+resultBuffer.toString());
        }
        return resultBuffer;      
    }


    public String toString() {
        return "PostingBody";
    }
}
