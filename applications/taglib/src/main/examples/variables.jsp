<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"

%><html>
<head>
  <title>Variables</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="menu.jsp"%>

<mm:context> <!-- the explicit use of context tag is only necessary in orion 1.5.2 -->

<h1>Variables</h1>

<p>
  Please have the <a target="_new" href="<mm:url page="showanypage.jsp"><mm:param
  name="page"><%=request.getServletPath()%></mm:param></mm:url>">source
  of this page</a> of this page handy.
</p>
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
</p>
<mm:remove referid="a" /><mm:import id="a">aaab</mm:import>
<p>
  And print it out again. a: <mm:write referid="a" />
</p>
<p>
  The same thing can be done by the 'reset' attribute of the mm:import tag.
  <mm:import id="a" reset="true">aaabb</mm:import>
  The value of a is now: <mm:write value="$a" />.
</p>
<p>
  <mm:import id="a" reset="true">[<mm:write referid="a" />, <mm:isnotempty referid="a"><mm:write referid="a" /></mm:isnotempty>]</mm:import>
  The value of a is now: <mm:write value="$a" />.
</p>
<p>
  To write the value of a variable to the page, we did use the `write'
  tag. If the value of the variable must be used in some attribute than
  you have to use the {}-notation. For example like this:
  <mm:url   page="${a}" />
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
<mm:write jspvar="a" referid="a" vartype="String">
  <mm:import id="A"><%= a.toUpperCase() %></mm:import>
</mm:write>
<p>
 The value of A: <mm:write referid="A" />
 (could also have been done like this: <mm:write referid="a" escape="uppercase" />)
</p>
<p>
  Jsp-variable also have a type. Currently they can be `Object',
  `String', `Node' or `Integer'. This can lead to exceptions (error
  messages). You can for example not use our variable `a' as a Node,
  since it is not a node, but a String.
</p>
<p>
  Lets put a node in variable `typedefnode'.
</p>
<mm:cloud rank="administrator">
  <mm:listnodes type="object" max="2" >
    <mm:first><mm:node id="typedefnode" /></mm:first><mm:last><mm:node id="anothernode" /></mm:last>
  </mm:listnodes>
  <p>
    Writing this variable is possible (though not very useful). typedef: <mm:write referid="typedefnode" />
  </p>
  <p>
    Lets make a jsp-variable of this node, and write a field of it:
    <mm:write jspvar="td" referid="typedefnode" vartype="Node">
      <%= td.getValue("description") %>
    </mm:write>
  </p>
  <p>
    This was of course a lousy example, because for simply writing a field
    you don't need jsp at all:
    <mm:node referid="typedefnode"><mm:field name="description" /></mm:node>
  </p>
  <h1>Tags and id's</h1>
  <h2>Referring to other tags than the direct parent</h2>
  <p>
    We have already seen how to use the 'id' attribute of the 'import'
    tag and of the 'node' tag. The 'id' like the name of the variable in
    the Context. It has also another function, which is however closely
    related. When a tag needs a parent tag - for example a 'field'
    always needs a 'NodeProvider' as a parent - and you don't want to
    refer to the direct parent, but to a parent of the parent, you can
    do this by indicating the id of this parent in an
    attribute. 'NodeReferrer' tags e.g. always can have the attribute
    'node' for this goal. Here we write again the same field as above,
    but it is not nested with two node tags:
    <mm:node id="outernode" referid="typedefnode">
      <mm:node referid="anothernode">
        <mm:field node="outernode" name="description" />
      </mm:node>
    </mm:node>
  </p>
  <h2>Creating context variables of lists</h2>
  <p>
    What happens if you supply the 'id' attribute to a 'List' tag? Of
    course, if the List tag is a 'NodeProvider' still this id can be
    used in the 'node' attribute of Field tags in the body. But such
    reference does not use the Context.
  </p>
  <p>
    Before every iteration the value of the current item is written to
    the context, which is removed after every iteration. So inside the
    body of a listnodes tag, with the id of the list you can get a
    'Node' from the context.
  </p>
  <p>
    When the list is done, then a variable with the type 'List' is
    written to the Context, containing the whole list.  This means that
    also list tags can have the 'referid' attribute, so that you can
    reuse the list. Lets show this.
  </p>
  <p>A list:
  <ul>
    <mm:listnodes id="alist" type="typedef" max="5">
      <li><mm:field name="description" />
      (<mm:first>showing use of referid in list: </mm:first><mm:node referid="alist"><mm:field name="number" /></mm:node>)
    </li>
  </mm:listnodes>
</ul>
</p>
<p>
  Reusing the same list:
  <ul>
    <mm:listnodes referid="alist">
      <li><mm:field name="description" /></li>
    </mm:listnodes>
  </ul>
</p>
<p>
  Making a jspvar of the list. Size of list is:
  <mm:write referid="alist" jspvar="list" vartype="List">
    <%= list.size() %>
  </mm:write>
</p>
<p>
  Of course, by the way, for the size of a list you can also use mm:size:
  <mm:listnodes referid="alist">
    <mm:first><mm:size /></mm:first>
  </mm:listnodes>
</p>

</mm:cloud>
</mm:context>
</body>
</html>
