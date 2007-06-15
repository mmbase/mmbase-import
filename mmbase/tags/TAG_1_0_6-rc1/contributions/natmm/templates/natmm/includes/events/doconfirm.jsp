<%@include file="/taglibs.jsp" %>
<%@page import = "nl.leocms.evenementen.forms.*,org.mmbase.bridge.Node" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<% 
String confirmID = request.getParameter("s");
boolean subscriptionConfirmed = false;
String datum_inschrijvingID = "";
String inschrijvings_nummerID = "";

if(confirmID!=null){
   int usPos = confirmID.indexOf("_");
   if(usPos>-1) {
      datum_inschrijvingID = confirmID.substring(0,usPos);
      inschrijvings_nummerID = confirmID.substring(usPos+1);
   }
   %>
   <mm:node number="<%= inschrijvings_nummerID %>" notfound="skipbody" id="subscription" jspvar="thisSubscription">
      <mm:field name="datum_inschrijving" jspvar="dummy" vartype="String" write="false"><%
         if(datum_inschrijvingID.equals(dummy)) { 
            SubscribeAction.sendConfirmEmail(cloud, inschrijvings_nummerID); 
            %><mm:related path="related,inschrijvings_status">
               <mm:deletenode element="related" />
            </mm:related>
            <mm:node number="confirmed" id="confirmed_status" />
            <mm:createrelation source="subscription" destination="confirmed_status" role="related" /><% 
            subscriptionConfirmed = true;
        } %>
      </mm:field>   
   </mm:node><%
}
%>

<body style="overflow:auto;">
   <span class="colortitle">Bevestiging van uw aanmelding.</span><br/><br/>
   <% if(subscriptionConfirmed) { %>
      Uw aanmelding is bevestigd. U ontvangt hiervan een afschrift per email.
   <% } else { %>
      Deze link bevat geen geldig inschrijvingsnummer. Neem contact op met op met de ledenservice (035) 655 99 55 of desbetreffende bezoekerscentrum om uw aanmelding te bevestigen.
   <% } %>
<br/>
<br/>
<br/>
</body>
</html>
</mm:cloud>
