/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.xml;

import org.mmbase.datatypes.DataType;
import org.mmbase.datatypes.Constants;
import org.mmbase.core.util.DataTypeSetter;
import org.mmbase.util.xml.AbstractBuilderReader;
import org.mmbase.bridge.mock.MockField;
import org.mmbase.bridge.util.NodeManagerDescription;
import java.util.*;
import org.mmbase.bridge.*;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This version of {@link AbstractBuilderReader} implements every method besides a {@link #getNodeManagerDescription(String)}. This minimizes the effort to implement a BuilderReader.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MockBuilderReader.java 39084 2009-10-13 08:01:36Z michiel $
 * @since   MMBase-1.9.2
 */

public abstract class ParentBuilderReader extends AbstractBuilderReader<Field>  {


    static {
        org.mmbase.datatypes.DataTypes.initialize();
    }


    public ParentBuilderReader(InputSource is) {
        super(is);
    }

    public ParentBuilderReader(Document doc) {
        super(doc);
    }

    protected AbstractBuilderReader<Field> parent;


    @Override
    protected int getParentSearchAge() {
        return parent.getSearchAge();
    }
    @Override
    protected String getParentClassName() {
        return parent.getClassName();
    }


    protected abstract NodeManagerDescription getNodeManagerDescription(String parentBuilder);

    @Override
    protected boolean resolveInheritance() {
        String parentBuilder = getExtends();
        if ("".equals(parentBuilder)) {
            parent = null;
            inheritanceResolved = true;
            return true;
        } else {
            NodeManagerDescription  p  = getNodeManagerDescription(parentBuilder);
            if (p == null) {
                return false;
            }
            if (p.reader == null) {
                throw new UnsupportedOperationException("Parent '" + parentBuilder + "' of '" + getName() + "' was not configured with XML");
            }


            Document inherit = (Document) this.document.cloneNode(true);
            Element root = (Element) (this.document.importNode(p.reader.getDocument().getDocumentElement(), true));
            this.document.removeChild(this.document.getDocumentElement());
            this.document.appendChild( root);
            resolveInheritanceByXML(this.document, inherit);
            parent = p.reader;
            inheritanceResolved = true;
            return true;
        }
    }

    /**
     * @inheritDoc
     *
     * This implementation produces {@link MockField}s. This may be want you want. If not, override this method.
     */
    @Override
    public List<Field> getFields() {
        final List<Field> results = new ArrayList<Field>();
        int pos = 1;
        if (hasParent()) {
            for (Field f : parent.getFields()) {
                Field newField = new MockField(f, f.getDataType().clone());
                results.add(newField);
            }
        }

        for(Element fieldList : getChildElements("builder", "fieldlist")) {
            for (Element field : getChildElements(fieldList,"field")) {
                String fieldName = getElementAttributeValue(field, "name");
                // legacy support
                if (fieldName.length() == 0) {
                    Element name = getElementByPath(field, "field.db.name");
                    if (name != null) {
                        fieldName = getNodeTextValue(name);
                    }
                }


                final Element dbtype = getElementByPath(field, "field.db.type");

                final MockField newField = new MockField(fieldName, null, Constants.DATATYPE_UNKNOWN);
                DataTypeSetter setter = new DataTypeSetter(newField) {
                        @Override
                        public void set(DataType dt) {
                            super.set(dt);
                            if (dbtype != null) {
                                if ("true".equals(dbtype.getAttribute("notnull"))) {// legacy
                                    dt.setRequired(true);
                                }
                            }
                        }
                    };
                // legacy support

                if (dbtype != null) {
                    String fieldType = getNodeTextValue(dbtype);
                    setter.setType(Fields.getType(fieldType));
                    if (setter.getType() == Field.TYPE_LIST) {
                        if (fieldType.length() > 5) {
                            String subType = fieldType.substring(5, fieldType.length());
                            setter.setListItemType(Fields.getType(subType));
                        }
                    }
                }

                decodeDataType(setter,
                               getName(), org.mmbase.datatypes.DataTypes.getSystemCollector(),
                               fieldName, field, true);

                String fieldState = getElementAttributeValue(field, "state");
                if ("".equals(fieldState)) {
                    newField.setState(Field.STATE_PERSISTENT);
                } else {
                    newField.setState(Fields.getState(fieldState));
                }
                assert newField.getName().length() > 0 : XMLWriter.write(field);

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
