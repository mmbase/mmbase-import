<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
	<%@include file="/shared/setImports.jsp"%>
<html>
<head>
<title></title>
</head>
<body>
<%--
  Empty page with only the 'loading' image
--%>
<html>
 <head></head>
 <body bgcolor="white">
   <img src="<mm:treefile page="/education/wizards/gfx/loading.gif" objectlist="$includePath" referids="$referids" />" alt="">
   <script type="text/javascript">
    if (top.frames['code'].location) {
	top.frames['code'].location.reload();
    }
   </script>
   
   </body>
</html>
 </mm:cloud>
</mm:content>
