package nl.didactor.builders;
import nl.didactor.component.Component;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.core.CoreField;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import java.util.*;


/**
 * This class provides a default framework for Didactor builders, where components
 * can register themselves for events on the builders.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DidactorBuilder extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(DidactorBuilder.class);

    private SortedSet<EventInstance> preInsertComponents  = new TreeSet<EventInstance>();
    private SortedSet<EventInstance> postInsertComponents = new TreeSet<EventInstance>();

    private SortedSet<EventInstance> preCommitComponents  = new TreeSet<EventInstance>();
    private SortedSet<EventInstance> postCommitComponents = new TreeSet<EventInstance>();

    private SortedSet<EventInstance> preDeleteComponents  = new TreeSet<EventInstance>();

    public boolean init() {
        checkAddTmpField("_justinserted");
        return super.init();
    }

    public void registerPreInsertComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        preInsertComponents.add(event);
        log.service("Added listener on " + getTableName() + ".preInsert(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPostInsertComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        postInsertComponents.add(event);
        log.service("Added listener on " + getTableName() + ".postInsert(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPreCommitComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        preCommitComponents.add(event);
        log.service("Added listener on " + getTableName() + ".preCommit(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPostCommitComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        postCommitComponents.add(event);
        log.service("Added listener on " + getTableName() + ".postCommit(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPreDeleteComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        preDeleteComponents.add(event);
        log.service("Added listener on " + getTableName() + ".preDelete(): '" + c.getName() + "' with priority " + priority);
    }

    /**
     * Overridden 'insert' from MMObjectBuilder. It will call the 'preInsert()'
     * method for all registered components just before inserting the node. It
     * calls the 'postInsert()' for all registered components after inserting the node.
     */
    public int insert(String owner, MMObjectNode node) {
        node.setValue("_justinserted", "true");
        for (EventInstance e : preInsertComponents) {
            Component c = e.component;
            if (log.isDebugEnabled()) {
                log.debug("Firing " + c.getName() + ".preInsert() on object of type '" + node.getBuilder().getTableName() + "'");
            }
            c.preInsert(node);
        }
        int res = super.insert(owner, node);

        for (CoreField fd : (Collection<CoreField>) getFields()) {
            /*
            if (fd.getDBState() == FieldDefs.DBSTATE_VIRTUAL && fd.getDBPos() == 300) {
            // WTF?
                if (log.isDebugEnabled()) {
                    log.debug("Have to process set on field [" + fd.getName() + "] with value [" + node.getValues().get(fd.getName()) + "]");
                }
                setFieldValue(owner, node, fd.getName());
            }
            */
        }

        for (EventInstance e : postInsertComponents) {
            Component c = e.component;
            if (log.isDebugEnabled()) {
                log.debug("Firing " + c.getName() + ".postInsert() on object of type '" + node.getBuilder().getTableName() + "'");
            }
            c.postInsert(node);
        }
        return res;
    }


    /**
     * Overridden 'preCommit' from MMObjectBuilder. It will call the 'preCommit()'
     * method for all registered components.
     */
    public MMObjectNode preCommit(MMObjectNode node) {
        if (preCommitComponents.size() > 0) {
            if (node.getValue("_justinserted") != null) {
                // the preCommit() is called on the newly inserted node since the new storage layer.
                // pre-insert behavior should be handled by preinsert handlers, and not by
                // precommit handlers. So we skip this.
                return node;
            }
        }
        for (EventInstance e : preCommitComponents) {
            Component c = e.component;
            log.info("Firing " + c.getName() + ".preCommit() on object of type '" + node.getBuilder().getTableName() + "'");
            c.preCommit(node);
        }
        super.preCommit(node);
        return node;
    }

    /**
     * Overridden 'commit' from MMObjectBuilder. It will call the 'postCommit()' .
     * method for all registered components
     */
    public boolean commit(MMObjectNode node) {
        boolean bSuperCommit = super.commit(node);

        for (CoreField fd : (Collection<CoreField>) getFields()) {
            /*
              WTF ?
            if (fd.getDBState() == FieldDefs.DBSTATE_VIRTUAL && fd.getDBPos() == 300) {
                log.debug("Have to process set on field [" + fd.getDBName() + "] with value [" + node.getValues().get(fd.getDBName()) + "]");
                setFieldValue(node.getStringValue("owner"), node, fd.getDBName());
            }
            */
        }

        for (EventInstance e : postCommitComponents) {
            Component c = e.component;
            log.debug("Firing " + c.getName() + ".postCommit() on object of type '" + node.getBuilder().getTableName() + "'");
            c.postCommit(node);
        }
        return bSuperCommit;
    }

    /**
     * This method does NOT override any methods from MMObjectBuilder, but is triggered
     * by the authorization class. This is a rather ugly hack, which might not be supported
     * in upcoming MMBase releases, but at the moment it is the only place to handle
     * delete events on nodes before the bridge complains.
     */
    public boolean preDelete(MMObjectNode node) {
        for (EventInstance e : preDeleteComponents) {
            Component c = e.component;
            log.info("Firing " + c.getName() + ".preDelete() on object of type '" + node.getBuilder().getTableName() + "'");
            c.preDelete(node);
        }
        return true;
    }

    /**
     * Return the value for a field of the node. This method
     * is overridden from MMObjectBuilder, and will return a value
     * for the virtual 'isonline' field.
     * @param node Node to get a value for
     * @param field Name of the field.
     * @return an object containing the value.
     */
    public Object getValue(MMObjectNode node, String field) {
        FieldDefs fd = getField(field);

        if (fd != null) {
            if (fd.getDBState() == FieldDefs.DBSTATE_VIRTUAL && node.getNumber() != -1 && fd.getDBPos() == 300) {
                log.debug("Getting field [" + field + "] from db fields thingie");
                // Special Didactor case: field was added by a component.xml file, we read it from a related fields node
                List fieldNodes = node.getRelatedNodes("fields");
                for (int i=0; i<fieldNodes.size(); i++) {
                    MMObjectNode fieldNode = (MMObjectNode)fieldNodes.get(i);
                    if (field.equals(fieldNode.getStringValue("name"))) {
                        log.debug("Found value: " + fieldNode.getStringValue("value"));
                        return fieldNode.getStringValue("value");
                    }
                }
                log.debug("No value found!!");
            }
        }

        return super.getValue(node, field);
    }

    private void setFieldValue(String owner, MMObjectNode node, String field) {
        List fieldNodes = node.getRelatedNodes("fields");
        for (int i=0; i<fieldNodes.size(); i++) {
            MMObjectNode fieldNode = (MMObjectNode)fieldNodes.get(i);
            if (field.equals(fieldNode.getStringValue("name"))) {
                fieldNode.setValue("value", node.getValues().get(field));
                fieldNode.commit();
                return;
            }
        }

        // not found: create the node
        MMObjectBuilder fieldBuilder = MMBase.getMMBase().getBuilder("fields");
        MMObjectNode fieldNode = fieldBuilder.getNewNode(owner);

        fieldNode.setValue("name", field);
        fieldNode.setValue("value", node.getValues().get(field));
        int fieldId = fieldBuilder.insert(owner, fieldNode);

        int authrel = MMBase.getMMBase().getRelDef().getNumberByName("authrel");
        InsRel insrel = (InsRel)MMBase.getMMBase().getBuilder("authrel");
        insrel.insert(owner, node.getNumber(), fieldId, authrel);
    }

    /**
     * This small innerclass represents an event-instance. It mainly is just a wrapper
     * around the component, but also has a priority that allows the components to be
     * sorted when they are fired.
     */
    private class EventInstance implements Comparable {
        protected Component component;
        protected int priority;

        /**
         * Public constructor
         */
        public EventInstance(Component component, int priority) {
            this.component = component;
            this.priority = priority;
        }

        /**
         * @returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
         */
        public int compareTo(Object o) {
            if (o instanceof EventInstance) {
                EventInstance other = (EventInstance)o;
                if (this.priority == other.priority) {
                    return this.component.getName().compareTo(other.component.getName());
                }
                return this.priority - other.priority;
            }
            return -1;
        }
    }
}
