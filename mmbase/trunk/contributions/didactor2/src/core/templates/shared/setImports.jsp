<%--
  THIS JSP IS NEARLY DEPRECATED.


  Figure out the following variables:
  - current username (into variable $username)
  - current user id (into variable $user)
  - current server name (into varabiel $servername)
  - if 'provider' is not set:
    - current provider (into variable $provider)
      - if there is more than 1 provider, look if there is an 'url' related to
         one of the providers with the $servername name.
  - if 'education' is not set:
    - current education: if there is only 1 education for the selected provider
    - if there is only one education with an 'url' related to it that matches
      the servername
  - template include path (into variable $includePath)
--%>
<jsp:directive.include file="findUser.jspx" />
<jsp:directive.include file="findProvider.jspx" />
<%--
  Step 6: Based on the student and the education, try to find the class.
--%>
<mm:import externid="class"    from="request" />

<%--
  Step 7: call the 'validateUser' (which can be overwritten for a specific implementation)
  to make sure that this user may log in.
--%>
<mm:import escape="trimmer" id="validatemessage"><mm:treeinclude page="/shared/validateUser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
<mm:isnotempty referid="validatemessage">
  <mm:cloud method="logout" />
  <% if (! response.isCommitted()) { %>
  <mm:redirect page="/login/declined.jspx" referids="validatemessage@message">
    <mm:param name="referrer"><mm:treefile page="/index.jsp" objectlist="$includePath" referids="$referids" /></mm:param>
  </mm:redirect>
  <% } %>

</mm:isnotempty>
