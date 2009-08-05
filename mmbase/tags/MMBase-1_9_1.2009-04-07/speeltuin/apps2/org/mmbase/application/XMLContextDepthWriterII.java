/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.application;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

import org.mmbase.module.corebuilders.*;

/**
 *  This class is used to write (export) a selection of nodes to xml format.
 *  The nodes to export are read from a XML context file, which specifies the
 *  startnode and depth to which to parse.
 *  The current version of this class combines a number of methods which we want to split - or at least share -
 *  with a seperate class for handling contexts.
 *  Note that because of it's static nature, no object instance need be made (in fact, none CAN be made) of this class.<br>
 *  To use this class in stead of the old XMLContextDepthWriter, replace the following lines in
 *  util/XMLApplicationWriter.java:
 *
 *  @author Daniel Ockeloen
 *  @author Jacco de Groot
 *  @author Pierre van Rooden
 *  @author Rob Vermeulen (VPRO)
 */
public class XMLContextDepthWriterII  {
    
    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(XMLContextDepthWriterII.class.getName());
    
    /**
     * Writes the application's nodes, according to that application's contexts, to a path.
     * The files written are stored in a subdirectory (datasets/datasetnames/data/*), and contain the datasource (xml) files for
     * both datanodes and relation nodes.
     * @param app A <code>DataSetElement</code> is used to retrieve information about what builders and relations are needed.
     * and in which files data should be stored.
     * @param capp A <code>XMLContextDepthReader</code> initialised to read the application's context file
     *		This object is used to retrieve information regarding search depth and starting nodes for
     *		the search tree whoch determines what nodes are part of this application.
     * @param mmb Reference to the MMbase processormodule. Used to retrieve the nodes to write.
     * @param resultmsgs Storage for messages which can be displayed to the user.
     * @returns Returns true if succesful, false if no valid depth or startnode could be found
     *		Failure of the export itself is not detected, though may be visible in the messages returned.
     */
    
    public static boolean writeContext(DataSetElement datasetelement,XMLContextDepthReader capp,MMBase mmb,Vector resultmsgs) {
        
        // First determine the startnodes, following the specs in the current context reader.
        int startnode=getStartNode(capp,mmb);
        if (startnode==-1) {
            return(false);
        }
        
        // get the depth from the current context reader
        int depth=capp.getDepth();
        if (depth==-1) {
            return(false);
        }
        
        // get valid builders to filter
	// ROB I guess that getNeededBuilders = ObjectSources + RelationSources
	Vector neededbuilders = datasetelement.getObjectSources();
	for (Enumeration e = datasetelement.getRelationSources().elements();e.hasMoreElements();) {
		Hashtable relationsource = (Hashtable)e.nextElement();
		neededbuilders.addElement(relationsource);
	}

        HashSet fb=getFilterBuilders(neededbuilders,mmb.getTypeDef());
        
        // the trick is to get all nodes until depth x and filter them
        HashSet relnodes = new HashSet();
        HashSet nodes = new HashSet();
        getSubNodes(startnode,depth,fb, nodes,relnodes,mmb);
        
        resultmsgs.addElement("Context found : "+nodes.size()+" nodes in application, "+relnodes.size()+" relations.");
        
        // create the dir for the Data & resource files
        File file = new File(datasetelement.getElementPath()+"data");
        try {
            file.mkdirs();
        } catch(Exception e) {
            log.error("Can't create dir : "+datasetelement.getElementPath()+"data");
        }
        
        // write DataSources
        writeDataSources(datasetelement,nodes,mmb,resultmsgs);
        
        
        // write relationSources
        writeRelationSources(datasetelement,relnodes,mmb,resultmsgs);
        
        return(true);
    }
    
    /**
     *  Writes the required datasources to their corresponding xml files by calling writeNodes()
     *  @param datasetelement The DataSetElement object, which is used to retrieve what datasources to write (and to what file).
     *  @param nodes The nodes that are part of the application. Those that are of a type compatible with the datasources are exported.
     *  @param mmb MMBase object used to retrieve builder information
     *  @param resultmsgs Used to store messages that can be showmn to the user
     */
    
