/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import java.util.*;
import java.io.File;
import org.mmbase.util.xml.URIResolver;
import org.mmbase.applications.editwizard.SecurityException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.*;
/**
 * This struct contains configuration information for the jsps. This
 * thing is put in the session. A subclass 'Configurator' can be used
 * to fill this struct.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.6
 * @version $Id: Config.java,v 1.33 2003-05-27 11:17:09 pierre Exp $
 */

public class Config {

    /**
     * Default maximum upload size for files (4 MB). 
     */
    public final static int DEFAULT_MAX_UPLOAD_SIZE = 4 * 1024 * 1024;

    // logging
    private static Logger log = Logging.getLoggerInstance(Config.class.getName());

    // protocol string to test referrer pages
    private final static String PROTOCOL = "http://";

    public String      sessionKey        = null;
    public URIResolver uriResolver       = null;
    public int         maxupload = DEFAULT_MAX_UPLOAD_SIZE;
    public Stack       subObjects = new Stack(); // stores the Lists and Wizards.
    public String      sessionId;   // necessary if client doesn't accept cookies to store sessionid (this is appended to urls)
    public String      backPage;
    public String      templates;
    public String      language;

    static public class SubConfig {
        public boolean debug = false;
        public String wizard;
        public String page;
        public Map   popups     = new HashMap(); // all popups now in use below this (key -> Config)
        public Map    attributes = new HashMap();

        /**
         * Basic configuration. The configuration object passed is updated with information retrieved 
         * from the request object with which the configurator was created. The following parameters are accepted:
         *
         * <ul>
         *   <li>wizard</li>
         *   <li>origin</li>
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
                wizard = "file://" + configurator.getRequest().getRealPath(wizard);
            }
            setAttribute("origin",configurator.getParam("origin"));
            // debug parameter
            debug = configurator.getParam("debug",  debug);
        }

        public void setAttribute(String name, String value) {
            if (value!=null) {
                log.debug("storing "+name+" :"+value);
                attributes.put(name,value);
            }
        }
        
        /** 
         * Returns available attributes in a map, so they can be passed to the list stylesheet
         */
        public Map getAttributes() {
            Map attributeMap = new HashMap(attributes);
            if (wizard!=null) attributeMap.put("wizard", wizard);
            attributeMap.put("debug", ""+debug);
            return attributeMap;
        }

    }

    static public class WizardConfig extends SubConfig {
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
         * Calls {@link #baseConfig()} to read common parameters.
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
        public Map getAttributes() {
            Map attributeMap = super.getAttributes();
            attributeMap.put("popupid", popupId);
            if (objectNumber!=null) attributeMap.put("objectnumber", objectNumber);
            
            return attributeMap;   
        }
    }

    static public class ListConfig extends SubConfig {
        public String title;
        public File   template;
        public String fields;
        public String startNodes;
        public String nodePath;
        public String constraints;
        public String orderBy;
        public String directions;
        public String searchDir;

        public String searchFields;
        public String searchValue="";
        public String searchType="like";
        public String baseConstraints;
        public boolean forceSearch = false;

        public int    age = -1;
        public int    start = 0;
        public boolean distinct = false;
        public int pagelength   = 50;
        public int maxpagecount = 10;
        
        public boolean multilevel = false;
        public String mainObjectName = null;
        public List fieldList = null;
        
        private boolean parsed = false;
        
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
         *   <li>searchdir</li>
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
            title       = configurator.getParam("title", title);
            pagelength   = configurator.getParam("pagelength", new Integer(pagelength)).intValue();
            maxpagecount   = configurator.getParam("maxpagecount", new Integer(maxpagecount)).intValue();
            startNodes  = configurator.getParam("startnodes", startNodes);
            
            // Get nodepath parameter. if a (new) parameter was passed,
            // re-parse the node path and field list
            // This allows for custom list stylesheets to make a query more or less complex through 
            // user interaction
            String parameter  = configurator.getParam("nodepath");
            if (parameter!=null) {
                nodePath=parameter;
                parsed=false;
            }
            if (nodePath == null) {
                throw new WizardException("The parameter 'nodepath' is required but not given.");
            }
            
