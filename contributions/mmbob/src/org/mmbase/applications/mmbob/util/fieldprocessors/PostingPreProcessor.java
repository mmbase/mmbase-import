/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.util.fieldprocessors;

import java.io.Reader;
import java.io.Writer;
import java.util.*;
import java.util.regex.*;

import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.applications.mmbob.util.transformers.PostingBody;

/**
 * Processes the body of every posting it's committed into the database, 
 * it will be translated it the MMBob xml-format 
 *
 * @author Gerard van Enk 
 * @since MMBase-1.7
 * @version $Id: PostingPreProcessor.java,v 1.2 2005-02-22 15:29:12 gerard Exp $
 */

public class PostingPreProcessor implements Processor {
    private static Logger log = Logging.getLoggerInstance(PostingPreProcessor.class);

    //the transformer to be used
    private PostingBody postingbody;

    /**
     * Constructor
     */
    public PostingPreProcessor() {
        log.info("init");
        postingbody = new PostingBody();
    }

    /**
     * processes the value of the field before commiting it into the database
     * 
     * @param node the node which is going to be committed
     * @param field the field which must be processed
     * @param value the value of the field
     * @return the processed value
     */
    public final Object process(Node node, Field field, Object value) {
        log.debug("value = " + value);
        if (value == null) return null; 
        return "<posting>"+(String)postingbody.transform((String)value)+"</posting>";
    }


    public String toString() {
        return "PostingPreProcessor";
    }
}