    static void writeDataSources(DataSetElement datasetelement, HashSet nodes,MMBase mmb,Vector resultmsgs) {
        writeNodes(datasetelement, nodes, mmb, resultmsgs, false);
    }
    
    
    /**
     *  Writes the required relation sources to their corresponding xml files by calling writeNodes()
     *  @param datasetelement The DataSetElement object, which is used to retrieve what relationsources to write (and to what file).
     *  @param nodes The relation nodes that are part of the application. Those that are of a type compatible with the relationsources are exported.
     *  @param mmb MMBase object used to retrieve builder information
     *  @param resultmsgs Used to store messages that can be showmn to the user
     */
    
    static void writeRelationSources(DataSetElement datasetelement, HashSet nodes,MMBase mmb,Vector resultmsgs) {
        writeNodes(datasetelement, nodes, mmb, resultmsgs, true);
    }
    
    /**
     *  Writes the nodes to their corresponding xml files
     *  @param datasetelement The DataSetElement object, which is used to retrieve what sources to write (and to what file).
     *  @param nodes The nodes that are part of the application. Those that are of a type compatible with the sources are exported.
     *  @param mmb MMBase object used to retrieve builder information
     *  @param resultmsgs Used to store messages that can be showmn to the user
     *  @param isRelation Indicates whether the nodes to write are data (false) or relation (true) nodes
     */
    
    static void writeNodes(DataSetElement datasetelement, HashSet nodes, MMBase mmb, Vector resultmsgs,
    boolean isRelation) {
        
        String path = datasetelement.getElementPath()+"data"+File.separator;
        // Retrieve an enumeration of sources to write
        // The list of sources retrieved is dependent on whether the nodes to write are data or relation nodes
        Enumeration res;
        if (isRelation) {
            res=datasetelement.getRelationSources().elements();
        } else {
            res=datasetelement.getObjectSources().elements();
        }
        
        // create a list of writer objects for the nodes
        Hashtable nodeWriters = new Hashtable();
        while (res.hasMoreElements()) {
            Hashtable bset = (Hashtable)res.nextElement();	// retrieve source builder name
            String name = (String)bset.get("nodemanager");
            
            // Create nodewriter for this builder
            NodeWriter nw = new NodeWriter(mmb, resultmsgs, path, name, isRelation);
            // and store in table
            nodeWriters.put(name, nw);
        }
        
        MMObjectBuilder bul = mmb.getMMObject("typedef"); // get Typedef object
        
        // Store all the nodes that apply using their corresponding NodeWriter object
        for (Iterator nods=nodes.iterator(); nods.hasNext(); ) {
            // retrieve the node to export
            int nr = ((Integer)nods.next()).intValue();
            MMObjectNode node = bul.getNode(nr);
            String name = node.getTableName();
            NodeWriter nodeWriter = (NodeWriter)nodeWriters.get(name);
            // export the node if the writer was found
            if (nodeWriter!=null) {
                nodeWriter.write(node);
            }
            // if null, the node was specified as being part of the application, but should not (for some reason) be exported
            // note that this plays havoc with the relations!
            // better solution (not implemented): create Writers 'on the fly' if necessary, and export
            // everything, even if no datasource is given (should not be too tough), but this also means changing the context file.
        }
        
        // close the files.
        for (Enumeration e = nodeWriters.keys(); e.hasMoreElements();) {
            String name = (String)e.nextElement();
            NodeWriter nodeWriter;
            nodeWriter = (NodeWriter)nodeWriters.get(name);
            nodeWriter.done();
        }
    }
    
    /**
     *  Determines the number of the node referenced by another node.
     *  @param nodeNumber number of the referencing node
     *  @param relationNode node from the relationtable containing the relation data
     *  @returns An <code>int</code> value for the number of the node referenced
     */
    static int getRelatedNode(int nodeNumber, MMObjectNode relationNode) {
        int snumber = relationNode.getIntValue("snumber"); // referenced node is either source
        if (snumber == nodeNumber) {
            return relationNode.getIntValue("dnumber"); // or destination
        } else {
            return snumber;
        }
    }
    
