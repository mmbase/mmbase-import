<%@ page import="org.mmbase.security.AuthenticationData,org.mmbase.util.functions.DataType" 
%><%@ include file="page_base.jsp"  
%><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0">
<mm:write referid="style" escape="none" />
<title>Login</title>
</head>
<mm:cloud sessionname="$config.session" method="logout" />
<body class="basic">
  <h2>Login</h2>
  <mm:import externid="reason">please</mm:import>
  <mm:import externid="exactreason" />
  <mm:import externid="usernames" />
  <mm:import externid="referrer">search_node.jsp</mm:import>
  <mm:compare referid="reason" value="failed">
    <p class="failed">
      Failed to log in <mm:write referid="exactreason"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:write>. Try again.
    </p>
  </mm:compare>
  <mm:compare referid="reason" value="rank">
    <p class="failed">
      Failed to log in, rank too low. Try again with another user name.
    </p>
  </mm:compare>
  <table>
    <%
      AuthenticationData authentication = ContextProvider.getDefaultCloudContext().getAuthentication();
      String[] authenticationTypes = authentication.getTypes(authentication.getDefaultMethod(request.getProtocol()));
    %>
    <mm:import externid="authenticate" jspvar="currentType" vartype="string" ><%=authenticationTypes[0]%></mm:import>
    <tr>
      <td>Authenticate:</td>
      <td>
        <form method="post" name="auth">
        <select name="authenticate" onChange="document.forms['auth'].submit();">
          <% for (int i = 0 ; i < authenticationTypes.length; i++) { %>
          <option value="<%=authenticationTypes[i]%>" <%= currentType.equals(authenticationTypes[i])? " selected='selected'" : ""%>><%=authenticationTypes[i]%></option>
          <% } %>
          <input type="hidden" name="referrer" value="<mm:write referid="referrer" />" />
          <input type="hidden" name="usernames" values="<mm:write referid="usernames" />" />
         </select>
       </form>
    </tr>
    <form method="post" action="<mm:write referid="referrer" />" >
    <% DataType[] params = authentication.createParameters(currentType).getDefinition();
       for (int j = 0; j < params.length ; j++) {
         DataType param = params[j];
         Class type = param.getType();
         if (type.isAssignableFrom(String.class) && param.isRequired()) {         
    %>
       
               <tr>
                 <td><%=param.getName()%>:</td>
                 <td>
                   <%-- hack for password fields Would need some method using DataType --%>
                   <input type="<%= param.getName().equals("password") ? "password" : "text" %>" name="<%=param.getName()%>">
                 </td>
               </tr>
     <%  }
        }
     %>
     <input type="hidden" name="usernames" values="<mm:write referid="usernames" />" />
    <tr><td /><td><input type="submit" name="command" value="login" /></td></tr>
  </form>
</table>
<%@ include file="footfoot.jsp"  %>
</mm:content>