/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.io.File;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.FileWatcher;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;


/**
 * This Reloadable Module extension reloads its configuration and calls reload, automaticly if the
 * module XML changes.
 *
 * @author   Michiel Meeuwissen
 * @since    MMBase-1.8
 * @version  $Id: WatchedReloadableModule.java,v 1.1 2004-04-19 14:59:59 michiel Exp $
 */
public abstract class WatchedReloadableModule extends ReloadableModule {

    private static final Logger log = Logging.getLoggerInstance(WatchedReloadableModule.class);

    private FileWatcher fileWatcher = new FileWatcher() {
            public void onChange(File file) {
                if (reloadConfiguration(file)) {
                    reload();
                }
            }
        };

    /**
     * {@inheritDoc} 
     * On the onload of a reloadable module, a filewatcher is started. You should call
     * super.onload if you need to override this.
     */
    public void onload() {
        File dir = new File(MMBaseContext.getConfigPath(), "modules");        
        fileWatcher.setDelay(10 * 1000);
        fileWatcher.add(new File(dir, getName() + ".xml"));
        fileWatcher.start();
    }


}
