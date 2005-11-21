package nl.didactor.faq;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.Module;
import java.util.Hashtable;
import java.util.Enumeration;
import nl.eo.chat.*;
import java.io.File;

/**
 * Wrapper that allows  to be started as an MMBase module.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class FaqModule extends Module {
    private Logger log = Logging.getLoggerInstance(FaqModule.class.getName());
    Hashtable properties;
    private boolean hasstarted = false;

    public void init() {
        init(getInitParameters());
    }

    /**
     * Initialize  module.
     */
    private void init(Hashtable properties) {
        log.info("Initializing faq module");
        this.properties = properties;
        maintainance();
    }

    public void onload() {
    }



    /**
     * Shutdown method. 
     */
    public void shutdown() {
        log.info("Shutting down faq module");
    }
}
