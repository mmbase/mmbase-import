package org.mmbase.util.transformers;
import org.mmbase.util.*;
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
public class UrlEscaperTest  {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        List<Object[]> data = new ArrayList<Object[]>();
        //data.add(new Object[] {"Caf\u00e9 e\u0125o\u015dan\u011do"});
        //data.add(new Object[] {"\u043f\u0435"});
        //data.add(new Object[] {"\u0440\u0435\u0441\u0442\u0440\u043e\u0439\u043a\u0430.txt"});
        data.add(new Object[] {"\u043f\u0435\u0440\u0435\u0441\u0442\u0440\u043e\u0439\u043a\u0430.txt"});
        //                         p    e     r     e     s     t     r     o     j     k      a
        return data;
    }

    private String testString;

    public UrlEscaperTest(String ts) {
        testString = ts;
    }

    @Test
    public void basic() {
        UrlEscaper u = new UrlEscaper();
        String transformed = u.transform(testString);
        System.out.println(transformed);
        assertEquals(testString, u.transformBack(transformed));
    }

}
