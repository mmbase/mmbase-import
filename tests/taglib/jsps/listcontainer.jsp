<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<h2>listcontainer</h2>

<mm:cloud>

  <mm:log>1</mm:log>
  <h3>searchdirs</h3>
  <mm:listcontainer path="mags,news,people" searchdirs="destination">
    <mm:aliasconstraint element="mags" name="default.mags" />
    number: <mm:size id="number" /> <br />
    <mm:list fields="news.title">
      <mm:index />:<mm:field name="news.title" /><br />
    </mm:list>
    <p>
      Should show the news articles of MyNews.
    </p>
  </mm:listcontainer>

  <mm:log>2</mm:log>
  <mm:listcontainer path="mags,news,people" searchdirs="destination,source">
    <mm:aliasconstraint element="mags" name="default.mags" />
    <p>
      number: <mm:size /> (should be 0)
    </p>
  </mm:listcontainer>


  <mm:log>3</mm:log>
  <mm:listcontainer path="people,news,mags" searchdirs="source,source">
    <mm:aliasconstraint element="mags" name="default.mags" />
    <p>
      number: <mm:size /> (should be <mm:write referid="number" />)
    </p>
  </mm:listcontainer>


  <mm:log>4</mm:log>
  <mm:listcontainer path="people,news,mags">
    <mm:aliasconstraint element="mags" name="default.mags" />    
    <p>
      number: <mm:size /> (should be <mm:write referid="number" />)
    </p>
  </mm:listcontainer>

  <mm:log>5</mm:log>
  <mm:list path="people,news,mags" searchdir="source" fields="news.title">
    <mm:index />:<mm:field name="news.title" /><br />
  </mm:list>

  <h3>orderby</h3>
  <mm:listcontainer path="news,mags" searchdirs="source">
    <mm:aliasconstraint element="mags" name="default.mags" />    
    <mm:sortorder field="news.title" />
    <mm:list fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
    <p>
      Should show ordered news.
    </p>
  </mm:listcontainer>


  <mm:listcontainer path="news,mags" searchdirs="source">
    <mm:aliasconstraint element="mags" name="default.mags" />    
    <mm:list orderby="news.title" fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
    <p>
      Should show ordered news.
    </p>
  </mm:listcontainer>

  
  <mm:listcontainer path="news">
    <mm:maxnumber value="5" />
    <mm:list orderby="news.title" directions="down" fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
    <p>
      Should show (inversely) ordered news.
    </p>
  </mm:listcontainer>

  <h3>constraint</h3>

  <mm:listcontainer path="news,mags" searchdirs="source">
    <mm:aliasconstraint element="mags" name="default.mags" />    
    <mm:constraint     field="news.title" operator="LIKE" value="%xml%" />
    <mm:list fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
  </mm:listcontainer>


  <mm:listcontainer path="news,mags" searchdirs="source">
    <mm:aliasconstraint element="mags" name="default.mags" />    
    <mm:list constraints="[news.title] LIKE '%XML%'" fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
  </mm:listcontainer>


  <mm:listcontainer path="news">
    <mm:list constraints="[news.title] LIKE '%XML%'" fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
  </mm:listcontainer>



</mm:cloud>
<hr />
</body>
</html>