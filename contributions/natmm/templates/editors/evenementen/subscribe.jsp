<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="nl.leocms.evenementen.forms.*,
   nl.leocms.evenementen.Evenement,
   nl.leocms.authorization.*,
   nl.leocms.util.DoubleDateNode,
	nl.leocms.util.tools.HtmlCleaner,
   java.util.*,
   org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<%@include file="dateutil.jsp" %>
<mm:import externid="action" jspvar="actionId" id="actionId">no</mm:import>
<% // *** showpastdatesId is also used to signal that the template is send by email *** %>
<mm:import externid="showpastdates" jspvar="showpastdatesId" id="showpastdatesId">false</mm:import>
<mm:import externid="orderby" jspvar="orderbyId" id="orderbyId">number</mm:import>
<mm:import externid="direction" jspvar="directionId" id="directionId">down</mm:import>
<% String newDirection = "up"; if(directionId.equals("up")) { newDirection = "down"; } %>

<mm:cloud jspvar="cloud" 
   method="<%= (actionId.equals("printsubscriptions")? "" : "http") %>"
   rank="<%= (actionId.equals("printsubscriptions")? "" : "basic user") %>">
<html>
<head>
<title><% if(actionId.indexOf("printdates")>-1) { 
         %>Print van data voor activiteit<% 
      } else if(actionId.indexOf("printsubscriptions")>-1) { 
         %>Print van aanmeldingen voor activiteit<% 
      } else {
         %>Aanmelden voor activiteit<%
      } %></title>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<style>
   p { margin: 0px; }
</style>
<% if(actionId.indexOf("print")==-1) { %>
<script language="javascript" src="../scripts/launchcenter.js"></script>
<script language="javascript" src="../scripts/cookies.js"></script>
<script language="javascript">
   var lastSelected = '';
   function populateInput(selectedParticipant,subscriptionNumber
      ,prefix,firstName,initials,suffix,lastName
      ,numberInCategory,participantsCategory,privatePhone,email,memberId
      ,streetName,houseNumber,city,country,paymentType,gender,zipCode,source,description,status) {
     document.forms[0].selectedParticipant.value = selectedParticipant;
     document.forms[0].subscriptionNumber.value = subscriptionNumber;
     document.forms[0].prefix.value = prefix;
     document.forms[0].firstName.value = firstName;
     document.forms[0].initials.value = initials;
     document.forms[0].suffix.value = suffix;
     document.forms[0].lastName.value = lastName;
     document.forms[0].numberInCategory.value = numberInCategory;
     document.forms[0].participantsCategory.value = participantsCategory;
     document.forms[0].privatePhone.value = privatePhone;
     document.forms[0].email.value = email;
     document.forms[0].memberId.value = memberId;
     document.forms[0].streetName.value = streetName;
     document.forms[0].houseNumber.value = houseNumber;
     document.forms[0].city.value = city;
     document.forms[0].country.value = country;
	  document.forms[0].paymentType.value = paymentType;
	  document.forms[0].gender.value = gender;
     document.forms[0].zipCode.value = zipCode;
     document.forms[0].source.value = source;
     document.forms[0].description.value = description;
     document.forms[0].status.value = status;
     setSelected(selectedParticipant);
   }
   function setSelected(selectedParticipant) {
     if(document.getElementById('tr_'+selectedParticipant)!=null) {
        document.getElementById('tr_'+selectedParticipant).style.color = 'red';
        if(lastSelected!=''&&lastSelected!=selectedParticipant) {
            document.getElementById('tr_'+lastSelected).style.color = '';
        }
        lastSelected = selectedParticipant;
     }
   }
   function checkMaxPerGroup() {
   	if (document.forms[0].numberInCategory.value>5) {
   		alert("Groepen groter dan 5 personen kunnen gebruik maken van een excursie op aanvraag.");
   	}
   	return true;
   }  
   var cancelClick = false;
   function doDelete(prompt) {
   	var conf;
   	if (prompt && prompt!="") {
   		conf = confirm(prompt);
   	} else conf=true;
   	cancelClick=true;
   	return conf;
   }    
</script>
<script>
   var root = window.addEventListener || window.attachEvent ? window : document.addEventListener ? document : null;
   var WIN_CLOSE_MSG = "U hebt een wijziging ingevoerd, zonder dat deze is opgeslagen in de CAD.";
   
   function warnOnEditwizardOpen() {
      if(readCookie('ew')!=null) {
         return WIN_CLOSE_MSG;
      } 
   }
   
   function ignore_modified(){
       if(readCookie('ew')==null) {
         root.onbeforeunload = null;
       }
   }
   
   function init(){
     if (typeof(root.onbeforeunload) != "undefined") { 
          root.onbeforeunload = warnOnEditwizardOpen; 
     }
     else return;
     for (var i = 0; oCurrForm = document.forms[i]; i++){	
         if (oCurrForm.addEventListener) oCurrForm.addEventListener("submit", ignore_modified, false);
         else if (oCurrForm.attachEvent) oCurrForm.attachEvent("onsubmit", ignore_modified);
     }
   }  

   if (root){
     if (root.addEventListener) root.addEventListener("load", init, false);
     else if (root.attachEvent) root.attachEvent("onload", init);
   }
</script>
<% } else { %>
<style rel="stylesheet" type="text/css">
	body,td {
	   color:#000000;
      background-color: #FFFFFF;
      font-size: 11px;
	}
	table {
   	border-style: solid;
	   border-collapse: collapse;
   }
</style>
<% } %>
</head>
<%
String account = cloud.getUser().getIdentifier();
AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud); 

boolean isAdmin = false;
boolean isChiefEditor = false;
boolean isEditor = false;
if(actionId.indexOf("printsubscriptions")==-1) {
   isAdmin = cloud.getUser().getRank().equals("administrator");
	isChiefEditor = cloud.getUser().getRank().equals("chiefeditor");
	UserRole role = authorizationHelper.getRoleForUser(authorizationHelper.getUserNode(account), cloud.getNode("natuurin_rubriek"));
   isEditor = (role.getRol()>0);
}

String dateStyle = "width:40px;text-align:right;"; 
String buttonStyle = "width:90px;";
String extButtonStyle = "width:150px;";
String fNStyle = "width:100px;padding-bottom:5px;vertical-align:top;";
String paymentCondition = "";

int iMinNumber = 0;
int iMaxNumber = 9999;
int curAantalDeelnemers = 0;

