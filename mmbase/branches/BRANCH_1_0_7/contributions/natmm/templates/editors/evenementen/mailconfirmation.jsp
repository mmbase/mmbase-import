<%@page import="java.util.*,nl.leocms.util.DoubleDateNode,
   nl.leocms.evenementen.forms.*" %>
<%@include file="/taglibs.jsp" %>

<mm:cloud jspvar="cloud" method="http" rank="basic user">

<mm:import externid="selectedParticipant" jspvar="thisSelectedParticipant">-1</mm:import>
<mm:import externid="subscriptionNumber" jspvar="thisSubscriptionNumber">-1</mm:import>
<mm:import externid="pressedConfirmed" jspvar="thisPressedConfirmed">false</mm:import>

<bean:define id="nodenr" property="node" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="lastSentMessage" property="lastSentMessage" name="SubscribeForm" scope="session" type="java.lang.String"/>

<mm:node number="<%= nodenr %>" jspvar="thisEvent" notfound="skipbody">
<%
   DoubleDateNode ddn = new DoubleDateNode(); 
   ddn.setBegin(new Date(thisEvent.getLongValue("begindatum")*1000));
   ddn.setEnd(new Date(thisEvent.getLongValue("einddatum")*1000)); 
%>

<html>
<head>
   <title>Bevestig aanmelding</title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
   <div align="right"><a href="#" onClick="window.close()"><img src='../img/close.gif' align='absmiddle' border='0' alt='Sluit dit venster'></a></div>

   <% if ("false".equals(thisPressedConfirmed)) { %>
      
      De aanmelding is bevestigd! De onderstaande tekst is opgenomen in de bevestgings mail:
      </br></br>
      <div width="450" height="300" style="overflow: auto; background-color: white;"> 
         <%=  lastSentMessage %>
      </div>  
      </br></br>
      
      <input type="button" name="cancel" value="Sluiten" onClick="opener.location.reload();self.close();" style="width:150px;text-align:center;" />     
   
   <% } else { %>
   
      <h4>Bevestig aanmelding</h4>
      
      Bevestig hier de aanmelding en voeg eventueel extra tekst toe aan de bevestigings mail:
      </br></br>
      
      <mm:node number="<%= thisSelectedParticipant %>" jspvar="thisParticipant">
      <%
         String thisParticipantName =  thisParticipant.getStringValue("firstname")
         + (thisParticipant.getStringValue("initials").equals("") ? "" : " " +  thisParticipant.getStringValue("initials"))
         + (thisParticipant.getStringValue("suffix").equals("") ? "" : " " +  thisParticipant.getStringValue("suffix"))
         + (thisParticipant.getStringValue("lastname").equals("") ? "" : " " +  thisParticipant.getStringValue("lastname"));
      %>
      <b><%= thisParticipantName %> (<%=thisParticipant.getStringValue("email")%>)</b></br>    
      </mm:node>
      
      <b>"<mm:field name="titel" />", <%= ddn.getReadableDate() %>, <%= ddn.getReadableTime() %></b></br></br>
      
      <table class="formcontent">      
      
         <html:form action="/editors/evenementen/SubscribeAction" scope="session">
            <input type="hidden" name="selectedParticipant" value="<%=thisSelectedParticipant %>" />
            <input type="hidden" name="subscriptionNumber" value="<%=thisSubscriptionNumber %>" />   
      
            <tr>
               <td class="fieldname">Extra tekst:</td>
               <td><textarea cols="60" rows="10" name="extraText" value="" style="width:400px;"></textarea><br/><br/></td>
               
            </tr>
            <tr>
               <td colspan="2">
                  <input type="submit" name="action" value="<%= SubscribeForm.CONFIRM_ACTION %>" style="width:150px;text-align:center;" />
                  <input type="button" name="cancel" value="Annuleren" onClick="window.close()" style="width:150px;text-align:center;" />
               </td>
            </tr>
         </html:form>
      </table>      
    
   <% } %>

</body>
</html>

</mm:node>
</mm:cloud>
