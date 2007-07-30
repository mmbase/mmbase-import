/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

/**
 * A Processor handles interaction of a {@link Block}. It is like a {@link Renderer}, but it renders
 * nothing, it only can change the state of the block, and hence influence the renderers.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Processor.java,v 1.9 2007-07-30 16:36:05 michiel Exp $
 * @since MMBase-1.9
 */
public interface Processor {

    /**
     * Every processor processes for a certain block.
     */
    Block getBlock();


    /**
     * A processor may need certain parameters. These are added to the block-parameters. This method
     * is called on instantation of the processor.
     */
    Parameter[] getParameters();

    /**
     * Process. In case of e.g. a JSPProcessor, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void process(Parameters blockParameters, Parameters frameworkParameters) throws FrameworkException;

    /**
     * An URI which may identify the implementation of this Renderer. 
     */

    java.net.URI getUri();

}
