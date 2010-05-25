/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.util;

import java.util.*;
//import com.ibm.icu.text.*;
import java.text.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import org.mmbase.storage.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.logging.*;


/**
 * @javadoc
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.7
 */
public class StorageReader<SM extends StorageManager> extends DocumentReader  {

    private static final Logger log = Logging.getLoggerInstance(StorageReader.class);

    /** Public ID of the Storage DTD version 1.0 */
    public static final String PUBLIC_ID_STORAGE_1_0 = "-//MMBase//DTD storage config 1.0//EN";
    /** DTD resource filename of the Database DTD version 1.0 */
    public static final String DTD_STORAGE_1_0 = "storage_1_0.dtd";

    /** Public ID of the most recent Database DTD */
    public static final String PUBLIC_ID_STORAGE = PUBLIC_ID_STORAGE_1_0;
    /** DTD resource filename of the most Database DTD */
    public static final String DTD_STORAGE = DTD_STORAGE_1_0;

    static {
        org.mmbase.util.xml.EntityResolver.registerPublicID(PUBLIC_ID_STORAGE_1_0, DTD_STORAGE_1_0, StorageReader.class);
    }

    /**
     * The factory for which the reader reads the document.
     * The factory is used to verify whether the document is compatible, and is used to instantiate objects
     * that depend on factory information (such as schemes)
     */
    protected StorageManagerFactory factory;

    /**
     * Constructor.
     *
     * @param factory the factory for which to read the storage configuration
     * @param source to the xml document.
     * @since MMBase-1.7
     */
    public StorageReader(StorageManagerFactory factory, InputSource source) {
        super(source, DocumentReader.validate(), StorageReader.class);
        this.factory = factory;
    }

    /**
     * Attempt to load a StorageManager class, using the classname as given in the configuration.
     * The method verifies whether the instantiated class is of the correct version.
     * @return the storage manager Class, or null if none was configured
     * @throws StorageConfigurationException if the factory version did not match, or the class configured is invalid
     */
    public Class<SM> getStorageManagerClass() throws StorageConfigurationException {
        Element root = document.getDocumentElement();
        if (factory != null) {
            // verify if the storagemanagerfactory is of the correct class, and
            // of the correct version
            NodeList factoryTagList = root.getElementsByTagName("storagemanagerfactory");
            if (factoryTagList.getLength()>0) {
                Element factoryTag = (Element)factoryTagList.item(0);
                try {
                    // obtain and check class
                    String factoryClassName = factoryTag.getAttribute("classname");
                    Class<SM> factoryClass = (Class<SM>) Class.forName(factoryClassName);
                    if (!factoryClass.isInstance(factory)) {
                        throw new StorageConfigurationException("StorageManager Configuration requires factory class '"+factoryClassName+"'.");
                    }
                    // obtain and check version
                    String storageManagerFactoryVersion = factoryTag.getAttribute("version");
                    if (storageManagerFactoryVersion != null) {
                        double version = Double.parseDouble(storageManagerFactoryVersion);
                        if (version > factory.getVersion()) {
                            throw new StorageConfigurationException("StorageManager Configuration requires factory version '"+version+", found "+factory.getVersion()+".");
                        }
                    }
                } catch (NumberFormatException pe) {
                    throw new StorageConfigurationException(pe);   // version is not an integer
                } catch (ClassNotFoundException cnfe) {
                    throw new StorageConfigurationException(cnfe); // factory class not found
                }
            }
        }
        NodeList managerTagList = root.getElementsByTagName("storagemanager");
        if (managerTagList.getLength()>0) {
            Element managerTag = (Element)managerTagList.item(0);
            String managerClassName = managerTag.getAttribute("classname");
            // intantiate storage manager and check version
            try {
                Class<SM> managerClass = (Class<SM>) Class.forName(managerClassName);
                StorageManager manager = managerClass.newInstance();
                // obtain and check version
                String storageManagerVersion = managerTag.getAttribute("version");
                if (storageManagerVersion != null) {
                    double version = Double.parseDouble(storageManagerVersion);
                    if (version > manager.getVersion()) {
                        throw new StorageConfigurationException("StorageManager Configuration requires storage manager version '"+version+", found "+manager.getVersion()+".");
                    }
                }
                return managerClass;
            } catch (NumberFormatException pe) {
                throw new StorageConfigurationException(pe);   // version is not an integer
            } catch (ClassNotFoundException cnfe) {
                throw new StorageConfigurationException(cnfe);
            } catch (IllegalAccessException iae) {
                throw new StorageConfigurationException(iae);
            } catch (InstantiationException ie) {
                throw new StorageConfigurationException(ie);
            }
        } else {
            return null;
        }
    }

