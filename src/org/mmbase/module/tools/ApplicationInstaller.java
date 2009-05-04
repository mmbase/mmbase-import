/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.cache.NodeCache;
import org.mmbase.core.CoreField;
import org.mmbase.module.builders.Versions;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.ApplicationReader;
import org.xml.sax.InputSource;


/**
 * Application installations functionalite of MMAdmin.
 *
 * @author Nico Klasens
 * @since MMBase-1.8
 * @version $Id$
 */
public class ApplicationInstaller {

    private static final Logger log = Logging.getLoggerInstance(ApplicationInstaller.class);


    private final MMBase mmb;
    private final MMAdmin admin;

    public ApplicationInstaller(MMBase mmb, MMAdmin admin) {
        this.mmb = mmb;
        this.admin = admin;
    }

    public void installApplications() throws SearchQueryException {
        ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
        Iterator i = applicationLoader.getResourcePaths(ResourceLoader.XML_PATTERN, false).iterator();
        while (i.hasNext()) {
            String appResource = (String) i.next();
            ApplicationResult result = new ApplicationResult();
            if (!installApplication(appResource.substring(0, appResource.length() - 4), -1, null, result, new HashSet(), true)) {
                log.error("Problem installing application : " + appResource + ", cause: \n" + result.getMessage());
            }
        }
    }

