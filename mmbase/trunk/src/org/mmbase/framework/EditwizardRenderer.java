/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import javax.servlet.http.*;
import java.io.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Currently this generates a link to an editwizard list page.
 *
 * @todo Needs to produce a div, not an url.
 *
 * @author Michiel Meeuwissen
 * @version $Id: EditwizardRenderer.java,v 1.16 2009-01-12 21:11:46 michiel Exp $
 * @since MMBase-1.9
 */
public class EditwizardRenderer extends IFrameRenderer {
    private static final Logger log = Logging.getLoggerInstance(EditwizardRenderer.class);

    protected String list;
    protected String wizard;
    public EditwizardRenderer(String t, Block parent) {
        super(t, parent);
    }

    public void setList(String l) {
        list = l;
    }
    public void setWizard(String w) {
        wizard = w;
    }

    /**
     */
    @Override public String getIFrameUrl(Parameters blockParameters)  {

        if (list != null && wizard != null) throw new IllegalStateException();

        Locale  locale = blockParameters.get(Parameter.LOCALE);
        String templates = JspRenderer.JSP_ROOT + getBlock().getComponent().getName();
        Map<String, Object> props = new TreeMap<String, Object>(properties);

        props.put("wizard", list != null ? list : wizard);
        props.put("language", locale.getLanguage());
        HttpServletRequest request   = blockParameters.get(Parameter.REQUEST);

        String url = list != null ?
            "/mmbase/edit/wizard/jsp/list.jsp" :
            "/mmbase/edit/wizard/jsp/wizard.jsp";

        return org.mmbase.framework.basic.BasicUrlConverter.getUrl(url, props, request, true);
    }
    public String toString() {
        return "EW " + (list != null ? ("list.jsp?wizard=" + list) : ("wizard.jsp?wizard=" + wizard)) + "&" + properties;
    }



}
