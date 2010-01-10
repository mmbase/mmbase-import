<%-- config --%>
<mm:import id="max"         from="cookie" externid="my_editors_maxitems">25</mm:import>
<mm:import id="list"        from="cookie" externid="my_editors_typelist">editable</mm:import>
<mm:import id="searchbox"   from="cookie" externid="my_editors_searchbox">top</mm:import>
<mm:import id="columns"     from="cookie" externid="my_editors_columns">contentright</mm:import>
<mm:import id="maxdays"     from="cookie" externid="my_editors_maxdays">99</mm:import>
<mm:import externid="days"><mm:write referid="maxdays" /></mm:import>
<%-- other variables --%>
<mm:import externid="offset">0</mm:import>
  <link href="<mm:url absolute="server" page="css/my_editors.css" />" rel="stylesheet" type="text/css" />
  <mm:compare referid="columns" value="contentleft"><link href="<mm:url absolute="server" page="css/contentleft.css" />" rel="stylesheet" type="text/css" /></mm:compare>

  <link href="<mm:url absolute="server" page="img/favicon.ico" />" rel="icon" type="image/x-icon" />
  <link href="<mm:url absolute="server" page="img/favicon.ico" />" rel="shortcut icon" type="image/x-icon" />
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <title>my_editors - <mm:write referid="pagetitle" /></title>
  <script src="<mm:url absolute="server" page="scripts/tables.js" />" type="text/javascript"><!-- for MSIE --></script>
  <script src="<mm:url absolute="server" page="scripts/showdiv.js" />" type="text/javascript"><!-- for MSIE --></script>
