<%@ include file="inc.jsp" %>
<%@ page isErrorPage="true" %>
<body class="basic">
<mm:cloud name="mmbase">
<mm:import externid="channel" />
  <h2>Error on post</h2>
     <p><%= exception.getMessage() %></p>
      <pre>
  <%= org.mmbase.util.logging.Logging.stackTrace(exception) %>
      </pre>		
      </pre>
     &nbsp;
</body></html>
[ <a href="<mm:url page="forum.jsp" referids="channel" />" >Return to Forum</a> ]
</mm:cloud>
</body></html>
