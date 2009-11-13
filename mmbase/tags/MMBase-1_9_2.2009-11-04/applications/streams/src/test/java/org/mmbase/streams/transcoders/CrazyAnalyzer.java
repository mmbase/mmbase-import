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
import org.mmbase.util.ThreadPools;
import org.mmbase.util.logging.*;
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;


/**
 * This analyzer as a crazyman changes the type of the nodes. This is just for testing.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MockAnalyzer.java 38480 2009-09-07 16:19:19Z michiel $
 */

public class CrazyAnalyzer extends MockAnalyzer {


    int busy = 0;
    int count = 0;

    @Override
    public void analyze(String l, final Node source, final Node des) {
        busy++;
        ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    count++;
                    if (count % 2 == 0  ) {
                        System.out.print("V");
                        util.toVideo(source, des);
                    } else {
                        System.out.print("A");
                        util.toAudio(source, des);
                    }
                    synchronized(CrazyAnalyzer.this) {
                        busy--;
                        CrazyAnalyzer.this.notifyAll();
                    }

                }
            });
    }

    @Override
    public void ready(Node sourceNode, Node destNode) {
        synchronized(this) {
            while (busy > 0) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
        super.ready(sourceNode, destNode);
        assert sourceNode.getCloud().hasNode(sourceNode.getNumber());
    }
}
