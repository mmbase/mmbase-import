/**
 * Component description interface.
 */
package nl.didactor.component.sources;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorSources extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "sources";
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
}
