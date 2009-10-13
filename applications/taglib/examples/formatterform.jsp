<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ taglib uri="http://mmbase.omroep.nl/omroep-taglib-1.0" prefix="nos"
%><% response.setContentType("text/html; charset=UTF-8");
	java.util.Locale utf = new java.util.Locale("nl", "NL");
	java.util.Locale.setDefault(utf);
 %><html>
<head>
<title>The formatter tag mm:formatter. Try-it-yourself page.</title>
<link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>

<%@ include file="menu.jsp"%>

<mm:import externid="xslt" ><xsl:import href="mmbase:2xhtml.xslt" /></mm:import>
<mm:import externid="number">5352</mm:import><!-- 81 / 5352 -->
<mm:cloud>
<mm:node number="$number">
<h1>The formatter tag mm:formatter. Try-it-yourself page.</h1>
<p>
  You are in a news node, in a formatter with node and listrelations
  (all descrels). You can supply the XSLT to transform it. Please don't hack my computer.
</p>
<table border="1" width="100%">
<tr valign="top">
<td>
<pre><mm:formatter format="escapexml"><mm:include page="codesamples/form.jsp" /></mm:formatter></pre>
<form enctype="multipart/form-data" action="<mm:url />">
  <textarea wrap="off" name="xslt"><mm:write referid="xslt" /></textarea><br />
  <input type="submit" value="Submit XSLT" />
</form>
</td>
</tr>
<tr valign="top">
  <td>
  <%@include file="codesamples/form.jsp" %>
  </td>
</tr>
</table>
<hr />
<a href="formatter.jsp">back</a>
</mm:node>
</mm:cloud>
</body>
</html>
