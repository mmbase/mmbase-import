<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp"%>
<mm:content type="text/html" expires="0">
<html>
<head>
 <title>The edit tag</title>
 <link href="style.css" rel="stylesheet" type="text/css"/>
 <style>
h3 {
 margin: 0;
}
 </style>
</head>
<body>
<%@ include file="menu.jsp"%>

<mm:cloud>

<h1>Edit tag</h1>
  <h2>Create a link to an editor</h2>
  <p>
  Just by placing the tags &lt;mm:edit&gt; and &lt;/mm:edit&gt; around some nodes and 
  fields you can create a link to an editor that lets you edit them. In this example
  you can edit the MyNews example, but you should really try this out on one of your
  own jsp's.
  </p>
  <p>This example uses the magazine from the MyNews application. The edittag creates 
  a link to a generic editor YAMMeditor which it presumes to be located at 
  /yammeditor/yammeditor.jsp. You can download yammeditor from the taglib project 
  page at www.mmbase.org.<br />
  After installing yammeditor: Click the icon to use the editor to edit the magazine, 
  its related news nodes and their authors.</p>

<%@ include file="codesamples/edittag.jsp" %>


<hr />
<p>
It was implemented like this:
</p>
<pre><mm:include page="codesamples/edittag.jsp" escape="text/xml" cite="true" /></pre>

</mm:cloud>
</body>
</html>
</mm:content>
