<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" 
%><mm:content language="$config.lang" postprocessor="reducespace">
<mm:import externid="logout" />
<mm:present referid="logout">
  <mm:cloud method="logout" />
</mm:present>

<mm:cloud jspvar="cloud" method="asis">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <link href="style/streammanager.css" type="text/css" rel="stylesheet"><!-- help IE --></link>
   <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=" />" language="javascript"><!--help IE--></script>
<head>
<mm:import externid="origin">media.myfragments</mm:import>
<body class="left"  onload="initLeft('entrance');">
  <%@include file="submenu.jsp" %>
  <hr />
 <h1><%=m.getString("search")%></h1>
  <p>
  <table>
  <form target="content" action="<mm:url page="view/index.jsp" />" >   
    <tr><td><%=m.getString("type")%></td><td><select name="type">
       <option value="mediafragments"><%=m.getString("audio")%>/<%=m.getString("video")%></option>
       <option value="audiofragments"><%=m.getString("audio")%></option>
       <option value="videofragments"><%=m.getString("video")%></option>
     </select></td></tr>
    <tr><td><%=m.getString("category")%></td><td><select name="origin">
       <option value=""><%=m.getString("any")%></option>
       <mm:node number="media.streams">
       <mm:related path="parent,pools2" orderby="pools2.name">
        <mm:context>
         <mm:node id="origin" element="pools2">
           <option value="<mm:field name="number" />"><mm:field name="name" /></option>
         </mm:node>
        </mm:context>
       </mm:related>
       </mm:node>
     </select></td></tr>
    <tr><td><%=m.getString("owner")%></td><td><input type="text" name="owner" /></td></tr>
    <tr><td><%=m.getString("text")%></td><td><input type="text" name="searchvalue" /></td></tr>
    <tr><td><%=m.getString("send")%></td><td><button type="submit"><img src="media/search.gif" /></button></td></tr>
  </form>
  </table>
  </p>
</body>
</html>
</mm:cloud>
</mm:content>