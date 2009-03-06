<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@page import="nl.leocms.util.DoubleDateNode,nl.leocms.evenementen.Evenement,java.util.*" 
%><%@include file="/taglibs.jsp" 
%><mm:content type="text/html" escaper="none">
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%
//******************************* IMPORT PARAMETERS ********************************
Calendar cal = Calendar.getInstance();
cal.setTime(new Date());
int maxAge = 60 * 60 * 24 * 365;

String orderbyIdFC = "titel";
String directionIdFC = "up";
String soortIdFC = "activiteiten";
String titelIdFC = "";
String natuurgebiedenIdFC = "";

String aanmelderNaamIdFC = "";
String aanmelderPostcodeIdFC = "";
String aanmelderNummerIdFC = "";

String beginDyIdFC = "" + cal.get(Calendar.DAY_OF_MONTH);
String beginMnthIdFC = "" + (cal.get(Calendar.MONTH)+1);
String beginYrIdFC = "" + cal.get(Calendar.YEAR);
String endDyIdFC = "";
String endMnthIdFC = "";
String endYrIdFC = "";
String provincieIdFC = "";
String evenement_typeIdFC = "";
String no_group_eventsIdFC = "off";
String afdelingIdFC = "";
String offsetIdFC = "0";
Cookie[] cookies = request.getCookies();
if(cookies!=null){
   for (int c = 0; c < cookies.length; c++) {
      String thisName = cookies[c].getName();
      String thisValue = cookies[c].getValue();
      if (thisName!=null&&thisValue!=null) {
         if(thisName.equals("provincieIdFC")) { provincieIdFC = thisValue; }
         if(thisName.equals("evenement_typeIdFC")) { evenement_typeIdFC = thisValue; }
         if(thisName.equals("no_group_eventsIdFC")) { no_group_eventsIdFC = thisValue; }
         if(thisName.equals("afdelingIdFC")) { afdelingIdFC = thisValue; }
         if(thisName.equals("orderbyIdFC")) { orderbyIdFC = thisValue; }
         if(thisName.equals("directionIdFC")) { directionIdFC = thisValue; }
         if(thisName.equals("soortIdFC")) { soortIdFC = thisValue; }
         if(thisName.equals("titelIdFC")) { titelIdFC = thisValue; }
         if(thisName.equals("natuurgebiedenIdFC")) { natuurgebiedenIdFC = thisValue; }

         if(thisName.equals("aanmelderNaamIdFC")) { aanmelderNaamIdFC = thisValue; }
         if(thisName.equals("aanmelderPostcodeIdFC")) { aanmelderPostcodeIdFC = thisValue; }
         if(thisName.equals("aanmelderNummerIdFC")) { aanmelderNummerIdFC = thisValue; }

         if(thisName.equals("beginDyIdFC")) { beginDyIdFC = thisValue; }
         if(thisName.equals("beginMnthIdFC")) { beginMnthIdFC = thisValue; }
         if(thisName.equals("beginYrIdFC")) { beginYrIdFC = thisValue; }
         if(thisName.equals("endDyIdFC")) { endDyIdFC = thisValue; }
         if(thisName.equals("endMnthIdFC")) { endMnthIdFC = thisValue; }
         if(thisName.equals("endYrIdFC")) { endYrIdFC = thisValue; }
         if(thisName.equals("offsetIdFC")) { offsetIdFC = thisValue; }
      }
   }
}
%>
<mm:import externid="command" jspvar="commandId" id="commandId" /><% if(commandId==null) { commandId = ""; } %>
<mm:import externid="delete" jspvar="deleteId" id="deleteId" /><% if(deleteId==null) { deleteId = ""; } %>
<mm:import externid="docopy" jspvar="docopyId" id="docopyId" /><% if(docopyId==null) { docopyId = ""; } %>
<mm:import externid="provincie" jspvar="provincieId" id="provincieId" /><% if(provincieId==null) { provincieId = provincieIdFC; } %>
<mm:import externid="evenement_type" jspvar="evenement_typeId" id="evenement_typeId" /><% if(evenement_typeId==null) { evenement_typeId = evenement_typeIdFC; } %>
<mm:import externid="no_group_events" jspvar="no_group_eventsId" id="no_group_eventsId" /><% if(no_group_eventsId==null) { no_group_eventsId = ""; } %>
<mm:import externid="afdeling" jspvar="afdelingId" id="afdelingId" /><% if(afdelingId==null) { afdelingId = afdelingIdFC; } %>
<mm:import externid="orderby" jspvar="orderbyId" id="orderbyId"><%= orderbyIdFC %></mm:import><% if(orderbyId==null) { orderbyId = "titel"; } %>
<mm:import externid="direction" jspvar="directionId" id="directionId"><%= directionIdFC %></mm:import>
<mm:import externid="soort" jspvar="soortId" id="soortId"><%= soortIdFC %></mm:import><% if(soortId==null) { soortId = ""; } %>
<mm:import externid="titel" jspvar="titelId" id="titelId" /><% if(titelId==null) { titelId = titelIdFC; } %>
<mm:import externid="natuurgebieden" jspvar="natuurgebiedenId" id="natuurgebiedenId" /><% if(natuurgebiedenId==null) { natuurgebiedenId =  natuurgebiedenIdFC; } %>

<mm:import externid="aanmelder_naam" jspvar="aanmelderNaamId" id="aanmelderNaamId" /><% if(aanmelderNaamId==null) { aanmelderNaamId = aanmelderNaamIdFC; } %>
<mm:import externid="aanmelder_postcode" jspvar="aanmelderPostcodeId" id="aanmelderPostcodeId" /><% if(aanmelderPostcodeId==null) { aanmelderPostcodeId = aanmelderPostcodeIdFC; } %>
<mm:import externid="aanmelder_nummer" jspvar="aanmelderNummerId" id="aanmelderNummerId" /><% if(aanmelderNummerId==null) { aanmelderNummerId = aanmelderNummerIdFC; } %>

