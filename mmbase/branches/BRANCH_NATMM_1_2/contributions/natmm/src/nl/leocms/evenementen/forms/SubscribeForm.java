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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.*;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.evenementen.Evenement;
import nl.leocms.applications.NatMMConfig;
import nl.leocms.util.tools.SearchUtil;
import nl.mmatch.CSVReader;
import com.cfdev.mail.verify.EmailVerifier;

public class SubscribeForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(EvenementForm.class);
   public static String initPhone = "0..-....";
   public static String cashPaymentType = "Contant";

   // backoffice subscriptions from /editors/evenementen/subscribe.jsp
   public static String CHANGE_ACTION           = "Wijzig";
   public static String SUBSCRIBE_ACTION        = "Meld aan";
   public static String NEW_SUBSCRIPTION_ACTION = "Nieuwe aanmelding";
   public static String ADDRESS_ACTION          = "Adres en betalingswijze";
   public static String CONFIRM_ACTION          = "Bevestig aanmelding";
   
   // website subscriptions from /natmm/includes/events/subscribe.jsp
   public static String TO_AGENDA_ACTION        = "Naar agenda";
   public static String CANCEL_ACTION           = "Annuleer";
   public static String OTHER_DATES_ACTION      = "andere data";
   public static String SELECT_DATE_ACTION      = "select_date";
   public static String FIX_DATE_ACTION         = "fix_date";
   public static String PROMPT_FOR_CONFIRMATION = "promptforconfirmation";
   public static String CANCELED                = "canceled";

   private String action;
   private int validateCounter = 0;
   private String skipValidation;
   private SubscribeButtons buttons = null;
   private String showAddress;
   private String showPastDates;

   private String node;
   private String parent;
   private String subscriptionNumber;
   private String prefix;
   private String initials;
   private String firstName;
   private String suffix;
   private String lastName;
   private String privatePhone;
   private String email;
   private String memberId;
   private String streetName;
   private String houseNumber;
   private String city;
   private String country;
   private String zipCode;
   private String source;
   private String description;
   private String bankaccount;

   
   private String selectedParticipant;
   private String numberInCategory;
   private String participantsCategory;
   private String [] participantsPerCat;
   private String status;

   private String TicketOffice;
   private String TicketOfficeSource;   
   private String userId;
   private String paymentType;
   private String pageNumber;
   private String gender;
   
   private String extraText;
   private String lastSentMessage;
   
   private boolean inProcess;
   
   private Date start;

   public void reset(ActionMapping mapping, HttpServletRequest request) { // reset is necessary for checkboxes in session
      buttons = new SubscribeButtons();
   }

   public String getAction() { return action; }
   public void setAction(String action ) {
         if(action==null) { action= ""; }
         this.action = action;
   }

   public int getValidateCounter() { return validateCounter; }
   public void setValidateCounter(int validateCounter) { this.validateCounter = validateCounter; }

   public String getSkipValidation() {
      if(skipValidation==null) { skipValidation = "N"; }
      return skipValidation;
   }
   public void setSkipValidation(String skipValidation) { this.skipValidation = skipValidation;  }

   public SubscribeButtons getButtons() { return buttons; }
   public void setButtons(SubscribeButtons buttons) { this.buttons = buttons; }

   public String getShowAddress() { return showAddress; }
   public void setShowAddress(String showAddress) { this.showAddress = showAddress; }

   public String getShowPastDates() { return showPastDates; }
   public void setShowPastDates(String showPastDates) { this.showPastDates = showPastDates; }

   public String getNode() { return node; }
   public void setNode(String node) { this.node = node; }

   public String getParent() { return parent; }
   public void setParent(String parent) { this.parent = parent; }

   public String getSubscriptionNumber() { return subscriptionNumber; }
   public void setSubscriptionNumber(String subscriptionNumber) { this.subscriptionNumber = subscriptionNumber; }

   public String getPrefix() { return prefix; }
   public void setPrefix(String prefix) { this.prefix = prefix; }

   public String getInitials() { return initials; }
   public void setInitials(String initials) { this.initials = initials.toUpperCase(); }

   public String getFirstName() { return firstName; }
   public void setFirstName(String firstName ) { this.firstName = cleanName(firstName); }
   public static String cleanName(String name) {
      String thisName = name;
      if(!thisName.equals("")) {
         thisName = thisName.substring(0,1).toUpperCase() + thisName.substring(1);
      }
      return thisName;
   }

   public String getSuffix() { return suffix; }
   public void setSuffix(String suffix) { this.suffix = suffix; }

   public String getLastName() { return lastName; }
   public void setLastName(String lastName) { this.lastName = cleanName(lastName); }

   public String getPrivatePhone() {
      if(this.privatePhone==null||this.privatePhone.equals("")) { this.privatePhone = initPhone; }
      return privatePhone;
   }
   public void setPrivatePhone(String privatePhone) { this.privatePhone = privatePhone; }

   public String getPhoneOnClickEvent() {
      return "if(this.value=='" + initPhone + "') { this.value=''; }";
   }

   public String getEmail() { return email; }
   public void setEmail(String email) { this.email = cleanEmail(email); }
   public static String cleanEmail(String email) {
      return email.replaceAll(" ","").replaceAll(",",".");
   }

   public String getMemberId() { return memberId; }
   public void setMemberId(String memberId) { this.memberId = cleanPid(memberId); }
   public static String cleanPid(String id) {
      String memberId = id;
      if(memberId==null) { memberId = ""; }
      // delete everything which is not a number
      int charPos = 0;
      while(charPos < memberId.length()){
         char c = memberId.charAt(charPos);
         if( c<'0' || c>'9' ) {
            memberId = memberId.substring(0,charPos) + memberId.substring(charPos+1);
         } else {
            charPos++;
         }
      }
      // delete trailing zero's
      while(!memberId.equals("")&&memberId.charAt(0)=='0') { memberId = memberId.substring(1); }
      return memberId;
   }

   public String getStreetName() { return streetName; }
   public void setStreetName(String streetName) { this.streetName =  cleanName(streetName); }

   public String getHouseNumber() { return houseNumber; }
   public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber.toUpperCase(); }

   public String getCity() { return city; }
   public void setCity(String city) { this.city = cleanName(city); }

   public String getExtraText() { return extraText; }
   public void setExtraText(String extraText) { this.extraText = cleanName(extraText); }

   public String getLastSentMessage() { return lastSentMessage; }
   public void setLastSentMessage(String lastSentMessage) { this.lastSentMessage = cleanName(lastSentMessage); }   
   
   public String getCountry() { return country; }
   public void setCountry(String country) { this.country = cleanName(country); }   
   
   public String getZipCode() { return zipCode; }
   public void setZipCode(String zipCode) { this.zipCode = cleanZipCode(zipCode); }
   public static String cleanZipCode(String zipCode) {
      return zipCode.toUpperCase().replaceAll(" ","");
   }

   public String getSource() { return source; }
   public void setSource(String source) { this.source = source; }

   public String getDescription() { return description; }
   public void setDescription(String description) { this.description = description; }

   public String getBankaccount() { return bankaccount; }
   public void setBankaccount(String bankaccount) { this.bankaccount = cleanPid(bankaccount); }

   public String getSelectedParticipant() { return selectedParticipant; }
   public void setSelectedParticipant(String selectedParticipant) { this.selectedParticipant = selectedParticipant; }

   public String getNumberInCategory() { return numberInCategory; }
   public void setNumberInCategory(String numberInCategory) { this.numberInCategory = numberInCategory; }

   public String getParticipantsCategory() { return participantsCategory; }
   public void setParticipantsCategory(String participantsCategory ) { this.participantsCategory = participantsCategory; }

   public String getParticipantsPerCat(int index) {
     return participantsPerCat[index];
   }

   public void setParticipantsPerCat(int index, String value) {
     participantsPerCat[index] = value;
   }

   public String getStatus() { return status; }
   public void setStatus(String status) { this.status = status; }

   public String getTicketOffice() { return TicketOffice; }
   public void setTicketOffice(String TicketOffice) { this.TicketOffice = TicketOffice; }

   public String getTicketOfficeSource() { return TicketOfficeSource; }
   public void setTicketOfficeSource(String TicketOfficeSource) { this.TicketOfficeSource = TicketOfficeSource; }

   public String getUserId() { return userId; }
   public void setUserId(String userId ) { this.userId = userId; }

   public String getPageNumber(){ return pageNumber; }
   public void setPageNumber(String pageNumber) { this.pageNumber = pageNumber; }

   public String getGender() { return gender; }
   public void setGender(String gender) { this.gender = gender; }

   public String getPaymentType() { return paymentType; }
   public void setPaymentType(String paymentType) {
      if(paymentType==null||paymentType.equals("")) {
         paymentType = cashPaymentType;
      }
      this.paymentType = paymentType;
   }

   public String getDefaultPaymentType(){
      String paymentType = cashPaymentType;
      if (this.pageNumber!=null){
         Node pagina = CloudFactory.getCloud().getNode(this.pageNumber);
         NodeList payments = pagina.getRelatedNodes("payment_type", "posrel", "DESTINATION");
         NodeIterator i = payments.nodeIterator();
         if (i.hasNext()) {
            Node payment = i.nextNode();
            paymentType = payment.getStringValue("naam");
         }
      }
      return paymentType;
   }

   public boolean getInProcess() { return inProcess; }
   public void setInProcess(boolean inProcess) {
      log.info(getTimePassed((inProcess ? "in process" : "idle")));
      this.inProcess = inProcess;
   }
   
   public String getTimePassed(String action) {
      return action + " after " + ((new Date()).getTime() - start.getTime())/1000 + "s";
   }
   
   public void resetBean() {
      // *** called by SubscribeAction.nieuweaanmelding

      resetNumbers();

      this.gender = "";
      this.prefix = "";
      this.initials = "";
      this.firstName = "";
      this.suffix = "";
      this.lastName = "";
      this.privatePhone = initPhone;
      this.email = "";
      this.memberId = "";
      this.streetName ="";
      this.houseNumber ="";
      this.city ="";
      this.country ="";
      this.zipCode = "";
      this.paymentType = "";
      
      this.lastSentMessage = "";
      this.extraText = "";
   }

   public void resetNumbers() {
      // *** called by SubscribeInitAction.execute and resetBean
      this.action = "startsubscription";
      this.validateCounter = 0;
      this.skipValidation = "N";

      Node thisEvent = CloudFactory.getCloud().getNode(this.getNode());
      
      this.showAddress = "true";

      if(thisEvent.getLongValue("embargo") < (new Date()).getTime()/1000) {
         this.showPastDates = "false";
      } else {
         this.showPastDates = "";
      }

      this.source = "";
      this.description = "";

      this.selectedParticipant = "";
      this.numberInCategory = "";
      this.participantsCategory = "";
      this.participantsPerCat = new String[10];
      for(int i = 0; i<10; i++) { this.participantsPerCat[i] = "0"; }
      this.status = "";

      this.TicketOffice = "backoffice";
      this.TicketOfficeSource = "";

      this.lastSentMessage = "";
      this.extraText = "";      
      
   }

   public String findParent() {
      return Evenement.findParentNumber(this.node);
   }

   public Node createParticipant(Cloud cloud, String action, Node thisEvent, Node thisSubscription, String thisCategory, String thisNumber) {

      Node thisParticipant = null;
      if(action.equals(CHANGE_ACTION)) {
        try {
            thisParticipant = cloud.getNode(getSelectedParticipant());
        } catch (Exception e) {
            log.info("Action 'Wijzig' for a none existing participant."
               + " Probably editor (1) first selected, (2) then deleted and (3) then tried to change the participant");
        }
      }
      if(thisParticipant==null) {
        thisParticipant = cloud.getNodeManager("deelnemers").createNode();
      }

      thisParticipant.setStringValue("prefix",getPrefix());
      thisParticipant.setStringValue("initials",getInitials());
      thisParticipant.setStringValue("firstname",getFirstName());
      thisParticipant.setStringValue("suffix",getSuffix());
      thisParticipant.setStringValue("lastname",getLastName());
      thisParticipant.setStringValue("email",getEmail());
      thisParticipant.setStringValue("privatephone", (getPrivatePhone().equals(initPhone) ? "" : getPrivatePhone()));
      thisParticipant.setStringValue("straatnaam",getStreetName());
      thisParticipant.setStringValue("huisnummer",getHouseNumber());
      thisParticipant.setStringValue("plaatsnaam",getCity());
      thisParticipant.setStringValue("land",getCountry());
      thisParticipant.setStringValue("postcode",getZipCode());
      thisParticipant.setStringValue("lidnummer",getMemberId());
      thisParticipant.setStringValue("gender",getGender());
      thisParticipant.commit();

      NodeList userList = cloud.getNodeManager("users").getList("account='"+userId+"'",null,null);
      if (userList.size()!=0) {
         userList.getNode(0).createRelation(thisParticipant,cloud.getRelationManager("schrijver")).commit();
      }

      Relation thisRel = null;
      if(action.equals(CHANGE_ACTION)) {
         RelationList relations = thisParticipant.getRelations("posrel","inschrijvingen");
         if(relations.size()>0) {
            thisRel = relations.getRelation(0);
         }
      }
      if(thisRel==null) {
         thisRel = thisSubscription.createRelation(thisParticipant,cloud.getRelationManager("posrel"));
      }

      // set the price for this participant
      int costs = SubscribeAction.DEFAULT_COSTS;
      String sParent = Evenement.findParentNumber(thisEvent.getStringValue("number"));
      if(!Evenement.isGroupBooking(cloud,thisParticipant.getStringValue("number"))) {
         // this is a regular excursion
         NodeList dcl = cloud.getList( sParent
                                       ,"evenement,posrel,deelnemers_categorie"
                                       ,"posrel.pos"
                                       ,"deelnemers_categorie.number='"+ thisCategory+ "'",null,null,null,false);
         if(dcl.size()>0) {
            costs = dcl.getNode(0).getIntValue("posrel.pos");
            // if these are members of a group_excursion, but not the main group excursion participant: set costs to zero
            if(Evenement.isGroupExcursion(cloud,sParent)
               && (costs==SubscribeAction.GROUP_EXCURSION_COSTS || costs==SubscribeAction.DEFAULT_COSTS )) {
                     costs = 0;
            }
         }
         costs = costs * Integer.parseInt(thisNumber);
      } else {
         // this is the subscription for group excursion
         costs =  Evenement.getGroupExcursionCosts(cloud, sParent, thisSubscription.getStringValue("number"));
      }
      thisRel.setIntValue("pos",costs);
      thisRel.commit();

      // *** update deelnemers,related,deelnemers_categorie
      if(!Evenement.isGroupBooking(cloud,thisParticipant.getStringValue("number"))) {

         thisParticipant.setStringValue("bron",thisNumber);
         thisParticipant.commit();
         RelationList relations = thisParticipant.getRelations("related","deelnemers_categorie");
         for(int r=0; r<relations.size(); r++) { relations.getRelation(r).delete(true); }
         if(!thisCategory.equals("-1")) {

           Node thisCategoryNode = cloud.getNode(thisCategory);
           thisParticipant.createRelation(thisCategoryNode,cloud.getRelationManager("related")).commit();

         }
      }

      thisEvent.commit(); // *** save to update cur_aantal_deelnemers

      return thisParticipant;
   }

   public static String getMemberIdMessage(String memberId, String zipCode) {
      String memberIdMessage = "";
      if(!memberId.equals("")) {
         try {
            for(int charPos = 0; charPos < memberId.length(); charPos++){ // delete everything which is not a number
               char c = memberId.charAt(charPos);
               if  (!(('0'<=c)&&(c<='9'))) {
                   memberId = memberId.substring(0,charPos) + memberId.substring(charPos+1);
               }
            }
            int dummy = (new Integer(memberId)).intValue(); // to delete trailing zero's
            memberId = "" + dummy;

            MMBaseContext mc = new MMBaseContext();
            ServletContext application = mc.getServletContext();
            TreeMap zipCodeTable = (TreeMap) application.getAttribute("zipCodeTable");
            if(zipCodeTable==null) {
               (new CSVReader(CSVReader.ONLY_MEMBERLOAD)).run();
               memberIdMessage = "evenementen.members.membersnotloaded";
            } else {
               String zipFromTable = (String) zipCodeTable.get(memberId);
               if(zipFromTable==null) {
                  memberIdMessage = "evenementen.members.notfound";
               } else if(zipFromTable.equals("")) {
                  // *** Give members without zipcode (0.3% of total) benefit of the doubth
                  memberIdMessage = "evenementen.members.nozipcode";
               } else if(!zipFromTable.equals(zipCode)) {
                  memberIdMessage = "evenementen.members.otherzipcode";
               }
            }
         } catch(Exception e) {
           memberIdMessage = "evenementen.members.memberid_nan";
         }
      }
      return memberIdMessage;
   }

   public static String getZipCodeMessage(String zipCode) {
      String zipCodeMessage = "";
      if(!zipCode.equals("")) {

         if(zipCode.length()==6) {
            boolean bValidZipCode = true;
            for(int i=0; i<4; i++) {
                  bValidZipCode = bValidZipCode && ('0'<= zipCode.charAt(i)) && (zipCode.charAt(i) <= '9');
            }
            for(int i=5; i<6; i++) {
                  bValidZipCode = bValidZipCode && ('A'<= zipCode.charAt(i)) && (zipCode.charAt(i) <= 'Z');
            }
            if(!bValidZipCode) {
               zipCodeMessage = "evenementen.members.zipcode_incorrect";
            }
         } else {
           zipCodeMessage = "evenementen.members.zipcode_incorrect";
         }
      } else {
        zipCodeMessage = "evenementen.required.zipcode";
      }
      return zipCodeMessage;
   }

   public static String getPhoneMessage(String sPhone) {
      String phoneMessage = "";
      if(!sPhone.equals(initPhone)) {
         if(sPhone.indexOf("-")==-1) {
             phoneMessage = "evenementen.phone.onedashrequired";
         } else {
            int iDash = sPhone.indexOf("-");
            sPhone = sPhone.substring(0,iDash) + sPhone.substring(iDash+1);
            if(sPhone.indexOf("-")>-1) {
               phoneMessage = "evenementen.phone.onedashrequired";
            } else if(sPhone.length()!=10) {
               phoneMessage = "evenementen.phone.tendigits";
            } else {
               try {
                  int dummy = (new Integer(sPhone)).intValue();
               } catch(Exception e) {
                  phoneMessage = "evenementen.phone.nan";
               }
            }
         }
      }
      return phoneMessage;
   }

   public static String getEmailMessage(String sEmail, String requiredMessage) {
      String emailMessage = "";
      if(sEmail.equals("")) {
          emailMessage = requiredMessage;
      } else if (sEmail.indexOf("@")==-1){
           emailMessage = "evenementen.email.no_at";
      } else if (!EmailVerifier.validateEmailAddressSyntax(sEmail)) {
          emailMessage = "evenementen.email.invalid";
      } else if ( NatMMConfig.checkEmailByMailHost &&
         ( !EmailVerifier.validateMXRecord(sEmail)  || !EmailVerifier.validateMailServer(sEmail)) ) {
         emailMessage = "evenementen.email.invalid";
      }
      return emailMessage;
   }

   public static String getBankAccountMessage(String sBankAccount) {
      String sMessage = "";
      try {
         boolean validAccount = false;
         long iBankAccount = Long.parseLong(sBankAccount);
         long twoTimesNine   = 99;
         long sevenTimesNine = 9999999;
         long eightTimesNine = 99999999;
         long nineTimesNine  = 999999999;
         boolean validDutchBankAccount = ( eightTimesNine <= iBankAccount ) && ( iBankAccount<= nineTimesNine );
         boolean validDutchPostbankAccount = ( twoTimesNine <= iBankAccount ) && ( iBankAccount<= sevenTimesNine );
         if (validDutchBankAccount) {
            int checkSum = 0;
            for(int i = 0; i < sBankAccount.length(); i++) {
               checkSum += (i+1)*Integer.parseInt("" + sBankAccount.charAt((sBankAccount.length()-1)-i));
            }
            validAccount = (checkSum%11)==0;
         } else if (validDutchPostbankAccount) {
            validAccount = true;
         }
         if(!validAccount) {
            sMessage = "membershipform.bankaccount.notvalid";
         }
      } catch (Exception e) {
         sMessage = "membershipform.bankaccount.notvalid";
         log.error("Bank account " + sBankAccount + " is not a number.");
      }
      return sMessage;
   }


   public boolean participantsCategoryIsSet(Cloud cloud, String sParent, String sParticipantsCategory) {
      boolean pcIsNotSet = sParticipantsCategory.equals("-1");
      if(pcIsNotSet) {
         NodeList nl = cloud.getList(sParent,
            "evenement,posrel,deelnemers_categorie",
            null,null,null,null,null,false);
         pcIsNotSet = (nl.size()>0); // no related deelnemers_categorie also means that pc is set
         if(pcIsNotSet) {
             pcIsNotSet = !Evenement.isGroupExcursion(cloud,parent); // groups excursion also means pc is set
         }
      }
      return !pcIsNotSet;
   }

   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      
      log.info("SubscribeForm.validate(" + this.getAction() + ")");
      start = new Date();

      ActionErrors errors = new ActionErrors();
      
      if (this.getButtons().getGoBack().pressed()){
         return errors;
      }

      Cloud cloud = CloudFactory.getCloud();

      if(this.node==null) { // *** browser lost session ***
         errors.add("warning",new ActionError("evenementen.session.lostsession"));
      }

      String action = this.getAction();
      if(this.getAction()==null){

         log.info("null action in SubscribeForm");

      } else if(this.getAction().equals(CHANGE_ACTION)&&this.getSelectedParticipant().equals("")) {                // *** Wijzig ***

         errors.add("warning",new ActionError("evenementen.noselection.change"));

      } else if(this.getButtons().getAddParticipant().pressed()) {                                                // *** Add ***

         if(this.getSelectedParticipant().equals("")){
            errors.add("warning",new ActionError("evenementen.noselection.add"));
         }

         if(!this.participantsCategoryIsSet(cloud, this.getParent(), this.getParticipantsCategory())) {
            errors.add("warning",new ActionError("evenementen.nodeelnemercategorie.add"));
         }

      } else if(this.getButtons().getConfirmSubscription().pressed()) {                                            // *** Confirm ***

         Node thisParticipant = cloud.getNode(this.getSelectedParticipant());
         String toEmailAddress = thisParticipant.getStringValue("email");

         if(toEmailAddress.equals("")) {
            errors.add("warning",new ActionError("evenementen.email.notspecified"));
         } else if(toEmailAddress.indexOf("@")==-1) {
            errors.add("warning",new ActionError("evenementen.email.notvalid"));
         }

      }

      if(this.getAction().equals(SUBSCRIBE_ACTION)||this.getAction().equals(CHANGE_ACTION)) {                       // *** Meld aan / Wijzig ***

         // *** this form is already processing a subscription, signal the user to wait
         if(this.getInProcess()) {
           errors.add("warning",new ActionError("evenementen.subscription_in_process"));
         } else {
           setInProcess(true);
         }

         if(this.getLastName().equals("")) {
            errors.add("warning",new ActionError("evenementen.required.lastname"));
         }

         if(!this.participantsCategoryIsSet(cloud, this.getParent(), this.getParticipantsCategory())) {
            errors.add("warning",new ActionError("evenementen.nodeelnemercategorie.add"));
         }

         // *** check whether member id can be found in application attribute
         String memberIdMessage = getMemberIdMessage(memberId,this.getZipCode());
         if(!memberIdMessage.equals("")&&!memberIdMessage.equals("evenementen.members.nozipcode")) {
            errors.add("warning",new ActionError(memberIdMessage));
         }
         // *** check if the zipcode is valid 
         if(this.getSkipValidation().equals("N")) {
            String zipCodeMessage = getZipCodeMessage(this.getZipCode());
            if(!zipCodeMessage.equals("")&&!memberIdMessage.equals("evenementen.members.nozipcode")) {
               errors.add("warning",new ActionError(zipCodeMessage));
            }
         }
         // *** check if the phonenumber is valid
         boolean bPhoneFound = false;
         String phoneMessage = getPhoneMessage(this.getPrivatePhone());
         if(this.getSkipValidation().equals("N") && !phoneMessage.equals("")) {                                      
            errors.add("warning",new ActionError(phoneMessage));
         } else if(!this.getPrivatePhone().equals(initPhone)){
            bPhoneFound = true;
         }
         // *** check if the email is valid
         String requiredMessage = "";
         if(this.getEmail().equals("")){
            if(this.getTicketOffice().equals("website")) {
               requiredMessage = "evenementen.email.required";
            } else if (!bPhoneFound) {
               requiredMessage = "evenementen.required.phoneoremail";
            }
         }
         String emailMessage = getEmailMessage(this.getEmail(), requiredMessage);
         if(!emailMessage.equals("")){
            errors.add("warning",new ActionError(emailMessage));
         }
         // *** check if the bankaccount is valid
         if(!this.getBankaccount().equals("")) {
            String bankAccountMessage = getBankAccountMessage(this.getBankaccount());
            if(!bankAccountMessage.equals("")) {
               errors.add("warning",new ActionError(bankAccountMessage));
            }
         }
         if(this.getTicketOffice().equals("website")) {

           String titel = this.getFirstName();
           if (this.getSuffix() != null && !"".equals(this.getSuffix().trim())) {
             titel += " " + this.getSuffix();
           }
           titel +=  " " + this.getLastName();
           String deelnemersConstraint = "deelnemers.titel LIKE '" + (new SearchUtil()).superSearchString(titel) + "'";
           log.info("looking for deelnemers for event " + node + " with " + deelnemersConstraint);
           NodeList nl = cloud.getList( node
                  ,"evenement,posrel,inschrijvingen,posrel,deelnemers"
                  ,"deelnemers.titel", deelnemersConstraint
                  ,null,null,null,true);
           if(nl.size()>0) {
             errors.add("warning",new ActionError("evenementen.double_booking_on_name"));
           }
           
           if(this.getFirstName().equals("")&&this.getInitials().equals("")) {
               errors.add("warning",new ActionError("evenementen.required.initials_or_firstname"));
            }

            if(this.getPrivatePhone().equals(initPhone)) {
                errors.add("warning",new ActionError("evenementen.required.phone"));
            }
            if(this.getStreetName().equals("")) {
               errors.add("warning",new ActionError("evenementen.required.streetname"));
            }
            if(this.getHouseNumber().equals("")) {
               errors.add("warning",new ActionError("evenementen.required.housenumber"));
            }
            if(this.getCity().equals("")) {
               errors.add("warning",new ActionError("evenementen.required.city"));
            }

            int iParticipants = 0;
            for(int i = 0; i<10; i++) {
               iParticipants += Integer.parseInt(this.participantsPerCat[i]);
            }
            if(iParticipants==0) {
               errors.add("warning",new ActionError("evenementen.required.participant"));
            } else if(iParticipants>5) {
               errors.add("warning",new ActionError("evenementen.required.notexceedfive"));
            } else {
               int iCurPart = Integer.parseInt(cloud.getNode(node).getStringValue("cur_aantal_deelnemers"));
               int iMaxPart = Integer.parseInt(cloud.getNode(parent).getStringValue("max_aantal_deelnemers"));
               if(iCurPart>=iMaxPart) {
                  errors.add("warning",new ActionError("evenementen.required.notfullybooked"));
               } else if((iCurPart+iParticipants)>iMaxPart) {
                  errors.add("warning",new ActionError("evenementen.required.notexceedmax"));
               }
            }
         } else {

            if(cloud.getNode(parent).getStringValue("adres_verplicht").equals("1")) {
               if(this.getStreetName().equals("")) {
                  errors.add("warning",new ActionError("evenementen.required.streetname"));
               }
               if(this.getHouseNumber().equals("")) {
                  errors.add("warning",new ActionError("evenementen.required.housenumber"));
               }
               if(this.getCity().equals("")) {
                  errors.add("warning",new ActionError("evenementen.required.city"));
               }
            }

            String sParticipants = this.getNumberInCategory();
            try {
               int dummy = (new Integer(sParticipants)).intValue();
               if(dummy==0) {
                  errors.add("warning",new ActionError("evenementen.numberofparticipants.nonzero"));
               }
            } catch(Exception e) {
               errors.add("warning",new ActionError("evenementen.numberofparticipants.nan"));
            }
         }
         
         
         if(errors.size()>0) {
            validateCounter++;
            setInProcess(false);
         }
      }
      log.info(getTimePassed("finished evaluation"));
      return errors;
   }
}

