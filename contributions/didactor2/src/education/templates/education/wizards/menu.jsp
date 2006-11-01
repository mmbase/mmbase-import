<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
   <%@include file="/shared/setImports.jsp"%>

<html>
<head>
   <title></title>
</head>
<%//  This page gains control when EW is finished %>
   <body bgcolor="white">
      <%
         if(session.getAttribute("show_metadata_in_list") != null)
         {
            response.sendRedirect((String) session.getAttribute("metalist_url"));
         }
         else
         {
            %>
               <script type="text/javascript">
                  document.location.href='<mm:treefile page="/education/wizards/loaded.jsp" objectlist="$includePath" referids="$referids" />';
               </script>
            <%
         }
      %>
   </body>
</html>
 </mm:cloud>
</mm:content>
