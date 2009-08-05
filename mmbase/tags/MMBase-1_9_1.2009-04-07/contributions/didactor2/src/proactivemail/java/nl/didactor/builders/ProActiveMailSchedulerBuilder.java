package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.*;
import org.mmbase.util.*;
import java.util.Date;

/**
 * This class handles objects of type 'ProActiveMailScheduler'.
 * @author Goran Kostadinov     (Levi9 Balkan Global Sourcing)
 */
public class ProActiveMailSchedulerBuilder extends MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(ProActiveMailSchedulerBuilder.class.getName());

    public boolean init() {
        return super.init();
    }

    public MMObjectNode preCommit(MMObjectNode node) {
        nl.didactor.component.proactivemail.cron.ProActiveMailRefreshJob.refresh();
        return node;
    }

    public int insert(String owner, MMObjectNode node) {
        int nr = super.insert(owner, node);
        nl.didactor.component.proactivemail.cron.ProActiveMailRefreshJob.refresh();
        return nr;
    }

    public void removeNode(MMObjectNode node) {
        nl.didactor.component.proactivemail.cron.ProActiveMailRefreshJob.refresh();
        super.removeNode(node);
    }
}

