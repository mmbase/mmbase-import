/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

import java.io.*;

import org.mmbase.util.externalprocess.CommandLauncher;
import org.mmbase.util.logging.*;

/**
 * If you want to transform a Reader stream by the use of an external command, than you can extend
 * this class. Implement the 'getCommand' function.
 *
 * @author Michiel Meeuwissen 
 * @since MMBase-1.7
 */

public abstract class AbstractCommandStringTransformer extends StringTransformer 
    implements CharTransformer {
    private static Logger log = Logging.getLoggerInstance(AbstractCommandStringTransformer.class);


    protected abstract String[] getCommand();

    // javadoc inherited
    public final String transform(String s) {
        try {
            String encoding = System.getProperty("file.encoding");
            CommandLauncher launcher = new CommandLauncher("Transformer");
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            InputStream inputStream = new ByteArrayInputStream(s.getBytes(encoding));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            launcher.execute(getCommand());
            launcher.waitAndWrite(inputStream, outputStream, errorStream);        
            return new String(outputStream.toByteArray(), encoding) + new String(errorStream.toByteArray(), encoding);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.toString());
        } catch (org.mmbase.util.externalprocess.ProcessException pe) {
            log.error(pe.toString());
        }
        return s;
    }

    public String toString() {
        return getCommand()[0].toUpperCase();
    }
}
