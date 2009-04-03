/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.didactor.filter;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.framework.*;
import org.mmbase.framework.basic.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**

 *
 * @author Michiel Meeuwissen
 * @version $Id: DidactorUrlConverter.java,v 1.14 2009-01-16 23:22:14 michiel Exp $
 */
public class DidactorUrlConverter extends DirectoryUrlConverter {
    private static final Logger log = Logging.getLoggerInstance(DidactorUrlConverter.class);

    public DidactorUrlConverter(BasicFramework fw) {
        super(fw);
        setDirectory("c");
    }

    @Override public int getDefaultWeight() {
        int q = super.getDefaultWeight();
        return Math.max(q, q + 10000);
    }


    @Override protected void getNiceDirectoryUrl(StringBuilder b,
                                                   Block block,
                                                   Parameters blockParameters,
                                                   Parameters frameworkParameters,
                                                   boolean action) throws FrameworkException {
        log.debug("Found block " + block);
        //Node n = (Node) parameters.get(Framework.N.getName());
        b.append(block.getComponent().getName());
        b.append('/');
        if (! block.getComponent().getDefaultBlock().equals(block)) {
            b.append(block.getName());
            b.append('/');
        }
    }


    @Override protected Url getFilteredInternalDirectoryUrl(List<String> path, Map<String, ?> blockParameters, Parameters frameworkParameters) throws FrameworkException {
        StringBuilder result = new StringBuilder("/shared/render.jspx");
        // article mode
        if (path.size() == 0) {
            log.debug("No component in path");
            return Url.NOT;
        }
        Component component = ComponentRepository.getInstance().getComponent(path.get(0));
        if (component == null) {
            log.debug("No didactor component in " + path);
            return Url.NOT;
        }
        Setting<String> setting = (Setting<String>) component.getSetting("didactor_nodeprovider");
        String value = "education";
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);

        if (setting != null) {
            Framework fw = Framework.getInstance();
            Parameters ps = fw.createSettingValueParameters();
            ps.setIfDefined(Parameter.REQUEST, request);
            value = fw.getSettingValue(setting, ps);
        }
        log.debug("Putting on request " + value + " -> " + request.getAttribute(value));
        request.setAttribute("node", request.getAttribute(value));

        result.append("?name=");
        result.append(component.getName());
        Block block = path.size() > 1 ? component.getBlock(path.get(1)) : component.getDefaultBlock();
        if (block == null) return Url.NOT;
        result.append("&block=");
        result.append(block.getName());
        return new BasicUrl(this, result.toString());
    }




}
