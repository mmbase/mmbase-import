/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.cache.Cache;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.functions.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.jumpers.JumperCalculatorInterface;


/**
 * This class will calculate urls and is called from the {@link org.mmbase.servlet.servjumpers} servlet.<br />
 *<br /> 
 * There are 2 types of jumpers:<br />
 *<br />
 *  [1] http://www.yoursite.com/name<br />
 *  [2] http://www.yoursite.com/objectnumber<br />
 *<br />
 *<br />
 * [1] jumper with a name<br />
 * --- ------------------<br />
 * These are the classical jumpers and can go internally or externally.<br />
 * 
 * One could specify a jumper with a name 'books' and a url which goes<br />
 * to something like /books/index.jsp Think of them as aliases for urls.<br />
 *<br />
 *<br />
 * [2] jumper as objectnumber<br />
 * --- ----------------------<br />
 * These are the jumpers which refer to a certain objectnumber. <br />
 *<br />
 * By overriding the {@link org.mmbase.module.builders.core.MMObjectBuilder#getDefaultUrl} method one can make <br />
 * certain objects jump to certain subsites. For example the Movies-builder could let all the Movie-nodes<br />
 * jump to '/movies/movie.jsp?movie=number'. More intelligent builders can make nodes jump based on the <br />
 * objects they are related to. For example the Items-builder can make Items related to Groups to <br />
 * somewhere different than Items related to Movies. <br />
 *<br />
 * The jumpers builder can be configured using two properties:<br />
 *<ul>
 *<li><code>JumperCacheSize</code> determines the size of the jumper cache (in nr of items).
 *                                The default size is 1000.</li>
 *<li><code>JumperNotFoundURL</code> Determines the default url (such as a home page or error page)
 *             when no jumper is found. If not specified nothing will be done if no jumper is found.</li>
 *</ul>
 * 
 * This implementation has the following enhancements:
 * 
 * - calculating jumpers is now threadsafe
 *   This means that while the jumper is calculated, all other requests for that jumper are blocked
 * 
 * - better support for multiple servers
 *   When a 
 * 
 * Flow:
 *  
 * There are 2 flows in the jumpers, one comes from servdb, which is the call from the server
 * to fetch the url for the given jumper. The second comes from the machine editing the nodes,
 * which signals by means of nodeLocalChanged and nodeRemoteChanged that a node is changed.
 * 
 * The first one (call from server) will insert the url into the cache, the nodeRemoteChange 
 * will trigger a deleteFromMemCache and a nodeLocalChange will trigger a deleteFromMemChache
 * and deleteFromDatabaseCache. This can be the same machine, but ithat does not have to be the case.
 * So the servers will only call getJump and the edit-machine will get its signal from nodeChange.
 * This has the effect that only nodes get cached which are used as jumpers, but it also creates
 * the need for the jumperbuilder to watch over all builders which are used as jumpers. 
 * So when a builder implements the getDefaultUrl, it has to subscribe jumpers as a observer or
 * changes are lost and a invalid node is cached. 
 * 
 *  
 *  
 *<br />
 * @author Marcel Maatkamp
 * @version $Version$
 */

public class Jumpers extends MMObjectBuilder implements MMBaseObserver {

    private static final Logger log = Logging.getLoggerInstance(Jumpers.class);

    /**
     * Default Jump Cache Size.
     * Customization can be done through the central caches.xml
     * Make an entry under the name "JumpersCache" with the size you want.
     */
    private static final int DEFAULT_JUMP_CACHE_SIZE = 1000;
    public JumpersCache jumperMemoryCache = new JumpersCache(DEFAULT_JUMP_CACHE_SIZE);

    /**
     * Default redirect if no jumper can be found.
     * If this field is <code>null</code>, a url will not be 'redirected' if the
     * search for a jumper failed. This may cause a 404 error on your server if
     * the path specified is unavailable.
     * However, you may need it if other servlets rely on specific paths
     * that would otherwise be caught by the jumper servlet.
     * The value fo this field is set using the <code>JumperNotFoundURL</code>
     * property in the builder configuration file.
     */
    protected static String jumperNotFoundURL;

    private MMObjectBuilder jumpercachebuilder = null;
    private Vector observers = new Vector();

