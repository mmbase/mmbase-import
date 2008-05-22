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
package nl.leocms.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.*;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.evenementen.forms.SubscribeForm;
import nl.leocms.evenementen.forms.SubscribeAction;
import nl.leocms.evenementen.stats.OptionedStats;
import nl.leocms.applications.NatMMConfig;
import nl.mmatch.CSVReader;

public class MembershipForm extends ActionForm {

   private static final Logger log = Logging.getLoggerInstance(MembershipForm.class);
   public static String initAction = "PLEASE FILL IN THE FORM";
   public static String submitAction = "JA, IK WORD LID";
   public static String correctAction = "PLEASE FILL ALL NECESSARY FIELDS IN THE FORM";
   public static String skipValidationAction = "NEGEER CONTROLE OP POSTCODE";
   public static String backAction = "TERUG";
   public static String confirmAction = "BEVESTIG";
   public static String readyAction = "Naar de homepage";
   public static String AUTHORIZE = "auth";
   public static String INVOICE = "invoice";
   public static String YEAR = "J";
   public static String MONTH = "M";
   public static String DEFAULT_COUNTRY = "NEDERLAND";
   public static int MINIMUM_PER_MONTH = 200;
   public static int MINIMUM_PER_YEAR = 2000;

   private String action = initAction;
   private int validateCounter = 0;
   private String node;
   private String referid;
   private String gender;
   private String firstname;
   private String initials;
   private String suffix;
   private String lastname;
   private String street;
   private String housenumber;
   private String housenumber_extension;
   private String zipcode;
   private String city;
   private String country_code;
   private String organisation;
   private String extra_info;
   private String phone;
   private String bankaccount;
   private String payment;          // e.g. M200 or J2500
   private String payment_type;     // INVOICE or AUTHORIZE
   private String payment_period;   // M or J
   private String amount;
   private Date subscribe_date;
   private int memberid;
   private int externid;
   private String email;
   private String digital_newsletter;
   private String senior;
   private String product1;
   private String product2;
   private String product3;
   private int batchnumber;
   private int serialnumber;
   private String status;
   private Date downloaddate;
   private String dayofbirthDate;
   private int dayofbirthMonth;
   private String dayofbirthYear;
   private ArrayList streets;		// used to extract multiple streets for one zip code

   TreeMap zipCodeMap;

   private boolean assertZipCodeMap() {
      if(zipCodeMap==null) {
         MMBaseContext mc = new MMBaseContext();
         ServletContext application = mc.getServletContext();
         zipCodeMap = (TreeMap) application.getAttribute("zipCodeMap");
         if(zipCodeMap==null) {
            (new CSVReader(CSVReader.ONLY_ZIPCODELOAD)).run();
            zipCodeMap = (TreeMap) application.getAttribute("zipCodeMap");
         }
      }
      return country_code!=null && country_code.equals(DEFAULT_COUNTRY) && (zipCodeMap!=null);
   }

   public String getAction() { return action; }
   public void setAction(String action ) {
         if(action==null) { action= initAction; }
         this.action = action;
   }

   public String getNode() { return node; }
   public void setNode(String node) { this.node = node; }

   public int getValidateCounter() { return validateCounter; }
   public void setValidateCounter(int validateCounter) { this.validateCounter = validateCounter; }

   public String getReferid() { return referid; }
   public void setReferid(String referid) { this.referid = referid; }


   public String getDigital_newsletter() {
      if(digital_newsletter==null) { digital_newsletter = "J"; }
      return digital_newsletter;
   }
   public void setDigital_newsletter(String digital_newsletter) {
      this.digital_newsletter = digital_newsletter;
   }

   public String getGender() { if (gender==null||gender.equals("")) { gender = MONTH; } return gender; }
   public void setGender(String gender) { this.gender = gender; }

   public String getFirstname() { return firstname; }
   public void setFirstname(String firstname) { this.firstname = SubscribeForm.cleanName(firstname); }

   public String getInitials() { return initials; }
   public void setInitials(String initials) { this.initials = cleanInitials(initials); }

   public String cleanInitials(String initials) {
      initials = initials.toUpperCase();
      int i = 0;
      while(i < initials.length() ){
         char c = initials.charAt(i);
         if  ( !(('A'<=c)&&(c<='Z')) ) {
            initials = initials.substring(0,i) + initials.substring(i+1);
         }
         i++;
      }
      String initialsWithDots = "";
      i = 0;
      while(i < initials.length()) {
         initialsWithDots += initials.charAt(i) + ".";
         i++;
      }
      return initialsWithDots;
   }