    /* Searches the MMBase cloud, colelcting all nodes (and corresponmding relation nodes) that belong to a specific
     * type, and which can be traced up to a certain depth of nodes to a starting node.
     *
     * @param startnodenr the number of the node to start with
     * @param maxdeoth the maximum depth a tree is traversed. A depth of 0 or less means only the sdtartnode is added.
     *			A depth of one includes all teh nodes refernced by the startnode, etc.
     *			Relation nodes are not counted when determining 'depth'.
     * @param fb a <code>HashSet</code> containing the set of types that are allowed for export
     * @param nodesdoneSet  A <code>HashSet</code> which holds all nodes that are already 'done' or 'almost done'. this set is expanded in the method
     *			nodes already in this set are skipped (optimization). After return, the set has been expanded
     *			with all nodes found while traversing the cloud
     * @param mmb MMBase object used to retrieve builder information
     */
    
    static void getSubNodes(int startnodenr, int maxdepth, HashSet fb, HashSet nodesdoneSet, HashSet relationnodesSet,MMBase mmb) {
        HashSet nodesSet_current = null;	// holds all nodes not yet 'done' that are on the current level
        HashSet nodesSet_next = new HashSet();  // holds all nodes not yet 'done' that are on the next level
        InsRel bul = mmb.getInsRel();		// builder for collecting relations. should be changed to MMRelations later on!
        
        Integer type = new Integer(bul.getNodeType(startnodenr));	// retrieve node type (new method in MMObjectBuiilder)
        
        if (!fb.contains(type)) {   // exit if the type of this node conflicts.
            // essentially, no nodes are added. This can only occur if the context of
            // an application specified an invalid node.
            return;
        }
        nodesSet_next.add(new Integer(startnodenr)); // add the very first node to the set...
        
        //
        // For each depth of the tree, traverse the nodes on that depth
        //
        
        for (int curdepth=1;curdepth<=maxdepth;curdepth++) {
            
            nodesSet_current = nodesSet_next;	// use the next level of nodes to tarverse
            nodesSet_next = new HashSet();          // and create a new holder for the nodes one level deeper
            
            // since the nodes on this level are 'almost done', and therefor should be skipped
            // when referenced in the next layer, add the current set to the set of nodes that are 'done'
            //
            nodesdoneSet.addAll(nodesSet_current);
            
            // iterate through the current level
            
            for (Iterator curlist=nodesSet_current.iterator(); curlist.hasNext();) {
                
                // get the next node's number
                Integer thisnodenr = (Integer)curlist.next();
                
                // Iterate through all the relations of a node
                // determining relations has to be adapted when using MMRelations!
                
                for (Iterator rel=bul.getRelationsVector(thisnodenr.intValue()).iterator(); rel.hasNext();) {
                    
                    // get the relation node and node number
                    MMObjectNode relnode=(MMObjectNode)rel.next();
                    Integer relnumber=new Integer(relnode.getIntValue("number"));
                    
                    // check whether to add the referenced node
                    // and the relation between this node and the referenced one.
                    // if relation is in pool, save trouble and do not traverse further
                    if (!relationnodesSet.contains(relnumber)) {
                        // determine node referenced
                        int nodenumber=getRelatedNode(thisnodenr.intValue(),relnode);
                        
                        // check type of referenced node
                        type = new Integer(bul.getNodeType(nodenumber));
                        if (fb.contains(type)) {	// good node? then proceed
                            // add the relation node
                            relationnodesSet.add(relnumber);
                            // if the node has been 'done', don't add it!
                            Integer nodeNumber=new Integer(nodenumber);
                            if (!nodesdoneSet.contains(nodeNumber)) {
                                // because we use a set, no double nodes will be added (cool, uh?)
                                nodesSet_next.add(nodeNumber);
                            }
                        }
                    }
                }
            }
        }
        // add the last retrieved set to the set of nodes that are 'done'
        nodesdoneSet.addAll(nodesSet_next);
        
        return;
    }
    
