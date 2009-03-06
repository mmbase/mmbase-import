<%@page import="java.text.SimpleDateFormat,
   java.util.TreeSet,
   java.util.Iterator,
   nl.leocms.util.tools.HtmlCleaner,
   nl.leocms.util.DoubleDateNode,
   nl.leocms.evenementen.forms.SubscribeForm,
   nl.leocms.evenementen.Evenement,
   nl.leocms.evenementen.EventNotifier" 
%><%@include file="includes/top0.jsp" 
%><%
// *** use paginaID from session to return to the last visited agenda page (in case of redirect from subscribe form) ***
if(paginaID.equals("-1") && session.getAttribute("pagina")!=null) { 
   paginaID = (String) session.getAttribute("pagina");
} %>
<mm:content type="text/html" escaper="none">
<mm:cloud jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<mm:import jspvar="searchID" externid="search">show</mm:import>
<!-- cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
<%@include file="includes/top3_nav.jsp" 
%><%@include file="includes/top4_head.jsp" 
%><%@include file="includes/top5_breadcrumbs_and_pano.jsp" 
%>

<%-- Any template calling others need to pass isNaardermeer as PaginaHelper/mm:import fails--%>
<%request.setAttribute("isNaardermeer", isNaardermeer);%>

<!-- /cache:cache -->

<%@include file="includes/calendar.jsp" %>
<%@include file="includes/events/dateutil.jsp" %>

<%
String actionID = request.getParameter("action");

if(!natuurgebiedID.equals("-1")) { 
   %><mm:list nodes="<%= natuurgebiedID %>" path="natuurgebieden,posrel,provincies" fields="provincies.number" max="1">
      <mm:field name="provincies.number" jspvar="provincies_number" vartype="String" write="false"><%
         if(provID.equals("-1")) {
            provID = provincies_number;
         } else if(!provID.equals(provincies_number)) { // *** natuurgebied should be part of provincie
            natuurgebiedID = "-1";
         }%>
      </mm:field>
   </mm:list><%
}
   
SubscribeForm subscribeForm = (SubscribeForm) session.getAttribute("SubscribeForm");
%><%@include file="includes/events/selecteddateandtype.jsp" %>
  <% if (isNaardermeer.equals("true")) { %>		
   	<div style="position:absolute; left:681px; width:70px; height:216px; background-image: url(media/natmm_logo_rgb2.gif); background-repeat:no-repeat;"></div>
  <% } %>
