<%@ page import = "java.util.Enumeration" %>
<%@ page import = "java.util.Iterator" %>
<%@ page import = "java.util.Properties" %>

<%@ page import = "java.io.FileInputStream" %>


<% response.setHeader("Cache-Control","no-cache"); %>
<% response.setHeader("Pragma","no-cache"); %>
<% response.setDateHeader ("Expires", 0);%>



<%
/*
   System.out.println("------------");
   Enumeration enumTest = request.getAttributeNames();
   while(enumTest.hasMoreElements())
   {
      String sKey = (String) enumTest.nextElement();
      System.out.println(sKey);
      System.out.println(request.getAttribute(sKey));
   }
*/


   String sBasePath = (String) getServletContext().getInitParameter("internalUrl");
//   String sReferRef = (String) request.getAttribute("javax.servlet.forward.request_uri");
   String sReferRef = (String) request.getParameter("referrer");

   if(sReferRef == null) sReferRef = "index.jsp";


    Properties propLogin = new Properties();
    try
    {
        propLogin.load(new FileInputStream(getServletContext().getRealPath("/WEB-INF/config/security/login.properties")));

        if(propLogin.getProperty("default_login") != null)
        {
           response.sendRedirect(sBasePath + "/login_" + propLogin.getProperty("default_login") + ".jsp?referrer=" + sReferRef);
        }
    }
    catch(Exception e)
    {
       response.sendRedirect(sBasePath + "/login_plain.jsp?referrer=" + sReferRef);
    }

%>


1. <a href="<%= sBasePath %>/login_plain.jsp?referrer=<%= sReferRef %>">Plain</a>

<br/>
<br/>

2. <a href="<%= sBasePath %>/login_aselect.jsp?referrer=<%= sReferRef %>">A-Select</a>
