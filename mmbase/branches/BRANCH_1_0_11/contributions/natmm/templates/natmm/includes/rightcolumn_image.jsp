<%@include file="/taglibs.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@include file="../includes/image_vars.jsp" %>
<mm:cloud jspvar="cloud">
<%

String shortyRol = "";
// *** show images related to the selected article and its paragraphs
if(!artikelID.equals("-1")) { 
   imgFormat = "rightcolumn"; 
   %><%@include file="../includes/getstyle.jsp" %>
   <mm:node number="<%= artikelID %>">
   	<%@include file="../includes/image_logic.jsp" %>
   </mm:node>
   <mm:list nodes="<%= artikelID %>" path="artikel,posrel,paragraaf" fields="paragraaf.number,paragraaf.titel,paragraaf.tekst" orderby="posrel.pos">
   	<mm:field name="paragraaf.number" jspvar="paragraaf_number" vartype="String" write="false">
   	<mm:node number="<%= paragraaf_number %>">
      	<%@include file="../includes/image_logic.jsp" %>
   	</mm:node>
   	</mm:field>
   </mm:list><%
} %>
</mm:cloud>