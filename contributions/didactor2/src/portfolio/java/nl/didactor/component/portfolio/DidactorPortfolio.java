/**
 * Component description interface.
 */
package nl.didactor.component.portfolio;
import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.Map;

public class DidactorPortfolio extends Component {
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
        return "DidactorPortfolio";
    }

    public void init() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        classes.registerPostInsertComponent(this, 10);
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
     * This method is called when a new object is added to Didactor. If the component
     * needs to insert objects for this object, it can do so. 
     */
    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people"))
            return createUser(node);
        if (node.getBuilder().getTableName().equals("classes"))
            return createClass(node);
        return true;
    }

    /**
     * Create a personal portfolio's for the user.
     */
    private boolean createUser(MMObjectNode user) {
        MMBase mmb = user.getBuilder().getMMBase();
        String username = user.getStringValue("username");
        MMObjectBuilder portfolios = mmb.getBuilder("portfolios");
        MMObjectBuilder folders = mmb.getBuilder("folders");
        MMObjectBuilder posrelBuilder = mmb.getBuilder("posrel");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");
        int posrel = mmb.getRelDef().getNumberByName("posrel");

        MMObjectNode portfolio = portfolios.getNewNode(username);
        portfolio.setValue("type", 0);
        portfolio.setValue("name", "Development portfolio");
        portfolios.insert(username, portfolio);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", portfolio.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        portfolio = portfolios.getNewNode(username);
        portfolio.setValue("type", 1);
        portfolio.setValue("name", "Assessment portfolio");
        portfolios.insert(username, portfolio);
        relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", portfolio.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);
        int assessment = portfolio.getNumber();

        portfolio = portfolios.getNewNode(username);
        portfolio.setValue("type", 2);
        portfolio.setValue("name", "Showcase portfolio");
        portfolios.insert(username, portfolio);
        relation = insrel.getNewNode(username);
        relation.setValue("snumber", user.getNumber());
        relation.setValue("dnumber", portfolio.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        MMObjectNode folder = folders.getNewNode(username);
        folder.setValue("name", "Assessment");
        folders.insert(username, folder);
        relation = posrelBuilder.getNewNode(username);
        relation.setValue("snumber", assessment);
        relation.setValue("dnumber", folder.getNumber());
        relation.setValue("rnumber", posrel);
        relation.setValue("pos", 0);
        posrelBuilder.insert(username, relation);

        return true;
    }

    /**
     * Create a class portfolio.
     */
    private boolean createClass(MMObjectNode cls) {
        MMBase mmb = cls.getBuilder().getMMBase();
        String username = "system";
        MMObjectBuilder portfolios = mmb.getBuilder("portfolios");
        InsRel insrel = mmb.getInsRel();
        int related = mmb.getRelDef().getNumberByName("related");

        MMObjectNode portfolio = portfolios.getNewNode(username);
        portfolio.setValue("name", "Portfolio van klas '" + cls.getStringValue("name") + "'");
        portfolios.insert(username, portfolio);
        MMObjectNode relation = insrel.getNewNode(username);
        relation.setValue("snumber", cls.getNumber());
        relation.setValue("dnumber", portfolio.getNumber());
        relation.setValue("rnumber", related);
        insrel.insert(username, relation);

        return true;
    }
}
