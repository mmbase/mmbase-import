<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Home</h1>

<p>
The pages found here are made to give some taglib examples. Things are kept
simple so that the page sources are as clear as possible.
</p>

<p>
This page should work after building Tomcat or Orion from the build file. 
</p>

<p>
Here's a simple list of typedefs to see if the MMBase taglib is working:
</p>

<mm:listnodes type="typedef">
 <mm:first><ul></mm:first>
 <li>
   <mm:field name="name"/> is: <mm:field name="description"/>
   <mm:first>(first)</mm:first>
   <mm:last>(last)</mm:last>
   <mm:odd>(odd)</mm:odd>
   <mm:even>(even)</mm:even>
   <mm:changed>(changed)</mm:changed>
 </li>
 <mm:last></ul></mm:last>
</mm:listnodes>

</body>

</html>

</mm:cloud>
