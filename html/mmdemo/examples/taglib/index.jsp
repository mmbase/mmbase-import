<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">

<html>

Here's a simple list of jumpers to see if it's working:

<mm:list type="jumpers" fields="name,url">

<mm:head><ul></mm:head>
 <li><%=name%> redirects to <%=url%></il>
<mm:tail></ul></mm:tail>

</mm:list>

<a href="../../../mmdocs/mmbase-taglib.html">More info about the MMBase tag library ...</a>

</html>

</mm:cloud>
