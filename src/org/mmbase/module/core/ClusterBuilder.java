/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ClusterBuilder is a builder which creates 'virtual' nodes.
 * This is an alternate class for 'MultiRelations', which will be used by the MMCI.
 * The old class may eventually get deprecated, but is supported to allow the
 * new system to be developed without accidentally breaking older code.
 * <br />
 * The nodes are build out of a set of fields from different nodes, combined through a complex query,
 * which is in turn based on the relations that exist between nodes.<br>
 * The builder supplies a method to retrieve these virtual nodes, {@link #searchMultiLevelVector}.
 * Other public methods in this builder function to handle the requests for data obtained from this particular node.
 * Individual nodes in a 'cluster' node can be retrieved by calling the getNodeValue() method, with the builder name
 * of the needed node as parameter value.
 *
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id: ClusterBuilder.java,v 1.5 2002-03-04 12:35:15 pierre Exp $
 */
public class ClusterBuilder extends VirtualBuilder {

    /**
     * Search for all valid relations.
     * When searching relations, return both relations from source to deastination and from destination to source,
     * provided directionality allows
     */
    public static final int SEARCH_BOTH = 0;
    /**
     * Search for destinations,
     * When searching relations, return only relations from source to deastination.
     */
    public static final int SEARCH_DESTINATION = 1;
    /**
     * Seach for sources.
     * When searching a multilevel, return only relations from destination to source, provided directionality allows
     */
    public static final int SEARCH_SOURCE = 2;
    /**
     * Search for all relations.
     * When searching a multilevel, return both relations from source to deastination and from destination to source.
     * Directionality is not checked - ALL relations are used.
     */
    public static final int SEARCH_ALL = 3;

    /**
     * Search for either destination or source.
     * When searching a multilevel, return either relations from source to destination OR from destination to source.
     * The returned set is decided through the typerel tabel. However, if both directiosn ARE somehow supported, the
     * system onyl returns source to destination relations.
     * This is the default value (for compatibility purposes).
     */
    public static final int SEARCH_EITHER = 4;

    // logging variable
    private static Logger log = Logging.getLoggerInstance(ClusterBuilder.class.getName());

    /**
     * Creates an instance of the MultiRelations builder.
     * Called from the MMBase class.
     * @param m the MMbase cloud creating the node
     */
    public ClusterBuilder(MMBase m) {
        super(m,"clusternodes");
    }

    /**
     * Get a new node, using this builder as its parent.
     * The new node is a cluster node.
     * Unlike most other nodes, a cluster node does not have a number,
     * owner, or otype fields.
     * @param owner The administrator creating the new node (ignored).
     * @return A newly initialized <code>VirtualNode</code>.
     */
    public MMObjectNode getNewNode(String owner) {
        MMObjectNode node=new ClusterNode(this);
        return node;
    }

    /**
     * What should a GUI display for this node.
     * This version displays the contents of the 'name' field(s) that were retrieved.
     * XXX: should be changed to something better
     * @param node The node to display
     * @return the display of the node as a <code>String</code>
     */
     public String getGUIIndicator(MMObjectNode node) {
        String s="";
        for (Enumeration i=node.values.keys(); i.hasMoreElements(); ) {
            String key = (String)i.nextElement();
            if (key.endsWith(".name")) {
                if (s.length()!=0) s+=", ";
                s+=node.values.get(key);
            }
        }
        if (s.length()>15) {
            return s.substring(0,12)+"...";
        } else {
            return s;
        }
    }

    /**
     * What should a GUI display for this node/field combo.
     * For a multilevel node, the builder tries to determine
     * the original builder of a field, and invoke the method using
     * that builder.
     * XXX: should retrieve the actual node belonging to the field, instead...
     * @param node The node to display
     * @param field the name field of the field to display
     * @return the display of the node's field as a <code>String</code>, null if not specified
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
        int pos=field.indexOf('.');
        String bulname=null;
        if ((pos!=-1) && (node instanceof ClusterNode)) {
            bulname=field.substring(0,pos);
        }
        MMObjectNode n=((ClusterNode)node).getRealNode(bulname);
        if (n==null) n=node;
        bulname=getTrueTableName(bulname);
        MMObjectBuilder bul=mmb.getMMObject(bulname);
        if (bul!=null) {
            String tmp=field.substring(pos+1);
            return bul.getGUIIndicator(tmp, n);
        }
        return null;
    }

    /**
     * Determines the builder part of a specified field.
     * @param fieldname the name of the field
     * @return the name of the field's builder
     */
    public String getBuilderNameFromField(String fieldname) {
        String bulname="";
        int pos=fieldname.indexOf(".");
        if (pos!=-1) {
            bulname=fieldname.substring(0,pos);
            return getTrueTableName(bulname);
        }
        return "";
    }

    /**
     * Determines the fieldname part of a specified field (without the builder name).
     * @param fieldname the name of the field
     * @return the name of the field without its builder
     */
    public String getFieldNameFromField(String fieldname) {
        int pos=fieldname.indexOf(".");
        if (pos!=-1) {
            fieldname=fieldname.substring(pos+1);
        }
        return fieldname;
    }

    /**
     * Return a field's database type. The returned value is one of the following values
     * declared in FieldDefs:
     * TYPE_STRING,
     * TYPE_INTEGER,
     * TYPE_BYTE,
     * TYPE_FLOAT,
     * TYPE_DOUBLE,
     * TYPE_LONG, or
     * TYPE_UNKNOWN (returned if the original builder of the field cannot be determined)
     * @param the requested field's name
     * @return the field's type.
     */
    public int getDBType(String fieldName) {
        String buildername=getBuilderNameFromField(fieldName);
        if (buildername.length()>0) {
            MMObjectBuilder bul=mmb.getMMObject(buildername);
            return bul.getDBType(getFieldNameFromField(fieldName));
        }
        return FieldDefs.TYPE_UNKNOWN;
    }

    /**
     * Return a field's database state. The returned value is one of the following values
     * declared in FieldDefs:
     * DBSTATE_VIRTUAL,
     * DBSTATE_PERSISTENT,
     * DBSTATE_SYSTEM, or
     * DBSTATE_UNKNOWN (returned if the original builder of the field cannot be determined)
     * @param the requested field's name
     * @return the field's type.
     */
    public int getDBState(String fieldName) {
        String buildername=getBuilderNameFromField(fieldName);
        if (buildername.length()>0) {
            MMObjectBuilder bul=mmb.getMMObject(buildername);
            return bul.getDBState(getFieldNameFromField(fieldName));
        }
        return FieldDefs.DBSTATE_UNKNOWN;
    }

    /**
     * Return all the objects that match the searchkeys.
     * @param snode The number of the node to start the search with. The node has to be present in the first table
     *      listed in the tables parameter.
     * @param fields The fieldnames to return. This should include the name of the builder. Fieldnames without a builder name are ignored.
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname'
     * @param pdistinct 'YES' indicates the records returned need to be distinct. Any other value indicates double values can be returned.
     * @param tables The builder chain. A list containing builder names.
     *      The search is formed by following the relations between successive builders in the list. It is possible to explicitly supply
     *      a relation builder by placing the name of the builder between two builders to search.
     *      Example: company,people or typedef,authrel,people.
     * @param where The contraint. this is in essence a SQL where clause, using the NodeManager names from the nodes as tablenames.
     *      The syntax is either sql (if preceded by "WHERE') or
     *      Examples: "WHERE people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param orderVec the fieldnames on which you want to sort.
     * @param direction A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      the first value in the list is used for the remaining fields. Default value is <code>'UP'</code>.
     * @return a <code>Vector</code> containing all matching nodes
     */
    public Vector searchMultiLevelVector(int snode,Vector fields,String pdistinct,Vector tables,String where, Vector orderVec,Vector direction) {
        Vector v=new Vector();
        v.addElement(""+snode);
        return searchMultiLevelVector(v,fields,pdistinct,tables,where,orderVec,direction, SEARCH_EITHER);
    }

    /**
     * Return all the objects that match the searchkeys.
     * @param snodes The numbers of the nodes to start the search with. These have to be present in the first table
     *      listed in the tables parameter.
     * @param fields The fieldnames to return. This should include the name of the builder. Fieldnames without a builder name are ignored.
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname'
     * @param pdistinct 'YES' indicates the records returned need to be distinct. Any other value indicates double values can be returned.
     * @param tables The builder chain. A list containing builder names.
     *      The search is formed by following the relations between successive builders in the list. It is possible to explicitly supply
     *      a relation builder by placing the name of the builder between two builders to search.
     *      Example: company,people or typedef,authrel,people.
     * @param where The contraint. this is in essence a SQL where clause, using the NodeManager names from the nodes as tablenames.
     *      The syntax is either sql (if preceded by "WHERE') or
     *      Examples: "WHERE people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param orderVec the fieldnames on which you want to sort.
     * @param direction A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      the first value in the list is used for the remaining fields. Default value is <code>'UP'</code>.
     * @return a <code>Vector</code> containing all matching nodes
     */
    public Vector searchMultiLevelVector(Vector snodes,Vector fields,String pdistinct,Vector tables,String where, Vector orderVec,Vector direction) {
        return searchMultiLevelVector(snodes,fields,pdistinct,tables,where,orderVec,direction, SEARCH_EITHER);
    }

    /**
     * Return all the objects that match the searchkeys.
     * @param snodes The numbers of the nodes to start the search with. These have to be present in the first table
     *      listed in the tables parameter.
     * @param fields The fieldnames to return. This should include the name of the builder. Fieldnames without a builder name are ignored.
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname'
     * @param pdistinct 'YES' indicates the records returned need to be distinct. Any other value indicates double values can be returned.
     * @param tables The builder chain. A list containing builder names.
     *      The search is formed by following the relations between successive builders in the list. It is possible to explicitly supply
     *      a relation builder by placing the name of the builder between two builders to search.
     *      Example: company,people or typedef,authrel,people.
     * @param where The contraint. this is in essence a SQL where clause, using the NodeManager names from the nodes as tablenames.
     *      The syntax is either sql (if preceded by "WHERE') or
     *      Examples: "WHERE people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param orderVec the fieldnames on which you want to sort.
     * @param direction A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      the first value in the list is used for the remaining fields. Default value is <code>'UP'</code>.
     * @return a <code>Vector</code> containing all matching nodes
     */
    public Vector searchMultiLevelVector(Vector snodes,Vector fields,String pdistinct,Vector tables,String where, Vector orderVec,Vector direction,
                                         int searchdir) {
        String stables,relstring,select,order,basenodestring,distinct;
        Vector alltables,selectTypes;
        MMObjectNode basenode;
        HashMap roles= new HashMap();
        int snode;

        boolean isdistinct = pdistinct!=null && pdistinct.equalsIgnoreCase("YES");
        if (isdistinct) {
            distinct="distinct";
        }  else {
            distinct="";
        }

        // Get ALL tables (including missing reltables)
        alltables=getAllTables(tables,roles);
        if (alltables==null) return null;

        // Get the destination select string;
        // if the requested set is not DISTINCT, the
        // query also searches all number-fields of the builders
        // involved.
        // Possibly, we want to turn this off or on, i.e. by using another
        // value for the distinct parameter (such as "BYREFERENCE")
        // Note that due to teh problems with distinct result sets, this is not
        // yet an optimal sollution for the multilevel authorization problem

        select=getSelectString(alltables,tables,fields,!isdistinct);
        if (select==null) return null;

        // Get the tables names corresponding to the fields (for the mapping)
        selectTypes=getSelectTypes(alltables,select);

        // create the order parts
        order=getOrderString(alltables,orderVec,direction);

        // get all the table names
        stables=getTableString(alltables,roles);

        // Supporting more then 1 source node or no source node at all
        // Note that node number -1 is seen as no source node
        if ((snodes!=null) && (snodes.size()>0)) {
            String str;
            snode = -1;

            // go trough the whole list and verify that it are all integers
            // from last to first,,... since we want snode to be the one that contains the first..
            for (int i=snodes.size() - 1 ; i >= 0 ; i--) {
                str = (String)snodes.elementAt(i);
                try {
                    snode=Integer.parseInt(str);
                }
                catch(NumberFormatException e) {
                    // maybe it was not an integer, hmm lets look in OAlias table then
                    snode = mmb.OAlias.getNumber(str);
                    if (snode<0) snode=0;
                }
                snodes.setElementAt(""+snode, i);
            }

            int sidx;
            StringBuffer bb=new StringBuffer();

            if (snode>0) {
                basenode=getNode(""+snode);
                if (basenode!=null) {
                    // not very neat... but it works
                    sidx=alltables.indexOf(basenode.parent.tableName);
                    if (sidx<0) sidx=alltables.indexOf(basenode.parent.tableName+"1");
                    if (sidx<0) sidx=0;
                } else {
                    sidx=0;
                }
                str=idx2char(sidx);
                bb.append(getNodeString(str,snodes));
                // Check if we got a relation to ourself
                basenodestring=bb.toString();
            } else {
                basenodestring="";
            }
        } else {
            basenodestring="";
        }

        // get the relation string
        relstring=getRelationString(alltables, searchdir, roles);
        if ((relstring.length()>0) && (basenodestring.length()>0)) {
                relstring=" AND "+relstring;
        }

        // create the extra where parts

        if (where!=null && !where.trim().equals("")) {
            where=QueryConvertor.altaVista2SQL(where).substring(5);
            where=getWhereConvert(alltables,where,tables);
            if (basenodestring.length()+relstring.length()>0) {
                where=" AND ("+where+")";
            }
        } else {
            where="";
        }

        String query="";
        try {
            MultiConnection con=null;
            Statement stmt=null;
            try {
                con=mmb.getConnection();
                stmt=con.createStatement();
                if (basenodestring.length()+relstring.length()+where.length()>1) {
                    query="select "+distinct+" "+select+" from "+stables+" where "+basenodestring+relstring+where+" "+order;
                } else {
                    query="select "+distinct+" "+select+" from "+stables+" "+order;
                }
                log.debug("Query "+query);

                ResultSet rs=stmt.executeQuery(query);
                ClusterNode node;
                Vector results=new Vector();
                String tmp,prefix;
                while(rs.next()) {
                    // create a new VIRTUAL object and add it to the result vector
                    node=new ClusterNode(this,tables.size());
                    ResultSetMetaData rd=rs.getMetaData();
                    String fieldname;
                    for (int i=1;i<=rd.getColumnCount();i++) {
                        prefix=selectTypes.elementAt(i-1)+".";
                        fieldname=rd.getColumnName(i);
                        database.decodeDBnodeField(node,fieldname,rs,i,prefix);
                    }
                    node.initializing=false;
                    results.addElement(node);
                }
                //  return the results
                return results;
            } finally {
                mmb.closeConnection(con,stmt);
            }
        } catch (Exception e) {
            // something went wrong print it to the logs
            log.error("Query failed:"+query);
            log.error(Logging.stackTrace(e));
            return null;
        }
    }

    /**
     * Stores the tables/builder names used in the request for each field to return.
     * @param fields the list of requested fields
     * @return a list of prefixes of fieldnames
     */
    private Vector getSelectTypes(Vector alltables, String fields) {
        Vector result=new Vector();
        String val;
        int pos;
        for (Enumeration e=getFunctionParameters(fields).elements();e.hasMoreElements();) {
            val=(String)e.nextElement();
            int idx=val.charAt(0) - 'a';
            result.addElement(alltables.get(idx));
        }
        return result;
    }


    /**
     * Creates a full chain of table names.
     * This includes adding relation tables when not specified, and converting table names by
     * removing numeric extensions (such as people1,people2).
     * @param tables the original chain of tables
     * @return an expanded list of tablesnames
     */
    private Vector getAllTables(Vector tables, HashMap roles) {
        Vector alltables=new Vector();
        boolean lastrel=true;  // true: prevents the first table to be preceded by a relation table
        String orgtable,curtable;

        for (Enumeration e=tables.elements();e.hasMoreElements();) {
            orgtable=(String)e.nextElement();
            curtable= getTableName(orgtable);
            // check builder - should throw exception if builder doesn't exist ?
            MMObjectBuilder bul = mmb.getMMObject(curtable);
            if (bul==null) {
                // check if it is a role name. if so, use the builder of the rolename and
                // store a filter on rnumber.
                int rnumber = mmb.getRelDef().getNumberByName(curtable);
                if (rnumber==-1) {
                    log.error("getAllTables() : Specified builder "+curtable+" does not exist.");
                    return null;
                } else {
                    bul=mmb.getInsRel(); // dummy
                    roles.put(orgtable,new Integer(rnumber));
                }
            }
            if (bul instanceof InsRel) {
                alltables.addElement(orgtable);
                lastrel=!lastrel;  // toggle lastrel - allows for relations to be made to relationnnodes
            } else {
                // nonrel, nonrel
                if (!lastrel) {
                    alltables.addElement(mmb.getInsRel().getTableName());
                }
                alltables.addElement(orgtable);
                lastrel=false;
            }
        }
        return alltables ;
    }

    /**
     * Returns the number part of a tablename, provided it has one.
     * The number is the numeric digit appended at a name in order to make using a table more than once possible.
     * @param table name of the original table
     * @return An <code>int</code> containing the table number, or -1 if the table has no number
     */
    private int getTableNumber(String table) {
        char ch;
        ch=table.charAt(table.length()-1);
        if (Character.isDigit(ch)) {
            return Integer.parseInt(""+ch);
        } else {
            return -1;
        }
    }

    /**
     * Returns the name part of a tablename.
     * The name part is the table name minus the numeric digit appended at a name (if appliable).
     * @param table name of the original table
     * @return A <code>String</code> containing the table name
     */
    private String getTableName(String table) {
        char ch;
        ch=table.charAt(table.length()-1);
        if (Character.isDigit(ch)) {
            return table.substring(0,table.length()-1);
        } else {
            return table;
        }
    }

    /**
     * Returns the name part of a tablename, and convert it to a buidler name.
     * This will catch specifying a rolename in stead of a builder name when using relations.
     * @param table name of the original table
     * @return A <code>String</code> containing the table name
     */
    private String getTrueTableName(String table) {
        String tab=getTableName(table);
        int rnumber = mmb.getRelDef().getNumberByName(tab);
        if (rnumber!=-1) {
            return mmb.getRelDef().getBuilderName(getNode(rnumber));
        } else {
            return tab;
        }
    }

    /**
     * Determines the SQL-query version of a tablename.
     * This is done by searching for the appropriate tablename in a known list, and
     * calculating a name based on the index in that list.
     * @param alltables the tablenames known (used to determine the SQL tablename)
     * @param table the table name to convert
     * @return the SQL table name as a <code>String</code>
     */
    private String getSQLTableName(Vector alltables,String table) {
        int idx=alltables.indexOf(table);
        if (idx>=0) {
            return idx2char(idx);
        } else {
            return null;
        }
    }

    /**
     * Determines the SQL-query version of a field name.
     * Basically, this means replacing the table name specified in the user's field name by the one created
     * for the query,
     * @param alltables the tablenames known (used to determine the SQL tablename)
     * @param fieldname the field name to convert
     * @return the SQL field name as a <code>String</code>
     */
    private String getSQLFieldName(Vector alltables,String fieldName) {
        int pos=fieldName.indexOf('.'); // check if a tablename precedes the fieldname
        if (pos!=-1) {
            String table=fieldName.substring(0,pos); // the table
            String idxn=getSQLTableName(alltables,table);
            if (idxn==null) {
                log.error("getSQLFieldName(): The field '"+fieldName+"' has an invalid type specified");
            } else {
                String field=fieldName.substring(pos+1); // the field
                field=mmb.getDatabase().getAllowedField(field);
                return idxn+"."+field;
            }
        } else {
            // field has no type
            log.error("getSQLFieldName(): The field '"+fieldName+"' has no type specified");
        }
        return null;
    }

    /**
     * Retrieves fieldnames from a value (possibly a function name), and adds
     * these to a set of fieldnames.
     * @param alltables List of tablenames, used for deriving a SQL table name
     * @param val the value to retrieve the fieldname from
     * @param realfields the set to add the fieldnames to
     */
    private void obtainSelectField(Vector alltables, String val, HashSet realfields) {
        // strip the function(s)
        int pos=val.indexOf('(');
        if (pos!=-1) {
            String result="";
            val=val.substring(pos+1);
            pos=val.lastIndexOf(')');
            if (pos!=-1) {
                val=val.substring(0,pos);
            }
            Vector fields=getFunctionParameters(val);
            for (int i=0; i<fields.size(); i++) {
                obtainSelectField(alltables,(String)fields.get(i), realfields);
            }
        } else {
            if (!Character.isDigit(val.charAt(0))) {
                String field=getSQLFieldName(alltables,val);
                if (field!=null) {
                    realfields.add(field);
                }
            }
        }
    }

    /**
     * Creates a select string for the Multi level query.
     * This consists of a list of fieldnames, preceded by a tablename.
     * @param alltables the tablenames to use
     * @param rfields the fields that were requested
     * @return a select <code>String</code>
     */
    protected String getSelectString(Vector alltables,Vector rfields) {
        return getSelectString(alltables, null,rfields,false);
    }

    /**
     * Creates a select string for the Multi level query.
     * This consists of a list of fieldnames, preceded by a tablename.
     * @param alltables the tablenames to use
     * @param originaltables the original tablenames that were specified
     * @param rfields the fields that were requested
     * @param includeAllReferences if <code>true</code>, for each trable specified in
     *        <code>originaltables</code>, the number field will be added to the
     *        returned select string. If <code>false</code>, no additional fields
     *        will be added (this should change : fields should be added, but
     *        only for those tables for which fields have been requested).
     * @return a select <code>String</code>
     */
    protected String getSelectString(Vector alltables,Vector originaltables,
                                     Vector rfields, boolean includeAllReferences) {
        String result=null;
        String val,field;
        HashSet realfields = new HashSet();
        for (Enumeration r=rfields.elements();r.hasMoreElements();) {
            val=(String)r.nextElement();
            obtainSelectField(alltables,val,realfields);
        }
        // add numbers for all originally specified tables
        if (includeAllReferences) {
            for (Enumeration r=originaltables.elements();r.hasMoreElements();) {
                val=(String)r.nextElement();
                realfields.add(numberOf(getSQLTableName(alltables,val)));
            }
        }
        for (Iterator r=realfields.iterator();r.hasNext();) {
            val=(String)r.next();
            if (result==null) {
              result=val;
            } else {
              result+=", "+val;
            }
        }
        return result;
    }

    /**
     * Creates an order string for the Multi level query.
     * This consists of a list of fieldnames (preceded by a tablename), with an ascending or descending order.
     * @param alltables the tablenames to use
     * @param orders the fields that were requested
     * @param direction the direction of each order field ("UP" or "DOWN")
     * @return a order <code>String</code>
     */
    private String getOrderString(Vector alltables,Vector orders,Vector direction) {
        String result="";
        String val,field,dir;
        int opos;

        if (orders==null) return result.toString();
        // Convert direction table
        for (int pos=0; pos<direction.size(); pos++) {
            val=(String)direction.elementAt(pos);
            if (val.equalsIgnoreCase("DOWN")) {
                direction.setElementAt("DESC",pos); // DOWN is DESC
            } else {
                direction.setElementAt("ASC",pos);  // UP is ASC
            }
        }

        opos=0;
        for (Enumeration r=orders.elements();r.hasMoreElements();opos++) {
            val=(String)r.nextElement();
            field=getSQLFieldName(alltables,val);
            if (field==null) {
                return null;
            } else {
                if (!result.equals("")) {
                    result+=", ";
                } else {
                    result+=" ORDER BY ";
                }
                if (opos<direction.size()) {
                    dir=(String)direction.elementAt(opos);
                } else {
                    dir=(String)direction.elementAt(0);
                }
                result+=field+" "+dir;
            }
        }
        return result;
    }

    /**
     * Creates a WHERE clause for the Multi level query.
     * This involves replacing fieldnames in the clouse with those fit for the SQL query.
     * @param alltables the tablenames to use
     * @param string the original where clause
     * @param tables ?
     * @return a where clause <code>String</code>
     */
    private String getWhereConvert(Vector alltables,String where,Vector tables) {
        String atable,table,result=where;
        int cx;
        char ch;

        for (Enumeration e=tables.elements();e.hasMoreElements();) {
            atable=(String)e.nextElement();
            table = getSQLTableName(alltables,atable);

            // This translates the long tablename to the short one
            // i.e. people.account to a.account.
            cx=result.indexOf(atable+".",0);
            while (cx!=-1) {
                if (cx>0)
                    ch=result.charAt(cx-1);
                else
                    ch=0;
                if (!isTableNameChar(ch)) {
                    int fx=cx+atable.length()+1;
                    int lx;
                    for (lx=fx;
                         lx < result.length() && (Character.isLetterOrDigit(result.charAt(lx)) || result.charAt(lx) == '_');
                         lx++);
                    result=result.substring(0,cx)+
                           table+"."+
                           mmb.getDatabase().getAllowedField(result.substring(fx,lx))+
                           result.substring(lx);
                }
                cx=result.indexOf(atable+".",cx+1);
            }
            log.debug("getWhereConvert for table "+atable+"|"+result+"|");
        }
        return result;
    }

    /**
     * This method defines what is 'allowed' in tablenames.
     * Multilevel uses this to find out what is a tablename and what not
     */
    private boolean isTableNameChar(char ch) {
        return  (ch=='_') || Character.isLetterOrDigit(ch);
    }

    /**
     * This method defines what is 'allowed' in tablenames.
     * Multilevel uses this to find out what is a tablename and what not
     */
    protected String getTableString(Vector alltables) {
        return getTableString(alltables, new HashMap());
    }

    /**
     * This method defines what is 'allowed' in tablenames.
     * Multilevel uses this to find out what is a tablename and what not
     */
    protected String getTableString(Vector alltables, HashMap roles) {
        StringBuffer result=new StringBuffer("");
        String val;
        int idx=0;

        for (Enumeration r=alltables.elements();r.hasMoreElements();) {
            val=(String)r.nextElement();
            if (!result.toString().equals("")) result.append(", ");

            Integer role=(Integer)roles.get(val);
            if (role!=null) {
                val=mmb.getRelDef().getBuilderName(getNode(role.intValue()));
                result.append(mmb.baseName+"_"+val);
            } else {
                result.append(mmb.baseName+"_"+getTableName(val));
            }

            result.append(" "+idx2char(idx));
            idx++;
        }
        return result.toString();
    }

    // get a reference to the number field in a table
    private String numberOf(String table) {
        return table+"."+mmb.getDatabase().getNumberString();
    };

    /**
     * Creates a condition string which checks the relations between nodes.
     * The string can then be added to the query's where clause.
     * @param alltables the tablenames to use
     * @return a condition as a <code>String</code>
     */
    protected String getRelationString(Vector alltables) {
        return getRelationString(alltables, SEARCH_EITHER, new HashMap());
    }

    /**
     * Creates a condition string which checks the relations between nodes.
     * The string can then be added to the query's where clause.
     * @param alltables the tablenames to use
     * @param searchdir the directionality option to use
     * @return a condition as a <code>String</code>
     */
    protected String getRelationString(Vector alltables, int searchdir, HashMap roles) {
        StringBuffer result=new StringBuffer("");
        int siz;
        String src,dst;
        int so,ro;
        TypeDef typedef;
        TypeRel typerel;
        InsRel insrel;
        RelDef reldef;

        typedef=mmb.getTypeDef();
        typerel=mmb.getTypeRel();
        reldef=mmb.getRelDef();
        insrel=mmb.getInsRel();
        siz=alltables.size()-2;

        log.debug("SEARCHDIR="+searchdir);


        for (int i=0;i<siz;i+=2) {
            boolean desttosrc=false;
            boolean srctodest=false;
            src=getTableName((String)alltables.elementAt(i));                            // name of the source table
            dst=getTableName((String)alltables.elementAt(i+2));                        // name of destination table

            Integer rnum = (Integer)roles.get((String)alltables.elementAt(i+1));  // role ?
            so=typedef.getIntValue(src);                                // get the number of the source
            ro=typedef.getIntValue(dst);                                // get the number of the destination

            if (!result.toString().equals("")) result.append(" AND ");

            // check if  a definite rnumber was requested...
            if (rnum!=null) {
                result.append(idx2char(i+1)+".rnumber="+rnum.intValue()+" AND ");
                srctodest=(searchdir!=SEARCH_SOURCE) && typerel.reldefCorrect(so,ro,rnum.intValue());
                desttosrc=(searchdir!=SEARCH_DESTINATION) && typerel.reldefCorrect(ro,so,rnum.intValue());
            } else {
                MMObjectNode typenode;
                for (Enumeration e=typerel.getAllowedRelations(so, ro); e.hasMoreElements(); ) {
                    // get the allowed relation definitions
                    typenode = (MMObjectNode)e.nextElement();
                    desttosrc= (searchdir!=SEARCH_DESTINATION) && (desttosrc || typenode.getIntValue("snumber")==ro);
                    srctodest= (searchdir!=SEARCH_SOURCE) && (srctodest || typenode.getIntValue("snumber")==so);
                    if (desttosrc && srctodest) break;
                }
            }

            // check for directionality if supported
            String dirstring="";
            if (InsRel.usesdir && (searchdir!=SEARCH_ALL)) {
                dirstring=" AND "+idx2char(i+1)+".dir<>1";
            }

            if (desttosrc && srctodest && (searchdir==SEARCH_EITHER)) { // support old
                desttosrc=false;
            }
            if (desttosrc) {
                // there is a typed relation from destination to src
                if (srctodest) {
                    // there is ALSO a typed relation from src to destination - make a more complex query
                    result.append(
                           "(("+numberOf(idx2char(i))+"="+idx2char(i+1)+".snumber AND "+
                                numberOf(idx2char(i+2))+"="+idx2char(i+1)+".dnumber ) OR ("+
                                numberOf(idx2char(i))+"="+idx2char(i+1)+".dnumber AND "+
                                numberOf(idx2char(i+2))+"="+idx2char(i+1)+".snumber"+dirstring+"))");
                } else {
                    // there is ONLY a typed relation from destination to src - optimized query
                    result.append(numberOf(idx2char(i))+"="+idx2char(i+1)+".dnumber AND "+
                                numberOf(idx2char(i+2))+"="+idx2char(i+1)+".snumber"+dirstring);
                }
            } else {
                // there is no typed relation from destination to src (assume a relation between src and destination)  - optimized query
                result.append(numberOf(idx2char(i))+"="+idx2char(i+1)+".snumber AND "+
                            numberOf(idx2char(i+2))+"="+idx2char(i+1)+".dnumber");
            }

        }
        return result.toString();
    }

    /**
     * Converts an index to a one-character string.
     * I.e. o becomes 'a', 1 becomes 'b', etc.
     * This is used to map the tables in a List to alternate names (using their index in the list).
     * @param idx the index
     * @return the one-letter name as a <code>String</code>
     */
    protected String idx2char(int idx) {
        return ""+new Character((char)('a'+idx));
    }

    private String getNodeString(String bstr,Vector snodes) {
        String snode,str;
        StringBuffer bb=new StringBuffer();
        snode=(String)snodes.elementAt(0);
        if (snodes.size()>1) {
            bb.append(bstr+"."+mmb.getDatabase().getNumberString()+" in ("+snode);
            for (int i=1;i<snodes.size();i++) {
                str=(String)snodes.elementAt(i);
                bb.append(","+str);
            }
            bb.append(")");
        } else {
            bb.append(bstr+"."+mmb.getDatabase().getNumberString()+"="+snode);
        }
        return bb.toString();
    }

    /**
     * Get text from a blob field. the text is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return a <code>String</code> containing the contents of a field as text
     */
    public String getShortedText(String fieldname,int number) {
        String buildername=getBuilderNameFromField(fieldname);
        if (buildername.length()>0) {
            MMObjectBuilder bul=mmb.getMMObject(buildername);
            return bul.getShortedText(getFieldNameFromField(fieldname),number);
        }
        return null;
    }


    /**
     * Get binary data of a database blob field. the data is cut if it is to long.
     * @param fieldname name of the field
     * @param number number of the object in the table
     * @return an array of <code>byte</code> containing the contents of a field as text
     */
    public byte[] getShortedByte(String fieldname,int number) {
        String buildername=getBuilderNameFromField(fieldname);
        if (buildername.length()>0) {
            MMObjectBuilder bul=mmb.getMMObject(buildername);
            return bul.getShortedByte(getFieldNameFromField(fieldname),number);
        }
        return null;
    }

}
