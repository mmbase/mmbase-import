<%
String paymentTypeID = "";
%><mm:list nodes="<%= paginaID %>" path="pagina,posrel,payment_type" max="1"
   ><mm:field name="payment_type.number" jspvar="payment_type_number" vartype="String" write="false"><% 
      paymentTypeID = payment_type_number; 
   %></mm:field><%
%></mm:list><%

// ----------------- This is a list of events ----------------------

String sFromDate = "" + iFromDay + "-" + iFromMonth + "-" + iFromYear;
String sTillDate = "" + iTillDay + "-" + iTillMonth + "-" + iTillYear;

SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
Date dateSearchFrom = df.parse(sFromDate);
Date dateSearchTill = df.parse(sTillDate);

long lDateSearchFrom = dateSearchFrom.getTime() / 1000;
long lDateSearchTill = dateSearchTill.getTime() / 1000; 

// *** first: try to find all parents that meet the search criteria
// Is provincie criterion set?
String sParentConstraint = "";
if(!provID.equals("-1")) {
   sParentConstraint += "lokatie LIKE '%," + provID + ",%'";
}
String thisEvents = "";
HashSet parentEvents = null;
if(application.getAttribute("events_till")==null ){
     EventNotifier.updateAppAttributes(cloud);
}   
if(application.getAttribute("events_till")!=null
   && lDateSearchTill<= ((Long) application.getAttribute("events_till")).longValue()) {
   
   parentEvents = (HashSet) application.getAttribute("events");
   if(parentEvents!=null) {
      thisEvents = parentEvents.toString();
      thisEvents = thisEvents.substring(1,thisEvents.length()-1);
   }
}
parentEvents = new HashSet();
%>
<%-- added evenement_type enforcement by a path change. from "evenement" to "evenement,related,evenement_type" --%>
<mm:list nodes="<%= thisEvents %>" path="evenement,related,evenement_type" fields="evenement.number" constraints="<%= sParentConstraint %>"
   ><mm:field name="evenement.number" jspvar="parent_number" vartype="String" write="false"><% 
      boolean parentBelongsToActivityType = true;
      if(!sActivityTypes.equals("")) { // *** only use constraint for events that do have an activity type
         %><mm:list nodes="<%= parent_number %>" path="evenement,related,evenement_type" constraints="evenement_type.isoninternet='1'" max="1"><%
            parentBelongsToActivityType = false;
         %></mm:list
         ><mm:list nodes="<%= parent_number %>" path="evenement,related,evenement_type" constraints="<%= sActivityTypes %>" max="1"><%
            parentBelongsToActivityType = true; 
         %></mm:list><%
      }
      boolean parentBelongsToNatuurgebied = true;
      if(!natuurgebiedID.equals("-1")) {
         parentBelongsToNatuurgebied = false;
         %><mm:list nodes="<%= natuurgebiedID %>" path="natuurgebieden,related,evenement"
             constraints="<%= "evenement.number='" + parent_number + "'" %>" max="1"><%
            parentBelongsToNatuurgebied = true; 
         %></mm:list><%
      } 
		if (!paymentTypeID.equals("")) {
         %><mm:field name="evenement.aanmelden_vooraf" jspvar="aanmeldenVooraf" vartype="String" write="false"><% 
         if(parentBelongsToActivityType
               &&parentBelongsToNatuurgebied
               &&aanmeldenVooraf.equals("1")
               &&!Evenement.exceedsMaxPrice(cloud,parent_number,paymentTypeID)) {
            parentEvents.add(parent_number);
         } 
         %></mm:field><%	
      } else {
	     if(parentBelongsToActivityType&&parentBelongsToNatuurgebied) {
           parentEvents.add(parent_number);
        }
	   } %>
	</mm:field
></mm:list><%

thisEvents = parentEvents.toString();
thisEvents = thisEvents.substring(1,thisEvents.length()-1);
String sChildConstraints = Evenement.getEventsConstraint(lDateSearchFrom,lDateSearchTill);
if(!paymentTypeID.equals("")) {
   sChildConstraints = Evenement.getBookableEventsConstraint(lDateSearchFrom,lDateSearchTill);
}
TreeMap events = new TreeMap();
if(!thisEvents.equals("")) {
   %><mm:list nodes="<%= thisEvents %>" path="evenement1,partrel,evenement" searchdir="destination"
      fields="evenement.number" constraints="<%= sChildConstraints %>"
      ><mm:node element="evenement" jspvar="evenement"><%
         if(!events.containsValue(evenement.getStringValue("number")) ) {
            long eventBeginDate = evenement.getLongValue("begindatum");
            while(events.containsKey(new Long(eventBeginDate))) {
               eventBeginDate++;	
            }
            events.put(new Long(eventBeginDate),evenement.getStringValue("number"));
         }
      %></mm:node
   ></mm:list
   ><mm:list nodes="<%= thisEvents %>" path="evenement"
      fields="evenement.number" constraints="<%= sChildConstraints %>"
      ><mm:node element="evenement" jspvar="evenement"><% 
         if(!events.containsValue(evenement.getStringValue("number")) ) {
            long eventBeginDate = evenement.getLongValue("begindatum");
            while(events.containsKey(new Long(eventBeginDate))) {
               eventBeginDate++;	
            }
            events.put(new Long(eventBeginDate),evenement.getStringValue("number"));
         }
      %></mm:node
   ></mm:list><%
}
%>
<%--
<table>
   <tr><td>Type</td><td><%= sActivityTypes %></td></tr>
   <tr><td>Provence</td><td><%= sParentConstraint %></td></tr>
   <tr><td>Natuurgebied</td><td><%= natuurgebiedID %></td></tr>
   <tr><td>Time</td><td><%= sChildConstraints %></td></tr>
   <tr><td>Events</td><td><%= events %></td></tr>
