/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * Test cases for the Encoder
 *
 * @author keesj
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
                for (int x =0 ; x < TESTS.length ; x++){
                    String encoded = encode.encode(TESTS[x]);
                    String decoded = encode.decode(encoded);
                    assertTrue("char encoder["+ name +"] failed symetric test with input value["+ TESTS[x]+ "]",TESTS[x].equals(decoded));
                }
            }
        }
    }

    public Set getSymmetricEncodings() {
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
    public Set getDocumentedEncodings() {
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

}
