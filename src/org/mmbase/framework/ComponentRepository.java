/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.util.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The class maintains all compoments which are registered in the current MMBase.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentRepository.java,v 1.1 2006-08-30 19:18:09 michiel Exp $
 * @since MMBase-1.9
 */
public class ComponentRepository {
    private static final Logger log = Logging.getLoggerInstance(ComponentRepository.class);

    private  static final ComponentRepository repository = new ComponentRepository();
    public static ComponentRepository getInstance() {
        return repository;
    }

    private Map<String, Component> rep = new HashMap<String, Component>();

    private ComponentRepository() {
        readConfiguration();
    }

    public Collection<Component> getComponents() {
        return Collections.unmodifiableCollection(rep.values());
    }

    public Component getComponent(String name) {
        return rep.get(name);
    }

    public void shutdown() {
        rep.clear();
    }

    protected void readConfiguration() {
        ResourceLoader loader =  ResourceLoader.getConfigurationRoot().getChildResourceLoader("components");
        Collection<String> components = loader.getResourcePaths(ResourceLoader.XML_PATTERN, true /* recursive*/);
        log.info("In " + loader + " the following components XML's were found " + components);
        for (String file : components) {
            String fileName = ResourceLoader.getName(file);
            rep.put(file, null);

        }

    }

}
