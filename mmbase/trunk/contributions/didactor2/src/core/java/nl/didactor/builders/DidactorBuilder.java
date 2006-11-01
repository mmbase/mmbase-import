package nl.didactor.builders;
import nl.didactor.component.Component;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import java.util.*;


/**
 * This class provides a default framework for Didactor builders, where components
 * can register themselves for events on the builders.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DidactorBuilder extends MMObjectBuilder {
    private org.mmbase.util.Encode encoder = null;
    private static Logger log = Logging.getLoggerInstance(DidactorBuilder.class.getName());

    private SortedSet preInsertComponents = new TreeSet();
    private SortedSet postInsertComponents = new TreeSet();

    private SortedSet preCommitComponents = new TreeSet();
    private SortedSet postCommitComponents = new TreeSet();

    private SortedSet preDeleteComponents = new TreeSet();

    public boolean init() {
        FieldDefs fd = new FieldDefs("_justinserted", "string", -1, -1, "_justinserted", FieldDefs.TYPE_STRING, -1, FieldDefs.DBSTATE_VIRTUAL);
        addField(fd);

        return super.init();
    }

    public void registerPreInsertComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        preInsertComponents.add(event);
        log.info("Added listener on " + getTableName() + ".preInsert(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPostInsertComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        postInsertComponents.add(event);
        log.info("Added listener on " + getTableName() + ".postInsert(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPreCommitComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        preCommitComponents.add(event);
        log.info("Added listener on " + getTableName() + ".preCommit(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPostCommitComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        postCommitComponents.add(event);
        log.info("Added listener on " + getTableName() + ".postCommit(): '" + c.getName() + "' with priority " + priority);
    }

    public void registerPreDeleteComponent(Component c, int priority) {
        EventInstance event = new EventInstance(c, priority);
        preDeleteComponents.add(event);
        log.info("Added listener on " + getTableName() + ".preDelete(): '" + c.getName() + "' with priority " + priority);
    }

    /**
     * Overridden 'insert' from MMObjectBuilder. It will call the 'preInsert()' 
     * method for all registered components just before inserting the node. It
     * calls the 'postInsert()' for all registered components after inserting the node.
     */
    public int insert(String owner, MMObjectNode node) {
        node.setValue("_justinserted", "true");
        Iterator i = preInsertComponents.iterator();
        while (i.hasNext()) {
            Component c = ((EventInstance)i.next()).component;
            log.info("Firing " + c.getName() + ".preInsert() on object of type '" + node.getBuilder().getTableName() + "'");
            c.preInsert(node);
        }
        int res = super.insert(owner, node);

        Collection fields = getFields();
        Iterator it = fields.iterator();
        while (it.hasNext()) {
            FieldDefs fd = (FieldDefs) it.next();
            if (fd.getDBState() == FieldDefs.DBSTATE_VIRTUAL && fd.getDBPos() == 300) {
                log.debug("Have to process set on field [" + fd.getDBName() + "] with value [" + node.getValues().get(fd.getDBName()) + "]");
                setFieldValue(owner, node, fd.getDBName());
            }
        }

        i = postInsertComponents.iterator();
        while (i.hasNext()) {
            Component c = ((EventInstance)i.next()).component;
            log.info("Firing " + c.getName() + ".postInsert() on object of type '" + node.getBuilder().getTableName() + "'");
            c.postInsert(node);
        }
        return res;
    }


    /**
     * Overridden 'preCommit' from MMObjectBuilder. It will call the 'preCommit()' 
     * method for all registered components.
     */
    public MMObjectNode preCommit(MMObjectNode node) {
        Iterator i = preCommitComponents.iterator();
        if (i.hasNext()) {
            if (node.getValue("_justinserted") != null) {
                // the preCommit() is called on the newly inserted node since the new storage layer.
                // pre-insert behavior should be handled by preinsert handlers, and not by
                // precommit handlers. So we skip this.
                return node;
            }
        }
        while (i.hasNext()) {
            Component c = ((EventInstance)i.next()).component;
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

        Collection fields = getFields();
        Iterator it = fields.iterator();
        while (it.hasNext()) {
            FieldDefs fd = (FieldDefs)it.next();
            if (fd.getDBState() == FieldDefs.DBSTATE_VIRTUAL && fd.getDBPos() == 300) {
                log.debug("Have to process set on field [" + fd.getDBName() + "] with value [" + node.getValues().get(fd.getDBName()) + "]");
                setFieldValue(node.getStringValue("owner"), node, fd.getDBName());
            }
        }

        Iterator i = postCommitComponents.iterator();
        while (i.hasNext()) {
            Component c = ((EventInstance)i.next()).component;
            log.info("Firing " + c.getName() + ".postCommit() on object of type '" + node.getBuilder().getTableName() + "'");
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
        Iterator i = preDeleteComponents.iterator();
        while (i.hasNext()) {
            Component c = ((EventInstance)i.next()).component;
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
                Vector fieldNodes = node.getRelatedNodes("fields");
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
        Vector fieldNodes = node.getRelatedNodes("fields");
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
