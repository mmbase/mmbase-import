/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;


/**
 * The goal of implementations of this interface is to be wrapped by a {@link AnalyzerLogger} (which
 * itself probably is an entry in a {@link ChainedLogger}). It can use these log lines to store
 * information about a {@link Transcoder} process, which appears in its' logging, in the 2 involved nodes.
 *
 * @author Michiel Meeuwissen
 */

public interface Analyzer extends org.mmbase.util.PublicCloneable<Analyzer> {


    void addThrowable(Throwable t);

    /**
     * How many lines of logging should be offered for {@link $analyze}ing at the most.
     */
    int  getMaxLines();


    void addLogger(Logger l);

    /**
     * Analyzes a line of logging of
     * @param l This line
     * @param source The node representing the original mediasource.
     * @param dest   The node representing the mediasource to which the transcoding result is being
     * written.
     */
    void analyze(String l, Node source, Node dest);

    void ready(Node source, Node dest);

}
