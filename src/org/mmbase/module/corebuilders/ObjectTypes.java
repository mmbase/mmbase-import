/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * This builder is the same as TypeDef, only it has an adittion field, which is the config field. This field
 * contains the xml-Document of the builder wich is represented by a Node inside this Builder.
 * The filename which shall be used depends on the fields 'name' of the Node. Also, a new node, means a new builder
 * removal of node is removal of builder (also of the xml). Changes to the config will also be active on commit of the
 * node.
 * TODO: update/merging code, and futher testing..
 * @author Eduard Witteveen
 * @version $Id: ObjectTypes.java,v 1.9 2002-05-13 09:40:17 eduard Exp $
 */
public class ObjectTypes extends TypeDef {
    private static Logger log = Logging.getLoggerInstance(ObjectTypes.class.getName());
    private java.io.File defaultDeploy = null;
    private boolean creationEnabled = true;
    

    /**
     * Sets the default deploy directory for the builders.
     * @return true if init was completed, false if uncompleted.
     */
    public boolean init() {
        boolean result = super.init();        
        if(defaultDeploy != null) {
            log.warn("This method should only be called once! FIXME B4 1.6");
            return result;
        }        
        // look if we have a property set, where to deploy default our builders...
        String BUILDER_DEPLOY_PROPERTY = "deploy-dir";
        String builderDeployDir = "applications";
        if(getInitParameter(BUILDER_DEPLOY_PROPERTY) != null) {
            builderDeployDir = getInitParameter(BUILDER_DEPLOY_PROPERTY);
        }
        else {
            log.debug("property: '"+BUILDER_DEPLOY_PROPERTY+"' was not set using default :" + builderDeployDir);
        }
        defaultDeploy = new java.io.File(MMBaseContext.getConfigPath() + java.io.File.separator + "builders" + java.io.File.separator + builderDeployDir);
        // create the dir, when it wasnt there...
        if(!defaultDeploy.exists()) {
            // try to create the directory for deployment....
            if(!defaultDeploy.mkdirs()) {
                log.warn("Could not create directory: " + defaultDeploy + ", new nodes cannot be created, since we cant write the configs to file");
                creationEnabled = false;
            }
            // check if we may write in the specified dir
            else if(!defaultDeploy.canWrite()) {
                log.error("Could not write in directory: " + defaultDeploy + ", new nodes cannot be created, since we cant write the configs to file");
                creationEnabled = false;
            }
        }
        defaultDeploy = defaultDeploy.getAbsoluteFile();
        log.info("Using '"+defaultDeploy+"' as default deploy dir for our builders.");
        return result;
    }
    
    /**
     * Provides additional functionality when obtaining field values.
     * This method is called whenever a Node of the builder's type fails at evaluating a getValue() request
     * (generally when a fieldname is supplied that doesn't exist).
     * It allows the system to add 'functions' to be included with a field name, such as 'html(body)' or 'time(lastmodified)'.
     * This method will parse the fieldname, determining functions and calling the {@link #executeFunction} method to handle it.
     * Functions in fieldnames can be given in the format 'functionname(fieldname)'. An old format allows 'functionname_fieldname' instead,
     * though this only applies to the text functions 'short', 'html', and 'wap'.
     * Functions can be nested, i.e. 'html(shorted(body))'.
     * Derived builders should override this method only if they want to provide virtual fieldnames. To provide addiitonal functions,
     * override {@link #executeFunction} instead.
     * @param node the node whos efields are queries
     * @param field the fieldname that is requested
     * @return the result of the 'function', or null if no valid functions could be determined.
     */
    public Object getValue(MMObjectNode node,String field) {       
        log.debug("node:" + node + " field: " + field);
        
        // return the Document from the config file..
        if (field.equals("config")) {
            // first check if we already have a value in node fields...
            Object o = super.getValue(node,field);
            if(o != null) return o;

            // otherwise, open the file to return it...
            log.debug("retrieving the document for node #" + node.getNumber());
            try {                
                // method node.getStringValue("name") should work, since getStringValue("path") checked it already...
                java.io.File file = new java.io.File(getBuilderFilePath(node));
                // when the file doesnt exist, the value we return should be null...
                if(!file.exists()) {
                    log.warn("file with name: " + file + " didnt exist, getValue will return null for builder config");
                    return null;
                }                
                org.w3c.dom.Document doc =  org.mmbase.util.XMLBasicReader.getDocumentBuilder().parse(file);
                // set the value in the node fields..
                node.setValue(field, doc);
                return doc;
            }
            catch(org.xml.sax.SAXException se) {
                throw new RuntimeException(Logging.stackTrace(se));
            }
            catch(java.io.IOException ioe) {
                throw new RuntimeException(Logging.stackTrace(ioe));
            }
        }
        return super.getValue(node, field);
    }
        
