package org.mmbase.util.transformers;
import org.mmbase.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class AsciifierTest  {


    @Test
    public void basic() {
        Asciifier a = new Asciifier();
        String testString = "Caf\u00e9 e\u0125o\u015dan\u011do";
        assertEquals("Cafe ehosango", a.transform(testString));

        a.setReplacer("_");
        String perestrojka = "\u043f\u0435\u0440\u0435\u0441\u0442\u0440\u043e\u0439\u043a\u0430?";
        assertEquals("___________?", a.transform(perestrojka));
        a.setCollapseMultiple(true);
        assertEquals("_?", a.transform(perestrojka));

        a.setMoreDisallowed("[\\s!?]");
        String testString2 = "Caf\u00e9 22?";
        assertEquals("Cafe_22_", a.transform(testString2));
    }

}
