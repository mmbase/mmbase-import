<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="com.finalist.tree.*,
   nl.leocms.authorization.forms.*,
   nl.leocms.evenementen.forms.*,
   nl.leocms.evenementen.Evenement,
   java.util.*,
   org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" jspvar="cloud" rank="basic user">
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Activiteiten</title>
   <style>
   .contentblock {
       display: block;
       overflow: auto;
       width: 750px;
       position: relative;
       height: 442px; /* 600 - 158 */
   }
   </style>
   <script language="JavaScript" src="../../calendar/calendar.js"></script>
   <script>
        var popupSource = 'begin';
        var lastSelected = '';
        function popUpCalendar(source) {
            popupSource = source;
            if(popupSource=='begin') {
               if(document.forms[0].beginDay.value != "" 
                  && document.forms[0].beginMonth.value != "" 
                  && document.forms[0].beginYear.value != "")
                 document.forms[0]._hiddenDate.value = 
                  document.forms[0].beginDay.value 
                     + '-' + document.forms[0].beginMonth.value 
                     + '-' + document.forms[0].beginYear.value;
            } else {
               if(document.forms[0].endDay.value != "" 
                  && document.forms[0].endMonth.value != "" 
                  && document.forms[0].endYear.value != "")
                 document.forms[0]._hiddenDate.value = 
                  document.forms[0].endDay.value 
                     + '-' + document.forms[0].endMonth.value 
                     + '-' + document.forms[0].endYear.value;
            }
            popup_calendar_year("document.forms[0]._hiddenDate");
        }             
        function popupDone(resultfield) {
            if(resultfield == document.forms[0]._hiddenDate){
              var splitData = document.forms[0]._hiddenDate.value.split('-');
              if (splitData.length == 3) {
                if(popupSource=='begin') {
                  document.forms[0].beginDay.value = splitData[0];
                  document.forms[0].beginMonth.value = splitData[1];
                  document.forms[0].beginYear.value = splitData[2];
                } else {
                  document.forms[0].endDay.value = splitData[0];
                  document.forms[0].endMonth.value = splitData[1];
                  document.forms[0].endYear.value = splitData[2];
                }
              }
            }
        } 
        function populateInput(
            beginYear,beginMonth,beginDay,beginHour,beginMinute
            ,endYear,endMonth,endDay,endHour,endMinute,selectedEvent,isSpareDate,isOnInternet,isCanceled) {
           document.forms[0].beginYear.value = beginYear;
           document.forms[0].beginMonth.value = beginMonth;
           document.forms[0].beginDay.value = beginDay;
           document.forms[0].beginHour.value = beginHour;
           document.forms[0].beginMinute.value = beginMinute;
           if(endYear!=beginYear||endMonth!=beginMonth||endDay!=beginDay) {
               document.forms[0].endYear.value = endYear;
               document.forms[0].endMonth.value = endMonth;
               document.forms[0].endDay.value = endDay;
           } else {
              document.forms[0].endYear.value = '';
              document.forms[0].endMonth.value = '';
              document.forms[0].endDay.value = '';
           }
           document.forms[0].endHour.value = endHour;
           document.forms[0].endMinute.value = endMinute;
           document.forms[0].selectedEvent.value = selectedEvent;
           document.forms[0].isSpareDate.checked = isSpareDate;
           document.forms[0].isOnInternet.checked = isOnInternet;
           document.forms[0].isCanceled.checked = isCanceled;
           setSelected(selectedEvent);
        }
        function setSelected(selectedEvent) {
           if(document.getElementById('d_'+selectedEvent)!=null) {
              document.getElementById('d_'+selectedEvent).style.color = 'red';
              document.getElementById('t_'+selectedEvent).style.color = 'red';
              document.getElementById('n_'+selectedEvent).style.color = 'red';
              if(lastSelected!='') {
                  document.getElementById('d_'+lastSelected).style.color = '';
                  document.getElementById('t_'+lastSelected).style.color = '';
                  document.getElementById('n_'+lastSelected).style.color = '';
              }
              lastSelected = selectedEvent;
           }
        }
        function resizeBlocks(){
            var MZ=(document.getElementById?true:false); 
            var IE=(document.all?true:false);
            var windowHeight = 0;
            var contentDiv = 300;
            var minHeight = 400;
            if(IE){ 
            	windowHeight = document.body.clientHeight;
            	if(windowHeight>minHeight) {
                  document.all['contentblock'].style.height = windowHeight - contentDiv;
               }
            } else if(MZ){
            	windowHeight = window.innerHeight;
           		if(windowHeight>minHeight) {
           		   document.getElementById('contentblock').style.height= windowHeight - contentDiv;
           		}
            }
            return false;
        }
        
	     var cancelClick = false;
        function doDelete(thisObject,prompt) {
      		var conf;
      		if (prompt && prompt!="") {
      			conf = confirm(prompt);
      		} else conf=true;
      		cancelClick=true;
            
            if (conf) showMessage(thisObject,'Verwijder selectie');
      		return conf;
        }
        
        function selectAllDates(field) {
            for (i = 0; i < field.length; i++) {
               if (field[i].checked) { 
                  field[i].checked = false;
               } else {
                  field[i].checked = true;
               }
            }
        } 
                  
      var clickedButton = '';
      function showMessage(obj,fAction){
         string = 'theTarget = document.getElementById("message");';
         eval(string);
         if(theTarget != null){
            theTarget.innerHTML = 'Een moment geduld a.u.b.';
         }
         clickedButton=obj;
         clickedButton.disabled=true;

         document.getElementById("formAction").value= fAction;
         document.forms[0].submit();
      }                  
          
    </script>