    // jumper calculators
    private String MMBASE_CALCULATOR = "org.mmbase.util.jumpers.JumperCalculator";
    private JumperCalculatorInterface defaultCalculator = null;
    private JumperCalculatorInterface overrideCalculator = null;
 

    // -------------------------------------------------------
    // init 

    /**
     * Initializes the builder.
     * Determines the jumpercache, and initializes it.
     * Also determines the default jumper url.
     * @return <code>true</code> when there are no problems
     */
    public boolean init() {
        boolean result = super.init();
        
        // cache
        jumpercachebuilder = mmb.getMMObject("jumpercache");
        if(jumpercachebuilder==null) { 
            log.error("Jumpercache is not found, make sure the builder 'jumpercache' is enabled!");
            result = false;
        }

        // not-found url
        jumperNotFoundURL = getInitParameter("JumperNotFoundURL");

        // override function
        setCalculators();

        // observers
        addObservers();

        return result;
    }

    /**
    * Specify the calculators for jumpers.
    *
    * Calculators define the behaviour of how numbers are being translated into urls.
    * They provide the logic, whereas this builder contains the caching.
    * 
    * There are 2 calculators: override and default. 
    * 
    * default: this is the default jumper, containing the logic as it always was. This file
    * can be copied and enhanced to fit your own need. That will then become the override
    * calculator.
    * 
    * Flow: if override.calculate(number) returns an url, that one is used; if it returns
    * a null or just isnt defined in jumpers.xml, the default calculator.calculate(number)
    * is used. The default.calculate(number) will map number into a node and call 
    * the method getDefaultUrl(number) of the builder of that node.
    *
    * The override class is defined in jumpers.xml as a property:
    *
    *  WEB-INF/config/builders/core/jumpers.xml
    *
    *       <properties>
    *           <property name="calculator">nl.vpro.mmbase.util.jumpers.JumperCalculator</property>
    *       </properties>
    *
    * @see {org.mmbase.util.jumpers.JumperCalculator}
    */
    private void setCalculators() {
        // default calculator
        try { 
            defaultCalculator = (JumperCalculatorInterface)Class.forName(MMBASE_CALCULATOR).newInstance();
        } catch(java.lang.ClassNotFoundException e) { 
            log.fatal("default calculator with name("+MMBASE_CALCULATOR+"): Exception: "+e);
        } catch(java.lang.InstantiationException e) { 
            log.fatal("default calculator with name("+MMBASE_CALCULATOR+"): Exception: "+e);
        } catch(java.lang.IllegalAccessException e) { 
            log.fatal("default calculator with name("+MMBASE_CALCULATOR+"): Exception: "+e);
        } 

        if(defaultCalculator!=null) 
            log.info("going to use as default calculator: " +MMBASE_CALCULATOR);

        // override calculator
        String calculator_name = getInitParameter("calculator");
        if(calculator_name != null && !calculator_name.equals("")) { 
            try { 
                overrideCalculator = (JumperCalculatorInterface)Class.forName(calculator_name).newInstance();
            } catch(java.lang.ClassNotFoundException e) {
                log.fatal("override calculator with name("+calculator_name+"): Exception: "+e);
            } catch(java.lang.InstantiationException e) {
                log.fatal("override calculator with name("+calculator_name+"): Exception: "+e);
            } catch(java.lang.IllegalAccessException e) { 
                log.fatal("override calculator with name("+calculator_name+"): Exception: "+e);
            }
        }
        if(overrideCalculator!=null)
            log.info("going to use as override calculator: "+calculator_name);
    }

    /**
    * Add all active builders which are not relations as observers
    */
    private void addObservers() {
        for(Enumeration e = mmb.getMMObjects();e.hasMoreElements();) {
            addObserver(((MMObjectBuilder)e.nextElement()).getTableName());
        }
    }

