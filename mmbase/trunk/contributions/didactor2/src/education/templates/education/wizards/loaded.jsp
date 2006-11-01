<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
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
      <img src="<mm:treefile page="/education/wizards/gfx/ok.gif" objectlist="$includePath" referids="$referids" />" title="OK" alt="OK">
   </body>
</html>
</mm:cloud>
</mm:content>

