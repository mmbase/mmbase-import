/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.functions.*;
import org.mmbase.util.GenericResponseWrapper;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Renderer implmentation based on a jsp.
 *
 * @author Michiel Meeuwissen
 * @version $Id: EditwizardRenderer.java,v 1.2 2006-10-19 13:37:09 michiel Exp $
 * @since MMBase-1.9
 */
public class EditwizardRenderer extends AbstractRenderer {
    private static final Logger log = Logging.getLoggerInstance(EditwizardRenderer.class);

    protected String list;
    protected String path;

    public EditwizardRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void setList(String l) {
        list = l;
    }
    public void setNodepath(String p) {
        path = p;
    }

    public void render(Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException {
        w.write("/mmbase/edit/wizard/jsp/list.jsp?wizard=" + list + "&nodepath=" + path);
    }
}