    /**
     * Subscribe to a builder and thus watch its updates/deletes.
     *
     * In order to get a signal when the jumper is changed, it subscribes itself
     * to the builder.
     * 
     * To avoid conflicts and overhead (subscribing to object will also subscribe 
     * to relations, which can never be a jumper), only objects which are 
     * 
     *  - not object
     *  - not jumpers
     *  - not jumpercache 
     *  - not relations
     * 
     * are allowed to be watched. 
     *
     * @param name is the builder which to observe
     */
    private void addObserver(String name) {
        if( !observers.contains(name) && 
            !name.equals("object") && 
            !name.equals("jumpers") && 
            !name.equals("jumpercache") &&
            !mmb.getRelDef().isRelationTable(name)) 
        {
            mmb.addLocalObserver(name,this);
            mmb.addRemoteObserver(name,this);
            observers.addElement(name);
        }
    }

    // -------------------------------------------------------
    // servdb: (recalculating) jumper calls

    public String getJump(StringTokenizer tok) {
        return getJump(tok,false);
    }
    public String getJump(StringTokenizer tok, boolean reload) {
        String key = tok.nextToken();
        return getJump(key,reload);
    }
    public String getJump(String key){
        return getJump(key,false);
    }
    public String getJump(String key, boolean reload) {
        String url = null;
        try { 
            // invalid key
            if(key.equals(""))
                return jumperNotFoundURL;

            // cache
            if(!reload) { 
                url = (String) jumperMemoryCache.get(key);
                if(url!=null) return url;
            }

            // jumper.name
            url = getNameJumper(key);
            if(url!=null) return url;

            // jumper.id
            url = getIDJumper(key);
            if(url!=null) return url;

            // jumper.number
            try { url = getNumberJumper(Integer.parseInt(key),reload); } catch(NumberFormatException e) { } 
            if(url!=null) return url;

            // no jumper found
            if(url==null) { 
                log.warn("jumper("+key+") not found!");
                url = jumperNotFoundURL;
            }
        } catch(Exception e) { 
            log.fatal("Exception: jumper("+key+"): "+e.toString());
            url = jumperNotFoundURL;
        }

        return url;
    }
    
    // jumper.field
    // ------------

    protected String getJumpByField(String fieldName, String key) {
        NodeSearchQuery query = new NodeSearchQuery(this);
        FieldDefs fieldDefs = getField(fieldName); // "name");
        StepField field = query.getField(fieldDefs);
        FieldDefs numberFieldDefs = getField("number");
        StepField numberField = query.getField(numberFieldDefs);
        BasicSortOrder sortOrder = query.addSortOrder(numberField); // use 'oldest' jumper

        BasicFieldValueConstraint cons = null;

        if(fieldDefs.getDBType() == FieldDefs.TYPE_STRING)
            cons = new BasicFieldValueConstraint(field, key);
        else if(fieldDefs.getDBType() == FieldDefs.TYPE_INTEGER) { 
            try { 
                cons = new BasicFieldValueConstraint(field, new Integer(key));
            } catch(NumberFormatException e) { log.error("this key("+key+") should be a number because field("+fieldName+") is of type int!"); 
                cons = null;
            }
        }

        query.setConstraint(cons);
        query.setMaxNumber(1);

        try {
            List resultList = getNodes(query);
            if (resultList.size() > 0) {
                MMObjectNode node = (MMObjectNode) resultList.get(0);
                return node.getStringValue("url");
            }
        } catch (SearchQueryException sqe) {
            log.error(sqe.getMessage());
        }
        return null;
    }


    // jumper.name
    // -----------
    private String getNameJumper(String name) { 
        String url = getJumpByField("name", name);
        if(url!=null)
            jumperMemoryCache_put(name,url);

        return url;
    }

    // jumper.id
    // ---------
    private String getIDJumper(String key) { 
        String url = null;
        int ikey = -1;
        try {
            ikey = Integer.parseInt(key);
            if(ikey >= 0) 
                url = getJumpByField("id", key);
        } catch (NumberFormatException e) { 
            log.warn("this key("+key+") is not a number!");
        }

        return url;
    }

    // jumper.number
    // -------------
    private String getNumberJumper(int number,boolean reload) { 
        String url = null;

        // get from database cache
        if(!reload)
            url = jumperDatabaseCache_get(""+number);

        // if not in cache or reload, recalculate
        if(url==null || reload)
            url = lockAndRecalculateNumberJumper(number);

        // put in mem cache

        if(url!=null)
            jumperMemoryCache_put("" + number,url);

        return url;
    }

