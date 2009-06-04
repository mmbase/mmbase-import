/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.urlcomposers;

import org.mmbase.module.core.*;
import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.util.logging.*;
import org.mmbase.applications.media.Format;
import java.util.*;


/**
 * Provides the functionality to create URL's (or URI's) for a certain
 * fragment, source, provider combination.
 *
 * Depends on mediafragment.ram.jsp and mediafragment.asf.jsp in the
 * templates dir. These can be mapped to something else in
 * web.xml. The servlet name must be media-asf and media-rm then.
 *
 * @author Michiel Meeuwissen
 * @author Rob Vermeulen (VPRO)
 * @since MMBase-1.7
 * @see   Config
 */
public class RamURLComposer extends FragmentURLComposer { // also for wmp/asx
    private static final Logger log = Logging.getLoggerInstance(RamURLComposer.class);

    protected  Format          format;

    @Override
    public void init(MMObjectNode provider, MMObjectNode source, MMObjectNode fragment, Map<String, Object> info, Set<MMObjectNode> cacheExpireObjects) {
        super.init(provider, source, fragment, info, cacheExpireObjects);
        format = Format.get(source.getIntValue("format"));
    }

    @Override
    protected StringBuffer  getURLBuffer() {
        List<String> servlets = MMBaseServlet.getServletMappings("media-" + format);
        String servlet;
        if (servlets == null || servlets.size() == 0) {
            log.error("No mapping found to media-" + format + " servlet. Change this in your web.xml");
            servlet = Config.templatesDir + "mediafragment." + format + ".jsp";
        } else {
            String root = MMBaseContext.getHtmlRootUrlPath();
            root = root.substring(0, root.length() - 1);
            servlet = root  + servlets.get(0);
        }

        return new StringBuffer("http://" + Config.host + servlet + "?fragment=" + (fragment == null ? "" : "" + fragment.getNumber()) + "&source=" + (source == null ? "" : "" + source.getNumber()));

    }
    @Override
    public Format  getFormat()   {
        if (format == Format.RM)  return Format.RAM;
        if (format == Format.ASF) return Format.WMP;
        return format;
    }

}