    /**
     * Attempt to obtain a list of SearchQueryHandler classes, using the classname as given in the configuration.
     *
     *
     * @return A List of Class objects, each being the SearchQueryHandler class, or an empty list if none was configured
     * @throws StorageConfigurationException if the class configured is invalid
     */
    public List<Class<?>> getSearchQueryHandlerClasses() throws StorageConfigurationException {
        // override if otherwise specified
        List<Class<?>> classes = new ArrayList<Class<?>>();
        Element root = document.getDocumentElement();
        NodeList handlerTagList = root.getElementsByTagName("searchqueryhandler");
        for(int i=0; i<handlerTagList.getLength(); i++) {
            Element handlerTag = (Element)handlerTagList.item(i);
            String queryHandlerClassName = handlerTag.getAttribute("classname");
            //  get class
            try {
                classes.add(Class.forName(queryHandlerClassName));
            } catch (ClassNotFoundException cnfe) {
                throw new StorageConfigurationException(cnfe);
            }
        }
        return classes;
    }

    /**
     * Reads all attributes from the reader and returns them as a map.
     * This include options, as well as the following special attributes:
     * <ul>
     *  <li>option-disallowed-fields-case-sensitive : has the Boolean value TRUE if disallowed fields are case sensitive (default FALSE)</li>
     * </ul>
     * @return attributes as a map
     */
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        Element root = document.getDocumentElement();
        NodeList attributesTagList = root.getElementsByTagName("attributes");
        if (attributesTagList.getLength()>0) {
            Element attributesTag = (Element)attributesTagList.item(0);
            NodeList attributeTagList = attributesTag.getElementsByTagName("attribute");
            for (int i=0; i<attributeTagList.getLength(); i++) {
                Element attributeTag = (Element)attributeTagList.item(i);
                String attributeName = attributeTag.getAttribute("name");
                // require an attribute name.
                // if not given, skip the option.
                if (attributeName != null) {
                    attributes.put(attributeName,getNodeTextValue(attributeTag));
                }
            }
            NodeList optionTagList = attributesTag.getElementsByTagName("option");
            for (int i=0; i<optionTagList.getLength(); i++) {
                Element optionTag = (Element)optionTagList.item(i);
                // require an option name.
                // if not given, skip the option.
                String optionName = optionTag.getAttribute("name");
                if (optionName != null) {
                    String optionValue = optionTag.getAttribute("value");
                    Boolean value = Boolean.TRUE;
                    if (optionValue != null && !optionValue.equals("")) {
                        value = Boolean.valueOf(optionValue);
                    }
                    attributes.put(optionName,value);
                }
            }
            NodeList schemeTagList = attributesTag.getElementsByTagName("scheme");
            for (int i=0; i<schemeTagList.getLength(); i++) {
                Element schemeTag = (Element)schemeTagList.item(i);
                String schemeName = schemeTag.getAttribute("name");
                // require a scheme name
                // if not given, skip the option
                if (schemeName != null) {
                    String pattern = getNodeTextValue(schemeTag);
                    if (pattern==null || pattern.equals("")) {
                        attributes.put(schemeName, null);
                    } else {
                        attributes.put(schemeName, new Scheme(factory,getNodeTextValue(schemeTag)));
                    }
                }
            }
        }
        // system attributes
        NodeList disallowedFieldsList = root.getElementsByTagName("disallowed-fields");
        if (disallowedFieldsList.getLength()>0) {
            Element disallowedFieldsTag = (Element)disallowedFieldsList.item(0);
            attributes.put(Attributes.DISALLOWED_FIELD_CASE_SENSITIVE, Boolean.valueOf(disallowedFieldsTag.getAttribute("case-sensitive")));
            attributes.put(Attributes.ENFORCE_DISALLOWED_FIELDS, Boolean.valueOf(disallowedFieldsTag.getAttribute("enforce")));
        }
        return attributes;
    }

    /**
     * Returns all disallowed fields and their possible alternate values.
     * The fields are returned as name-value pairs, where the disallowedfieldname is the key, and
     * the alternate name is the value (null if no name is given).
     * @return disallowed fields as a map
     */
    public Map<String, String> getDisallowedFields() {
        Map<String, String>  disallowedFields = new HashMap<String, String>();
        Element root = document.getDocumentElement();
        NodeList disallowedFieldsList = root.getElementsByTagName("disallowed-fields");
        if (disallowedFieldsList.getLength() > 0) {
            Element disallowedFieldsTag = (Element)disallowedFieldsList.item(0);
            boolean casesensitive = Boolean.valueOf(disallowedFieldsTag.getAttribute("case-sensitive")).booleanValue();
            NodeList fieldTagList = disallowedFieldsTag.getElementsByTagName("disallowed-field");
            for (int i = 0; i < fieldTagList.getLength(); i++) {
                Element fieldTag = (Element)fieldTagList.item(i);
                String fieldName = fieldTag.getAttribute("name");
                // require a field name.
                // if not given, skip the option.
                if (fieldName != null) {
                    if (!casesensitive) fieldName = fieldName.toLowerCase();
                    String replacement = fieldTag.getAttribute("replacement");
                    disallowedFields.put(fieldName, replacement);
                }
            }
        }
        return disallowedFields;
    }


    /**
     * Returns all type mappings.
     * The mappings are returned in the order that they were given in the reader.
     * Calling code should sort this list if they want to use TypoMapping fuzzy matching.
     * @return a List of TypeMapping objects
     */
    public List<TypeMapping> getTypeMappings() {
        List<TypeMapping> typeMappings = new ArrayList<TypeMapping>();
        Element root = document.getDocumentElement();
        NodeList typeMappingsTagList = root.getElementsByTagName("type-mappings");
        if (typeMappingsTagList.getLength()>0) {
            Element typeMappingsTag = (Element)typeMappingsTagList.item(0);
            NodeList typeMappingTagList = typeMappingsTag.getElementsByTagName("type-mapping");
            for (int i=0; i<typeMappingTagList.getLength(); i++) {
                Element typeMappingTag = (Element)typeMappingTagList.item(i);
                TypeMapping typeMapping = new TypeMapping();
                typeMapping.name = typeMappingTag.getAttribute("name");
                // require a type-mapping name (a MMBase type)
                // if not given, skip the option.
                if (typeMapping.name != null) {
                    // obtain min/max values for sizes
                    try {
                        typeMapping.minSize = Long.parseLong(typeMappingTag.getAttribute("min-size"));
                    } catch (NumberFormatException nfe) {}
                    try {
                        typeMapping.maxSize = Long.parseLong(typeMappingTag.getAttribute("max-size"));
                    } catch (NumberFormatException nfe) {}
                    // get the type to convert to
                    typeMapping.type = typeMappingTag.getAttribute("type");
                    typeMappings.add(typeMapping);
                    if (typeMapping.name.equals("BYTE")) {
                        log.warn("In " + this + " deprecated mapping for 'BYTE' is specified. This must be changed to 'BINARY'");
                        typeMapping.name = "BINARY";
                    }
                }
            }
        }
        return typeMappings;
    }

    /**
     * @since MMBase-1.8.5
     */
    public List<String> getStoreBinaryAsFileObjects() {
        List<String> binaryAsFileObjects = new ArrayList<String>();
        Element root = document.getDocumentElement();
        NodeList binaryAsFileOjectsTagList = root.getElementsByTagName("store-binary-as-file-objects");
        if (binaryAsFileOjectsTagList.getLength() > 0) {
            Element binaryAsFileObjectsTag = (Element)binaryAsFileOjectsTagList.item(0);
            NodeList binaryAsFileObjectTagList = binaryAsFileObjectsTag.getElementsByTagName("store-binary-as-file-object");
            for (int i = 0; i<binaryAsFileObjectTagList.getLength(); i++) {
                Element binaryAsFileobjectTag = (Element)binaryAsFileObjectTagList.item(i);
                String objectName = binaryAsFileobjectTag.getAttribute("name");
                binaryAsFileObjects.add(objectName);
            }
        }
        return binaryAsFileObjects;
    }


    /**
     * @since MMBase-1.9.2
     */
    public Map<String, String> getCollationMappings() {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        Element root = document.getDocumentElement();
        NodeList mappingsLists = root.getElementsByTagName("collation-mappings");
        if (mappingsLists.getLength()>0) {
            Element  mappingsList = (Element)mappingsLists.item(0);
            NodeList list = mappingsList.getElementsByTagName("collation-mapping");
            for (int i=0; i<list.getLength(); i++) {
                Element mapping = (Element)list.item(i);
                result.put(mapping.getAttribute("java"), mapping.getAttribute("database"));
            }
        }
        return result;
    }
}
