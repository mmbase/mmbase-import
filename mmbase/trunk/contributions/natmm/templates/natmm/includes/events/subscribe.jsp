<%@page import="nl.leocms.evenementen.forms.*,nl.leocms.util.DoubleDateNode,java.util.*,org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%
String sRubriekLayout = request.getParameter("rl");

String dateStyle = "width:40px;text-align:right;"; 
String buttonStyle = "width:90px;";
String fNStyle = "width:150px;vertical-align:top;";

DoubleDateNode nodeDDN = new DoubleDateNode();

String localPath = request.getServletPath(); // localPath will always start with a forwardslash
localPath = localPath.substring(0,localPath.lastIndexOf("/"));

int iCurPart = 0;
int ti = 1;
%>
<mm:import externid="p" jspvar="paginaID" vartype="String"/>
<mm:import externid="sid" jspvar="subsiteID" vartype="String"/>
<%
boolean isDemoLayout = sRubriekLayout.equals("" + NatMMConfig.DEMO_LAYOUT);
boolean isEventTemplate = true;
%>
<mm:node number="<%= paginaID %>">
   <mm:related path="contentrel,evenement">
      <% isEventTemplate = false; %>
   </mm:related>
</mm:node>
<html:form action="<%= localPath +  "/SubscribeAction" %>" scope="session">
<html:hidden property="ticketOffice" value="website" />

<mm:node number="<%= subsiteID %>">
   <mm:field name="naam" jspvar="p_naam" vartype="String" >
      <html:hidden property="ticketOfficeSource" value="<%=p_naam%>" />
   </mm:field>
</mm:node>

<html:hidden property="selectedParticipant" />
<html:hidden property="subscriptionNumber" />
<html:hidden property="pageNumber" value="<%= paginaID %>" />
<bean:define id="actionId" property="action" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="nodenr" property="node" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="parent_number" property="parent" name="SubscribeForm" scope="session" type="java.lang.String"/>
<bean:define id="parent_number" property="parent" name="SubscribeForm" scope="session" type="java.lang.String"/>
<mm:node number="<%= nodenr %>" jspvar="thisEvent">
   <% nodeDDN.setBegin(new Date(thisEvent.getLongValue("begindatum")*1000));
      nodeDDN.setEnd(new Date(thisEvent.getLongValue("einddatum")*1000));
      iCurPart = Integer.parseInt(thisEvent.getStringValue("cur_aantal_deelnemers"));
   %>
