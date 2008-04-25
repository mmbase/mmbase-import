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
          <mm:function  name="gui" />          
          <pre>
            <mm:functioncontainer>
              <mm:param name="field" value="title" />
              <mm:param name="length" value="10" />
              <mm:function name="wrap" escape="none" />
            </mm:functioncontainer>
          </pre>
          <br />
        </mm:listnodes>
      </mm:functioncontainer>
      <hr />
      <%-- 
      <mm:functioncontainer>
        <mm:param name="template" value="s(100x100)" />
        <mm:listnodes type="news" max="5">
          <mm:function  name="gui" />
          <br />
        </mm:listnodes>
      </mm:functioncontainer>
      --%>
      <hr />
      <mm:listnodes type="pools" max="1">
        <mm:import id="max">100</mm:import>
        <mm:nodelistfunction referids="max" name="function1">
          -- <mm:field name="number" /><br />
        </mm:nodelistfunction>
      </mm:listnodes>
    </mm:cloud>
  </body>
</html>
