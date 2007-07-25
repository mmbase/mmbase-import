/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.util.*;
import org.mmbase.util.*;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.datatypes.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.functions.*;
import org.mmbase.util.transformers.Url;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

/**
 * The framework that does nothing, besides adding the block-parameters to the URL. No support for
 * conflicting block parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicFramework.java,v 1.57 2007-07-25 05:08:40 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicFramework implements Framework {
    private static final Logger log = Logging.getLoggerInstance(BasicFramework.class);

    private static final CharTransformer paramEscaper = new Url(Url.ESCAPE);

    public final static String RENDER_ID = "org.mmbase.framework.render_id";

    public static final String XSD_FRAMEWORK = "framework.xsd";
    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/framework";
    static {
        XMLEntityResolver.registerSystemID(NAMESPACE + ".xsd", XSD_FRAMEWORK, BasicFramework.class);
    }


    public static final Parameter<Node>   N         = new Parameter<Node>("n", Node.class);
    public static final Parameter<String> COMPONENT = new Parameter<String>("component", String.class);
    public static final Parameter<String> BLOCK     = new Parameter<String>("block", String.class);
    public static final Parameter<String> CATEGORY  = new Parameter<String>("category", String.class);

    public static final Parameter<Boolean> PROCESS  = new Parameter<Boolean>("process", Boolean.class);

    protected final ChainedUrlConverter urlConverter = new ChainedUrlConverter();

    protected final LocalizedString description      = new LocalizedString("description");


    protected final Map<Setting<?>, Object> settingValues = new HashMap<Setting<?>, Object>();

    public BasicFramework(Element el) {
        configure(el);
    }
    public BasicFramework() {
        urlConverter.add(new BasicUrlConverter());
    }
    

    public StringBuilder getUrl(String path, Collection<Map.Entry<String, Object>> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        return urlConverter.getUrl(path, parameters, frameworkParameters, escapeAmps);
    }
    public StringBuilder getInternalUrl(String page, Collection<Map.Entry<String, Object>> params, Parameters frameworkParameters) {
        log.debug("we're calling urlConverter");
        return urlConverter.getInternalUrl(page, params, frameworkParameters);
    }

    public String getName() {
        return "BASIC";
    }

    

    /**
     * Configures the framework by reading its config file 'config/framework.xml'
     * containing a list with UrlConverters.
     */
    protected void configure(Element el) {
        try {
            description.fillFromXml("description", el);

            NodeList urlconverters = el.getElementsByTagName("urlconverter");
            for (int i = 0; i < urlconverters.getLength(); i++) {
                Element element = (Element) urlconverters.item(i);
                UrlConverter uc;
                try {
                    uc = (UrlConverter) ComponentRepository.getInstance(element, (Framework) this);
                } catch (NoSuchMethodException nsme) {
                    uc = (UrlConverter) ComponentRepository.getInstance(element);
                }
                urlConverter.add(uc);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        BasicUrlConverter buc = new BasicUrlConverter();
        if (! urlConverter.contains(buc)) {
            urlConverter.add(buc);
        }
        log.info("Configured BasicFrameWork: " + this);

    }

    public Block getBlock(Parameters frameworkParameters) {
        Component comp  = ComponentRepository.getInstance().getComponent(frameworkParameters.get(COMPONENT));
        if (comp == null) return null;
        Block block = comp.getBlock(frameworkParameters.get(BLOCK));
        return block;
    }


    public Block getBlock(Component component, String blockName) {
        return component.getBlock(blockName);
    }

    public Parameters createParameters() {
        return new Parameters(Parameter.REQUEST, Parameter.CLOUD, N, COMPONENT, BLOCK, PARAMETER_ACTION, CATEGORY, PROCESS);
    }

    public boolean makeRelativeUrl() {
        return false;
    }


    protected void setBlockParameters(State state, Parameters blockParameters) {
        for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
            if (entry.getValue() == null) {
                blockParameters.set(entry.getKey(), state.getRequest().getParameter(getPrefix(state) + entry.getKey()));
            }
        }
    }

    public void render(Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Writer w, Renderer.WindowState windowState) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (log.isDebugEnabled()) {
            log.info("Rendering " + renderer);
        }
        State state = State.getState(request);
        try {
            request.setAttribute(COMPONENT_CLASS_KEY, "mm_fw_basic");
            if (state.render(renderer, frameworkParameters)) {
                Processor processor = renderer.getBlock().getProcessor();
                log.service("Processing " + renderer.getBlock() + " " + processor);
                request.setAttribute(Processor.KEY, processor);
                renderer.getBlock().getProcessor().process(blockParameters, frameworkParameters);
                request.setAttribute(Processor.KEY, null);
            }
            request.setAttribute(Renderer.KEY, renderer);

            request.setAttribute(COMPONENT_ID_KEY, "mm" + getPrefix(state));
            setBlockParameters(state, blockParameters);

            renderer.render(blockParameters, frameworkParameters, w, windowState);
            
        } finally {
            state.endBlock();
        }
    }

    public void process(Processor processor, Parameters blockParameters, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        for (Map.Entry<String, ?> entry : blockParameters.toMap().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        processor.process(blockParameters, frameworkParameters);
    }

    public Node getUserNode(Parameters frameworkParameters) {
        Cloud cloud = frameworkParameters.get(Parameter.CLOUD);
        return cloud.getCloudContext().getAuthentication().getNode(cloud.getUser());
    }

    public String getUserBuilder() {
        return org.mmbase.module.core.MMBase.getMMBase().getMMBaseCop().getAuthentication().getUserBuilder();
    }

    public Map<String, Object> prefix(State state, Map<String, Object> params) {
        return getMap(state, params);
    }

    protected String getPrefix(final State state) {
        //return "_" + renderer.getBlock().getComponent().getName() + "_" +
        //renderer.getBlock().getName() + "_" + count + "_";
        return "_" + state.getId();
    }
    protected Map<String, Object> getMap(final State state, final Map<String, Object> params) {
        return new AbstractMap<String, Object>() {
            public Set<Map.Entry<String, Object>> entrySet() {
                return new AbstractSet<Map.Entry<String, Object>>() {
                    public int size() { return params.size(); }
                    public Iterator<Map.Entry<String, Object>> iterator() {
                        return new Iterator<Map.Entry<String, Object>>() {
                            private Iterator<Map.Entry<String, Object>> i = params.entrySet().iterator();
                            public boolean hasNext() { return i.hasNext(); };
                            public Map.Entry<String, Object> next() {
                                Map.Entry<String, Object> e = i.next();
                                return new org.mmbase.util.Entry<String, Object>(getPrefix(state) + e.getKey(), e.getValue());
                                }
                            public void remove() { throw new UnsupportedOperationException(); }

                        };
                    }
                };
            }
        };
    }

    public String toString() {
        return getName() + ": " + description + ": " + urlConverter.toString();
    }

    private static final Parameter<Boolean> USE_REQ = new Parameter<Boolean>("usesession", Boolean.class, Boolean.TRUE);
    public Parameters createSettingValueParameters() {
        return new Parameters(Parameter.REQUEST, Parameter.CLOUD, USE_REQ);
    }

    protected String getKey(Setting<?> setting) {
        return "org.mmbase.framework." + setting.getComponent().getName() + "." + setting.getName();
    }

    public <C> C getSettingValue(Setting<C> setting, Parameters parameters) {
        boolean useSession = parameters != null && parameters.get(USE_REQ);
        if (useSession) {
            HttpServletRequest req = parameters.get(Parameter.REQUEST);
            Object v = req.getSession(true).getAttribute(getKey(setting));
            if (v != null) {
                return setting.getDataType().cast(v, null, null);
            }
        }
        if (settingValues.containsKey(setting)) {
            return (C) settingValues.get(setting);
        } else {
            return setting.getDataType().getDefaultValue();
        }
    }

    public <C> C setSettingValue(Setting<C> setting, Parameters parameters, C value) {
        if (parameters == null) throw new SecurityException("You should provide Cloud and request parameters");
        boolean useSession = parameters.get(USE_REQ);
        if (useSession) {
            C ret = getSettingValue(setting, parameters);
            HttpServletRequest req = parameters.get(Parameter.REQUEST);
            req.getSession(true).setAttribute(getKey(setting), value);
            return ret;
        } else {
            
            Cloud cloud = parameters.get(Parameter.CLOUD);
            if (cloud.getUser().getRank() == org.mmbase.security.Rank.ADMIN) {
                return (C) settingValues.put(setting, value);
            } else {
                throw new SecurityException("Permission denied");
            }
        }
    }
}