<mm:import externid="beginDy" jspvar="beginDyId" id="beginDyId" /><% if(beginDyId==null||beginDyId.equals("")) { beginDyId = beginDyIdFC; } %>
<mm:import externid="beginMnth" jspvar="beginMnthId" id="beginMnthId" /><% if(beginMnthId==null||beginMnthId.equals("")) { beginMnthId = beginMnthIdFC; } %>
<mm:import externid="beginYr" jspvar="beginYrId" id="beginYrId" /><% if(beginYrId==null||beginYrId.equals("")) { beginYrId = beginYrIdFC; } %>
<mm:import externid="endDy" jspvar="endDyId" id="endDyId" /><% if(endDyId==null||endDyId.equals("")) { endDyId = endDyIdFC; } %>
<mm:import externid="endMnth" jspvar="endMnthId" id="endMnthId" /><% if(endMnthId==null||endMnthId.equals("")) { endMnthId = endMnthIdFC; } %>
<mm:import externid="endYr" jspvar="endYrId" id="endYrId" /><% if(endYrId==null||endYrId.equals("")) { endYrId = endYrIdFC; } %>
<mm:import externid="offset" jspvar="offsetId" id="offsetId" /><% if(offsetId==null) { offsetId = offsetIdFC; } %>
<mm:import externid="action" jspvar="actionId" id="actionId" /><% if(actionId==null) { actionId = ""; } %>
<%
if(commandId.equals("Wis")) { // *** reset to default values ***
   provincieId = "";
   evenement_typeId = "";
   no_group_eventsId = "";
   afdelingId = "";
   orderbyId = "titel";
   directionId = "up";
   soortId = "activiteiten";
   titelId = "";
   natuurgebiedenId = "";

   aanmelderNaamId = "";
   aanmelderPostcodeId = "";
   aanmelderNummerId = "";

   beginDyId = "" + cal.get(Calendar.DAY_OF_MONTH);
   beginMnthId = "" + (cal.get(Calendar.MONTH)+1);
   beginYrId = "" + cal.get(Calendar.YEAR);
   endDyId = beginDyId;
   endMnthId = beginMnthId;
   endYrId = "" + (cal.get(Calendar.YEAR) +1);
}
%>
<%--
command: <%= commandId %><br/>
delete: <%= deleteId %><br/>
docopy: <%= docopyId %><br/>
provincie: <%= provincieId %><br/>
evenement_type: <%= evenement_typeId %><br/>
no_group_events: <%= no_group_eventsId %><br/>
afdeling: <%= afdelingId %><br/>
orderby: <%= orderbyId %><br/>
direction: <%= directionId %><br/>
soort: <%= soortId %><br/>
titel: <%= titelId %><br/>
natuurgebieden:<%= natuurgebiedenId %><br/>

aanmelder_naam: <%= aanmelderNaamId %><br/>
aanmelder_postcode: <%= aanmelderPostcodeId %><br/>
aanmelder_nummer: <%= aanmelderNummerId %><br/>

