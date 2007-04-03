<mm:field name="intro" jspvar="dummy" vartype="String" write="false">
  <% textStr = dummy; %>
</mm:field>
<mm:field name="tekst" jspvar="dummy" vartype="String" write="false">
  <% if (!textStr.equals("")) { textStr += " "; }
	  textStr += dummy; %>
</mm:field>
<mm:related path="posrel,paragraaf">
	<mm:field name="paragraaf.titel_zichtbaar" jspvar="titel_zichtbaar" vartype="String" write="false">
	<% if ((titel_zichtbaar==null||!titel_zichtbaar.equals("0"))) { %>
		<mm:field name="paragraaf.titel" jspvar="dummy" vartype="String" write="false">
		<% if (!textStr.equals("")) { textStr += " "; }
		  textStr += dummy; %>
		</mm:field>
<% }%>
</mm:field>
<mm:field name="paragraaf.tekst" jspvar="dummy" vartype="String" write="false">
<% if (!textStr.equals("")) { textStr += " "; }
   textStr += dummy; %>
</mm:field>
</mm:related>