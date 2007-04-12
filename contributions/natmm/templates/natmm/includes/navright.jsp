<%@include file="/taglibs.jsp" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud">
<%
String rubriekID = request.getParameter("r");
String lnRubriekID = request.getParameter("lnr");
String paginaID = request.getParameter("s");

if(!lnRubriekID.equals(rubriekID)) { 
   PaginaHelper pHelper = new PaginaHelper(cloud);
   %>
   <mm:list nodes="<%= rubriekID %>" path="rubriek,posrel,pagina" fields="pagina.number" orderby="posrel.pos">
      <mm:size jspvar="size" vartype="Integer">
      <% 
      if(size.intValue()>1) {
         %>
         <mm:node element="pagina">
            <mm:field name="number" jspvar="pagina_number" vartype="String" write="false">      
               <mm:first>
                  <mm:field name="titel" />
                  <ul>
               </mm:first>
               <mm:first inverse="true">
               <% 
      			if(!paginaID.equals(pagina_number)) { 
      	         %><li><a href="<%= pHelper.createPaginaUrl(pagina_number,request.getContextPath()) %>"><mm:field name="titel" /></a></li><%
      	      } else {
      	         %><li style="list-style-type:none;"><mm:field name="titel" /></li><%
      	      }
      	      %>
      	      <%-- 
       		   <mm:related path="posrel,panno,posrel,images" fields="images.alt_tekst" max="1">
      			   <mm:node element="images">
      		   	   <a href="<%= pHelper.createPaginaUrl(pagina_number,request.getContextPath()) %>"><img src="<mm:image template="s(170!x45!)"/>" alt="<mm:field name="alt_tekst" />" border="0"></a>
      			   </mm:node>
      			</mm:related>
      			--%>
               </mm:first>
               <mm:last></ul></mm:last>
            </mm:field>
      	</mm:node>
         <%
      } %>
      </mm:size>
   </mm:list><br/><br/>
   <% 
} %>
</mm:cloud>