    /**
     * Insert a new object (content provided) in the cloud, including an entry for the object alias (if provided).
     * This method indirectly calls {@link #preCommit}.
     * @param owner The administrator creating the node
     * @param node The object to insert. The object need be of the same type as the current builder.
     * @return An <code>int</code> value which is the new object's unique number, -1 if the insert failed.
     */     
    public int insert(String owner, MMObjectNode node) {
        log.info("[insert of builder-node with name '" + node.getStringValue("name") + "' ]");
        
        // look if we can store to file...
        if(!creationEnabled) throw new RuntimeException("deploy directory for new builders was not set, look for error message in init");        
        
        // first store our config....
        storeBuilderFile(node);
           
        // try if it still not here...HACK HACK
        if(getIntValue(node.getStringValue("name")) > 0) {
            // there was already a node for the builder with this name!
            // can happen, when an other thread was here first in multi-threaded
            return getIntValue(node.getStringValue("name"));
        }
                
        // now save our node...
        int result = super.insert(owner, node);
        
        // load our builder
        loadBuilder(node);
        return result;
    }


    /**
     * Commit changes to this node to the database. This method indirectly calls {@link #preCommit}.
     * Use only to commit changes - for adding node, use {@link #insert}.
     * @param node The node to be committed
     * @return true if commit successful
     */
    public boolean commit(MMObjectNode node) {
        log.info("[commit of builder-node with name '" + node.getStringValue("name") + "' ( #"+node.getNumber()+")]");
        
        // in future make it also possible to change active / not active... als builder merging, first make this work!
        // TODO: merging code!        
        MMObjectBuilder builder = getObjectBuilder(node);
        
        // TODO: active / not active code.. IT CAN MESS UP BUILDERS THAT ARE SET INACTIVE, AND STILL HAVE DATA IN DATABASE!
        // if(builder == null) throw new RuntimeException("I can only change active builders(otherwise information could get lost..)");
        if(builder != null && builder.size() > 0 ) throw new RuntimeException("Cannot chage node which represents a builder, (otherwise information could get lost..)");

        // first save our config,...
        storeBuilderFile(node);

        // store otherthings, when there...
        boolean result = super.commit(node);        
        
        // unload the builder... 
        builder = unloadBuilder(node);        

        // apply changes on the database..
        deleteBuilderTable(builder);
        
        // load the builder again.. (will create a new table also)
        loadBuilder(node);
        
        return result;
    }    


    /**
     * Remove a node from the cloud, when the represented builder was active
     * it will also be unloaded
     * @param node The node to remove.
     * @throw RuntimeException When the operation could not be performed
     */     
    public void removeNode(MMObjectNode node) {
        log.info("[remove of builder-node with name '" + node.getStringValue("name") + "' ( #"+node.getNumber()+")]");

        // only delete when builder is completely empty...
        MMObjectBuilder builder = getObjectBuilder(node);
        if(builder == null) throw new RuntimeException("I can only delete active builders(otherwise we table's could stay in database..)");
        if(builder != null && builder.size() > 0 ) throw new RuntimeException("Cannot delete node which represents a builder, (otherwise information could get lost..)");

        builder = unloadBuilder(node);
        
        // try to delete the configuration file first!.....
        if(!deleteBuilderFile(node)) {
            // delete-ing failed, reload the builder again...
            loadBuilder(node);
            throw new RuntimeException("Could not delete builder config");
        }
                                
        // now that the builder cannot be started again (since config is now really missing)
        if(!deleteBuilderTable(builder))  throw new RuntimeException("Could not delete builder table");        
        
        super.removeNode(node);        
    }

