/*

This file is part of the MMBase Streams application,
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

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
    int errors = 0;

    @Override
    public void analyze(String l, final Node source, final Node des) {
        busy++;
        ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    count++;
                    try {
                        if (count % 2 == 0  ) {
                            System.out.print("V");
                            util.toVideo(source, des);
                        } else {
                            System.out.print("A");
                            util.toAudio(source, des);
                        }
                    } catch (Throwable t) {
                        errors++;
                        System.out.println(t);
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
        assert errors == 0;
    }

    @Override
    public String toString() {
        return "CRAZYANALYZER";
    }
}
