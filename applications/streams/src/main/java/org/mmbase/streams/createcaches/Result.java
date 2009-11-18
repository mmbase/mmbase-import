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



public abstract class Result {
    final JobDefinition definition;
    final URI in;
    boolean ready = false;
    Result(JobDefinition def, URI in) {
        assert in != null;
        definition = def;
        this.in = in;
    }

    JobDefinition getJobDefinition() {
        return definition;
    }

    public abstract Node getDestination();

    //public abstract Node getNode();

    public abstract URI getOut();

    public URI getIn() {
        return in;
    }
    public void ready() {
        ready = true;

    }
    public boolean isReady() {
        return ready;
    }
    public abstract MimeType getMimeType();

    public final Stage getStage() {
        return getJobDefinition().getStage();
    }

}
