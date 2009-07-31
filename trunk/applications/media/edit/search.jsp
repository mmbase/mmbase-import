<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" 
%><mm:content language="$config.lang" type="text/html" expires="0">

<mm:cloud jspvar="cloud" method="asis">
<html>
<head>
  <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet"><!-- help IE --></link>
  <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
</head>

<mm:notpresent referid="config.mediaeditors_origin_set"> 
  <body class="left">
    Selecteer een streammanager-categorie.
    <form action="<mm:url />" >  
    <mm:import id="user"><%=cloud.getUser().getIdentifier()%></mm:import>
      <select name="mediaeditors_origin_<mm:write referid="user" />">
        <mm:node number="media.allstreams">
          <mm:relatednodes id="origin" directions="down" role="parent" type="pools" orderby="pools.name">            
            <option value="<mm:field name="number" />"><mm:field name="name" /></option>
          </mm:relatednodes>
        </mm:node>
      </select>
      <br />
      <%=m.getString("send")%><button type="submit"><img src="media/search.gif" /></button>
    </form>
    <%-- p>
      U kunt ook onmiddelijk <a href="<mm:url page="login.jsp" />">inloggen</a>.
      </p --%>
  </body>
</mm:notpresent><%-- no origin yet --%>

<mm:present referid="config.mediaeditors_origin_set">

<mm:import externid="origin"><mm:write referid="config.mediaeditors_origin" /></mm:import>

<body class="left"  onload="initLeft('entrance');">
<%@include file="submenu.jsp" %>
<hr />
<h1><%=m.getString("search")%> <mm:node number="$origin" notfound="skip"> - <mm:field name="name" /></mm:node></h1>
<p>
<table>
<form target="content" action="<mm:url page="view/index.jsp" />" >   
  <tr>
    <td><%=m.getString("type")%></td>
    <td>
      <select name="type">
        <option value="mediafragments"><%=m.getString("audio")%>/<%=m.getString("video")%></option>
        <option value="audiofragments"><%=m.getString("audio")%></option>
        <option value="videofragments"><%=m.getString("video")%></option>
      </select>
    </td>
  </tr>
  <tr>
    <td><%=m.getString("category")%></td>
    <td>
      <select name="origin">
        <option value=""><%=m.getString("any")%></option>
        <mm:node number="media.allstreams">
          <mm:relatedcontainer searchdirs="destination,destination" path="parent,pools1,parent,pools2">
            <mm:sortorder field="pools1.name" />
            <mm:sortorder field="pools2.name" />
            <mm:write referid="origin">
              <mm:isnotempty>
                <mm:constraint field="pools1.number" value="$origin" />
              </mm:isnotempty>
            </mm:write>
            <mm:related>
              <option value="<mm:field name="pools2.number" />">
                <mm:write referid="origin"><mm:isempty><mm:field name="pools1.name" />  - </mm:isempty></mm:write>
                <mm:field name="pools2.name" />
              </option>
            </mm:related>
          </mm:relatedcontainer>
        </mm:node>
      </select>
    </td>
  </tr>
  <tr><td><%=m.getString("owner")%></td><td><input type="text" name="owner" /></td></tr>
  <tr><td><%=m.getString("text")%></td><td><input type="text" name="searchvalue" /></td></tr>
  <tr><td><%=m.getString("send")%></td><td><button type="submit"><img src="media/search.gif" /></button></td></tr>
</form>
</table>
</p>
</body>
</mm:present><%-- origin set --%>
</html>
</mm:cloud>
</mm:content>