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
		<mm:node id="cod" number="$node" notfound="skip">
		  <h2><mm:field name="title" /></h2>
		  <h3><mm:field name="uppercase(subtitle)" /></h3>
		  <p>
			<mm:field name="intro" />
		  </p>
		  <mm:field name="html(body)" />
		  <mm:import id="articlefound" />
		  <h1>Image</h1>
		  <mm:listnodes type="images" orderby="number" directions="down" max="1">
             <mm:field node="cod" name="subtitle">
             <img src="<mm:image template="s(600x80!)+f(png)+modulate(200,0)+font(mm:fonts/Arial.ttf)+fill(ff0000)+pointsize(20)+text(20,50,'$_')" />" alt="<mm:field name="title" />" />: <mm:field name="description" />
           </mm:field>
           </mm:listnodes>

    <h1>Included pages</h1>
    <p><em>With mm:include (utf-8 page):</em></p>
    <% try { %>
    <mm:include page="included.jsp?node=$node" />
    <% } catch (Exception e) { %> 
      <p>Did not work (<%=e.toString()%>)</p>
    <% } %>
    <p><em>With mm:include (iso-8859-1 page):</em></p>
    <% try { %>
    <mm:include page="included1.jsp?node=$node" />
    <% } catch (Exception e) { %> 
      <p>Did not work (<%=e.toString()%>)</p>
    <% } %>


          <hr />
          <a href="<mm:url page="index.shtml" />">SCAN version of this page</a><br />
  	  	  <a href="<mm:url page="default.jsp" />">UTF-8 version of this page</a><br />
          <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
		</mm:node>


	  </body>
	</html>
</mm:cloud>  