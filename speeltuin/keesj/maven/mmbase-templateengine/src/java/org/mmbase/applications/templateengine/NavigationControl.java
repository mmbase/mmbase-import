/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;

/**
 * @author Kees Jongenburger
 */
public abstract class NavigationControl implements NavigationResolver {
    /**
     * String PATH_SEPARATOR ="/";
     */
    public final static String PATH_SEPARATOR = "/";

    /** 
     * @return the root navigation with childs
     * the root navigation is always skipped
     */
    public abstract Navigation getNavigation();

    /** 
     * @param path navigation path separated by  PATH_SEPARATOR tokens
     * <b>examples:</b><br>
     * <ul>
     *     <li>programmas/nieuws</li>
     *     <li>programmas/tv/ram</li>
     *     <li>programma/radio/ochtenden</li>
     * </ul>
     * @return the navigation of null if not found
     */
    public Navigation resolveNavigation(Path path) {
        //get the root navigation
        Navigation rootNavigation = getNavigation();
        return rootNavigation.resolveNavigation(path);
    }

    public String getURLString(Navigation navigation) {
        StringBuffer sb = new StringBuffer();
        if (navigation.isVisible()) {
            sb.append(navigation.getURLString());
        }
        while (!navigation.isRootNavigation()) {
            navigation = navigation.getParentNavigation();
            if (navigation.isVisible()) {
                sb.insert(0, PATH_SEPARATOR);
                sb.insert(0, navigation.getURLString());
            }
        }
        return sb.toString();
    }

    public abstract Template getTemplate(Navigation navigation);
    
    public abstract String resoveURL(Navigation currentNavigation,List params);
    
}
