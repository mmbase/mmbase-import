<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="org.mmbase.bridge.*" %>
<html>
<title>Testing MMBase/taglib</title>
<body>
<h1>Testing MMBase/taglib</h1>
<h2>listcontainer</h2>

<mm:cloud jspvar="cloud">

  <h3>constraint</h3>


  <mm:list path="news" nodes="106002342">
    <mm:field name="news.number" /><br />
  </mm:list>
  

  <hr />
    

  <mm:listcontainer path="news">
    <mm:constraint field="news.subtitle" value="%%" operator="like" />
    <mm:constraint field="news.title" value=""  />

    <mm:composite operator="OR">
     <mm:constraint field="news.subtitle" value="XML%" operator="like" />
   </mm:composite>

    <mm:list fields="news.title">
      <mm:field name="news.title" /><br />
    </mm:list>
  </mm:listcontainer>



</mm:cloud>
<hr />
</body>
</html>