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
import org.mmbase.util.functions.*;

/**
 * Allow the smilies transformer to be configurable
 *
 * @author Johannes Verelst
 * @version $Id: SmileyFactory.java,v 1.2 2006-12-20 13:00:08 johannes Exp $
 */
public class SmileyFactory implements ParameterizedTransformerFactory {
    private static final Parameter[] PARAM = new Parameter[] {
        new Parameter("themeid", String.class, "default"),
        new Parameter("imagecontext",   String.class, "/mmbase/thememanager/"),
    };


    public Transformer createTransformer(Parameters parameters) {
        return new Smilies((String)parameters.get("themeid"), (String)parameters.get("imagecontext"));
    }

    public Parameters createParameters() {
        return new Parameters(PARAM);
    }
}

