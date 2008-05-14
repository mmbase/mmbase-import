<%@include file="includes/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<mm:content type="text/html" encoding="UTF-8">
   <cmsc:location var="cur" sitevar="site" />
   <html xmlns="http://www.w3.org/1999/xhtml" lang="${site.language}" xml:lang="${site.language}">
   <cmsc:screen>
      <head>
      <title><cmsc:title /></title>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <cmsc:headercontent dublin="true" />
      <cmsc:insert-stylesheet var="stylesheet" />
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/stijl.css'/>" media="screen,projection,print" />
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/2column.css'/>" media="screen,projection,print" />
         <c:forEach var="style" items="${stylesheet}">
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/${style.resource}'/>" media="${style.media}" />
      </c:forEach>
      <!--[if IE]>
         <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/stijl_ie.css'/>" media="screen,projection,print" />
      <![endif]-->
      <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/print.css'/>" media="print" />

      <cmscf:editresources />
      </head>


      <body>
            <%@include file="includes/top.jsp" %>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">	
		<tr>
			<td valign="top" width="75%"><cmsc:insert-portlet layoutid="column1_1" /></td>
			<td rowspan="3" width="25%"><cmsc:insert-portlet layoutid="column2_1" /></td>
		</tr>
		<tr>
			<td valign="top" width="75%"><cmsc:insert-portlet layoutid="column1_2" /></td>
		</tr>
		<tr>
			<td valign="top" width="75%"><cmsc:insert-portlet layoutid="column1_3" /></td>
		</tr>


            <%@include file="includes/footer.jsp"%>

      </body>
   </cmsc:screen>
   </html>
</mm:content>