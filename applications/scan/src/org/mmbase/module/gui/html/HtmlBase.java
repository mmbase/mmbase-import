/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.module.gui.html;

import java.util.*;
import java.io.*;

import org.mmbase.core.CoreField;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.DayMarkers;
import org.mmbase.jumpers.Jumpers;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * The module which provides access to the multimedia database
 * it creates, deletes and gives you methods to keep track of
 * multimedia objects. It does not give you direct methods for
 * inserting and reading them thats done by other objects
 *
 * @application SCAN
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class HtmlBase extends ProcessorModule {
    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(HtmlBase.class);

    sessionsInterface sessions;
    boolean scancache=false;
    MMBase mmb=null;

    // should use org.mmbase.cache!
    private int multilevel_cachesize=300;
    private MultilevelCacheHandler multilevel_cache;

    public void init() {
        scancache tmp=(scancache)getModule("SCANCACHE");

        if (tmp!=null && tmp.getStatus()) scancache=true;

        mmb=(MMBase)getModule("MMBASEROOT");
        sessions=(sessionsInterface)getModule("SESSION");

        // get size from properties
        multilevel_cache=new MultilevelCacheHandler(mmb,multilevel_cachesize);
    }

    static Enumeration search(MMObjectBuilder bul, String where, String sorted, boolean direction) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        String directions = (direction? "UP": "DOWN");
        Vector result = new Vector();
        NodeSearchQuery query = getSearchQuery(bul, where, sorted, directions);
        try {
            List nodes = bul.getNodes(query);
            result.addAll(nodes);
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return result.elements();
    }

    static NodeSearchQuery getSearchQuery(MMObjectBuilder bul, String where, String sorted, String directions) {
        NodeSearchQuery query = bul.getStorageConnector().getSearchQuery(where);

        if (directions == null) {
            directions = "";
        }
        StringTokenizer sortedTokenizer = new StringTokenizer(sorted, ",");
        StringTokenizer directionsTokenizer = new StringTokenizer(directions, ",");

        String direction = "UP";
        while (sortedTokenizer.hasMoreElements()) {
            String fieldName = sortedTokenizer.nextToken().trim();
            CoreField coreField = bul.getField(fieldName);
            if (coreField == null) {
                throw new IllegalArgumentException(
                "Not a known field of builder " + bul.getTableName()
                + ": '" + fieldName + "'");
            }
            StepField field = query.getField(coreField);
            BasicSortOrder sortOrder = query.addSortOrder(field);
            if (directionsTokenizer.hasMoreElements()) {
                direction = directionsTokenizer.nextToken().trim();
            }
            if (direction.equalsIgnoreCase("DOWN")) {
                sortOrder.setDirection(SortOrder.ORDER_DESCENDING);
            } else {
                sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
            }
        }
        return query;
    }

    /**
     */
    public HtmlBase() {
    }

    /**
     * Generate a list of values from a command to the processor
     */
    @Override public List<String> getList(PageInfo sp,StringTagger tagger, String value)  {
        String line = Strip.doubleQuote(value,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("OBJECTS")) return doObjects(sp,tagger);
            if (cmd.equals("RELATIONS")) return doRelations(sp,tagger);
            if (cmd.equals("MULTILEVEL")) return doMultiLevel(sp,tagger);
            if (cmd.equals("MULTI")) return doMultiLevel(sp,tagger);
            if (cmd.equals("BUILDER")) return doBuilder(sp,tagger,tok);
        }
        return null;
    }

    /**
     * show Objects
     */
    public Vector doObjects(PageInfo sp, StringTagger tagger) {
        String result=null;
        MMObjectNode node;
        Vector results=new Vector();
        String type=tagger.Value("TYPE");
        String where=tagger.Value("WHERE");
        String dbsort=tagger.Value("DBSORT");
        String dbdir=tagger.Value("DBDIR");
        MMObjectBuilder bul=mmb.getBuilder(type);
        long begin=System.currentTimeMillis();
        Enumeration e=null;
        if (dbsort==null) {
            e = bul.search(where);
        } else {
            if (dbdir==null) {
                e = search(bul,where,dbsort, true);
            } else {
                if (dbdir.equals("DOWN")) {
                    e = search(bul,where,dbsort,false);
                } else {
                    e = search(bul,where,dbsort,true);
                }
            }
        }

        for (;e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            Enumeration f=tagger.Values("FIELDS").elements();
            for (;f.hasMoreElements();) {

                String fieldname=Strip.doubleQuote((String)f.nextElement(),Strip.BOTH);
                result=node.getStringValue(fieldname);

                if (result!=null && !result.equals("null")) {
                    results.addElement(result);
                } else {
                    results.addElement("");
                }
            }
        }
        tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
        long end=System.currentTimeMillis();
        log.debug("doObjects("+type+")="+(end-begin)+" ms");
        return results;
    }

    /**
     * Creates a {@link org.mmbase.storage.search.FieldCompareConstraint
     * FieldCompareConstraint}, based on parts of a field expression in a
     * MMNODE expression.
     *
     * @param field The field
     * @param comparison The second character of the comparison operator.
     * @param strValue The value to compare with, represented as
     *        <code>String<code>.
     * @return The constraint.
     * @since MMBase-1.7
     */
    private BasicFieldValueConstraint parseFieldPart(StepField field, char comparison, String strValue) {

        Object value = strValue;

        // For numberical fields, convert string representation to Double.
        if (field.getType() != FieldDefs.TYPE_STRING &&
            field.getType() != FieldDefs.TYPE_XML &&
            field.getType() != FieldDefs.TYPE_UNKNOWN) {
                // backwards comp fix. This is needed for the scan editors.
                int length = strValue.length();
                if (strValue.charAt(0) == '*' && strValue.charAt(length - 1) == '*') {
                    strValue = strValue.substring(1, length - 1);
                }
                value = Double.valueOf(strValue);
        }

        BasicFieldValueConstraint constraint =
            new BasicFieldValueConstraint(field, value);

        switch (comparison) {
            case '=':
            case 'E':
                // EQUAL (string field)
                if (field.getType() == FieldDefs.TYPE_STRING ||
                    field.getType() == FieldDefs.TYPE_XML) {
                    // Strip first and last character of value, when
                    // equal to '*'.
                    String str = (String) value;
                    int length = str.length();
                    if (str.charAt(0) == '*' && str.charAt(length - 1) == '*') {
                        value = str.substring(1, length - 1);
                    }

                    // Convert to LIKE comparison with wildchard characters
                    // before and after (legacy).
                    constraint.setValue('%' + (String) value + '%');
                    constraint.setCaseSensitive(false);
                    constraint.setOperator(FieldCompareConstraint.LIKE);

                // EQUAL (numerical field)
                } else {
                    constraint.setOperator(FieldCompareConstraint.EQUAL);
                }
                break;

            case 'N':
                constraint.setOperator(FieldCompareConstraint.NOT_EQUAL);
                break;

            case 'G':
                constraint.setOperator(FieldCompareConstraint.GREATER);
                break;

            case 'g':
                constraint.setOperator(FieldCompareConstraint.GREATER_EQUAL);
                break;

            case 'S':
                constraint.setOperator(FieldCompareConstraint.LESS);
                break;

            case 's':
                constraint.setOperator(FieldCompareConstraint.LESS_EQUAL);
                break;

            default:
                throw new IllegalArgumentException(
                    "Invalid comparison character: '" + comparison + "'");
        }
        return constraint;
    }

    /**
     * Creates query based on an MMNODE expression.
     *
     * @param expr The MMNODE expression.
     * @return The query.
     * @throws IllegalArgumentException when an invalid argument is supplied.
     * @since MMBase-1.7
     */
    private NodeSearchQuery convertMMNodeSearch2Query(MMObjectBuilder builder, String expr) {
        NodeSearchQuery query = new NodeSearchQuery(builder);
        BasicCompositeConstraint constraints
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        String logicalOperator = null;

        // Strip leading string "MMNODE " from expression, parse
        // fieldexpressions and logical operators.
        // (legacy: eol characters '\n' and '\r' are interpreted as "AND NOT")
        StringTokenizer tokenizer
            = new StringTokenizer(expr.substring(7), "+-\n\r", true);
        while (tokenizer.hasMoreTokens()) {
            String fieldExpression = tokenizer.nextToken();

            // Remove prefix if present (example episodes.title==).
            int pos = fieldExpression.indexOf('.');
            if (pos != -1) {
                fieldExpression = fieldExpression.substring(pos + 1);
            }

            // Break up field expression in fieldname, comparison operator
            // and value.
            pos = fieldExpression.indexOf('=');
            if (pos != -1 && fieldExpression.length() > pos + 2) {
                String fieldName = fieldExpression.substring(0, pos);
                char comparison = fieldExpression.charAt(pos + 1);
                String value = fieldExpression.substring(pos + 2);

                // Add corresponding constraint to constraints.
                FieldDefs fieldDefs = builder.getField(fieldName);
                if (fieldDefs == null) {
                    throw new IllegalArgumentException(
                        "Invalid MMNODE expression: " + expr);
                }
                StepField field = query.getField(fieldDefs);
                BasicConstraint constraint
                    = parseFieldPart(field, comparison, value);
                constraints.addChild(constraint);

                // Set to inverse if preceded by a logical operator that is
                // not equal to "+".
                if (logicalOperator != null && !logicalOperator.equals("+")) {
                    constraint.setInverse(true);
                }
            } else {
                // Invalid expression.
                throw new IllegalArgumentException(
                    "Invalid MMNODE expression: " + expr);
            }

            // Read next logical operator.
            if (tokenizer.hasMoreTokens()) {
                logicalOperator = tokenizer.nextToken();
            }
        }

        List childs = constraints.getChilds();
        if (childs.size() == 1) {
            query.setConstraint((FieldValueConstraint) childs.get(0));
        } else if (childs.size() > 1) {
            query.setConstraint(constraints);
        }
        return query;
    }

    /**
     * Returns a Vector containing all the objects that match the searchkeys. Only returns the object numbers.
     * @since MMBase-1.8
     * @param where scan expression that the objects need to fulfill
     * @return a <code>Vector</code> containing all the object numbers that apply, <code>null</code> if en error occurred.
     * @deprecated Use {@link #getNodes(NodeSearchQuery)
     *             getNodes(NodeSearchQuery} to perform a node search.
     */
    private Vector<Integer> searchNumbers(MMObjectBuilder builder, String where) {
        // In order to support this method:
        // - Exceptions of type SearchQueryExceptions are caught.
        // - The result is converted to a vector.
        Vector<Integer> results = new Vector<Integer>();
        NodeSearchQuery query;
        if (where != null && where.startsWith("MMNODE ")) {
            // MMNODE expression.
            query = convertMMNodeSearch2Query(builder, where);
        } else {
            query = new NodeSearchQuery(builder);
            org.mmbase.storage.search.legacy.QueryConvertor.setConstraint(query, where);
        }

        // Wrap in modifiable query, replace fields by just the "number"-field.
        ModifiableQuery modifiedQuery = new ModifiableQuery(query);
        Step step = query.getSteps().get(0);
        FieldDefs numberFieldDefs = builder.getField(MMObjectBuilder.FIELD_NUMBER);
        StepField field = query.getField(numberFieldDefs);
        List<StepField> newFields = new ArrayList<StepField>(1);
        newFields.add(field);
        modifiedQuery.setFields(newFields);

        try {
            List resultNodes = mmb.getSearchQueryHandler().getNodes(modifiedQuery,
                new ResultBuilder(mmb, modifiedQuery));

            // Extract the numbers from the result.
            Iterator iResultNodes = resultNodes.iterator();
            while (iResultNodes.hasNext()) {
                ResultNode resultNode = (ResultNode) iResultNodes.next();
                results.add(resultNode.getIntegerValue(MMObjectBuilder.FIELD_NUMBER));
            }
        } catch (SearchQueryException e) {
            log.error(e);
            results = null;
        }
        return results;
    }

    /**
     * show Relations
     */
    public Vector doRelations(PageInfo sp, StringTagger tagger) {
        Object tmp;
        MMObjectNode node;
        MMObjectBuilder bul=null;
        int otype=-1;
        int snode=-1;
        Vector results=new Vector();
        Vector<Integer> wherevector=null;
        String type=tagger.Value("TYPE");
        String where=tagger.Value("WHERE");

        try {
            String tm = tagger.Value("NODE");
            MMObjectNode srcnode = mmb.getTypeDef().getNode(tm);
            snode = srcnode.getIntValue("number");
            bul = srcnode.getBuilder();

            if (type!=null) {
                bul=mmb.getBuilder(type);
                if (bul==null) {
                    throw new Exception("cannot find object type : "+type);
                }
                otype=bul.getNumber();
            }
            if ((where != null) && (bul != null)) {
                wherevector = searchNumbers(bul,where);
            }
            Iterator i = null;
            if (type==null) {
                i=srcnode.getRelatedNodes().iterator();
            } else {
                i=srcnode.getRelatedNodes(type).iterator();
            }
            while(i.hasNext()) {
                node=(MMObjectNode)i.next();
                if (where==null || wherevector.contains(new Integer(node.getIntValue("number")))) {
                    for (Iterator f=tagger.Values("FIELDS").iterator(); f.hasNext();) {
                        // hack hack this is way silly Strip needs to be fixed
                        tmp=node.getValue(Strip.doubleQuote((String)f.next(),Strip.BOTH));
                        if (tmp!=null && !tmp.equals("null")) {
                            results.addElement(""+tmp);
                        } else {
                            results.addElement("");
                        }
                    }
                }
            }
            tagger.setValue("ITEMS",""+tagger.Values("FIELDS").size());
        } catch(Exception e) {
            log.error("doRelations("+sp+"): ERROR: node("+snode+"), type("+type+"), where("+where+"):"+e);
            if (bul!=null) {
                log.error(Logging.stackTrace(e));
            }
        }
        return results;
    }


    public String doGetRelationValue(PageInfo sp, StringTokenizer tok) {
        MMObjectBuilder bul = mmb.getBuilder("typedef");

        // reads $MOD-MMBASE-GETRELATIONVALUE-12-23-title where 12 is the source
        // number, 23 the target number and title the key of the relation
        // value you want.
        int snumber=-1;
        int dnumber=-1;

        //obtain source number
        if (tok.hasMoreTokens()) {
            try {
                snumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong source node";
            }
        } else {
            return "missing source node";
        }


        //obtain destination number
        if (tok.hasMoreTokens()) {
            try {
                dnumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong destination node";
            }
        } else {
            return "missing destination node";
        }

        //obtain field name
        if (tok.hasMoreTokens()) {
            String fieldname=tok.nextToken();
            MMObjectNode snode=bul.getNode(""+snumber);
            if (snode!=null) {
                for (Enumeration e=snode.getRelations();e.hasMoreElements();) {
                    MMObjectNode inode=(MMObjectNode)e.nextElement();
                    int s=inode.getIntValue("snumber");
                    int d=inode.getIntValue("dnumber");
                    if (d==dnumber || s==dnumber) {
                        String result="";
                        int n=inode.getIntValue("number");
                        MMObjectNode dnode=bul.getNode(""+n);
                        if (dnode!=null) {
                            result=dnode.getStringValue(fieldname);
                            if (result!=null && !result.equals("null")) {
                                return result;
                            } else {
                                return "";
                            }
                        }
                    }
                }
            } else {
                return "wrong source node";
            }
        } else {
            return "missing fieldname";
        }
        return "";
    }

    public String doGetRelationCount(PageInfo sp, StringTokenizer tok) {
        MMObjectBuilder bul=mmb.getBuilder("typedef");
        // reads $MOD-MMBASE-GETRELATIONCOUNT-12-images where 12 is the nodenumber
        // and images is optional (if not it will return the total number of
        // relations it has.

        int snumber=-1;
        String bulname=null;

        //obtain source number
        if (tok.hasMoreTokens()) {
            try {
                snumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong source node";
            }
        } else {
            return "missing source node";
        }

        // obtain possible builder if not defined it will return the total count
        if (tok.hasMoreTokens()) {
            bulname=tok.nextToken();
        }

        MMObjectNode snode=bul.getNode(""+snumber);
        if (snode!=null) {
            if (bulname==null) {
                return ""+snode.getRelationCount();
            } else {
                return ""+snode.getRelationCount(bulname);
            }
        } else {
            return "0";
        }
    }

    public String doSetRelationValue(PageInfo sp, StringTokenizer tok) {
        MMObjectBuilder bul=mmb.getBuilder("typedef");
        // reads $MOD-MMBASE-GETRELATIONVALUE-12-23-title where 12 is the source
        // number, 23 the target number and title the key of the relation
        // value you want.
        int snumber=-1;
        int dnumber=-1;

        //obtain source number
        if (tok.hasMoreTokens()) {
            try {
                snumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong source node";
            }
        } else {
            return "missing source node";
        }

        //obtain destination number
        if (tok.hasMoreTokens()) {
            try {
                dnumber=Integer.parseInt(tok.nextToken());
            } catch (Exception e) {
                return "wrong destination node";
            }
        } else {
            return "missing destination node";
        }

        //obtain field name
        if (tok.hasMoreTokens()) {
            String fieldname=tok.nextToken();
            MMObjectNode snode=bul.getNode(""+snumber);
            if (snode!=null) {
                for (Enumeration e=snode.getRelations();e.hasMoreElements();) {
                    MMObjectNode inode=(MMObjectNode)e.nextElement();
                    int s=inode.getIntValue("snumber");
                    int d=inode.getIntValue("dnumber");
                    if (d==dnumber || s==dnumber) {
                        String result="";
                        int n=inode.getIntValue("number");
                        MMObjectNode dnode=bul.getNode(""+n);
                        if (dnode!=null) {
                            result=dnode.getStringValue(fieldname);
                            if (result!=null && !result.equals("null")) {
                                return result;
                            } else {
                                return "";
                            }
                        }
                    }
                }
            } else {
                return "wrong source node";
            }
        } else {
            return "missing fieldname";
        }
        return "";
    }

    /**
     * show Relations
     */
    public Vector doRelations_replace(PageInfo sp, StringTokenizer tok) {
        Object tmp;
        MMObjectNode node;
        MMObjectBuilder bul=null;
        int otype=-1;
        int snode=-1;
        Vector results=new Vector();
        try {
            String type=tok.nextToken();
            bul=mmb.getBuilder(type);
            otype=bul.getNumber();

            snode=Integer.parseInt(tok.nextToken());
            MMObjectNode node2=bul.getNode(snode);

            Iterator i=null;
            if (type==null) {
                i=node2.getRelatedNodes().iterator();
            } else {
                i=node2.getRelatedNodes(type).iterator();
            }
            while(i.hasNext()) {
                node=(MMObjectNode)i.next();
                // use StringValue instead?
                tmp=node.getValue(tok.nextToken());
                if (tmp!=null && !tmp.equals("null")) {
                    results.addElement(""+tmp);
                } else {
                    results.addElement("");
                }
            }
        } catch(Exception g) {
            return null;
        }
        if (results.size()>0) {
            return results;
        } else {
            return null;
        }
    }

    /**
     * Execute the commands provided in the form values
     */
    @Override public boolean process(PageInfo sp, Hashtable cmds,Hashtable vars) {
        String cmdline,token;

        for (Enumeration h = cmds.keys();h.hasMoreElements();) {
            cmdline=(String)h.nextElement();
            StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
            token = tok.nextToken();
            if (token.equals("CACHEDELETE")) {
                log.debug("process(): DELETE ON CACHES");
                //                InsRel.deleteNodeCache();
                //                InsRel.deleteRelationCache();

            }
        }
        return false;
    }

    /**
     *	Handle a $MOD command
     */
    @Override public String replace(PageInfo sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("FIELD")) {
                return getObjectField(sp,tok);
            } else if (cmd.equals("GETVALUE")) {
                return getBuilderValue(sp,tok);
            } else if (cmd.equals("PROPERTY")) {
                return getObjectProperty(sp,tok);
            } else if (cmd.equals("OTYPE")) {
                return getObjectType(sp,tok);
            } else if (cmd.equals("TYPENAME")) {
                return getObjectTypeName(sp,tok);
            } else if (cmd.equals("GUIINDICATOR")) {
                return getGuiIndicator(sp,tok);
            } else if (cmd.equals("RELATION")) {
                Vector result=doRelations_replace(sp,tok);
                if (result!=null) return (String)result.elementAt(0);
                return "";
            } else if (cmd.equals("GETRELATIONCOUNT")) {
                return doGetRelationCount(sp,tok);
            } else if (cmd.equals("GETRELATIONVALUE")) {
                return doGetRelationValue(sp,tok);
            } else if (cmd.equals("SETRELATIONVALUE")) {
                return doSetRelationValue(sp,tok);
            } else if (cmd.equals("GETAUTHTYPE")) {
                return mmb.getAuthType();
            } else if (cmd.equals("GETSEARCHAGE")) {
                return getSearchAge(tok);
            } else if (cmd.equals("CACHE")) {
                return ""+doCache(sp,tok);
            } else if (cmd.equals("GETDAYMARKER")) {
                return doGetAgeMarker(tok);
                // org.mmbase } else if (cmd.equals("FILEINFO")) {
                // org.mmbase		return (doFile(rq, tok));
            } else if (cmd.equals("BUILDER")) {
                return doBuilderReplace(sp, tok);
            } else if (cmd.equals("BUILDERACTIVE")) {
                return isBuilderActive(tok);
            } else if (cmd.equals("GETJUMP")) {
                Jumpers bul=(Jumpers)mmb.getBuilder("jumpers");
                String url=bul.getJump(tok);
                if (url.startsWith("http://")) {
                    return url;
                } else {
                    return "";
                }
            } else if (cmd.equals("GETNUMBER")) {
                // Get the number for a alias
                return ""+mmb.getOAlias().getNumber(tok.nextToken());
            } else if (cmd.equals("FIELDLENGTH")) {
                String s = getObjectField(sp,tok);
                if (s==null)
                    return "0";
                else
                    return ""+s.length();
            }
        }
        return "No command defined";
    }

    String doCache(PageInfo sp, StringTokenizer tok) {
        String result="";
        String cmd = tok.nextToken();
        if (cmd.equals("SIZE")) {
            if (tok.hasMoreTokens()) {
                String type = tok.nextToken();
                int i = mmb.getTypeDef().getIntValue(type);
                int j = 0;
                for (Object element : org.mmbase.cache.NodeCache.getCache().values()) {
                    MMObjectNode n=(MMObjectNode)element;
                    if (n.getOType()==i) j++;
                }
                result = "" + j;
            } else {
                result = "" + org.mmbase.cache.NodeCache.getCache().size();
            }
        }
        return result;
    }

    String getObjectType(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return mmb.getTypeDef().getValue(node.getIntValue("otype"));
        }
        return "unknown";
    }

    String getObjectTypeName(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return node.getName();
        }
        return "unknown";
    }

    String getGuiIndicator(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return node.getGUIIndicator();
        }
        return "unknown";
    }

    String getBuilderValue(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String number=tok.nextToken();
            String field="number";
            if (tok.hasMoreTokens()) field=tok.nextToken();
            MMObjectNode node=mmb.getTypeDef().getNode(number);
            return ""+node.getValue(field);
        }
        return "";
    }

    String getObjectField(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String nodeNr=tok.nextToken();
            if( tok.hasMoreTokens()){
                String fieldname=tok.nextToken();
                String result=null;
                MMObjectBuilder bul=mmb.getBuilder("typedef");
                MMObjectNode node=bul.getNode(nodeNr);
                sessionInfo pagesession=getPageSession((scanpage) sp);
                if (pagesession!=null) {
                    pagesession.addSetValue("PAGECACHENODES",""+nodeNr);
                }
                if (result!=null) {
                    return result;
                } else  {
                    if (node!=null) {
                        result=node.getStringValue(fieldname);
                    }
                    if (result!=null && !result.equals("null")) {
                        return result;
                    } else {
                        return "";
                    }
                }
            } else log.error("getObjectField(): no token fieldname found, nodenr("+nodeNr+"), url("+sp +")");
        } else log.error("getObjectField(): no token nodenr found, url("+sp +")");
        return "no command defined";
    }

    String getObjectProperty(PageInfo sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String nodeNr=tok.nextToken();
            String fieldname=tok.nextToken();
            MMObjectBuilder bul=mmb.getBuilder("fielddef");
            MMObjectNode node=bul.getNode(nodeNr);
            sessionInfo pagesession=getPageSession((scanpage) sp);
            if (pagesession!=null) {
                pagesession.addSetValue("PAGECACHENODES",""+nodeNr);
            }
            MMObjectNode pnode=node.getProperty(fieldname);
            if (pnode!=null) {
                return pnode.getStringValue("value");
            } else {
                return "";
            }
        }
        return "no command defined";
    }

    public Hashtable<Integer, MMObjectNode> getSearchHash(Vector se,String mapper) {
        Hashtable<Integer, MMObjectNode> results=new Hashtable<Integer, MMObjectNode>();
        Enumeration t = se.elements();
        MMObjectNode node;
        while (t.hasMoreElements()) {
            node=(MMObjectNode)t.nextElement();
            results.put(new Integer(node.getIntValue(mapper)),node);
        }
        return results;
    }

    public String getWhereList(Vector se,String mapper) {
        if (se==null) return null;
        StringBuffer inlist = new StringBuffer();
        inlist.append(" (");
        Enumeration t = se.elements();
        MMObjectNode node;
        while (t.hasMoreElements()) {
            node=(MMObjectNode)t.nextElement();
            inlist.append(node.getIntValue(mapper) + ",");
        }
        if (inlist.length() >= 1 ) inlist.setLength(inlist.length()-1);
        inlist.append( ") ");
        return inlist.toString();
    }

    private byte[] getFileBytes(String file) {
        File scanfile;
        int filesize,len=0;
        byte[] buffer;
        FileInputStream scan;

        scanfile = new File(file);
        filesize = (int)scanfile.length();
        buffer=new byte[filesize];
        try {
            scan = new FileInputStream(scanfile);
            len=scan.read(buffer,0,filesize);
            scan.close();
        } catch(FileNotFoundException e) {
            // oops we have a problem
        } catch(IOException e) {}
        if (len!=-1) {
            return buffer;
        }
        return null;
    }

    public Vector doMultiLevel(PageInfo sp, StringTagger tagger) throws MultiLevelParseException {
        String result=null,fieldname;
        MMObjectNode node;
        Integer hash;
        Vector results=null,nodes;
        Enumeration e,f;
        boolean reload=true;

        if (scancache) reload=getReload((scanpage) sp,tagger);

        Vector type=tagger.Values("TYPE");
        if ((type==null) || (type.size()==0)) throw new MultiLevelParseException("No TYPE specified");
        Vector dbsort=tagger.Values("DBSORT");
        Vector dbdir=tagger.Values("DBDIR");
        String where=tagger.Value("WHERE");
        Vector fields=tagger.Values("FIELDS");
        if ((fields==null) || (fields.size()==0)) throw new MultiLevelParseException("No FIELDS specified");
        Vector snodes=tagger.Values("NODE");
        if ((snodes==null) || (snodes.size()==0)) throw new MultiLevelParseException("No NODE specified. Use NODE=\"-1\" to specify no node");
        String distinct=tagger.Value("DISTINCT");
        String searchdirs=tagger.Value("SEARCH");
        int searchdir = RelationStep.DIRECTIONS_EITHER;
        if (searchdirs!=null) {
            searchdirs = searchdirs.toUpperCase();
            if ("DESTINATION".equals(searchdirs)) {
                log.debug("DESTINATION");
                searchdir = RelationStep.DIRECTIONS_DESTINATION;
            } else if ("SOURCE".equals(searchdirs)) {
                log.debug("SOURCE");
                searchdir = RelationStep.DIRECTIONS_SOURCE;
            } else if ("BOTH".equals(searchdirs)) {
                log.debug("BOTH");
                searchdir = RelationStep.DIRECTIONS_BOTH;
            } else if ("ALL".equals(searchdirs)) {
                log.debug("ALL");
                searchdir = RelationStep.DIRECTIONS_ALL;
            }
        }

        tagger.setValue("ITEMS",""+fields.size());

        hash=calcHashMultiLevel(tagger);
        results=(Vector)multilevel_cache.get(hash);

        //if (results==null || reload) {
        if (results==null) {

            if (reload) {
                log.debug("doMultiLevel cache RELOAD "+hash);
            } else {
                log.debug("doMultiLevel cache MISS "+hash);
            }
            ClusterBuilder clusterBuilder = mmb.getClusterBuilder();
            long begin=System.currentTimeMillis(),len;

            // strip the fields of their function codes so we can query the needed
            // fields (teasers.number,shorted(episodes.title)
            Vector cleanfields=removeFunctions(fields);
            // now we have (teasers.number,episodes.title);

            if (dbdir==null) {
                dbdir=new Vector();
                dbdir.addElement("UP"); // UP == ASC , DOWN =DESC
            }
            nodes = clusterBuilder.searchMultiLevelVector(snodes,cleanfields,distinct,type,where,dbsort,dbdir,searchdir);
            if (nodes == null) {
                nodes = new Vector();
            }
            results = new Vector();
            for (e=nodes.elements();e.hasMoreElements();) {
                node=(MMObjectNode)e.nextElement();
                for (f=fields.elements();f.hasMoreElements();) {
                    // hack hack this is way silly, StringTagger needs to be fixed
                    fieldname=Strip.doubleQuote((String)f.nextElement(),Strip.BOTH);
                    if (fieldname.indexOf('(')>=0) {
                        result=""+node.getValue(fieldname);
                    } else {
                        result=node.getStringValue(fieldname);
                    }
                    if (result!=null && !result.equals("null")) {
                        results.addElement(result);
                    } else {
                        results.addElement("");
                    }
                }
            }

            multilevel_cache.put(hash,results,type,tagger);
            long end=System.currentTimeMillis();
            len=(end-begin);
            if (len>200) {
                log.debug("doMultilevel("+type+")="+(len)+" ms URI for page("+sp +")");
            }
        } else {
            log.debug("doMultiLevel cache HIT  "+hash);
        }
        return results;
    }

    /**
     * Belongs to doMultiLevel
     */
    private Integer calcHashMultiLevel(StringTagger tagger) {
        int hash=1;
        Object obj;

        obj=tagger.Values("TYPE");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("DBSORT");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("DBDIR");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Value("WHERE");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("FIELDS");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Values("NODE");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Value("DISTINCT");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());
        obj=tagger.Value("SEARCH");
        hash = 31*hash + (obj==null ? 0 : obj.hashCode());

        return new Integer(hash);
    }

    public String doBuilderReplace(PageInfo sp,StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            MMObjectBuilder bul=mmb.getBuilder(type);
            if (bul!=null) {
                return bul.replace(sp,tok);
            }
        }
        return null;
    }

    /**
     * Returns whether a builder is active.
     * @param tok tokenized command, should contain the builder name
     * @return <code>TRUE</code> if the builder is active, <code>FALSE</code> otherwise
     */
    public String isBuilderActive(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            MMObjectBuilder bul=mmb.getBuilder(type);
            if (bul!=null) {
                return "TRUE";
            }
        }
        return "FALSE";
    }

    public Vector doBuilder(PageInfo sp,StringTagger tagger, StringTokenizer tok) throws ParseException {
        if (tok.hasMoreTokens()) {
            String type=tok.nextToken();
            MMObjectBuilder bul=mmb.getBuilder(type);
            if (bul!=null) {
                return bul.getList(sp,tagger,tok);
            }
        }
        return null;
    }

    private boolean getReload(scanpage sp,StringTagger tagger) {
        boolean rtn=false;
        boolean done=false;
        String memcache;
        if (tagger!=null) {
            memcache=tagger.Value("MEMCACHE");
            if (memcache!=null && memcache.equals("NO")) {
                rtn=true;
                done=true;
            }
        }
        if (!done && sessions!=null) {
            sessionInfo session=sessions.getSession(sp,sp.sname);
            if (session!=null) {
                rtn=sp.reload;
            }
            // When pagemaster calls set the reload on true
            if (sp.wantCache!=null && sp.wantCache.equals("PAGE")) {
                rtn=true;
            }
        } else {
            log.debug("getReload no session module loaded ? ");
        }
        return rtn;
    }

    /**
     * @vpro refers to 'James'
     * @deprecated always returns null, do not use.
     */
    public sessionInfo getPageSession(scanpage sp) {
        if (sessions!=null) {
            // org.mmbase sessionInfo session=sessions.getSession(rq,rq.getSessionName());
            //sessionInfo session=sessions.getSession(sp.req,"james/1234");
            if( sp.sname == null || sp.sname.equals("")) {
                sp.sname = "james/1234";
            }
            sessionInfo session=sessions.getSession(sp,sp.sname);
            String cachetype=session.getValue("CACHE");
            if (cachetype!=null && cachetype.equals("PAGE")) {
                // return session;
            }
        }
        return null;
    }

    public void stop() {}

    String getObjectField(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String result=null;
            String nodeNr=tok.nextToken();
            String fieldname=tok.nextToken();
            MMObjectBuilder bul=mmb.getBuilder("fielddef");
            MMObjectNode node=bul.getNode(nodeNr);
            if (node!=null) {
                result=node.getStringValue(fieldname);
            }
            if (result!=null && !result.equals("null")) {
                return result;
            } else {
                return "";
            }
        }
        return "no command defined";
    }

    public String doObjects(StringTagger tagger) {
        String result=null;
        MMObjectNode node;
        String results="";
        String type=tagger.Value("TYPE");
        String where=tagger.Value("WHERE");
        String dbsort=tagger.Value("DBSORT");
        String dbdir=tagger.Value("DBDIR");
        //log.debug("TYPE="+type);
        MMObjectBuilder bul=mmb.getBuilder(type);
        long begin=System.currentTimeMillis();
        Enumeration e=null;
        if (dbsort==null) {
            e = bul.search(where);
        } else {
            if (dbdir==null) {
                e = search(bul,where,dbsort, true);
            } else {
                if (dbdir.equals("DOWN")) {
                    e = search(bul,where,dbsort,false);
                } else {
                    e = search(bul,where,dbsort,true);
                }
            }
        }

        for (;e.hasMoreElements();) {
            node=(MMObjectNode)e.nextElement();
            Enumeration f=tagger.Values("FIELDS").elements();
            for (;f.hasMoreElements();) {
                // hack hack this is way silly Strip needs to be fixed
                String fieldname=Strip.doubleQuote((String)f.nextElement(),Strip.BOTH);
                result=node.getStringValue(fieldname);
                if (result!=null && !result.equals("null")) {
                    results+=" "+result;
                } else {
                    // this is weird
                }
            }
            results+="\n";
        }
        long end=System.currentTimeMillis();
        //log.debug("MMbase -> doObject ("+type+")="+(end-begin)+" ms");
        return results;
    }

    private Vector removeFunctions(Vector fields) {
        Vector results=new Vector();
        String fieldname,prefix;
        int posdot,posarc,posunder,pos;
        Enumeration f=fields.elements();
        for (;f.hasMoreElements();) {
            fieldname=Strip.doubleQuote((String)f.nextElement(),Strip.BOTH);
            // get the first part (Example : episodes.);
            // we got two styles:
            // episodes.html_body
            // html(episodes.body)
            prefix="";
            posarc=fieldname.indexOf('(');
            if (posarc!=-1) {
                pos=fieldname.indexOf(')');
                String fieldname2 = fieldname.substring(posarc+1,pos);
                if (fieldname2 != null && fieldname2.length() > 0) {
                    results.addElement(fieldname2);
                }
            } else {
                posdot=fieldname.indexOf('.');
                if (posdot!=-1) {
                    prefix=fieldname.substring(0,posdot+1);
                    fieldname=fieldname.substring(posdot+1);
                }
                posunder=fieldname.indexOf('_');
                if (posunder!=-1) {
                    results.addElement(prefix+fieldname.substring(posunder+1));
                } else {
                    results.addElement(prefix+fieldname);
                }
            }
        }
        return results;
    }

    public String getSearchAge(StringTokenizer tok) {
        String builder=tok.nextToken();
        log.debug("getSearchAge(): BUILDER="+builder);
        MMObjectBuilder bul=mmb.getBuilder(builder);
        if (bul!=null) {
            return bul.getSearchAge();
        } else {
            return "30"; // ???
        }
    }

    public MultilevelCacheHandler getMultilevelCacheHandler() {
        return multilevel_cache;
    }


    /**
     * Returns the number of marked days from a specified daycount (?)
     */
    public String doGetAgeMarker(StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String age = tok.nextToken();
            try {
                int agenr = Integer.parseInt(age);
                int agecount = ((DayMarkers)mmb.getBuilder("daymarks")).getDayCountAge(agenr);
                return "" + agecount;
            } catch (Exception e) {
                log.debug(" Not a valid AGE");
                return "No valid age given";
            }
        } else {
            return "No age given";
        }
    }
}
