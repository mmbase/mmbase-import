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
 * A View is a thing that can actually be rendered, and can be returned by a {@link Component}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Renderer.java,v 1.7 2006-10-31 22:21:45 michiel Exp $
 * @since MMBase-1.9
 */
public interface Renderer {

    public final static String KEY = "org.mmbase.framework.renderer";

    enum Type {
        HEAD, BODY;
        Renderer getEmpty(final Block block) {
            return new Renderer() {
                public Type getType() { return Type.this; }
                public void render(Parameters parameters, Parameters urlparameters, Writer w) { };
                public Parameter[] getParameters() { return Parameter.EMPTY; };
                public Block getBlock() { return block ; };
            };
        }
    }

    Type getType();

    Block getBlock();


    Parameter[] getParameters();

    /**
     * Renders to a writer. In case of e.g. a JSPView, the parameters must also contain
     * the Http Servlet response and request, besided specific parameters for this component.
     */
    void render(Parameters blockParameters, Parameters frameworkParameters, Writer w) throws IOException;
}
