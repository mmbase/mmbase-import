/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import org.mmbase.module.Module;
import org.mmbase.util.logging.*;

import org.w3c.dom.*;

/**
 * @author Daniel Ockeloen
 * @version $Id: ModuleWriter.java,v 1.5 2003-03-10 11:51:17 pierre Exp $
 */
public class ModuleWriter extends DocumentWriter  {

    // logger
    private static Logger log = Logging.getLoggerInstance(ModuleWriter.class.getName());

    /**
     * Hold a reference to the module for which to create an XML document.
     */
    private Module module;

    /**
     * Constructs the document writer.
     * The constructor calls its super to  create a basic document, based on the module document type.
     * @param module the module for which to create an XML document.
     */
    public ModuleWriter(Module module) throws DOMException {
        super("module", "-//MMBase/DTD module config 1.0//EN","http://www.mmbase.org/dtd/module_1_0.dtd");
        this.module=module;
        getMessageRetriever("org.mmbase.util.xml.resources.modulewriter");
    }

    /**
     * Generates the document. Can only be called once.
     * @throws DOMException when an error occurred during generation
     */
    protected void generate() throws DOMException {
        Element root=document.getDocumentElement();
        addComment("module.configuration",module.getName(),module.getModuleInfo(),root);
        root.setAttribute("maintainer",module.getMaintainer());
        root.setAttribute("version",""+module.getVersion());
        // status
        addComment("module.status",root);
        addContentElement("status","active",root);
        // classfile
        addComment("module.classfile",root);
        addContentElement("classfile",module.getClass().getName(),root);
        // properties
        Element properties=document.createElement("properties");
        addComment("module.properties",root);
        root.appendChild(properties);
        // properties.property
        Map datamap=module.getInitParameters();
        for (Iterator i=datamap.keySet().iterator(); i.hasNext();) {
            String propname=(String)i.next();
            String propvalue=(String)datamap.get(propname);
            Element elm=addContentElement("property",propvalue,properties);
            elm.setAttribute("name",propname);
        }

    }
}
