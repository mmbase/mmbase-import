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
   <%-- 
    this reloads the menu when the editwizards are "done"

    the first check is to make sure were in the right iframe (this page is loaded in 2 frames
    and this code won't work when we're in the wrong one)
    --%>
    
<%--    if (top.frames['text'].location.href == document.location.href && top.frames['code'].location.href.indexOf("code.jsp") >= 0) {
	top.frames['code'].location.href=top.frames['code'].location.href;
    } --%>
    document.location.href='<mm:treefile page="/education/wizards/loaded.jsp" objectlist="$includePath" referids="$referids" />';
   </script>
   
   </body>
</html>
 </mm:cloud>
</mm:content>
