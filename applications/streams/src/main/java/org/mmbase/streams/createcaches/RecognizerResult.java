/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;

import org.mmbase.bridge.Node;
import org.mmbase.applications.media.MimeType;

import java.net.*;


/**
 * Result of a recognizer JobDefinition, just recognizes the type of stream etc.
 * Does not transcode. The result out is the same as in, same for mimetype.
 */
class RecognizerResult extends Result {
    final Node source;
    RecognizerResult(JobDefinition def, Node source, URI in) {
        super(def, in);
        this.source = source;
    }
    public Node getSource() {
        return source;
    }
    public Node getDestination() {
        return null;
    }
    public URI getOut() {
        return getIn();
    }
    public MimeType getMimeType() {
        return new MimeType(getSource().getStringValue("mimetype"));
    }
    @Override
    public void ready() {
        super.ready();
        if (definition.getLabel() != null && source.getNodeManager().hasField("label")) {
            source.setStringValue("label", definition.getLabel());
        }

    }
}
