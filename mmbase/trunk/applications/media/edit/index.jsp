<%@page language="java" contentType="text/html; charset=utf-8"%>
<html>
  <head>
    <title>Media editor</title>
    <link rel="icon"          href="images/favicon.ico"" type="image/x-icon" />
    <link rel="shortcut icon" href="images/favicon.ico"  type="image/x-icon" />
  </head>
  <frameset  rows="75,*" border="0">
      <frame 
        name="header" 
        src ="header.jsp" 
        scrolling="no" frameborder="0" border="0"  framespacing="0">
        <frameset  cols="331,*"><!-- 2 x 2 margin + 32 + 35 td's + 1 * 260 td -->
          <frame 
             name="player" 
             src="placeholder.jsp" 
             marginwidth="0" marginheight="0" scrolling="no" frameborder="0" border="0" noresize framespacing="0">
            <frame name="content" src="entrancepage.jsp" marginwidth="0" marginheight="0" scrolling="auto" frameborder="0" border="0" noresize framespacing="0">
    </frameset>
</frameset>
</html>
