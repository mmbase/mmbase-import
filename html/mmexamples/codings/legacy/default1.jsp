<%@page session="false" language="java" contentType="text/html;charset=iso-8859-1"  
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>
<mm:cloud>
<html>
  <head>
    <title>Codings examples</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
  </head>
  <body>
    <h1>Introduction</h1>
    <p>
      This page is using ISO-8859-1.
    </p>
    <h1>In-page text</h1>
    <p>Café tweeëntwintig</p>
    <h1>MMBase data</h1>
    <mm:import externid="node" from="parameters">codings</mm:import>
    <mm:node id="cod" number="$node" notfound="skip">
      <h2><mm:field name="title" /></h2>
      <h3><mm:field name="subtitle" escape="uppercase,text/html" /></h3>
			<mm:field name="intro" escape="p" />
		  <mm:field name="body" escape="p" />
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
      <p><em>With jsp:include (utf-8 page):</em></p>
      <% try { %>
      <jsp:include page="included.jsp" />
      <% } catch (Exception e) { %> 
      <p>Did not work (<%=e.toString()%>)</p>
      <% } %>
      <p><em>With jsp:include (iso-8859-1 page):</em></p>
      <% try { %>
      <jsp:include page="included1.jsp" />
      <% } catch (Exception e) { %> 
      <p>Did not work (<%=e.toString()%>)</p>
      <% } %>   
      <p><em>With &lt;%@:include (utf-8 page)</em></p>
      <%@include file="atincluded.jsp" %>

      <p><em>With &lt;%@:include (iso-8859-1 page):</em></p>
      <%@include file="atincluded1.jsp" %>

      <hr />
      <a href="<mm:url page=".." />">back</a><br />
      <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
    </mm:node>
       
  </body>
</html>
</mm:cloud>

