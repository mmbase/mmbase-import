/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* NodeListTag, provides functionality for listing objects stored in MMBase
*
* @author Kees Jongenburger
**/
public class NodeListTag extends AbstractNodeProviderTag implements BodyTag, ListItemInfo{
    //this class is growing to big.. 
    //need to look at the MMCI again. I am sure MMCI can handle more nows

    
    private static Logger log = Logging.getLoggerInstance(NodeListTag.class.getName());
    
    private String nodesString=null;
    private String typeString=null;
    private String whereString=null;
    private String sortedString=null;
    private String directionString=null;
    private String distinctString=null;
    private Integer max = null;
    private String searchString=null;
    private String commandString = null;
    
    private int    offset   = 0;
    private boolean changed = true;
    
    /**
    * used when nesting tags to make a differance between the variables
    * exported from the parent tag and the on expored from this tag
    *
    **/
    // private String prefixString=null; deprecated
    
    /**
    * private data member to hold an enumeration
    * of the values to return. this variable will be set in
    * the start tag, and will be used to fill de return variables
    * for every iteration.
    **/
    protected NodeIterator returnValues;
    
    
    /**
    * please help we use a NodeIterator to keep the Nodes used
    * for each iteration but wil still want to know
    * in the loop if we are talking about the first/last node
    * currentItemIndex is updated by fillVars
    * listSize is set in
    * used by size() isFirst() and isLast()
    **/
    private int listSize = 0;
    private int currentItemIndex= -1;
    
    public int getIndex() {
        return currentItemIndex;
    }
    
    
    /**
    * @deprecated
    **/
    
    public void setPrefix(String prefix){
        //this.prefixString = prefix;
        setId(prefix);
    }
    
    /**
    * @return the prefix to used for this tag if the value is set then the value is returned else an empty string is returned
    * @deprecated Use getId
    **/
    public String getPrefix(){
        return getId();
    }
    
    
    public void setCommand(String command) {
        commandString = command;
    }
    
    
    /**
    * @param nodes a node or  a commad separated list of nodes
    * to fit history if the value is -1 this is defined as null
    * it is not clear to me when multiple nodes are used and a what
    * point we need to decide is the node is a number for the moment
    * the whole node part is not interpreted at all
    **/
    public void setNodes(String nodes){
        // parse/map the nodes they can be params, sessions or aliases
        // instead of just numbers
        nodesString=parseNodes(nodes);
    }
    
    public void setNode(String node){
        nodesString=parseNodes(node);
    }
    
    /**
    * @param type a comma separated list of nodeManagers
    **/
    public void setType(String type){
        this.typeString = type;
    }
    
    /**
    * @param where the selection query for the object we are looking for
    * the query has two syntax depending on the amount of node managers
    * inserted in the type field
    **/
    public void setWhere(String where){
        log.debug("where " + where);
        this.whereString = where;
    }
    
    /**
    * @param search The search parameter, determines how directionality affects the search.
    *               possible values are <code>"both"</code>, <code>"destination"</code>,
    *                      <code>"source"</code>, and <code>"all"</code>
    **/
    public void setSearch(String search){
        this.searchString = search;
    }
    
    /**
    * @param sorted A comma separated list of fields on witch the returned
    * nodes should be sorted
    **/
    public void setSorted(String sorted){
        this.sortedString = sorted;
    
    }
    
    /**
    * @param direction the selection query for the object we are looking for
    * direction
    **/
    public void setDirection(String direction){
        this.directionString = direction;
    }
    
    /**
    * @param distinct the selection query for the object we are looking for
    **/
    public void setDistinct(String distinct){
        this.distinctString = distinct;
    }
    
    /**
    * @param max the max number of values returned
    **/
    public void setMax(Integer max){
        this.max = max;
    } 
    
    /**
    * @param offset
    **/
    public void setOffset(int o) {
        offset = o;
    }
    