    // recalcultating number jumper
    // ----------------------------
    private String recalculateNumberJumper(int number) {
        String result =null;
        MMObjectNode node=getNode(number);
        if (node!=null) {
            String buildername= node.parent.getTableName();
            MMObjectBuilder builder=mmb.getMMObject(buildername);
            if (builder!=null) {
                // calculate url

                // determine if a override url exists for this node
                if(overrideCalculator!=null)
                    result = overrideCalculator.calculate(node);

                // otherwise determine default url 
                // if(result==null) 
                    // result = defaultCalculator.calculate(node);

                // cache node and observe parent
                if(result!=null && !result.equals("")) {
                    addObserver(buildername);
                    jumperDatabaseCache_put("" + number,result);
                    log.info("recalculated: "+buildername+"("+number+"): url("+result+"): now in persistent cache");
                }
            }
        }
        return result;
    }


    // -------------------------------------------------------
    // locking

    private static Hashtable jumperLocks = new Hashtable();
    private Object globalJumperLocks=new Object();
    class JumperLock{
        Object lock=null;
        boolean cacheFilled=false;
        int count=0;
        JumperLock(){
            lock=new Object();
            count=1;
        }
    }

    private String lockAndRecalculateNumberJumper(int number) {
        String url = null;
        JumperLock jumperLock=null;;
        try{
            synchronized(globalJumperLocks){
                jumperLock=(JumperLock)jumperLocks.get(""+number);
                if(jumperLock==null){
                    jumperLock=new JumperLock();
                    jumperLocks.put(""+number,jumperLock);
                }else{
                    jumperLock.count++;
                }
            }
            log.info("there are "+jumperLock.count+ " thread(s) waiting for "+number);
            synchronized(jumperLock.lock){
                if(!jumperLock.cacheFilled){
                    Calendar tStartCalc=Calendar.getInstance();

                    // recalc
                    url =recalculateNumberJumper(number);

                    Calendar tEndCalc=Calendar.getInstance();
                    Calendar totalTime=Calendar.getInstance();
                    totalTime.setTime(new Date(tEndCalc.getTime().getTime()-tStartCalc.getTime().getTime()));
                    jumperLock.cacheFilled=true;
                } else {
                    url = (String)jumperMemoryCache_get(""+number);
                }
            }
        } catch(Exception e) {
            log.error("Exception for jumper("+number+"): "+e.toString());
            e.printStackTrace();
        } finally {
            synchronized(globalJumperLocks){
                jumperLock.count--;
                if(jumperLock.count<=0)
                    jumperLocks.remove(""+number);
            }
        }

        return url;
    }

    // -------------------------------------------------------
    // jumpercaches


    // memory caches
    // -------------
    // defined so that logging can be used to debug put/remove

    // memorycache.contains
    private boolean jumperMemoryCache_contains(String number) { 
        return jumperMemoryCache.containsKey(number);
    }
    // memorycache.get
    private String jumperMemoryCache_get(String number) {
        return (String) jumperMemoryCache.get(number);
    }
    // memorycache.put
    private void jumperMemoryCache_put(String number, String url) {
        if(log.isDebugEnabled()) log.debug("memcache: put("+number+","+url+")");
        jumperMemoryCache.put(number,url);
    }
    // memorycache.remove
    private void jumperMemoryCache_remove(String number) {
        if(jumperMemoryCache.containsKey(number)) { 
            if(log.isDebugEnabled()) log.debug("memcache: removed("+number+")");
            jumperMemoryCache.remove(number);
        }
    }

    // database caches
    // ---------------

    // database.get
    private String jumperDatabaseCache_get(String number) { 
        String url = null;
        List nodes = null;
        try {
            NodeSearchQuery query = new NodeSearchQuery(jumpercachebuilder);
            StepField keyField = query.getField(jumpercachebuilder.getField("key"));
            query.setConstraint(new BasicFieldValueConstraint(keyField,number));
            nodes = jumpercachebuilder.getNodes(query);
        } catch (SearchQueryException e) {
            log.error(e.toString());
        }

        Iterator i = nodes.iterator();
        while(i.hasNext()) { 
            MMObjectNode node = (MMObjectNode)i.next();
            if(url==null)
                url = node.getStringValue("url");
            // remove double
            else { 
                log.warn("dbcache: get: multiple entries detected for number("+number+"): node("+node.getNumber()+"): key("+node.getStringValue("key")+") url("+node.getStringValue("url")+")");
                node.parent.removeNode(node);
            }
        }

        if(log.isDebugEnabled()) log.debug("dbcache: get("+number+"): url("+url+")");
        return url;
    }

