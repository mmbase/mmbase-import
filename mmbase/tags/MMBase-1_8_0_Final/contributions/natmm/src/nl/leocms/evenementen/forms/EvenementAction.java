/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.evenementen.forms;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import javax.servlet.*;
import nl.leocms.evenementen.Evenement;

import com.finalist.mmbase.util.CloudFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * EvenementAction
 *
 * @author Henk Hangyi
 *
 * @struts:action name="EvenementForm"
 *                path="/editors/evenementen/EvenementAction"
 *                scope="request"
 *                validate="true"
 *                input="/editors/evenementen/evenement.jsp"
 *
 * @struts:action-forward name="success" path="/editors/evenementen/evenement.jsp"
 */
public class EvenementAction extends Action {
   private static final Logger log = Logging.getLoggerInstance(EvenementAction.class);

   private void removeObsoleteFormBean(ActionMapping mapping, HttpServletRequest request) {
      // *** Remove the obsolete form bean ***
      if (mapping.getAttribute() != null) {
         if ("request".equals(mapping.getScope()))
            request.removeAttribute(mapping.getAttribute());
         else {
            HttpSession session = request.getSession();
            session.removeAttribute(mapping.getAttribute());
         }
      }
   }

   /**
    * The actual perform function: MUST be implemented by subclasses.
    *
    * @param mapping
    * @param form
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("EvenementAction.execute()");

        EvenementForm evenementForm = (EvenementForm) form;
        ActionForward forwardAction = null;
        String action = evenementForm.getAction();
        if (isCancelled(request)) {

            removeObsoleteFormBean(mapping, request);
            forwardAction = mapping.findForward("success");

        } else if(evenementForm.getButtons().getShowPastDates().pressed()) {               // ****************** ShowPastDates *************

           if(evenementForm.getShowPastDates().equals("true")) {
               evenementForm.setShowPastDates("false");
           } else {
               evenementForm.setShowPastDates("true");
           }

           forwardAction = mapping.findForward("continue");

        } else if(action.indexOf("Opslaan")>-1) {                                            // ********** Opslaan ********
            // change history:
            // concurrent modification exception -> clone()
            // duplicated dates one not related to parent -> Iterators

            // flush the coming events cache (see nl.mmatch.CSVReader)
            MMBaseContext mc = new MMBaseContext();
            ServletContext application = mc.getServletContext();
            application.removeAttribute("events_till");

            TreeSet newEvents = (TreeSet) evenementForm.getDates().clone();
            log.info("newEvents = " + newEvents);
            TreeSet oldEvents = (TreeSet) evenementForm.getOldDates().clone();
            log.info("oldEvents = " + oldEvents);

            Cloud cloud = CloudFactory.getCloud();

            String nodeNumber = evenementForm.getNode();
            Node node = null;
            if (!nodeNumber.equals("")) {
                node= cloud.getNode(nodeNumber);
            }

            Iterator newEventsIt = newEvents.iterator();
            while(newEventsIt.hasNext()) {
               Evenement thisEvent = (Evenement) newEventsIt.next();
               Node thisEventNode = null;
               if(!oldEvents.contains(thisEvent)) {
                  // *** this is a new node ***
                  thisEventNode = cloud.getNodeManager("evenement").createNode();
                  if(node==null) {
                      thisEventNode.setStringValue("soort","parent");
                      thisEventNode.setIntValue("reageer",12); // ** default closing time 12 hours before event
                  } else {
                      thisEventNode.setStringValue("soort","child");
                  }
                  // these values are set to their defaults (and will not be updated for child events)
                  thisEventNode.setIntValue("min_aantal_deelnemers",0);
                  thisEventNode.setIntValue("max_aantal_deelnemers",9999);
                  thisEventNode.setIntValue("cur_aantal_deelnemers",0);
                  thisEventNode.setStringValue("groepsexcursie","0"); // not used, see deelnemerscategorie.groepsactiviteit
                  thisEventNode.setStringValue("adres_verplicht","0");
                  thisEventNode.setStringValue("aanmelden_vooraf","1");
                  thisEventNode.setStringValue("achteraf_bevestigen","1");
                  thisEventNode.setStringValue("voorkeur_verplicht","0");
                  thisEventNode.commit();
                  if(node==null) {
                     node = thisEventNode;
                  } else {
                     Relation relation = node.createRelation(thisEventNode,cloud.getRelationManager("partrel"));
                     relation.commit();
                  }
               } else {
                  // *** this is an already existing node ***
                  thisEventNode= cloud.getNode(thisEvent.getNumber());
                  oldEvents.remove(thisEvent);
               }
               String titel = evenementForm.getName();
               titel = titel.substring(0,1).toUpperCase() + titel.substring(1);
               thisEventNode.setStringValue("titel",titel);
               thisEventNode.setLongValue("begindatum",(thisEvent.getBegin().getTime())/1000);
               thisEventNode.setLongValue("einddatum",(thisEvent.getEnd().getTime())/1000);
               thisEventNode.setStringValue("isspare",thisEvent.getIsSpareDate());
               thisEventNode.setStringValue("isoninternet",thisEvent.getIsOnInternet());
               if (!thisEventNode.getStringValue("iscanceled").equals(thisEvent.getIsCanceled())){
                  thisEventNode.setStringValue("iscanceled",thisEvent.getIsCanceled());
               }
               thisEventNode.setStringValue("dagomschrijving",evenementForm.selectedDays(thisEvent.getNumber()));
               thisEventNode.commit();

               NodeList userList = cloud.getNodeManager("users").getList("account='"+evenementForm.getUserId()+"'",null,null);
               if (userList.size()!=0) {
                  userList.getNode(0).createRelation(thisEventNode,cloud.getRelationManager("schrijver")).commit();
               }

            }

            // *** delete nodes not available in the user input from MMBase ***
            Iterator oldEventsIt = oldEvents.iterator();
            while(oldEventsIt.hasNext()) {
               Evenement thisEvent = (Evenement) oldEventsIt.next();
               Node oldEvent = cloud.getNode(thisEvent.getNumber());
               oldEvent.delete(true);
            }

            if(action.equals("Opslaan")) {
               evenementForm.initDates("" + node.getNumber());
               forwardAction = mapping.findForward("continue");
            } else {
               removeObsoleteFormBean(mapping, request);
               forwardAction = mapping.findForward("success");
            }

        } else if(action.equals("Voeg toe")) {                                               // *** Voeg toe ***

            Evenement newEvent = new Evenement(evenementForm.nextNewNodeNumber());
            evenementForm.update(newEvent);
            forwardAction = mapping.findForward("continue");

         } else if(action.equals("Wijzig")) {                                                // *** Wijzig ***

            TreeSet dates = evenementForm.getDates();
            String selectedEvent = evenementForm.getSelectedEvent();
            Iterator datesIterator = dates.iterator();
            while(datesIterator.hasNext()) {
               Evenement thisEvent = (Evenement) datesIterator.next();
               if(thisEvent.getNumber().equals(selectedEvent)) {
                  evenementForm.update(thisEvent);
               }
            }
            forwardAction = mapping.findForward("continue");

         } else if(action.equals("Wis")) {                                                   // *** Wis ***

            evenementForm.setBeginYear("");
            evenementForm.setBeginMonth("");
            evenementForm.setBeginDay("");
            evenementForm.setBeginHour("");
            evenementForm.setBeginMinute("");
            evenementForm.setEndYear("");
            evenementForm.setEndMonth("");
            evenementForm.setEndDay("");
            evenementForm.setEndHour("");
            evenementForm.setEndMinute("");
            evenementForm.setIsSpareDate("false");
            evenementForm.setIsOnInternet("false");
            evenementForm.setIsCanceled("false");
            forwardAction = mapping.findForward("continue");

         } else if(action.equals("Verwijder selectie")) {                                    // *** Verwijder selectie ***

            String [] selectedDates = evenementForm.getSelectedDates();
            TreeMap datesMap = evenementForm.getDatesMap();
            for(int i=0; i< selectedDates.length; i++) {
               if(datesMap.containsKey(selectedDates[i])) {
                  datesMap.remove(selectedDates[i]);
                  // *** could also remove from selectedDaysOfWeek, but this is not necessary ***
               }
            }
            evenementForm.setDates(new TreeSet(datesMap.values()));
            evenementForm.setSelectedDates(null);
            forwardAction = mapping.findForward("continue");

         } else if(action.equals("Activeer selectie")||action.equals("Annuleer selectie")) { // *** Annuleer / Activeer selectie ***

            String [] selectedDates = evenementForm.getSelectedDates();
            TreeMap datesMap = evenementForm.getDatesMap();
            for(int i=0; i< selectedDates.length; i++) {
               if(datesMap.containsKey(selectedDates[i])) {
                  Evenement thisEvent = (Evenement) datesMap.get(selectedDates[i]);
                  if(action.equals("Activeer selectie")) {
                     thisEvent.setIsCanceled("false");
                  } else {
                     thisEvent.setIsCanceled("true");
                  }
               }
            }
            evenementForm.setSelectedDates(null);
            forwardAction = mapping.findForward("continue");

         } else if(action.equals("Periode -> data")) {                                        // *** Periode -> data ***

            String [] selectedDates = evenementForm.getSelectedDates();
            TreeMap datesMap = evenementForm.getDatesMap();
            for(int i=0; i< selectedDates.length; i++) {
               if(datesMap.containsKey(selectedDates[i])) {
                  Evenement thisEvent = (Evenement) datesMap.get(selectedDates[i]);
                  evenementForm.createDatesFromPeriod(thisEvent);
               }
            }
            evenementForm.setSelectedDates(null);
            forwardAction = mapping.findForward("continue");

        }
        return forwardAction;
   }
}
