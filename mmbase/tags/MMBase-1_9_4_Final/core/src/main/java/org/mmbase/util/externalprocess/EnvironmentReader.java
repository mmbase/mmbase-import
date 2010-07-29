/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

/** Reader for importing the OS Environment properties into java.
 * java.lang.System.getProperties() has only some environment info
 *
 * @todo more OS support
 *
 * @author Nico Klasens (Finalist IT Group)
 * @version $Id$
 * @since MMBase-1.6
 */
public class EnvironmentReader {
    private static Properties envVars = null;
    private static Vector<String> rawVars = null;

    /**
     * Get value of environment properties
     * @return Properties environment
     */
    public static Properties getEnvVars() {

        if (null != envVars)
            return envVars;

        envVars = new Properties();
        rawVars = new Vector<String>(32);
        String lineSeparator = System.getProperty("line.separator");

        String command = getEnvCommand();

        CommandLauncher launcher = new CommandLauncher("EnvironmentReader");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            launcher.execute(command);
            launcher.waitAndRead(outputStream, null);

            String envStr = outputStream.toString();
            StringTokenizer strTokens = new StringTokenizer(envStr, lineSeparator);

            String line;
            while (strTokens.hasMoreTokens()) {
                line = strTokens.nextToken();

                rawVars.add(line);
                int idx = line.indexOf('=');
                if (idx != -1) {
                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    envVars.setProperty(key, value);
                } else {
                    envVars.setProperty(line, "");
                }
            }
        } catch (ProcessException e) {} finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
        rawVars.trimToSize();
        return envVars;
    }

    /**
     * Command string to get OS Environment properties
     * @return String
     */
    public static String getEnvCommand() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("windows 9") > -1) {
            return "command.com /c set";
        } else {
            if ((OS.indexOf("nt") > -1) || (OS.indexOf("windows 2000") > -1) || (OS.indexOf("windows xp") > -1)) {
                return "cmd.exe /c set";
            } else {
                throw new UnsupportedOperationException("OS not supported yet");
            }
        }
    }

    /**
     * Get value of environment property
     * @param key property name
     * @return String value of environment property
     */
    public static String getEnvVar(String key) {
        Properties p = getEnvVars();
        return p.getProperty(key);
    }

    /**
     * getRawEnvVars returns an array of lines read from the shell.
     * @return String[] lines of the shell
     */
    public static String[] getRawEnvVars() {
        getEnvVars();
        return rawVars.toArray(new String[0]);
    }
}