    // database.put
    private void jumperDatabaseCache_put(String number, String url) { 
        // jumper.id is an override, dont update
        if(getIDJumper(number)!=null)
            return;

        String oldurl = null;
        List nodes = null;
        // if contains
        try {
            NodeSearchQuery query = new NodeSearchQuery(jumpercachebuilder);
            StepField keyField = query.getField(jumpercachebuilder.getField("key"));
            query.setConstraint(new BasicFieldValueConstraint(keyField,number));
            nodes = jumpercachebuilder.getNodes(query);
        } catch (SearchQueryException e) {
            log.error(e.toString());
        }

        Iterator i = nodes.iterator();
        // then update 
        if(i.hasNext()) { 
            while(i.hasNext()) {
                MMObjectNode node = (MMObjectNode)i.next();
                if(oldurl==null) { 
                    oldurl = node.getStringValue("url");
                    node.setValue("url",url);
                    node.commit();
                    log.info("dbcache: put: update detected for number("+number+"): old("+oldurl+") -> new("+url+")");

                 // and remove double
                 } else { 
                    log.warn("dbcache: put: multiple entries detected for number("+number+"): node("+node.getNumber()+"): key("+node.getStringValue("key")+") url("+node.getStringValue("url")+")");
                    node.parent.removeNode(node);
                }
            }
        // else insert
        } else { 
            if(log.isDebugEnabled()) log.debug("dbcache: put("+number+","+url+")");
            MMObjectNode jumpercachenode = jumpercachebuilder.getNewNode("jumper");
            jumpercachenode.setValue("key", "" + number);
            jumpercachenode.setValue("url", url);
            jumpercachenode.insert("jumper");
        }
    }

    // database.remove
    private void jumperDatabaseCache_remove(String number) {
       List nodes = null;
        try {
            NodeSearchQuery query = new NodeSearchQuery(jumpercachebuilder);
            StepField keyField = query.getField(jumpercachebuilder.getField("key"));
            query.setConstraint(new BasicFieldValueConstraint(keyField,number));
            nodes = jumpercachebuilder.getNodes(query);
        } catch (SearchQueryException e) {
            log.error(e.toString());
        }
        if(nodes!=null && nodes.size()>0) {
            Iterator i = nodes.iterator();
            while(i.hasNext()) {
                MMObjectNode node = (MMObjectNode)i.next();
                if(log.isDebugEnabled()) log.debug("dbcache: removed("+node.getNumber()+")");
                node.parent.removeNode(node);
            }
        }

    }

    // -------------------------------------------------------
    // (remote) changes

