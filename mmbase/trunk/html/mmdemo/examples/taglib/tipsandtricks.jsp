<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>


<mm:cloud>

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Tips and Tricks</h1>

<ul>
  <li><em>How do I find out if a list is empty?</em><br />
      Define a variable in the mm:first tag. After the list you can put in the body of mm:notpresent 
	  the things which should happen if the list is empty.
  </li>
  <li><em>How can I write a field only if another field is
       empty?</em><br /> Use the 'mm:empty' tag in combination with
       the 'write="true"' attribute. This is valid for all 'writer'
       tags. For example 
       <pre>
&lt;mm:field name="subtitle" write="true" &gt;
     &lt;mm:isempty&gt;
         &lt;mm:field name="title" /&gt;
    &lt;/mm:isempty&gt;
&lt;/mm:field&gt;
</pre>
   You need the 'write' attribute because it is false on default if the tag has a body.
  </li>
  <li><em>I get exception if I use the 'id' attribute in a tag in the body of a
     list</em><br /><p>This can be solved in two ways.</p><p> The
     first solution is to make sure that the tag with the id attribute
     is evaluated only once. Then it is clear which value excactly
     must be written to the context, and you will not see an
     exception.</p> <p>The second way is to add an 'anonymous'
     (without 'id' attribute) mm:context text inside your list. In
     that way every evalution of the list-body has its own context,
     and variables cannot interfer. The drawback is of course that
     these variables cannot be accessed outside the list, because they
     are in an anonymous context. This is only logical.</p>
</ul>

</body>

</html>

</mm:cloud>
