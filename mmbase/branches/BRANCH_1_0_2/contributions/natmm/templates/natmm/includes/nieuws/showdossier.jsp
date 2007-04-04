<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,nl.leocms.util.*,nl.leocms.util.tools.*,nl.leocms.util.tools.HtmlCleaner" %>
<%@include file="../../includes/time.jsp" %>
<%
String dossierID = request.getParameter("d");
String paginaID = request.getParameter("p");
TreeMap articles = new TreeMap();
%>
<mm:cloud jspvar="cloud">
<mm:node number="<%=dossierID%>">
	<table cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
        <strong>Dossier <mm:field name="naam" /></strong><br />
        <mm:field name="omschrijving" />
      </td>
			<td>
			<mm:relatednodes type="images" max="1" role="posrel">
				<table>
					<tr>
						<td><img src="<mm:image template="s(170)" />"></td>
					</tr>
          <mm:field name="bron"
					  ><mm:isnotempty
						  ><tr><td class='imagecaption'>Foto: <mm:write /></td></tr>
            </mm:isnotempty
          ></mm:field>
				</table>
			</mm:relatednodes>
			</td>
		</tr>
	</table>
  <table class="dotline"><tr><td height="3"></td></tr></table>
  <mm:field name="showdate">
    <mm:compare value="yes">
      <mm:import id="showdate" />
    </mm:compare>
  </mm:field>
  <%@include file="articlessearch.jsp" %>
  <%@include file="searchresults.jsp" %>
</mm:node>
</mm:cloud>