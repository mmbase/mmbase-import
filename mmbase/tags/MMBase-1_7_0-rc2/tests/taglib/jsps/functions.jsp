<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page import="java.util.*,org.mmbase.util.*,org.mmbase.cache.Cache" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Function tags.</title>
  </head>

  <body>
    <mm:cloud>
      <mm:listnodes type="news" max="2">
        <mm:function name="gui" /><br />
      </mm:listnodes>
      <hr />
      <mm:listnodes type="news" max="2">
        <mm:functioncontainer>
          <mm:function  name="gui" /><br />
        </mm:functioncontainer>
      </mm:listnodes>
      <hr />
      <mm:functioncontainer>
        <mm:listnodes type="news" max="2">
          <mm:param name="language" value="nl" />
          <mm:function  name="gui" /><br />
        </mm:listnodes>
      </mm:functioncontainer>
      <hr />
      <mm:functioncontainer>
        <mm:param name="template" value="s(100x100)" />
        <mm:listnodes type="news" max="5">
          <mm:function  name="gui" /><br />
        </mm:listnodes>
      </mm:functioncontainer>
    </mm:cloud>
  </body>
</html>
