/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;

import org.mmbase.streams.transcoders.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.storage.search.*;
import org.mmbase.security.UserContext;
import org.mmbase.security.ActionRepository;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.datatypes.processors.*;
import org.mmbase.applications.media.State;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.MimeType;
import org.mmbase.servlet.FileServlet;
import org.mmbase.core.event.*;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.*;



/**
 * The description or definition of one 'transcoding' sub job that's doing the transcoding. This
 * combines a transcoder, with a mime type for which it must be valid, and a list of analyzers.
 */
class JobDefinition implements Serializable {
    private static final long serialVersionUID = 0L;
    final Transcoder transcoder;
    final List<Analyzer> analyzers;
    final MimeType mimeType;

    final String inId;
    final String id;
    final String label;

    final Stage stage;

    /**
     * Creates an JobDefinition template (used in the configuration container).
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
        return "" + transcoder + " " + analyzers + "(" + label + ")";
    }
}