</head>
<%
boolean isAdmin = cloud.getUser().getRank().equals("administrator");
boolean isChiefEditor = cloud.getUser().getRank().equals("chiefeditor");

session.setAttribute("creatierubriek","natuurin_rubriek"); // ** to let editwizard relate evenement to creatierubriek **

String dateStyle = "width:40px;text-align:right;"; 
String buttonStyle = "width:130px;"; 
String evenementId = "";

// the focus="name" works together with the onkeypress for the name field, to make sure the form is only posted once on [ENTER]-key
%>
<html:form action="/editors/evenementen/EvenementAction" scope="session" focus="name">

<html:hidden property="userId" value="<%= cloud.getUser().getIdentifier() %>" />
<html:hidden property="node" />
<html:hidden property="selectedEvent" />
<input type="hidden" id="formAction" name="action" value="" />

<body style="overflow:auto;" onload="javascript:setSelected(<bean:write name="EvenementForm" property="selectedEvent" />);javascript:resizeBlocks();"  onResize="javascript:resizeBlocks();">
<logic:equal name="EvenementForm" property="node" value="">
    <h1>Nieuwe activiteit</h1>   
</logic:equal>
<logic:notEqual name="EvenementForm" property="node" value="">
    <bean:define id="nodenr" property="node" name="EvenementForm" scope="session" type="java.lang.String"/>
    <% evenementId =  nodenr; %>
    <h1>Activiteit wijzigen</h1>
</logic:notEqual>
<table class="formcontent">
<tr>
   <td colspan="2" class="fieldname" style="color:red;font-weight:bold;" id="message"></td>
</tr>
<tr>
   <td colspan="2">&nbsp;</td>
</tr> 
<tr><td class="fieldname" style="width:90px;">Naam</td>
    <td><html:text property="name" style="width:349px;" tabindex="1" onkeypress="return event.keyCode!=13" />
        <span class="notvalid"><br><html:errors bundle="LEOCMS" property="naam" /></span></td></tr>       
<tr><td class="fieldname" style="width:90px;">&nbsp;</td>
    <td><table class="formcontent" style="width:auto;">
         <tr><td style="width:45px;">dag</td><td style="width:45px;">maand</td><td style="width:72px;">jaar</td>
             <td style="width:45px;">uur</td><td style="width:45px;">minuut</td>
             <td style="width:45px;">uur</td><td style="width:45px;">minuut</td>
             <td style="width:45px;">internet</td><td style="width:45px;">reserve</td><td style="width:45px;">geannuleerd</td>
        </table>
    </td></tr>
