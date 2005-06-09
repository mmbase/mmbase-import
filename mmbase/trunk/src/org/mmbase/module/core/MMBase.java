/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.io.File;
import java.util.*;

import org.mmbase.clustering.MMBaseChangeDummy;
import org.mmbase.clustering.MMBaseChangeInterface;
import org.mmbase.module.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.security.MMBaseCop;
import org.mmbase.storage.*;
import org.mmbase.storage.search.SearchQueryException;
import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.platform.setUser;
import org.mmbase.util.xml.*;

/**
 * The module which provides access to the MMBase storage defined
 * by the provided name/setup.
 * It holds the overal object cloud made up of builders, objects and relations and
 * all the needed tools to use them.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @author Johannes Verelst
 * @version $Id: MMBase.java,v 1.131 2005-06-09 21:33:03 michiel Exp $
 */
public class MMBase extends ProcessorModule {

    /**
     * State of MMBase after shutdown
     * @since MMBase-1.7
     */
    private static final int STATE_SHUT_DOWN = -2;


    /**
     * State of MMBase at the beginning of startup
     * @since MMBase-1.6
     */
    private static final int STATE_START_UP = -1;
    /**
     * State of MMBase before builders are loaded
     * @since MMBase-1.6
     */
    private static final int STATE_LOAD = 0;
    /**
     * State of MMBase before builders are initialized
     * @since MMBase-1.6
     */
    private static final int STATE_INITIALIZE = 1;
    /**
     * State of MMBase after startup is completed
     * @since MMBase-1.6
     */
    private static final int STATE_UP = 2;

    // logging
    private static final Logger log = Logging.getLoggerInstance(MMBase.class);

    /**
     * Reference to the MMBase singleton. Used for quick reference by getMMBase();
     */
    private static MMBase mmbaseroot = null;

    /**
     * Time in seconds, when mmbase was started.
     * @since MMBase-1.7
     */
    public static final int startTime = (int) (System.currentTimeMillis() / 1000);

    /**
     * Builds a MultiCast Thread to receive and send
     * changes from other MMBase Servers.
     * @scope private
     */
    public MMBaseChangeInterface mmc;

    /**
     * Base name for the storage  to be accessed using this instance of MMBase.
     * Retrieved from the mmbaseroot module configuration setUser
     * If not specified the default is "def1"
     * Should be made private and accessed instead using getBaseName()
     * @scope private
     */
    public String baseName = "def1";

    /**
     * Reference to the TypeDef builder.
     */
    private TypeDef typeDef;
    /**
     * Reference to the RelDef builder.
     */
    private RelDef relDef;
    /**
     * Reference to the OALias builder.
     */
    private OAlias oAlias;
    /**
     * Reference to the InsRel builder.
     */
    private InsRel insRel;
    /**
     * Reference to the TypeRel builder.
     */
    private TypeRel typeRel;

    /**
     * The table that contains all loaded builders. Includes virtual builders.
     * Should be made private and accessed using getMMObjects()
     * @scope private
     */
    public Hashtable mmobjs = new Hashtable();

    /**
     * The (base)path to the builder configuration files
     */
    private ResourceLoader builderLoader;

    /**
     * @deprecated-now unused
     * @scope private
     */
    int delay;

    /**
     * @deprecated-now unused
     * @scope private
     */
    boolean nodecachesdone = false;

    /**
     * A thread object that gets activated by MMbase.
     * It activates every X seconds and takes this signal to call all the
     * builders probeCalls, using the callback {@link #doProbeRun} method in MMBase.
     * @scope private
     */
    MMBaseProbe probe;

    /**
     * Name of the machine used in the mmbase cluster.
     * it is used for the mmservers objects. Make sure that this is different
     * for each node in your cluster. This is not the machines dns name
     * (as defined by host as name or ip number).
     * @scope private
     */
    String machineName = "unknown";

    /**
     * The host or ip number of the machine this module is
     * running on. Its important that this name is set correctly because it is
     * used for communication between mmbase nodes and external devices
     * @scope private
     */
    String host = "unknown";

    /**
     * Authorisation type. Access using getAuthType()
     * @scope private
     */
    String authtype = "none";

    /**
     * Cookie domain (?). Access using getCookieDomain()
     * @scope private
     */
    String cookieDomain = null;

    /**
     * The storage manager factory to use. Retrieve using getStorageManagerFactory();
     */
    private StorageManagerFactory storageManagerFactory = null;

    /**
     * Reference to the Root builder (the most basic builder, aka 'object').
     * This can be null (does not exist) in older systems
     */
    private MMObjectBuilder rootBuilder;

    /**
     * Base url for the location of the DTDs. obtained using getDTDBase()
     * @deprecated
     */
    private String dtdbase = "http://www.mmbase.org";

    /**
     * our securityManager (MMBaseCop)
     */
    private MMBaseCop mmbaseCop = null;

    /**
     * Reference to the cluster builder, a virtual builder used to perform
     * multilevel searches.
     * @see ClusterBuilder
     */
    private ClusterBuilder clusterBuilder;

    /**
     * Currently used locale. Access using getLanguage()
     */
    private Locale locale = Locale.ENGLISH;

