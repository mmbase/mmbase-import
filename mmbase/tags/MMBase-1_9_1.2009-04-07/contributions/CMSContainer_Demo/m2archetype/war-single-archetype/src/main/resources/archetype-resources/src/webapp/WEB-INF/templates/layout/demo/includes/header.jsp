  <title><cmsc:title /></title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <cmsc:headercontent dublin="true" />
  <link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
  <link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
  <cmsc:insert-stylesheet var="stylesheet" />
  <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/stijl.css'/>" media="screen,projection,print" />
  <c:forEach var="style" items="${stylesheet}">
    <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/${style.resource}'/>" media="${style.media}" />
  </c:forEach>
  <!--[if IE]>
     <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/stijl_ie.css'/>" media="screen,projection,print" />
  <![endif]-->
  <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/css/print.css'/>" media="print" />
  <cmsc:feeds />
  <cmscf:editresources />