   public String getSuffix() { return suffix; }
   public void setSuffix(String suffix) { this.suffix = suffix; }

   public String getLastname() { return lastname; }
   public void setLastname(String lastname) { this.lastname = SubscribeForm.cleanName(lastname); }

   /*
   public String getStreet() {
      if(assertZipCodeMap()) {
         street = CSVReader.getStreet(zipCodeMap, zipcode, street);
      }
      return street;
   }
   */
   public String getStreet() {
	      return street;
	   }
   public void setStreet(String street) { this.street = street.toUpperCase(); }

   /**
    * Returns all streets for the zip code specified in this object.
    * 
    * @return All streets for a passed zip code or an empty arraylist if none can be found
    */
   public ArrayList getStreets() {
	   if(assertZipCodeMap()) {
		   return CSVReader.getStreets(zipCodeMap, zipcode, street);
	   }
	   return new ArrayList();
   }
   public void setStreets(Object streets) { }
   

   public String getHousenumber() { return housenumber; }
   public void setHousenumber(String housenumber) { this.housenumber = housenumber; }

   public String getHousenumber_extension() { return housenumber_extension; }
   public void setHousenumber_extension(String housenumber_extension) { this.housenumber_extension = housenumber_extension; }

   public String getZipcode() { return zipcode; }
   public void setZipcode(String zipcode) { this.zipcode = SubscribeForm.cleanZipCode(zipcode); }

   public String getCity() {
      if(assertZipCodeMap()) {
         city = CSVReader.getCity(zipCodeMap, zipcode, city);
      }
      return city;
   }
   public void setCity(String city) { this.city = city.toUpperCase(); }

   public String getCountry_code() { if(country_code==null||country_code.equals("")) { country_code = DEFAULT_COUNTRY; } return country_code; }
   public void setCountry_code(String country_code) { this.country_code = country_code; }

   public String getOrganisation() { return organisation; }
   public void setOrganisation(String organisation) { this.organisation = organisation; }

   public String getExtra_info() { return extra_info; }
   public void setExtra_info(String extra_info) { this.extra_info = extra_info; }

   public String getPhone() {
      if(phone==null||phone.equals("")) { phone = SubscribeForm.initPhone; }
      return phone;
   }
   public void setPhone(String phone) { this.phone = phone; }

   public String getPhoneOnClickEvent() {
      return (new SubscribeForm()).getPhoneOnClickEvent();
   }

   public String getBankaccount() { return bankaccount; }
   public void setBankaccount(String bankaccount) { this.bankaccount = SubscribeForm.cleanPid(bankaccount); }

   public String getPayment_type() {
      if(payment_type==null||payment_type.equals("")) {
         payment_type = AUTHORIZE;
      } else if(payment_type.equals(INVOICE)) { // in case of invoice, no payment
         setPayment("");
         setPayment_period("");
         setAmount("");
      }
      return payment_type;
   }
   public void setPayment_type(String payment_type) { this.payment_type = payment_type; }

   /* payment vs. payment_period + amount are mutually exclusive options */

   public String getPayment() {
      if(payment==null) { payment = ""; }
      return payment;
   }
   public void setPayment(String payment) {
      if(payment!=null&&!payment.equals("")) {
         this.payment_period = "";
         this.amount = "";
      }
      this.payment = payment;
   }

   public String getPayment_period() {
      if(payment_period==null) { payment_period = ""; }
      return payment_period;
   }
   public void setPayment_period(String payment_period) {
      if(payment_period!=null&&!payment_period.equals("")) {
         this.payment = "";
      }
      this.payment_period = payment_period;
   }

   public String getAmount() {
      String sAmount = null;
      if(payment!=null&&!payment.equals("")) {
         // a choice has been made by using the radio buttons
         sAmount = "";
      } else {
         sAmount = getSamount();
         if(sAmount.equals("0,00")) {
            sAmount = "";
         }
      }
      return sAmount;
   }
   public void setAmount(String amount) {
      if(amount!=null&&!amount.equals("")) {
         this.payment = "";
      }
      this.amount = amount;
   }

