/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.util.logging.*;
import org.mmbase.bridge.Node;


/**
 * A Logger which only wraps  a {@link Analyzer} to analyze the logs.
 *
 * @author Michiel Meeuwissen
 */

public class AnalyzerLogger  extends AbstractSimpleImpl {

    public static final Logger LOG = Logging.getLoggerInstance(AnalyzerLogger.class);

    private final Analyzer analyzer;
    private final Node source;
    private final Node destination;

    private int line = 0;
    private boolean commited = false;

    public AnalyzerLogger(Analyzer a, Node source, Node destination) {
        analyzer = a;
        this.source = source;
        this.destination = destination;
        setLevel(Level.SERVICE);
    }

    protected void log(String s, Level level) {
        if (line++ < analyzer.getMaxLines()) {
            try {
                analyzer.analyze(s, source, destination);
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        } else if (! commited) {
            LOG.service(" " + source.getNumber() + " " + source.getChanged());
            if (source.isChanged()) {
                source.commit();
            }
            if (destination.isChanged()) {
                destination.commit();
            }
            commited = true;
        }
    }
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public String toString() {
        return super.toString() + " " + level;
    }
}