<tr><td class="fieldname" style="width:90px;">Datum</td>
    <td><input type="hidden" name="_hiddenDate" >
        <html:text property="beginDay" maxlength="2" style="<%= dateStyle %>" tabindex="2"/>
        -<html:text property="beginMonth" maxlength="2" style="<%= dateStyle %>" tabindex="3"/>
        -<html:text property="beginYear" maxlength="4" style="<%= dateStyle %>" tabindex="4"/>
        <a href="javascript:popUpCalendar('begin')" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0' alt='Selecteer begindatum'></a>
        -<html:text property="beginHour" maxlength="2" style="<%= dateStyle %>" tabindex="5"/>
        -<html:text property="beginMinute" maxlength="2" style="<%= dateStyle %>" tabindex="6"/>
        -<html:text property="endHour" maxlength="2" style="<%= dateStyle %>" tabindex="7"/>
        -<html:text property="endMinute" maxlength="2" style="<%= dateStyle %>" tabindex="8"/>&nbsp;
        <html:checkbox property="isOnInternet" value="true" style="width:13px;margin-left:25px;" />&nbsp;
        <html:checkbox property="isSpareDate" value="true" style="width:13px;margin-left:15px;" />&nbsp;
        <html:checkbox property="isCanceled" value="true" style="width:13px;margin-left:35px;" />&nbsp;
        <span class="notvalid"><html:errors bundle="LEOCMS" property="begintijd"/></span>
    </td></tr>
<tr><td class="fieldname" style="width:90px;">Einde periode</td>
    <td><html:text property="endDay" maxlength="2" style="<%= dateStyle %>" tabindex="9"/>
        -<html:text property="endMonth" maxlength="2" style="<%= dateStyle %>" tabindex="10"/>
        -<html:text property="endYear" maxlength="4" style="<%= dateStyle %>" tabindex="11"/>
        <a href="javascript:popUpCalendar('end')" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0' alt='Selecteer einddatum'></a>
        <span class="notvalid"><html:errors bundle="LEOCMS" property="eindtijd"/></span><br>
		  <input type="button" name="action" value="Voeg toe" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Voeg toe');"/>&nbsp;
        <input type="button" name="action" value="Wijzig" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Wijzig');"/>&nbsp;
        <input type="button" name="action" value="Wis" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Wis');"/>&nbsp;
    </td></tr>