beginDy: <%= beginDyId %><br/>
beginMnth: <%= beginMnthId %><br/>
beginYr: <%= beginYrId %><br/>
endDy: <%= endDyId %><br/>
endMnth: <%= endMnthId %><br/>
endYr: <%= endYrId %><br/>
offset: <%= offsetId %><br/>
action: <%= actionId %><br/>
--%>
<%
Cookie thisCookie = null;
thisCookie = new Cookie("provincieIdFC", provincieId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("evenement_typeIdFC", evenement_typeId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("no_group_eventsIdFC", no_group_eventsId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("afdelingIdFC", afdelingId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("orderbyIdFC", orderbyId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("directionIdFC", directionId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("soortIdFC", soortId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("titelIdFC", titelId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("natuurgebiedenIdFC",natuurgebiedenId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);

// thisCookie = new Cookie("aanmelderNaamIdFC", aanmelderNaamId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
// thisCookie = new Cookie("aanmelderPostcodeIdFC", aanmelderPostcodeId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
// thisCookie = new Cookie("aanmelderNummerIdFC", aanmelderNummerId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);

thisCookie = new Cookie("beginDyIdFC", beginDyId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("beginMnthIdFC", beginMnthId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("beginYrIdFC", beginYrId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("endDyIdFC", endDyId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("endMnthIdFC", endMnthId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("endYrIdFC", endYrId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);
thisCookie = new Cookie("offsetIdFC", offsetId ); thisCookie.setMaxAge(maxAge); response.addCookie(thisCookie);

if(Integer.parseInt(beginMnthId)==13) {
   beginMnthId = "1";
   beginYrId = "" + (Integer.parseInt(beginYrId)+1);
}
if(Integer.parseInt(beginMnthId)==0) {
   beginMnthId = "12";
   beginYrId = "" + (Integer.parseInt(beginYrId)-1);
}

if(!endMnthId.equals("")&&!endYrId.equals("")) {
   if(Integer.parseInt(endMnthId)==13) {
      endMnthId = "1";
      endYrId = "" + (Integer.parseInt(endYrId)+1);
   }
   if(Integer.parseInt(endMnthId)==0) {
      endMnthId = "12";
      endYrId = "" + (Integer.parseInt(endYrId)-1);
   }
}

boolean isAdmin = cloud.getUser().getRank().equals("administrator");
boolean isChiefEditor = cloud.getUser().getRank().equals("chiefeditor");

String newDirection = "up"; if(directionId.equals("up")) { newDirection = "down"; }
String newSoort = "activiteiten"; if(soortId.equals("activiteiten")) { newSoort = "overzicht data"; }

String searchSettingMinMonth =
      "&beginDy=" + beginDyId
      + "&beginYr=" + beginYrId
      + "&endDy=" + endDyId
      + "&endYr=" + endYrId
      + "&titel=" + titelId
      + "&natuurgebieden=" + natuurgebiedenId;
String searchSetting = searchSettingMinMonth + "&beginMnth=" + beginMnthId + "&endMnth=" + endMnthId;
DoubleDateNode ddn = new DoubleDateNode();
TreeMap events = new TreeMap();

int thisOffset = 0;
try{
    if(!offsetId.equals("")){
        thisOffset = Integer.parseInt(offsetId);
        offsetId ="";
    }
} catch(Exception e) {}
int pageSize = 25;

long begintime = 0;
if(!beginDyId.equals("")&&!beginMnthId.equals("")&&!beginYrId.equals("")) {
   cal.set(Integer.parseInt(beginYrId),Integer.parseInt(beginMnthId)-1,Integer.parseInt(beginDyId),0,0,0);
   begintime = cal.getTime().getTime()/1000;
}
long endtime = 0;
if(!endDyId.equals("")&&!endMnthId.equals("")&&!endYrId.equals("")) {
   cal.set(Integer.parseInt(endYrId),Integer.parseInt(endMnthId)-1,Integer.parseInt(endDyId),23,59,59);
   endtime = cal.getTime().getTime()/1000;
}

if(!deleteId.equals("")) {
   %><mm:deletenode number="<%= deleteId %>" deleterelations="true"/><%
}

if(!docopyId.equals("")) {
   %><jsp:include page="copyevent.jsp">
      <jsp:param name="e" value="<%= docopyId %>" />
      <jsp:param name="d" value="<%= begintime %>" />
    </jsp:include><%
   titelId = "Copy van";
   provincieId = "";
   evenement_typeId = "";
   afdelingId = "";
}

%>
<!-- ******************************* FIND ACTIVITEITEN ******************************** -->
<% String allowedNatuurgebieden = ""; %>
<mm:listnodes type="users" constraints="<%= "[account]='" + cloud.getUser().getIdentifier() + "'" %>" max="1">
   <mm:related path="rolerel,afdelingen,posrel,natuurgebieden">
      <mm:field name="natuurgebieden.number" jspvar="sNumber" vartype="String" write="false">
         <% allowedNatuurgebieden += "," + sNumber; %>
      </mm:field>
   </mm:related>
</mm:listnodes>
<%
if(!allowedNatuurgebieden.equals("")) {
   allowedNatuurgebieden = allowedNatuurgebieden.substring(1);
}
%>
<mm:listnodescontainer type="evenement">
<%

if(!titelId.equals("")) {
   %><mm:constraint field="titel" operator="LIKE" value="<%= "%" + titelId + "%" %>" /><%
}

if(soortId.equals("activiteiten")) {
   // [embargo,verloopdatum] of activity should have overlap with [begintime,endtime]
   %>
   <mm:constraint field="soort" value="parent" />
   <mm:constraint field="embargo" operator="<=" value="<%= "" + endtime %>" />
   <mm:constraint field="verloopdatum" operator=">=" value="<%= "" + begintime %>" />
   <%
} else {
   // [begindatum,einddatum] of date should be part of [begintime,endtime]
   %>
   <mm:constraint field="begindatum" operator=">=" value="<%= "" + begintime %>" />
   <mm:constraint field="einddatum" operator="<=" value="<%= "" + endtime %>" />
   <%
}

if(!provincieId.equals("")) {
   %><mm:constraint field="lokatie" operator="LIKE" value="<%= "%," + provincieId  + ",%" %>" /><%
}

%><mm:listnodes
   ><mm:field name="number" jspvar="event_number" vartype="String" write="false"
   ><mm:field name="titel" jspvar="titel" vartype="String" write="false"
   ><mm:field name="begindatum" jspvar="begindatum" vartype="String" write="false"><%

   String parent_number = Evenement.findParentNumber(event_number);

   // *** find the key for ordering ***
   String key = "";
   if(orderbyId.equals("titel")) {
      key = titel;
   } else if(orderbyId.equals("begindatum")) {
      if(soortId.equals("activiteiten")) {
         %><mm:field name="embargo" jspvar="embargo" vartype="String" write="false"><%
            key = embargo;
         %></mm:field><%
      } else {
         key = begindatum;
      }
   } else if(orderbyId.equals("natuurgebieden")) {
     %><mm:list nodes="<%= parent_number %>" path="evenement,related,natuurgebieden" max="1"
         ><mm:field name="natuurgebieden.naam"  jspvar="natuurgebieden_naam" vartype="String" write="false"><%
         key = natuurgebieden_naam;
         %></mm:field
     ></mm:list><%
   }
   String tmpKey = key;
   int i =0;
   while(events.containsKey(key)) { key = tmpKey + i; i++; }

   // *** add extra restrictions, the sequence of if-statements works as an AND on isValidEvent ***
   boolean isValidEvent = true;
   if (!evenement_typeId.equals("")) {
      isValidEvent = false;
      %><mm:listcontainer nodes="<%= parent_number %>"  path="evenement,related,evenement_type">
            <mm:constraint field="evenement_type.number" operator="=" value="<%= evenement_typeId %>" />
            <mm:list max="1"><%
               isValidEvent = true;
            %></mm:list
         ></mm:listcontainer>
      <%
   }
   if (isValidEvent && no_group_eventsId.equals("on")){
      // at least one deelnemers_categorie.groepsactiviteit implies a group event
      isValidEvent = true;
      %><mm:listcontainer nodes="<%= parent_number %>"  path="evenement,posrel,deelnemers_categorie">
            <mm:constraint field="deelnemers_categorie.groepsactiviteit" operator="=" value="1" />
            <mm:list max="1"><%
               isValidEvent = false;
            %></mm:list
         ></mm:listcontainer>
      <%
   }
   if(isValidEvent && (!natuurgebiedenId.equals("")||!afdelingId.equals("")||!allowedNatuurgebieden.equals(""))) {
     isValidEvent = false;
     %><mm:listcontainer nodes="<%= parent_number %>"  path="evenement,related,natuurgebieden"><%
       if(!natuurgebiedenId.equals("")) {
         %><mm:constraint field="natuurgebieden.naam" operator="LIKE" value="<%= "%" + natuurgebiedenId + "%" %>" /><%
       }
       if(!allowedNatuurgebieden.equals("")) {
         %><mm:constraint field="natuurgebieden.number" operator="IN" value="<%= allowedNatuurgebieden %>" /><%
       }
       if(!afdelingId.equals("")) {
         %><mm:constraint field="natuurgebieden.titel_eng" operator="LIKE" value="<%= "%," + afdelingId + ",%" %>" /><%
       }
       %><mm:list max="1"><%
         isValidEvent = true;
       %></mm:list
     ></mm:listcontainer><%
   }

   // the search query much check for participant constraints
   if(isValidEvent && (!aanmelderNaamId.equals("")||!aanmelderPostcodeId.equals("")||!aanmelderNummerId.equals(""))) {
        isValidEvent = false;
        %><mm:listcontainer nodes="<%= event_number %>"  path="evenement,posrel,inschrijvingen,posrel,deelnemers"><%
          if(!aanmelderNaamId.equals("")) {
            %><mm:constraint field="deelnemers.lastname" operator="LIKE" value="<%= "%" + aanmelderNaamId + "%" %>" /><%
          }
          if(!aanmelderPostcodeId.equals("")) {
            %><mm:constraint field="deelnemers.postcode" operator="=" value="<%= aanmelderPostcodeId %>" /><%
          }
          if(!aanmelderNummerId.equals("")) {
            %><mm:constraint field="inschrijvingen.number" operator="=" value="<%= aanmelderNummerId %>" /><%
          }
          %><mm:list max="1"><%
            isValidEvent = true;
          %></mm:list
        ></mm:listcontainer><%
   }

   if(isValidEvent) { events.put(key,event_number); }

   %></mm:field
   ></mm:field
   ></mm:field
></mm:listnodes
></mm:listnodescontainer>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title><% if(actionId.indexOf("print")>-1) { %>Print <% } %><%= soortId %></title>
<script language="JavaScript" src="../../calendar/calendar.js"></script>
<script language="javascript" src="../scripts/launchcenter.js"></script>
<script>
  var newwin;
  var popupSource = 'begin';
  function popUpCalendar(source) {
      popupSource = source;
      if(popupSource=='begin') {
         if(document.EvenementForm.beginDy.value != ""
            && document.EvenementForm.beginMnth.value != ""
            && document.EvenementForm.beginYr.value != "")
           document.EvenementForm._hiddenDate.value =
            document.EvenementForm.beginDy.value
               + '-' + document.EvenementForm.beginMnth.value
               + '-' + document.EvenementForm.beginYr.value;
      } else {
         if(document.EvenementForm.endDy.value != ""
            && document.EvenementForm.endMnth.value != ""
            && document.EvenementForm.endYr.value != "")
           document.EvenementForm._hiddenDate.value =
            document.EvenementForm.endDy.value
               + '-' + document.EvenementForm.endMnth.value
               + '-' + document.EvenementForm.endYr.value;
      }
      popup_calendar_year("document.EvenementForm._hiddenDate");
  }
  function popupDone(resultfield) {
      if(resultfield == document.EvenementForm._hiddenDate){
        var splitData = document.EvenementForm._hiddenDate.value.split('-');
        if (splitData.length == 3) {
          if(popupSource=='begin') {
            document.EvenementForm.beginDy.value = splitData[0];
            document.EvenementForm.beginMnth.value = splitData[1];
            document.EvenementForm.beginYr.value = splitData[2];
          } else {
            document.EvenementForm.endDy.value = splitData[0];
            document.EvenementForm.endMnth.value = splitData[1];
            document.EvenementForm.endYr.value = splitData[2];
          }
        }
      }
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
   var clickedButton = '';
   function showMessage(obj,contentString){
      string = 'theTarget = document.getElementById("message");';
      eval(string);
      if(theTarget != null){
         theTarget.innerHTML = contentString;
      }
      clickedButton=obj;
      document.EvenementForm.command.value = clickedButton.value;
   }
   
   function disableExtraSearch() {
      document.EvenementForm.aanmelder_naam.disabled=true;
      document.EvenementForm.aanmelder_postcode.disabled=true;
      document.EvenementForm.aanmelder_nummer.disabled=true;
      
      document.EvenementForm.aanmelder_naam.style.backgroundColor="#cccccc";
      document.EvenementForm.aanmelder_postcode.style.backgroundColor="#cccccc";
      document.EvenementForm.aanmelder_nummer.style.backgroundColor="#cccccc";  
   }
   

</script>
<% if(actionId.indexOf("print")>-1) { %>
<style rel="stylesheet" type="text/css">
   body,td{
      color:#000000;
      background-color: #FFFFFF;
   }
</style>
<% } %>
</head>
<body style="overflow:auto;" <%
   if(actionId.indexOf("print")>-1) {
      %>onload="self.print();"<%
   } %>>
<!-- ******************************* HEADER TABLE ******************************** -->
<form name="EvenementForm" method="post" action="evenementen.jsp?orderby=<%= orderbyId %>&direction=<%= directionId %>&soort=<%= soortId %>&offset=0"
   onsubmit="clickedButton.disabled=true;">
<input type="hidden" name="command" value="">
<table class="formcontent">
<tr><td colspan="4" style="text-align:center;color:red;font-weight:bold;" id="message"></td></tr>
<tr><td><%  if((isAdmin||isChiefEditor)&&actionId.indexOf("print")==-1) { %>
            <a href="EvenementInitAction.eb"><img src="<mm:url page="<%= editwizard_location %>"/>/media/new.gif" border='0' alt='Maak een nieuwe activiteit' onclick="javascript:showMessage(this,'Een moment geduld a.u.b.');"/></a>
        <% } %>
    </td>
    <td class="fieldname" style="padding-left:5px;vertical-align:middle;"><nobr><%= soortId.toUpperCase() %></nobr></td>
    <td style="padding-left:25px;vertical-align:middle;width:50%;"><%
      if(actionId.indexOf("print")==-1) {
         %><nobr><a href="evenementen.jsp?offset=0&orderby=<%= orderbyId %>&direction=<%= directionId %>&soort=<%= newSoort %><%= searchSetting %>">(<%= newSoort %>)</a></nobr><%
      } %></td>
    <td style="vertical-align:bottom;"><%
      if(actionId.indexOf("print")>-1) {
         %><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a><%
      } else {
         %><nobr><a href="<%= "evenementen.jsp?action=print&orderby=" + orderbyId + "&direction=" + directionId + "&soort=" +  soortId + "&offset=" +  offsetId + searchSetting
             %>" target="_blank"><img src='../img/print.gif' align='absmiddle' border='0' alt='Print het overzicht'></a>
         <% if (soortId.equals("activiteiten")&&(events.size()>0)) { %>
                <a href="#" onClick="javascript:launchCenter('download_popup.jsp?type=ad', 'center', 300, 400, 'resizable=1');setTimeout('newwin.focus();',250);">
                   <img src='../img/icexcel.gif' align='absmiddle' border='0' alt='Download alle geselecteerde activiteiten'>
                </a>
          <% } %>
         <select name="afdeling" style="width:130px;">
            <% int iNumberOfDept = 0; %>
            <mm:list nodes="<%= allowedNatuurgebieden %>" path="natuurgebieden,posrel,afdelingen" orderby="afdelingen.naam"
               fields="afdelingen.number" distinct="true">
               <mm:node element="afdelingen">
                  <mm:field name="number" jspvar="afdeling_number" vartype="String" write="false">
                     <option value="<%= afdeling_number %>" <%
                        if(afdeling_number.equals(afdelingId)) {
                           %>selected<% }
                  %>></mm:field
                  ><mm:field name="naam" />
                </mm:node>
                <% iNumberOfDept++; %>
            </mm:list>
            <% if(iNumberOfDept>1) { %>
               <option value="-1" <% if(afdelingId.equals("-1")) { %>selected<% } %>>Geen afdeling
               <option value="" <% if(afdelingId.equals("")) { %>selected<% } %>>Alle afdelingen...
            <% } %>
         </select>
         <select name="evenement_type" style="width:130px;">
         <mm:listnodes type="evenement_type" orderby="naam">
            <mm:field name="number" jspvar="evenement_type_number" vartype="String" write="false">
               <option value="<%= evenement_type_number %>" <%
                  if(evenement_type_number.equals(evenement_typeId)) {
                     %>selected<% }
                  %>></mm:field><mm:field name="naam" />
         </mm:listnodes>
         <option value="-1" <% if(evenement_typeId.equals("-1")) { %>selected<% } %>>Geen type
         <option value="" <% if(evenement_typeId.equals("")) { %>selected<% } %>>Alle types...
         </select>
         <select name="provincie" style="width:130px;">
         <mm:listnodes type="provincies" orderby="naam">
            <mm:field name="number" jspvar="provincie_number" vartype="String" write="false">
               <option value="<%= provincie_number %>" <%
                  if(provincie_number.equals(provincieId)) {
                     %>selected<% }
                  %>></mm:field><mm:field name="naam" />
         </mm:listnodes>
         <option value="-1" <% if(provincieId.equals("-1")) { %>selected<% } %>>Geen provincie
         <option value="" <% if(provincieId.equals("")) { %>selected<% } %>>Alle provincies...
       </select></nobr>
       <table class="formcontent" style="width:150px;"><tr><td>
          <input type="checkbox" name="no_group_events" style="width:12px;height:12px;" <% if(no_group_eventsId.equals("on")){%>checked <% } %>/>
       </td><td>Geen groepsactiviteiten</td></tr></table>
       <%
      } %>
    </td>
</tr>
<tr>
   <%
   int listSize = events.size();
   String sSoort = soortId;
   if(!sSoort.equals("activiteiten")) { sSoort = "data"; }
   // show navigation to other pages if there are more than pageSize events
   if(listSize>pageSize) {
      int lastEvent = pageSize*(thisOffset+1);
      if(lastEvent>listSize) { lastEvent = listSize; }
      %><td colspan="2">Gevonden <%= listSize %> <%= sSoort %><br> Getoond <%= pageSize*thisOffset %> - <%= lastEvent %></td>
      <td colspan="2"><%
         String sThisUrl = "evenementen.jsp?orderby=" +  orderbyId + "&direction=" + directionId + "&soort=" +  soortId + "&action=" +  actionId + searchSetting;
         if(thisOffset>0) {
           %><a href="<%= sThisUrl %>&offset=<%= thisOffset-1 %>">&nbsp;<<&nbsp;</a>  <%
         }
         for(int i=0; i < (listSize/pageSize + 1); i++) {
              if((i>0)&&((i+1)%20==1)) { %><br/><% }
              if(i==thisOffset) {
                  %><span style="color:red;">&nbsp;<%= i+1 %>&nbsp;</span>  <%
              } else {
                  %><a href="<%= sThisUrl %>&offset=<%= i %>">&nbsp;<%= i+1 %>&nbsp;</a>  <%
              }
         }
         if(thisOffset+1<(listSize/pageSize + 1)) {
           %><a href="<%= sThisUrl %>&offset=<%= thisOffset+1 %>">&nbsp;>>&nbsp;</a><%
         }
      %></td><%
   } else {
      %><td colspan="4">Gevonden <%= listSize %> <%= sSoort %> </td><%
   } %>
</tr>
</table>
<% if(actionId.indexOf("print")>-1) { %>
   <table class="formcontent" style="width:auto;">
      <tr><td class="fieldname" colspan="2">Selectie criteria</td></tr>
      <tr><td class="fieldname">Naam&nbsp;</td><td><%= titelId %></td></tr>
      <tr><td class="fieldname">Provincie&nbsp;</td><td><mm:node number="<%= provincieId %>" notfound="skipbody"><mm:field name="naam" /></mm:node></td></tr>
      <tr><td class="fieldname">Natuurgebied&nbsp;</td><td><%= natuurgebiedenId %></td></tr>

      <tr><td class="fieldname">Achternaam aanmelder&nbsp;</td></tr>
     <tr><td class="fieldname">Postcode aanmelder&nbsp;</td></tr>
        <tr><td class="fieldname">Aanmeldingsnummer&nbsp;</td></tr>

      <tr><td class="fieldname">Vanaf&nbsp;</td><td><%= beginDyId %>-<%= beginMnthId %>-<%= beginYrId %></td></tr>
      <tr><td class="fieldname">Tot en met&nbsp;</td><td><%= endDyId %>-<%= endMnthId %>-<%= endYrId %></td></tr>
      <tr><td class="fieldname">Gesorteerd op&nbsp;</td><td><%= (orderbyId.equals("titel") ? "naam van de activiteit" : "begindatum" )%></td></tr>
      <tr><td class="fieldname">Sorteer volgorde&nbsp;</td><td><%= (directionId.equals("up") ? "oplopend" : "afnemend" )%></td></tr>
   </table>
<% } %>
<!-- ******************************* TABLE HEADER ******************************** -->
<table class="formcontent" style="width:auto;"  <% if(actionId.indexOf("print")>-1) { %>border="1"<% } %>>
<%
if(actionId.indexOf("print")==-1) {
%>
   <tr>
   <!--0--><td></td>
   <!--1--><td class="fieldname" style="vertical-align:bottom;">
       <a href="evenementen.jsp?orderby=titel&soort=<%= soortId %>&direction=<%
         if(orderbyId.equals("titel")) { %><%=newDirection%><% } else { %><%=directionId%><% } %><%= searchSetting %>">
            <b>Naam</b><%
         if(orderbyId.equals("titel")) { %><img src="../img/<%= newDirection %>.gif" border='0'  align='absmiddle' alt='Keer sorteervolgorde om' /><% } %></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         </td>
   <!--2--><td class="fieldname" style="vertical-align:bottom;">
       <a href="evenementen.jsp?orderby=natuurgebieden&soort=<%= soortId %>&direction=<%
         if(orderbyId.equals("natuurgebieden")) { %><%=newDirection%><% } else { %><%=directionId%><% } %><%= searchSetting %>">
            <b>Natuurgebied</b><%
         if(orderbyId.equals("natuurgebieden")) { %><img src="../img/<%= newDirection %>.gif" border='0' align='absmiddle' alt='Keer sorteervolgorde om' /><% } %><a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

   <!--3--><td class="fieldname" style="vertical-align:bottom;"><a href="evenementen.jsp?orderby=begindatum&soort=<%= soortId %>&direction=<%
         if(orderbyId.equals("begindatum")) { %><%=newDirection%><% } else { %><%=directionId%><% } %><%= searchSetting %>">
         <b>Datum</b><%
         if(orderbyId.equals("begindatum")) { %><img src="../img/<%= newDirection %>.gif" border='0'  align='absmiddle' alt='Keer sorteervolgorde om' /><% } %></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         <a href="evenementen.jsp?orderby=<%= orderbyId %>&soort=<%= soortId %>&direction=<%= directionId
            %>&endMnth=<%= endMnthId %>&beginMnth=<%= Integer.parseInt(beginMnthId)-1 %><%= searchSettingMinMonth %>">
            <img src="../img/prev.gif" border='0'  align='absmiddle' alt='Begindatum ??n maand terug' /></a>
         <a href="evenementen.jsp?orderby=<%= orderbyId %>&soort=<%= soortId %>&direction=<%= directionId
            %>&endMnth=<%= endMnthId %>&beginMnth=<%= Integer.parseInt(beginMnthId)+1 %><%= searchSettingMinMonth %>">
            <img src="../img/next.gif" border='0' align='absmiddle' alt='Begindatum ??n maand vooruit'/></a>
         </td>
   <!--4--><td colspan="2" style="text-align:right;vertical-align:bottom;">
       <% if(!endMnthId.equals("")) { %>
          <a href="evenementen.jsp?orderby=<%= orderbyId %>&soort=<%= soortId %>&direction=<%= directionId
            %>&beginMnth=<%= beginMnthId %>&endMnth=<%= Integer.parseInt(endMnthId)-1 %><%= searchSettingMinMonth %>">
            <img src="../img/prev.gif" border='0'  align='absmiddle' alt='Eindatum ??n maand terug'/></a>
          <a href="evenementen.jsp?orderby=<%= orderbyId %>&soort=<%= soortId %>&direction=<%= directionId
            %>&beginMnth=<%= beginMnthId %>&endMnth=<%= Integer.parseInt(endMnthId)+1 %><%= searchSettingMinMonth %>">
            <img src="../img/next.gif" border='0' align='absmiddle' alt='Einddatum ??n maand vooruit'/></a>
       <% } %>
       <input type="submit" name="button" value="Zoek" style="width:40px;margin-left:20px;" onclick="javascript:showMessage(this,'Uw zoekopdracht wordt uitgevoerd. Een moment geduld a.u.b.');">
       </td>
   <!--6--><td><input type="submit" name="button" value="Wis" style="width:40px;" onclick="javascript:showMessage(this,'Het formulier wordt gewist. Een moment geduld a.u.b.');"></td>
   </tr>

   <tr>
   <!--0--><td></td>
   <!--1--><td style="padding-top:3px;"><input type="text" name="titel" tabindex="1" value="<%= titelId %>" style="width:130px;" onkeypress="return event.keyCode!=13"></td>
   <!--2--><td style="padding-top:3px;"><input type="text" name="natuurgebieden" tabindex="2" value="<%= natuurgebiedenId %>" style="width:130px;"></td>
   <!--3--><td colspan="4"><input type=hidden name="_hiddenDate" >
       <input type="text" name="beginDy" maxlength="2" tabindex="3" value="<%= beginDyId %>" style="width:40px;text-align:right;">
       -<input type="text" name="beginMnth" maxlength="2" tabindex="4" value="<%= beginMnthId %>" style="width:40px;text-align:right;">
       -<input type="text" name="beginYr" maxlength="4" tabindex="5" value="<%= beginYrId %>" style="width:40px;text-align:right;">
       <a tabindex="6" href="javascript:popUpCalendar('begin')" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0' alt='Selecteer begindatum'></a>
       <input type="text" name="endDy" maxlength="2" tabindex="7" value="<%= endDyId %>" style="width:40px;text-align:right;">
        -<input type="text" name="endMnth" maxlength="2" tabindex="8" value="<%= endMnthId %>" style="width:40px;text-align:right;">
        -<input type="text" name="endYr" maxlength="4" tabindex="9" value="<%= endYrId %>" style="width:40px;text-align:right;">
       <a tabindex="10" href="javascript:popUpCalendar('end')" ><img src='../../calendar/show-calendar-on-button.gif' width='24' height='24' align='absmiddle' border='0' alt='Selecteer einddatum'></a></td>
   </tr>

   <tr>
         <td></td>
   <!-- Aanmelder -->
      <td class="fieldname" style="vertical-align:bottom;"><b>Achternaam aanmelder</b></td>
   <!-- Postcode aanmelder -->
      <td class="fieldname" style="vertical-align:bottom;"><b>Postcode aanmelder</b></td>
   <!-- Aanmeldingsnummer -->
      <td colspan="4" class="fieldname" style="vertical-align:bottom;"><b>Aanmeldingsnummer</b></td>
   </tr>

   <tr>
         <td></td>
   <!-- Aanmelder -->
         <td style="padding-top:3px;"><input type="text" name="aanmelder_naam" tabindex=11" value="<%= aanmelderNaamId %>" style="width:130px;"></td>
   <!-- Postcode aanmelder -->
         <td style="padding-top:3px;"><input type="text" name="aanmelder_postcode" tabindex="12" value="<%= aanmelderPostcodeId %>" style="width:100px;" MAXLENGTH="6"></td>
   <!-- Aanmeldingsnummer -->
         <td colspan="4"style="padding-top:3px;"><input type="text" name="aanmelder_nummer" tabindex=13" value="<%= aanmelderNummerId %>" style="width:100px;"></td>
   
   <% if(soortId.equals("activiteiten")) out.print("<script>disableExtraSearch();</script>"); %>

   </tr>
   <tr>
         <td colspan="7">&nbsp;</td>
   </tr>

<% } else { %>
   <tr>
   <!--1--><td class="fieldname">Naam&nbsp;</td>
   <!--2--><td class="fieldname">Natuurgebied&nbsp;</td>
   <!--3--><td class="fieldname" colspan="2">Datum (dagen van toepassing)&nbsp;</td>
   <!--5--><td class="fieldname">Tijd</td>
   <!--6--><td class="fieldname">Beschikbare plaatsen</td>
   </tr>
   <tr>
   <!-- Aanmelder --><td class="fieldname">Achternaam aanmelder&nbsp;</td>
   <!-- Postcode aanmelder --><td class="fieldname">Postcode<br/>aanmelder&nbsp;</td>
   <!-- Aanmeldingsnummer --><td class="fieldname">Aanmeldings-<br/>nummer&nbsp;</td>
   </tr>

<% } %>
<!-- ******************************* TABLE BODY ******************************** -->
<%
// ** offset = thisOffset * pageSize
for(int i = 0; i< (thisOffset * pageSize); i++) {
   String thisEvent = "";
   if(directionId.equals("up")){
      thisEvent =(String) events.firstKey();
   } else {
      thisEvent =(String) events.lastKey();
   }
   events.remove(thisEvent);
}
int tL = 25;
int iEventCtr = 0;
String sCSList = "";
while(events.size()>0&&iEventCtr<pageSize) {

   String thisEvent = "";
   if(directionId.equals("up")){
      thisEvent =(String) events.firstKey();
   } else {
      thisEvent =(String) events.lastKey();
   }
   String extraInfo = "";
   %><mm:node number="<%= (String) events.get(thisEvent) %>" jspvar="thisNode"
   ><mm:field name="titel" jspvar="titel" vartype="String" write="false"
   ><mm:field name="number" jspvar="event_number" vartype="String" write="false"><%
   String parent_number = event_number;

   if (iEventCtr!=0){ sCSList += ",";  }
   sCSList += parent_number;

   boolean hasChilds = false;
   int iMaxNumber = 9999;
   %><mm:field name="soort"
      ><mm:compare value="child"
         ><mm:related path="partrel,evenement" searchdir="source"
            ><mm:field name="evenement.number" jspvar="dummy" vartype="String" write="false"><%
               parent_number = dummy;
            %></mm:field
            ><mm:field name="evenement.titel" jspvar="dummy" vartype="String" write="false"><%
               titel = dummy;
            %></mm:field
            >
            </mm:related
      ></mm:compare
      ><mm:compare value="parent"
         ><mm:related path="partrel,evenement" searchdir="destination" max="1"><%
            hasChilds = true;
         %></mm:related
      ></mm:compare
   ></mm:field>
   <mm:node number="<%= parent_number %>" jspvar="parentEvent"><%
      try { iMaxNumber = parentEvent.getIntValue("max_aantal_deelnemers"); } catch (Exception e) { }
      if(iMaxNumber==-1) iMaxNumber = 9999;
      %><mm:relatednodes type="evenement_type"
         ><mm:first><%
            extraInfo += "\nType: ";
         %></mm:first
         ><mm:first inverse="true"><%
            extraInfo += ", ";
         %></mm:first
         ><mm:field name="naam" jspvar="dummy" vartype="String" write="false"><%
            extraInfo += dummy;
         %></mm:field
      ></mm:relatednodes
      ><mm:relatednodes type="vertrekpunten"
         ><mm:first><%
            extraInfo += "\nVertrekpunten: ";
         %></mm:first
         ><mm:first inverse="true"><%
            extraInfo += ", ";
         %></mm:first
         ><mm:field name="titel" jspvar="dummy" vartype="String" write="false"><%
            extraInfo += dummy;
         %></mm:field
      ></mm:relatednodes>
   </mm:node>
   <%
   if(soortId.equals("activiteiten")) {
      ddn.setBegin(new Date(thisNode.getLongValue("embargo")*1000));
      ddn.setEnd(new Date(thisNode.getLongValue("verloopdatum")*1000));
   } else {
      ddn.setBegin(new Date(thisNode.getLongValue("begindatum") *1000));
      ddn.setEnd(new Date( thisNode.getLongValue("einddatum") *1000));
   }
   int iChildCurParticipants = -1;
   try { iChildCurParticipants = thisNode.getIntValue("cur_aantal_deelnemers"); } catch (Exception e) {}
   if(actionId.indexOf("print")==-1) { %>
   <tr>
   <!--0--><td><%
            if(isAdmin&&!hasChilds) {
               %><a href="evenementen.jsp?orderby=<%= orderbyId %>&direction=<%= directionId %>&soort=<%= soortId %>&delete=<mm:field name="number" /><%= searchSetting %>">
                  <img onclick="return doDelete('Weet u zeker dat u <%= titel.replace('"',' ').replace('\'',' ') + ", " + ddn.getReadableDate() + ", " + ddn.getReadableTime() %> wilt verwijderen?');"  onmousedown="cancelClick=true;"
                     src="../img/delete.gif" border='0' align='absmiddle' alt='Deze activiteit verwijderen'/></a><%
            }
            if(isAdmin||isChiefEditor){
               if(soortId.equals("activiteiten")) {
                  %><a href="evenementen.jsp?orderby=begindatum&direction=up&soort=<%= soortId %>&docopy=<mm:field name="number" /><%= searchSetting %>">
                     <img onclick="return doDelete('Weet u zeker dat u <%= titel.replace('"',' ').replace('\'',' ') + ", " + ddn.getReadableDate() + ", " + ddn.getReadableTime() %> wilt kopieren? Wijzig na kopieren de datum van de copy.');"  onmousedown="cancelClick=true;"
                        src="../img/copy.gif" border='0' align='absmiddle' alt='Deze activiteit kopieren'/></a>
                    <a href="#" onClick="javascript:launchCenter('download_popup.jsp?event=<%= parent_number %>&type=as', 'center', 300, 400, 'resizable=1');setTimeout('newwin.focus();',250);">
                      <img src='../img/icexcel.gif' align='absmiddle' border='0' alt='Download alle data met aanmeldingen voor deze activiteit'></a><%
               }
               %>
               <a href="javascript:void(0);" onClick="newwin = window.open('findwriters.jsp?p=<%= parent_number %>&e=<%= event_number %>','writers','width=800,height=400,scrollbars=yes,toolbar=no,location=no');setTimeout('newwin.focus();',250);"><img src="../img/people.gif" border='0'  align='absmiddle' alt='Medewerkers die aan deze activiteit hebben gewerkt.'
                  /></a>
               <%
            }
            if(!soortId.equals("activiteiten")) { %>
               <mm:field name="isoninternet"><mm:compare value="true">
                  <img src='../img/preview.gif' align='absmiddle' border='0' alt='Activiteit staat op internet'>
               </mm:compare></mm:field>
               <mm:field name="isspare"><mm:compare value="true">
                  <img src='../img/rubriek.gif' align='absmiddle' border='0' alt='Reserve datum voor activiteit'>
               </mm:compare></mm:field>
               <mm:field name="iscanceled"><mm:compare value="true">
                  <img src='../img/remove.gif' align='absmiddle' border='0' alt='Activiteit is geannuleerd'>
               </mm:compare></mm:field>
               <%
            } %>
           </td>
   <!--1--><td><%
               String openLink = "<a href=\"";
               String closeLink = "</a>";
               if(isAdmin||isChiefEditor) {
                  openLink += "EvenementInitAction.eb?number=" + parent_number + "&select=" + event_number;
               } else {
                  openLink += "#";
               }
               %><%= openLink + "\" title=\"" + titel + extraInfo + "\">" + ( titel.length()<=tL ? titel : titel.substring(0,tL)) + closeLink %><%
               if(!isAdmin&&!isChiefEditor) {
                  openLink = "";
                  closeLink = "";
               } %>
           </td>
   <!--2--><td>
               <% String natuurgebieden = "";
               %><mm:list nodes="<%= parent_number %>" path="evenement,related,natuurgebieden"
                  ><mm:field name="natuurgebieden.naam" jspvar="naam" vartype="String" write="false"><%
                     natuurgebieden += ", " + naam;
                  %></mm:field
               ></mm:list><%
               if(!natuurgebieden.equals("")) { natuurgebieden = natuurgebieden.substring(2); }
               if(!openLink.equals("")) {
                  %><%= openLink + "\" title=\"" + natuurgebieden + "\">" %><%
               }
               if(natuurgebieden.length()>tL) natuurgebieden = natuurgebieden.substring(0,tL);
               %><%= natuurgebieden + closeLink %>
           </td>
           <%
           if(!openLink.equals("")) { openLink += "\">"; } %>

   <!--3--><td><%= openLink + ddn.getReadableDate() + closeLink %></td>
   <!--4--><td><mm:field name="dagomschrijving" /></td>
   <!--5--><td><%= openLink + ddn.getReadableTime() + closeLink %></td>
   <!--6--><td>
            <% if(soortId.equals("activiteiten")&&hasChilds) {

                  String ticketIcon = "../img/ticket_act.gif";
                  String altText = "Bekijk data voor deze activiteit";

                  %><%@include file="event_parent_status.jsp" %><%

                 %><a href="SubscribeInitAction.eb?number=<%= event_number %>"><img src='<%= ticketIcon %>' align='absmiddle' border='0' alt='<%= altText %>'></a>
            <% } else { %>
                  <%= iMaxNumber - iChildCurParticipants %>
                  <jsp:include page="subscribelink.jsp">
                     <jsp:param name="p" value="<%= parent_number %>" />
                     <jsp:param name="e" value="<%= event_number %>" />
                  </jsp:include>
            <% } %>
           </td>
       </tr><%
   } else {
      %><tr>
   <!--1--><td><%= titel %></td>
   <!--2--><td><% String natuurgebieden = "";
               %><mm:list nodes="<%= parent_number %>" path="evenement,related,natuurgebieden"
                  ><mm:field name="natuurgebieden.naam" jspvar="naam" vartype="String" write="false"><%
                     natuurgebieden += ", " + naam;
                  %></mm:field
               ></mm:list><%
               if(!natuurgebieden.equals("")) { natuurgebieden = natuurgebieden.substring(2); }
               %><%= natuurgebieden %>
           </td>
   <!--3--><td><%= ddn.getReadableDate() %></td>
   <!--4--><td><mm:field name="dagomschrijving" /></td>
   <!--5--><td><%= ddn.getReadableTime() %></td>
   <!--6--><td style="text-align:center;">
            <% if(!soortId.equals("activiteiten")){
                  %><%= iMaxNumber - iChildCurParticipants %><%
            } %>
           </td>
        </tr><%
   }
   iEventCtr++;
   %></mm:field
   ></mm:field
   ></mm:node><%
   events.remove(thisEvent);
}
session.setAttribute("sCSList",sCSList);
if(iEventCtr==0) {
   %>
   <tr>
      <td colspan="7" style="padding-top:50px;padding-left:100px;">
      <b>Er zijn geen activiteiten gevonden, die voldoen aan uw zoekopdracht.</b><br/><br/>
      Klik op de "Wis" knop om terug te gaan naar de standaard zoekinstellingen.
      </td>
   </tr>
   <%
} %>
</table>
</form>
<script type="text/javascript" language="JavaScript">
  // works together with the the onkeypress for the titel field, to make sure the form is only posted once on [ENTER]-key
  var focusControl = document.forms["EvenementForm"].elements["titel"];
  if (focusControl.type != "hidden") {
     focusControl.focus();
  }
</script>
<br/><br/>
</body>
</html>
</mm:cloud>
</mm:content>