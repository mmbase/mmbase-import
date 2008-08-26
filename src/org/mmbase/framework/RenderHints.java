/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;
import java.io.*;
import java.net.URI;
import org.mmbase.util.functions.*;

/**
 * A Renderer is used ({@link Renderer#render}), with a RenderHints object, which gives to Renderer
 * implementation some instructions about how to render excactly, since it may vary a bit in that.
 *
 * @author Michiel Meeuwissen
 * @version $Id: RenderHints.java,v 1.1 2008-08-26 06:45:36 michiel Exp $
 * @since MMBase-1.9
 */
public class RenderHints {

    private final WindowState state;
    private final String id;
    private final String clazz;
    private final Renderer renderer;

    public RenderHints(Renderer renderer, WindowState state, String id, String clazz) {
        this.renderer = renderer;
        this.state = state;
        this.id = id;
        this.clazz = clazz;
    }


    public Renderer getRenderer() {
        return renderer;
    }
    public WindowState getWindowState() {
        return state;
    }

    public String getId() {
        return id;
    }
    public String getStyleClass() {
        return clazz;
    }

}
