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

class SkippedResult extends Result {
    SkippedResult(JobDefinition def, URI in) {
        super(def, in);
    }
    public Node getDestination() {
        return null;
    }
    public URI getOut() {
        return null;
    }
    public MimeType getMimeType() {
        return null;
    }
    public boolean isReady() {
        return true;
    }
}
