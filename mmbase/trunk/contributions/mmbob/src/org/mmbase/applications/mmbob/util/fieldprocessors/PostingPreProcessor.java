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
 * Replaces certain 'forbidden' words by something more decent. Of course, censoring is evil, but
 * sometimes it can be amusing too. This is only an example implementation.
 *
 * @author Gerard van Enk 
 * @since MMBase-1.7
 * @version $Id: PostingPreProcessor.java,v 1.1 2004-06-13 14:29:22 daniel Exp $
 */

public class PostingPreProcessor implements Processor {
    private static Logger log = Logging.getLoggerInstance(PostingPreProcessor.class);

    private PostingBody postingbody;

    public PostingPreProcessor() {
        log.info("init postprocessor");
        postingbody = new PostingBody();
    }

    public final Object process(Node node, Field field, Object value) {
        log.info("value="+value);
        if (value == null) return null; 
        log.info("going to process");
        return "<posting>"+(String)postingbody.transform((String)value)+"</posting>";
    }


    public String toString() {
        return "PostingPreProcessor";
    }
}
