/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.mmbob.util.transformers;


import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.*;

/**
 */
public class BBCode {
    private static Logger log = Logging.getLoggerInstance(BBCode.class);

    public static String decode(String body) {
        StringObject obj=new StringObject(body);
	obj.replace("[/QUOTE]","</quote>");
	return obj.toString();
    }

}
