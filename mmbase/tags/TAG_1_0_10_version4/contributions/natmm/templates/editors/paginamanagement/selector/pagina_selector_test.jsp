<%@page import="com.finalist.tree.*,nl.leocms.pagina.*,nl.leocms.authorization.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Pagina-selector-test</title>
<script language="javascript" src="../../../js/gen_utils.js">
</script>
<script language="javascript">
   function testFunction(number, name) {
      alert('number: ' + number + ' name: ' + name);
      return true;
   }
</script>
</head>
<body>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<%
    String pageNumber = request.getParameter("pagenumber");
    if (pageNumber==null || "".equals(pageNumber)) {
        pageNumber= "not defined";
    }
%>
Geselecteerde pagina: <%= pageNumber %><br/>
<a href="pagina_selector.jsp?refreshtarget=pagina_selector_test.jsp&refreshframe=wizard" onclick="openPopupWindow('selector')" target="selector">Open frame selector</a><br/>
<a href="pagina_selector.jsp?refreshtarget=pagina_selector_test.jsp" onclick="openPopupWindow('selector')" target="selector">Open noframe selector</a><br/>
<a href="pagina_selector.jsp?refreshtarget=testFunction&refreshframe=wizard&mode=js" onclick="openPopupWindow('selector')" target="selector">Open javascript selector</a>
</mm:cloud>
</body>
