/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.magicfile;
import org.mmbase.util.IOUtil;
import java.util.*;
import java.io.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@RunWith(Parameterized.class)
public class MagicFileTest  {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] files = {
            // MMB-1935:
            new Object[] {"flash1.swf", "application/x-shockwave-flash"},
            new Object[] {"flash2.swf", "application/x-shockwave-flash"},

            new Object[] {"cx.png", "image/png"},
            new Object[] {"wordxml.doc", "application/msword"},
            new Object[] {"test.xml", "text/xml"},
            new Object[] {"dot.", MagicFile.FAILED
            }
        };

        File dir = new File("src" + File.separator + "test" + File.separator + "files");

        List<Object[]> data = new ArrayList<Object[]>();
        for (Object[] file : files) {
            File f = new File(dir, (String) file[0]);
            data.add(new Object[] { f, file[1] });
        }
        return data;
    }

    private final File file;
    private final String  mimeType;
    public MagicFileTest(File f, String mimeType) {
        file = f;
        this.mimeType = mimeType;
    }

    @Test
    public void test() throws IOException  {
        assertEquals(file.getName(), mimeType, MagicFile.getInstance().getMimeType(file));
    }


    @Test
    public void noExtension() throws IOException  {
        File tempFile = File.createTempFile(MagicFileTest.class.getName(), ".tmp");
        IOUtil.copy(new FileInputStream(file), new FileOutputStream(tempFile));
        assertEquals(tempFile.getName(), mimeType, MagicFile.getInstance().getMimeType(tempFile));
    }


    @Test
    public void uppercase() throws IOException  {
        File tempFile = File.createTempFile("uppered", file.getName().toUpperCase());
        IOUtil.copy(new FileInputStream(file), new FileOutputStream(tempFile));
        assertEquals(tempFile.getName(), mimeType, MagicFile.getInstance().getMimeType(tempFile));
    }




}
