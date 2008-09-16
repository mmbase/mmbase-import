<%@page import="nl.leocms.evenementen.forms.SubscribeForm,nl.leocms.applications.NatMMConfig" %>
<%@include file="includes/top0.jsp" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/top1_params.jsp" %>
<%@include file="includes/top2_cacheparams.jsp" %>
<!-- cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application" -->
<%@include file="includes/top3_nav.jsp" %>
<% 
if(request.getParameter("memberid")!=null) { 
   %><mm:import id="onload_statement">window.location='#form';</mm:import><% 
} %>
<%@include file="includes/top4_head.jsp" %>
<%@include file="includes/top5_breadcrumbs_and_pano.jsp" %>
<%@include file="/editors/mailer/util/memberid_get.jsp" %>
   <br>
   <table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
   <tr>
      <td style="vertical-align:top;padding:10px;padding-top:0px;width:185px;">
      <%@include file="includes/navleft.jsp" %>
      <br>
      <jsp:include page="includes/teaser.jsp">
         <jsp:param name="s" value="<%= paginaID %>" />
         <jsp:param name="r" value="<%= rubriekID %>" />
         <jsp:param name="rs" value="<%= styleSheet %>" />
         <jsp:param name="sr" value="0" />
      </jsp:include>
      </td>
      <%
      String statusID = request.getParameter("status"); // status is set on login action
      if(memberid==null||statusID==null) {

         %><%@include file="includes/checkmembership.jsp" %><%

      } else { // only after login send email and show "thank you" page

         %><%@include file="/editors/mailer/util/memberid_set.jsp" %>
         <%
         String emailAddresses = NatMMConfig.getToEmailAddress();
         String pages_title = "";
         String subject = "";
         %>
         <mm:node number="<%= paginaID %>" jspvar="thisPage" id="this_page">
            <% pages_title = thisPage.getStringValue("titel"); %>
            <mm:relatednodes type="formulier" jspvar="thisForm" max="1">
            <%
               if(!thisForm.getStringValue("emailadressen").equals("")) { 
                  emailAddresses = thisForm.getStringValue("emailadressen").trim();
               }
            %>
            </mm:relatednodes>
            <% subject = pages_title + "(" + memberid + ")"; %>
            <mm:relatednodes type="email" max="1" constraints="<%= "email.subject ='" + subject + "'" %>" id="emailsent" />
            <mm:notpresent referid="emailsent">
               <mm:createnode type="email" id="mail1">
                  <mm:setfield name="from"><%= NatMMConfig.getFromEmailAddress() %></mm:setfield>
                  <mm:setfield name="subject"><%= subject %></mm:setfield>
                  <mm:setfield name="replyto"><%= emailAddresses %></mm:setfield>
                  <mm:setfield name="mailtype">3</mm:setfield>
                  <mm:setfield name="body">
                      <multipart id="plaintext" type="text/plain" encoding="UTF-8">
                         <%= memberid %>
                      </multipart>
                      <multipart id="htmltext" alt="plaintext" type="text/html" encoding="UTF-8">
                         <html>
                         Lid <%= memberid %> heeft zich ingelogd op pagina <%= pages_title %>.
                         </html>
                      </multipart>
                   </mm:setfield>
               </mm:createnode>
               <mm:createrelation source="this_page" destination="mail1" role="related" /><%
      
               emailAddresses = emailAddresses + ";";
               int semicolon = emailAddresses.indexOf(";");
               while(semicolon>-1)
               {
                  String nextEmailAddress = emailAddresses.substring(0,semicolon);
                  emailAddresses = emailAddresses.substring(semicolon+1);
                  semicolon = emailAddresses.indexOf(";");
                  %><mm:node referid="mail1">
                        <mm:setfield name="to"><%= nextEmailAddress %></mm:setfield>
                        <mm:field name="mail(oneshot)"/>
                     </mm:node><%
               }
               %>
            </mm:notpresent>
            <td style="vertical-align:top;width:374px;padding:10px;padding-top:0px;">
               <mm:relatednodes type="formulier" max="1" jspvar="thisForm">
                  <span class="colortitle"><mm:field name="titel_fra" /></span><br/>
                  <div style="margin:9px 0px 0px 0px"><mm:field name="omschrijving_fra" /></div>
                  <br/>
               </mm:relatednodes>
            </td>
            <td style="vertical-align:top;padding-left:10px;padding-right:10px;width:185px;<jsp:include page="includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
               <jsp:include page="includes/navright.jsp">
                  <jsp:param name="s" value="<%= paginaID %>" />
                  <jsp:param name="r" value="<%= rubriekID %>" />
                  <jsp:param name="lnr" value="<%= lnRubriekID %>" />
               </jsp:include>
               <jsp:include page="includes/shorty.jsp">
                  <jsp:param name="s" value="<%= paginaID %>" />
                  <jsp:param name="r" value="<%= rubriekID %>" />
                  <jsp:param name="rs" value="<%= styleSheet %>" />
                  <jsp:param name="sr" value="2" />
               </jsp:include>
               <img src="media/trans.gif" height="1px" width="165px;" />
            </td>
         </mm:node>
         <%
      } %>
   </tr>
   </table>
<%@include file="includes/footer.jsp" %>
<!-- /cache:cache --> 
</mm:cloud>



