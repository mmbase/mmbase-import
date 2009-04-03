package nl.didactor.component.proactivemail;

/**
 * DidactorProActiveMail class, initialization of component ProActiveMail.
 * Do instantiating of quartz and triggers used by this component
 *
 * @author Goran Kostadinov     (Levi9 Balkan Global Sourcing)
 *
 * @version $Id$
 */

import org.quartz.*;
import org.quartz.impl.*;

import nl.didactor.component.Component;
import nl.didactor.component.core.DidactorCore;
import nl.didactor.component.email.DidactorEmail;
import nl.didactor.component.proactivemail.cron.ProActiveMailJob;
import nl.didactor.proactivemail.util.EventManager;

import org.mmbase.bridge.Cloud;
import java.util.List;
import java.util.Map;

import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.corebuilders.TypeRel;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 */
public class DidactorProActiveMail extends Component{

    private static final Logger log = Logging.getLoggerInstance(DidactorProActiveMail.class);
    private static Scheduler scheduler = null;
    private static String groupName = "DidactorGroup";

    /**
     * Returns the version of the component
     */

    public String getVersion() {
        return "1.0";
    }

    /**
     * Returns the name of the component
     */
    public String getName() {
        return "proactivemail";
    }

    public static String getGroupName () {
        return groupName;
    }

    public void init() {
        super.init();

        Component.getComponent("email").registerInterested(this);

        initRelations();
        restartJobs();
        org.mmbase.core.event.EventListener reporting = new nl.didactor.proactivemail.util.EventManager();
        org.mmbase.core.event.EventManager.getInstance().addEventListener(reporting);
    }

    /**
     * Returns an array of components this component depends on.
     * This should always contain at least one component: 'DidactorCore'
     */
    public Component[] dependsOn() {
        Component[] components = new Component[2];
        components[0] = new DidactorCore();
        components[1] = new DidactorEmail();
        return components;
    }

    @Override
    public String getValue(String setting, Cloud cloud, Map context, String[] arguments) {
        if ("showlo".equals(setting)) { // is this actually used somewhere?
            return "2";
        }
        return "";
    }

    private static boolean startScheduler() {
        try {
            if ( scheduler == null ) {
                // first initialize Scheduler
                SchedulerFactory schedFact = new StdSchedulerFactory();
                scheduler = schedFact.getScheduler();
                scheduler.start();
            }
        } catch (Exception e) {
            log.error("Can't initialize quartz scheduler inside component 'proactivemail'.");
            return false;
        }
        return true;
    }
    public static void restartJobs() {
        if ( !startScheduler() )
            return;
        try {
            String[] s = scheduler.getJobNames(groupName);
            for (int i = 0; i < s.length; i++) {
                scheduler.deleteJob(s[i], groupName);
            }
        } catch (Exception e) {
            log.error("Can't delete scheduler jobs inside component 'proactivemail'. \r\n"+e.toString());
            try {
                if ( !scheduler.isShutdown() )
                    scheduler.shutdown(true);
            } catch (Exception e1) {}
            scheduler = null;
            if ( !startScheduler() )
                return;
        }
        initScheduler();
    }


