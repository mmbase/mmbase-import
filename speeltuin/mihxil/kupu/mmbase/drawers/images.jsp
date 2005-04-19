<?xml version="1.0" ?>
<%@page session="false"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content type="text/xml" language="en" expires="-1">
<collection>
  <uri>drawers/images.jsp</uri>
  <icon>kupuimages/kupulibrary.png</icon>
  <title>MMBase</title>
  <uri>drawers/images.jsp</uri>
  <items>
    <mm:cloud>
      <mm:listnodes type="images" id="image">
        <resource id="${image.number}">          
          <uri><mm:image template="s(100)" /></uri>
          <title><mm:field name="title" /></title>
          <size><mm:field name="filesize" />b</size>
          <mm:image>
            <height>${dimension.height}</height>
            <width>${dimension.width}</width>
          </mm:image>
          <preview><mm:image template="s(100)" /></preview>
        </resource>
      </mm:listnodes>
    </mm:cloud>
  </items>
</collection>
</mm:content>