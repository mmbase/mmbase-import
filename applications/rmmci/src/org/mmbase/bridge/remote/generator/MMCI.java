/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.bridge.remote.generator;
import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.xml.XMLWriter;
import org.w3c.dom.*;

/**
 * @author Kees Jongenburger <keesj@dds.nl>
 **/
public class MMCI {
    Map classes;
    List classesList;

    private static MMCI STATIC_MMCI = null;

    public MMCI(){
        classes = new HashMap();
        classesList = new ArrayList();
    }

    public static MMCI getDefaultMMCI() throws Exception{
        return getDefaultMMCI("MMCI.xml");
    }

    public static MMCI getDefaultMMCI(String fileName) throws Exception{
        if (MMCI.STATIC_MMCI == null){
            DocumentBuilder db = XMLBasicReader.getDocumentBuilder(false);
            MMCI.STATIC_MMCI =  MMCI.fromXML(db.parse(fileName));
        }
        return MMCI.STATIC_MMCI;
    }

    public static MMCI fromXML(Document document) throws Exception{
        MMCI mmci =  new MMCI();
        Element xmle = document.getDocumentElement();
        NodeList nls = xmle.getChildNodes();
        for(int i = 0; i < nls.getLength(); i++) {
            Node element = nls.item(i);
            if (element instanceof Element) {
                XMLClass myClass = XMLClass.fromXML((Element)element);
                if (myClass == null) throw new Exception("Not found " + element);
                mmci.classes.put(myClass.getName(), myClass);
                mmci.classesList.add(myClass);
            }
        }
        return mmci;
    }

    public List getClasses(){
        return classesList;
    }
    public XMLClass getClass(String name)  {
        if (classes.get(name) == null) {
            return null;
        }
        return (XMLClass)((XMLClass)classes.get(name)).clone(true);
    }

    public static void addDefaultBridgeClasses(Element xmle, Document doc) throws Exception {
        //mmbase interfaces
        //xmle.setComment("MMCI XML description file\nCreated on " + new java.util.Date() + "\nby remote.common.MMCI");
        //should we use BridgeException interface?
        //xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.BridgeException"));

        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.BridgeList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Cacheable",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Cloud",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.CloudContext",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Descriptor", doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Field",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.FieldIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.FieldList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.FieldValue",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Module",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.ModuleIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.ModuleList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Node",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManager",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManagerIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManagerList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Relation",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManager",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManagerIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManagerList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.StringIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.StringList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Transaction",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Query",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeQuery",doc));
        // storage interfaces
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.Constraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.Step",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.RelationStep",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.StepField",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.AggregatedField",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.DateSortOrder",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.SortOrder",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.FieldNullConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.CompareFieldsConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.FieldValueConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.FieldValueInConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.FieldValueBetweenConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.FieldConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.LegacyConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.CompositeConstraint",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.SearchQuery",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.storage.search.FieldCompareConstraint",doc));
        // cache classes
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.cache.CachePolicy",doc));
        // datatypes classes
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.DataType", doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.DataType$Restriction", doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.LengthDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.ComparableDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.BinaryDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.BooleanDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.DateTimeDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.DoubleDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.FloatDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.IntegerDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.ListDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.LongDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.NodeDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.NumberDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.StringDataType",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.datatypes.XmlDataType",doc));
        // security classes
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.security.UserContext",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.security.Rank",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.security.AuthenticationData",doc));
        // function classes
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.util.functions.ReturnType", doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.util.functions.Parameter", doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.util.functions.Parameters", doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.util.functions.Function", doc));

        xmle.appendChild(ClassToXML.classToXML("org.mmbase.util.LocalizedString", doc));

    }

    public static void main(String [] argv) throws Exception{
        OutputStream os = System.out;
        if (argv.length >1){
            System.err.println("Usage: java org.mmbase.bridge.remote.generator.MMCI <outputfile>");
        } else {
            DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
            DOMImplementation dom= docBuilder.getDOMImplementation();
            DocumentType doctype = dom.createDocumentType("mmci","//-//MMBase/DTD mmci 1.0//EN//","http://www.mmbase.org/dtd/mmci_1_0.dtd");
            Document doc = dom.createDocument(null,"mmci",doctype);
            MMCI.addDefaultBridgeClasses(doc.getDocumentElement(), doc);
            if (argv.length==1) {
                os = new FileOutputStream(argv[0]);
            }
        OutputStreamWriter w = new OutputStreamWriter(os);
        XMLWriter.write(doc,w,true);
            w.flush();
        w.close();
        }
    }

}