    // if change is from remote machine, only flush memory cache
    public boolean nodeRemoteChanged(String machine,String number,String builder,String ctype) {
        super.nodeRemoteChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype, false);
    }
    // if change is from local machine, flush memorycache and database entry
    public boolean nodeLocalChanged(String machine,String number,String builder,String ctype) {
        super.nodeLocalChanged(machine,number,builder,ctype);
        return nodeChanged(machine,number,builder,ctype, true);
    }
    public boolean nodeChanged(String machine,String number,String builder,String ctype, boolean nodeLocalChanged) {
        if(log.isDebugEnabled()) log.debug("machine("+machine+"), "+builder+"("+number+"), ctype("+ctype+"), local("+nodeLocalChanged+")");

        if(ctype.equals("d")){
            if(log.isDebugEnabled()) log.debug("delete detected: removing "+builder+"("+number+") from cache");
            // remove cache
            jumperMemoryCache_remove(number);
            // remove persistent cache
            if(nodeLocalChanged)
                jumperDatabaseCache_remove(number);

        } else if(ctype.equals("c") || ctype.equals("r")) {
            if(builder.equals("jumpers")) { 
                jumperMemoryCache_remove(getNode(number).getStringValue("name"));
            } else { 
                if(log.isDebugEnabled()) log.debug("change detected: removing "+builder+"("+number+") from cache");
                // remove cache
                jumperMemoryCache_remove(number);
                // remove persistent cache
                if(nodeLocalChanged)
                    jumperDatabaseCache_remove(number);
            
                // update? 
            }

        }
        return true;
    }

    // -------------------------------------------------------
    // gui

    protected String getGUIIndicator(MMObjectNode node, Parameters args) {
        String field = (String) args.get("field");
        if (field == null || field.equals("url")) {
            String url = node.getStringValue("url");
            HttpServletRequest req = (HttpServletRequest) args.get(Parameter.REQUEST);
            HttpServletResponse res = (HttpServletResponse) args.get(Parameter.RESPONSE);
            String link;
            if (url.startsWith("http:") || url.startsWith("https:") || url.startsWith("ftp:")) {
                link = url;
            } else if (! url.startsWith("/")) { // requested relative to context path
                String context = req == null ? MMBaseContext.getHtmlRootUrlPath() : req.getContextPath();
                String u = context + "/" + url;
                link = res == null ? u : res.encodeURL(u);
            } else {
                String context = req == null ? MMBaseContext.getHtmlRootUrlPath() : req.getContextPath();
                // request relative to host's root
                if (url.startsWith(context + "/")) { // in this context!
                    String u = url.substring(context.length() + 1);
                    link = res == null ? u : res.encodeURL(u);
                } else { // in other context
                    link = url;
                }
            }
            return("<a href=\"" + link + "\" target=\"extern\">" + url + "</a>");
        } else {
            if (field == null || field.equals("")) {
                return super.getGUIIndicator(node);
            } else {
                return super.getGUIIndicator(field, node);
            }
        }

    }

    protected Object executeFunction(MMObjectNode node, String function, List arguments) {
         if (function.equals("gui")) {
             String rtn;
             if (arguments == null || arguments.size() == 0) {
                 rtn = getGUIIndicator(node);
             } else {
                 rtn =  getGUIIndicator(node, Parameters.get(GUI_PARAMETERS, arguments));
             }
             if (rtn != null) return rtn;
         }
         return super.executeFunction(node, function, arguments);
    }

    
    // -------------------------------------------------------
    // caches

    // remove from memorycache (local and remote changes)
    public void delJumpCache(String number) { 
        delJumpCache(number, false);
    }

    // remove from memorycache and database (local change)
    public void delJumpCache(String number, boolean nodeLocalChanged) {
        jumperMemoryCache_remove(number);
        if(nodeLocalChanged)
            jumperDatabaseCache_remove(number);   
    }

    // remove from memorycache
    public void removeNode(MMObjectNode node) {
        jumperMemoryCache_remove(node.getStringValue("name"));
        super.removeNode(node);
    }

    // cache class
    public class JumpersCache extends Cache {
        public String getName() { 
            return "JumpersCache"; 
        }
        public String getDescription() {
             return "Cache for Jumpers";
        }
        JumpersCache(int size) {
            super(size);
            putCache(this);
        }
    }

    // -------------------------------------------------------
    // test

    public void test(String number) { 
        try { 
            test(Integer.parseInt(number));
        } catch(NumberFormatException e) { 
            log.error("Exception: node("+number+") not a number: " + e);
        }
    }

    public void test( int number ) {
        MMObjectNode node = getNode(number);
        if(node!=null) { 
            test(node);
        } else { 
            log.fatal("node("+number+") does not exists!");
        }
    }

    public void test(MMObjectNode node) { 
        String newurl = overrideCalculator.calculate(node);
        String oldurl = defaultCalculator.calculate(node);
            if(oldurl==null && newurl==null)
                // log.error("test: "+node.parent.getTableName()+"("+node.getNumber()+"): old("+oldurl+"), newurl("+newurl+")");
                log.debug("test: "+node.parent.getTableName()+"("+node.getNumber()+"): old("+oldurl+"), newurl("+newurl+"): both null, thats ok");
            else { 
                if(newurl!=null && !newurl.equals(oldurl))
                    log.error("test: "+node.parent.getTableName()+"("+node.getNumber()+"): old("+oldurl+"), newurl("+newurl+") there is a difference!");
                else 
                    log.debug("test: "+node.parent.getTableName()+"("+node.getNumber()+"): ok!");   
            }
    }
}