    /**
     *  Sets a key/value pair in the main values of this node.
     *  Note that if this node is a node in cache, the changes are immediately visible to
     *  everyone, even if the changes are not committed.
     *  The fieldname is added to the (public) 'changed' vector to track changes.
     *  @param fieldname the name of the field to change
     *  @param fieldValue the value to assign
     *  @param originalValue the value which was original in the field
     *  @return <code>true</code> When an update is required(when changed),
     *	<code>false</code> if original value was set back into the field.
     */
    public boolean setValue(MMObjectNode node,String fieldname, Object originalValue) {
        // the field with the name 'name' may not be changed.....
        if(fieldname.equals("name")) {
	    Object newValue = node.values.get(fieldname);
	    if(originalValue!=null && !originalValue.equals(newValue)) {
		// restore the original value...
                node.values.put(fieldname,originalValue);
                return false;
	    }
     	}
        return true;
    }   

    /**
     * Returns the MMObjectBuilder which is represented by the node.
     * @param   node The node, from which we want to know it;s MMObjectBuilder
     * @return  The builder which is represented by the node, or <code>null</code>
     *          When the builder was not loaded.
     */
    protected  MMObjectBuilder getObjectBuilder(MMObjectNode node) {
        String builderName = node.getStringValue("name");
        return mmb.getMMObject(builderName);    
    }

    /**
     * Returns the path, where the builderfile can be found, for not exising builders, a path will be generated.
     * @param   node The node, from which we want to know it;s MMObjectBuilder
     * @return  The path where the builder should live or <code>null</code> in case of strange failures
     *          When the builder was not loaded.
     */
    protected  String getBuilderFilePath(MMObjectNode node) {
        // call our code above, to get our path...
        String path = getBuilderPath(node);

        // do we have a path?
        if(path == null) {
            log.error("field 'path' was empty.");
            return null;
        }
        return path + java.io.File.separator + node.getStringValue("name") + ".xml";
    }

    /**
     * Returns the path, where the builderfile can be found, for not exising builders, a path will be generated.
     * @param   node The node, from which we want to know it;s MMObjectBuilder
     * @return  The path where the builder should live or <code>null</code> in case of strange failures
     *          When the builder was not loaded.
     */
    protected  String getBuilderPath(MMObjectNode node) {
        log.debug("retrieving the path for node #" + node.getNumber());
        // some basic checking
        if(node == null) {
            log.error("node was null");
            return null;
        }
        if(node.getStringValue("name") == null ) {
            log.error("field 'name' was null");
            return null;
        }
        if(node.getStringValue("name").trim().length() == 0)  {
            log.error("field 'name' was empty.");
            return null;
        }

        // first request the url from the active builder....
        MMObjectBuilder builder = getObjectBuilder(node);
        if(builder != null) {
            // return the file path,..
            String file = builder.getConfigFile().getAbsoluteFile().getParent();
            log.debug("builder file:" + file);
            return file;
        }            
        // builder was inactive... try to get the correct path in some other way :(
        String pathInBuilderDir = mmb.getBuilderPath(node.getStringValue("name"), "");
        if(pathInBuilderDir != null) {
            // return the file path,..
            String file = MMBaseContext.getConfigPath() + java.io.File.separator + "builders" + java.io.File.separator + pathInBuilderDir;
            log.debug("builder file:" + file);
            return file;
        }
        // still null, make up a nice url for our builder!
        String file = defaultDeploy.getPath();
        log.debug("builder file:" + file);
        return file;
    }
    
    /**
     */
    protected  MMObjectBuilder loadBuilder(MMObjectNode node) {
        log.debug("[load builder '" + node.getStringValue("name") + "' ( #"+node.getNumber()+")]");
        
        String path = getBuilderPath(node);
        // remove everything till last 'config/builders/' 
        // TODO: find a better way for whole file location stuff
        String search = "config/builders/";
        int pos = path.indexOf(search);
        if(pos == -1) throw new RuntimeException("could not retrieve the path to store the file..");
        path = path.substring(pos + search.length());
        MMObjectBuilder builder = mmb.loadBuilderFromXML(node.getStringValue("name"), path);
        if(builder == null) {
            // inactive builder?
            log.info("could not load builder from xml, is in inactive?(name: '" + node.getStringValue("name") + "' path: '"+ path +"')");
            return null;
        }
        mmb.initBuilder(builder);
        return builder;
    }

