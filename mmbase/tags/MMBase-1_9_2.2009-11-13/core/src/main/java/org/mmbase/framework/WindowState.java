/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

/**
 * Every block can be in a certain window state, which could be considered during rendering.
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public enum WindowState {


    /**
     * Rendering may suppose a full browser window
     */
    MAXIMIZED,

    /**
     * Rendering should suppose only a 'link' version from the component.
     */
     MINIMIZED,
     /**
      * Rendering may suppose quite a large area, but should be aware that other blocks are in a
      * similar state.
      */
     NORMAL;
}

