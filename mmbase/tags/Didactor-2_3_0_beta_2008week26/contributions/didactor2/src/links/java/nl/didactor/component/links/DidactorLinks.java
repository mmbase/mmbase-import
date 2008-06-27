/**
 * Component description interface.
 */
package nl.didactor.component.links;
import nl.didactor.component.Component;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.core.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.implementation.NodeSearchQuery;

/**
 * Links
 * @author Michiel Meeuwissen
 * @since Didactor-2.3
 */

public class DidactorLinks extends Component {
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
        return "links";
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
        /*
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("urls");
        people.registerPostInsertComponent(this, 10);
        people.registerPreDeleteComponent(this, 10);
        */
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
