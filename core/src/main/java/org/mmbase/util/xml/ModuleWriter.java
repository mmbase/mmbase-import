/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.util.Map.Entry;

import org.mmbase.module.Module;
import org.mmbase.util.xml.EntityResolver;

import org.w3c.dom.*;

/**
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class ModuleWriter extends DocumentWriter  {

    /**
     * Hold a reference to the module for which to create an XML document.
     */
    protected Module module;

    /**
     * Constructs the document writer.
     * The constructor calls its super to  create a basic document, based on the module document type.
     * @param module the module for which to create an XML document.
     */
    public ModuleWriter(Module module) throws DOMException {
        super("module", ModuleReader.PUBLIC_ID_MODULE,
                        EntityResolver.DOMAIN + EntityResolver.DTD_SUBPATH + ModuleReader.DTD_MODULE);
        this.module = module;
        getMessageRetriever("org.mmbase.util.xml.resources.modulewriter");
    }

    /**
     * Generates the document. Can only be called once.
     * @throws DOMException when an error occurred during generation
     */
    protected void generate() throws DOMException {
        Element root = document.getDocumentElement();
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
        Map<String, String> datamap=module.getInitParameters();
        for (Entry<String, String> entry : datamap.entrySet()) {
            String propname = entry.getKey();
            String propvalue = entry.getValue();
            Element elm=addContentElement("property",propvalue,properties);
            elm.setAttribute("name",propname);
        }
    }
}
