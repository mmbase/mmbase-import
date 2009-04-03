package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.ResourceLoader;
import org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.xml.applicationdata.ApplicationReader;
import org.mmbase.util.xml.BuilderReader;

import java.util.*;
import java.io.File;

import java.sql.*;
import java.lang.reflect.*;

import nl.didactor.component.Component;
import nl.didactor.component.BasicComponent;

/**
 *
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @version $Id: ComponentBuilder.java,v 1.15 2008-09-04 09:49:14 michiel Exp $
 */
public class ComponentBuilder extends DidactorBuilder {

    private static final Logger log = Logging.getLoggerInstance(ComponentBuilder.class);

    /**
     * Initialize this builder
     */
    public boolean init() {
        super.init();

        log.info("Registering didactor components");
        NodeSearchQuery query = new NodeSearchQuery(this);
        List<Component> v = new ArrayList<Component>();

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
        // initBuilders(); // i

        // Make sure that all applications are correct.
        initApplications();

        // Initialize all the components
        for (Component c : v) {
            try {
                c.init();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
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
                Class clazz  = Class.forName(classname);
                try {
                    Constructor c = clazz.getConstructor(MMObjectNode.class);
                    comp = (Component) c.newInstance(component);
                } catch (NoSuchMethodException  nsme) {
                    comp = (Component) clazz.newInstance();
                }
            } catch (ClassNotFoundException e) {
                log.info("Class not found: " + classname);
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
     * @todo currently unused
     */
    private void initBuilders() {
        Iterator i = MMBase.getMMBase().getBuilderLoader().getResourcePaths(ResourceLoader.XML_PATTERN, true).iterator();
        while (i.hasNext()) {
            try {
                String builder = (String) i.next();
                String path = ResourceLoader.getDirectory(builder);
                String bname = ResourceLoader.getName(builder);
                initBuilder(path, bname);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * This method will verify that all the fields specified in the builder XML
     * are also in the database. If not, the field will be created in the database.
     * @todo currently unused
     */
    private void initBuilder(String path, String builderName) throws java.io.IOException {
        if (!getMMBase().getBuilder(builderName).created()) {
            // Builder is not yet created in database, so there is no work for us
            return;
        }
        BuilderReader parser;
        try {
            parser = new BuilderReader(getMMBase().getBuilderLoader().getDocument(path + "/" + builderName + ".xml", false,  BuilderReader.class), getMMBase());
        } catch (Exception sax) {
            log.warn("Could not read " + path + "/" + builderName + ".xml: " + sax.getMessage() + " skipping");
            return;
        }
        String status = parser.getStatus();
        if (status.equals("active")) {
            HashMap columns = new HashMap();
            Connection con = null;
            Statement stmt = null;
            MMObjectBuilder builder = getMMBase().getBuilder(builderName);
            try {
                con = ((DatabaseStorageManagerFactory) getMMBase().getStorageManagerFactory()).getDataSource().getConnection();
                DatabaseMetaData meta = con.getMetaData();

                String tableName = getMMBase().getBaseName() + "_" + builder.getTableName();

                // If we use the new storage, we do it the 'cleaner' way
                if (getMMBase().getStorageManagerFactory() != null) {
                    tableName = (String) getMMBase().getStorageManagerFactory().getStorageIdentifier(builder);
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
                try {if (con != null) con.close(); } catch (Exception e) {}
                try {if (stmt != null) stmt.close();} catch (Exception e) {}
            }

            Collection fields = parser.getFields();
            Iterator it = fields.iterator();
            while (it.hasNext()) {
                FieldDefs fdef = (FieldDefs) it.next();
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
                    id = ("" + mmb.getStorageManagerFactory().getStorageIdentifier(id)).toUpperCase();
                }

                if (false && !columns.containsKey(id)) { // switched off, because it doesn't work well
                    log.info("Builder '" + builderName + "' does not have field '" + id + "' in the database, creating it");
                    try {
                        if (getMMBase().getStorageManagerFactory() != null) {
                            getMMBase().getStorageManagerFactory().getStorageManager().create(fdef);
                            // The verify() method of the database storage manager just made this field nonpersistent,
                            // we undo that damage here.
                            FieldDefs cf = builder.getField(fdef.getName());
                            if (cf != null) {
                                cf.setDBState(FieldDefs.DBSTATE_PERSISTENT);
                            } else {
                                log.error("No such field " + fdef.getName());
                            }
                        } else {
                            // Old storage ... call is not implemented unfortunately
                            builder.addField(fdef);
                            //getMMBase().getDatabase().addField(builder, fdef.getDBName());
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
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
                String path = "applications/";
                if (! ResourceLoader.getConfigurationRoot().getResource(path + appname + ".xml").openConnection().getDoInput()) {
                    log.warn("Application '" +  appname + "' is in the Versions table, but application XML file cannot be loaded.");
                    continue;
                }
                ApplicationReader app = new ApplicationReader(ResourceLoader.getConfigurationRoot().getInputSource(path + appname + ".xml"));

                List neededRelDefs = app.getNeededRelDefs();
                for (int i=0; i<neededRelDefs.size(); i++) {
                    Map rd = (Map)neededRelDefs.get(i);
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
                        builder = getMMBase().getInsRel().getObjectType();
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

                List allowedRelations = app.getAllowedRelations();
                for (int i=0; i<allowedRelations.size(); i++) {
                    boolean error = false;
                    Map tr = (Map)allowedRelations.get(i);
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
            log.error(e.getMessage(), e);
        }
    }
}


