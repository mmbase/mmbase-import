<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<mm:import externid="location" />
<mm:compare referid="location" value="" >
   <mm:remove referid="location" />
   <mm:import id="location">sitemenu</mm:import>
</mm:compare>
<html>
<head>
   <title>Site menu</title>
</head>

<frameset rows="40,*">
  <frame name="menu" src="menu.jsp?location=<mm:write referid="location" />" marginwidth="5" marginheight="5" scrolling="no" frameborder="0">
  <frame name="editscreen" src="select.jsp?location=<mm:write referid="location" />" marginwidth="5" marginheight="5" scrolling="auto" frameborder="0">
</frameset>

</html>

</mm:cloud>