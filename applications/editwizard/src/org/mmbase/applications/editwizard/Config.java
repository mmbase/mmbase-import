/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.*;
import java.net.*;
import java.io.*;
import org.mmbase.util.xml.URIResolver;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.ResourceLoader;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.util.Encode;

/**
 * This struct contains configuration information for the jsps. This
 * thing is put in the session. A subclass 'Configurator' can be used
 * to fill this struct.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: Config.java,v 1.74 2008-11-15 12:46:30 michiel Exp $
 */

public class Config implements java.io.Serializable {

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    /**
     * Default maximum upload size for files (4 MB).
     */
    public final static int DEFAULT_MAX_UPLOAD_SIZE = 4 * 1024 * 1024;

    private static final Logger log = Logging.getLoggerInstance(Config.class);

    // protocol string to test referrer pages
    private final static String PROTOCOL = "http://";

    // default values

    public static final String CONFIG_FILE = "editwizard.xml";

    public static String wizardStyleSheet = "xsl/wizard.xsl";
    public static String listStyleSheet = "xsl/list.xsl";
    public static String searchlistStyleSheet = "xsl/searchlist.xsl";
    public static long maxUploadSize = DEFAULT_MAX_UPLOAD_SIZE;

    /**
     * @since MMBase-1.8.1
     */
    private static final UtilReader reader = new UtilReader(CONFIG_FILE,
                                                     new Runnable() {
                                                         public void run() {
                                                             readConfiguration(reader.getProperties());
                                                         }
                                                     });
    static {
        readConfiguration(reader.getProperties());
    }

    synchronized static void readConfiguration(Map configuration) {
        String tmp = (String) configuration.get("wizardStyleSheet");
        if (tmp != null && !tmp.equals("")) {
            wizardStyleSheet = tmp;
            log.service("Editwizard default wizard style sheet "    + wizardStyleSheet);
        }
        tmp = (String) configuration.get("listStyleSheet");
        if (tmp != null && !tmp.equals("")) {
            listStyleSheet = tmp;
            log.service("Editwizard default list style sheet "    + listStyleSheet);
        }
        tmp = (String) configuration.get("searchlistStyleSheet");
        if (tmp != null && !tmp.equals("")) {
            searchlistStyleSheet = tmp;
            log.service("Editwizard default searchlist style sheet "    + searchlistStyleSheet);
        }
        tmp = (String) configuration.get("maxUploadSize");
        if (tmp != null && !tmp.equals("")) {
            try {
                maxUploadSize = Long.parseLong(tmp);
            } catch (Exception e) {}
            log.service("Editwizard default max upload size "    + maxUploadSize);
        }

    }

    public String sessionKey = null;
    public URIResolver uriResolver = null;
    public long maxupload = Config.maxUploadSize;
    public Stack<SubConfig> subObjects = new Stack<SubConfig>(); // stores the Lists and Wizards.
    public String sessionId;   // necessary if client doesn't accept cookies to store sessionid (this is appended to urls)
    public String backPage;
    public String templates;
    public String language;
    public String timezone;

    /**
     * Contains all auxiliary attributes to the first page. Using this map, they can be found in
     * sub pages as well.
     *
     * @since MMBase-1.7
     */
    protected Map<String, Object> attributes;



    //   public String context; (contained in attributes now)

    static public class SubConfig implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        public boolean debug = false;
        public String wizard;
        public String page;
        public HashMap<String, Stack<SubConfig>> popups = new HashMap<String, Stack<SubConfig>>(); // all popups now in use below this (key -> Config)

        public HashMap<String, Object> attributes = new HashMap<String, Object>();

        /**
         * Basic configuration. The configuration object passed is updated with information retrieved
         * from the request object with which the configurator was created. The following parameters are accepted:
         *
         * <ul>
         *   <li>wizard</li>
         //*   <li>origin</li>
         //*   <li>context</li>
         *   <li>debug</li>
         * </ul>
         *
         * @since MMBase-1.6.4
         * @param configurator the configurator containing request information
         * @throws WizardException if expected parameters were not given or ad bad content
         */
        public void configure(Config.Configurator configurator) throws WizardException  {
            wizard = configurator.getParam("wizard", wizard);
            if (wizard != null && wizard.startsWith("/")) {
                wizard = configurator.getResource(wizard).toString();
            }
            configurator.fillAttributes(attributes);

            // contained in Config#attributes now
            // setAttribute("origin", configurator.getParam("origin"));
            // setAttribute("context", configurator.getContext());
            // debug parameter
            debug = configurator.getParam("debug",  debug);
        }

