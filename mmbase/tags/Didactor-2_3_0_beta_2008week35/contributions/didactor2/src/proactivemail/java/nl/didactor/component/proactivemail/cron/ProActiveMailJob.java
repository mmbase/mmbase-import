package nl.didactor.component.proactivemail.cron;

/**
 * ProActiveMailJob class, purpose is to collect and run all scripts which are realated
 * to this instance of Job. Instantiating of quartz and execute method called,
 * with JobExecutionContext param.
 *
 * Quartz and triggers are set in DidactorProActiveMail class during the startup.
 *
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


public class ProActiveMailJob implements Job {
   private static Logger log = Logging.getLoggerInstance(ProActiveMailJob.class);

   /**
    * Execute methods, called from triggers
    *
    * @param context JobExecutionContext
    * @return nothing
    */
   public void execute(JobExecutionContext context)  throws JobExecutionException {
       // get all scripts related to this cron
       String jobName = context.getJobDetail().getName();
       int i = jobName.indexOf("_");
       if ( i >= 0 )
           jobName = jobName.substring(i+1);
       List scriptsCode = this.getScripts(jobName);

       Iterator it = scriptsCode.iterator();
       while ( it.hasNext() ) {
           String scr = (String)it.next();

           org.mozilla.javascript.Context cx = org.mozilla.javascript.Context.enter();
           org.mozilla.javascript.Scriptable scope = cx.initStandardObjects();
           try {
               // executing script
               Object result = cx.evaluateString(scope, scr, context.getJobDetail().getName(), 0, null);

               // Convert the result to a string and print it.
           } catch (Exception e) {
               log.error("Can't execute a script"+context.getJobDetail().getName()+".\r\n    "+e.toString());
           } finally {
               org.mozilla.javascript.Context.exit();
           }
       }
   }

   /**
    * Collects all related scripts to this cron
    *
    * @param cronName String
    * @return List of code of scripts
    */
   protected List getScripts(String cronName) {

       ArrayList result = new ArrayList();

       try {

           MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
           MMObjectBuilder schedulerBuilder = mmb.getBuilder("proactivemailscheduler");

           // first find this cron in DB by cron name

           NodeSearchQuery nsQuery = new NodeSearchQuery(schedulerBuilder);
           StepField nameField = nsQuery.getField(schedulerBuilder.getField("name"));
           BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(nameField, cronName);
           nsQuery.setConstraint(constraint);
           List cronsList = schedulerBuilder.getNodes(nsQuery);

           if ( cronsList.size() > 0 ) {

               // now find all related proactivemailtemplates, related ('insrel') to this cron

               MMObjectNode cronsnode  = (MMObjectNode) cronsList.get(0);
               Integer cronNumber = cronsnode.getIntegerValue("number");

               MMObjectBuilder insrelbuilder = mmb.getBuilder("insrel");

               NodeSearchQuery nsQuery1 = new NodeSearchQuery(insrelbuilder);
               StepField dnumberField = nsQuery1.getField(insrelbuilder.getField("dnumber"));
               BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(dnumberField, cronNumber);
               nsQuery1.setConstraint(constraint1);

               List insrelsList = insrelbuilder.getNodes(nsQuery1);
               for (int i = 0; i < insrelsList.size(); i++ ) {

                   // finally get a script and add to result list

                   MMObjectNode insrelNode  = (MMObjectNode) insrelsList.get(i);
                   Integer scriptNumber = insrelNode.getIntegerValue("snumber");

                   MMObjectBuilder scriptsbuilder = mmb.getBuilder("proactivemailscripts");
                   NodeSearchQuery nsQuery2 = new NodeSearchQuery(scriptsbuilder);
                   StepField numberField = nsQuery2.getField(scriptsbuilder.getField("number"));
                   BasicFieldValueConstraint constraint2 = new BasicFieldValueConstraint(numberField, scriptNumber);
                   nsQuery2.setConstraint(constraint2);

                   List scriptsList = scriptsbuilder.getNodes(nsQuery2);

                   try {
                       for ( int j = 0; j < scriptsList.size(); j++ ) {
                           MMObjectNode scriptNode  = (MMObjectNode) scriptsList.get(j);
                           String scriptName = scriptNode.getStringValue("name");
                           String scriptCode = scriptNode.getStringValue("code");
                           Integer scriptActive = scriptNode.getIntegerValue("active");
                           if ( scriptActive != null && scriptActive.intValue() > 0 && scriptCode.trim().length() > 0 ) {
                               result.add(scriptCode);
                           }
                       }
                   } catch (Exception escr) {}
               }
           }
       } catch (Exception ex) {
           log.error("Can't initialize proactivemailscheduler crons.\r\n     "+ex.toString());
       }
       return result;
   }
}
