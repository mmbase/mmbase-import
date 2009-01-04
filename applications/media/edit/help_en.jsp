<DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" postprocessor="reducespace">
<mm:cloud method="asis">
<html>
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
   <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<body class="help">
  <h1>Help</h1>
  <p>
    TODO: translate from dutch.
    <ul>
      <li><a href="#editor">Editor</a></li>
      <li><a href="#search">Search</a></li>
    </ul>
  </p>  
  <h2>Editor</h2>
  <a name="editor"> </a>
  <p>
    <img src="images/Media.jpg" />
  </p>
  <h2>Search</h2>  
  <a name="search"> </a>
</body>
</html>
</mm:cloud>
</mm:content>