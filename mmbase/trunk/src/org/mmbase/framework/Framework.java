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
import org.mmbase.util.functions.*;

/**
 * A framework displays and processes components.
 *
 * @author Johannes Verelst
 * @author Pierre van Rooden
 * @version $Id: Framework.java,v 1.34 2007-07-26 21:03:11 michiel Exp $
 * @since MMBase-1.9
 */
public interface Framework extends UrlConverter { 

    public static final Parameter<Integer> PARAMETER_ACTION   = new Parameter<Integer>("action", Integer.class);

    /**
     * CSS-id to be used on block
     */
    public final static String COMPONENT_ID_KEY    = "componentId";
    /**
     * CSS-class to be used on block
     */
    public final static String COMPONENT_CLASS_KEY = "componentClassName";

    /**
     * Return the name of the framework 
     */
    public String getName();


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
     * @param parameters The parameters to be passed to the page
     * @param frameworkParameters The parameters that are required by the framework
     * @param escapeAmps <code>true</code> if parameters should be added with an escaped &amp; (&amp;amp;). 
     *                   You should escape &amp; when a URL is exposed (i.e. in HTML), but not if the url is 
     *                   for some reason called directly. 
     * @return An URL relative to the root of this web application (i.e. withouth a context path)
     */
    public StringBuilder getUrl(String path, 
                                Collection<Map.Entry<String, Object>> parameters,
                                Parameters frameworkParameters, boolean escapeAmps);

    
    /**
     * Returns the current block, according to the framework.
     */
    public Block getBlock(Parameters frameworkParameters);

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
    public StringBuilder getInternalUrl(String path, 
                                        Collection<Map.Entry<String, Object>> params, 
                                        Parameters frameworkParameters);


    /**
     * Return a Parameters object that needs to be passed on to the getUrl() call. 
     * The MMBase taglib component tag auto-fills the following parameters:
     * <ul>
     *  <li>Parameter.CLOUD</li>
     *  <li>Parameter.REQUEST</li>
     *  <li>Parameter.RESPONSE</li>
     * </ul>
     * It is recommended that a framework at least contains the above parameters, as these are often used by MMBase components.
     * A framework may create a different or expanded list of parameters, but is responsible for filling them properly.
     * If the framework does not use the MMBase taglib for rendering of components, it needs to provide it's own mechanism to 
     * fill the above parameters with default values (such as through a servlet or portlet).
     */
    public Parameters createParameters(); 

    /**
     * Render content (such as HTML or XML) using a Renderer obtained from a component's block.
     *
     * @param renderer the Renderer used to produce the content. This parameter is obtained using {@link Block#getRenderer()}
     * @param blockParameters The parameters specific for the call of this renderer's block
     * @param frameworkParameters The parameters that are required by the framework, such as the 'request' and 'cloud' objects     
     * @param w The writer where the code generated by the renderer is to be written (such as the jspWriter)
     * @param state the window state in which the content should be rendered
     * @throws FrameworkException when the renderer failed to create content or could not write data to the writer
     */
    public void render(Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Writer w, Renderer.WindowState state) throws FrameworkException;
    
    /**
     * Processes a block. This method can change or se state information and should be called prior to rendering a component's block.
     * A process does not generate content.
     *
     * @param processor the Processor used to produce the content. This parameter is obtained using {@link Block#getProcessor()}
     * @param blockParameters The parameters specific for the call of this renderer's block.
     * @param frameworkParameters The parameters that are required by the framework, such as the 'request' and 'cloud' objects.     
     * @throws FrameworkException when the process failed to run
     */
    public void process(Processor processor, Parameters blockParameters, Parameters frameworkParameters) throws FrameworkException;

    /**
     * Return the MMBase Node for the given user id. This will use the underlying security implementation by default, but a
     * framework can override this, for instance if it chooses not to use MMBase security to distinguish between users.
     */
    public Node getUserNode(Parameters frameworkParameters);

    /**
     * Return the builder name that is used to store users. This will return the name of the nodemanager that returns
     * the nodes from the getUserNode() method.
     */
    public String getUserBuilder();

    
    /** 
     * Prepares a map of parameters to add to URL
     */
    public Map<String, Object> prefix(State state, Map<String, Object> params);


    /**
     * @see #getSettingValue(Setting, Parameters)
     * @see #setSettingValue(Setting, Parameters, Object)
     */
    public Parameters createSettingValueParameters(); 

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
    public <C> C getSettingValue(Setting<C> setting, Parameters parameters);

    /**
     * See {@link #getSettingValue}. Depending on the framework, the set value may not necessarily be persistant.
     *
     * @throws SecurityException If you are not allowed to change the setting.
     */
    public <C> C setSettingValue(Setting<C> setting, Parameters parameters, C value) throws SecurityException;


}
