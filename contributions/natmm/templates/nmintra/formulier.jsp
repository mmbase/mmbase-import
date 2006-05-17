<%@include file="/taglibs.jsp" 
%><mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%@include file="includes/calendar.jsp" 

%><%@include file="includes/header.jsp" 
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
			String sWvjePageId = ""; %>
			<mm:list path="rubriek,posrel,pagina" constraints="rubriek.naam = 'Wat vind je ervan?' AND pagina.titel = 'Wat vind je ervan?'">
				<mm:field name="pagina.number" jspvar="number" vartype="String" write="false">
					<% sWvjePageId = number; %>
				</mm:field>
			</mm:list>
		<% if(pageId.equals(sWvjePageId)) {
            sDefaultText = "Het volgende wil ik melden over de rubriek ";
            %>
            <mm:node number="<%= sPageRefMinOne %>" jspvar="lastPage" notfound="skipbody">
               <mm:related path="posrel,rubriek" constraints="posrel.pos!=0">
                  <mm:node element="rubriek" jspvar="lastRubriek">
                     <% sDefaultText += lastRubriek.getStringValue("naam") + " - "; %>
                  </mm:node>
               </mm:related>
               <%  sDefaultText += lastPage.getStringValue("titel") + ": "; %>
            </mm:node>
            <%
         } 
         %><% templateTitle = "formscript"; 
         %><%@include file="includes/cacheopen.jsp" 
         %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"
         ><%@include file="includes/form/script.jsp" 
         %></cache:cache><%
         
         if(!postingStr.equals("")){
             postingStr += "|";
             %><%@include file="includes/form/result.jsp" %><% 
         } else {
             templateTitle = "formtable_" + session.getAttribute("pagerefminone"); 
             %><%@include file="includes/cacheopen.jsp" 
             %><cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application"
             ><%@include file="includes/form/table.jsp" 
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