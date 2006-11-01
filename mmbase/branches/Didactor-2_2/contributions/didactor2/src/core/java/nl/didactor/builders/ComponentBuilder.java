package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.XMLApplicationReader;
import org.mmbase.util.xml.BuilderReader;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;

import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import nl.didactor.component.Component;
import nl.didactor.component.BasicComponent;
/**
 * This class provides extra functionality for the People builder. It
 * can encrypt the password of a user, and return a bridge.Node for
 * a given username/password combination
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ComponentBuilder extends AbstractSmartpathBuilder {
    private org.mmbase.util.Encode encoder = null;
    private static Logger log = Logging.getLoggerInstance(ComponentBuilder.class.getName());

    /**
     * Initialize this builder
     */
    public boolean init() {
        super.init();
        NodeSearchQuery query = new NodeSearchQuery(this);
        Vector v = new Vector();

        //register all components
        try {
            Iterator i = getNodes(query).iterator();
            while (i.hasNext()) {
                Component c = registerComponent((MMObjectNode)i.next());
                if (c != null) {
                    v.add(c); 
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        // Make sure that all builders are correct.
        initBuilders();

        // Make sure that all applications are correct.
        initApplications();

        // Initialize all the components
        for (int i=0; i<v.size(); i++) {
            Component c = (Component)v.get(i);
            c.init();
        }
        return true;
    }

    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);
        Component c = registerComponent(node);
        if (c != null) {
            c.init();
            c.install();
        }
        return number;
    }

    private Component registerComponent(MMObjectNode component) {
        String classname = component.getStringValue("classname");
        String componentname = component.getStringValue("name");
        if (componentname != null) {
            componentname = componentname.toLowerCase();
        }
        log.info("Registering component " + componentname + " with class '" + classname + "'");
        Component comp = null;
       
        if (classname == null || "".equals(classname)) {
            comp = new BasicComponent(componentname);
        } else {
            try {
                Class c = Class.forName(classname);
                if (c == null) {
                    comp = new BasicComponent(componentname);
                } else {
                    comp = (Component)c.newInstance();
                    if (comp == null) {
                        comp = new BasicComponent(componentname);
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("Class not found: " + classname);
            } catch (Exception e) {
                log.error("Exception while initializing (" + component + "): " + e);
            }
        }
        if (comp == null) {
            comp = new BasicComponent(componentname);
        }
        comp.setNode(component);
        Component.register(componentname, comp);
        return comp;
    }

    /**
     * This method will do a sanity check between the XML files that define the builders,
     * and the tables in the database. If fields are missing on database level, they will be added.
     * Note: inheritance will make this a little hard!.
     */
    private void initBuilders() {
        initBuilders(MMBaseContext.getConfigPath() + File.separator + "builders" + File.separator);    
    }

    private void initBuilders(String path) {
        File bdir = new File(path);
        if (bdir.isDirectory() && bdir.canRead()) {
            log.service("Reading all builders of directory " + bdir);
            String files[] = bdir.list();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String bname = files[i];
                    if (bname.endsWith(".xml")) {
                        bname = bname.substring(0, bname.length() - 4);
                        initBuilder(path, bname);
                    } else if (bdir.isDirectory()) {
                        initBuilders(path + bname + File.separator);
                    }
                }
            } else {
                log.error("Cannot find builders in " + path);
            }
        }
    }

    /**
     * This method will verify that all the fields specified in the builder XML
     * are also in the database. If not, the field will be created in the database.
     */
    private void initBuilder(String path, String builderName) {
        if (!getMMBase().getBuilder(builderName).created()) {
            // Builder is not yet created in database, so there is no work for us
            return;
        }

        BuilderReader parser = new BuilderReader(path + builderName + ".xml", getMMBase());
        String status = parser.getStatus();
        if (status.equals("active")) {
            HashMap columns = new HashMap();
            MultiConnection con = null;
            Statement stmt = null;
            MMObjectBuilder builder = getMMBase().getBuilder(builderName);
            try {
                con = getMMBase().getConnection();
                DatabaseMetaData meta = con.getMetaData();

                String tableName = getMMBase().getBaseName() + "_" + builder.getTableName();

                // If we use the new storage, we do it the 'cleaner' way
                if (getMMBase().getStorageManagerFactory() != null) {
                    tableName = (String)getMMBase().getStorageManagerFactory().getStorageIdentifier(builder);
                }

                tableName = tableName.toUpperCase();

                ResultSet rs = meta.getColumns(null, null, tableName, null);
                try {
                    while (rs.next()) {
                        Map colInfo = new HashMap();
                        colInfo.put("DATA_TYPE", new Integer(rs.getInt("DATA_TYPE")));
                        colInfo.put("TYPE_NAME", rs.getString("TYPE_NAME"));
                        colInfo.put("COLUMN_SIZE", new Integer(rs.getInt("COLUMN_SIZE")));
                        colInfo.put("NULLABLE", new Boolean(rs.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls));
                        columns.put(rs.getString("COLUMN_NAME").toUpperCase(), colInfo);
                    }
                } catch (SQLException e) {
                    log.error(e);
                } finally {
                    rs.close();
                }
            } catch (SQLException e) {
                log.error(e);
            } finally {
                getMMBase().closeConnection(con, stmt);
            }

            Vector fields = parser.getFieldDefs();
            for (int i=0; i<fields.size(); i++) {
                FieldDefs fdef = (FieldDefs)fields.get(i);
                if (fdef.getDBState() == FieldDefs.DBSTATE_VIRTUAL) {
                    continue;
                }

                String id = fdef.getDBName();

                if (getMMBase().getStorageManagerFactory() != null) {
                    if (fdef.getParent() == null) {
                        fdef.setParent(builder);
                    }
                    id = ((String)fdef.getStorageIdentifier()).toUpperCase();
                } else {
                    id = mmb.getDatabase().getAllowedField(id).toUpperCase();
                }

                if (!columns.containsKey(id)) {
                    log.error("Builder '" + builderName + "' does not have field '" + id + "' in the database, creating it");
                    try {
                        if (getMMBase().getStorageManagerFactory() != null) {
                            getMMBase().getStorageManagerFactory().getStorageManager().create(fdef);
                            // The verify() method of the database storage manager just made this field nonpersistent,
                            // we undo that damage here.
                            builder.getField(fdef.getDBName()).setDBState(FieldDefs.DBSTATE_PERSISTENT);
                        } else {
                            // Old storage ... call is not implemented unfortunately
                            getMMBase().getDatabase().addField(builder, fdef.getDBName());
                        }
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            }
        }
    }


    /**
     * This method will do a sanity check between what is currently in the database,
     * and the application configuration files that live on the disk. Several things are tested:
     * <ul>
     *   <li> If a new RelDef is defined, but it is not yet in the database, it will be added
     *   <li> If a new TypeRel is defined, but it is not yet in the database, it will be added
     * </ul>
     */
    private void initApplications() {
        MMObjectBuilder versions = getMMBase().getBuilder("versions");
        RelDef reldef = getMMBase().getRelDef();
        TypeRel typerel = mmb.getTypeRel();
        TypeDef typedef = mmb.getTypeDef();

        NodeSearchQuery query = new NodeSearchQuery(versions);
        StepField typeField = query.getField(versions.getField("type"));
        query.setConstraint(new BasicFieldValueConstraint(typeField, "application"));

        try {
            Iterator it = versions.getNodes(query).iterator();
            while (it.hasNext()) {
                MMObjectNode appNode = (MMObjectNode)it.next();
                String appname = appNode.getStringValue("name");
                String path = MMBaseContext.getConfigPath() + File.separator + "applications" + File.separator;
                if (!new File(path + appname + ".xml").exists()) {
                    log.warn("Application '" + appname + "' is in the Versions table, but application XML file cannot be loaded.");
                    continue;
                }
                XMLApplicationReader app = new XMLApplicationReader(path + appname + ".xml");

                Vector neededRelDefs = app.getNeededRelDefs();
                for (int i=0; i<neededRelDefs.size(); i++) {
                    Hashtable rd = (Hashtable)neededRelDefs.get(i);
                    String sname = (String)rd.get("source");
                    String dname = (String)rd.get("target");
                    String direction = (String)rd.get("direction");
                    String sguiname = (String)rd.get("guisourcename");
                    String dguiname = (String)rd.get("guitargetname");
                    String buildername = (String)rd.get("builder");
                    int builder = -1;
                    if (buildername != null) {
                        builder = getMMBase().getTypeDef().getIntValue(buildername);
                    }
                    if (builder <= 0) {
                        builder = getMMBase().getInsRel().oType;
                    }

                    int dir = 0;
                    if ("unidirectional".equals(direction)) {
                        dir = 1;
                    } else {
                        dir = 2;
                    }

                    if (reldef.getNumberByName(sname + "/" + dname) == -1) {
                        MMObjectNode node = reldef.getNewNode("system");
                        node.setValue("sname", sname);
                        node.setValue("dname", dname);
                        node.setValue("dir", dir);
                        node.setValue("sguiname", sguiname);
                        node.setValue("dguiname", dguiname);
                        node.setValue("builder", builder);
                        int id = reldef.insert("system", node);
                        if (id != -1) {
                            log.info("Application upgrade: RefDef (" + sname + "," + dname + ") installed");
                        } else {
                            log.error("Application upgrade: RelDef (" + sname + "," + dname + ") could not be installed");
                        }
                    }
                }

                Vector allowedRelations = app.getAllowedRelations();
                for (int i=0; i<allowedRelations.size(); i++) {
                    boolean error = false;
                    Hashtable tr = (Hashtable)allowedRelations.get(i);
                    String sname = (String)tr.get("from");
                    String dname = (String)tr.get("to");
                    String rname = (String)tr.get("type");
                    int rnumber = reldef.getNumberByName(rname);
                    int snumber = typedef.getIntValue(sname);
                    int dnumber = typedef.getIntValue(dname);
                    if (rnumber == -1) {
                        log.error("Application upgrade: No reldef with role '" + rname + "' defined, skipping (" + sname + "/" + dname + "/" + rname + ")");
                        error = true;
                    }
                    if (snumber == -1) {
                        log.error("Application upgrade: No builder with name '" + sname + "' defined, skipping (" + sname + "/" + dname + "/" + rname + ")");
                        error = true;
                    }
                    if (dnumber == -1) {
                        log.error("Application upgrade: No builder with name '" + dname + "' defined, skipping (" + sname + "/" + dname + "/" + rname + ")");
                        error = true;
                    }
                    if (!error && !typerel.contains(snumber, dnumber, rnumber, TypeRel.STRICT)) {
                        MMObjectNode node = typerel.getNewNode("system");
                        node.setValue("snumber", snumber);
                        node.setValue("dnumber", dnumber);
                        node.setValue("rnumber", rnumber);
                        node.setValue("max", -1);
                        int id = typerel.insert("system", node);
                        if (id != -1) {
                            log.info("Application upgrade: TypeRel (" + sname + "," + dname + "," + rname + ") installed");
                        } else {
                            log.error("Application upgrade: TypeRel (" + sname + "," + dname + "," + rname + ") could not be installed");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


