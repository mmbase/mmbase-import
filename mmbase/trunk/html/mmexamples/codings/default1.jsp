<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page language="java" contentType="text/html;charset=iso-8859-1" 
%><mm:cloud jspvar="cloud">
	<html>
	  <head>
		<title>Codings examples</title>
	  </head>
	  <body>
		<h1>Introduction</h1>
        This page is using ISO-8859-1.
		<h1>In-page text</h1>
        <p>Café tweeëntwintig</p>
        <h1>MMBase data</h1>
        <mm:import externid="node" from="parameters">codings</mm:import>
		<mm:node number="$node" notfound="skip">
		  <h2><mm:field name="title" /></h2>
		  <h3><mm:field name="subtitle" /></h3>
		  <p>
			<mm:field name="intro" />
		  </p>
		  <mm:field name="html(body)" />
		  <mm:import id="articlefound" />
		  <hr />
          <a href="<mm:url page="index.shtml" />">SCAN version of this page</a><br />
  		  <a href="<mm:url page="default.jsp" />">UTF-8 version of this page</a>
		</mm:node>
	  </body>
	</html>
</mm:cloud>  