/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.io.*;
import java.util.*;
import org.mmbase.bridge.Node;
import org.mmbase.util.*;
import org.mmbase.util.xml.Instantiator;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A framework displays and processes components.
 *
 * The Framework as UrlConverter must never return <code>null</code>, iow, it should
 * <em>always</em> know how to do this.
 *
 *
 * @author Johannes Verelst
 * @author Pierre van Rooden
 * @version $Id: Framework.java,v 1.47 2008-02-24 10:46:20 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class Framework {

    private static final Logger log = Logging.getLoggerInstance(Framework.class);

    /**
     * Reference to the Framework singleton.
     * @since MMBase-1.9
     */
    static Framework framework = null;


    public static final String XSD = "framework.xsd";
    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/framework";
    static {
        XMLEntityResolver.registerSystemID(NAMESPACE + ".xsd", XSD, Framework.class);
    }

    /**
     * Return the framework, or null if there is no framework defined in mmbaseroot.xml
     * @return the framework
     */
    public static Framework getInstance() {
        if (framework == null) {
            org.mmbase.util.ResourceWatcher frameworkWatcher = new org.mmbase.util.ResourceWatcher() {
                    public void onChange(String resourceName) {
                        try {
                            ComponentRepository.getInstance();
                            org.w3c.dom.Document fwConfiguration = getResourceLoader().getDocument(resourceName, true, Framework.class);
                            if (fwConfiguration == null)  {
                                framework = new org.mmbase.framework.basic.BasicFramework();
                            } else {
                                org.w3c.dom.Element el = fwConfiguration.getDocumentElement();
                                try {
                                    framework = (Framework) Instantiator.getInstance(el, el);
                                } catch (NoSuchMethodError nsme) {
                                    framework = (Framework) Instantiator.getInstance(el);
                                }
                            }
                            org.w3c.dom.NodeList blockTypes = fwConfiguration.getDocumentElement().getElementsByTagName("blocktype");
                            for (int i = 0; i < blockTypes.getLength(); i++) {
                                org.w3c.dom.Element element = (org.w3c.dom.Element) blockTypes.item(i);
                                String classification = element.getAttribute("name");
                                int weight = Integer.parseInt(element.getAttribute("weight"));
                                for (Block.Type t : Block.Type.getClassification(classification, true)) {
                                    t.setWeight(weight);
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            framework = new org.mmbase.framework.basic.BasicFramework();
                        }
                    }
                };
            frameworkWatcher.add("framework.xml");
            frameworkWatcher.setDelay(10 * 1000); // check every 10 secs if config changed
            frameworkWatcher.start();
            frameworkWatcher.onChange();
        }
        return framework;
    }

    /**
     * CSS-id to be used on block
     */
    public final static String COMPONENT_ID_KEY    = "componentId";

    /**
     * CSS-class to be used on block. T
     */
    public final static String COMPONENT_CLASS_KEY = "componentClassName";


    /**
     * A framework must be able to provide a node to the rendered blocks. This parameter could
     * indicate _which_ node.
     * @todo Not yet suported, so basic framework cannot yet support block which require a framework
     * provided node.
     */
    public static final Parameter<Node>   N         = new Parameter<Node>("n", Node.class);


    /**
     * Return the name of the framework
     */
    public abstract String getName();


    /**
     * Returns the block which is specified by framework parameters.
     */
    public abstract Block getBlock(Parameters frameworkParameters);


    public abstract Block getRenderingBlock(Parameters frameworkParameters);

    /**
     * Return a Parameters object that needs to be passed on to the getUrl() call.
     *
     * Many components will be implemented as servlets, so will not work if the framework does not
     * at least include {@link Parameter.REQUEST} and {@link Parameter.RESPONSE}. So it is
     * recommended that those parameters are supported by the framework.
     *
     *
     *
     * The MMBase taglib component tag will e.g. auto-fill those parameters. Other parameters can be
     * added using 'mm:frameworkparameter'
     *
     * A framework may create a different or expanded list of parameters, but is responsible for filling them properly.
     * If the framework does not use the MMBase taglib for rendering of components, it needs to provide it's own mechanism to
     * fill the above parameters with default values (such as through a servlet or portlet).
     */
    public abstract Parameters createParameters();

    /**
     * Render content (such as HTML or XML) using a Renderer obtained from a component's block.
     * The framework decides on a (extra) class for the div which is to be rendered, which is put on
     * the request as {@link #COMPONENT_CLASS_KEY}.
     *
     * @param renderer the Renderer used to produce the content. This parameter is obtained using {@link Block#getRenderer()}
     * @param blockParameters The parameters specific for the call of this renderer's block
     * @param frameworkParameters The parameters that are required by the framework, such as the 'request' and 'cloud' objects
     * @param w The writer where the code generated by the renderer is to be written (such as the jspWriter)
     * @param state the window state in which the content should be rendered
     * @throws FrameworkException when the renderer failed to create content or could not write data to the writer
     */
    public abstract void render(Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Writer w, Renderer.WindowState state) throws FrameworkException;

    /**
     * Processes a block. This method can change or se state information and should be called prior to rendering a component's block.
     * A process does not generate content.
     *
     * @param processor the Processor used to produce the content. This parameter is obtained using {@link Block#getProcessor()}
     * @param blockParameters The parameters specific for the call of this renderer's block.
     * @param frameworkParameters The parameters that are required by the framework, such as the 'request' and 'cloud' objects.
     * @throws FrameworkException when the process failed to run
     */
    public abstract void process(Processor processor, Parameters blockParameters, Parameters frameworkParameters) throws FrameworkException;

    /**
     * Return an MMBase Node for the user currently using the framework. It is recommended that this
     * is implemented as is done in {@link BasicFramework}, so based on MMBase security only, and
     * using a {@link Parameter.CLOUD} as a framework parameter. It can be implemented differently,
     * if the framework chooses not to use MMBase security to distinguish between users.
     */
    public abstract Node getUserNode(Parameters frameworkParameters);
;

    /**
     * Return the builder name that is used to store users. This will return the name of the nodemanager that returns
     * the nodes from the getUserNode() method.
     * @TODO What if the framework wants to return virtual nodes?
     * @throws UnsupportedOperationException
     */
    public abstract String getUserBuilder();


    /**
     * @see #getSettingValue(Setting, Parameters)
     * @see #setSettingValue(Setting, Parameters, Object)
     */
    public abstract Parameters createSettingValueParameters();


    /**
     * Retrieves the value as configured by this framework for a certain {@link Setting} (which is
     * always associated with a certain {@link Component}.
     *
     * The framework can (and should) return the default values of the Setting if it does not know
     * what to do. It can also adminstrate overridden values, e.g. in its own configuration file.

     * Using the 'parameters' (created with {@link #createSettingValueParameters}, the Framework can also
     * implement context specific values for a setting. It can e.g. use a request object, and store
     * user specific value as cookies.
     */
    public abstract <C> C getSettingValue(Setting<C> setting, Parameters parameters);

    /**
     * See {@link #getSettingValue}. Depending on the framework, the set value may not necessarily be persistant.
     *
     * @throws SecurityException If you are not allowed to change the setting.
     */
    public abstract <C> C setSettingValue(Setting<C> setting, Parameters parameters, C value) throws SecurityException;



    public abstract Parameter[] getParameterDefinition();

    /**
     * Return a (possibly modified) URL for a given path.
     * This method is called (for example) from within the mm:url tag, and can be exposed to the outside world.
     * I.e. when within a components's head you use<br />
     * &lt;mm:url page="/css/style.css" /&gt;, <br />
     * this method is called to determine the proper url (i.e., relative to the framework or component base).
     * If you need treefile/leaffile type of functionality in your framework, you can implement that
     * here in your code.
     *
     * @param path The path (generally a relative URL) to create an URL for.
     * @param parameters Parameters The parameters to be passed to the page
     * @param frameworkParameters The parameters that are required by the framework
     * @param escapeAmps <code>true</code> if parameters should be added with an escaped &amp; (&amp;amp;).
     *                   You should escape &amp; when a URL is exposed (i.e. in HTML), but not if the url is
     *                   for some reason called directly.
     * @return An URL relative to the root of this web application (i.e. withouth a context path),
     */
    public abstract String getUrl(String path,
                                  Map<String, Object> parameters,
                                  Parameters frameworkParameters,
                                  boolean escapeAmps) throws FrameworkException;

    public abstract String getProcessUrl(String path,
                                         Map<String, Object> parameters,
                                         Parameters frameworkParameters,
                                         boolean escapeAmps) throws FrameworkException;


    /**
     * Generates an URL to a resource to be called and included by a renderer.
     * Typically, this generates a URL to a jsp, called by a renderer such as the {@link JspRenderer},
     * who calls the resource using the RequestDispatcher.
     * This method allows for frameworks to do some filtering on URLs (such as pretty URLs).
     * You should generally not call this method unless you write a Renderer that depends on code or
     * data from external resources.
     * @param path The page (e.g. image/css) provided by the component to create an URL for
     * @param params Extra parameters for that path
     * @param frameworkParameters The parameters that are required by the framework, such as the
     *                            'request' and 'cloud' objects
     * @return A valid interal URL, or <code>null</code> if nothing framework specific could be
     *         determined (this would make it possible to 'chain' frameworks).
     */
    public abstract String getInternalUrl(String path,
                                          Map<String, Object> params,
                                          Parameters frameworkParameters) throws FrameworkException;


}
