/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import java.util.regex.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;


/**
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class MockAnalyzer implements Analyzer {


    private static final Logger LOG = Logging.getLoggerInstance(MockAnalyzer.class);

    private final ChainedLogger log = new ChainedLogger(LOG);
    protected final AnalyzerUtils util = new AnalyzerUtils(log);
    {
        util.setUpdateSource(true);
    }

    protected int x = -1;
    protected int y = -1;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    private List<Throwable> errors = new ArrayList<Throwable>();

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }

    public int getMaxLines() {
        return Integer.MAX_VALUE;
    }

    public void analyze(String l, Node source, Node des) {

    }

    public void ready(Node sourceNode, Node destNode) {
        synchronized(util) {
            if (destNode != null) {
                destNode.setValue("height", y);
                destNode.setValue("width", x);
                destNode.commit();
            }
            util.toVideo(sourceNode, destNode);
            sourceNode.commit();
            log.info("READY for " + sourceNode.getNodeManager().getName() + " " + sourceNode.hashCode() + " " + sourceNode.getNumber());
        }

    }
    @Override
    public MockAnalyzer clone() {
        try {
            return (MockAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
