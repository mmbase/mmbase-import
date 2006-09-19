<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
   String rubriekID = request.getParameter("r");
   String styleSheet = request.getParameter("rs");
   String paginaID = request.getParameter("s");
   PaginaHelper ph = new PaginaHelper(cloud);

%>
<div style="padding-left:3px;">
  <mm:list  nodes="<%=paginaID%>"  path="pagina,posrel,linklijst,lijstcontentrel,link" orderby="lijstcontentrel.pos,link.titel"
     fields="link.titel,link.url,link.alt_tekst">
    <a class="hover" href="<mm:field name="link.url" />" title="<mm:field name="link.alt_tekst" 
      />" target="<mm:field name="link.target" />"><mm:field name="link.titel" /></a>
    - <span class="colortxt" style="font-size:90%;"><mm:field name="link.omschrijving" /></span>
    <table class="dotline" style="width:212px;"><tr><td height="3"></td></tr></table>
  </mm:list>
</div>
</mm:cloud>
