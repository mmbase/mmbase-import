package nl.didactor.component.core;

import nl.didactor.component.Component;
import nl.didactor.builders.*;
import org.mmbase.security.Action;
import org.mmbase.security.ActionChecker;
import org.mmbase.security.UserContext;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.Parameter;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import java.util.*;

public class DidactorCore extends Component {
    private static Logger log = Logging.getLoggerInstance(DidactorCore.class);
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
        return "core";
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        return new Component[0];
    }


    public static final Parameter EDITCONTEXT   = new Parameter("editcontext", String.class, true);
    private static final Parameter[] PARAMS = new Parameter[] {EDITCONTEXT, Parameter.CLOUD};


    protected static boolean check(int hasInteger, Cloud cloud, UserContext user, String editContext) {
        if (user.getRank() == org.mmbase.security.Rank.ADMIN) return true;
        int userNumber = ((nl.didactor.security.UserContext)user).getUserNumber();
        if (! cloud.hasNode(userNumber)) return false;
        Node u = cloud.getNode(userNumber);
        NodeManager roles = cloud.getNodeManager("roles");
        NodeQuery q =  Queries.createRelatedNodesQuery(u, roles, "related", "destination");
        NodeManager editcontext = cloud.getNodeManager("editcontexts");
        RelationStep rs = q.addRelationStep(editcontext, "posrel", "destination");
        StepField pos = q.createStepField(rs, "pos");
        Queries.addConstraint(q, q.createConstraint(pos, FieldCompareConstraint.GREATER_EQUAL, (Integer) hasInteger));
        StepField name =  q.createStepField(rs.getNext(), "name");
        Queries.addConstraint(q, q.createConstraint(name, editContext));
        if (log.isDebugEnabled()) {
            log.debug(q.toSql());
        }
        return Queries.count(q) > 0;
    }

    private static final Action RO = new Action("core","ro", new ActionChecker() {
            public boolean check(UserContext user, Action ac, Parameters parameters) {
                Cloud cloud = (Cloud) parameters.get(Parameter.CLOUD);
                String editContext = (String) parameters.get(EDITCONTEXT);
                return DidactorCore.check(1, cloud, user, editContext);
            }
        }) {
            public Parameters createParameters() {
                return new Parameters(PARAMS);
            }
            public String toString() {
                return "RO";
            }
        };
    private static final Action RW = new Action("core","rw", new ActionChecker() {
            public boolean check(UserContext user, Action ac, Parameters parameters) {
                Cloud cloud = (Cloud) parameters.get(Parameter.CLOUD);
                String editContext = (String) parameters.get(EDITCONTEXT);
                return DidactorCore.check(2, cloud, user, editContext);
            }
        }) {
            public Parameters createParameters() {
                return new Parameters(PARAMS);
            }
            public String toString() {
                return "RW";
            }
        };
    private static final Action RWD = new Action("core","rwd", new ActionChecker() {
            public boolean check(UserContext user, Action ac, Parameters parameters) {
                Cloud cloud = (Cloud) parameters.get(Parameter.CLOUD);
                String editContext = (String) parameters.get(EDITCONTEXT);
                return DidactorCore.check(3, cloud, user, editContext);
            }
        }) {
            public Parameters createParameters() {
                return new Parameters(PARAMS);
            }
            public String toString() {
                return "RWD";
            }
        };

    private static final Map<String, Action> actions = new HashMap<String, Action>();

    static {
        actions.put(RO.getName(), RO);
        actions.put(RW.getName(), RW);
        actions.put(RWD.getName(), RWD);
    }

    public Map<String, Action> getActions() {
        return Collections.unmodifiableMap(actions);
    }
    @Override
    public boolean[] may(Cloud cloud, Action action, Parameters arguments) {
        boolean mayvalue[]= new boolean[] {false, false};
        mayvalue[0] = action.getDefault().check(cloud.getUser(), action, arguments);
        return mayvalue;
    }

    public void init() {
        super.init();
        MMBase mmbase = MMBase.getMMBase();
        DidactorRel classrel = (DidactorRel)mmbase.getBuilder("classrel");
        classrel.registerPostInsertComponent(this, 10);
    }

    public boolean postInsert(MMObjectNode node) {
        if (node.getBuilder().getTableName().equals("classrel")) {
            MMBase mmbase = MMBase.getMMBase();
            DidactorRel classrel = (DidactorRel)mmbase.getBuilder("classrel");
            MMObjectNode source = classrel.getSource(node);
            MMObjectNode destination = classrel.getDestination(node);
            // adding a copybook to the relation between people and classes
            if (source.getBuilder().getTableName().equals("classes") && destination.getBuilder().getTableName().equals("people")) {
                return insertCopybook(node, destination);
            }
            if (source.getBuilder().getTableName().equals("people") && destination.getBuilder().getTableName().equals("classes")) {
                return insertCopybook(node, source);
            }
            // if people are directly connected to educations they should also have a copybook
            if (source.getBuilder().getTableName().equals("educations") && destination.getBuilder().getTableName().equals("people")) {
                return insertCopybook(node, destination);
            }
            if (source.getBuilder().getTableName().equals("people") && destination.getBuilder().getTableName().equals("educations")) {
                return insertCopybook(node, source);
            }
        }

        return true;
    }

    /**
     * When inserting a new classrel, we need to add a copybook.
     * @param classrel The new object
     */
    private boolean insertCopybook(MMObjectNode classrel, MMObjectNode person) {
        String owner = classrel.getStringValue("owner");

        MMObjectNode copybook = MMBase.getMMBase().getBuilder("copybooks").getNewNode(owner);
        copybook.setValue("name", "copybook for " + person.getFunctionValue("gui", null).toString());
        copybook.insert(owner);

        MMObjectNode relnode = MMBase.getMMBase().getInsRel().getNewNode(owner);
        int rnumber = MMBase.getMMBase().getRelDef().getNumberByName("related");
        relnode.setValue("snumber", classrel.getNumber());
        relnode.setValue("dnumber", copybook.getNumber());
        relnode.setValue("rnumber", rnumber);
        relnode.insert(owner);

        return true;
    }
}
