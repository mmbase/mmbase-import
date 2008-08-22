package nl.didactor.component.proactivemail.cron;

/**
 * ProActiveMailRefreshJob class, purpose is to refresh crons after changes of
 * cron configuration
 * @author Goran Kostadinov     (Levi9 Balkan Global Sourcing)
 *
 * @version $Id$
 */

import org.quartz.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.*;
import org.mozilla.javascript.*;


public class ProActiveMailRefreshJob implements Job {
   private static Logger log = Logging.getLoggerInstance(ProActiveMailJob.class);
   private static boolean cronsChanged = false;
   private static int count = 0;

   /**
    * Execute methods, called from triggers
    *
    * @param context JobExecutionContext
    * @return nothing
    */
   public void execute(JobExecutionContext context)  throws JobExecutionException {
       if ( cronsChanged && count++ >= 0 ) {
           cronsChanged = false;
           count = 0;
           nl.didactor.component.proactivemail.DidactorProActiveMail.restartJobs();
       }
   }

   public static boolean isRefreshing() {
       return cronsChanged;
   }

   public static void refresh() {
       cronsChanged = true;
       count = 0;
   }
}
