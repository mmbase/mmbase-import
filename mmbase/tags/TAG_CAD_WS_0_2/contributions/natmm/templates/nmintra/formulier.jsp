<%@include file="/taglibs.jsp" %>
<mm:content type="text/html" escaper="none">
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<%
String sPageRefMinOne = (String) session.getAttribute("pagerefminone");
if(!postingStr.equals("|")) { expireTime = 0; } 
%>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey + "~" + sPageRefMinOne %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" %>
<%@include file="includes/header.jsp" %>
<td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage">
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
        
        // ** hack: special functionality on page "Wat vind je ervan?"
        String sWvjePageId = ""; %>
        <mm:list path="rubriek,posrel,pagina" constraints="rubriek.naam = 'Wat vind je ervan?' AND pagina.titel = 'Wat vind je ervan?'" searchdir="destination">
          <mm:field name="pagina.number" jspvar="number" vartype="String" write="false">
            <% sWvjePageId = number; %>
          </mm:field>
        </mm:list>
        <%@include file="includes/form/script.jsp" %>
        <%
        if(!postingStr.equals("")){
            postingStr += "|";
            %><%@include file="includes/form/result.jsp" %><% 
        } else {
            %><%@include file="includes/form/table.jsp" %><%
        } 
        %>
        </td>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
    </tr>
</table>
</div>
</td><%

// *************************************** right bar *******************************
%><td>&nbsp;</td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
</mm:content>