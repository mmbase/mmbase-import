<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<mm:import id="a" jspvar="a">A</mm:import>
<mm:import externid="b" jspvar="b">X</mm:import>
<mm:import id="c" jspvar="c">C</mm:import>

<mm:import externid="dummy"></mm:import>

<head>
<mm:notpresent referid="dummy">
  <meta http-equiv="refresh" content="1; url=<mm:url><mm:param name="b" value="B" /><mm:param name="a" value="X" /><mm:param name="dummy" value="" /></mm:url>" />
</mm:notpresent>
</head>
<body>


<mm:import id="l" vartype="list" jspvar="l">A,B</mm:import>

<table>
  <tr><th>Test</th><th>Should be</th><th>Is</th></tr>
  <tr><td>Writing local vars</td><td>A, B, A, B</td><td><mm:write referid="a" />, <mm:write referid="b" />, <mm:write referid="l" /></td></tr>
  <tr><td>Simple include</td><td>A | A</td><td><mm:include page="writea.jsp" referids="a" /></td></tr>
  <tr><td>Simple include/import</td><td>B | B</td><td><mm:include page="writea.jsp" referids="b@a" /></td></tr>
  <tr><td>Simple include/import</td><td>C | C </td><td><mm:include page="writea.jsp" referids="c@a" /></td></tr>
  <tr><td>Simple include of list</td><td>A,B | A,B</td><td><mm:include page="writea.jsp" referids="l@a" /></td></tr>

  <tr><td>Simple jsp:include</td><td>A | A</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=a%>" /></jsp:include></td></tr>
  <tr><td>Simple jsp:include/import</td><td>B | B</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=b%>" /></jsp:include></td></tr>
  <tr><td>Simple jsp:include/import</td><td>C | C </td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=c%>" /></jsp:include></td></tr>
  <tr><td>Simple jsp:include of list</td><td>A,B | A,B</td><td><jsp:include page="writea.jsp"><jsp:param name="a" value="<%=l%>" /></jsp:include></td></tr>
</table>
</body>
</html>