    /**
     * Installs the application
     * @param applicationName Name of the application file, without the xml extension
     *                        This is also assumed to be the name of teh application itself
     *                        (if not, a warning will be issued)
     * @param result the result object, containing error messages when the installation fails,
     * or the installnotice if succesfull or already installed
     * @param installationSet set of installations that are currently being installed.
     *                        used to check if there are circular dependencies
     * @param autoDeploy if true, the installation is only installed if the application is set to autodeploy
     * @return true if succesfull, false otherwise
     */
    public boolean installApplication(String applicationName, int requiredVersion,
            String requiredMaintainer, ApplicationResult result, Set installationSet,
            boolean autoDeploy) throws SearchQueryException {

        if (installationSet.contains(applicationName)) {
            return result.error("Circular reference to application with name " + applicationName);
        }

        ApplicationReader reader = getApplicationReader(applicationName);
        if (reader != null) {
            Versions ver = (Versions)mmb.getMMObject("versions");
            // test autodeploy
            // test autodeploy
            if (autoDeploy) {
                if (!reader.hasAutoDeploy()) {
                    return true;
                } else {
                    if (admin.getIgnoredAutodeployApplications().contains(applicationName)) {
                        log.info("Ignoring auto-deploy '" + applicationName + "' because specified as ignore-auto-deploy parameter of mmadmin");
                        return true;
                    }
                }
            }

            String name = reader.getName();
            String maintainer = reader.getMaintainer();
            if (requiredMaintainer != null && !maintainer.equals(requiredMaintainer)) {
                return result.error("Install error: " + name + " requires maintainer '" + requiredMaintainer +
                                    "' but found maintainer '" + maintainer + "'");
            }
            int version = reader.getVersion();
            if (requiredVersion != -1 && version != requiredVersion) {
                return result.error("Install error: " + name + " requires version '" + requiredVersion +
                                    "' but found version '" + version + "'");
            }
            int installedVersion = ver.getInstalledVersion(name, "application");
            if (installedVersion == -1 || version > installedVersion) {
                if (!name.equals(applicationName)) {
                    result.warn("Application name " + name + " not the same as the base filename " + applicationName + ".\n"
                                + "This may cause problems when referring to this application.");
                }
                // We should possibly check whether the maintainer is valid here (see sample code below).
                // There is currently no way to do this, though, unless we use awful queries.
                // what we need is a getInstalledMaintainer() method on the Versions builder
                /* sample code
                String installedMaintainer=ver.getInstalledMaintainer(name,"application");
                if (!maintainer.equals(installedAppMaintainer)) {
                    return result.error("Install error: "+name+" is of maintainer '"+maintainer+"' but installed application is of maintainer '"+installedMaintainer+"'");
                }
                 */
                // should be installed - add to installation set
                installationSet.add(applicationName);
                List requires = reader.getRequirements();
                for (Iterator i = requires.iterator(); i.hasNext();) {
                    Map reqapp = (Map)i.next();
                    String reqType = (String)reqapp.get("type");
                    if (reqType == null || reqType.equals("application")) {
                        String appName = (String)reqapp.get("name");
                        int installedAppVersion = ver.getInstalledVersion(appName, "application");
                        String appMaintainer = (String)reqapp.get("maintainer");
                        int appVersion = -1;
                        try {
                            String appVersionAttr = (String)reqapp.get("version");
                            if (appVersionAttr != null)
                                appVersion = Integer.parseInt(appVersionAttr);
                        } catch (Exception e) {}
                        if (installedAppVersion == -1 || appVersion > installedAppVersion) {
                            log.service("Application '" + applicationName + "' requires : " + appName);
                            if (!installApplication(appName, appVersion, appMaintainer,
                                result, installationSet, false)) {
                                return false;
                            }
                        } else if (appMaintainer != null) {
                            // we should possibly check whether the maintainer is valid here (see sample code below).
                            // There is currently no way to do this, though, unless we use awful queries.
                            // what we need is a getInstalledMaintainer() method on the Versions builder
                            /* sample code
                            String installedAppMaintainer=ver.getInstalledMaintainer(name,"application");
                            if (!appMaintainer.equals(installedAppMaintainer)) {
                                return result.error("Install error: "+name+" requires maintainer '"+appMaintainer+"' but found maintainer '"+installedAppMaintainer+"'");
                            }
                             */
                        }
                    }
                }
                // note: currently name and application file name should be the same
                if (installedVersion == -1) {
                    log.info("Installing application : " + name);
                } else {
                    log.info("installing application : " + name + " new version from " + installedVersion + " to " + version);
                }
                if (installBuilders(reader.getNeededBuilders(), "applications/" + applicationName, result)
                    && installRelDefs(reader.getNeededRelDefs(), result)
                    && installAllowedRelations(reader.getAllowedRelations(), result)
                    && installDataSources(reader.getDataSources(), applicationName, result)
                    && installRelationSources(reader.getRelationSources(), applicationName, result)) {

                    if (installedVersion == -1) {
                        ver.setInstalledVersion(name, "application", maintainer, version);
                    } else {
                        ver.updateInstalledVersion(name, "application", maintainer, version);
                    }
                    log.info("Application '" + name + "' deployed succesfully.");
                    result.success(
                        "Application loaded oke\n\n"
                            + "The application has the following install notice for you : \n\n"
                            + reader.getInstallNotice());
                }
                // installed or failed - remove from installation set
                installationSet.remove(applicationName);
            } else {
                // only return this message if the application is the main (first) application
                // and if it was not auto-deployed (as in that case messages would not be deemed very useful)
                if (installationSet.size() == 1) {
                    result.success(
                        "Application was allready loaded (or a higher version)\n\n"
                            + "To remind you here is the install notice for you again : \n\n"
                            + reader.getInstallNotice());
                }
            }
        } else {
            result.error("Install error: can't find xml resource: applications/" + applicationName + ".xml");
        }
        return result.isSuccess();
    }

    /**
     * @javadoc
     * @since MMBase-1.7
     */
    protected boolean installDataSources(List dataSources, String appName, ApplicationResult result) {
        MMObjectBuilder syncbul = mmb.getMMObject("syncnodes");

        List nodeFieldNodes = new ArrayList(); // a temporary list with all nodes that have NODE fields, which should be synced, later.
        if (syncbul != null) {
            for (Iterator h = dataSources.iterator(); h.hasNext();) {
                Map bh = (Map) h.next();
                XMLNodeReader nodeReader = getNodeReader(bh, appName);
                if (nodeReader == null) {
                    continue;
                }
                else {
                    installDatasource(syncbul, nodeReader, nodeFieldNodes, result);
                }
            }

            treatNodeFields(nodeFieldNodes, syncbul);

            return result.isSuccess();
        } else {
            return result.error("Application installer : can't reach syncnodes builder"); //
        }
    }

