/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import org.mmbase.bridge.Cloud;
import org.mmbase.datatypes.util.xml.*;
import java.util.*;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.xml.XMLWriter;

import org.xml.sax.InputSource;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test cases for DataTypes which can be done stand alone, without usage of an actually running MMBase.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id$
 */
public class DataTypesTest  {
    @BeforeClass
    public static void setUp() throws Exception {
        LocalizedString.setDefault(new Locale("da"));
        DataTypes.initialize();
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("tests"));
    }

    private StringDataType getString() {
        DataType<?> dt = DataTypes.getDataType("string");
        assertTrue("" + dt.getClass(), dt instanceof StringDataType);
        return (StringDataType) dt;
    }
    private StringDataType getLine() {
        DataType<?> dt = DataTypes.getDataType("eline");
        assertTrue("" + dt.getClass(), dt instanceof StringDataType);
        return (StringDataType) dt;
    }

    private StringDataType getStringClone() {
        return getString().clone("clone");
    }


    @Test
    public void setup(){
        assertEquals(new Locale("da"), LocalizedString.getDefault());
    }

    @Test
    public void name() {
        assertEquals("string", getString().getName());
        assertEquals("clone", getStringClone().getName());
        assertEquals("eline", getLine().getName());
    }
    @Test
    public void guiName() {

        assertEquals("da", LocalizedString.getDefault().getLanguage());

        assertEquals("string", getString().getName());

        assertEquals("Tekst", getString().getGUIName(new Locale("nl")));
        assertEquals("Text", getString().getGUIName(new Locale("en")));
        assertEquals(getString().getLocalizedGUIName().getDebugString(), "string", getString().getGUIName());

        StringDataType clone = getStringClone();
        assertEquals("Tekst", clone.getGUIName(new Locale("nl")));
        assertEquals("Text", clone.getGUIName(new Locale("en")));
        assertEquals("clone", clone.getLocalizedGUIName().getKey());
        assertEquals(clone.getLocalizedGUIName().getDebugString(), "clone", clone.getLocalizedGUIName().get(null));
        assertEquals("clone", getStringClone().getGUIName(null));
        assertEquals("clone", getStringClone().getGUIName());

        assertEquals("Tekst", getLine().getGUIName(new Locale("nl")));
        assertEquals("Text", getLine().getGUIName(new Locale("en")));
        assertEquals("eline", getLine().getGUIName());

    }

    @Test
    public void origin() {
        assertNull(getString().getOrigin());
        assertEquals(getString(), getStringClone().getOrigin());
        assertEquals(getString(), getLine().getOrigin());
    }

    @Test
    public void baseTypeIdentifier() {
        assertEquals("string", getString().getBaseTypeIdentifier());
        assertEquals("string", getStringClone().getBaseTypeIdentifier());
        assertEquals("string", getLine().getBaseTypeIdentifier());
    }

    @Test
    public void baseType() {
        assertEquals(Field.TYPE_STRING, getString().getBaseType());
        assertEquals(Field.TYPE_STRING, getStringClone().getBaseType());
        assertEquals(Field.TYPE_STRING, getLine().getBaseType());
    }

    @Test
    public void getTypeAsClass() {
        assertEquals(String.class, getString().getTypeAsClass());
        assertEquals(String.class, getStringClone().getTypeAsClass());
        assertEquals(String.class, getLine().getTypeAsClass());
    }

    @Test
    public void checkType() {
        try {
            getString().checkType(Integer.valueOf(1));
            fail();
        } catch (IllegalArgumentException iae) {
        }
        try {
            getStringClone().checkType(Integer.valueOf(1));
            fail();
        } catch (IllegalArgumentException iae) {
        }
        try {
            getLine().checkType(Integer.valueOf(1));
            fail();
        } catch (IllegalArgumentException iae) {
        }
        getString().checkType("foo");
        getStringClone().checkType("foo");
        getLine().checkType("foo");
    }

    @Test
    public void cast() {
        assertEquals("foo", getString().cast("foo", null, null));
        assertEquals("foo", getStringClone().cast("foo", null, null));
        assertEquals("1", getString().cast(new Integer(1), null, null));
        assertEquals("1", getStringClone().cast(new Integer(1), null, null));
        assertEquals("1", getLine().cast(new Integer(1), null, null));

    }

    @Test
    public void preCast() {
        assertEquals("foo", getString().preCast("foo", null, null));
        assertEquals("foo", getStringClone().preCast("foo", null, null));

    }

    @Test
    public void defaultValue() {
        assertNull(getString().getDefaultValue());
        assertNull(getStringClone().getDefaultValue());
        assertNull(getLine().getDefaultValue());
    }


    @Test
    public void finished() {
        assertTrue(getString().isFinished());
        assertFalse(getStringClone().isFinished());

        try {
            getString().setRequired(true);
            fail();
        } catch (IllegalStateException ise) {
        }
        try {
            getString().getLocalizedGUIName().set("bla", new Locale("nl"));
            fail();
        } catch (UnsupportedOperationException ise) {
        }
        getStringClone().setRequired(true);
    }

    @Test
    public void required() {
        assertFalse(getString().isRequired());
        assertFalse(getStringClone().isRequired());
        StringDataType clone = getStringClone();
        clone.setRequired(true);
        assertTrue(clone.isRequired());
    }

    @Test
    public void enumerationValues() {
        assertNull(getString().getEnumerationValues(null, null, null, null));
        assertNull(getStringClone().getEnumerationValues(null, null, null, null));
    }

    @Test
    public void enumerationValue() {
        assertNull(getString().getEnumerationValue(null, null, null, null, "foo"));
        assertNull(getStringClone().getEnumerationValue(null, null, null, null, "foo"));
    }

    @Test
    public void enumerationFactory() {
        assertNotNull(getString().getEnumerationFactory());
        assertNotNull(getStringClone().getEnumerationFactory());
    }
    @Test
    public void enumerationRestriction() {
        assertNotNull(getString().getEnumerationRestriction());
        assertNotNull(getStringClone().getEnumerationRestriction());
    }

    @Test
    public void getProcessor() {
        // TODO
    }


    protected boolean equals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }
    protected boolean xmlEquivalent(Node el1, Node el2) {
        if (! equals(el1.getNodeName(), el2.getNodeName())) {
            return false;
        }
        NodeList nl1 = el1.getChildNodes();
        NodeList nl2 = el2.getChildNodes();
        if (nl1.getLength() != nl2.getLength()) {
            return false;
        }
        for (int i = 0 ; i < nl1.getLength(); i++) {
            Node child1 = nl1.item(i);
            Node child2 = nl2.item(i);
            if (! xmlEquivalent(child1, child2)) {
                return false;
            }
        }
        return true;
    }


    protected Element getElement(String s) {
        DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(s)), false);
        Element el = reader.getDocument().getDocumentElement();
        return el;
    }

    @Test
    public void getElement() {
        BasicDataType s = getString();
        Element a = getElement("<a />");
        s.getElement(a, "b", "b");
        assertTrue(xmlEquivalent(getElement("<a><b /></a>"), a));
        s.getElement(a, "c", "b,c");
        assertTrue(xmlEquivalent(getElement("<a><b /><c /></a>"), a));
        s.getElement(a, "x", "x,b,c");
        assertTrue(xmlEquivalent(getElement("<a><x /><b /><c /></a>"), a));
        s.getElement(a, "b", "x,b,c");
        assertTrue(xmlEquivalent(getElement("<a><x /><b /><c /></a>"), a));

    }


    /**
     * Parses a string as a datatype XML and serializes it back using {@link DataType#toXml}. Input and output should be sufficiently (see {@link #xmlEquivalent}) similar.
     */
    protected void testXml(String xml, boolean mustBeEqual) throws Exception {
        DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(xml)), false, DataTypeReader.class);
        BasicDataType dt = DataTypeReader.readDataType(reader.getDocument().getDocumentElement(), null, null).dataType.clone();
        dt.setXml(null);
        Element toXml = dt.toXml();
        boolean equiv = xmlEquivalent(reader.getDocument().getDocumentElement(), toXml);

        if (mustBeEqual) {
            assertTrue("" + xml + " != " + XMLWriter.write(toXml), equiv);
        } else {
            assertFalse("" + xml + " == " + XMLWriter.write(toXml), equiv);
        }
        if (equiv) {
            //System.out.println("" + xml + " " + XMLWriter.write(toXml));
        }
    }

    //@Test
    public void xml1() throws Exception {
        testXml("<datatype  />", true);
    }
    //@Test
    public void xml2() throws Exception {
        testXml("<datatype base='string'><name>foo</name></datatype>", true);
    }
    //@Test
    public void xml3() throws Exception {
        testXml("<datatype base='string'><description>bar</description></datatype>", true);
    }

    //@Test
    public void xml4() throws Exception {
        testXml("<datatype base='string'><description>bar</description><enumeration><entry value='a' /></enumeration></datatype>", true);
    }

    //@Test
    public void xml5() throws Exception {
        testXml("<datatype base='string'><description>bar</description><default value='bar' /><unique value='true' /></datatype>", true);
    }

    //@Test
    public void xml6() throws Exception {
        testXml("<datatype base='string'><description>bar</description><default value='bar' /><unique value='true' /><required value='true' /></datatype>", true);
    }

    //@Test
    public void xml7() throws Exception {
        testXml("<datatype base='string'><description>bar</description><default value='bar' /><required value='true' /><unique value='true' /></datatype>", false);
    }


    @Test
    public void filesize() throws Exception {
        DataType<?> dt = DataTypes.getDataType("filesize");
        MockCloudContext cc = new MockCloudContext();
        Map<String, DataType> map = new HashMap<String, DataType>();
        map.put("filesize1", dt);
        map.put("filesize2", dt.clone());
        cc.addNodeManager("testfilesize", map);

        org.mmbase.bridge.Node n = cc.getCloud("mmbase").getNodeManager("testfilesize").createNode();
        n.setIntValue("filesize1", 100);
        n.setIntValue("filesize2", 100);
        n.commit();

        assertEquals(100, n.getIntValue("filesize1"));
        assertEquals("100 B", n.getStringValue("filesize1"));

        assertEquals(100, n.getIntValue("filesize2"));
        assertEquals("100 B", n.getStringValue("filesize2"));

    }


    @Test
    public void required_integer() {
        DataType<?> dt = DataTypes.getDataType("integer").clone();
        dt.setRequired(true);
        assertTrue(dt.castAndValidate("", null, null).size() > 0);
        assertTrue(dt.castAndValidate(null, null, null).size() > 0);
        assertTrue(dt.castAndValidate("aaa", null, null).size() > 0);
        assertTrue(dt.castAndValidate("NaN", null, null).size() > 0);

    }

    @Test
    public void required_decimal() {
        DataType<?> dt = DataTypes.getDataType("decimal").clone();
        dt.setRequired(true);
        assertTrue(dt.castAndValidate("", null, null).size() > 0);
        assertTrue(dt.castAndValidate(null, null, null).size() > 0);
        assertTrue(dt.castAndValidate("aaa", null, null).size() > 0);
        assertTrue(dt.castAndValidate("NaN", null, null).size() > 0);

    }

    @Test
    public  void requiredNoValidDefaultValue() {
        NodeManager nm = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("invalid_defaults");
        org.mmbase.bridge.Node n = nm.createNode();
        try {
            n.commit();
            fail ("Default value of 'title' is invalid of " + n);
        } catch (IllegalArgumentException e) {
            // ok
            System.out.println("" + e);
        }

    }

    @Test
    public  void datatypesBuilder() {
        NodeManager nm = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("datatypes");
        System.out.println(MockCloudContext.getInstance().nodeManagers.get("datatypes").fields.keySet());
        assertTrue("" + nm.getFields(), nm.hasField("boolean_string"));
        assertTrue("" + nm.getFields(), nm.hasField("field"));
    }

    /**
     * The 'typedef' datatype is interesting, because it is a _restricted_ node datatype
     */
    @Test
    public void typedef() throws Exception {
        NodeDataType typedefDataType = (NodeDataType) DataTypes.getDataType("typedef");
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        CloudThreadLocal.bind(cloud);
        org.mmbase.bridge.Node typedef =  cloud.getNodeManager("typedef").getList(null).getNode(0); // a valid node

        assertEquals(typedef, typedefDataType.castToValidate(typedef, null, null));
        assertEquals(typedef.getNumber(), ((org.mmbase.bridge.Node) typedefDataType.castToValidate(typedef.getNumber(), null, null)).getNumber());

        assertTrue(typedefDataType.enumerationRestriction.simpleValid(typedef, null, null));
        assertEquals(DataType.VALID, typedefDataType.enumerationRestriction.validate(DataType.VALID, typedef, null, null));

        assertEquals(DataType.VALID, typedefDataType.castAndValidate(typedef, null, null));
        assertEquals(DataType.VALID, typedefDataType.castAndValidate(typedef.getNumber(), null, null));
        assertEquals(DataType.VALID, typedefDataType.castAndValidate("" + typedef.getNumber(), null, null));


        org.mmbase.bridge.Node aa =  MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("aa").createNode(); // an invalid node
        aa.commit();

        assertFalse(typedefDataType.enumerationRestriction.simpleValid(aa, null, null));
        assertTrue(typedefDataType.enumerationRestriction.validate(DataType.VALID, aa, null, null).size() > 0);


        assertTrue(typedefDataType.castAndValidate(aa, null, null).size() > 0);
        assertTrue(typedefDataType.castAndValidate(aa.getNumber(), null, null).size() > 0);
        assertTrue(typedefDataType.castAndValidate("" + aa.getNumber(), null, null).size() > 0);


        assertEquals(typedef.getNumber(), typedefDataType.cast(typedef, null, null).getNumber());

        assertEquals(aa.getNumber(), typedefDataType.cast(aa, null, null).getNumber());
        CloudThreadLocal.unbind();
    }



    @Test
    public void lengthEnforce() throws DependencyException {
        String xml = "<datatype base='string'><length value='6' enforce='always' /></datatype>";
        DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(xml)), false, DataTypeReader.class);
        StringDataType dt = (StringDataType) DataTypeReader.readDataType(reader.getDocument().getDocumentElement(), null, null).dataType.clone();
        assertEquals(6, dt.getMinLength());
        assertEquals(6, dt.getMaxLength());
        assertEquals(DataType.ENFORCE_ALWAYS, dt.getMaxLengthRestriction().getEnforceStrength());
    }

    @Test
    public void virtualField() {
        NodeManager aa = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("aa");
        NodeManager insrel = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("insrel");
        assertEquals(aa.getField("security_context").getType(), Field.TYPE_STRING);
        assertEquals(insrel.getField("security_context").getType(), Field.TYPE_STRING);
        assertEquals(aa.getField("name").getType(), Field.TYPE_STRING);
    }


    @Test
    public void dataTypeOfAnotherField() {
        NodeManager aa = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("aa");
        NodeManager datatypes = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("datatypes");
        assertEquals(aa.getField("datatypesstring").getDataType(), datatypes.getField("string").getDataType());
        assertEquals(datatypes.getField("aaname").getDataType(), aa.getField("name").getDataType());
        System.out.println(aa.getField("datatypesstring") + "==" + datatypes.getField("string"));

    }



    @Test
    public void listDataTypes() {
        NodeManager lists = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("lists");
        {
            Field string = lists.getField("stringlist");
            ListDataType dataType = (ListDataType) string.getDataType();
            assertTrue(dataType.getItemDataType() instanceof StringDataType);
        }
        {
            Field legacy = lists.getField("legacy_stringlist");
            ListDataType dataType = (ListDataType) legacy.getDataType();
            assertTrue("" + dataType.getItemDataType().getClass().getName(), dataType.getItemDataType() instanceof StringDataType);
        }


    }
    protected int size(Iterator i) {
        int size = 0;
        while (i.hasNext()) {
            Object v = i.next();
            size++;
        }
        return size;
    }

    @Test
    public void enumLength() throws DependencyException {
        String xml = "<datatype base='string'><pattern enforce='onvalidate' value='a+' /><enumeration enforce='onchange'><entry value='a' /><entry value='aa' /><entry value='b' /></enumeration></datatype>";
        DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(xml)), false, DataTypeReader.class);
        StringDataType dt = (StringDataType) DataTypeReader.readDataType(reader.getDocument().getDocumentElement(), null, null).dataType.clone();
        assertEquals(2, size(dt.getEnumerationValues(null, null, null, null)));
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        NodeManager aa = cloud.getNodeManager("aa");
        Field f = aa.getField("stringfield");

        {
            org.mmbase.bridge.Node nod = aa.createNode();
            assertTrue(nod.isNew());
            assertEquals(2, size(dt.getEnumerationValues(null, cloud, nod, f)));
            nod.commit();
            assertFalse(nod.isNew());
            assertEquals(2, size(dt.getEnumerationValues(null, cloud, nod, f)));
        }

        assertEquals(2, size(dt.getEnumerationValues(null, cloud, null, f))); // fails?


    }

  @Test
    public void nodeManagerNamesEnumLength() throws DependencyException {
        String xml = "<datatype base='string'><class name='org.mmbase.datatypes.NodeManagerNamesDataType' /><pattern enforce='onvalidate' value='aa|bb' /></datatype>";
        DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(xml)), false, DataTypeReader.class);
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        NodeManager aa = cloud.getNodeManager("aa");

        NodeManagerNamesDataType dt = (NodeManagerNamesDataType) DataTypeReader.readDataType(reader.getDocument().getDocumentElement(), null, null).dataType.clone();
        assertEquals(2, size(dt.getEnumerationValues(null, cloud, null, null)));
        org.mmbase.bridge.Node nod = aa.createNode();
        Field f = aa.getField("stringfield");
        nod.commit();
        assertFalse(nod.isNew());
        assertEquals(2, size(dt.getEnumerationValues(null, cloud, nod, f)));

    }

}