            // Get fields parameter. if a (new) parameter was passed,
            // re-parse the node path and field list
            // This allows for custom list stylesheets to make a query more or less complex through 
            // user interaction
            parameter  = configurator.getParam("fields");
            if (parameter!=null) {
                fields=parameter;
                parsed=false;
            }
            if (fields==null) {
                throw new WizardException("The parameter 'fields' is required but not given."); 
            }
            
            age         = configurator.getParam("age", new Integer(age)).intValue();
            if (age>=99999) age=-1;
            
            start       = configurator.getParam("start", new Integer(start)).intValue();
            searchType=configurator.getParam("searchtype", searchType);
            searchFields=configurator.getParam("searchfields", searchFields);
            searchValue=configurator.getParam("searchvalue", searchValue);
            searchDir=configurator.getParam("searchdir",searchDir);
            baseConstraints=configurator.getParam("constraints", baseConstraints);
            forceSearch=configurator.getParam("forcesearch", forceSearch);
            
            if (searchFields==null) {
                constraints = baseConstraints;
            } else {
                // search type: default
                String sType=searchType;
                // get the actual field to serach on.
                // this can be 'owner' or 'number' instead of the original list of searchfields,
                // in which case serachtype may change 
                String sFields=configurator.getParam("realsearchfield", searchFields);
                if (sFields.equals("owner") || sFields.endsWith(".owner")) {
                    sType="string";
                } else if (sFields.equals("number") || sFields.endsWith(".number")) {
                    sType="equals";
                }
                String search=searchValue;
                constraints=null;
                if (sType.equals("like")) {
                    // actually we should unquote search...
                    search=" LIKE '%"+search.toLowerCase()+"%'";
                } else if (sType.equals("string")) {
                    search=" = '"+search+"'";
                } else {
                    if (search.equals("")) {
                        search="0";
                    }
                    if (sType.equals("greaterthan")) {
                        search=" > "+search;
                    } else if (sType.equals("lessthan")) {
                        search=" < "+search;
                    } else if (sType.equals("notgreaterthan")) {
                        search=" <= "+search;
                    } else if (sType.equals("notlessthan")) {
                        search=" >= "+search;
                    } else if (sType.equals("notequals")) {
                        search=" != "+search;
                    } else { // equals
                        search=" = "+search;
                    }
                }
                StringTokenizer searchtokens= new StringTokenizer(sFields,",");
                while (searchtokens.hasMoreTokens()) {
                    String tok=searchtokens.nextToken();
                    if (constraints!=null) {
                        constraints+=" OR ";
                    } else {
                        constraints="";
                    }
                    if (sType.equals("like")) {
                        constraints+="lower(["+tok+"])"+search;
                    } else {
                        constraints+="["+tok+"]"+search;
                    }
                }
                if (baseConstraints!=null) {
                    constraints="("+baseConstraints+") and ("+constraints+")";
                }
            }
            searchDir  = configurator.getParam("searchdir", searchDir);
            directions  = configurator.getParam("directions", directions);
            orderBy     = configurator.getParam("orderby", orderBy);
            distinct    = configurator.getParam("distinct", new Boolean(true)).booleanValue();
            