    private void installDatasource(MMObjectBuilder syncbul, XMLNodeReader nodeReader, List nodeFieldNodes, ApplicationResult result) {
        String exportsource = nodeReader.getExportSource();
        int timestamp = nodeReader.getTimeStamp();

        nodeReader.setLoadBinaries(false);

        // loop all nodes , and add to syncnodes.
        for (Iterator n = nodeReader.getNodes(mmb).iterator(); n.hasNext();) {
            try {
                MMObjectNode newNode = (MMObjectNode)n.next();
                nodeReader.loadBinairyFields(newNode);

                int exportnumber = newNode.getIntValue("number");
                if (existsSyncnode(syncbul, exportsource, exportnumber)) {
                    // XXX To do : we may want to load the node and check/change the fields
                    log.debug("node allready installed : " + exportnumber);
                } else {
                    try {
                        newNode.setValue("number", -1);
                        int localnumber = doKeyMergeNode(syncbul, newNode, exportsource, result);
                        if (localnumber != -1) { // this node was not yet imported earlier
                            createSyncnode(syncbul, exportsource, timestamp, exportnumber, localnumber);
                            if (localnumber == newNode.getNumber()) {
                                findFieldsOfTypeNode(nodeFieldNodes, exportsource, newNode);
                            }
                        }
                        NodeCache.getCache().remove(new Integer(localnumber));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                n.remove();
            }
            catch (SearchQueryException sqe) {
                log.error(sqe);
            }
        }
    }

    private void findFieldsOfTypeNode(List nodeFieldNodes, String exportsource, MMObjectNode newNode) {
        // determine if there were NODE fields, which need special treatment later.
        Collection fields = newNode.getBuilder().getFields();
        Iterator i = fields.iterator();
        while (i.hasNext()) {
            CoreField field = (CoreField) i.next();

            // Fields with type NODE and notnull=true will be handled
            // by the doKeyMergeNode() method.
            if (field.getType() == Field.TYPE_NODE
                    && ! field.getName().equals("number")
                    && ! field.isRequired()) {

                newNode.storeValue("__exportsource", exportsource);
                nodeFieldNodes.add(newNode);
                break;
            }
        }
    }

    private void treatNodeFields(List nodeFieldNodes, MMObjectBuilder syncbul) {
        Iterator i = nodeFieldNodes.iterator();
        while (i.hasNext()) {
            MMObjectNode importedNode = (MMObjectNode) i.next();
            String exportsource = (String) importedNode.getValues().get("__exportsource");
            // clean it up
            importedNode.storeValue("__exportsource", null); // hack to remove it.

            Collection fields = importedNode.getBuilder().getFields();
            Iterator j = fields.iterator();
            while (j.hasNext()) {
                CoreField def = (CoreField) j.next();
                String fieldName = def.getName();
                if (def.getType() == Field.TYPE_NODE &&
                    !fieldName.equals("number") &&
                    !fieldName.equals("snumber") &&
                    !fieldName.equals("dnumber") &&
                    !fieldName.equals("rnumber")
                   ) {

                    updateFieldWithTypeNode(syncbul, importedNode, exportsource, fieldName);
                }
            }
            if (importedNode.isChanged()) {
                importedNode.commit();
            }
        }
    }

    /**
     * @javadoc !!!
     */
    private int doKeyMergeNode(MMObjectBuilder syncbul, MMObjectNode newNode, String exportsource, ApplicationResult result) {
        MMObjectBuilder bul = newNode.getBuilder();
        if (bul != null) {
            Collection vec = bul.getFields();
            Constraint constraint = null;
            NodeSearchQuery query = null;
            for (Iterator h = vec.iterator(); h.hasNext();) {
                CoreField def = (CoreField)h.next();
                // check for notnull fields with type NODE.
                if (def.getType() == Field.TYPE_NODE
                    && ! def.getName().equals("number")
                    && ! def.getName().equals("otype")
                    && def.isRequired()) {

                    // Dangerous territory here.
                    // The node contains a reference to another node.
                    // The referenced node has to exist when this node is inserted.
                    // trying to update the node.
                    updateFieldWithTypeNode(syncbul, newNode, exportsource, def.getName());
                    if (newNode.getIntValue(def.getName()) == -1) {
                       // guess that failed
                       result.error("Insert of node " + newNode + " failed. Field '" + def.getName() + "' with type NODE is not allowed to have a null value. " +
                                    "The referenced node is not found. Try to reorder the nodes so the referenced node is imported before this one.");
                       return -1;
                    }
                }

                // generation of key constraint to check if there is a node already present.
                // if a node is present then we can't insert this one.
                if (def.isUnique()) {
                    int type = def.getType();
                    String name = def.getName();
                    if (type == Field.TYPE_STRING) {
                        String value = newNode.getStringValue(name);
                        if (query==null) {
                            query = new NodeSearchQuery(bul);
                        }
                        StepField field = query.getField(def);
                        Constraint newConstraint = new BasicFieldValueConstraint(field, value);
                        if (constraint==null) {
                            constraint= newConstraint;
                        } else {
                            BasicCompositeConstraint compConstraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
                            compConstraint.addChild(constraint);
                            compConstraint.addChild(newConstraint);
                            constraint = compConstraint;
                        }
                    }
                }
            }
            if (query != null && constraint != null) {
                query.setConstraint(constraint);
                try {
                    List nodes = bul.getNodes(query);
                    if (nodes.size()>0) {
                        MMObjectNode oldNode = (MMObjectNode)nodes.get(0);
                        return oldNode.getIntValue("number");
                    }
                } catch (SearchQueryException sqe) {
                    result.error("Application installer can't search builder storage (" + sqe.getMessage()+")");
                    return -1;
                }
            }

            int localnumber = newNode.insert("import");
            if (localnumber == -1) {
                result.error("Insert of node " + newNode + " failed.");
            }
            return localnumber;

        } else {
            result.error("Application installer can't find builder for : " + newNode);
            return -1;
        }
    }

   /** update the field with the real node number of the referenced node
    *
    * @param syncbul syncnode builder
    * @param importedNode Node to update
    * @param exportsource export source of the node to update
    * @param fieldname name of the field
    */
   private void updateFieldWithTypeNode(
      MMObjectBuilder syncbul,
      MMObjectNode importedNode,
      String exportsource,
      String fieldname) {

      int exportnumber;
      try {
          exportnumber = Integer.parseInt((String) importedNode.getValues().get("__" + fieldname));
      } catch (Exception e) {
          exportnumber = -1;
      }

      // clean it up (don't know if this is necessary, but don't risk anything!)
      importedNode.storeValue("__" + fieldname, null);

      int localNumber = -1;

      List syncnodes = null;
      try {
          syncnodes = getSyncnodes(syncbul, exportsource, exportnumber);
      }
      catch (SearchQueryException e) {
          log.warn("Search for exportnumber " + exportnumber + " exportsource " + exportsource + "failed", e);
      }
      if (syncnodes != null && !syncnodes.isEmpty()) {
          MMObjectNode n2 = (MMObjectNode) syncnodes.get(0);
          localNumber = n2.getIntValue("localnumber");
      }
      if (localNumber != -1) { // leave it unset in that case, because foreign keys whine otherwise (so, if you have foreign keys (e.g. hsql), the field _must not_ be required).
          importedNode.setValue(fieldname, localNumber);
      }
   }

   /**
     * @javadoc
     */
    boolean installRelationSources(Vector ds, String appname, ApplicationResult result) {
        MMObjectBuilder syncbul = mmb.getMMObject("syncnodes");
        InsRel insRel = mmb.getInsRel();
        if (syncbul != null) {
            List nodeFieldNodes = new ArrayList(); // a temporary list with all nodes that have NODE fields, which should be synced, later.
            for (Enumeration h = ds.elements(); h.hasMoreElements();) {
                Hashtable bh = (Hashtable)h.nextElement();
                XMLRelationNodeReader nodereader = getRelationNodeReader(appname, bh);
                if (nodereader == null) {
                    continue;
                }
                else {
                    installRelationSource(syncbul, insRel, nodereader, nodeFieldNodes, result);
                }
            }
            treatNodeFields(nodeFieldNodes,syncbul);
        } else {
            result.error("Application installer : can't reach syncnodes builder");
        }
        return result.isSuccess();
    }

    private void installRelationSource(MMObjectBuilder syncbul, InsRel insRel, XMLRelationNodeReader nodereader, List nodeFieldNodes, ApplicationResult result) {
        String exportsource = nodereader.getExportSource();
        int timestamp = nodereader.getTimeStamp();

        for (Enumeration n = (nodereader.getNodes(mmb)).elements(); n.hasMoreElements();) {
            try {
                MMObjectNode newNode = (MMObjectNode)n.nextElement();
                int exportnumber = newNode.getIntValue("number");

                if (existsSyncnode(syncbul, exportsource, exportnumber)) {
                    // XXX To do : we may want to load the relation node and check/change the fields
                    log.debug("node allready installed : " + exportnumber);
                } else {
                    newNode.setValue("number", -1);
                    // The following code determines the 'actual' (synced) numbers for the destination and source nodes
                    // This will normally work well, however:
                    // It is _theoretically_ possible that one or both nodes are _themselves_ relation nodes.
                    // (since relations are nodes).
                    // Due to the order in which syncing takles place, it is possible that such structures will fail
                    // to get imported.
                    // ye be warned.

                    // find snumber
                    int snumber = newNode.getIntValue("snumber");
                    List snumberNodes = getSyncnodes(syncbul, exportsource, snumber);
                    if (!snumberNodes.isEmpty()) {
                        MMObjectNode n2 = (MMObjectNode)snumberNodes.get(0);
                        snumber = n2.getIntValue("localnumber");
                    } else {
                        snumber = -1;
                    }
                    newNode.setValue("snumber", snumber);

                    // find dnumber
                    int dnumber = newNode.getIntValue("dnumber");
                    List dnumberNodes = getSyncnodes(syncbul, exportsource, dnumber);
                    if (!dnumberNodes.isEmpty()) {
                        MMObjectNode n2 = (MMObjectNode)dnumberNodes.get(0);
                        dnumber = n2.getIntValue("localnumber");
                    } else {
                        dnumber = -1;
                    }
                    newNode.setValue("dnumber", dnumber);

                    int localnumber = -1;
                    if (snumber != -1 && dnumber != -1) {
                        // test whether a relation with the proposed snumber/dnumber/rnumber already exists
                        // if so, skip this relation when the same
                        if (relationAlreadyExists(insRel, newNode, snumber, dnumber)) {
                            log.warn("Application tries to add relation which already exists. " +
                                    "Skipping relation with exportnumber " + exportnumber);
                        }
                        else {
                            localnumber = newNode.insert("import");
                            if (localnumber != -1) {
                                createSyncnode(syncbul, exportsource, timestamp, exportnumber, localnumber);
                                if (localnumber == newNode.getNumber()) {
                                    findFieldsOfTypeNode(nodeFieldNodes, exportsource, newNode);
                                }
                            }
                        }
                    } else {
                        result.error("Cannot sync relation (exportnumber==" + exportnumber
                                + ", snumber:" + snumber + ", dnumber:" + dnumber + ")");
                    }
                }
            }
            catch (SearchQueryException sqe) {
                log.error(sqe);
            }
        }
    }

    private boolean existsSyncnode(MMObjectBuilder syncbul, String exportsource, int exportnumber) throws SearchQueryException {
        List nodes = getSyncnodes(syncbul, exportsource, exportnumber);
        return !nodes.isEmpty();
    }

    private List getSyncnodes(MMObjectBuilder syncbul, String exportsource, int exportnumber) throws SearchQueryException {
        NodeSearchQuery existQuery = new NodeSearchQuery(syncbul);
        BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(existQuery.getField(syncbul.getField("exportnumber")), new Integer(exportnumber));
        BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(existQuery.getField(syncbul.getField("exportsource")), exportsource);
        BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraint.addChild(constraint1);
        constraint.addChild(constraint2);
        existQuery.setConstraint(constraint);
        List nodes  = syncbul.getNodes(existQuery);
        if (nodes == null) {
            // could this happen?
            nodes = new ArrayList();
        }
        return nodes;
    }

    private void createSyncnode(MMObjectBuilder syncbul, String exportsource, int timestamp, int exportnumber, int localnumber) {
        MMObjectNode syncnode = syncbul.getNewNode("import");
        syncnode.setValue("exportsource", exportsource);
        syncnode.setValue("exportnumber", exportnumber);
        syncnode.setValue("timestamp", timestamp);
        syncnode.setValue("localnumber", localnumber);
        syncnode.insert("import");
    }

    /**
     * This method uses the {@link ResourceLoader} to fetch an application by name. for this purpose
     * it requests the resource by adding <code>applications/</code> to the start of the appName and appends <code>.xml</core> to the end
     * @param appName the name of the application to be read.
     * @return the ApplicationReader for the application, or null is the application wat not found or an exception occured. In the later a message is logged
     */
    private ApplicationReader getApplicationReader(String appName) {
        String resourceName = appName + ".xml";
        try {
            ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
            InputSource is = applicationLoader.getInputSource(resourceName);
            if (is == null) {
                return null;
            }
            return new ApplicationReader(is);
        } catch (Exception e) {
            log.error("error while reading application from resource " + resourceName  + " : " + e.getMessage() , e);
            return null;
        }
    }

    private XMLNodeReader getNodeReader(Map bh, String appName) {
        XMLNodeReader nodeReader = null;

        String path = (String) bh.get("path");
        ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
        InputSource is = null;
        try {
            is = applicationLoader.getInputSource(path);
        } catch (Exception e) {
            log.info("No datasource resource " + path);
        }
        if (is != null) {
            nodeReader = new XMLNodeReader(is, applicationLoader.getChildResourceLoader(appName));
        }
        return nodeReader;
    }

    private XMLRelationNodeReader getRelationNodeReader(String appname, Hashtable bh) {
        XMLRelationNodeReader nodereader = null;

        String path = (String)bh.get("path");
        ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
        InputSource is = null;
        try {
            is = applicationLoader.getInputSource(path);
        } catch (Exception e) {
            log.info("No relationsource resource " + path);
        }
        if (is != null) {
            nodereader = new XMLRelationNodeReader(is, applicationLoader.getChildResourceLoader(appname));
        }
        return nodereader;
    }

    private boolean relationAlreadyExists(InsRel insRel, MMObjectNode newNode, int snumber, int dnumber) {
        boolean relationAlreadyExists = false;
        MMObjectNode testNode = insRel.getRelation(snumber, dnumber, newNode.getIntValue("rnumber"));
        if (testNode != null) {
            relationAlreadyExists = true;
            Map values = newNode.getValues();
            for (Iterator iter = values.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String newFieldName = (String) entry.getKey();
                if (!insRel.hasField(newFieldName)) {
                    Object newValue = entry.getValue();
                    Object testValue = testNode.getValue(newFieldName);
                    if (!newValue.equals(testValue)) {
                        relationAlreadyExists = false;
                    }
                }
            }
        }
        return relationAlreadyExists;
    }

    /**
     * Checks and if required installs needed relation definitions.
     * Retrieves, for each reldef entry, the attributes, and passes these on to {@link #installRelDef}
     * @param reldefs a list of hashtables. Each hashtable represents a reldef entry, and contains a list of name-value
     *      pairs (the reldef attributes).
     * @return Always <code>true</code> (?)
     */
    private boolean installRelDefs(Vector reldefs, ApplicationResult result) {
        for (Enumeration h = reldefs.elements(); h.hasMoreElements();) {
            Hashtable bh = (Hashtable)h.nextElement();
            String source = (String)bh.get("source");
            String target = (String)bh.get("target");
            String direction = (String)bh.get("direction");
            String guisourcename = (String)bh.get("guisourcename");
            String guitargetname = (String)bh.get("guitargetname");
            // retrieve builder info
            int builder = -1;
            if (RelDef.usesbuilder) {
                String buildername = (String)bh.get("builder");
                // if no 'builder' attribute is present (old format), use source name as builder name
                if (buildername == null) {
                    buildername = (String)bh.get("source");
                }
                builder = mmb.getTypeDef().getIntValue(buildername);
            }
            // is not explicitly set to unidirectional, direction is assumed to be bidirectional
            if ("unidirectional".equals(direction)) {
                if (!installRelDef(source, target, 1, guisourcename, guitargetname, builder, result))
                    return false;
            } else {
                if (!installRelDef(source, target, 2, guisourcename, guitargetname, builder, result))
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks and if required installs needed allowed type relations.
     * Retrieves, for each allowed relation entry, the attributes, and passes these on to {@link #installTypeRel}
     * @param relations a list of hashtables. Each hashtable represents a allowedrelation entry, and contains a list of name-value
     *      pairs (the allowed relation attributes).
     * @return <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installAllowedRelations(Vector relations, ApplicationResult result) {
        for (Enumeration h = relations.elements(); h.hasMoreElements();) {
            Hashtable bh = (Hashtable)h.nextElement();
            String from = (String)bh.get("from");
            String to = (String)bh.get("to");
            String type = (String)bh.get("type");
            if (!installTypeRel(from, to, type, -1, result)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Lists the required builders for this application, and makes attempts to install any builders that are
     * not present.
     * If there is a failure, the function returns false.
     * Failure messages are stored in the lastmsg member.
     * @param neededbuilders a list with builder data that need be installed on teh system for this application to work
     *                       each element in teh list is a Map containing builder properties (in particular, 'name').
     * @param applicationRoot the rootpath where the application's configuration files are located
     * @return true if the builders were succesfully installed, false if the installation failed
     */
    private boolean installBuilders(List neededbuilders, String applicationRoot, ApplicationResult result) {
        for (Iterator i = neededbuilders.iterator(); i.hasNext();) {
            Map builderdata = (Map)i.next();
            String name = (String)builderdata.get("name");
            MMObjectBuilder bul = mmb.getMMObject(name);
            // if builder not loaded
            if (bul == null) {
                // if 'inactive' in the config/builder path, fail
                String path = mmb.getBuilderPath(name, "");
                if (path != null) {
                    result.error("The builder '" + name + "' was already on our system, but inactive." +
                                 "To install this application, make the builder '" + path + name + ".xml ' active");
                    continue;
                }
                ResourceLoader appLoader     = ResourceLoader.getConfigurationRoot().getChildResourceLoader(ResourceLoader.getDirectory(applicationRoot));
                ResourceLoader thisAppLoader = appLoader.getChildResourceLoader(ResourceLoader.getName(applicationRoot));
                ResourceLoader builderLoader = thisAppLoader.getChildResourceLoader("builders");

                // attempt to open the builder file.
                org.w3c.dom.Document config;
                try {
                    config = builderLoader.getDocument(name + ".xml");
                } catch (org.xml.sax.SAXException se) {
                    String msg = "builder '" + name + "':\n" + se.toString();
                    log.error(msg, se);
                    result.error("A XML parsing error occurred (" + se.toString() + "). Check the log for details.");
                    continue;
                } catch (java.io.IOException ioe) {
                    String msg = "builder '" + name + "':\n" + ioe.toString();
                    log.error(msg, ioe);
                    result.error("A file I/O error occurred (" + ioe.toString() + "). Check the log for details.");
                    continue;
                } catch (Throwable t) {
                    String msg = "builder '" + name + "': " + t.getMessage();
                    log.error(msg, t);
                    result.error("An error occured " + t.getClass() + " " + msg);
                    continue;
                }

                if (config == null) {
                    result.error("Could not find the builderfile :  '" + builderLoader.getResource(name + ".xml") + "' (builder '" + name + "')");
                    continue;
                }


                // check the presence of typedef (if not present, fail)
                MMObjectBuilder typeDef = mmb.getTypeDef();
                if (typeDef == null) {
                    return result.error("Could not find the typedef builder.");
                }
                try {
                    // try to add a node to typedef, same as adding a builder...
                    MMObjectNode typeNode = typeDef.getNewNode("system");
                    // fill the name....
                    typeNode.setValue("name", name);
                    typeNode.setValue("config", config);
                    // insert into mmbase
                    typeNode.insert("system");
                } catch (Exception e) {
                    result.error(e.getMessage());
                    continue;
                }
                // we now made the builder active.. look for other builders...
            }
        }
        return result.isSuccess();
    }

    /**
     * Checks whether a given relation definition exists, and if not, creates that definition.
     * @param sname source name of the relation definition
     * @param dname destination name of the relation definition
     * @param dir directionality (uni or bi)
     * @param sguiname source GUI name of the relation definition
     * @param dguiname destination GUI name of the relation definition
     * @param builder references the builder to use (only in new format)
     * @return <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installRelDef(String sname, String dname, int dir, String sguiname,
            String dguiname, int builder, ApplicationResult result) {

        RelDef reldef = mmb.getRelDef();
        if (reldef != null) {
            int rnumber = reldef.getNumberByName(sname );
            if (rnumber == -1) {
                MMObjectNode node = reldef.getNewNode("system");
                node.setValue("sname", sname);
                node.setValue("dname", dname);
                node.setValue("dir", dir);
                node.setValue("sguiname", sguiname);
                node.setValue("dguiname", dguiname);
                if (RelDef.usesbuilder) {
                    // if builder is unknown (falsely specified), use the InsRel builder
                    if (builder <= 0) {
                        builder = mmb.getInsRel().getNumber();
                    }
                    node.setValue("builder", builder);
                }
                int id = reldef.insert("system", node);
                if (id != -1) {
                    log.debug("RefDef (" + sname + "," + dname + ") installed");
                } else {
                    return result.error("RelDef (" + sname + "," + dname + ") could not be installed");
                }
            } else {
                MMObjectNode node = reldef.getNode(rnumber);
                String foundDname = node.getStringValue("dname");
                int foundBuilder = node.getIntValue("builder");
                if (! foundDname.equals(dname)) {
                    // MMB-1727
                    log.warn("Found already an reldef with this sname ('" + sname + "') but with different dname ('" + foundDname + "'). So not using " + sname + "/" + dname + " but " + sname + "/" + foundDname + ".");
                }
                if (foundBuilder != builder) {
                    result.error("Role '" + sname + "' already defined with different builder (" + foundBuilder + "), while this application required it to be " + builder);
                    return false;
                }
            }
        } else {
            return result.error("Can't get reldef builder");
        }
        return true;
    }

    /**
     * Checks and if required installs an allowed type relation (typerel object).
     * @param sname source type name of the type relation
     * @param dname destination type name of the type relation
     * @param rname role name of the type relation
     * @param count cardinality of the type relation
     * @return <code>true</code> if succesfull, <code>false</code> if an error occurred
     */
    private boolean installTypeRel(String sname, String dname, String rname, int count, ApplicationResult result) {
        TypeRel typerel = mmb.getTypeRel();
        if (typerel != null) {
            TypeDef typedef = mmb.getTypeDef();
            if (typedef == null) {
                return result.error("Can't get typedef builder");
            }
            RelDef reldef = mmb.getRelDef();
            if (reldef == null) {
                return result.error("Can't get reldef builder");
            }

            // figure out rnumber
            int rnumber = reldef.getNumberByName(rname);
            if (rnumber == -1) {
                return result.error("No reldef with role '" + rname + "' defined");
            }

            // figure out snumber
            int snumber = typedef.getIntValue(sname);
            if (snumber == -1) {
                return result.error("No builder with name '" + sname + "' defined");
            }

            // figure out dnumber
            int dnumber = typedef.getIntValue(dname);
            if (dnumber == -1) {
                return result.error("No builder with name '" + dname + "' defined");
            }

            if (!typerel.contains(snumber, dnumber, rnumber, TypeRel.STRICT)) {
                MMObjectNode node = typerel.getNewNode("system");
                node.setValue("snumber", snumber);
                node.setValue("dnumber", dnumber);
                node.setValue("rnumber", rnumber);
                node.setValue("max", count);
                int id = typerel.insert("system", node);
                if (id != -1) {
                    log.debug("TypeRel (" + sname + "," + dname + "," + rname + ") installed");
                } else {
                    return result.error("TypeRel (" + sname + "," + dname + "," + rname + ") could not be installed");
                }
            }
            return true;
        } else {
            return result.error("Can't get typerel builder");
        }
    }

}