    /**
     * Currently used encoding. Access using getEncoding(). This
     * default to ISO-8859-1 as long as support for other encodings is
     * not thoroughly tested. In the feature we will probably switch
     * to UTF-8.
     *
     * @since MMBase-1.6
     */
    private String encoding = "ISO-8859-1";

    /**
     * MMbase 'up state. Access using getState()
     *
     * @since MMBase-1.6
     */
    private int mmbaseState = STATE_START_UP;

    /**
     * The table that indexes builders being loaded.
     * This map does not actually contian builders - it merely contains
     * a reference that the builder is in the process of being loaded.
     * The map is used to prevent circular references when extending builders.
     *
     * @since MMBase-1.6
     */
    private Set loading = new HashSet();

    /**
     * Constructor to create the MMBase root module.
     */
    public MMBase() {
        if (mmbaseroot != null) log.error("Tried to instantiate a second MMBase");
        log.debug("MMBase constructed");
    }

    /**
     * Initalizes the MMBase module. Evaluates the parameters loaded from the configuration file.
     * Sets parameters (authorisation, language), loads the builders, and starts MultiCasting.
     */
    public void init() {
        log.service("Init of " + org.mmbase.Version.get() + " (" + this + ")");

        // Set the mmbaseroot singleton var
        // This prevents recursion if MMBase.getMMBase() is called while
        // this method is run
        mmbaseroot = this;

        // is there a basename defined in MMBASE.properties ?
        String tmp = getInitParameter("BASENAME");
        if (tmp != null) {
            // yes then replace the default name (def1)
            baseName = tmp;
        } else {
            log.info("init(): No name defined for mmbase using default (def1)");
        }

        tmp = getInitParameter("AUTHTYPE");
        if (tmp != null && !tmp.equals("")) {
            authtype = tmp;
        }

        tmp = getInitParameter("LANGUAGE");
        if (tmp != null && !tmp.equals("")) {
            locale = new Locale(tmp, "");
        }
        log.info("MMBase default locale : " + locale);
        org.mmbase.util.LocalizedString.setDefault(locale);


        tmp = getInitParameter("ENCODING");
        if (tmp != null && !tmp.equals("")) {
            encoding = tmp;
        }

        tmp = getInitParameter("DTDBASE");
        if (tmp != null && !tmp.equals("")) {
            dtdbase = tmp;
        }

        tmp = getInitParameter("HOST");
        if (tmp != null && !tmp.equals("")) {
            host = tmp;
        }

        tmp = getInitParameter("COOKIEDOMAIN");
        if (tmp != null && !tmp.equals("")) {
            cookieDomain = tmp;
        }

        machineName = getInitParameter("MACHINENAME");

        log.debug("Starting JDBC module");

        // start the JDBC module if present
        getModule("JDBC", true);

        initializeClustering(getInitParameter("CLUSTERING"));

        String builderPath = getInitParameter("BUILDERFILE");
        if (builderPath == null || builderPath.equals("")) {
            builderPath = "builders";
        }
        log.debug("Builder path: " + builderPath);
        builderLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader(builderPath);

        mmbaseState = STATE_LOAD;

        log.debug("Loading builders:");

        loadBuilders();

        mmbaseState = STATE_INITIALIZE;

        log.service("Initializing  storage:");
        initializeStorage();

        log.debug("Checking MMBase");
        if (!checkMMBase()) {
            // there is no base defined yet, create the core objects
            createMMBase();
        }

        log.service("Initializing  builders:");
        initBuilders();

        log.debug("Objects started");

        String writerpath = getInitParameter("XMLBUILDERWRITERDIR");
        if (writerpath != null && !writerpath.equals("")) {
            Enumeration t = mmobjs.elements();
            while (t.hasMoreElements()) {
                MMObjectBuilder fbul = (MMObjectBuilder)t.nextElement();
                if (!fbul.isVirtual()) {
                    String name = fbul.getTableName();
                    log.debug("WRITING BUILDER FILE =" + writerpath + File.separator + name);
                    try {
                        BuilderWriter builderOut = new BuilderWriter(fbul);
                        builderOut.setIncludeComments(false);
                        builderOut.setExpandBuilder(false);
                        builderOut.writeToFile(writerpath + File.separator + fbul.getTableName() + ".xml");
                    } catch (Exception ex) {
                        log.error(Logging.stackTrace(ex));
                    }
                }
            }
        }

        // try to load security...
        try {
            mmbaseCop = new MMBaseCop();
        } catch (Exception e) {
            log.fatal("Error loading the mmbase cop: " + e.getMessage());
            log.error(Logging.stackTrace(e));
            log.error("MMBase will continue without security.");
            log.error("All future security invocations will fail.");
        }

        typeRel.readCache();

        // signal that MMBase is up and running
        mmbaseState = STATE_UP;
        log.info("MMBase is up and running");
        checkUserLevel();

    }