String sHighLight = "style=\"background-color:#729DC2;\"";
%>
<bean:define id="nodenr" property="node" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="parent_number" property="parent" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="selectedParticipant"  property="selectedParticipant" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="validateCounter" property="validateCounter" name="SubscribeForm" scope="session" type="java.lang.Integer"/>
<!-- ******************************* FIND PARENT AND SUBSCRIPTIONS ******************************** -->
<body style="overflow:auto;" id="body" onload="<%
   if(actionId.indexOf("print")==-1) { 
       if(!selectedParticipant.equals("")) { 
         %>javascript:setSelected(<%= selectedParticipant %>);<%
       }
       if(validateCounter.intValue()>0) { 
         %>saveCookie('ew','on',1);<%
       } else {
         %>deleteCookie('ew');<%
       }
       %>window.location='#form';<%
   } else { 
      %>self.print();<%
   } %>">
<html:form action="/editors/evenementen/SubscribeAction" scope="session">
<html:hidden property="ticketOffice" value="backoffice" />

<html:hidden property="ticketOfficeSource" value="CAD" />

<html:hidden property="userId" value="<%= cloud.getUser().getIdentifier() %>" />
<html:hidden property="selectedParticipant" />
<html:hidden property="subscriptionNumber" />
<html:hidden property="bankaccount" value="" />
<%
boolean isGroupExcursion = Evenement.isGroupExcursion(cloud,parent_number);
boolean addressIsRequired = false;
String sReferrer = "/editors/evenementen/SubscribeInitAction.eb?number=" + nodenr;
TreeMap subscriptions = new TreeMap();
int iTotalParticipants = 0;
DoubleDateNode ddn = new DoubleDateNode(); 
%><mm:node number="<%= nodenr %>" jspvar="thisEvent"><%
   ddn.setBegin(new Date(thisEvent.getLongValue("begindatum")*1000));
   ddn.setEnd(new Date(thisEvent.getLongValue("einddatum")*1000));  
%></mm:node
><mm:list nodes="<%= nodenr %>" path="evenement,posrel,inschrijvingen"
   ><mm:field name="inschrijvingen.number" jspvar="subscription_number" vartype="String" write="false"><%
   // *** find the key for ordering ***
   String key = subscription_number;
   if(orderbyId.equals("lastname")) {
     %><mm:list nodes="<%= subscription_number %>" path="inschrijvingen,posrel,deelnemers" max="1" orderby="deelnemers.number" directions="UP"
         ><mm:field name="deelnemers.lastname" jspvar="lastname" vartype="String" write="false"><%
         key = lastname;
         %></mm:field
     ></mm:list><%
   } 
   String tmpKey = key;
   int i =0;
   while(subscriptions.containsKey(key)) { key = tmpKey + i; i++; }
    
   subscriptions.put(key,subscription_number);
   // *** update the costs for the group excursion booking ***
   if(actionId.indexOf("print")==-1 && isGroupExcursion) { Evenement.updateGroupExcursionCosts(cloud, parent_number, subscription_number); }
   %></mm:field
