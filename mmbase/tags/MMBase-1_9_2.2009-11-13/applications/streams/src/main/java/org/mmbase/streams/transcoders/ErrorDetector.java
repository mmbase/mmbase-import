/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.util.logging.*;
import java.util.regex.*;
/**
 *
 * @author Michiel Meeuwissen
 */

public class ErrorDetector  extends AbstractSimpleImpl implements java.io.Serializable {

    public static final Logger LOG = Logging.getLoggerInstance(ErrorDetector.class);

    protected Pattern error;
    public ErrorDetector(Pattern p){
        error = p;
        setLevel(Level.SERVICE);
    }

    @Override
    protected void log(String s, Level level) {
        if (error.matcher(s).matches()) {
            throw new Error(s);
        }
    }

}