            // only perform the following is there was no prior parsing
            if (!parsed) {

                String templatePath = configurator.getParam("template","xsl/list.xsl");
                template = configurator.resolveToFile(templatePath);

                // determine mainObjectName from main parameter
                mainObjectName = configurator.getParam("main",mainObjectName);
    
                // create fieldlist
                StringTokenizer stok = new StringTokenizer(fields, ",");
                int fieldcount = stok.countTokens();
                if (fieldcount == 0) {
                    throw new WizardException("The parameter 'fields' should be passed with a comma-separated list of fieldnames.");
                }
            
                fieldList = new ArrayList();
                while (stok.hasMoreTokens()) {
                    String token = stok.nextToken();
                    fieldList.add(token);
                    // Check if the number field for a multilevel object was specified 
                    // (determine mainObjectName from fieldlist)
                    if (mainObjectName == null && token.endsWith(".number")) {
                        mainObjectName = token.substring(0,token.length() - 7);
                    }
                }
    
                stok = new StringTokenizer(nodePath, ",");
                int nodecount = stok.countTokens();
                if (nodecount == 0) {
                    throw new WizardException("The parameter 'nodepath' should be passed with a comma-separated list of nodemanagers.");
                }
                multilevel = nodecount>1;
                if (mainObjectName == null) {
                    // search last manager - default 'main' object.
                    while (stok.hasMoreTokens()) {
                        mainObjectName = stok.nextToken();
                    }
                }
    
                // add the main object's numberfield to fields
                // this ensures the field is retrieved even if distinct weas specified
                String numberField = "number"; 
                if (multilevel) {
                    numberField = mainObjectName+".number";
                }
                if (fieldList.indexOf(numberField) == -1) {
                    fields = numberField + "," + fields;
                }
                parsed=true;
            }

        }

        /** 
         * Returns available attributes in a map, so they can be passed to the list stylesheet
         */
        public Map getAttributes() {
            Map attributeMap = super.getAttributes();
            // mandatory attributes
            attributeMap.put("nodepath", nodePath);
            attributeMap.put("fields", fields);
            // optional attributes
            if (title != null) attributeMap.put("title", title);
            attributeMap.put("age", age+"");
            if (multilevel) attributeMap.put("objecttype",mainObjectName);
            if (startNodes!=null) attributeMap.put("startnodes", startNodes);
            if (orderBy!=null) attributeMap.put("orderby", orderBy);
            if (directions!=null) attributeMap.put("directions", directions);
            attributeMap.put("distinct", distinct+"");
            if (searchDir!=null) attributeMap.put("searchdir", searchDir);
            if (baseConstraints!=null) attributeMap.put("constraints", baseConstraints);
            // search attributes
            if (searchType!=null) attributeMap.put("searchtype", searchType);
            if (searchFields!=null) attributeMap.put("searchfields", searchFields);
            if (searchValue!=null) attributeMap.put("searchvalue", searchValue);
            
            return attributeMap;   
        }
    }

    /**
     * To fill the Config struct, this 'Configurator' exists. You
     * could extend it to change wich query parameters must be used,
     * and what are the defaults and so on.
     */
    public static class Configurator {
        private static Logger log = Logging.getLoggerInstance(Config.class.getName());

        protected HttpServletRequest request;
        protected HttpServletResponse response;
        private Config config;

        public HttpServletRequest getRequest() {
            return request;
        }

        public File resolveToFile(String templatePath) {
            return config.uriResolver.resolveToFile(templatePath);
        }
        
