<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<html>
<mm:import id="a">A</mm:import>
<mm:import externid="b">X</mm:import>
<mm:import id="c">C</mm:import>

<mm:import externid="dummy"></mm:import>

<head>
<mm:notpresent referid="dummy">
  <meta http-equiv="refresh" content="1; url=<mm:url><mm:param name="b" value="B" /><mm:param name="a" value="X" /><mm:param name="dummy" value="" /></mm:url>" />
</mm:notpresent>
</head>
<body>


<mm:import id="l" vartype="list">A,B</mm:import>

<table>
  <tr><th>Test</th><th>Should be</th><th>Is</th></tr>
  <tr><td>Writing local vars</td><td>A, B, A, B</td><td><mm:write referid="a" />, <mm:write referid="b" />, <mm:write referid="l" /></td></tr>
  <tr><td>Simple include</td><td>A | A</td><td><mm:include page="writea.jsp" referids="a" /></td></tr>
  <tr><td>Simple include/import</td><td>B | B</td><td><mm:include page="writea.jsp" referids="b@a" /></td></tr>
  <tr><td>Simple include/import</td><td>C | C </td><td><mm:include page="writea.jsp" referids="c@a" /></td></tr>
  <tr><td>Simple include of list</td><td>A,B | A,B</td><td><mm:include page="writea.jsp" referids="l@a" /></td></tr>
</table>
</body>
</html>