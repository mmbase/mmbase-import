<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="../../scripts/images.js" %>
<mm:cloud jspvar="cloud">
<%
String paginaID = request.getParameter("p");
%>
<table border="0" cellspacing="0" cellpadding="0" width="167" height="199">
  <tr> 
    <td valign="top" background="media/images/ngb/gr.gif"> 
      <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="28" border="0"><br>
            <img src="media/images/ngb/x.gif" width="50" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='06'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/6.gif','document.dot6','document.dot6','media/images/ngb/6w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/6.gif" width="13" height="13" border="0" name="dot6"></a></td>
        </tr>
        <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="5" border="0"><br>
            <img src="media/images/ngb/x.gif" width="70" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='03'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/3.gif','document.dot3','document.dot3','media/images/ngb/3w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/3.gif" width="13" height="13" border="0" name="dot3"></a></td>
        </tr>
        <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="5" border="0"><br>
            <img src="media/images/ngb/x.gif" width="128" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='02'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/2.gif','document.dot2','document.dot2','media/images/ngb/2w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/2.gif" width="13" height="13" border="0" name="dot2"></a></td>
        </tr>
  <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="1" border="0"><br>
            <img src="media/images/ngb/x.gif" width="55" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='04'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/4.gif','document.dot4','document.dot4','media/images/ngb/4w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/4.gif" width="13" height="13" border="0" name="dot4"></a></td>
        </tr>
        <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="1" border="0"><br>
            <img src="media/images/ngb/x.gif" width="68" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='07'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/7.gif','document.dot7','document.dot7','media/images/ngb/7w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/7.gif" width="13" height="13" border="0" name="dot7"></a></td>
        </tr>
        <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="6" border="0"><br>
            <img src="media/images/ngb/x.gif" width="68" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='05'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/5.gif','document.dot5','document.dot5','media/images/ngb/5w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/5.gif" width="13" height="13" border="0" name="dot5"></a></td>
        </tr>
        <tr> 
          <td background="/media/images/ngb/pix.gif"><img src="media/images/ngb/x.gif" width="1" height="10" border="0"><br>
            <img src="media/images/ngb/x.gif" width="130" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='01'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/1.gif','document.dot1','document.dot1','media/images/ngb/1w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/1.gif" width="13" height="13" border="0" name="dot1"></a></td>
        </tr>
      </table>
    </td>
  </tr>
</table>

</mm:cloud>