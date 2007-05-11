package nl.didactor.component.portfolio;

import nl.didactor.builders.DidactorBuilder;
import nl.didactor.component.Component;
import nl.didactor.component.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;

import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.*;

import java.util.*;



public class DidactorPortfolio extends Component {

    private static final Logger log = Logging.getLoggerInstance(DidactorPortfolio.class);
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
        return "portfolio";
    }

    public void init() {
        super.init();
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        people.registerPostInsertComponent(this, 10);
        people.registerPreDeleteComponent(this, 10);
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        classes.registerPostInsertComponent(this, 10);
        classes.registerPreDeleteComponent(this, 10);
        
        MMObjectBuilder chatlogs = mmbase.getBuilder("chatlogs");
        if (chatlogs != null) {
            //<relation from="folders"     to="chatlogs"    type="related" />
            //<relation from="chatlogs"    to="portfoliopermissions" type="related"/>
            TypeRel typeRel = mmbase.getTypeRel();
            RelDef  relDef = mmbase.getRelDef();
            int related = relDef.getNumberByName("related");
            MMObjectBuilder folders = mmbase.getBuilder("folders");
            MMObjectBuilder portfoliopermissions = mmbase.getBuilder("portfoliopermissions");
            if (!typeRel.contains(folders.getObjectType(), chatlogs.getObjectType(), related)) {
                log.info("No relation folders-related->chatlogs. Creating now");
                MMObjectNode n = typeRel.getNewNode("system");
                n.setValue("snumber", folders.getObjectType());
                n.setValue("dnumber", chatlogs.getObjectType());
                n.setValue("rnumber", related);
                int id = typeRel.insert("system", n);
            }
            if (!typeRel.contains(chatlogs.getObjectType(), portfoliopermissions.getObjectType(), related)) {
                log.info("No relation chatlogs-related->portfoliopermissions. Creating now");
                MMObjectNode n = typeRel.getNewNode("system");
                n.setValue("snumber", chatlogs.getObjectType());
                n.setValue("dnumber", portfoliopermissions.getObjectType());
                n.setValue("rnumber", related);
                int id = typeRel.insert("system", n);
            }
            
        }
    }

    public void install() {
        MMBase mmbase = MMBase.getMMBase();
        DidactorBuilder people = (DidactorBuilder)mmbase.getBuilder("people");
        DidactorBuilder classes = (DidactorBuilder)mmbase.getBuilder("classes");
        try {
            List nodes = people.getNodes(new NodeSearchQuery(people));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
            nodes = classes.getNodes(new NodeSearchQuery(classes));
            for (int i=0; i<nodes.size(); i++) {
                postInsert((MMObjectNode)nodes.get(i));
            }
        } catch (SearchQueryException e) {
        }
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
        if (node.getBuilder().getTableName().equals("people")) {
            return createUser(node);
        }
        if (node.getBuilder().getTableName().equals("classes")) {
            return createClass(node);
        }
        return true;
    }
    
    public boolean preDelete(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("people")) {
            return deleteUser(node);
        }
        if (node.getBuilder().getTableName().equals("classes")) {
            return deleteClass(node);
        }
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
    
    // TODO: remove objects in portfolio, and related permissions
    private boolean deleteUser(MMObjectNode user) {
        Vector portfolios = user.getRelatedNodes("portfolios", "related", RelationStep.DIRECTIONS_DESTINATION);

        // Iterate the addressbooks, to remove them all
        for (int i=0; i<portfolios.size(); i++) {
            MMObjectNode portfolio = (MMObjectNode)portfolios.get(i);
            Vector folders = portfolio.getRelatedNodes("folders", "related", RelationStep.DIRECTIONS_DESTINATION);

            // Iterate the contacts, to remove them all
            for (int j=0; j<folders.size(); j++) {
                MMObjectNode folder= (MMObjectNode)folders.get(j);
                folder.getBuilder().removeRelations(folder);
                folder.getBuilder().removeNode(folder);
            }

            portfolio.getBuilder().removeRelations(portfolio);
            portfolio.getBuilder().removeNode(portfolio);
        }
        return true;
    }
    private boolean deleteClass(MMObjectNode cls) {
        return deleteUser(cls);
    }
}
