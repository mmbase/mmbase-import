<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">

<% 
String vraagId=request.getParameter("v");
String callingNode=request.getParameter("c");

String rb = request.getParameter("rb");
String rbid = request.getParameter("rbid");
String pgid = request.getParameter("pgid");
String ssid = request.getParameter("ssid");

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
        /
        <a target="_blank" href="includes/relatedvraagbaak.jsp?&pst=|action=print&v=<%=vraagId%>">print</a>
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
          Deskundige:
          <a href="smoelenboek_vraagbaak.jsp?employee=<mm:field name="number"/>&rb=<%=rb%>&rbid=<%=rbid%>&pgid=<%=pgid%>&ssid=<%=ssid%>"><mm:field name="titel"/></a>
          </mm:relatednodes>
      </div>

  </td>
  </tr>  
  </table>
  
  <p>
  <%@include file="../includes/relatedimage_no_description.jsp" %>
  
  <mm:field name="intro" />
  
  <mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
      <mm:first><br/></mm:first>
      
      <%@include file="../includes/relatedparagraph.jsp" %>
    </mm:related>
  
  
  <% if (!printView && callingNode != null) { %>
  	<p>&nbsp;<br/><a href="<%= ph.createPaginaUrl(callingNode,request.getContextPath()) %>#top">naar boven</a></p><br/>
  <% } %>	

  </p>

</mm:node>

<% // print page proper css format
if (printView) { %>
   </body></html>
<% } %>

</mm:cloud>


