package org.mmbase.util.transformers;
import org.mmbase.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**

 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class DiacriticsRemoverTest  {


    @Test
    public void basic() {
        DiacriticsRemover norm = new DiacriticsRemover();
        String testString = "Caf\u00e9 e\u0125o\u015dan\u011do";
        assertEquals("Cafe ehosango", norm.transform(testString));


    }

}
