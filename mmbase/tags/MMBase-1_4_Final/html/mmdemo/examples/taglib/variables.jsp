<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-0.8" prefix="mm"
%><html>
<body>
<%@ include file="menu.jsp"%>

<mm:context> <!-- the explicit use of context tag is only necessary in orion 1.5.2 -->

<h1>Variables</h1>
Please have the code of this page handy.
<h2>Simple pure taglib</h2>
<p>
In a limited way you can use `variables' without escaping to
jsp-coding. A variable you can define with the `import' tag. Lets
define a variable with name `a' and value `aaaa'.
</p>
<mm:import id="a">aaaa</mm:import>
<p>
And print it out.  a: <mm:write referid="a" />
</p>
<p>
Of course it is easy to make a new variable in which the value of the
variable `a' is used. Lets put some b's around it, and put it in `b'.
</p>
<mm:import id="b">bbb<mm:write referid="a" />bbb</mm:import>
<p>
The variable b is now: <mm:write referid="b" />
</p>
<p>
Another common thing to want is to change the value of a
variable. This can only be done by first destroying the variable, and
then recreating it. Lets set the variable 'a' to 'aaab'.
<!-- should this not be more handy ??
  I'd like an attribute in import for this.
  This would also permit to more easily _change_ the value of a variable.
-->
</p>
<mm:remove referid="a" /><mm:import id="a">aaab</mm:import>
<p>
And print it out again. a: <mm:write referid="a" />
</p>
<p>
To write the value of a variable to the page, we did use the `write'
tag. If the value of the variable must be used in some attribute than
you have to use the ${}-notation. For example like this: <mm:url
page="${a}" />
</p>
<h2>Escaping to JSP</h2>
<p>
 If you want to use more complicated things than this with variables,
 then you need to convert your variable to a jsp-variable, which you
 can treat with all means of Java. This can be done immediately with
 the import tag (if you really often need the variable in JSP), but
 also later, with the `write' tag (The variable then is available only
 in the body of the write tag). We create a new variable 'A' wich is
 'a' but uppercased with a java function.
</p>
<mm:write jspvar="a" referid="a" type="String">
 <mm:import id="A"><%= a.toUpperCase() %></mm:import>
</mm:write>
<p>
 The value of A: <mm:write referid="A" />
</p>
<p>
 Jsp-variable also have a type. Currently they can be `Object',
 `String', `Node' or `Integer'. This can lead to exceptions (error
 messages). You can for example not use our variable `a' as a Node,
 since it is not a node, but a String.
</p>
<p>
 Lets put a news node in variable `typedefnode'.
</p>
<mm:cloud>
<mm:listnodes type="typedef" max="1" ><mm:node id="typedefnode" /></mm:listnodes>
<p>
 Writing this variable is possible (though not very usefull). typedef: <mm:write referid="typedefnode" />  
</p>
<p>
 Lets make a jsp-variable of this node, and write a field of it:
<mm:write jspvar="td" referid="typedefnode" type="Node">
 <%= td.getValue("description") %>
</mm:write>
</p>
<p>
 This was of course a lousy example, because for simply writing a field
 you don't need jsp at all:
<mm:node referid="typedefnode">
  <mm:field name="description" />
</mm:node>
</p>

</mm:cloud>
</mm:context>
</body>
</html>