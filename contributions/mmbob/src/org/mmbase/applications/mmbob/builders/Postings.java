package org.mmbase.applications.mmbob.builders;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.SortOrder;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;
import org.mmbase.applications.mmbob.util.transformers.Smilies;


/**
 * Example builder implementation implementing functions. Lots of people are sooner or earlier
 * trying to make their own builder implementation. Especially whith the advent the 'function' tags in 
 * 1.7 it would be nice that people could seen an example of how that could be done.
 *
 * To try it out, take a builder xml and add 
 * <code>&lt;classfile&gt;org.mmbase.util.functions.ExampleBuilder&lt;/classfile&gt; </code>
 * and e.g. a jsp like this:
 * <code>
 * <pre>
 * &lt;mm:listnodes type="pools" max="1"&gt;
 *  &lt; mm:import id="max"&gt;100&lt;/mm:import&gt;
 *   &lt;mm:nodelistfunction referids="max" name="function1"&gt;
 *    -- &lt;mm:field name="number" /&gt;&lt;br /&gt;
 *   &lt/mm:nodelistfunction&gt;
 * &lt;/mm:listnodes&gt;
 * </pre>
 * </code>
 *
 * @author Gerard van Enk
 * @version $Id: Postings.java,v 1.2 2004-12-09 17:23:05 daniel Exp $
 * @since MMBob-1.0
 */
public class Postings extends MMObjectBuilder { 
    private static final Logger log = Logging.getLoggerInstance(Postings.class);

    public final static Parameter[] ESCAPESMILIES_PARAMETERS = {
        /* name, type, default value */
        new Parameter("imagecontext", String.class, "/thememanager/images"),
        new Parameter("themeid", String.class, "default"), 
        new Parameter("smileysetid", String.class, "default"),
        new Parameter("name", String.class, "body"),
        new Parameter(Parameter.CLOUD, true)                  /* true: required! */
    };

    private static Smilies smilies = new Smilies ();

    /**
     * A very crude way to implement getParameterDefinition, using the utitily function in NodeFunction, which uses
     * reflection to find the constant(s) defined in this class.
     * 
     * If you prefer you could also use an explicit if/else tree to reach the same goal.
     */
    // overridden from MMObjectBuilder
	/*
    public Parameter[] getParameterDefinition(String function) {
        Parameter[] params = NodeFunction.getParametersByReflection(Postings.class, function);
        if (params == null) return super.getParameterDefinition(function);
        return params;
        
    }
	*/

    /**
     * A 'function' implementation which ignores the 'node' and does something with a 'Cloud' object.
     * @todo this might be interpreted as a function on the builder, somehow!
     */

    private String escapeSmiliesImplementation(MMObjectNode node, List args) {
        String imagecontext = (String) args.get(0);
        String themeid = (String) args.get(1);
        String smileysetid = (String) args.get(2);
        String fieldname = (String) args.get(3);
        Cloud cloud = (Cloud) args.get(4);
	/*
        String themeid = (String) p.get("themeid");
        log.debug("themeid="+themeid);
        String smileysetid = (String) p.get("smileysetid");
        String fieldname = (String) p.get("name");
	*/
        //smilies.initSmilies(themeid, smileysetid);
        String field = node.getStringValue(fieldname);
        String result = "";
        log.debug("Before: escapeSmiliesImpl: themeid = " + themeid + ", smileysetid = " +
                  smileysetid + ", fieldname = " + fieldname + ", result = " + result);
        if (field != null) {
            result = smilies.transform(field, themeid, imagecontext);
        }

        log.debug("After: escapeSmiliesImpl: themeid = " + themeid + ", smileysetid = " +
                  smileysetid + ", fieldname = " + fieldname + ", result = " + result);

        return result;

        /*
        Integer max = (Integer) p.get("max");
        Cloud cloud = (Cloud) p.get(Parameter.CLOUD);
        // Node n = cloud.getNode(node.getNumber());
        NodeManager thisManager = cloud.getNodeManager(getTableName());
        NodeQuery q = thisManager.createQuery();
        q.setMaxNumber(max.intValue());
        q.addSortOrder(q.getStepField(thisManager.getField("number")), SortOrder.ORDER_DESCENDING);
        return thisManager.getList(q);*/        

    }

    // overridden from MMObjectBuilder
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (log.isDebugEnabled()) {
            log.info("executefunction of Postings builder " + function + " " + args);
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
            // more examples should be implemented here.
        } else {
            return super.executeFunction(node, function, args);
        }
    }


}
