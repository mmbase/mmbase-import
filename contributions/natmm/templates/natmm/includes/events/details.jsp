<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/getstyle.jsp" %>
<%@ page import = "java.util.Date,nl.leocms.util.DoubleDateNode,nl.leocms.evenementen.forms.*,nl.leocms.evenementen.Evenement" %>
<%@include file="../../includes/time.jsp" %>
<%@include file="dateutil.jsp" %>
<mm:cloud jspvar="cloud">
<%
String templateID = request.getParameter("template");

nowSec = nowSec-24*60*60;

String parentID = Evenement.findParentNumber(evenementID);

// ----------------- These are the details of the event ----------------------

%><mm:node number="<%=parentID%>" jspvar="parentEvent">
   <jsp:include page="../artikel_1_column.jsp">
      <jsp:param name="o" value="<%= parentID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
   </jsp:include>
   <mm:node number="<%= evenementID %>" jspvar="thisEvent">
      <% DoubleDateNode ddn = new DoubleDateNode(thisEvent); %>
      <div style="margin:5px 0px 5px 0px"><strong>Wanneer |</strong>
         <%= ddn.getReadableDate(", ")
         %><mm:field name="dagomschrijving"><mm:isnotempty> op <mm:write/></mm:isnotempty></mm:field
         > om <%= ddn.getReadableTime()
         %><% if(nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,thisEvent)) { %>, <strong>volgeboekt</strong><% }
         %><% if(thisEvent.getStringValue("iscanceled").equals("true")) { %>, <strong>geannuleerd</strong><% } %>
      </div>
   </mm:node>
   <mm:related path="related,evenement_type" fields="evenement_type.naam" >
      <mm:first><div style="margin:5px 0px 5px 0px"><strong>Activiteitstype |</strong></mm:first>
      <mm:field name="evenement_type.naam" />
      <mm:last inverse="true">/</mm:last>
      <mm:last></div></mm:last>
   </mm:related>
   <mm:related path="related,natuurgebieden" fields="natuurgebieden.naam,natuurgebieden.number" >
      <mm:first><div style="margin:0px 0px 5px 0px"><strong>Natuurgebied | </strong></mm:first>
      <a href="natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" />"><mm:field name="natuurgebieden.naam" /></a>
      <mm:last inverse="true">/</mm:last>
      <mm:last></div></mm:last>
   </mm:related>
   <mm:related path="related,natuurgebieden,posrel,provincies" fields="provincies.naam" distinct="true">
      <mm:first><div style="margin:0px 0px 5px 0px"><strong>Provincie | </strong></mm:first>
      <mm:field name="provincies.naam" />
      <mm:last inverse="true">/</mm:last>
      <mm:last></div></mm:last>
   </mm:related>
   <mm:related path="readmore,extra_info" fields="extra_info.omschrijving" orderby="extra_info.omschrijving">
      <mm:first>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" >
         <tr>
            <td style="vertical-align:top;"><strong>Let&nbsp;op&nbsp;|&nbsp;</strong></td>
            <td>
      </mm:first>
               <mm:field name="extra_info.omschrijving">
                  <mm:isnotempty><mm:write /><br/></mm:isnotempty>
               </mm:field>
      <mm:last>
            </td>
         </tr>
      </table>
      <table class="dotline"><tr><td height="3"></td></tr></table>
      </mm:last>
   </mm:related>
   <%
     boolean bTableFinished = true;
   %>
   <mm:related path="posrel,deelnemers_categorie" orderby="deelnemers_categorie.naam">
      <mm:first>
         <div style="margin:0px 0px 5px 0px"><span class="colortitle">Kosten</span>
         <table width="100%" border="0" cellpadding="0" cellspacing="0">
      </mm:first>
      <mm:odd>
        <tr>
           <td>
              <mm:field name="deelnemers_categorie.naam" />
              <mm:field name="posrel.pos" jspvar="sPrice" vartype="String" write="false">
                 <%= SubscribeAction.priceFormating(sPrice)%>
              </mm:field>
              <%
                 bTableFinished = false;
              %>
           </td>
      </mm:odd>
      <mm:even>
           <td>
              <mm:field name="deelnemers_categorie.naam" />
              <mm:field name="posrel.pos" jspvar="sPrice" vartype="String" write="false">
                 <%= SubscribeAction.priceFormating(sPrice)%>
              </mm:field>
              <%
                 bTableFinished = true;
              %>
           </td>
        </tr>
      </mm:even>
      <mm:last>
        <%
           if(!bTableFinished)
           {
              %>
                    <td>&nbsp;</td>
                 </tr>
              <%
           }
         %>
         </table>
         </div>
         <br/>
      </mm:last>
   </mm:related>
   <mm:related path="posrel,vertrekpunten" fields="vertrekpunten.titel" max="1">
      <div style="margin:0px 0px 5px 0px"><span class="colortitle">Vertrekpunt |</span>
      <mm:field name="vertrekpunten.titel" />
      <mm:field name="vertrekpunten.tekst" />
      </div>
      <br/>
   </mm:related>
   </div>
   <mm:remove referid="afdfound" />
   <mm:related path="readmore,afdelingen" constraints="readmore.readmore='2'">
      <mm:first><div style="margin:0px 0px 5px 0px"><span class="colortitle">Aanmelden | </span></mm:first>
      <mm:first inverse="true"><br/><br/></mm:first>
      <mm:field name="afdelingen.naam"
         /><mm:field name="afdelingen.telefoonnummer"><mm:isnotempty>, <nobr><mm:write /></nobr></mm:isnotempty></mm:field>
         <mm:field name="afdelingen.omschrijving"><mm:isnotempty><br/><mm:write /></mm:isnotempty></mm:field>
      <mm:last></div><mm:import id="afdfound" /></mm:last>
   </mm:related>

   <mm:node number="<%= evenementID %>" jspvar="thisEvent"><%
      boolean isBookableEvent =
         parentEvent.getStringValue("aanmelden_vooraf").equals("1")
         && (thisEvent.getLongValue("einddatum") * 1000 > (new Date()).getTime())
         && thisEvent.getStringValue("isspare").equals("false")
         && thisEvent.getStringValue("iscanceled").equals("false")
         && !nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,thisEvent)
         && !nl.leocms.evenementen.Evenement.subscriptionClosed(parentEvent,thisEvent);

      if(isBookableEvent){
         boolean isAuthenticated = true;
         if(NatMMConfig.hasClosedUserGroup) {
            %>
            <%@include file="/editors/mailer/util/memberid_get.jsp" %>
            <%
            isAuthenticated = nl.leocms.evenementen.Evenement.isAuthenticated(parentEvent, memberid);
         }
         if(isAuthenticated){
            %><div style="margin:0px 0px 5px 0px"><span class="colortitle">Aanmelden via internet | </span><a href="SubscribeInitAction.eb?number=<%= evenementID %>&p=<mm:write referid="p"/>">klik hier</a></div><%
         }
         else{
            %><div style="margin:0px 0px 5px 0px"><span class="colortitle">Dit evenement is alleen toegankelijk voor leden | </span><a href="/lidworden">klik hier om lid te worden</a></div><%
         }
      }
   %>

   </mm:node>


   <br/>
   <%
    DoubleDateNode parentDDN = new DoubleDateNode(parentEvent);
    boolean bFirst = true;
    // *** the following code does only provide a table with dates when there is at least one child event
   %>
   <mm:relatednodescontainer type="evenement">
      <mm:constraint field="isspare" value="false" />
      <mm:constraint field="isoninternet" value="true" />
      <mm:relatednodes jspvar="childEvent" orderby="begindatum">
      <mm:first>
         <div style="margin:0px 0px 5px 0px"><span class="colortitle">Alle data</span>
         <br/>
         <table width="auto" border="0" cellpadding="0" cellspacing="0">
      </mm:first>
      <%
        DoubleDateNode childDDN = new DoubleDateNode(childEvent);

        // Show parent date
        if((parentDDN.compareTo(childDDN)<0) && bFirst) {
           %>
           <%= CalculateTimeTableDate(parentID,evenementID, parentDDN,
                  parentEvent.getStringValue("dagomschrijving"),
                  nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,parentEvent),
                  parentEvent.getStringValue("iscanceled").equals("true"),
                  nowSec) %>

           <%
           bFirst = false;
        }
       %>
       <%=CalculateTimeTableDate(childEvent.getStringValue("number"),evenementID, childDDN,
               childEvent.getStringValue("dagomschrijving"),
               nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,childEvent),
               childEvent.getStringValue("iscanceled").equals("true"),
               nowSec) %>
      <mm:last>
         <%
            if(bFirst) {
               %>
               <%= CalculateTimeTableDate(parentID, evenementID, parentDDN,
                        parentEvent.getStringValue("dagomschrijving"),
                        nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,parentEvent),
                        parentEvent.getStringValue("iscanceled").equals("true"),
                        nowSec) %>
               <%
               bFirst = false;
            }
         %>
         </table>
         </div>
      </mm:last>
      </mm:relatednodes>
   </mm:relatednodescontainer>
</mm:node>
<br/>
</mm:cloud>
