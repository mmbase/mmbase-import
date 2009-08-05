package nl.didactor.component.faq;

import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import java.util.Map;
import nl.didactor.component.portalpages.DidactorPortalPages;

public class DidactorFaq extends Component {
    /**
     * Returns the version of the component
     */
    public String getVersion() {
        return "0.1";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "faq";
    }

    /**
     * Returns an array of components this component depends on.
     */
    public Component[] dependsOn() {
        Component[] components = new Component[2];
        components[0] = new DidactorCore();
        components[1] = new DidactorPortalPages();
        return components;
    }

    /**
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so.
     */
    public boolean notifyCreate(MMObjectNode node) {
    	/*
          if (node.getBuilder().getTableName().equals("classes"))
          return createClass(node);
        */
        return true;
    }


}
