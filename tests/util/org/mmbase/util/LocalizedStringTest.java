/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.xml.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import java.util.*;
import junit.framework.TestCase;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class LocalizedStringTest extends TestCase {

    public static final Locale NL = new Locale("nl");
    public static final Locale BE = new Locale("nl", "BE");
    public static final Locale BE_VAR = new Locale("nl", "BE", "a_b");
    public static final Locale EN = new Locale("en", "GB");
    public static final Locale DK = new Locale("dk");
    public static final Locale EO = new Locale("eo");


    protected LocalizedString getInstance() {
        LocalizedString.setDefault(DK);
        LocalizedString fun = new LocalizedString("funny");
        fun.set("leuk", NL);
        fun.set("plezant", BE);
        fun.set("amuza", EO);
        return fun;
    }

    protected LocalizedString getInstanceWithDefaultFilled() {
        LocalizedString.setDefault(DK);
        LocalizedString fun = new LocalizedString("funny");
        fun.set("leuk", NL);
        fun.set("plezant", BE);
        fun.set("amuza", EO);
        fun.set("morsom", DK);
        return fun;
    }

    public void testBasic() {
        LocalizedString fun = getInstance();
        assertEquals("funny", fun.get(null));
        assertEquals("amuza", fun.get(EO));
        assertEquals("plezant", fun.get(BE_VAR));
        assertEquals("leuk", fun.get(NL));

        LocalizedString fun2 = getInstanceWithDefaultFilled();
        assertEquals("morsom", fun2.get(DK));
        assertEquals("" + fun2.getDebugString(), "morsom", fun2.get(null));
        assertEquals("funny", fun2.get(Locale.CHINESE));


    }

    public void testClone() {
        LocalizedString fun = getInstance();
        LocalizedString clone = fun.clone();

        assertEquals(fun, clone);

        assertEquals(clone.get(null), "funny");
        assertEquals(clone.get(EO), "amuza");
        assertEquals(clone.get(BE_VAR), "plezant");
        assertEquals(clone.get(NL), "leuk");

        fun.set("plezzant", BE_VAR);

        assertFalse(fun.equals(clone));



    }


    public void testSerializable() throws IOException, java.lang.ClassNotFoundException {
        LocalizedString l = getInstance();

        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(l);
        oos.close();


         //deserialize
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(in);
        LocalizedString dl  =  (LocalizedString) ois.readObject();

        assertEquals(l, dl);
    }
    public void testReadonlySerializable() throws IOException, java.lang.ClassNotFoundException {
        LocalizedString rol = new ReadonlyLocalizedString(getInstance());
        // serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(rol);
        oos.close();


         //deserialize
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(in);
        LocalizedString drol  =  (LocalizedString) ois.readObject();

        assertEquals(rol, drol);
    }

    public void testMakeReadOnly() {
        LocalizedString l = getInstance();
        LocalizedString rol = l.getReadonlyLocalizedString();

        assertEquals(l, rol);

        l.set("plezant", BE_VAR);

        assertEquals(l, rol);

        try {
            rol.set("plezzant", BE_VAR);
            fail();
        } catch (UnsupportedOperationException ise) {
        }
        assertEquals(l, rol);
    }

    public void testMakeReadOnlyClone() {
        LocalizedString rol = getInstance().getReadonlyLocalizedString();
        LocalizedString clone = rol.clone();

        assertEquals(rol, clone);

        try {
            rol.set("plezzant", BE_VAR);
            fail();
        } catch (UnsupportedOperationException ise) {
        }
        assertEquals(rol, clone);
        assertEquals(clone, rol);

        // a clone is not read only any more
        clone.set("plezzant", BE_VAR);

        assertFalse(rol.equals(clone));
        assertFalse(clone.equals(rol));
    }

    public void testSetKey() {
        LocalizedString l = getInstance();
        LocalizedString rol = l.getReadonlyLocalizedString();
        LocalizedString clone = l.clone();

        assertEquals("funny", l.getKey());
        assertEquals("funny", rol.getKey());
        assertEquals("funny", clone.getKey());

        clone.setKey("fun");
        assertEquals("funny", l.getKey());
        assertEquals("funny", rol.getKey());
        assertEquals("fun", clone.getKey());
        assertEquals("funny", l.get(DK));
        assertEquals("funny", l.get(null));
        assertEquals("funny", rol.get(DK));
        assertEquals("funny", rol.get(null));
        assertEquals("fun", clone.get(DK));
        assertEquals("fun", clone.get(null));

        try {
            rol.setKey("nuf");
            fail();
        } catch (UnsupportedOperationException ise) {
        }
        assertEquals("funny", l.getKey());
        assertEquals("funny", rol.getKey());
        assertEquals("fun", clone.getKey());
        assertEquals("funny", l.get(DK));
        assertEquals("funny", l.get(null));
        assertEquals("funny", rol.get(DK));
        assertEquals("funny", rol.get(null));
        assertEquals("fun", clone.get(DK));
        assertEquals("fun", clone.get(null));

        l.setKey("nuf");

        assertEquals("nuf", l.getKey());
        assertEquals("nuf", rol.getKey());
        assertEquals("fun", clone.getKey());
        assertEquals("nuf", l.get(DK));
        assertEquals("nuf", l.get(null));
        assertEquals("nuf", rol.get(DK));
        assertEquals("nuf", rol.get(null));
        assertEquals("fun", clone.get(DK));
        assertEquals("fun", clone.get(null));
    }



}
