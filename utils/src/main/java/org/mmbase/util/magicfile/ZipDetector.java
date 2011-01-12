/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import java.io.*;
import java.util.zip.*;
import java.util.*;
import java.util.regex.*;
import org.mmbase.util.logging.*;

/**
 * A detector which can match on zipfiles. On containing files and directories.
 *
 * @version $Id$
 * @author Michiel Meeuwissen
 * @since MMBase-2.0
 */

class ZipDetector extends AbstractDetector {
    private static final Logger LOG = Logging.getLoggerInstance(ZipDetector.class);


    protected final List<Pattern> files = new ArrayList<Pattern>();
    protected final List<Pattern> directories = new ArrayList<Pattern>();

    public void setHasFiles(String hasFiles) {
        for (String f : hasFiles.split(",")) {
            files.add(Pattern.compile(f));
        }
    }
    public void setHasDirectories(String hasDirectories) {
        for (String d : hasDirectories.split(",")) {
            directories.add(Pattern.compile(d));
        }
    }

    /**
     * @return Whether detector matches the prefix/lithmus of the file
     */
    @Override
    public boolean test(byte[] lithmus, InputStream input) {
        return test(input);
    }
    protected boolean test(InputStream input) {
        try {
            Map<Pattern, Boolean> matchedFiles = new HashMap<Pattern, Boolean>();
            for (Pattern p : files) {
                matchedFiles.put(p, false);
            }
            Map<Pattern, Boolean> matchedDirectories = new HashMap<Pattern, Boolean>();
            for (Pattern p : directories) {
                matchedDirectories.put(p, false);
            }

            ZipInputStream zis = new ZipInputStream(input);
            ZipEntry ze;
            while((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                for (Map.Entry<Pattern, Boolean> e : matchedFiles.entrySet()) {
                    if (! e.getValue() && e.getKey().matcher(name).matches()) {
                        LOG.debug("Matched " + e.getKey() + " on" + ze);
                        e.setValue(true);
                    }
                }
                int dirIndex = name.lastIndexOf("/");
                if (dirIndex > 0) {
                    String dir = name.substring(0, dirIndex);
                    for (Map.Entry<Pattern, Boolean> e : matchedDirectories.entrySet()) {
                        if (! e.getValue() && e.getKey().matcher(dir).matches()) {
                            LOG.debug("Matched " + e.getKey() + " on " + dir);
                            e.setValue(true);
                        }
                    }
                }
            }
            boolean result = true;
            for (boolean subResult : matchedFiles.values()) {
                result &= subResult;
            }
            for (boolean subResult : matchedDirectories.values()) {
                result &= subResult;
            }

            return result;
        } catch (IOException ioe) {
            LOG.warn(ioe);
            return false;
        } finally {
        }
    }


    @Override
    public String toString() {
        return "files:" + files + " directories:" + directories;
    }
}