    /**
     */
    protected  java.io.File storeBuilderFile(MMObjectNode node) {
        log.debug("[store builder '" + node.getStringValue("name") + "' ( #"+node.getNumber()+")]");
        
        org.w3c.dom.Document doc = node.getXMLValue("config");
        if(doc==null) {
            throw new RuntimeException("Field config was null! Could not save the file");
        }
        java.io.File file = new java.io.File(getBuilderFilePath(node));
        if(file == null) {
            throw new RuntimeException("file was null, could not continue");
        }
        if(file.exists()) {
            log.debug("found file: " + file + ", only store when changed.");
            // we already had a file, look if we have to save it (only needed when was modified)
            try {
                org.w3c.dom.Document original =  org.mmbase.util.XMLBasicReader.getDocumentBuilder().parse(file);
                if(equals(doc,original)) {
                    // doc's were the same.. 
                    log.debug("document already there, with same data, xml will not be written to file:" + file);
                    return file;
                }                
            }
            catch(org.xml.sax.SAXException se) {
                // original document wasnt a xml document?
                log.warn("found an other file on location, which wasnt xml(can't compare), gonna overwrite the file with current config.(error:" + se.toString() + ")");
            }
            catch(java.io.IOException ioe) {
                // original document gave an io exception, strange...
                throw new RuntimeException("failure opening old configuration for comparison, error: " + ioe.toString());
            }                        
        }
        try {
            javax.xml.transform.Transformer transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, mmb.getEncoding());
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_PUBLIC, doc.getDoctype().getPublicId());
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
            log.service("gonna save builderconfig to file:" + file);            
            transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(file));
        }
        catch(javax.xml.transform.TransformerException te) {
            throw new RuntimeException("failure saving configuration to disk : " + te.toString());
            // storing the builder failed!
        }
        return file;
    }

    /**
        documents may not be null!
    */
    private boolean equals(org.w3c.dom.Document a, org.w3c.dom.Document b) {
        try {
            //make a string from the XML
            javax.xml.transform.TransformerFactory tfactory = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer serializer = tfactory.newTransformer();
            // serializer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            // serializer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            
            // maybe some better code?
            java.io.StringWriter asw = new java.io.StringWriter();
            serializer.transform(new javax.xml.transform.dom.DOMSource(a),  new javax.xml.transform.stream.StreamResult(asw));

            java.io.StringWriter bsw = new java.io.StringWriter();
            serializer.transform(new javax.xml.transform.dom.DOMSource(b),  new javax.xml.transform.stream.StreamResult(bsw));
            
            // compare the 2 document-strings
            return asw.toString().equals(bsw.toString());
            
        } catch(javax.xml.transform.TransformerConfigurationException tce) {
            String message = tce.toString() + " " + Logging.stackTrace(tce);
            log.error(message);
            throw new RuntimeException(message);
        } catch(javax.xml.transform.TransformerException te) {
            String message = te.toString() + " " + Logging.stackTrace(te);
            log.error(message);
            throw new RuntimeException(message);
        }        
    }

    /**
     */
    protected  MMObjectBuilder unloadBuilder(MMObjectNode node) {
        log.debug("[unload builder '" + node.getStringValue("name") + "' ( #"+node.getNumber()+")]");
        
        // unload the builder,...        
        MMObjectBuilder builder = getObjectBuilder(node);
        if(builder != null) {
            mmb.unloadBuilder(builder);
        }
        return builder;
    }    
    
    /**
     */
    protected  boolean deleteBuilderTable(MMObjectBuilder builder) {
        log.debug("[delete table of builder '" + builder + "' ]");
        
        // well, since the whole thing doesnt exist anymore, now also drop the table, to clean the system a little bit...
        try {
            builder.drop();
            return true;
        }
        catch(Exception e) {
            log.fatal("please report this error: "  + e);
            return false;
        }
    }    
    

    /**        
     */
    protected  boolean deleteBuilderFile(MMObjectNode node) {
        log.debug("[delete file of builder '" + node.getStringValue("name") + "' ( #"+node.getNumber()+")]");
        
        java.io.File file = new java.io.File(getBuilderFilePath(node));        
        if(file.exists())  {
            if(!file.canWrite()) {
                log.error("file: "+file+" had no write rights for me.");
                return false;
            }
            // remove the file from the file system..
            file.delete();
            log.debug("file: "+file+" has been deleted");
        }
        return true;
    }    
}