</mm:node>
<mm:node number="<%= parent_number %>" jspvar="parentEvent">
   <table style="width:559px;margin-bottom:10px;" border="0">
      <tr>
         <td colspan="2" style="width:80%;"><span class="colortitle">Aanmelden voor: <mm:field name="titel" /></span></td>
         <td style="text-align:right;padding-right:19px;"><% if(isEventTemplate) { %><html:submit property="action" value="<%= SubscribeForm.TO_AGENDA_ACTION %>" styleClass="submit_image" style="width:150px;" /><% } %></td>
      </tr>
      <% 
      if(isEventTemplate) { 
         %>
         <tr>
            <td style="<%= fNStyle %>"><strong>Wanneer</strong></td>
            <%
            // determine iEventFound, only show select box if actionId.equals("select_date")
            int iEventFound = 0;
            DoubleDateNode parentDDN = new DoubleDateNode(parentEvent);
            boolean  parentIsBookable =
               !(parentEvent.getLongValue("einddatum") * 1000 < (new Date()).getTime())
               && parentEvent.getStringValue("isspare").equals("false")
               && parentEvent.getStringValue("isoninternet").equals("true")
               && parentEvent.getStringValue("iscanceled").equals("false")
               && !nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,parentEvent);
            if(actionId.equals(SubscribeForm.SELECT_DATE_ACTION)) { 
               %><html:hidden property="action" value="<%= SubscribeForm.FIX_DATE_ACTION %>" /><% } 
            %>
            <mm:relatednodescontainer type="evenement">
               <mm:constraint field="isspare" value="false" />
               <mm:constraint field="isoninternet" value="true" />
               <mm:constraint field="iscanceled" value="false" />
               <mm:relatednodes jspvar="childEvent" orderby="begindatum">
                  <%
                  if(!nl.leocms.evenementen.Evenement.isFullyBooked(parentEvent,childEvent)
                     && (childEvent.getLongValue("einddatum") * 1000 > (new Date()).getTime()) ) { 

                     if(actionId.equals(SubscribeForm.SELECT_DATE_ACTION)) { 
                        DoubleDateNode childDDN = new DoubleDateNode(childEvent);   
                        String child_number = childEvent.getStringValue("number");
                        if(iEventFound==0) { 
                           %><td>
                              <select name="node" style="font-size:11px;" tabindex="<%= ti %>" onChange="javascript:form.submit();"><% 
                           ti++;
                        }
                        if(actionId.equals(SubscribeForm.SELECT_DATE_ACTION)) { // show the child date, proceeded by the parent date if necessary
                           if(parentIsBookable && (parentDDN.compareTo(childDDN)<0) && (iEventFound==0)) {
                             %><option value="<%= parent_number %>" <%= (parent_number.equals(nodenr) ? "SELECTED" : "") %>>
                                 <%= parentDDN.getReadableDate(" | ") + " | " + parentDDN.getReadableTime() %></option><%
                           }
                           %><option value="<%= child_number %>" <%= (child_number.equals(nodenr) ? "SELECTED" : "") %>>
                                <%= childDDN.getReadableDate(" | ") + " | " + childDDN.getReadableTime() %></option><%
                        }
                     }
                     iEventFound++;
                  } %>
               </mm:relatednodes>
            </mm:relatednodescontainer>
            <%
            if(parentIsBookable && (iEventFound==0)) {
               if(actionId.equals(SubscribeForm.SELECT_DATE_ACTION)) { 
                  if(iEventFound==0) { 
                     %><td>
                        <select name="node" style="font-size:11px;" tabindex="<%= ti %>" onChange="javascript:form.submit();"><% 
                     ti++;
                  }  
                  %><option value="<%= parent_number %>" <%= (parent_number.equals(nodenr) ? "SELECTED" : "") %>>
                    <%= parentDDN.getReadableDate(" | ") + " | " + parentDDN.getReadableTime() %></option><%
               }
               iEventFound++;
            }
            if(!(iEventFound>0)&&actionId.equals(SubscribeForm.SELECT_DATE_ACTION)) { 
               %></select>
               </td><%
            }
            if(!actionId.equals(SubscribeForm.SELECT_DATE_ACTION)) { 
               %><td><%= nodeDDN.getReadableDate() %>, <%= nodeDDN.getReadableTime() %></td><%
            } %>
            <td>
               <% if(iEventFound>1) { %>
                  <html:submit property="action" value="<%= SubscribeForm.OTHER_DATES_ACTION %>" style="text-decoration:underline;color:#D71920;width:100px;line-height:0.80em;height:16px;padding:2px;border:0;background-color:#FFFFFF;" />
               <% } %>
            </td>
         </tr>
         <tr><td style="<%= fNStyle %>"><strong>Type activiteit</strong></td>
            <td colspan="2"><mm:relatednodes type="evenement_type"
               ><mm:first inverse="true">, </mm:first
                  ><mm:field name="naam" jspvar="evenementTypeName" vartype="String"
               ></mm:field
            ></mm:relatednodes>
            </td></tr>
         <mm:relatednodes type="natuurgebieden">
            <mm:first><tr><td style="<%= fNStyle %>"><strong>Natuurgebied</strong></td><td colspan="2"></mm:first>
            <mm:first inverse="true">, </mm:first>
            <mm:field name="naam" />
            <mm:last></td></tr></mm:last>
         </mm:relatednodes>
         <%
         int iMaxPart = Integer.parseInt(parentEvent.getStringValue("max_aantal_deelnemers"));
         int iAvailable = iMaxPart - iCurPart;
         if(iAvailable<0) { iAvailable = 0; }
         %>
         <tr><td style="<%= fNStyle %>"><strong>Nog beschikbare plaatsen</strong></td>
            <td colspan="2" style="vertical-align:top;"><%= iAvailable %></td>
         </tr>
         <%--
         <mm:relatednodes type="vertrekpunten">
            <mm:first><tr><td style="<%= fNStyle %>"><strong>Vertrekpunt</strong></td><td colspan="2"></mm:first>
            <mm:first inverse="true"><br/><br/></mm:first>
            <b><mm:field name="titel" /></b><br/><mm:field name="tekst" />
            <mm:last></td></tr></mm:last>
         </mm:relatednodes>
         <mm:field name="tekst">
            <mm:isnotempty>
               <tr><td style="<%= fNStyle %>"><strong>Bijzonderheden</strong></td>
               <td colspan="2"><mm:write /></td></tr>
            </mm:isnotempty>
         </mm:field>
         <mm:related path="readmore,extra_info" orderby="extra_info.naam">
            <mm:first><tr><td style="<%= fNStyle %>"><strong>Extra informatie</strong></td><td colspan="2"></mm:first>
            <li/><mm:field name="extra_info.omschrijving" /><br>
            <mm:last></td></tr></mm:last>
         </mm:related>
         --%>
       <%
      } %>
</mm:node>

