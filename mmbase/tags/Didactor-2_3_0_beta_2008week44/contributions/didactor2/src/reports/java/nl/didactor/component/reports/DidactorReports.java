package nl.didactor.component.reports;

import nl.didactor.component.Component;
import nl.didactor.component.core.DidactorCore;
import nl.didactor.events.*;
import nl.didactor.reports.util.EventManager;


/**
 * @javadoc
 */
public class DidactorReports extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "2.0";
    }

    public void init() {
        super.init();
        org.mmbase.core.event.EventListener reporting = new EventManager();
        org.mmbase.core.event.EventManager.getInstance().addEventListener(reporting);
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "reports";
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
