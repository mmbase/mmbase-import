<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<p>
Click <a href="../../../mmdocs/mmbase-taglib.html">here</a> to read the MMBase taglib documentation.
</p>

<p>
Here's a simple list of jumpers to see if it's working:
</p>

<mm:list type="jumpers" fields="name,url">
 <mm:first><ul></mm:first>
 <li>
   <%=name%><mm:field name="name"/> redirects to <%=url%> <mm:field name="url"/>
   <mm:first>(first)</mm:first>
   <mm:last>(last)</mm:last>
   <mm:odd>(odd)</mm:odd>
   <mm:even>(even)</mm:even>
   <mm:changed>(changed)</mm:changed>
 </il>
 <mm:last></ul></mm:last>
</mm:list>

</body>

</html>

</mm:cloud>
