<%@page language="java" contentType="text/html; charset=utf-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" 
%><mm:content type="text/html" expires="0">
<html>
  <head>
    <title>Media editor</title>
    <link rel="icon"          href="images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />    
   <script language="javascript">
  // todo:
      function resize(x) {
        document.open();
        urlleft    = document.frames["left"].src;
        urlcontent = document.frames["content"].src;

        document.write("<frameset rows=\"" + x + "%\">");
        document.write("<frame src=\"" + url0 + "\">");
        document.write("<frame src=\"" + url1 + "\">");
        document.write("</frameset>");
        document.write.document.close();
}
  </script>

  </head>  
  <frameset  rows="75,*" border="0">
      <frame name="header" src ="<mm:url page="header.jsp" />" scrolling="no" frameborder="0" border="0"  framespacing="0">
        <frameset  cols="331,*"><!-- 2 x 2 margin + 32 + 35 td's + 1 * 260 td -->
          <frame   name="left"    src="<mm:url page="search.jsp" />" marginwidth="0" marginheight="0" scrolling="no" frameborder="0" border="0" noresize framespacing="0">
          <frame   name="content" src="<mm:url page="placeholder.jsp" />" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" border="0" noresize framespacing="0">
       </frameset>
  </frameset>
</html>
</mm:content>