    /**
     * initialize Clustering
     * @param clusterClass classname of cluster manager
     * @since MMBase-1.7.2
     */
    private void initializeClustering(String clusterClass) {
        if (clusterClass != null) {
            log.debug("Starting Multicasting: " + clusterClass);

            Class newclass;
            try {
                newclass = Class.forName(clusterClass);
                mmc = (MMBaseChangeInterface) newclass.newInstance();
            } catch (Exception e) {
                log.error("Failed to start MMBaseChangeInterface: " + e.getMessage());
                mmc = new MMBaseChangeDummy();
            }
        } else {
            log.debug("Not starting MMBaseChangeInterface");
            mmc = new MMBaseChangeDummy();
        }
        mmc.init(this);
    }

    // javadoc inherited
    protected void shutdown() {
        mmbaseState = STATE_SHUT_DOWN;
    }

    /**
     * @since MMBase-1.7
     */
    public boolean isShutdown() {
        return  mmbaseState == STATE_SHUT_DOWN;
    }

    /**
     * Started when the module is loaded.
     * @deprecated-now unused
     */
    public void onload() {}

    /**
     * @deprecated-now unused
     */
    public void unload() {}

    /**
     * Checks whether the storage to be used exists.
     * The system determines whether the object table exists
     * for the baseName provided in the configuration file.
     * @return <code>true</code> if the storage exists and is accessible, <code>false</code> otherwise.
     */
    boolean checkMMBase() {
        return getStorageManager().exists();
    }

    /**
     * Loads the object builder if object.xml could be found.
     * @return The MMObjectBuilder 'object' or null, if no object.xml was found.
     * @since MMBase-1.7.1
     */
    private MMObjectBuilder loadRootBuilder() {
        if (rootBuilder != null) return rootBuilder;
        try {
            rootBuilder = loadBuilder("object");
        } catch (BuilderConfigurationException e) {
            // object builder was not defined -
            // builder is optional, so this is not an error
            rootBuilder = null;
        }
        return rootBuilder;
    }

    /**
     * Create a new MMBase persistent storage instance.
     * The storage instance created is based on the baseName provided in the configuration file.
     * This call automatically creates an object table.
     * The fields in the table are either specified in an object builder xml,
     * or from a default setup existing of a number field and a owner field.
     * Note: If specified, the object builder is instantiated and its table created, but
     * the builder is not registered in the TypeDef builder, as this builder does not exist yet.
     * Registration happens when the other builders are registered.
     * @return <code>true</code> if the storage was succesfully created, otherwise a runtime exception is thrown
     *   (shouldn't it return <code>false</code> instead?)
     */
    boolean createMMBase() {
        log.debug(" creating new multimedia base : " + baseName);
        getStorageManager().create();
        return true;
    }

    /**
     * Determines whether a builder is in the process of being loaded,
     * but not yet finished. Needed to track down circular references.
     * @return true if the builder is being loaded
     *
     * @since MMBase-1.6
     */
    private boolean builderLoading(String name) {
        return loading.contains(name);
    }

    /**
     * Retrieves a specified builder.
     * If the builder is not loaded, but the system is in the 'startup'  state
     * (i.e. it is in the process of loading builders), an attempt is made to
     * directly load the builder.
     * This allows for dependencies between builders to exist (i.e. inheritance).
     * When circular reference occurs between two loading buidlers, an exception is thrown.
     *
     * @since MMBase-1.6
     * @param name The name of the builder to retrieve
     * @return a <code>MMObjectBuilder</code> for the specified builder
     * @throws CircularReferenceException when circular reference is detected
     * @throws BuilderConfigurationException if the builder config file does not exist
     */
    public MMObjectBuilder getBuilder(String name) throws CircularReferenceException {
        MMObjectBuilder builder = getMMObject(name);
        if (builder == null && (mmbaseState == STATE_LOAD)) {
            // MM:  odd way to check this. Could it not be done a bit more explicitely?
            if (builderLoading(name)) {
                throw new CircularReferenceException("Circular reference to builder with name '" + name + "': currently loading " + loading);
            }
            builder = loadBuilder(name);
        }
        return builder;
    }

    /**
     * Retrieves a specified builder.
     * Note: may get deprecated in the future - use getBuilder instead.
     * @param name The name of the builder to retrieve
     * @return a <code>MMObjectBuilder</code> if found, <code>null</code> otherwise
     */
    public MMObjectBuilder getMMObject(String name) {
        Object o = mmobjs.get(name);
        if (o == null) {
            log.trace("MMObject " + name + " could not be found"); // can happen...
        }
        return (MMObjectBuilder) o;
    }

    /**
     * Retrieves the MMBase module('mmbaseroot').
     * @return the active MMBase module
     */
    public static MMBase getMMBase() {
        if (mmbaseroot == null) {
            mmbaseroot = (MMBase) getModule("mmbaseroot", true);
        }
        return mmbaseroot;
    }

    /**
     * Retrieves the loaded security manager(MMBaseCop).
     * @return the loaded security manager(MMBaseCop)
     */
    public MMBaseCop getMMBaseCop() {
        return mmbaseCop;
    }

    /**
     * Retrieves the loaded builders.
     * @return an <code>Enumeration</code> listing the loaded builders
     */
    public Enumeration getMMObjects() {
        return mmobjs.elements();
    }

