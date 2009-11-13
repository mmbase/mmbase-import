/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.security.ActionRepository;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: CreateCachesFunction.java 36715 2009-07-08 22:30:03Z michiel $
 */

public class WaitUntilTranscodingFunction extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(WaitUntilTranscodingFunction.class);
    public WaitUntilTranscodingFunction() {
        super("waitfor");
    }


    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        CreateCachesProcessor.Job job = CreateCachesProcessor.getJob(node);
        if (job != null) {
            try {
                job.waitUntilTranscoding();
            } catch (InterruptedException ie) {
                return false;
            }
        }
        return true;

    }

}
