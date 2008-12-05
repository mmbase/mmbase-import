package nl.teleacnot.mmbase.nebo;

import org.mmbase.applications.crontab.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A MMBase cronjob that starts Importer to import NEBO streams.
 *
 * @author Andr\U00e9 vanToly &lt;andre@toly.nl&gt;
 */
public class NEBOCronJob extends AbstractCronJob implements CronJob {
    private static Logger log = Logging.getLoggerInstance(NEBOCronJob.class);

    public NEBOCronJob() {
        log.info("NEBOCronJob at your masters service.");
    }

    public void run() {
        log.info("job starts with cronEntry" + cronEntry);
        //log.info("the entry has this configuration " + cronEntry.getConfiguration());
        //log.info("the entry has this id " + cronEntry.getId());
        
        Importer importer = new Importer();
        importer.importXML();
        
        log.info("job just stopped");
    }
}

