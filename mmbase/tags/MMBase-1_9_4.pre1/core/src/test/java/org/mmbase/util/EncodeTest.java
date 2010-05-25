/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.functions.*;

import junit.framework.TestCase;

/**
 * Test cases for the Encoder
 *
 * @author keesj
 * @author Michiel Meeuwissen
 */
public class EncodeTest extends TestCase {

    public final static String TEST_EMPTY_STRING = "";

    public final static String TEST_QUOTES = "'TEST_QUOTES'";

    public final static String TEST_ESCAPED_QUOTES = "\'TEST_ESCAPED_QUOTES\'";

    public final static String TEST_LT_GT = "4 < x > 2";

    public final static String TEST_ENTITY = "&lt; &gt;";

    public final static String TEST_HTML_ENTITY = "&eacute; &euml;";

    private final static String[] TESTS = { TEST_EMPTY_STRING, TEST_QUOTES, TEST_ESCAPED_QUOTES, TEST_LT_GT, TEST_HTML_ENTITY };

    /**
     * tests if the encoders documented in Encode are present in the list of
     * possible encodings
     */
    public void testDocumentedEncodersPresent() {
        Set documentedEncodings = getDocumentedEncodings();
        Set set = Encode.possibleEncodings();

        //are there documented encoders that are not present?
        if (!set.containsAll(documentedEncodings)) {
            documentedEncodings.removeAll(set);
            fail("found documented encoders that are not found in the list of know encoders" + documentedEncodings);
        }
    }

    /**
     * tests if the encoders documented in Encode are requestable
     */

