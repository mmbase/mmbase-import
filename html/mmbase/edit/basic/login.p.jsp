<%--
  Reuseable generic login-page.
  Perhaps this could be placed on a more generic location like /mmbase
--%>
<%@ page import="org.mmbase.security.AuthenticationData,org.mmbase.bridge.*" 
%><%@  taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%>
<mm:import from="request" externid="language">en</mm:import>
<mm:import from="request" externid="country"></mm:import>
<mm:import from="request" externid="sessionname">cloud_mmbase</mm:import>

<mm:content type="text/html" language="$language" country="$country" expires="0" jspvar="locale">

<mm:cloud sessionname="$sessionname" method="logout" />

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
    <form method="post" action="<mm:write referid="referrer" />" >
    <% DataType[] params = authentication.createParameters(currentType).getDefinition();
       for (int j = 0; j < params.length ; j++) {
         DataType param = params[j];
         Class type = param.getTypeAsClass();
         if (type.isAssignableFrom(String.class) && param.isRequired()) {         
    %>       
    <tr>
      <td><%=param.getDescription(locale)%>:</td>
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
</table>
</mm:content>