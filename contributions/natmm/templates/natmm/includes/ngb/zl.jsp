<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="../../scripts/images.js" %>
<mm:cloud jspvar="cloud">
<%
String paginaID = request.getParameter("p");
%>
<table border="0" cellspacing="0" cellpadding="0" width="256" height="145">
  <tr> 
    <td valign="top" background="media/images/ngb/zl.gif"><img src="media/images/ngb/x.gif" width="1" height="5" border="0"><br>
      <img src="media/images/ngb/x.gif" width="55" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='10'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/10.gif','document.dot10','document.dot10','media/images/ngb/10w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/10.gif" width="13" height="13" border="0" name="dot10"></a><br>
      <img src="media/images/ngb/x.gif" width="1" height="2" border="0"><br>
      <img src="media/images/ngb/x.gif" width="52" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='07'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/7.gif','document.dot7','document.dot7','media/images/ngb/7w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/7.gif" width="13" height="13" border="0" name="dot7"></a><img src="media/images/ngb/x.gif" width="21" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='13'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/13.gif','document.dot13','document.dot13','media/images/ngb/13w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/13.gif" width="13" height="13" border="0" name="dot13"></a><br>
      <img src="media/images/ngb/x.gif" width="1" height="7" border="0"><br>
      <img src="media/images/ngb/x.gif" width="60" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='06'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/6.gif','document.dot6','document.dot6','media/images/ngb/6w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/6.gif" width="13" height="13" border="0" name="dot6"></a><img src="media/images/ngb/x.gif" width="4" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='04'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/4.gif','document.dot4','document.dot4','media/images/ngb/4w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/4.gif" width="13" height="13" border="0" name="dot4"></a><img src="media/images/ngb/x.gif" width="120" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='05'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/5.gif','document.dot5','document.dot5','media/images/ngb/5w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/5.gif" width="13" height="13" border="0" name="dot5"></a><br>
      <img src="media/images/ngb/x.gif" width="1" height="4" border="0"><br>
      <img src="media/images/ngb/x.gif" width="205" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='09'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/9.gif','document.dot9','document.dot9','media/images/ngb/9w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/9.gif" width="13" height="13" border="0" name="dot9"></a><br>
      <img src="media/images/ngb/x.gif" width="1" height="4" border="0"><br>
      <table border="0" cellspacing="0" cellpadding="0" background="media/images/ngb/x.gif">
        <tr> 
          <td valign="top"><img src="media/images/ngb/x.gif" width="120" height="1" border="0"><br>
            <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td valign="top"><img src="media/images/ngb/x.gif" width="51" height="1" border="0"></td>
                <td valign="top"><img src="media/images/ngb/x.gif" width="1" height="4" border="0"><br>
                  <a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='03'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/3.gif','document.dot3','document.dot3','media/images/ngb/3w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/3.gif" width="13" height="13" border="0" name="dot3"></a></td>
                <td valign="top"><img src="media/images/ngb/x.gif" width="1" height="8" border="0"><br>
                  <a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='02'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/2.gif','document.dot2','document.dot2','media/images/ngb/2w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/2.gif" width="13" height="13" border="0" name="dot2"></a></td>
                <td valign="top"><img src="media/images/ngb/x.gif" width="1" height="8" border="0"><br>
                  <a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='12'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/12.gif','document.dot12','document.dot12','media/images/ngb/12w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/12.gif" width="13" height="13" border="0" name="dot12"></a></td>
              </tr>
            </table>
          </td>
          <td> 
            <table border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td valign="top"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='11'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/11.gif','document.dot11','document.dot11','media/images/ngb/11w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/11.gif" width="13" height="13" border="0" name="dot11"></a></td>
                <td valign="top"><img src="media/images/ngb/x.gif" width="1" height="6" border="0"><br>
                  <img src="media/images/ngb/x.gif" width="44" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='01'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/1.gif','document.dot1','document.dot1','media/images/ngb/1w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/1.gif" width="13" height="13" border="0" name="dot1"></a></td>
                <td valign="top"><img src="media/images/ngb/x.gif" width="1" height="6" border="0"><br>
                  <img src="media/images/ngb/x.gif" width="6" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='08'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/8.gif','document.dot8','document.dot8','media/images/ngb/8w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/8.gif" width="13" height="13" border="0" name="dot8"></a></td>
                <td><img src="media/images/ngb/x.gif" width="1" height="14" border="0"><br>
                  <img src="media/images/ngb/x.gif" width="12" height="1" border="0"><a target="_top" href="<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,provincies,posrel,natuurgebieden" constraints="natuurgebieden.bron='14'">natuurgebieden.jsp?n=<mm:field name="natuurgebieden.number" /></mm:list>" onmouseover="simages('document.dummy','document.dummy','media/images/ngb/14.gif','document.dot14','document.dot14','media/images/ngb/14w.gif')" onmouseout="simgr();simgr()"><img src="media/images/ngb/14.gif" width="13" height="13" border="0" name="dot14"></a></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</mm:cloud>