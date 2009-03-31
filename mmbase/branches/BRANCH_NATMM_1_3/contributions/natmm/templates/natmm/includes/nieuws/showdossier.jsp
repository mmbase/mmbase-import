<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="java.util.*,nl.leocms.util.*,nl.leocms.util.tools.*,nl.leocms.util.tools.HtmlCleaner" %>
<%@include file="../../includes/time.jsp" %>
<%-- NMCMS-639 --%>
<%@include file="../../includes/image_vars.jsp" %>
<%
String styleSheet = request.getParameter("rs");
String lnRubriekID = request.getParameter("lnr");
String rnImageID = request.getParameter("rnimageid");

String shortyRol = ""; 

%>

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
            	<%-- NMCMS-639 --%>
                <%@include file="../../includes/image_logic.jsp" %>
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