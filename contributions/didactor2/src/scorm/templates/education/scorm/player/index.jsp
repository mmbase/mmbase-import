<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%
   String sPath;
   if (request.getParameter("path") == null)
   {
      %>
         <mm:cloud method="delegate" jspvar="cloud">
            <%@include file="/shared/setImports.jsp" %>
            <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
               <mm:param name="extraheader">
                  <title>Didactor</title>
                  <link rel="stylesheet" type="text/css" href="css/pop.css" />
               </mm:param>
            </mm:treeinclude>
         </mm:cloud>
      <%
      sPath = "";
   }
   else
   {
      sPath = request.getParameter("path");
   }
%>
<iframe id="didactor_frame" name="didactor_frame" src="index2.jsp?path=<%= sPath %>" width="100%" height="85%"></iframe>
