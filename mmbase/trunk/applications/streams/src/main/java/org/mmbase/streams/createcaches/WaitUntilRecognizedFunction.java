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
 * @author André van Toly
 * @version $Id$
 */

public class WaitUntilRecognizedFunction extends NodeFunction<Boolean> {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logging.getLoggerInstance(WaitUntilRecognizedFunction.class);
    public WaitUntilRecognizedFunction() {
        super("waitfor");
    }


    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        Job job = Processor.getJob(node);
        if (job != null) {
            try {
                job.waitUntilAfter(Stage.RECOGNIZER);
            } catch (InterruptedException ie) {
                return false;
            }
        }
        return true;

    }

}
