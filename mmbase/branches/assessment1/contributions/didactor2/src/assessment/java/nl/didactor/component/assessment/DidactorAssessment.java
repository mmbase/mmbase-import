package nl.didactor.component.assessment;

import nl.didactor.component.Component;
import org.mmbase.bridge.*;
import java.util.Map;
import org.mmbase.util.logging.*;

public class DidactorAssessment extends Component{

   private static Logger log = Logging.getLoggerInstance(DidactorAssessment.class);

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
        return "assessment";
    }

    public void init() {
        super.init();
        Component.getComponent("education").registerInterested(this);
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        return new Component[] {Component.getComponent("education")};
    }

    @Override
    public String getValue(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("showlo".equals(setting)) { // is this used somewhere?
            return "2";
        }
        return "";
    }
}