<tr><td class="fieldname" style="width:90px;height:20px">
      <% boolean bShowPastDates = false;
         long now = (new Date()).getTime()/1000; %>
      <logic:equal name="EvenementForm" property="showPastDates" value="">
         Data
      </logic:equal>
      <logic:equal name="EvenementForm" property="showPastDates" value="false">
         Komende data<br/>
         <html:image src="../img/add.gif" style="width:13px;" property="buttons.showPastDates" alt="Toon alle data"  />
      </logic:equal>
      <logic:equal name="EvenementForm" property="showPastDates" value="true">
         Alle data<br/>
         <html:image src="../img/minus.gif" style="width:13px;" property="buttons.showPastDates" alt="Laat data uit het verleden niet zien"  />
         <% bShowPastDates = true; %>
      </logic:equal>
    </td>
    <td>
      <div class="contentblock" id="contentblock">
      <% int row=-1; %>
      <logic:iterate name="EvenementForm" id="event" property="dates">
        <logic:notEqual name="event" property="number" value="<%= evenementId %>">
           <mm:import id="childeventfound" reset="true"/> 
        </logic:notEqual>
        <bean:define name="event" property="number" id="eventnr" />
        <mm:notpresent referid="isfirstevent">
        <table class="formcontent" style="width:auto;">
        </mm:notpresent>
        <% boolean bShowDate = true; 
           String curNmbParticipants = "";
        %><mm:node number="<%= (String) eventnr %>" jspvar="thisEvent" notfound="skipbody"><%
            if(!bShowPastDates&&thisEvent.getLongValue("einddatum")<now) { bShowDate = false; } 
            curNmbParticipants = thisEvent.getStringValue("cur_aantal_deelnemers");
        %></mm:node>
           <%
           if(bShowDate) {
              row++; 
              if(row%10==0) { %>
                  <tr><td>selectie&nbsp;</td><td>datum of periode&nbsp;</td><td>tijd&nbsp;</td><td>dagen van toepassing&nbsp;</td>
                     <td>internet&nbsp;</td><td>reserve&nbsp;</td><td>geannuleerd&nbsp;</td><td>aanmeldingen&nbsp;</td></tr><% 
              } %>
              <tr><td><html:multibox property="selectedDates" style="width:13px;">
                           <bean:write name="event" property="key" /> 
                      </html:multibox>   
                      </td>
                  <td><a href="#" onclick="javascript:populateInput(<bean:write name="event" property="commaSeparatedValue" />);">
                        <div id="d_<%= eventnr %>"><bean:write name="event" property="readableDate" /></div></a></td>
                  <td><a href="#" onclick="javascript:populateInput(<bean:write name="event" property="commaSeparatedValue" />);">
                         <div id="t_<%= eventnr %>"><bean:write name="event" property="readableTime" /></div></a></td>
                  <td><logic:equal name="event" property="multiDay" value="true">
                         <logic:iterate name="EvenementForm" id="day" property="daysOfWeek" indexId="dayCtr">
                            <html:multibox property="selectedDaysOfWeek" style="width:13px;">
                               <%= "" + eventnr + "," + dayCtr %>
                            </html:multibox>
                            <bean:write name="day" property="value" />
                         </logic:iterate>
                         <mm:import id="multidayfound" reset="true"/> 
                      </logic:equal></td>
                 <td style="text-align:center;"><logic:equal name="event" property="isOnInternet" value="true"><img src='../img/preview.gif' align='absmiddle' border='0' alt='Activiteit staat op internet'></logic:equal></td>
                 <td style="text-align:center;"><logic:equal name="event" property="isSpareDate" value="true"><img src='../img/rubriek.gif' align='absmiddle' border='0' alt='Reserve datum voor activiteit'></logic:equal></td>
                 <td style="text-align:center;"><logic:equal name="event" property="isCanceled" value="true"><img src='../img/remove.gif' align='absmiddle' border='0' alt='Activiteit is geannuleerd'> </logic:equal></td>
                 <td style="text-align:right;"><div id="n_<%= eventnr %>">
                     <%= curNmbParticipants %>
                     <jsp:include page="subscribelink.jsp">
                        <jsp:param name="p" value="<%= evenementId %>" />
                        <jsp:param name="e" value="<%= eventnr %>" />
                     </jsp:include>
                  </div></td>
                 </tr>
                 <%
            } %>
        
        <mm:import id="isfirstevent" reset="true" /> 
     </logic:iterate>
     <mm:present referid="isfirstevent">
        </table>
     </mm:present>
     </div>
     <span class="notvalid"><html:errors bundle="LEOCMS" property="lijst" /></span><br>
     <input type="button" value="Alles (de)selecteren" style="<%= buttonStyle %>" onclick="selectAllDates(document.forms[0].selectedDates);"/>
     <input type="button" name="action" value="Activeer selectie" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Activeer selectie');"/>
     <input type="button" name="action" value="Annuleer selectie" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Annuleer selectie');"/>
     <mm:present referid="multidayfound">
         <input type="button" name="action" value="Periode -> data" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Periode -> data');"/>
     </mm:present>
     <mm:present referid="childeventfound">
         <input type="button" name="action" value="Verwijder selectie" onclick="return doDelete(this,'Weet u zeker dat u de geselecteerde data wilt verwijderen?');" style="<%= buttonStyle %>" />
     </mm:present>
     </td></tr>
   <tr><td class="fieldname" style="width:90px;">Overige onderdelen</td>
      <td>
         <logic:notEqual name="EvenementForm" property="node" value="">
         <% String wizard = "evenement_bu";
            if(isChiefEditor) wizard = "evenement_ce";
            if(isAdmin) wizard = "evenement_admin";
            %><a href="<mm:url page="<%= editwizard_location %>"/>/jsp/wizard.jsp?wizard=config/evenement/<%= wizard %>&nodepath=evenement&objectnumber=<%= evenementId %>">bewerk</a>
         </logic:notEqual>
         <logic:equal name="EvenementForm" property="node" value="">
         De activiteit kan worden bewerkt, nadat data zijn toegevoegd en de activiteit vervolgens is opgeslagen.
         </logic:equal></td></tr>
</table>
<hr>
<table class="formcontent">
   <tr>
      <td>
         <span class="notvalid"><html:errors bundle="LEOCMS" property="opslaan"/></span>
         <html:cancel value="Annuleren" style="<%= buttonStyle %>" />&nbsp;
         <input type="button" name="action" value="Opslaan & be&euml;indigen" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Opslaan en beeindingen');"/>&nbsp;
         <input type="button" name="action" value="Opslaan" style="<%= buttonStyle %>" onclick="javascript:showMessage(this,'Opslaan');"/>
      </td>
   </tr>
</table>
</body>
</html:form>
</html>
</mm:cloud>