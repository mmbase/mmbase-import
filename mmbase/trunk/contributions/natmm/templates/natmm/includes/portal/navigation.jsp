<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String styleSheet = request.getParameter("rs");
   String sID = request.getParameter("s");
   String shortyRol = request.getParameter("sr");
   int maxShorties = 20;
   imgFormat = "shorty";
   int padding = 3;
   if(request.getParameter("sp")!=null && request.getParameter("sp").equals("natuurgebieden,posrel")) { 
      padding = 0;
   } 
   PaginaHelper ph = new PaginaHelper(cloud);
%><%@include file="../../includes/shorty_logic_1.jsp" %>
<% 
   for (int i =0; i<shortyCnt;i++){ 
%>
     <% // show the shorty %>
     <mm:node number="<%= shortyID[i] %>">
       <mm:field name="titel" write="false" jspvar="shorty_titel" vartype="String">
       <mm:field name="titel_zichtbaar" write="false" jspvar="shorty_tz" vartype="String">
<%
         if(!shorty_titel.equals("")&&!shorty_tz.equals("0")){ 
%>
           <span class="navbutton" style="text-align:right;"><%= shorty_titel.toUpperCase() %></span>
<%
         }
%>
       <mm:related path="readmore,contentelement" fields="contentelement.number,readmore.readmore" 
          orderby="readmore.pos">
         <%@include file="nav_logic_2.jsp" %>
<%
         linkTXT =  HtmlCleaner.cleanBRs(HtmlCleaner.cleanPs(readmoreTXT)).trim();
         if(!linkTXT.equals("")) {
%>
           <a href="<%= readmoreURL %>" class="subnavbutton" style="text-align:right;"<% 
             if(!readmoreTarget.equals("")){ %> target="<%= readmoreTarget %>"<% }
             if(!altTXT.equals("")){ %> title="<%= altTXT %>"<% } 
             %>><%= linkTXT %>
           </a>
           <mm:last inverse="true"><table class="rule" style="width:100%;"><tr><td></td></tr></table></mm:last>
<%
         } 
%>
       </mm:related>
       </mm:field>
       </mm:field>
     </mm:node>
<%   
   }
%>
</mm:cloud>