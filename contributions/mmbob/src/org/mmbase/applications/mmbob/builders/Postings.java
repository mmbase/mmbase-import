/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.builders;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;
import org.mmbase.applications.mmbob.util.transformers.Smilies;


/**
 * Builder implementation for using smileys inside the Postings. 
 * It uses the theme used within the forum and the path to the images, to return 
 * the smiley images.
 * 
 * It contains a few methods to be MMBase-1.7.x compatible, these are marked deprecated
 * and will be removed in the future.
 *
 * To use it in eg a jsp, use this code:
 * (where postingid is the number of the posting, themeid is the id of the theme to be used
 * and imagecontext is the path to the images)
 * <code>
 * <pre>
 * &lt;mm:node referid="postingid"&gt;
 *  &lt; mm:function referids="imagecontext,themeid" name="escapesmilies"&gt;
 * &lt;node&gt;
 * </pre>
 * </code>
 *
 * @author Gerard van Enk
 * @version $Id: Postings.java,v 1.3 2005-02-22 15:30:59 gerard Exp $
 * @since MMBob-1.0
 */
public class Postings extends MMObjectBuilder { 
    private static final Logger log = Logging.getLoggerInstance(Postings.class);

    /** default params */
    public final static Parameter[] ESCAPESMILIES_PARAMETERS = {
        /* name, type, default value */
        new Parameter("imagecontext", String.class, "/thememanager/images"),
        new Parameter("themeid", String.class, "default"), 
        new Parameter("smileysetid", String.class, "default"),
        new Parameter("name", String.class, "body"),
        new Parameter(Parameter.CLOUD, true)                  /* true: required! */
    };
    /** The smilies transformer */
    private static Smilies smilies = new Smilies ();

    /**
     * A very crude way to implement getParameterDefinition, 
     * using the utitily function in NodeFunction, which uses
     * reflection to find the constant(s) defined in this class.
     * This method is overrriden from MMObjectBuilder 
     *
     * @param function name of the function which is called
     * @return the parameters for this function
     * @deprecated only for MMBase-1.7.x compatibility 
     */
    // overridden from MMObjectBuilder
    public Parameter[] getParameterDefinition(String function) {
        return this.ESCAPESMILIES_PARAMETERS;
    }


    /**
     * The escapeSmilies function implementation. This function will replace in a field
     * of an MMObjectNode all know smilies in their graphical version.
     * The fieldname of the field defaults to body, but can be overridden in the params.
     * 
     * @param node the MMObjectNode containing the field needed to be transformed
     * @param p the params used for replacing the smilies
     * @return the replaced version of the field
     * @deprecated only for MMBase-1.7.x compatibility
     */
    private String escapeSmiliesImplementation(MMObjectNode node, Parameters p) {
        String imagecontext = (String) p.get("imagecontext");
        String themeid = (String) p.get("themeid");
        String smileysetid = (String) p.get("smileysetid");
        //get fieldname to use, defaults to body
        String fieldname = (String) p.get("name");
        if (log.isDebugEnabled()) {
            log.debug("using the following params:");
            log.debug("  imagecontext: " + imagecontext);
            log.debug("  themeid: " + themeid);
            log.debug("  smileysetid: " + smileysetid);
            log.debug("  fieldname: " + fieldname);
        }
        Cloud cloud = (Cloud) p.get(Parameter.CLOUD);
        String field = node.getStringValue(fieldname);
        String result = "";
        if (field != null) {
            //use the smilies transformer to replace the smilies
            result = smilies.transform(field, themeid, imagecontext);
        }
        if (log.isDebugEnabled()) {
            log.debug("result afther transformation is: " + result);
        }
        return result;
    }

    /**
     * The escapeSmilies function implementation. This function will replace in a field
     * of an MMObjectNode all know smilies in their graphical version.
     * The fieldname of the field defaults to body, but can be overridden in the params.
     * 
     * @param node the MMObjectNode containing the field needed to be transformed
     * @param args the list of params used for replacing the smilies
     * @return the replaced version of the field
     */
    private String escapeSmiliesImplementation(MMObjectNode node, List args) {
        String imagecontext = (String) args.get(0);
        String themeid = (String) args.get(1);
        String smileysetid = (String) args.get(2);
        //get fieldname to use, defaults to body
        String fieldname = (String) args.get(3);
        if (log.isDebugEnabled()) {
            log.debug("using the following params:");
            log.debug("  imagecontext: " + imagecontext);
            log.debug("  themeid: " + themeid);
            log.debug("  smileysetid: " + smileysetid);
            log.debug("  fieldname: " + fieldname);
        }
        Cloud cloud = (Cloud) args.get(4);
        String field = node.getStringValue(fieldname);
        String result = "";
        if (field != null) {
            //use the smilies transformer to replace the smilies
            result = smilies.transform(field, themeid, imagecontext);
        }
        if (log.isDebugEnabled()) {
            log.debug("result afther transformation is: " + result);
        }
        return result;    
    }


    // overridden from MMObjectBuilder
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.debug("executefunction of Postings builder " + function + " " + args);
        }
        if (function.equals("info")) {
            List empty = new ArrayList();
            Map info = (Map) super.executeFunction(node, function, empty);
            info.put("escapesmilies",     "" + ESCAPESMILIES_PARAMETERS + " transfers smiley code into image tags");
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (function.equals("escapesmilies")) {
            return escapeSmiliesImplementation(node, args);
        } else {
            return super.executeFunction(node, function, args);
        }
    }


}
