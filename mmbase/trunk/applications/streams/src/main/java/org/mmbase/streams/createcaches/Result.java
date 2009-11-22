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
 * When executing an actual {@link JobDefinition} the result is contained in an object like this.
 * @author Michiel Meeuwissen
 * @version $Id$
 */
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

    /**
     * The Node receiving the result or <code>null</code> if that is not applicable (for recognizers).
     */
    public abstract Node getDestination();

    //public abstract Node getNode();

    /**
     * The file receiving the result, or <code>null</code> if that is not applicable
     */
    public abstract URI getOut();

    /**
     * The file containing the input for the job.
     */
    public URI getIn() {
        return in;
    }

    /**
     * Marks this result as ready, meaning that there is nothing left to be done and {@link #isReady} will return true from now on.
     * Also, extensions may override this with extra functionality which can only be done if transcoding is ready.
     */
    public void ready() {
        ready = true;

    }
    public boolean isReady() {
        return ready;
    }

    /**
     * On what kind of inputs this result can work.
     */
    public abstract MimeType getMimeType();

    /**
     * To what {@link Stage} of the transcoding process this result belongs. Either {@link Stage.RECOGNIZER} or {@link Stage.TRANSCODER}.
     */
    public final Stage getStage() {
        return getJobDefinition().getStage();
    }

}
