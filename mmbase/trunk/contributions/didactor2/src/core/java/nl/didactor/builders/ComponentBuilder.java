package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import java.util.Iterator;
import java.util.Vector;
import nl.didactor.component.Component;

/**
 * This class provides extra functionality for the People builder. It
 * can encrypt the password of a user, and return a bridge.Node for
 * a given username/password combination
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ComponentBuilder extends AbstractSmartpathBuilder {
    private org.mmbase.util.Encode encoder = null;
    private static Logger log = Logging.getLoggerInstance(ComponentBuilder.class.getName());

    /**
     * Initialize this builder
     */
    public boolean init() {
        super.init();
        NodeSearchQuery query = new NodeSearchQuery(this);
        Vector v = new Vector();

        //register all components
        try {
            Iterator i = getNodes(query).iterator();
            while (i.hasNext()) {
                Component c = registerComponent((MMObjectNode)i.next());
                if (c != null) 
                    v.add(c); 
            }
        } catch (Exception e) {
            log.error(e);
        }

        // Initialize all the components
        for (int i=0; i<v.size(); i++) {
            Component c = (Component)v.get(i);
            c.init();
        }
        return true;
    }

    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);
        //registerComponent(node);
        Component c = registerComponent(node);
        if (c != null) {
            c.init();
        }
        return number;
    }

    private Component registerComponent(MMObjectNode component) {
        String classname = component.getStringValue("classname");
        String componentname = component.getStringValue("name");
        log.info( "Defining " + classname);
       
        if (classname == null || "".equals(classname))
            return null;

        try {
            Class c = Class.forName(classname);
            if (c == null)
                return null;

            Component comp = (Component)c.newInstance();
            if (comp == null)
                return null;
            
            comp.setNode(component);
            Component.register(componentname, comp);
            return comp;
        } catch (ClassNotFoundException e) {
            log.error("Class not found: " + classname);
            return null;
        } catch (Exception e) {
            log.error("Exception while initializing (" + component + "): " + e);
            return null;
        }
    }
}

