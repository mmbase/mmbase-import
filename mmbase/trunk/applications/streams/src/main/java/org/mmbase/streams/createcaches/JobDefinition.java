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

import java.io.Serializable;
import java.util.*;

import org.mmbase.streams.transcoders.Analyzer;
import org.mmbase.streams.transcoders.Transcoder;
import org.mmbase.util.MimeType;


/**
 * The description or definition of one 'transcoding' sub job that's doing the transcoding. This
 * combines a {@link Transcoder}, with a mime type for which it must be valid, and a list of analyzers.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class JobDefinition implements Serializable {
    private static final long serialVersionUID = 0L;
    final Transcoder transcoder;
    final List<Analyzer> analyzers;
    final MimeType mimeType;

    final String inId;
    final String id;
    final String label;

    final Stage stage;

    /**
     * Creates a JobDefinition template as used in the configuration container or to kick-start
     * a transcoding.
     * @param id    identifier of this job, normally matches the one in configuration
     * @param inId  identifier of the jobdefinition or cache that is used as input.
                    If no inId is found the orginal source is used, otherwise it should match with
                    a preceding jobdefinition id in config or a node number of an already existing cache.
     * @param label description for user purposes of the result
     * @param t     transcoder to perform the job
     * @param mt    mime type for which the jobdefinition is valid
     * @param s     stage the jobdefinition applies to
     */
    public JobDefinition(String id, String inId, String label, Transcoder t, MimeType mt, Stage s) {
        assert id != null;
        transcoder = t.clone();
        analyzers = new ArrayList<Analyzer>();
        mimeType = mt;
        this.id = id;
        this.inId = inId;
        this.label = label;
        this.stage = s;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }

    public void addAnalyzer(Analyzer a) {
        analyzers.add(a);
    }
    public void addAnalyzers(List<Analyzer> l) {
        analyzers.addAll(l);
    }
    public List<Analyzer> getAnalyzers() {
        return Collections.unmodifiableList(analyzers);
    }

    public MimeType getMimeType() {
        return mimeType;
    }
    public Stage getStage() {
        return stage;
    }
    public String getId() {
        return id;
    }
    public String getInId() {
        return inId;
    }
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "" + transcoder + " " + analyzers + (label == null ? "" : (" (" + label + ")"));
    }

}
