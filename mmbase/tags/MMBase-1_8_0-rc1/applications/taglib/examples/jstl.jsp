<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%><html>
<head>
  <title>JSTL, EL and Variables</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="menu.jsp"%>

<h1><a href="http://java.sun.com/products/jsp/jstl">JSTL</a>, EL and variables</h1>

<p>
  MMBase 1.8 taglib variables are equivalent with JSTL taglib variables and EL. This was not the case in MMBase 1.7. A 1.7 compatiblity mode can be turned on by a property in web.xml:
  <pre>
  &lt;context-param&gt;
    &lt;param-name&gt;mmbase.taglib.isELIgnored&lt;/param-name&gt;
    &lt;param-value&gt;true&lt;/param-value&gt;
  &lt;/context-param&gt;
  </pre>
  This page assumes jstl to be installed, and this parameter to be <code>false</code>, or missing altogether.
</p>

<!-- Trickery to avoid Exception if EL not available -->
<mm:import id="included">jstl.included.jsp</mm:import>
<mm:import id="test">${included}</mm:import>
<mm:include page="${test}" />
</body>
</html>
