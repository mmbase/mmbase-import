<%--
This is the main page of printing, this include the requested page just like in the cockpit, only now ready for printing.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:cloud jspvar="cloud" name="mmbase" loginpage="/login.jsp">
  <%@ include file="/shared/setImports.jsp"%>

  <mm:import externid="page" id="page"/>
  <html>
    <head>
      <title>Didactor - Print</title>
      <link rel=StyleSheet href="<mm:treefile write="true" page="css/print.css" objectlist="$includePath"/>" type="text/css">
    </head>
    <body onload="window.print()">
      <table width="100%">
        <tr>
          <td class="tablecell" valign="top">
            <mm:treeinclude write="true" page="$page" objectlist="$includePath"/>
          </td>
        </tr>
      </table>
    </body>
  </html>
</mm:cloud>
