<%@page session="false" import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,java.util.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content type="text/html">
<html>
<head>
  <title>Testing MMBase/taglib</title>
</head>
<body>
  <h1>url-tags</h1>

  <mm:import id="a">A</mm:import>
  <mm:import id="b">B</mm:import>
  <mm:import id="c">C</mm:import>

  <p>
    <mm:url page="index.jsp" referids="a,b,c" />
  </p>
  <p>
    <mm:url page="index.jsp" referids="c,b,a" />
  </p>
  <p>
    <mm:url page="index.jsp" referids="b,c,a" />
  </p>
  <p>
    <mm:url page="index.jsp" referids="b,c,a">
      <mm:param name="a">AA</mm:param>
    </mm:url>
  </p>
</body>
</html>
</mm:content>