></mm:list>
<!-- ******************************* description of evenement (from parent) ******************************** -->
<mm:node number="<%= parent_number %>" jspvar="parentEvent">
   <%
   try { iMinNumber = parentEvent.getIntValue("min_aantal_deelnemers"); } catch (Exception e) { }
   if(iMinNumber==-1) iMinNumber = 0;
   try { iMaxNumber = parentEvent.getIntValue("max_aantal_deelnemers"); } catch (Exception e) { }
   if(iMaxNumber==-1) iMaxNumber = 9999;
   try { curAantalDeelnemers = parentEvent.getIntValue("cur_aantal_deelnemers"); } catch (Exception e) { }
   if(curAantalDeelnemers==-1) curAantalDeelnemers = 0;

   addressIsRequired = parentEvent.getStringValue("adres_verplicht").equals("1");

   boolean isExtendedAct = false;
   
   if(actionId.indexOf("print")==-1) { 
      %><html:image src="../img/left.gif" property="buttons.goBack" style="width:13px;" alt="Naar overzicht" />
      <a href="SubscribeInitAction.eb?number=<%= nodenr %>&action=printsubscriptions&orderby=lastname&direction=up" target="_blank">
         <img src='../img/print_subscriptions.gif' align='absmiddle' border='0' alt='Print de aanmeldingen voor deze activiteit'></a>
      <a href="#" onClick="javascript:launchCenter('mailsubscriptions.jsp?event=<%= nodenr %>', 'mail', 420, 520);setTimeout('newwin.focus();',250);">
         <img src='../img/mail_subscriptions.gif' align='absmiddle' border='0' alt='Verstuur de aanmeldingen voor deze activiteit per email'></a>
   	<a href="#" onClick="javascript:launchCenter('download_popup.jsp?event=<%= nodenr %>&type=s', 'center', 300, 400,'resizable=1');setTimeout('newwin.focus();',250);">
			<img src='../img/excel_subscriptions.gif' align='absmiddle' border='0' alt='Download de aanmeldingen voor deze activiteit'></a><%
   } %>
   <h1 style="margin-top:0px;"><% 
      if(actionId.indexOf("printdates")>-1) { 
         %>Overzicht data <%
      } else if(actionId.indexOf("printsubscriptions")>-1) { 
         %>Overzicht aanmeldingen <%
      } %><mm:field name="titel" /></h1>
   <table class="formcontent" style="width:500px;margin-bottom:20px;" <% if(actionId.indexOf("print")>-1) { %>border="1"<% } %>>
      <% 
      if(actionId.indexOf("printdates")==-1) { 
         %>
         <tr><td class="fieldname" style="<%= fNStyle %>">datum</td><td><%= ddn.getReadableDate() %></td></tr>
         <tr><td class="fieldname" style="<%= fNStyle %>">tijd</td><td><%= ddn.getReadableTime() %></td></tr>
         <% 
      }
      String [] altEvent = Evenement.altEventLink(cloud,parent_number,nodenr);
      if(actionId.indexOf("print")==-1 && !altEvent[0].equals("-1")) {
         %>
         <tr>
            <td class="fieldname" style="<%= fNStyle %>">alternatief</td>
            <td>
               <mm:node number="<%= altEvent[0] %>"><mm:field name="titel" /></mm:node>
               <jsp:include page="<%= altEvent[1] %>" />
            </td>
         </tr>
         <%
      } %>
      <tr><td class="fieldname" style="<%= fNStyle %>">type</td>
         <td><mm:relatednodes type="evenement_type"
            ><mm:first inverse="true">, </mm:first
            ><mm:field name="naam" jspvar="evenementTypeName" vartype="String"><%
               isExtendedAct = isExtendedAct || evenementTypeName.equals("Varen");
            %></mm:field
         ></mm:relatednodes>
         </td></tr>
      <tr><td class="fieldname" style="<%= fNStyle %>">natuurgebied</td>
          <td><mm:relatednodes type="natuurgebieden"><mm:first inverse="true">, </mm:first><mm:field name="naam" /></mm:relatednodes></td></tr>   
      <tr><td class="fieldname" style="<%= fNStyle %>">vertrekpunt</td>
          <td>
            <script>
             function toggle() {
             if( document.getElementById("toggle_div").style.display=='none' ){
               document.getElementById("toggle_div").style.display = '';
               document.getElementById("toggle_image").src = "../img/tmin.gif";
             } else {
               document.getElementById("toggle_div").style.display = 'none';
               document.getElementById("toggle_image").src = "../img/tplus.gif";
             }
            }
            </script>
            <mm:relatednodes type="vertrekpunten">
               <mm:first inverse="true"><br/><br/></mm:first>
               <%
               if(actionId.indexOf("print")!=-1) { 
                  %><b><mm:field name="titel" /></b><br/><mm:field name="tekst" /><% 
               } else { 
                  %>
                  <span onClick="toggle();"><b><mm:field name="titel" /></b><img src="../img/tplus.gif" border="0" align="absmiddle" id="toggle_image" /></span><br/>
                  <div id="toggle_div" style="display='none'"><mm:field name="tekst" /></div>
                  <%
               } %>
            </mm:relatednodes></td></tr>
      <tr><td class="fieldname" style="<%= fNStyle %>">max. aantal deelnemers</td><td><%= iMaxNumber %></td></tr>
      <tr><td class="fieldname" style="<%= fNStyle %>">min. aantal deelnemers</td><td><%= iMinNumber %></td></tr>
      <tr><td class="fieldname" style="<%= fNStyle %>">betrokken personen</td>
          <td><%
            String source = parent_number;
            if(isEditor && isGroupExcursion) {
               %>
               <a href="<mm:url page="<%= editwizard_location %>"/>/jsp/wizard.jsp?wizard=config/evenement/evenement_medewerker&nodepath=evenement&objectnumber=<%= nodenr %>&referrer=<%= sReferrer %>&language=nl">
                  <img src='../img/edit_w.gif' align='absmiddle' border='0' alt='Bewerk betrokken personen'>
               </a>
               <%
               source = nodenr;
            } %>
            <mm:list nodes="<%= source %>" path="evenement,readmore,medewerkers" orderby="medewerkers.lastname">
               <mm:first><table class="formcontent"></mm:first>
               <tr><td style="width:130px;">
                     <mm:field name="readmore.readmore">
                        <mm:compare value="1">aanspreekpunt</mm:compare>
                        <mm:compare value="2">excursieleider</mm:compare>
                        <mm:compare value="99">overig</mm:compare>
                     </mm:field></td>
                   <td><mm:field name="medewerkers.titel" 
                     /><mm:field name="medewerkers.companyphone"><mm:isnotempty>, <nobr><mm:write /></nobr></mm:isnotempty></mm:field
                     ><mm:field name="medewerkers.email"><mm:isnotempty>, <nobr><a href="mailto:<mm:write />"><mm:write /></a></nobr></mm:isnotempty></mm:field>
                   </td>
               </tr>
               <mm:last></table></mm:last>
            </mm:list>
            </td></tr>
      <%
      if(isGroupExcursion) {
      %>
      <tr><td class="fieldname" style="<%= fNStyle %>">behandeld door</td>
          <td>
          <mm:list nodes="<%= nodenr %>" path="evenement,posrel,inschrijvingen,schrijver,users" fields="users.number" distinct="true">
               <mm:node element="users">
               <mm:first><table class="formcontent"></mm:first>
               <tr>
                  <td><mm:field name="voornaam" /> <mm:field name="achternaam" 
                      /><mm:field name="emailadres"><mm:isnotempty>, <nobr><a href="mailto:<mm:write />"><mm:write /></a></nobr></mm:isnotempty></mm:field>
                   </td>
               </tr>
               <mm:last></table></mm:last>
               </mm:node>
            </mm:list>
            </td></tr>
      <%
      }
      %>
      <tr><td class="fieldname" style="<%= fNStyle %>">betrokken afdelingen</td>
          <td><mm:related path="readmore,afdelingen" orderby="readmore.readmore">
               <mm:first><table class="formcontent"></mm:first>
               <tr><td style="width:130px;">
                     <mm:field name="readmore.readmore">
                        <mm:compare value="1">organisator</mm:compare>
                        <mm:compare value="2">boekende afdeling</mm:compare>
                        <mm:compare value="99">overig</mm:compare>
                     </mm:field></td>
                   <td><mm:field name="afdelingen.naam" 
                     /><mm:field name="afdelingen.telefoonnummer"><mm:isnotempty>, <nobr><mm:write /></nobr></mm:isnotempty></mm:field>
                   </td>
               </tr>
               <mm:last></table></mm:last>
             </mm:related></td></tr>
      <mm:field name="tekst"><mm:isnotempty
            ><tr><td class="fieldname" style="<%= fNStyle %>">bijzonderheden</td><td><mm:write /></td></tr>
      </mm:isnotempty></mm:field><%
      if(actionId.indexOf("print")==-1) { 
         %><mm:field name="omschrijving"><mm:isnotempty
            ><tr><td class="fieldname" style="<%= fNStyle %>">interne notitie</td><td style="color:red;"><mm:write /></td></tr>
         </mm:isnotempty></mm:field><%
      } %>
      <tr><td class="fieldname" style="<%= fNStyle %>">extra info</td>
          <td><mm:related path="readmore,extra_info" orderby="extra_info.omschrijving">
                  <mm:field name="extra_info.omschrijving" /><br>
               </mm:related></td></tr>
   </table>
   <% // *** info on number of subscriptions per deelnemers_categorie *** 
      // use relation parent_event -> deelnemers_categorie -> deelnemers -> inschrijvingen -> this_event
   %>
   <table class="formcontent" <% if(actionId.indexOf("print")>-1) { %>border="1"<% } 
      %> style="width:<% if(isExtendedAct) {  %>500px<% } else { %>300px<% } %>;margin-bottom:20px;">
   <tr <%= sHighLight %>><td>categorie&nbsp;&nbsp;</td><td>kosten&nbsp;&nbsp;</td>
         <% if(isExtendedAct) {  %>
         	<% if(actionId.indexOf("printdates")==-1) { 
                %><td>deelnemers&nbsp;&nbsp;</td><% } %>
            <td>aantal&nbsp;plaatsen</td>
         <% } %>
         <% if(actionId.indexOf("printdates")==-1) { 
             %><td>aanmeldingen</td><% } %>
         </tr>
   <mm:related path="posrel,deelnemers_categorie" orderby="deelnemers_categorie.naam">
      <mm:field name="posrel.pos" jspvar="costs" vartype="String" write="false">
      <mm:node element="deelnemers_categorie">
         <mm:field name="number" jspvar="thisCategory" vartype="String" write="false">
         <mm:field name="groepsactiviteit" jspvar="isGroupEvent" vartype="String" write="false">
         <% 
         if(!isGroupExcursion||isGroupEvent.equals("1")) {
            int iNumberPerParticipant = 1;
            int iParticipantsInCat = 0; 
            %>
            <mm:field name="aantal_per_deelnemer" jspvar="dummy" vartype="Integer" write="false">
               <% try { iNumberPerParticipant = dummy.intValue(); } catch (Exception e) {} %>
            </mm:field>
            <mm:related path="related,deelnemers,posrel,inschrijvingen,posrel,evenement"
               constraints="<%= "evenement.number = '" + nodenr + "'" %>">
               <mm:field name="deelnemers.bron" jspvar="dummy" vartype="Integer" write="false">
                  <% try { iParticipantsInCat += dummy.intValue(); } catch (Exception e) {}  %>
               </mm:field>
            </mm:related>
            <% iTotalParticipants += iParticipantsInCat*iNumberPerParticipant; %>
            <tr <mm:even><%= sHighLight %></mm:even>><td style="width:130px;"><mm:field name="naam" /></td>
                  <% // kosten %>
               <td><%= SubscribeAction.priceFormating(costs) %></td>
               <% 
               if(isExtendedAct) {
                  if(actionId.indexOf("printdates")==-1) { 
                	  // deelnemers
                     %><td style="text-align:center;"><%= iParticipantsInCat %></td><% 
                  } 
                  // aantal plaatsen %>
                  <td style="text-align:center;"><%= iNumberPerParticipant %></td>
                  <% 
               }
               if(actionId.indexOf("printdates")==-1) { 
            	   // aanmeldingen
                     %><td style="text-align:center;"><%= iParticipantsInCat*iNumberPerParticipant %></td><%
               } %>
            </tr>
            <%
         } %>
         </mm:field>
         </mm:field>
      </mm:node>
      </mm:field>
   </mm:related>
   <% 
   // inschrijving related to this_event that do not have a deelnemers_categorie
   // it is meant no deelnemers_categorie attached to the deelnemer
   // this can be reproduced by deleting the deelnemers_categorie relation attached to the deelnemer, or the whole deelnemers_categorie
   if(!isGroupExcursion) {
      int iParticipantsWithoutCat = 0;
         
      %><mm:list nodes="<%= nodenr %>" path="evenement,posrel,inschrijvingen,posrel,deelnemers"
         ><mm:remove referid="has_deelnemers_categorie"
         /><mm:node element="deelnemers"
            ><mm:related path="related,deelnemers_categorie"
               ><mm:import id="has_deelnemers_categorie"
            /></mm:related
         ></mm:node
         ><mm:notpresent referid="has_deelnemers_categorie"
            ><mm:field name="deelnemers.bron" jspvar="dummy" vartype="Integer" write="false">
               <% try { iParticipantsWithoutCat += dummy.intValue(); 
                  } catch (Exception e) { iParticipantsWithoutCat++; } %>
            </mm:field
         ></mm:notpresent
      ></mm:list><%
      if(iParticipantsWithoutCat>0) { 
         %>
         <tr><td style="width:130px;">Zonder categorie</td>
         	<% // kosten %>
            <td><%=  SubscribeAction.priceFormating("-1") %></td>
            <% if(isExtendedAct) {  %>
               <% if(actionId.indexOf("printdates")==-1) { 
            	   // deelnemers
                  %><td style="text-align:center;"><%= iParticipantsWithoutCat %></td><% } %>
                  <% // aantal plaatsen %>
                  <td style="text-align:center;"></td>
            <% } %>
            <% if(actionId.indexOf("printdates")==-1) { 
            	// aanmeldingen
               %><td style="text-align:center;"><%= iParticipantsWithoutCat %></td><% } %>      
         </tr>
         <%
         iTotalParticipants += iParticipantsWithoutCat;
      }
   }
   if(actionId.indexOf("printdates")==-1) { 
     %><tr>
         <td>TOTAAL</td>
         <td></td>
         <% if(isExtendedAct) {  %>
         	<% // deelnemers %>
         	<td></td>
         	<% // aantal plaatsen %>
         	<td></td>
         <% } %>
         <td style="text-align:center;"><%= iTotalParticipants  %></td>
      </tr><%
   } %>
   </table>