    /**
     * Initialize quartz and crons
     */
    private static void initScheduler() {
        if ( scheduler == null ) return;

        MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");

        try {
            boolean refreshActive = false;
            String[] refreshGroup = scheduler.getJobNames("refreshgroup");
            for (int i = 0; i < refreshGroup.length; i++)
                if ( refreshGroup[i].compareTo("refreshjob")==0 ) {
                    refreshActive = true;
                }
            if ( !refreshActive ) {
                JobDetail jobDetail = new JobDetail("refreshjob",
                                                    "refreshgroup",
                                                    nl.didactor.component.proactivemail.cron.ProActiveMailRefreshJob.class);
                CronTrigger trigger = new CronTrigger("refreshjob", null, "0 0/1 * * * ?"); // refresh any minutes
                scheduler.scheduleJob(jobDetail, trigger);
            }
            // read all specific schedulers we should make trigger for it

            MMObjectBuilder cronsbuilder = mmb.getBuilder("proactivemailscheduler");
            NodeSearchQuery nsQuery = new NodeSearchQuery(cronsbuilder);
            StepField nameField = nsQuery.getField(cronsbuilder.getField("name"));
            StepField cronpatternField = nsQuery.getField(cronsbuilder.getField("cronpattern"));
            StepField activeField = nsQuery.getField(cronsbuilder.getField("active"));
            List cronsList = cronsbuilder.getNodes(nsQuery);
            for (int i = 0; i < cronsList.size(); i++) {
                // initialize all of them

                MMObjectNode cronsnode  = (MMObjectNode) cronsList.get(i);
                Integer cronNumber = cronsnode.getIntegerValue("number");
                String cronName = cronsnode.getStringValue("name");
                String cronPattern = cronsnode.getStringValue("cronpattern");
                Integer cronActive = cronsnode.getIntegerValue("active");
                if ( cronActive.intValue() > 0 && cronName != null &&
                     cronName.length() > 0 && cronPattern.length() > 0 ) {
                    log.info("Initialize active scheduler '"+cronName+"'.");
                    try {
                        JobDetail jobDetail = new JobDetail(cronNumber.toString()+"_"+cronName,
                                                            groupName,
                                                            nl.didactor.component.proactivemail.cron.ProActiveMailJob.class);

                        CronTrigger trigger = new CronTrigger(cronName, null, cronPattern);
                        scheduler.scheduleJob(jobDetail, trigger);
                    } catch (Exception ex1) {
                        log.error("Can't initialize proactivemailscheduler cron named '"+cronName+"'.\r\n     "+ex1.toString());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Can't initialize proactivemailscheduler crons.\r\n     "+ex.toString());
        }

    }

    /**
     * Initialize relations that component needs.
     */
    public void initRelations() {
        MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
        String username = "system";
        String admin = "admin";
        RelDef reldef = mmb.getRelDef();
        TypeDef typedef = mmb.getTypeDef();
        int posrel = reldef.getNumberByName("posrel");
        int editcontexts = typedef.getIntValue("editcontexts");

        MMObjectBuilder editcontextsbuilder = mmb.getBuilder("editcontexts");

        try{
            NodeSearchQuery nsQuery = new NodeSearchQuery(editcontextsbuilder);
            StepField nameField = nsQuery.getField(editcontextsbuilder.getField("name"));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(nameField, "proactivemail");
            nsQuery.setConstraint(constraint);
            List editcontextList = editcontextsbuilder.getNodes(nsQuery);
            if(editcontextList.size()<1){

                //create entry for proactivemail in editcontext
                MMObjectNode editcontextsnode = editcontextsbuilder.getNewNode(admin);
                editcontextsnode.setValue("name", "proactivemail");
                editcontextsnode.setValue("otype", editcontexts);
                editcontextsbuilder.insert(admin, editcontextsnode);

                //find number of proactivemail editcontext
                NodeSearchQuery eQuery = new NodeSearchQuery(editcontextsbuilder);
                StepField eNameField = eQuery.getField(editcontextsbuilder.getField("name"));
                BasicFieldValueConstraint eConstraint = new BasicFieldValueConstraint(eNameField, "proactivemail");
                eQuery.setConstraint(eConstraint);
                editcontextList = editcontextsbuilder.getNodes(eQuery);
                if (editcontextList.size()>0){
                    editcontextsnode  = (MMObjectNode) editcontextList.get(0);
                    int proactivemailNb = editcontextsnode.getNumber();

                    //find number of systemadministrator role
                    MMObjectBuilder rolesbuilder = mmb.getBuilder("roles");
                    NodeSearchQuery rQuery = new NodeSearchQuery(rolesbuilder);
                    StepField rNameField = rQuery.getField(rolesbuilder.getField("name"));
                    BasicFieldValueConstraint rConstraint = new BasicFieldValueConstraint(rNameField, "systemadministrator");
                    rQuery.setConstraint(rConstraint);
                    List roleList = rolesbuilder.getNodes(rQuery);
                    if (roleList.size()>0){
                        MMObjectNode systAdmin  = (MMObjectNode) roleList.get(0);
                        int systAdminNb = systAdmin.getNumber();

                        //crete relation from systemadministrator role to proactivemail editcontext
                        MMObjectBuilder posrelbuilder = mmb.getBuilder("posrel");
                        MMObjectNode relation = posrelbuilder.getNewNode(username);
                        relation.setValue("snumber", proactivemailNb);
                        relation.setValue("dnumber", systAdminNb);
                        relation.setValue("rnumber", posrel);
                        relation.setValue("pos", 3);
                        posrelbuilder.insert(username, relation);
                    }
                }
            }



        } catch (Exception ex) {
            log.error("Can't initialize editcontext table with new value 'proactivemail'.");
        }
    }

}
