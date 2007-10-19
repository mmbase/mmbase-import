<mm:node element="pagina">
  <mm:field name="number" jspvar="page_number" vartype="String" write="false">
  <mm:field name="titel_fra" jspvar="showDate" vartype="String" write="false">
  <a href="<%= ph.createPaginaUrl(page_number,request.getContextPath()) %>" class="pageheader" title="<mm:field name="titel" />">
    <span class="dark"><mm:field name="titel" /></span>
  </a><br/>
	<mm:related path="contentrel,artikel" orderby="artikel.embargo"
      directions="DOWN" max="<%= "" + numberOfMessages %>" constraints="<%= articleConstraint %>"
		  ><mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false"><%
        String readmoreUrl = ph.createPaginaUrl(page_number,request.getContextPath()) + "?article=" + artikel_number;
				%><%@include file="summaryrow.jsp" 

			%></mm:field
		></mm:related
	></mm:field
	></mm:field>
</mm:node>