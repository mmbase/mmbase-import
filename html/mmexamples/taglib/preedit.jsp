<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<title>A simple editor</title>
<link href="style.css" rel="stylesheet" type="text/css"/>
<body>
<%@ include file="menu.jsp"%>

<h1>Simple editor</h1>
<p>
This is an example of how one could make a simple editor with the
MMBase taglib. It must be called with a `number' parameter. The page
has two appearances (with the use of the `present' Tag), one with a
form, and one which processes the form.
</p>

<form  action="<mm:url page="edit.jsp" />">
 number (alias):  <input type="text" name="number" />
 <input class="submit"  type ="submit" value="ok" />
</form>
</body>
</html>
