<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>

<%
   session.removeAttribute("show_metadata_in_list");
%>

<html>
<head></head>
   <body>
      <script>
         if (top == self)
         {
            var loc = document.location.href;
            loc = loc.replace(/&amp;/ig,'&').replace(/(education\/).*/,"$1wizards/index.jsp");
            document.location.href = loc;
         }
      </script>
      <img src="<mm:treefile page="/education/wizards/gfx/ok.gif" objectlist="$includePath" referids="$referids" />" alt="OK">
   </body>
</html>
</mm:cloud>
</mm:content>