    public void testDocumentedEncodersRequestable() {
        Iterator iter = getDocumentedEncodings().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            new Encode(name);
        }
    }


    public void testUndocumentedEncoders() {
        Set encodings = new TreeSet(Encode.possibleEncodings());
        encodings.removeAll(getDocumentedEncodings());
        assertTrue("found undocumented encoding(s)" + encodings, encodings.isEmpty());
    }

    /**
     * test if encode and decode methods are symetric string is encoded and the decoded the same result
     */
    public void testCharEncoders(){
        Iterator iter = getSymmetricEncodings().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            Encode encode = new Encode(name);
            if (encode.isCharEncoder()){
                for (String element : TESTS) {
                    String encoded = encode.encode(element);
                    String decoded = encode.decode(encoded);
                    assertTrue("char encoder["+ name +"] failed symetric test with input value["+ element+ "]",element.equals(decoded));
                }
            }
        }
    }

    private Set getSymmetricEncodings() {
        SortedSet symmetricEncodings = new TreeSet();
        symmetricEncodings.add("BASE64");
        symmetricEncodings.add("ESCAPE_HTML");
        symmetricEncodings.add("ESCAPE_HTML_ATTRIBUTE");
        symmetricEncodings.add("ESCAPE_SINGLE_QUOTE");
        symmetricEncodings.add("ESCAPE_URL");
        symmetricEncodings.add("ESCAPE_URL_PARAM");
        symmetricEncodings.add("ESCAPE_WML");
        symmetricEncodings.add("ESCAPE_XML");
        symmetricEncodings.add("ESCAPE_XML_ATTRIBUTE");
        symmetricEncodings.add("ESCAPE_XML_ATTRIBUTE_DOUBLE");
        symmetricEncodings.add("ESCAPE_XML_ATTRIBUTE_SINGLE");
        symmetricEncodings.add("ROT-13");
        symmetricEncodings.add("ROT-5");

        return symmetricEncodings;
    }
    /**
     *
     * @return a new set containing the names of the encoders that are
     *         documented in the Encode
     */
    private Set getDocumentedEncodings() {
        SortedSet documentedEncodings = new TreeSet();
        documentedEncodings.add("BASE64");
        documentedEncodings.add("ESCAPE_HTML");
        documentedEncodings.add("ESCAPE_HTML_ATTRIBUTE");
        documentedEncodings.add("ESCAPE_SINGLE_QUOTE");
        documentedEncodings.add("ESCAPE_URL");
        documentedEncodings.add("ESCAPE_URL_PARAM");
        documentedEncodings.add("ESCAPE_WML");
        documentedEncodings.add("ESCAPE_XML");
        documentedEncodings.add("ESCAPE_XML_ATTRIBUTE");
        documentedEncodings.add("ESCAPE_XML_ATTRIBUTE_DOUBLE");
        documentedEncodings.add("ESCAPE_XML_ATTRIBUTE_SINGLE");
        documentedEncodings.add("HEX");
        documentedEncodings.add("MD5");
        documentedEncodings.add("MMXF_ASCII");
        documentedEncodings.add("MMXF_BODY_POOR");
        documentedEncodings.add("MMXF_BODY_RICH");
        documentedEncodings.add("MMXF_HTML_BLOCK");
        documentedEncodings.add("MMXF_HTML_BLOCK_BR");
        documentedEncodings.add("MMXF_HTML_BLOCK_BR_NOSURROUNDINGP");
        documentedEncodings.add("MMXF_HTML_BLOCK_NOSURROUNDINGP");
        documentedEncodings.add("MMXF_HTML_BLOCK_LIST");
        documentedEncodings.add("MMXF_HTML_BLOCK_LIST_BR");
        documentedEncodings.add("MMXF_HTML_BLOCK_LIST_BR_NOSURROUNDINGP");
        documentedEncodings.add("MMXF_HTML_BLOCK_LIST_NOSURROUNDINGP");
        documentedEncodings.add("MMXF_HTML_INLINE");
        documentedEncodings.add("MMXF_XHTML");
        documentedEncodings.add("REGEXPS_ENTIRE");
        documentedEncodings.add("REGEXPS_LINES");
        documentedEncodings.add("REGEXPS_WORDS");
        documentedEncodings.add("REGEXPS_XMLTEXT");
        documentedEncodings.add("REGEXPS_XMLTEXT_WORDS");
        documentedEncodings.add("ROT-13");
        documentedEncodings.add("ROT-5");
        documentedEncodings.add("UNICODEESCAPER");
        return documentedEncodings;
    }

    private CharTransformer getRegexpReplacer(Map.Entry<String, Object>... settings) {
        RegexpReplacerFactory fact = new RegexpReplacerFactory();
        Parameters pars = fact.createParameters();
        pars.set("mode", "ENTIRE");
        List<Map.Entry<String, String>> patterns = new ArrayList<Map.Entry<String, String>>();
        patterns.add(new Entry<String, String>("\\s{2,}", " "));
        patterns.add(new Entry<String, String>("aa", "bb"));
        patterns.add(new Entry<String, String>("bb", "AAA"));
        patterns.add(new Entry<String, String>("^start", "BEGIN"));
        pars.set("patterns", patterns);
        for (Map.Entry<String, Object> setting : settings) {
            pars.set(setting.getKey(), setting.getValue());
        }
        return fact.createTransformer(pars);
    }

    public void testRegexpReplacer() {
        CharTransformer reg = getRegexpReplacer();
        assertEquals("a a", reg.transform("a a"));
        assertEquals("a a", reg.transform("a  a"));
        assertEquals("a a", reg.transform("a \n a"));
        assertEquals("a a", reg.transform("a \n\t a"));
        assertEquals("a a a", reg.transform("a  a  a"));
        assertEquals("AAA AAA AAA", reg.transform("aa  aa  aa"));
    }

    public void testRegexpReplacerFirstPattern() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("onlyFirstPattern", "true"));
        assertEquals("a a a", reg.transform("a  a  a"));
        assertEquals("a bb a", reg.transform("a  aa  a"));
    }

    public void testRegexpReplacerFirstMatch() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("onlyFirstMatch", "true"));
        assertEquals("x xx  x", reg.transform("x  xx  x"));
        assertEquals("a AAA  x", reg.transform("a  bb  x"));
        assertEquals("a AAA  x", reg.transform("a  aa  x"));
    }
    public void testRegexpReplacerFirstMatchFirstPattern() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("onlyFirstMatch", "true"),
                                                new Entry<String, Object>("onlyFirstPattern", "true")
                                                );
        assertEquals("a bb  a", reg.transform("a  aa  a"));
        assertEquals("a bb  aa", reg.transform("a  aa  aa"));
        assertEquals("a astart bb", reg.transform("a astart aa"));
        assertEquals("a start bb", reg.transform("a start aa"));

        // This may be a bit unexpected, but what to do...
        // The adcie should perhaps be that regexp replaced matching on the begin, should be the
        // first in the chain.
        assertEquals("a BEGIN bb", reg.transform("a  start aa"));
    }

    // WORDS mode
    // white space is effectively ignored. you can match on the begin of words with ^
    // content will be read word-for-word, so only a small buffer is used.

    public void testRegexpReplaceModeWords() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "WORDS"));
        assertEquals("a  AAA  a", reg.transform("a  aa  a"));
        assertEquals("a  AAA  AAA", reg.transform("a  aa  aa"));
    }
    public void testRegexpReplaceModeWordsFirstPattern() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "WORDS"),
                                                new Entry<String, Object>("onlyFirstPattern", "true")
                                                );
        assertEquals("a  bb  a", reg.transform("a  aa  a"));
        assertEquals("a  bb  bb", reg.transform("a  aa  aa"));
    }

    public void testRegexpReplaceModeWordsFirstMatch() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "WORDS"),
                                                new Entry<String, Object>("onlyFirstMatch", "true")
                                                );
        assertEquals("a  AAA  a", reg.transform("a  aa  a"));
        assertEquals("a  AAA  aa", reg.transform("a  aa  aa"));
    }
    public void testRegexpReplaceModeWordsFirstMatchFirstPattern() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "WORDS"),
                                                new Entry<String, Object>("onlyFirstPattern", "true"),
                                                new Entry<String, Object>("onlyFirstMatch", "true")
                                                );
        assertEquals("a  bb  a", reg.transform("a  aa  a"));
        assertEquals("a  bb  aa", reg.transform("a  aa  aa"));
        assertEquals("a  astart  bb", reg.transform("a  astart  aa"));
        assertEquals("a  BEGIN  aa", reg.transform("a  start  aa"));
    }
    // LINES mode
    // a lot like WORDS mode. Not all white space is ignored, only new lines.
    // content will be read line-for-line, so only a small buffer is used.
    public void testRegexpReplaceModeLines() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "LINES"));
        assertEquals("a AAA a", reg.transform("a  aa  a"));
        assertEquals("a AAA AAA", reg.transform("a  aa  aa"));
        assertEquals("a AAA AAA\n\nbla", reg.transform("a  aa  aa\n\nbla"));
    }

    // XMLTEXT mode
    // Ignores all XML markup, and only works on CDATA sections
    public void testRegexpReplaceModeXml() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "XMLTEXT"));
        assertEquals("<aa>a AAA a</aa>", reg.transform("<aa>a  aa  a</aa>"));
        assertEquals("<aa>a AAA AAA</aa>", reg.transform("<aa>a  aa  aa</aa>"));

        assertEquals("<bb aa='aa'>a AAA a</bb>", reg.transform("<bb aa='aa'>a  aa  a</bb>"));

        assertEquals("<bb aa='aa'><!-- aa -->a AAA a</bb>", reg.transform("<bb aa='aa'><!-- aa -->a  aa  a</bb>"));

        assertEquals("<aa><bb aa='aa'><!-- aa -->a AAA a</bb><bb>AAA</bb></aa>", reg.transform("<aa><bb aa='aa'><!-- aa -->a  aa  a</bb><bb>aa</bb></aa>"));
    }
    public void testRegexpReplaceModeXmlFirstPattern() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "XMLTEXT"),
                                                new Entry<String, Object>("onlyFirstPattern", "true")
                                                );
        assertEquals("<aa>a bb a</aa>", reg.transform("<aa>a  aa  a</aa>"));
        assertEquals("<aa>a bb bb</aa>", reg.transform("<aa>a  aa  aa</aa>"));

        assertEquals("<bb aa='aa'>a bb a</bb>", reg.transform("<bb aa='aa'>a  aa  a</bb>"));

        assertEquals("<bb aa='aa'><!-- aa -->a bb a</bb>", reg.transform("<bb aa='aa'><!-- aa -->a  aa  a</bb>"));
    }

    public void testRegexpReplaceModeXmlFirstMatch() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "XMLTEXT"),
                                                new Entry<String, Object>("onlyFirstMatch", "true")
                                                );
        assertEquals("<aa>a AAA  a</aa>", reg.transform("<aa>a  aa  a</aa>"));
        assertEquals("<aa>a AAA  aa</aa>", reg.transform("<aa>a  aa  aa</aa>"));

        assertEquals("<bb aa='aa'>a AAA  a</bb>", reg.transform("<bb aa='aa'>a  aa  a</bb>"));

        assertEquals("<bb aa='aa'><!-- aa -->a AAA  a</bb>", reg.transform("<bb aa='aa'><!-- aa -->a  aa  a</bb>"));
    }

    public void testRegexpReplaceModeXmlFirstMatchFirstPattern() {
        CharTransformer reg = getRegexpReplacer(new Entry<String, Object>("mode", "XMLTEXT"),
                                                new Entry<String, Object>("onlyFirstMatch", "true"),
                                                new Entry<String, Object>("onlyFirstPattern", "true")
                                                );
        assertEquals("<aa>a bb  a</aa>", reg.transform("<aa>a  aa  a</aa>"));
        assertEquals("<aa>a bb  aa</aa>", reg.transform("<aa>a  aa  aa</aa>"));

        assertEquals("<bb aa='aa'>a bb  a</bb>", reg.transform("<bb aa='aa'>a  aa  a</bb>"));

        assertEquals("<bb aa='aa'><!-- aa -->a bb  a</bb>", reg.transform("<bb aa='aa'><!-- aa -->a  aa  a</bb>"));
    }


    // XMLTEXT_WORDS mode
    // Ignores all XML markup, and only works on CDATA sections, further like WORDS
    public void testRegexpReplaceModeXmlWords() {
        // TODO
    }


    public void testLinkFinder() {
        // See also http://www.mmbase.org/jira/browse/MMB-1568
        LinkFinder lf = new LinkFinder();
        assertEquals("bla bla <a href=\"http://www.mmbase.org\">http://www.mmbase.org</a> bloe bloe",
                     lf.transform("bla bla http://www.mmbase.org bloe bloe"));
        assertEquals("bla bla <a href=\"http://www.mmbase.org\">click here</a> bloe bloe",
                     lf.transform("bla bla <a href=\"http://www.mmbase.org\">click here</a> bloe bloe"));
    }

    public void testLinkFinder2() {
        LinkFinder lf = new LinkFinder();
        assertEquals("bla bla <a href=\"http://www.mmbase.org\">http://www.mmbase.org</a> bloe bloe",
                     lf.transform("bla bla <a href=\"http://www.mmbase.org\">http://www.mmbase.org</a> bloe bloe"));
    }

}
