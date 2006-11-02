/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.io.*;
import org.mmbase.util.functions.*;

/**
 * A Renderer renders a certain aspect of a {@link Block}. Currently every block has two renderers,
 * which are identified by the renderer 'type' (see {@link #getType }). Every block also has a
 * {@link Processor}, which is similar to a Renderer, but a processor never generates contents, only
 * handles interaction.
 * @author Michiel Meeuwissen
 * @version $Id: Renderer.java,v 1.9 2006-11-02 10:49:56 michiel Exp $
 * @since MMBase-1.9
 */
public interface Renderer {

    public final static String KEY = "org.mmbase.framework.renderer";

    enum Type {
        /**
         * Rendering for 'HEAD' typically happens in the &lt;head&gt; block of HTML, and can
         * e.g. produces links to javascript.
         */
        HEAD,
       /**
        * A body typed renderer renders the actual content of a block. It should produce a &lt;div&gt;
        */
        BODY;

        /**
         * Returns a renderer that does nothing.
         */
        Renderer getEmpty(final Block block) {
            return new Renderer() {
                public Type getType() { return Type.this; }
                public void render(Parameters parameters, Parameters urlparameters, Writer w) { };
                public Parameter[] getParameters() { return Parameter.EMPTY; };
                public Block getBlock() { return block ; };
            };
        }
    }

    /**
     * Describes what kind of renderer this is
     */
    Type getType();

    /**
     * Every renderer renders for a certain block.
     */
    Block getBlock();

    /**
     * A renderer may need certain parameters. These are added to the block-parameters. This method
     * is called on instantation of the renderer.
     */
    Parameter[] getParameters();

    /**
     * Renders to a writer. In case of e.g. a JSPView, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void render(Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException;
}
