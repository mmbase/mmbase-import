<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Shows the source code of any page</title>
    <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %></h1>
    <mm:import externid="page" />
  </head>

  <body>
    <mm:notpresent referid="page">
    <h1>Showing any page</h1>
    <form action="" method="POST">
    <input type="text" size="50" name="page" value="<%=request.getServletPath()%>" />
    <hr />
    <input type="submit" value="show"/>
    </form>
    </mm:notpresent>

    <mm:present referid="page">
       <pre><mm:formatter format="escapexml"><mm:include page="$page" cite="true"/></mm:formatter></pre>
       <hr />
       visit <a href="<mm:url page="$page"/>"><mm:write referid="page" /></a> | <a href="<mm:url />">back</a>
    </mm:present>        
  </body>
</html>