    /* Retrieves the builders used for filtering the nodes for this application
     * @param filter Vector containign all the buildernames that are part of this application
     *		Note that being part of an application does not mean that they are exported!
     * @param bul reference to the TypeDef builder, used for rertrieving builder types
     * @returns a <code>HashSet</code>, containing the types (Integer) of all builders part of this application.
     */
    static HashSet getFilterBuilders(Vector filter,TypeDef bul) {
        HashSet resultset=new HashSet();
        
        for(Iterator res=filter.iterator(); res.hasNext(); ) {
            Hashtable bset=(Hashtable)res.next();
            String name=(String)bset.get("nodemanager");
            int value=bul.getIntValue(name);
            if (value!=-1) {
                resultset.add(new Integer(value));
            } else {
                log.error("XMLContextDepthWriter -> can't get intvalue for : "+name);
            }
        }
        return(resultset);
    }
    
    
    /* Retrieves the number of the startnode referenced by the context configuration file..
     * Returns always only one node (should be changed?)
     * @param capp XMLContextDepthReader object for retrieving data from the context
     * @param mmb reference to the MMBase object, used for retrieving aliases and builders
     * @returns An <code>integer</code>, the number of the startnode if succesful, -1 otherwise.
     */
    
    static int getStartNode(XMLContextDepthReader capp, MMBase mmb) {
        // first check for an alias
        String alias=capp.getStartAlias();
        if (alias!=null) {
            // if so, get the node associated with that alias
            OAlias bul=(OAlias)mmb.getMMObject("oalias");
            int number=bul.getNumber(alias);
            if (number==-1) log.error("Invalid Start Node Alias please make sure its valid");
            return(number);
        } else {
            // otherwise, get a builder and the where clause to run on that builder
            String builder=capp.getStartBuilder();
            String where=capp.getStartWhere();
            
            // retrieve the actual builder
            MMObjectBuilder bul=mmb.getMMObject(builder);
            if (bul!=null) {
                
                // find the nodes that match
                Enumeration results=bul.search(where);
                
                // check if there are any nodes
                if (results.hasMoreElements()) {
                    // then return the first node found.
                    MMObjectNode node=(MMObjectNode)results.nextElement();
                    return(node.getIntValue("number"));
                }
            } else {
                log.error("ContextDepthWriter-> can't find builder ("+builder+")");
            }
        }
        log.error("Invalid Start Node please fix your 'where' settings or use a alias");
        return(-1);
    }
    
    /* Saves a string value to a file.
     * @param filename Name of the file to save.
     * @param value string to store in the file
     * @returns True if succesfull, false if an error occurred.
     */
    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(e);
            log.error(Logging.stackTrace(e));
            return(false);
        }
        return(true);
    }
    
    /* Saves an array of byte to a file.
     * @param filename Name of the file to save.
     * @param value array to stiore in the file
     * @returns True if succesfull, false if an error occurred.
     */
    static boolean saveFile(String filename,byte[] value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(e);
            log.error(Logging.stackTrace(e));
            return(false);
        }
        return(true);
    }
    
    /* Writes the context file, based on what was supplied by the application
     * @param capp XMLContextDepthReader providing original context data
     * @param filename Name of the xml file to save.
     * @returns always true
     */
    public static boolean writeContextXML(XMLContextDepthReader capp,String filename) {
        String body="<contextdepth>\n";
        String alias=capp.getStartAlias();
        if (alias!=null) {
            body+="\t<startnode alias=\""+alias+"\" />\n";
        } else {
            body+="\t<startnode>\n";
            body+="\t\t<builder>"+capp.getStartBuilder()+"</builder>\n";
            body+="\t\t<where>"+capp.getStartWhere()+"</where>\n";
            body+="\t</startnode>\n\n";
        }
        body+="\t<depth>"+capp.getDepth()+"</depth>\n";
        body+="</contextdepth>\n";
        saveFile(filename,body);
        return(true);
    }
    
}