<tr><td style="<%= fNStyle %>"></td><td colspan="2">
<mm:node number="<%= nodenr %>" jspvar="thisEvent">
   <br/>
   <bean:define id="actionid" property="action" name="SubscribeForm" scope="session" type="java.lang.String"/>
   <% if(!actionid.equals("startsubscription")) { %> 
       <span class="colortitle">De door u ingevoerde gegevens kunnen niet worden verwerkt:
       <html:errors bundle="LEOCMS" property="warning"/>
       </span><br/><br/>
   <% } %>
   <table border="0" cellpadding="0" cellspacing="0" style="width:374px;">
   <% 
   int dc = 0;
   if(isEventTemplate) { %>
      <mm:list nodes="<%= parent_number %>" path="evenement,posrel,deelnemers_categorie" orderby="deelnemers_categorie.naam">
         <mm:first>
            <tr>
               <td colspan="2" class="maincolor" style="padding:5px;line-height:0.85em;">
                  <nobr>Met hoeveel personen komt u?</nobr>
               </td>
            </tr>
            <tr><td colspan="2" style="height:5px;"></td></tr>
         </mm:first>
         <mm:field name="deelnemers_categorie.number" jspvar="deelnemersCategorie" vartype="String" write="false">
            <tr>
               <td class="maincolor" style="padding:5px;line-height:0.85em;">
                  <nobr><mm:field name="deelnemers_categorie.naam" />
                  &nbsp;(<mm:field name="posrel.pos" jspvar="costs" vartype="String" write="false">
                     <%= SubscribeAction.priceFormating(costs) %>
                  </mm:field>)
                  &nbsp;</nobr>
               </td>
               <td class="maincolor" style="width:100%;padding:1px;">
                  <html:select property="<%= "participantsPerCat[" + dc + "]" %>" style="align:right;border:0;width:100%;font-size:9px;" tabindex="<%= "" + ti %>">
                     <% for(int i = 0; i< 6; i++) { %>   
                        <html:option value="<%= "" +  i %>"><%= i %></html:option>
                     <% } %>
                  </html:select>
               </td>
            </tr>
            <tr><td colspan="2" style="height:5px;"></td></tr>
         </mm:field>
         <% dc++;
            ti++;
         %>
      </mm:list>
      <% 
      Vector vSelectPayment = new Vector(); 
      boolean bIsRelatedPayment = false;
      %>
      <mm:list nodes="<%= paginaID%>" path="pagina,posrel,payment_type" orderby="payment_type.naam" fields="payment_type.naam">
       <mm:field name="payment_type.naam" jspvar="sPaymentTypeName" vartype="String" write="false">
           <% 
           vSelectPayment.add(sPaymentTypeName);
           bIsRelatedPayment = true;
           %>
       </mm:field>   
      </mm:list>
      <mm:node number="cash_payment">
        <mm:field name="naam" jspvar="sCashPayment" vartype="String" write="false">
           <% vSelectPayment.add(sCashPayment);%>
        </mm:field>
      </mm:node>
      <% 
      if (bIsRelatedPayment) {%>
         <tr>
            <td class="maincolor" style="padding:5px;line-height:0.85em;">
               Betaalwijze
            </td>
            <td class="maincolor" style="width:100%;padding:1px;">
               <html:select property="paymentType" style="align:right;border:0;width:100%;font-size:9px;" tabindex="<%= "" + ti %>">
                  <% 
                  for(int i= 0; i<vSelectPayment.size(); i++) { 
                     %>
                      <html:option value="<%= (String) vSelectPayment.get(i) %>"><%= vSelectPayment.get(i) %></html:option>
                     <% 
                  } %>
               </html:select> 
            </td>
         </tr>
         <tr><td colspan="2" style="height:5px;"></td></tr>
         <% 
       }
       ti++;
       if(dc==0) { %>
         <tr>
            <td colspan="2" class="maincolor" style="padding:5px;line-height:0.85em;">
               <nobr>Met hoeveel personen komt u?</nobr>
            </td>
         </tr>
         <tr><td colspan="2" style="height:5px;"></td></tr>
         <tr>
            <td class="maincolor" style="padding:5px;line-height:0.85em;"><nobr>Aantal deelnemers&nbsp;</nobr></td>
            <td class="maincolor" style="width:100%;padding:1px;">
               <html:select property="<%= "participantsPerCat[" + dc + "]" %>" style="align:right;border:0;width:100%;font-size:9px;" tabindex="<%= "" + ti %>">
                  <% for(int i = 0; i< 6; i++) { %>   
                     <html:option value="<%= "" +  i %>"><%= i %></html:option>
                  <% } %>
               </html:select>
            </td>
         </tr>
         <tr><td colspan="2" style="height:5px;"></td></tr>
         <% ti++;
         }
      } else {
         %>
         <html:hidden property="<%= "participantsPerCat[" + dc + "]" %>" value="1" />
         <%
      } %>
      <tr>
         <td colspan="2" class="maincolor" style="padding:5px;line-height:0.85em;"><nobr>Uw gegevens ...</nobr></td>
      </tr>
      <tr><td colspan="2" style="height:5px;"></td></tr>
      <tr>
         <td class="maincolor" style="padding:5px;line-height:0.85em;">De heer / mevrouw
         </td>
         <td class="maincolor" style="width:100%;padding:1px;">
            <html:select property="gender" style="align:right;border:0;width:100%;font-size:9px;" tabindex="<%= "" + ti %>">
               <html:option value="male">De heer</html:option>
               <html:option value="female">Mevrouw</html:option>
            </html:select>
         </td>
      </tr>
      <% ti++; %>      
      <tr><td colspan="2" style="height:5px;"></td></tr>
      <bean:define id="phoneOnClickEvent" property="phoneOnClickEvent" name="SubscribeForm" scope="session" type="java.lang.String"/>
      <%
      String [] labels = { "Voornaam", "Voorletter", "Tussenvoegsel", "Achternaam", "Telefoon", "E-mail",
            "Straat", "Huisnummer", "Postcode", "Plaats", "Land", "Lidnummer", "Bijzonderheden" };
      String [] properties = { "firstName", "initials", "suffix", "lastName", "privatePhone", "email",
            "streetName", "houseNumber", "zipCode", "city", "country", "memberId", "description" };
      boolean [] requiredFields = { true, false, false, true, true, true,
            true, true, true, true, false, false, false};
      for(int i= 0; i< labels.length; i++) {
         if(isDemoLayout && labels[i].equals("Lidnummer")) {
            // do nothing
            %>
            <html:hidden property="<%= properties[i] %>" value="" />
            <%
            
         } else {
            %>
            <tr>
               <td class="maincolor" style="padding:5px;line-height:0.85em;">
                  <nobr><%= labels[i] %>&nbsp;<%= (requiredFields[i] ? "*" : "") %></nobr>
               </td>
               <td class="maincolor" style="padding:0px;padding-right:1px;vertical-align:top;">
                  <html:text 
                     property="<%= properties[i] %>"
                     style="width:100%;border:0;" tabindex="<%= "" + ti %>"
                     onclick="<%= (labels[i].equals("Telefoon") ? phoneOnClickEvent : "") %>" />
               </td>
            </tr>
            <tr><td colspan="2" style="height:5px;"></td></tr>
            <%
            ti++;
         }
      }
      if(isDemoLayout) { 
         %>
         <tr>
            <td class="maincolor" style="padding:5px;line-height:0.85em;">
               <nobr>Bank- / gironummer</nobr>
            </td>
            <td class="maincolor" style="padding:0px;padding-right:1px;vertical-align:top;">
               <html:text 
                  property="bankaccount"
                  style="width:100%;border:0;" tabindex="<%= "" + ti %>" />
            </td>
         </tr>
         <tr><td colspan="2" style="height:5px;"></td></tr>
         <%
         ti++;
      } else {
        %>
        <html:hidden property="bankaccount" value="" />
        <%
      } %>
      <tr>
         <td class="maincolor" style="padding:5px;line-height:0.85em;">
            U heeft over deze activiteit vernomen via
         </td>
         <td class="maincolor" style="width:100%;padding:1px;vertical-align:top;">
            <html:select property="source" style="align:right;border:0;width:100%;font-size:9px;" tabindex="<%= "" + ti %>">
               <html:option value=""></html:option>
               <mm:listnodes type="media" orderby="naam" directions="UP">
                  <mm:field name="naam" jspvar="mediaNaam" vartype="String" write="false">
                     <html:option value="<%= mediaNaam %>"><%= mediaNaam %></html:option>
                  </mm:field>
               </mm:listnodes>
            </html:select>
         </td>
      </tr>
      <tr><td colspan="2" style="height:5px;"></td></tr>
      <tr>
         <td colspan="2" style="padding:5px;line-height:0.85em;">(*) Vul minimaal deze velden in i.v.m. een correcte afhandeling.</td>
      </tr>
      <tr>
         <td>
            <%
               if(isEventTemplate) { %>
                  <html:submit property="action" value="<%= SubscribeForm.CANCEL_ACTION %>" styleClass="submit_image" style="width:150px;" />
                  <% 
               }
             %>
         </td>
         <td align="right">
            <html:submit property="action" value="<%= SubscribeForm.SUBSCRIBE_ACTION %>" tabindex="<%= "" + ti %>" styleClass="submit_image" style="width:150px;" />
         </td>
      </tr>
   </table>
   
</td></tr>
</table>

</mm:node>
</html:form>
</mm:cloud>
