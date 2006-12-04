/**
 * Component description interface.
 */
package nl.didactor.component.portal;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.module.core.*;

/**
 * Portal
 * @author Michiel Meeuwissen
 * @since Didactor-2.3
 */

public class DidactorPortal extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "1.0";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "portal";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[1];
        components[0] = new DidactorCore();
        return components;
    }

    public void init() {
        super.init();
        MMBase mmbase = MMBase.getMMBase();
    }

    public void install() {
        MMBase mmbase = MMBase.getMMBase();
    }


    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        return true;
    }

    /**
     * This method is called just before an object is removed from MMBase.
     */
    public boolean preDelete(MMObjectNode node) {

        return true;
    }

}
