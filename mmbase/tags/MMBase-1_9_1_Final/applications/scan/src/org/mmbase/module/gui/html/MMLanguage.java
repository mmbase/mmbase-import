/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.gui.html;

import java.util.*;

import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Module for multi-language support.
 * This module reads (english) terms and their localized value(s) from a
 * configuration file.
 * It can then return the localized terms when needed.
 *
 * @application SCAN - Removing this from Core requires changes in Casting
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class MMLanguage extends ProcessorModule {

    // logger
    private static Logger log = Logging.getLoggerInstance(MMLanguage.class.getName());

    /**
     * Reference to the MMbase module.
     */
    MMBase mmb=null;
    /**
     * The language currently in use.
     */
    String languagePrefix;

    public void init() {
        // As the modules are loaded in a hashtable, mmlanguage can be initialized *before*
        // MMBase whereby MMLanguage gets the *default* language value, rather than the
        // set value in mmbaseroot.xml. Hence delay of setting language until first
        // translation call.
        languagePrefix = null;
    }


    /**
     * Basic constructor
     */
    public MMLanguage() {}

    /**
     * Handles the $MOD-MMLANGUAGE-commands.
     * Commands handled by this method are:
     * <ul>
     * <li> GET-term : translates 'term' to the current language, if possible
     *      (otherwise returns the term unchanged).</li>
     * <li> LANGUAGE: returns the language prefix currently in use.</li>
     * </ul>
     * @param sp the current page context
     * @param cmds the tokenized command
     * @return the result of the command as a String
     */
    @Override public String replace(PageInfo sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("GET")) {
                if (tok.hasMoreTokens()) {
                    return getFromCoreEnglish(tok.nextToken());
                } else {
                    return "missing core term";
                }
            } else if (cmd.equals("LANGUAGE")) {
                return languagePrefix;
            }
        }
        return "No command defined";
    }

    public String getFromCoreEnglish(String term) {
        // Set languagePrefix if not set yet. It isn't set at initialization time because
        // we can't be sure the MMBase module has already been initialized.
        if (languagePrefix == null) {
            mmb=(MMBase)getModule("MMBASEROOT");
            languagePrefix=mmb.getLanguage();
        }

        String translated=getInitParameter(languagePrefix+"_"+term);
        if (translated==null || translated.equals("")) {
            log.warn("MMLanguage -> could not convert : "+term+" into : "+languagePrefix);
            return term;
        } else {
            return translated;
        }
    }
}
