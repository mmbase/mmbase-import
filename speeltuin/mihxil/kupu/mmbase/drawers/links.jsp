<?xml version="1.0" ?>
<%@page session="false"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/xml" language="en" expires="-1">
<collection>
  <uri>drawers/links.jsp</uri>
  <icon>kupuimages/kupulibrary.png</icon>
  <title>MMBase</title>
  <uri>drawers/links.jsp</uri>
  <items>
    <mm:cloud>
      <mm:listnodes type="urls" id="url">
        <resource id="${url.number}">          
          <uri><mm:field name="url" /></uri>
          <title><mm:field name="tite" /></title>
        </resource>
      </mm:listnodes>
    </mm:cloud>
  </items>
</collection>
</mm:content>