<%@include file="includes/taglibs.jsp"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<mm:content type="text/html" encoding="UTF-8">
  <cmsc:location var="cur" sitevar="site" />
  <html xmlns="http://www.w3.org/1999/xhtml" lang="${site.language}" xml:lang="${site.language}">
  <cmsc:screen>
    <head>
      <%@include file="includes/header.jsp"%>
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/landingpage.css'/>" media="screen,projection,print" />
    </head>
    <body>
       <div id="holder">
          <h1 class="hidden"><cmsc:title /></h1>
          <%@include file="includes/top.jsp" %>
          <cmsc:insert-portlet layoutid="submenu" />
          <div id="background">
             <div class="clear"></div>
             <div id="main">
               <cmsc:insert-portlet layoutid="breadcrumb" />
               <div id="column1" class="print">
                  <cmsc:insert-portlet layoutid="column1_1" />
                  <cmsc:insert-portlet layoutid="column1_2" />
               </div>
               <div id="column2" class="banner">
                  <cmsc:insert-portlet layoutid="column2_1" />
                  <cmsc:insert-portlet layoutid="column2_2" />
                  <cmsc:insert-portlet layoutid="column2_3" />
               </div>
               <div id="column3">  
                  <cmsc:insert-portlet layoutid="column3_1" />
                  <cmsc:insert-portlet layoutid="column3_2" />
                  <cmsc:insert-portlet layoutid="column3_3" />
                  <cmsc:insert-portlet layoutid="column3_4" />
                  <cmsc:insert-portlet layoutid="column3_5" />
                  <cmsc:insert-portlet layoutid="column3_6" />
               </div>
            </div>
            <%@include file="includes/footer.jsp"%>
            <div class="clear"></div>
         </div>
      </div>
    </body>
  </cmsc:screen>
  </html>
</mm:content>