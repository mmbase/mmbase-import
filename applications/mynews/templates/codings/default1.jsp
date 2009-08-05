<%@page session="false" language="java" contentType="text/html;charset=iso-8859-1"  
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="postprocessor">reducespace</mm:import><mm:content type="text/html" postprocessor="$postprocessor" encoding="ISO-8859-1">
<mm:import externid="cite">false</mm:import>
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
    <p>Caf� twee�ntwintig</p>
    <p>CP1252:  <%= org.mmbase.util.transformers.CP1252Surrogator.getTestString() %></p>
    <p>CP1252 escaped:  <mm:write value="<%= org.mmbase.util.transformers.CP1252Surrogator.getTestString() %>" escape="cp1252" /></p>
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
<% String thisDir = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") + new java.io.File(request.getRequestURI()).getParent() + "/"; %>            
      <h1>Included pages</h1>
      <p><em>With mm:include (utf-8 page):</em></p>
      <% try { %>
      <mm:include referids="node" page="included.jsp" />
      <p><em>With mm:include (external, utf-8 page):</em></p>
      <mm:include referids="node" page='<%=thisDir + "included.jsp"%>' />
      <% } catch (Exception e) { %> 
      <p>Did not work (<%=e.toString()%>)</p>
      <% } %>
      <p><em>With mm:include (iso-8859-1 page):</em></p>
      <% try { %>
      <mm:include referids="node" page="included1.jsp" />
      <p><em>With mm:include (external, iso-8859-1 page):</em></p>
      <mm:include referids="node" page='<%=thisDir + "included1.jsp"%>' />
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
      <h2><a name="xmls" />
      Including XML's
  <mm:write referid="cite">
    (
    <mm:compare value="true">
      <a href="<mm:url><mm:param name="cite" value="false" /></mm:url>#xmls">No cite</a>
    </mm:compare>
    <mm:compare value="true" inverse="true">
      <a href="<mm:url><mm:param name="cite" value="true" /></mm:url>#xmls">Cite</a>
    </mm:compare>
  </mm:write>
  )
</h2>
<p><em>With mm:include (iso-8859-1 xml<mm:compare referid="cite" value="true">, Cite</mm:compare>):</em></p>
<pre>
<mm:include page="included1.xml" escape="text/xml" cite="$cite"/>
</pre>
 <p><em>With mm:include (UTF-8 xml<mm:compare referid="cite" value="true">, Cite</mm:compare>):</em></p>
<pre>
<mm:include page="included8.xml" escape="text/xml" cite="$cite" />
</pre>
      <p><em>With jsp:include (iso-8859-1 xml):</em></p>
<pre>
<mm:import id="i1"><jsp:include page="included1.xml"  /></mm:import><mm:write referid="i1" escape="text/xml" />
</pre>
      <p><em>With jsp:include (UTF-8 xml):</em></p>
<pre>
<mm:import id="i8"><jsp:include page="included8.xml"  /></mm:import><mm:write referid="i8" escape="text/xml" />
</pre>
      <p><em>With &lt;%@include (iso-8859-1 xml):</em></p>
<pre>
<mm:import id="ai1"><%@include file="included1.xml" %></mm:import><mm:write referid="ai1" escape="text/xml" />
</pre>
      <p><em>With &lt;@include (UTF-8 xml):</em></p>
<pre>
<mm:import id="ai8"><%@include file="included8.xml" %></mm:import><mm:write referid="ai8" escape="text/xml" />
</pre>
   <h2>Formatter-tag</h2>
   <p><em>In-page XML, explicitely specifying encoding:</em></p>
<pre>
  <mm:formatter format="escapexmlpretty">
    <?xml version="1.0" encoding="iso-8859-1"?>
    <mmxf version="1" >
      <p>Caf� twee�ntwintig</p>
    </mmxf>

  </mm:formatter>
</pre>
 <p><em>With &lt;%@include (iso-8859-1 xml):</em></p>
<pre>
  <mm:formatter format="escapexmlpretty">
    <%@include file="included1.xml" %>
  </mm:formatter>
</pre>
 <p><em>With &lt;%@include (UTF-8 xml):</em></p>
<pre>
  <mm:formatter format="escapexmlpretty">
    <%@include file="included8.xml" %>
  </mm:formatter>
</pre>

<hr />
      <a href="<mm:url page="." />">back</a><br />
      <a href="<mm:url page="../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
    </mm:node>
    
       
  </body>
</html>
</mm:cloud>
</mm:content>