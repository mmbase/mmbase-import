/**
 * Component description interface.
 */
package nl.didactor.component.core;
import nl.didactor.component.Component;
import org.mmbase.bridge.Cloud;
import java.util.Map;

public class DidactorCore extends Component {
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
        return "DidactorCore";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        return new Component[0];
    }

    public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments) {
        return new boolean[]{true, true};
    }

    public String getSetting(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("something".equals(setting)) {
            return "1";
        } else if ("else".equals(setting)) {
            return "2";
        }
        return "";
    }
}
