<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>

<html>
   <head>
      <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   </head>

   <body class="educationMenubarCockpit" style="border-bottom:none">
      <%
         if(request.getParameter("refresh_chechbox_commit") != null)
         {
            if (request.getParameter("refresh_chechbox_state") != null)
            {// Strore "Autorefresh refresh state" in Session
               session.setAttribute("refresh_chechbox_state", "");
            }
            else
            {// Reset "refresh state" in Session
               session.removeAttribute("refresh_chechbox_state");
            }
         }
      %>
      <table border="0" cellpadding="0" cellspacing="0" >
         <%// Table prevents from line feed after <FORM> tag %>
         <form>
            <input type="hidden" name="refresh_chechbox_commit" value="on"/>
            <tr>
               <td style="font-size:11px; font-weight:bold">
                  <input type="checkbox" name="refresh_chechbox_state" <% if(session.getAttribute("refresh_chechbox_state") != null) {out.println(" checked=\"checked\" ");}%> onClick="this.form.submit()"/>Automatisch herladen
               </td>
               <td>
                  &nbsp;<a href="#" onClick='top.frames["menu"].location.reload();  top.frames["code"].location.href = top.frames["code"].location.href'><img src="../gfx/refresh.gif" border="0"/></a>
               </td>
            </tr>
         </form>
      </table>
   </body>
</html>
</mm:cloud>