</table>
--%>
<%
int listSize = events.size();
int pageSize = 50;
int thisOffset = 0;
try{
   if(!offsetID.equals("")){
     thisOffset = Integer.parseInt(offsetID);
     offsetID ="";
   }
} catch(Exception e) {} 
// show navigation to other pages if there are more than pageSize events
%>
<table width="354" border="0" cellpadding="0" cellspacing="0">
   <tr><td colspan="2"><%
      if(listSize>pageSize) {
         int firstEvent = pageSize*thisOffset+1;
         int lastEvent = pageSize*(thisOffset+1);
         if(lastEvent>listSize) { lastEvent = listSize; }
         %>Gevonden <%= listSize %> activiteiten; getoond <%= firstEvent %><% if(firstEvent!=lastEvent) { %> - <%= lastEvent %><% } %><br/>
         <%@include file="offsetlinks.jsp" %><% 
      } else if(listSize==0) {
         %>Er zijn geen activiteiten gevonden, die voldoen aan uw zoekopdracht.<%
      } else {
         %>Gevonden <%= listSize %> activiteiten<%
      } %>
   </td></tr>
   <tr><td colspan="2"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
   <% // ** offset = thisOffset * pageSize
   for(int i = 0; i< (thisOffset * pageSize); i++) {
      events.remove(events.firstKey());
   }
   int iEventCtr = 0;
   while(events.size()>0&&iEventCtr<pageSize) {
   
      Long thisEvent = (Long) events.firstKey();
      String event_number = (String) events.get(thisEvent);
      String parent_number = Evenement.findParentNumber(event_number);
      %><mm:node number="<%= event_number %>" jspvar="evenement"><% 
         DoubleDateNode ddn = new DoubleDateNode(evenement);
         ddn.clipBeginOnToday();
         %>
         <tr>
            <td style="vertical-align:top;width:50%;padding-right:3px;">
              <%= ddn.getReadableDate(" | ") %>&nbsp;|&nbsp;<%= ddn.getReadableStartTime() %>
              <br/>
              <mm:list nodes="<%= parent_number %>" path="evenement,related,evenement_type"
                     constraints="evenement_type.isoninternet='1'">
                  <mm:field name="evenement_type.naam" />
                  <br/>
              </mm:list>
            </td>
            <td style="vertical-align:top;width:50%;">
              <mm:remove referid="imageused" 
              /><mm:list nodes="<%= parent_number %>" path="evenement,related,evenement_type,posrel,images" max="1"
                  ><table style="margin:0px;padding:0px;"><tr>
                     <td style="margin:0px;padding:0px;"><img src="<mm:node element="images"><mm:image template="s(37)" /></mm:node
                        >" alt="<mm:field name="evenement_type.naam"
                        />">&nbsp;</td>
                     <td style="margin:0px;padding:0px;vertical-align:top;"><mm:import id="imageused"
              /></mm:list>
              <mm:field name="titel" jspvar="title" vartype="String" write="false">   
               <a href="events.jsp?p=<%= paginaID 
                     %>&e=<%=event_number%>&<%= searchParams %>" class="maincolor_link"><%=HtmlCleaner.insertShy(title,30)%></a>
              </mm:field>
              <br/>
              <mm:node number="<%= parent_number %>" jspvar="parentEvent">
                  <mm:related path="related,natuurgebieden,posrel,provincies" fields="provincies.naam" distinct="true">
                    <mm:first inverse="true">, </mm:first>
                    <mm:field name="provincies.naam" />
                  </mm:related>
                  <br/>
                  <% if(nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,evenement)) { 
                        %><b>Volgeboekt</b><% 
                     } else if(nl.leocms.evenementen.Evenement.subscriptionClosed(parentEvent,evenement)) {
                        %><b>Aanmelding is gesloten (*)</b><%
                        containsSubscriptionClosed = true;
                     }
                     if(evenement.getStringValue("iscanceled").equals("true")) {
                        %><b>Helaas kan deze activiteit niet doorgaan.</b><% 
                     } %>
              </mm:node>
              <mm:present referid="imageused"></td></tr></table></mm:present>
            </td>
         </tr>
         <tr><td colspan="2"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
      </mm:node><%
      iEventCtr++;
      events.remove(thisEvent);
   }
   if(listSize>pageSize) {
      %><tr><td colspan="2"><%@include file="offsetlinks.jsp" %></td></tr><% 
   } %>   
</table>
<br/>