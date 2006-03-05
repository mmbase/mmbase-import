<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,nl.leocms.util.*,nl.mmatch.HtmlCleaner" %>
<%@include file="../../includes/time.jsp" %>
<%
String dossierID = request.getParameter("d");
String paginaID = request.getParameter("p");
TreeMap articles = new TreeMap();
%>
<mm:cloud jspvar="cloud">
<mm:node number="<%=dossierID%>">
	<table>
		<tr>
			<td valign="top"><strong>Dossier <mm:field name="naam" /></strong><br>
			<mm:field name="omschrijving" /></td>
			<td></td>
			<td>
			<mm:relatednodes type="images" max="1" role="posrel">
				<table>
					<tr>
						<td><img src="<mm:image template="s(170)" />"></td>
					</tr>
					<tr>
						<td><div class="imagecaption"><mm:field name="bron" /></div></td>
					</tr>
				</table>
			</mm:relatednodes>
			</td>
		</tr>
	</table>
   <table class="dotline"><tr><td height="3"></td></tr></table>
   <%@include file="articlessearch.jsp" %>
   <%@include file="searchresults.jsp" %>
</mm:node>
</mm:cloud>