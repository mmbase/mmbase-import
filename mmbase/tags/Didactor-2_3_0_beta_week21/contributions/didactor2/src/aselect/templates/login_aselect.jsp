<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>

<%
//   session.setAttribute("login_method", "delegate");
//   session.setAttribute("security", "a-select");

   String sBasePath = (String) getServletContext().getInitParameter("internalUrl");

   if(request.getParameter("referrer") != null){
       session.setAttribute("a-select-redirect", request.getParameter("referrer"));
   }
%>


<mm:cloud method="delegate" jspvar="cloud" authenticate="a-select">
   <%
       if(session.getAttribute("a-select-redirect") != null){
           response.sendRedirect((String) session.getAttribute("a-select-redirect"));
       }
       else{
           response.sendRedirect(sBasePath + "/index.jsp");
       }
/*
      if(request.getParameter("referrer") != null)
      {
         response.sendRedirect(request.getParameter("referrer"));
      }


      if(request.getAttribute("javax.servlet.forward.servlet_path") == null)
      {//login from the scratch
         response.sendRedirect(sBasePath + "/index.jsp");
      }
      else
      {
         response.sendRedirect(sBasePath + "/" + (String) request.getAttribute("javax.servlet.forward.request_uri"));
      }
*/
   %>

</mm:cloud>
