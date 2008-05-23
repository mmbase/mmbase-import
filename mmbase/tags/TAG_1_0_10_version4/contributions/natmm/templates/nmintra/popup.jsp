<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%
int screenWidth = 750;
int screenHeight = 430;

/*
Cookie[] cookies = request.getCookies();
if(cookies!=null){
  for(int i=0; i<cookies.length; i++) {
      Cookie cookie = cookies[i];
      if (cookie.getName().equals("screenWidth")){
          try {
              screenWidth = (new Integer(cookie.getValue())).intValue()-20;
          } catch(Exception e) { }
      }
      if (cookie.getName().equals("screenHeight")){
          try {
              screenHeight = (new Integer(cookie.getValue())).intValue()-170;
          } catch(Exception e) { }
      }
  }
}  
*/
%>
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,link" max="1"
  ><mm:import id="extraload">javascript:launchCenter('<mm:field name="link.url"
    />', 'popup<%= paginaID %>', <%= screenHeight %>,  <%= screenWidth %>, ',left=0,top=0,scrollbars,resizable=yes<mm:present referid="newwin">,toolbar=yes,menubar=yes</mm:present>');setTimeout('newwin.focus();',250);</mm:import>
</mm:list>
<%@include file="includes/header.jsp" 
%><td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>" id="infopage">
    <mm:node number="<%= paginaID %>">
        <table border="0" cellpadding="0" cellspacing="0">
            <tr><td style="padding:10px;padding-top:18px;">
                <h4><mm:field name="titel"/> zal in een nieuw venster worden geopend.</h4>
                <%@include file="includes/relatedteaser.jsp" %>
            </td></tr>
        </table>
     </mm:node>
     <mm:notpresent referid="extraload">Error: no url specified for page with external website template</mm:notpresent>
</div>
</td>
<td><%

// *********************************** right bar *******************************
%><img src="media/spacer.gif" width="10" height="1"></td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
