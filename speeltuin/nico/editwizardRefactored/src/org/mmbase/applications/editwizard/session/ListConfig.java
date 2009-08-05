package org.mmbase.applications.editwizard.session;

import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.util.HttpUtil;
import org.mmbase.bridge.*;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.URIResolver;


public class ListConfig extends AbstractConfig {

    private static final Logger log = Logging.getLoggerInstance(ListConfig.class);

    // constants for 'search' parameter. Order of value matters (force must be bigger then yes)
    public static final int SEARCH_NO   = 0;

    public static final int SEARCH_AUTO = 5; // search if searchfields given.

    public static final int SEARCH_YES  = 10;
    public static final int SEARCH_FORCE  = 11; // like 'yes', but searching occurs only if not searching empty string.


    private String title;
    private URL    template;
    private String fields;
    private String startNodes;
    private String nodePath;
    private String constraints;
    private String orderBy;
    private String directions;
    private String searchDir;

    private String searchFields;
    private String realSearchField;
    private String searchValue="";
    private String searchType="like";
    private String baseConstraints;
    private int    search = SEARCH_AUTO;

    private int    age = -1;
    private int    start = 0;
    private boolean distinct = false;
    private int pagelength   = 50;
    private int maxpagecount = 10;

    private boolean multilevel = false;
    private String mainObjectName = null;
    private List fieldList = null;

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
     * @param controller the configurator containing request information
     */
    public void configure(HttpServletRequest request, Cloud cloud, URIResolver uriResolver) throws WizardException {
        super.configure(request, uriResolver);
        title        = HttpUtil.getParam(request, "title", title);
        pagelength   = HttpUtil.getParam(request, "pagelength", new Integer(pagelength)).intValue();
        maxpagecount = HttpUtil.getParam(request, "maxpagecount", new Integer(maxpagecount)).intValue();
        startNodes   = HttpUtil.getParam(request, "startnodes", startNodes);

        // Get nodepath parameter. if a (new) parameter was passed,
        // re-parse the node path and field list
        // This allows for custom list stylesheets to make a query more or less complex through
        // user interaction
        String parameter  = HttpUtil.getParam(request, "nodepath");
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
        parameter  = HttpUtil.getParam(request, "fields");
        if (parameter != null) {
            fields = parameter;
            parsed = false;
        }
        if (fields == null) {
            //throw new WizardException("The parameter 'fields' is required but not given.");
            log.debug("The parameter 'fields' is  not given, going to take the first field");
            // this will happen during parsing.

        }

        age = HttpUtil.getParam(request, "age", new Integer(age)).intValue();
        if (age >= 99999) age=-1;

        start           = HttpUtil.getParam(request, "start", new Integer(start)).intValue();
        searchType      = HttpUtil.getParam(request, "searchtype", searchType);
        searchFields    = HttpUtil.getParam(request, "searchfields", searchFields);
        searchValue     = HttpUtil.getParam(request, "searchvalue", searchValue);
        searchDir       = HttpUtil.getParam(request, "searchdir", searchDir);
        searchDir       = HttpUtil.getParam(request, "searchdirs", searchDir);
        baseConstraints = HttpUtil.getParam(request, "constraints", baseConstraints);
        String searchString =  HttpUtil.getParam(request, "search", (String) null);
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
        realSearchField = HttpUtil.getParam(request, "realsearchfield", realSearchField);

        if (searchFields == null) {
            constraints = baseConstraints;
        } else {
            StringBuffer constraintsBuffer;
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
            String where = Encode.encode("ESCAPE_SINGLE_QUOTE",searchValue);
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
                if (where.equals("")) {
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
            if (! "".equals(where)) {
                StringTokenizer searchTokens= new StringTokenizer(sFields, ",");
                while (searchTokens.hasMoreTokens()) {
                    String tok = searchTokens.nextToken();
                    if (constraintsBuffer != null) {
                        constraintsBuffer.append(" OR ");
                    } else {
                        constraintsBuffer = new StringBuffer();
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
        searchDir   = HttpUtil.getParam(request, "searchdir",  searchDir);
        directions  = HttpUtil.getParam(request, "directions", directions);
        orderBy     = HttpUtil.getParam(request, "orderby",    orderBy);
        distinct    = HttpUtil.getParam(request, "distinct",   false);

        // only perform the following is there was no prior parsing
        if (!parsed) {
            String templatePath = HttpUtil.getParam(request, "template", "xsl/list.xsl");
            try {
                template = uriResolver.resolveToURL(templatePath, null);
            } catch (Exception e) {
                throw new WizardException(e);
            }

            // determine mainObjectName from main parameter
            mainObjectName = HttpUtil.getParam(request, "main", (String) null); // mainObjectName);

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
                    StringBuffer fieldsBuffer = new StringBuffer();
                    FieldIterator i = cloud.getNodeManager(removeDigits(mainObjectName)).
                        getFields(org.mmbase.bridge.NodeManager.ORDER_LIST).fieldIterator();
                    while (i.hasNext()) {
                        fieldsBuffer.append(multilevel ? mainObjectName + "." : "" ).append(i.nextField().getName());
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

            fieldList = new ArrayList();
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
                    StringBuffer searchFieldsBuffer = new StringBuffer();
                    FieldIterator i = cloud.getNodeManager(removeDigits(mainObjectName)).
                        getFields(NodeManager.ORDER_LIST).fieldIterator();
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
        if (realSearchField!=null) attributeMap.put("realsearchfield", realSearchField);
        if (searchValue!=null) attributeMap.put("searchvalue", searchValue);

        return attributeMap;
    }


    /**
     * @return Returns the age.
     */
    public int getAge() {
        return age;
    }


    /**
     * @return Returns the template.
     */
    public URL getTemplate() {
        return template;
    }


    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return Returns the constraints.
     */
    public String getConstraints() {
        return constraints;
    }


    /**
     * @return Returns the mainObjectName.
     */
    public String getMainObjectName() {
        return mainObjectName;
    }


    /**
     * @return Returns the maxpagecount.
     */
    public int getMaxpagecount() {
        return maxpagecount;
    }


    /**
     * @return Returns the multilevel.
     */
    public boolean isMultilevel() {
        return multilevel;
    }


    /**
     * @return Returns the directions.
     */
    public String getDirections() {
        return directions;
    }


    /**
     * @return Returns the distinct.
     */
    public boolean isDistinct() {
        return distinct;
    }


    /**
     * @return Returns the fieldList.
     */
    public List getFieldList() {
        return fieldList;
    }


    /**
     * @return Returns the fields.
     */
    public String getFields() {
        return fields;
    }


    /**
     * @return Returns the nodePath.
     */
    public String getNodePath() {
        return nodePath;
    }


    /**
     * @return Returns the orderBy.
     */
    public String getOrderBy() {
        return orderBy;
    }


    /**
     * @return Returns the pagelength.
     */
    public int getPagelength() {
        return pagelength;
    }


    /**
     * @return Returns the start.
     */
    public int getStart() {
        return start;
    }


    /**
     * @param constraints The constraints to set.
     */
    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }


    /**
     * @return Returns the search.
     */
    public int getSearch() {
        return search;
    }


    /**
     * @return Returns the searchDir.
     */
    public String getSearchDir() {
        return searchDir;
    }


    /**
     * @return Returns the searchFields.
     */
    public String getSearchFields() {
        return searchFields;
    }


    /**
     * @return Returns the searchType.
     */
    public String getSearchType() {
        return searchType;
    }


    /**
     * @return Returns the searchValue.
     */
    public String getSearchValue() {
        return searchValue;
    }


    /**
     * @return Returns the startNodes.
     */
    public String getStartNodes() {
        return startNodes;
    }

}

