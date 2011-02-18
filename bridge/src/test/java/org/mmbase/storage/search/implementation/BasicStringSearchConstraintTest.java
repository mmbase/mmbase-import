package org.mmbase.storage.search.implementation;

import org.junit.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.mock.*;
import static org.junit.Assert.*;
/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Id$
 */
public class BasicStringSearchConstraintTest  {

    private final static String BUILDER_NAME = "news";
    private final static String FIELD_NAME1 = "title";
    private final static String FIELD_NAME2 = "number";
    private final static String SEARCHTERMS1 = "some search terms";

    /** Test instance. */
    private BasicStringSearchConstraint instance = null;

    /** Field instance 1 (string field). */
    private BasicStepField field1 = null;

    /** Field instance 2 (integer field). */
    private StepField field2 = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
    }


    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        NodeManager builder = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager(BUILDER_NAME);
        Step step = new BasicStep(builder.getName());

        // Field1 (string field).
        Field CoreField = builder.getField(FIELD_NAME1);
        field1 = new BasicStepField(step, CoreField);

        // Field2 (integer field).
        CoreField = builder.getField(FIELD_NAME2);
        field2 = new BasicStepField(step, CoreField);

        instance = new BasicStringSearchConstraint(field1,
        StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED,
        StringSearchConstraint.MATCH_TYPE_LITERAL, SEARCHTERMS1);
    }

    /** Tests constructor (with searchterms as string). */
    @Test
    public void testConstructor1() {
        List<String> searchTerms
        = Arrays.asList("some", "search", "terms");
        // Applied to integer field, should throw IllegalArgumentException.
        try {
            new BasicStringSearchConstraint(field2,
            StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED,
            StringSearchConstraint.MATCH_TYPE_LITERAL, searchTerms);
            fail("Applied to integer field, should throw IllegalArgumentException.");
        } catch(IllegalArgumentException e) {}

        // Empty searchterm string, should throw IllegalArgumentException.
        try {
            new BasicStringSearchConstraint(field1,
            StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED,
            StringSearchConstraint.MATCH_TYPE_LITERAL, new ArrayList<String>());
            fail("Empty searchterm string, should throw IllegalArgumentException.");
        } catch(IllegalArgumentException e) {}
    }

    /** Tests constructor (with searchterms as list). */
    @Test
    public void testConstructor2() {
        // Applied to integer field, should throw IllegalArgumentException.
        try {
            new BasicStringSearchConstraint(field2,
            StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED,
            StringSearchConstraint.MATCH_TYPE_LITERAL, SEARCHTERMS1);
            fail("Applied to integer field, should throw IllegalArgumentException.");
        } catch(IllegalArgumentException e) {}

        // Empty searchterm string, should throw IllegalArgumentException.
        try {
            new BasicStringSearchConstraint(field1,
            StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED,
            StringSearchConstraint.MATCH_TYPE_LITERAL, " ");
            fail("Empty searchterm string, should throw IllegalArgumentException.");
        } catch(IllegalArgumentException e) {}
    }

    /** Test of getBasicSupportLevel method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    @Test
    public void testGetBasicSupportLevel() {
        // Not supported.
        assertTrue(
        instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_NONE);
    }

    /** Test of setMatchType method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    @Test
    public void testSetMatchType() {
        try {
            // Invalid value, should throw IllegalArgumentException.
            instance.setMatchType(0);
            fail("Invalid value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid value, should throw IllegalArgumentException.
            instance.setMatchType(4);
            fail("Invalid value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        instance.setMatchType(StringSearchConstraint.MATCH_TYPE_LITERAL);
        assertTrue(
        instance.getMatchType() == StringSearchConstraint.MATCH_TYPE_LITERAL);
        instance.setMatchType(StringSearchConstraint.MATCH_TYPE_FUZZY);
        assertTrue(
        instance.getMatchType() == StringSearchConstraint.MATCH_TYPE_FUZZY);
        BasicStringSearchConstraint result
            = instance.setMatchType(StringSearchConstraint.MATCH_TYPE_SYNONYM);
        assertTrue(
        instance.getMatchType() == StringSearchConstraint.MATCH_TYPE_SYNONYM);
        assertTrue(result == instance);
    }

    /** Test of setSearchType method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    public void testSetSearchType() {
        try {
            // Invalid value, should throw IllegalArgumentException.
            instance.setSearchType(0);
            fail("Invalid value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid value, should throw IllegalArgumentException.
            instance.setSearchType(4);
            fail("Invalid value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        instance.setSearchType(StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED);
        assertTrue(instance.getSearchType()
            == StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED);
        instance.setSearchType(StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED);
        assertTrue(instance.getSearchType()
            == StringSearchConstraint.SEARCH_TYPE_PHRASE_ORIENTED);
        BasicStringSearchConstraint result
            = instance.setSearchType(StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED);
        assertTrue(instance.getSearchType()
        == StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED);
        assertTrue(result == instance);
    }

    @Test
    public void testSetParameter() {
        try {
            // Invalid parameter name, should throw IllegalArgumentException.
            instance.setParameter("ongeldige parameter", 0);
            fail("Invalid parameter name, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid parameter type, should throw IllegalArgumentException.
            instance.setParameter(
            StringSearchConstraint.PARAM_FUZZINESS, 0);
            fail("Invalid parameter type, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid parameter type, should throw IllegalArgumentException.
            instance.setParameter(
            StringSearchConstraint.PARAM_PROXIMITY_LIMIT, new Float(1.0));
            fail("Invalid parameter type, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid parameter value, should throw IllegalArgumentException.
            instance.setParameter(
            StringSearchConstraint.PARAM_FUZZINESS, new Float(-1.0));
            fail("Invalid parameter value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid parameter value, should throw IllegalArgumentException.
            instance.setParameter(
            StringSearchConstraint.PARAM_FUZZINESS, new Float(1.1));
            fail("Invalid parameter value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid parameter value, should throw IllegalArgumentException.
            instance.setParameter(
            StringSearchConstraint.PARAM_PROXIMITY_LIMIT, 0);
            fail("Invalid parameter value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        Float fuzziness = new Float(0.5);
        instance.setMatchType(StringSearchConstraint.MATCH_TYPE_LITERAL);

        // Due to matchtype set to LITERAL, fuzziness parameter is invalid.
        try {
            // Invalid matchtype, should throw IllegalArgumentException.
            instance.setParameter(StringSearchConstraint.PARAM_FUZZINESS, fuzziness);
            fail("Invalid matchtype, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        assertTrue(instance.getParameters().get(StringSearchConstraint.PARAM_FUZZINESS)
        == null);

        // due to matchtype fuzziness parameter should be set
        instance.setMatchType(StringSearchConstraint.MATCH_TYPE_FUZZY);
        instance.setParameter(StringSearchConstraint.PARAM_FUZZINESS, fuzziness);
        assertTrue(instance.getParameters().get(StringSearchConstraint.PARAM_FUZZINESS).
        equals(fuzziness));

        // changing matchtype should clear fuzziness parameter
        instance.setMatchType(StringSearchConstraint.MATCH_TYPE_LITERAL);
        assertTrue(instance.getParameters().get(StringSearchConstraint.PARAM_FUZZINESS)
        == null);

        Integer proximityLimit = 2;
        instance.setSearchType(StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED);

        // Due to searchtype WORD_ORIENTED, proximity limit parameter is invalid.
        try {
            // Invalid matchtype, should throw IllegalArgumentException.
            instance.setParameter(
            StringSearchConstraint.PARAM_PROXIMITY_LIMIT, proximityLimit);
            fail("Invalid matchtype, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        assertTrue(instance.getParameters().get(StringSearchConstraint.PARAM_PROXIMITY_LIMIT)
        == null);

        instance.setSearchType(StringSearchConstraint.SEARCH_TYPE_PROXIMITY_ORIENTED);
        BasicStringSearchConstraint result
            = instance.setParameter(
                StringSearchConstraint.PARAM_PROXIMITY_LIMIT, proximityLimit);
        assertTrue(instance.getParameters()
            .get(StringSearchConstraint.PARAM_PROXIMITY_LIMIT)
            .equals(proximityLimit));
        assertTrue(result == instance);

        // changing searchtype should clear proximity limit parameter
        instance.setSearchType(StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED);
        assertTrue(instance.getParameters()
                .get(StringSearchConstraint.PARAM_PROXIMITY_LIMIT)
            == null);
    }

    /** Test of getMatchType method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    //@Test
    public void testGetMatchType() {
        // same as:
        testSetMatchType();
    }

    /** Test of getSearchType method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    //@Test
    public void testGetSearchType() {
        // same as:
        testSetSearchType();
    }

    /** Test of addSearchTerm method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    @Test
    public void testAddSearchTerm() {
        String newTerm = "skeukowkk";
        int nrTerms = instance.getSearchTerms().size();
        BasicStringSearchConstraint result = instance.addSearchTerm(newTerm);
        List<String> searchTerms = instance.getSearchTerms();
        assertTrue(searchTerms.size() == (nrTerms + 1));
        assertTrue(searchTerms.get(nrTerms).equals(newTerm));
        assertTrue(result == instance);

        try {
            // Empty searchterm, should throw IllegalArgumentException.
            instance.addSearchTerm("  ");
            fail("White space searchterm, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Empty searchterm, should throw IllegalArgumentException.
            instance.addSearchTerm("");
            fail("White space searchterm, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of setSearchTerms(List) method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    @Test
    public void testSetSearchTerms() {
        List<String> searchTerms = new ArrayList<String>();
        searchTerms.add("kjeid");
        searchTerms.add("uerui");
        searchTerms.add("zcvvc");
        BasicStringSearchConstraint result
            = instance.setSearchTerms(searchTerms);
        assertTrue(instance.getSearchTerms().equals(searchTerms));
        assertTrue(result == instance);

        try {
            // Empty list of searchterms, should throw IllegalArgumentException.
            instance.setSearchTerms(new ArrayList<String>());
            fail("Empty list of searchterms, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        searchTerms.add("0");
        try {
            // Non-string searchterms, should throw IllegalArgumentException.
            instance.setSearchTerms(new ArrayList<String>());
            fail("Non-string searchterms, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of setSearchTerms(String) method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    @Test
    public void testSetSearchTerms2() {
        String searchTerm1 = "qwei";
        String searchTerm2 = "2838";
        String searchTerm3 = "i3wn";
        BasicStringSearchConstraint result
            = instance.setSearchTerms("\n\t\r " + searchTerm1
                + "\r" + searchTerm2 + "  " + searchTerm3 + " \n ");
        assertTrue(instance.getSearchTerms().size() == 3);
        assertTrue(instance.getSearchTerms().get(0).equals(searchTerm1));
        assertTrue(instance.getSearchTerms().get(1).equals(searchTerm2));
        assertTrue(instance.getSearchTerms().get(2).equals(searchTerm3));
        assertTrue(result == instance);

        try {
            // Empty searchterm string, should throw IllegalArgumentException.
            instance.setSearchTerms("\n\r\t ");
            fail("Empty list of searchterms, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

    }

    /** Test of getSearchTerms method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    //@Test
    public void testGetSearchTerms() {
        // same as:
        testSetSearchTerms();
        testSetSearchTerms2();
    }

    /** Test of getParameters method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    @Test
    public void testGetParameters() {
        // same as:
        testSetParameter();

        Map<String,Object> map = instance.getParameters();
        try {
            // Trying to modify map, should throw UnsupportedOperationException.
            map.put("kdj", "iiup");
            fail("Trying to modify map, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicStringSearchConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement
    }

}