    /**
     * Returns a reference to the InsRel builder.
     * @return the <code>InsRel</code> builder if defined, <code>null</code> otherwise
     */
    public InsRel getInsRel() {
        return insRel;
    }

    /**
     * Returns a reference to the RelDef builder.
     * @return the <code>RelDef</code> builder if defined, <code>null</code> otherwise
     */
    public RelDef getRelDef() {
        return relDef;
    }

    /**
     * Returns a reference to the TypeDef builder.
     * @return the <code>TypeDef</code> builder if defined, <code>null</code> otherwise
     */
    public TypeDef getTypeDef() {
        return typeDef;
    }

    /**
     * Returns a reference to the TypeRel builder.
     * @return the <code>TypeRel</code> builder if defined, <code>null</code> otherwise
     */
    public TypeRel getTypeRel() {
        return typeRel;
    }

    /**
     * Returns a reference to the OAlias builder.
     * @return the <code>OAlias</code> builder if defined, <code>null</code> otherwise
     */
    public OAlias getOAlias() {
        return oAlias;
    }

    /**
     * Returns a reference to the Object builder.
     * The Object builder is the builder from which all other builders eventually extend.
     * If the builder is not defined in the MMbase configuration, the system creates one.
     * @return the <code>Object</code> builder.
     * @since MMBase-1.6
     */
    public MMObjectBuilder getRootBuilder() {
        if (loadRootBuilder() == null) {
            log.info("No object.xml found, taking defaults.");
            // instantiate a virtual 'object' builder if none is specified
            rootBuilder = new MMObjectBuilder();
            rootBuilder.setMMBase(this);
            rootBuilder.setTableName("object");
            List fields = new ArrayList();
            // number field  (note: state = 'system')
            FieldDefs def = new FieldDefs("Object", "integer", 10, 10, "number", FieldDefs.TYPE_INTEGER, 1, FieldDefs.STATE_SYSTEM);
            def.setDBPos(1);
            def.setDBNotNull(true);
            def.setParent(rootBuilder);
            fields.add(def);
            // otype field
            def = new FieldDefs("Type", "integer", -1, -1, "otype", FieldDefs.TYPE_INTEGER, -1, FieldDefs.STATE_SYSTEM);
            def.setDBPos(2);
            def.setDBNotNull(true);
            def.setParent(rootBuilder);
            fields.add(def);
            // owner field
            def = new FieldDefs("Owner", "string", 11, 11, "owner", FieldDefs.TYPE_STRING, -1, FieldDefs.STATE_SYSTEM);
            def.setDBSize(12);
            def.setDBPos(3);
            def.setDBNotNull(true);
            def.setParent(rootBuilder);
            fields.add(def);
            rootBuilder.setFields(fields);
        }
        return rootBuilder;
    }

    /**
     * Returns the otype of the Object builder, or -1 if it is not known.
     * The Object builder is the builder from which all other builders eventually extend.
     * @since MMBase-1.6
     */
    public int getRootType() {
        if (rootBuilder == null) {
            return -1;
        } else {
            return rootBuilder.oType;
        }
    }

    /**
     * Returns a reference to the cluster builder, a virtual builder used to
     * perform multilevel searches.
     *
     * @return The cluster builder.
     * @see ClusterBuilder
     */
    public ClusterBuilder getClusterBuilder() {
        assertUp();
        return clusterBuilder;
    }

    /**
     * Locks until init of mmbase is finished.
     * @since MMBase-1.7
     */
    protected void assertUp() {
        if (! getState()) {
            synchronized(this) {
                // lock until up. (Init is synchronized on this too)
            }
        }
    }

    /**
     * Retrieves the storage base name
     * @return the base name as a <code>String</code>
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * Callback method, called from MMBaseProbe.
     * The probe is a seperate thread that is created every 10 minutes by the module's
     * {@link #maintainance} call.
     * @performance daymarks should be loaded at initialization.
     * @todo Evaluate whether presence of daymarks is required
     */
    public void doProbeRun() {
        DayMarkers bul = (DayMarkers)getMMObject("daymarks");
        if (bul != null) {
            bul.probe();
        } else {
            log.error("Can't access builder : daymarks");
        }
    }

    /**
     * Performs periodic maintenance.
     * Starts a separate thread that probes the builders by calling {@link #doProbeRun}.
     * The reference to the thread is cleared when it dies (scheduled every 10 minutes), prompting
     * the system to start a new thread.
     * @see MMBaseProbe
     */
    public void maintainance() {
        if (probe == null) {
            probe = new MMBaseProbe(this);
        }
    }

    /**
     * Converts a vector containing nodes to a hashmap,
     * using a specified (unique) integer field as the hash key.
     * @param se The vector containing the nodes
     * @param mapper the name of the (integer) field that determines the hash key, i.e. "number"
     * @return the node list mapped to a <code>Hashtable</code>
     */
    public Hashtable getSearchHash(Vector se, String mapper) {
        Hashtable results = new Hashtable();
        Enumeration t = se.elements();
        MMObjectNode node;
        while (t.hasMoreElements()) {
            node = (MMObjectNode)t.nextElement();
            results.put(new Integer(node.getIntValue(mapper)), node);
        }
        return results;
    }

