<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" type="text/html" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<html>
<head>
  <title>Stream manager</title>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
  <script src="<mm:url page="style/streammanager.js.jsp?dir=&amp;fragment=&amp;" />" language="javascript"><!--help IE--></script>
</head>
<mm:cloud jspvar="cloud" method="asis">  
<body class="left">
  <mm:import id="current">edit</mm:import>
  <%@include file="submenu.jsp" %>
  <hr />
  <mm:node number="media.allstreams">
    <h1>
      <mm:write referid="config.mediaeditors_origin">
        <mm:isnotempty>
          <mm:node number="$config.mediaeditors_origin">
            <mm:field name="name" />
         </mm:node>
         </mm:isnotempty>
         <mm:isempty>
           <mm:field name="name" />
         </mm:isempty>
      </mm:write>
    </h1>
    <mm:import id="referrer"><%=new java.io.File(request.getServletPath()).getParentFile()%>/poolselectorholder.jsp</mm:import>
    
    <mm:field name="description" escape="p" />
    <p>
      <a  href="javascript:setContentFrame('<mm:url referids="referrer,config.mediaeditors_origin@superorigin" page="search_import.jsp"><mm:param name="onlyquick" value="yes" /></mm:url>');">
        <img border="0" src="media/quick.gif" /> Quick-knip
      </a>
    </p>
    <h1><%=m.getString("category")%></h1>   
    <mm:relatedcontainer searchdirs="destination,destination" path="parent,pools1,parent,pools2">
      <mm:sortorder field="pools1.name" />
        <mm:sortorder field="pools2.name" />
        <mm:write referid="config.mediaeditors_origin">
          <mm:isnotempty>
            <mm:constraint field="pools1.number" value="$config.mediaeditors_origin" />
          </mm:isnotempty>
        </mm:write>
        <mm:context>
        <mm:related id="related">
          <mm:node element="pools1"><mm:field id="superorigin" name="number" write="false" /></mm:node>
          <mm:first>
            <ul>
          </mm:first>
          <li>
            <mm:node element="pools2">
              <mm:maywrite>
                <a href="javascript:setContentFrame('<mm:url referids="superorigin" page="edit.jsp"><mm:param name="origin"><mm:field name="number" /></mm:param></mm:url>');">
                  <mm:write referid="config.mediaeditors_origin"><mm:isempty><mm:field node="related" name="pools1.name" />  - </mm:isempty></mm:write>
                  <mm:field name="name" />
                </a>
              </mm:maywrite>
              <mm:maywrite inverse="true">
                <mm:write referid="config.mediaeditors_origin"><mm:isempty><mm:field node="related" name="pools1.name" />  - </mm:isempty></mm:write>
                <mm:field name="name" />
              </mm:maywrite>
            </mm:node>
          </li>
          <mm:last></ul></mm:last>
        </mm:related>
        </mm:context>
      </mm:relatedcontainer>
    
  </mm:node>
</body>
</mm:cloud>
</html>
</mm:content>