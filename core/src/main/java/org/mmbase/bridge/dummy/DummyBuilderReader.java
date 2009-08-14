/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.dummy;

import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.util.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class can read builder XML's. For the moment it's main use is to parse to a Map of DataType's, which is used by {@link DummyCloudContext} to create NodeManagers.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MapNodeManager.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 */

public class DummyBuilderReader extends org.mmbase.util.xml.AbstractBuilderReader<Field>  {


    static {
        org.mmbase.datatypes.DataTypes.initialize();
    }
    DummyBuilderReader parent;

    DummyBuilderReader(InputSource s) {
        super(s);
    }
    DummyBuilderReader(Document d) {
        super(d);
    }

    @Override
    protected boolean resolveInheritance() {
        String parentBuilder = getExtends();
        if ("".equals(parentBuilder)) {
            parent = null;
            inheritanceResolved = true;
            return true;
        } else {
            DummyBuilderReader p  = DummyCloudContext.getInstance().builders.get(parentBuilder);
            if (p == null) {
                if (DummyCloudContext.getInstance().nodeManagers.containsKey(parentBuilder)) {
                    throw new UnsupportedOperationException("Parent was not configured with XML");
                }
                return false;
            }

            Document inherit = (Document) this.document.cloneNode(true);
            Element root = (Element) (this.document.importNode(p.getDocument().getDocumentElement(), true));
            this.document.removeChild(this.document.getDocumentElement());
            this.document.appendChild( root);
            resolveInheritanceByXML(this.document, inherit);
            parent = p;
            inheritanceResolved = true;
            return true;
        }
    }

    @Override
    protected int getParentSearchAge() {
        return parent.getSearchAge();
    }
    @Override
    protected String getParentClassName() {
        return parent.getClassName();
    }

    public List<Field> getFields() {
        List<Field> results = new ArrayList<Field>();
        int pos = 1;
        if (hasParent()) {
            for (Field f : parent.getFields()) {
                Field newField = new DummyField(f, f.getDataType().clone());
                results.add(newField);
            }
        }

        for(Element fieldList : getChildElements("builder", "fieldlist")) {
            for (Element field : getChildElements(fieldList,"field")) {
                String fieldName = getElementAttributeValue(field, "name");
                DataType dt = decodeDataType(getName(), org.mmbase.datatypes.DataTypes.getSystemCollector(),
                                             fieldName, field, Field.TYPE_UNKNOWN, Field.TYPE_UNKNOWN, true);

                Field newField = new DummyField(fieldName, dt);
                results.add(newField);
            }
        }
        return results;
    }

    @Override
    protected Map<String, String> getParentProperties() {
        return parent.getProperties();
    }
    @Override
    protected boolean hasParent() {
        return parent != null;
    }
    @Override
    protected int getParentVersion() {
        return parent.getVersion();
    }
    @Override
    protected String getParentMaintainer() {
        return parent.getMaintainer();
    }
}
