<%@include file="includes/templateheader.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/header.jsp" 
%><%@include file="includes/calendar.jsp" 


%><td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>">
<table border="0" cellpadding="0" cellspacing="0">
    <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
    <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
        <td><%    
        
         // See the editwizard article_form:    
         // <option id="1">textarea</option>
         // <option id="2">textline</option>
         // <option id="3">dropdown</option>
         // <option id="4">radio buttons</option>
         // <option id="5">check boxes</option>
         
         String sPageRefMinOne = (String) session.getAttribute("pagerefminone");
         // ** hack: add sDefaultValues on page "Wat vind je ervan?"
         String sDefaultName = "";
         String sDefaultEmail = "";
         String sDefaultText = "";
         if(pageId.equals("1713")) {
            sDefaultText = "Het volgende wil ik melden over de rubriek ";
            %><%@include file="includes/authenticate.jsp" %><% 
            if(!username.equals("")) { %>
            <mm:listnodes type="medewerkers" constraints="<%= "UPPER(account) = '" + username.toUpperCase() + "'" %>" jspvar="thisEmployee" max="1">
               <%
               sDefaultName += thisEmployee.getStringValue("firstname");
               if(!thisEmployee.getStringValue("suffix").equals("")) {
                  sDefaultName += " " + thisEmployee.getStringValue("suffix");
               }
               sDefaultName += " " + thisEmployee.getStringValue("lastname");
               sDefaultEmail += thisEmployee.getStringValue("email");
               %>
            </mm:listnodes>
            <% } %>
            <mm:node number="<%= sPageRefMinOne %>" jspvar="lastPage" notfound="skipbody">
               <mm:related path="posrel,rubriek" constraints="posrel.pos!=0">
                  <mm:node element="rubriek" jspvar="lastRubriek">
                     <% sDefaultText += lastRubriek.getStringValue("naam") + " - "; %>
                  </mm:node>
               </mm:related>
               <%  sDefaultText += lastPage.getStringValue("naam") + ": "; %>
            </mm:node>
            <%
         }  
       
         %><% templateTitle = "formscript"; 
         %><%@include file="includes/cacheopen.jsp" 
         %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"
         ><%@include file="includes/formscript.jsp" 
         %></cache:cache><%
         
         if(!postingStr.equals("")){
             postingStr += "|";
             %><%@include file="includes/formresult.jsp" %><% 
         } else {
             templateTitle = "formtable_" + session.getAttribute("pagerefminone"); 
             %><%@include file="includes/cacheopen.jsp" 
             %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"
             ><%@include file="includes/formtable.jsp" 
             %></cache:cache><%
         } %></td>
    <td><img src="media/spacer.gif" width="10" height="1"></td>
</tr>
</table>
</div>
</td><%-- 

*************************************** right bar *******************************
--%><td>&nbsp;</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>