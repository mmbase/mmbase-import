/**
 * Component description interface.
 */
package nl.didactor.component;
import nl.didactor.component.Component;
import nl.didactor.builders.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;
import java.util.Map;

public class BasicComponent extends Component {
    private String name;

    private BasicComponent() {
    }

    public BasicComponent(String name) {
        this.name = name;
    }

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
        return name;
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        return new Component[0];
    }
}

