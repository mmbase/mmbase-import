<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">

<% 
String vraagId=request.getParameter("v");
String callingNode=request.getParameter("c");
String printAction=request.getParameter("pst");
boolean printView = ((printAction != null) && (printAction.indexOf("print") != -1));
// needed to support images in paragraph include
String imageTemplate = "";
PaginaHelper ph = new PaginaHelper(cloud);
%>
<% // print page proper css format
if (printView) { %>
	<html><head><link rel="stylesheet" type="text/css" href="../css/main.css"></head></html>
<% } %>


<mm:node number="<%=vraagId%>">

  
  <table bgcolor="#cccccc" width="100%" >
  <tr>
  <td rowspan="2">
   <mm:field name="titel_zichtbaar"
	   ><mm:compare value="0" inverse="true"
   	   ><div class="pageheader"><mm:field name="titel" 
	   /></div></mm:compare
	></mm:field>
  </td>
  <td width="20%">
  status:
	<mm:relatednodes type="pools" max="1">
    <mm:field name="name"/>
    </mm:relatednodes>
    
    
  </td>
  <td width="5%">
  <% if (!printView) { %>
  <a href="javascript:history.go(-1);">terug</a>
  <% } %>
  </td>
  <td width="5%">
  	<% if (!printView) { %>
	  <a target="_blank" href="includes/relatedvraagbaak.jsp?&pst=|action=print&v=<%=vraagId%>">print</a>
	<% } %>  
  </td>
  </tr>
  <tr>
  <td colspan="3">
  medewerker:
  	<mm:relatednodes type="persoon" max="1">
    <a href="smoelenboek.jsp?employee=<mm:field name="number"/>"><mm:field name="titel"/></a>
    </mm:relatednodes>
    
  </td>
  </tr>
  </table>
  
  <mm:field name="intro" />
  
  
  <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <mm:first><br/></mm:first>
      
      <%@include file="../includes/relatedparagraph.jsp" %>
    </mm:related>
  
  
  <% if (!printView && callingNode != null) { %>
  	<p><a href="<%= ph.createPaginaUrl(callingNode,request.getContextPath()) %>#top">link to top</a></p><br/>
  <% } %>	

</mm:node>
</mm:cloud>
