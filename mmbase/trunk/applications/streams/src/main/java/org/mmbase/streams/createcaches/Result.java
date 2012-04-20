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

package org.mmbase.streams.createcaches;

import java.net.URI;

import org.mmbase.bridge.Node;
import org.mmbase.util.MimeType;


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
     * To what {@link Stage} of the transcoding process this result belongs. Either {@link Stage#RECOGNIZER} or {@link Stage#TRANSCODER}.
     */
    public final Stage getStage() {
        return getJobDefinition().getStage();
    }

}
