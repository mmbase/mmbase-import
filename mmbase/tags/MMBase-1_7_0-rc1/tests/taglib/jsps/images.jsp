<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:content type="text/html" postprocessor="reducespace">
<html>
  <head>

  </head>
  <body>
    <h1>Image tag </h1>
    <mm:cloud logon="foo" pwd="bar"  jspvar="cloud">
    <mm:node number="474" />
    <%=cloud.getUser().getIdentifier()%>
    <mm:listnodes type="images">
       nodeinfo: <mm:nodeinfo type="gui" /><br />
       field: <mm:field escape="none" name="gui()" /> <br />
       image tag: <a href="<mm:image />"><img src="<mm:image template="s(100x100)" />" /></a>
    </mm:listnodes>
    </mm:cloud>
  </body>
</html>
</mm:content>