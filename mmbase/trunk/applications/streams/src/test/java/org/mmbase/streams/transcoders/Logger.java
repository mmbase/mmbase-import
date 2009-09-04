package org.mmbase.streams.transcoders;

import java.util.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.util.logging.Level;
import org.mmbase.util.logging.AbstractSimpleImpl;

/**
 * Low level tests of Regexps and stuff.
 * @author Michiel Meeuwissen
 */

public abstract class Logger  extends AbstractSimpleImpl {

    static Node getMockNode() {
        Map<String, Object> mock = new HashMap<String, Object>();
        mock.put("mimetype", null);
        mock.put("length", null);
        mock.put("bitrate", null);
        return new MapNode(mock);
    }

    public boolean success = false;


    protected Node source      = getMockNode();
    protected Node destination = getMockNode();


    protected final AnalyzerUtils util = new AnalyzerUtils();

    public Logger() {
        setLevel(Level.DEBUG);
    }


    protected abstract void log(String s);

    @Override
    protected void log(String s, Level level) {
        log(s);
    }




}


