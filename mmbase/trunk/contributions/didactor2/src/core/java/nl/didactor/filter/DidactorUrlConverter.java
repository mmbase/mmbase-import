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
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**

 *
 * @author Michiel Meeuwissen
 * @version $Id: DidactorUrlConverter.java,v 1.3 2008-08-08 13:42:06 michiel Exp $
 */
public class DidactorUrlConverter implements org.mmbase.framework.basic.UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(DidactorUrlConverter.class);

    private String  directory = "/c";
    private final Framework framework;

    public DidactorUrlConverter(Framework fw) {
        framework = fw;
    }


    public Parameter[] getParameterDefinition() {
        return new Parameter[] {};
    }

    protected String getUrl(String path,
                             Map<String, Object> parameters,
                             Parameters frameworkParameters, boolean escapeAmps, boolean action) {
        if (log.isDebugEnabled()) {
            log.debug("" + path + parameters + frameworkParameters);
        }
        Block renderingBlock = framework.getRenderingBlock(frameworkParameters);
        if (renderingBlock == null) {
            log.debug("No current block found for parameters " + frameworkParameters);
            return null;
        } else {
            HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);

            Block block = framework.getBlock(frameworkParameters);
            if (block == null) {
                block = renderingBlock.getComponent().getBlock(path);
            }
            if (block != null) {
                log.debug("Found block " + block);
                //Node n = (Node) parameters.get(Framework.N.getName());
                StringBuilder b = new StringBuilder(directory);
                b.append('/');
                b.append(block.getComponent().getName());
                b.append('/');
                if (! block.getComponent().getDefaultBlock().equals(block)) {
                    b.append(block.getName());
                    b.append('/');
                }
                return b.toString();
            } else {
                log.debug("No block found");
                return null;
            }
        }
    }

    public String getUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, false);
    }
    public String getProcessUrl(String path,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, true);
    }

    public String getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith(directory + '/')) {
            log.debug("Found a didactor url");
            String sp = FrameworkFilter.getPath(request);
            String[] path = sp.split("/");
            if (log.isDebugEnabled()) {
                log.debug("Going to filter " + Arrays.asList(path));
            }
            if (path.length >= 3) {
                StringBuilder result = new StringBuilder("/shared/render.jspx");
                assert path[0].equals("");
                assert path[1].equals(directory.substring(1));
                // article mode
                Component component = ComponentRepository.getInstance().getComponent(path[2]);
                Setting<String> setting = (Setting<String>) component.getSetting("didactor_nodeprovider");
                String value = "education";
                if (setting != null) {
                    Framework fw = Framework.getInstance();
                    Parameters ps = fw.createSettingValueParameters();
                    ps.setIfDefined(Parameter.REQUEST, request);
                    value = fw.getSettingValue(setting, ps);
                }
                log.debug("Putting on request " + value + " -> " + request.getAttribute(value));
                request.setAttribute("node", request.getAttribute(value));
                request.setAttribute(Framework.COMPONENT_INCLUDEPATH_KEY, request.getAttribute("includePath"));

                result.append("?name=");
                result.append(component.getName());
                Block block = path.length > 3 ? component.getBlock(path[3]) : component.getDefaultBlock();
                if (block == null) return null;
                result.append("&block=");
                result.append(block.getName());
                return result.toString();
            } else {
                log.debug("path length " + path.length);
                return null;
            }
        } else {
            log.debug("Leaving unfiltered");
            return null;
        }
    }

    public String toString() {
        return directory;
    }


}
