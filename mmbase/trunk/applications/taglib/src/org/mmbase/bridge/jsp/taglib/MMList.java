/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.*;

/**
 * MMList
 * @author Kees Jongenburger
 **/
public class MMList extends MMTaglib
    implements BodyTag{
    
    
    private String nodesString=null;
    private String typeString=null;
    private String fieldsString=null;
    private String whereString=null;
    private String sortedString=null;
    private String directionString=null;
    private String distinctString=null;
    private String maxString=null;

    /**
     * used when nesting tags to make a differance between the variables
     * exported from the parent tag and the on expored from this tag
     **/
    private String prefixString=null;

    public static boolean debug = true;
    
    //simple method to dump debug data in to System.err
    public void debug(String debugdata){
	System.err.println("MMList:" + debugdata);
    }

    /**
     * private data member to hold an enumeration
     * of the values to return. this variable will be set in
     * the start tag, and will be used to fill de return variables
     * for every iteration.
     **/
    private Enumeration returnValues;

    
    /**
     * implementation of TagExtraInfo return values declared here
     * should be filled at one point, currently fillVars is responsible for 
     * that ant gets called before every 
     **/
    public VariableInfo[] getVariableInfo(TagData data){
	VariableInfo[] variableInfo =    null;
	//this method is called /before/ the values are set
	//so we can not use the datamembers in this class
	//but the TagData provides the necesary data
	//in effect we have to parse the data twice
	//once here and onces specific attributes are set
	//maybe this can be done better I do not know
	
	// prefix is used when nesting tags to be able to make a difference
	// between the variable declared in the root tag ant this tag
	String prefix = "";
	if (data.getAttribute("prefix") != null){
	    prefix= "" + data.getAttribute("prefix");
	}

	//the tag parameter fields defines what variables should be available
	//within the body of the tag currently the only thing we do here
	//is return the a Virtual node and some variables
	//if the variable has dots in it they will be replaced by underscores
	// <%= fieldName %> or <%= node.getValue("fieldName") %>
	if (data.getAttribute("fields")!= null){
	    Vector fields  = StringSplitter(data.getAttribute("fields").toString(),",");
		
	    //size +1 since we return every variable + one hashTable
	    //for every iteration
	    //variableInfo =    new VariableInfo[fields.size() + 1];
	    variableInfo =    new VariableInfo[(fields.size()*2) + 1];
	    int j=0;
	    for (int i =0 ; i < fields.size(); i++){
		String field = (String)fields.elementAt(i);
		//it would be nice to return Integer is a field is of that type
		variableInfo[j++] = new VariableInfo(prefix + getSimpleReturnValueName(field),"java.lang.String",true,VariableInfo.NESTED);
		variableInfo[j++] = new VariableInfo("item"+(i+1),"java.lang.String",true,VariableInfo.NESTED);
		    
	    }
	    //add the Hashtable , name it node to confuse people :)
	    variableInfo[j++] = new VariableInfo(prefix + "node","org.mmbase.bridge.Node",true,VariableInfo.NESTED);
	} 
	return variableInfo;
    }
    
    
    public void setPrefix(String prefix){
        this.prefixString = prefix;
    }

    /**
      * @return the prefix to used for this tag if the value is set then the value is returned else an empty string is returned
      **/
    public String getPrefix(){
        if (prefixString == null){
	    return "";
        }
        return prefixString;
    }

    
    /**
     * @param nodes a node or  acomma separated list of nodes
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
     * @param type a comma separated list of nodeTypes
     **/
    public void setType(String type){
	this.typeString = type;
    }
    /**
     * @param fields a comma separated list of fields
     **/
    public void setFields(String fields){
	this.fieldsString = fields;
    }

    /**
     * @param where the selection query for the object we are looking for
     **/
    public void setWhere(String where){
	this.whereString = where;
    }

    /**
     * @param sorted 
     **/
    public void setSorted(String sorted){
	this.sortedString = sorted;
    }

    /**
     * @param direction the selection query for the object we are looking for
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
    public void setMax(String max){
	this.maxString = max;
    }
    
    /**
     *
     **/
    public int doStartTag() throws JspException{
	//this is where we do the seach

	Vector nodes =  new Vector();//we hope at the end of this funtion
	//this Vector is filled with nodes after that we create an enumeration of it
	//and use one for each iteration
	String nodesSearchString= (nodesString == null)? "-1" : nodesString;
	String nodeManagers = typeString;
	String searchNodes= (nodesString == null)? "-1" : nodesString;
	String searchFields= fieldsString;
	String searchWhere= whereString;
	String searchSorted= sortedString;
	String searchDirection= sortedString;
	boolean searchDistinct= (distinctString == null)? false : true;

	String action= "none";
	try {
		boolean multilevel = (StringSplitter(typeString,",").size() > 1)? true : false;
		if (multilevel){
		    action = "multilevel search";
		    nodes.addAll(getDefaultCloud().search(nodesSearchString,nodeManagers,searchFields,searchWhere,searchSorted,searchDirection,searchDistinct));
		} else {
		    boolean hasSearch = (searchWhere == null)? false: true;
		    boolean hasNode = (nodesString == null)? false: true;
		    if (hasSearch){
			if (hasNode){
			    //first hack, the MMCI does not provide a search on a node
			    //but this is what we need here, so we expand the query
			    //to resctrict the search on the node given
			    searchWhere = "(" + searchWhere +") and ( number = " + nodesString + " )";
			}
		    	action = "search relations with start node (" + nodesString + ") using a search";
			NodeManager nodeManager = getDefaultCloud().getNodeManager(nodeManagers);
			boolean direction = ("UP".equals(searchDirection))? true: false;
			List list = nodeManager.search(searchWhere,null,direction);
			if (list != null){
			    nodes.addAll(list);
			}
		    } else if (hasNode){
		    	action = "list all relations of node("+ searchNodes +") of type("+ typeString + ")";
			try {
				Node node  = getDefaultCloud().getNode(Integer.parseInt(searchNodes));
				nodes.addAll(node.getRelatedNodes(typeString));
			} catch(Exception e) {
				Node node  = getDefaultCloud().getNode(searchNodes);
				System.out.println("NODES="+node.getRelatedNodes(typeString));
				nodes.addAll(node.getRelatedNodes(typeString));
			}
		    } else { 
		    	action = "list all objects of type("+ typeString + ")";
			NodeManager nodeManager = getDefaultCloud().getNodeManager(nodeManagers);
			boolean direction = ("UP".equals(searchDirection))? true: false;
			List list = nodeManager.search(null,null,direction);
			nodes.addAll(list);
		    }
		}
	} catch (NullPointerException npe){
		showListError(npe,nodesSearchString,nodeManagers,searchNodes,searchFields,searchWhere,searchSorted,searchDirection,searchDistinct,maxString,action);
	}


	if (maxString != null){ // for the moment max can only be here because
				//there is no other way to tell the MMCI that a list sould be shorter
	    try {
		int max = Integer.parseInt(maxString);
		if (max < nodes.size()){
				//very bag coding
		    returnValues = new Vector(nodes.subList(0,max)).elements();
		}else {
		    returnValues = nodes.elements();
		}
	    } catch (NumberFormatException e){
		throw new JspException ("MAX Field in tag is no a number");
	    }
	} else {
	    returnValues = nodes.elements();
	}
	// if we get a result from the query 
	// evaluate the body , else skip the body
	if (returnValues.hasMoreElements())
	    return EVAL_BODY_TAG;
	return SKIP_BODY;
    }


    public void doInitBody() throws JspException {
	fillVars();
    }

    public int doAfterBody() throws JspException {
	try {
	    if (returnValues.hasMoreElements()){
		fillVars();
		return EVAL_BODY_TAG;
	    } else {
		bodyOut.writeOut(bodyOut.getEnclosingWriter());
		return Tag.SKIP_BODY;
	    }
	} catch (IOException ioe){
	    throw new JspTagException(ioe.toString());
	}
    }

    private void fillVars(){
	if (returnValues.hasMoreElements()){
	    Node node = (Node)returnValues.nextElement();
	    String prefix = getPrefix();
	    Enumeration returnFieldEnum = StringSplitter(fieldsString,",").elements();
	    int j=1;	
	    while (returnFieldEnum.hasMoreElements()){
		String field = (String)returnFieldEnum.nextElement();
	    	pageContext.setAttribute(getPrefix() + getSimpleReturnValueName(field) ,"" + node.getValue(field));
	    	pageContext.setAttribute("item"+(j++) ,"" + node.getValue(field));
	    }
    	    pageContext.setAttribute(getPrefix() + "node" ,node);
	}
    }


    /**
     * simple util method to split comma separated values
     * to a vector
     * @param string the string to split
     * @param delimiter 
     * @return a Vector containing the elements, the elements are also trimed
     **/
    private Vector StringSplitter(String string,String delimiter){
	Vector retval = new Vector();
	StringTokenizer st = new StringTokenizer(string,delimiter);
	while(st.hasMoreTokens()){
	    retval.addElement(st.nextToken().trim());
	}
	return retval;
    }

    private String getSimpleReturnValueName(String fieldName){
	return fieldName.replace('.','_');
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


    private void showListError(Exception npe,String nodesSearchString,String nodeManagers,String searchNodes,String searchFields,String searchWhere, String searchSorted,String searchDirection, boolean searchDistinct,String maxString,String action) throws JspException { 
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
		sb.append("max=" + maxString);
		sb.append("\n");
		sb.append("Base on the input the taglib did");
		sb.append("\n");
		sb.append(action);
		sb.append("\n");
		// now do some basic stuff to find the error

		//go to each node in the nodes string and look if they exist
		if (nodesString != null){
		Enumeration nodeList = StringSplitter(nodesString,",").elements();
			while(nodeList.hasMoreElements()){
			    String nodeString = (String)nodeList.nextElement();
			    try {
				int nodeNumber = Integer.parseInt(nodeString);
				Node node = getDefaultCloud().getNode(nodeNumber);
				if (node == null){
					sb.append("ERROR: node(" + nodeString +") does not exist\n");
				}
			    } catch (NumberFormatException e){
				//the node is probabely a alias
				Node node = getDefaultCloud().getNode(nodeString);
				if (node == null){
					sb.append("ERROR: node with alias("+ nodeString +") does not exist\n");
				}
			    }
			}
		}
		//take a look at the nodeManagers
		 Enumeration managerList =  StringSplitter(typeString,",").elements();
		 while(managerList.hasMoreElements()){
				String managerName = (String)managerList.nextElement();
				try {//note nodeManager sould  realy not throw a nullPointerException
					NodeManager nodeManager = getDefaultCloud().getNodeManager(managerName);
					if (nodeManager == null){
					sb.append("ERROR: NodeManager with name("+ managerName +") does not exist\n");
					}
				} catch (NullPointerException nullpe){
					sb.append("ERROR: NodeManager with name("+ managerName +") does not exist , nullpointer exception while debuging ARGGGG\n");
				}
		 }
		throw new JspException("MMList error\n" + sb.toString());
	}
}