   public int getIamount() {
      int iAmount = 0;
      if(payment!=null&&!payment.equals("")) {
         iAmount = Integer.parseInt(payment.substring(1));
      } else if(amount!=null&&!amount.equals("")) {
         String sAmount = amount;
         sAmount = sAmount.replace('.',',');
         sAmount = sAmount.replaceAll("--","00");
         sAmount = sAmount.replaceAll("-","00");
         if(sAmount.indexOf(",")==-1) {
            sAmount += "00";
         } else {
            sAmount = sAmount.replaceAll(",","");
         }
         try {
            iAmount = Integer.parseInt(sAmount);
         } catch (Exception e) { }
      }
      return iAmount;
   }

   public String convertToString(int iAmount) {
      return "" + iAmount/100 + "," + (iAmount%100<10 ? "0" : "" ) + (iAmount%100);
   }

   public String getSamount() {
      return convertToString(getIamount());
   }

   public String getPeriod() {
      String sPeriod = null;
      if(!getPayment().equals("")) {
         sPeriod = MONTH;
      } else {
         sPeriod = payment_period;
      }
      return sPeriod;
   }

   public Date getSubscribe_date() { return subscribe_date; }
   public void setSubscribe_date(Date subscribe_date) { this.subscribe_date = subscribe_date; }

   public int getMemberid() { return memberid; }
   public void setMemberid(int memberid) { this.memberid = memberid; }

   public int getExternid() { return externid; }
   public void setExternid(int externid) { this.externid = externid; }

   public String getEmail() { return email; }
   public void setEmail(String email) { this.email = SubscribeForm.cleanEmail(email); }

   public String getSenior() { return senior; }
   public void setSenior(String senior) { this.senior = senior; }

   public String getProduct1() { return product1; }
   public void setProduct1(String product1) { this.product1 = product1; }

   public String getProduct2() { return product2; }
   public void setProduct2(String product2) { this.product2 = product2; }

   public String getProduct3() { return product3; }
   public void setProduct3(String product3) { this.product3 = product3; }

   public int getBatchnumber() { return batchnumber; }
   public void setBatchnumber(int batchnumber) { this.batchnumber = batchnumber; }

   public int getSerialnumber() { return serialnumber; }
   public void setSerialnumber(int serialnumber) { this.serialnumber = serialnumber; }

   public String getStatus() { return status; }
   public void setStatus(String status) { this.status = status; }

   public Date getDownloaddate() { return downloaddate; }
   public void setDownloaddate(Date downloaddate) { this.downloaddate = downloaddate; }

   public String getDayofbirthDate() { return dayofbirthDate; }
   public void setDayofbirthDate(String dayofbirthDate) { this.dayofbirthDate = dayofbirthDate; }

   public int getDayofbirthMonth() { return dayofbirthMonth; }
   public void setDayofbirthMonth(int dayofbirthMonth) { this.dayofbirthMonth = dayofbirthMonth; }

   public String getDayofbirthYear() { return dayofbirthYear; }
   public void setDayofbirthYear(String dayofbirthYear) { this.dayofbirthYear = dayofbirthYear; }

