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
   <br/>
   <mm:list nodes="<%= rubriekID %>" path="rubriek,posrel,pagina" fields="pagina.number" orderby="posrel.pos">
      <mm:size jspvar="size" vartype="Integer">
      <% 
      if(size.intValue()>1) {
         %>
         <mm:node element="pagina">
            <mm:field name="number" jspvar="pagina_number" vartype="String" write="false">      
               <mm:first>
                  <ul>
               </mm:first>
               <mm:first inverse="true">
               <mm:last inverse="true">
               <% 
      		  if(!paginaID.equals(pagina_number)) { 
      	         %><li><a href="<%= pHelper.createPaginaUrl(pagina_number,request.getContextPath()) %>"><mm:field name="titel" /></a></li><%
      	      } else {
      	         %><mm:field name="titel" /><%
      	      }
      	      %>
      			</mm:last>
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