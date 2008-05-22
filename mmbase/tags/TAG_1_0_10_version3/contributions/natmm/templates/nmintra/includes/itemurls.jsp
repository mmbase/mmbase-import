<mm:list nodes="<%= paginaID %>" path="pagina,posrel,linklijst"
   ><mm:field name="linklijst.naam" jspvar="items_name" vartype="String" write="false"><%
      rightBarTitle = items_name;
      %><%@include file="../includes/rightbartitle.jsp" 
  %></mm:field
></mm:list>
<%@include file="../includes/whiteline.jsp" %>
<div <%= (iRubriekLayout!=NMIntraConfig.SUBSITE1_LAYOUT ? " class='smoelenboeklist' id='smoelenboeklist'" : "") 
      %> style="padding-left:20px;margin-bottom:5px;">
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,linklijst"
       ><mm:node element="linklijst"
       ><mm:related path="lijstcontentrel,link" orderby="lijstcontentrel.pos" directions="UP"
           ><a target="_blank" href="<mm:field name="link.url" />" class="menuItem">
               <span class="normal"><mm:field name="link.titel" /></span>
            </a>
            <br/>
       </mm:related
       ></mm:node>
</mm:list>
</div>