   public String getDayofbirth() {
      String dayOfBirth = dayofbirthYear;
      if(dayofbirthMonth<10) { dayOfBirth += "0"; }
      dayOfBirth += dayofbirthMonth;
      if(dayofbirthDate.length()==1) { dayOfBirth += "0"; }
      dayOfBirth += dayofbirthDate;
      return dayOfBirth;
   }

   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      log.info("MembershipForm.validate()");
      ActionErrors errors = new ActionErrors();

      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());

      Cloud cloud = CloudFactory.getCloud();

      if(this.getAction()==null){  // *** browser lost session ***

         errors.add("warning",new ActionError("evenementen.session.lostsession"));
         log.info("Null action in MembershipForm");

      } else if(this.getAction().equals(submitAction)||this.getAction().equals(skipValidationAction)) { // only two buttons to submit the form

         validateCounter++;

         if(this.getInitials().equals(""))  {
            errors.add("warning",new ActionError("membershipform.required.initials"));
         }

         if(this.getInitials().length()>10){
            errors.add("warning",new ActionError("membershipform.toolong.initials"));
         }

         if(this.getSuffix().length()>7){
            errors.add("warning",new ActionError("membershipform.toolong.suffix"));
         }

         if(this.getLastname().equals("")) {
            errors.add("warning",new ActionError("evenementen.required.lastname"));
         }

         if(this.getLastname().length()>25){
            errors.add("warning",new ActionError("membershipform.toolong.lastname"));
         }

         if(this.getStreet().equals("")) {
            errors.add("warning",new ActionError("evenementen.required.streetname"));
         }

         if(this.getStreet().length()>24){
            errors.add("warning",new ActionError("membershipform.toolong.street"));
         }

         if(this.getHousenumber().equals("")) {
            errors.add("warning",new ActionError("evenementen.required.housenumber"));
         } else if(this.getHousenumber().length()>6){
            errors.add("warning",new ActionError("membershipform.toolong.housenumber"));
         } else {
            
            int iHouseNumber = 0;
            try {
               iHouseNumber = Integer.parseInt(this.getHousenumber());
            } catch(Exception e) {
               errors.add("warning",new ActionError("membershipform.housenumber.nan"));
            }
            try {
               if(!this.getAction().equals(skipValidationAction)
                  && assertZipCodeMap()
                  && !CSVReader.isInRange(zipCodeMap, zipcode, iHouseNumber)) {
                  errors.add("warning",new ActionError("membershipform.housenumber.notinrange"));
               }
            } catch(Exception e) {
               errors.add("warning",new ActionError("membershipform.housenumber.notinrange"));
            }            
         }

         if(this.getHousenumber_extension().length()>6){
            errors.add("warning",new ActionError("membershipform.toolong.housenumber_extension"));
         }

         if(this.getCountry_code().equals(DEFAULT_COUNTRY)) { // only check zipcode in case of Nederland
            if(this.getZipcode().equals("")) {
               errors.add("warning",new ActionError("evenementen.required.zipcode"));
            } else if(this.getZipcode().length()>6){
               errors.add("warning",new ActionError("membershipform.toolong.zipcode"));
            } else if(!this.getAction().equals(skipValidationAction)
                      && CSVReader.getAddress(zipCodeMap, zipcode)==null) {
               errors.add("warning",new ActionError("membershipform.notvalid.zipcode"));
            }
         }

         if(this.getCity().equals("")) {
            errors.add("warning",new ActionError("evenementen.required.city"));
         }

         if(this.getCity().length()>25){
            errors.add("warning",new ActionError("membershipform.toolong.city"));
         }

         if(this.getCountry_code().equals("")) {
            errors.add("warning",new ActionError("membershipform.required.land"));
         }

         String emailMessage = SubscribeForm.getEmailMessage(this.getEmail(), "evenementen.email.required");
         if(!emailMessage.equals("")){
            errors.add("warning",new ActionError(emailMessage));
         } else if(this.getEmail().length()>60){
            errors.add("warning",new ActionError("membershipform.toolong.email"));
         }

         if((this.getDayofbirthDate().equals(""))||(this.getDayofbirthMonth()==-1)||(this.getDayofbirthYear().equals(""))){

           errors.add("warning",new ActionError("membershipform.required.dayofbirth"));

         } else { // ** now see, if all subfields of this date hold

            if(this.getDayofbirthYear().equals("")) {
               errors.add("warning",new ActionError("membershipform.required.dayofbirthyear"));
            } else {
               try {
                  int year = (new Integer(this.getDayofbirthYear())).intValue();
                  if(year<100) {
                     year += 1900;
                     setDayofbirthYear("" + year);
                  }
                  if(year<cal.get(Calendar.YEAR)-100) {
                     errors.add("warning",new ActionError("membershipform.dayofbirthyear.lessthancentury"));
                  } else if(this.getDayofbirthMonth()==-1){
                        errors.add("warning",new ActionError("membershipform.required.dayofbirthmonth"));
                  } else if(this.getDayofbirthDate().equals("")) {
                        errors.add("warning",new ActionError("membershipform.required.dayofbirthdate"));
                  } else {
                     try {
                        int day = (new Integer(this.getDayofbirthDate())).intValue();
                        cal.set(year,this.getDayofbirthMonth(),1);
                        if(day<cal.getActualMinimum(cal.DAY_OF_MONTH)) {
                           errors.add("warning",new ActionError("membershipform.dayofbirthdate.smallerthanmin"));
                        } else if(day>cal.getActualMaximum(cal.DAY_OF_MONTH)) {
                           errors.add("warning",new ActionError("membershipform.dayofbirthdate.largerthanmax"));
                        } else {
                           cal.set(year,this.getDayofbirthMonth(),day);
                           long now = (new Date()).getTime();
                           if(cal.getTime().getTime() > now ) {
                              errors.add("warning",new ActionError("membershipform.dayofbirthyear.atmosttoday"));
                           }
                        }
                     } catch(Exception e) {
                        errors.add("warning",new ActionError("membershipform.dayofbirthdate.nan"));
                     }
                  }
               } catch(Exception e) {
                  errors.add("warning",new ActionError("membershipform.dayofbirthyear.nan"));
               }
            }
         }


         if(this.getBankaccount().equals("")) {
            if(this.getPayment_type().equals(AUTHORIZE)) {
               errors.add("warning",new ActionError("membershipform.bankaccount.mandatory_on_authorization"));
            }
         } else {
            String bankAccountMessage = SubscribeForm.getBankAccountMessage(this.getBankaccount());
            if(!bankAccountMessage.equals("")) {
               errors.add("warning",new ActionError(bankAccountMessage));
            }
         }

         if(this.getPayment_type().equals(AUTHORIZE)) {
            if(this.getIamount()==0) {
               errors.add("warning",new ActionError("membershipform.required.amount"));
            }
            if(this.getPayment_period().equals(MONTH)&&this.getIamount()<MINIMUM_PER_MONTH) {
               errors.add("warning",new ActionError("membershipform.amount.month.minimum"));
            }
            if(this.getPayment_period().equals(YEAR)&&this.getIamount()<MINIMUM_PER_YEAR) {
               errors.add("warning",new ActionError("membershipform.amount.year.minimum"));
            }
            if(!this.getAmount().equals("")&&this.getPayment_period().equals("")) {
               errors.add("warning",new ActionError("membershipform.required.monthoryear"));
            }
         }

         String phoneMessage = SubscribeForm.getPhoneMessage(this.getPhone());
         if(!phoneMessage.equals("")) {  // *** check if the phonenumber is valid ***
            errors.add("warning",new ActionError(phoneMessage));
         } else if(false&&this.getPhone().equals(SubscribeForm.initPhone)){ // *** phone is not required ***
            errors.add("warning",new ActionError("membershipform.required.phone"));
         }

      }

      if(errors.size()!=0) {
         this.setAction(correctAction);
      }

      return errors;
   }

   public Node createMember(Cloud cloud) {

     Calendar cal = Calendar.getInstance();
     Date thisDate = cal.getTime();
     cal.set((new Integer(this.getDayofbirthYear())).intValue()
         ,getDayofbirthMonth()
         ,(new Integer(this.getDayofbirthDate())).intValue());
     Date birthDate = cal.getTime();


     Node thisMember = cloud.getNodeManager("members").createNode();

     thisMember.setStringValue("refererid",getReferid());
     thisMember.setStringValue("gender",getGender());
     thisMember.setStringValue("firstname","");
     thisMember.setStringValue("initials",getInitials());
     thisMember.setStringValue("suffix",getSuffix());
     thisMember.setStringValue("lastname",getLastname());
     thisMember.setStringValue("street",getStreet());
     thisMember.setStringValue("housenumber",getHousenumber());
     thisMember.setStringValue("housenumber_extension",getHousenumber_extension());
     thisMember.setStringValue("zipcode",getZipcode());
     thisMember.setStringValue("city",getCity());
     thisMember.setStringValue("country_code",getCountry_code());
     thisMember.setStringValue("organisation","");
     thisMember.setStringValue("extra_info","");
     thisMember.setStringValue("phone",(getPhone().equals(SubscribeForm.initPhone) ? "" : getPhone()));
     thisMember.setLongValue("dayofbirth",birthDate.getTime()/1000);
     thisMember.setStringValue("bankaccount",getBankaccount());
     thisMember.setIntValue("amount",getIamount());
     thisMember.setStringValue("payment_type",(getPayment_type().equals(INVOICE) ? "A" : getPeriod()));
     thisMember.setLongValue("subscribe_date",thisDate.getTime()/1000);
     thisMember.setIntValue("memberid",0);
     thisMember.setIntValue("externid",0);
     thisMember.setStringValue("email",getEmail());
     thisMember.setStringValue("digital_newsletter",(getDigital_newsletter().equals("J") ? "J" : ""));
     thisMember.setStringValue("senior","");
     thisMember.setStringValue("product1","");
     thisMember.setStringValue("product2","");
     thisMember.setStringValue("product3","");
     thisMember.setIntValue("batchnumber",5555);
     thisMember.setIntValue("serialnumber",20);

     thisMember.setStringValue("status","N");

     thisMember.commit();

     this.setNode(thisMember.getStringValue("number"));

     if(!getCountry_code().equals(DEFAULT_COUNTRY)) {
        sendSubscription(cloud, thisMember);
     }

     return thisMember;
  }

  public void sendSubscription(Cloud cloud, Node thisMember) {

      String fromEmailAddress = NatMMConfig.getFromEmailAddress();

      Node emailNode = cloud.getNodeManager("email").createNode();
      emailNode.setValue("from", fromEmailAddress);
      emailNode.setValue("subject", "Lid worden");
      emailNode.setValue("replyto", fromEmailAddress);
      emailNode.setValue("to",  NatMMConfig.getToSubscribeAddress());
      emailNode.setValue("body",
                      "<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\">"
                         + getSubscribeMessage(thisMember, "plain")
                      + "</multipart>"
                      + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                      + "<html>"
                        + getSubscribeMessage(thisMember, "html") + "</html>"
                      + "</multipart>");
      emailNode.commit();
      emailNode.getValue("mail(oneshotkeep)");
  }

  public String getSubscribeMessage(Node thisMember, String type) {
     OptionedStats stats = new OptionedStats();
     String newline = "<br/>";
     if(type.equals("plain")) { newline = "\n"; }
     String message = "E-mail verstuurd vanaf www.natuurmonumenten.nl / pagina: Lid worden" + newline + newline +
               "JA, IK WORD LID (alleen buitenlandse aanmeldingen worden per email verstuurd)" + newline +
               "--------------------------------------------------------------------------" + newline;
     message += "Dhr/Mw: " + thisMember.getStringValue("gender") + newline;
     message += "Voorletters: " + thisMember.getStringValue("initials") + newline;
     message += "Tussenvoegsel: " + thisMember.getStringValue("suffix") + newline;
     message += "Achternaam: " + thisMember.getStringValue("lastname") + newline;
     message += "Straat: " + thisMember.getStringValue("street") + newline;
     message += "Huisnummer: " + thisMember.getStringValue("housenumber") + newline;
     message += "Huisnummer toevoeging: " + thisMember.getStringValue("housenumber_extension") + newline;
     message += "Postcode: " + thisMember.getStringValue("zipcode") + newline;
     message += "Woonplaats: " + thisMember.getStringValue("city") + newline;
     message += "Land: " + thisMember.getStringValue("country_code") + newline;
     message += "Telefoon: " + thisMember.getStringValue("phone") + newline;
     message += "Geboortedatum: " + stats.dateString(thisMember.getLongValue("dayofbirth")) + newline;
     message += "Bank-/ gironummer: " + thisMember.getStringValue("bankaccount") + newline;
     message += "Bedrag: " + convertToString(thisMember.getIntValue("amount")) + newline;
     message += "Betalingswijze: " + thisMember.getStringValue("payment_type") + newline;
     message += "Inschrijvings datum: " + stats.dateString(thisMember.getLongValue("subscribe_date")) + newline;
     message += "E-mail: " + thisMember.getStringValue("email") + newline;
     message += "Maandelijkse digitale nieuwsbrief :" + thisMember.getStringValue("digital_newsletter") + newline;
     return message;
  }

  public void sendConfirmEmail(Cloud cloud, Node thisMember){

     String emailSubject = "Bevestiging aanmelding als Natuurmonumenten lid";
     String toEmailAddress = thisMember.getStringValue("email");
     Node emailNode = cloud.getNodeManager("email").createNode();
     emailNode.setValue("to", toEmailAddress);
     emailNode.setValue("from", NatMMConfig.getToSubscribeAddress());
     emailNode.setValue("subject", emailSubject);
     emailNode.setValue("replyto", NatMMConfig.getToSubscribeAddress());
     emailNode.setValue("body", "<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\">"
        + getMessage(thisMember,"plain") + "</multipart>"
        + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
        + "<html>" + getMessage(thisMember,"html") + "</html>" + "</multipart>");
     emailNode.commit();
     emailNode.getValue("mail(oneshotkeep)");
  }

  public static String getMessage(Node thisMember, String type) {
     String newline = "<br/>";
     if(type.equals("plain")) { newline = "\n"; }
     String message = "Beste";
     if (thisMember.getStringValue("gender").equals("M")){
        message += " heer";
     } else {
        message += " mevrouw";
     }
     if(!thisMember.getStringValue("suffix").equals("")) {
         message += " " + thisMember.getStringValue("suffix");
     }
     message += " " + thisMember.getStringValue("lastname") + "," + newline + newline;
     message += "Welkom als nieuw lid bij Natuurmonumenten.  " +
        "Fijn dat u de natuur in Nederland wilt steunen want de natuur kan niet zonder uw steun. " +  newline + newline +
        
        "Binnen enkele weken ontvangt u het welkomstpakket met daarin \"Het Natuurboek\", een prachtig boek, " +
        "boordevol informatie over natuur in Nederland voor jong en oud, het kwartaalmagazine Natuurbehoud en " +
        "uw lidmaatschapspas met daarop uw persoonlijke lidmaatschapsnummer."  +  newline + newline +
        
        "Om u zo snel mogelijk van dienst te zijn, geven wij u nu een tijdelijk lidmaatschapsnummer, " +
        "waarmee u direct gebruik kunt maken van alle voordelen en aanbiedingen voor leden zoals korting " + 
        "op artikelen uit onze webwinkel. Kijk voor meer voordelen op <a href='http://www.natuurmonumenten.nl/steun_ons/acties_en_voordeel_voor_leden.htm'>onze website</a>" + newline + newline; 

     message += "Uw voorlopig lidnummer: 9002162" + newline + newline;

     if (thisMember.getStringValue("payment_type").equals("A")) {
        message += "U heeft aangegeven per acceptgiro te willen betalen, " +
            " deze sturen wij u binnen enkele weken toe. ";
     } else {
        int iAmount = thisMember.getIntValue("amount");
        String payment_type = thisMember.getStringValue("payment_type");
        message += "U heeft aangegeven dat u voor minimaal een jaar lid wordt van Natuurmonumenten en dat u ons wilt machtigen om tot wederopzegging " +
           "het bedrag van " + SubscribeAction.price(iAmount) + " per " + (payment_type.equals(YEAR) ? "jaar" : "maand") +
           " van rekeningnummer " + thisMember.getStringValue("bankaccount") + " af te schrijven. ";
        if(iAmount<200&&payment_type.equals(MONTH)) {
           message += "Omdat dit bedrag kleiner is dan &euro; 2,- zal het totale bedrag " +
              "( " + SubscribeAction.price(iAmount) + " x 12 = ) " + SubscribeAction.price(iAmount*12) +
              " eenmaal per jaar worden ge&iuml;ncasseerd." + newline + newline;
        }
     }
     message += "Heeft u vragen, mail ons dan via het vragen formulier op onze website: ";
     if(type.equals("html")) {
         message += "<a href='" + NatMMConfig.getInfoUrl() + "'>" + NatMMConfig.getInfoUrl() + "</a>";
     } else {
         message +=  NatMMConfig.getInfoUrl();
     }
     message += " of bel 035-6559911. " + newline + newline;
     message +=
        "Met vriendelijke groet," + newline + newline +
        "Jan Jaap de Graeff" + newline +
        "algemeen directeur" + newline + newline;
     message += "P.S. Heeft u zich bedacht? Stuur dan binnen 3 dagen een mailtje met uw naam, postcode en huisnummer naar ";
     if(type.equals("html")) {
         message += "<a href='mailto:" + NatMMConfig.getToSubscribeAddress() + "'>" + NatMMConfig.getToSubscribeAddress() + "</a>";
     } else {
         message += NatMMConfig.getToSubscribeAddress();
     }
     message += ". Wij maken uw aanmelding dan ongedaan. Deze email moet de volgende regels bevatten:" + newline + newline;
     message += "naam: " + thisMember.getStringValue("lastname") + newline;
     message += "email: " + thisMember.getStringValue("email") + newline;
     message += "geboortedatum: " + (new OptionedStats()).dateString(thisMember.getLongValue("dayofbirth")) + newline;
     return message;
  }

  public NodeList notDownloadedMembersList(Cloud cloud) {
    return cloud.getNodeManager("members").getList("members.status = 'N' AND country_code='" +  DEFAULT_COUNTRY + "'","subscribe_date","UP");
  }

  public void generateAsciiFile(Cloud cloud, String sPresentUserName){
    log.info("MembershipForm.generateAsciiFile()");
    Calendar cal = Calendar.getInstance();

    Node nPresentUser = null;
    NodeList nlUser = cloud.getNodeManager("users").getList("account='" + sPresentUserName + "'",null,null);
    if(nlUser.size()>0) {
      nPresentUser = nlUser.getNode(0);
    } else {
      log.error("Username in generateAsciiFile could not be found");
    }

    LinkedHashMap llFields = new LinkedHashMap();
    llFields.put("refererid","4");              // 0
    llFields.put("gender","1");                 // 1
    llFields.put("firstname","10");             // 2
    llFields.put("initials","10");              // 3
    llFields.put("suffix","7");                 // 4
    llFields.put("lastname","25");              // 5
    llFields.put("street","24");                // 6
    llFields.put("housenumber","6");            // 7
    llFields.put("housenumber_extension","6");  // 8
    llFields.put("zipcode","6");                // 9
    llFields.put("city","25");                  // 10
    llFields.put("country_code","25");          // 11
    llFields.put("organisation","30");          // 12
    llFields.put("extra_info","30");            // 13
    llFields.put("phone","11");                 // 14
    llFields.put("dayofbirth","8");             // 15
    llFields.put("bankaccount","10");           // 16
    llFields.put("amount","6");                 // 17
    llFields.put("payment_type","1");           // 18
    llFields.put("subscribe_date","8");         // 19
    llFields.put("memberid","7");               // 20
    llFields.put("externid","8");               // 21
    llFields.put("email","60");                 // 22
    llFields.put("digital_newsletter","1");     // 23
    llFields.put("senior","1");                 // 24
    llFields.put("product1","1");               // 25
    llFields.put("product2","1");               // 26
    llFields.put("product3","1");               // 27
    llFields.put("batchnumber","4");            // 28
    llFields.put("serialnumber","4");           // 29

    Set set = llFields.entrySet();
    Iterator it = set.iterator();
    Map.Entry me = null;

    NodeList nl = notDownloadedMembersList(cloud);
    if (nl.size()!=0){
       OptionedStats stats = new OptionedStats();
       String title = "LW"
               + stats.dateString(cal.getTime().getTime() / 1000)
               + "_"
               + stats.timeString(cal.getTime().getTime() / 1000)
               + "_leden";
       StringBuffer sData = new StringBuffer();

       NodeManager nmAttachmentsManager = cloud.getNodeManager("events_attachments");
       Node attNode = nmAttachmentsManager.createNode();
       attNode.setValue("filename", title + ".txt");
       try { // only to set mimetype
         attNode.setValue("handle", sData.toString().getBytes("UTF-8"));
       } catch (Exception e) {
         log.error(e);
       }
       attNode.setIntValue("size", sData.toString().length());
       attNode.setStringValue("titel", title);
       attNode.setStringValue("title", title);
       attNode.commit();
       RelationManager related = cloud.getRelationManager("members", "events_attachments", "related");
       RelationManager gebruikt = cloud.getRelationManager("users", "events_attachments", "gebruikt");
       Relation user_attachment = gebruikt.createRelation(attNode, nPresentUser);
       user_attachment.commit();


       for (int j = 0; j < nl.size(); j++) {

          Node nMember = nl.getNode(j);

          nMember.setStringValue("status", "D");
          nMember.setLongValue("downloaddate", cal.getTime().getTime() / 1000);
          nMember.commit();
          Relation member_attchment = related.createRelation(attNode, nMember);
          member_attchment.commit();
          it = set.iterator();
          int i = 0;
          while (it.hasNext()) {
             me = (Map.Entry) it.next();
             if ( (i == 16) || (i == 17) || (i == 20) || (i == 21) || (i == 28) || (i == 29)) { // Int
                sData.append(
                     align(nMember.getStringValue( (String) me.getKey()),
                           Integer.parseInt( (String) me.getValue()),
                           true)
                     );

             } else if ( (i == 15) || (i == 19)) { // Date
                sData.append(stats.dateString(nMember.getLongValue( (String) me.getKey())));
             } else if (i==11) { // write spaces instead of country_code
                sData.append(
                   align("",
                          Integer.parseInt( (String) me.getValue()),
                          false)
                   );
             } else { // String
                sData.append(
                   align(nMember.getStringValue( (String) me.getKey()),
                          Integer.parseInt( (String) me.getValue()),
                          false)
                   );
             }
             i++;
          }

          if (j < nl.size() - 1) {
             sData.append("\r\n"); // unix has '\n' as the line separator, whereas windows has '\r\n' and mac has '\r'.
          }
       }

       try {
         attNode.setValue("handle", sData.toString().getBytes("UTF-8"));
       } catch (Exception e) {
         log.error(e);
       }
       attNode.setIntValue("size", sData.toString().length());
       attNode.setStringValue("bron", new Integer(nl.size()).toString());
       attNode.commit();
    }
  }

  public static String align(String s, int length, boolean leftAlign){
     if(s.length()>length) {
         s = s.substring(0,length);
     } else {
         while (s.length()<length){
            if(leftAlign) {
               s = "0" + s;
            } else {
               s = s + " ";
            }
         }
     }
     return s;
  }

}

