package org.mmbase.jumpers;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.datatypes.processors.CommitProcessor;

/**
 * @javadoc
 * @since MMBase-1.9.1
 * @author Sander de Boer
 */
public class StripJsessionidCommitProcessor implements CommitProcessor {

    private static final long serialVersionUID = 1L;

    private static String stripJsessionId(String url) {
        int startPos = url.indexOf(";jsessionid=");
        if ( startPos != -1 ) {
            int endPos = url.indexOf("?",startPos);
            if ( endPos == -1 ) {
                url = url.substring(0,startPos);
            } else {
                url = url.substring(0,startPos) + url.substring(endPos,url.length());
            }
        }
        return url;
    }

    public void commit(Node n, Field f) {
        if (n != null) {
            n.setStringValue("url", stripJsessionId(n.getStringValue("url")));
        }
    }
}
