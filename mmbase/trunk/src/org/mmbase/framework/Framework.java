/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import javax.servlet.jsp.PageContext;
import java.util.*;
import java.io.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.Entry;
import org.mmbase.util.functions.Parameters;

/**
 * A framework displays and processes components.
 *
 * @author Johannes Verelst
 * @author Pierre van Rooden
 * @version $Id: Framework.java,v 1.19 2006-12-09 12:57:08 johannes Exp $
 * @since MMBase-1.9
 */
public interface Framework {

    public final static String COMPONENT_ID_KEY    = "componentId";
    public final static String COMPONENT_CLASS_KEY = "componentClassName";

    /**
     * Return the name of the framework 
     */
    public String getName();

    /** 
     * Returns a URL that can be presented to the user (to be put into HTML) to a specific block
     * for a component. The url might be different based on the WindowState of the block.
     *
     * @param block The block to create an URL for, or a page (e.g. image/css) provided by the component
     * @param component The component to use to search the file for
     * @param blockParameters The parameters that were set on the block using referids and sub-&lt;mm:param&gt; tags
     * @param frameworkParameters The parameters that are required by the framework, for instance containing the 'request' and 'cloud'.
     * @param state the window state in which the content should be rendered
     * @param escapeAmps <code>true</code> if parameters should be added with an escaped &amp; (&amp;amp;). 
     *                   You should escape &amp; when a URL is exposed (i.e. in HTML), but not if the url is 
     *                   for some reason called directly.     
     */
    public StringBuilder getBlockUrl(Block block, Component component, Parameters blockParameters, Parameters frameworkParameters, Renderer.WindowState state, boolean escapeAmps);

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
     * @param component The component requesting the modified URL
     * @param urlParameters The parameters to be passed to the page
     * @param frameworkParameters The parameters that are required by the framework
     * @param escapeAmps <code>true</code> if parameters should be added with an escaped &amp; (&amp;amp;). 
     *                   You should escape &amp; when a URL is exposed (i.e. in HTML), but not if the url is 
     *                   for some reason called directly. 
     */
    public StringBuilder getUrl(String path, Component component, Parameters urlParameters, Parameters frameworkParameters, boolean escapeAmps);

    /**
     * Generates an URL to a resource to be called and included by a renderer.
     * Typically, this generates a URL to a jsp, called by a renderer such as the {@link JspRenderer}, 
     * who calls the resource using the RequestDispatcher.
     * This method allows for frameworks to do some filtering on URLs (such as pretty URLs).
     * You should generally not call this method unless you write a Renderer that depends on code or data from external resources.
     *
     * @param path The page (e.g. image/css) provided by the component to create an URL for
     * @param renderer The renderer that is to call the URL
     * @param component The component to use to search the file for
     * @param blockParameters The parameters that were set on the block using referids and sub-&lt;mm:param&gt; tags
     * @param frameworkParameters The parameters that are required by the framework, such as the 'request' and 'cloud' objects
     */
    public StringBuilder getInternalUrl(String path, Renderer renderer, Component component, Parameters blockParameters, Parameters frameworkParameters);

    /**
     * Generates an URL to a resource to be called by a processor.
     * Typically, this generates a URL to a jsp, called by a processor such as the {@link JspProcessor}, 
     * who calls the resource using the RequestDispatcher.
     * This method allows for frameworks to do some filtering on URLs (such as pretty URLs).
     * You should generally not call this method unless you write a Processor that depends on code or data from external resources.
     *
     * @param path The page (e.g. image/css) provided by the component to create an URL for
     * @param processor The processor that is to call this URL
     * @param component The component to use to search the file for
     * @param blockParameters The parameters that were set on the block using referids and sub-&lt;mm:param&gt; tags
     * @param frameworkParameters The parameters that are required by the framework, such as the 'request' and 'cloud' objects
     */
    public StringBuilder getInternalUrl(String path, Processor processor, Component component, Parameters blockParameters, Parameters frameworkParameters);

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
    public Parameters createFrameworkParameters(); 

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
}