        public Configurator(HttpServletRequest req, HttpServletResponse res, Config c) throws WizardException {
            request = req;
            response = res;
            config  = c;

            config.sessionId = res.encodeURL("test.jsp").substring(8);
            log.debug("Sessionid : " + config.sessionId);

            

            if (config.language == null) {
                config.language = getParam("language", org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getDefaultLocale().getLanguage());
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
                File refFile;
                // capture direct reference of http:// and shttp:// referers
                int protocolPos= config.backPage.indexOf(PROTOCOL);

                if (protocolPos >=0 ) { // given absolutely
                    String path =  config.backPage.substring(config.backPage.indexOf('/', protocolPos + PROTOCOL.length()));
                    // Using URL.getPath() would be nicer, but is not available in java 1.2
                    // suppose it is from the same server, web can find back the directory then:
                    refFile = new File(request.getRealPath(path.substring(request.getContextPath().length()))).getParentFile();

                    // TODO: What if it happened to be not from the same server?
                } else {
                    // Was given relatively, that's easy:
                    refFile = new File(request.getRealPath(config.backPage)).getParentFile();
                }
                if (refFile.exists()) {
                    extraDirs.add("ref:", refFile);
                }

                /* Optionally, you can indicate with a 'templates' option where the xml's and 
                   xsl must be searched (if they cannot be found in the referring dir).
                */
                config.templates = request.getParameter("templates");

                if (config.templates != null) {
                    File templatesDir = new File(request.getRealPath(config.templates));
                    try {
                        templatesDir = templatesDir.getCanonicalFile();
                    } catch (java.io.IOException e) {
                        throw new WizardException(e.toString());
                    }
                    if(! templatesDir.isDirectory()) {
                        throw new WizardException("Template directory not found : " + templatesDir);
                    }
                    extraDirs.add("templates:", templatesDir);
                }

                /**
                 * Then of course also the directory of editwizard installation must be added. This will allow for the 'basic' xsl's to be found,
                 * and also for 'library' editors.
                 */

                File jspFileDir = new File(request.getRealPath(request.getServletPath())).getParentFile(); // the directory of this jsp (list, wizard)
                File basedir    = new java.io.File(jspFileDir.getParentFile().getAbsolutePath(), "data"); // ew default data/xsls is in ../data then

                if (! config.language.equals("")) {
                    File i18n = new File(basedir, "i18n" + File.separator + config.language);
                    if (i18n.isDirectory()) {
                        extraDirs.add("i18n:", i18n);
                    } else {
                        if (! "en".equals(config.language)) { // english is default anyway
                            log.warn("Tried to internationalize the editwizard for language " + config.language + " for which support is lacking (" + i18n + " is not an existing directory)");
                        }
                    }
                }

                extraDirs.add("ew:", basedir);
                config.uriResolver = new URIResolver(jspFileDir, extraDirs);
                config.maxupload = getParam("maxsize", config.maxupload);
            }
        }
        
        protected String getParam(String paramName) {
            if (request.getParameter(paramName) == null) return null;
            return request.getParameter(paramName);
        }

        protected String getParam(String paramName, String def) {
            if (request.getParameter(paramName) == null) {
                if (def == null) return null;
                return def.toString();
            }
            return getParam(paramName);
        }
        
        protected int  getParam(String paramName, int def) {
            String i = request.getParameter(paramName);
            if (i == null || i.equals("")) return def;
            return new Integer(i).intValue();
        }

        protected Integer getParam(String paramName, Integer def) {
            String i = request.getParameter(paramName);
            if (i == null || i.equals("")) return def;
            return new Integer(i);
        }

        protected boolean getParam(String paramName, boolean def) {
            if (request.getParameter(paramName) == null) return def;
            return new Boolean(request.getParameter(paramName)).booleanValue();
        }

        protected Boolean getParam(String paramName, Boolean def) {
            if (request.getParameter(paramName) == null) return def;
            return new Boolean(request.getParameter(paramName));
        }
        public String getBackPage(){
            if(config.subObjects.size() == 0) {
                return config.backPage;
            } else {
                return ((SubConfig) config.subObjects.peek()).page;
            }
        }

        public  ListConfig createList() {
            ListConfig l = new ListConfig();
            l.page = response.encodeURL(request.getServletPath() + "?proceed=yes");
            return l;
        }
        
        public Config.WizardConfig createWizard(Cloud cloud) throws SecurityException, WizardException {
            WizardConfig wizard = new WizardConfig();
            wizard.page = response.encodeURL(request.getServletPath() + "?proceed=yes");
            config(wizard); // determine the objectnumber and assign the wizard name.
            // wizard should now have a name!
            if (wizard.wizard == null) throw new WizardException("Wizardname may not be null, conigurated by class with name: " + this.getClass().getName());
            wizard.wiz = new Wizard(request.getContextPath(), config.uriResolver, wizard, cloud);
            wizard.wiz.setSessionId(config.sessionId);
            wizard.wiz.setSessionKey(config.sessionKey);
            wizard.wiz.setReferrer(config.backPage);
            wizard.wiz.setTemplatesDir(config.templates);
            return wizard;
        }

        /**
         * Configure a list or wizard. The configuration object passed is updated with information retrieved 
         * from the request object with which the configurator was created.
         * @since MMBase-1.6.4
         * @param config the configuration object for the list or wizard.
         */
        public void config(Config.SubConfig c) throws WizardException {
            c.configure(this);
        }

    }

}
