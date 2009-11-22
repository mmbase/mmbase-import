/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;

import org.mmbase.bridge.Node;
import org.mmbase.applications.media.State;
import org.mmbase.applications.media.MimeType;

import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;




/**
 * Container for the result of a JobDefinition This is the result of an actual transcoding. 
 * This means that it does have a 'destination' node {@link #getDestination()} and URI {@link #getOut()}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
class TranscoderResult extends Result {
    private static final Logger LOG = Logging.getLoggerInstance(TranscoderResult.class);
    final Node dest;
    final URI out;
    final File directory;

    TranscoderResult(File directory, JobDefinition def, Node dest, URI in, URI out) {
        super(def, in);
        assert out != null;
        this.dest = dest;
        this.out = out;
        LOG.info("Setting " + dest.getNumber() + " to request");
        dest.setIntValue("state",  State.REQUEST.getValue());
        dest.commit();
        this.directory = directory;
        LOG.info("Created Result " + this + " " + definition.transcoder.getClass().getName());
    }

    public Node getDestination() {
        return dest;
    }

    public URI getOut() {
        return out;
    }
    public void ready() {
        super.ready();
        if (dest != null) {
            LOG.service("Setting " + dest.getNumber() + " to done");
            File outFile = new File(directory, dest.getStringValue("url").replace("/", File.separator));
            dest.setLongValue("filesize", outFile.length());
            if (outFile.length() > 1) {     // @TODO: there should maybe be other ways to detect if a transcoding failed
                dest.setIntValue("state", State.DONE.getValue());
            } else {
                LOG.warn("Filesize < 1, setting " + dest.getNumber() + " to failed");
                dest.setIntValue("state", State.FAILED.getValue());
            }
            if (definition.getLabel() != null && dest.getNodeManager().hasField("label")) {
                dest.setStringValue("label", definition.getLabel());
            }
            dest.commit();
        }
    }

    @Override
    public String toString() {
        if (dest != null) {
            return dest.getNumber() + ":" + out;
        } else {
            return "(NO RESULT:" + definition.toString() + ")";
        }

    }
    public MimeType getMimeType() {
        return new MimeType(getDestination().getStringValue("mimetype"));
    }


}
