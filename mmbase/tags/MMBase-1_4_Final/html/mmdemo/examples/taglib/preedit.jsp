<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm"
%><html>
<body>
<%@ include file="menu.jsp"%>
<form  action="<mm:url page="edit.jsp" />">
 number (alias):  <input type="text" name="number" />
 <input class="submit"  type ="submit" value="ok" />
</form>
</body>
</html>