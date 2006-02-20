package nl.didactor.chat;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.Module;
import java.util.Hashtable;
import java.util.Enumeration;
import nl.eo.chat.*;
import java.io.File;

/**
 * Wrapper that allows the EO chat to be started as an MMBase module.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class ChatModule extends Module {
    private Logger log = Logging.getLoggerInstance(ChatModule.class.getName());
    Hashtable properties;
    private boolean hasstarted = false;

    public void init() {
        init(getInitParameters());
    }

    /**
     * Initialize the Chat module.
     */
    private void init(Hashtable properties) {
        log.info("Initializing Chat module");
        this.properties = properties;
        maintainance();
    }

    public void onload() {
    }

    /**
     * We override the 'maintainance' method to wait for MMBase to be completely started.
     */
    public void maintainance() {
        if (!hasstarted) {
            if (((Module)Module.getModule("MMBASEROOT")).hasStarted()) {
                String[] args = {MMBaseContext.getConfigPath() + File.separator + "chat" + File.separator + "chat.properties"};
                log.info("Starting chatserver with path: '" + args[0] + "'");
                ChatStarter cs = new ChatStarter();
                cs.setArgs(args);
                cs.start();
                hasstarted = true;
            }
        }
    }

    /**
     * Shutdown method. 
     */
    public void shutdown() {
        log.info("Shutting down Chat module");
    }
}
