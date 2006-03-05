<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="../../scripts/images.js" %>
<mm:cloud jspvar="cloud">
<%
String paginaID = request.getParameter("p");
%>
<table border="0" cellspacing="0" cellpadding="0"  width="211" height="221">
  <tr> 
    <td valign="top" background="media/images/ngb/fl.gif"> <img src="media/images/ngb/x.gif" width="1" height="65" border="0"><br>
      <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td background="media/images/ngb/x.gif" valign="top"><img src="media/images/ngb/x.gif" width="181" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='05'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/5.gif','document.dot5','document.dot5','media/images/ngb/5w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/5.gif" width="13" height="13" border="0" name="dot5"></a></td>
          <td background="media/images/ngb/x.gif" valign="top"><img src="media/images/ngb/x.gif" width="1" height="10" border="0"><br>
            <img src="media/images/ngb/x.gif" width="4" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='04'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/4.gif','document.dot4','document.dot4','media/images/ngb/4w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/4.gif" width="13" height="13" border="0" name="dot4"></a></td>
        </tr>
      </table>
      <img src="media/images/ngb/x.gif" width="1" height="57" border="0"><br>
      <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td background="media/images/ngb/x.gif" valign="top"><img src="media/images/ngb/x.gif" width="100" height="1" border="0"></td>
          <td background="media/images/ngb/x.gif" valign="top"><img src="media/images/ngb/x.gif" width="1" height="22" border="0"><br>
            <a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='02'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/2.gif','document.dot2','document.dot2','media/images/ngb/2w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/2.gif" width="13" height="13" border="0" name="dot2"></a></td>
          <td background="media/images/ngb/x.gif" valign="top"><img src="media/images/ngb/x.gif" width="1" height="11" border="0"><br>
            <a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='01'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/1.gif','document.dot1','document.dot1','media/images/ngb/1w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/1.gif" width="13" height="13" border="0" name="dot1"></a></td>
          <td background="media/images/ngb/x.gif" valign="top"><img src="media/images/ngb/x.gif" width="1" height="2" border="0"><br>
            <a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='03'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/3.gif','document.dot3','document.dot3','media/images/ngb/3w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/3.gif" width="13" height="13" border="0" name="dot3"></a></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</mm:cloud>