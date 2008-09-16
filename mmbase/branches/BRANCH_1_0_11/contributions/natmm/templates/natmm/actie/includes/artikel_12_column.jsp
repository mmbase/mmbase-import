<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
<%@include file="../../includes/image_vars.jsp" %>
<%
String styleSheet = request.getParameter("rs");
String lnRubriekID = request.getParameter("lnr");
String rnImageID = request.getParameter("rnimageid");

String shortyRol = ""; 

%>
<mm:import externid="showdate" jspvar="showdateID">false</mm:import>
<mm:import externid="showpageintro">false</mm:import>
<mm:cloud jspvar="cloud">
<%
boolean hasRightCell = false;
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel">
   <mm:size jspvar="size" vartype="Integer" write="false">
      <% hasRightCell = size.intValue()>1; %>
   </mm:size>
</mm:list>
<% 
if(!hasRightCell) { 
   %><mm:list nodes="<%= artikelID %>" path="artikel,posrel,images" fields="images.number" constraints="posrel.pos='1' OR posrel.pos='7'" max="1">
      <% hasRightCell = true; %>
   </mm:list><%
}
if(!hasRightCell) { 
   %><mm:list nodes="<%= artikelID %>" path="artikel,posrel1,paragraaf,posrel2,images" fields="images.number" constraints="posrel2.pos='1' OR posrel2.pos='7'" max="1">
   	 <% hasRightCell = true; %>
   </mm:list><%
}
if(!hasRightCell) { 
   shortyRol = "2";
   int maxShorties = 20;
   imgFormat = "shorty";
   String sID = paginaID;
   %><%@include file="../../includes/shorty_logic_1.jsp" %><%
   hasRightCell = (shortyCnt>0); 
}
boolean onlyShortyRelatedToArticle = false;
if(!hasRightCell) { // special case, shorty related to article
   %>
    <mm:listcontainer path="artikel,rolerel,shorty">
   		<mm:constraint field="rolerel.rol" operator="EQUAL" value="2" />
   		<mm:list nodes="<%= artikelID %>" fields="shorty.number" max="1">
   			<% 
            hasRightCell = true;
            onlyShortyRelatedToArticle = true;
            %>
     		</mm:list>
   </mm:listcontainer>
   <%
}
if(hasRightCell) { 
   // ** the left and right padding has been taken care of by the container
   // ** only pageintro if it is prescribed by the referring template
   %><table width="539px;" border="0" cellspacing="0" cellpadding="0">
   <tr>
   	<td style="vertical-align:top;padding-right:10px;padding-bottom:10px;width:364px;">
         <mm:compare referid="showpageintro" value="true">
            <%@include file="../../includes/page_intro.jsp" %>
         </mm:compare>
         <% if(!artikelID.equals("-1")) { %>
            <jsp:include page="../../includes/artikel_1_column.jsp">
               <jsp:param name="o" value="<%= artikelID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="s" value="false" />
               <jsp:param name="q" value="<%= showdateID %>" />
            </jsp:include>
            <mm:compare referid="showpageintro" value="true">
               <jsp:include page="../../includes/shorty.jsp">
         	      <jsp:param name="s" value="<%= paginaID %>" />
          	      <jsp:param name="r" value="<%= rubriekID %>" />
                  <jsp:param name="rs" value="<%= styleSheet %>" />
      		      <jsp:param name="sr" value="1" />
      		   </jsp:include>
      		</mm:compare>
            <jsp:include page="../../includes/shorty.jsp">
               <jsp:param name="s" value="<%= artikelID %>" />
               <jsp:param name="r" value="<%= rubriekID %>" />
               <jsp:param name="rs" value="<%= styleSheet %>" />
               <jsp:param name="sp" value="artikel,rolerel" />
               <jsp:param name="sr" value="1" />
            </jsp:include>
      	<% } %>
      </td>
   	<td style="vertical-align:top;padding-left:10px;width:175px;<jsp:include page="../includes/rightcolumn_bgimage.jsp"><jsp:param name="rnimageid" value="<%= rnImageID %>" /></jsp:include>">
         <jsp:include page="nav.jsp">
            <jsp:param name="p" value="<%= paginaID %>" />
            <jsp:param name="a" value="<%= artikelID %>" />
         </jsp:include>
         <br/>
         <jsp:include page="../../includes/rightcolumn_image.jsp">
            <jsp:param name="a" value="<%= artikelID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
         </jsp:include>
   		<jsp:include page="../../includes/shorty.jsp">
   	      <jsp:param name="s" value="<%= paginaID %>" />
   	      <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
   	      <jsp:param name="sr" value="2" />
   	   </jsp:include>
         <% if(!onlyShortyRelatedToArticle) {
            %>
            <table class="dotline"><tr><td height="3"></td></tr></table>
            <% 
         } %>
         <jsp:include page="../../includes/shorty.jsp">
            <jsp:param name="s" value="<%= artikelID %>" />
            <jsp:param name="r" value="<%= rubriekID %>" />
            <jsp:param name="rs" value="<%= styleSheet %>" />
            <jsp:param name="sp" value="artikel,rolerel" />
            <jsp:param name="sr" value="2" />
         </jsp:include>
   	</td>
   </tr>
   </table><%
} else {
   %><jsp:include page="../../includes/artikel_1_column.jsp">
      <jsp:param name="o" value="<%= artikelID %>" />
      <jsp:param name="r" value="<%= rubriekID %>" />
      <jsp:param name="rs" value="<%= styleSheet %>" />
      <jsp:param name="s" value="false" />
      <jsp:param name="q" value="<%= showdateID %>" />
   </jsp:include><%
}
%>
</mm:cloud>