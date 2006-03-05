<%
// field mailstatus of email
// -1 = unknown
// 0  = waiting
// 1  = delivered
// 2  = failed
// 3  = spam filter hit, not mailed
// 4  = queued
%><%@page import="java.util.*" 
%><%@include file="/taglibs.jsp" 
%><mm:import externid="s" jspvar="snumber" id="snumber">-1</mm:import
><mm:import externid="d" jspvar="dnumber" id="dnumber">-1</mm:import><%

if(snumber.indexOf("-1")==-1) {

   String emailIcon = "/editors/img/confirmdo.gif";
   String altText = "Verstuur bevestigings email";
   int iConfirmNumber;
   
   %><mm:cloud method="http" rank="basic user" jspvar="cloud"
      ><mm:node number="$snumber" jspvar="thisSubscription"
      ><mm:relatednodes type="email" orderby="mailedtime" directions="down" jspvar="lastEmail" max="1"><%
         Date dd = new Date(lastEmail.getLongValue("mailedtime")*1000);
         String status = lastEmail.getStringValue("mailstatus");
         if(status.equals("-1")) { status = "onbekend"; emailIcon = "/editors/img/confirmquestion.gif"; } 
         if(status.equals("0")) { status = "in de wachtrij"; emailIcon = "/editors/img/confirmwait.gif"; } 
         if(status.equals("1")) { status = "verstuurd"; emailIcon = "/editors/img/confirmdone.gif"; } 
         if(status.equals("2")) { status = "kan niet worden verstuurd, controleer het emailadres"; emailIcon = "/editors/img/confirmfail.gif"; } 
         if(status.equals("3")) { status = "geblokkeerd door een spamfilter"; emailIcon = "/editors/img/confirmblocked.gif"; } 
         if(status.equals("4")) { status = "in de wachtrij"; emailIcon = "/editors/img/confirmwait.gif"; } 
                  
         altText = "LAATST VERSTUURDE BEVESTIGINGSEMAIL"
            + "\nAan: " + lastEmail.getStringValue("to")
            + "\nGepost op: " + dd
            + "\nStatus: " + status;
       
      %></mm:relatednodes
      ></mm:node
   ></mm:cloud
   
   ><html:image src="<%= emailIcon %>" style="width:16px;" property="buttons.confirmSubscription" alt="<%= altText %>" onclick="<%= "document.forms[0].selectedParticipant.value='" + dnumber + "';document.forms[0].subscriptionNumber.value='" + snumber + "';return doDelete('Weet u zeker dat u een bevestigings email wilt versturen voor aanmelding " + snumber + "?');" %>"  /><%
} %>
                     