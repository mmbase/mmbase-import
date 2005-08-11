/**
 * Component description interface.
 */
package nl.didactor.component.address;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorAddress extends Component {
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
		return "DidactorSources";
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

	/**
	 * Permission framework: indicate whether or not a given operation may be done, with the
	 * given arguments. The return value is a list of 2 booleans; the first boolean indicates
	 * whether or not the operation is allowed, the second boolean indicates whether or not
	 * this result may be cached.
	 */
	public boolean[] may (String operation, Cloud cloud, Map context, String[] arguments) {
		if ("maynot".equals(operation)) {
			return new boolean[] {false, false};
		} else {
			return new boolean[]{true, true};
		}
	}

	public String getSetting(String setting, Cloud cloud, Map context, String[] arguments) {
		if ("mayforward".equals(setting)) {
			return getUserSetting(setting, "" + context.get("user"), cloud, arguments);
		} else if ("bladiebla".equals(setting)) {
			return "2";
		} else { 
			throw new IllegalArgumentException("Unknown setting '" + setting + "'");
		}
	}

	/**
	 * This method is called when a new object is added to Didactor. If the component
	 * needs to insert objects for this object, it can do so. 
	 */
	public boolean notifyCreate(MMObjectNode node) {
		if (node.getBuilder().getTableName().equals("people"))
			return createUser(node);
		return true;
	}
    /**
     * Create the addressbook for this user
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder addressbooks = mmb.getBuilder("addressbooks");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode addressbook = addressbooks.getNewNode(username);
        addressbook.setValue("name", "Adresboek van " + username);
        addressbooks.insert(username, addressbook);
        
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", addressbook.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);
       
        return true;
    }

}
