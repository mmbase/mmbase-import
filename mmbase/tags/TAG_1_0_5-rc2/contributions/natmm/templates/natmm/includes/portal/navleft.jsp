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
   
   boolean isIE = (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE")>-1);

%><%@include file="../../includes/shorty_logic_1.jsp" %>
  <% 
  for (int i =0; i<shortyCnt;i++){ 
     // show the shorty 
     %>
     <mm:node number="<%= shortyID[i] %>">
       <mm:field name="titel" write="false" jspvar="shorty_titel" vartype="String">
       <mm:field name="titel_zichtbaar" write="false" jspvar="shorty_tz" vartype="String">
         <%
         if(!shorty_titel.equals("")&&!shorty_tz.equals("0")){ 
            %>
            <span class="navbutton" style="text-align:right;font-weight:bold;font-size:1.0em;width:<%= (isIE ? "164px;" : "154px;" ) %>"><%= shorty_titel.toUpperCase() %></span>
            <%
         }
         %>
       </mm:field>
       </mm:field>
       <% int j=0; %>
       <mm:related path="readmore,contentelement" fields="contentelement.number" orderby="readmore.pos">
         <mm:import id="offset" reset="true"><%= j %></mm:import>
         <%@include file="../shorty_logic_2.jsp" %>
         <%
         if(!altTXT.equals("")) {
         %>
           <a href="<%= readmoreURL %>" class="subnavbutton" style="text-align:right;"<% 
             if(!readmoreTarget.equals("")){ %> target="<%= readmoreTarget %>"<% }
             if(!altTXT.equals("")){ %> title="<%= altTXT %>"<% } 
             %>>
             <% 
             int maxLength = 24;
             if(altTXT.length()>maxLength) {
               altTXT = altTXT.substring(0,maxLength) + "&hellip;";
             } 
             %>
             <%= altTXT %>&nbsp;
           </a>
           <mm:last inverse="true">
             <table cellpadding="0" cellspacing="0" style="width:164px;height:1px;"><tr>
               <td height="1" class="leftnavline"><img src="media/trans.gif" width="164px" height="1" vspace="0" border="0" alt=""></td>
             </tr></table>
           </mm:last>
         <%
         }
         j++;
       %>
       </mm:related>
     </mm:node>
     <%   
   }
%>
<table cellpadding="0" cellspacing="0" style="width:164px;height:1px;"><tr>
  <td height="1" class="leftnavline"><img src="media/trans.gif" width="164px" height="1" vspace="0" border="0" alt=""></td>
</tr></table>
</mm:cloud>