<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<%@ include file="menu.jsp"%>
<h1>context</h1>
<p>
To use a context, you can use a context tag. But often you don't have
to, because there is one implicit (unnamed) context. These pages are
using this feature.
</p>
<p>
To start with, a context is empty, you can put things in it e.g. with
the 'import' tag, which gets objects from the outside world (like
parameters), and makes `taglib' variables of them. We now will put
into the variable 'hoi' the value of the parameter 'haj'.
</p>
<mm:import id="hoi" externid="haj" from="parameters" required="true" jspvar="groet" />
<p>
There are several ways to write something from the context to the
page. Here are a few examples with the 'write' tag. The write tag uses
the 'referid' attribute. Such an attribute expects the <em>name</em>
of the variable:
</p>
<p>
<mm:write referid="hoi" />, 
<mm:write referid="hoi" jspvar="greet"><%= greet %></mm:write>
</p>
<p>
We have also made a jsp variable of it, which can be used as well:
<%= groet.toUpperCase() %>
</p>
<p>
Contextes can be nested. Lets make the contextes A, B and C.
<mm:context id="A">  
  <mm:import id="hoi">hola!</mm:import>
  In context A we also create the variable 'hoi'. This time its value
 is 'hola!' </p><p>
  <mm:context id="B">
    Within a 'child' context of A (we named it B), you can write easily the variable
    from the parent context:
	<mm:write referid="hoi" /></p><p>
    Imagine that also in this context B we create a variable 'hoi', so
    that now we already have three variable named 'hoi'.
	<mm:import id="hoi">foobar</mm:import>
    <mm:context id="C">
     Then we could also demonstrate the use of the attribute
	'context'. It is not really necessary but exist for symmetry
	reasons. With it you can indicate in which of the parent contexes
	the write tag must be evaluated: <mm:write context="A"
	referid="hoi" />, <mm:write referid="A.hoi" />, <mm:write
	context="B" referid="hoi" />, <mm:write referid="A.B.hoi" />,
	<mm:write referid="hoi" /> </p>
    <p>The most upper value of the
	variable 'hoi' in this page (the one which was gotten with 'haj')
	is not accessible here in context C, because the implicit page
	context is unnamed and the 'hoi' variables of contextes A and B
	are shielding it.
    </mm:context>
  </mm:context>
</mm:context>
</p><p>
If you are not in a context, you can still access the variables of it,
but you have to indicate the full name.
<mm:write referid="A.B.hoi" />, <mm:write referid="A.hoi" />, <mm:write referid="hoi" />
</p>
<p>
Sometimes you also want to use the <em>value</em> of a variable in an attribute. In
that case a construction with a dollar sign ($) must be used. Imagine
for example that one of the variables 'hoi'  must be used in an url:
</p>
<mm:url page="${hoi}.jsp">
 <mm:param name="some_variable">value</mm:param>
</mm:url>, 
<mm:url page="test.${A.hoi}.jsp">
 <mm:param name="some_variable">another_example</mm:param>
</mm:url>,
<mm:url page="test.${A.B.hoi}.jsp">
 <mm:param name="some_variable">third_example</mm:param>
</mm:url>
<br />
<hr />
<a href='<mm:url page="context2.jsp" referids="hoi">
          <mm:param name="hello">saluton</mm:param>
         </mm:url>'>next page</a>	 
</body>
</html>