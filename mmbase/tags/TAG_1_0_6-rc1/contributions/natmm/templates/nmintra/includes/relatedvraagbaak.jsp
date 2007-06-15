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
	<html><head><link rel="stylesheet" type="text/css" href="../css/main.css"></head><body style="overflow: scroll !important;">
<% } %>


<mm:node number="<%=vraagId%>">

  
  <table bgcolor="#d9e4f4" width="100%" >
  <tr>
  <td>
     <div style="float:left;">
      <mm:field name="titel_zichtbaar"
         ><mm:compare value="0" inverse="true"
            ><div class="pageheader"><mm:field name="titel" 
         /></div></mm:compare
      ></mm:field>
      </div>
     <div style="float:right;">
        <% if (!printView) { %>
        <a href="javascript:history.go(-1);">terug</a>
        <a target="_blank" href="includes/relatedvraagbaak.jsp?&pst=|action=print&v=<%=vraagId%>">/ print</a>
      	<% } %>  
      </div>

  </td>
  </tr>
  <tr>
  <td>
     <div style="float:left;">
      	<mm:relatednodes type="pools" max="1">
        Status:
          <mm:field name="name"/>
          </mm:relatednodes>
      </div>
     <div style="float:right;">
        	<mm:relatednodes type="persoon" max="1">
          Medewerker:
          <a href="smoelenboek.jsp?employee=<mm:field name="number"/>"><mm:field name="titel"/></a>
          </mm:relatednodes>
      </div>

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

<% // print page proper css format
if (printView) { %>
   </body></html>
<% } %>

</mm:cloud>