    /**
     * Converts a vector containing nodes to a comma seperated list of values,
     * obtained from a specified integer field.
     * @param se The vector containing the nodes
     * @param mapper the name of the (integer) field whose value to include in the list
     * @return a parenthised, comma-seperated list of values, as a <code>String</code>
     */
    public String getWhereList(Vector se, String mapper) {
        if (se == null)
            return null;
        StringBuffer inlist = new StringBuffer();
        inlist.append(" (");
        Enumeration t = se.elements();
        MMObjectNode node;
        while (t.hasMoreElements()) {
            node = (MMObjectNode)t.nextElement();
            inlist.append(node.getIntValue(mapper) + ",");
        }
        if (inlist.length() >= 1)
            inlist.setLength(inlist.length() - 1);
        inlist.append(") ");
        return inlist.toString();
    }

    /**
     * Retrieves a reference to the sendmail module.
     * @deprecated use getModule("sendmail") instead
     *      SendMail will become a separate application.
     *      In MMBase 1.8. this method will be removed
     * @return a <code>SendMailInterface</code> object if the module was loaded, <code>null</code> otherwise.
     */
    public SendMailInterface getSendMail() {
        return  (SendMailInterface)getModule("sendmail");
    }

    /**
     * Retrieves the machine name.
     * This value is set using the configuration file.
     * @return the machine name as a <code>String</code>
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * Retrieves the host name or ip number
     * This value is set using the configuration file.
     * @return the host name as a <code>String</code>
     */
    public String getHost() {
        return host;
    }

    /**
     * Retrieves the cookiedomain (whatever that is)
     * This value is set using the configuration file.
     * @return the cookie domain as a <code>String</code>
     */
    public String getCookieDomain() {
        return cookieDomain;
    }

    /**
     * Adds a remote observer to a specified builder.
     * The observer is notified whenever an object of that builder is changed, added, or removed.
     * @return <code>true</code> if adding the observer succeeded, <code>false</code> otherwise.
     */
    public boolean addRemoteObserver(String type, MMBaseObserver obs) {
        MMObjectBuilder bul = getMMObject(type);
        if (bul != null) {
            return bul.addRemoteObserver(obs);
        } else {
            log.error("addRemoteObserver(): ERROR: Can't find builder : " + type);
            return false;
        }
    }

    /**
     * Adds a local observer to a specified builder.
     * The observer is notified whenever an object of that builder is changed, added, or removed.
     * @return <code>true</code> if adding the observer succeeded, <code>false</code> otherwise.
     */
    public boolean addLocalObserver(String type, MMBaseObserver obs) {
        MMObjectBuilder bul = getMMObject(type);
        if (bul != null) {
            return bul.addLocalObserver(obs);
        } else {
            log.error("addLocalObserver(): ERROR: Can't find builder : " + type);
            return false;
        }
    }

