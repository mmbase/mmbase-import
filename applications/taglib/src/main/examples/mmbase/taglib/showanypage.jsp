<%@ page language="java" contentType="text/html; charset=UTF-8"
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Shows the source code of any page</title>
    <link href="style.css" rel="stylesheet" type="text/css"/>
    <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %></h1>
    <mm:import externid="page" />
    <script language="javascript"><!--
	    function gotoPage(el) {
   	    var href = el.getAttribute("href");
	      var args = document.forms[0].elements["arguments"].value;
       
    	  if (args != '') {
        	  href += '?' + args;
	      }
	      document.location = href;
	      return false;
     }
--></script>
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
       <form action="<mm:url page="$page" />">
       <pre><mm:formatter format="escapexml"><mm:include page="$page" cite="true"/></mm:formatter></pre>
       <hr />
       visit <a href="<mm:url page="$page" />" onClick="return gotoPage(this);" ><mm:write referid="page" /></a>?<input type="text" name="arguments" value="" />
       </form>
    </mm:present>        
  </body>
</html>
