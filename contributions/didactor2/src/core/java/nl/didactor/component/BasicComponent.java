/**
 * Component description interface.
 */
package nl.didactor.component;


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

