/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: CreateCachesFunction.java 36715 2009-07-08 22:30:03Z michiel $
 */

public class WaitUntilTranscodingFunction extends NodeFunction<Boolean> {
    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logging.getLoggerInstance(WaitUntilTranscodingFunction.class);
    public WaitUntilTranscodingFunction() {
        super("waitfor");
    }


    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        Job job = Processor.getJob(node);
        if (job != null) {
            try {
                job.waitUntil(Stage.TRANSCODER);
            } catch (InterruptedException ie) {
                return false;
            }
        }
        return true;

    }

}