        /*
        public void setAttribute(String name, String value) {
            if (value != null) {
                log.debug("storing "+name+" :"+value);
                attributes.put(name,value);
            }
        }
        */
        /**
         * Returns available attributes in a map, so they can be passed to the list stylesheet
         */
        public Map<String, Object> getAttributes() {
            Map<String, Object> attributeMap = new HashMap<String, Object>(attributes);
            return attributeMap;
        }

    }

    static public class WizardConfig extends SubConfig {
        private static final long serialVersionUID = 1L;
        public Wizard wiz;
        public String objectNumber;
        public String parentFid;
        public String parentDid;
        public String popupId;

        /**
         * Configure a wizard. The configuration object passed is updated with information retrieved
         * from the request object with which the configurator was created. The following parameters are accepted:
         *
         * <ul>
         *   <li>popupid</li>
         *   <li>objectnumber</li>
         * </ul>
         *
         * @since MMBase-1.6.4
         * @param configurator the configurator containing request information
         * @throws WizardException if expected parameters were not given
         */
        public void configure(Config.Configurator configurator) throws WizardException {
            super.configure(configurator);
            popupId = configurator.getParam("popupid",  "");
            objectNumber = configurator.getParam("objectnumber");
        }

        /**
         * Returns available attributes in a map, so they can be passed to the list stylesheet
         */
        /*
        public Map getAttributes() {
            Map attributeMap = super.getAttributes();
            attributeMap.put("popupid", popupId);
            if (objectNumber!=null) attributeMap.put("objectnumber", objectNumber);

            return attributeMap;
        }
        */
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    static public class ListConfig extends SubConfig {
        private static final long serialVersionUID = 2L;

        // constants for 'search' parameter. Order of value matters (force must be bigger then yes)
        public static final int SEARCH_NO   = 0;

        public static final int SEARCH_AUTO = 5; // search if searchfields given.

        public static final int SEARCH_YES  = 10;
        public static final int SEARCH_FORCE  = 11; // like 'yes', but searching occurs only if not searching empty string.


        public String title;
        public transient URL    template;
        public String fields;
        public String startNodes;
        public String nodePath;
        public String constraints;
        public String orderBy;
        public String directions;
        public String searchDir;

        public String searchFields;
        public String realSearchField;
        public String searchValue="";
        public String searchType="like";
        public String baseConstraints;
        public int    search = SEARCH_AUTO;

        public int    age = -1;
        public int    start = 0;
        public boolean distinct = false;
        public int pagelength   = 50;
        public int maxpagecount = 10;

        public boolean multilevel = false;
        public String mainObjectName = null;
        public List<String> fieldList = null;

        protected Cloud cloud;

        ListConfig(Cloud cloud) {
            this.cloud = cloud;
        }

        /**
         * @deprecated
         */
        ListConfig() { // for backwards compatibility
            this.cloud = null;
        }

        private boolean parsed = false;

        protected static String removeDigits(String complete) {
            int end = complete.length() - 1;
            while (Character.isDigit(complete.charAt(end))) {
                --end;
            }
            return complete.substring(0, end + 1);
        }

        /**
         * Configure a list page. The configuration object passed is updated with information retrieved
         * from the request object with which the configurator was created. The following parameters are accepted:
         *
         * <ul>
         *   <li>title</li>
         *   <li>pagelength</li>
         *   <li>maxpagecount</li>
         *   <li>startnodes</li>
         *   <li>fields</li>
         *   <li>age</li>
         *   <li>start</li>
         *   <li>searchtype</li>
         *   <li>searchfields</li>
         *   <li>searchvalue</li>
         *   <li>searchdir</li>
         *   <li>constraints</li>
         *   <li>forcesearch</li>
         *   <li>realsearchfield</li>
         *   <li>directions</li>
         *   <li>orderby</li>
         *   <li>distinct</li>
         * </ul>
         *
         * @since MMBase-1.6.4
         * @param configurator the configurator containing request information
         */
        public void configure(Config.Configurator configurator) throws WizardException {
            super.configure(configurator);
            title        = configurator.getParam("title", title);
            pagelength   = configurator.getParam("pagelength", Integer.valueOf(pagelength));
            maxpagecount = configurator.getParam("maxpagecount", Integer.valueOf(maxpagecount));
            startNodes   = configurator.getParam("startnodes", startNodes);

            // Get nodepath parameter. if a (new) parameter was passed,
            // re-parse the node path and field list
            // This allows for custom list stylesheets to make a query more or less complex through
            // user interaction
            String parameter  = configurator.getParam("nodepath");
            if (parameter != null) {
                nodePath = parameter;
                parsed = false;
            }
            if (nodePath == null) {
                throw new WizardException("The parameter 'nodepath' is required but not given.");
            }

            // Get fields parameter. if a (new) parameter was passed,
            // re-parse the node path and field list
            // This allows for custom list stylesheets to make a query more or less complex through
            // user interaction
            parameter  = configurator.getParam("fields");
            if (parameter != null) {
                fields = parameter;
                parsed = false;
            }
            if (fields == null) {
                //throw new WizardException("The parameter 'fields' is required but not given.");
                log.debug("The parameter 'fields' is  not given, going to take the first field");
                // this will happen during parsing.

            }

            age = configurator.getParam("age", Integer.valueOf(age));
            if (age >= 99999) age=-1;

            start           = configurator.getParam("start", Integer.valueOf(start));
            searchType      = configurator.getParam("searchtype", searchType);
            searchFields    = configurator.getParam("searchfields", searchFields);
            searchValue     = configurator.getParam("searchvalue", searchValue);
            searchDir       = configurator.getParam("searchdir", searchDir);
            searchDir       = configurator.getParam("searchdirs", searchDir);
            baseConstraints = configurator.getParam("constraints", baseConstraints);
            String searchString =  configurator.getParam("search", (String) null);
            if (searchString != null) {
                searchString = searchString.toLowerCase();
                if (searchString.equals("auto")) {
                    search = SEARCH_AUTO;
                } else if (searchString.equals("no")) {
                    search = SEARCH_NO;
                } else if (searchString.equals("yes")) {
                    search = SEARCH_YES;
                } else if (searchString.equals("force")) {
                    search = SEARCH_FORCE;
                } else {
                    throw new WizardException("Unknown value for search parameter '" + searchString + "'");
                }
            } else {
                log.debug("Search is null?");
            }

            /// what the heck is this.
            realSearchField = configurator.getParam("realsearchfield", realSearchField);

            if (searchFields == null) {
                constraints = baseConstraints;
            } else {
                StringBuilder constraintsBuffer;
                // search type: default
                String sType = searchType;
                // get the actual field to search on.
                // this can be 'owner' or 'number' instead of the original list of searchfields,
                // in which case searchtype may change
                String sFields = realSearchField;
                if (sFields == null) sFields = searchFields;
                if (sFields.equals("owner") || sFields.endsWith(".owner")) {
                    sType = "like";
                } else if (sFields.equals("number") || sFields.endsWith(".number")) {
                    sType = "equals";
                }
                String where = Encode.encode("ESCAPE_SINGLE_QUOTE", searchValue);
                constraintsBuffer = null;
                if (sType.equals("like")) {
                    if (! "".equals(where)) {
                        where = " LIKE '%" + where.toLowerCase() + "%'";
                    }
                } else if (sType.equals("string")) {
                    if (! "".equals(where)) {
                        where = " = '" + where + "'";
                    }
                } else {
                    if (! "".equals(where)) {
                        if (! org.mmbase.datatypes.StringDataType.DOUBLE_PATTERN.matcher(where).matches()) {
                            where = "0";
                        }
                        if (sType.equals("greaterthan")) {
                            where = " > " + where;
                        } else if (sType.equals("lessthan")) {
                            where = " < " + where;
                        } else if (sType.equals("notgreaterthan")) {
                            where = " <= " + where;
                        } else if (sType.equals("notlessthan")) {
                            where = " >= " + where;
                        } else if (sType.equals("notequals")) {
                            where = " != " + where;
                        } else { // equals
                            where = " = " + where;
                        }
                    }
                }
                if (! "".equals(where)) {
                    StringTokenizer searchTokens= new StringTokenizer(sFields, ",");
                    while (searchTokens.hasMoreTokens()) {
                        String tok = searchTokens.nextToken();
                        if (constraintsBuffer != null) {
                            constraintsBuffer.append(" OR ");
                        } else {
                            constraintsBuffer = new StringBuilder();
                        }
                        if (sType.equals("like")) {
                            constraintsBuffer.append("lower([").append(tok).append("])").append(where);
                        } else {
                            constraintsBuffer.append('[').append(tok).append(']').append(where);
                        }
                    }
                }
                if (baseConstraints != null) {
                    if (constraintsBuffer != null) {
                        constraints = "(" + baseConstraints + ") and (" + constraintsBuffer.toString() + ")";
                    } else {
                        constraints = baseConstraints;
                    }
                } else {
                    if (constraintsBuffer != null) {
                        constraints = constraintsBuffer.toString() ;
                    } else {
                        constraints = null;
                    }
                }
            }
            searchDir   = configurator.getParam("searchdir",  searchDir);
            directions  = configurator.getParam("directions", directions);
            orderBy     = configurator.getParam("orderby",    orderBy);
            distinct    = configurator.getParam("distinct",   distinct);

            // only perform the following is there was no prior parsing
            if (!parsed) {
                String defaultTemplate = Config.listStyleSheet;
                if ("search".equals(configurator.getParam("listtype"))) {
                     defaultTemplate =Config.searchlistStyleSheet;
                }
                String templatePath = configurator.getParam("template", defaultTemplate);
                try {
                    template = configurator.resolveToURL(templatePath);
                } catch (Exception e) {
                    throw new WizardException(e);
                }

                // determine mainObjectName from main parameter
                mainObjectName = configurator.getParam("main", (String) null); // mainObjectName);

                boolean mainPresent = mainObjectName != null;

                // parse the nodePath.
                StringTokenizer stok = new StringTokenizer(nodePath, ",");
                int nodecount = stok.countTokens();
                if (nodecount == 0) {
                    throw new WizardException("The parameter 'nodepath' should be passed with a comma-separated list of nodemanagers.");
                }
                multilevel = nodecount > 1;
                if (mainObjectName == null) {
                    // search last manager - default 'main' object.
                    while (stok.hasMoreTokens()) {
                        mainObjectName = stok.nextToken();
                    }
                }
                // now we always have a mainObjectName already (the last from nodePath)

                // so we can make up a nice default for fields.
                if (fields == null) {
                    if (cloud != null) {
                        StringBuilder fieldsBuffer = new StringBuilder();
                        FieldIterator i = cloud.getNodeManager(removeDigits(mainObjectName)).getFields(org.mmbase.bridge.NodeManager.ORDER_LIST).fieldIterator();
                        while (i.hasNext()) {
                            Field field = i.nextField();
                            if (multilevel && field.isVirtual()) {
                                // cannot be queried any way.
                                // these fields are directly added the query. You could perhaps deterin virtual fields afterwards.
                                // should perhaps also be valid for monolevels.
                                continue;
                            }
                            fieldsBuffer.append(multilevel ? mainObjectName + "." : "" ).append(field.getName());
                            if (i.hasNext()) fieldsBuffer.append(',');
                        }
                        fields = fieldsBuffer.toString();
                    } else {
                        // the list.jsp _does_ provide a cloud, but well, perhaps people have old list.jsp's?
                        throw new WizardException("The parameter 'fields' is required but not given (or make sure there is a cloud)");
                    }
                }

                // create fieldlist
                stok = new StringTokenizer(fields, ",");
                if (stok.countTokens() == 0) {
                    throw new WizardException("The parameter 'fields' should be passed with a comma-separated list of fieldnames.");
                }

                fieldList = new ArrayList<String>();
                while (stok.hasMoreTokens()) {
                    String token = stok.nextToken();
                    fieldList.add(token);
                    // Check if the number field for a multilevel object was specified
                    // (determine mainObjectName from fieldlist)

                    // MM: so, there are several ways to specify the 'main' object.
                    // 1. defaults to last in nodePath
                    // 2. with 'main' parameter
                    // 3. with the first 'number' field of the fields parameter.

                    // I think 2 & 3 serve the same goal and 3 must be deprecated.

                    if (! mainPresent && token.endsWith(".number")) {
                        mainObjectName = token.substring(0, token.length() - 7);
                        mainPresent = true;
                        // Only to avoid reentering this 'if'. Of course the 'main' parameter actually is still not present.
                    }
                }

                if (search >= SEARCH_YES && searchFields == null) {
                    if (cloud != null) {
                        StringBuilder searchFieldsBuffer = new StringBuilder();
                        FieldIterator i = cloud.getNodeManager(removeDigits(mainObjectName)).
                            getFields(org.mmbase.bridge.NodeManager.ORDER_LIST).fieldIterator();
                        while (i.hasNext()) {
                            Field f = i.nextField();
                            if (f.getType() == Field.TYPE_STRING && ! f.getName().equals("owner")) {
                                if (searchFieldsBuffer.length() > 0) searchFieldsBuffer.append(',');
                                searchFieldsBuffer.append(multilevel ? mainObjectName + "." : "" ).append(f.getName());
                            }
                        }
                        searchFields = searchFieldsBuffer.toString();

                    } else {
                        // the list.jsp _does_ provide a cloud, but well, perhaps people have old list.jsp's?
                        throw new WizardException("Cannot auto-determin search-fields without a cloud (use a newer list.jsp");
                    }
                }

                if (search == SEARCH_NO && searchFields != null) {
                    log.debug("Using searchfields and explicitiy no search");
                    searchFields = null;
                }

                // add the main object's numberfield to fields
                // this ensures the field is retrieved even if distinct weas specified
                String numberField = "number";
                if (multilevel) {
                    numberField = mainObjectName + ".number";
                }
                if (fieldList.indexOf(numberField) == -1) {
                    fields = numberField + "," + fields;
                }
                parsed = true;
            }


        }
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeUTF(template.toString());
        }
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            String u = in.readUTF();
            template = ResourceLoader.getWebRoot().getResource(u);
        }

        /**
         * Returns available attributes in a map, so they can be passed to the list stylesheet
         */
        public Map<String, Object> getAttributes() {
            Map<String, Object> attributeMap = super.getAttributes();
            // mandatory attributes
            attributeMap.put("nodepath", nodePath);
            attributeMap.put("fields",   fields);
            // optional attributes
            if (title           != null) attributeMap.put("title",       title);
            attributeMap.put("age", age + "");
            if (multilevel             ) attributeMap.put("objecttype",  mainObjectName);
            if (startNodes      != null) attributeMap.put("startnodes",  startNodes);
            if (orderBy         != null) attributeMap.put("orderby",     orderBy);
            if (directions      != null) attributeMap.put("directions",  directions);
            attributeMap.put("distinct", distinct + "");
            if (searchDir       != null) attributeMap.put("searchdir",   searchDir);
            if (baseConstraints != null) attributeMap.put("constraints", baseConstraints);
            // search attributes
            if (searchType      != null) attributeMap.put("searchtype",  searchType);
            if (searchFields    != null) attributeMap.put("searchfields",    searchFields);
            if (realSearchField != null) attributeMap.put("realsearchfield", realSearchField);
            if (searchValue     != null) attributeMap.put("searchvalue",     searchValue);

            return attributeMap;
        }
    }

    /**
     * To fill the Config struct, this 'Configurator' exists. You
     * could extend it to change wich query parameters must be used,
     * and what are the defaults and so on.
     */
    public static class Configurator {

        protected PageContext page;
        protected HttpServletRequest request;
        protected HttpServletResponse response;
        private   Config config;

        public Configurator(PageContext pageContext, Config c) throws WizardException, java.net.MalformedURLException {
            page = pageContext;
            request = (HttpServletRequest)page.getRequest();
            response = (HttpServletResponse)page.getResponse();
            config  = c;

            config.sessionId = response.encodeURL("test.jsp").substring(8);
            if (log.isDebugEnabled()) {
                log.debug("Sessionid : " + config.sessionId);
            }

            if (config.language == null) {
                config.language = getParam("language", org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale().getLanguage());
            }

            if (config.timezone == null) {
                config.timezone = getParam("timezone", "");
            }

            /*
              // contained in config.attributes now
            if (config.context == null) {
                config.context = getParam("context");
            }
            */
            if (config.attributes == null) {
                config.attributes = new HashMap<String, Object>();
                fillAttributes(config.attributes);
            }
            // The editwizard need to know the 'backpage' (for 'index' and 'logout' links).
            // It can be specified by a 'referrer' parameter. If this is missing the
            // 'Referer' http header is tried.
            if (config.backPage == null) {
                log.debug("No backpage. Getting from parameters");
                config.backPage = org.mmbase.util.Encode.decode("ESCAPE_URL_PARAM", getParam("referrer", "")).replace('\\', '/'); // this translations seems to be needed by some windows setups

                if (config.backPage.equals("")) {
                    log.debug("No backpage getting from header");
                    config.backPage = request.getHeader("Referer");
                }
                if (config.backPage == null) {
                    log.debug("No backpage setting to ''");
                    config.backPage = "";
                }
            }

            // if no 'uriResolver' is configured yet, then there is one created right now:
            // the uriResolver is used to find xml's and xsl's.
            if (config.uriResolver == null) {
                if (log.isDebugEnabled()) {
                    log.trace("creating uriresolver (backpage = " + config.backPage + ")");
                }
                URIResolver.EntryList extraDirs = new URIResolver.EntryList();

                /* Determin the 'referring' page, and add its directory to the URIResolver.
                   That means that xml can be placed relative to this page, and xsl's int xsl-dir.
                 */
                URL ref;
                // capture direct reference of http:// and https:// referers
                int protocolPos= config.backPage.indexOf(PROTOCOL);




                if (protocolPos >= 0 ) { // given absolutely
                    String path = new URL(config.backPage).getPath();
                    ref = new URL(getResource(path.substring(request.getContextPath().length())), ".");
                    // TODO: What if it happened to be not from the same server?
                } else {
                    // Was given relatively, that's trickie, because cannot use URL object to take of query.
                    String bp = config.backPage;
                    int questionPos = bp.indexOf('?');
                    if (questionPos != -1) {
                        bp = bp.substring(0, questionPos);
                    }
                    URL path = getResource(bp);

                    if (path != null) {
                        ref = new URL(path, ".");
                    } else {
                        ref = null;
                    }
                }
                if (ref != null) {
                    if (! config.language.equals("")) {
                        URL refi18n = new URL(ref, "i18n/" + config.language + "/");
                        if (getResource(refi18n.getPath()) != null) {
                            extraDirs.add("refi18n:", refi18n);
                        }
                    }
                    extraDirs.add("ref:", ref);
                } else {
                    log.warn("" + ref + " does not exist");
                }

                /* Optionally, you can indicate with a 'templates' option where the xml's and
                   xsl must be searched (if they cannot be found in the referring dir).
                */
                config.templates = request.getParameter("templates");

                if (config.templates != null) {
                    URL templatesDir = getResource(config.templates);
                    if (templatesDir == null) {
                        throw new WizardException("" +  config.templates + " does not exist");
                    }
                    if (! config.language.equals("")) {
                        URL templatesi18n = new URL(templatesDir, "i18n/" + config.language + "/");
                        if (getResource(templatesi18n.getPath()) != null) {
                            extraDirs.add("templatesi18n:", templatesi18n);
                        }
                    }
                    extraDirs.add("templates:", templatesDir);
                }

                /**
                 * Then of course also the directory of editwizard installation must be added. This will allow for the 'basic' xsl's to be found,
                 * and also for 'library' editors.
                 */

                URL jspFileDir = new URL(getResource(request.getServletPath()), "."); // the directory of this jsp (list, wizard)
                URL basedir    = new URL(jspFileDir, "../data/");                      // ew default data/xsls is in ../data then

                if (! config.language.equals("")) {
                    URL i18n = new URL(basedir, "i18n/" + config.language + "/");
                    if (i18n == null) {
                        if (! "en".equals(config.language)) { // english is default anyway
                            log.warn("Tried to internationalize the editwizard for language " + config.language + " for which support is lacking (" + i18n + " is not an existing directory)");
                        }
                    } else {
                        extraDirs.add("i18n:", i18n);
                    }
                }

                extraDirs.add("ew:", basedir);
                URL rootDir = new URL(getResource(request.getServletPath()), "/"); // the directory of this jsp (list, wizard)
                extraDirs.add("root:" , rootDir);
                config.uriResolver = new URIResolver(jspFileDir, extraDirs);
                config.maxupload = getParam("maxsize", config.maxupload);
            }
        }

        public URL getResource(String path) {
            return ResourceLoader.getWebRoot().getResource(path);
        }


        public URL resolveToURL(String templatePath) throws javax.xml.transform.TransformerException {
            return config.uriResolver.resolveToURL(templatePath, null);
        }


        public PageContext getPage() {
            return page;
        }

        protected String getParam(String paramName) {
            return request.getParameter(paramName);
        }

        protected String getParam(String paramName, String defaultValue) {
            String value = getParam(paramName);
            if (value == null) value = defaultValue;
            return value;
        }

        protected int  getParam(String paramName, int def) {
            String i = getParam(paramName);
            if (i == null || i.equals("")) return def;
            return Integer.parseInt(i);
        }
        protected long  getParam(String paramName, long def) {
            String i = getParam(paramName);
            if (i == null || i.equals("")) return def;
            return Long.parseLong(i);
        }

        protected Integer getParam(String paramName, Integer def) {
            String i = getParam(paramName);
            if (i == null || i.equals("")) return def;
            return Integer.parseInt(i);
        }

        protected boolean getParam(String paramName, boolean def) {
            String b = getParam(paramName);
            if (b == null) return def;
            return Boolean.valueOf(b).booleanValue();
        }

        protected Boolean getParam(String paramName, Boolean def) {
            String b = getParam(paramName);
            if (b == null) return def;
            return Boolean.valueOf(b);
        }

        /**
         * Fills the given map with all attributes from the URI, but first all attributes from the
         * first call are added.  No arrays supported, only single values.
         * @since MMBase-1.7
         */
        protected void fillAttributes(Map<String, Object> map) {
            map.putAll(config.attributes);  // start with setting in global config

            Enumeration<String> e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String param = e.nextElement();
                map.put(param, request.getParameter(param));
            }

            // map.putAll(request.getParameterMap()); key -> String[] (not useable)
            // log.info(map);
        }

        public String getBackPage(){
            if(config.subObjects.size() == 0) {
                return config.backPage;
            } else {
                return (config.subObjects.peek()).page;
            }
        }


        public  ListConfig createList(Cloud cloud) {
            ListConfig l = new ListConfig(cloud);
            l.page = response.encodeURL(request.getServletPath() + "?proceed=yes");
            return l;
        }

        /**
         * @deprecated use createList(cloud)
         */
        public  ListConfig createList() {
            return createList(null);
        }

        public Config.WizardConfig createWizard(Cloud cloud) throws WizardException {
            WizardConfig wizard = new WizardConfig();
            wizard.page = response.encodeURL(request.getServletPath() + "?proceed=yes");
            config(wizard); // determine the objectnumber and assign the wizard name.
            // wizard should now have a name!
            if (wizard.wizard == null) {
                throw new WizardException("Wizardname may not be null, configurated by class with name: " + this.getClass().getName());
            }
            wizard.wiz = new Wizard(request, config.uriResolver, wizard, cloud);
            wizard.wiz.setSessionId(config.sessionId);
            wizard.wiz.setSessionKey(config.sessionKey);
            wizard.wiz.setReferrer(config.backPage);
            wizard.wiz.setTemplatesDir(config.templates);
            wizard.wiz.setTimezone(config.timezone);
            return wizard;
        }

        /**
         * Configure a list or wizard. The configuration object passed is updated with information retrieved
         * from the request object with which the configurator was created.
         * @since MMBase-1.6.4
         * @param config the configuration object for the list or wizard.
         */
        public void config(Config.SubConfig config) throws WizardException {
            config.configure(this);
        }

    }

}