</mm:node>

<!-- ******************************* table header of table with subscriptions ******************************** -->
<% if(actionId.indexOf("printdates")==-1) { %>
<a name="form"></a>
<mm:node number="<%= nodenr %>" jspvar="thisEvent"><%
   if(isEditor && actionId.indexOf("print")==-1) { 
      %><html:submit property="action" value="<%= SubscribeForm.NEW_SUBSCRIPTION_ACTION %>" style="width:130px;" /><% 
   } %>
   <b><mm:field name="titel" />, <%= ddn.getReadableDate() %>, <%= ddn.getReadableTime() %></b> |
   <% // *** details on subscriptions *** %>
   <% 
   if(iTotalParticipants<iMaxNumber) { 
     %>er zijn nog <%= iMaxNumber - iTotalParticipants %> plaatsen open voor deze activiteit<% 
   }
   if(iTotalParticipants<iMinNumber) {
     %><span class="notvalid" style="font-weight:normal"> | Let op: Als minimum aantal deelnemers niet wordt bereikt, gaat de excursie niet door. De aangemelde deelnemers worden gebeld.</span><%
   }
   String altText = "";
   String ticketIcon = "";
   %><mm:node number="<%= parent_number %>" jspvar="parentEvent"><%@include file="event_status.jsp" %></mm:node><%
   if(!altText.equals("")) { %><span class="notvalid" style="font-weight:normal"> | <%= altText %></span><% } %>
   <span class="notvalid"><html:errors bundle="LEOCMS" property="warning"/></span><br>
   <table class="formcontent" cellpadding="0" cellspacing="0" <% if(actionId.indexOf("print")>-1) { %>border="1"<% } %>>
   <tr <%= sHighLight %>>
      <td><nobr><%
         if(actionId.indexOf("print")==-1) { 
            %><a href="subscribe.jsp?orderby=number&direction=<% if(orderbyId.equals("number")) { %><%=newDirection%><% } else { %><%=directionId%><% } %>">
            nummer
            <% if(orderbyId.equals("number")) { %><img src="../img/<%= newDirection %>.gif" border='0'  align='absmiddle' alt='Keer sorteervolgorde om' /><% } %></a><%
         } else {
            %>nummer<%
         } %>&nbsp;</nobr>
      </td>
		<td>m/v</td>
      <td>voornaam&nbsp;</td>
      <td>voorl.&nbsp;</td>
      <td>tussenv.&nbsp;</td>
      <td><%
         if(actionId.indexOf("print")==-1) { 
            %><a href="subscribe.jsp?orderby=lastname&direction=<% if(orderbyId.equals("lastname")) { %><%=newDirection%><% } else { %><%=directionId%><% } %>" style="font-weight:bold;">
            achternaam
            <% if(orderbyId.equals("lastname")) { %><img src="../img/<%= newDirection %>.gif" border='0'  align='absmiddle' alt='Keer sorteervolgorde om' /><% } %></a><%
         } else {
            %>achternaam<%
         } %>&nbsp;
      </td>
      <td style="font-weight:bold;">aantal&nbsp;</td>
      <td colspan="2" style="font-weight:bold;">categorie&nbsp;</td>
      <td>kosten&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
      <td style="font-weight:bold;">telefoon&nbsp;</td>
      <td style="font-weight:bold;">email&nbsp;</td>
      <td>lidnummer&nbsp;</td>
      <td style="font-weight:bold;">postcode&nbsp;</td>
      <td>bron&nbsp;</td>
      <td><% 
         if(!isGroupExcursion) { 
            %>bijzonderheden&nbsp;<% 
         } else {
            %>naam&nbsp;vd&nbsp;groep&nbsp;<%
         } %></td>
      <td colspan="2">status&nbsp;</td>
   </tr>
	<% 
   if(isEditor && actionId.indexOf("print")==-1) {
      %>
      <tr>
         <td></td>
   		<td>
   		   <html:select property="gender">
   				<html:option value="male">Dhr</html:option>
   				<html:option value="female">Mevr</html:option>
   			</html:select>
   		</td>
         <td class="inputfield"><html:text property="firstName" style="width:50px;" tabindex="1" /></td>
         <td><html:text property="initials" style="width:25px;" tabindex="2" /></td>
         <td><html:text property="suffix" style="width:45px;" tabindex="3"/></td>
         <td><html:text property="lastName" style="width:80px;" tabindex="4"/></td>
         <td><html:text property="numberInCategory" style="width:20px;" tabindex="5"/></td>
         <td colspan="2"><html:select property="participantsCategory">
               <html:option value="-1">Selecteer</html:option>
               <mm:list nodes="<%= parent_number %>" path="evenement,posrel,deelnemers_categorie" orderby="deelnemers_categorie.naam"
                  constraints="deelnemers_categorie.groepsactiviteit=='0'">
                  <mm:field name="deelnemers_categorie.number" jspvar="dc_number" vartype="String" write="false">
                     <html:option value="<%= dc_number %>"><mm:field name="deelnemers_categorie.naam" /></html:option>
                  </mm:field>
               </mm:list>
            </html:select>
         </td>
         <td><html:image src="../img/add.gif" style="width:13px;" property="buttons.addParticipant"
               onclick="<%= ((!isGroupExcursion ? "checkMaxPerGroup()": "") + ";deleteCookie('ew')") %>" alt="Voeg toe aan de geselecteerde inschrijving" /></td>
         <td>
            <bean:define id="phoneOnClickEvent" property="phoneOnClickEvent" name="SubscribeForm" scope="session" type="java.lang.String"/>
            <html:text property="privatePhone" style="width:80px;" tabindex="7" maxlength="11" onclick="<%= phoneOnClickEvent %>" />
         </td>
         <td><html:text property="email" style="width:100px;" tabindex="8"/></td>
         <td>
            <script>
               var newwin;
               function memberIdPopup() {
                  var href = "findmemberid.jsp?";
                  var answer = document.forms[0].lastName.value;
                  if(answer != '') { href += "lastname=" + answer + "&"; }
                  var answer = document.forms[0].zipCode.value;
                  if(answer != '') { href += "zipcode=" + answer + "&"; }
                  var answer = document.forms[0].houseNumber.value;
                  if(answer != '') { href += "housenumber=" + answer + "&"; }
                  newwin = window.open(href,'memberidfinder','width=400,height=400,scrollbars=no,toolbar=no,location=no');
                  return false;
               }
            </script>
            <a href="#form" onClick="javascript:memberIdPopup();setTimeout('newwin.focus();',250);"><img src="../img/qmark.gif" border='0'  align='absmiddle' alt='Zoek lidnummer op basis van achternaam, postcode en huisnummer' 
                     /></a><html:text property="memberId" style="width:55px;" tabindex="9"/>
         </td>
         <td><html:text property="zipCode" style="width:50px;" tabindex="10" maxlength="6" /></td>
         <td>
            <html:select property="source" tabindex="11">
               <html:option value=""></html:option>
               <mm:listnodes type="media" orderby="naam" directions="UP">
                  <mm:field name="naam" jspvar="mediaNaam" vartype="String" write="false">
                     <html:option value="<%= mediaNaam %>"><%= mediaNaam %></html:option>
                  </mm:field>
               </mm:listnodes>
            </html:select>
         </td>
         <td><% 
            if(!isGroupExcursion) {
                %><html:text property="description" style="width:80px;" tabindex="12"/>
                  <html:hidden property="prefix" /><% 
            } else {
                %><html:text property="prefix" style="width:80px;" tabindex="12"/>
                  <html:hidden property="description" /><% 
            } %></td>
         <td colspan="2">
            <html:select property="status">
               <% // *** skip first = website aanmelding *** %>
               <mm:listnodes type="inschrijvings_status" orderby="omschrijving" offset="1">
                  <mm:field name="number" jspvar="inschrijvingsStatus" vartype="String" write="false">
                     <html:option value="<%= inschrijvingsStatus %>"><mm:field name="naam" /></html:option>
                  </mm:field>
               </mm:listnodes>
            </html:select>
         </td>
      </tr>
      <logic:equal name="SubscribeForm" property="showAddress" value="true">
      <tr>
         <td colspan="2"></td>
         <td colspan="16" >
            <% if(addressIsRequired) { %><span style="font-weight:bold;"><% } %>
            Straat:&nbsp;<html:text property="streetName"style="width:150px;" tabindex="13" />&nbsp;&nbsp;
            Huisnummer:&nbsp;<html:text property="houseNumber" style="width:35px;" tabindex="14" />&nbsp;&nbsp;
            Plaats:&nbsp;<html:text property="city" style="width:100px;" tabindex="15"/>&nbsp;&nbsp;
            <% if(addressIsRequired) { %></span><% } %>
            Land:&nbsp;<html:text property="country" style="width:100px;" tabindex="16"/>&nbsp;&nbsp;
   			Betalingswijze:&nbsp;<html:select property="paymentType" tabindex="17">
   			<mm:listnodes type="payment_type" orderby="naam">
   				<mm:field name="naam" jspvar="sPaymentName" vartype="String" write="false">
   				   <html:option value="<%= sPaymentName %>"><%= sPaymentName %></html:option>
   				</mm:field>
   			</mm:listnodes>
   			</html:select>
   		</td>
      </tr>
      </logic:equal>
      <logic:equal name="SubscribeForm" property="showAddress" value="false">
         <html:hidden property="streetName" />
         <html:hidden property="houseNumber" />
         <html:hidden property="city" />
         <html:hidden property="country" />
   		<html:hidden property="paymentType"/>
      </logic:equal>
      <tr>
         <td colspan="13" style="text-align:right;">
            <% 
            if(validateCounter.intValue()>0) { 
               %>
               Negeer controle op postcode en telefoonnummer
               <html:radio property="skipValidation" style="width:14px;" value="Y" /> ja
               <html:radio property="skipValidation" style="width:14px;" value="N" /> nee
               <script>
						saveCookie('ew','on',1);
					</script>
               <% 
            }
            %>
         </td>
         <td colspan="5">
            <nobr>
               <html:submit property="action" value="<%= SubscribeForm.SUBSCRIBE_ACTION %>" style="<%= buttonStyle %>" onclick="<%= ((!isGroupExcursion ? "checkMaxPerGroup()": "") + ";deleteCookie('ew')") %>" />
               <html:submit property="action" value="<%= SubscribeForm.CHANGE_ACTION %>" style="<%= buttonStyle %>" onclick="<%= ((!isGroupExcursion ? "checkMaxPerGroup()": "") + ";deleteCookie('ew')")%>" />
               
            </nobr>
         </td>
      </tr>
      <% 
   } %>
   <!-- ******************************* table body of table with subscriptions ******************************** -->
   <% 
   int row = 0; 
   while(subscriptions.size()>0) {
      
      String thisSubscription = "";
      if(directionId.equals("up")){ 
         thisSubscription =(String) subscriptions.firstKey();
      } else {
         thisSubscription =(String) subscriptions.lastKey();
      }
      boolean bIsActive = true;
      String extraInfo = "";
      %><mm:node number="<%= (String) subscriptions.get(thisSubscription) %>"
         ><mm:remove referid="snumber" /><mm:field name="number" id="snumber" jspvar="snumber" vartype="String" write="false"
         ><mm:remove referid="sdescription" /><mm:field name="description" id="sdescription" jspvar="sdescription" vartype="String" write="false"
         ><mm:remove referid="ssource" /><mm:field name="source" id="ssource" write="false" 
         /><mm:remove referid="sticket_office" /><mm:field name="ticket_office" id="sticket_office" write="false" 
         />
         <% int thisParticipantsCosts = 0;
            int totalCosts = 0; 
         %>
         <mm:related path="posrel,deelnemers" orderby="deelnemers.number" directions="UP">
         <mm:node element="deelnemers" jspvar="thisParticipant">
         <mm:field name="number" jspvar="dnumber" vartype="String" write="false">
         <tr <% row++; if(row%2==1){ %><%= sHighLight %><% } %> id="tr_<mm:field name="number" />">
            <mm:first>   
               <td><%
                     if(actionId.indexOf("print")==-1) { 
                        %><a href="#form" onclick="<%@include file="populateform.jsp" %>">
                           <mm:write referid="snumber" />
                        </a><%
                     } else { 
                        %><mm:write referid="snumber" /><%
                     } %>
               </td>
               <td colspan="5">
                  <mm:compare referid="sticket_office" value="website"
                     ><img src="../img/preview.gif" border='0'  align='absmiddle' alt='Aangemeld via de website' />
                  </mm:compare
                  ><mm:remove referid="hascomma" 
                  /><mm:field name="gender" jspvar="sGender" vartype="String" write="false"
						><% if (sGender.equals("female")) { %>Mevr <% } else if (sGender.equals("male")){ %>Dhr <% } 
						%></mm:field><mm:field name="lastname" 
                  /><mm:field name="initials"><mm:isnotempty>, <mm:write/><mm:import id="hascomma" /></mm:isnotempty></mm:field
                  ><mm:field name="suffix"><mm:isnotempty><mm:notpresent referid="hascomma">,</mm:notpresent> <mm:write/></mm:isnotempty></mm:field
                  ><mm:field name="firstname"><mm:isnotempty>, <mm:write/></mm:isnotempty></mm:field>
                  <% boolean showAddress = false; %>
                  <logic:equal name="SubscribeForm" property="showAddress" value="true">
                     <% showAddress = true; %>
                  </logic:equal>
                  <mm:field name="straatnaam"><mm:isnotempty><% showAddress = true; %></mm:isnotempty></mm:field>
                  <mm:field name="huisnummer"><mm:isnotempty><% showAddress = true; %></mm:isnotempty></mm:field>
                  <mm:field name="plaatsnaam"><mm:isnotempty><% showAddress = true; %></mm:isnotempty></mm:field>
                  <mm:field name="land"><mm:isnotempty><% showAddress = true; %></mm:isnotempty></mm:field>
                  <% if(actionId.indexOf("print")>-1) { showAddress = true; } 
                  if(showAddress) { %>
                     <mm:remove referid="isfirst" 
                     /><mm:field name="straatnaam"><mm:isnotempty><br><mm:write/><mm:import id="isfirst" /></mm:isnotempty></mm:field
                     ><mm:field name="huisnummer"
                        ><mm:isnotempty><mm:notpresent referid="isfirst"><br><mm:import id="isfirst" /></mm:notpresent> <mm:write/></mm:isnotempty
                     ></mm:field
                     ><mm:field name="plaatsnaam"
                        ><mm:isnotempty><mm:notpresent referid="isfirst"><br><mm:import id="isfirst" /></mm:notpresent>, <mm:write/></mm:isnotempty
                     ></mm:field
                     ><mm:field name="land"
                        ><mm:isnotempty><mm:notpresent referid="isfirst"><br><mm:import id="isfirst" /></mm:notpresent>, <mm:write/></mm:isnotempty
                     ></mm:field>
                   <% } %>
                  </td>
            </mm:first>
            <mm:first inverse="true">
               <td><%
                     if(actionId.indexOf("print")==-1) {
                     %><a href="#form" onclick="<%@include file="populateform.jsp" %>">
                        &nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;
                     </a><% 
                     } else {
                        %>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;<%
                     } %>
               </td>
               <td colspan="5"></td>
            </mm:first>
            <td style="text-align:right;">
               <% int iPartsCtr = thisParticipant.getIntValue("bron"); 
                %><%= iPartsCtr %>
            </td>
            <td>
               <mm:relatednodes type="deelnemers_categorie"><mm:field name="naam" /></mm:relatednodes>
            </td>
            <td style="text-align:right;">
               <mm:first inverse="true">
                  <% if(actionId.indexOf("print")==-1) {
                     %><html:image src="../img/delete.gif" style="width:16px;" property="buttons.deleteParticipant" alt="Verwijder deelnemers" onclick="<%= "document.forms[0].selectedParticipant.value='" + dnumber + "';return doDelete('Weet u zeker dat u deze deelnemers wilt verwijderen?');" %>"  /><%
                     } %>
               </mm:first>
            </td>
            <td style="text-align:right;padding-right:5px;"><nobr>
               <mm:related path="posrel,inschrijvingen"
                  ><mm:field name="inschrijvingen.betaalwijze" jspvar="sPaymentType" vartype="String" write="false"><%
                     if (sPaymentType==null||sPaymentType.equals("Contant")) {
							   %><mm:field name="posrel.pos" jspvar="thisCosts" vartype="String" write="false"
                           ><%= SubscribeAction.priceFormating(thisCosts,isGroupExcursion) %><%
                              totalCosts += Integer.parseInt(thisCosts);
                        %></mm:field><% 
                     } else { 
						     %><%= (sPaymentType.length()>8) ? sPaymentType.substring(0,8) : sPaymentType %> (*)<% 
                        paymentCondition = "(*) Dit aanbod is geldig voor twee personen (voor extra deelnemers betaalt u de ledenprijs)";
                        totalCosts = -1;
                     } %></mm:field
               ></mm:related>
               </nobr>
            </td>
            <mm:first>
               <td><mm:field name="privatephone" /></td>
               <td><mm:field name="email" /></td>
               <td><mm:field name="lidnummer" /></td>
               <td><mm:field name="postcode" /></td>
               <td><mm:write referid="ssource" /></td>
               <td><% 
                  if(!isGroupExcursion) { 
                     %><mm:write referid="sdescription" /><% 
                  } else {
                     %><mm:field name="prefix" /><%
                  } %></td>
               <td><mm:list nodes="<%= snumber %>" path="inschrijvingen,related,inschrijvings_status"><mm:field name="inschrijvings_status.naam" /></mm:list></td>
               <td style="text-align:right;"><nobr>
                  <% if(isEditor && actionId.indexOf("print")==-1) {
                     %><a href="printsubscription.jsp?e=<%= nodenr %>&p=<%= parent_number %>&s=<%= snumber %>&d=<%= dnumber %>" target="_blank">
                        <img src='../img/printsubscription.gif' align='absmiddle' border='0' alt='Print bevestigingsbrief'></a>
                     <jsp:include page="maillink.jsp">
                        <jsp:param name="s" value="<%= snumber %>" />
                        <jsp:param name="d" value="<%= dnumber %>" />
                     </jsp:include>
                     <html:image src="../img/delete.gif" style="width:16px;" property="buttons.deleteSubscription" alt="Verwijder aanmelding" onclick="<%= "document.forms[0].subscriptionNumber.value='" + snumber + "';return doDelete('Weet u zeker dat u aanmelding " + snumber + " wilt verwijderen?');" %>"  />
                  <% } %></nobr>
               </td>
            </mm:first>
            <mm:first inverse="true">
               <td colspan="8"></td>
            </mm:first>
         </tr>
         <mm:first>
         <% 
         if(isGroupExcursion) { 
            %>
            <tr <% row++; if(row%2==1){ %><%= sHighLight %><% } %>>
               <td colspan="10" style="vertical-align:top;text-align:right;">bijzonderheden
                  <%
                  if(isEditor) {
                     %><a href="<mm:url page="<%= editwizard_location %>"/>/jsp/wizard.jsp?wizard=config/inschrijvingen/inschrijvingen_addcategorie&nodepath=inschrijvingen&objectnumber=<%= snumber %>&origin=<%= parent_number %>&referrer=<%= sReferrer %>&language=nl">
                        <img src='../img/edit_w.gif' align='absmiddle' border='0' alt='Bewerk bijzonderheden'>
                     </a><%
                  } %>
               </td>
               <td colspan="8">   
                  <mm:write referid="sdescription" />
               </td>
            </tr>
            <tr <% row++; if(row%2==1){ %><%= sHighLight %><% } %>>
               <td colspan="10" style="vertical-align:top;text-align:right;">aard van de groep
                  <%
                  if(isEditor) {
                     %><a href="<mm:url page="<%= editwizard_location %>"/>/jsp/wizard.jsp?wizard=config/inschrijvingen/inschrijvingen_addcategorie&nodepath=inschrijvingen&objectnumber=<%= snumber %>&origin=<%= parent_number %>&referrer=<%= sReferrer %>&language=nl">
                        <img src='../img/edit_w.gif' align='absmiddle' border='0' alt='Selecteer aanmeldingscategorie'>
                     </a><%
                  } %>
               </td>
               <td colspan="8">   
                  <mm:list nodes="<%= snumber %>" path="inschrijvingen,related,inschrijvings_categorie">
                     <mm:field name="inschrijvings_categorie.name" />
                  </mm:list>
               </td>
            </tr>
            <tr <% row++; if(row%2==1){ %><%= sHighLight %><% } %>>
               <td colspan="10" style="vertical-align:top;text-align:right;">bevestigings tekst en afwijkende kosten
                  <%
                  if(isEditor) {
                     %><a href="<mm:url page="<%= editwizard_location %>"/>/jsp/wizard.jsp?wizard=config/inschrijvingen/inschrijvingen_addcategorie&nodepath=inschrijvingen&objectnumber=<%= snumber %>&origin=<%= parent_number %>&referrer=<%= sReferrer %>&language=nl">
                        <img src='../img/edit_w.gif' align='absmiddle' border='0' alt='Selecteer bevestigingstekst'>
                     </a><%
                  } %>
               </td>
               <td colspan="8">
                  <mm:list nodes="<%= snumber %>" path="inschrijvingen,daterel,bevestigings_teksten">
                     <mm:node element="bevestigings_teksten">
                        <mm:field name="titel" />
                        <mm:related path="posrel,evenement" constraints="<%= "evenement.number = '" + parent_number + "'" %>">
                           <mm:field name="posrel.pos" jspvar="thisCosts" vartype="String" write="false"
                              >(<%= SubscribeAction.priceFormating(thisCosts,isGroupExcursion) %>)
                           </mm:field>
                        </mm:related>
                     </mm:node>
                  </mm:list>
               </td>
            </tr>
            <% 
         } %>
         </mm:first>
         <mm:first inverse="true">
         <mm:last>
            <tr <% row++; if(row%2==1){ %><%= sHighLight %><% } %>>
               <td colspan="9"></td>
               <td style="text-align:right;padding-right:5px;">-------</td>
               <td colspan="8"></td>
            </tr>
            <tr <% row++; if(row%2==1){ %><%= sHighLight %><% } %>>
               <td colspan="9"></td>
               <td style="text-align:right;padding-right:5px;text-decoration:underline;">&nbsp;<%= SubscribeAction.priceFormating(totalCosts) %></td>
               <td colspan="8"></td>
            </tr>
         </mm:last>
         </mm:first>
         </mm:field>
         </mm:node>
         </mm:related>
      </mm:field>
      </mm:field>
      </mm:node><%
      subscriptions.remove(thisSubscription);
   }
   %>
   </table>
   <%= paymentCondition %>
</mm:node>

<% } %>
<!-- ******************************* info on other data of this evenement ******************************** -->
<%
if(actionId.indexOf("printsubscriptions")==-1) {
   if(actionId.indexOf("printdates")==-1) {
      %><div style="position:absolute;right:5px;top:5px;height:350px;width:370px;overflow:auto;z-index:100"><% 
   } %>
   <mm:node number="<%= parent_number %>"  jspvar="parentEvent">
      <table class="formcontent" border="0" cellpadding="0" cellspacing="0" style="width:350px;">
      <tr>
         <td colspan="4" class="fieldname">
         <% boolean bShowPastDates = false;
         long now = (new Date()).getTime();
         if(actionId.indexOf("print")==-1) {
            %><logic:equal name="SubscribeForm" property="showPastDates" value="false">
               overzicht komende data voor deze activiteit
               <html:image src="../img/add.gif" style="width:13px;" property="buttons.showPastDates" alt="Toon alle data"  />
            </logic:equal>
            <logic:equal name="SubscribeForm" property="showPastDates" value="true">
               overzicht alle data voor deze activiteit
               <html:image src="../img/minus.gif" style="width:13px;" property="buttons.showPastDates" alt="Laat data uit het verleden niet zien"  />
               <% bShowPastDates = true; %>
            </logic:equal>
            <a href="SubscribeInitAction.eb?number=<%= nodenr %>&action=printdates&showpastdates=<%= bShowPastDates %>" target="_blank">
               <img src='../img/print_dates.gif' align='absmiddle' border='0' alt='Print het overzicht van data voor deze activiteit'></a>
				<a href="#" onClick="javascript:launchCenter('download_popup.jsp?event=<%= nodenr %>&type=d', 'center', 300, 400, 'resizable=1');setTimeout('newwin.focus();',250);">
					<img src='../img/excel_dates.gif' align='absmiddle' border='0' alt='Download alle data voor deze activiteit'></a><%
         } else {
            if(showpastdatesId.equals("false")) {
               %>overzicht komende data voor deze activiteit<%
            } else {
               %>overzicht alle data voor deze activiteit<%
                bShowPastDates = true;
            }
         } %>  
         </td>
      </tr>
      <tr><td>datum</td><td colspan="2">tijd</td><td>beschikbare<br>plaatsen</td></tr>
      <%
      Evenement ev = new Evenement();
      NodeList nl = ev.getSortedList(cloud, parent_number);
      for(int i=0; i< nl.size(); i++) {
         Node childEvent = cloud.getNode(nl.getNode(i).getStringValue("evenement.number"));
         long lChildEventEndDate = childEvent.getLongValue("einddatum") * 1000;
         if(bShowPastDates || (lChildEventEndDate > now)) {
           long lChildEventBeginDate = childEvent.getLongValue("begindatum") * 1000;
           String sChildEventDayDescr = childEvent.getStringValue("dagomschrijving");
           int iChildCurParticipants = -1; 
           try { iChildCurParticipants = childEvent.getIntValue("cur_aantal_deelnemers"); } catch (Exception e) {}
           %>
           <tr>
               <%= CalculateTimeTableDate(childEvent.getNumber(),lChildEventBeginDate, lChildEventEndDate, sChildEventDayDescr) %>
               <td>
                 <%= iMaxNumber-iChildCurParticipants %>
                 <% if(!nodenr.equals(""+childEvent.getNumber())&&(actionId.indexOf("print")==-1)) { %>
                    <jsp:include page="subscribelink.jsp">
                       <jsp:param name="p" value="<%= parentEvent.getNumber() %>" />
                       <jsp:param name="e" value="<%= childEvent.getNumber() %>" />
                     </jsp:include>
                 <% } %>
               </td>
           </tr>
         <% }
      } %>
      </table>
</mm:node>
<%
   if(actionId.indexOf("printdates")==-1) {
      %></div><%
   }
} 
if(actionId.indexOf("print")>-1&&!showpastdatesId.equals("true")) {
   %><div style="position:absolute;right:5px;top:5px;z-index:100">
      <a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a>
   </div><%
} %> 
</html:form>
<br/>
<br/>
<br/>
</body>
</html>
</mm:cloud>