    /**
    *
    **/
    public int doStartTag() throws JspTagException{
        //this is where we do the seach
        
        currentItemIndex= -1;  // reset index
        NodeList nodes = null;
        //we hope at the end of this funtion
        //this Vector is filled with nodes after that we create an enumeration of it
        //and use one for each iteration
        String nodesSearchString= (nodesString == null)? "-1" : nodesString;
        String nodeManagers = typeString;
        String searchNodes= (nodesString == null)? "-1" : nodesString;
        String searchFields= fields; // why copying it?
        String searchWhere= whereString;
        String searchSorted= sortedString;
        String searchDirection= directionString;
        String searchSearch= searchString;
        boolean searchDistinct= (distinctString != null);
        
        String action= "none";
        log.debug("where " + whereString);
        try {
            
            if (commandString != null) {
                if (stringSplitter(typeString,",").size() != 1) { // cannot be done on multilevel: keesj what ?
                    throw new JspTagException ("Cannot do a multilevel with a command");
                }
                action = "command " + commandString + " on NodeManager " + typeString;
                log.debug(action);
                nodes = getCloudProviderVar().getNodeManager(typeString).getList(commandString, null);
                if (searchSorted != null) {
                    throw new JspTagException ("Cannot do a sort on a command");
                }
                if (searchWhere != null) {
                    throw new JspTagException ("Cannot do a where on a command");
                }
            
            }
            else if (stringSplitter(typeString,",").size() > 1){ // multilevel
                action = "multilevel search";
                nodes = getCloudProviderVar().getList(nodesSearchString,nodeManagers,searchFields,searchWhere,searchSorted,searchDirection,searchSearch,searchDistinct);
            } else {
                action = "no multilevel search";
                boolean hasSearch = (searchWhere != null);
                boolean hasNode   = (nodesString != null);
                boolean hasSort   = (searchSorted != null);
                if (hasSearch) {
                    action = "has a where-search";
                    if (hasNode){
                        action = "has a node";
                        //first hack, the MMCI does not provide a search on a node
                        //but this is what we need here, so we expand the query
                        //to resctrict the search on the node given
                        
                        //keesj@Thu May 17 05:54:08 CEST 2001-> is this still true?
                        searchWhere = "(" + searchWhere +") and ( number = " + getCloudProviderVar().getNode(nodesString).getNumber() + " )";
                    }
                    action = "search relations with start node (" + nodesString + ") using a search " + searchWhere;
                    log.debug(action);
                    NodeManager nodeManager = getCloudProviderVar().getNodeManager(nodeManagers);
                    
                    //            boolean direction = ("UP".equals(searchDirection))? true: false;
                    //            nodes= nodeManager.getList(searchWhere,null,direction);
                    log.debug("search " + searchWhere + " sorted "  + searchSorted + " direction " + searchDirection);
                    nodes= nodeManager.getList(searchWhere, searchSorted, searchDirection);
                } else if (hasNode){
                    if (stringSplitter(searchNodes,",").size() > 1){ 
                        throw new JspTagException ("Cannot have multiple starting points on a getRelatedNodes");
                    }
                    action = "list all relations of node("+ searchNodes +") of type("+ typeString + ")";
                    log.debug(action);
                    if (hasSort) { // a not-so-nice hack.
                        log.warn("Trying to sort on non-multilevel list. Bad for performance.");
                        // get the type of this one node:
                        Node baseNode = getCloudProviderVar().getNode(searchNodes);
                        NodeManager baseNodeManager = baseNode.getNodeManager();
                        
                        // look which types are wanted:
                        Vector nodeManagersVector = stringSplitter(nodeManagers);
                        if (nodeManagersVector.size() == 0) {
                            throw new JspTagException ("Must specify at least one NodeManager");
                        }
                        // for a getList the first type must be the type of the node.
                        // So if it is not there, we can easily add it:
                        if (! ((String)nodeManagersVector.get(0)).equalsIgnoreCase(baseNodeManager.getName())) {
                            nodeManagersVector.add(0, baseNodeManager.getName());
                        }
                        
                        // now there must be only two nodemanagers:
                        if (nodeManagersVector.size() != 2) {
                            throw new JspTagException("Must be 2 nodemanagers");
                        } 
                        nodeManagers = nodeManagersVector.get(0) + "," + nodeManagersVector.get(1);
                        
                        
                        // The fieldlist must be simply the numbers...
                        
                        String numbers = nodeManagersVector.get(1) + ".number";
                        
                        // check if searchSorted is ok:
                        StringTokenizer st = new StringTokenizer(searchSorted, ".");
                        st.nextToken();
                        if (! st.hasMoreTokens()) {
                            searchSorted = nodeManagersVector.get(1) + "." + searchSorted;
                        } else {
                            String nm = (String) st.nextToken();
                            if (! nm.equalsIgnoreCase((String)nodeManagersVector.get(1))) {
                                throw new JspTagException("Indicated wrong nodemanager in search " + searchSorted + ", must be " + nodeManagersVector.get(1)); 
                            }
                        }
                        nodes = getCloudProviderVar().getList("" + baseNode.getNumber(), nodeManagers, numbers, null, searchSorted, searchDirection, null, false); 
                        
                        // now make normal nodes of it.                       
                        NodeIterator i = nodes.nodeIterator();
                        while(i.hasNext()){
                            i.set(getCloudProviderVar().getNode(i.nextNode().getStringValue(numbers))); 
                        }
                    } else {
                        nodes = getCloudProviderVar().getNode(searchNodes).getRelatedNodes(typeString);
                    
                    }
                
                } else {
                    action = "list all objects of type("+ typeString + ")";
                    NodeManager nodeManager = getCloudProviderVar().getNodeManager(nodeManagers);
                    //            boolean direction = ("UP".equals(searchDirection))? true: false;
                    //            nodes= nodeManager.getList(null,null,direction);
                    nodes= nodeManager.getList(searchWhere, searchSorted, searchDirection);
                }
            }
        } catch (NullPointerException npe){
            showListError(npe, nodesSearchString, nodeManagers, searchNodes, searchFields, searchWhere, searchSorted,searchDirection,searchSearch,searchDistinct,max.toString(),action);
        }
        
        
        if (max != null || offset > 0) { // for the moment max can only be here because
            //there is no other way to tell the MMCI that a list sould be shorter
            int maxx = (max == null ? nodes.size() - 1 : max.intValue());
            int to = maxx + offset;
            
            listSize = nodes.size();
            if (to >= listSize) {
                to = listSize;
            }
            if (offset >= listSize) {
                offset = listSize - 1;
            }
            if (offset < 0) {
                offset = 0;
            }
            nodes=nodes.subNodeList(offset, to);
            
            returnValues = nodes.nodeIterator();
        } else {
            listSize = nodes.size();
            returnValues = nodes.nodeIterator();
        }
        // if we get a result from the query
        // evaluate the body , else skip the body
        if (returnValues.hasNext())
            return EVAL_BODY_TAG;
        return SKIP_BODY;
    }
    
    
    public int doAfterBody() throws JspTagException {
        if (returnValues.hasNext()){
            doInitBody();
            return EVAL_BODY_TAG;
        } else {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }
            return SKIP_BODY;
        }
    
    }
    
    
    private String previousValue = null; // static voor doInitBody
    public void doInitBody() throws JspTagException {
        if (returnValues.hasNext()){
            currentItemIndex ++;
            Node next = returnValues.nextNode();
            if (sortedString != null) { // then you can also ask if 'changed' the node
                // look only at first field of sorted for the /moment.
                String f = (String)stringSplitter(sortedString).get(0);
                String value = "" + next.getValue(f); // cannot cast  to String, since it can also be e.g. Integer.
                if (previousValue !=null) {
                    if (value.equals(previousValue)) {
                        changed = false;
                    } else {
                        changed = true;
                    }
                }
                previousValue = value;
            }
            setNodeVar(next);
            fillVars();
        }
    }
    
    
    private String parseNodes(String nodes) {
        // should be a StringTokenizer have to check mmci how
        // multinodes are handled
        if (nodes.startsWith("param(")) {
            String name=nodes.substring(6,nodes.length()-1);
            HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
            return(req.getParameter(name));
        }
        return(nodes);
    }
    
    public boolean isFirst(){
        return (currentItemIndex == 0);
    }
    
    /** 
     * If you order a list, then this the 'changed' property will be
     * true if the field on which you order changed value.
     **/
    public boolean isChanged() {
        return changed;
    }
    
    public int size(){
        return listSize;
    }
    
    public boolean isLast(){
        return (! returnValues.hasNext());
    }
    
    private void showListError( Exception npe,       String nodesSearchString,
        String nodeManagers, String searchNodes,
        String searchFields, String searchWhere,
        String searchSorted, String searchDirection,
        String searchSearch, boolean searchDistinct,
        String maxString,    String action             ) throws JspTagException {
        StringBuffer sb = new StringBuffer();
        sb.append("nodes=" + nodesSearchString);
        sb.append("\n");
        sb.append("modeManagers=" + nodeManagers);
        sb.append("\n");
        sb.append("searchNodes=" + searchNodes);
        sb.append("\n");
        sb.append("searchFields=" + searchFields);
        sb.append("\n");
        sb.append("searchWhere=" + searchWhere);
        sb.append("\n");
        sb.append("searchSorted=" + searchSorted);
        sb.append("\n");
        sb.append("searchDirection=" + searchDirection);
        sb.append("\n");
        sb.append("searchDistinct=" + searchDistinct);
        sb.append("\n");
        sb.append("searchSearch=" + searchSearch);
        sb.append("\n");
        sb.append("max=" + maxString);
        sb.append("\n");
        sb.append("Base on the input the taglib did");
        sb.append("\n");
        sb.append(action);
        sb.append("\n");
        // now do some basic stuff to find the error
        
        //go to each node in the nodes string and look if they exist
        if (nodesString != null){
            Enumeration nodeList = stringSplitter(nodesString,",").elements();
            while(nodeList.hasMoreElements()){
                String nodeString = (String)nodeList.nextElement();
                try {
                    int nodeNumber = Integer.parseInt(nodeString);
                    Node node = getCloudProviderVar().getNode(nodeNumber);
                    if (node == null){
                        sb.append("ERROR: node(" + nodeString +") does not exist\n");
                    }
                } catch (NumberFormatException e){
                    //the node is probabely a alias
                    Node node = getCloudProviderVar().getNodeByAlias(nodeString);
                    if (node == null){
                        sb.append("ERROR: node with alias("+ nodeString +") does not exist\n");
                    }
                }
            }
        }
        //take a look at the nodeManagers
        Enumeration managerList =  stringSplitter(typeString,",").elements();
        while(managerList.hasMoreElements()){
            String managerName = (String)managerList.nextElement();
            try {//note nodeManager sould  realy not throw a nullPointerException
                NodeManager nodeManager = getCloudProviderVar().getNodeManager(managerName);
                if (nodeManager == null){
                    sb.append("ERROR: NodeManager with name("+ managerName +") does not exist\n");
                }
            } catch (NullPointerException nullpe){
                sb.append("ERROR: NodeManager with name("+ managerName +") does not exist , nullpointer exception while debuging ARGGGG\n");
            }
        }
        throw new JspTagException("NodeList error\n" + sb.toString());
    }
}

