/*
 * Created on Feb 11, 2005
 */
package org.mmbase.util;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Tesst cases for the Encoder
 * 
 * @author keesj
 *  
 */
public class EncodeTest extends TestCase {
    //MMBase logger
    private static Logger log = Logging.getLoggerInstance(ResourceLoaderTest.class);

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
        Set encodings = Encode.possibleEncodings();
        encodings.removeAll(getDocumentedEncodings());
        assertTrue("found undocumeted encoding(s)" + encodings, encodings.isEmpty());
    }

    /**
     * test if encode and decode methods are symetric string is encoded and the decoded the same result  
     */
    public void testCharEncoders(){
        Iterator iter = getDocumentedEncodings().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            //this test currently fails , but the previous test testDocumentedEncodersRequestable does not
            //it looks like one of the test above can brake the workings
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
    
    /**
     * 
     * @return a new set containing the names of the encoders that are
     *         documented in the Encode
     */
    public Set getDocumentedEncodings() {
        SortedSet documentedEncodings = new TreeSet();
        documentedEncodings.add("BASE64");
        documentedEncodings.add("ESCAPE_XML");
        documentedEncodings.add("ESCAPE_HTML");
        documentedEncodings.add("ESCAPE_HTML_ATTRIBUTE");
        documentedEncodings.add("ESCAPE_WML");
        //documentedEncodings.add("ESCAPE_WML_ATTRIBUTE");
        documentedEncodings.add("ESCAPE_URL");
        documentedEncodings.add("ESCAPE_URL_PARAM");
        documentedEncodings.add("ESCAPE_SINGLE_QUOTE");
        return documentedEncodings;
    }
}