    /**
     * Returns the number of marked days from a specified daycount (?)
     * @deprecated SCAN related, should not be in this module.
     */
    public String doGetAgeMarker(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String age = tok.nextToken();
            try {
                int agenr = Integer.parseInt(age);
                int agecount = ((DayMarkers)getMMObject("daymarks")).getDayCountAge(agenr);
                return "" + agecount;
            } catch (Exception e) {
                log.debug(" Not a valid AGE");
                return "No valid age given";
            }
        } else {
            return "No age given";
        }
    }

    /**
     * Retrieves an unique key to use for a new node's number.
     * Calls the storage to request the key. <code>Sychronized</code> so the same number cannot be dealt out to different nodes.
     * Does possibly not work well with multiple mmbase systems that work on the same database.
     * @return the new unique key as an <code>int</code> value
     * @deprecated use getStorageManager().createKey()
     */
    public synchronized int getDBKey() {
        return getStorageManager().createKey();
    }

    /**
     * Retrieves a (mmbase) module by name.
     * @return the module as an <code>Object</code> if it exists, <code>null</code> otherwise
     * @deprecated-now Use {@link #getModule} instead
     */
    public Object getBaseModule(String name) {
        return getModule(name);
    }

    /**
     * @deprecated-now not used
     */
    public void stop() {}

    /**
     * Loads a core Builder.
     * If the builder  does not exist, an exception is thrown.
     * @since MMBase-1.6
     * @param name the name of the builder to load
     * @return the builder
     * @throws BuilderConfigurationException if the builder config file does not exist or is inactive
     */
    private MMObjectBuilder loadCoreBuilder(String name) {
        MMObjectBuilder builder = loadBuilder(name);
        if (builder == null) {
            throw new BuilderConfigurationException("The core builder " + name + " is mandatory but inactive.");
        } else {
            log.debug("Loaded core builder " + builder + " with otype " + builder.oType);
            return builder;
        }
    }


    /**
     *@since MMBase-1.7
     */
    private void loadBuilders() {
        // first load the core builders
        // remarks:
        //  - If nodescaches inactive, in init of typerel reldef nodes are created wich uses InsRel.oType, so typerel must be started after insrel and reldef. (bug #6237)

        loadRootBuilder(); // loads object.xml if present.

        typeDef = (TypeDef) loadCoreBuilder("typedef");
        relDef  = (RelDef)  loadCoreBuilder("reldef");
        insRel  = (InsRel)  loadCoreBuilder("insrel");
        typeRel = (TypeRel) loadCoreBuilder("typerel");





        try {
            oAlias = (OAlias)loadBuilder("oalias");
        } catch (BuilderConfigurationException e) {
            // OALias  builder was not defined -
            // builder is optional, so this is not an error
        }


        Set builders = builderLoader.getResourcePaths(ResourceLoader.XML_PATTERN, true/* recursive*/);

        log.info("Loading builders: " + builders);
        Iterator i = builders.iterator();
        while (i.hasNext()) {
            String builderXml  = (String) i.next();
            loadBuilderFromXML(ResourceLoader.getName(builderXml), ResourceLoader.getDirectory(builderXml) + "/");
        }

        log.debug("Starting Cluster Builder");
        clusterBuilder = new ClusterBuilder(this);
    }

    /**
     * Initializes the builders, using the builder xml files in the config directory
     * @return Always <code>true</code>
     */
    boolean initBuilders() {

        typeDef.init();

        // first initialize versions, if available (table must exist for quereis to succeed)
        log.debug("Versions:");
        Versions versions = (Versions)getMMObject("versions");
        if (versions != null) {
            versions.init();
        }

        relDef.init();
        insRel.init();
        typeRel.init();


        log.debug("mmobjects, inits");
        Iterator bi = mmobjs.entrySet().iterator();
        while (bi.hasNext()) {
            Map.Entry me = (Map.Entry)bi.next();
            MMObjectBuilder fbul = (MMObjectBuilder)me.getValue();
            log.debug("init " + fbul);
            try {
                initBuilder(fbul);
            } catch (BuilderConfigurationException e) {
                // something bad with this builder or its parents - remove it
                log.error("Removed builder " + fbul.getTableName() + " from the builderlist, as it cannot be initialized.");
                bi.remove();
            } catch (Exception ex) {
                log.error("Something went wrong while initializing builder " + fbul.getTableName());
                log.info("This builder will be removed from active builder list");
                log.error(Logging.stackTrace(ex));
                bi.remove();
            }
        }

        log.debug("**** end of initBuilders");
        return true;
    }

    /**
     * inits a builder
     * @param builder The builder which has to be initialized
     */
    public void initBuilder(MMObjectBuilder builder) {
        if (!builder.isVirtual()) {
            builder.init();
            typeDef.loadTypeDef(builder.getTableName());
            Versions versions = (Versions)getMMObject("versions");
            if (versions != null && versions.created()) {
                checkBuilderVersion(builder.getTableName(), versions);
            }
        }
    }

    /**
     * Unloads a builders from MMBase. After this, the builder is gone
     * @param builder the builder which has to be unloaded
     */
    public void unloadBuilder(MMObjectBuilder builder) {
        if (mmobjs.remove(builder.getTableName()) == null) {
            throw new RuntimeException("builder with name: " + builder.getTableName() + " could not be unloaded, since it was not loaded.");
        }
        if (!builder.isVirtual()) {
            typeDef.unloadTypeDef(builder.getTableName());
            log.info("unloaded builder with name:" + builder.getTableName());
        } else {
            log.info("unloaded virtual builder with name:" + builder.getTableName());
        }
    }


    /**
     * @since MMBase-1.8
     */
    public ResourceLoader getBuilderLoader() {
        return builderLoader;
    }


    /**
     * Locate one specific builder withing the main builder config path, including sub-paths.
     * If the builder already exists, the existing object is returned instead.
     * If the builder cannot be found in this path, a BuilderConfigurationException is thrown.
     * @since MMBase-1.6
     * @param builder name of the builder to initialize
     * @return the initialized builder object, or null if the builder could not be created (i.e. is inactive).
     * @throws BuilderConfigurationException if the builder config file does not exist
     */
    synchronized MMObjectBuilder loadBuilder(String builder) { // synchronized to make sure that storage initialized only once
        return loadBuilder(builder, "");
    }

    /**
     * Locate one specific builder within a given path, relative to the main builder config path, including sub-paths.
     * Return the actual path.
     * @param builder name of the builder to find
     * @param path the path to start searching. The path need be closed with a '/ character
     * @return the file path to the builder xml, or null if the builder could not be created (i.e. is inactive).
     * @throws BuilderConfigurationException if the builder config file does not exist
     * @todo The second argument (and perhaps the whole function) is silly, only exists because this
     *       function used to be implemented recursively (now delegated to ResourceLoader).
     */
    public String getBuilderPath(String builder, String path) {
        Set builders = builderLoader.getResourcePaths(java.util.regex.Pattern.compile(path + ResourceLoader.XML_PATTERN.pattern()), true /*recursive*/);
        Iterator i = builders.iterator();
        if (log.isDebugEnabled()) {
            log.debug("Found builder " + builders + " from " +  builderLoader  + " searching for " + builder);
        }
        String xml = builder + ".xml";
        while (i.hasNext()) {
            String builderXml = (String) i.next();
            if (builderXml.equals(xml)) {
                return "";
            } else if (builderXml.endsWith("/" + xml)) {
                return builderXml.substring(0, builderXml.length() - xml.length());
            }
        }
        return null;
    }



    /**
     * Locate one specific builder within a given path, relative to the main builder config path, including sub-paths.
     * If the builder already exists, the existing object is returned instead.
     * @param builder name of the builder to initialize
     * @param ipath the path to start searching. The path need be closed with a File.seperator character.
     * @return the initialized builder object, or null if the builder could not be created (i.e. is inactive).
     * @throws BuilderConfigurationException if the builder config file does not exist
     */
    MMObjectBuilder loadBuilder(String builder, String ipath) {
        MMObjectBuilder bul = getMMObject(builder);
        if (bul != null) {
            log.debug("Builder '" + builder + "' is already loaded");
            return bul;
        }
        String path = getBuilderPath(builder, ipath);
        if (path != null) {
            return loadBuilderFromXML(builder, path);
        } else {
            log.error("Cannot find specified builder " + builder);
            throw new BuilderConfigurationException("Cannot find specified builder " + builder);
        }
    }

    /**
     * Create a new builder object using a xml configfile located in a given path relative to the main builder config path,
     * and return the builder object.
     * If the builder already exists, the existing object is returned instead.
     * Note that the builder's init() method is NOT called (since some builders need other builders in memory when their init() is called,
     * this method is called seperately after all builders are loaded).
     * @deprecation-used uses deprecated buidedr methods, contains commented-out code
     * @param builder name of the builder to initialize
     * @param ipath the path to start searching. The path need be closed with a '/' character.
     * @return the loaded builder object.
     */
    public MMObjectBuilder loadBuilderFromXML(String builder, String ipath) {
        MMObjectBuilder bul = getMMObject(builder);
        if (bul != null) {
            log.debug("Builder '" + builder + "' is already loaded");
            return bul;
        }

        String objectName = builder; // should this allow override in file ?
        try {
            // register the loading of this builder
            loading.add(objectName);
            BuilderReader parser = new BuilderReader(builderLoader.getInputSource(ipath + builder + ".xml"), this);
            String status = parser.getStatus();
            if (status.equals("active")) {
                log.info("Starting builder : " + objectName);
                Class newclass;
                try {
                    String classname = parser.getClassFile();
                    newclass = Class.forName(classname);
                } catch (ClassNotFoundException cnfe) {
                    MMObjectBuilder p = parser.getParentBuilder();
                    if(p != null) {
                        newclass = p.getClass();
                    } else {
                        newclass = MMObjectBuilder.class;
                    }
                    log.error(cnfe.toString() + " Falling back to " + newclass.getName());
                }
                bul = (MMObjectBuilder)newclass.newInstance();

                mmobjs.put(objectName, bul);

                bul.setXMLPath(ipath);
                bul.setMMBase(this);
                bul.setTableName(objectName);

                // register the parent builder, if applicable
                MMObjectBuilder parent = parser.getParentBuilder();
                if (parent != null) {
                    bul.setParentBuilder(parent);
                } else if ((bul instanceof InsRel) && !objectName.equals("insrel")) {
                    bul.setParentBuilder(getInsRel());
                } else if (!objectName.equals("object")) {
                    bul.setParentBuilder(getRootBuilder());
                }

                Hashtable descriptions = parser.getDescriptions();
                bul.setDescriptions(descriptions);
                String desc = (String)descriptions.get(locale.getLanguage());
                // XXX" set description by builder?
                bul.setDescription(desc);
                bul.setSingularNames(parser.getSingularNames());
                bul.setPluralNames(parser.getPluralNames());
                bul.setVersion(parser.getBuilderVersion());
                bul.setMaintainer(parser.getBuilderMaintainer());
                bul.setSearchAge("" + parser.getSearchAge());
                bul.setInitParameters(parser.getProperties());
                bul.setFields(parser.getFields()); // temp  ?

                Iterator f = parser.getFunctions().iterator();
                while (f.hasNext()) {
                    org.mmbase.util.functions.Function func = (org.mmbase.util.functions.Function) f.next();
                    bul.addFunction(func);
                    log.service("Added " + func + " to " + bul);
                }
                // oke set the huge hack for insert layout
                // XXX: setDBLayout is deprecated
                //bul.setDBLayout(fields);

            }
        } catch (Exception e) { // what kind of exceptions are these?
            loading.remove(objectName);
            log.error(Logging.stackTrace(e));
            return null;
        }
        loading.remove(objectName);
        return bul;
    }

    /**
     * Retrieves the DTD base url.
     * This value is set using the configuration file.
     * @deprecated keesj: This method is not used. Document type definitions
     * should contain a fully qualified url.
     * I think the author was thinking of a dtdpath wich makes sence
     * @return the dtd base as a <code>String</code>
     */
    public String getDTDBase() {
        return dtdbase;
    }

    /**
     * Loads either the storage manager factory or the appropriate support class using the configuration parameters.
     * @since MMBase-1.7
     */
    protected void initializeStorage() {
        if (storageManagerFactory != null) return; // initialized allready
        log.service("Initializing storage");
        try {
            storageManagerFactory = StorageManagerFactory.newInstance(this);
            // print information about storage
            log.info("Using class: '" + storageManagerFactory.getClass().getName() + "'.");
        } catch (StorageException se) {
            log.error(se.getMessage());
            throw new StorageError();
        }
    }

    /**
     * Returns StorageManagerFactory class used to access the storage configuration.
     * @since MMBase-1.7
     * @return a StorageManagerFactory class, or <code>null</code> if not configured
     */
    public StorageManagerFactory getStorageManagerFactory() {
        return  storageManagerFactory;
    }

    /**
     * Returns a StorageManager to access the storage.. Equal to getStorageManagerFactory().getStorageManager().
     * @since MMBase-1.7
     * @return a StorageManager class
     * @throws StorageException if no storage manager could be instantiated
     */
    public StorageManager getStorageManager() throws StorageException {
        if (storageManagerFactory == null) {
            throw new StorageConfigurationException("Storage manager factory not configured.");
        } else {
            return storageManagerFactory.getStorageManager();
        }
    }

    /**
     * Returns a SearchQueryHandler to access the storage.. Equal to getStorageManagerFactory().getSearchQueryHandler().
     * @since MMBase-1.8
     * @return a StorageManager class
     * @throws StorageException if no storage manager could be instantiated
     */
    public SearchQueryHandler getSearchQueryHandler() throws StorageException {
        if (storageManagerFactory == null) {
            throw new StorageConfigurationException("Storage manager factory not configured.");
        } else {
            return storageManagerFactory.getSearchQueryHandler();
        }
    }

    /**
     * Loads a Node again, using its 'right' parent.
     * Reloading may retrieve extra fields if the original node was not loaded accurately.
     * @deprecated Not necessary in most cases, with the possible exception of lists obtained from InsRel.
     *   However, in the later case using this method is probably too costly.
     */
    public MMObjectNode castNode(MMObjectNode node) {
        /* fake because solved
         */
        int otype = node.getOType();
        String ename = typeDef.getValue(otype);
        if (ename == null) {
            return null;
        }
        MMObjectBuilder res = getMMObject(ename);
        return res.getNode(node.getNumber());
    }

    /**
     * Retrieves the autorisation type.
     * This value is set using the configuration file.
     * Examples are 'none' or 'basic'.
     * @return a <code>String</code> identifying the type
     */
    public String getAuthType() {
        return authtype;
    }

    /**
     * Retrieves the current language.
     * This value is set using the configuration file.
     * Examples are 'en' or 'nl'.
     * @return the language as a <code>String</code>
     */
    public String getLanguage() {
        return locale.getLanguage();
    }

    /**
     * Retrieves the current locale.
     * @since MMBase-1.8
     */
    public Locale getLocale() {
        return locale;
    }


    /**
     * Retrieves the encoding.
     * This value is set using the configuration file.
     * Examples are 'UTF-8' (default) or 'ISO-8859-1'.
     *
     * @return the coding as a <code>String</code>
     * @since  MMBase-1.6
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Retrieves whether this mmbase module is running.
     * @return <code>true</code> if the module has been initialized and all builders loaded, <code>false</code> otherwise.
     */
    public boolean getState() {
        return mmbaseState == STATE_UP;
    }

    /**
     * Checks and switches the user/grouplevel in which MMBase runs.
     * The userlevel is set using the -Dmmbase:userlevel=user:group commandline parameter.
     * Should probably be changed to <code>private</code>.
     */
    public void checkUserLevel() {
        String level = System.getProperty("mmbase.userlevel");
        if (level != null) {
            log.info("CheckUserLevel ->  mmmbase.userlevel=" + System.getProperty("mmbase.userlevel"));
            int pos = level.indexOf(':');
            if (pos != -1) {
                String user = level.substring(0, pos);
                String group = level.substring(pos + 1);
                setUser setuser = new setUser();
                setuser.setUserGroup(user, group);
            } else {
                log.info("CheckUserLevel ->  mmmbase.userlevel= not defined as user:group");
            }
        }
    }

    /**
     * Checks the builder version and, if needed, updates the version table.
     * Queries the xml files instead of the builder itself (?)
     * @return always <code>true</code>.
     */
    private boolean checkBuilderVersion(String buildername, Versions ver) {

        MMObjectBuilder tmp = (MMObjectBuilder)mmobjs.get(buildername);
        BuilderReader bapp;
        try {
            bapp = new BuilderReader(builderLoader.getInputSource(tmp.getXMLPath() + buildername + ".xml"), this);
        } catch (Exception e) {
            log.error(e);
            return false;
        }

        if (bapp != null) {
            int version = bapp.getBuilderVersion();
            String maintainer = bapp.getBuilderMaintainer();

            try {
                int installedversion = ver.getInstalledVersion(buildername, "builder");
                if (installedversion == -1 || version > installedversion) {
                    ver.setInstalledVersion(buildername, "builder", maintainer, version);
                }
            } catch (SearchQueryException e) {
                log.warn(Logging.stackTrace(e));
            }

        }
        return true;
    }



}
