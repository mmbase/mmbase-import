<%@include file="includes/taglibs.jsp"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<mm:content type="text/html" encoding="UTF-8">
   <cmsc:location var="cur" sitevar="site" />
   <html xmlns="http://www.w3.org/1999/xhtml" lang="${site.language}" xml:lang="${site.language}">
   <cmsc:screen>
    <head>
      <%@include file="includes/header.jsp"%>
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/1column.css'/>" media="screen,projection,print" />
    </head>
    <body>
       <h1 class="hidden"><cmsc:title /></h1>
       <div id="holder">
         <%@include file="includes/top.jsp" %>
         <cmsc:insert-portlet layoutid="submenu" />
         <div id="main">
            <cmsc:insert-portlet layoutid="breadcrumb" />
            <cmsc:insert-portlet layoutid="column1_1" />
            <cmsc:insert-portlet layoutid="column1_2" />
            <cmsc:insert-portlet layoutid="column1_3" />
         </div>
         <%@include file="includes/footer.jsp"%>
         <div class="clear"></div>
      </div>
      </body>
   </cmsc:screen>
   </html>
</mm:content>