<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<title>A simple editor</title>
<link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
<body>
<%@ include file="menu.jsp"%>
<form  action="<mm:url page="edit.jsp" />">
 number (alias):  <input type="text" name="number" />
 <input class="submit"  type ="submit" value="ok" />
</form>
</body>
</html>