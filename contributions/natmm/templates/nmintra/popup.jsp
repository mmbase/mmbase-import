<%@include file="/taglibs.jsp" 
%><mm:cloud jspvar="cloud"
><%@include file="includes/templateheader.jsp" 
%><%
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
%><%@include file="includes/header.jsp" 
%><td><%@include file="includes/pagetitle.jsp" %></td>
<td><% String rightBarTitle = "";
    %><%@include file="includes/rightbartitle.jsp" 
%></td>
</tr>
<tr>
<td class="transperant">
<div class="<%= infopageClass %>">
    <mm:list nodes="<%= pageId %>" path="pagina,posrel,link" max="1"
        ><body onload="javascript:launchCenter('<mm:field name="link.url"
            />', 'popup', <%= screenHeight %>,  <%= screenWidth %>, ',left=0,top=0,scrollbars,resizable=yes<mm:present referid="newwin">,toolbar=yes,menubar=yes</mm:present>');setTimeout('newwin.focus();',250)"></body>
        <table border="0" cellpadding="0" cellspacing="0">
            <tr><td style="padding:10px;padding-top:18px;">
                <h4><mm:field name="pagina.titel"/> zal in een nieuw venster worden geopend.</h4>
                <%@include file="includes/relatedteaser.jsp" %>
            </td></tr>
        </table>
        <mm:import id="urlexists"
    /></mm:list
    ><mm:notpresent referid="urlexists">Error: no url specified for page with external website template</mm:notpresent
    ><mm:remove referid="urlexists"/>
</div>
</td>
<td><%-- 

*********************************** right bar *******************************
--%><img src="media/spacer.gif" width="10" height="1"></td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>
