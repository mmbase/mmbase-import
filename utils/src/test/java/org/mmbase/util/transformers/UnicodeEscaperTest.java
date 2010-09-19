package org.mmbase.util.transformers;
import java.util.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;
/**

 * @author Michiel Meeuwissen
 * @version $Id$
 */
@RunWith(Parameterized.class)
public class UnicodeEscaperTest {

    protected static UnicodeEscaper escaper = new UnicodeEscaper();

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new String[] {"abcdefghijklmnopqrstuvwxyz\n1234567890"});
        return data;
    }

    private final String testString;
    public UnicodeEscaperTest(String s) {
        testString = s;
    }

    @Test
    public void basic() {
        String transformed = escaper.transform(testString);
        String transformedBack = escaper.transformBack(transformed);
        System.out.println(testString + "->" + transformed + "->" + transformedBack);
        assertEquals(testString, transformedBack);
    }




}