<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
      <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
         <br/>
         <%@include file="includes/navleft.jsp" %>
         <br>
         <jsp:include page="includes/teaser.jsp">
            <jsp:param name="s" value="<%= paginaID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sr" value="0" />
         </jsp:include>
      </td>
      <td width="<%= (subscribeForm==null ? "374" : "559") %>" valign="top" style="padding-left:10px;padding-right:10px;">
         <br/>
         <% 
          if(actionID!=null&&actionID.equals("confirm")) {
          
            %><jsp:include page="includes/events/doconfirm.jsp">
                  <jsp:param name="s" value="<%=  request.getParameter("s") %>" />
              </jsp:include><%

         } else if(subscribeForm!=null&&!subscribeForm.getAction().equals(SubscribeForm.CANCELED)) {
         
            session.setAttribute("pagina",paginaID); 

            // *** there is a form in this session, show the user the form
            // *** parameters are already passed by SubscribeInitAction
            
            if(subscribeForm.getAction().equals(SubscribeForm.PROMPT_FOR_CONFIRMATION)) {
               %><jsp:include page="includes/events/confirm.jsp" /><%            
            } else {
               %><jsp:include page="includes/events/subscribe.jsp">
                  <jsp:param name="p" value="<%= paginaID%>" />
                  <jsp:param name="sid" value="<%= subsiteID%>" />
                  <jsp:param name="rl" value="<%= iRubriekLayout %>" />
               </jsp:include><%
            }
         
         } else {
         
            boolean containsSubscriptionClosed = false;
            if(!evenementID.equals("-1")) { 
            
               %><jsp:include page="includes/events/details.jsp">
                  <jsp:param name="r" value="<%= rubriekID %>" />
                  <jsp:param name="e" value="<%= evenementID%>" />
               </jsp:include><%
            
            } else { 
               %><mm:node number="<%= paginaID %>" jspvar="thispage" notfound="skip">
                  <mm:field name="titel">
                     <mm:isnotempty>
                        <span class="colortitle"><%= thispage.getStringValue("titel").toUpperCase() %></span><br/>
                     </mm:isnotempty>
                  </mm:field>
                  <mm:field name="omschrijving">
                     <mm:isnotempty>
                        <div style="margin:9px 0px 0px 0px"><mm:write /></div>
                     </mm:isnotempty>
               </mm:field>
               
               <table class="dotline"><tr><td height="3"></td></tr></table>
  <table width="354" border="0" cellpadding="0" cellspacing="0">
               <mm:related path="contentrel,evenement" fields="evenement.number">
                  <mm:field name="evenement.number" jspvar="eventId" vartype="String" write="false"><%
                  
      String event_number = eventId;
      String parent_number = Evenement.findParentNumber(event_number);
      %><mm:node number="<%= event_number %>" jspvar="evenement"><% 
         DoubleDateNode ddn = new DoubleDateNode(evenement);
         ddn.clipBeginOnToday();
         %>
         <tr>
            
            <td style="vertical-align:top;width:50%;">
              <mm:field name="titel" jspvar="title" vartype="String" write="false">   
               <a href="activity2.jsp?p=<%= paginaID 
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
            
            <td style="vertical-align:top;width:50%;padding-right:3px;">
              <%= ddn.getReadableDate(" | ") %>&nbsp;|&nbsp;<%= ddn.getReadableStartTime() %>
              <br/>
              <mm:list nodes="<%= parent_number %>" path="evenement,related,evenement_type"
                     constraints="evenement_type.isoninternet='1'">
                  <mm:field name="evenement_type.naam" />
                  <br/>
              </mm:list>
            </td>
            
         </tr>
         <tr><td colspan="2"><table class="dotline"><tr><td height="3"></td></tr></table></td></tr>
      </mm:node>
                  </mm:field>
              </mm:related>
               </mm:node>
</table>
<br/>               
               <%
            } %>
            </td>
             	 <% if (isNaardermeer.equals("true")) { %>
            	 <td style="vertical-align:top;padding-left:5px;padding-right:5px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">			
   					<img src="media/trans.gif" height="226" width="1">
	 			 <% } 
	 			 else { %>
	 			 <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
	 			 	<% } %>
			  <br/>
               <jsp:include page="includes/events/searchform.jsp">
                  <jsp:param name="p" value="<%= paginaID %>" />
                  <jsp:param name="prov" value="<%= provID %>" />
                  <jsp:param name="n" value="<%= natuurgebiedID %>" />
                  <jsp:param name="rl" value="<%= iRubriekLayout %>" />
               </jsp:include>
               <jsp:include page="includes/shorty.jsp">
                  <jsp:param name="s" value="<%= paginaID %>" />
                  <jsp:param name="r" value="<%= rubriekID %>" />
                  <jsp:param name="rs" value="<%= styleSheet %>" />
                  <jsp:param name="sr" value="2" />
               </jsp:include>
               <%
               if(containsSubscriptionClosed) { 
                  %>
                  <table class="dotline"><tr><td height="3"></td></tr></table>
                  <b>(*) Het via internet aanmelden voor deze activiteit is gesloten.
                  Telefonisch aanmelden is misschien nog wel mogelijk. Zie de beschrijving 
                  van de activiteit voor meer details en het telefoonnummer.</b>
                  <%
               }
            } %>
            </td>
   </tr>
</table>
<a name="bottom"></a>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
</mm:content>